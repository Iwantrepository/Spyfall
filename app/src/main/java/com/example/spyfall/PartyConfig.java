package com.example.spyfall;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class PartyConfig extends AppCompatActivity {

    SeekBar seekBarSpys = null;
    TextView textViewSpys = null;
    String path;
    int spys = 0;

    String[] dataList;

    EditText[] editTextTextPersonNameButton;
    Switch[] switchButton;
    Button[] timerButtons;
    Button buttonSaveConfig;

    Animation scaleUp, scaleDown;

    SharedPreferences sharedPreferences;

    long timerVal;

    @SuppressLint({"ClickableViewAccessibility", "SuspiciousIndentation", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_config);

/***************************** ▼ Timer ▼ *****************************/
        sharedPreferences = getSharedPreferences(getString(R.string.preferenceFileKey),MODE_PRIVATE);
        long millis = sharedPreferences.getLong("timeSP2", 3000);
        timerVal = millis;
        int sec = (int) (millis / 1000);
        ((TextView) findViewById(R.id.textView_Timer)).setText(sec/60 + ":" + ((sec%60<10)?"0":"") + sec%60);
/***************************** ▲ Timer ▲ *****************************/
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            path = (String) arguments.get("path") + "/config";
        }

        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        seekBarSpys = (SeekBar) findViewById(R.id.seekBarSpys);
        textViewSpys = (TextView) findViewById(R.id.textViewSpyNum);

        editTextTextPersonNameButton = new EditText[8];
        editTextTextPersonNameButton[0] = (EditText) findViewById(R.id.editTextTextPersonNameButton1);
        editTextTextPersonNameButton[1] = (EditText) findViewById(R.id.editTextTextPersonNameButton2);
        editTextTextPersonNameButton[2] = (EditText) findViewById(R.id.editTextTextPersonNameButton3);
        editTextTextPersonNameButton[3] = (EditText) findViewById(R.id.editTextTextPersonNameButton4);
        editTextTextPersonNameButton[4] = (EditText) findViewById(R.id.editTextTextPersonNameButton5);
        editTextTextPersonNameButton[5] = (EditText) findViewById(R.id.editTextTextPersonNameButton6);
        editTextTextPersonNameButton[6] = (EditText) findViewById(R.id.editTextTextPersonNameButton7);
        editTextTextPersonNameButton[7] = (EditText) findViewById(R.id.editTextTextPersonNameButton8);

        switchButton = new Switch[8];
        switchButton[0] = (Switch) findViewById(R.id.switchButton1);
        switchButton[1] = (Switch) findViewById(R.id.switchButton2);
        switchButton[2] = (Switch) findViewById(R.id.switchButton3);
        switchButton[3] = (Switch) findViewById(R.id.switchButton4);
        switchButton[4] = (Switch) findViewById(R.id.switchButton5);
        switchButton[5] = (Switch) findViewById(R.id.switchButton6);
        switchButton[6] = (Switch) findViewById(R.id.switchButton7);
        switchButton[7] = (Switch) findViewById(R.id.switchButton8);


        //Кнопки таймера
        timerButtons = new Button[4];
        timerButtons[0] = (Button) findViewById(R.id.button_TimerMinusLarge);
        timerButtons[1] = (Button) findViewById(R.id.button_TimerMinus);
        timerButtons[2] = (Button) findViewById(R.id.button_TimerPlus);
        timerButtons[3] = (Button) findViewById(R.id.button_TimerPlusLarge);




        buttonSaveConfig = (Button) findViewById(R.id.buttonSaveConfig);

        String data = readFile(path);
        if(data == null)
        {
            //Toast.makeText(getApplicationContext(), "Файл настроек отсутствует" , Toast.LENGTH_SHORT).show();
            File buff = new File(path);
            try {
                buff.createNewFile();
                if(writeFile(path, "1\n+1\n+2\n+3\n+4\n+5\n+6\n+7\n+8"))
                {
                    data = readFile(path);
                    Toast.makeText(getApplicationContext(), "Файл настроек создан" , Toast.LENGTH_SHORT).show();
                    if(data != null)
                    {
                        if(!parseData(data));
                        {
                            parseData(readFile(path));
                        }
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
            if(!parseData(data));
            {
                parseData(readFile(path));
            }
        }
        parceListForButtons();


        //Поведение кнопок таймера
        for (Button bu:timerButtons){
            bu.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        bu.startAnimation(scaleDown);
                    }
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        bu.startAnimation(scaleUp);


                        if(bu == findViewById(R.id.button_TimerPlusLarge)){
                            timerVal += 60000;
                        }
                        if(bu == findViewById(R.id.button_TimerPlus)){
                            timerVal += 30000;
                        }
                        if(bu == findViewById(R.id.button_TimerMinusLarge)){
                            timerVal -= 60000;
                        }
                        if(bu == findViewById(R.id.button_TimerMinus)){
                            timerVal -= 30000;
                        }

                        if(timerVal <= 0){
                            timerVal = 0;
                        }
                        int sec = (int) (timerVal / 1000);

                        ((TextView) findViewById(R.id.textView_Timer)).setText(sec/60 + ":" + ((sec%60<10)?"0":"") + sec%60);
                    }
                    return false;
                }
            });
        }

        buttonSaveConfig.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    buttonSaveConfig.startAnimation(scaleDown);
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    buttonSaveConfig.startAnimation(scaleUp);
                    String res = "";
                    for(int i=0; i<dataList.length; i++){
                        res+=dataList[i];
                        if(i<dataList.length-1) {
                            res+="\n";
                        }
                    }



                    sharedPreferences = getSharedPreferences(getString(R.string.preferenceFileKey),MODE_PRIVATE);
                    long millis = timerVal;
                    sharedPreferences.edit().putLong("timeSP2",millis).apply();



                    Intent intent = new Intent();
                    intent.putExtra("strList", res);
                    writeFile(path, res);
                    setResult(RESULT_OK, intent);

                    Toast.makeText(getApplicationContext(), "Настройки сохранены" , Toast.LENGTH_SHORT).show();
                    finish();
                }
                return false;
            }
        });

        seekBarSpys.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBarSpys, int progress, boolean fromUser) {
                spys = progress+1;
                textViewSpys.setText(Integer.toString(spys));
                dataList[0] = Integer.toString(spys);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBarSpys) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBarSpys) {

            }
        });


        for(int i=0; i<switchButton.length; i++){
            int finalI = i;
            switchButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(switchButton[finalI].isChecked()) {
                        onOffGamer(finalI, true);
                    }else{
                        onOffGamer(finalI, false);
                    }
                }
            });
        }

        for(int i=0; i<switchButton.length; i++){


            int finalI = i;
            editTextTextPersonNameButton[i].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    setName(finalI, s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(s.length() != finalI)
                        setName(finalI, "");
                }
            });
        }

    }

    void setName(int idx, String name){
        dataList[idx+1] = dataList[idx+1].substring(0,1) + name;
        parceListForButtons();
    }

    void onOffGamer(int idx, boolean inGame)
    {
        if(inGame){
            dataList[idx+1] = "+" + dataList[idx+1].substring(1);
        }else{
            dataList[idx+1] = "-" + dataList[idx+1].substring(1);
        }
        parceListForButtons();
    }

    void parceListForButtons(){
    }

    boolean parseData(String data) {
        dataList = data.split("\n");
        if(dataList.length<9){
            resetConfig();
            return false;
        }
        spys = Integer.parseInt(dataList[0]);
        seekBarSpys.setProgress(spys-1);
        textViewSpys.setText(Integer.toString(spys));

        String name;
        for (int i = 0; i < 8; i++)
        {
            name = dataList[i+1];
            if(name.startsWith("+")){
                switchButton[i].setChecked(true);
            }else if(name.startsWith("-")) {
                switchButton[i].setChecked(false);
            }else{
                resetConfig();
                return false;
            }
            name = name.substring(1);
            editTextTextPersonNameButton[i].setText(name);
        }
        return true;
    }

    boolean resetConfig()
    {
        writeFile(path, "1\n+1\n+2\n+3\n+4\n+5\n+6\n+7\n+8");
        Toast.makeText(getApplicationContext(), "В файле настроек обнаружена критическая ошибка. Файл настроек пересоздан" , Toast.LENGTH_SHORT).show();
        return true;
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
                //Toast.makeText(getApplicationContext(), "Файла не существует (возможно выбран относительный путь)", Toast.LENGTH_LONG).show();
                return null;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Хранилище не читаемо", Toast.LENGTH_LONG).show();
            return null;
        }
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
}

