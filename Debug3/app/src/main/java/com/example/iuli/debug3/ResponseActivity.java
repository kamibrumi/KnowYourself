package com.example.iuli.debug3;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class ResponseActivity extends AppCompatActivity {
    private static String URL = "http://api.openweathermap.org/data/2.5/forecast?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    String gb;
    private final static String serverIP = "192.168.1.225";
    private final static int serverPort = 5037; //TODO NU STIU CARE II PORTUL CORECT!?
    private final static String fileOutput =  "data.txt"; //"C:\\testout.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        TextView response = (TextView) findViewById(R.id.response);
        response.setText("Thanks for your answer. We wait for you next day at " + this.getResources().getInteger(R.integer.startHour) + "h.");

        Intent intent = this.getIntent();
        gb = intent.getStringExtra("how");

        //we cancel the notification
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(123);
    }

    //we get the temperature and we write in the file day + Good/Bad + temperature
    @Override
    public void onResume() {
        super.onResume();
        String weatherData = null;
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
                    average_temp += main.getDouble("temp");
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

        writeToFile("answered.txt", String.valueOf(true), this);

        Calendar calendar = GregorianCalendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        writeToFile("data.txt", day + " " + gb + " " + average_temp + '\n', getApplicationContext());
    }

    private void writeToFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter;
            if (fileName == "data.txt") outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            else outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        new TCPClient().execute(fileOutput);
    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


}
