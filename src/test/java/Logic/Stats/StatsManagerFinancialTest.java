package Logic.Stats;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Logic.Stats.StatsManagerFinancial;
import Logic.Stats.CityMasterStats;

class StatsManagerFinancialTest {

    private CityMasterStats master;

    // เคลียร์ค่าเงินในคลัง ความสุข และภาษี ก่อนเริ่มเทสทุกครั้ง
    @BeforeEach
    void setUp() {
        master = CityMasterStats.getInstance();
        master.finance.setTreasuryCurrent(1000.0); // ทุนตั้งต้น
        master.social.setHappiness(100.0);         // ความสุขเต็ม 100 (Multiplier = 1.2)
        master.finance.setTaxRateResidential(1.0); // ภาษีปกติ (1.0x)
        master.finance.setTaxRateCommercial(1.0);
        master.finance.setTaxRateIndustrial(1.0);
        master.finance.setTaxRateAgricultural(1.0);
    }

    // เช็คว่า Singleton สามารถเรียกใช้งานได้
    @Test
    void testSingletonInstance() {
        StatsManagerFinancial manager1 = StatsManagerFinancial.getInstance();
        StatsManagerFinancial manager2 = StatsManagerFinancial.getInstance();
        assertNotNull(manager1);
        assertEquals(manager1, manager2);
    }

    // เช็คการคำนวณ Net Income และอัปเดตเงินคลังในกรณีที่รายได้มากกว่ารายจ่าย
    @Test
    void testProcessTickPositiveIncome() {
        StatsManagerFinancial manager = StatsManagerFinancial.getInstance();

        // รายได้ 100, รายจ่าย 50
        // สูตร: (100 * 1.0 เรทภาษี * 1.2 บัฟความสุข) = 120
        // Net Income = 120 - 50 = 70
        manager.processTick(100, 0, 0, 0, 50, 0, 0, 0, 0);

        assertEquals(70.0, master.finance.getNetIncomeCurrent());
        assertEquals(1070.0, master.finance.getTreasuryCurrent()); // 1000 + 70
    }

    // เช็คการคำนวณกรณีเมืองขาดทุน (รายจ่ายมากกว่ารายได้) เงินคลังต้องลดลง
    @Test
    void testProcessTickNegativeIncome() {
        StatsManagerFinancial manager = StatsManagerFinancial.getInstance();

        // รายได้ 0, รายจ่าย 200
        // Net Income = 0 - 200 = -200
        manager.processTick(0, 0, 0, 0, 200, 0, 0, 0, 0);

        assertEquals(-200.0, master.finance.getNetIncomeCurrent());
        assertEquals(800.0, master.finance.getTreasuryCurrent()); // 1000 - 200
    }

    // เช็คสูตรตัวคูณความสุข (Happiness Multiplier) ว่าถ้าความสุข 0 รายได้ภาษีต้องหดลงเหลือแค่ 20%
    @Test
    void testProcessTickLowHappinessPenalty() {
        StatsManagerFinancial manager = StatsManagerFinancial.getInstance();

        // เซ็ตความสุขเป็น 0 (Multiplier = 0.2 + (0/100)*1.0 = 0.2)
        master.social.setHappiness(0.0);

        // เก็บภาษีมา 100 แต่ความสุข 0 เลยได้เงินจริงแค่ 100 * 0.2 = 20
        manager.processTick(100, 0, 0, 0, 0, 0, 0, 0, 0);

        assertEquals(20.0, master.finance.getNetIncomeCurrent());
        assertEquals(1020.0, master.finance.getTreasuryCurrent()); // 1000 + 20
    }
}
