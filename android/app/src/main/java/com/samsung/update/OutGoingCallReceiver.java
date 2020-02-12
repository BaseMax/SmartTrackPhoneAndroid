package com.samsung.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutGoingCallReceiver extends BroadcastReceiver {
    /* onReceive will execute on out going call */
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "OutGoingCallReceiver", Toast.LENGTH_SHORT).show();
    }
}
