package com.example.camelia.debug6;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseActivity extends AppCompatActivity {
    //private static String URL = "http://api.openweathermap.org/data/2.5/forecast?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    private String URL;
    String gb;
    private TCPClient mTcpClient;
    TextView loadMessage, dayTv;
    ArrayList<Double> tempsArray, pressuresArray, humiditiesArray, cloudsArray, windArray;
    ProgressBar loading;
    boolean isNetworkEnabled = false;
    Location location;
    double latitude;
    double longitude;
    long cityId;
    String cityName;
    boolean isFromMain;
    int day, dayOfMonth, month, year, hourOfDay, durationInStrips; //day is the the day of week


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        loadMessage = (TextView) findViewById(R.id.loadMessage);
        loading = (ProgressBar) findViewById(R.id.loading);
        dayTv = (TextView) findViewById(R.id.dayTv);

        Intent intent = this.getIntent();
        gb = intent.getStringExtra("how");
        System.out.println("on CReate INAINTE DE A INCEPE AFACEREA CU LOCATIA");

    }

    //we get the temperature and we write in the file day + Good/Bad + temperature
    @Override
    public void onResume() {
        super.onResume();

        Calendar calendar = GregorianCalendar.getInstance();
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        //we cancel the notification
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(123);

        isFromMain = Boolean.valueOf(readFromInternalFile(getString(R.string.isFromMain)));
        writeToInternalFile(getString(R.string.isFromMain), String.valueOf(false));
        System.out.println("onResume -- INAINTE DE A INCEPE AFACEREA CU LOCATIA");

        if (!isFromMain) { //// TODO: 14/08/17 if(xsFile is empty) --> arata continutul la prediction file... Astept sa imi deie Iuli formatul la raspunsul ca sa bag in listView.

            dayTv.setText("Tomorrow will be");
            dayTv.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            String prediction = readFromInternalFile(getString(R.string.predictionFile));
            String[] dayAndPrediction = prediction.split(" ");
            Double percentage = Double.parseDouble(dayAndPrediction[1]);
            loadMessage.setText(new DecimalFormat("#0.0").format(percentage) + "% GOOD");
            loadMessage.setTextSize(40);

        }
        else {
            // Acquire a reference to the syfalsestem Location Manager
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            System.out.println("LOCATION MANAGER DECLARED");
            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    System.out.println("ON LOCATION CHANGED");
                    writeDataAndPredict();
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
                System.out.println("LOCATION ENCABLED");

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0, locationListener);

                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    System.out.println("LOCATION MANAGER NOT NULL");

                    if (location != null) {
                        System.out.println("LOCATION NOT NULL");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }

            } else {
                Toast toast = Toast.makeText(this, "Open your location!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }



    public void writeDataAndPredict() {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                System.out.println("WE WRITE DATA");

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                URL = "http://api.openweathermap.org/data/2.5/find?lat=" + latitude + "&lon=" + longitude + "&cnt=1&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
                //we use the current data link to retrieve the id of the city and use it to calculate the link of the forecast
                String weatherData = null;
                try {
                    weatherData = new getURLData().execute(URL).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                JSONObject obj = null;
                try {
                    obj = new JSONObject(weatherData);
                    cityId = obj.getJSONArray("list").getJSONObject(0).getLong("id");
                    cityName = obj.getJSONArray("list").getJSONObject(0).getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //we put underscore in between the words of the cityName
                String[] cn = cityName.split(" ");
                String res = "";
                for (int i = 0; i < cn.length-1; ++i) {
                    res += cn[i] + "_";
                }
                res += cn[cn.length - 1];
                cityName = res;

                writeToExternalFile(getString(R.string.idLatLonFile), cityId + " " + latitude + " " + longitude, false);

                URL = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityId + "&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";

                weatherData = null;
                try {
                    weatherData = new getURLData().execute(URL).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                obj = null;
                try {
                    obj = new JSONObject(weatherData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray arr = null;
                try {
                    arr = obj.getJSONArray("list");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //we start calculating the average and standard deviation of the future
                // TEMPERATURE
                double futureSumTemp = 0;
                double futureAverageTemp = 0;

                ArrayList<Double> futureTemps = new ArrayList<Double>();

                // PRESSURE
                double futureSumPressure = 0;
                double futureAveragePressure = 0;

                ArrayList<Double> futurePressures = new ArrayList<Double>();

                // HUMIDITY
                double futureSumHumidity = 0;
                double futureAverageHumidity = 0;

                ArrayList<Double> futureHumidities = new ArrayList<Double>();

                // CLOUDS
                double futureSumClouds = 0;
                double futureAverageClouds = 0;

                ArrayList<Double> futureClouds = new ArrayList<Double>();

                // WIND
                double futureSumWind = 0;
                double futureAverageWind = 0;

                ArrayList<Double> futureWind = new ArrayList<Double>();

                for (int i = 0; i < 24; i++) {
                    String dt_compare = null;
                    try {
                        dt_compare = arr.getJSONObject(i).getString("dt_txt");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Pattern hourP = Pattern.compile("[0-9]*:[0-9]*:[0-9]*");
                    Matcher hourM = hourP.matcher(dt_compare);
                    if (hourM.find()) {
                        //System.out.println("ORA: " + hourM.group());
                        int hour = Integer.parseInt(hourM.group().substring(0, 2)); // we get the hour int
                        int futureStripId = hour/3;
                        int futureDurationInStrips = i + 1;
                        JSONObject main = null;
                        Double t = 25.;
                        Double pressure = 1020.;
                        Double humid = 50.;
                        Double clouds = 50.;
                        Double wind = 5.;
                        try {
                            main = arr.getJSONObject(i).getJSONObject("main");
                            t = main.getDouble("temp");
                            pressure = main.getDouble("pressure");
                            humid = main.getInt("humidity") * 1.; // convert it from int to double
                            clouds = arr.getJSONObject(i).getJSONObject("clouds").getInt("all") * 1.;
                            wind = arr.getJSONObject(i).getJSONObject("wind").getDouble("speed");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // TEMPERATURE
                        futureTemps.add(t);
                        futureSumTemp += t;
                        futureAverageTemp = futureSumTemp/futureDurationInStrips;
                        double futureStdDevTemp = getStdDev(getVariance(futureAverageTemp, futureTemps));

                        // PRESSURE
                        futurePressures.add(pressure);
                        futureSumPressure += pressure;
                        futureAveragePressure = futureSumPressure/futureDurationInStrips;
                        double futureStdDevPressure = getStdDev(getVariance(futureAveragePressure, futurePressures));

                        // HUMIDITY
                        futureHumidities.add(humid);
                        futureSumHumidity += humid;
                        futureAverageHumidity = futureSumHumidity/futureDurationInStrips;
                        double futureStdDevHumidity = getStdDev(getVariance(futureAverageHumidity, futureHumidities));

                        // CLOUDS
                        futureClouds.add(clouds);
                        futureSumClouds += clouds;
                        futureAverageClouds = futureSumClouds/futureDurationInStrips;
                        double futureStdDevClouds = getStdDev(getVariance(futureAverageClouds, futureClouds));

                        // WIND
                        futureWind.add(wind);
                        futureSumWind += wind;
                        futureAverageWind = futureSumWind/futureDurationInStrips;
                        double futureStdDevWind = getStdDev(getVariance(futureAverageWind, futureWind));

                        writeToExternalFile(getString(R.string.predictionDataFile), day + " " + cityName //// TODO: 14/08/17 DAY!!!
                                + " " + futureAverageTemp + " " + futureStdDevTemp
                                + " " + futureAveragePressure + " " + futureStdDevPressure
                                + " " + futureAverageHumidity * 1. + " " + futureStdDevHumidity * 1.
                                + " " + futureAverageClouds + " " + futureStdDevClouds
                                + " " + futureAverageWind + " " + futureStdDevWind + " "
                                + " " + futureStripId + " " + futureDurationInStrips + " final", true); // TODO: 14/08/17 DURATION IN STRIPS!!

                    }
                }

                Calendar calendar = GregorianCalendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_WEEK);


                //we calculate averages of the CURRENT strips of hours
                getArrayLists();

                // CURRENT TEMP
                double averageTemp = getAverage(tempsArray);
                double stdDevTemp = getStdDev(getVariance(averageTemp, tempsArray));

                // CURRENT PRESSURE
                double averagePressure = getAverage(pressuresArray);
                double stdDevPressure = getStdDev(getVariance(averagePressure, pressuresArray));

                // CURRENT HUMIDITY
                double averageHumidity = getAverage(humiditiesArray);
                double stdDevHumidity = getStdDev(getVariance(averageHumidity, humiditiesArray));

                // CURRENT CLOUDS
                double averageClouds = getAverage(cloudsArray);
                double stdDevClouds = getStdDev(getVariance(averageClouds, cloudsArray));

                // CURRENT WIND
                double averageWind = getAverage(windArray);
                double stdDevWind = getStdDev(getVariance(averageWind, windArray));

                int stripId = hourOfDay/3;

                writeToExternalFile(getString(R.string.currentDataFile), day + " " + gb + " " + cityName
                        + " " + averageTemp + " " + stdDevTemp
                        + " " + averagePressure + " " + stdDevPressure
                        + " " + averageHumidity + " " + stdDevHumidity
                        + " " + averageClouds + " " + stdDevClouds
                        + " " + averageWind + " " + stdDevWind + " " + stripId + " " + durationInStrips + " final", true);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // connect to the server
        new connectTask().execute("commit");
        System.out.println("FIRST COMMIT");
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        mTcpClient.stopClient();
                        // connect to the server
                        new connectTask().execute("predict");
                        System.out.println("PREDICT???");
                    }
                },
                5000
        );
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        mTcpClient.stopClient();
                    }
                },
                8000
        );
    }

    private void writeToExternalFile(String filename, String data, Boolean append) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, filename);
        try {
            FileOutputStream fos = new FileOutputStream(file, append); //true because we append
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

    private void writeToInternalFile(String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            System.out.println("message of do in backGROUND" + message[0]);
            if (message[0] == "commit") {
                try {
                    mTcpClient.run(readFromExternalFile(getString(R.string.currentDataFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("AM COMITEJAT");
            }
            else {
                mTcpClient.run("");
                System.out.println("ACUM PREZICEM");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] != null) writeToInternalFile(getString(R.string.predictionFile),String.valueOf(dayOfMonth) + " " + values[0]);
            writeToInternalFile(getString(R.string.predictionDayFile), dayOfMonth + "-" + month + "-" + year);
            System.out.println("on progress update SE EXECUTA CODUL ASTA!!");

            dayTv.setText("Tomorrow will be");

            System.out.println("DESPUES DE SET TEXT");
            dayTv.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);

            String prediction = readFromInternalFile(getString(R.string.predictionFile));
            String[] dayAndPrediction = prediction.split(" ");
            Double percentage = Double.parseDouble(dayAndPrediction[1]);
            loadMessage.setText(new DecimalFormat("#0.0").format(percentage) + "% GOOD");

            loadMessage.setText(new DecimalFormat("#0.0").format(percentage) + "% GOOD");
            loadMessage.setTextSize(40);

            writeToInternalFile(getString(R.string.xsFile), "");
            System.out.println("STOP CLIENT DIN PROGRESS UPDATE");
            mTcpClient.stopClient();

            //serverResponse.setText(values[0]);
            //arrayList.add(values[0]);
            //we can add the message received from server to a text view
            //return;

        }

        @Override
        protected void onPostExecute(TCPClient result){
            super.onPostExecute(result);
            mTcpClient.stopClient();

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

    private String readFromInternalFile(String fileName) {

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
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void getArrayLists() {
        String currentData = readFromInternalFile(getString(R.string.xsFile));
        String[] data = currentData.split(" final");

        durationInStrips = data.length;

        tempsArray = new ArrayList<>();
        pressuresArray = new ArrayList<>();
        humiditiesArray = new ArrayList<>();
        cloudsArray = new ArrayList<>();
        windArray = new ArrayList<>();
        for (int i = 0; i < data.length; ++i) {
            String[] sarr = data[i].split(" ");
            tempsArray.add(Double.parseDouble(sarr[0]));
            pressuresArray.add(Double.parseDouble(sarr[1]));
            humiditiesArray.add(Double.parseDouble(sarr[2]));
            cloudsArray.add(Double.parseDouble(sarr[3]));
            windArray.add(Double.parseDouble(sarr[4]));
        }
    }



    private double getAverage(ArrayList<Double> arr) {
        Double sum = .0;
        int s = arr.size();
        for(int i = 0; i < s; ++i) {
            sum += arr.get(i);
        }
        //System.out.println("PRINT 11(get average) --------------");
        return sum/s;
    }


    private double getVariance(double average, ArrayList<Double> arr)
    {
        double sum = 0;
        int s = arr.size();
        for(int i = 0; i < s; ++i)
            sum += (arr.get(i)-average)*(arr.get(i)-average);
        //System.out.println("PRINT 12(get variance) --------------");
        return sum/s;
    }

    private double getStdDev(double variance)
    {
        //System.out.println("PRINT 13(get stdvDev) --------------");
        return Math.sqrt(variance);
    }
}
