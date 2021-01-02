package agh.cs.lab.AnimalSimulator.Configuration;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Config {

    private static final String jsonPath = "parameters.json";

    private static Config instance = null;
    private int mapWidth;
    private int mapHeight;
    private int startAnimalsCount;
    private int startEnergy;
    private int grassEnergy;
    private int moveEnergy;
    private double jungleRatio;
    private int dayLength;

    public static Config getInstance() {
        if (instance == null) {
            Gson gson = new Gson();
            try {
                JsonReader reader = new JsonReader(new FileReader(jsonPath));
                instance = gson.fromJson(reader, Config.class);
            } catch (FileNotFoundException e) {
                instance = new Config();
                e.printStackTrace();
            }
        }
        return instance;
    }
    public int getMapWidth(){
        if(mapWidth<0 || mapWidth>500) throw new IllegalArgumentException("Niepoprawna szerokosc mapy (max 500)");
        return mapWidth;
    }
    public int getMapHeight(){
        if(mapHeight<0 || mapWidth>500) throw new IllegalArgumentException("Niepoprawna wysokosc mapy (max 500)");
        return mapHeight;
    }
    public int getStartAnimalsCount(){
        if(startAnimalsCount<0 || startAnimalsCount>mapHeight*mapWidth) throw new IllegalArgumentException("Niepoprawna poczatkowa ilosc zwierzat");
        return startAnimalsCount;
    }
    public int getGrassEnergy(){
        if(grassEnergy<0) throw new IllegalArgumentException("Niepoprawna energia trawy");
        return grassEnergy;
    }
    public int getMoveEnergy(){
        if(moveEnergy<0) throw new IllegalArgumentException("Niepoprawny koszt ruchu zwierzecia");
        return moveEnergy;
    }
    public double getJungleRatio() {
        if(jungleRatio>1|| jungleRatio<0) throw new IllegalArgumentException("Zly rozmiar jungli. Podaj liczbe z przedzialu (0,1)");
        return jungleRatio;
    }
    public int getDayLength(){
        if(dayLength<1) throw new IllegalArgumentException("Niepoprawny czas miedzy dniami");
        return dayLength;
    }

    public int getStartingEnergy() {
        if(startEnergy<0) throw new IllegalArgumentException("Niepoprawna energia poczatkowa zwierzaka");
        return startEnergy;
    }
}
