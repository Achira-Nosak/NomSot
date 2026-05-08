package Logic.Stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CityMasterStatsTest {

    private CityMasterStats stats;

    @BeforeEach
    void setUp() {
        // ดึง Instance มาใช้ก่อนเริ่มแต่ละ Test
        stats = CityMasterStats.getInstance();
    }

    @Test
    @DisplayName("Singleton Test: ต้องคืนค่า Instance ตัวเดิมเสมอ")
    void testSingleton() {
        CityMasterStats anotherInstance = CityMasterStats.getInstance();
        assertSame(stats, anotherInstance, "CityMasterStats ต้องเป็น Singleton");
    }

    @Test
    @DisplayName("FinancialStats: ตรวจสอบการ Clamp ค่าอัตราดอกเบี้ยและงบประมาณ")
    void testFinancialConstraints() {
        CityMasterStats.FinancialStats finance = stats.finance;

        // ทดสอบ Loan Interest Rate (0.0 - 0.1)
        finance.setLoanInterestRate(0.5); // เกินขอบบน
        assertEquals(0.1, finance.getLoanInterestRate(), 0.001);

        finance.setLoanInterestRate(-1.0); // เกินขอบล่าง
        assertEquals(0.0, finance.getLoanInterestRate(), 0.001);

        // ทดสอบ Maintenance Cost (ห้ามติดลบ)
        finance.setMaintenanceCostBase(-500);
        assertEquals(0, finance.getMaintenanceCostBase());
    }

    @Test
    @DisplayName("PopulationStats: ตรวจสอบว่าประชากรต้องไม่เกินค่าสูงสุดและไม่ติดลบ")
    void testPopulationConstraints() {
        CityMasterStats.PopulationStats pop = stats.population;

        pop.setPopMax(1000);

        pop.setPopCurrent(1500); // เกิน Max
        assertEquals(1000, pop.getPopCurrent());

        pop.setPopCurrent(-10); // ต่ำกว่า 0
        assertEquals(0, pop.getPopCurrent());
    }

    @Test
    @DisplayName("SocialStats: ตรวจสอบระดับการศึกษา (Vocational ต้องไม่เกิน Basic)")
    void testEducationConstraints() {
        CityMasterStats.SocialStats social = stats.social;

        social.setEducationBasic(500);

        social.setEducationVocational(600); // เกินค่าพื้นฐาน
        assertEquals(500, social.getEducationVocational());

        social.setEducationGraduate(100); // อยู่ในขอบเขต
        assertEquals(100, social.getEducationGraduate());
    }

    @Test
    @DisplayName("EnvironmentalStats: ตรวจสอบการ Clamp ค่ามลพิษ (0 - 1000)")
    void testEnvironmentalConstraints() {
        CityMasterStats.EnvironmentalStats env = stats.environment;

        env.setDebuffPollutionAir(2000); // เกิน 1000
        assertEquals(1000, env.getDebuffPollutionAir());

        env.setDebuffPollutionWater(-50); // ต่ำกว่า 0
        assertEquals(0, env.getDebuffPollutionWater());
    }

    @Test
    @DisplayName("BuildingStats: ทดสอบระบบนับจำนวนตึก")
    void testBuildingCounts() {
        CityMasterStats.BuildingStats bld = stats.building;
        String testId = "Power_Plant";

        // เริ่มต้นต้องไม่มีตึก
        int initialCount = bld.getCount(testId);

        // ทดสอบการเพิ่ม
        bld.incrementCount(testId);
        bld.incrementCount(testId);
        assertEquals(initialCount + 2, bld.getCount(testId));
        assertTrue(bld.hasBuilding(testId));

        // ทดสอบการลด
        bld.decrementCount(testId);
        assertEquals(initialCount + 1, bld.getCount(testId));

        // ลดจนหมด
        bld.decrementCount(testId);
        assertFalse(bld.hasBuilding(testId));
    }

    @Test
    @DisplayName("ZoningStats: ตรวจสอบความต้องการโซน (Demand -100 ถึง 100)")
    void testZoningDemand() {
        CityMasterStats.ZoningStats zone = stats.zoning;

        zone.setResidentialDemand(150); // เกิน 100
        assertEquals(100, zone.getResidentialDemand());

        zone.setIndustrialDemand(-200); // ต่ำกว่า -100
        assertEquals(-100, zone.getIndustrialDemand());
    }
}