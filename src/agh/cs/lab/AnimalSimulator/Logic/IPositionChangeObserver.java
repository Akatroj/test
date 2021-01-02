package agh.cs.lab.AnimalSimulator.Logic;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Animal animal);
}
