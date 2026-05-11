package GUI.GUIServices;

import GUI.GameLoopPhase.UpdateRender.HybridUI.BuildBar;
import GUI.GameLoopPhase.UpdateRender.HybridUI.DateTimeBar;
import GUI.GameLoopPhase.UpdateRender.HybridUI.StatsBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


/**
 * Main GUI Manager
 * <p>Prototype Note: ปัจจุบันรวมชิ้นส่วน UI พื้นฐานได้แก่ DateTimeBar, StatsBar, BuildBar ไว้เพื่อสาธิตการจัดวาง Layout</p>
 * <p><b>Architecture and Layout Strategies:</b></p>
 * <ul>
 *      <li>Singleton</li>
 *      <li><b>StackPane</b>: ใช้ StackPane เป็น Root เพื่อซ้อนทับการแสดงผล 2 ชั้น (Layering)
 *      <ul>
 *          <li>Layer 1: <b>Canvas</b> (RenderGameWorld Map,Object) (ตึก, พื้นดิน) ใช้แทน GridPane เพราะ Canvas มีประสิทธิภาพในการประมวลผลวาดกราฟิกจำนวนมากต่อเฟรม (High FPS) มากกว่าการใช้ Node ย่อยๆ</li>
 *          <li>Layer 2: <b>BorderPane</b> (uiOverlay) ทำหน้าที่จัดวาง UI (ปุ่ม, ตัวหนังสือ) ให้ชิดขอบจอโดยอัตโนมัติ โดยตั้งค่า setPickOnBounds(false) เพื่อให้คลิกทะลุช่องว่างของ UI ลงไปโดน Canvas ชั้นล่างได้
 *          <ul>
 *              <li>Top Left: <b>HBox</b> DateTimeBar</li>
 *              <li>Top Right: <b>HBox(VBox inside)</b> StatsBar</li>
 *              <li>Bottom Center: <b>VBox(HBox inside)</b> BuildBar</li>
 *          </ul>
 *          </li>
 *      </ul>
 *      </li>
 * </ul>
 */
public class GUIManager {
    private static GUIManager instance;

    private Scene scene;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private StackPane root;

    private GUIManager() {
    }

    public static GUIManager getInstance() {
        if (instance == null) instance = new GUIManager();
        return instance;
    }

    /**
     * เรียกใช้ครั้งเดียวตรง Game Start จัดเรียง element ให้เข้าที่
     */
    public void initialize(double width, double height) {
        this.root = new StackPane();

        // 1. สร้าง Canvas
        this.gameCanvas = new Canvas(width, height);
        this.gameCanvas.widthProperty().bind(root.widthProperty());
        this.gameCanvas.heightProperty().bind(root.heightProperty());
        this.gc = gameCanvas.getGraphicsContext2D();

        // 2. สร้าง UI Overlay
        BorderPane uiOverlay = new BorderPane();
        uiOverlay.setPickOnBounds(false);

        // --- ประกอบร่าง UI Component ---
        uiOverlay.setLeft(createDateTimeSection());
        uiOverlay.setRight(createStatsSection());
        uiOverlay.setBottom(createBuildBarSection());

        // 3. รวมร่าง
        root.getChildren().addAll(gameCanvas, uiOverlay);
        this.scene = new Scene(root, width, height);
    }



    // Helper Methods สำหรับจัดระเบียบ Margin

    /**
     * สาธิตสร้าง DateTimeBar และ set ตำแหน่ง
     */
    private HBox createDateTimeSection() {
        HBox bar = DateTimeBar.create();
        BorderPane.setAlignment(bar, Pos.TOP_LEFT);
        BorderPane.setMargin(bar, new Insets(20, 0, 0, 20));
        return bar;
    }

    /**
     * สาธิตสร้าง StatsBar และ set ตำแหน่ง
     */
    private HBox createStatsSection() {
        HBox bar = StatsBar.create();
        BorderPane.setAlignment(bar, Pos.TOP_RIGHT);
        BorderPane.setMargin(bar, new Insets(20, 20, 0, 0));
        return bar;
    }

    /**
     * สาธิตสร้าง BuildBar และ set ตำแหน่ง
     */
    private VBox createBuildBarSection() {
        VBox bar = BuildBar.create();
        BorderPane.setAlignment(bar, Pos.CENTER);
        BorderPane.setMargin(bar, new Insets(0, 0, 20, 0));
        return bar;
    }

    public void clear() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    // --- Getters ---
    public Scene getScene() { return scene; }
    public GraphicsContext getGc() { return gc; }
    public Canvas getGameCanvas() { return gameCanvas; }
}