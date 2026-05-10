package Logic.Core;

import Model.BaseBuilding;
import Model.IAuraProvider;


/**
 * <ul>
 * <li>Singleton</li>
 * <li>เก็บค่า intensity ของ Stats แต่ละประเภท จากตึก IAuraProvider เป็น Map ในรูปแบบ double[x][y]</li>
 * <li>Aura สามารถทับซ้อนกันได้ ทำให้ intensity เพิ่ม</li>
 * <li>Class นี้ถูกเรียกใช้โดย GridDirtyFlag</li>
 * </ul>
 */
public class AuraMapManager {
    private static AuraMapManager instance;
    private int mapSize;

    private double[][] pollutionMap;
    private double[][] safetyMap;
    private double[][] healthMap;

    private AuraMapManager() {
        this.mapSize = GameMapManager.getInstance().getMapSize();
        pollutionMap = new double[mapSize][mapSize];
        safetyMap = new double[mapSize][mapSize];
        healthMap = new double[mapSize][mapSize];
    }

    public static AuraMapManager getInstance() {
        if (instance == null) instance = new AuraMapManager();
        return instance;
    }


    /**
     * ลูปเรียก building ทุกตัวที่ instance of IAuraProvider มาเรียก applyAuraToSurroundings()
     */
    public void recalculateAuras() {
        clearMaps();
        SimulationManager sim = SimulationManager.getInstance();
        BaseBuilding[] buildings = sim.getBuildingRefs();

        // วนลูปเฉพาะตึกที่มี Interface IAuraProvider และกำลังทำงานอยู่
        for (int i = 0; i < sim.getCurrentCount(); i++) {
            if (buildings[i] != null && sim.getIsOperational(i) && buildings[i] instanceof IAuraProvider) {
                // ให้ตึกจัดการระบายสีออร่าของตัวเองลงใน Manager นี้
                ((IAuraProvider) buildings[i]).applyAuraToSurroundings(this);
            }
        }
    }


    /**
     * Helper Method ให้ ตึกที่มี IAuraProvider เรียกคำนวณ Intensity ให้อัตโนมัติ โดยยิ่งไกลยิ่งจาง (Gradient)
     */
    public void paintGradientAura(String type, int cx, int cy, int radius, double intensity) {
        double[][] targetMap = getMapByType(type);
        if (targetMap == null || radius <= 0) return;

        int startX = Math.max(0, cx - radius);
        int endX = Math.min(mapSize - 1, cx + radius);
        int startY = Math.max(0, cy - radius);
        int endY = Math.min(mapSize - 1, cy + radius);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                double distance = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));
                if (distance <= radius) {
                    // คำนวณค่าความแรงแบบ Linear Falloff (ยิ่งไกลยิ่งจาง)
                    double factor = 1.0 - (distance / radius);
                    targetMap[x][y] += intensity * factor;
                }
            }
        }
    }

    private double[][] getMapByType(String type) {
        return switch (type.toLowerCase()) {
            case "pollution" -> pollutionMap;
            case "safety" -> safetyMap;
            case "health" -> healthMap;
            default -> null;
        };
    }

    private void clearMaps() {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                pollutionMap[i][j] = 0;
                safetyMap[i][j] = 0;
                healthMap[i][j] = 0;
            }
        }
    }

    public double getPollutionAt(int x, int y) { return pollutionMap[x][y]; }
    public double getSafetyAt(int x, int y) { return safetyMap[x][y]; }
    public double getHealthAt(int x, int y) { return healthMap[x][y]; }
}