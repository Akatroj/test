package agh.cs.lab.AnimalSimulator.Logic;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class Genome {

    private static final int GENOME_SIZE = 32;
    private static final List<Integer> possibleGenes = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
    private final int[] genes = new int[GENOME_SIZE];

    public Genome(Animal parent1, Animal parent2) {
        generateGenomeFromParent(parent1.getGenes(), parent2.getGenes());
        fixGenes();
        Arrays.sort(genes);
    }

    public Genome() {
        generateRandomGenome();
        fixGenes();
        Arrays.sort(genes);
    }

    private void generateRandomGenome() {
        for (int i = 0; i < GENOME_SIZE; i++) {
            genes[i] = ThreadLocalRandom.current().nextInt(Directions.values().length);
        }
    }

    public int getRandomGene() {
        return genes[getRandomGeneIndex()];
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i : genes) {
            result.append(i);
        }
        return result.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }

    private void generateGenomeFromParent(Genome mumGenome, Genome dadGenome) {
        int i = 0;
        int genesFromDadAmount = abs(
                ThreadLocalRandom.current().nextInt(GENOME_SIZE - 2) -
                ThreadLocalRandom.current().nextInt(GENOME_SIZE - 2)
        ) + 1; //1-31
        //array will be sorted anyway, no point in dividing it into 3 parts

        while (i < genesFromDadAmount) {
            genes[i] = dadGenome.getRandomGene();
            i++;
        }

        while (i < GENOME_SIZE) {
            genes[i] = mumGenome.getRandomGene();
            i++;
        }
    }

    private int getRandomGeneIndex() {
        return ThreadLocalRandom.current().nextInt(GENOME_SIZE); //0-31
    }

    private List<Integer> getMissingGenes() {
        List<Integer> temp = Arrays.stream(genes).boxed().distinct().collect(Collectors.toList());
        return possibleGenes.stream()
                .filter(gene -> !temp.contains(gene))
                .collect(Collectors.toList());
    }

    private void fixGenes() {
        while (!getMissingGenes().isEmpty()) {
            List<Integer> missingGenes = getMissingGenes();
            for (Integer i : missingGenes) {
                genes[getRandomGeneIndex()] = i;
            }
        }
    }

}


