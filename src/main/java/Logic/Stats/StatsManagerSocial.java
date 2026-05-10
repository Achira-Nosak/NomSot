package Logic.Stats;


/**
 * <ul>
 * <li>Singleton</li>
 * <li>จัดการ logic และ Stat ทั้งหมดที่เกี่ยวกับ Social ของเกม</li>
 * <li>Class นี้ถูกเรียกใช้โดย SimulationManager</li>
 * </ul>
 */
public class StatsManagerSocial {
    private static StatsManagerSocial instance;

    private StatsManagerSocial() {}

    public static StatsManagerSocial getInstance() {
        if (instance == null) {
            instance = new StatsManagerSocial();
        }
        return instance;
    }

    /**
     * จัดการ Logic และ Stat (Social) ของเกมในแต่ละ Tick
     * <p><b>Logic Overview:</b>
     * <ul>
     * <li>สาธิตการคำนวณ Happiness เบื้องต้น</li>
     * <li>totalHappiness = Base(from CityMasterStat) - PollutionPenalty(Pollution 10 หน่วย หัก Happiness 1%)</li>
     * </ul>
     * * <p><b>Future Enhancement:</b>
     * <ul>
     * <li>พัฒนาระบบการรวบรวมข้อมูล currentHappiness จากตึกแต่ละ building โดยตรงและ Real-Time เพื่อความแม่นยำ</li>
     * <li>พัฒนาระบบการคำนวณ happiness และค่าต่างๆ ตาม Environmental, Financial, Infrastructure Factors จาก AuraMapManager</li>
     * <li>พัฒนาแบบจำลองการคำนวณคุณภาพสังคมในด้านต่างๆ เช่น เช่น Wealth Index, Safety and Security Index</li>
     * </ul>
     * * @param averageLocalHappiness
     */
    public void processTick(double averageLocalHappiness) {
        CityMasterStats master = CityMasterStats.getInstance();

        double happinessPenalty = 0.0;

        // (Pollution 10 หน่วย หัก Happiness 1%)
        double pollution = master.environment.getDebuffPollutionTotal();
        happinessPenalty += (pollution / 10.0);

        double finalHappiness = averageLocalHappiness - happinessPenalty;

        master.social.setHappiness(finalHappiness);
    }
}