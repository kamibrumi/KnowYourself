package com.example.camelia.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeatherReceiver extends BroadcastReceiver {
    public WeatherReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        Intent intent2 = new Intent(context, CurrentWeatherIntentService.class);
        context.startService(intent2);
    }
}