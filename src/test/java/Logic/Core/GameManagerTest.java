package Logic.Core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Logic.Core.GameManager;

class GameManagerTest {

    // เทสว่า Singleton ทำงานถูกต้องและดึง Instance เดียวกันเสมอ
    @Test
    void testSingletonInstance() {
        GameManager instance1 = GameManager.getInstance();
        GameManager instance2 = GameManager.getInstance();
        assertNotNull(instance1);
        assertEquals(instance1, instance2);
    }

    // เทสว่าระบบป้องกันไม่ให้ City Level ต่ำกว่า 1.0 ได้
    @Test
    void testSetCityLevelMinimumBound() {
        GameManager manager = GameManager.getInstance();

        // ลองใส่ค่าติดลบหรือ 0 จะต้องถูกปัดขึ้นมาเป็น 1.0 เสมอ
        manager.setCityLevel(-5.0);
        assertEquals(1.0, manager.getCityLevel());

        // ลองใส่ค่าปกติ ต้องเปลี่ยนได้ตามปกติ
        manager.setCityLevel(3.5);
        assertEquals(3.5, manager.getCityLevel());
    }

    // เทสว่าการเดินเวลาครบ 24 Tick จะทำให้เปลี่ยนวัน (Day +1) จริงหรือไม่
    @Test
    void testAdvanceTickDayRollover() {
        GameManager manager = GameManager.getInstance();
        int initialDay = manager.getGameDay();

        // จำลองการรันลูป 24 Tick
        for (int i = 0; i < 24; i++) {
            manager.advanceTick();
        }

        // วันต้องเพิ่มขึ้น 1 วัน
        assertEquals(initialDay + 1, manager.getGameDay());
    }

    // เทสว่าการเดินเวลา 30 วัน (24 * 30 = 720 Tick) จะทำให้เปลี่ยนเดือนและรีเซ็ตวัน
    @Test
    void testAdvanceTickMonthRollover() {
        GameManager manager = GameManager.getInstance();

        // เราไม่รู้ว่ารันเทสก่อนหน้ามาเท่าไหร่ เลยต้องคำนวณ Tick ที่เหลือให้ครบเดือน
        int daysToNextMonth = 30 - manager.getGameDay() + 1;
        int ticksToNextMonth = daysToNextMonth * 24;
        int expectedMonth = manager.getGameMonth() + 1;

        // จำลองการรันลูปจนข้ามเดือน
        for (int i = 0; i < ticksToNextMonth; i++) {
            manager.advanceTick();
        }

        // วันต้องถูกรีเซ็ตกลับเป็น 1 และเดือนต้องเพิ่มขึ้น 1
        assertEquals(1, manager.getGameDay());
        assertEquals(expectedMonth, manager.getGameMonth());
    }
}