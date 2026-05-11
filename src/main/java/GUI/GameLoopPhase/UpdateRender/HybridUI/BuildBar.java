package GUI.GameLoopPhase.UpdateRender.HybridUI;

import Config.BuildingData;
import Config.ConfigLoader;
import GUI.GUIServices.AssetManager;
import GUI.GameLoopPhase.UpdateLogic.InputSensing;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component UI แถบเครื่องมือสร้างสิ่งปลูกสร้าง (Dynamic Build Bar Overlay)
 * <p>Prototype Note: ปัจจุบันจัดกลุ่มตึกประเภท Social, Infrastructure, Zone, Wellness ฯลฯ และยังใช้สัญลักษณ์ Emoji เป็นไอคอนชั่วคราวสำหรับเมนูหมวดหมู่หลัก</p>
 * <ul>
 * <li>Hierarchical Layout: โครงสร้าง UI แบบ 2 Layer จัดวางตำแหน่งชิดขอบล่างจอ (BOTTOM_CENTER)
 * <ul>
 * <li>Layer 1 VBox Root (BOTTOM_CENTER ของจอ): เป็นฐานหลักสำหรับจัดเรียงแถบเครื่องมือ</li>
 * <li>Layer 2 HBox MainMenuBar (BOTTOM ของ Root):  แถบเมนูหมวดหมู่หลัก แสดงผลตลอดเวลา (Always on) ตกแต่งด้วย CSS Glass Effect (พื้นหลังโปร่งแสง, ขอบมน, DropShadow)</li>
 * <li>Layer 2 HBox SubBuildingBar (TOP ของ Root):  แถบเลือกตึกย่อย จะแสดงผล (Visible=true) ก็ต่อเมื่อผู้เล่นกดเลือกหมวดหมู่จาก MainBar ช่วยประหยัดพื้นที่แสดงผลแผนที่</li>
 * </ul>
 * </li>
 * <li>Data-Driven UI: ออกแบบให้สร้างปุ่มตึกอัตโนมัติ โดยระบบจะกวาดข้อมูลจาก ConfigLoader และจัดกลุ่มตึกตามค่า "BuildBarTag" ในไฟล์ JSON</li>
 * <li>Future Scalability: รองรับการขยายตัวของเกม 100% หากต้องการเพิ่มหรือลบตึกในอนาคต เพียงแค่แก้ไขไฟล์ JSON ระบบจะดึงภาพจาก AssetManager มาสร้างปุ่มให้เองโดยไม่ต้องแก้โค้ด UI เพิ่มเติม</li>
 * <li>UX Fallback System: มีระบบ Tooltip บอกชื่อตึกและราคาเมื่อนำเมาส์ชี้ และมีลอจิก Fallback ป้องกันโปรแกรม crash หากดึงรูปตึกไม่สำเร็จ ระบบจะใช้อักษรตัวแรกของชื่อตึกมาทำเป็นปุ่มแทน</li>
 * </ul>
 */
public class BuildBar {

    private static String activeCategory = "";

