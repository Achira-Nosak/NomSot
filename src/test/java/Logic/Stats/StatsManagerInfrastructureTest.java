package Logic.Stats;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Logic.Stats.StatsManagerInfrastructure;
import Logic.Stats.CityMasterStats;

class StatsManagerInfrastructureTest {

    private CityMasterStats master;

    // เคลียร์ค่าสาธารณูปโภคทั้งหมดให้เป็น 0 ก่อนเริ่มเทส
    @BeforeEach
    void setUp() {
        master = CityMasterStats.getInstance();
        master.infrastructure.setPowerDemand(0);
        master.infrastructure.setWaterDemand(0);
        master.infrastructure.setFoodDemand(0);
        master.infrastructure.setPowerSupply(0);
        master.infrastructure.setWaterSupply(0);
        master.infrastructure.setFoodSupply(0);
    }

    // เช็คว่า Singleton สามารถเรียกใช้งานได้
    @Test
    void testSingletonInstance() {
        StatsManagerInfrastructure manager1 = StatsManagerInfrastructure.getInstance();
        StatsManagerInfrastructure manager2 = StatsManagerInfrastructure.getInstance();
        assertNotNull(manager1);
        assertEquals(manager1, manager2);
    }

    // เช็คว่าการรับค่า Demand และ Supply ถูกจัดเก็บเข้าฐานข้อมูลตรงหมวดหมู่ (ไม่สลับช่องกัน)
    @Test
    void testProcessTickUpdatesCorrectly() {
        StatsManagerInfrastructure manager = StatsManagerInfrastructure.getInstance();

        // จำลองสถานการณ์: Demand (ไฟ 100, น้ำ 200, อาหาร 300) | Supply (ไฟ 150, น้ำ 250, อาหาร 350)
        manager.processTick(100.0, 200.0, 300.0, 150.0, 250.0, 350.0);

        // ตรวจสอบฝั่ง Demand
        assertEquals(100.0, master.infrastructure.getPowerDemand());
        assertEquals(200.0, master.infrastructure.getWaterDemand());
        assertEquals(300.0, master.infrastructure.getFoodDemand());

        // ตรวจสอบฝั่ง Supply
        assertEquals(150.0, master.infrastructure.getPowerSupply());
        assertEquals(250.0, master.infrastructure.getWaterSupply());
        assertEquals(350.0, master.infrastructure.getFoodSupply());
    }

    // เช็คกรณีมีการส่งค่าติดลบเข้ามา (เทสการทำงานร่วมกับ CityMasterStats ว่ากรองข้อมูลขยะได้)
    @Test
    void testProcessTickWithNegativeValues() {
        StatsManagerInfrastructure manager = StatsManagerInfrastructure.getInstance();

        // จำลองการส่งค่าติดลบ (อาจเกิดจากบั๊กของลอจิกเมือง)
        manager.processTick(-50.0, -10.0, -5.0, -100.0, -20.0, -15.0);

        // ฐานข้อมูลต้องดันค่ากลับมาที่ 0 ทันที ป้องกันตัวเลขในเกมรวน
        assertEquals(0.0, master.infrastructure.getPowerDemand());
        assertEquals(0.0, master.infrastructure.getPowerSupply());
    }
}
