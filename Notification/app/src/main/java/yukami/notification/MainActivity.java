package yukami.notification;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.Inbox);
        Intent intent = this.getIntent();
        tv.setText(intent.getStringExtra("how"));

        Intent notifyIntent = new Intent(this,MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1000 * 9, pendingIntent);



    }

    public void sendNotification(View view) {
        switch (view.getId()) {
            case R.id.buttonBasic:
                sendBasicNotification();
                break;
            case R.id.buttonBigPicture:
                break;
            case R.id.buttonAction:
                sendActionNotification();
        }
    }

    private void sendBasicNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("Basic Notification");
        builder.setContentText("Yelloooooo from the other side!");
        builder.setSmallIcon(R.drawable.smiley);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(123, notification);
    }

    private void sendActionNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("Basic Notification");
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
