package com.example.doc_app_android.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.doc_app_android.actvities.Home;
import com.example.doc_app_android.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
        boolean isDoctor = preferences.getBoolean("isDoc",false);




        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if (isDoctor && notification.getTitle().equals("NewAppointment")){
            sendNotification("Appointment Request", notification.getBody());
        } else if(isDoctor && notification.getTitle().equals("Patient Report Shared")){
            sendNotification(notification.getTitle(), notification.getBody());
        } else if(!isDoctor && notification.getTitle().equals("Appointment")){
            sendNotification("Appointment Status", notification.getBody());
        }

        Log.d("NotificationTitles", "Title->" + notification.getTitle() + ".");

    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        String channelId = getString(R.string.default_notification_channel_id);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}