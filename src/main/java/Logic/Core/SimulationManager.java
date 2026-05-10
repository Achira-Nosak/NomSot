package Logic.Core;

import GUI.GUIServices.TerrainMapManager;
import Logic.Stats.*;
import Model.BaseBuilding;
import Config.Enums.ZoneType;
import Model.UtilityBuilding;


/**
 * <ul>
 * <li>Singleton</li>
 * <li>หัวใจหลักของการ simulation เกมสร้างเมืองนี้</li>
 * <li>Hybrid OOP-DOD Architecture: OOP จัดการพฤติกรรมที่ซับซ้อนรายตึก (Polymorphism) และใช้ DOD (Parallel Arrays) ในการประมวลผลตัวเลขรวมของข้อมูลชุดใหญ่เพื่อประสิทธิภาพและความเร็ว</li>
 * <li>Separation of Behavior and Data: แยกลอจิกการทำงาน (Behavior) ไว้ที่ Object ตึก และแยกข้อมูลสถิติดิบ (Raw Data) ออกมาเรียงใน Array เพื่อให้ระบบ Simulation รันคำนวณได้ไวที่สุด</li>
 * <li>Swap and Pop: เมื่อทุบตึก ใช้วิธีสลับข้อมูลตัวสุดท้ายมาแทนที่ช่องว่าง ทั้งในฝั่ง Array ข้อมูลและ Array ของ Object</li>
 * </ul>
 */
import java.util.Arrays;

public class SimulationManager {
    private static SimulationManager instance;

    // Data-Oriented Design Arrays
    private int currentCapacity = 100;
    private int currentCount = 0;
    private double[] powerConsumptions;
    private double[] waterConsumptions;
    private double[] foodConsumptions;
    private double[] powerProductions;
    private double[] waterProductions;
    private double[] foodProductions;
    private double[] pollutionTotals;
    private double[] taxRevenues;
    private double[] maintenanceCosts;
    private boolean[] isOperational;
    private BaseBuilding[] buildingRefs;
    private ZoneType[] zoneTypes;
    private double[] happinessLevels; // 0-100 happiness for each building
    private double[] maxResidents;
    private double[] currentResidents;

    public static SimulationManager getInstance() {
        if (instance == null) instance = new SimulationManager();
        return instance;
    }

    // --------------- Initial ---------------
    private SimulationManager() {
        initArrays(currentCapacity);
    }

    private void initArrays(int size) {
        powerConsumptions = new double[size];
        waterConsumptions = new double[size];
        foodConsumptions = new double[size];
        powerProductions = new double[size];
        waterProductions = new double[size];
        foodProductions = new double[size];
        pollutionTotals = new double[size];
        taxRevenues = new double[size];
        maintenanceCosts = new double[size];
        isOperational = new boolean[size];
        buildingRefs = new BaseBuilding[size];
        zoneTypes = new ZoneType[size];
        happinessLevels = new double[size];
        maxResidents = new double[size];
        currentResidents = new double[size];
    }

