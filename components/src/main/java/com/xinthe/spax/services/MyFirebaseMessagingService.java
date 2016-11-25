package com.xinthe.spax.services;

/**
 * Created by xinthe on 17-Nov-16.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.xinthe.spax.BeaconCollector;
import com.xinthe.spax.MessagingController;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //Toast.makeText(this,
          //      "Notification getfrom"+remoteMessage.getFrom(), Toast.LENGTH_LONG).show();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
          //  Toast.makeText(this,
            //        "Notification data "+remoteMessage.getData(), Toast.LENGTH_LONG).show();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
           // Toast.makeText(this,
             //       "Notification body"+remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
        }

        sendNotification(remoteMessage);
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message  received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(MessagingController.NOTIFICATION);
        Map<String, String> params = remoteMessage.getData();
        JSONObject jsonobject = new JSONObject(params);

        intent.putExtra(MessagingController.DATA_PAYLOAD,  jsonobject.toString());
        intent.putExtra(MessagingController.MESSAGE_CONTENT, remoteMessage.getNotification().getBody());
        sendBroadcast(intent);

        /*
        Intent intent2 = new Intent(this, MessagingController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(MessagingController.NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);



        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());
        */
    }
}