package Logic.Stats;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Logic.Stats.StatsManagerPopulation;
import Logic.Stats.CityMasterStats;

class StatsManagerPopulationTest {

    private CityMasterStats master;

    // เคลียร์ค่าประชากรและความสุขก่อนเริ่มเทส
    @BeforeEach
    void setUp() {
        master = CityMasterStats.getInstance();
        master.population.setPopMax(0);
        master.population.setPopCurrent(0);
        master.social.setHappiness(100.0); // เซ็ตความสุขตั้งต้นไว้ที่ 100%
    }

    // เช็คว่า Singleton สามารถเรียกใช้งานได้
    @Test
    void testSingletonInstance() {
        StatsManagerPopulation manager1 = StatsManagerPopulation.getInstance();
        StatsManagerPopulation manager2 = StatsManagerPopulation.getInstance();
        assertNotNull(manager1);
        assertEquals(manager1, manager2);
    }

    // เช็คกรณีเมืองมีความสุขเต็ม 100% คนต้องย้ายเข้ามาอยู่เต็มความจุ (Occupancy = 1.0)
    @Test
    void testProcessTickFullHappiness() {
        StatsManagerPopulation manager = StatsManagerPopulation.getInstance();

        master.social.setHappiness(100.0);

        // ส่งความจุบ้าน (Max Pop) ไป 500 คน
        manager.processTick(500.0);

        // ความสุข 100% คนต้องอยู่ครบ 500 คน
        assertEquals(1.0, manager.getOccupancyRate());
        assertEquals(500, master.population.getPopMax());
        assertEquals(500, master.population.getPopCurrent());
    }

    // เช็คกรณีเมืองไม่มีความสุขเลย (0%) คนต้องย้ายออกหมดกลายเป็นเมืองร้าง (Occupancy = 0.0)
    @Test
    void testProcessTickZeroHappiness() {
        StatsManagerPopulation manager = StatsManagerPopulation.getInstance();

        master.social.setHappiness(0.0);

        manager.processTick(500.0);

        // ความสุข 0% คนต้องเหลือ 0 คน
        assertEquals(0.0, manager.getOccupancyRate());
        assertEquals(500, master.population.getPopMax()); // ความจุบ้านยังเท่าเดิม
        assertEquals(0, master.population.getPopCurrent()); // แต่ไม่มีคนอยู่เลย
    }

    // เช็คกรณีความสุขอยู่ระดับปานกลาง (เช่น 50%) คนต้องเข้ามาอยู่แค่ครึ่งเดียวของความจุ
    @Test
    void testProcessTickPartialHappiness() {
        StatsManagerPopulation manager = StatsManagerPopulation.getInstance();

        master.social.setHappiness(50.0); // ความสุข 50%

        manager.processTick(1000.0); // ความจุ 1000 คน

        // ความสุข 50% คนต้องมีแค่ 500 คน
        assertEquals(0.5, manager.getOccupancyRate());
        assertEquals(1000, master.population.getPopMax());
        assertEquals(500, master.population.getPopCurrent());
    }
}
