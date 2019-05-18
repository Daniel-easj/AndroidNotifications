package com.dba.notificationstestproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);

        createNotificationChannels();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("FIREBASEID", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log token
                        Log.d("FIREBASEID", token);
                    }
                });
    }


    public void simpleNotification(View view) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification Title")
                .setContentText("Main content of notification");

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void simpleNotificationAction(View view) {
        Intent intent = new Intent(this, NotificationActionExample.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification with Action")
                .setContentText("Click this notification to go to an Activity")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, notificationBuilder.build());
    }

    public void ongoingDownloadProgress(View view) {

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "TestID2");
        notificationBuilder.setContentTitle("App Downloading")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true);


        new Thread(new Runnable() {
            @Override
            public void run() {
                int maxProgress = 100;
                int currentProgress = 0;
                while(currentProgress < maxProgress){

                    notificationBuilder.setProgress(maxProgress, currentProgress, false);
                    notificationManager.notify(2, notificationBuilder.build());

                    currentProgress += 1;
                    SystemClock.sleep(50);
                }

                notificationBuilder.setContentText("Download complete")
                        .setProgress(0,0,false)
                        .setOngoing(false);
                notificationManager.notify(2, notificationBuilder.build());
            }
        }).start();
    }

    public void insistentNotification(View view) {

        Notification notification = new NotificationCompat.Builder(this, "TestID")
                .setContentTitle("Alarm App")
                .setContentText("Alarm is active")
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .build();

        notification.flags = Notification.FLAG_INSISTENT;

        notification.vibrate = new long[]{500, 500, 500};

        notificationManager.notify(3, notification);
    }


    private void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create Channel Groups
            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannelGroup(new NotificationChannelGroup("AlarmGroup", "High Priority Notifications"));
            manager.createNotificationChannelGroup(new NotificationChannelGroup("Reminders", "Medium Priority Notifications"));

            // Create "Alerts" notification channel
            CharSequence name = "Alerts";
            String description = "Notifications with alarms";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channelAlarm = new NotificationChannel("TestID", name, importance);
            channelAlarm.setDescription(description);
            channelAlarm.setGroup("AlarmGroup");

            // Create "Reminders" notification channel
            NotificationChannel channelReminder = new NotificationChannel("TestID2", "Reminders", NotificationManager.IMPORTANCE_LOW);
            channelReminder.setGroup("Reminders");

            // Register channels in systems
            manager.createNotificationChannel(channelAlarm);
            manager.createNotificationChannel(channelReminder);
        }
    }



}
