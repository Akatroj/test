package agh.cs.lab.AnimalSimulator.GUI.FXMLControllers;

import agh.cs.lab.AnimalSimulator.Configuration.Config;
import agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map.MapDrawer;
import agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map.Statistics.GlobalStatisticsController;
import agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map.Statistics.PinnedAnimalStatisticsController;
import agh.cs.lab.AnimalSimulator.Logic.SimulationEngine;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;

import java.util.Timer;
import java.util.TimerTask;

public class SimulationController {

    private final int nextDayDelay;

    private final MapDrawer mapDrawer;
    private final GlobalStatisticsController globalStatisticsController;
    private final PinnedAnimalStatisticsController pinnedAnimalStatisticsController;


    private final SimulationEngine engine;

    private Timer scheduler;
    private boolean isPaused;
    private boolean hasStarted;


    public SimulationController(Canvas canvas, GlobalStatisticsController globalStatisticsController,
                                PinnedAnimalStatisticsController pinnedAnimalStatisticsController) {

        engine = new SimulationEngine(Config.getInstance());
        this.mapDrawer = new MapDrawer(engine.getMap(), canvas);
        this.nextDayDelay = Config.getInstance().getDayLength();
        this.globalStatisticsController = globalStatisticsController;
        this.pinnedAnimalStatisticsController = pinnedAnimalStatisticsController;
        globalStatisticsController.setSimulationEngine(engine);
        isPaused = true;
        hasStarted = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public MapDrawer getMapDrawer() {
        return mapDrawer;
    }

    public SimulationEngine getEngine() {
        return engine;
    }

    public void startSimulation() {
        if (isPaused) {
            if (!hasStarted) hasStarted = true;
            scheduler = new Timer();
            scheduler.schedule(createTimerTask(), 0, nextDayDelay);
            isPaused = false;
        }
    }

    public void pauseSimulation() {
        if (!isPaused && scheduler != null) {
            scheduler.cancel();
            isPaused = true;
        }
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    engine.nextDay();
                    Platform.runLater(() -> {
                        mapDrawer.dayChanged();
                        globalStatisticsController.updateStatistics();
                        pinnedAnimalStatisticsController.updateStats();
                    });
                });
            }
        };
    }
}
