package yukami.notification;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

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
        builder.setSmallIcon(R.drawable.smiley);
        Intent intent_good = new Intent(this, MainActivity.class);
        intent_good.putExtra("how", "good");
        //startActivity(intent);nu o sa deschidem noua activity cu un intent, ci o sa folosim un pending intent
        PendingIntent pIntent_good = PendingIntent.getActivity(this, 0, intent_good, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.mipmap.ic_launcher, "Good", pIntent_good);

        Intent intent_naspa = new Intent(this, MainActivity.class);
        intent_naspa.putExtra("how", "naspa");
        //startActivity(intent);nu o sa deschidem noua activity cu un intent, ci o sa folosim un pending intent
        PendingIntent pIntent_naspa = PendingIntent.getActivity(this, 1, intent_naspa, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.mipmap.ic_launcher, "Naspa", pIntent_naspa);


        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(123, notification);
    }
}