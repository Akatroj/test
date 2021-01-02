package agh.cs.lab.AnimalSimulator.Logic;

import java.util.LinkedList;
import java.util.List;

public class Animal {

    private static final double ENERGY_PASSED_TO_CHILD_PERCENT = 0.25;

    private final Genome myGenes;
    private final WrappingMap map;
    private final List<IPositionChangeObserver> observers = new LinkedList<>();
    private final List<Animal> children = new LinkedList<>();

    private int birthDay;
    private Vector2d position;
    private Directions direction;
    private int energy;
    private int deathDate;

    //random animal
    public Animal(WrappingMap map, Vector2d position, int energy) {
        this.map = map;
        this.position = position;
        this.energy = energy;
        myGenes = new Genome();
        this.direction = Directions.getRandomDirection();
    }

    //animal from breeding
    public Animal(WrappingMap map, Animal mom, Animal dad) {
        this.map = map;
        this.direction = Directions.getRandomDirection();
        position = map.getRandomNeighbour(mom.getPosition());
        myGenes = new Genome(mom, dad);

        int energyTakenFromMom = (int) Math.round(mom.getEnergy() * ENERGY_PASSED_TO_CHILD_PERCENT);
        int energyTakenFromDad = (int) Math.round(dad.getEnergy() * ENERGY_PASSED_TO_CHILD_PERCENT);

        mom.decreaseEnergy(energyTakenFromMom);
        dad.decreaseEnergy(energyTakenFromDad);
        energy = energyTakenFromMom + energyTakenFromDad;
    }

    public Directions getDirection() {
        return direction;
    }

    public Vector2d getPosition() {
        return position;
    }

    public Genome getGenes() {
        return myGenes;
    }

    public int getEnergy() {
        return energy;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public int getDeathDate() {
        return deathDate;
    }

    public int getChildCount() {
        return children.size();
    }

    public long getDescendantCount() {
        long result = children.size();
        for (Animal child : children) {
            result += child.getDescendantCount();
        }
        return result;
    }

    public boolean isDead() {
        return deathDate != 0;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
    }

    public void setDeathDate(int day) {
        deathDate = day;
    }


    public void moveAndRotate() {
        Vector2d oldPosition = position;
        position = position.add(direction.toUnitVector()).normalise(map.getWidth(), map.getHeight());
        rotate();
        positionChanged(oldPosition);
    }

    public void rotate() {
        direction = direction.nextDirection(myGenes.getRandomGene());
    }

    public Animal breed(Animal partner) {
        Animal offspring = new Animal(map, this, partner);
        this.addChild(offspring);
        partner.addChild(offspring);
        return offspring;
    }

    public void addChild(Animal child) {
        children.add(child);
    }

    public void decreaseEnergy(int amount) {
        this.energy -= amount;
    }

    public void increaseEnergy(int amount) {
        this.energy += amount;
    }

    public void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        this.observers.remove(observer);
    }

    private void positionChanged(Vector2d oldPosition) {
        for (IPositionChangeObserver o : this.observers) {
            o.positionChanged(oldPosition, this);
        }
    }

    @Override
    public String toString() {
        return direction.toString();
    }

    public String printForTests() {
        return super.toString() + " on " + position;
    }

}
