package Model;

import Config.BuildingData;
import Config.ConfigLoader;
import Config.Enums.ZoneType;

public abstract class BaseBuilding {
    protected String buildingId; // ID ที่ตรงกับ JSON
    protected int gridX;
    protected int gridY;
    protected int dataIndex; // Index ใน DOD Array ของ SimulationManager
    protected ZoneType zoneType;

    public BaseBuilding(String buildingId, int gridX, int gridY, ZoneType zoneType) {
        this.buildingId = buildingId;
        this.gridX = gridX;
        this.gridY = gridY;
        this.zoneType = zoneType;
    }

    // ดึง Data ข้อมูลดิบจาก JSON
    public BuildingData getStats() {
        return ConfigLoader.getBuildingConfig(buildingId);
    }


    // SimulationManager จะเรียกตัวนี้ทุกๆ 3 Tick
    public abstract void onTick(long currentTick);


    public String getBuildingId() { return buildingId; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public int getDataIndex() { return dataIndex; }
    public void setDataIndex(int index) { this.dataIndex = index; }
    public ZoneType getZoneType() { return zoneType; }
}