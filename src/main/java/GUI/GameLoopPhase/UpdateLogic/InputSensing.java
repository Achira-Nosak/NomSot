package GUI.GameLoopPhase.UpdateLogic;

import GUI.GUIServices.GUIManager;
import GUI.GUIServices.CameraManager;
import Logic.Core.GameMapManager;
import Logic.Core.SimulationManager;
import Model.BaseBuilding;
import Model.BuildingFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


/**
 * ศูนย์กลางจัดการคำสั่งผู้เล่น (Input Sensing)
 * <p>Prototype Note: ปัจจุบันรองรับแค่ Mouse และออกแบบโครงสร้างไว้ 3 โหมดหลัก (Idle, Build, Demolish) ซึ่งรองรับการต่อยอดการใช้ keyboard หรือลูกเล่นโหมดอื่นๆ (เช่น Inspect) ในอนาคต</p>
 * * <p>Architecture:</p>
 * <ul>
 * <li>Finite State Machine: ควบคุมพฤติกรรม Mouse ด้วยระบบ State ให้การคลิกซ้ายปุ่มเดียว สามารถให้ผลลัพธ์ที่ต่างกันได้ตามโหมดที่กำลัง Active อยู่</li>
 * <li>Inverse Isometric Math: ใช้สมการคณิตศาสตร์ย้อนกลับเพื่อแปลงพิกัดเมาส์บนจอ (2D Flat) ให้กลายเป็นพิกัด Array บนแผนที่ (2.5D Grid) และชดเชยมุมกล้อง</li>
 * <li>Separation of Concerns: คลาสนี้ทำหน้าที่เป็นเพียง "ผู้รับสาร" (Controller) แล้วส่งพิกัดไปให้ SimulationManager ทำหน้าที่ "ตัดสินใจ" (Logic)</li>
 * </ul>
 * * <p>Execution Pipeline:</p>
 * <ul>
 * <li>1. Initialize: SetMouse() ผูก Listener ดักจับ Event เมาส์ทั้งหมดไว้ที่ Canvas ตอนเริ่มเกม</li>
 * <li>2. Tracking (Move): ลำเลียงพิกัดจอไปให้กล้อง (CameraManager) และแปลงเป็นพิกัด Grid เก็บไว้เพื่อทำระบบ Preview แบบ Real-time</li>
 * <li>3. Action (Click): รับคำสั่งคลิก เช็ค State ปัจจุบัน (Idle/Build/Demolish) แล้วแจกจ่ายพิกัดไปให้ระบบหลังบ้านทำงาน (สร้างหรือทุบตึก)</li>
 * </ul>
 */
public class InputSensing {

    // ตัวแปรเก็บพิกัดเมาส์บนหน้าจอ (สำหรับ Camera เลื่อนจอ)
    private static double currentScreenX = 0;
    private static double currentScreenY = 0;

    // สถานะ MODE_IDLE (สำหรับ handleGridClick)
    public static final int MODE_IDLE = 0;
    public static final int MODE_BUILD = 1;
    public static final int MODE_DEMOLISH = 2;
    private static int currentMode = MODE_IDLE; // เริ่มเกมมาให้เมาส์ว่าง
    private static String selectedBuildingId = "";

    // ตัวแปรเก็บตำแหน่ง Grid ที่เมาส์ชี้อยู่ (สำหรับ Preview)
    private static int hoverGridX = -1;
    private static int hoverGridY = -1;

    /**
     * เรียกใช้ครั้งเดียวใน Game Start
     */
    public static void SetMouse() {
        Canvas canvas = GUIManager.getInstance().getGameCanvas();

        canvas.setOnMouseClicked(event -> handleMouseClick(event, canvas));

        canvas.setOnMouseMoved(event -> handleMouseMove(event, canvas));

        canvas.setOnScroll(event -> CameraManager.getInstance().handleZoom(event.getDeltaY()));
    }


    // CHANGE MOUSE MODE
    // ------------------------------------------------------------------------------
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
    // ------------------------------------------------------------------------------


