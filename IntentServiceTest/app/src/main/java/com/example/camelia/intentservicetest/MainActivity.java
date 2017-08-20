package com.example.camelia.intentservicetest;

import android.content.Intent;
import java.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("INAINTE DE 26");
        Calendar calendar = Calendar.getInstance();
        System.out.println("DUPA 26");
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int hourLaunchService = currentHour + 3 - (currentHour%3)%24;

        Calendar cal = Calendar.getInstance();
        //cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),hourLaunchService, 30);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),18, 07);
        long millisLaunchService = cal.getTimeInMillis();

        //System.out.println("peste 3 h in milisecunde: " + String.valueOf(millisLaunchService));
/*
        //WE LAUNCH THE SERVICE THAT WILL RETRIEVE THE WEATHER DATA
        Intent weatherIntent = new Intent(getApplicationContext(), MyReceiver.class);
        PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                (getApplicationContext(), 1, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager1 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, millisLaunchService,
                1000 * 10, weatherPendingIntent); //TODO put frequency of currentWeather data (current every 3h) */

        Intent i = new Intent(getApplicationContext(), MyIntentService.class);
        startService(i);
    }

    public void startIntentService(View v) {
        Intent intent = new Intent(this, MyIntentService.class);
        startService(intent);
    }
}
