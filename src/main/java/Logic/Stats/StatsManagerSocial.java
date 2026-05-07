package Logic.Stats;

public class StatsManagerSocial {
    private static StatsManagerSocial instance;

    private StatsManagerSocial() {}

    public static StatsManagerSocial getInstance() {
        if (instance == null) {
            instance = new StatsManagerSocial();
        }
        return instance;
    }


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