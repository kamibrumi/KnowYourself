package com.example.camelia.location;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Location location;
    double latitude;
    double longitude;
    TextView latTv, longTv, tv;
    LocationManager locationManager;
    LocationListener locationListener;
    boolean isNetworkEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTv = (TextView) findViewById(R.id.latitude);
        longTv = (TextView) findViewById(R.id.longitude);
        tv = (TextView) findViewById(R.id.tv);
        //Intent i = new Intent(getApplicationContext(), LocationService.class);
        //startService(i);



    }

    @Override
    protected void onResume() {
        super.onResume();
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                System.out.println("LOCATION CHANGED");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                System.out.println("PROVIDER ENABLED!");
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (!isNetworkEnabled) {
            System.out.println("SE VA CREA DIALOGUL");
            //final Intent showDialogIntent = new Intent(getApplicationContext(), LocationDialog.class);
            //showDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(showDialogIntent);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Enable your location for a while, pls.");
            dialog.setTitle("Location Needed");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.show();

            System.out.println("S-A ARATAT DIALOGUL");

            Looper.prepare();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isNetworkEnabled) {
                        synchronized (this) {
                            try {
                                wait(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("waitin'");
                            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                    if (isNetworkEnabled) {
                        System.out.println("NETWORK ENABLED");

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                        }

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                0,
                                0, locationListener);

                        if (locationManager != null) {
                            System.out.println("LOCATION MANAGER NOT NULL");
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            System.out.println("DUPA GET LAST KNOWN LOCATION SI INAINTE DE LOCATION NOT NULL");
                            if (location != null) {
                                System.out.println("LOCATION NOT NULL");

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                System.out.println("LAT AND LON: " + latitude + " " + longitude);
                            } else System.out.println("LOCATION NULL");
                        }
                    }
                }
            });
            thread.start();

        }


    }
    /*
    private void makeUseOfNewLocation(Location location) throws IOException {
        System.out.println("LOCATION CHANGED");
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
    } */


}
