package com.example.spyfall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.StringWriter;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import com.google.android.material.circularreveal.CircularRevealHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity {


    public final static int DEV_PASSCODE = 1638;

    public final static int REQUEST_CODE_LOCATIONS = 1;
    public final static int REQUEST_CODE_NEW_LOCATION = 2;
    public final static int REQUEST_CODE_PARTY_CONFIG = 3;
    public final static int REQUEST_CODE_INFO_BAR = 4;

    public final static int REQUEST_CODE_UPLOAD_LOCATIONS = 10;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    Animation scaleUp, scaleDown;

    Menu menuBox;

    Intent intentTimer;
    String TAG = "Timer";

    //Класс состояния игры
    class GameState {

        public int mData = 0;
        String logString;

        String[] str_list;
        String path;
        String pathToEx;
        String pathFromToUpload;

        String dataConfig;

        String[] loc = new String[8];
        String[] prof = new String[8];

        int devCode;
        boolean isDevOn;

        boolean game_started = false;
        boolean is_game_worked = true;
        int gamers = 0;
        int spys = 1;
        int lastLocs = 0;
        int presentedLocs = 0;
        int gamersFromConfig = 0;

        boolean isNeedPoolLoc = true;

        boolean isPrestartGamers = false;
        int prestartGamers;


        boolean[] ispressed = new boolean[8];

        List<Integer> spyList = new ArrayList<>();

        String locToPool = "";
    }

    GameState game_state;




    Button[] buttons = new Button[8];
    Button button_reset;
    Button button_start;

    Button buttonTimer;



    ImageView spyImage;

    CountDownTimer timer;
    boolean isInTimer = false;



    TextView textViewLoc;
    TextView textViewProf;
    TextView locsWithoutPool;


    Intent myFileIntent;

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        Toast.makeText(getApplicationContext(), "Saved " + String.valueOf(mdata) , Toast.LENGTH_SHORT).show();
        game_state.mData++;


        GsonBuilder builder = new GsonBuilder().serializeNulls();
        Gson gson = builder.create();
        String json= gson.toJson(game_state).toString();
        Log.i("JSON > saved", json);
        outState.putString("game_state", json);

//        Toast.makeText(getApplicationContext(), "Saved " + json, Toast.LENGTH_SHORT).show();

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        String json = savedInstanceState.getString("game_state");
        Log.i("JSON > recieved", json);

        Gson gson = new Gson();
        game_state = gson.fromJson(json, game_state.getClass());


//        Toast.makeText(getApplicationContext(), "Restored " + String.valueOf(game_state.mData), Toast.LENGTH_SHORT).show();
        super.onRestoreInstanceState(savedInstanceState);






        // Обработка развертывания
//        Toast.makeText(getApplicationContext(), "Saved time", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < 8; i++) {
//            buttons[i].setBackground(getDrawable(R.drawable.button_back_on));

            if(game_state.game_started){
                button_start.setVisibility(View.GONE);

                if(game_state.ispressed[i]) {
                    buttons[i].setBackground(getDrawable(R.drawable.button_back_off));
                }else{
                    buttons[i].setBackground(getDrawable(R.drawable.button_back_on));
                }
                if(i+1 > game_state.gamers) {
                    buttons[i].setBackground(getDrawable(R.drawable.button_back_default));
                    buttons[i].setEnabled(false);
                }
            }else {
                buttons[i].setBackground(getDrawable(R.drawable.button_back_default));

                if(game_state.isPrestartGamers){
                    buttons[game_state.prestartGamers-1].setBackground(getDrawable(R.drawable.button_back_gold));
                }
            }

        }
        parseConfig(game_state.dataConfig);
        if(game_state.isPrestartGamers){
            game_state.gamers = game_state.prestartGamers;
        }
        refresh_loc_list();

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, STORAGE_SERVICE);
        int permission = ContextCompat.checkSelfPermission(activity , READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menuBox = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

//        Toast.makeText(getApplicationContext(), "INTENT", Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), intent.getAction(), Toast.LENGTH_LONG).show();
        game_state.logString += "\n▼ INTENT ▼\n";
        intentDisassembler(intent);


    }

    public void intentDisassembler(Intent intent){
        if (intent!=null){
            String action = intent.getAction();
            String type = intent.getType();

            game_state.logString += action + "\n" + type + "\n";

            if(Intent.ACTION_SEND.equals(action) && type != null){
                if(type.equalsIgnoreCase("text/plain")){
                    handleTextData(intent);
                }
            }
            if(Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null){
                handleMutlipleTextData(intent);
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.info_bar:
                Intent infoBar = new Intent(this, infoBar.class);

                infoBar.putExtra("path", game_state.path);
                infoBar.putExtra("pathFromToUpload", game_state.pathFromToUpload);
                infoBar.putExtra("logString", game_state.logString);
                startActivityForResult(infoBar, REQUEST_CODE_INFO_BAR);



                break;

            case R.id.party_config:
                Intent partyConfig = new Intent(this, PartyConfig.class);
                partyConfig.putExtra("path", game_state.path);
                startActivityForResult(partyConfig, REQUEST_CODE_PARTY_CONFIG);

                refresh_loc_list();
                break;

            case R.id.locations:
                refresh_loc_list();
                Intent  locations = new Intent(this, locations_list.class);
                locations.putExtra("str_list", game_state.str_list);
                locations.putExtra("path", game_state.path);
                locations.putExtra("pathFromToUpload",game_state.pathFromToUpload);
                locations.putExtra("pathToEx",game_state.pathToEx);
                startActivityForResult(locations, REQUEST_CODE_LOCATIONS);
                break;

            case R.id.add_location:
                Intent  add_locations = new Intent(this, new_location_form.class);
                add_locations.putExtra("path", game_state.path);
                startActivityForResult(add_locations, REQUEST_CODE_NEW_LOCATION);

                break;

            case R.id.upload_locations:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Инструкция")
                        .setMessage("1) Через проводник выберите нужно вам количество локаций\n2) Нажмите \"поделиться\"\n3) Выберите приложение \"" + getApplicationContext().getApplicationInfo().loadLabel(getPackageManager()).toString() + "\"")
                ;
                builder.create().show();

//                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                myFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                myFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                myFileIntent.setType("text/plain");
//                startActivityForResult(myFileIntent, REQUEST_CODE_UPLOAD_LOCATIONS);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_UPLOAD_LOCATIONS:
                if(resultCode == RESULT_OK)
                {
                    if(null != data) { // checking empty selection
                        String []fileList;
                        Uri[] uriList;
                        if(null != data.getClipData()) { // checking multiple selection or not
                            fileList = new String[data.getClipData().getItemCount()];
                            uriList = new Uri[data.getClipData().getItemCount()];
                            for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();

                                fileList[i] = uri.getPath();
                                uriList[i] = uri;
                            }
                        } else {
                            fileList = new String[1];
                            uriList = new Uri[1];
                            Uri uri = data.getData();

                            fileList[0] = uri.getPath();
                            uriList[0] = uri;
                        }


                        for (Uri uri : uriList) {
                            if (uri != null) {
                                copyFile(uri, game_state.path);
                            }
                        }
                    }
                }
                refresh_loc_list();
                break;

            case REQUEST_CODE_LOCATIONS:
            case REQUEST_CODE_INFO_BAR:
            case REQUEST_CODE_NEW_LOCATION:
                refresh_loc_list();
                break;

            case REQUEST_CODE_PARTY_CONFIG:
                //Toast.makeText(getApplicationContext(), "Возврат от настройки партии" , Toast.LENGTH_SHORT).show();
                if (data == null) {return;}
                String strList = data.getStringExtra("strList");
                //Toast.makeText(getApplicationContext(), strList , Toast.LENGTH_SHORT).show();
                game_state.dataConfig = strList;
                parseConfig(strList);
            break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);


//        Intent intent2 = getIntent();
//        Toast.makeText(getApplicationContext(), intent2.getAction() , Toast.LENGTH_SHORT).show();
//        intentDisassembler(intent2);


/*******************************************************/
        game_state = new GameState();
        game_state.logString = "";
        game_state.devCode = 0;
        game_state.isDevOn = false;
/*******************************************************/


        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        File dir;
        String FolderName = "Spyfall";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/"+FolderName );
//            Toast.makeText(getApplicationContext(), "DOC", Toast.LENGTH_SHORT).show();
            game_state.logString += "StoragePick = DOCUMENT\n";
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/"+FolderName);
//            Toast.makeText(getApplicationContext(), "EXT", Toast.LENGTH_SHORT).show();
            game_state.logString += "StoragePick = EXTERNAL\n";
        }
        dir.mkdirs();

        game_state.pathToEx = dir.toString();
        game_state.path = getApplicationContext().getFilesDir().getPath() + "/Spyfall";
        game_state.pathFromToUpload = dir.toString();

        game_state.logString += "pathToEx = " + game_state.pathToEx + "\n";
        game_state.logString += "path = " + game_state.path + "\n";
        game_state.logString += "pathFromToUpload = " + game_state.pathFromToUpload + "\n";


        spyImage = (ImageView) findViewById(R.id.imageViewSpy);
        int imageSpyRes = getResources().getIdentifier("@mipmap/ic_launcher_foreground", null, this.getPackageName());
        spyImage.setImageResource(imageSpyRes);
        spyImage.setAlpha((float) 0 );

        locsWithoutPool = (TextView) findViewById(R.id.locsWithoutPool);


        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttons[0] = (Button) findViewById(R.id.button1);
        buttons[1] = (Button) findViewById(R.id.button2);
        buttons[2] = (Button) findViewById(R.id.button3);
        buttons[3] = (Button) findViewById(R.id.button4);
        buttons[4] = (Button) findViewById(R.id.button5);
        buttons[5] = (Button) findViewById(R.id.button6);
        buttons[6] = (Button) findViewById(R.id.button7);
        buttons[7] = (Button) findViewById(R.id.button8);

        button_reset = (Button) findViewById(R.id.button_reset);
        button_start = (Button) findViewById(R.id.button_start);

        buttonTimer = (Button) findViewById(R.id.buttonTimer);


        if (savedInstanceState != null){
            //
        }else {
//            Toast.makeText(getApplicationContext(), "First time", Toast.LENGTH_SHORT).show();

            refresh_loc_list();

            for (int i = 0; i < 8; i++) {
                buttons[i].setBackground(getDrawable(R.drawable.button_back_default));
            }

            for (int i = 0; i < 8; i++) {
                game_state.loc[i] = Integer.toString(i + 1);
                game_state.prof[i] = Integer.toString(i + 1);
                game_state.ispressed[i] = false;
            }

            game_state.dataConfig = readFile(game_state.path + "/config");
            String configPath = game_state.path + "/config";

            if (game_state.dataConfig == null) {
                //Toast.makeText(getApplicationContext(), "Файл настроек отсутствует" , Toast.LENGTH_SHORT).show();
                File buff = new File(configPath);
                try {
                    buff.createNewFile();
                    if (writeFile(configPath, "1\n+1\n+2\n+3\n+4\n+5\n+6\n+7\n+8")) {
                        game_state.dataConfig = readFile(configPath);
                        Toast.makeText(getApplicationContext(), "Файл настроек создан", Toast.LENGTH_SHORT).show();
                        if (game_state.dataConfig != null) {
                            if (!parseConfig(game_state.dataConfig))
                                parseConfig(readFile(configPath));
                        } else {
                            Toast.makeText(getApplicationContext(), "Ошибка чтения файла настроек", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Ошибка записи файла настроек", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Невозможно создать файл настроек", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                //Toast.makeText(getApplicationContext(), "Файл настроек наден" , Toast.LENGTH_SHORT).show();
                if (!parseConfig(game_state.dataConfig))
                    parseConfig(readFile(configPath));
            }

            parseConfig(game_state.dataConfig);
            game_state.prestartGamers = game_state.gamers;
        }

        textViewLoc = (TextView) findViewById(R.id.textViewLoc);
        textViewProf = (TextView) findViewById(R.id.textViewProf);

        button_reset.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        button_reset.setTextColor(Color.parseColor("#000000"));
                        button_reset.startAnimation(scaleDown);
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        button_reset.startAnimation(scaleUp);
                    }
                return false;
            }
        });


        button_reset.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(getApplicationContext(), "Карты пересданы" , Toast.LENGTH_SHORT).show();
                button_reset.setTextColor(Color.parseColor("#FF0000"));

                resetGameButtons();

                Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vi.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vi.vibrate(100);
                }
                game_state.isNeedPoolLoc = true;
                updateLocCounter();
                return true;
            }
        });




        //Timer
        buttonTimer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            public void onClick(View v){


                if(isInTimer){
                    stopService(new Intent(getApplicationContext(), BroadcastService.class));
                    buttonTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer_off,0,0,0);
                    isInTimer = false;
                }else{
//                    SharedPreferences sharedPreferences;
//                    sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//                    sharedPreferences.edit().putLong("timeSP2", 30000).apply();
//                    Log.i(TAG, "Shared long: " + sharedPreferences.getLong("timeSP2", 9));

                    Intent intent = new Intent(getApplicationContext(), BroadcastService.class);
                    startService(intent);
                    Log.i(TAG, "" + intent);
                    Log.i(TAG, "Started Service" + getPackageName());
                    isInTimer = true;
                    buttonTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer_on,0,0,0);
                }

            }
        });


        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                button_start.startAnimation(scaleUp);
                refresh_loc_list();

                if(game_state.is_game_worked)
                {
                    if(game_state.gamers > 2)
                    {
                        boolean is_started = game_prepare(game_state.gamers, game_state.spys);
                        if(is_started)
                        {
                            game_state.game_started = true;
                            button_start.setVisibility(View.GONE);
                            //Toast.makeText(getApplicationContext(), "Запущена игра на " + gamers + " игроков" , Toast.LENGTH_SHORT).show();
                        }else{
                            //
                        }

                    }else{
                        //Toast.makeText(getApplicationContext(), "Мало игроков для начала игры" , Toast.LENGTH_SHORT).show();
                        if(game_state.gamers > 2){
                            boolean is_started = game_prepare(game_state.gamers, game_state.spys);
                            if(is_started)
                            {
                                game_state.game_started = true;
                                button_start.setVisibility(View.GONE);
                                //Toast.makeText(getApplicationContext(), "Запущена игра на " + gamers + " игроков" , Toast.LENGTH_SHORT).show();
                            }else{
                                //
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Мало игроков: " + String.valueOf(game_state.gamers) , Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Обнаружены критические неполадки" , Toast.LENGTH_SHORT).show();
                }
            }
        });


