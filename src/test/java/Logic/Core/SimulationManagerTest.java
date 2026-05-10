package Logic.Core;

import static org.junit.jupiter.api.Assertions.*;

import Config.ConfigLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Logic.Core.SimulationManager;
import Model.ZonedBuilding;
import Config.Enums.ZoneType;

class SimulationManagerTest {

    // เทสว่าคลาสสามารถดึง Singleton Instance มาใช้งานได้ตามปกติ
    @Test
    void testSingletonInstance() {
        SimulationManager sim1 = SimulationManager.getInstance();
        SimulationManager sim2 = SimulationManager.getInstance();
        assertNotNull(sim1);
        assertEquals(sim1, sim2);
    }

     @BeforeEach
     void setup() { ConfigLoader.loadConfig(); }

    // เช็คว่าลงทะเบียนตึกแล้ว Count เพิ่มขึ้น
    @Test
    void testRegisterBuilding() {
        SimulationManager sim = SimulationManager.getInstance();
        int initialCount = sim.getCurrentCount();

        sim.registerBuilding(new ZonedBuilding("ResidentialLow", 1, 1, ZoneType.RESIDENTIAL_LOW));
        assertEquals(initialCount + 1, sim.getCurrentCount());
    }

    // เช็คระบบขยาย Array DOD แบบอัตโนมัติ (ใส่เกิน 100 หลังต้องไม่ Crash)
    @Test
    void testEnsureCapacity() {
        SimulationManager sim = SimulationManager.getInstance();
        int initialCount = sim.getCurrentCount();

        for (int i = 0; i < 150; i++) {
            sim.registerBuilding(new ZonedBuilding("CommercialLow", i, 2, ZoneType.COMMERCIAL_LOW));
        }
        assertEquals(initialCount + 150, sim.getCurrentCount());
    }

    // เช็คระบบลบทิ้งแบบ Swap (เอาตึกท้ายสุดมาเสียบแทนช่องที่โบ๋)
    @Test
    void testRemoveBuildingAndSwap() {
        SimulationManager sim = SimulationManager.getInstance();

        ZonedBuilding b1 = new ZonedBuilding("ResidentialLow", 10, 10, ZoneType.RESIDENTIAL_LOW);
        ZonedBuilding b2 = new ZonedBuilding("CommercialLow", 11, 11, ZoneType.COMMERCIAL_LOW);

        sim.registerBuilding(b1);
        sim.registerBuilding(b2);

        int countBefore = sim.getCurrentCount();
        int targetIndex = b1.getDataIndex();

        sim.removeBuilding(targetIndex);

        assertEquals(countBefore - 1, sim.getCurrentCount());
        assertEquals(targetIndex, b2.getDataIndex()); // ตึก b2 ต้องย้ายมา Index ของ b1
    }
}
