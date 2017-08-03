package com.example.camelia.debug6;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
    private static String URL = "http://api.openweathermap.org/data/2.5/forecast?q=Amposta,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    String gb;
    private final static String serverIP = "192.168.1.225";
    private final static int serverPort = 55160;
    private TCPClient mTcpClient;
    TextView serverResponse;
    TextView loadMessage, dayTv;
    ArrayList<Double> tempsArray, pressuresArray, humiditiesArray, cloudsArray, windArray;
    ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        //serverResponse = (TextView) findViewById(R.id.serverResponse); //TODO: cand o sa primim un raspuns de la server o sa folosim textView-ul asta ca sa scriem rezultatul
        loadMessage = (TextView) findViewById(R.id.loadMessage);
        loading = (ProgressBar) findViewById(R.id.loading);
        dayTv = (TextView) findViewById(R.id.dayTv);


        Intent intent = this.getIntent();
        gb = intent.getStringExtra("how");

    }

    //we get the temperature and we write in the file day + Good/Bad + temperature
    @Override
    public void onResume() {
        super.onResume();
        //we cancel the notification
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(123);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String weatherData = null;
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

                JSONArray arr = null;
                try {
                    arr = obj.getJSONArray("list");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //System.out.println("PRINT 3 --------------");

                //we get tomorrow's date
                Date date = new Date();
                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dt = dayFormat.format(date); // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 1);  // number of days to add
                dt = sdf.format(c.getTime());  // dt is now the new date
                //System.out.println("PRINT 4 --------------");
                //we start calculating the average and standard deviation of tomorrow (day and night separatelly)

                // TEMPERATURE
                double dayTomorrowAverageTemp = 0;
                double nightTomorrowAverageTemp = 0;

                ArrayList<Double> dayTomorrowTemps = new ArrayList<Double>();
                ArrayList<Double> nightTomorrowTemps = new ArrayList<Double>();

                // PRESSURE
                double dayTomorrowAveragePressure = 0;
                double nightTomorrowAveragePressure = 0;

                ArrayList<Double> dayTomorrowPressures = new ArrayList<Double>();
                ArrayList<Double> nightTomorrowPressures = new ArrayList<Double>();

                // HUMIDITY
                int dayTomorrowAverageHumidity = 0;
                int nightTomorrowAverageHumidity = 0;

                ArrayList<Double> dayTomorrowHumidities = new ArrayList<Double>();
                ArrayList<Double> nightTomorrowHumidities = new ArrayList<Double>();

                // CLOUDS
                double dayTomorrowAverageClouds = 0;
                double nightTomorrowAverageClouds = 0;

                ArrayList<Double> dayTomorrowClouds = new ArrayList<Double>();
                ArrayList<Double> nightTomorrowClouds = new ArrayList<Double>();

                // WIND
                double dayTomorrowAverageWind = 0;
                double nightTomorrowAverageWind = 0;

                ArrayList<Double> dayTomorrowWind = new ArrayList<Double>();
                ArrayList<Double> nightTomorrowWind = new ArrayList<Double>();

                //System.out.println("PRINT 5 --------------");
                int nrOfHoursDay = 0;
                int nrOfHoursNight = 0;
                String dt_compare = null;

                for (int i = 0; i < arr.length(); i++) {
                    //System.out.println("PRINT 6 --------------loop " + i);
                    try {

                        dt_compare = arr.getJSONObject(i).getString("dt_txt");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (dt_compare.contains(dt)) { //see if is tomorrow's date
                        Pattern hourP = Pattern.compile("[0-9]*:[0-9]*:[0-9]*");
                        Matcher hourM = hourP.matcher(dt_compare);
                        if (hourM.find()) {
                            //System.out.println("ORA: " + hourM.group());
                            int hour = Integer.parseInt(hourM.group().substring(0, 2)); // we get the hour int
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
                            if (hour <= 8) {
                                ++nrOfHoursNight;
                                // TEMPERATURE
                                nightTomorrowAverageTemp += t;
                                nightTomorrowTemps.add(t);

                                //PRESSURE
                                nightTomorrowAveragePressure += pressure;
                                nightTomorrowPressures.add(pressure);

                                // HUMIDITY
                                nightTomorrowAverageHumidity += humid;
                                nightTomorrowHumidities.add(humid);

                                // CLOUDS
                                nightTomorrowAverageClouds += clouds;
                                nightTomorrowClouds.add(clouds);

                                // WIND
                                nightTomorrowAverageWind += wind;
                                nightTomorrowWind.add(wind);
                            }
                            else {
                                ++nrOfHoursDay;
                                // TEMPERATURE
                                dayTomorrowAverageTemp += t;
                                dayTomorrowTemps.add(t);

                                // PRESSURE
                                dayTomorrowAveragePressure += pressure;
                                dayTomorrowPressures.add(pressure);

                                // HUMIDITY
                                dayTomorrowAverageHumidity += humid;
                                dayTomorrowHumidities.add(humid);

                                // CLOUDS
                                dayTomorrowAverageClouds += clouds;
                                dayTomorrowClouds.add(clouds);

                                // WIND
                                dayTomorrowAverageWind += wind;
                                dayTomorrowWind.add(wind);
                            }
                        }
                    }


                }

                // TEMPERATURE
                dayTomorrowAverageTemp = dayTomorrowAverageTemp - 273.15; //celsius
                nightTomorrowAverageTemp = nightTomorrowAverageTemp - 273.15; //celsius

                dayTomorrowAverageTemp = dayTomorrowAverageTemp / nrOfHoursDay; // kelvin
                nightTomorrowAverageTemp = nightTomorrowAverageTemp / nrOfHoursNight; // kelvin

                double dayTomorrowStdDevTemp = getStdDev(getVariance(dayTomorrowAverageTemp, dayTomorrowTemps));
                double nightTomorrowStdDevTemp = getStdDev(getVariance(nightTomorrowAverageTemp, nightTomorrowTemps));

                // PRESSURE
                dayTomorrowAveragePressure = dayTomorrowAveragePressure / nrOfHoursDay;
                nightTomorrowAveragePressure = nightTomorrowAveragePressure / nrOfHoursNight;

                double dayTomorrowStdDevPressure = getStdDev(getVariance(dayTomorrowAveragePressure, dayTomorrowPressures));
                double nightTomorrowStdDevPressure = getStdDev(getVariance(nightTomorrowAveragePressure, nightTomorrowPressures));

                // HUMIDITY
                dayTomorrowAverageHumidity = dayTomorrowAverageHumidity / nrOfHoursDay;
                nightTomorrowAverageHumidity = nightTomorrowAverageHumidity / nrOfHoursNight;

                double dayTomorrowStdDevHumidity = getStdDev(getVariance(dayTomorrowAverageHumidity, dayTomorrowHumidities));
                double nightTomorrowStdDevHumidity = getStdDev(getVariance(nightTomorrowAverageHumidity, nightTomorrowHumidities));

                // CLOUDS
                dayTomorrowAverageClouds = dayTomorrowAverageClouds / nrOfHoursDay;
                nightTomorrowAverageClouds = nightTomorrowAverageClouds / nrOfHoursNight;

                double dayTomorrowStdDevClouds = getStdDev(getVariance(dayTomorrowAverageClouds, dayTomorrowClouds));
                double nightTomorrowStdDevClouds = getStdDev(getVariance(nightTomorrowAverageClouds, nightTomorrowClouds));

                // WIND
                dayTomorrowAverageWind = dayTomorrowAverageWind / nrOfHoursDay;
                nightTomorrowAverageWind = nightTomorrowAverageWind / nrOfHoursNight;

                double dayTomorrowStdDevWind = getStdDev(getVariance(dayTomorrowAverageWind, dayTomorrowWind));
                double nightTomorrowStdDevWind = getStdDev(getVariance(nightTomorrowAverageWind, nightTomorrowWind));

                Calendar calendar = GregorianCalendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                //we calculate averages of the CURRENT night and day
                getArrayLists("dayCurrentWeather.txt");

                ArrayList<Double> dayTempsArray, dayPressuresArray, dayHumiditiesArray, dayCloudsArray, dayWindArray;
                // CURRENT TEMP
                dayTempsArray = tempsArray;
                double dayAverageTemp = getAverage(dayTempsArray);
                double dayStdDevTemp = getStdDev(getVariance(dayAverageTemp, dayTempsArray));

                // CURRENT PRESSURE
                dayPressuresArray = pressuresArray;
                double dayAveragePressure = getAverage(dayPressuresArray);
                double dayStdDevPressure = getStdDev(getVariance(dayAveragePressure, dayPressuresArray));

                // CURRENT HUMIDITY
                dayHumiditiesArray = humiditiesArray;
                double dayAverageHumidity = getAverage(dayHumiditiesArray);
                double dayStdDevHumidity = getStdDev(getVariance(dayAverageHumidity, dayHumiditiesArray));

                // CURRENT CLOUDS
                dayCloudsArray = cloudsArray;
                double dayAverageClouds = getAverage(dayCloudsArray);
                double dayStdDevClouds = getStdDev(getVariance(dayAverageClouds, dayCloudsArray));

                // CURRENT WIND
                dayWindArray = windArray;
                double dayAverageWind = getAverage(dayWindArray);
                double dayStdDevWind = getStdDev(getVariance(dayAverageWind, dayWindArray));


                String fileContent_nightCurrentT = readFromFile("nightCurrentWeather.txt");
                //System.out.println("FILE CONTENT " + fileContent_nightCurrentT);
                ArrayList<Double> nightTempsArray, nightPressuresArray, nightHumiditiesArray, nightCloudsArray, nightWindArray;
                double nightAverageTemp, nightStdDevTemp, nightAveragePressure, nightStdDevPressure, nightAverageHumidity, nightStdDevHumidity, nightAverageClouds, nightStdDevClouds, nightAverageWind, nightStdDevWind;
                if (fileContent_nightCurrentT == null || fileContent_nightCurrentT == "") {
                    nightTempsArray = dayTempsArray;
                    nightPressuresArray = dayPressuresArray;
                    nightHumiditiesArray = dayHumiditiesArray;
                    nightCloudsArray = dayCloudsArray;
                    nightWindArray = dayWindArray;
                    System.out.println("FILE CONTENT IS NULL OR VOID");
                }
                else {
                    getArrayLists("nightCurrentWeather.txt");
                    nightTempsArray = tempsArray;
                    nightPressuresArray = pressuresArray;
                    nightHumiditiesArray = humiditiesArray;
                    nightCloudsArray = cloudsArray;
                    nightWindArray = windArray;
                }

                // TEMPERATURE
                nightAverageTemp = getAverage(nightTempsArray);
                nightStdDevTemp = getStdDev(getVariance(nightAverageTemp, nightTempsArray));

                // PRESSURE
                nightAveragePressure = getAverage(nightPressuresArray);
                nightStdDevPressure = getStdDev(getVariance(nightAveragePressure, nightPressuresArray));

                // HUMIDITY
                nightAverageHumidity = getAverage(nightHumiditiesArray);
                nightStdDevHumidity = getStdDev(getVariance(nightAverageHumidity, nightHumiditiesArray));

                // CLOUDS
                nightAverageClouds = getAverage(nightCloudsArray);
                nightStdDevClouds = getStdDev(getVariance(nightAverageClouds, nightCloudsArray));

                // WIND
                nightAverageWind = getAverage(nightWindArray);
                nightStdDevWind = getStdDev(getVariance(nightAverageWind, nightWindArray));

                writeToFile(getString(R.string.dataFile), day + " " + gb + " " + nightAverageTemp + " " + nightStdDevTemp
                        + " " + nightAveragePressure + " " + nightStdDevPressure
                        + " " + nightAverageHumidity + " " + nightStdDevHumidity
                        + " " + nightAverageClouds + " " + nightStdDevClouds
                        + " " + nightAverageWind + " " + nightStdDevWind
                        + " " + dayAverageTemp + " " + dayStdDevTemp
                        + " " + dayAveragePressure + " " + dayStdDevPressure
                        + " " + dayAverageHumidity + " " + dayStdDevHumidity
                        + " " + dayAverageClouds + " " + dayStdDevClouds
                        + " " + dayAverageWind + " " + dayStdDevWind
                        + " " + nightTomorrowAverageTemp + " " + nightTomorrowStdDevTemp
                        + " " + nightTomorrowAveragePressure + " " + nightTomorrowStdDevPressure
                        + " " + nightTomorrowAverageHumidity + " " + nightTomorrowStdDevHumidity
                        + " " + nightTomorrowAverageClouds + " " + nightTomorrowStdDevClouds
                        + " " + nightTomorrowAverageWind + " " + nightTomorrowStdDevWind
                        + " " + dayTomorrowAverageTemp + " " + dayTomorrowStdDevTemp
                        + " " + dayTomorrowAveragePressure + " " + dayTomorrowStdDevPressure
                        + " " + dayTomorrowAverageHumidity * 1. + " " + dayTomorrowStdDevHumidity * 1.
                        + " " + dayTomorrowAverageClouds + " " + dayTomorrowStdDevClouds
                        + " " + dayTomorrowAverageWind + " " + dayTomorrowStdDevWind + " final");
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writeToFile(getString(R.string.answeredFile), String.valueOf(true));

        // connect to the server
        new connectTask().execute("commit");
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


        System.out.println("S-A TERMINAT ON RESUME");
    }

    private void writeToFile(String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter;
            if (fileName == getString(R.string.dataFile))
                outputStreamWriter = new OutputStreamWriter(this.openFileOutput(fileName, Context.MODE_APPEND));
            else outputStreamWriter = new OutputStreamWriter(this.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
/*
    public void sendMessageToServer(View v) {
        String message = readFromFile(getString(R.string.dataFile));
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
                mTcpClient.run(readFromFile(getString(R.string.dataFile)));
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
            if (values[0] != null) writeToFile(getString(R.string.predictionFile), values[0]);
            System.out.println("on progress update SE EXECUTA CODUL ASTA!!");
            boolean answered = Boolean.valueOf(readFromFile(getString(R.string.answeredFile)));
            if (answered) dayTv.setText("Tomorrow will be");
            else dayTv.setText("The current day may be");

            System.out.println("DESPUES DE SET TEXT");
            dayTv.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            Double percentage = Double.parseDouble(readFromFile(getString(R.string.predictionFile)));
            loadMessage.setText(new DecimalFormat("#0.0").format(percentage) + "% GOOD");
            loadMessage.setTextSize(40);
            System.out.println("STOP CLIENT DIN PROGRESS UPDATE");
            mTcpClient.stopClient();

            //serverResponse.setText(values[0]);
            //arrayList.add(values[0]);
            //we can add the message received from server to a text view

        }
    }

    private String readFromFile(String fileName) {

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

    private void getArrayLists(String filename) {

        Pattern p1 = Pattern.compile("[-+]?[0-9]*\\.[0-9]* [-+]?[0-9]*\\.[0-9]* [-+]?[0-9]*\\.[0-9]* [-+]?[0-9]*\\.[0-9]* [-+]?[0-9]*\\.[0-9]*"); // temp + pressure + humid + clouds + wind (5)
        Matcher m1 = p1.matcher(readFromFile(filename));

        tempsArray = new ArrayList<>();
        pressuresArray = new ArrayList<>();
        humiditiesArray = new ArrayList<>();
        cloudsArray = new ArrayList<>();
        windArray = new ArrayList<>();
        while (m1.find()) {
            String[] sarr = m1.group().split(" ", 5); // TODO: 31/07/17 CAND ADAUGI O VARIABILA INCREMENTEAZA NUMARUL DE LA SPLIT
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
