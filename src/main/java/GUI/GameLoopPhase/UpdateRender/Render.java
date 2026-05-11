package GUI.GameLoopPhase.UpdateRender;

import Config.BuildingData;
import Config.ConfigLoader;
import GUI.GUIServices.AssetManager;
import GUI.GUIServices.CameraManager;
import Logic.Core.GameMapManager;
import GUI.GUIServices.TerrainMapManager;
import GUI.GameLoopPhase.UpdateLogic.InputSensing;
import GUI.GUIServices.GUIManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


/**
 * Engine ประมวลผลการแสดงผลหลัก (Main Render Engine - 2.5D Isometric)
 * <p>Prototype Note: ปัจจุบันใช้โครงสร้างการวาดแบบลูปเดียวรวด (Single-pass Bottom-up) ในอนาคตสามารถยกระดับเป็นระบบ Z-Sorting หรือ Layered Rendering เต็มรูปแบบ เพื่อรองรับตึกที่มีความสูงซ้อนทับกันหลายชั้นได้</p>
 * * <p><b>Architecture and Algorithms:</b></p>
 * <ul>
 * <li>Isometric Projection: แปลงพิกัดตาราง 2D (Array x,y) ให้เป็นมุมมองเฉียง 2.5D ด้วยสมการทางคณิตศาสตร์ isoX = (x-y) และ isoY = (x+y) ทำให้ได้มุมมองเมืองแบบสมจริงมากขึ้น</li>
 * <li>Smart Culling (Viewport Optimization): ระบบจะคำนวณจากพิกัดกล้อง (CameraManager) เพื่อหากรอบสี่เหลี่ยม (Bounding Box) ที่มองเห็นได้จริงบนจอ และสั่ง Render เฉพาะช่องนั้น (startX ถึง endX) ช่วยลดภาระ CPU/GPU</li>
 * <li>Dynamic Scaling (Zoom System): รองรับการซูมแบบ Real-time ด้วยการคำนวณอัตราส่วน zoomFactor จาก tileSize เพื่อปรับสเกลขนาดรูปภาพ และชดเชยพิกัด Offset ของตึกให้ยืดหดสัมพันธ์กับตาราง Grid</li>
 * </ul>
 * * <p><b>Rendering Pipeline:</b></p>
 * <ul>
 * <li>Step 1: Base Terrain (พื้นดิน/น้ำ):
 * ดึงรหัสพื้นที่จาก TerrainMapManager และดึงรูปภาพจาก AssetManager เพื่อวาดเป็นฐานชั้นล่างสุด (หาก InputSensing.MODE_BUILD จะมีการวาดเส้น Grid ลางๆ ซ้อนทับให้ด้วย)</li>
 * <li>Step 2: World Objects (สิ่งปลูกสร้าง):
 * ดึง ID ตึกจาก GameMapManager ทาบกับ ConfigLoader เพื่อคำนวณการยืดหด (Zoom Factor) และพิกัดออฟเซ็ต (X/Y Offset) ก่อนจะวาดรูปตึกซ้อนทับลงบนพื้นดิน</li>
 * <li>Step 3: Interactive Overlay (ระบบพรีวิว/ทุบตึก):
 * ดึงพิกัดเมาส์และโหมดปัจจุบันจาก InputSensing หากเป็น MODE_BUILD จะวาดภาพตึกแบบโปร่งแสง (Alpha 0.5) แต่หากเป็น MODE_DEMOLISH จะวาดกรอบสีแดงซ้อนทับบนสุดเพื่อเป็น Visual Feedback ให้ผู้เล่น</li>
 * </ul>
 */
public class Render {

    public static void render() {
        draw(GUIManager.getInstance().getGc());
    }

