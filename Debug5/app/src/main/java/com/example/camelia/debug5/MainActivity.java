package com.example.camelia.debug5;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    TextView raspuns;
    Button button_great;
    Button button_naspa;
    TextView question;
    TextView debug; //TODO: elimina-l cand termini cu el
    Boolean answered;
    int startHour, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        raspuns = (TextView) findViewById(R.id.raspuns);
        button_great = (Button) findViewById(R.id.good);
        button_naspa = (Button) findViewById(R.id.bad);
        question = (TextView) findViewById(R.id.intrebare);
        debug = (TextView) findViewById(R.id.debug);

        int unixCurrentTime = (int) (System.currentTimeMillis() / 1000L);
        int unixStartTime = unixCurrentTime - 12*60*60;
        //System.out.println("unixStartTime: " + String.valueOf(unixStartTime));
        //System.out.println("unixCurrentTime: " + String.valueOf(unixCurrentTime));

        button_great.setVisibility(View.GONE);
        button_naspa.setVisibility(View.GONE);
        question.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        //WE LAUNCH THE SERVICE THAT WILL RETRIEVE THE WEATHER DATA
        Intent weatherIntent = new Intent(this,WeatherReceiver.class);
        PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                (this, 1, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            //System.out.println("WEATHER PENDING INTENT TO SEND");
            weatherPendingIntent.send(this, 0, weatherIntent);
            //System.out.println("WEATHER PENDING INTENT SENT");
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        AlarmManager alarmManager1 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                1000 * 3 * 60 * 60, weatherPendingIntent); //TODO put frequency of currentWeather data (current every 3h)

        Calendar calendar = GregorianCalendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);


        startHour = this.getResources().getInteger(R.integer.startHour);
        raspuns.setText("Asteptam raspunsul dumneavoastra la orele " + startHour + ".");
        if (hour < startHour) {
            writeToFile(getString(R.string.answeredFile), String.valueOf(false), this);
        } else {
            String answeredContent = null;
            try {
                answeredContent = readFromFile(getString(R.string.answeredFile), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (answeredContent == null) answered = false;
            answered = Boolean.valueOf(answeredContent); //when the user opens the aplication for the first time after the star hour --> answer == null
            //System.out.println("ANSWERED VALUE: " + String.valueOf(answered));
            if (!answered) {

                //WE LAUNCH THE NOTIFICATION
                Intent notifyIntent = new Intent(this,MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1000 * startHour * 60 * 60,
                        1000 * 24 * 60 * 60, pendingIntent);

                button_great.setVisibility(View.VISIBLE);
                button_naspa.setVisibility(View.VISIBLE);
                question.setVisibility(View.VISIBLE);
                raspuns.setText("");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        raspuns.setText("Va multumim de raspuns, butoanele au sa ramana inactive pana la " + startHour +"h din ziua urmatoare.");
        button_great.setVisibility(View.GONE);
        button_naspa.setVisibility(View.GONE);
        question.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void writeToFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            if (fileName == getString(R.string.answeredFile)) outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
/*
    private void writeToFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter;
            if (fileName == getString(R.string.dataFile)) outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            else outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    } */

    private String readFromFile(String fileName, Context context) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

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
            //File yourFile = new File(fileName);
            //yourFile.createNewFile(); // if file already exists will do nothing
            //readFromFile(fileName, context);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void sendMessage(View view) throws IOException {
        if (!answered) {
            Button button = (Button) view;
            //System.out.println("1111111111111111111111");
            String howWasYourDay = button.getText().toString();
            //System.out.println("22222222222222222222222222");

            //System.out.println("33333333333333333");
            //we start the response activity
            Intent intent = new Intent(this, ResponseActivity.class);
            //System.out.println("444444444444444444444444444444444");
            intent.putExtra("how", howWasYourDay);
            //System.out.println("55555555555555555555555");
            startActivity(intent);


        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}