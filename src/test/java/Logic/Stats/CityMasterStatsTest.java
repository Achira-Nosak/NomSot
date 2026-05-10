package Logic.Stats;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CityMasterStatsTest {

    // เช็คว่า Singleton สามารถเรียกใช้งานได้และเป็น instance เดียวกันเสมอ
    @Test
    void testSingletonInstance() {
        CityMasterStats stats1 = CityMasterStats.getInstance();
        CityMasterStats stats2 = CityMasterStats.getInstance();
        assertNotNull(stats1);
        assertEquals(stats1, stats2);
    }

    // เช็คว่าระบบป้องกันการใส่ค่าเกินขอบเขต (Math.clamp) ทำงานถูกต้องในหมวดการเงิน
    @Test
    void testFinancialBoundsClamp() {
        CityMasterStats stats = CityMasterStats.getInstance();

        // ลองเซ็ตดอกเบี้ยเกิน 0.1 ระบบต้องตบกลับมาที่ขีดสุดคือ 0.1
        stats.finance.setLoanInterestRate(0.5);
        assertEquals(0.1, stats.finance.getLoanInterestRate());

        // ลองเซ็ต Buff ภาษีต่ำกว่า 1.0 ระบบต้องดันขึ้นมาที่ฐาน 1.0
        stats.finance.setTaxRevenueBuff(0.5);
        assertEquals(1.0, stats.finance.getTaxRevenueBuff());
    }

    // เช็คลอจิกความสัมพันธ์ของจำนวนประชากร (ต้องไม่เกิน popMax และไม่ต่ำกว่า 0)
    @Test
    void testPopulationCurrentVsMax() {
        CityMasterStats stats = CityMasterStats.getInstance();

        // กำหนดขีดจำกัดประชากรสูงสุดไว้ที่ 100
        stats.population.setPopMax(100);

        // ลองยัดประชากรเข้าเมืองไป 150 ระบบต้องตัดเหลือแค่ 100
        stats.population.setPopCurrent(150);
        assertEquals(100, stats.population.getPopCurrent());

        // ลองใส่ประชากรติดลบ ระบบต้องดันกลับมาที่ 0
        stats.population.setPopCurrent(-20);
        assertEquals(0, stats.population.getPopCurrent());
    }

    // เช็คว่าการกำหนด Supply และ Demand ไม่สามารถติดลบได้ (Math.max)
    @Test
    void testInfrastructureNoNegative() {
        CityMasterStats stats = CityMasterStats.getInstance();

        // ใส่ค่าติดลบเข้าไป ผลลัพธ์ต้องออกมาเป็น 0 เสมอ
        stats.infrastructure.setPowerSupply(-500);
        assertEquals(0, stats.infrastructure.getPowerSupply());
    }

    // เช็คระบบนับจำนวนตึก (ConcurrentHashMap) ว่าบวก/ลบถูกต้อง และรับมือการลบตอนไม่มีตึกได้
    @Test
    void testBuildingCounterLogic() {
        CityMasterStats stats = CityMasterStats.getInstance();
        String testId = "PowerPlant_Test";

        // สร้างตึก 2 หลัง
        stats.building.incrementCount(testId);
        stats.building.incrementCount(testId);
        assertEquals(2, stats.building.getCount(testId));
        assertTrue(stats.building.hasBuilding(testId));

        // ทุบทิ้ง 1 หลัง
        stats.building.decrementCount(testId);
        assertEquals(1, stats.building.getCount(testId));

        // ทุบทิ้งอีก 1 หลัง (ตึกต้องหายไปจากระบบแบบสมบูรณ์)
        stats.building.decrementCount(testId);
        assertEquals(0, stats.building.getCount(testId));
        assertFalse(stats.building.hasBuilding(testId));

        // ลองสั่งทุบตึกตอนที่ไม่มีตึกเหลือแล้ว (ต้องไม่ Error โปรแกรมไม่พัง)
        stats.building.decrementCount(testId);
        assertEquals(0, stats.building.getCount(testId));
    }
}
