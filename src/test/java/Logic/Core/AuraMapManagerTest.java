package Logic.Core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Logic.Core.AuraMapManager;

class AuraMapManagerTest {

    private AuraMapManager auraManager;

    @BeforeEach
    void setUp() {
        auraManager = AuraMapManager.getInstance();
        // เคลียร์ค่าเก่าก่อนเริ่มเทสแต่ละอัน (สมมติว่า recalculateAuras จะเรียก clear ให้เอง)
        auraManager.recalculateAuras();
    }

    // เทสว่า Singleton ทำงานถูกต้องและไม่เป็นค่าว่าง
    @Test
    void testSingletonInstance() {
        assertNotNull(auraManager);
        AuraMapManager secondInstance = AuraMapManager.getInstance();
        assertEquals(auraManager, secondInstance);
    }

    // เทสว่าที่จุดศูนย์กลางการระบายสี ต้องได้ค่าความเข้มข้นสูงสุด (Intensity)
    @Test
    void testPaintGradientAtCenter() {
        double intensity = 10.0;
        // ระบายที่จุด (5, 5) รัศมี 3
        auraManager.paintGradientAura("pollution", 5, 5, 3, intensity);

        // ที่จุดศูนย์กลาง (5,5) ระยะทางเป็น 0 factor ต้องเป็น 1.0 ค่าต้องได้เท่ากับ intensity
        assertEquals(10.0, auraManager.getPollutionAt(5, 5));
    }

    // เทสว่ายิ่งห่างออกไป ค่าออร่าต้องค่อยๆ จางลงตามสูตร Linear Falloff
    @Test
    void testPaintGradientFalloff() {
        double intensity = 10.0;
        int radius = 2;
        auraManager.paintGradientAura("safety", 5, 5, radius, intensity);

        // จุด (5, 6) ห่างจากศูนย์กลาง 1 หน่วย ในรัศมี 2
        // factor = 1 - (1/2) = 0.5 ดังนั้นค่าต้องเป็น 10 * 0.5 = 5.0
        assertEquals(5.0, auraManager.getSafetyAt(5, 6));
    }

    // เทสว่าถ้าจุดนั้นอยู่นอกรัศมี ค่าออร่าต้องไม่เปลี่ยนแปลง (ต้องเป็น 0)
    @Test
    void testPaintOutsideRadius() {
        auraManager.paintGradientAura("health", 5, 5, 2, 10.0);

        // จุด (5, 8) ห่างออกไป 3 หน่วย ซึ่งเกินรัศมี 2
        assertEquals(0.0, auraManager.getHealthAt(5, 8));
    }

    // เทสการซ้อนทับกันของออร่า (Stacking) จากตึกหลายต้น
    @Test
    void testAuraStacking() {
        // ระบายสีมลพิษจากตึก 2 ต้นลงที่จุดเดียวกัน
        auraManager.paintGradientAura("pollution", 5, 5, 2, 10.0);
        auraManager.paintGradientAura("pollution", 5, 5, 2, 5.0);

        // ค่าที่จุด (5,5) ต้องบวกกันได้ 10 + 5 = 15
        assertEquals(15.0, auraManager.getPollutionAt(5, 5));
    }
}