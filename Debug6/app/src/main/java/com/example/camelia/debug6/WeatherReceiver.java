package com.example.camelia.debug6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class WeatherReceiver extends BroadcastReceiver {
    public WeatherReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String lastStripCollectedData = null;
        try {
            lastStripCollectedData = readFromExternalFile("currentDataStrip.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        int currentStrip = calendar.get(Calendar.HOUR_OF_DAY)/3;
        if (lastStripCollectedData == null || lastStripCollectedData == "" || currentStrip != Integer.parseInt(lastStripCollectedData)) {
            Intent i = new Intent(context, CurrentWeatherIntentService.class);
            context.startService(i);
        }
    }

    public String readFromExternalFile(String filename) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, filename);
        //get InputStream of a file
        if (file.exists()) {
            InputStream is = new FileInputStream(file);
            String strContent = "";

                /*
                 * There are several way to convert InputStream to String. First is using
                 * BufferedReader as given below.
                 */

            //Create BufferedReader object
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sbfFileContents = new StringBuffer();
            String line = null;

            if ((line = bReader.readLine()) == null) return null; // here **
            else {
                sbfFileContents.append(line);
                //read file line by line
                while( (line = bReader.readLine()) != null){
                    sbfFileContents.append(line);
                }
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
}