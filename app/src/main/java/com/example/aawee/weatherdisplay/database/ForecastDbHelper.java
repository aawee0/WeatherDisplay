package com.example.aawee.weatherdisplay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aawee on 14/03/2017.
 */

public class ForecastDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "forecasts.db";

    private static final int DATABASE_VERSION = 3;

    public ForecastDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create table of points
        final String SQL_CREATE_POINT_TABLE = "CREATE TABLE " + ForecastContract.ForecastLocDB.TABLE_NAME +
                " (" + ForecastContract.ForecastLocDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ForecastContract.ForecastLocDB.LOC_NAME_NAME + " TEXT NOT NULL, " +
                ForecastContract.ForecastLocDB.LATITUDE_NAME + " DECIMAL(10,6) NOT NULL, " +
                ForecastContract.ForecastLocDB.LONGITUDE_NAME + " DECIMAL(10,6) NOT NULL, " +
                ForecastContract.ForecastLocDB.TEMPERATURE_NAME + " DECIMAL(4,2) NOT NULL, " +
                ForecastContract.ForecastLocDB.WEATHER_ID_NAME + " INTEGER NOT NULL, " +
                ForecastContract.ForecastLocDB.WEATHER_DESCRIPTION_NAME + " TEXT NOT NULL, " +
                ForecastContract.ForecastLocDB.FORECAST_TIME_NAME + " LONG NOT NULL, " +
                " CONSTRAINT " + ForecastContract.ForecastLocDB.UNIQUE_CONSTRAINT_NAME + " UNIQUE " +
                "(" + ForecastContract.ForecastLocDB.LATITUDE_NAME + "," +
                ForecastContract.ForecastLocDB.LONGITUDE_NAME + ") );";

        sqLiteDatabase.execSQL(SQL_CREATE_POINT_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ForecastContract.ForecastLocDB.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
