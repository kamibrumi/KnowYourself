package com.example.camelia.debug6;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] strips;
    private final Double[] happinessLevels;
    private final Integer[] imageId;
    private final Double[] temp, pres, humid, cloud, wind;
    public CustomList(Activity context,
                      String[] strips, Double[] happinessLevels, Integer[] imageId, Double[] temp, Double[] pres, Double[] humid, Double[] cloud, Double[] wind) {
        super(context, R.layout.list_item, strips);
        this.context = context;
        this.strips = strips;
        this.happinessLevels = happinessLevels;
        this.imageId = imageId;
        this.temp = temp;
        this.pres = pres;
        this.humid = humid;
        this.cloud = cloud;
        this.wind = wind;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);

        TextView tempT = (TextView) rowView.findViewById(R.id.temp);
        TextView presT = (TextView) rowView.findViewById(R.id.pres);
        TextView humidT = (TextView) rowView.findViewById(R.id.humid);
        TextView cloudT = (TextView) rowView.findViewById(R.id.cloud);
        TextView windT = (TextView) rowView.findViewById(R.id.wind);


        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        firstLine.setText(strips[position]);
        secondLine.setText(new DecimalFormat("#0").format(happinessLevels[position]) + "% PRODUCTIVE");
        //secondLine.setText(happinessLevels[position] + "% PRODUCTIVE");
        System.out.println("position in custom list =========================== " + position);
        System.out.println("DEBUGGING ");
        System.out.println("DEBUGGING temp.length: " + temp.length);
        tempT.setText("Temperature: " + String.valueOf(temp[position]) + "°");
        presT.setText("Pressure: " + String.valueOf(pres[position]) + "hPa");
        humidT.setText("Humidity: " + String.valueOf(humid[position]) + "%");
        cloudT.setText("Cloudiness: " + String.valueOf(cloud[position]));
        windT.setText("Wind Speed: " + String.valueOf(wind[position]) + " mps");

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}