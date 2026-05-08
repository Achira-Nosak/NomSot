package Model;

import Config.Enums.ZoneType;
import Logic.Core.AuraMapManager;
import Logic.Core.SimulationManager;

public class UtilityBuilding extends BaseBuilding implements IResourceProducer, IAuraProvider {

    private double currentPowerProduction;
    private double currentWaterProduction;
    private double currentFoodProduction;
    private double currentAuraRadius;

    private double durability = 100.0;
    private double currentEfficiency = 1.0;

    public UtilityBuilding(String buildingId, int gridX, int gridY) {
        super(buildingId, gridX, gridY, ZoneType.OTHER);
        if (getStats() != null) {
            this.currentPowerProduction = getStats().getPowerProduction();
            this.currentWaterProduction = getStats().getWaterProduction();
            this.currentFoodProduction = getStats().getFoodProduction();
            this.currentAuraRadius = getStats().getServiceRadius();
        }
    }

    @Override
    public void onTick(long currentTick) {
        // คำนวณความเสื่อมโทรมทุกๆ 60 Tick
        if ((currentTick + dataIndex) % 60 != 0) return;

        // ลดความทนทานลงอย่างช้าๆ
        durability -= 0.1;
        if (durability < 0) durability = 0;

        double oldEfficiency = currentEfficiency;

        // ตรวจสอบประสิทธิภาพจากความทนทาน
        if (durability <= 0) {
            currentEfficiency = 0.0;
        } else if (durability < 50.0) {
            currentEfficiency = 0.5;
        } else {
            currentEfficiency = 1.0;
        }


        if (oldEfficiency != currentEfficiency) {
            if (getStats() != null) {
                this.currentPowerProduction = getStats().getPowerProduction() * currentEfficiency;
                this.currentWaterProduction = getStats().getWaterProduction() * currentEfficiency;
                this.currentFoodProduction = getStats().getFoodProduction() * currentEfficiency;
            }
            SimulationManager.getInstance().updateBuildingData(this);
        }
    }

    @Override
    public void produceResources(boolean[][] powerMap, boolean[][] waterMap) {
        int bx = getGridX();
        int by = getGridY();
        int radius = (int) getStats().getServiceRadius();
        int mapSize = powerMap.length;

        // 🌟 โชว์ Polymorphism: ตึกผลิตอะไร ก็ระบายสีอันนั้น
        if (this.currentPowerProduction > 0) {
            paintResourceRadius(powerMap, bx, by, radius, mapSize);
        }
        if (this.currentWaterProduction > 0) {
            paintResourceRadius(waterMap, bx, by, radius, mapSize);
        }
    }

    private void paintResourceRadius(boolean[][] map, int cx, int cy, int radius, int mapSize) {
        int startX = Math.max(0, cx - radius);
        int endX = Math.min(mapSize - 1, cx + radius);
        int startY = Math.max(0, cy - radius);
        int endY = Math.min(mapSize - 1, cy + radius);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                map[x][y] = true;
            }
        }
    }

    @Override
    public boolean isNetworkConnected() {
        return true; // ลอจิกเช็คถนน/สายไฟ
    }


    @Override
    public void applyAuraToSurroundings(AuraMapManager manager) {
        if (getStats().getPollutionIntensity() > 0) {
            manager.paintGradientAura("pollution", gridX, gridY, (int)getAuraRadius(), getStats().getPollutionIntensity());
        }
    }

    @Override
    public double getAuraRadius() {
        return currentAuraRadius;
    }

    public double getCurrentPowerProduction() { return currentPowerProduction; }
    public void setCurrentPowerProduction(double currentPowerProduction) { this.currentPowerProduction = currentPowerProduction; }

    public double getCurrentWaterProduction() { return currentWaterProduction; }
    public void setCurrentWaterProduction(double currentWaterProduction) { this.currentWaterProduction = currentWaterProduction; }

    public double getCurrentFoodProduction() { return currentFoodProduction; }
    public void setCurrentFoodProduction(double currentFoodProduction) { this.currentFoodProduction = currentFoodProduction; }

    public double getCurrentAuraRadius() { return currentAuraRadius; }
    public void setCurrentAuraRadius(double currentAuraRadius) { this.currentAuraRadius = currentAuraRadius; }

    public double getDurability() { return durability; }

    // เผื่ออนาคตทำปุ่มซ่อมใน UI เรียกใช้เมธอดนี้ได้
    public void repair() {
        this.durability = 100.0;
        this.currentEfficiency = 1.0;
        if (getStats() != null) {
            this.currentPowerProduction = getStats().getPowerProduction();
            this.currentWaterProduction = getStats().getWaterProduction();
            this.currentFoodProduction = getStats().getFoodProduction();
        }
        SimulationManager.getInstance().updateBuildingData(this);
    }
}