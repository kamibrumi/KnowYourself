package com.example.camelia.debug6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class WeatherReceiver extends BroadcastReceiver {
    public WeatherReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String lastStripCollectedData = null;
        try {
            lastStripCollectedData = WriteAndReadFile.readFromExternalFile("currentDataStrip.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        int currentStrip = calendar.get(Calendar.HOUR_OF_DAY)/3;
        /*
        if (lastStripCollectedData == null || lastStripCollectedData == "" || currentStrip != Integer.parseInt(lastStripCollectedData)) {
            Intent i = new Intent(context, CurrentWeatherIntentService.class);
            context.startService(i);
        }*/
        Intent i = new Intent(context, CurrentWeatherIntentService.class);
        context.startService(i);
    }
}