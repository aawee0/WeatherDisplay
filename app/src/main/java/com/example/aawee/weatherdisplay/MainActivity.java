package com.example.aawee.weatherdisplay;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aawee.weatherdisplay.classes.ForecastArea;
import com.example.aawee.weatherdisplay.classes.ForecastLoc;
import com.example.aawee.weatherdisplay.tools.RemoteFetch;
import com.example.aawee.weatherdisplay.tools.Utilities;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.List;

import rx.Observable;
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
    private ForecastArea mForecastArea;

    Handler handler;

    public MainActivity(){
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //weatherArray = new ArrayList<WeatherLocation>();
        mDisplayText = (TextView) findViewById(R.id.map_text);

        // initialize the map
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(map);
        mMapFragment.getMapAsync(this);



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



            double mapZoom = googleMap.getCameraPosition().zoom;
            LatLngBounds bounds = (googleMap.getProjection().getVisibleRegion()).latLngBounds;
            LatLng ne = bounds.northeast;
            LatLng sw = bounds.southwest;


            Log.d("JSONnot", Double.toString(sw.longitude) + " " + Double.toString(sw.latitude) + " " +
                    Double.toString(ne.longitude) + " " + Double.toString(ne.latitude));

            //RemoteFetch.getForecastResponse(sw.longitude, sw.latitude,
            //        ne.longitude, ne.latitude, (int) mapZoom, "45f96ecf780e933deba504d9df18e977");
            //new getWeatherDataTask().execute(sw.longitude, sw.latitude, ne.longitude, ne.latitude, mapZoom);

            setSubscriber(sw.longitude, sw.latitude, ne.longitude, ne.latitude, (int) mapZoom);

        }
        catch (Error e) {
            Log.e("MAPerr", e.getMessage());
        }
        googleMap.setOnCameraMoveStartedListener(this);
    }

    public void setSubscriber (double lonLeft, double latBottom, double lonRight, double latTop, int zoom) {

        Subscriber<ForecastArea> subscriber = new Subscriber<ForecastArea>() {
            @Override
            public void onCompleted() {
                Log.d("RXJavaNot", "Subscriber completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ForecastArea forecastArea) {
                Log.d("RXJavaNot", forecastArea.getList().get(0).getName() + " " + forecastArea.getList().get(0).getCoord().getLat());
                drawMarkers(forecastArea.getList());
            }
        };

        Observable<ForecastArea> call = RemoteFetch.getForecastResponse(lonLeft, latBottom,
                lonRight,latTop, zoom, getString(R.string.open_weather_maps_app_id));

        call.subscribeOn(Schedulers.io()) // thread for execution
                .observeOn(AndroidSchedulers.mainThread()) // observe in main thread
                .subscribe(subscriber);
    }

    private class getWeatherDataTask extends AsyncTask<Double, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Double... params) {
            JSONObject data = RemoteFetch.getJsonForArea(getBaseContext(), params[0],params[1],params[2],params[3],params[4]);
            return data;
        }

        @Override
        protected void onPostExecute (JSONObject result) {
            if (result == null) Toast.makeText(getBaseContext(), "Place not found", Toast.LENGTH_LONG).show();
            else {
                mForecastArea = ForecastArea.parseJSON(result.toString());
            }
            if (mForecastArea!=null) {
                mDisplayText.setText( Integer.toString(mForecastArea.getList().size()) );
                drawMarkers(mForecastArea.getList());
            }
        }

    }

    private void drawMarkers (List<ForecastLoc> listForecasts) {

        //List<ForecastLoc> listForecasts = mForecastArea.getList();

        for(ForecastLoc forecast: listForecasts) {

            LatLng pos = new LatLng(forecast.getCoord().getLat() , forecast.getCoord().getLon());
            int icon_num = Utilities.getIconResourceForWeatherCondition(forecast.getWeather().get(0).getId());

            Log.d("MAPNot", forecast.getName() + " " + forecast.getCoord().getLat() + " " + forecast.getCoord().getLon());

            googleMap.addMarker(new MarkerOptions().position(pos).title("...")
                    .icon(BitmapDescriptorFactory.fromResource( icon_num )));

        }
    }

}
