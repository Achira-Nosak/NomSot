package Config;

import javafx.scene.paint.Color;

public class BuildingData {
    // Basic Info
    private String id;
    private String name;
    private String description = ""; // เพิ่มคำอธิบายตึกไว้โชว์ใน UI
    private String logicGroup = "NONE";
    private String buildBarTag = "Uncategorized";

    // === Graphic Data ===
    private String assetFileName = ""; // ชื่อไฟล์รูป
    private String baseColorHex = "#32CD32"; // สี Base ตอนวาดข้าวหลามตัด (Hex Code)
    private double xOffset = 0.0;
    private double yOffset = 0.0;
    private String specialLogicClass = null; // ชื่อคลาส Logic พิเศษ (ถ้ามี)

    // Construction & Maintenance
    private double constructionCost = 0.0;
    private double maintenance = 0.0;
    private double upgradeCost = 0.0;

    // Resources (Input/Output)
    private double powerConsumption = 0.0;
    private double powerProduction = 0.0;
    private double waterConsumption = 0.0;
    private double waterProduction = 0.0;
    private double foodConsumption = 0.0;
    private double foodProduction = 0.0;

    // Economy & Tax
    private double baseTaxRevenue = 0.0; // ภาษีที่เก็บได้ต่อ Tick
    private double taxBonus = 0.0;
    private double propertyValue = 0.0;  // มูลค่าทรัพย์สิน

    // Population & Employment
    private int maxResidents = 0;
    private int maxWorkers = 0;
    private int educationRequired = 0; // ระดับการศึกษาที่ต้องการ 0-4 (0= unEdu 1=Elementary 2=HighSchool 3=Vocational 4=Graduated)
    private int maxPeopleCapacity = 0;

    // Social Buffs & Radius
    private double healthBonus = 0.0;
    private double eduBonus = 0.0;
    private double safetyBonus = 0.0;
    private double happinessBonus = 0.0;
    private double efficiencyBonus = 0.0;

    private double pollutionReduction = 0.0;
    private double crimeReduction = 0.0;

    private double serviceRadius = 0.0;

    // Environment
    private double pollutionIntensity = 0.0;
    private double pollutionRadius = 0.0;
    private double landValueBonus = 0.0;  //ไม่มั่นใจจะได้ใช้มั้ย

    public BuildingData() {}

    public Color getBaseColor() {
        try {
            return Color.web(baseColorHex);
        } catch (Exception e) {
            return Color.MAGENTA; // สี Error ถ้าใส่ Hex ผิด
        }
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getLogicGroup() { return logicGroup; }
    public String getBuildBarTag() { return buildBarTag; }
    public String getAssetFileName() { return assetFileName; }
    public double getXOffset() { return xOffset; }
    public double getYOffset() { return yOffset; }
    public String getSpecialLogicClass() { return specialLogicClass; }
    public double getConstructionCost() {
        return constructionCost;
    }
    public double getMaintenance() {
        return maintenance;
    }
    public double getUpgradeCost() {
        return upgradeCost;
    }
    public double getPowerConsumption() {
        return powerConsumption;
    }
    public double getPowerProduction() {
        return powerProduction;
    }
    public double getWaterConsumption() {
        return waterConsumption;
    }
    public double getWaterProduction() {
        return waterProduction;
    }
    public double getFoodConsumption() {
        return foodConsumption;
    }
    public double getFoodProduction() {
        return foodProduction;
    }
    public double getBaseTaxRevenue() {
        return baseTaxRevenue;
    }
    public double getTaxBonus() {
        return taxBonus;
    }
    public double getPropertyValue() {
        return propertyValue;
    }
    public int getMaxResidents() {
        return maxResidents;
    }
    public int getMaxWorkers() {
        return maxWorkers;
    }
    public int getEducationRequired() {
        return educationRequired;
    }
    public int getMaxPeopleCapacity() {
        return maxPeopleCapacity;
    }
    public double getHealthBonus() {
        return healthBonus;
    }
    public double getEduBonus() {
        return eduBonus;
    }
    public double getSafetyBonus() {
        return safetyBonus;
    }
    public double getHappinessBonus() {
        return happinessBonus;
    }
    public double getEfficiencyBonus() {
        return efficiencyBonus;
    }
    public double getPollutionReduction() {
        return pollutionReduction;
    }
    public double getCrimeReduction() {
        return crimeReduction;
    }
    public double getServiceRadius() {
        return serviceRadius;
    }
    public double getPollutionIntensity() {
        return pollutionIntensity;
    }
    public double getPollutionRadius() {
        return pollutionRadius;
    }
    public double getLandValueBonus() {
        return landValueBonus;
    }
}
