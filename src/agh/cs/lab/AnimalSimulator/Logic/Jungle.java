package agh.cs.lab.AnimalSimulator.Logic;

import java.util.Random;

public class Jungle {

    private final Vector2d lowerLeft;
    private final Vector2d upperRight;
    private final Random generator = new Random();
    private final int width;
    private final int height;

    public Jungle(WrappingMap map, double jungleRatio) {
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();
        lowerLeft = new Vector2d(
                (int) Math.floor((mapWidth*(1-jungleRatio))/2),
                (int) Math.floor((mapHeight*(1-jungleRatio))/2)
        );
        upperRight = new Vector2d(
                (int) Math.floor((mapWidth*(1+jungleRatio))/2),
                (int) Math.floor((mapHeight*(1+jungleRatio))/2)
        );

        width = upperRight.x - lowerLeft.x + 1;
        height = upperRight.y - lowerLeft.y + 1;
    }

    public Vector2d getLowerLeft() {
        return lowerLeft;
    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2d getRandomPositionInJungle() {
        return new Vector2d(
                generator.nextInt(width) + lowerLeft.x,
                generator.nextInt(height) + lowerLeft.y
        );
    }

    public boolean isVectorInJungle(Vector2d vector) {
        return vector.between(lowerLeft, upperRight);
    }
}
