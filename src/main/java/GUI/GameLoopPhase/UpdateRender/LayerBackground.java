package GUI.GameLoopPhase.UpdateRender;

import Config.BuildingData;
import Config.ConfigLoader;
import GUI.GUIServices.AssetManager;
import GUI.GUIServices.CameraManager;
import GUI.GUIServices.MapManager;
import GUI.GameLoopPhase.UpdateLogic.InputSensing;
import GUI.GUIServices.GUIManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class LayerBackground {

    public static void render() {
        draw(GUIManager.getInstance().getGc());
    }

    public static void draw(GraphicsContext gc) {
        double camX = CameraManager.getInstance().getX();
        double camY = CameraManager.getInstance().getY();
        int mapSize = MapManager.getInstance().getMapSize();
        double tileSize = CameraManager.getInstance().getTileSize();

        double screenWidth = gc.getCanvas().getWidth();
        double screenHeight = gc.getCanvas().getHeight();

        double halfWidth = tileSize / 2.0;
        double halfHeight = tileSize / 4.0;

        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double[] xPoints = new double[4];
        double[] yPoints = new double[4];

        // =========================================================
        // SMART CULLING ALGORITHM
        // =========================================================

        // 1. คำนวณหา "พิกัด Array (Grid X, Y)" ที่อยู่ตรงกึ่งกลางหน้าจอพอดี
        int centerGridX = (int) Math.floor(((camX) / halfWidth + camY / halfHeight) / 2.0);
        int centerGridY = (int) Math.floor((camY / halfHeight - (camX) / halfWidth) / 2.0);

        // 2. คำนวณระยะมองเห็น (View Distance) ว่าหน้าจอกว้างแค่ไหน ควรวาดเผื่อออกไปกี่ช่อง
        // หาร tileSize เพื่อดูว่าจอกว้างกี่ช่อง แล้วบวกเผื่อขอบจอไปอีกสัก 5-8 ช่องกันภาพแหว่ง
        int viewDistX = (int) (screenWidth / tileSize) + 8;
        int viewDistY = (int) (screenHeight / (tileSize / 2.0)) + 8;

        // 3. กำหนดขอบเขตการ Loop (ห้ามติดลบ และ ห้ามเกิน mapSize)
        int startX = Math.max(0, centerGridX - viewDistX);
        int endX = Math.min(mapSize, centerGridX + viewDistX);
        int startY = Math.max(0, centerGridY - viewDistY);
        int endY = Math.min(mapSize, centerGridY + viewDistY);

        // =========================================================

        // Loop เฉพาะโซนที่กล้องมองเห็นเท่านั้น!
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {

                double isoX = (x - y) * halfWidth;
                double isoY = (x + y) * halfHeight;

                // หัก Offset ให้ภาพอยู่กลางจอ
                double drawX = isoX - camX + (screenWidth / 2.0);
                double drawY = isoY - camY;

                // 1. ดึง String ID จาก MapManager
                String buildingId = MapManager.getInstance().getBuildingIdAt(x, y);

                // 2. ดึง Config จาก Registry (JSON)
                BuildingData config = ConfigLoader.getBuildingConfig(buildingId);

                // กันเหนียว: ถ้าไม่เจอข้อมูลตึก ให้ข้ามช่องนี้ไป หรือวาดเป็นพื้นหญ้าเริ่มต้น
                if (config == null) continue;

                double pad = 0.5;
                // เตรียมพิกัดข้าวหลามตัด (ขยายออกด้านนอก)
                xPoints[0] = drawX;
                xPoints[1] = drawX + halfWidth + pad;
                xPoints[2] = drawX;
                xPoints[3] = drawX - halfWidth - pad;

                yPoints[0] = drawY - pad;
                yPoints[1] = drawY + halfHeight;
                yPoints[2] = drawY + halfHeight * 2 + pad;
                yPoints[3] = drawY + halfHeight;

                // 3. วาดสีพื้นตามที่ระบุใน JSON (ดึงผ่าน getBaseColor)
                gc.setFill(config.getBaseColor());
                gc.fillPolygon(xPoints, yPoints, 4);

                // วาดเส้นขอบบางๆ
                gc.setStroke(Color.rgb(255, 255, 255, 0.15));
                gc.setLineWidth(1.0);
                gc.strokePolygon(xPoints, yPoints, 4);

                // 4. วาดรูปภาพตึกโดยดึงจาก AssetManager ผ่าน Building ID
                if (!config.getId().equals("EMPTY")) {
                    Image tileImage = AssetManager.getInstance().getImage(config.getId());

                    if (tileImage != null) {
                        double currentTileSize = CameraManager.getInstance().getTileSize();

                        // ขนาดภาพวาด (ใช้ ratio)
                        double imageRatio = currentTileSize / tileImage.getWidth();
                        double drawWidth = currentTileSize;
                        double drawHeight = tileImage.getHeight() * imageRatio;

                        // หาระดับการซูมของกล้อง (Zoom Factor)
                        double BASE_TILE_SIZE = 32.0; // ปรับเลขนี้ให้ตรงกับค่า Default ตอนเริ่มเกม
                        double zoomFactor = currentTileSize / BASE_TILE_SIZE;

                        // ขยาย Offset ตามระดับการซูมของกล้อง
                        double scaledXOffset = config.getXOffset() * zoomFactor;
                        double scaledYOffset = config.getYOffset() * zoomFactor;

                        double renderX = drawX - halfWidth + scaledXOffset;
                        double renderY = drawY + (halfHeight * 2) - drawHeight + scaledYOffset;

                        gc.drawImage(tileImage, renderX, renderY, drawWidth, drawHeight);
                    }
                }


                // =========================================================
                // ระบบวาด Preview (ภาพโปร่งใสก่อนสร้าง)
                // =========================================================
                if (x == InputSensing.getHoverGridX() && y == InputSensing.getHoverGridY()) {

                    if (InputSensing.getCurrentMode() == InputSensing.MODE_BUILD) {
                        String previewId = InputSensing.getSelectedBuildingId();
                        Image previewImg = AssetManager.getInstance().getImage(previewId);

                        if (previewImg != null) {
                            BuildingData previewConfig = ConfigLoader.getBuildingConfig(previewId);

                            double currentTileSize = CameraManager.getInstance().getTileSize();
                            double imageRatio = currentTileSize / previewImg.getWidth();
                            double pDrawWidth = currentTileSize;
                            double pDrawHeight = previewImg.getHeight() * imageRatio;

                            double BASE_TILE_SIZE = 32.0; // ใช้ค่าเดียวกับที่คุณตั้งไว้
                            double zoomFactor = currentTileSize / BASE_TILE_SIZE;
                            double pXOffset = previewConfig.getXOffset() * zoomFactor;
                            double pYOffset = previewConfig.getYOffset() * zoomFactor;

                            double pRenderX = drawX - halfWidth + pXOffset;
                            double pRenderY = drawY + (halfHeight * 2) - pDrawHeight + pYOffset;

                            // วาดตึกแบบจางๆ (โปร่งใส 50%)
                            gc.setGlobalAlpha(0.5);
                            gc.drawImage(previewImg, pRenderX, pRenderY, pDrawWidth, pDrawHeight);
                            gc.setGlobalAlpha(1.0);
                        }
                    }

                    else if (InputSensing.getCurrentMode() == InputSensing.MODE_DEMOLISH) {
                        // วาดไฮไลท์สีแดงที่พื้น ตอนเล็งทุบตึก
                        gc.setGlobalAlpha(0.4);
                        gc.setFill(Color.TOMATO);
                        gc.fillPolygon(xPoints, yPoints, 4);
                        gc.setGlobalAlpha(1.0);
                    }
                }
            }
        }
    }
}