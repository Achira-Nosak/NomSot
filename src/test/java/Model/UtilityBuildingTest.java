package Model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Model.UtilityBuilding;

class UtilityBuildingTest {

    // (หมายเหตุ: ใช้ ID "CommercialLow" ไปก่อนเพื่อดึงโครงสร้าง JSON ไม่ให้เกิด Null
    // แต่ถ้าใน JSON ของคุณมีตึกเช่น "PowerPlant" ให้เปลี่ยนชื่อ ID เป็นอันนั้นได้เลยครับ)
    private final String MOCK_ID = "CommercialLow";

    // เช็คว่าตึกสาธารณูปโภคมีการ Implement Interface ครบถ้วนตามหลัก Polymorphism
    @Test
    void testInterfacesImplemented() {
        UtilityBuilding utility = new UtilityBuilding(MOCK_ID, 0, 0);

        // พิสูจน์ให้เห็นว่าตึกนี้ทำงานได้หลายรูปแบบ (เป็นผู้ผลิตทรัพยากร และ ผู้แผ่ออร่า)
        assertTrue(utility instanceof Model.IResourceProducer);
        assertTrue(utility instanceof Model.IAuraProvider);
    }

    // เช็คว่าเมื่อเวลาผ่านไป (60 Tick) ความทนทาน (Durability) จะลดลงตามลอจิก
    @Test
    void testDurabilityDegradesOnTick() {
        UtilityBuilding utility = new UtilityBuilding(MOCK_ID, 0, 0);
        utility.setDataIndex(0); // เซ็ตเพื่อให้ (currentTick + dataIndex) % 60 == 0 ทำงานตรงจังหวะ
        double initialDurability = utility.getDurability();

        // จำลองเวลารันไป 60 Tick
        utility.onTick(60);

        // ความทนทานต้องลดลง 0.1
        assertEquals(initialDurability - 0.1, utility.getDurability(), 0.001);
    }

    // เช็คระบบซ่อมแซม (Repair) ว่าทำให้ตึกกลับมาสภาพสมบูรณ์ 100%
    @Test
    void testRepairRestoresDurability() {
        UtilityBuilding utility = new UtilityBuilding(MOCK_ID, 0, 0);
        utility.setDataIndex(0);

        // จำลองให้เวลาผ่านไปนานๆ โดยวนลูปลดความทนทาน
        for(int i = 0; i < 100; i++) {
            utility.onTick(60); // ลดไปทีละ 0.1
        }

        // ยืนยันว่าตึกพังลงไปแล้วจริงๆ (ต้องน้อยกว่า 100)
        assertTrue(utility.getDurability() < 100.0);

        // กดซ่อมตึก
        utility.repair();

        // สภาพตึกต้องกลับมาเต็ม 100.0 เท่าเดิม
        assertEquals(100.0, utility.getDurability());
    }

    // เช็คการระบายสี (Polymorphism) ว่ามันสามารถเปิดจุดทำงานลงบนแผนที่น้ำ/ไฟ ได้ถูกต้อง
    @Test
    void testProduceResourcesPaintsMap() {
        UtilityBuilding utility = new UtilityBuilding(MOCK_ID, 5, 5);

        // บังคับเซ็ตค่าว่าตึกนี้ "ผลิตไฟฟ้าได้อย่างเดียว"
        utility.setCurrentPowerProduction(100.0);
        utility.setCurrentWaterProduction(0.0);

        boolean[][] powerMap = new boolean[10][10];
        boolean[][] waterMap = new boolean[10][10];

        // สั่งเดินเครื่องผลิตทรัพยากร
        utility.produceResources(powerMap, waterMap);

        // จุดที่ตึกตั้งอยู่ (5,5) บนแผนที่ไฟฟ้าจะต้องถูกเปิดใช้งาน (true)
        assertTrue(powerMap[5][5]);

        // ส่วนแผนที่น้ำต้องไม่เกิดอะไรขึ้น (false) เพราะตึกนี้ไม่ได้ผลิตน้ำ
        assertFalse(waterMap[5][5]);
    }
}