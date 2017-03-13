package com.example.aawee.weatherdisplay.classes;

import java.util.List;

/**
 * Created by Aawee on 8/03/2017.
 */

public class ForecastLoc {

    String name; // name of the location
    Coord coord; // coordinates of the location

    WeatherMain main; // field with info about temperature, humidity, etc
    List<WeatherType> weather; // field with information about weather type: Clear, Cloudy, etc

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
}
