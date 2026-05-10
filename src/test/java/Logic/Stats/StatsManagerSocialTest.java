package Logic.Stats;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Logic.Stats.StatsManagerSocial;
import Logic.Stats.CityMasterStats;

class StatsManagerSocialTest {

    private CityMasterStats master;

    // เคลียร์ค่าความสุขและมลพิษใน Database ก่อนเริ่มเทสทุกครั้ง
    @BeforeEach
    void setUp() {
        master = CityMasterStats.getInstance();
        master.social.setHappiness(50.0); // เซ็ตความสุขกลางๆ ไว้ก่อน
        master.environment.setDebuffPollutionTotal(0.0); // ล้างมลพิษให้เป็น 0
    }

    // เช็คว่า Singleton สามารถเรียกใช้งานได้
    @Test
    void testSingletonInstance() {
        StatsManagerSocial manager1 = StatsManagerSocial.getInstance();
        StatsManagerSocial manager2 = StatsManagerSocial.getInstance();
        assertNotNull(manager1);
        assertEquals(manager1, manager2);
    }

    // เช็คกรณีเมืองสะอาด (ไม่มีมลพิษเลย) ความสุขต้องไม่ถูกหัก
    @Test
    void testProcessTickNoPollution() {
        StatsManagerSocial manager = StatsManagerSocial.getInstance();

        // ส่งความสุขตั้งต้นเข้ามา 80 มลพิษเป็น 0
        manager.processTick(80.0);

        // ความสุขต้องเหลือ 80.0 เท่าเดิมเป๊ะๆ
        assertEquals(80.0, master.social.getHappiness());
    }

    // เช็คสูตรหักความสุข (มลพิษ 10 หน่วย = หัก 1%)
    @Test
    void testProcessTickWithPollutionPenalty() {
        StatsManagerSocial manager = StatsManagerSocial.getInstance();

        // จำลองสถานการณ์เมืองมีมลพิษ 50 หน่วย (ต้องโดนหักความสุข 5.0)
        master.environment.setDebuffPollutionTotal(50.0);

        // ส่งความสุขตั้งต้นเข้ามา 90
        manager.processTick(90.0);

        // ความสุขต้องเหลือ 85.0 (90 - 5)
        assertEquals(85.0, master.social.getHappiness());
    }

    // เช็คกรณีมลพิษล้นเมือง (ความสุขต้องโดนหักจนเหลือ 0 แต่ห้ามติดลบ)
    @Test
    void testProcessTickExtremePollution() {
        StatsManagerSocial manager = StatsManagerSocial.getInstance();

        // มลพิษ 2000 หน่วย (ถ้าตามสูตรต้องโดนหัก 200.0)
        master.environment.setDebuffPollutionTotal(2000.0);

        // ส่งความสุขตั้งต้นเข้ามา 100
        manager.processTick(100.0);

        // ความสุขโดนหักทะลุหลอด แต่ CityMasterStats.setHappiness มี Math.clamp ดักไว้
        // ผลลัพธ์ต้องหยุดที่ 0.0 ไม่ใช่ -100.0
        assertEquals(0.0, master.social.getHappiness());
    }
}
