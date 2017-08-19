package com.example.camelia.debug6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class WeatherReceiver extends BroadcastReceiver {
    public WeatherReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CurrentWeatherIntentService.class);
        context.startService(i);
    }
}