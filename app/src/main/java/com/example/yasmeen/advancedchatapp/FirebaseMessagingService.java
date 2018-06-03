package com.example.yasmeen.advancedchatapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        String from_user_id = remoteMessage.getData().get("from_user_id");
        String from_user_status = remoteMessage.getData().get("from_user_status");
        String from_user_name = remoteMessage.getData().get("from_user_name");
        String from_user_image = remoteMessage.getData().get("from_user_image");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody);
        Intent resultIntent = new Intent(click_action);
        Users users =new Users(from_user_name,from_user_image,from_user_status,from_user_id);
        resultIntent.putExtra("User",users);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        int mNotificationId =(int)System.currentTimeMillis();
        NotificationManager mNotifyMngr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifyMngr.notify(mNotificationId,mBuilder.build());
    }
}
