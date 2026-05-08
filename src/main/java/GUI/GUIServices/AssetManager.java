package GUI.GUIServices;

import Config.BuildingData;
import Config.ConfigLoader;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static AssetManager instance;
    private Map<String, Image> imageCache;

    // แยก Path ให้ชัดเจน
    private final String BUILDING_PATH = "/Assets/Building/";
    private final String TILE_PATH = "/Assets/Tile/";

    private AssetManager() {
        imageCache = new HashMap<>();
    }

    public static AssetManager getInstance() {
        if (instance == null) instance = new AssetManager();
        return instance;
    }

    public void loadAllAssets() {
        // ==========================================
        // 1. โหลด Asset พื้นฐาน (Terrain Tiles) ที่ไม่ได้อยู่ใน JSON
        // ==========================================
        try {
            // โหลดรูปน้ำ (เก็บด้วยคีย์ "water_tile")
            Image waterImg = new Image(getClass().getResourceAsStream(TILE_PATH + "water_tile.png"));
            imageCache.put("water_tile", waterImg);
            System.out.println("Loaded Asset: water_tile");

            // โหลดรูปหญ้า (เก็บด้วยคีย์ "grass_tile")
            Image grassImg = new Image(getClass().getResourceAsStream(TILE_PATH + "grass_tile.png"));
            imageCache.put("grass_tile", grassImg);
            System.out.println("Loaded Asset: grass_tile");

        } catch (Exception e) {
            System.err.println("Failed to load Terrain Tiles (water/grass)");
        }

        // ==========================================
        // 2. โหลด Asset ของตึกทั้งหมดจาก JSON (โค้ดเดิมของคุณ)
        // ==========================================
        Map<String, BuildingData> allConfigs = ConfigLoader.getAllConfigs();

        for (BuildingData data : allConfigs.values()) {
            if (data.getAssetFileName() == null || data.getAssetFileName().isEmpty()) continue;

            String fullPath = BUILDING_PATH + data.getAssetFileName();
            try {
                Image img = new Image(getClass().getResourceAsStream(fullPath));
                imageCache.put(data.getId(), img);
                System.out.println("Loaded Asset for: " + data.getId());
            } catch (Exception e) {
                System.err.println("Failed to load: " + fullPath);
            }
        }
    }

    public Image getImage(String buildingId) {
        return imageCache.get(buildingId);
    }
}