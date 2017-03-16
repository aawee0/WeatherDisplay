package com.example.aawee.weatherdisplay.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aawee on 8/03/2017.
 */

public class ForecastArea {
    //private int cod; // code
    //private double calctime;
    //private int cnt; // number of local forecasts

    private List<ForecastLoc> list; // list of local forecasts
    private boolean isFromAPI; // true (means got from API) by default, unless assigned otherwise

    public ForecastArea() {
        list = new ArrayList<ForecastLoc>();
        isFromAPI = true;
    }

    public ForecastArea(List<ForecastLoc> forecastList) {
        list = forecastList;
        isFromAPI = true;
    }

//    public void initIsFresh () {
//        isFresh = new boolean[list.size()];
//        for (int i = 0; i<list.size(); i++) isFresh[i] = true;
//    }

    public List<ForecastLoc> getList() {
        return list;
    }

    public boolean getIsFromAPI () { return isFromAPI; }

    public void setList (List<ForecastLoc> forecastList) {
        list = forecastList;
    }

    public void setIsFromAPI(boolean fromDB) {
        isFromAPI = fromDB;
    }

    public static ForecastArea parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        ForecastArea forecastArea = gson.fromJson(response, ForecastArea.class);
        return forecastArea;
    }



}
