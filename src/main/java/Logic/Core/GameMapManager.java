package Logic.Core;


/**
 * <ul>
 * <li>Singleton</li>
 * <li>เก็บ Map object ทั้งหมดของเกม ว่าตรง x y นั้นเป็นตึกอะไร ในรูปแบบ string[x][y]</li>
 * <li>Class นี้ถูกเรียกใช้ได้ทุกที่</li>
 * </ul>
 */
public class GameMapManager {
    private static GameMapManager instance;

    // 1. String[][] เพื่อเก็บ ID ของตึกให้ตรงกับ JSON
    private String[][] mapData;
    private final int MAP_SIZE = 1000;

    // ชื่อ ID พื้นฐานสำหรับช่องว่าง
    public static final String EMPTY_TILE_ID = "EMPTY";

    private GameMapManager() {
        mapData = new String[MAP_SIZE][MAP_SIZE];
        initializeMap();
    }

    public static GameMapManager getInstance() {
        if (instance == null) {
            instance = new GameMapManager();
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

    /**
     * ใช้ใน LayerBackground / Render
     */
    // 3. ชื่อและ Type ให้คืนค่าเป็น String
    public String getBuildingIdAt(int x, int y) {
        if (x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE) {
            return mapData[x][y];
        }
        return null; // กรณีอยู่นอกขอบเขต
    }


    /**
     * ใช้ใน InputSensing / วางตึก
     */
    // 4.  Type ของ value เป็น String
    public void setTile(int x, int y, String buildingId) {
        if (x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE) {
            mapData[x][y] = buildingId;
        }
    }

    public int getMapSize() {
        return MAP_SIZE;
    }
}