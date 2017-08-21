package com.example.camelia.location;

import android.Manifest;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class LocationService extends IntentService {
    LocationManager locationManager;
    LocationListener locationListener;
    Boolean isNetworkEnabled;
    Double latitude, longitude;
    Location location;
    private Handler dialogHandler;

    public LocationService() {
        super("LocationService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dialogHandler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
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

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                Toast toast = Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT);
                toast.show();
            }

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isNetworkEnabled) {
                System.out.println("SE VA CREA DIALOGUL");
                final Intent showDialogIntent = new Intent(getApplicationContext(), LocationDialog.class);
                showDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(showDialogIntent);
                    }
                });

                System.out.println("S-A ARATAT DIALOGUL");

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
            }

        }

        if (isNetworkEnabled) {
            System.out.println("NETWORK ENABLED");

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, locationListener);

            if (locationManager != null) {
                System.out.println("LOCATION MANAGER NOT NULL");
                //location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                location = getLastKnownLocation();
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

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        System.out.println("number of providers: " + providers.size());
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
            }
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
}
