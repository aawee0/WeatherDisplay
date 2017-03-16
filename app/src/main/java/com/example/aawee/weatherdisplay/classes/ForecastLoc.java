package com.example.aawee.weatherdisplay.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aawee on 8/03/2017.
 */

public class ForecastLoc {

    private String name; // name of the location
    private Coord coord; // coordinates of the location

    private WeatherMain main; // field with info about temperature, humidity, etc
    private List<WeatherType> weather; // field with information about weather type: Clear, Cloudy, etc

    @SerializedName("dt")
    private long time;

    public ForecastLoc (String locName, Coord coordinates,
                        WeatherMain weatherMain, List<WeatherType> weatherTypes, long timeCreated) {
        name = locName;
        coord = coordinates;
        main = weatherMain;
        weather = weatherTypes;
        time = timeCreated;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return coord;
    }

    public WeatherMain getMain() {
        return main;
    }

    public List<WeatherType> getWeather() {
        return weather;
    }

    public long getTime() { return time; }
}
