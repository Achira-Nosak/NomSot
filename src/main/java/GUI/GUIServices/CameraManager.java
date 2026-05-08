package GUI.GUIServices;

import Logic.Core.GameMapManager;

public class CameraManager {
    private static CameraManager instance;
    private double x = 0;
    private double y = 0;
    private double tileSize = 32.0;
    private final double MIN_TILE_SIZE = 32.0;  // ซูมออกสุด (กระเบื้องเล็กสุด)
    private final double MAX_TILE_SIZE = 128.0; // ซูมเข้าสุด (กระเบื้องใหญ่สุด)

    private CameraManager() {}

    public static CameraManager getInstance() {
        if (instance == null) instance = new CameraManager();
        return instance;
    }

    // Getter & Setter สำหรับ x, y
    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getTileSize() { return tileSize; }

    //------------------------------------------------------------------------------------------------

    private final double SCROLL_SPEED = 8.0; // ความเร็วกล้อง
    private final double EDGE_THRESHOLD = 30.0; // ระยะห่างจากขอบจอที่จะเริ่มเลื่อน (Pixels)

    public void update(double mouseX, double mouseY, double screenWidth, double screenHeight) {
        // เลื่อนไปทางซ้าย (เมื่อเมาส์ชิดขอบซ้าย)
        if (mouseX < EDGE_THRESHOLD) x -= SCROLL_SPEED;

        // เลื่อนไปทางขวา (เมื่อเมาส์ชิดขอบขวา)
        if (mouseX > screenWidth - EDGE_THRESHOLD) x += SCROLL_SPEED;

        // เลื่อนขึ้น (เมื่อเมาส์ชิดขอบบน)
        if (mouseY < EDGE_THRESHOLD) y -= SCROLL_SPEED;

        // เลื่อนลง (เมื่อเมาส์ชิดขอบล่าง)
        if (mouseY > screenHeight - EDGE_THRESHOLD) y += SCROLL_SPEED;

        // --- จุดสำคัญ: Camera Bounds (กันกล้องหลุดโลก) ---
        double mapSizeInPixels = GameMapManager.getInstance().getMapSize() * tileSize;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > mapSizeInPixels - screenWidth) x = Math.max(0, mapSizeInPixels - screenWidth);
        if (y > mapSizeInPixels - screenHeight) y = Math.max(0, mapSizeInPixels - screenHeight);
    }


    public void handleZoom(double deltaY) {
        double zoomSpeed = 4.0; // ความเร็วในการซูม (เพิ่มทีละ 4 พิกเซล)

        if (deltaY > 0) {
            tileSize += zoomSpeed;
        } else if (deltaY < 0) {
            tileSize -= zoomSpeed;
        }
        tileSize = Math.max(MIN_TILE_SIZE, Math.min(MAX_TILE_SIZE, tileSize));

        // System.out.println("Current TileSize: " + tileSize);
    }

    public void centerOnMap(int mapSize, double screenWidth, double screenHeight) {
        double halfWidth = tileSize / 2.0;
        double halfHeight = tileSize / 4.0;

        // คำนวณพิกัด Isometric ของช่องกึ่งกลาง (mapSize/2, mapSize/2)
        double centerIsoX = 0;
        double centerIsoY = mapSize * halfHeight;
        this.x = centerIsoX;
        this.y = centerIsoY - (screenHeight / 2.0);
    }
}
