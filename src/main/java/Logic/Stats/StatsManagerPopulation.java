package Logic.Stats;


/**
 * <ul>
 * <li>Singleton</li>
 * <li>จัดการ logic และ Stat ทั้งหมดที่เกี่ยวกับ Population ของเกม</li>
 * <li>Class นี้ถูกเรียกใช้โดย SimulationManager</li>
 * </ul>
 */
public class StatsManagerPopulation {
    private static StatsManagerPopulation instance;

    private double currentOccupancyRate = 0.0;

    private StatsManagerPopulation() {}

    public static StatsManagerPopulation getInstance() {
        if (instance == null) {
            instance = new StatsManagerPopulation();
        }
        return instance;
    }

    /**
     * จัดการ Logic และ Stat (Population) ของเกมในแต่ละ Tick
     * <p><b>Logic Overview:</b>
     * <ul>
     * <li>สาธิตการคำนวณ Population เบื้องต้น</li>
     * <li>totalPopulation = MaxPop(from simulationManager) * occupancyRate(by Happiness 0.0(0%) - 1.0(100%)</li>
     * </ul>
     * * <p><b>Future Enhancement:</b>
     * <ul>
     * <li>พัฒนาระบบการรวบรวมข้อมูล currentResidents currentWorkers จากตึกแต่ละ building โดยตรงและ Real-Time เพื่อความแม่นยำ</li>
     * <li>พัฒนาระบบการเพิ่มลดของประชากรตาม Environmental and Social Factors และ PopLifeSpan</li>
     * <li>พัฒนาระบบตลาดแรงงานแยกตามระดับการศึกษา (Elementary, HighSchool, Vocational, University)</li>
     * </ul>
     * * @param rawTotalMaxPop
     */
    public void processTick(double rawTotalMaxPop) {

        CityMasterStats master = CityMasterStats.getInstance();

        // 1. ดึงความสุขรวมของเมืองจาก MasterStats
        double currentHappiness = master.social.getHappiness();

        // 2. คำนวณอัตราการเข้าพัก (Occupancy Rate)
        // ถ้าความสุข 100 = คนอยู่เต็ม 100% (1.0), ถ้าความสุข 0 = คนย้ายออกหมด (0.0)
        this.currentOccupancyRate = Math.clamp(currentHappiness / 100.0, 0.0, 1.0);

        // 3. คำนวณประชากรจริงที่อาศัยอยู่ในเมือง
        int totalHousingCapacity = (int) rawTotalMaxPop;
        int totalPopulation = (int) Math.round(totalHousingCapacity * this.currentOccupancyRate);

        // 4. อัปเดตข้อมูลกลับไปเก็บที่ศูนย์กลาง (CityMasterStats)

        master.population.setPopMax(totalHousingCapacity);
        master.population.setPopCurrent(totalPopulation);
        master.population.setPopIncreasingRate((int) this.currentOccupancyRate);

    }


    public double getOccupancyRate() {
        return currentOccupancyRate;
    }
}