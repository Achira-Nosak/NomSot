package Logic.Stats;

public class CityMasterStats {
    private static CityMasterStats instance;

    private CityMasterStats() {}

    public static CityMasterStats getInstance() {
        if (instance == null) instance = new CityMasterStats();
        return instance;
    }


    // Calls by CityMasterStats.getInstance().{These public final}.get{Something}()
    public final FinancialStats finance = new FinancialStats();
    public final PopulationStats population = new PopulationStats();
    public final InfrastructureStats infrastructure = new InfrastructureStats();
    public final SocialStats social = new SocialStats();
    public final EnvironmentalStats environment = new EnvironmentalStats();
    public final ZoningStats zoning = new ZoningStats();
    public final BuildingStats building = new BuildingStats();          // Special for logic





    // ------------------------------------------------------------------------------------------




    // ------------------------------ Financial ------------------------------
    public static class FinancialStats {
        private volatile double treasuryCurrent = 10000;     //-inf - +inf Main
        private volatile double netIncomeCurrent = 0;        //-inf - +inf Main
        // Special Function
        private volatile double loanInterestRate = 0;        //0.0 - 0.1 Multiplier           // User Adjustable in Game
        // Money Increasing
        private volatile double taxRevenueBase = 0;          //0 - inf                        // Main
        private volatile double taxRevenueBuff = 1.0;         //1.0 - 1.5 Multiplier
        private volatile double taxRevenueDebuff = 1.0;        //0.5 - 1.0 Multiplier
        // Money Increasing Per Zone
        private volatile double taxRateResidential = 1.0;      //0.5 - 1.5 Multiplier           // User Adjustable in Game
        private volatile double taxRateAgricultural = 1.0;     //0.5 - 1.5 Multiplier           // User Adjustable in Game
        private volatile double taxRateIndustrial = 1.0;       //0.5 - 1.5 Multiplier           // User Adjustable in Game
        private volatile double taxRateCommercial = 1.0;       //0.5 - 1.5 Multiplier           // User Adjustable in Game
        // Money Decreasing
        private volatile double maintenanceCostBase = 0;     //0 - inf Main
        private volatile double maintenanceCostBuff = 1.0;     //1.0 - 1.5 Multiplier (Cost-saving)
        private volatile double maintenanceCostDebuff = 1.0;   //0.5 - 1.0 Multiplier (Cost-increasing)





        // Getters
        public double getTreasuryCurrent() { return treasuryCurrent; }
        public double getNetIncomeCurrent() { return netIncomeCurrent; }
        public double getLoanInterestRate() { return loanInterestRate; }
        public double getTaxRevenueBase() { return taxRevenueBase; }
        public double getTaxRevenueBuff() { return taxRevenueBuff; }
        public double getTaxRevenueDebuff() { return taxRevenueDebuff; }
        public double getTaxRateResidential() { return taxRateResidential; }
        public double getTaxRateAgricultural() { return taxRateAgricultural; }
        public double getTaxRateIndustrial() { return taxRateIndustrial; }
        public double getTaxRateCommercial() { return taxRateCommercial; }
        public double getMaintenanceCostBase() { return maintenanceCostBase; }
        public double getMaintenanceCostBuff() { return maintenanceCostBuff; }
        public double getMaintenanceCostDebuff() { return maintenanceCostDebuff; }

        // Setters with Math.clamp
        public void setTreasuryCurrent(double v) { this.treasuryCurrent = v; }
        public void setNetIncomeCurrent(double v) { this.netIncomeCurrent = v; }
        public void setLoanInterestRate(double v) { this.loanInterestRate = Math.clamp(v, 0.0, 0.1); }
        public void setTaxRevenueBase(double v) { this.taxRevenueBase = Math.max(0, v); }
        public void setTaxRevenueBuff(double v) { this.taxRevenueBuff = Math.clamp(v, 1.0, 1.5); }
        public void setTaxRevenueDebuff(double v) { this.taxRevenueDebuff = Math.clamp(v, 0.5, 1.0); }
        public void setTaxRateResidential(double v) { this.taxRateResidential = Math.clamp(v, 0.5, 1.5); }
        public void setTaxRateAgricultural(double v) { this.taxRateAgricultural = Math.clamp(v, 0.5, 1.5); }
        public void setTaxRateIndustrial(double v) { this.taxRateIndustrial = Math.clamp(v, 0.5, 1.5); }
        public void setTaxRateCommercial(double v) { this.taxRateCommercial = Math.clamp(v, 0.5, 1.5); }
        public void setMaintenanceCostBase(double v) { this.maintenanceCostBase = Math.max(0, v); }
        public void setMaintenanceCostBuff(double v) { this.maintenanceCostBuff = Math.clamp(v, 1.0, 1.5); }
        public void setMaintenanceCostDebuff(double v) { this.maintenanceCostDebuff = Math.clamp(v, 0.5, 1.0); }
    }




