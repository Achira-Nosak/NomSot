package Logic.Stats;

public class StatsManagerEnvironmental {
    private static StatsManagerEnvironmental instance;

    private StatsManagerEnvironmental() {}

    public static StatsManagerEnvironmental getInstance() {
        if (instance == null) {
            instance = new StatsManagerEnvironmental();
        }
        return instance;
    }


    public void processTick(double rawPollutionTotal) {
        CityMasterStats master = CityMasterStats.getInstance();


        master.environment.setDebuffPollutionTotal(rawPollutionTotal);


        master.environment.setDebuffPollutionAir(0);
        master.environment.setDebuffPollutionWater(0);
        master.environment.setDebuffPollutionGround(0);
        master.environment.setDebuffPollutionGarbage(0);

        // อนาคต: ถ้ามลพิษทะลุขีดจำกัด (เช่น เกิน 500) อาจจะแจ้งเตือนภัยพิบัติที่นี่
        // if (rawPollutionTotal > 500) {
        //     System.out.println("⚠️ แจ้งเตือน: มลพิษในเมืองสูงเกินมาตรฐาน!");
        // }
    }
}