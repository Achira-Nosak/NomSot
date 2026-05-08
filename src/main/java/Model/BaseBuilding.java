package Model;

import Config.BuildingData;
import Config.ConfigLoader;
import Config.Enums.ZoneType;
import Logic.Stats.CityMasterStats;

public abstract class BaseBuilding {
    protected String buildingId; // ID ที่ตรงกับ JSON
    protected int gridX;
    protected int gridY;
    protected int dataIndex; // Index ใน DOD Array ของ SimulationManager
    protected ZoneType zoneType;

    protected int level = 1;
    protected double currentMaintenance;
    protected double currentTax;
    protected double currentPowerConsume;
    protected double currentWaterConsume;
    protected double currentFoodConsume;
    protected double currentHappiness;

    public BaseBuilding(String buildingId, int gridX, int gridY, ZoneType zoneType) {
        this.buildingId = buildingId;
        this.gridX = gridX;
        this.gridY = gridY;
        this.zoneType = zoneType;

        BuildingData baseStats = ConfigLoader.getBuildingConfig(buildingId);
        if (baseStats != null) {
            this.currentMaintenance = baseStats.getMaintenance();
            this.currentTax = baseStats.getBaseTaxRevenue();
            this.currentPowerConsume = baseStats.getPowerConsumption();
            this.currentWaterConsume = baseStats.getWaterConsumption();
            this.currentFoodConsume = baseStats.getFoodConsumption();
            this.currentHappiness = baseStats.getHappinessBonus();
        }
    }

    // ดึง Data ข้อมูลดิบจาก JSON
    public BuildingData getStats() {
        return ConfigLoader.getBuildingConfig(buildingId);
    }


    // SimulationManager เรียก
    public abstract void onTick(long currentTick);


    public String getBuildingId() { return buildingId; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public int getDataIndex() { return dataIndex; }
    public void setDataIndex(int index) { this.dataIndex = index; }
    public ZoneType getZoneType() { return zoneType; }

    public double getCurrentMaintenance() { return currentMaintenance; }
    public void setCurrentMaintenance(double currentMaintenance) { this.currentMaintenance = currentMaintenance; }

    public double getCurrentTax() { return currentTax; }
    public void setCurrentTax(double currentTax) { this.currentTax = currentTax; }

    public double getCurrentPowerConsume() { return currentPowerConsume; }
    public void setCurrentPowerConsume(double currentPowerConsume) { this.currentPowerConsume = currentPowerConsume; }

    public double getCurrentWaterConsume() { return currentWaterConsume; }
    public void setCurrentWaterConsume(double currentWaterConsume) { this.currentWaterConsume = currentWaterConsume; }

    public double getCurrentFoodConsume() { return currentFoodConsume; }
    public void setCurrentFoodConsume(double currentFoodConsume) { this.currentFoodConsume = currentFoodConsume; }

    public double getCurrentHappiness() { return currentHappiness; }
    public void setCurrentHappiness(double currentHappiness) { this.currentHappiness = currentHappiness; }
}