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
        System.out.println("WEATHER RECEIVEEEEEER");
        // TODO: 20/08/17 daca se schimba situatia la wi-fi nu se verifica daca s-au luat date sau nu la stripul actual 
    }
}