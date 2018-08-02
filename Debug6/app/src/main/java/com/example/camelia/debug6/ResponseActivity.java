package com.example.camelia.debug6;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import smile.data.parser.DelimitedTextParser;
import smile.data.*;
import smile.RegressionTree;
import java.lang.Math;

public class ResponseActivity extends AppCompatActivity {
    //private static String URL = "http://api.openweathermap.org/data/2.5/forecast?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    private String URL;
    String gb;
    private TCPClient mTcpClient;
    TextView loadMessage, dayTv;
    ArrayList<Double> tempsArray, pressuresArray, humiditiesArray, cloudsArray, windArray;
    ProgressBar loading;
    //String cityName;
    int dayOfWeek, dayOfMonth, month, year, hourOfDay, durationInStrips; //day is the the day of week
    Double latitude, longitude;
    Location location;
    LocationManager locationManager;
    String[] strips;
    Double[] happinessLevels;
    Integer[] imageId;
    int NUMBER_OF_STRIPS_TO_PREDICT = 24;
    ListView list;
    String[] weekDayStrings = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    Double[]  futureTemps, futurePressures, futureHumidities, futureClouds, futureWind;
    Context thisContext;
    String xsFileExample = "15 30.510000000000048 1015.0 70.0 0.0 3.6 final";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        loadMessage = (TextView) findViewById(R.id.loadMessage);
        loading = (ProgressBar) findViewById(R.id.loading);
        dayTv = (TextView) findViewById(R.id.dayTv);
        list =(ListView)findViewById(R.id.list);

        Intent intent = this.getIntent();
        gb = intent.getStringExtra("how");

