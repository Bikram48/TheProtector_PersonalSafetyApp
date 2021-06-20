package com.example.theprotector;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String FCM_CHANNEL_ID="FCM_CHANNEL_ID";
    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(FCM_CHANNEL_ID,"channel id", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This is the notification channel");
            NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
