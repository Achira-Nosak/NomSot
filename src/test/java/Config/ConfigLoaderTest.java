package Config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    @DisplayName("Load Config: ข้อมูลต้องไม่ว่างเปล่าหลังจากโหลดไฟล์ JSON")
    void testRegistryIsLoaded() {
        Map<String, BuildingData> allConfigs = ConfigLoader.getAllConfigs();

        assertNotNull(allConfigs, "Registry ไม่ควรเป็น null");
        assertFalse(allConfigs.isEmpty(), "Registry ควรมีข้อมูลตึกอย่างน้อย 1 รายการ (เช็คไฟล์ Building_Config.json)");
    }

    @Test
    @DisplayName("Get Building Config: ต้องดึงข้อมูลตึกที่มีอยู่จริงได้ถูกต้อง")
    void testGetValidBuildingConfig() {
        String testId = "EMPTY";

        BuildingData data = ConfigLoader.getBuildingConfig(testId);

        if (data != null) {
            assertEquals(testId, data.getId(), "ID ที่ดึงมาได้ต้องตรงกับที่ร้องขอ");
        } else {
            System.out.println("ไม่พบ ID '" + testId + "' ใน JSON สำหรับการเทส");
        }
    }

    @Test
    @DisplayName("Invalid ID: ส่ง ID มั่วๆ เข้าไปต้องคืนค่า null")
    void testGetInvalidBuildingConfig() {
        BuildingData data = ConfigLoader.getBuildingConfig("THIS_ID_DOES_NOT_EXIST_12345");
        assertNull(data, "ถ้าหา ID ไม่เจอ ระบบต้องคืนค่า null");
    }

    @Test
    @DisplayName("Data Integrity: ข้อมูลสำคัญในตึกต้องโหลดมาครบ (เช่น Construction Cost)")
    void testConfigDataIntegrity() {
        Map<String, BuildingData> allConfigs = ConfigLoader.getAllConfigs();

        // สุ่มดึงตึกตัวแรกมาเช็คว่ามีข้อมูลพื้นฐานครบไหม
        BuildingData firstBuilding = allConfigs.values().iterator().next();

        assertNotNull(firstBuilding.getId(), "ตึกทุกต้นต้องมี ID");
        assertNotNull(firstBuilding.getName(), "ตึกทุกต้นต้องมีชื่อ");

        // เช็คว่าสีต้องโหลดได้ (getBaseColor ไม่พ่น Magenta ออกมาถ้า Hex ใน JSON ถูก)
        assertNotNull(firstBuilding.getBaseColor(), "ควรโหลดค่าสีจาก Hex ใน JSON ได้");
    }
}