//Поведение 8 кнопок
        for (int i = 0; i < 8; i++){
            int finalI = i;
            buttons[finalI].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        buttons[finalI].startAnimation(scaleUp);
                    }

                    if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        buttons[finalI].startAnimation(scaleDown);
                    }




                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        game_state.devCode %= 1000;
                        game_state.devCode *= 10;
                        game_state.devCode += finalI+1;

                        if(game_state.devCode == DEV_PASSCODE){
//                            Toast.makeText(getApplicationContext(), "DEV" , Toast.LENGTH_SHORT).show();
                            game_state.isDevOn = true;
                            menuBox.findItem(R.id.info_bar).setVisible(true);
                        }
                    }


                    if (game_state.game_started) { // Действие кнопки во время игры
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            textViewLoc.setText("Локация");
                            textViewProf.setText("Профессия");
                            button_reset.setVisibility(View.VISIBLE);

                            hideSpy();
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            game_state.ispressed[finalI] = true;
                            tryAddLocToPool(finalI);
                            textViewLoc.setText(game_state.loc[finalI]);
                            textViewProf.setText(game_state.prof[finalI]);
                            buttons[finalI].setBackground(getDrawable(R.drawable.button_back_off));
                            button_reset.setVisibility(View.GONE);

                            showSpy(game_state.prof[finalI]);
                        }
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) { // Выбор количества игроков до начала игры
                        if (game_state.isPrestartGamers) {

                            if (game_state.prestartGamers == finalI+1) {
                                for (int j = 0; j < 8; j++) {
                                    buttons[j].setBackground(getDrawable(R.drawable.button_back_default));
                                }
                                game_state.isPrestartGamers = false;
                                game_state.gamers = game_state.gamersFromConfig;
                            } else {
                                for (int j = 0; j < 8; j++) {
                                    buttons[j].setBackground(getDrawable(R.drawable.button_back_default));
                                }

                                buttons[finalI].setBackground(getDrawable(R.drawable.button_back_gold));
                                game_state.prestartGamers = finalI + 1;
                                game_state.gamers = game_state.prestartGamers;
                            }

                        } else {
                            for (int j = 0; j < 8; j++) {
                                buttons[j].setBackground(getDrawable(R.drawable.button_back_default));
                            }

                            buttons[finalI].setBackground(getDrawable(R.drawable.button_back_gold));
                            game_state.prestartGamers = finalI + 1;
                            game_state.isPrestartGamers = true;
                            game_state.gamers = game_state.prestartGamers;
                        }
                    }

                    return false;
                }
            });
        }




        }


