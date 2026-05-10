package Model;

import Config.Enums.ZoneType;
import Logic.Core.AuraMapManager;
import Logic.Core.SimulationManager;
import Logic.Stats.CityMasterStats;


/**
 * คลาสอาคารบริการสาธารณะ (เช่น สถานีตำรวจ โรงพยาบาล ดับเพลิง)
 * <p>Prototype Note: ปัจจุบันสาธิตระบบ Dynamic Budgeting โครงสร้างถูกออกแบบมาเพื่อรองรับ Future Enhancement  เช่น การส่งรถฉุกเฉินออกไปยังจุดเกิดเหตุในอนาคต</p>
 * <p><b>Architecture Highlight:</b></p>
 * <ul>
 * <li>Inheritance: สืบทอดคุณสมบัติพื้นฐานมาจาก BaseBuilding</li>
 * <li>Multiple Interfaces: ทำ Implements IAuraProvider และ IUpgradable</li>
 * </ul>
 */
public class ServiceBuilding extends BaseBuilding implements IAuraProvider, IUpgradable {

    private double currentAuraRadius;
    private int currentBudgetStep = 0;

    public ServiceBuilding(String buildingId, int gridX, int gridY) {
        super(buildingId, gridX, gridY, ZoneType.OTHER);
        if (getStats() != null) {
            this.currentAuraRadius = getStats().getServiceRadius();
        }
    }

    /**
     * <ul>
     * <li>Polymorphism: ถูกเรียกใช้งานพร้อมกับตึกอื่นๆ ใน SimulationManager ผ่าน BaseReference
     * ทำให้สั่งรัน onTick ได้ทันทีโดยไม่ต้องเขียน if-else แยกประเภทตึก</li>
     * <li>Optimization: ใช้ลอจิก (currentTick + dataIndex) % 60 เพื่อกระจายคิว
     * ให้ตึกแต่ละหลังทยอยประมวลผลไม่พร้อมกัน ช่วยลดภาระ CPU และป้องกันเกมกระตุก</li>
     * <li>Prototype Logic: สาธิตระบบรัดเข็มขัด ถ้าเงินติดลบ จะลด radius ครึ่ง, maintCost ถ้่ามากกว่า 500000 จะเพิ่ม radius 1, maintCost แบบขั้นบันไดทีละ 100000
     * เป็นโครงสร้างพื้นฐานที่เตรียมไว้รองรับฟีเจอร์อื่นๆ ในอนาคต</li>
     * </ul>
     */
    @Override
    public void onTick(long currentTick) {
        // เช็คการเงินเมืองทุกๆ 60 Tick
        if ((currentTick + dataIndex) % 60 == 0) {
            double treasury = CityMasterStats.getInstance().finance.getTreasuryCurrent();
            int targetStep = 0;

            if (treasury < 0) {
                targetStep = -1; // ขั้นติดลบ
            } else if (treasury >= 500000) {
                // คำนวณโบนัสขั้นละ 100,000 (สูงสุด 3 ขั้น)
                targetStep = (int) ((treasury - 500000) / 100000) + 1;
                if (targetStep > 3) targetStep = 3;
            }

            // ถ้าขั้นเปลี่ยน ค่อยอัปเดตข้อมูล
            if (currentBudgetStep != targetStep) {
                currentBudgetStep = targetStep;

                if (targetStep == -1) {
                    this.currentAuraRadius = getStats().getServiceRadius() * 0.5;
                    this.currentMaintenance = getStats().getMaintenance() * 0.5;
                } else if (targetStep == 0) {
                    this.currentAuraRadius = getStats().getServiceRadius();
                    this.currentMaintenance = getStats().getMaintenance();
                } else {
                    // เพิ่มรัศมีขั้นละ 1 บล็อก และค่าบำรุงรักษาขั้นละ 20%
                    this.currentAuraRadius = getStats().getServiceRadius() + targetStep;
                    this.currentMaintenance = getStats().getMaintenance() * (1.0 + (0.2 * targetStep));
                }

                SimulationManager.getInstance().updateBuildingData(this);
            }
        }

        if (canUpgrade()) {
            upgradeLevel();
            SimulationManager.getInstance().updateBuildingData(this);
        }
    }

    /**
     * สาธิตการประยุกต์ใช้ Polymorphism ผ่าน Interface เพื่อประมวลผลข้อมูลเชิงพื้นที่ โดยเรียกใช้ helper method AuraMapManager.paintGradientAura()
     */
    @Override
    public void applyAuraToSurroundings(AuraMapManager manager) {
        if (getStats().getSafetyBonus() > 0) {
            manager.paintGradientAura("safety", gridX, gridY, (int)getAuraRadius(), getStats().getSafetyBonus());
        }
        if (getStats().getHealthBonus() > 0) {
            manager.paintGradientAura("health", gridX, gridY, (int)getAuraRadius(), getStats().getHealthBonus());
        }
    }

    @Override
    public double getAuraRadius() {
        return currentAuraRadius;
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

    public double getCurrentAuraRadius() { return currentAuraRadius; }
    public void setCurrentAuraRadius(double currentAuraRadius) { this.currentAuraRadius = currentAuraRadius; }
}