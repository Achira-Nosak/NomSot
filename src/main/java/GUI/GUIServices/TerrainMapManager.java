package GUI.GUIServices;

import Logic.Core.GameMapManager;

import java.util.Random;

public class TerrainMapManager {
    private static TerrainMapManager instance;

    private int[][] terrainMap;
    private int mapSize;

    // รหัสประเภทพื้นดิน
    public static final int TERRAIN_GRASS = 0;
    public static final int TERRAIN_WATER = 1;

    private TerrainMapManager() {}

    public static TerrainMapManager getInstance() {
        if (instance == null) {
            instance = new TerrainMapManager();
        }
        return instance;
    }


    // 1. LOGIC: สร้างแผนที่ (ดึงขนาดจาก ObjectMapManager อัตโนมัติ)
    public void initMap() {
        this.mapSize = GameMapManager.getInstance().getMapSize();
        this.terrainMap = new int[mapSize][mapSize];

        // ถมที่ด้วยหญ้าทั้งหมดก่อน
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                terrainMap[x][y] = TERRAIN_GRASS;
            }
        }

        // สุ่มขุดแม่น้ำ
        generateRiver();
    }

    private void generateRiver() {
        Random rand = new Random();
        // สุ่มจุดเกิดแม่น้ำให้อยู่ช่วงบนของแผนที่ก่อน
        int currentX = 5 + rand.nextInt(10);
        int riverWidth = 5;

//        System.out.println("River started at X: " + currentX);

        for (int y = 0; y < mapSize; y++) {
            // โอกาสหักเลี้ยว 30%
            if (rand.nextDouble() > 0.7) {
                currentX += (rand.nextBoolean() ? 1 : -1);
            }
            // ล็อกให้อยู่ในกรอบแผนที่
            currentX = Math.max(0, Math.min(mapSize - riverWidth, currentX));

            for (int w = 0; w < riverWidth; w++) {
                terrainMap[currentX + w][y] = TERRAIN_WATER;
            }
        }
    }


    // 2. GETTERS (สำหรับให้ LayerBackground หรือระบบวางตึกเรียกใช้)
    public int getTerrainAt(int x, int y) {
        if (x >= 0 && x < mapSize && y >= 0 && y < mapSize) {
            return terrainMap[x][y];
        }
        return TERRAIN_GRASS; // ถ้านอกขอบเขต ถือว่าเป็นหญ้าไปเลย
    }

    public boolean isWater(int x, int y) {
        return getTerrainAt(x, y) == TERRAIN_WATER;
    }

    public boolean isGrass(int x, int y) {
        return getTerrainAt(x, y) == TERRAIN_GRASS;
    }

    public int getMapSize() {
        return mapSize;
    }
}