package Logic.Stats;

public class StatsManagerFinancial {
    private static StatsManagerFinancial instance;

    private StatsManagerFinancial() {}

    public static StatsManagerFinancial getInstance() {
        if (instance == null) {
            instance = new StatsManagerFinancial();
        }
        return instance;
    }

    public void processTick(
            double rawResTax, double rawComTax, double rawIndTax, double rawAgrTax,
            double rawResMaint, double rawComMaint, double rawIndMaint, double rawAgrMaint, double rawOtherMaint) {

        CityMasterStats master = CityMasterStats.getInstance();

        // 1. ดึงนโยบายภาษีจาก MasterStats (ที่ผู้เล่นปรับใน UI)
        double resRate = master.finance.getTaxRateResidential();
        double comRate = master.finance.getTaxRateCommercial();
        double indRate = master.finance.getTaxRateIndustrial();
        double agrRate = master.finance.getTaxRateAgricultural();

        // 2. คำนวณ Happiness Multiplier (0.2x ที่ความสุข 0 ถึง 1.2x ที่ความสุข 100)
        double happiness = master.social.getHappiness();
        double happyMult = 0.2 + (happiness / 100.0) * 1.0;

        // 3. คำนวณรายได้สุทธิแยกตามหมวดหมู่
        double finalResTax = rawResTax * resRate * happyMult;
        double finalComTax = rawComTax * comRate * happyMult;
        double finalIndTax = rawIndTax * indRate * happyMult;
        double finalAgrTax = rawAgrTax * agrRate * happyMult;

        double totalRevenue = finalResTax + finalComTax + finalIndTax + finalAgrTax;

        // 4. คำนวณค่าบำรุงรักษารวม
        double totalMaintenance = rawResMaint + rawComMaint + rawIndMaint + rawAgrMaint + rawOtherMaint;

        // 5. คำนวณ Net Income (รายได้สุทธิต่อ Tick)
        double netIncome = totalRevenue - totalMaintenance;
        master.finance.setNetIncomeCurrent(netIncome);

        // 6. อัปเดตเงินในคลัง (Treasury)
        double currentTreasury = master.finance.getTreasuryCurrent();
        master.finance.setTreasuryCurrent(currentTreasury + netIncome);

        // 7. อัปเดตสถิติรายก้อนเพื่อให้ UI เอาไปแยกแสดงผล
        master.finance.setTaxRevenueBase(totalRevenue);
        master.finance.setMaintenanceCostBase(totalMaintenance);
    }
}