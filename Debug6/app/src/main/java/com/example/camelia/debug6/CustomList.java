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
    public CustomList(Activity context,
                      String[] strips, Double[] happinessLevels, Integer[] imageId) {
        super(context, R.layout.list_item, strips);
        this.context = context;
        this.strips = strips;
        this.happinessLevels = happinessLevels;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        firstLine.setText(strips[position]);
        secondLine.setText(new DecimalFormat("#0").format(happinessLevels[position]) + "% PRODUCTIVE");
        //secondLine.setText(happinessLevels[position] + "% PRODUCTIVE");

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}