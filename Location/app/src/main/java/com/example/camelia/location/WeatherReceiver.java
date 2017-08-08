package com.example.camelia.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeatherReceiver extends BroadcastReceiver {
    public WeatherReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("suntem in receiver");
        //double[] coordinates = intent.getDoubleArrayExtra("coordinates");
        System.out.println("am luat coordonatele");
        Intent intent2 = new Intent(context, CurrentWeatherIntentService.class);
        //intent2.putExtra("coordinates", coordinates);
        System.out.println("inainte sa incepem serviciul");
        context.startService(intent2);
    }
}