    // ------------------------------ Population ------------------------------
    public static class PopulationStats {
        private volatile int popCurrent = 0;             //0 - popMax
        private volatile int popMax = 0;                 //0 - inf
        private volatile int popIncreasingRate = 0;      //0 - 10000
        private volatile int popDecreasingRate = 0;      //0 - 10000
        private volatile double popLifeSpan = 75;        //0 - 100
        private volatile double popTotalLabor = 0;       //0 - popCurrent
        private volatile double popUnemploymentRate = 0.05; //0.0 - 1.0 Percent





        // Getters
        public int getPopCurrent() { return popCurrent; }
        public int getPopMax() { return popMax; }
        public int getPopIncreasingRate() { return popIncreasingRate; }
        public int getPopDecreasingRate() { return popDecreasingRate; }
        public double getPopLifeSpan() { return popLifeSpan; }
        public double getPopTotalLabor() { return popTotalLabor; }
        public double getPopUnemploymentRate() { return popUnemploymentRate; }

        // Setters with Math.clamp
        public void setPopCurrent(int v) { this.popCurrent = Math.clamp(v, 0, this.popMax); }
        public void setPopMax(int v) { this.popMax = Math.max(0, v); }
        public void setPopIncreasingRate(int v) { this.popIncreasingRate = Math.clamp(v, 0, 10000); }
        public void setPopDecreasingRate(int v) { this.popDecreasingRate = Math.clamp(v, 0, 10000); }
        public void setPopLifeSpan(double v) { this.popLifeSpan = Math.clamp(v, 0, 100); }
        public void setPopTotalLabor(double v) { this.popTotalLabor = Math.clamp(v, 0, this.popCurrent); }
        public void setPopUnemploymentRate(double v) { this.popUnemploymentRate = Math.clamp(v, 0.0, 1.0); }
    }




    // ------------------------------ Infrastructure (Supply & Demand) ------------------------------
    public static class InfrastructureStats {
        private volatile double powerSupply = 0;               // 0 - inf Main
        private volatile double powerDemand = 0;               // 0 - inf Main
        private volatile double waterSupply = 0;               // 0 - inf Main
        private volatile double waterDemand = 0;               // 0 - inf Main
        private volatile double foodSupply = 0;                // 0 - inf Main
        private volatile double foodDemand = 0;                // 0 - inf Main





        // Getters
        public double getPowerSupply() { return powerSupply; }
        public double getPowerDemand() { return powerDemand; }
        public double getWaterSupply() { return waterSupply; }
        public double getWaterDemand() { return waterDemand; }
        public double getFoodSupply() { return foodSupply; }
        public double getFoodDemand() { return foodDemand; }

        // Setters with Math.clamp
        public void setPowerSupply(double v) { this.powerSupply = Math.max(0, v); }
        public void setPowerDemand(double v) { this.powerDemand = Math.max(0, v); }
        public void setWaterSupply(double v) { this.waterSupply = Math.max(0, v); }
        public void setWaterDemand(double v) { this.waterDemand = Math.max(0, v); }
        public void setFoodSupply(double v) { this.foodSupply = Math.max(0, v); }
        public void setFoodDemand(double v) { this.foodDemand = Math.max(0, v); }
    }




    // ------------------------------ Social ------------------------------
    public static class SocialStats {
        // Happiness
        private volatile double happiness = 100;               // 0 - 100 Percent Main
        private volatile double happinessBuff = 0;             // 0 - 100 (Not sure)
        private volatile double happinessDebuff = 0;           // 0 - 100 (Not sure)
        // Education
        private volatile double educationNon = 0;              // 0 - popCurrent     SubMain
        private volatile double educationBasic = 0;            // 0 - popCurrent     Main
        private volatile double educationVocational = 0;       // 0 - educationBasic SubMain
        private volatile double educationGraduate = 0;         // 0 - educationBasic SubMain
        private volatile double educationBuff = 1.0;             // 1.0 - 2.0 Multiplier
        private volatile double educationDebuff = 1.0;           // 0.0 - 1.0 Multiplier





