package com.example.spyfall;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BroadcastService extends Service {
    private String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "com.example.spyfall.COUNTDOWN_BR";
    Intent intent = new Intent(COUNTDOWN_BR);
    CountDownTimer countDownTimer = null;



    public void setTimer(){
        Log.i(TAG,"Starting timer..." + getPackageName());

        SharedPreferences sharedPreferences;

        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);



        long millis = sharedPreferences.getLong("timeSP2",3000);

        Log.i(TAG,"Get Shared : " + millis);

        countDownTimer = new CountDownTimer(millis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);
                intent.putExtra("countdown",millisUntilFinished);
                intent.putExtra("isWork",true);
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
//                stopSelf();
            }
        };
        countDownTimer.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

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
