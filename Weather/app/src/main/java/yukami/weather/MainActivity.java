package yukami.weather;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static String URL = "http://api.openweathermap.org/data/2.5/forecast?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String weatherData = null;
        try {
            weatherData = new getURLData().execute(URL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(weatherData);

        JSONObject obj = null;
        try {
            obj = new JSONObject(weatherData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray arr = null;
        try {
            arr = obj.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Date date=new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dt = dayFormat.format(date); // Start date
        System.out.println("DATA DE ASTAZI ESTE: " + dt);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add
        dt = sdf.format(c.getTime());  // dt is now the new date
        System.out.println("DATA DE MAINE ESTE : " + dt);


        double average_temp = 0;
        int nrOfHours = 0;
        String dt_compare = null;

        for (int i = 0; i < arr.length(); i++) {
            try {
                System.out.println(arr.getJSONObject(i).toString());

                dt_compare = arr.getJSONObject(i).getString("dt_txt");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(dt_compare);
            if (dt_compare.contains(dt)) {
                System.out.println(dt_compare);
                ++nrOfHours;
                System.out.println("COMPTADOR" + nrOfHours);

                JSONObject main = null;
                try {
                    main = arr.getJSONObject(i).getJSONObject("main");
                    System.out.println("MAIN IN STRING: " + main.toString());
                    average_temp += main.getDouble("temp"); //in kelvins
                    System.out.println("temperatura: " + main.getDouble("temp"));
                    System.out.println("temperatura: " + main.getDouble("temp_min"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
        average_temp = average_temp/nrOfHours; // kelvin
        System.out.println("AVERAGE TEMPERATURE OF TOMORROW IS : " + average_temp + " en kelvin");
        average_temp = average_temp-273.15; //celsius

        System.out.println("AVERAGE TEMPERATURE OF TOMORROW IS : " + average_temp + " en celsius");
    }

}
