package agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map.Statistics;

import agh.cs.lab.AnimalSimulator.Logic.SimulationEngine;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class GlobalStatisticsController {

    @FXML
    private FlowPane root;
    @FXML
    private Label currentDay;
    @FXML
    private Label currentAnimalCount;
    @FXML
    private Label currentPlantCount;
    @FXML
    private Label dominatingGenome;
    @FXML
    private Label meanEnergy;
    @FXML
    private Label meanLifeLength;
    @FXML
    private Label meanChildrenCount;

    private SimulationEngine engine;

    public void setSimulationEngine(SimulationEngine engine) {
        this.engine = engine;
    }

    public void updateStatistics() {
        currentDay.setText(String.format("%d", engine.getCurrentDay()));
        currentAnimalCount.setText(String.format("%d", engine.getCurrentAnimalsCount()));
        currentPlantCount.setText(String.format("%d", engine.getCurrentGrassCount()));
        dominatingGenome.setText(engine.getDominatingGenome().toString());
        meanEnergy.setText(String.format("%.2f", engine.getMeanEnergy()));
        meanLifeLength.setText(String.format("%.2f", engine.getMeanDeadAnimalAge()));
        meanChildrenCount.setText(String.format("%.2f", engine.getMeanChildCount()));
    }

}
