package Model;


import Config.BuildingData;
import Config.ConfigLoader;
import Config.Enums.ZoneType;

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
                // แก้ zoneType
                return new ZonedBuilding(buildingId, gridX, gridY, ZoneType.RESIDENTIAL_LOW);

            default:
                System.out.println("⚠️ Warning: Unknown logic group for " + buildingId);
                return null;
        }
    }
}
