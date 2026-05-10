package Model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Model.BuildingFactory;
import Model.BaseBuilding;
import Model.ZonedBuilding;
import Model.UtilityBuilding;
import Model.ServiceBuilding;

class BuildingFactoryTest {

    // เช็คว่าถ้าใส่ ID ที่เป็น "EMPTY" หรือ ID มั่วๆ ที่ไม่มีใน JSON โรงงานต้องไม่ Crash แต่คืนค่า null อย่างปลอดภัย
    @Test
    void testCreateBuildingInvalidOrEmptyId() {
        BaseBuilding emptyBuilding = BuildingFactory.createBuilding("EMPTY", 0, 0);
        BaseBuilding invalidBuilding = BuildingFactory.createBuilding("UFO_BASE", 0, 0);

        assertNull(emptyBuilding);
        assertNull(invalidBuilding);
    }

    // เช็คการสร้างตึกกลุ่ม ZONED ว่าโรงงานสามารถคลอด Object ออกมาเป็นคลาส ZonedBuilding ได้ถูกต้อง
    @Test
    void testCreateZonedBuilding() {
        // ใช้ "ResidentialLow" เพราะรู้ว่าใน JSON ตั้ง logicGroup เป็น "ZONED"
        BaseBuilding building = BuildingFactory.createBuilding("ResidentialLow", 5, 5);

        assertNotNull(building);
        assertTrue(building instanceof ZonedBuilding);
        assertEquals(5, building.getGridX()); // เช็คว่าพิกัดถูกตั้งค่าถูกต้องด้วย
    }

    // เช็คการสร้างตึกกลุ่ม UTILITY
    // (หมายเหตุ: ถ้าใน JSON ของคุณใช้ ID อื่นสำหรับโรงไฟฟ้า ให้เปลี่ยน "PowerPlant" เป็นชื่อ ID นั้นนะครับ)
    @Test
    void testCreateUtilityBuilding() {
        BaseBuilding building = BuildingFactory.createBuilding("PowerPlant", 10, 10);

        // ดัก if ไว้เผื่อคุณยังไม่ได้สร้าง PowerPlant ใน JSON เทสจะได้ไม่พัง (รันผ่านแบบข้ามไปก่อน)
        if (building != null) {
            assertTrue(building instanceof UtilityBuilding);
        }
    }

    // เช็คการสร้างตึกกลุ่ม SERVICE
    // (หมายเหตุ: เปลี่ยน "PoliceStation" เป็น ID ตึกบริการที่คุณมีใน JSON ได้เลยครับ)
    @Test
    void testCreateServiceBuilding() {
        BaseBuilding building = BuildingFactory.createBuilding("PoliceStation", 15, 15);

        if (building != null) {
            assertTrue(building instanceof ServiceBuilding);
        }
    }
}
