package GUI.GameLoopPhase.UpdateRender.HybridUI;

import Logic.Core.GameManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


/**
 * Component UI สำหรับแสดงผลวันและเวลาภายในเกม (Date and Time Overlay)
 * <ul>
 * <li>UI Styling: ตกแต่งหน้าตาและสีสันด้วย JavaFX CSS เพื่อทำพื้นหลังแบบโปร่งใส ขอบโค้งมน และเพิ่มเอฟเฟกต์ DropShadow</li>
 * <li>Dynamic Layout: จัดการขนาดกล่องให้หดพอดีกับตัวอักษร (USE_PREF_SIZE) และตั้งค่า PickOnBounds(false) เพื่อป้องกันไม่ให้พื้นที่ว่างของกล่องไปบล็อกการคลิกเมาส์ของผู้เล่นบนแผนที่เกม</li>
 * <li>Update: ออกแบบให้ทำงานสอดคล้องกับ Game Loop โดยมีเมธอด update() สำหรับดึงค่าล่าสุดจาก GameManager มาอัปเดตแบบ Real-time</li>
 * </ul>
 */
public class DateTimeBar {

    private static Label lblTime;

    /**
     * เรียกใช้ใน GUIManager
     */
    public static HBox create() {
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);

        root.setPadding(new Insets(8, 15, 8, 15));
        root.setPickOnBounds(false);

        // 1. สั่งให้กล่องหดพอดีตัวอักษร ไม่ยืดเต็มช่อง!
        root.setMaxWidth(Region.USE_PREF_SIZE);
        root.setMaxHeight(Region.USE_PREF_SIZE);

        root.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.75);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetY(5);
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        root.setEffect(shadow);

        lblTime = new Label("📅 Month: 1 | Day: 1");
        lblTime.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        root.getChildren().add(lblTime);
        update();

        return root;
    }

    /**
     * เรียกใช้ใน ScheduledExecutorService simulationThread
     */
    public static void update() {
        int month = GameManager.getInstance().getGameMonth();
        int day = GameManager.getInstance().getGameDay();
        lblTime.setText(String.format("📅 Month: %d  |  Day: %d", month, day));
    }
}