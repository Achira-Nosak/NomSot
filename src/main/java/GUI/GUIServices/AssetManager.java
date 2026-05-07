package GUI.GUIServices;

import Config.BuildingData;
import Config.ConfigLoader;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static AssetManager instance;
    private Map<String, Image> imageCache; // เปลี่ยน Key เป็น String (Building ID)
    private final String BUILDING_PATH = "/Assets/Building/";

    private AssetManager() {
        imageCache = new HashMap<>();
    }

    public static AssetManager getInstance() {
        if (instance == null) instance = new AssetManager();
        return instance;
    }

    public void loadAllAssets() {
        Map<String, BuildingData> allConfigs = ConfigLoader.getAllConfigs();

        for (BuildingData data : allConfigs.values()) {
            if (data.getAssetFileName() == null || data.getAssetFileName().isEmpty()) continue;

            String fullPath = BUILDING_PATH + data.getAssetFileName();
            try {
                // โหลดภาพขนาดจริง
                Image img = new Image(getClass().getResourceAsStream(fullPath));

                imageCache.put(data.getId(), img);
                System.out.println("✅ Loaded Asset for: " + data.getId());
            } catch (Exception e) {
                System.err.println("❌ Failed to load: " + fullPath);
            }
        }
    }

    public Image getImage(String buildingId) {
        return imageCache.get(buildingId);
    }
}