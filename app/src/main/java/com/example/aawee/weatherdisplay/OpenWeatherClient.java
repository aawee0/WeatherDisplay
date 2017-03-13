package com.example.aawee.weatherdisplay;

import com.example.aawee.weatherdisplay.classes.ForecastArea;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aawee on 8/03/2017.
 */

public interface OpenWeatherClient {
    @GET("data/2.5/box/city")
    Observable<ForecastArea> forecastsForArea(@Query("bbox") String coordsZoom, @Query("appid") String apiKey);

}