    /**
     * เรียกใช้ใน GUIManager
     */
    public static VBox create() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.BOTTOM_CENTER);
        root.setPickOnBounds(false);

        String glassStyle =
                "-fx-background-color: rgba(40, 40, 40, 0.75);" +
                        "-fx-background-radius: 25;" +
                        "-fx-border-radius: 25;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;";

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10); shadow.setOffsetY(5); shadow.setColor(Color.rgb(0, 0, 0, 0.5));

        // Sub-Bar (แถบด้านบน)
        HBox subBar = new HBox(15);
        subBar.setAlignment(Pos.CENTER);
        subBar.setPadding(new Insets(10, 20, 10, 20));
        subBar.setStyle(glassStyle);
        subBar.setEffect(shadow);
        subBar.setMaxWidth(Region.USE_PREF_SIZE);

        subBar.setVisible(false);
        subBar.setManaged(false);

        // Main-Bar (แถบหลัก)
        HBox mainBar = new HBox(15);
        mainBar.setAlignment(Pos.CENTER);
        mainBar.setPadding(new Insets(10, 20, 10, 20));
        mainBar.setStyle(glassStyle);
        mainBar.setEffect(shadow);
        mainBar.setMaxWidth(Region.USE_PREF_SIZE);

        Map<String, List<BuildingData>> categorizedBuildings = new HashMap<>();

        // แยกประเภทตึกตาม JSON BuildBarTag
        for (BuildingData data : ConfigLoader.getAllConfigs().values()) {
            if (data.getId().equals("EMPTY")) continue;

            String tag = data.getBuildBarTag();
            if (tag == null || tag.isEmpty()) tag = "Misc";

            categorizedBuildings.putIfAbsent(tag, new ArrayList<>());
            categorizedBuildings.get(tag).add(data);
        }

        // สร้างปุ่มหมวดหมู่หลัก (Prototype: ใช้ Emoji)
        for (String category : categorizedBuildings.keySet()) {
            String icon = getCategoryEmoji(category);

            Button btnCategory = createTextButton(icon, e -> {
                // setAction ถ้า click ปุ่มเมนูหลักอันนี้
                if (activeCategory.equals(category) && subBar.isVisible()) {
                    // ถ้ากดซ้ำ ปิด bar
                    subBar.setVisible(false);
                    subBar.setManaged(false);
                    activeCategory = "";
                } else {
                    // ถ้ากดครั้งแรก สร้าง sub Bar ด้านบน
                    subBar.getChildren().clear();
                    List<BuildingData> buildings = categorizedBuildings.get(category);

                    // สร้างปุ่มของตึกแต่ละตึกในประเภทนั้นๆ
                    for (BuildingData bData : buildings) {
                        // 1. ดึงรูปภาพจาก AssetManager
                        Image buildingImage = AssetManager.getInstance().getImage(bData.getId());

                        // 2. ส่งรูปไปสร้างเป็นปุ่ม (ถ้าไม่มีรูป จะใช้อักษรตัวแรกแทน)
                        String fallbackText = bData.getName().substring(0, 1);
                        Button btnBuild = createImageButton(buildingImage, fallbackText, ev -> InputSensing.setBuildMode(bData.getId()));

                        Tooltip t = new Tooltip(bData.getName() + "\nCost: $" + bData.getConstructionCost());
                        t.setStyle("-fx-font-size: 14px;");
                        btnBuild.setTooltip(t);

                        subBar.getChildren().add(btnBuild);
                    }

                    subBar.setVisible(true);
                    subBar.setManaged(true);
                    activeCategory = category;
                }
            });

            Tooltip catTip = new Tooltip(category);
            catTip.setStyle("-fx-font-size: 14px;");
            btnCategory.setTooltip(catTip);

            mainBar.getChildren().add(btnCategory);
        }

        // ปุ่มทุบตึก
        Button btnDemolish = createTextButton("💣", e -> InputSensing.setDemolishMode());
        Tooltip demolishTip = new Tooltip("Demolish");
        demolishTip.setStyle("-fx-font-size: 14px;");
        btnDemolish.setTooltip(demolishTip);
        mainBar.getChildren().add(btnDemolish);

        root.getChildren().addAll(subBar, mainBar);

        return root;
    }



    /**
     * Helper Method ของ create() สร้างปุ่มแบบมีรูปภาพ (สำหรับตึก)
     */
    private static Button createImageButton(Image img, String fallbackText, EventHandler<ActionEvent> action) {
        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        if (img != null) {
            // ถ้ารูปมีอยู่จริง ให้ยัดใส่ ImageView
            ImageView view = new ImageView(img);
            // บังคับให้รูปย่อลงมาเหลือ 40x40 พิกเซล โดยรักษาสัดส่วน (ไม่เบี้ยว)
            view.setFitWidth(40);
            view.setFitHeight(40);
            view.setPreserveRatio(true);

            btn.setGraphic(view); // แปะรูปลงปุ่ม
        } else {
            // กันเหนียว ถ้าตึกไหนลืมใส่รูป ให้กลับไปโชว์ตัวหนังสือ
            btn.setText(fallbackText);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px; -fx-cursor: hand;");
        }

        // บังคับขนาดปุ่มให้เท่ากันหมด
        btn.setMinSize(50, 50);
        btn.setMaxSize(50, 50);
        btn.setOnAction(action);
        return btn;
    }

    /**
     * Helper Method ของ create() สร้างปุ่มแบบตัวอักษรธรรมดา (สำหรับหมวดหมู่หลัก)
     */
    private static Button createTextButton(String text, EventHandler<ActionEvent> action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 20px; -fx-cursor: hand;");
        btn.setMinSize(50, 50);
        btn.setMaxSize(50, 50);
        btn.setOnAction(action);
        return btn;
    }

    /**
     * Helper Method ของ create() Prototype Note:จัดเก็บ iconEmoji ของแต่ละประเภท
     */
    private static String getCategoryEmoji(String category) {
        switch (category) {
            case "Social": return "🎭";
            case "Infrastructure": return "⚙️";
            case "Government": return "🏛️";
            case "Transportation": return "🚌";
            case "Wellness": return "🏥";
            case "Zone": return "🏘️";
            default: return "📦";
        }
    }
}