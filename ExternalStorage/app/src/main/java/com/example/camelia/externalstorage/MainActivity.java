package com.example.camelia.externalstorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "getDir() ERROR: ";
    String data = "data.txt";
    File file;
    TextView tv;
    private static final String TAG = "MEDIA";
    File path;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        getDir(data);
        if (!file.exists())  getDir(data);
        tv = (TextView) findViewById(R.id.tv);
        checkExternalMedia();
        writeToSDFile();
        readRaw();
        // Get the directory for the user's public pictures directory.
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)));
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        } */
        //createExternalStoragePublicPicture();
        //writeToFile();

    }
    @Override
    public void onResume(){
        super.onResume();
        writeToFile();
    }

    private void writeToFile() {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/docs");
        myDir.mkdirs();

        File file = new File (myDir, "data.txt");
        if (file.exists ()) file.delete ();
        try {
            //File inputFile = new File("farrago.txt");
            //File outputFile = new File("data.txt");
            System.out.println("inainte de posibila erroare");
            //FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(file);
            int c;
            String str = "Here is Cami";
            byte[] strb = str.getBytes();
            /*
            while ((c = fis.read()) != -1) {
                fos.write(c);
            } */
            for(int i = 0; i < strb.length; ++i) {
                fos.write(strb[i]);
            }

            //fis.close();
            fos.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileStreamsTest: " + e);
        } catch (IOException e) {
            System.err.println("FileStreamsTest: " + e);
        }
    }

    void createExternalStoragePublicPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, "data.txt");

        // Make sure the Pictures directory exists.
        path.mkdirs();

        try {
            File inputFile = new File("farrago.txt");
            File outputFile = new File("outagain.txt");

            FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(outputFile);
            int c;

            while ((c = fis.read()) != -1) {
                fos.write(c);
            }

            fis.close();
            fos.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileStreamsTest: " + e);
        } catch (IOException e) {
            System.err.println("FileStreamsTest: " + e);
        }
            /*
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            tv.setText(is.read(data));
            os.write(data);
            is.close();
            os.close(); */

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
    }

}