    /**
     * Dynamic Resizing ถูกเรียกใช้ทุกครั้งที่มีการลงทะเบียนอาคารใหม่ (Register Building)
     * <ul>
     * <li>Cache Friendly: Primitive Array จะเก็บข้อมูลตัวเลขเรียงติดกันใน Memory ทำให้ CPU Cache อ่านข้อมูลรวดเดียวกว่า ArrayList</li>
     * <li>ขยายขนาดทีละ 2 เท่า</li>
     * </ul>
     */
    // --------------- Dynamic Resizing ---------------
    private void ensureCapacity() {
        if (currentCount >= currentCapacity) {
            currentCapacity *= 2; // ขยายทีละ 2 เท่า

            powerConsumptions = Arrays.copyOf(powerConsumptions, currentCapacity);
            waterConsumptions = Arrays.copyOf(waterConsumptions, currentCapacity);
            foodConsumptions = Arrays.copyOf(foodConsumptions, currentCapacity);
            powerProductions = Arrays.copyOf(powerProductions, currentCapacity);
            waterProductions = Arrays.copyOf(waterProductions, currentCapacity);
            foodProductions = Arrays.copyOf(foodProductions, currentCapacity);
            pollutionTotals = Arrays.copyOf(pollutionTotals, currentCapacity);
            taxRevenues = Arrays.copyOf(taxRevenues, currentCapacity);
            maintenanceCosts = Arrays.copyOf(maintenanceCosts, currentCapacity);
            isOperational = Arrays.copyOf(isOperational, currentCapacity);
            buildingRefs = Arrays.copyOf(buildingRefs, currentCapacity);
            zoneTypes = Arrays.copyOf(zoneTypes, currentCapacity); // CHANGED
            happinessLevels = Arrays.copyOf(happinessLevels, currentCapacity);
            maxResidents = Arrays.copyOf(maxResidents, currentCapacity);
            currentResidents = Arrays.copyOf(currentResidents, currentCapacity);

            System.out.println("Arrays resized to: " + currentCapacity);
        }
    }

    // --------------- Register Every Building ---------------
    /**
     * เรียกใช้เมื่อมีการ construct building เพื่อ register เข้าระบบกลาง
     */
    public void registerBuilding(BaseBuilding b) {
        ensureCapacity();

        int index = currentCount;
        powerConsumptions[index] = b.getStats().getPowerConsumption();
        waterConsumptions[index] = b.getStats().getWaterConsumption();
        foodConsumptions[index] = b.getStats().getFoodConsumption();
        powerProductions[index] = b.getStats().getPowerProduction();
        waterProductions[index] = b.getStats().getWaterProduction();
        foodProductions[index] = b.getStats().getFoodProduction();
        pollutionTotals[index] = b.getStats().getPollutionIntensity();
        taxRevenues[index] = b.getStats().getBaseTaxRevenue();
        maintenanceCosts[index] = b.getStats().getMaintenance();
        isOperational[index] = true;
        buildingRefs[index] = b;
        happinessLevels[index] = 50.0;
        maxResidents[index] = b.getStats().getMaxResidents();
        currentResidents[index] = 0;

        zoneTypes[index] = b.getZoneType();

        b.setDataIndex(index);
        GridDirtyFlag.getInstance().makeGridDirty();

        // Update building count stats
        CityMasterStats.getInstance().building.incrementCount(b.getStats().getId());

        currentCount++;
    }

    /**
     * เรียกใช้เมื่อมีการ upgrade building เพื่อ update ข้อมูล Array แต่ละตัว
     */
    // --------------- Update Building Data (When Upgrade) ---------------
    public void updateBuildingData(BaseBuilding b) {
        int index = b.getDataIndex();

        if (index < 0 || index >= currentCount) return;

        powerConsumptions[index] = b.getCurrentPowerConsume();
        waterConsumptions[index] = b.getCurrentWaterConsume();
        foodConsumptions[index] = b.getCurrentFoodConsume();

        if (b instanceof UtilityBuilding) {
            UtilityBuilding utility = (UtilityBuilding) b;
            powerProductions[index] = utility.getCurrentPowerProduction();
            waterProductions[index] = utility.getCurrentWaterProduction();
            foodProductions[index] = utility.getCurrentFoodProduction();
        } else {
            powerProductions[index] = b.getStats().getPowerProduction();
            waterProductions[index] = b.getStats().getWaterProduction();
            foodProductions[index] = b.getStats().getFoodProduction();
        }

        pollutionTotals[index] = b.getStats().getPollutionIntensity();
        taxRevenues[index] = b.getCurrentTax();
        maintenanceCosts[index] = b.getCurrentMaintenance();
        happinessLevels[index] = b.getCurrentHappiness();
        maxResidents[index] = b.getStats().getMaxResidents();

        GridDirtyFlag.getInstance().makeGridDirty();
    }

