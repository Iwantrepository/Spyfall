package com.example.spyfall;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ForegroundTimer extends Service {


    boolean isNotifyAllowed = false;
    PowerManager.WakeLock wakeLock = null;
    private boolean isServiceStarted = false;

    CountDownTimer countDownTimer = null;

    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    PendingIntent pendingIntent;

    public static final String COUNTDOWN_BR = "com.example.spyfall.COUNTDOWN_BR";
    Intent intentBR = new Intent(COUNTDOWN_BR);

    String TAG = "ForegroundTimer";

    SoundPool soundPool;
    int sound;

    int notificationId = 2999;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Some component want to bind with the service");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (isServiceStarted) {
            //
        } else {

            Log.i(TAG, "Starting the foreground service task");
//            Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
            isServiceStarted = true;




            // we need this lock so our service gets not affected by Doze Mode
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.wakeLockKey));
            wakeLock.acquire(1000 * 60 * 30);


            // we're starting a loop in a coroutine

            Log.i(TAG,"Starting timer..." + getPackageName());

            SharedPreferences sharedPreferences;

            sharedPreferences = getSharedPreferences(getString(R.string.preferenceFileKey),MODE_PRIVATE);

            sharedPreferences.edit().putBoolean("isWork", true).apply();

            long millis = sharedPreferences.getLong("timeSP2",3000);

            Log.i(TAG,"Get Shared : " + millis);

            countDownTimer = new CountDownTimer(millis,200) {
                // TODO Перенести интервал куда-то в глобальное место
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);
                    intentBR.putExtra("countdown",millisUntilFinished);
                    intentBR.putExtra("isWork",true);
                    intentBR.putExtra("timeSP2",millis);
                    intentBR.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);


                    long sec = millisUntilFinished / 1000;

                    builder.setPriority(Notification.PRIORITY_LOW); // for under android 26 compatibility
                    builder.setContentText("Countdown: " + sec/60 + ":" + sec%60);
                    notificationManager.notify(notificationId, builder.build());

                    sendBroadcast(intentBR);
                }

                @Override
                public void onFinish() {
                    Log.i(TAG,"Finish");
                    intentBR.putExtra("isWork",true);

                    Log.i(TAG,"Countdown seconds remaining:" + 0);
                    intentBR.putExtra("countdown",0);
                    intentBR.putExtra("isWork",false);

                    intentBR.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intentBR);

                    sharedPreferences.edit().putBoolean("isWork", false).apply();
                    soundPool.play(sound, 1, 1, 0, 0, 1);



                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            stopSelf();
                        }
                    }, 5000);
                }
            };
            countDownTimer.start();
        }

//        return super.startService(service);

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.i(TAG, "The service has been created".toUpperCase());

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        sound = soundPool.load(this, R.raw.alarm, 1);

        Notification notification = createNotification();
        Log.i(TAG, notification.toString());
        startForeground(notificationId, notification);
        isNotifyAllowed = true;

        super.onCreate();
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "Stopping the foreground service");
//        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();

        try {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
            stopForeground(true);
            stopSelf();

        } catch (Exception e) {
            Log.e(TAG, "Service stopped without being started: " + e.toString());
        }
        isServiceStarted = false;

//        setServiceState(this, ServiceState.STOPPED)

        countDownTimer.cancel();

        super.onDestroy();
    }


    @SuppressLint("MissingPermission")
    Notification createNotification() {
        String notificationChannelId = "ENDLESS SERVICE CHANNEL";

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel;
            channel = new NotificationChannel(
                    notificationChannelId,
                    "SP2Timer",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Endless Service channel");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, notificationChannelId);
        } else {
            builder = new NotificationCompat.Builder(this);
        }



        builder.setContentTitle("Spyfall 2 timer");
        builder.setContentText("Timer start");
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_MAX); // for under android 26 compatibility

        Notification not = builder.build();

        Log.i(TAG, not.toString());

        return not;
    }
}
