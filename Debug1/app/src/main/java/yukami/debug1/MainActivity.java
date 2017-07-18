package yukami.debug1;

import android.support.v7.app.AppCompatActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

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

    /** Called when the user touches the button */
    public void sendMessage(View view) {
        Button buton = (Button)view;
        String cumAFostZiulica = buton.getText().toString();

        // proba ca sa vad ca functioneaza butoanele
        //TextView txtView = (TextView)findViewById(R.id.intrebare) ;
        //txtView.setText("Hello");

        // get day of the week
        Date now = new Date();
        System.out.println("toString(): " + now);  // dow mon dd hh:mm:ss zzz yyyy


        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        //System.out.println("Format 3:   " + dateFormatter.format(now));

        writeToFile(dateFormatter.format(now) + " " + cumAFostZiulica,getApplicationContext());
        TextView data = (TextView) findViewById(R.id.date);

        data.setText(readFromFile(getApplicationContext()));

        //--------------- pana aici cu scrierea si citirea de fisiere -----------------------

        // Key for the string that's delivered in the action's intent. falta(private static)
        final String KEY_TEXT_REPLY = "good_reply";
        String replyLabel = getResources().getString(R.string.reply_good);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();


        // Create the reply action and add the remote input.
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Action action_good = new NotificationCompat.Action.Builder(R.drawable.good, "Good", replyPendingIntent).build();
        NotificationCompat.Action action_naspa = new NotificationCompat.Action.Builder(R.drawable.good, "Naspa", replyPendingIntent).build();

        /*Notification.Action action =
                new Notification.Action.Builder(R.drawable.good,
                        getString(R.string.reply_good), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build(); **/

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.smiley)
                        .setContentTitle("KY")
                        .setContentText("Cum ti-a fost ziulica?");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        //adaugam actiunea la notificare
        mBuilder.addAction(action_good);
        mBuilder.addAction(action_naspa);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        mNotificationManager.notify(12345, mBuilder.build());
    }
}
