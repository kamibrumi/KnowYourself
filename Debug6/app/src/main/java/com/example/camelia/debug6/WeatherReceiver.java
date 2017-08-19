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
        Intent intent2 = new Intent(context, CurrentWeatherIntentService.class);
        context.startService(intent2);

        /*
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Intent intent2 = new Intent(context, CurrentWeatherIntentService.class);
            context.startService(intent2);
        } else {

            Toast toast = Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT);
            toast.show();
            /*try{

                Intent i=new Intent(context, InternetDialogActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch(Exception e){
                e.printStackTrace();
            }
        } */
    }
}