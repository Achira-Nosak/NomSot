package Config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Config.ConfigLoader;
import Config.BuildingData;
import java.util.Map;

class ConfigLoaderTest {

    // (หมายเหตุ: ConfigLoader มี static block ที่เรียก loadConfig() อัตโนมัติแล้ว จึงไม่ต้องใส่ @BeforeEach)

    // เช็คว่าระบบอ่านไฟล์ JSON เข้ามาใน Map ได้สำเร็จ (Map ไม่ว่างเปล่า)
    @Test
    void testRegistryIsLoadedSuccessfully() {
        Map<String, BuildingData> registry = ConfigLoader.getAllConfigs();

        assertNotNull(registry);
        assertTrue(registry.size() > 0, "Registry should contain building data from JSON");
    }

    // เช็คการดึงข้อมูลตึกที่มีอยู่จริงในระบบ (ใช้ "ResidentialLow" ตามที่คุณเคยให้มา)
    @Test
    void testGetBuildingConfigValidId() {
        BuildingData data = ConfigLoader.getBuildingConfig("ResidentialLow");

        assertNotNull(data);
        // เช็คซ้ำว่า ID ข้างใน Object ตรงกับที่ดึงมาจริงๆ
        assertEquals("ResidentialLow", data.getId());
    }

    // เช็คระบบดักจับข้อผิดพลาด (Fallback) เมื่อเรียกใช้ ID ตึกที่ไม่มีอยู่จริง ต้องได้ค่า null ไม่ใช่ Error
    @Test
    void testGetBuildingConfigInvalidIdReturnsNull() {
        BuildingData data = ConfigLoader.getBuildingConfig("ALIEN_UFO_BASE_999");

        assertNull(data);
    }
}
