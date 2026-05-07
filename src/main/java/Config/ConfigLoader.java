package Config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

public class ConfigLoader {
    private static Map<String, BuildingData> registry;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        try {
            // อ่านไฟล์จากโฟลเดอร์ resources
            InputStream is = ConfigLoader.class.getResourceAsStream("/Building_Config.json");
            if (is == null) {
                throw new Exception("Cannot find Building_Config.json in resources!");
            }

            InputStreamReader reader = new InputStreamReader(is);
            Gson gson = new Gson();

            // บอก Gson ว่าเราต้องการแปลง JSON เป็น Map<String, BuildingData>
            Type type = new TypeToken<Map<String, BuildingData>>(){}.getType();
            registry = gson.fromJson(reader, type);

            System.out.println("[ConfigLoader] Successfully loaded " + registry.size() + " buildings from JSON.");
        } catch (Exception e) {
            System.out.println("[ConfigLoader] Error loading JSON config!");
        }
    }

    public static BuildingData getBuildingConfig(String id) {
        if (registry == null || !registry.containsKey(id)) {
            System.out.println("[ConfigLoader] Error: Building ID '" + id + "' not found!");
            return null;
        }
        return registry.get(id);
    }

    public static Map<String, BuildingData> getAllConfigs() {
        return registry;
    }
}
