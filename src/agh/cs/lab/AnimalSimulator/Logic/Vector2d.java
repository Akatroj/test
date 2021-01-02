package agh.cs.lab.AnimalSimulator.Logic;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Vector2d {

    public final int x, y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Vector2d)) return false;

        Vector2d that = (Vector2d) other;
        return (this.x == that.x && this.y == that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.x + other.x, this.y + other.y);
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    //normalise vector, so that coordinates are in range <0, bound)
    public Vector2d normalise(int boundX, int boundY) {
        return new Vector2d(Math.floorMod(x, boundX), Math.floorMod(y, boundY));
    }

    public Vector2d opposite() {
        return new Vector2d(-x, -y);
    }

    public boolean between(Vector2d lowerLeft, Vector2d upperRight) {
        return (x<=upperRight.x && x>=lowerLeft.x) && (y<=upperRight.y && y>=lowerLeft.y);
    }

    public static Vector2d generateRandomVector(int boundX, int boundY) {
        return new Vector2d(ThreadLocalRandom.current().nextInt(boundX), ThreadLocalRandom.current().nextInt(boundY));
    }
}
