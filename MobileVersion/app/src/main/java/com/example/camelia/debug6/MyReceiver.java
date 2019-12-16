package com.example.camelia.debug6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        Intent intent1 = new Intent(context, NotificationIntentService.class);
        context.startService(intent1);

    }
}