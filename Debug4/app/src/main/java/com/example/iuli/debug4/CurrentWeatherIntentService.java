package com.example.iuli.debug4;


import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class CurrentWeatherIntentService extends IntentService{
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";

    public CurrentWeatherIntentService() {
        super("CurrentWeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 0 && hour <= 21) {
            String weatherData = null;
            try {
                weatherData = new getURLData().execute(URL).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            double average_temp = 0;
            int nrOfHours = 0;
            JSONObject obj = null;
            try {
                obj = new JSONObject(weatherData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            double currentTemp = 25; //initialized because I had problems with String.valueOf() (variable not declared)
            try {
                currentTemp = obj.getJSONObject("main").getDouble("temp");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String.valueOf(currentTemp);


            average_temp = average_temp / nrOfHours; // kelvin
            average_temp = average_temp - 273.15; //celsius
        }

    }

}