        // Getters
        public double getHappiness() { return happiness; }
        public double getHappinessBuff() { return happinessBuff; }
        public double getHappinessDebuff() { return happinessDebuff; }
        public double getEducationNon() { return educationNon; }
        public double getEducationBasic() { return educationBasic; }
        public double getEducationVocational() { return educationVocational; }
        public double getEducationGraduate() { return educationGraduate; }
        public double getEducationBuff() { return educationBuff; }
        public double getEducationDebuff() { return educationDebuff; }

        // Setters with Math.clamp
        public void setHappiness(double v) { this.happiness = Math.clamp(v, 0, 100); }
        public void setHappinessBuff(double v) { this.happinessBuff = Math.clamp(v, 0, 100); }
        public void setHappinessDebuff(double v) { this.happinessDebuff = Math.clamp(v, 0, 100); }
        public void setEducationNon(double v) { this.educationNon = Math.max(0, v); } // Upper bound (popCurrent) managed by caller
        public void setEducationBasic(double v) { this.educationBasic = Math.max(0, v); } // Upper bound (popCurrent) managed by caller
        public void setEducationVocational(double v) { this.educationVocational = Math.clamp(v, 0, this.educationBasic); }
        public void setEducationGraduate(double v) { this.educationGraduate = Math.clamp(v, 0, this.educationBasic); }
        public void setEducationBuff(double v) { this.educationBuff = Math.clamp(v, 1.0, 2.0); }
        public void setEducationDebuff(double v) { this.educationDebuff = Math.clamp(v, 0.0, 1.0); }
    }




    // ------------------------------ Environmental ------------------------------
    public static class EnvironmentalStats {
        // Pollution
        private volatile double debuffPollutionTotal = 0;      // 0 - 1000 Main
        private volatile double debuffPollutionAir = 0;        // 0 - 1000 SubMain
        private volatile double debuffPollutionWater = 0;      // 0 - 1000 SubMain
        private volatile double debuffPollutionGround = 0;     // 0 - 1000 SubMain
        private volatile double debuffPollutionGarbage = 0;    // 0 - 1000 SubMain
        // Coverage
        private volatile double healthCoverage = 0;            // 0.0 - 1.0 Percent Coverage
        private volatile double fireCoverage = 0;              // 0.0 - 1.0 Percent Coverage
        private volatile double policeCoverage = 0;            // 0.0 - 1.0 Percent Coverage
        // OTHER Buff
        private volatile double buffHealth = 0;                // 0 - 100 Percent SubMain
        private volatile double buffSafety = 0;                // 0 - 100 Percent SubMain
        private volatile double buffGreenery = 0;              // 0 - 100 Percent SubMain
        private volatile double buffCityBeauty = 0;
        // OTHER Debuff
        private volatile double debuffNoise = 0;               // 0 - 100 Percent SubMain
        private volatile double debuffCrime = 0;               // 0 - 100 Percent SubMain
        private volatile double debuffFireHazard = 0;          // 0 - 100 Percent SubMain





        // Getters
        public double getDebuffPollutionTotal() { return debuffPollutionTotal; }
        public double getDebuffPollutionAir() { return debuffPollutionAir; }
        public double getDebuffPollutionWater() { return debuffPollutionWater; }
        public double getDebuffPollutionGround() { return debuffPollutionGround; }
        public double getDebuffPollutionGarbage() { return debuffPollutionGarbage; }
        public double getHealthCoverage() { return healthCoverage; }
        public double getFireCoverage() { return fireCoverage; }
        public double getPoliceCoverage() { return policeCoverage; }
        public double getBuffHealth() { return buffHealth; }
        public double getBuffSafety() { return buffSafety; }
        public double getBuffGreenery() { return buffGreenery; }
        public double getBuffCityBeauty() { return buffCityBeauty; }
        public double getDebuffNoise() { return debuffNoise; }
        public double getDebuffCrime() { return debuffCrime; }
        public double getDebuffFireHazard() { return debuffFireHazard; }

