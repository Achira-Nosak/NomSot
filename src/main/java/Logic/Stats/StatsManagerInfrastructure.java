package Logic.Stats;


/**
 * <ul>
 * <li>Singleton</li>
 * <li>จัดการ logic และ Stat ทั้งหมดที่เกี่ยวกับ Infrastructure ของเกม</li>
 * <li>Class นี้ถูกเรียกใช้โดย SimulationManager</li>
 * </ul>
 */
public class StatsManagerInfrastructure {
    private static StatsManagerInfrastructure instance;

    private StatsManagerInfrastructure() {}

    public static StatsManagerInfrastructure getInstance() {
        if (instance == null) {
            instance = new StatsManagerInfrastructure();
        }
        return instance;
    }

    /**
     * จัดการ Logic และ Stat (Infrastructure) ของเกมในแต่ละ Tick
     * <p><b>Logic Overview:</b>
     * <ul>
     * <li>สาธิตการคำนวณ Power Water Food (Demand/Supply) เบื้องต้น</li>
     * </ul>
     * * <p><b>Future Enhancement:</b>
     * <ul>
     * <li>เพิ่มระบบตรวจสอบสถานะวิกฤติ (Infrastructure Crisis)</li>
     * </ul>
     * * @param totalPowerDemand
     * @param totalWaterDemand
     * @param totalFoodDemand
     * @param totalPowerSupply
     * @param totalWaterSupply
     * @param totalFoodSupply
     */
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
            // System.out.println("ภาวะวิกฤติ: ไฟฟ้าดับทั้งเมือง!");
        }
    }
}