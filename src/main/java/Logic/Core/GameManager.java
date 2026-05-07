package Logic.Core;

import Logic.Stats.CityMasterStats;

public class GameManager {
    private static GameManager instance;

    private long currentTick = 0;
    private int gameDay = 1;
    private int gameMonth = 1;
    private double cityLevel = 1;

    private GameManager() {}

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // Getters
    public long getCurrentTick() { return currentTick; }
    public int getGameDay() { return gameDay; }
    public int getGameMonth() { return gameMonth; }
    public double getCityLevel() { return cityLevel; }

    // Setter cityLevel
    public void setCityLevel(double level) { this.cityLevel = Math.max(1.0, level); }


    // Main Game State
    /**
     * Advances the game time by one tick.
     * To be called exclusively by the SimulationManager.
     */
    public void advanceTick() {
        currentTick++;
        if (currentTick > 0 && currentTick % 24 == 0) { // 24 ticks per day
            gameDay++;
            if (gameDay > 30) { // 30 days per month
                gameDay = 1;
                gameMonth++;
            }
            printDailyReport();
        }
    }


    private void printDailyReport() {
        double treasury = CityMasterStats.getInstance().finance.getTreasuryCurrent();
        double netIncome = CityMasterStats.getInstance().finance.getNetIncomeCurrent();
        double tax = CityMasterStats.getInstance().finance.getTaxRevenueBase();

        System.out.println("📅 [Month " + gameMonth + " | Day " + gameDay + "] " +
                "💰 เงินในคลัง: $" + String.format("%.2f", treasury) +
                " | 📈 รายได้สุทธิ: $" + String.format("%.2f", netIncome) +
                " | รายได้: $" + String.format("%.2f", tax)
        );
    }

}