    /**
     * เรียกใช้เมื่อมีการ destruct building เพื่อ ลบข้อมูลตึกและทำการ swap and pop
     */
    // --------------- Remove Building ---------------
    public void removeBuilding(int indexToRemove) {
        if (indexToRemove < 0 || indexToRemove >= currentCount) return;

        // Update building count stats BEFORE removing the reference
        BaseBuilding removedBuilding = buildingRefs[indexToRemove];
        if (removedBuilding != null) {
            CityMasterStats.getInstance().building.decrementCount(removedBuilding.getStats().getId());
        }

        int lastIndex = currentCount - 1;

        if (indexToRemove != lastIndex) {
            // Swap DOD
            powerConsumptions[indexToRemove] = powerConsumptions[lastIndex];
            waterConsumptions[indexToRemove] = waterConsumptions[lastIndex];
            foodConsumptions[indexToRemove] = foodConsumptions[lastIndex];
            powerProductions[indexToRemove] = powerProductions[lastIndex];
            waterProductions[indexToRemove] = waterProductions[lastIndex];
            foodProductions[indexToRemove] = foodProductions[lastIndex];
            pollutionTotals[indexToRemove] = pollutionTotals[lastIndex];
            taxRevenues[indexToRemove] = taxRevenues[lastIndex];
            maintenanceCosts[indexToRemove] = maintenanceCosts[lastIndex];
            isOperational[indexToRemove] = isOperational[lastIndex];
            zoneTypes[indexToRemove] = zoneTypes[lastIndex]; // CHANGED
            happinessLevels[indexToRemove] = happinessLevels[lastIndex];
            maxResidents[indexToRemove] = maxResidents[lastIndex];
            currentResidents[indexToRemove] = currentResidents[lastIndex];

            // Move Reference Building
            buildingRefs[indexToRemove] = buildingRefs[lastIndex];
            buildingRefs[indexToRemove].setDataIndex(indexToRemove);
        }

        powerConsumptions[lastIndex] = 0;
        waterConsumptions[lastIndex] = 0;
        foodConsumptions[lastIndex] = 0;
        powerProductions[lastIndex] = 0;
        waterProductions[lastIndex] = 0;
        foodProductions[lastIndex] = 0;
        pollutionTotals[lastIndex] = 0;
        taxRevenues[lastIndex] = 0;
        maintenanceCosts[lastIndex] = 0;
        isOperational[lastIndex] = false;
        buildingRefs[lastIndex] = null;
        zoneTypes[lastIndex] = ZoneType.UNZONED;
        happinessLevels[lastIndex] = 0;
        maxResidents[lastIndex] = 0;
        currentResidents[lastIndex] = 0;

        GridDirtyFlag.getInstance().makeGridDirty();
        currentCount--;
    }

    /**
     * Add on removeBuilding() เพื่อไว้เรียกใช้ที่ InputSensing เมื่อมีการทุบตึกที่ตำแหน่งนั้นๆ
     */
    // --------------- Remove Building By Position (x,y) ---------------
    public void removeBuildingAt(int gridX, int gridY) {
        for (int i = 0; i < currentCount; i++) {
            BaseBuilding b = buildingRefs[i];

            if (b != null && b.getGridX() == gridX && b.getGridY() == gridY) {

                removeBuilding(i);
                System.out.println("Demolished building at [" + gridX + "," + gridY + "] (Index: " + i + ")");
                return;
            }
        }
        System.out.println("No building found at [" + gridX + "," + gridY + "] to demolish.");
    }

