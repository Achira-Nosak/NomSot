package Model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Model.ServiceBuilding;
import Logic.Stats.CityMasterStats;

class ServiceBuildingTest {

    // (หมายเหตุ: ใช้ ID "CommercialLow" ชั่วคราวเพื่อให้มี Data จาก JSON มารองรับ ไม่ให้เกิด Null)
    private final String MOCK_ID = "CommercialLow";

    @BeforeEach
    void setUp() {
        // เคลียร์เงินคลังให้เป็น 0 ก่อนเริ่มเทสทุกครั้ง
        CityMasterStats.getInstance().finance.setTreasuryCurrent(0);
    }

    // เช็คว่าถ้าเงินคลังติดลบ (ถังแตก) รัศมีออร่าและค่าบำรุงรักษาต้องลดลงเหลือ 50%
    @Test
    void testNegativeBudgetPenaltyOnTick() {
        CityMasterStats.getInstance().finance.setTreasuryCurrent(-5000);
        ServiceBuilding service = new ServiceBuilding(MOCK_ID, 5, 5);
        service.setDataIndex(0);

        double baseRadius = service.getStats().getServiceRadius();

        // จำลองเวลาให้ครบ 60 Tick เพื่อกระตุ้นลอจิกเช็คการเงิน
        service.onTick(60);

        // รัศมีออร่าต้องหดลงเหลือครึ่งหนึ่ง
        assertEquals(baseRadius * 0.5, service.getCurrentAuraRadius());
    }

    // เช็คว่าถ้าเมืองรวยมาก (เงิน >= 500,000) ตึกบริการจะได้งบอัดฉีด ทำให้รัศมีกว้างขึ้นตาม Step
    @Test
    void testHighBudgetBonusOnTick() {
        // เซ็ตเงิน 600,000 -> ตามลอจิกคือจะได้โบนัส Step 2
        CityMasterStats.getInstance().finance.setTreasuryCurrent(600000);
        ServiceBuilding service = new ServiceBuilding(MOCK_ID, 5, 5);
        service.setDataIndex(0);

        double baseRadius = service.getStats().getServiceRadius();

        // จำลองเวลาให้ครบ 60 Tick
        service.onTick(60);

        // รัศมีออร่าต้องกว้างขึ้น 2 บล็อก (Base + 2)
        assertEquals(baseRadius + 2.0, service.getCurrentAuraRadius());
    }

    // เช็คการทำงานของ Interface ว่าคลาสนี้มีพฤติกรรม IAuraProvider จริงๆ
    @Test
    void testIsAuraProviderInterface() {
        ServiceBuilding service = new ServiceBuilding(MOCK_ID, 0, 0);

        // เช็คหลัก Polymorphism
        assertTrue(service instanceof Model.IAuraProvider);
        assertTrue(service instanceof Model.IUpgradable);
    }
}
