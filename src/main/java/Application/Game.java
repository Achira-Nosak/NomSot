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
 * Main Entry Point and Dual-Thread Game Engine
 * <p><b>Architecture Design:</b></p>
 * <ul>
 * <li>Decoupled Game Loop: แยกการประมวลผลลอจิก (Simulation) ออกจากการวาดกราฟิก (Rendering) ป้องกัน UI Freezing</li>
 * <li>Thread Safety: ใช้ Platform.runLater()</li>
 * </ul>
 * <p><b>Execution Pipeline:</b></p>
 * <ul>
 * <li>Step 1 - Initialization: Init Set Load   DataStructure, Assets, Map, GUI, InputSensing ก่อนเริ่มเกม</li>
 * <li>Step 2 - Start Game Loops: เปิดการทำงาน 2 ระบบคู่ขนานกัน:
 * <ul>
 * <li>Simulation Thread (ScheduledExecutorService): รันเบื้องหลังด้วยความถี่คงที่ (Fixed Timestep) เพื่อประมวลผลระบบจำลองเมือง</li>
 * <li>Render Loop (AnimationTimer): รันตามอัตรารีเฟรชจอ (60 FPS) เพื่ออัปเดตกล้องและวาดภาพเมือง</li>
 * </ul>
 * </li>
 * </ul>
 */
public class Game extends Application {

    @Override
    public void start(Stage primaryStage) {
        // INITIALIZATION
        // ------------------------------------------------------------------------------
        InitDataStructure.Init();
        InitAssetNSound.Init();
        InitMap.Init();


        GUIManager ui = GUIManager.getInstance();
        ui.initialize(1000, 800);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setTitle("NomSot City Game");
        primaryStage.setScene(ui.getScene());
        primaryStage.show();


        InputSensing.SetMouse();
        // ------------------------------------------------------------------------------





        // SIMULATION THREAD
        // ------------------------------------------------------------------------------

        // สร้าง Background Thread สำหรับ Simulation โดยเฉพาะ
        // ใช้แทน new Thread เพราะตัวนี้ รักษาจังหวะ 1 วินาทีเป๊ะๆ หักลบเวลาที่ใช้ประมวลผลโค้ดให้อัตโนมัติ เวลาแม่นกว่า
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
        });
        // ------------------------------------------------------------------------------





        // RENDER THREAD
        // ------------------------------------------------------------------------------
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                CameraManager.getInstance().update(
                        InputSensing.getCurrentScreenX(),
                        InputSensing.getCurrentScreenY(),
                        GUIManager.getInstance().getScene().getWidth(),
                        GUIManager.getInstance().getScene().getHeight()
                );


                // UpdateRender
                GUIManager.getInstance().clear(); // ล้างจอ
                LayerBackground.render();
//                renderWorldObject();

            }
        };
        gameLoop.start();
        // ------------------------------------------------------------------------------
    }

    public static void main(String[] args) {
        launch(args);
    }
}
