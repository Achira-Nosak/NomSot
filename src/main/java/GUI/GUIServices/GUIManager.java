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
    private HBox createDateTimeSection() {
        HBox bar = DateTimeBar.create();
        BorderPane.setAlignment(bar, Pos.TOP_LEFT);
        BorderPane.setMargin(bar, new Insets(20, 0, 0, 20));
        return bar;
    }

    private HBox createStatsSection() {
        HBox bar = StatsBar.create();
        BorderPane.setAlignment(bar, Pos.TOP_RIGHT);
        BorderPane.setMargin(bar, new Insets(20, 20, 0, 0));
        return bar;
    }

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