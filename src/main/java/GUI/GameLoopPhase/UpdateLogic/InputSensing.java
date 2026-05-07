package GUI.GameLoopPhase.UpdateLogic;

import GUI.GUIServices.GUIManager;
import GUI.GUIServices.CameraManager;
import GUI.GUIServices.MapManager;
import Logic.Core.SimulationManager;
import Model.BaseBuilding;
import Model.BuildingFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class InputSensing {

    // ⭐️ 1. เพิ่มสถานะ MODE_IDLE
    public static final int MODE_IDLE = 0;
    public static final int MODE_BUILD = 1;
    public static final int MODE_DEMOLISH = 2;

    private static int currentMode = MODE_IDLE; // เริ่มเกมมาให้เมาส์ว่าง
    private static String selectedBuildingId = "";

    // ⭐️ 2. ตัวแปรเก็บตำแหน่ง Grid ที่เมาส์ชี้อยู่ (สำหรับ Preview)
    private static int hoverGridX = -1;
    private static int hoverGridY = -1;

    public static void SetMouse() {
        setupInput(GUIManager.getInstance().getGameCanvas());

        GUIManager.getInstance().getScene().setOnScroll(e -> {
            CameraManager.getInstance().handleZoom(e.getDeltaY());
        });
    }

    // --- Getters สำหรับดึงไปวาดภาพ Preview ---
    public static int getCurrentMode() { return currentMode; }
    public static String getSelectedBuildingId() { return selectedBuildingId; }
    public static int getHoverGridX() { return hoverGridX; }
    public static int getHoverGridY() { return hoverGridY; }

    // --- Setters สำหรับเปลี่ยนโหมด ---
    public static void setIdleMode() {
        currentMode = MODE_IDLE;
        selectedBuildingId = "";
        System.out.println("Mode: IDLE (เมาส์ว่าง)");
    }

    public static void setBuildMode(String buildingId) {
        currentMode = MODE_BUILD;
        selectedBuildingId = buildingId;
        System.out.println("Mode: BUILD, Selected: " + buildingId);
    }

    public static void setDemolishMode() {
        currentMode = MODE_DEMOLISH;
        selectedBuildingId = "";
        System.out.println("Mode: DEMOLISH");
    }

    public static void setupInput(Canvas canvas) {
        canvas.setOnMouseClicked(event -> handleMouseClick(event, canvas));
        // ⭐️ 3. เพิ่มการดักจับตอนขยับเมาส์ เพื่ออัปเดต Hover
        canvas.setOnMouseMoved(event -> handleMouseMove(event, canvas));
    }

    // ========================================================
    // 🧮 เมธอดคำนวณ Isometric แบบใช้ซ้ำได้
    // ========================================================
    private static int[] getGridCoords(double mouseX, double mouseY, double screenWidth) {
        double camX = CameraManager.getInstance().getX();
        double camY = CameraManager.getInstance().getY();
        double tileSize = CameraManager.getInstance().getTileSize();
        double halfWidth = tileSize / 2.0;
        double halfHeight = tileSize / 4.0;

        double relX = mouseX + camX - (screenWidth / 2.0);
        double relY = mouseY + camY;

        int gridX = (int) Math.floor((relX / halfWidth + relY / halfHeight) / 2.0);
        int gridY = (int) Math.floor((relY / halfHeight - relX / halfWidth) / 2.0);

        return new int[]{gridX, gridY};
    }

    // ========================================================
    // 🖱️ จัดการ Event เมาส์
    // ========================================================
    private static void handleMouseMove(MouseEvent event, Canvas canvas) {
        int[] coords = getGridCoords(event.getX(), event.getY(), canvas.getWidth());
        hoverGridX = coords[0];
        hoverGridY = coords[1];
    }

    private static void handleMouseClick(MouseEvent event, Canvas canvas) {
        // ⭐️ 4. ดักจับคลิกขวา (SECONDARY) เพื่อยกเลิกกลับไปสถานะว่าง
        if (event.getButton() == MouseButton.SECONDARY) {
            setIdleMode();
            return;
        }

        // คลิกซ้ายปกติ (PRIMARY)
        if (event.getButton() == MouseButton.PRIMARY) {
            int gridX = hoverGridX;
            int gridY = hoverGridY;
            int mapSize = MapManager.getInstance().getMapSize();

            if (gridX >= 0 && gridX < mapSize && gridY >= 0 && gridY < mapSize) {
                handleGridClick(gridX, gridY);
            }
        }
    }

    private static void handleGridClick(int x, int y) {
        if (currentMode == MODE_IDLE) {
            return; // อนาคตค่อยเพิ่มระบบคลิกดูรายละเอียดตึก (Inspect) ตรงนี้
        }

        else if (currentMode == MODE_BUILD) {
            if (!MapManager.getInstance().getBuildingIdAt(x, y).equals(MapManager.EMPTY_TILE_ID)) {
                System.out.println("❌ สร้างไม่ได้ มีตึกอยู่แล้ว!");
                return;
            }
            MapManager.getInstance().setTile(x, y, selectedBuildingId);
            BaseBuilding newBuilding = BuildingFactory.createBuilding(selectedBuildingId, x, y);
            if (newBuilding != null) {
                SimulationManager.getInstance().registerBuilding(newBuilding);
                System.out.println("✅ Built: " + selectedBuildingId + " at " + x + "," + y);
            }
        }

        else if (currentMode == MODE_DEMOLISH) {
            String existingId = MapManager.getInstance().getBuildingIdAt(x, y);
            if (existingId != null && !existingId.equals(MapManager.EMPTY_TILE_ID)) {
                MapManager.getInstance().setTile(x, y, MapManager.EMPTY_TILE_ID);
                SimulationManager.getInstance().removeBuildingAt(x, y);
            }
        }
    }
}