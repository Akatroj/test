package agh.cs.lab.AnimalSimulator.Logic;

import agh.cs.lab.AnimalSimulator.Configuration.Config;

import java.util.*;
import java.util.stream.Collectors;

public class SimulationEngine {

    public static final double ENERGY_REQUIRED_TO_BREED_PERCENT = 0.5;
    public final int maxTries;


    private final WrappingMap map;
    private final int startEnergy;
    private final int plantEnergy;
    private final int moveEnergy;
    private final int startAnimals;
    private final List<Animal> animalList;
    private final Map<Genome, Integer> currentGenomeOccurrences;
    private final double energyToBreed;

    private final EngineStateBackuper backuper;

    private int currentDay;
    private long deadAnimals = 0;
    private long totalDeadAnimalsAge = 0;

    public SimulationEngine(Config config) {
        animalList = new LinkedList<>();
        currentGenomeOccurrences = new HashMap<>();
        startEnergy = config.getStartingEnergy();
        plantEnergy = config.getGrassEnergy();
        moveEnergy = config.getMoveEnergy();
        startAnimals = config.getStartAnimalsCount();
        map = new WrappingMap(config.getMapWidth(), config.getMapHeight(), config.getJungleRatio());
        energyToBreed = startEnergy * ENERGY_REQUIRED_TO_BREED_PERCENT;
        maxTries = map.getWidth()*map.getHeight();
        backuper = new EngineStateBackuper(this);

        populateMap();

        backuper.backupAll();
    }

    public WrappingMap getMap() {
        return map;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getCurrentAnimalsCount() {
        return animalList.size();
    }

    public int getCurrentGrassCount() {
        return map.getGrassMap().size();
    }

    public double getMeanEnergy() {
        return animalList.stream().mapToDouble(Animal::getEnergy).average().orElse(0d);
    }

    public double getMeanChildCount() {
        return animalList.stream().mapToDouble(Animal::getChildCount).average().orElse(0d);
    }

    public Genome getDominatingGenome() {
        return currentGenomeOccurrences.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
    }

    public double getMeanDeadAnimalAge() {
        if (deadAnimals == 0) return 0;
        return totalDeadAnimalsAge / ((double) deadAnimals);
    }


    public void nextDay() {
        removeDeadAnimals();
        moveAnimals();
        eatGrass();
        breedAnimals();
        growGrass();
        currentDay++;
        backuper.backupAll();
    }

    public EngineStateBackuper getBackuper() {
        return backuper;
    }

    private void addAnimalToSimulation(Animal animal) {
        map.placeAnimal(animal);
        animalList.add(animal);
        addGenomeOccurrence(animal.getGenes());
    }

    private void breedAnimals() {
        List<Animal> children = new LinkedList<>();
        map.getAnimalMap().forEach((pos, animalsAtPos) -> {
            if (animalsAtPos.size() > 1) {
                Animal parent1 = animalsAtPos.get(0);
                Animal parent2 = animalsAtPos.get(1);
                if ((parent1.getEnergy() > energyToBreed) && (parent2.getEnergy() > energyToBreed)) {
                    Animal child = parent1.breed(parent2);
                    child.setBirthDay(currentDay);
                    children.add(child);
                }
            }
        });
        for (Animal child : children) {
            addAnimalToSimulation(child);
        }
    }

    private void moveAnimals() {
        for (Animal a : animalList) {
            a.moveAndRotate();
            a.decreaseEnergy(moveEnergy);
        }
    }

    private void removeDeadAnimals() {
        Iterator<Animal> iterator = animalList.iterator();
        while (iterator.hasNext()) {
            Animal a = iterator.next();
            if (a.getEnergy() <= 0) {
                a.setDeathDate(currentDay);
                deadAnimals++;
                totalDeadAnimalsAge += currentDay - a.getBirthDay();
                removeGenomeOccurrence(a.getGenes());
                map.deleteAnimal(a);
                iterator.remove();
            }
        }
    }

    private void eatGrass() {
        Set<Vector2d> animalMapKeySet = map.getAnimalMap().keySet();

        for (Vector2d position : animalMapKeySet) {
            Grass grassAtPosition = map.getGrassMap().get(position);
            if (grassAtPosition != null) {
                List<Animal> animalsToFeed = map.getAnimalsWithMostEnergyAtPosition(position);
                int energyGained = plantEnergy / animalsToFeed.size();
                animalsToFeed.forEach(animal -> animal.increaseEnergy(energyGained));
                map.removeGrassAtPosition(position);
            }
        }
    }

    private void growGrass() {
        Set<Vector2d> grassKeySet = map.getGrassMap().keySet();
        Set<Vector2d> animalKeySet = map.getAnimalMap().keySet();
        Vector2d positionInJungle = map.getRandomPositionInJungle();
        Vector2d positionOutsideJungle = map.getRandomPositionNotInJungle();

        int i = 0;
        while ((grassKeySet.contains(positionInJungle) || animalKeySet.contains(positionInJungle)) && i < maxTries) {
            positionInJungle = map.getRandomPositionInJungle();
            i++;
        }
        if (i < maxTries) {
            map.addGrassAtPosition(positionInJungle);
        }

        i = 0;
        while ((grassKeySet.contains(positionOutsideJungle) || animalKeySet.contains(positionOutsideJungle)) && i < maxTries) {
            positionOutsideJungle = map.getRandomPositionNotInJungle();
            i++;
        }
        if (i < maxTries) {
            map.addGrassAtPosition(positionOutsideJungle);
        }
    }

    private void populateMap() {
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();
        int j = 0;
        for (int i = 0; i < startAnimals && j < maxTries; i++) {
            Vector2d position = Vector2d.generateRandomVector(mapWidth, mapHeight);
            boolean add = !animalList.stream().map(Animal::getPosition).collect(Collectors.toList()).contains(position);
            if (add) {
                addAnimalToSimulation(new Animal(map, position, startEnergy));
                j--;
            } else {
                i--;
                j++;
            }
        }
    }

    private void addGenomeOccurrence(Genome g) {
        currentGenomeOccurrences.computeIfPresent(g, (key, value) -> value++);
        currentGenomeOccurrences.putIfAbsent(g, 1);
    }

    private void removeGenomeOccurrence(Genome g) {
        Integer occurrences = currentGenomeOccurrences.get(g);

        if (occurrences < 1) {
            currentGenomeOccurrences.remove(g);
        }
        else {
            currentGenomeOccurrences.replace(g, occurrences-1);
        }
    }

    public Map<Genome, Integer> getCurrentGenomeOccurrences() {
        return currentGenomeOccurrences;
    }

}


