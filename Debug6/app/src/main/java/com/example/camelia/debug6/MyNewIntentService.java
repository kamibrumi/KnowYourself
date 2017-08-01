package com.example.camelia.debug6;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MyNewIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 3;


    public MyNewIntentService() {
        super("MyNewIntentService");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("KY");
        builder.setContentText("Cum ti-a fost ziulica?!");
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent intent_good = new Intent(this, ResponseActivity.class);
        intent_good.putExtra("how", "Good");
        //startActivity(intent);nu o sa deschidem noua activity cu un intent, ci o sa folosim un pending intent
        PendingIntent pIntent_good = PendingIntent.getActivity(this, 0, intent_good, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.mipmap.ic_launcher, "Good", pIntent_good);

        Intent intent_naspa = new Intent(this, ResponseActivity.class);
        intent_naspa.putExtra("how", "Bad");
        //startActivity(intent);nu o sa deschidem noua activity cu un intent, ci o sa folosim un pending intent
        PendingIntent pIntent_naspa = PendingIntent.getActivity(this, 1, intent_naspa, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.mipmap.ic_launcher, "Bad", pIntent_naspa);


        final Notification notification = builder.build();

/*
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ResponseActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent_good);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent); */

        final NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().

        manager.notify(123, notification);

        Timer timer=new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                manager.cancel(123);
            }
        };

        timer.schedule(task, 1000 * 3 * 60 * 60 ); //TODO: if there is a more efficient way to make the notification dissapear after the midnight
    }
}