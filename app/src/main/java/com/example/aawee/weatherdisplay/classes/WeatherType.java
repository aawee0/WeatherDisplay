package com.example.aawee.weatherdisplay.classes;

/**
 * Created by Aawee on 8/03/2017.
 */

public class WeatherType {
    private int id; // id of the weather type
    private String main; // type of weather, e.g.: Clear, Clouds, etc

    public WeatherType(int weatherID, String weatherDesc) {
        id = weatherID;
        main = weatherDesc;
    }

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }
}
