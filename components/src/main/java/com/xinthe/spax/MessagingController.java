package com.xinthe.spax;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xinthe.spax.services.BeaconScanner;
import com.xinthe.spax.services.MyFirebaseInstanceIdService;
import com.xinthe.spax.utils.Constants;

/**
 * Created by xinthe on 17-Nov-16.
 */
public class MessagingController extends BroadcastReceiver{
    Context parentContext;
    Intent instanceIdServiceIntent;
    BroadcastReceiver broadcastReceiver;
    public static final String NOTIFICATION = "com.xinthe.spax.messaging";
    public static final String MESSAGE_CONTENT = "message_content";
    public static final String DATA_PAYLOAD = "data_payload";
    CloudMessagingInterface messagingInterface;
    Activity parentActivity;

    public MessagingController(Activity parentActivity, CloudMessagingInterface messagingInterface)
    {
        this.parentActivity = parentActivity;
        this.messagingInterface = messagingInterface;
        this.parentContext =  parentActivity.getApplicationContext();
        this.instanceIdServiceIntent = null;

        startService();
    }

    public Activity GetParentActivity()
    {
        return parentActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            messagingInterface.onMessageReceived(bundle);
        }
    }

    /**
     * Start beacons scanning service running in background.
     */
    private void startService() {

        parentActivity.registerReceiver(this, new IntentFilter(MessagingController.NOTIFICATION));
    }

    /**
     * Start beacons scanning service running in background.
     */
    private void stopService() {
        parentActivity.unregisterReceiver(this);
    }

}
