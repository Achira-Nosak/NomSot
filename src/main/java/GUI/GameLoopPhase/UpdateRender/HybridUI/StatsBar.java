package GUI.GameLoopPhase.UpdateRender.HybridUI;

import Logic.Stats.CityMasterStats;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class StatsBar {

    // ตัวแปรเก็บ UI
    private static Label lblMoneyVal, lblMoneyNet;
    private static Label lblPopVal, lblPopNet;
    private static ProgressBar pbHappy;
    private static Tooltip tooltipHappy;
    private static Label lblPowerVal, lblWaterVal, lblFoodVal, lblPollutionVal, lblEduVal;

    public static HBox create() {
        HBox root = new HBox(10);
        root.setAlignment(Pos.TOP_RIGHT);
        root.setPickOnBounds(false);

        String glassStyle =
                "-fx-background-color: rgba(40, 40, 40, 0.75);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;";

        VBox iconBar = new VBox(15);
        iconBar.setAlignment(Pos.TOP_CENTER);
        iconBar.setPadding(new Insets(15, 10, 15, 10));
        iconBar.setStyle(glassStyle);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10); shadow.setOffsetY(5); shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        iconBar.setEffect(shadow);

        VBox detailsBox = new VBox(15);
        detailsBox.setAlignment(Pos.TOP_RIGHT);
        detailsBox.setPadding(new Insets(15, 0, 15, 0));
        detailsBox.setPickOnBounds(false);


        // 1. การเงิน
        lblMoneyVal = createLabel("💰 $0");
        lblMoneyNet = createLabel("(+0)");
        addStatRow("💰", iconBar, detailsBox, glassStyle, lblMoneyVal, lblMoneyNet);

        // 2. ประชากร
        lblPopVal = createLabel("👥 Pop: 0");
        lblPopNet = createLabel("(+0)");
        addStatRow("👥", iconBar, detailsBox, glassStyle, lblPopVal, lblPopNet);

        // 3. ความสุข
        pbHappy = new ProgressBar(0.0);
        pbHappy.setPrefSize(100, 15);
        tooltipHappy = new Tooltip("Happiness: 0%");
        tooltipHappy.setStyle("-fx-font-size: 14px;");
        tooltipHappy.setShowDelay(Duration.millis(100));
        Tooltip.install(pbHappy, tooltipHappy);
        addStatRow("😊", iconBar, detailsBox, glassStyle, createLabel("😊 "), pbHappy);

        // 4. พลังงาน
        lblPowerVal = createLabel("⚡ 0 / 0");
        addStatRow("⚡", iconBar, detailsBox, glassStyle, lblPowerVal);

        // 5. น้ำ
        lblWaterVal = createLabel("💧 0 / 0");
        addStatRow("💧", iconBar, detailsBox, glassStyle, lblWaterVal);

        // 6. อาหาร
        lblFoodVal = createLabel("🌾 0 / 0");
        addStatRow("🌾", iconBar, detailsBox, glassStyle, lblFoodVal);

        // 7. มลพิษ
        lblPollutionVal = createLabel("☣️ Total: 0");
        addStatRow("☣️", iconBar, detailsBox, glassStyle, lblPollutionVal);

        // 8. การศึกษา
        lblEduVal = createLabel("🎓 N:0 | B:0 | V:0 | G:0");
        Tooltip tEdu = new Tooltip("Non-Edu | Basic | Vocational | Graduate");
        tEdu.setStyle("-fx-font-size: 14px;");
        Tooltip.install(lblEduVal, tEdu);
        addStatRow("🎓", iconBar, detailsBox, glassStyle, lblEduVal);

        root.getChildren().addAll(detailsBox, iconBar);
        update();
        return root;
    }


    // Helper Methods (ตัวช่วยลดบรรทัดซ้ำ)

    // สร้าง Label แบบกำหนด Style ไว้แล้ว
    private static Label createLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        return lbl;
    }

    // จัดการสร้างกล่อง, ปุ่ม, ผูก Action และยัดลงจอ
    private static void addStatRow(String iconText, VBox iconBar, VBox detailsBox, String style, Node... elements) {
        // 1. สร้างกล่องรายละเอียด
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);
        box.setMinHeight(40);
        box.setMaxHeight(40);
        box.setStyle(style + "-fx-padding: 0 15 0 15;");
        box.setVisible(false); // ซ่อนไว้แต่แรก

        // เอา Label หรือ ProgressBar ที่ส่งเข้ามา ยัดใส่กล่อง
        box.getChildren().addAll(elements);

        // 2. สร้างปุ่ม
        Button btn = new Button(iconText);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        btn.setMinSize(40, 40);
        btn.setMaxSize(40, 40);

        // 3. ผูก Action สลับซ่อน/โชว์
        btn.setOnAction(e -> box.setVisible(!box.isVisible()));

        // 4. ยัดลง VBox หลัก
        iconBar.getChildren().add(btn);
        detailsBox.getChildren().add(box);
    }


    // ระบบอัปเดตข้อมูลแบบ Real-time
    public static void update() {
        CityMasterStats stats = CityMasterStats.getInstance();

        double treasury = stats.finance.getTreasuryCurrent();
        double netIncome = stats.finance.getNetIncomeCurrent();
        lblMoneyVal.setText(String.format("💰 $%,.0f", treasury));
        if (netIncome >= 0) {
            lblMoneyNet.setText(String.format("(+$%,.0f)", netIncome));
            lblMoneyNet.setTextFill(Color.LIMEGREEN);
        } else {
            lblMoneyNet.setText(String.format("(-$%,.0f)", Math.abs(netIncome)));
            lblMoneyNet.setTextFill(Color.TOMATO);
        }

        int pop = stats.population.getPopCurrent();
        int netPop = stats.population.getPopIncreasingRate() - stats.population.getPopDecreasingRate();
        lblPopVal.setText(String.format("👥 Pop: %,d", pop));
        if (netPop >= 0) {
            lblPopNet.setText(String.format("(+%d)", netPop));
            lblPopNet.setTextFill(Color.LIMEGREEN);
        } else {
            lblPopNet.setText(String.format("(%d)", netPop));
            lblPopNet.setTextFill(Color.TOMATO);
        }

        double happy = stats.social.getHappiness();
        pbHappy.setProgress(happy / 100.0);
        tooltipHappy.setText(String.format("Happiness: %.1f%%", happy));
        if (happy >= 75) pbHappy.setStyle("-fx-accent: limegreen;");
        else if (happy >= 40) pbHappy.setStyle("-fx-accent: gold;");
        else pbHappy.setStyle("-fx-accent: tomato;");

        double pSupply = stats.infrastructure.getPowerSupply();
        double pDemand = stats.infrastructure.getPowerDemand();
        lblPowerVal.setText(String.format("⚡ %,.0f / %,.0f MW", pSupply, pDemand));
        lblPowerVal.setTextFill(pSupply >= pDemand ? Color.WHITE : Color.TOMATO);

        double wSupply = stats.infrastructure.getWaterSupply();
        double wDemand = stats.infrastructure.getWaterDemand();
        lblWaterVal.setText(String.format("💧 %,.0f / %,.0f L", wSupply, wDemand));
        lblWaterVal.setTextFill(wSupply >= wDemand ? Color.WHITE : Color.TOMATO);

        double fSupply = stats.infrastructure.getFoodSupply();
        double fDemand = stats.infrastructure.getFoodDemand();
        lblFoodVal.setText(String.format("🌾 %,.0f / %,.0f", fSupply, fDemand));
        lblFoodVal.setTextFill(fSupply >= fDemand ? Color.WHITE : Color.TOMATO);

        double pol = stats.environment.getDebuffPollutionTotal();
        lblPollutionVal.setText(String.format("☣️ Pollution: %,.0f", pol));
        if (pol > 500) lblPollutionVal.setTextFill(Color.TOMATO);
        else if (pol > 100) lblPollutionVal.setTextFill(Color.GOLD);
        else lblPollutionVal.setTextFill(Color.WHITE);

        double eNon = stats.social.getEducationNon();
        double eBasic = stats.social.getEducationBasic();
        double eVoc = stats.social.getEducationVocational();
        double eGrad = stats.social.getEducationGraduate();
        lblEduVal.setText(String.format("🎓 N:%.0f | B:%.0f | V:%.0f | G:%.0f", eNon, eBasic, eVoc, eGrad));
    }
}