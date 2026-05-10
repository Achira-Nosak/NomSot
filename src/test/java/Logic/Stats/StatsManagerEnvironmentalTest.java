package Logic.Stats;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Logic.Stats.StatsManagerEnvironmental;
import Logic.Stats.CityMasterStats;

class StatsManagerEnvironmentalTest {

    // เคลียร์ค่ามลพิษในฐานข้อมูลก่อนเริ่มเทสทุกครั้ง ป้องกันผลลัพธ์เพี้ยน
    @BeforeEach
    void setUp() {
        CityMasterStats.getInstance().environment.setDebuffPollutionTotal(0);
        CityMasterStats.getInstance().environment.setDebuffPollutionAir(50); // แกล้งใส่ค่าค้างไว้
    }

    // เช็คว่า Singleton สามารถเรียกใช้งานได้และเป็น instance เดียวกันเสมอ
    @Test
    void testSingletonInstance() {
        StatsManagerEnvironmental manager1 = StatsManagerEnvironmental.getInstance();
        StatsManagerEnvironmental manager2 = StatsManagerEnvironmental.getInstance();
        assertNotNull(manager1);
        assertEquals(manager1, manager2);
    }

    // เช็คว่าระบบดึงค่า Raw Data เข้าไปบันทึกใน CityMasterStats อย่างถูกต้อง
    @Test
    void testProcessTickUpdatesPollutionTotal() {
        StatsManagerEnvironmental manager = StatsManagerEnvironmental.getInstance();

        // จำลองการส่งค่ามลพิษดิบ (Raw Data) เข้าไป 250.5
        manager.processTick(250.5);

        // ตรวจสอบในฐานข้อมูลกลางว่าค่าตรงกับที่รับมาหรือไม่
        assertEquals(250.5, CityMasterStats.getInstance().environment.getDebuffPollutionTotal());
    }
}