        // Setters with Math.clamp
        public void setDebuffPollutionTotal(double v) { this.debuffPollutionTotal = Math.clamp(v, 0, 1000); }
        public void setDebuffPollutionAir(double v) { this.debuffPollutionAir = Math.clamp(v, 0, 1000); }
        public void setDebuffPollutionWater(double v) { this.debuffPollutionWater = Math.clamp(v, 0, 1000); }
        public void setDebuffPollutionGround(double v) { this.debuffPollutionGround = Math.clamp(v, 0, 1000); }
        public void setDebuffPollutionGarbage(double v) { this.debuffPollutionGarbage = Math.clamp(v, 0, 1000); }
        public void setHealthCoverage(double v) { this.healthCoverage = Math.clamp(v, 0.0, 1.0); }
        public void setFireCoverage(double v) { this.fireCoverage = Math.clamp(v, 0.0, 1.0); }
        public void setPoliceCoverage(double v) { this.policeCoverage = Math.clamp(v, 0.0, 1.0); }
        public void setBuffHealth(double v) { this.buffHealth = Math.clamp(v, 0, 100); }
        public void setBuffSafety(double v) { this.buffSafety = Math.clamp(v, 0, 100); }
        public void setBuffGreenery(double v) { this.buffGreenery = Math.clamp(v, 0, 100); }
        public void setBuffCityBeauty(double v) { this.buffCityBeauty = Math.clamp(v, 0, 100); }
        public void setDebuffNoise(double v) { this.debuffNoise = Math.clamp(v, 0, 100); }
        public void setDebuffCrime(double v) { this.debuffCrime = Math.clamp(v, 0, 100); }
        public void setDebuffFireHazard(double v) { this.debuffFireHazard = Math.clamp(v, 0, 100); }
    }




    // ------------------------------ Zoning ------------------------------
    public static class ZoningStats {
        // Zoning Info
        private volatile int residentialProportion = 0;                      // 0 - inf ByGrid
        private volatile int agriculturalProportion = 0;                     // 0 - inf ByGrid
        private volatile int industrialProportion = 0;                       // 0 - inf ByGrid
        private volatile int commercialProportion = 0;                       // 0 - inf ByGrid
        // RCIAF Demand ( 0 ถึง 100 )
        private volatile double residentialDemand = 50;                      // -100 - 100   Percent
        private volatile double agriculturalDemand = 50;                     // -100 - 100   Percent
        private volatile double industrialDemand = 50;                       // -100 - 100   Percent
        private volatile double commercialDemand = 50;                       // -100 - 100   Percent
        // Zone Production
        private volatile double residentialTotalTaxRevenue = 0;              // 0 - inf
        private volatile double residentialTotalMaintenanceCost = 0;         // 0 - inf

        private volatile double agriculturalTotalFoodProduction = 0;         // 0 - inf
        private volatile double agriculturalTotalTaxRevenue = 0;              // 0 - inf
        private volatile double agriculturalTotalMaintenanceCost = 0;        // 0 - inf
        private volatile double agriculturalEfficiencyBuff = 1.0;              // 1.0 - 2.0 Multiplier
        private volatile double agriculturalEfficiencyDebuff = 1.0;            // 0.0 - 1.0 Multiplier

        private volatile double industrialTotalTaxRevenue = 0;               // 0 - inf
        private volatile double industrialTotalMaintenanceCost = 0;          // 0 - inf
        private volatile double industrialEfficiencyBuff = 1.0;                // 1.0 - 2.0 Multiplier
        private volatile double industrialEfficiencyDebuff = 1.0;              // 0.0 - 1.0 Multiplier

        private volatile double commercialTotalTaxRevenue = 0;               // 0 - inf
        private volatile double commercialTotalMaintenanceCost = 0;          // 0 - inf
        private volatile double commercialEfficiencyBuff = 1.0;                // 1.0 - 2.0 Multiplier
        private volatile double commercialEfficiencyDebuff = 1.0;              // 0.0 - 1.0 Multiplier





        // Getters
        public int getResidentialProportion() { return residentialProportion; }
        public int getAgriculturalProportion() { return agriculturalProportion; }
        public int getIndustrialProportion() { return industrialProportion; }
        public int getCommercialProportion() { return commercialProportion; }
        public double getResidentialDemand() { return residentialDemand; }
        public double getAgriculturalDemand() { return agriculturalDemand; }
        public double getIndustrialDemand() { return industrialDemand; }
        public double getCommercialDemand() { return commercialDemand; }
        public double getResidentialTotalTaxRevenue() { return residentialTotalTaxRevenue; }
        public double getResidentialTotalMaintenanceCost() { return residentialTotalMaintenanceCost; }
        public double getAgriculturalTotalFoodProduction() { return agriculturalTotalFoodProduction; }
        public double getAgriculturalTotalTaxRevenue() { return agriculturalTotalTaxRevenue; }
        public double getAgriculturalTotalMaintenanceCost() { return agriculturalTotalMaintenanceCost; }
        public double getAgriculturalEfficiencyBuff() { return agriculturalEfficiencyBuff; }
        public double getAgriculturalEfficiencyDebuff() { return agriculturalEfficiencyDebuff; }
        public double getIndustrialTotalTaxRevenue() { return industrialTotalTaxRevenue; }
        public double getIndustrialTotalMaintenanceCost() { return industrialTotalMaintenanceCost; }
        public double getIndustrialEfficiencyBuff() { return industrialEfficiencyBuff; }
        public double getIndustrialEfficiencyDebuff() { return industrialEfficiencyDebuff; }
        public double getCommercialTotalTaxRevenue() { return commercialTotalTaxRevenue; }
        public double getCommercialTotalMaintenanceCost() { return commercialTotalMaintenanceCost; }
        public double getCommercialEfficiencyBuff() { return commercialEfficiencyBuff; }
        public double getCommercialEfficiencyDebuff() { return commercialEfficiencyDebuff; }

