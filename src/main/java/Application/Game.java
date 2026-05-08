package Application;

import GUI.GUIServices.GUIManager;
import GUI.GUIServices.CameraManager;
import GUI.GameLoopPhase.UpdateLogic.InputSensing;
import GUI.GameLoopPhase.UpdateRender.HybridUI.DateTimeBar;
import GUI.GameLoopPhase.UpdateRender.HybridUI.StatsBar;
import GUI.GameLoopPhase.UpdateRender.LayerBackground;
import GUI.InitialPhase.InitAssetNSound;
import GUI.InitialPhase.InitDataStructure;
import GUI.InitialPhase.InitMap;
import Logic.Core.SimulationManager;
import Model.BaseBuilding;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Adds a real-time stats HUD and a build bar.
 */
public class Game extends Application {

    private double currentMouseX, currentMouseY;

    @Override
    public void start(Stage primaryStage) {
        InitDataStructure.Init();
        InitAssetNSound.Init();
        InitMap.Init();



        GUIManager ui = GUIManager.getInstance();
        ui.initialize(1000, 800);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setTitle("My City Game");
        primaryStage.setScene(ui.getScene());
        primaryStage.show();



        GUIManager.getInstance().getScene().setOnMouseMoved(e -> {
            currentMouseX = e.getX();
            currentMouseY = e.getY();
        });


        InputSensing.SetMouse();


        // สร้าง Background Thread สำหรับ Simulation โดยเฉพาะ
        ScheduledExecutorService simulationThread = Executors.newSingleThreadScheduledExecutor();
        simulationThread.scheduleAtFixedRate(() -> {
            try {
                SimulationManager.getInstance().update();

                Platform.runLater(() -> {
                    StatsBar.update();
                    DateTimeBar.update();
                });

            } catch (Exception ex) {
                // ดัก Error
                ex.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
        // ปรับเลข 1 เป็น 500 (และเปลี่ยนเป็น TimeUnit.MILLISECONDS) ได้ถ้าอยากให้เกมเดินเร็วขึ้น

        primaryStage.setOnCloseRequest(e -> {
            simulationThread.shutdown();
            System.out.println("Simulation Thread Shutdown.");
        });


        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // ส่วนที่ 1: UpdateLogic
//                simulationExecution();
//                stateRuleChecking();
//                uiDataPrep();

                CameraManager.getInstance().update(
                        currentMouseX,
                        currentMouseY,
                        GUIManager.getInstance().getScene().getWidth(),
                        GUIManager.getInstance().getScene().getHeight()
                );


                // ส่วนที่ 2: UpdateRender
                GUIManager.getInstance().clear(); // ล้างจอ
                LayerBackground.render();
//                renderWorldObject();

            }
        };



        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
