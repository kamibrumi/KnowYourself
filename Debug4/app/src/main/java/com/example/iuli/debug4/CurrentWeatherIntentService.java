package com.example.iuli.debug4;


import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class CurrentWeatherIntentService extends IntentService{
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    int dayOfMonth, month, minute, hour, startHour;

    public CurrentWeatherIntentService() {
        super("CurrentWeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
        Calendar calendar = GregorianCalendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        startHour = this.getResources().getInteger(R.integer.startHour);


        if (hour >= 9 && hour < startHour) {
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

            String currentDateId = String.valueOf(dayOfMonth) + String.valueOf(month); //we use it to overwrite yesterday's currentWeather file (see writeToFile method)
            currentTemp = currentTemp - 273.15; //in celsius
            writeToFile(currentDateId + " " + String.valueOf(currentTemp), this); //we converted kelvin to celsius

            System.out.println("currentWeather.txt: " + readFromFile(this));

            //average_temp = average_temp / nrOfHours; // kelvin
            //average_temp = average_temp - 273.15; //celsius
        }

    }

    private void writeToFile(String data, Context context) {
        try {
            String fileContent = readFromFile(this);
            String currentDateId = String.valueOf(dayOfMonth) + String.valueOf(month);
            OutputStreamWriter outputStreamWriter = null;
            if (fileContent.contains(currentDateId))  outputStreamWriter = new OutputStreamWriter(context.openFileOutput("currentWeather.txt", Context.MODE_APPEND)); //TODO nu face un contains, ca nu cumva sa se dea o temperatura care contine id-ul
            else outputStreamWriter = new OutputStreamWriter(context.openFileOutput("currentWeather.txt", Context.MODE_PRIVATE));

            outputStreamWriter.write(" " + data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("currentWeather.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
