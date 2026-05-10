package Model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Model.ZonedBuilding;
import Config.Enums.ZoneType;
import Logic.Stats.CityMasterStats;

class ZonedBuildingTest {

    // ใช้ ID ที่มีใน JSON แน่ๆ เพื่อป้องกัน NullPointerException
    private final String MOCK_ID = "ResidentialLow";

    @BeforeEach
    void setUp() {
        // เคลียร์มลพิษเมืองใน Database ศูนย์กลางก่อนเริ่มเทส
        CityMasterStats.getInstance().environment.setDebuffPollutionTotal(0.0);
    }

    // เช็คว่าคลาสนี้มีการ Implement Interface การอัปเกรดตึกอย่างถูกต้อง
    @Test
    void testIsUpgradableInterface() {
        ZonedBuilding building = new ZonedBuilding(MOCK_ID, 0, 0, ZoneType.RESIDENTIAL_LOW);

        // โชว์ความรู้เรื่อง Polymorphism
        assertTrue(building instanceof Model.IUpgradable);
    }

    // เช็คลอจิกตึกพัฒนา (เลเวลอัป) เมื่อคนมีความสุขสูงและมลพิษต่ำ
    @Test
    void testUpgradeLogicOnHighHappiness() {
        ZonedBuilding building = new ZonedBuilding(MOCK_ID, 0, 0, ZoneType.RESIDENTIAL_LOW);
        building.setDataIndex(0); // บังคับให้ (currentTick + dataIndex) % 60 ลงตัวเสมอตอนเทส

        // จำลองสภาพแวดล้อมชั้นยอด
        building.setCurrentHappiness(90.0); // ความสุข > 80
        CityMasterStats.getInstance().environment.setDebuffPollutionTotal(50.0); // มลพิษ < 100

        int initialLevel = building.getCurrentLevel();

        // จำลองเวลาให้ผ่านไปจน upgradeTimer ทะลุ 100 ครั้ง
        // (ส่งเลข 60 เข้าไปรัวๆ เพื่อให้ if แรกใน onTick ทำงาน)
        for (int i = 0; i <= 101; i++) {
            building.onTick(60);
        }

        // เลเวลตึกต้องเพิ่มขึ้น 1
        assertEquals(initialLevel + 1, building.getCurrentLevel());
    }

    // เช็คลอจิกตึกเสื่อมโทรม (ดาวน์เกรด) เมื่อมลพิษในเมืองสูงเกินรับไหว
    @Test
    void testDowngradeLogicOnExtremePollution() {
        ZonedBuilding building = new ZonedBuilding(MOCK_ID, 0, 0, ZoneType.RESIDENTIAL_LOW);
        building.setDataIndex(0);

        // แอบอัปเกรดให้ตึกเป็นเลเวล 2 ก่อน จะได้มีพื้นที่ให้ตกลงมา
        building.upgradeLevel();
        assertEquals(2, building.getCurrentLevel());

        // จำลองสภาพแวดล้อมสุดเลวร้าย
        building.setCurrentHappiness(50.0); // ความสุขกลางๆ
        CityMasterStats.getInstance().environment.setDebuffPollutionTotal(600.0); // แต่มลพิษทะลุ 500!

        // จำลองเวลาให้ downgradeTimer ทะลุ 100 ครั้ง
        for (int i = 0; i <= 101; i++) {
            building.onTick(60);
        }

        // เลเวลตึกต้องร่วงกลับมาที่ 1
        assertEquals(1, building.getCurrentLevel());
    }

    // เช็คว่าถ้าสภาพเมือง "ปกติ" (ไม่ดีไป ไม่แย่ไป) ตึกจะต้องไม่เปลี่ยนเลเวล
    @Test
    void testNormalConditionResetsTimers() {
        ZonedBuilding building = new ZonedBuilding(MOCK_ID, 0, 0, ZoneType.RESIDENTIAL_LOW);
        building.setDataIndex(0);

        // ตั้งค่าให้อยู่ในเกณฑ์ปกติ (ความสุข 50, มลพิษ 50)
        building.setCurrentHappiness(50.0);
        CityMasterStats.getInstance().environment.setDebuffPollutionTotal(50.0);

        int initialLevel = building.getCurrentLevel();

        // รันเวลาผ่านไปนานๆ (200 รอบ)
        for (int i = 0; i < 200; i++) {
            building.onTick(60);
        }

        // ถ้าระบบปกติ เลเวลต้องเท่าเดิมไม่ขยับ
        assertEquals(initialLevel, building.getCurrentLevel());
    }
}
