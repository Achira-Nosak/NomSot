import Config.BuildingData;
import Config.ConfigLoader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class testLoadBuildingConfig {


    @Test
    void testLoadCoalPowerPlant() {
        ConfigLoader.loadConfig();

        BuildingData stats = ConfigLoader.getBuildingConfig("CoalPowerPlant");


        assertNotNull(stats);
        assertEquals("Coal Power Plant", stats.getName());
        System.out.println(stats.getConstructionCost());
        System.out.println(stats.getId());
        System.out.println(stats.getBaseTaxRevenue());

    }
}
