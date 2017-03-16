package com.example.aawee.weatherdisplay.database;

import android.provider.BaseColumns;

/**
 * Created by Aawee on 14/03/2017.
 */

public class ForecastContract {

    public static final class ForecastLocDB implements BaseColumns {
        public static final String TABLE_NAME = "forecast_loc_table";

        public static final String LOC_NAME_NAME = "location_name";

        public static final String LATITUDE_NAME = "lat";

        public static final String LONGITUDE_NAME = "lon";

        public static final String TEMPERATURE_NAME = "temp";

        public static final String WEATHER_ID_NAME = "weather_id";

        public static final String WEATHER_DESCRIPTION_NAME = "weather_desc";

        public static final String FORECAST_TIME_NAME = "forecast_time";

        public static final String UNIQUE_CONSTRAINT_NAME = "UC_coords";

    }


}
