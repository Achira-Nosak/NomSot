package Logic.Core;

import Model.BaseBuilding;
import GUI.GUIServices.MapManager;

public class GridDirtyFlag {
    private static GridDirtyFlag instance;
    private boolean isGridDirty = true;

    // Map
    private boolean[][] powerMap;
    private boolean[][] waterMap;

    private GridDirtyFlag() {}

    public static GridDirtyFlag getInstance() {
        if (instance == null) instance = new GridDirtyFlag();
        return instance;
    }

    // Getter ให้ฝั่ง GUI ดึงไปวาดหน้าจอ
    public boolean[][] getPowerMap() { return powerMap; }
    public boolean[][] getWaterMap() { return waterMap; }

    public void makeGridDirty() {
        this.isGridDirty = true;
    }

    public void updateIfGridDirty() {
        if (!this.isGridDirty) return;

        System.out.println("Re-calculating Power and Water Grid...");

        int mapSize = MapManager.getInstance().getMapSize();

        powerMap = new boolean[mapSize][mapSize];
        waterMap = new boolean[mapSize][mapSize];

        SimulationManager sim = SimulationManager.getInstance();
        BaseBuilding[] buildings = sim.getBuildingRefs();
        int count = sim.getCurrentCount();

        // 1: ให้ Provider ระบายสีรัศมี
        for (int i = 0; i < count; i++) {
            BaseBuilding b = buildings[i];
            if (b == null) continue;

            int bx = b.getGridX();
            int by = b.getGridY();

            int radius = (int) b.getStats().getServiceRadius();

            if (b.getStats().getPowerProduction() > 0) {
                paintRadius(powerMap, bx, by, radius, mapSize);
            }
            if (b.getStats().getWaterProduction() > 0) {
                paintRadius(waterMap, bx, by, radius, mapSize);
            }
        }


        // 2: เช็คสถานะตึก (ถ้าขาดน้ำไฟ ให้หยุดทำงาน)
        for (int i = 0; i < count; i++) {
            BaseBuilding b = buildings[i];
            if (b == null) continue;

            int bx = b.getGridX();
            int by = b.getGridY();

            boolean hasPower = true;
            boolean hasWater = true;

            // ตึกต้องการไฟ แต่ช่องที่อยู่ไม่มีสีไฟ
            if (b.getStats().getPowerConsumption() > 0 && !powerMap[bx][by]) {
                hasPower = false;
            }
            // ตึกต้องการน้ำ แต่ช่องที่อยู่ไม่มีสีน้ำ
            if (b.getStats().getWaterConsumption() > 0 && !waterMap[bx][by]) {
                hasWater = false;
            }


            boolean isOp = hasPower && hasWater;
            sim.setIsOperational(i, isOp);
        }

        isGridDirty = false;
        System.out.println("✅ Grid Updated Successfully!");
    }


    // Helper Method: ระบายสี
    private void paintRadius(boolean[][] map, int centerX, int centerY, int radius, int mapSize) {
        int startX = Math.max(0, centerX - radius);
        int endX = Math.min(mapSize - 1, centerX + radius);
        int startY = Math.max(0, centerY - radius);
        int endY = Math.min(mapSize - 1, centerY + radius);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                map[x][y] = true;
            }
        }
    }
}