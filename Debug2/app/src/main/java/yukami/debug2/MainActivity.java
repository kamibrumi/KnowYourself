package yukami.debug2;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView raspuns, debug;
    Button button_great;
    Button button_naspa;
    TextView question;
    Boolean answered;

    //Daca o sa avem nevoie:
    /*

                    String date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
                            + String.valueOf(calendar.get(Calendar.MONTH))
                            + String.valueOf(calendar.get(Calendar.YEAR));

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        raspuns = (TextView) findViewById(R.id.raspuns);
        button_great = (Button) findViewById(R.id.buna);
        button_naspa = (Button) findViewById(R.id.naspa);
        question = (TextView) findViewById(R.id.intrebare);

        button_great.setVisibility(View.GONE);
        button_naspa.setVisibility(View.GONE);
        question.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (hour < 15) {
            raspuns.setText("Asteptam raspunsul dumneavoastra la orele 19."); //TODO: schimba la orele 21
            writeToFile("answered.txt", String.valueOf(false), this);
        } else {
            answered = Boolean.valueOf(readFromFile("answered.txt", this));
            if (!answered) {
                //WE LAUNCH THE NOTIFICATION
                Intent notifyIntent = new Intent(this,MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1000 * 12 * 60 * 60, // TODO: CHANGE THIS TO System.currentTimeMillis() OR TO 1000*21*60*60
                        1000 * 24 * 60 * 60, pendingIntent);

                button_great.setVisibility(View.VISIBLE); //TODO: daca nu functioneaza sa sterg (FUNCTIONEAZA!)
                button_naspa.setVisibility(View.VISIBLE);
                question.setVisibility(View.VISIBLE);
                raspuns.setText("");

                /*
                //find out if the main activity was launched by the notification
                Intent intent = this.getIntent();
                String gb = intent.getStringExtra("how");
                if (gb == "Buna" || gb == "Naspa") {
                    //Log.i("WHAT IS GB?", gb);
                    debug = (TextView) findViewById(R.id.debug);
                    debug.setText("vin de la notificare");
                    //scriem in fisier data + cum a fost ziua
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    writeToFile("data.txt", day + " " + gb + '\n', getApplicationContext());
                    raspuns.setText("Va multumim de raspuns! Asteptam raspunsul tau si maine la orele 21.");
                    button_great.setVisibility(View.GONE);
                    button_naspa.setVisibility(View.GONE);
                    question.setVisibility(View.GONE);

                } else {
                    button_great.setVisibility(View.VISIBLE);
                    button_naspa.setVisibility(View.VISIBLE);
                    question.setVisibility(View.VISIBLE);
                    raspuns.setText("");
                }

                */
            }
        }
    }

    private void writeToFile(String fileName, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter;
            if (fileName == "data.txt") outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            else outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data + '\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String fileName, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

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

    public void sendMessage(View view) {
        //boolean answered = raspuns.getText() != "Inca nu ati raspuns la intrebare";
        //Log.i("SENDMESSAGE", String.valueOf(answered));
        answered = Boolean.valueOf(readFromFile("answered.txt", this));
        if (!answered) {
            Button button = (Button) view;
            String howWasYourDay = button.getText().toString();

            // get day of the week
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            //we write in the file the number of the day + how was it
            writeToFile("data.txt", day + " " + howWasYourDay + '\n', getApplicationContext()); //REMEMBER: the first day is sunday

            raspuns.setText("Va multumim de raspuns, butoanele au sa ramana inactive pana la 21h din ziua urmatoare.");
            button_great.setVisibility(View.GONE);
            button_naspa.setVisibility(View.GONE);
            question.setVisibility(View.GONE);
            writeToFile("answered.txt", String.valueOf(true), this);

            //we cancel the notification
            NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(123);
        }
    }
}
