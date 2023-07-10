package com.example.spyfall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class BroadcastService extends Service {
    private String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "com.example.spyfall.COUNTDOWN_BR";
    Intent intent = new Intent(COUNTDOWN_BR);
    CountDownTimer countDownTimer = null;

    SoundPool soundPool;
    int sound;



    public void setTimer(){
        Log.i(TAG,"Starting timer..." + getPackageName());

        SharedPreferences sharedPreferences;

        sharedPreferences = getSharedPreferences(getString(R.string.preferenceFileKey),MODE_MULTI_PROCESS);

        sharedPreferences.edit().putBoolean("isWork", true).apply();

        long millis = sharedPreferences.getLong("timeSP2",3000);

        Log.i(TAG,"Get Shared : " + millis);

        countDownTimer = new CountDownTimer(millis,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);
                intent.putExtra("countdown",millisUntilFinished);
                intent.putExtra("isWork",true);
                sharedPreferences.edit().putLong("countdown", millisUntilFinished).apply();
                intent.putExtra("timeSP2",millis);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                Log.i(TAG,"Finish");
                intent.putExtra("isWork",true);

                Log.i(TAG,"Countdown seconds remaining:" + 0);
                intent.putExtra("countdown",0);
                intent.putExtra("isWork",false);

                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(intent);


//                launchApp(getApplicationContext(), getPackageName());
//                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setTitle("Инструкция")
//                        .setMessage("1) Через проводник выберите нужно вам количество локаций\n2) Нажмите \"поделиться\"\n3) Выберите приложение \"" + getApplicationContext().getApplicationInfo().loadLabel(getPackageManager()).toString() + "\"")
//                ;
//                builder.create().show();

//                stopSelf();

                sharedPreferences.edit().putBoolean("isWork", false).apply();
                soundPool.play(sound, 1, 1, 0, 0, 1);
            }
        };
        countDownTimer.start();
    }

    public void launchApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

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


        Log.i(TAG,"Created! : " + getPackageName());
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        Log.i(TAG,"Destroyed! : " + getPackageName());
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
