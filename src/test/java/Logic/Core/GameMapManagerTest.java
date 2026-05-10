package Logic.Core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Logic.Core.GameMapManager;

class GameMapManagerTest {

    // เทสว่า Singleton ดึงมาใช้งานได้และเป็น Instance เดียวกันเสมอ
    @Test
    void testSingletonInstance() {
        GameMapManager instance1 = GameMapManager.getInstance();
        GameMapManager instance2 = GameMapManager.getInstance();
        assertNotNull(instance1);
        assertEquals(instance1, instance2);
    }

    // เทสว่าตอนเริ่มเกม ช่องบน Map ต้องถูกกำหนดเป็น "EMPTY" อย่างถูกต้อง
    @Test
    void testInitializeMap() {
        GameMapManager manager = GameMapManager.getInstance();

        // ลองสุ่มเช็คตามมุมและจุดกึ่งกลางของ Map
        assertEquals(GameMapManager.EMPTY_TILE_ID, manager.getBuildingIdAt(0, 0));
        assertEquals(GameMapManager.EMPTY_TILE_ID, manager.getBuildingIdAt(500, 500));
        assertEquals(GameMapManager.EMPTY_TILE_ID, manager.getBuildingIdAt(999, 999));
    }

    // เทสการวางตึก (Set) และดึงข้อมูลตึก (Get) ในพิกัดปกติว่าทำงานได้ถูกไหม
    @Test
    void testSetAndGetTileWithinBounds() {
        GameMapManager manager = GameMapManager.getInstance();

        manager.setTile(10, 10, "RESIDENTIAL_LV1");
        assertEquals("RESIDENTIAL_LV1", manager.getBuildingIdAt(10, 10));
    }

    // เทสว่าถ้าดึงข้อมูลหรือวางตึก "นอกขอบเขตแมพ" ระบบต้องดักไว้ ไม่ให้ Array พัง (Crash)
    @Test
    void testOutOfBoundsHandling() {
        GameMapManager manager = GameMapManager.getInstance();

        // ดึงพิกัดติดลบและเกินขนาดแมพ ต้องคืนค่า null
        assertNull(manager.getBuildingIdAt(-1, 0));
        assertNull(manager.getBuildingIdAt(1000, 1000));

        // ลองสั่งวางตึกนอกแมพ โค้ดที่ดัก if ไว้ต้องทำงานและโปรแกรมต้องไม่ Error
        manager.setTile(-5, 20, "GHOST_TOWER");
        assertNull(manager.getBuildingIdAt(-5, 20)); // เช็คซ้ำว่ามันไม่ได้ถูกวางลงไปจริงๆ
    }
}
