package com.cloverexamples.stock.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cloverexamples.stock.R;
import com.cloverexamples.stock.activity.MainActivity;
import com.cloverexamples.stock.utils.Constant;

/**
 * Created by dewei.kung on 6/9/16.
 */
public class MyBroadcastReceiver extends BroadcastReceiver{
    private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri dummyUri = Uri.parse("stock://");

        String content = intent.getStringExtra(Constant.EXTRA_NOTIF_CONTENT);
        if (content == null) {
            content = "";
        }

        String title = intent.getStringExtra(Constant.EXTRA_NOTIF_TITLE);
        if (title == null) {
            title = "";
        }

        generateNotification(context, title, content, dummyUri);
    }

    private void generateNotification(Context context, String title, String content, Uri dummyUri) {
        Intent intent;
        intent = new Intent(Intent.ACTION_VIEW, dummyUri, context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSmallIcon(R.drawable.app_icon)
                ;

        mBuilder.setContentIntent(contentIntent);
        Log.d(TAG, "Notify");
        mNotificationManager.notify(1, mBuilder.build());
    }
}
