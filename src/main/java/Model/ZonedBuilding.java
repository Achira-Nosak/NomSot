package Model;

import Config.Enums.ZoneType;
import Logic.Core.SimulationManager;
import Logic.Stats.CityMasterStats; // อย่าลืม Import เข้ามาใช้งาน

public class ZonedBuilding extends BaseBuilding implements IUpgradable {
    private int currentResidents = 0;
    private int currentWorkers = 0;
    private int upgradeTimer = 0;
    private int downgradeTimer = 0;

    public ZonedBuilding(String buildingId, int gridX, int gridY, ZoneType zoneType) {
        super(buildingId, gridX, gridY, zoneType);
    }

    @Override
    public void onTick(long currentTick) {
        if ((currentTick + dataIndex) % 60 != 0) return;

        // ดึงมลพิษรวมของเมืองมาใช้ชั่วคราว
        double cityPollution = CityMasterStats.getInstance().environment.getDebuffPollutionTotal();

        // ลอจิกตรวจสอบความมั่งคั่ง: ความสุขสูงและเมืองไม่มีมลพิษมากไป
        if (currentHappiness > 80 && cityPollution < 100) {
            upgradeTimer++;
            if (upgradeTimer > 100) {
                if (canUpgrade()) {
                    upgradeLevel();
                    SimulationManager.getInstance().updateBuildingData(this);
                }
                upgradeTimer = 0; // รีเซ็ตเวลาหลังอัปเกรด
            }
        }
        // ลอจิกตรวจสอบความโทรม: ความสุขตกต่ำ หรือ มลพิษเมืองล้นหลาม
        else if (currentHappiness < 20 || cityPollution > 500) {
            downgradeTimer++;
            if (downgradeTimer > 100 && level > 1) {
                level--; // ดาวน์เกรดตึก
                SimulationManager.getInstance().updateBuildingData(this);
                downgradeTimer = 0;
            }
        } else {
            // ถ้าระดับความสุข/มลพิษ ปกติ ให้รีเซ็ตตัวจับเวลา
            upgradeTimer = 0;
            downgradeTimer = 0;
        }
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgradeLevel() {
        level++;
    }

    @Override
    public int getCurrentLevel() { return level; }

    public int getCurrentResidents() { return currentResidents; }
    public void setCurrentResidents(int currentResidents) { this.currentResidents = currentResidents; }

    public int getCurrentWorkers() { return currentWorkers; }
    public void setCurrentWorkers(int currentWorkers) { this.currentWorkers = currentWorkers; }
}