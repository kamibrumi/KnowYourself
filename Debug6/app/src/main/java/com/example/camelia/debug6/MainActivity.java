package com.example.camelia.debug6;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button commitB;
    ProgressBar pB;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    int currentHour;
    private int REQUEST_CODE = 1;// the one that identifies the pending intent that sets the collectWeatherAlarm


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commitB = (Button) findViewById(R.id.commit);
        pB = (ProgressBar) findViewById(R.id.progressBar);

        //check whether the app is opened for the very first time. if the file "firstTime" do not exist, then the application is opened for the first time and we create it.
        //if (Boolean.valueOf(readFromInternalFile(getString(R.string.firstTime)))){

        if (Boolean.valueOf(readFromInternalFile(getString(R.string.firstTime)))) { // the function readFromInternalFile returns a true if it catches an error if the file firstTime does not exist
            int currentStrip = getCurrentStrip();
            WriteAndReadFile.writeToExternalFile(getString(R.string.currentDataStrip), String.valueOf(currentStrip), false);
            WriteAndReadFile.writeToInternalFile(getString(R.string.firstTime), "false", this, Context.MODE_PRIVATE);
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isNetworkEnabled) {
                displayLocationSettingsRequest(this);
            } else {
                Intent i = new Intent(this, LocationService.class);
                this.startService(i);
            }

        }

        //WE GET THE LOCATION---------------------------------------------
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                //String message = intent.getStringExtra("Status");
                Bundle b = intent.getBundleExtra("Location");
                Location lastKnownLoc = (Location) b.getParcelable("Location");


                if (lastKnownLoc != null) { //TODO I think is != not ==, we get the location if the location is not null, right?
                    WriteAndReadFile.writeToExternalFile(getString(R.string.idLatLonFile), lastKnownLoc.getLatitude() + " " + lastKnownLoc.getLongitude(), false);
                    System.out.println("SE IA CURRENT WEATHER MULTUMITA LA BROADCAST RECEIVER");
                    collectCurrentWeather();
                    Intent weatherIntent = new Intent(getApplicationContext(), WeatherReceiver.class);
                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),REQUEST_CODE,weatherIntent,PendingIntent.FLAG_NO_CREATE);
                    if (pendingIntent==null) setCollectWeatherRepeatingAlarm();
                }
                //tvStatus.setText(message);
                // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
    }

    private void setCollectWeatherRepeatingAlarm() {
        // we calculate the hour when we are going to launch the alarm to retrieve the weather data!
        Calendar calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int hourLaunchService = currentHour + 3 - (currentHour%3)%24;

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),hourLaunchService, 0); //FIRST HOUR OF THE NEXT STRIP
        long millisLaunchService = cal.getTimeInMillis();
        System.out.println("se seteaza ALAAAAAARM!");
        //WE LAUNCH THE SERVICE THAT WILL RETRIEVE THE WEATHER DATA
        Intent weatherIntent = new Intent(getApplicationContext(), WeatherReceiver.class); //if it gives an error, comment it!
        PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                (getApplicationContext(), 1, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT); //1 is the REQUEST_CODE, that means it identifies this particular pending intent so you can see whether an alarm was set with this pendinf intent
        AlarmManager alarmManager1 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        //alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, millisLaunchService, // TODO: 17/10/17 UNCOMMMENT THIS AND NEXT LINE
        //        1000 * 3 * 60 * 60, weatherPendingIntent); //frequency of currentWeather data is 3h from the the start of the next strip

        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, weatherPendingIntent);
    }

    private void collectCurrentWeather() {
        String lastStripCollectedData = null;
        try {
            lastStripCollectedData = WriteAndReadFile.readFromExternalFile(getString(R.string.currentDataStrip));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int currentStrip = getCurrentStrip();
        System.out.println("AR TREBUI SA SE LANSEZE SERVICIUL DE CURRENT WEATHER");
        Intent i = new Intent(getApplicationContext(), CurrentWeatherIntentService.class);
        startService(i);
    }

    public int getCurrentStrip() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.HOUR_OF_DAY)/3);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void commit(View view) {
        int howWasYourDay = pB.getProgress();

        String predictionStrip = readFromInternalFile(getString(R.string.predictionStripFile));
        if (Objects.equals(predictionStrip, String.valueOf(currentHour / 3))) {
            Intent intent = new Intent(this, ResponseActivity.class);
            intent.putExtra("how", String.valueOf(howWasYourDay));
            startActivity(intent);
        } else {
            if (isNetworkAvailable()) {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(isNetworkEnabled) {
                    Intent locationService = new Intent(this, LocationService.class);
                    startService(locationService);
                    //writeToInternalFile(getString(R.string.isFromMain), "true", this); // TODO: 15/08/17 make it false when in response activity
                    System.out.println("how was my day------------> " + howWasYourDay);
                    Intent intent = new Intent(this, ResponseActivity.class);
                    intent.putExtra("how", String.valueOf(howWasYourDay));
                    startActivity(intent);
                } else displayLocationSettingsRequest(this);
            } else {
                Toast toast = Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private String readFromInternalFile(String fileName) { // TODO: 21/08/17 this method is different than the readFromInternalFile from the WriteAndReadFile class 

        String ret = "";

        try {
            InputStream inputStream = this.openFileInput(fileName);


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
            if (fileName == getString(R.string.firstTime)) return "true";

        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            if (fileName == getString(R.string.firstTime)) return "true";
        }

        return ret;
    }

    public void incrementProgressBar (View view) {
        pB.incrementProgressBy(5);
    }

    public void decrementProgressBar (View view) {
        pB.incrementProgressBy(-5);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("SUCCESS", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("RESOLUTION_REQUIRED", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS); // TODO: 23/08/17 if there is an error this may be the cause! (the constant REQUEST_CHECK_SETTINGS)
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("SendIntentException", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("CHANGE_UNAVAILABLE", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
}