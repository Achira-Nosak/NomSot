package GUI.GUIServices;

public class MapManager {
    private static MapManager instance;

    // 1. String[][] เพื่อเก็บ ID ของตึกให้ตรงกับ JSON
    private String[][] mapData;
    private final int MAP_SIZE = 1000;

    // กำหนดชื่อ ID พื้นฐานสำหรับช่องว่าง
    public static final String EMPTY_TILE_ID = "EMPTY";

    private MapManager() {
        mapData = new String[MAP_SIZE][MAP_SIZE];
        initializeMap();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    // --- Helper Methods ---

    private void initializeMap() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                // 2. ใส่ค่าเริ่มต้นเป็น "EMPTY"
                mapData[i][j] = EMPTY_TILE_ID;
            }
        }
    }

    // 3. ชื่อและ Type ให้คืนค่าเป็น String (เอาไปใช้ใน LayerBackground)
    public String getBuildingIdAt(int x, int y) {
        if (x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE) {
            return mapData[x][y];
        }
        return null; // กรณีอยู่นอกขอบเขต
    }

    // 4.  Type ของ value เป็น String (เอาไว้ให้ InputSensing เรียกตอนคลิกวางตึก)
    public void setTile(int x, int y, String buildingId) {
        if (x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE) {
            mapData[x][y] = buildingId;
        }
    }

    public int getMapSize() {
        return MAP_SIZE;
    }
}