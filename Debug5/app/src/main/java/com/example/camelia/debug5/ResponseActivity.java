package com.example.camelia.debug5;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseActivity extends AppCompatActivity {
    private static String URL = "http://api.openweathermap.org/data/2.5/forecast?q=Barcelona,es&APPID=afbef7bdcea5f0feb4b7e97fe6b57aba";
    String gb;
    private final static String serverIP = "192.168.1.225";
    private final static int serverPort = 55160;
    private TCPClient mTcpClient;
    TextView serverResponse;
    TextView clientTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        TextView response = (TextView) findViewById(R.id.response);
        response.setText("Thanks for your answer. We wait for you next day at " + this.getResources().getInteger(R.integer.startHour) + "h.");

        //serverResponse = (TextView) findViewById(R.id.serverResponse); //TODO: cand o sa primim un raspuns de la server o sa folosim textView-ul asta ca sa scriem rezultatul
        clientTv = (TextView) findViewById(R.id.clientMessage);

        Intent intent = this.getIntent();
        gb = intent.getStringExtra("how");

        //we cancel the notification
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(123);
    }

    //we get the temperature and we write in the file day + Good/Bad + temperature
    @Override
    public void onResume() {
        super.onResume();
        //System.out.println("PRINT 1 --------------");

        String weatherData = null;
        try {
            weatherData = new getURLData().execute(URL).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //System.out.println("PRINT 2 --------------");
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
        double dayTomorrowAverageTemp = 0;
        double nightTomorrowAverageTemp = 0;

        ArrayList<Double> dayTomorrowTemps = new ArrayList<>();
        ArrayList<Double> nightTomorrowTemps = new ArrayList<>();

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
                    try {
                        main = arr.getJSONObject(i).getJSONObject("main");
                        t = main.getDouble("temp");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (hour <= 8) {
                        ++nrOfHoursNight;
                        nightTomorrowAverageTemp += t;
                        nightTomorrowTemps.add(t);
                        //System.out.println("ORA night: " + hour);

                    }
                    else {
                        ++nrOfHoursDay;
                        dayTomorrowAverageTemp += t;
                        dayTomorrowTemps.add(t);
                        //System.out.println("ORA day: " + hour);
                    }
                }
            }


        }

        dayTomorrowAverageTemp = dayTomorrowAverageTemp - 273.15; //celsius
        nightTomorrowAverageTemp = nightTomorrowAverageTemp - 273.15; //celsius

        //System.out.println("PRINT 7 --------------");
        dayTomorrowAverageTemp = dayTomorrowAverageTemp / nrOfHoursDay; // kelvin
        nightTomorrowAverageTemp = nightTomorrowAverageTemp / nrOfHoursNight; // kelvin
        //System.out.println("PRINT 8 --------------");

        double v = getVariance(dayTomorrowAverageTemp, dayTomorrowTemps);
        //System.out.println("PRINT 8v --------------");
        double dayTomorrowStdDev = getStdDev(v);


        double nightTomorrowStdDev = getStdDev(getVariance(nightTomorrowAverageTemp, nightTomorrowTemps));
        //System.out.println("PRINT 9 --------------");

        Calendar calendar = GregorianCalendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //we calculate average temperature of the current night and day

        ArrayList<Double> nightTemps = getTempArrayList("nightCurrentWeather.txt");
        ArrayList<Double> dayTemps = getTempArrayList("dayCurrentWeather.txt");

        double nightAverageTemp = getAverage(nightTemps);
        double dayAverageTemp = getAverage(dayTemps);

        double nightStdDev = getStdDev(getVariance(nightAverageTemp, nightTemps));
        double dayStdDev = getStdDev(getVariance(dayAverageTemp, dayTemps));

        writeToFile(getString(R.string.dataFile), " " + day + " " + gb + " " + nightAverageTemp + " " + nightStdDev + " " + dayAverageTemp + " " + dayStdDev + " " + nightTomorrowAverageTemp + " " + nightTomorrowStdDev + " " + dayTomorrowAverageTemp + " " + dayTomorrowStdDev, getApplicationContext());

        writeToFile(getString(R.string.answeredFile), String.valueOf(true), this);
        // connect to the server
        new connectTask().execute("");
    }

    private void writeToFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter;
            if (fileName == getString(R.string.dataFile))
                outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            else outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    /*
    private void writeToFile(String nameOfFile, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter;
            if (nameOfFile == getString(R.string.dataFile)) //whe we write into the data file
                outputStreamWriter = new OutputStreamWriter(context.openFileOutput(getString(R.string.dataFile), Context.MODE_APPEND));
            else //when we write into the booleans file
                outputStreamWriter = new OutputStreamWriter(context.openFileOutput(nameOfFile, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    } */

    @Override
    protected void onStop() {
        super.onStop();
        sendMessageToServer();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void sendMessageToServer() {
        String message = readFromFile(getString(R.string.dataFile));
        System.out.println(message);
        //sends the message to the server
        if (mTcpClient != null) {
            System.out.println("TCP CLIENT IS NOT NULL (RESPONSE ACTIVITY)");
            mTcpClient.sendMessage(message);
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
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            serverResponse.setText(values[0]);
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

    private ArrayList<Double> getTempArrayList(String filename) {

        Pattern p = Pattern.compile("[-+]?[0-9]*\\.[0-9]+");
        Matcher m = p.matcher(readFromFile(filename));

        ArrayList<Double> temps = new ArrayList<>();

        while (m.find())
            temps.add(Double.valueOf(m.group()));
        //System.out.println("PRINT 10(getarrayfun) --------------");
        return temps;
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
