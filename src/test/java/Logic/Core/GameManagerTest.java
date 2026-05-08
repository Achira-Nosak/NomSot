package Logic.Core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    private GameManager gameManager;

    @BeforeEach
    void setUp() throws Exception {
        gameManager = GameManager.getInstance();

        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instance = GameManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        // ดึง instance ใหม่ที่ค่าถูก reset แล้ว
        gameManager = GameManager.getInstance();
    }

    @Test
    @DisplayName("Initial State: ตรวจสอบค่าเริ่มต้นของระบบเวลาและเลเวล")
    void testInitialState() {
        assertEquals(0, gameManager.getCurrentTick(), "Tick เริ่มต้นต้องเป็น 0");
        assertEquals(1, gameManager.getGameDay(), "วันเริ่มต้นต้องเป็น 1");
        assertEquals(1, gameManager.getGameMonth(), "เดือนเริ่มต้นต้องเป็น 1");
        assertEquals(1.0, gameManager.getCityLevel(), "Level เริ่มต้นต้องเป็น 1.0");
    }

    @Test
    @DisplayName("City Level: ตรวจสอบว่าเลเวลห้ามต่ำกว่า 1.0")
    void testCityLevelConstraints() {
        gameManager.setCityLevel(5.5);
        assertEquals(5.5, gameManager.getCityLevel());

        gameManager.setCityLevel(0.5); // ต่ำกว่า 1.0
        assertEquals(1.0, gameManager.getCityLevel(), "City Level ห้ามต่ำกว่า 1.0");
    }

    @Test
    @DisplayName("Advance Tick: ตรวจสอบการเพิ่มขึ้นของ Tick")
    void testAdvanceTick() {
        gameManager.advanceTick();
        assertEquals(1, gameManager.getCurrentTick());

        gameManager.advanceTick();
        assertEquals(2, gameManager.getCurrentTick());
    }

    @Test
    @DisplayName("Day Rollover: 24 Ticks ต้องเปลี่ยนเป็นวันใหม่")
    void testDayRollover() {
        // รันไป 24 Ticks
        for (int i = 0; i < 24; i++) {
            gameManager.advanceTick();
        }

        assertEquals(2, gameManager.getGameDay(), "หลังจาก 24 Ticks วันต้องเปลี่ยนเป็นวันที่ 2");
        assertEquals(0, gameManager.getCurrentTick() % 24, "Tick สะสมต้องสอดคล้องกับวัน");
    }

    @Test
    @DisplayName("Month Rollover: 30 วัน ต้องเปลี่ยนเป็นเดือนใหม่")
    void testMonthRollover() {
        // 24 ticks * 30 days = 720 ticks
        for (int i = 0; i < 720; i++) {
            gameManager.advanceTick();
        }

        // ณ tick ที่ 720:
        // 720 % 24 == 0 -> gameDay จะกลายเป็น 31
        // 31 > 30 -> gameDay Reset เป็น 1 และ gameMonth กลายเป็น 2
        assertEquals(1, gameManager.getGameDay(), "ต้อง Reset วันกลับมาเป็นวันที่ 1");
        assertEquals(2, gameManager.getGameMonth(), "ต้องเปลี่ยนเป็นเดือนที่ 2");
    }
}