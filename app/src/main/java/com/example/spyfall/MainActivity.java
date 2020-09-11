package com.example.spyfall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_CODE_LOCATIONS = 1;
    public final static int REQUEST_CODE_NEW_LOCATION = 2;
    public final static int REQUEST_CODE_PARTY_CONFIG = 3;

    public final static int REQUEST_CODE_UPLOAD_LOCATIONS = 10;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String[] str_list;
    String path;
    String pathFromToUpload;
    String dataConfig;

    String[] loc = new String[8];
    String[] prof = new String[8];

    Button[] buttons = new Button[8];
    Button button_reset;
    Button button_start;

    List<Integer> spyList = new ArrayList<Integer>();


    ImageView spyImage;

    boolean game_started = false;
    boolean is_game_worked = true;
    int gamers = 0;
    int spys = 1;
    int lastLocs = 0;
    int gamersFromConfig = 0;

    boolean isNeedPoolLoc = true;


    boolean[] ispressed = new boolean[8];

    TextView textViewLoc;
    TextView textViewProf;
    TextView locsWithoutPool;

    String locToPool = "";

    Intent myFileIntent;


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        //int permission = ActivityCompat.checkSelfPermission(activity, STORAGE_SERVICE);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.party_config:
                Intent partyConfig = new Intent(this, PartyConfig.class);
                partyConfig.putExtra("path", path);
                startActivityForResult(partyConfig, REQUEST_CODE_PARTY_CONFIG);

                refresh_loc_list();
                break;

            case R.id.locations:
                //Toast.makeText(getApplicationContext(), "Пока что нет локаций, дядь", Toast.LENGTH_SHORT).show();
                refresh_loc_list();
                Intent  locations = new Intent(this, locations_list.class);
                locations.putExtra("str_list", str_list);
                locations.putExtra("path", path);
                startActivityForResult(locations, REQUEST_CODE_LOCATIONS);
                break;

            case R.id.add_location:
                //Toast.makeText(getApplicationContext(), "Функция временно отключена", Toast.LENGTH_LONG).show();
                //writeFile(path+"/test.txt", "Loc\nr1\nr2\nr3\nr4\nr5\nr6\nr7");

                Intent  add_locations = new Intent(this, new_location_form.class);
                add_locations.putExtra("path", path);
                startActivityForResult(add_locations, REQUEST_CODE_NEW_LOCATION);

                break;

            case R.id.add_starter_pack:
                Toast.makeText(getApplicationContext(), "Добавлено немножко стартовых локаций ", Toast.LENGTH_LONG).show();
                writeFile(path+"/starter_pack_1.txt", "Университет\nПрогульщик\nПодлиза\nОтличник\nПофигист\nИдущий на красный диплом\nПреподаватель\nПостоянно ест");
                writeFile(path+"/starter_pack_2.txt", "Школа\nКурит за школой\nСтароста\nХулиган\nОхранник\nУставший учитель\nДиректор\nОтличник");
                writeFile(path+"/starter_pack_3.txt", "База террористов\nОбожает телеграмм\nКоординатор\nРазведчик\nЛюбитель ножей\nОтвечает за боезапас\nОтветственный за шифры\nБезответственный создатель бомб");
                writeFile(path+"/starter_pack_4.txt", "Форум в интернете\nНедовольный комментатор\nМодератор\nЗадает глупые вопросы\nРугается с модератором\nПытается красиво оформить топик\nШибко умный участник форума\nТот, кто начал этот тред");
                writeFile(path+"/starter_pack_5.txt", "Рок-концерт\nСтоит у самой колонки\nПытается прыгнуть в толпу\nПодпевает (кричит)\nГлавный голос сцены\nБэквокалист\nЗабытый за кулисами клавишник\nПьяный фанат");
                writeFile(path+"/starter_pack_6.txt", "Церквушка окутанная коррупцией\nПоп\nНедовольный прихожанин\nТорговец свечками\nПлакальщица\nНелегально продает свечки\nОдержимая религией мать\nРебенок одержимой матери");
                refresh_loc_list();
                break;

            case R.id.reset_pool_flags:
                tryRemovePoolFlags();
                break;

            case R.id.upload_locations:
                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                myFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                myFileIntent.setType("text/plain");
                startActivityForResult(myFileIntent, REQUEST_CODE_UPLOAD_LOCATIONS);
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


                        for (int i=0; i< uriList.length; i++)
                        {
                            if(uriList[i] != null){
                                copyFile(uriList[i], path);
                            }
                        }
                    }
                }
                refresh_loc_list();
                break;
            case REQUEST_CODE_NEW_LOCATION:
                refresh_loc_list();
                break;

            case REQUEST_CODE_LOCATIONS:
                refresh_loc_list();
                break;

            case REQUEST_CODE_PARTY_CONFIG:
                //Toast.makeText(getApplicationContext(), "Возврат от настройки партии" , Toast.LENGTH_SHORT).show();
                if (data == null) {return;}
                String strList = data.getStringExtra("strList");
                //Toast.makeText(getApplicationContext(), strList , Toast.LENGTH_SHORT).show();
                dataConfig = strList;
                parceConfig(strList);
            break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        pathFromToUpload = Environment.getExternalStorageDirectory().toString();// + "/Spyfall";   //Внешнее хранилище
        path = getApplicationContext().getFilesDir().getPath() + "/Spyfall";    //Выделенное внутреннее хранилище


        spyImage = (ImageView) findViewById(R.id.imageViewSpy);
        int imageSpyRes = getResources().getIdentifier("@mipmap/ic_launcher_foreground", null, this.getPackageName());
        spyImage.setImageResource(imageSpyRes);
        spyImage.setAlpha((float) 0 );

        locsWithoutPool = (TextView) findViewById(R.id.locsWithoutPool);

        refresh_loc_list();

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

        for (int i = 0; i < 8; i++)
            buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));

        for (int i = 0; i < 8; i++)
        {
            loc[i] = Integer.toString(i+1);
            prof[i] = Integer.toString(i+1);
            ispressed[i] = false;
        }

        dataConfig = readFile(path+"/config");
        String configPath = path+"/config";

        if(dataConfig == null)
        {
            //Toast.makeText(getApplicationContext(), "Файл настроек отсутствует" , Toast.LENGTH_SHORT).show();
            File buff = new File(configPath);
            try {
                buff.createNewFile();
                if(writeFile(configPath, "1\n+1\n+2\n+3\n+4\n+5\n+6\n+7\n+8"))
                {
                    dataConfig = readFile(configPath);
                    Toast.makeText(getApplicationContext(), "Файл настроек создан" , Toast.LENGTH_SHORT).show();
                    if(dataConfig != null)
                    {
                        if(!parceConfig(dataConfig));
                        parceConfig(readFile(configPath));
                    }else{
                        Toast.makeText(getApplicationContext(), "Ошибка чтения файла настроек" , Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Ошибка записи файла настроек" , Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Невозможно создать файл настроек" , Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            //Toast.makeText(getApplicationContext(), "Файл настроек наден" , Toast.LENGTH_SHORT).show();
            if(!parceConfig(dataConfig));
            parceConfig(readFile(configPath));
        }

        parceConfig(dataConfig);

        textViewLoc = (TextView) findViewById(R.id.textViewLoc);
        textViewProf = (TextView) findViewById(R.id.textViewProf);


        button_reset.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        button_reset.setTextColor(Color.parseColor("#000000"));
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        //
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
                isNeedPoolLoc = true;
                updateLocCounter();
                return true;
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(is_game_worked)
                {
                    if(gamers > 2)
                    {
                        boolean is_started = game_prepare(gamers, spys);
                        if(is_started)
                        {
                            game_started = true;
                            button_start.setVisibility(View.GONE);
                            //Toast.makeText(getApplicationContext(), "Запущена игра на " + gamers + " игроков" , Toast.LENGTH_SHORT).show();
                        }else{
                            //
                        }

                    }else{
                        //Toast.makeText(getApplicationContext(), "Мало игроков для начала игры" , Toast.LENGTH_SHORT).show();
                        if(gamersFromConfig > 2){
                            gamers = gamersFromConfig;
                            boolean is_started = game_prepare(gamers, spys);
                            if(is_started)
                            {
                                game_started = true;
                                button_start.setVisibility(View.GONE);
                                //Toast.makeText(getApplicationContext(), "Запущена игра на " + gamers + " игроков" , Toast.LENGTH_SHORT).show();
                            }else{
                                //
                            }
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Обнаружены критические неполадки" , Toast.LENGTH_SHORT).show();
                }
            }
        });


        buttons[0].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[0] = true;
                        tryAddLocToPool(0);
                        textViewLoc.setText(loc[0]);
                        textViewProf.setText(prof[0]);
                        buttons[0].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[0]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[0].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 1;
                }
                return false;
            }
        });


        buttons[1].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[1] = true;
                        tryAddLocToPool(1);
                        textViewLoc.setText(loc[1]);
                        textViewProf.setText(prof[1]);
                        buttons[1].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[1]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[1].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 2;
                }
                return false;
            }
        });

        buttons[2].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[2] = true;
                        tryAddLocToPool(2);
                        textViewLoc.setText(loc[2]);
                        textViewProf.setText(prof[2]);
                        buttons[2].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[2]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[2].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 3;
                }
                return false;
            }
        });

        buttons[3].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[3] = true;
                        tryAddLocToPool(3);
                        textViewLoc.setText(loc[3]);
                        textViewProf.setText(prof[3]);
                        buttons[3].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[3]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[3].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 4;
                }
                return false;
            }
        });

        buttons[4].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[4] = true;
                        tryAddLocToPool(4);
                        textViewLoc.setText(loc[4]);
                        textViewProf.setText(prof[4]);
                        buttons[4].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[4]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[4].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 5;
                }
                return false;
            }
        });

        buttons[5].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[5] = true;
                        tryAddLocToPool(5);
                        textViewLoc.setText(loc[5]);
                        textViewProf.setText(prof[5]);
                        buttons[5].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[5]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[5].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 6;
                }
                return false;
            }
        });

        buttons[6].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[6] = true;
                        tryAddLocToPool(6);
                        textViewLoc.setText(loc[6]);
                        textViewProf.setText(prof[6]);
                        buttons[6].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[6]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[6].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 7;
                }
                return false;
            }
        });

        buttons[7].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(game_started){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        textViewLoc.setText("Локация");
                        textViewProf.setText("Профессия");
                        button_reset.setVisibility(View.VISIBLE);

                        hideSpy();
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ispressed[7] = true;
                        tryAddLocToPool(7);
                        textViewLoc.setText(loc[7]);
                        textViewProf.setText(prof[7]);
                        buttons[7].setBackgroundColor(Color.parseColor("#ff8080"));
                        button_reset.setVisibility(View.GONE);

                        showSpy(prof[7]);
                    }
                }else{
                    for (int i = 0; i < 8; i++)
                        buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
                    buttons[7].setBackgroundColor(Color.parseColor("#8080ff"));
                    gamers = 8;
                }
                return false;
            }
        });




        }

    private void refresh_loc_list() {

            //Log.d("Files", "Path: " + path);
            File directory = new File(path);
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
                    is_game_worked = false;
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
                lastLocs = 0;
                File[] files = directory.listFiles(filter);
                str_list = new String[files.length];
                for (int i = 0; i < files.length; i++)
                {
                    if(files[i].isFile())
                    {
                        str_list[i] = files[i].getName();
                        String buf = readFile(path+"/"+str_list[i]);
                        if(!buf.startsWith("+") && !buf.startsWith("-"))
                        {
                            buf = "-" + buf;
                        }

                        if(buf.startsWith("-"))
                        {
                            lastLocs++;
                        }

                        writeFile(path+"/"+str_list[i], buf);
                    }
                }

                if(files.length>2)
                {
                    for (int i = 0; i < files.length-1; i++)
                    {
                        for (int j = 0;  j < (files.length - i - 1) ; j++)
                        {
                            if(str_list[j].compareTo(str_list[j+1]) > 0)
                            {
                                String buf = str_list[j];
                                str_list[j] = str_list[j+1];
                                str_list[j+1] = buf;
                            }

                        }
                    }
                }

                //Toast.makeText(getApplicationContext(), "Локаций: "+Integer.toString(files.length) , Toast.LENGTH_SHORT).show();
                updateLocCounter();
            }
    }




    boolean game_prepare (int gamers_v, int spys_v)
    {
        if(spys_v > gamers_v)
            spys_v = gamers_v;

        if(str_list.length == 0)
        {
            Toast.makeText(getApplicationContext(), "Отсутствуют файлы локаций" , Toast.LENGTH_SHORT).show();
            return false;
        }else{

            int locs = 0;
            for (int i = 0; i<str_list.length; i++)
            {
                if(readFile(path+"/"+str_list[i]).startsWith("-"))
                {
                    locs+=1;
                }
            }
            String[] str_list_for_game = new String[locs];
            int j = 0;
            for (int i = 0; i<str_list.length; i++)
            {
                if(readFile(path+"/"+str_list[i]).startsWith("-"))
                {
                    str_list_for_game[j] = str_list[i];
                    j++;
                }
            }

            if(str_list_for_game.length == 0)
            {
                Toast.makeText(getApplicationContext(), "Отсутствуют неотыгранные локации ", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                lastLocs = locs;
                updateLocCounter();
            }




            int loc_num = ((int)(Math.random()*1000))%(str_list_for_game.length);
            String loc_name = str_list_for_game[loc_num];
            locToPool = loc_name;

            String res = readFile(path+"/"+loc_name).replaceAll("\r","");
            String []res_list = res.split("\n");

            if(res_list.length<8)
            {
                Toast.makeText(getApplicationContext(), "Неверный формат файла "+loc_name , Toast.LENGTH_SHORT).show();
                tryAddLocToPool(0);
                isNeedPoolLoc = true;
                updateLocCounter();
                resetGameButtons();
                return false;
            }

            for (int i = 0; i < 8; i++)
                buttons[i].setBackgroundColor(Color.parseColor("#80ff82"));

            for(int i = gamers_v; i<8; i++)
            {
                buttons[i].setEnabled(false);
                buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
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

            spyList.clear();
            for(int i = 0; i < final_list.size(); i++) {
                loc[i] = res_list[0].substring(1);
                prof[i] = (String) final_list.get(i);
                if (prof[i] == "Шпион") {
                    //Toast.makeText(getApplicationContext(), Integer.toString(i) , Toast.LENGTH_SHORT).show();
                    spyList.add(i);
                    loc[i] = "Узнай, где мы";
                }
            }


            return true;
        }
    }

    boolean tryAddLocToPool(int id)
    {
        if(isNeedPoolLoc && !(spyList.contains(id)))
        {
            //Toast.makeText(getApplicationContext(), "Локация помечена как отыгранная " + spyList.toString() , Toast.LENGTH_SHORT).show();
            lastLocs --;
            isNeedPoolLoc = false;

            String res = readFile(path+"/"+locToPool);
            res = "+" + res.substring(1);
            writeFile(path+"/"+locToPool, res);
        }

        if(gamers == countPressed())
            updateLocCounter();
        return true;
    }

    boolean tryRemovePoolFlags()
    {
        for (int i = 0; i<str_list.length; i++)
        {
            if(readFile(path+"/"+str_list[i]).startsWith("+"))
            {
                String res = readFile(path+"/"+str_list[i]);
                res = "-" + res.substring(1);
                if(writeFile(path+"/"+str_list[i], res))
                {
                    //Toast.makeText(getApplicationContext(), "Файл " + str_list[i] + " не был записан. Удаление флагов остановлено" , Toast.LENGTH_SHORT).show();
                    //Почему-то запись происходит, но сыпятся ошибки
                    //return false;
                }
            }
        }
        lastLocs = str_list.length;
        updateLocCounter();

        return true;
    }

    int countPressed()
    {
        int res = 0;
        for(int i=0; i<8; i++)
        {
            if(ispressed[i])
                res++;
        }
        return res;
    }

    void updateLocCounter()
    {
        locsWithoutPool.setText(Integer.toString(lastLocs));
    }

    void resetGameButtons()
    {
        for (int i = 0; i < 8; i++)
            buttons[i].setBackgroundColor(Color.parseColor("#dbdbdb"));
        game_started = false;
        gamers = 0;
        button_start.setVisibility(View.VISIBLE);

        for(int i = 0; i<8; i++)
        {
            buttons[i].setEnabled(true);
        }

        for(int i = 0; i<8; i++)
        {
            ispressed[i] = false;
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


    boolean isExternalStorageReadable()
    {
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))
        {
            return true;
        }else{
            return false;
        }
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
                            sb.append(line + '\n');
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
                    Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_SHORT).show();
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
                    //InputStreamReader isr = new InputStreamReader(fis);
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader buff = new BufferedReader(isr);

                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        sb.append(line + '\n');
                    }

                    data = sb.toString();
                    //Toast.makeText(getApplicationContext(), "Прочитано:\n"+data , Toast.LENGTH_SHORT).show();
                    return data;
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return null;
                }

        } else {
            Toast.makeText(getApplicationContext(), "Хранилище не читаемо", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    void showSpy (String role){
        if(role == "Шпион"){
            spyImage.setAlpha((float) 0.3);
            locsWithoutPool.setVisibility(View.GONE);
        }
    }

    void hideSpy (){
        spyImage.setAlpha((float) 0);
        locsWithoutPool.setVisibility(View.VISIBLE);
    }

    boolean parceConfig(String data){
        String[] dataList = data.split("\n");
        if(dataList.length<9){
            resetConfig();
            return false;
        }
        spys = Integer.parseInt(dataList[0]);

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
        gamersFromConfig = 0;
        for(int i = 1; i < 9; i++)
        {
            if(dataList[i].startsWith("+")) {
                buttons[idx].setText(dataList[i].substring(1));
                idx++;
                gamersFromConfig++;
            }
        }

        for(int i = idx+1; i < 9; i++)
        {
            buttons[i-1].setText(Integer.toString(i));
            //buttons[i-1].setText("");
        }

        return true;
    }

    boolean resetConfig()
    {
        writeFile(path, "1\n+1\n+2\n+3\n+4\n+5\n+6\n+7\n+8");
        Toast.makeText(getApplicationContext(), "В файле настроек обнаружена критическая ошибка. Файл настроек пересоздан" , Toast.LENGTH_SHORT).show();
        return true;
    }

}