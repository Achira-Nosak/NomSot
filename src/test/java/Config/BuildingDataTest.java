package Config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;
import Config.BuildingData;
import java.lang.reflect.Field;

class BuildingDataTest {

    // เช็คว่าถ้าค่าเริ่มต้น (Default) เป็นโค้ดสีที่ถูกต้อง จะต้องแปลงเป็น JavaFX Color ได้เป๊ะๆ
    @Test
    void testGetBaseColorValidHex() {
        BuildingData data = new BuildingData();

        // ค่าเริ่มต้นคือ "#32CD32" (LimeGreen)
        Color expectedColor = Color.web("#32CD32");
        assertEquals(expectedColor, data.getBaseColor());
    }

    // เช็คระบบ Fallback ว่าถ้า JSON ดึงค่าโค้ดสีมามั่วๆ (Try-Catch) เกมต้องไม่ Crash แต่ต้องคืนค่าสี Magenta
    @Test
    void testGetBaseColorInvalidHexFallback() throws Exception {
        BuildingData data = new BuildingData();

        // 🌟 ใช้ Java Reflection เพื่อแอบยัดค่าตัวแปร Private (จำลองสถานการณ์ JSON อ่านไฟล์ผิด)
        Field colorField = BuildingData.class.getDeclaredField("baseColorHex");
        colorField.setAccessible(true);
        colorField.set(data, "NOT_A_COLOR"); // ใส่ตัวหนังสือมั่วๆ ที่ไม่ใช่ Hex

        // เมธอดต้อง Catch Exception ได้ และคืนค่า Magenta ออกมาอย่างปลอดภัย
        assertEquals(Color.MAGENTA, data.getBaseColor());
    }

    // เช็คว่าค่า Default ของตึกที่เพิ่งสร้างใหม่ จะต้องเป็น 0 หรือค่าว่างที่ปลอดภัยต่อการนำไปคำนวณเสมอ
    @Test
    void testDefaultValuesAreSafe() {
        BuildingData data = new BuildingData();

        assertEquals(0.0, data.getConstructionCost());
        assertEquals(0, data.getMaxResidents());
        assertEquals("NONE", data.getLogicGroup());
        assertEquals("Uncategorized", data.getBuildBarTag());
    }
}
