package Model;


import Config.BuildingData;
import Config.ConfigLoader;
import Config.Enums.ZoneType;


/**
 * Class ที่เป็น Bridge ระหว่าง InputSensing(Click Construct), ConfigLoader(JSON), Building(By Type) เพื่อคลอดตึกออกมาแบบ Plug and Play
 * <p>Prototype Note: ปัจจุบันเป็นการสาธิตโครงสร้าง Factory โดยการสมมติค่า ZoneType เบื้องต้น ซึ่งในอนาคตจะพัฒนาให้รับพารามิเตอร์ Dynamic จากระบบ Zoning ของผู้เล่นโดยตรง</p>
 * <ul>
 * <li>Creational Pattern: แยกตรรกะการสร้าง Object ออกจากระบบหลัก</li>
 * <li>Data-Driven Instantiation: ตัดสินใจเลือกสร้างคลาสลูก (Utility, Service, Zoned) ตามค่า LogicGroup ที่โหลดมาจาก JSON ทำให้การเพิ่มตึกชนิดใหม่ในอนาคตทำได้แบบ Plug and Play</li>
 * <li>Easy Scalability: รองรับ Future Enhancement แบบ 100% ในอนาคตหากต้องการ "สร้างตึกชนิดใหม่"  หรือ "ปรับเปลี่ยนคุณสมบัติตึก"
 * สามารถทำได้ง่ายๆ เพียงแค่เพิ่ม Data ลงในไฟล์ JSON ระบบ Factory และ Polymorphism จะจัดการสร้างตึกให้ทำงานได้อัตโนมัติ</li>
 * </ul>
 */
public class BuildingFactory {

    // เมธอดนี้รับ ID ตึก และพิกัด x, y แล้วคลอด Object ตึกออกมา
    public static BaseBuilding createBuilding(String buildingId, int gridX, int gridY) {
        // 1. ดึงข้อมูลจาก JSON
        BuildingData data = ConfigLoader.getBuildingConfig(buildingId);

        if (data == null || buildingId.equals("EMPTY")) {
            return null;
        }

        // 2. ใช้ Switch-Case เช็คกลุ่มพฤติกรรม แล้วเรียกคลาสลูกให้ตรงกัน
        switch (data.getLogicGroup()) {
            case "UTILITY":
                return new UtilityBuilding(buildingId, gridX, gridY);

            case "SERVICE":
                return new ServiceBuilding(buildingId, gridX, gridY);

            case "ZONED":
                // สมมติ zoneType เบื้องต้น
                return new ZonedBuilding(buildingId, gridX, gridY, ZoneType.RESIDENTIAL_LOW);

            default:
                System.out.println("Warning: Unknown logic group for " + buildingId);
                return null;
        }
    }
}
