package com.example.aawee.weatherdisplay.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aawee on 8/03/2017.
 */

public class Coord {
    @SerializedName(value="lat", alternate={"Lat"})
    double lat;
    @SerializedName(value="lon", alternate={"Lon"})
    double lon;

    public double getLat() {
        return lat;
    }

    public double getLon() { return lon; }
}
