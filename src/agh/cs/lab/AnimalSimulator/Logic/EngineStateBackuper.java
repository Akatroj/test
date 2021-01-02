package agh.cs.lab.AnimalSimulator.Logic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EngineStateBackuper {

    private final SimulationEngine engine;
    private final Map<Integer, Integer> animalCountBackup;
    private final Map<Integer, Integer> plantCountBackup;
    private final Map<Integer, Double> meanDeathAgeBackup;
    private final Map<Integer, Map<Genome, Integer>> genomeOccurrencesBackup;
    private final Map<Integer, Double> meanEnergyBackup;

    private int currentDay;


    public EngineStateBackuper(SimulationEngine engine) {
        this.engine = engine;
        meanEnergyBackup = new HashMap<>();
        animalCountBackup = new HashMap<>();
        plantCountBackup = new HashMap<>();
        genomeOccurrencesBackup = new HashMap<>();
        meanDeathAgeBackup = new HashMap<>();
    }

    public void backupAll() {
        currentDay = engine.getCurrentDay();
        backupAnimalCount();
        backupPlantCount();
        backupMeanEnergy();
        backupGenomeOccurrences();
        backupMeanDeathAge();
    }

    public void writeAverageToFile(int days) {
        try {
            FileWriter fileWriter = new FileWriter("statystyki.txt");
            Genome dominatingGenome = dominatingGenome(days);
            String content = "Statystyki po " + days + " dniach:\n" +
                    "Średnia ilość żywych zwierząt w ciągu dnia: " + String.format("%.2f", averageAnimalCount(days)) + "\n" +
                    "Średnia energia zwierząt w ciągu dnia: " + String.format("%.2f", averageEnergy(days)) + "\n" +
                    "Średnia ilość roślin w ciągu dnia: " + String.format("%.2f", averagePlantCount(days)) + "\n" +
                    "Średni wiek śmierci zwierząt: " + String.format("%.2f", averageDeathAge(days)) + "\n" +
                    "Dominujący genotyp: " + dominatingGenome +
                    ", wystąpił " + countTotalGenomeOccurrences(dominatingGenome) + " razy." + "\n";
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backupMeanEnergy() {
        meanEnergyBackup.put(currentDay, engine.getMeanEnergy());
    }

    private void backupAnimalCount() {
        animalCountBackup.put(currentDay, engine.getCurrentAnimalsCount());
    }

    private void backupPlantCount() {
        plantCountBackup.put(currentDay, engine.getCurrentGrassCount());
    }

    private void backupMeanDeathAge() {
        meanDeathAgeBackup.put(currentDay, engine.getMeanDeadAnimalAge());
    }

    private void backupGenomeOccurrences() {
        genomeOccurrencesBackup.put(currentDay, engine.getCurrentGenomeOccurrences());
    }

    private double averageAnimalCount(int days) {
        double result = 0;
        for (int i = 0; i <= days; i++) {
            result += animalCountBackup.get(i);
        }
        return result / days;
    }

    private double averagePlantCount(int days) {
        double result = 0;
        for (int i = 0; i <= days; i++) {
            result += plantCountBackup.get(i);
        }
        return result / days;
    }

    private double averageDeathAge(int days) {
        double result = 0;
        for (int i = 0; i <= days; i++) {
            result += meanDeathAgeBackup.get(i);
        }
        return result / days;
    }

    private double averageEnergy(int days) {
        double result = 0;
        for (int i = 0; i <= days; i++) {
            result += meanEnergyBackup.get(i);
        }
        return result / days;
    }

    private Genome dominatingGenome(int days) {
        return genomeOccurrencesBackup.entrySet().stream()
                .filter(mapEntry -> mapEntry.getKey() <= days) //get backup entries from before or during given day
                .map(Map.Entry::getValue).map(Map::keySet) //get a list of lists of all appearing genomes per day
                .flatMap(Collection::stream).distinct() //unpack the lists and select distinct genomes
                .max(Comparator.comparing(this::countTotalGenomeOccurrences)) //get the one with most occurrences
                .orElse(null);
    }

    private int countTotalGenomeOccurrences(Genome g) {
        return genomeOccurrencesBackup.values().stream()
                .map(genomeIntegerMap -> genomeIntegerMap.get(g))
                .reduce(0, Integer::sum);
    }
}