    /**
     * เรียกใช้เมื่อมีการ construct building เพื่อเช็คว่าสร้างได้ไหม ก่อนสร้างจริง
     */
    public boolean canConstruct(BaseBuilding b) {
        CityMasterStats stats = CityMasterStats.getInstance();
        // 1. เช็คเงิน
        if (stats.finance.getTreasuryCurrent() < b.getStats().getConstructionCost()) return false;
        // 2. เช็คน้ำ (Terrain)
        if (TerrainMapManager.getInstance().isWater(b.getGridX(), b.getGridY())) return false;
        // 3. เช็คกำลังการผลิตไฟฟ้าและน้ำที่เหลืออยู่
        double powerSurplus = stats.infrastructure.getPowerSupply() - stats.infrastructure.getPowerDemand();
        double waterSurplus = stats.infrastructure.getWaterSupply() - stats.infrastructure.getWaterDemand();
        if (b.getCurrentPowerConsume() > 0 && powerSurplus < b.getCurrentPowerConsume()) return false;
        if (b.getCurrentWaterConsume() > 0 && waterSurplus < b.getCurrentWaterConsume()) return false;

        return true;
    }

    /**
     * Add on registerBuilding() เพื่อไว้เรียกใช้ที่ InputSensing เมื่อมีการสร้างตึก
     */
    public void construct(BaseBuilding b) {
        CityMasterStats stats = CityMasterStats.getInstance();
        // หักเงิน
        stats.finance.setTreasuryCurrent(stats.finance.getTreasuryCurrent() - b.getStats().getConstructionCost());
        // จดทะเบียนตึกเข้าสู่ระบบ
        GameMapManager.getInstance().setTile(b.getGridX(), b.getGridY(), b.getBuildingId());
        registerBuilding(b);
    }


