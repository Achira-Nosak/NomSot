package Logic.Stats;

public class StatsManagerInfrastructure {
    private static StatsManagerInfrastructure instance;

    private StatsManagerInfrastructure() {}

    public static StatsManagerInfrastructure getInstance() {
        if (instance == null) {
            instance = new StatsManagerInfrastructure();
        }
        return instance;
    }


    public void processTick(double totalPowerDemand, double totalWaterDemand, double totalFoodDemand,
                            double totalPowerSupply, double totalWaterSupply, double totalFoodSupply) {

        CityMasterStats master = CityMasterStats.getInstance();

        // 1. อัปเดต Demand
        master.infrastructure.setPowerDemand(totalPowerDemand);
        master.infrastructure.setWaterDemand(totalWaterDemand);
        master.infrastructure.setFoodDemand(totalFoodDemand);

        // 2. อัปเดต Supply
        master.infrastructure.setPowerSupply(totalPowerSupply);
        master.infrastructure.setWaterSupply(totalWaterSupply);
        master.infrastructure.setFoodSupply(totalFoodSupply);

        // 3. (Future Logic) เช็คสถานะวิกฤติของเมือง
        // ถ้ามีความต้องการ > กำลังผลิต = วิกฤติ
        boolean isBlackout = totalPowerDemand > totalPowerSupply;
        boolean isDrought = totalWaterDemand > totalWaterSupply;
        boolean isFamine = totalFoodDemand > totalFoodSupply;

        if (isBlackout) {
            // อนาคต: แอบไปบอก StatsManagerSocial ว่าไฟดับนะ ให้ลดความสุขชาวเมืองลงด่วน!
            // System.out.println("⚠️ ภาวะวิกฤติ: ไฟฟ้าดับทั้งเมือง!");
        }
    }
}