package com.samsung.update;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallStateListener extends PhoneStateListener {
    String number=""; // variable for storing incoming/outgoing number
    Context mContext; // Application Context

    //Constructor that will accept Application context as argument
    public CallStateListener(Context context) {
        mContext=context;
    }

    // This function will automatically invoke when call state changed
    public void onCallStateChanged(int state,String incomingNumber)
    {
        boolean end_call_state=false; // this variable when true indicate that call is disconnected
        switch(state) {
            case TelephonyManager.CALL_STATE_IDLE:
                // Handling Call disconnect state after incoming/outgoing call
                Toast.makeText(mContext, "idle", Toast.LENGTH_SHORT).show();
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                // Handling outgoing call
                Toast.makeText(mContext, "OFFHOOK", Toast.LENGTH_SHORT).show();
                // saving outgoing call state so that after disconnect idle state can act accordingly
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                Toast.makeText(mContext, "RINGING", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}