package com.example.aawee.weatherdisplay;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.aawee.weatherdisplay.classes.ForecastArea;
import com.example.aawee.weatherdisplay.classes.ForecastLoc;
import com.example.aawee.weatherdisplay.database.ForecastContract;
import com.example.aawee.weatherdisplay.database.ForecastDbHelper;
import com.example.aawee.weatherdisplay.tools.RemoteFetch;
import com.example.aawee.weatherdisplay.tools.Utilities;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.aawee.weatherdisplay.R.id.map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {
    // CONSTANTS
    private static final String DEFAULT_CITY = "Canberra";

    // FIELDS
    private TextView mDisplayText;
    private MapFragment mMapFragment;
    private GoogleMap googleMap;
    private HashMap<String, MarkerOptions> mMarkerOptions;

    // DB-related
    private SQLiteDatabase mainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //weatherArray = new ArrayList<WeatherLocation>();
        mDisplayText = (TextView) findViewById(R.id.map_text);

        // initialize the map
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(map);
        mMapFragment.getMapAsync(this);

        // database-related
        ForecastDbHelper dbHelper = new ForecastDbHelper(this);
        mainDB = dbHelper.getWritableDatabase();

        try {
            mainDB.beginTransaction();
            mainDB.delete(ForecastContract.ForecastLocDB.TABLE_NAME, null,null);
            mainDB.setTransactionSuccessful();
        }
        catch (SQLException e) {
            // error
            Log.e("DBerr", e.getStackTrace().toString() );
        }
        finally {
            mainDB.endTransaction();
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(new LatLng(0,0));
        builder.include(new LatLng(10,10));

        // set listener, so that camera changes only when map has undergone layout
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 100);

                googleMap.animateCamera(cu);
            }
        });
        googleMap.setOnCameraMoveStartedListener(this);


    }

    @Override
    public void onCameraMoveStarted(int i) {
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(null);
    }

    @Override
    public void onCameraIdle() {
        googleMap.setOnCameraIdleListener(null);
        try {
            //WHEN CAMERA MOVEMENT STOPS

            googleMap.clear(); // clear all markers
            mMarkerOptions = new HashMap<String, MarkerOptions>();

            // get screen bounds and zoom
            double mapZoom = googleMap.getCameraPosition().zoom;
            LatLngBounds bounds = (googleMap.getProjection().getVisibleRegion()).latLngBounds;
            LatLng ne = bounds.northeast;
            LatLng sw = bounds.southwest;

            // set subscriber for database and api request
            setSubscriber(sw.longitude, sw.latitude, ne.longitude, ne.latitude, (int) mapZoom);
        }
        catch (Error e) {
            Log.e("MAPerr", e.getMessage());
        }
        googleMap.setOnCameraMoveStartedListener(this);
    }

    public void setSubscriber (double lonLeft, double latBottom, double lonRight, double latTop, int zoom) {

        // threads
        Scheduler executionThread = Schedulers.io();
        Scheduler mainThread = AndroidSchedulers.mainThread();

        // get response (DB concatenated with API)
        Observable<ForecastArea> behaviorSubject = RemoteFetch.getConcatenatedResponse(mainDB,
                lonLeft, latBottom, lonRight,latTop, zoom);

        behaviorSubject
                .subscribeOn(executionThread) // thread for execution
                .observeOn(mainThread) // observe in main thread
                .subscribe(new Subscriber<ForecastArea>() {
                    @Override
                    public void onCompleted() {
                        // Log.d("RXJavaNot", "Subscriber completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // handle error
                    }

                    @Override
                    public void onNext(ForecastArea forecastArea) {
                        //Log.d("RXJavaNot", "Forecast for " + forecastArea.getList().get(0).getName() + " received by MainActivity subscriber.");
                        drawMarkers(forecastArea.getList());
                        // if the forecast is from API -- upload to database cache
                        if (forecastArea.getIsFromAPI()) uploadForecastsToDB(forecastArea.getList());
                    }
                });

    }


    private void drawMarkers (List<ForecastLoc> listForecasts) {
        //List<ForecastLoc> listForecasts = mForecastArea.getList();

        for(ForecastLoc forecast: listForecasts) {

            LatLng pos = new LatLng(forecast.getCoord().getLat() , forecast.getCoord().getLon());
            int icon_num = Utilities.getIconResourceForWeatherCondition(forecast.getWeather().get(0).getId());

            Log.d("MAPNot", forecast.getName() + " " + forecast.getCoord().getLat() + " " + forecast.getCoord().getLon());

            String titleForecast = "...";
            BitmapDescriptor iconImg = BitmapDescriptorFactory.fromResource( icon_num );

            String fCastName = forecast.getName();
            if (mMarkerOptions.containsKey(fCastName)) {
                mMarkerOptions.get(fCastName).icon(iconImg);
            }
            else {
                MarkerOptions marker = new MarkerOptions().position(pos).title(titleForecast).icon(iconImg);
                mMarkerOptions.put(fCastName, marker);
                googleMap.addMarker(marker);
            }
        }
        Log.d("MAPNot", "-----------------");

    }

    private void uploadForecastsToDB (List<ForecastLoc> listForecasts) {
        try {
            mainDB.beginTransaction();

            ContentValues cv;
            for(ForecastLoc forecast: listForecasts) {
                cv = new ContentValues();

                cv.put(ForecastContract.ForecastLocDB.LOC_NAME_NAME, forecast.getName());
                double lat = forecast.getCoord().getLat();
                double lon = forecast.getCoord().getLon();
                cv.put(ForecastContract.ForecastLocDB.LATITUDE_NAME, lat);
                cv.put(ForecastContract.ForecastLocDB.LONGITUDE_NAME, lon);
                cv.put(ForecastContract.ForecastLocDB.TEMPERATURE_NAME, forecast.getMain().getTemp());
                cv.put(ForecastContract.ForecastLocDB.WEATHER_ID_NAME, forecast.getWeather().get(0).getId());
                cv.put(ForecastContract.ForecastLocDB.WEATHER_DESCRIPTION_NAME, forecast.getWeather().get(0).getMain());
                cv.put(ForecastContract.ForecastLocDB.FORECAST_TIME_NAME, forecast.getTime());
                //long ins = mainDB.insert(ForecastContract.ForecastLocDB.TABLE_NAME, null, cv);

                long result = mainDB.insertWithOnConflict(ForecastContract.ForecastLocDB.TABLE_NAME,
                        null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                if (result==-1) mainDB.update(ForecastContract.ForecastLocDB.TABLE_NAME, cv,
                        ForecastContract.ForecastLocDB.LATITUDE_NAME + "=? AND " +
                        ForecastContract.ForecastLocDB.LONGITUDE_NAME + "=?",
                        new String[] { Double.toString(lat),  Double.toString(lon)});

                //Log.d("DBnot", forecast.getName() + Double.toString(lat) + " " + Double.toString(lon) + " " + Long.toString(result));

            }

            mainDB.setTransactionSuccessful();
        }
        catch (SQLException e) {
            // error
            Log.e("DBerr", e.getMessage() );
        }
        finally {
            mainDB.endTransaction();
        }

    }

}
