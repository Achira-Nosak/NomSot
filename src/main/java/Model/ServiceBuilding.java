package Model;

import Config.Enums.ZoneType;

public class ServiceBuilding extends BaseBuilding implements IAuraProvider {

    public ServiceBuilding(String buildingId, int gridX, int gridY) {
        super(buildingId, gridX, gridY, ZoneType.OTHER);
    }

    @Override
    public void onTick(long currentTick) {
        applyAuraToSurroundings();
    }

    @Override
    public void applyAuraToSurroundings() {
        double radius = getAuraRadius();
        // ดึงค่าบัฟต่างๆ จาก getStats() เช่น getHealthBonus(), getSafetyBonus()
        // แล้วลูปแจกบัฟให้ตึกคนอยู่อาศัยที่อยู่ในรัศมี
    }

    @Override
    public double getAuraRadius() {
        return getStats().getServiceRadius();
    }
}
