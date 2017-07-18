package yukami.weather;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeatherHttpClient whp = new WeatherHttpClient();
        String weatherData = "";
        if (isNetworkAvailable()) weatherData = whp.getWeatherData("Barcelona,es");
        System.out.println("VREMEA PE AZI" + weatherData);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
     /*
    @Override
    public void onStart() {
        super.onStart();




        // We create out JSONObject from the data
        JSONObject jObj;
        try {
            jObj = new JSONObject(((new WeatherHttpClient()).getWeatherData("Barcelona,es")));
            jObj.getJSONArray("list");
            JSONArray arr = jObj.getJSONArray("list");
            Log.i("INSIDE THE LIST:    ", arr.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */



}
