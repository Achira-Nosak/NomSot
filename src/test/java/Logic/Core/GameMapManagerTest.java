package Logic.Core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class GameMapManagerTest {

    private GameMapManager mapManager;

    @BeforeEach
    void setUp() throws Exception {
        resetSingleton();
        mapManager = GameMapManager.getInstance();
    }

    private void resetSingleton() throws Exception {
        Field instance = GameMapManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    @DisplayName("Initial State: ทุกช่องในแมพต้องเป็น EMPTY เมื่อเริ่มเกม")
    void testInitialMapIsEmpty() {
        int size = mapManager.getMapSize();
        // สุ่มเช็คบางจุดเพื่อความรวดเร็ว
        assertEquals(GameMapManager.EMPTY_TILE_ID, mapManager.getBuildingIdAt(0, 0));
        assertEquals(GameMapManager.EMPTY_TILE_ID, mapManager.getBuildingIdAt(size / 2, size / 2));
        assertEquals(GameMapManager.EMPTY_TILE_ID, mapManager.getBuildingIdAt(size - 1, size - 1));
    }

    @Test
    @DisplayName("Set & Get Tile: วางตึกแล้วต้องดึง ID กลับมาได้ถูกต้อง")
    void testSetAndGetTile() {
        String testBuilding = "POWER_PLANT_01";
        mapManager.setTile(10, 20, testBuilding);

        assertEquals(testBuilding, mapManager.getBuildingIdAt(10, 20), "ช่องที่วางตึกต้องมี ID ตรงตามที่กำหนด");
        assertEquals(GameMapManager.EMPTY_TILE_ID, mapManager.getBuildingIdAt(10, 21), "ช่องข้างๆ ต้องยังเป็น EMPTY เหมือนเดิม");
    }

    @Test
    @DisplayName("Boundary: การดึงข้อมูลนอกขอบเขตต้องคืนค่า null (ไม่ Crash)")
    void testGetTileOutOfBounds() {
        int size = mapManager.getMapSize();

        assertNull(mapManager.getBuildingIdAt(-1, 0), "พิกัดติดลบต้องคืนค่า null");
        assertNull(mapManager.getBuildingIdAt(0, -1), "พิกัดติดลบต้องคืนค่า null");
        assertNull(mapManager.getBuildingIdAt(size, size), "พิกัดเกินขนาดแมพต้องคืนค่า null");
    }

    @Test
    @DisplayName("Boundary: การวางตึกนอกขอบเขตต้องไม่ทำให้โปรแกรมพัง")
    void testSetTileOutOfBounds() {
        int size = mapManager.getMapSize();

        // ทดสอบว่าการสั่ง set นอกขอบเขตไม่พ่น Exception ออกมา
        assertDoesNotThrow(() -> {
            mapManager.setTile(-1, 50, "SOME_BUILDING");
            mapManager.setTile(50, -1, "SOME_BUILDING");
            mapManager.setTile(size, size, "SOME_BUILDING");
        }, "การวางตึกนอก Boundary ต้องถูกจัดการให้เงียบ (Silent Fail) ไม่ใช่ Error จนเกมเด้ง");
    }

    @Test
    @DisplayName("Map Size: ขนาดแมพต้องถูกต้องตามที่กำหนด (1000)")
    void testMapSize() {
        assertEquals(1000, mapManager.getMapSize());
    }
}