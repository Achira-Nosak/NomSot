package Model;

import Config.Enums.ZoneType;
import Logic.Core.SimulationManager;
import Logic.Stats.CityMasterStats;

/**
 * คลาสพื้นที่จัดสรร (Residential / Commercial / Industrial / Agricultural)
 * <p>Prototype Note: ปัจจุบันใช้แบบจำลองการเติบโตแบบ Timer-based เป็นโครงสร้างตั้งต้นที่พร้อมรองรับ Future Enhancement เช่น การคำนวณมูลค่าที่ดิน (Land Value) หรือระบบอุปสงค์อุปทาน (Supply/Demand)</p>
 * <p><b>Architecture Highlight:</b></p>
 * <ul>
 * <li>Inheritance: สืบทอดคุณสมบัติพื้นฐานมาจาก BaseBuilding</li>
 * <li>Multiple Interfaces: ทำ Implements IUpgradable</li>
 * </ul>
 */
public class ZonedBuilding extends BaseBuilding implements IUpgradable {
    private int currentResidents = 0;
    private int currentWorkers = 0;
    private int upgradeTimer = 0;
    private int downgradeTimer = 0;

    public ZonedBuilding(String buildingId, int gridX, int gridY, ZoneType zoneType) {
        super(buildingId, gridX, gridY, zoneType);
    }


    /**
     * <ul>
     * <li>Polymorphism: ถูกเรียกใช้งานพร้อมกับตึกอื่นๆ ใน SimulationManager ผ่าน BaseReference
     * ทำให้สั่งรัน onTick ได้ทันทีโดยไม่ต้องเขียน if-else แยกประเภทตึก</li>
     * <li>Optimization: ใช้ลอจิก (currentTick + dataIndex) % 60 เพื่อกระจายคิว
     * ให้ตึกแต่ละหลังทยอยประมวลผลไม่พร้อมกัน ช่วยลดภาระ CPU และป้องกันเกมกระตุก</li>
     * <li>Prototype Logic: สาธิตระบบ Upgrade/Downgrade ตึก โดยใช้ Timer หน่วงเวลาเช็คค่าความสุขและมลพิษต้องมีค่าสูง/ต่ำ ติดต่อกันนานระยะหนึ่ง ถึงจะเปลี่ยนเลเวล
     * เป็นโครงสร้างพื้นฐานที่เตรียมไว้รองรับฟีเจอร์อื่นๆ ในอนาคต</li>
     * </ul>
     */
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
        return level < 3;
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