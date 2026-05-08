package Config;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingDataTest {

    private BuildingData buildingData;

    @BeforeEach
    void setUp() {
        buildingData = new BuildingData();
    }

    @Test
    @DisplayName("Default Values: ตรวจสอบค่าเริ่มต้นของฟิลด์พื้นฐาน")
    void testDefaultValues() {
        assertNotNull(buildingData.getDescription());
        assertEquals("NONE", buildingData.getLogicGroup());
        assertEquals("Uncategorized", buildingData.getBuildBarTag());
    }

    @Test
    @DisplayName("getBaseColor: ใส่ Hex ถูกต้อง ต้องคืนค่าสีที่ถูกต้อง")
    void testGetBaseColorValid() throws Exception {
        setPrivateField(buildingData, "baseColorHex", "#FF0000"); // สีแดง

        Color result = buildingData.getBaseColor();

        assertEquals(Color.RED, result, "ควรคืนค่าสีแดงเมื่อใส่ Hex #FF0000");
    }

    @Test
    @DisplayName("getBaseColor: ใส่ Hex ผิดรูปแบบ ต้องคืนค่า Color.MAGENTA (Error Color)")
    void testGetBaseColorInvalid() throws Exception {
        setPrivateField(buildingData, "baseColorHex", "NOT_A_COLOR");

        Color result = buildingData.getBaseColor();

        assertEquals(Color.MAGENTA, result, "กรณี Error ต้องคืนค่าสี MAGENTA ตามที่กำหนดใน catch block");
    }

    @Test
    @DisplayName("Data Integrity: ตรวจสอบการคืนค่าตัวเลขจาก Getter (ตัวอย่าง Construction Cost)")
    void testDataIntegrity() throws Exception {
        double expectedCost = 5000.50;
        setPrivateField(buildingData, "constructionCost", expectedCost);

        assertEquals(expectedCost, buildingData.getConstructionCost(), 0.001);
    }

    @Test
    @DisplayName("Getters: ตรวจสอบการคืนค่า String (ตัวอย่าง Name และ ID)")
    void testStringGetters() throws Exception {
        setPrivateField(buildingData, "id", "power_plant_01");
        setPrivateField(buildingData, "name", "โรงไฟฟ้าถ่านหิน");

        assertEquals("power_plant_01", buildingData.getId());
        assertEquals("โรงไฟฟ้าถ่านหิน", buildingData.getName());
    }


    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}