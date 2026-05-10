package Model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Model.BaseBuilding;
import Config.Enums.ZoneType;

class BaseBuildingTest {

    // 1. สร้างคลาสจำลอง (Dummy Class) เพื่อเอาไว้ทดสอบ Abstract Class
    private static class DummyBuilding extends BaseBuilding {
        public DummyBuilding(String id, int x, int y, ZoneType type) {
            super(id, x, y, type);
        }

        @Override
        public void onTick(long currentTick) {
            // ปล่อยว่างไว้ เพราะเราแค่อยากเทสตัวแปรของ BaseBuilding
        }
    }

    // เช็คว่าตอนสร้างตึก มันดึงค่าเริ่มต้น (Tax, Maintenance) จาก JSON มาตั้งค่าถูกไหม
    @Test
    void testConstructorLoadsDataFromConfig() {
        // ใช้ "ResidentialLow" เพราะเรารู้ชัวร์ๆ ว่าใน JSON ตั้ง baseTaxRevenue = 15 และ maintenance = 2
        DummyBuilding building = new DummyBuilding("ResidentialLow", 5, 5, ZoneType.RESIDENTIAL_LOW);

        assertNotNull(building.getStats());
        assertEquals(15.0, building.getCurrentTax());
        assertEquals(2.0, building.getCurrentMaintenance());
    }

    // เช็คระบบ Getter/Setter ว่าสามารถอัปเดตค่าต่างๆ แบบ Dynamic ได้ในระหว่างเล่นเกม
    @Test
    void testDynamicStateChanges() {
        DummyBuilding building = new DummyBuilding("ResidentialLow", 0, 0, ZoneType.RESIDENTIAL_LOW);

        // สมมติว่าตึกอัปเกรดหรือโดนบัฟ เลยสั่งเปลี่ยนค่าความสุขและไฟที่กิน
        building.setCurrentHappiness(85.0);
        building.setCurrentPowerConsume(10.0);

        assertEquals(85.0, building.getCurrentHappiness());
        assertEquals(10.0, building.getCurrentPowerConsume());
    }

    // เช็คระบบจดจำ Index (ต้องเอาไปใช้ผูกกับ Array ใน SimulationManager)
    @Test
    void testDataIndexTracking() {
        DummyBuilding building = new DummyBuilding("ResidentialLow", 10, 10, ZoneType.RESIDENTIAL_LOW);

        // จำลองสถานการณ์ว่า SimulationManager ยัดตึกนี้ลงช่องที่ 42
        building.setDataIndex(42);

        assertEquals(42, building.getDataIndex());
    }
}