         thisContext = getApplicationContext();

    }

    //we get the temperature and we write in the file day + Good/Bad + temperature
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        Calendar calendar = GregorianCalendar.getInstance();
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);



        //we cancel the notification
        //NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        //manager.cancel(123);

        //isFromMain = Boolean.valueOf(readFromInternalFile(getString(R.string.isFromMain)));
        //writeToInternalFile(getString(R.string.isFromMain), String.valueOf(false));

        /*
        String xs = null;
        try {
            xs = WriteAndReadFile.readFromExternalFile(getString(R.string.xsFile));
        } catch (IOException e) {
            e.printStackTrace();
        } */ // TODO: 30/07/18 descomenteaza blocul de mai sus dupa debuggare

        String cityFile = null;
        try {
            cityFile = WriteAndReadFile.readFromExternalFile(getString(R.string.idLatLonFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String predictionStrip = WriteAndReadFile.readFromInternalFile(getString(R.string.predictionStripFile), thisContext);


        String xs = xsFileExample;
        if (Objects.equals(predictionStrip, String.valueOf(hourOfDay / 3))) {
            System.out.println("DECI PREDSTRIP IS THE SAME AS THE ACTUAL");
            loading.setVisibility(View.GONE);
            loadMessage.setVisibility(View.GONE);

            happinessLevels = new Double[NUMBER_OF_STRIPS_TO_PREDICT];
            imageId = new Integer[NUMBER_OF_STRIPS_TO_PREDICT];

            String prediction = WriteAndReadFile.readFromInternalFile(getString(R.string.predictionDisplayFile), thisContext);
            String[] dataToDisplay = prediction.split("split");

            futureTemps = new Double[NUMBER_OF_STRIPS_TO_PREDICT];
            futurePressures = new Double[NUMBER_OF_STRIPS_TO_PREDICT];
            futureHumidities = new Double[NUMBER_OF_STRIPS_TO_PREDICT];
            futureClouds = new Double[NUMBER_OF_STRIPS_TO_PREDICT];
            futureWind = new Double[NUMBER_OF_STRIPS_TO_PREDICT];


            //we get the temp, pres, etc array as strings, after this we use the other arrays in purple as double arrays
            System.out.println("futureTemps.txt: " + WriteAndReadFile.readFromInternalFile("futureTemps.txt", thisContext));
            String[] temps = WriteAndReadFile.readFromInternalFile("futureTemps.txt", thisContext).split(" ");
            String[] pressures = WriteAndReadFile.readFromInternalFile("futurePressures.txt", thisContext).split(" ");
            String[] humidities = WriteAndReadFile.readFromInternalFile("futureHumidities.txt", thisContext).split(" ");
            String[] clouds = WriteAndReadFile.readFromInternalFile("futureClouds.txt", thisContext).split(" ");
            String[] wind = WriteAndReadFile.readFromInternalFile("futureWind.txt", thisContext).split(" ");


            System.out.println("temps:============================" + Arrays.toString(temps));
            System.out.println("pres:============================" + Arrays.toString(pressures));
            System.out.println("humid:============================" + Arrays.toString(humidities));
            System.out.println("clouds:============================" + Arrays.toString(clouds));
            System.out.println("wind:============================" + Arrays.toString(wind));


            strips = dataToDisplay[0].split("finStrip");
            System.out.println("strips array: " + Arrays.toString(strips));
            for(int k = 0; k < strips.length; k++) System.out.println(strips[k]);

            String[] preHappinessLevels = dataToDisplay[1].split(" ");
            String[] preImageId = dataToDisplay[2].split(" ");
            System.out.println("LENGTHS: " + strips.length + " " + happinessLevels.length + " " + imageId.length + " " + temps.length + " " + pressures.length + " " + humidities.length + " " + clouds.length + " " + wind.length + " " + NUMBER_OF_STRIPS_TO_PREDICT);
            // LENGTHS: 24 25 25 49 49 49 49 49 24
            for (int j = 0; j < NUMBER_OF_STRIPS_TO_PREDICT; j++) {
                System.out.println("j =========================== " + j);
                happinessLevels[j] = Double.parseDouble(preHappinessLevels[j]);
                System.out.println();
                imageId[j] = Integer.parseInt(preImageId[j]);

                futureTemps[j] = Double.parseDouble(temps[j]);
                futurePressures[j] = Double.parseDouble(pressures[j]);
                futureHumidities[j] = Double.parseDouble(humidities[j]);
                futureClouds[j] = Double.parseDouble(clouds[j]);
                futureWind[j] = Double.parseDouble(wind[j]);


            }

            CustomList adapter = new
                    CustomList(this, strips, happinessLevels, imageId, futureTemps, futurePressures, futureHumidities, futureClouds, futureWind);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //Toast.makeText(ResponseActivity.this, "You Clicked at " + [+ position], Toast.LENGTH_SHORT).show();

                }
            });
            
            //loadMessage.setText(prediction);
            //loadMessage.setTextSize(40);
        } else if (xs == "" || xs == null || cityFile == "" || cityFile == null) {
            loadMessage.setText("Not sufficient data.");
            loading.setVisibility(View.GONE);
        } else {
            writeDataAndPredict();
        }
    }

    public void writeDataAndPredict() {
        System.out.println("WE ARE INTO WRITEDATAANDPREDICT()");


        //Stergem tot ce am scris in fisierele astea si scriem din nou. Acum utilizam functiile din WriteAndReadFile class fiindca vrem sa se stearga ce era inainte nu vrem sa facem un append
        WriteAndReadFile.writeToInternalFile("futureTemps.txt", "", thisContext, Context.MODE_PRIVATE);
        WriteAndReadFile.writeToInternalFile("futurePressures.txt", "", thisContext, Context.MODE_PRIVATE);
        WriteAndReadFile.writeToInternalFile("futureHumidities.txt", "", thisContext, Context.MODE_PRIVATE);
        WriteAndReadFile.writeToInternalFile("futureClouds.txt", "", thisContext, Context.MODE_PRIVATE);
        WriteAndReadFile.writeToInternalFile("futureWind.txt", "", thisContext, Context.MODE_PRIVATE);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String cityFile = null;
                try {
                    cityFile = WriteAndReadFile.readFromExternalFile(getString(R.string.idLatLonFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("CITYFILE CONTENT: " + cityFile);
                String[] idll = cityFile.split(" ");
                double lat, lon;
                lat = Double.parseDouble(idll[0]);
                lon = Double.parseDouble(idll[1]);

                URL = "http://api.openweathermap.org/data/2.5/find?lat=" + lat + "&lon=" + lon + "&cnt=1&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
                //we use the current data link to retrieve the id of the city and use it to calculate the link of the forecast
                String weatherData = null;
                try {
                    weatherData = new getURLData().execute(URL).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                JSONObject obj = null;
                long cityId = 0;
                try {
                    obj = new JSONObject(weatherData);
                    cityId = obj.getJSONArray("list").getJSONObject(0).getLong("id");
                    //cityName = obj.getJSONArray("list").getJSONObject(0).getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*
                //we put underscore in between the words of the cityName
                String[] cn = cityName.split(" ");
                String res = "";
                for (int i = 0; i < cn.length-1; i++) {
                    res += cn[i] + "_";
                }
                res += cn[cn.length - 1];
                cityName = res; */

                URL = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityId + "&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba"; //// TODO: 28/07/18 nu inteleg de ce mai cerem inca o data vremea cu un id diferit :/

                weatherData = null;
                try {
                    weatherData = new getURLData().execute(URL).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                Log.i("WEATHER DATA:", weatherData);
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

                ArrayList<Double> futureTempsAL = new ArrayList<Double>();

                // PRESSURE
                double futureSumPressure = 0;
                double futureAveragePressure = 0;

                ArrayList<Double> futurePressuresAL = new ArrayList<Double>();

                // HUMIDITY
                double futureSumHumidity = 0;
                double futureAverageHumidity = 0;

                ArrayList<Double> futureHumiditiesAL = new ArrayList<Double>();

                // CLOUDS
                double futureSumClouds = 0;
                double futureAverageClouds = 0;

                ArrayList<Double> futureCloudsAL = new ArrayList<Double>();

                // WIND
                double futureSumWind = 0;
                double futureAverageWind = 0;

                ArrayList<Double> futureWindAL = new ArrayList<Double>();

                strips = new String[NUMBER_OF_STRIPS_TO_PREDICT];

                for (int i = 0; i < NUMBER_OF_STRIPS_TO_PREDICT; i++) {
                    String dt = null;
                    try {
                        dt = arr.getJSONObject(i).getString("dt_txt");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println("DT: " + dt);
                    Pattern p = Pattern.compile("[0-9]*-[0-9]*-[0-9]*");
                    Matcher m = p.matcher(dt);
                    int predictionDayOfWeek = 0;
                    if(m.find()) {
                        String date = m.group();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        predictionDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    }

                    Pattern hourP = Pattern.compile("[0-9]*:[0-9]*:[0-9]*");
                    Matcher hourM = hourP.matcher(dt);
                    if (hourM.find()) {
                        //System.out.println("ORA: " + hourM.group());
                        int hour = Integer.parseInt(hourM.group().substring(0, 2)); // we get the hour int
                        int futureStripId = hour/3;
                        int futureDurationInStrips = i + 1;
                        System.out.println("PREDICTION_DAY_OF_WEEK: " + predictionDayOfWeek);
                        strips[i] = weekDayStrings[predictionDayOfWeek - 1] + " " + hour + "h";

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
                        Double celsiusTemp = t - 273.15;
                        futureTempsAL.add(celsiusTemp);
                        WriteAndReadFile.writeToInternalFile("futureTemps.txt", celsiusTemp + " ", thisContext, Context.MODE_APPEND);
                        System.out.println("strip "+ i + ": futureTemps.txt: " + WriteAndReadFile.readFromInternalFile("futureTemps.txt", thisContext));
                        futureSumTemp += t;
                        futureAverageTemp = futureSumTemp/futureDurationInStrips;
                        double futureStdDevTemp = getStdDev(getVariance(futureAverageTemp, futureTempsAL));

                        // PRESSURE
                        futurePressuresAL.add(pressure);
                        WriteAndReadFile.writeToInternalFile("futurePressures.txt",pressure + " ", thisContext, Context.MODE_APPEND);
                        futureSumPressure += pressure;
                        futureAveragePressure = futureSumPressure/futureDurationInStrips;
                        double futureStdDevPressure = getStdDev(getVariance(futureAveragePressure, futurePressuresAL));

                        // HUMIDITY
                        futureHumiditiesAL.add(humid);
                        WriteAndReadFile.writeToInternalFile("futureHumidities.txt", humid + " ", thisContext, Context.MODE_APPEND);
                        futureSumHumidity += humid;
                        futureAverageHumidity = futureSumHumidity/futureDurationInStrips;
                        double futureStdDevHumidity = getStdDev(getVariance(futureAverageHumidity, futureHumiditiesAL));

                        // CLOUDS
                        futureCloudsAL.add(clouds);
                        WriteAndReadFile.writeToInternalFile("futureClouds.txt", clouds + " ", thisContext, Context.MODE_APPEND);
                        futureSumClouds += clouds;
                        futureAverageClouds = futureSumClouds/futureDurationInStrips;
                        double futureStdDevClouds = getStdDev(getVariance(futureAverageClouds, futureCloudsAL));

                        // WIND
                        futureWindAL.add(wind);
                        WriteAndReadFile.writeToInternalFile("futureWind.txt", wind + " ", thisContext, Context.MODE_APPEND);
                        futureSumWind += wind;
                        futureAverageWind = futureSumWind/futureDurationInStrips;
                        double futureStdDevWind = getStdDev(getVariance(futureAverageWind, futureWindAL));

                        Boolean append = true;
                        if ( i == 0) append = false;
                        //writting state of the day: 0 given that the crappy library asks for this data
                        WriteAndReadFile.writeToExternalFile(getString(R.string.predictionDataFile), predictionDayOfWeek + " " + 0 + " " + cityId
                                + " " + futureAverageTemp + " " + futureStdDevTemp
                                + " " + futureAveragePressure + " " + futureStdDevPressure
                                + " " + futureAverageHumidity * 1. + " " + futureStdDevHumidity * 1.
                                + " " + futureAverageClouds + " " + futureStdDevClouds
                                + " " + futureAverageWind + " " + futureStdDevWind + " "
                                + " " + futureStripId + " " + futureDurationInStrips + System.getProperty("line.separator"), append);

                    }
                }

                futureTemps =  new Double[futureTempsAL.size()];
                futureTemps = futureTempsAL.toArray(futureTemps);

                futurePressures =  new Double[futurePressuresAL.size()];
                futurePressures = futurePressuresAL.toArray(futurePressures);

                futureHumidities =  new Double[futureHumiditiesAL.size()];
                futureHumidities = futureHumiditiesAL.toArray(futureHumidities);

                futureClouds =  new Double[futureCloudsAL.size()];
                futureClouds = futureCloudsAL.toArray(futureClouds);

                futureWind =  new Double[futureWindAL.size()];
                futureWind = futureWindAL.toArray(futureWind);



                try {
                    System.out.println("CE CONTINE XS INAINTE DE GET ARRAYS: " + WriteAndReadFile.readFromExternalFile(getString(R.string.xsFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try { // TODO: 18/08/17 nu are nevoie de exceptie daca xs este un fisier intern. acum este extern
                    getArrayLists();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println("CE CONTINE XS DUPA GET ARRAYS: " + WriteAndReadFile.readFromExternalFile(getString(R.string.xsFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                WriteAndReadFile.writeToExternalFile(getString(R.string.xsFile), "", false);

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
                //am inlocuit computeHash(cityName) cu city id
                WriteAndReadFile.writeToExternalFile(getString(R.string.currentDataFile),
                        dayOfWeek + " " + gb + " " + cityId
                        + " " + averageTemp + " " + stdDevTemp
                        + " " + averagePressure + " " + stdDevPressure
                        + " " + averageHumidity + " " + stdDevHumidity
                        + " " + averageClouds + " " + stdDevClouds
                        + " " + averageWind + " " + stdDevWind
                                + " " + stripId + " " + durationInStrips +
                                System.getProperty("line.separator"), true);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("INAINTE DE LOCALMODELTASK.EXECUTE");
        // connect to the server
        new LocalModelTask().execute(this);
        System.out.println("DEBUGGING 8");
        //new connectTask().execute("");

    /*    System.out.println("FIRST COMMIT");
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        mTcpClient.stopClient();
                        // connect to the server
                        new connectTask().execute("predict");
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
        );*/
    }


        public class LocalModelTask extends AsyncTask<Activity,Void,String> {
            Activity myResponseActivity;

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(Activity... activities) {
                myResponseActivity = activities[0];
                System.out.println("A INTRAT IN DO IN BACKGROUND");
                DelimitedTextParser parser = new DelimitedTextParser();
                //parser.setResponseIndex(new NumericAttribute("goodBad"), 1); //the second attr is the gb
                String trainingData, testData;
                trainingData = testData = null;
                String result = "";
                String imageIdString = "";
                System.out.println("INAINTE DE TRY");
                try {
                    int numberOfVariables = getApplicationContext().getResources().getInteger(R.integer.numberOfVariables);
                    Attribute[] attributes = new Attribute[numberOfVariables];
                    String[] attributesArray = {"day", "loc", "avgTemp", "stdDevTemp", "avgPres", "stdDevPres",
                            "avgHumid", "stdDevHumid", "avgClouds", "stdDevClouds", "avgWind", "stdDevWind", "stripId",
                            "durationInStrips"};
                    for (int i = 0; i < numberOfVariables; i++) {
                        if (i <= 1) //the first three attributes are the only nominal
                            attributes[i] = new NominalAttribute(attributesArray[i]);
                        else{
                            attributes[i] = new NumericAttribute(attributesArray[i]);
                        }
                    }
                    System.out.println("DEBUGGING 1");
                    parser.setResponseIndex(new NumericAttribute("goodBad"), 1); //the second attr is the gb
                    //I have verified in the code that the parse() routine explicitly avoids
                    //the column containing the prediction variable. This holds in the following
                    //two lines both for train as for test
                    String root = Environment.getExternalStorageDirectory().toString();
                    String dirName = WriteAndReadFile.dataDirectoryName;
                    File myDir = new File(root + dirName);
                    myDir.mkdirs();
                    System.out.println("DEBUGGING 2");
                    File currentFile = new File (myDir, getString(R.string.currentDataFile));
                    AttributeDataset train = parser.parse("TrainingData", attributes, currentFile);
                    File predictionFile = new File (myDir, getString(R.string.predictionDataFile));
                    AttributeDataset test = parser.parse("TestData", attributes, predictionFile);
                    double[][] x = train.toArray(new double[train.size()][]);
                    double[] y = train.toArray(new double[train.size()]);
                    double[][] testx = test.toArray(new double[test.size()][]);
                    double[] testy = test.toArray(new double[test.size()]);
                    int maxNodes = 100;
                    RegressionTree tree = new RegressionTree(x, y, maxNodes);
                    int error = 0;
                    happinessLevels = new Double[testx.length];
                    imageId = new Integer[testx.length];
                    System.out.println("DEBUGGING 3");

                    for (int i = 0; i < testx.length; i++) {
                        String resultToAppend = tree.predict(testx[i]) + "";
                        result = result + resultToAppend + " ";
                        Double level = Double.parseDouble(resultToAppend);
                        happinessLevels[i] = level;
                        if (level < 20.) imageId[i] = R.mipmap.very_sad;
                        else if (level < 40.) imageId[i] = R.mipmap.sad;
                        else if (level < 60.) imageId[i] = R.mipmap.neutral;
                        else if (level < 80.) imageId[i] = R.mipmap.happy;
                        else imageId[i] = R.mipmap.very_happy;
                        imageIdString += String.valueOf(imageId[i]) + " ";
                        System.out.println("DEBUGGING 4" + i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String stripsString = "";
                for (int k = 0; k < strips.length; k++) {
                    stripsString += strips[k] + "finStrip";
                    System.out.println("DEBUGGING 5" + k);
                }

                WriteAndReadFile.writeToInternalFile(getString(R.string.predictionDisplayFile), stripsString + "split" + result + "split" + imageIdString,thisContext, Context.MODE_PRIVATE);
                System.out.println("Prediction Display File:" + WriteAndReadFile.readFromInternalFile(getString(R.string.predictionDisplayFile), thisContext));
                return result;
            }
            //trainingData = readFromExternalFile(getString(R.string.currentDataFile));
            //testData =  readFromExternalFile(getString(R.string.predictionDataFile));
            //InputStream trainStream = new ByteArrayInputStream(trainingData.getBytes());
            //InputStream testStream = new ByteArrayInputStream(testData.getBytes());

            protected void onPostExecute(String result) { //you receive the result as a parameter
                System.out.println("================ON POST EXECUTE==================");
                WriteAndReadFile.writeToInternalFile(getString(R.string.predictionStripFile), String.valueOf(hourOfDay/3), thisContext, Context.MODE_PRIVATE);
                System.out.println("CONTINUTUL LUI PREDICTION STRIP din ONPROGRESSUPDATE: ---> " + WriteAndReadFile.readFromInternalFile(getString(R.string.predictionStripFile), thisContext));

                //dayTv.setText("Tomorrow will be"); //// TODO: 14/08/17
                //dayTv.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                loadMessage.setVisibility(View.GONE);

                //String prediction = readFromInternalFile(getString(R.string.predictionDisplayFile));
                //String[] dayAndPrediction = prediction.split(" ");
                //Double percentage = Double.parseDouble(dayAndPrediction[1]); //// TODO: 15/08/17 some error:  java.lang.NumberFormatException: Invalid double: "null"
                //loadMessage.setText(new DecimalFormat("#0.0").format(percentage) + "% GOOD");
                //loadMessage.setText(result);
                //loadMessage.setTextSize(40);

                //CustomList adapter = new CustomList(myResponseActivity, strips, happinessLevels, imageId);
                System.out.println("DEBUGGING 6");
                System.out.println("DEBUGGING STRIPS: " + Arrays.toString(strips));
                System.out.println("DEBUGGING happinessLevels: " + Arrays.toString(happinessLevels));
                System.out.println("DEBUGGING imageId: " + Arrays.toString(imageId));
                System.out.println("DEBUGGING futureTemps: " + Arrays.toString(futureTemps));
                System.out.println("DEBUGGING futurePressures: " + Arrays.toString(futurePressures));
                System.out.println("DEBUGGING futureHumidities: " + Arrays.toString(futureHumidities));
                System.out.println("DEBUGGING futureClouds: " + Arrays.toString(futureClouds));
                System.out.println("DEBUGGING futureWind: " + Arrays.toString(futureWind));


                CustomList adapter = new CustomList(myResponseActivity, strips, happinessLevels, imageId, futureTemps, futurePressures, futureHumidities, futureClouds, futureWind);
                list.setAdapter(adapter);
                System.out.println("DEBUGGING 7");
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //Toast.makeText(ResponseActivity.this, "You Clicked at " + [+ position], Toast.LENGTH_SHORT).show();

                    }
                });
                
                System.out.print("Prediction done locally on the mobile with result ");
                System.out.println(result);
            }

        }

        /*

    public void sendMessageToServer(View v) {
        String message = "Hola Mundo";
        System.out.println(message);
        //sends the message to the server
        if (mTcpClient != null) {
            //System.out.println("TCP CLIENT IS NOT NULL (RESPONSE ACTIVITY)");
            mTcpClient.sendMessage(message);
        }
    } */

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {
            System.out.println("S-A INTRAT IN DO IN BACKGROUND");
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String m) {}
            });
            try {
                mTcpClient.run(WriteAndReadFile.readFromExternalFile(getString(R.string.currentDataFile))); // the message that is sent to the server
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /*

        public class connectTask extends AsyncTask<String,String,TCPClient> {


        @Override
        protected TCPClient doInBackground(String... message) {
            System.out.println("message of do in backGROUND" + message[0]);
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });

            if (message[0] == "commit") {
                //mTcpClient.run(readFromExternalFile(getString(R.string.currentDataFile)) + "SPLIT" + readFromExternalFile(getString(R.string.predictionDataFile))); TODO DECOMENTEAZA DUPA CE DEBUGHEZI CONEXIUNEA CU SERVERUL
                mTcpClient.run("");
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
            System.out.println("SE VA SCRIE IN PREDICTION DISPLAY FILE!!!!!!!!!!!!!!!");
            if (values[0] != null) WriteAndReadFile.writeToInternalFile(getString(R.string.predictionDisplayFile),values[0], thisContext); //String.valueOf(dayOfMonth) + " " +
            WriteAndReadFile.writeToInternalFile(getString(R.string.predictionStripFile), String.valueOf(hourOfDay/3), thisContext);
            System.out.println("CONTINUTUL LUI PREDICTION STRIP din ONPROGRESSUPDATE: ---> " + readFromInternalFile(getString(R.string.predictionStripFile)));

            //dayTv.setText("Tomorrow will be"); //// TODO: 14/08/17
            //dayTv.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);

            String prediction = readFromInternalFile(getString(R.string.predictionDisplayFile));
            System.out.println("CONTINUTUL LUI PREDICTION DISPLAY FILE: " + prediction);
            //String[] dayAndPrediction = prediction.split(" ");
            //Double percentage = Double.parseDouble(dayAndPrediction[1]); //// TODO: 15/08/17 some error:  java.lang.NumberFormatException: Invalid double: "null"
            //loadMessage.setText(new DecimalFormat("#0.0").format(percentage) + "% GOOD");
            loadMessage.setText(prediction);
            loadMessage.setTextSize(40);

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

    */

    private void getArrayLists() throws IOException {
        //String currentData = readFromInternalFile(getString(R.string.xsFile));
        //String currentData = WriteAndReadFile.readFromExternalFile(getString(R.string.xsFile)); // TODO: 30/07/18 descomenteaza linia asta dupa debuggare
        String currentData = xsFileExample; // // TODO: 30/07/18 comenteaza linia asta dupa debuggare
        String[] data = currentData.split(" final");
        System.out.println("current data = " + currentData);
        durationInStrips = data.length;
        System.out.println(data.length);
        System.out.println("duration in strips: " + durationInStrips);

        tempsArray = new ArrayList<>();
        pressuresArray = new ArrayList<>();
        humiditiesArray = new ArrayList<>();
        cloudsArray = new ArrayList<>();
        windArray = new ArrayList<>();
        /*for (int i = 0; i < data.length; i++) { TODO descomenteaza asta daca nu o sa mai avem nevoie de ora la care a fost luata fiecare mostra de current weather
            String[] sarr = data[i].split(" ");
            tempsArray.add(Double.parseDouble(sarr[0]));
            pressuresArray.add(Double.parseDouble(sarr[1]));
            humiditiesArray.add(Double.parseDouble(sarr[2]));
            cloudsArray.add(Double.parseDouble(sarr[3]));
            windArray.add(Double.parseDouble(sarr[4]));
        } */

        for (int i = Math.max(0, durationInStrips - 3); i < data.length; i++) {
            //eroare: data si xs nu au nimic in ele!
            // data lenght: 1
            //data[i] =
            //xs.txt:
            System.out.println("Math.max(0, durationInStrips - 3) = "+ Math.max(0, durationInStrips - 3));
            System.out.println("data lenght: " + data.length);
            System.out.println("data[" + i + "] = " + data[i]);
            System.out.println("xs.txt: " + WriteAndReadFile.readFromExternalFile(getString(R.string.xsFile)));
            //System.out.println("" + );

            String[] sarr = data[i].split(" ");
            System.out.println("sarr.lenght = " + sarr.length);
            tempsArray.add(Double.parseDouble(sarr[1]));
            pressuresArray.add(Double.parseDouble(sarr[2]));
            humiditiesArray.add(Double.parseDouble(sarr[3]));
            cloudsArray.add(Double.parseDouble(sarr[4]));
            windArray.add(Double.parseDouble(sarr[5]));
        }
    }

    private double getAverage(ArrayList<Double> arr) {
        Double sum = .0;
        int s = arr.size();
        for(int i = 0; i < s; i++) {
            sum += arr.get(i);
        }
        //System.out.println("PRINT 11(get average) --------------");
        return sum/s;
    }


    private double getVariance(double average, ArrayList<Double> arr)
    {
        double sum = 0;
        int s = arr.size();
        for(int i = 0; i < s; i++)
            sum += (arr.get(i)-average)*(arr.get(i)-average);
        //System.out.println("PRINT 12(get variance) --------------");
        return sum/s;
    }

    private double getStdDev(double variance)
    {
        return Math.sqrt(variance);
    }

    /*
    public void getLocationAndPredict(){
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
        System.out.println("S-A DECLARAT LISTENER-UL DIN LOCATION");
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

        Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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

                    WriteAndReadFile.writeToExternalFile(getString(R.string.idLatLonFile),latitude + " " + longitude, false);
                    System.out.println("WE WRITE DATA AND PREDICT");
                    writeDataAndPredict();
                }
            }
        } else {
            Toast.makeText(ResponseActivity.this, "Your location is disabled! Try again...", Toast.LENGTH_SHORT).show();
        }
    } */
}