    /**
     * หัวใจหลักของระบบ Simulation (Main Loop) ถูกเรียกทำงานทุกๆ 1 Tick
     * <p><b>หมายเหตุ: ลอจิกปัจจุบันเป็นการวางโครงสร้างพื้นฐานเพื่อสาธิต Data Flow ซึ่งออกแบบสถาปัตยกรรมไว้ให้รองรับ Future Enhancement ได้ทันที</b></p>
     * * <p>Execution Pipeline (ลำดับการทำงาน):</p>
     * <ul>
     * <li>1. OOP Processing: วนลูปเรียก onTick() ให้แต่ละตึกจัดการลอจิกเฉพาะตัว</li>
     * <li>2. Grid Optimization: เช็ค GridDirtyFlag เพื่ออัปเดตโครงข่ายน้ำไฟเฉพาะเวลาที่แมพมีการเปลี่ยนแปลง</li>
     * <li>3. DOD Processing: รันลูปคำนวณและรวมค่าจาก Array (ข้ามตึกที่ปิดใช้งาน) พร้อมแยกหมวดหมู่ตาม ZoneType</li>
     * <li>4. Data Pipeline: ส่งตัวเลขที่รวบรวมได้ ไหลไปตามท่อของ StatsManager แต่ละฝ่ายตามลำดับ (Infrastructure -> Environment -> Social -> Finance -> Population)</li>
     * </ul>
     */
    // --------------- Main Update Loop ---------------
    public void update() {
        // OOP Loop
        for (int i = 0; i < currentCount; i++) {
            if (buildingRefs[i] != null) {
                buildingRefs[i].onTick(GameManager.getInstance().getCurrentTick());
            }
        }

        // GridDirtyCheck
        GridDirtyFlag.getInstance().updateIfGridDirty();

        // ตัวแปรเก็บ Raw Data
        double totalPowerDemand = 0, totalWaterDemand = 0, totalFoodDemand = 0;
        double totalPowerSupply = 0, totalWaterSupply = 0, totalFoodSupply = 0;
        double rawPollutionTotal = 0;

        double sumHappiness = 0;
        int activeBuildingCount = 0;
        double totalMaxPop = 0;

        double rawResTax = 0, rawComTax = 0, rawIndTax = 0, rawAgrTax = 0;
        double rawResMaint = 0, rawComMaint = 0, rawIndMaint = 0, rawAgrMaint = 0, rawOtherMaint = 0;

        // DOD Loop
        for (int i = 0; i < currentCount; i++) {
            if (!isOperational[i]) {
                happinessLevels[i] = 0;
                continue; // ถ้าตึกปิดใช้งาน ก็ข้ามไปเลย
            }

            double localHappy = 50.0;
            happinessLevels[i] = Math.clamp(localHappy, 0, 100);
            sumHappiness += happinessLevels[i];
            activeBuildingCount++;

            totalPowerDemand += powerConsumptions[i];
            totalWaterDemand += waterConsumptions[i];
            totalFoodDemand += foodConsumptions[i];

            totalPowerSupply += powerProductions[i];
            totalWaterSupply += waterProductions[i];
            totalFoodSupply += foodProductions[i];

            rawPollutionTotal += pollutionTotals[i];

            double baseTax = taxRevenues[i];
            double baseMaint = maintenanceCosts[i];

            // แยกตามประเภทโซน
            switch (zoneTypes[i]) {
                case RESIDENTIAL_LOW:
                case RESIDENTIAL_HIGH:
                    rawResTax += baseTax;
                    rawResMaint += baseMaint;
                    totalMaxPop += maxResidents[i];
                    break;
                case COMMERCIAL_LOW:
                case COMMERCIAL_HIGH:
                    rawComTax += baseTax;
                    rawComMaint += baseMaint;
                    break;
                case INDUSTRIAL_LIGHT:
                case INDUSTRIAL_HEAVY:
                    rawIndTax += baseTax;
                    rawIndMaint += baseMaint;
                    break;
                case AGRICULTURAL:
                    rawAgrTax += baseTax;
                    rawAgrMaint += baseMaint;
                    break;
                case OTHER:
                case UNZONED:
                    rawOtherMaint += baseMaint;
                    break;
            }
        }

        // Data Pipeline: ส่ง Data ดิบไปให้ Manager แต่ละส่วน

        // แผนกที่ 1: ตรวจสอบน้ำ/ไฟ ว่าพอเลี้ยงเมืองไหม
        StatsManagerInfrastructure.getInstance().processTick(totalPowerDemand, totalWaterDemand, totalFoodDemand, totalPowerSupply, totalWaterSupply, totalFoodSupply);

        // แผนกที่ 2: คำนวณมลพิษ (อาจจะอิงจากไฟที่ผลิตได้)
        StatsManagerEnvironmental.getInstance().processTick(rawPollutionTotal);

        // แผนกที่ 3: คำนวณความสุข (อิงจากมลพิษ และไฟที่ดับ)
        double averageLocalHappiness = (activeBuildingCount > 0) ? (sumHappiness / activeBuildingCount) : 50.0;
        StatsManagerSocial.getInstance().processTick(averageLocalHappiness);

        // แผนกที่ 4: การเงิน (รับ Data ดิบไปคูณ % ภาษี และ % ความสุข ภายในคลาสนี้)
        StatsManagerFinancial.getInstance().processTick(
                rawResTax, rawComTax, rawIndTax, rawAgrTax,
                rawResMaint, rawComMaint, rawIndMaint, rawAgrMaint, rawOtherMaint
        );

        // แผนกที่ 5: คน (ความต้องการคนเข้าอยู่ อิงจากความสุข)
        StatsManagerPopulation.getInstance().processTick(totalMaxPop);

        // เดินหน้าเวลาไป 1 Tick
        GameManager.getInstance().advanceTick();
    }

    // --------------- For Building To know its Operational ---------------
    public boolean getIsOperational(int index) {
        if (index < 0 || index >= currentCount) return false;
        return isOperational[index];
    }

    public void setIsOperational(int index, boolean status) {
        if (index >= 0 && index < currentCount) isOperational[index] = status;
    }

    public double[] getCurrentResidentsArray() {
        return currentResidents;
    }

    // --- Getters for Managers ---
    public BaseBuilding[] getBuildingRefs() {
        return buildingRefs;
    }

    public int getCurrentCount() {
        return currentCount;
    }
}