package com.example.camelia.debug6;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
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

public class MainActivity extends AppCompatActivity {
    Button commitB;
    ProgressBar pB;
    boolean isNetworkEnabled;
    LocationManager locationManager;
    Location location;
    Double latitude, longitude;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commitB = (Button) findViewById(R.id.commit);
        pB = (ProgressBar) findViewById(R.id.progressBar);

        //check whether the app is opened for the very first time. if the file "firstTime" do not exist, then the application is opened for the first time and we create it.
        if (Boolean.valueOf(readFromInternalFile(getString(R.string.firstTime)))){
            writeToInternalFile(getString(R.string.firstTime), "false", this);
            getLocation();
            System.out.println("APPLICATION FOR THE FIRST TIME");

        }

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int hourLaunchService = currentHour + 3 - (currentHour%3)%24;

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),hourLaunchService, 30);
        long millisLaunchService = cal.getTimeInMillis();

        //WE LAUNCH THE SERVICE THAT WILL RETRIEVE THE WEATHER DATA
        Intent weatherIntent = new Intent(getApplicationContext(), WeatherReceiver.class);
        PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                (getApplicationContext(), 1, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager1 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, millisLaunchService,
                1000 * 3 * 60 * 60, weatherPendingIntent); //frequency of currentWeather data is 3h from the the start of the next strip
    }

    @Override
    public void onResume() {
        super.onResume();
        String lastStripCollectedData = null;
        try {
            lastStripCollectedData = readFromExternalFile(getString(R.string.currentDataStrip));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        int currentStrip = calendar.get(Calendar.HOUR_OF_DAY)/3;
        if (lastStripCollectedData == null || currentStrip != Integer.parseInt(lastStripCollectedData)) {
            Intent i = new Intent(getApplicationContext(), WeatherReceiver.class);
            startService(i);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void writeToInternalFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeToExternalFile(String filename, String data, Boolean append) {
        /*
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, "data.txt"); */
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, filename);
        try {
            FileOutputStream fos = new FileOutputStream(file, append);
            byte[] strb = data.getBytes();
            for(int i = 0; i < strb.length; ++i) {
                fos.write(strb[i]);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileStreamsTest: " + e);
        } catch (IOException e) {
            System.err.println("FileStreamsTest: " + e);
        }
    }


    //this method contains a modification: it has another return to detect wether a file is null or not (here **)
    public String readFromExternalFile(String filename) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, filename);
        //get InputStream of a file
        if (file.exists()) {
            InputStream is = new FileInputStream(file);
            String strContent;

                /*
                 * There are several way to convert InputStream to String. First is using
                 * BufferedReader as given below.
                 */

            //Create BufferedReader object
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sbfFileContents = new StringBuffer();
            String line = null;

            if (bReader.readLine() == null) return null; // here **
            //read file line by line
            while( (line = bReader.readLine()) != null){
                sbfFileContents.append(line);
            }

            //finally convert StringBuffer object to String!
            strContent = sbfFileContents.toString();

                /*
                 * Second and one liner approach is to use Scanner class. This is only supported
                 * in Java 1.5 and higher version.
                 */

            //strContent = new Scanner(is).useDelimiter("\\A").next();
            return strContent;
        }
        return null;
    }

    public void commit(View view) {
        if (isNetworkAvailable()) {
            //writeToInternalFile(getString(R.string.isFromMain), "true", this); // TODO: 15/08/17 make it false when in response activity
            int howWasYourDay = pB.getProgress();
            System.out.println("how was my day------------> " + howWasYourDay);
            Intent intent = new Intent(this, ResponseActivity.class);
            intent.putExtra("how", String.valueOf(howWasYourDay));
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private String readFromInternalFile(String fileName) {

        String ret = "";

        try {
            System.out.println("000000000000000000000000");
            InputStream inputStream = this.openFileInput(fileName);
            System.out.println("11111111111111111111111111111111");


            if ( inputStream != null ) {
                System.out.println("222222222222222222222222");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                System.out.println("3333333333333333333333");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                System.out.println("444444444444444444444444444");
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

    public void getLocation(){
        System.out.println("A INTRAT IN ISLOCATION");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //boolean gps_enabled = false;
        //boolean network_enabled = false;
        System.out.println("");
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                System.out.println("ON LOCATION CHANGED");
                locationManager.removeUpdates(this);

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
            System.out.println("NETWORK ENABLED");

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, locationListener);

            if (locationManager != null) {
                System.out.println("LOCATION MANAGER NOT NULL");
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    System.out.println("LOCATION NOT NULL");

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    System.out.println("LAT AND LON: " + latitude + " " + longitude);

                    writeToExternalFile(getString(R.string.idLatLonFile),latitude + " " + longitude, false);
                    Intent i = new Intent(getApplicationContext(), WeatherReceiver.class);
                    startService(i);

                    /*
                    System.out.println("SE SETEAZA ALARMA");
                    //WE LAUNCH THE SERVICE THAT WILL RETRIEVE THE WEATHER DATA
                    Intent weatherIntent = new Intent(getApplicationContext(), WeatherReceiver.class);
                    PendingIntent weatherPendingIntent = PendingIntent.getBroadcast
                            (getApplicationContext(), 1, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager1 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                            1000 * 3 * 60 * 60, weatherPendingIntent); //TODO put frequency of currentWeather data (current every 3h) //1000 * 3 * 60 * 60
                    try {
                        System.out.println("CONTINUTUL LUI IDLATLON: " + readFromExternalFile(getString(R.string.idLatLonFile)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } */
                }
            }

        } else {
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
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }
}