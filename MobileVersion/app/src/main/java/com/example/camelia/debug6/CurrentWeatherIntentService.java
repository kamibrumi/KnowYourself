package com.example.camelia.debug6;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class CurrentWeatherIntentService extends IntentService{
    private String URL;
    String cityFile;

    public CurrentWeatherIntentService() {
        super("CurrentWeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("CURRENT WEATHER INTENT");
        if (isNetworkAvailable()) {
            cityFile = null;
            try {
                cityFile = WriteAndReadFile.readFromExternalFile(getString(R.string.idLatLonFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("CITYFILE CONTENT: " + cityFile);
            String[] idll = cityFile.split(" ");
            double lat, lon;
            lat = Double.parseDouble(idll[0]);
            lon = Double.parseDouble(idll[1]);

            URL = "http://api.openweathermap.org/data/2.5/find?lat=" + lat + "&lon=" + lon + "&cnt=1&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";

            //System.out.println("am facut calendarul");
            String weatherData = "";
            try {
                weatherData = new getURLData().execute(URL).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            JSONObject obj = null;
            try {
                obj = new JSONObject(weatherData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //System.out.println("am luat json object-ul");

            JSONObject main = null;
            //long cityId = 0;
            //String cityName = "name";
            Double currentTemp = 25.;
            Double pressure = 1020.;
            Double humid = 50.;
            Double clouds = 50.;
            Double wind = 5.;
            try {
                //cityId = obj.getJSONArray("list").getJSONObject(0).getLong("id");
                //cityName = obj.getJSONArray("list").getJSONObject(0).getString("name");
                //writeToExternalFile(getString(R.string.idLatLonFile), cityName + " " + cityId + " " + lat + " " + lon, false);
                main = obj.getJSONArray("list").getJSONObject(0).getJSONObject("main");
                currentTemp = main.getDouble("temp");
                pressure = main.getDouble("pressure");
                humid = main.getInt("humidity") * 1.; // convert it from int to double
                clouds = obj.getJSONArray("list").getJSONObject(0).getJSONObject("clouds").getInt("all") * 1.;
                wind = obj.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getDouble("speed");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            currentTemp = currentTemp - 273.15; //in celsius

            Calendar calendar = GregorianCalendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            //writeToFile(getString(R.string.xsFile), String.valueOf(currentTemp) + " " + String.valueOf(pressure) + " " + String.valueOf(humid) + " " + String.valueOf(clouds) + " " + String.valueOf(wind) + " final", this); //we converted kelvin to celsius
            WriteAndReadFile.writeToExternalFile(getString(R.string.xsFile),hour + " " + String.valueOf(currentTemp) + " " + String.valueOf(pressure) + " " + String.valueOf(humid) + " " + String.valueOf(clouds) + " " + String.valueOf(wind) + " final", true); //we converted kelvin to celsius
            try {
                System.out.println("XS.TXT FILE CONTAINS THIS: " + WriteAndReadFile.readFromExternalFile(getString(R.string.xsFile)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            WriteAndReadFile.writeToExternalFile(getString(R.string.currentDataStrip), String.valueOf(hour/3), false);

            Intent notificationIntent = new Intent(this, MyReceiver.class);
            PendingIntent notiPendingIntent = PendingIntent.getBroadcast(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                notiPendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("current weather= " + WriteAndReadFile.readFromExternalFile(getString(R.string.xsFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("CURRENT WEATHER INTENT FIN");
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
