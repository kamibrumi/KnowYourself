package com.example.camelia.location;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    boolean isNetworkEnabled = false;
    Location location;
    double latitude;
    double longitude;
    TextView latTv, longTv, tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //WE LAUNCH THE SERVICE THAT WILL RETRIEVE THE WEATHER DATA
        Intent weatherIntent = new Intent(this,WeatherReceiver.class);
        PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                (this, 1, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            System.out.println("inainte de a trimite pending intent");
            weatherPendingIntent.send(this, 0, weatherIntent);
            System.out.println("dupa ce am trimis");
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        AlarmManager alarmManager1 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                1000 * 2, weatherPendingIntent);
        System.out.println("am setat alarma!!");

        latTv = (TextView) findViewById(R.id.latitude);
        longTv = (TextView) findViewById(R.id.longitude);
        tv = (TextView) findViewById(R.id.tv);

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                try {
                    makeUseOfNewLocation(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Toast toast = Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT);
            toast.show();
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }

        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, locationListener);

            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

        } else {
            Toast toast = Toast.makeText(this, "Open your location!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void makeUseOfNewLocation(Location location) throws IOException {
        latTv.setText(String.valueOf(getLatitude()));
        longTv.setText(String.valueOf(getLongitude()));
        System.out.println("inainte de a declara coordinates");
        double[] coordinates = new double[]{getLatitude(), getLongitude()};
        System.out.println("dupa ce l-am declarat");

    }

    public double getLatitude() {
        if(location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if(location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }


}