        // Setters with Math.clamp
        public void setResidentialProportion(int v) { this.residentialProportion = Math.max(0, v); }
        public void setAgriculturalProportion(int v) { this.agriculturalProportion = Math.max(0, v); }
        public void setIndustrialProportion(int v) { this.industrialProportion = Math.max(0, v); }
        public void setCommercialProportion(int v) { this.commercialProportion = Math.max(0, v); }
        public void setResidentialDemand(double v) { this.residentialDemand = Math.clamp(v, -100, 100); }
        public void setAgriculturalDemand(double v) { this.agriculturalDemand = Math.clamp(v, -100, 100); }
        public void setIndustrialDemand(double v) { this.industrialDemand = Math.clamp(v, -100, 100); }
        public void setCommercialDemand(double v) { this.commercialDemand = Math.clamp(v, -100, 100); }
        public void setResidentialTotalTaxRevenue(double v) { this.residentialTotalTaxRevenue = Math.max(0, v); }
        public void setResidentialTotalMaintenanceCost(double v) { this.residentialTotalMaintenanceCost = Math.max(0, v); }
        public void setAgriculturalTotalFoodProduction(double v) { this.agriculturalTotalFoodProduction = Math.max(0, v); }
        public void setAgriculturalTotalTaxRevenue(double v) { this.agriculturalTotalTaxRevenue = Math.max(0, v); }
        public void setAgriculturalTotalMaintenanceCost(double v) { this.agriculturalTotalMaintenanceCost = Math.max(0, v); }
        public void setAgriculturalEfficiencyBuff(double v) { this.agriculturalEfficiencyBuff = Math.clamp(v, 1.0, 2.0); }
        public void setAgriculturalEfficiencyDebuff(double v) { this.agriculturalEfficiencyDebuff = Math.clamp(v, 0.0, 1.0); }
        public void setIndustrialTotalTaxRevenue(double v) { this.industrialTotalTaxRevenue = Math.max(0, v); }
        public void setIndustrialTotalMaintenanceCost(double v) { this.industrialTotalMaintenanceCost = Math.max(0, v); }
        public void setIndustrialEfficiencyBuff(double v) { this.industrialEfficiencyBuff = Math.clamp(v, 1.0, 2.0); }
        public void setIndustrialEfficiencyDebuff(double v) { this.industrialEfficiencyDebuff = Math.clamp(v, 0.0, 1.0); }
        public void setCommercialTotalTaxRevenue(double v) { this.commercialTotalTaxRevenue = Math.max(0, v); }
        public void setCommercialTotalMaintenanceCost(double v) { this.commercialTotalMaintenanceCost = Math.max(0, v); }
        public void setCommercialEfficiencyBuff(double v) { this.commercialEfficiencyBuff = Math.clamp(v, 1.0, 2.0); }
        public void setCommercialEfficiencyDebuff(double v) { this.commercialEfficiencyDebuff = Math.clamp(v, 0.0, 1.0); }
    }




    // ------------------------------ Building Counts & Flags ------------------------------
    public static class BuildingStats {
        // Using concurrent for Thread-Safe
        private final java.util.Map<String, Integer> counts = new java.util.concurrent.ConcurrentHashMap<>();

        // Count this building in city by using "buildingId"
        public int getCount(String buildingId) {
            return counts.getOrDefault(buildingId, 0);
        }

        // Check if city has this building by using "buildingId" The ID of the building from config.
        public boolean hasBuilding(String buildingId) {
            return getCount(buildingId) > 0;
        }

        // Call When Constructing new building
        public synchronized void incrementCount(String buildingId) {
            counts.merge(buildingId, 1, Integer::sum);
        }

        // Call When Destructing present building
        public synchronized void decrementCount(String buildingId) {
            counts.computeIfPresent(buildingId, (id, count) -> {
                int newCount = count - 1;
                if (newCount > 0) {
                    return newCount;
                } else {
                    return null;
                }
            });
        }
    }
}