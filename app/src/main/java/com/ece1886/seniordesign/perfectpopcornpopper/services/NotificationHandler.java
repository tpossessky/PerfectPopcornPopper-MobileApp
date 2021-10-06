package com.ece1886.seniordesign.perfectpopcornpopper.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ece1886.seniordesign.perfectpopcornpopper.R;
import com.ece1886.seniordesign.perfectpopcornpopper.activities.MainActivity;

public class NotificationHandler {

    // Notification handler singleton
    private static NotificationHandler nHandler;
    private static NotificationManager mNotificationManager;

    /**
     * Singleton pattern implementation
     * @return
     */
    public static NotificationHandler getInstance(Context context) {
        if(nHandler == null) {
            nHandler = new NotificationHandler();
            mNotificationManager =
                    (NotificationManager) context.getApplicationContext()
                            .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return nHandler;
    }

    /**
     * Shows notification
     * @param context application context
     */
    public void createNotification(Context context, String msg) {

        NotificationChannel channel = new NotificationChannel("PPP",
                "Popcorn Notification",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Building the notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "PPP")
                .setSmallIcon(R.drawable.ic_notification) // notification icon
                .setColor(context.getResources().getColor(R.color.white))
                .setContentTitle("Perfect Popcorn Popper") // main title of the notification
                .setContentText(msg) // notification text
                .setContentIntent(resultPending); // notification intent

        mNotificationManager.notify(10, mBuilder.build());
    }
}
