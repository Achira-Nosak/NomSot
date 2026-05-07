package Model;

import Config.Enums.ZoneType;

public class ZonedBuilding extends BaseBuilding implements IUpgradable {
    private int currentLevel = 1;
    private int currentOccupants = 0; // จำนวนคนหรือพนักงานปัจจุบัน

    public ZonedBuilding(String buildingId, int gridX, int gridY, ZoneType zoneType) {
        super(buildingId, gridX, gridY, zoneType);
    }

    @Override
    public void onTick(long currentTick) {
        // เช็คว่าคนอยู่อย่างมีความสุขไหม น้ำไฟพอไหม ถ้าพอถึงเกณฑ์ก็อัปเกรด
        if (canUpgrade()) {
            upgradeLevel();
        }
    }

    @Override
    public boolean canUpgrade() {
        // ลอจิกเช็ค Land Value หรือ Happiness
        return false;
    }

    @Override
    public void upgradeLevel() {
        currentLevel++;
        // เปลี่ยนโมเดลตึก หรือเพิ่มความจุก็ว่าไป
    }

    @Override
    public int getCurrentLevel() { return currentLevel; }
}
