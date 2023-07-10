package com.example.spyfall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ForegroundTimer extends Service {


    PowerManager.WakeLock wakeLock = null;
    private boolean isServiceStarted = false;

    String TAG = "ForegroundTimer";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Some component want to bind with the service");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.i(TAG, "onStartCommand executed with startId: " + startId);
        if (intent != null) {
            startService(intent);

            String action = intent.getAction();
            Log.i(TAG, "using an intent with action " + action);

//            switch (action){
//                case "START":
//                    startService();
//                    break;
//                case "STOP":
//                    stopService();
//                    break;
//                default:
//                    Log.i(TAG, "Unexpected value: " + action);
//            }
        } else {
            Log.i(TAG, "with a null intent. It has been probably restarted by the system");
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.i(TAG, "The service has been created".toUpperCase());

        Notification notification = createNotification();
        startForeground(1, notification);

        super.onCreate();
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "The service has been destroyed".toUpperCase());
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    @Nullable
    @Override
    public ComponentName startService(Intent service) {


        if (isServiceStarted){

        }else {

            Log.i(TAG, "Starting the foreground service task");
            Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
            isServiceStarted = true;


//        setServiceState(this, ServiceState.STARTED);


            // we need this lock so our service gets not affected by Doze Mode
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.wakeLockKey));
            wakeLock.acquire();


            // we're starting a loop in a coroutine

            new CountDownTimer(60000, 1000) {
                public void onFinish() {
                    // When timer is finished
                    // Execute your code here
                }

                public void onTick(long millisUntilFinished) {
                    Log.i(TAG, "The service working".toUpperCase());
                }
            }.start();
        }

        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {

        Log.i(TAG, "Stopping the foreground service");
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();

        try {
            if(wakeLock.isHeld()){
                wakeLock.release();
            }
            stopForeground(true);
            stopSelf();

        } catch (Exception e) {
            Log.e(TAG, "Service stopped without being started: " + e.toString());
        }
        isServiceStarted = false;

//        setServiceState(this, ServiceState.STOPPED)
        return super.stopService(name);
    }

    Notification createNotification()  {
        String notificationChannelId = "ENDLESS SERVICE CHANNEL";

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel;
            channel = new NotificationChannel(
                    notificationChannelId,
                    "asd",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Endless Service channel");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);



        Notification.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this,notificationChannelId);
        }else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("Endless Service");


        builder.setContentTitle("Endless Service");
        builder.setContentText("This is your favorite endless service working");
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Ticker text");
        builder.setPriority(Notification.PRIORITY_HIGH); // for under android 26 compatibility


        return builder.build();
    }
}
