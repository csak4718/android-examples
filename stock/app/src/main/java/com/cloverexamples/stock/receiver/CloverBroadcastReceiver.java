package com.cloverexamples.stock.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clover.sdk.v1.Intents;
import com.cloverexamples.stock.R;
import com.cloverexamples.stock.activity.MainActivity;

/**
 * Created by dewei.kung on 6/10/16.
 */
public class CloverBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = CloverBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "action: " + action);
        if (action.equals(Intents.ACTION_PAYMENT_PROCESSED)) {
            String orderId = intent.getStringExtra(Intents.EXTRA_CLOVER_ORDER_ID);
            showNotification(context, orderId);
        }
    }

    private void showNotification(Context context, String orderId) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Clover notification")
                        .setContentText(orderId);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "Clover Notify");
        mNotificationManager.notify(1, mBuilder.build());

    }
}