/*============================================================================*/


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Update GUI
            updateGUI(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));

        ContextCompat.registerReceiver(getBaseContext(), broadcastReceiver, new IntentFilter(BroadcastService.COUNTDOWN_BR), ContextCompat.RECEIVER_EXPORTED);
        Log.i(TAG,"Registered broadcast receiver");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG,"Unregistered broadcast receiver");
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,BroadcastService.class));
        Log.i(TAG,"Stopped service");
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown",90000);

            Log.i(TAG,"" + intent);
            Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);

//            buttonTimer.setText("" + millisUntilFinished / 1000);

            int sec = (int) (millisUntilFinished / 1000);
            buttonTimer.setText(sec/60 + ":" + ((sec%60<10)?"0":"") + sec%60);

            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);

            sharedPreferences.edit().putLong("time",millisUntilFinished).apply();
        }
    }
/*============================================================================*/

    private void handleMutlipleTextData(Intent intent) {
        ArrayList <Uri> texDataArray = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (texDataArray != null){

            int counter = 0;

            for(Uri uri : texDataArray){
                String dataPath = uri.getLastPathSegment();
                dataPath = dataPath.substring(dataPath.lastIndexOf(":")+1, dataPath.length());
                dataPath = Environment.getExternalStorageDirectory().toString() + "/" + dataPath;

                if(uri != null){

                    String text = null;
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        text = getStringFromInputStream(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!text.isEmpty()) {
                        game_state.logString += "!!! TEXT !!!: " + text + "\n";

                        String fileName = uri.getPath();
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

                        if (uri != null) {
                            game_state.logString += "!!! Filename: " + fileName + "\n";
                            game_state.logString += "Path: " + game_state.path + "\n";

                            writeFile(game_state.path + "/" + fileName, text);

//                        Toast.makeText(getApplicationContext(), "Локация загружена", Toast.LENGTH_SHORT).show();
                            refresh_loc_list();
                        }

                        counter++;
                        refresh_loc_list();
                    }
                }
            }
            Toast.makeText(getApplicationContext(), "Загружено локаций: " + String.valueOf(counter), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleTextData(Intent intent) {
        Uri textdata = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String text = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(textdata);
            text = getStringFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!text.isEmpty()) {
            game_state.logString += "!!! TEXT !!!: " + text + "\n";

            String fileName = textdata.getPath();
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

            if (textdata != null) {
                game_state.logString += "!!! Filename: " + fileName + "\n";
                game_state.logString += "Path: " + game_state.path + "\n";

                writeFile(game_state.path + "/" + fileName, text);

                Toast.makeText(getApplicationContext(), "Локация загружена", Toast.LENGTH_SHORT).show();
                refresh_loc_list();
            }
        }
    }


/**=============================================================================================================================**/

    public static String getStringFromInputStream(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }

/**=============================================================================================================================**/

    private void refresh_loc_list() {

            //Log.d("Files", "Path: " + path);
            File directory = new File(game_state.path);
            boolean dir_exist = directory.exists();
            boolean is_dir_good = false;

            if(dir_exist)
            {
                //Toast.makeText(getApplicationContext(), "Директория Spyfall найдена" , Toast.LENGTH_SHORT).show();
                is_dir_good = true;
            }
            else
            {
                if( directory.mkdirs() )
                {
                    Toast.makeText(getApplicationContext(), "Директория Spyfall создана" , Toast.LENGTH_SHORT).show();
                    is_dir_good = true;
                }else{
                    Toast.makeText(getApplicationContext(), "Директория Spyfall не может быть создана" , Toast.LENGTH_SHORT).show();
                    game_state.is_game_worked = false;
                }
            }

            FileFilter filter = new FileFilter() {
                public boolean accept(File f)
                {
                    return f.getName().endsWith("txt");
                }
            };

            if(is_dir_good)
            {
                game_state.lastLocs = 0;
                File[] files = directory.listFiles(filter);
                game_state.str_list = new String[files.length];
                for (int i = 0; i < files.length; i++)
                {
                    if(files[i].isFile())
                    {
                        game_state.str_list[i] = files[i].getName();
                        String buf = readFile(game_state.path+"/"+game_state.str_list[i]);
                        if(!buf.startsWith("+") && !buf.startsWith("-"))
                        {
                            buf = "-" + buf;
                        }

                        if(buf.startsWith("-"))
                        {
                            game_state.lastLocs++;
                        }

                        writeFile(game_state.path+"/"+game_state.str_list[i], buf);
                    }
                }

                if(game_state.game_started == false) {
                    game_state.presentedLocs = game_state.lastLocs;
                    Log.i("Locs", "presentedLocs = lastLocs");
                    Log.i("Locs", game_state.game_started?"true":"false");
                }else{
                    Log.i("Locs", "presentedLocs != lastLocs");
                    Log.i("Locs", game_state.game_started?"true":"false");
                }

                if(files.length>2)
                {
                    for (int i = 0; i < files.length-1; i++)
                    {
                        for (int j = 0;  j < (files.length - i - 1) ; j++)
                        {
                            if(game_state.str_list[j].compareTo(game_state.str_list[j+1]) > 0)
                            {
                                String buf = game_state.str_list[j];
                                game_state.str_list[j] = game_state.str_list[j+1];
                                game_state.str_list[j+1] = buf;
                            }

                        }
                    }
                }

                //Toast.makeText(getApplicationContext(), "Локаций: "+Integer.toString(files.length) , Toast.LENGTH_SHORT).show();
                updateLocCounter();
            }
    }




    boolean game_prepare (int gamers_v, int spys_v) {
        if(game_state.isPrestartGamers){
            gamers_v = game_state.prestartGamers;
            game_state.gamers = game_state.prestartGamers;
        }

        if(spys_v > gamers_v)
            spys_v = gamers_v;

        if(game_state.str_list.length == 0)
        {
            Toast.makeText(getApplicationContext(), "Отсутствуют файлы локаций" , Toast.LENGTH_SHORT).show();
            return false;
        }else{

            int locs = 0;
            for (String s : game_state.str_list) {
                if (readFile(game_state.path + "/" + s).startsWith("-")) {
                    locs += 1;
                }
            }
            String[] str_list_for_game = new String[locs];
            int j = 0;
            for (String s : game_state.str_list) {
                if (readFile(game_state.path + "/" + s).startsWith("-")) {
                    str_list_for_game[j] = s;
                    j++;
                }
            }

            if(str_list_for_game.length == 0)
            {
                Toast.makeText(getApplicationContext(), "Отсутствуют неотыгранные локации ", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                game_state.lastLocs = locs;
                game_state.presentedLocs = locs;
                updateLocCounter();
            }




            int loc_num = ((int)(Math.random()*1000))%(str_list_for_game.length);
            String loc_name = str_list_for_game[loc_num];
            game_state.locToPool = loc_name;

            String res = readFile(game_state.path+"/"+loc_name).replaceAll("\r","");
            String []res_list = res.split("\n");

            if(res_list.length<8)
            {
                Toast.makeText(getApplicationContext(), "Неверный формат файла "+loc_name , Toast.LENGTH_SHORT).show();
                tryAddLocToPool(0);
                game_state.isNeedPoolLoc = true;
                updateLocCounter();
                resetGameButtons();
                return false;
            }

            for (int i = 0; i < 8; i++)
                buttons[i].setBackground(getDrawable(R.drawable.button_back_on));

            for(int i = gamers_v; i<8; i++)
            {
                buttons[i].setEnabled(false);
                buttons[i].setBackground(getDrawable(R.drawable.button_back_default));
            }

            ArrayList<String> prep_list = new ArrayList<String>();
            for(int i = 1; i < res_list.length; i++)
                if(!res_list[i].isEmpty())
                    prep_list.add(res_list[i]);

            Collections.shuffle(prep_list);
            List final_list = prep_list.subList(0, gamers_v-spys_v);
            for(int i = 0; i < spys_v; i++)
                final_list.add("Шпион");
            Collections.shuffle(final_list);

            game_state.spyList.clear();
            for(int i = 0; i < final_list.size(); i++) {
                game_state.loc[i] = res_list[0].substring(1);
                game_state.prof[i] = (String) final_list.get(i);
                if (game_state.prof[i].equals("Шпион")) {
                    //Toast.makeText(getApplicationContext(), Integer.toString(i) , Toast.LENGTH_SHORT).show();
                    game_state.spyList.add(i);
                    game_state.loc[i] = "Узнай, где мы";
                }
            }


            return true;
        }
    }

    boolean tryAddLocToPool(int id) {
        if(game_state.isNeedPoolLoc && !(game_state.spyList.contains(id)))
        {
            //Toast.makeText(getApplicationContext(), "Локация помечена как отыгранная " + spyList.toString() , Toast.LENGTH_SHORT).show();
            game_state.lastLocs --;
            game_state.isNeedPoolLoc = false;

            String res = readFile(game_state.path+"/"+game_state.locToPool);
            res = "+" + res.substring(1);
            writeFile(game_state.path+"/"+game_state.locToPool, res);
        }

        if(game_state.gamers == countPressed()) {
            game_state.presentedLocs = game_state.lastLocs;
            updateLocCounter();
        }
        return true;
    }

    int countPressed() {
        int res = 0;
        for(int i=0; i<8; i++)
        {
            if(game_state.ispressed[i])
                res++;
        }
        return res;
    }

    void updateLocCounter() {
        locsWithoutPool.setText(Integer.toString(game_state.presentedLocs));
    }

    void resetGameButtons() {
        for (int i = 0; i < 8; i++) {
            buttons[i].setBackground(getDrawable(R.drawable.button_back_default));
            if( (i+1 == game_state.prestartGamers) && game_state.isPrestartGamers)
                buttons[i].setBackground(getDrawable(R.drawable.button_back_gold));
        }
        game_state.game_started = false;
        button_start.setVisibility(View.VISIBLE);

        for(int i = 0; i<8; i++)
        {
            buttons[i].setEnabled(true);
        }

        for(int i = 0; i<8; i++)
        {
            game_state.ispressed[i] = false;
        }
    }

    public boolean copyFile(String srcFile, String dstDir) {

        String dstFileName = srcFile.split("/")[srcFile.split("/").length-1];

        String fileData = readFile(srcFile);
        if(fileData != null)
        {
            boolean res = writeFile(dstDir+"/"+dstFileName, fileData);
            if(res)
            {
                //Toast.makeText(getApplicationContext(), "Файл записан" , Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(getApplicationContext(), "Файл не записан" , Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Файл не прочитан" , Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean copyFile(Uri srcUri, String dstDir) {

        String dstFileName = getFileName(srcUri);
        //Toast.makeText(getApplicationContext(), dstFileName , Toast.LENGTH_SHORT).show();

        String fileData = readFile(srcUri);
        if(fileData != null)
        {
            boolean res = writeFile(dstDir+"/"+dstFileName, fileData);
            if(res)
            {
                //Toast.makeText(getApplicationContext(), "Файл записан" , Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(getApplicationContext(), "Файл не записан" , Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Файл не прочитан" , Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    boolean isExternalStorageReadable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    boolean writeFile(String filename, String input) {
        File fileName = new File(filename);

        if (!fileName.exists()) {
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Файл не может быть создан" , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }

        try {
            FileWriter f = new FileWriter(fileName);
            f.write(input);
            f.flush();
            f.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ошибка записи" , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    String readFile(String filename) {

        java.io.File file = new File(filename);
        String data;

        if (isExternalStorageReadable()) {
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                try {
                    FileInputStream fis = new FileInputStream(file);

                    if (fis != null) {
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader buff = new BufferedReader(isr);

                        String line = null;
                        while ((line = buff.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        fis.close();

                        data = sb.toString();
                        //Toast.makeText(getApplicationContext(), "Прочитано:\n"data , Toast.LENGTH_SHORT).show();
                        return data;

                    } else {
                        Toast.makeText(getApplicationContext(), "FileInputStream = null", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Read exception " + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return null;
                }

            } else {
                Toast.makeText(getApplicationContext(), "Файла не существует", Toast.LENGTH_LONG).show();
                return null;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Хранилище не читаемо", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    String readFile(Uri uri) {

        String data;

        if (isExternalStorageReadable()) {
                StringBuilder sb = new StringBuilder();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader buff = new BufferedReader(isr);

                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        sb.append(line + '\n');
                    }

                    data = sb.toString();
                    return data;
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Read exception " + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return null;
                }

        } else {
            Toast.makeText(getApplicationContext(), "Хранилище не читаемо", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    void showSpy (String role){
        if(role.equals("Шпион")){
            spyImage.setAlpha((float) 0.5);
            locsWithoutPool.setVisibility(View.GONE);
        }
    }

    void hideSpy (){
        spyImage.setAlpha((float) 0);
        locsWithoutPool.setVisibility(View.VISIBLE);
    }

    boolean parseConfig(String data){
        String[] dataList = data.split("\n");
        if(dataList.length<9){
            resetConfig();
            return false;
        }
        game_state.spys = Integer.parseInt(dataList[0]);

        String name;
        for (int i = 0; i < 8; i++)
        {
            name = dataList[i+1];
            if(name.startsWith("+")){
                //good
            }else if(name.startsWith("-")) {
                //good
            }else{
                resetConfig();
                return false;
            }
        }

        int idx = 0;
        game_state.gamersFromConfig = 0;
        for(int i = 1; i < 9; i++)
        {
            if(dataList[i].startsWith("+")) {
                buttons[idx].setText(dataList[i].substring(1));
                idx++;
                game_state.gamersFromConfig++;
            }
        }
        game_state.gamers = game_state.gamersFromConfig;

        for(int i = idx+1; i < 9; i++)
        {
            buttons[i-1].setText(Integer.toString(i));
            //buttons[i-1].setText("");
        }

        return true;
    }

    boolean resetConfig()
    {
        writeFile(game_state.path, "1\n+1\n+2\n+3\n+4\n+5\n+6\n+7\n+8");
        Toast.makeText(getApplicationContext(), "В файле настроек обнаружена критическая ошибка. Файл настроек пересоздан" , Toast.LENGTH_SHORT).show();
        return true;
    }

}