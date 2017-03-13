package com.example.aawee.weatherdisplay.tools;

import android.content.Context;
import android.util.Log;

import com.example.aawee.weatherdisplay.OpenWeatherClient;
import com.example.aawee.weatherdisplay.R;
import com.example.aawee.weatherdisplay.classes.ForecastArea;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Aawee on 6/03/2017.
 */

public class RemoteFetch {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";

    //private static final String OPEN_WEATHER_BULK_API =
    //        "http://api.openweathermap.org/data/2.5/box/city?bbox=%f,%f,%f,%f,%f&appid=%s";
    private static final String OPEN_WEATHER_BULK_API = "http://api.openweathermap.org/data/2.5/box/city?bbox=";

    private static final String OPEN_WEATHER_BASE_URL = "http://api.openweathermap.org/";

//    public static JSONObject getJsonForCity(Context context, String city){
//        try {
//            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
//            String key = context.getString(R.string.open_weather_maps_app_id);
//
//            return getJsonForUrl(url, key);
//        }
//        catch (Exception e) {
//            Log.e("JSONerr", e.getMessage());
//            return null;
//        }
//    }

    // request for weather using Retrofit (get), Gson (parse), Rxjava (asynchronous call)
    public static Observable<ForecastArea> getForecastResponse (double lonLeft, double latBottom,
                                           double lonRight, double latTop, int zoom, String apiKey) {

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(OPEN_WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter);

        Retrofit retrofit = builder.build();
        OpenWeatherClient client = retrofit.create(OpenWeatherClient.class);

        Log.d("RXJavaNot", OPEN_WEATHER_BULK_API + Double.toString(lonLeft) + "," + Double.toString(latBottom)
                + "," + Double.toString(lonRight) + "," + Double.toString(latTop) + "," + Integer.toString(zoom)
                + "&appid=45f96ecf780e933deba504d9df18e977");

        return client.forecastsForArea(Double.toString(lonLeft) + "," + Double.toString(latBottom)
                + "," + Double.toString(lonRight) + "," + Double.toString(latTop) + "," + Integer.toString(zoom), apiKey);
    }



    public static JSONObject getJsonForArea(Context context,
                                            double lonLeft, double latBottom, double lonRight, double latTop, double zoom) {
        try {
            //URL url = new URL(String.format(OPEN_WEATHER_BULK_API, lonLeft, latBottom, lonRight, latTop, zoom,
            //        context.getString(R.string.open_weather_maps_app_id)));

            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);

            URL url = new URL( OPEN_WEATHER_BULK_API + Double.toString(lonLeft) + "," + Double.toString(latBottom) + ","
                    + Double.toString(lonRight) + "," + Double.toString(latTop) + "," + Integer.toString( (int) zoom )
                    + "&appid=" + context.getString(R.string.open_weather_maps_app_id) );

            return getJsonForUrl(url);
        }
        catch (Exception e) {
            Log.e("JSONerr", "For area " + e.getMessage().toString());
            return null;
        }
    }

    public static JSONObject getJsonForUrl(URL url){
        HttpURLConnection connection = null ;
        InputStream is = null;

        try {
            Log.d("JSONnot", url.toString() );

            connection = (HttpURLConnection) url.openConnection();

            int status = connection.getResponseCode();
            Log.d("JSONnot", Integer.toString(status) + " STATUS ");

            //is = connection.getInputStream();
            if (status < HttpURLConnection.HTTP_BAD_REQUEST) is = connection.getInputStream();
            else is = connection.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuffer json = new StringBuffer();
            String tmp = "";

            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            connection.disconnect();

            JSONObject data = new JSONObject(json.toString());

            Log.d("JSONnot", json.toString());

            if (data.getInt("cod") != 200) {
                return null;
            }
            return data;

        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("JSONerr", "For url " + e.getMessage().toString());
            return null;
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { connection.disconnect(); } catch(Throwable t) {}
        }

    }




}
