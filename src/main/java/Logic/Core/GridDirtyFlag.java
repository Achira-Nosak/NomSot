package Logic.Core;

import Model.BaseBuilding;
import Model.IResourceProducer;


/**
 * <ul>
 * <li>Singleton</li>
 * <li>Dirty Flag Pattern: เทคนิคเกมเพื่อเช็คสถานะการเปลี่ยนแปลงของข้อมูล</li>
 * <li>Optimization: ลดภาระและป้องกันการคำนวณซ้ำซ้อนในทุกเฟรมโดยไม่จำเป็น</li>
 * <li>Triggered : สั่งคำนวณใหม่เมื่อมีการ วางตึก, ทุบตึก, อัปเกรดตึก เท่านั้น</li>
 * <li>Class นี้ถูกเรียกใช้ใน simulationManager</li>
 * </ul>
 */
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


    /**
     * เรียกใช้เมื่อมีการ วางตึก, ทุบตึก, อัปเกรดตึก
     */
    public void makeGridDirty() {
        this.isGridDirty = true;
    }

    /**
     * Main Method เรียกใช้ใน simulationManager เมื่อ Grid Change
     * <p><b>Logic Overview:</b>
     * <ul>
     * <li>ล้างแผนที่น้ำไฟ ให้ตึกที่ IResourceProvider เรียก produceResorce() Map รัศมีน้ำไฟ เบื้องต้น</li>
     * <li>สั่ง AuraMapManager ให้คำนวณ IntensityMap แต่ละ Stat ของเมืองใหม่ ให้เป็นปัจจุบัน</li>
     * <li>เช็คตึกทุกหลัง ว่าจุดที่ตั้งอยู่มีน้ำไฟเข้าถึงมั้ย ถ้าไม่มีก็สั่งหยุดทำงาน (Operational = false)</li>
     * </ul>
     * * <p><b>Future Enhancement:</b>
     * <ul>
     * <li>พัฒนาระบบการลากสายไฟและท่อน้ำจริงๆ (Network System)</li>
     * </ul>
     */
    public void updateIfGridDirty() {
        if (!this.isGridDirty) return;

        System.out.println("Re-calculating Infrastructure Grids...");
        int mapSize = GameMapManager.getInstance().getMapSize();
        powerMap = new boolean[mapSize][mapSize];
        waterMap = new boolean[mapSize][mapSize];

        SimulationManager sim = SimulationManager.getInstance();
        BaseBuilding[] buildings = sim.getBuildingRefs();
        int count = sim.getCurrentCount();

        // 1: ให้แต่ละตึก mark ลงในแผนที่เอง
        for (int i = 0; i < count; i++) {
            if (buildings[i] instanceof IResourceProducer) {
                ((IResourceProducer) buildings[i]).produceResources(powerMap, waterMap);
            }
        }

        // 2: คำนวณออร่าสิ่งแวดล้อม (Pollution, Safety, Healthy)
        AuraMapManager.getInstance().recalculateAuras();

        // 3: เช็คสถานะการทำงาน (DOD Check)
        for (int i = 0; i < count; i++) {
            BaseBuilding b = buildings[i];
            if (b == null) continue;

            // เช็คว่าตึกมี น้ำ ไฟฟ้า มั้ย
            boolean hasPower = b.getStats().getPowerConsumption() <= 0 || powerMap[b.getGridX()][b.getGridY()];
            boolean hasWater = b.getStats().getWaterConsumption() <= 0 || waterMap[b.getGridX()][b.getGridY()];

            sim.setIsOperational(i, hasPower && hasWater);
        }

        isGridDirty = false;
        System.out.println("Re-calculating Grid Success");
    }
}