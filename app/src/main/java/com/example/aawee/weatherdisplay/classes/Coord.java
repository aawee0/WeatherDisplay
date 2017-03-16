package com.example.aawee.weatherdisplay.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aawee on 8/03/2017.
 */

public class Coord {
    @SerializedName(value="lat", alternate={"Lat"})
    private double lat;
    @SerializedName(value="lon", alternate={"Lon"})
    private double lon;

    public Coord(double latitude, double longitude) {
        lat = latitude;
        lon = longitude;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() { return lon; }
}
