package Logic.Stats;

/**
 * <ul>
 * <li>Singleton</li>
 * <li>จัดการ logic และ Stat ทั้งหมดที่เกี่ยวกับ Environmental ของเกม</li>
 * <li>Class นี้ถูกเรียกใช้โดย SimulationManager</li>
 * </ul>
 */
public class StatsManagerEnvironmental {
    private static StatsManagerEnvironmental instance;

    private StatsManagerEnvironmental() {}

    public static StatsManagerEnvironmental getInstance() {
        if (instance == null) {
            instance = new StatsManagerEnvironmental();
        }
        return instance;
    }

    /**
     * จัดการ Logic และ Stat (Environmental) ของเกมในแต่ละ Tick
     * <p><b>Logic Overview:</b>
     * <ul>
     * <li>สาธิตการคำนวณ Pollution เบื้องต้น</li>
     * </ul>
     * * <p><b>Future Enhancement:</b>
     * <ul>
     * <li>พัฒนาการดึงค่า pollutionIntensity แต่ละประเภทแบบ Real-time จาก AuraMapManager</li>
     * <li>เพิ่มการคำนวณผลกระทบเชิงบวกจากพื้นที่สีเขียว (BuffGreenery)</li>
     * <li>เพิ่มการคำนวณผลกระทบจากมลพิษทางเสียง (DebuffNoise)</li>
     * <li>เพิ่มระบบตรวจสอบสถานะวิกฤติ (Environmental Crisis)</li>
     * </ul>
     * * @param rawPollutionTotal
     */
    public void processTick(double rawPollutionTotal) {
        CityMasterStats master = CityMasterStats.getInstance();


        master.environment.setDebuffPollutionTotal(rawPollutionTotal);


        master.environment.setDebuffPollutionAir(0);
        master.environment.setDebuffPollutionWater(0);
        master.environment.setDebuffPollutionGround(0);
        master.environment.setDebuffPollutionGarbage(0);

        // อนาคต: ถ้ามลพิษทะลุขีดจำกัด (เช่น เกิน 500) อาจจะแจ้งเตือนภัยพิบัติที่นี่
        // if (rawPollutionTotal > 500) {
        //     System.out.println("แจ้งเตือน: มลพิษในเมืองสูงเกินมาตรฐาน");
        // }
    }
}