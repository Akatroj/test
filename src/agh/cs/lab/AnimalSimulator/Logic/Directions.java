package agh.cs.lab.AnimalSimulator.Logic;

import java.util.Random;

public enum Directions {
    NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

    private static final Random GENERATOR = new Random();
    private static final Directions[] VALUES = Directions.values();
    private static final int SIZE = VALUES.length;

    public static Directions getRandomDirection() {
        return VALUES[GENERATOR.nextInt(SIZE)];
    }

    public Directions nextDirection(int step) {
        return VALUES[(this.ordinal() + step) % VALUES.length];
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTH_EAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTH_WEST -> new Vector2d(-1, 1);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case EAST -> "\u21d2";
            case NORTH_EAST -> "\u21d7";
            case WEST -> "\u21d0";
            case SOUTH_EAST -> "\u21d8";
            case NORTH -> "\u21d1";
            case NORTH_WEST -> "\u21d6";
            case SOUTH -> "\u21d3";
            case SOUTH_WEST -> "\u21d9";
        };
    }
}
