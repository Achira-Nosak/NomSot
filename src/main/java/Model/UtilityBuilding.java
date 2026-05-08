package Model;

import Config.Enums.ZoneType;

public class UtilityBuilding extends BaseBuilding implements IResourceProducer, IAuraProvider {

    private double currentPowerProduction;
    private double currentWaterProduction;
    private double currentFoodProduction;
    private double currentAuraRadius;

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
        if (isNetworkConnected()) {
            produceResources();
        }
    }

    @Override
    public void produceResources() {
        // ดึงค่าจาก JSON ว่าตึกนี้ผลิตอะไร แล้วส่งเข้า InfrastructureStats
        double power = currentPowerProduction;
        double water = currentWaterProduction;
        // โค้ดอัปเดตทรัพยากร...
    }

    @Override
    public boolean isNetworkConnected() {
        return true; // ลอจิกเช็คถนน/สายไฟ
    }


    @Override
    public void applyAuraToSurroundings() {
        double radius = getAuraRadius();
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
}