    // MOUSE MOVE
    // ------------------------------------------------------------------------------
    /**
     * Main MouseMove Handle
     */
    private static void handleMouseMove(MouseEvent event, Canvas canvas) {

        // เก็บพิกัดหน้าจอล่าสุดเอาไว้
        currentScreenX = event.getX();
        currentScreenY = event.getY();

        // คำนวณ Isometric
        int[] coords = getGridCoords(event.getX(), event.getY(), canvas.getWidth());
        hoverGridX = coords[0];
        hoverGridY = coords[1];
    }

    /**
     * Helper Method ของ handleMouseMove คำนวณ Inverse Isometric
     */
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
    // ------------------------------------------------------------------------------



    // MOUSE CLICK
    // ------------------------------------------------------------------------------
    /**
     * Main MouseClick Handle
     */
    private static void handleMouseClick(MouseEvent event, Canvas canvas) {
        // คลิกขวา (SECONDARY) เพื่อยกเลิกกลับไปสถานะว่าง
        if (event.getButton() == MouseButton.SECONDARY) {
            setIdleMode();
            return;
        }

        // คลิกซ้ายปกติ (PRIMARY)
        if (event.getButton() == MouseButton.PRIMARY) {
            int gridX = hoverGridX;
            int gridY = hoverGridY;
            int mapSize = GameMapManager.getInstance().getMapSize();

            if (gridX >= 0 && gridX < mapSize && gridY >= 0 && gridY < mapSize) {
                handleGridClick(gridX, gridY);
            }
        }
    }


    /**
     * Helper Method ของ handleMouseClick จัดการใน mouse แต่ละโหมด
     * <ul>
     * <li>MODE_IDLE: </li>
     * <li>MODE_BUILD: </li>
     * <li>MODE_DEMOLISH: </li>
     * </ul>
     */
    private static void handleGridClick(int x, int y) {
        if (currentMode == MODE_IDLE) {
            return; // อนาคตค่อยเพิ่มระบบคลิกดูรายละเอียดตึก (Inspect) ตรงนี้
        }

        else if (currentMode == MODE_BUILD) {
            if (!GameMapManager.getInstance().getBuildingIdAt(x, y).equals(GameMapManager.EMPTY_TILE_ID)) {
                System.out.println("สร้างไม่ได้ มีตึกอยู่แล้ว!");
                return;
            }

            // สร้าง Object ตึกจำลองขึ้นมาเพื่อใช้เช็คเงื่อนไข
            BaseBuilding newBuilding = BuildingFactory.createBuilding(selectedBuildingId, x, y);

            if (newBuilding != null) {
                // เรียกใช้ลอจิกจาก SimulationManager
                if (SimulationManager.getInstance().canConstruct(newBuilding)) {
                    SimulationManager.getInstance().construct(newBuilding);
                    System.out.println("Built: " + selectedBuildingId + " at " + x + "," + y);
                } else {
                    System.out.println("สร้างไม่ได้: ทรัพยากรไม่พอ หรือพื้นที่ไม่เหมาะสม!");
                }
            }
        }

        else if (currentMode == MODE_DEMOLISH) {
            String existingId = GameMapManager.getInstance().getBuildingIdAt(x, y);
            if (existingId != null && !existingId.equals(GameMapManager.EMPTY_TILE_ID)) {
                GameMapManager.getInstance().setTile(x, y, GameMapManager.EMPTY_TILE_ID);
                SimulationManager.getInstance().removeBuildingAt(x, y);
            }
        }
    }
    // ------------------------------------------------------------------------------


    // --- Getters สำหรับดึงไปวาดภาพ Preview และดึงไปให้ CameraManager ---
    public static int getCurrentMode() { return currentMode; }
    public static String getSelectedBuildingId() { return selectedBuildingId; }
    public static int getHoverGridX() { return hoverGridX; }
    public static int getHoverGridY() { return hoverGridY; }
    public static double getCurrentScreenX() { return currentScreenX; }
    public static double getCurrentScreenY() { return currentScreenY; }
}