package com.example.camelia.intentservicetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.Set;


public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MyIntentService.class);
        context.startService(intent1);
    }
}
