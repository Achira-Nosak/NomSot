package Model;

import Config.Enums.ZoneType;

public class UtilityBuilding extends BaseBuilding implements IResourceProducer {

    public UtilityBuilding(String buildingId, int gridX, int gridY) {
        super(buildingId, gridX, gridY, ZoneType.OTHER);
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
        double power = getStats().getPowerProduction();
        double water = getStats().getWaterProduction();
        // โค้ดอัปเดตทรัพยากร...
    }

    @Override
    public boolean isNetworkConnected() {
        return true; // ลอจิกเช็คถนน/สายไฟ
    }
}
