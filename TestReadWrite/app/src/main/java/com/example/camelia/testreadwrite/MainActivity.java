package com.example.camelia.testreadwrite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String s1 = "";
        String s2 = "";

        WriteAndReadFile.writeToExternalFile("holaMundo.txt", "Hola Mundo!", true);
        try {
            s1 = WriteAndReadFile.readFromExternalFile("holaMundo.txt");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR EXTERNAL R: ", "holamundo.txt");
        }
        WriteAndReadFile.writeToInternalFile("holaMundo.txt", "heeeeeey", this);
        s2 = WriteAndReadFile.readFromInternalFile("holaMundo.txt", this);

        TextView tv1 = (TextView) findViewById(R.id.t1);
        tv1.setText(s1);
        TextView tv2 = (TextView) findViewById(R.id.t2);
        tv2.setText(s2);

    }
}
