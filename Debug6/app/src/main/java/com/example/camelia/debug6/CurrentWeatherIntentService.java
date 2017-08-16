package com.example.camelia.debug6;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrentWeatherIntentService extends IntentService{
    private String URL;
    String cityFile;

    public CurrentWeatherIntentService() {
        super("CurrentWeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("CURRENT WEATHER INTENT");
        if (isNetworkAvailable()) {
            cityFile = null;
            try {
                cityFile = readFromExternalFile(getString(R.string.idLatLonFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("CITYFILE CONTENT: " + cityFile);
            String[] idll = cityFile.split(" ");
            double lat, lon;
            lat = Double.parseDouble(idll[1]);
            lon = Double.parseDouble(idll[2]);

            URL = "http://api.openweathermap.org/data/2.5/find?lat=" + lat + "&lon=" + lon + "&cnt=1&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";

            //System.out.println("am facut calendarul");
            String weatherData = "";
            try {
                weatherData = new getURLData().execute(URL).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            JSONObject obj = null;
            try {
                obj = new JSONObject(weatherData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //System.out.println("am luat json object-ul");

            JSONObject main = null;
            Double currentTemp = 25.;
            Double pressure = 1020.;
            Double humid = 50.;
            Double clouds = 50.;
            Double wind = 5.;
            try {
                main = obj.getJSONArray("list").getJSONObject(0).getJSONObject("main");
                currentTemp = main.getDouble("temp");
                pressure = main.getDouble("pressure");
                humid = main.getInt("humidity") * 1.; // convert it from int to double
                clouds = obj.getJSONArray("list").getJSONObject(0).getJSONObject("clouds").getInt("all") * 1.;
                wind = obj.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getDouble("speed");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            currentTemp = currentTemp - 273.15; //in celsius

            writeToFile(getString(R.string.xsFile), String.valueOf(currentTemp) + " " + String.valueOf(pressure) + " " + String.valueOf(humid) + " " + String.valueOf(clouds) + " " + String.valueOf(wind) + " final", this); //we converted kelvin to celsius   //System.out.println("s-a terminat intentul");

        }
        try {
            System.out.println("current weather= " + readFromFile(getString(R.string.xsFile), this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("CURRENT WEATHER INTENT FIN");
    }

    private void writeToFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromExternalFile(String filename) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, filename);
        //get InputStream of a file
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

    private String readFromFile(String filename, Context context) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

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
            //Log.e("login activity", "File not found: " + e.toString());
            File yourFile = new File(filename);
            yourFile.createNewFile(); // if file already exists will do nothing
            readFromFile(filename, context);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