    public static void draw(GraphicsContext gc) {
        double camX = CameraManager.getInstance().getX();
        double camY = CameraManager.getInstance().getY();
        int mapSize = GameMapManager.getInstance().getMapSize();
        double tileSize = CameraManager.getInstance().getTileSize();

        double screenWidth = gc.getCanvas().getWidth();
        double screenHeight = gc.getCanvas().getHeight();

        double halfWidth = tileSize / 2.0;
        double halfHeight = tileSize / 4.0;

        // วาดพื้นหลังสีดำ
        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double[] xPoints = new double[4];
        double[] yPoints = new double[4];

        Image waterTile = AssetManager.getInstance().getImage("water_tile");
        Image grassTile = AssetManager.getInstance().getImage("grass_tile");

        // SMART CULLING
        // ---------------------------------------------------------

        int centerGridX = (int) Math.floor(((camX) / halfWidth + camY / halfHeight) / 2.0);
        int centerGridY = (int) Math.floor((camY / halfHeight - (camX) / halfWidth) / 2.0);

        int viewDistX = (int) (screenWidth / tileSize) + 8;
        int viewDistY = (int) (screenHeight / (tileSize / 2.0)) + 8;

        int startX = Math.max(0, centerGridX - viewDistX);
        int endX = Math.min(mapSize, centerGridX + viewDistX);
        int startY = Math.max(0, centerGridY - viewDistY);
        int endY = Math.min(mapSize, centerGridY + viewDistY);

        // ---------------------------------------------------------

        // RENDER
        // ---------------------------------------------------------
        boolean isEditMode = (InputSensing.getCurrentMode() == InputSensing.MODE_BUILD
                || InputSensing.getCurrentMode() == InputSensing.MODE_DEMOLISH);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                // ISOMETRIC
                double isoX = (x - y) * halfWidth;
                double isoY = (x + y) * halfHeight;

                double drawX = isoX - camX + (screenWidth / 2.0);
                double drawY = isoY - camY;

                double pad = 0.5;
                xPoints[0] = drawX;
                xPoints[1] = drawX + halfWidth + pad;
                xPoints[2] = drawX;
                xPoints[3] = drawX - halfWidth - pad;

                yPoints[0] = drawY - pad;
                yPoints[1] = drawY + halfHeight;
                yPoints[2] = drawY + halfHeight * 2 + pad;
                yPoints[3] = drawY + halfHeight;


                // Step 1: Base Terrain (พื้นดิน/น้ำ)
                int terrainType = TerrainMapManager.getInstance().getTerrainAt(x, y);

                if (terrainType == TerrainMapManager.TERRAIN_WATER && waterTile != null) {
                    // วาดน้ำ
                    gc.drawImage(waterTile, drawX - halfWidth, drawY, tileSize, tileSize);
                }
                else if (terrainType == TerrainMapManager.TERRAIN_GRASS && grassTile != null) {
                    // วาดหญ้า
                    gc.drawImage(grassTile, drawX - halfWidth, drawY, tileSize, tileSize);
                }
                else {
                    // Fallback กรณีรูปไม่มา ให้วาดสีแทน
                    gc.setFill(Color.web("#4C8A3C")); // สีเขียว
                    gc.fillPolygon(xPoints, yPoints, 4);
                }

                if (isEditMode) {
                    // วาดเส้นขอบบางๆ
                    gc.setStroke(Color.rgb(255, 255, 255, 0.15));
                    gc.setLineWidth(1.0);
                    gc.strokePolygon(xPoints, yPoints, 4);
                }


                // Step 2: World Objects (สิ่งปลูกสร้าง)
                String buildingId = GameMapManager.getInstance().getBuildingIdAt(x, y);
                BuildingData config = ConfigLoader.getBuildingConfig(buildingId);

                if (config != null && !config.getId().equals("EMPTY")) {
                    Image tileImage = AssetManager.getInstance().getImage(config.getId());

                    if (tileImage != null) {
                        double imageRatio = tileSize / tileImage.getWidth();
                        double drawWidth = tileSize;
                        double drawHeight = tileImage.getHeight() * imageRatio;

                        double BASE_TILE_SIZE = 32.0;
                        double zoomFactor = tileSize / BASE_TILE_SIZE;

                        double scaledXOffset = config.getXOffset() * zoomFactor;
                        double scaledYOffset = config.getYOffset() * zoomFactor;

                        double renderX = drawX - halfWidth + scaledXOffset;
                        double renderY = drawY + (halfHeight * 2) - drawHeight + scaledYOffset;

                        gc.drawImage(tileImage, renderX, renderY, drawWidth, drawHeight);
                    }
                }

                // Step 3: Interactive Overlay (ระบบพรีวิว/ทุบตึก)
                if (x == InputSensing.getHoverGridX() && y == InputSensing.getHoverGridY()) {
                    if (InputSensing.getCurrentMode() == InputSensing.MODE_BUILD) {
                        String previewId = InputSensing.getSelectedBuildingId();
                        Image previewImg = AssetManager.getInstance().getImage(previewId);

                        if (previewImg != null) {
                            BuildingData previewConfig = ConfigLoader.getBuildingConfig(previewId);
                            double imageRatio = tileSize / previewImg.getWidth();
                            double pDrawWidth = tileSize;
                            double pDrawHeight = previewImg.getHeight() * imageRatio;

                            double BASE_TILE_SIZE = 32.0;
                            double zoomFactor = tileSize / BASE_TILE_SIZE;
                            double pXOffset = previewConfig.getXOffset() * zoomFactor;
                            double pYOffset = previewConfig.getYOffset() * zoomFactor;

                            double pRenderX = drawX - halfWidth + pXOffset;
                            double pRenderY = drawY + (halfHeight * 2) - pDrawHeight + pYOffset;

                            gc.setGlobalAlpha(0.5);
                            gc.drawImage(previewImg, pRenderX, pRenderY, pDrawWidth, pDrawHeight);
                            gc.setGlobalAlpha(1.0);
                        }
                    } else if (InputSensing.getCurrentMode() == InputSensing.MODE_DEMOLISH) {
                        gc.setGlobalAlpha(0.4);
                        gc.setFill(Color.TOMATO);
                        gc.fillPolygon(xPoints, yPoints, 4);
                        gc.setGlobalAlpha(1.0);
                    }
                }
            }
        }
        // ---------------------------------------------------------
    }
}