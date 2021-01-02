package agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map;

import agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map.Statistics.GlobalStatisticsController;
import agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map.Statistics.PinnedAnimalStatisticsController;
import agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.SimulationController;
import agh.cs.lab.AnimalSimulator.Logic.Animal;
import agh.cs.lab.AnimalSimulator.Logic.SimulationEngine;
import agh.cs.lab.AnimalSimulator.Logic.Vector2d;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.Optional;

public class MapController {

    @FXML
    private StackPane canvasInnerContainer;
    @FXML
    private StackPane canvasOuterContainer;
    @FXML
    private Button startStopButton;
    @FXML
    private GlobalStatisticsController globalStatisticsController;
    @FXML
    public PinnedAnimalStatisticsController pinnedAnimalStatisticsController;


    private SimulationController simulationController;

    public void initialize() {
        MapRegionPreparer mapRegionPreparer = new MapRegionPreparer(canvasOuterContainer, canvasInnerContainer);

        simulationController = new SimulationController(mapRegionPreparer.getCanvas(), globalStatisticsController, pinnedAnimalStatisticsController);

        canvasInnerContainer.getChildren().add(simulationController.getMapDrawer().getCanvas());

        startDay0();

    }

    @FXML
    private void startStopAction() {
        if (simulationController.isPaused()) {
            simulationController.startSimulation();
            startStopButton.setText("Zatrzymaj");
        } else {
            simulationController.pauseSimulation();
            startStopButton.setText("Wznów");
        }
    }

    @FXML
    private void saveReport() {
        if (simulationController.hasStarted()) {
            if (simulationController.isPaused()) {
                saveReport(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Generowanie raportu");
                alert.setHeaderText("");
                alert.setContentText("Najpierw zatrzymaj symulacje!");

                alert.showAndWait();
            }
        }
    }

    private void canvasClicked(MouseEvent event) {
        if (simulationController.hasStarted() && simulationController.isPaused()) {
            int squareSize = simulationController.getMapDrawer().getSquareSize();
            Vector2d position = new Vector2d((int) event.getX() / squareSize, (int) event.getY() / squareSize);
            Object objectAt = simulationController.getEngine().getMap().objectAt(position);
            if (objectAt instanceof Animal) {
                pinnedAnimalStatisticsController.setObservedAnimal((Animal) objectAt);
            }
        }
    }

    private void startDay0() {
        Platform.runLater(() -> simulationController.getMapDrawer().drawDayZero());

        simulationController.getMapDrawer().getCanvas().setOnMouseClicked((this::canvasClicked));

    }

    private void saveReport (boolean good) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Generowanie raportu");
        dialog.setContentText("Podaj do którego dnia należy utworzyć raport");
        if(!good) dialog.setHeaderText("Podano błędne dane");
        else dialog.setHeaderText("");

        Optional<String> temp = dialog.showAndWait();
        if(temp.isPresent()) {
            try {
                int days = Integer.parseInt(temp.get());
                SimulationEngine engine = simulationController.getEngine();
                if (days>engine.getCurrentDay() || days<0) throw new IllegalArgumentException();
                else {
                    engine.getBackuper().writeAverageToFile(days);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Generowanie raportu");
                    alert.setHeaderText("");
                    alert.setContentText("Zapisano dane do pliku statystyki.txt");

                    alert.showAndWait();
                }
            }
            catch (IllegalArgumentException e) {
                saveReport(false);
            }
        }
    }
}
