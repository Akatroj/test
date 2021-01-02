package agh.cs.lab.AnimalSimulator.Logic;

import java.util.*;

public class WrappingMap implements IPositionChangeObserver {

    private final int width;
    private final int height;
    private final Jungle jungle;

    private final Map<Vector2d, Grass> grass;

    private final Map<Vector2d, List<Animal>> animalMap = new HashMap<>();

    public WrappingMap(int width, int height, double jungleRatio) {
        this.width = width;
        this.height = height;
        grass = new HashMap<>();
        jungle = new Jungle(this, jungleRatio);
    }

    public void placeAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        List<Animal> animalsAtPosition = animalMap.computeIfAbsent(position, elem -> new LinkedList<>());
        animalsAtPosition.add(animal);
        animalsAtPosition.sort(Comparator.comparing(Animal::getEnergy));
        animalMap.put(position, animalsAtPosition);
        animal.addObserver(this);
    }

    public void deleteAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        List<Animal> animalsAtPosition = getAnimalsAtPosition(position);
        if (animalsAtPosition == null) return;
        animalsAtPosition.remove(animal);
        animal.removeObserver(this);
        if (animalsAtPosition.isEmpty()) animalMap.remove(position, animalsAtPosition);
    }

    public void addGrassAtPosition(Vector2d position) {
        grass.put(position, new Grass(position));
    }

    public void removeGrassAtPosition(Vector2d position) {
        grass.remove(position);
    }

    //returns position of a random free neighbour or random occupied neighbour if none are free
    public Vector2d getRandomNeighbour(Vector2d position) {
        Random generator = new Random();
        List<Vector2d> freeNeighbours = new LinkedList<>();
        List<Vector2d> allNeighbours = new LinkedList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Vector2d offset = new Vector2d(i, j);
                Vector2d neighbour = position.add(offset).normalise(width, height);
                allNeighbours.add(neighbour);
                if (!isOccupied(neighbour)) freeNeighbours.add(neighbour);
            }
        }
        return freeNeighbours.isEmpty() ?
                allNeighbours.get(generator.nextInt(allNeighbours.size())) :
                freeNeighbours.get(generator.nextInt(freeNeighbours.size()));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getJungleWidth() {
        return jungle.getWidth();
    }

    public int getJungleHeight() {
        return jungle.getHeight();
    }

    public Vector2d getJungleStart() {
        return jungle.getLowerLeft();
    }

    public Vector2d getJungleEnd() {
        return jungle.getUpperRight();
    }

    public Map<Vector2d, List<Animal>> getAnimalMap() {
        return animalMap;
    }

    public Map<Vector2d, Grass> getGrassMap() {
        return grass;
    }

    public List<Animal> getAnimalsAtPosition(Vector2d position) {
        return animalMap.get(position);
    }

    public List<Animal> getAnimalsWithMostEnergyAtPosition(Vector2d position) {
        List<Animal> animalsAtPosition = getAnimalsAtPosition(position);
        List<Animal> result = new LinkedList<>();
        if (animalsAtPosition == null) return null;
        int maxEnergy = animalsAtPosition.get(0).getEnergy();
        for (Animal a : animalsAtPosition) {
            if (a.getEnergy() == maxEnergy) result.add(a);
            else break;
        }
        return result;
    }

    public Vector2d getRandomPositionNotInJungle() {
        Vector2d temp;
        do {
            temp = Vector2d.generateRandomVector(width, height);
        }
        while (isVectorInJungle(temp));
        return temp;
    }

    public Vector2d getRandomPositionInJungle() {
        return jungle.getRandomPositionInJungle();
    }

    public boolean isVectorInJungle(Vector2d vector) {
        return jungle.isVectorInJungle(vector);
    }

    public Object objectAt(Vector2d position) {
        List<Animal> animals = getAnimalsAtPosition(position);
        if (animals == null) return grass.get(position);
        if (animals.get(0) == null) return grass.get(position);
        return animals.get(0);
    }

    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Animal animal) {
        List<Animal> animalsAtOldPosition = animalMap.get(oldPosition);

        animalsAtOldPosition.remove(animal);
        if (animalsAtOldPosition.isEmpty()) animalMap.remove(oldPosition, animalsAtOldPosition);

        List<Animal> animalsAtNewPosition = animalMap.computeIfAbsent(animal.getPosition(), k -> new LinkedList<>());
        animalsAtNewPosition.add(animal);
        animalsAtNewPosition.sort(Comparator.comparing(Animal::getEnergy));
    }

    @Override
    public String toString() {
        MapVisualizer temp = new MapVisualizer(this);
        return temp.draw(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
    }

}
