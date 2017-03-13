package com.example.aawee.weatherdisplay.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aawee on 8/03/2017.
 */

public class ForecastArea {
    private int cod; // code
    private double calctime;
    private int cnt; // number of local forecasts
    private List<ForecastLoc> list; // list of local forecasts

    public ForecastArea() {
        list = new ArrayList<ForecastLoc>();
    }

    public int getCod() {
        return cod;
    }

    public double getCalctime() {
        return calctime;
    }

    public int getCnt() {
        return cnt;
    }

    public List<ForecastLoc> getList() {
        return list;
    }

    public static ForecastArea parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        ForecastArea forecastArea = gson.fromJson(response, ForecastArea.class);
        return forecastArea;
    }

}
