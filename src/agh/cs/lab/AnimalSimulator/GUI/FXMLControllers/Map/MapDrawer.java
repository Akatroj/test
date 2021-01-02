package agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map;

import agh.cs.lab.AnimalSimulator.Configuration.Config;
import agh.cs.lab.AnimalSimulator.Logic.Animal;
import agh.cs.lab.AnimalSimulator.Logic.WrappingMap;
import agh.cs.lab.AnimalSimulator.Logic.Vector2d;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapDrawer {

    private static final Color animalColor = Color.RED;
    private static final Color grassColor = Color.GREEN;
    private static final Color jungleColor = Color.LIME;
    private static final Color defaultColor = Color.WHITE;


    private final WrappingMap map;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private int squareSize;

    public MapDrawer(WrappingMap map, Canvas canvas) {
        this.map = map;
        this.canvas = canvas;
        gc = this.canvas.getGraphicsContext2D();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public int getSquareSize() {
        return squareSize;
    }

    public void drawDayZero() {
        setSquareSize();
        clearCanvas();
        drawJungle();
    }

    private void setSquareSize() {
        squareSize = (int) (canvas.widthProperty().getValue() / Config.getInstance().getMapWidth());
    }

    public void dayChanged() {
        clearCanvas();
        drawJungle();
        drawGrass();
        drawAnimals();
    }

    public void clearCanvas() {
        gc.setFill(defaultColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawRectangleFromVector2d(Vector2d lowerLeft, Vector2d upperRight, double squareSize) {
        double drawWidth = (upperRight.x - lowerLeft.x + 1) * squareSize;
        double drawHeight = (upperRight.y - lowerLeft.y + 1) * squareSize;

        gc.fillRect(lowerLeft.x * squareSize, lowerLeft.y * squareSize, drawWidth, drawHeight);
    }

    public void drawSquareFromVector2d(Vector2d position, double squareSize) {
        gc.fillRect(position.x * squareSize, position.y * squareSize, squareSize, squareSize);
    }

    private void setColor(Color color) {
        gc.setFill(color);
    }

    private void drawAnimals() {
        Set<Map.Entry<Vector2d, List<Animal>>> mapEntrySet = map.getAnimalMap().entrySet();
        for (Map.Entry<Vector2d, List<Animal>> entry : mapEntrySet) {
            double saturation = getSaturationForAnimal(entry.getValue().get(0));
            setColor(Color.hsb(animalColor.getHue(), saturation, animalColor.getBrightness()));
            Vector2d animalPosition = entry.getKey();
            drawSquareFromVector2d(animalPosition, squareSize);
        }
    }

    private double getSaturationForAnimal(Animal animal) {
        return Math.min(1, Math.max(0.3, ((double) animal.getEnergy()) / Config.getInstance().getStartingEnergy()));
    }

    private void drawGrass() {
        setColor(grassColor);
        Set<Vector2d> grassSet = map.getGrassMap().keySet();
        for (Vector2d grassPosition : grassSet) {
            drawSquareFromVector2d(grassPosition, squareSize);
        }
    }

    private void drawJungle() {
        setColor(jungleColor);
        drawRectangleFromVector2d(map.getJungleStart(), map.getJungleEnd(), squareSize);
    }

}
