package com.example.spyfall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ListViewCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

public class locations_list extends AppCompatActivity {

    String[] str_arr;

    ArrayList<String> str_list;
    String path;
    String pathExternalStorage;
    String pathToEx;

    ArrayAdapter<String> listAdapter;

    ListView listView;

    boolean isHandlerOn = false;
    Button buttonCheckAll;
    Button buttonUncheckAll;
    Button buttonGetFromPool;
    Button buttonDeleteChecked;
    Button buttonSetToPool;
    Button buttonCreatePack;
    Button buttonCheckPool;
    Button buttonPacks;

    String dirName;

    int touchListenerChildId = 0;



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.wipe_dir:
                //Toast.makeText(getApplicationContext(), "WIPE" , Toast.LENGTH_SHORT).show();
                wipeDir();
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);

        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonCheckAll = (Button) findViewById(R.id.buttonCheckAll);
        buttonUncheckAll = (Button) findViewById(R.id.buttonUncheckAll);
        buttonGetFromPool = (Button) findViewById(R.id.buttonGetFromPool);
        buttonDeleteChecked = (Button) findViewById(R.id.buttonDeleteChecked);
        buttonSetToPool = (Button) findViewById(R.id.buttonSetToPool);
        buttonCreatePack = (Button) findViewById(R.id.buttonCreatePack);
        buttonCheckPool = (Button) findViewById(R.id.buttonCheckPool);
        buttonPacks = (Button) findViewById(R.id.buttonPacks);

        str_list = new ArrayList<String>();

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            str_arr = (String[]) arguments.get("str_list");
            for(int j = 0; j < str_arr.length; j++ )
            {
                str_list.add(str_arr[j]);
            }
            path = (String) arguments.get("path");
            pathExternalStorage = (String) arguments.get("pathFromToUpload");
            pathToEx = (String) arguments.get("pathToEx");
        }

        listView = (ListView) findViewById(R.id.locationsListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        listAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice, str_list){ //simple_list_item_multiple_choice //simple_list_item_single_choice
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getView(position, convertView, parent);



                if(readFile(path+"/"+this.getItem(position)).startsWith("+"))
                {
                    view.setBackgroundColor(Color.parseColor("#ff8080"));
                }else{
                    view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }

                view.setClickable(false);
                return view;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }
        };

        listView.setAdapter(listAdapter);

//        listView.setItemsCanFocus(true);



        final Handler handler = new Handler();

        Runnable mLongPressed = new Runnable() {
            public void run() {
                Log.i("", "Long press!");


                AlertDialog.Builder builder = new AlertDialog.Builder(locations_list.this);

                String buf = readFile(path+"/"+str_list.get(touchListenerChildId));
                String head = buf.substring(1, buf.indexOf("\n"));
                String body = buf.substring(buf.indexOf("\n"));
                builder.setTitle(head)
                        .setMessage(body)
                ;
                builder.create().show();

                isHandlerOn = false;

//                listView.getChildAt(touchListenerChildId).setBackgroundDrawable(getDrawable( R.drawable.ic_cycle) );
            }
        };
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    handler.postDelayed(mLongPressed, ViewConfiguration.getLongPressTimeout());
                    isHandlerOn = true;

                    Rect rect = new Rect();
                    int childCount = listView.getChildCount();
                    int[] listViewCoords = new int[2];
                    listView.getLocationOnScreen(listViewCoords);
                    int x = (int) motionEvent.getRawX() - listViewCoords[0];
                    int y = (int) motionEvent.getRawY() - listViewCoords[1];
                    View child;
                    for (int i = 0; i < childCount; i++) {
                        child = listView.getChildAt(i);
                        child.getHitRect(rect);
                        if (rect.contains(x, y)) {
                            // This is your down view
//                        mDownView = child;
                            Log.i("LocList", "" + (i + listView.getFirstVisiblePosition()));
                            touchListenerChildId = i + listView.getFirstVisiblePosition();
//                            touchListenerChildId = i;
                            break;
                        }
                    }
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(isHandlerOn){
//                        listView.getChildAt(touchListenerChildId).setBackgroundDrawable(getDrawable( R.drawable.ic_cycle) );
//                        listView.setItemChecked(touchListenerChildId,false);

                        if (listView.isItemChecked(touchListenerChildId)){
                            listView.setItemChecked(touchListenerChildId,false);
                        }else{
                            listView.setItemChecked(touchListenerChildId,true);
                        }
                    }
                }
                if((motionEvent.getAction() == MotionEvent.ACTION_MOVE)||(motionEvent.getAction() == MotionEvent.ACTION_UP)) {

                    handler.removeCallbacks(mLongPressed);

                    isHandlerOn = false;
                }
                return false;
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //listView.setItemChecked(i, true);
            }
        });

        buttonCheckPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < listView.getCount(); i++)
                {
                    String data = readFile(path+"/"+listView.getItemAtPosition(i));
                    if(data.startsWith("+"))
                        listView.setItemChecked(i, true);
                    else
                        listView.setItemChecked(i, false);
                }
            }
        });

        buttonCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < listView.getCount(); i++)
                {
                    listView.setItemChecked(i, true);
                }
            }
        });

        buttonUncheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < listView.getCount(); i++)
                {
                    listView.setItemChecked(i, false);
                }
            }
        });

        buttonGetFromPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = listView.getAdapter().getCount() - 1; i >= 0; i--) {
                    if (checked.get(i)) {
                        String buf = readFile(path + "/" + listView.getItemAtPosition(i).toString());
                        writeFile(path + "/" + listView.getItemAtPosition(i).toString(), "-" + buf.substring(1));

                        //listView.setItemChecked(i, false);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });

        buttonDeleteChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SparseBooleanArray checked = listView.getCheckedItemPositions();
                if(listView.getCheckedItemCount() > 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(locations_list.this);
                    alert.setTitle("Внимание");
                    alert.setMessage("Вы действительно хотите удалить выбранные локации?\n("+listView.getCheckedItemCount()+" локаций)");
                    alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    });
                    alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int j = listView.getAdapter().getCount() - 1; j >= 0; j--) {
                                if (checked.get(j)) {
                                    deleteFile(path, listView.getItemAtPosition(j).toString());
                                    str_list.remove(j);
                                    listView.setItemChecked(j, false);
                                }
                            }
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                    alert.create();
                    alert.show();
                }
            }
        });

        buttonSetToPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = listView.getAdapter().getCount() - 1; i >= 0; i--) {
                    if (checked.get(i)) {
                        String buf = readFile(path + "/" + listView.getItemAtPosition(i).toString());
                        writeFile(path + "/" + listView.getItemAtPosition(i).toString(), "+" + buf.substring(1));

                        //listView.setItemChecked(i, false);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });


        buttonCreatePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new File(pathToEx).mkdirs();
                final SparseBooleanArray checked = listView.getCheckedItemPositions();
                if(listView.getCheckedItemCount() > 0)
                {
                    dirName = "SpyfallPack";
                    boolean isDirNotEx = false;
                    int nameTry = 1;

                    while(!isDirNotEx){
                        File dirBuf = new File(pathToEx+"/" + dirName + String.valueOf(nameTry));

                        if(!dirBuf.isDirectory()){
                            isDirNotEx = true;
                            dirName = dirName + String.valueOf(nameTry);
                        }
                        nameTry++;
                    }

                    AlertDialog.Builder alert = new AlertDialog.Builder(locations_list.this);
                    alert.setTitle("Создание пака");
                    alert.setMessage("Выбранные локации будут скопированы в /" + dirName + " в память телефона\n("+listView.getCheckedItemCount()+" локаций)");
                    alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    });
                    alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            File dir = new File(pathToEx+"/"+dirName);
                            dir.mkdir();

                            for (int j = listView.getAdapter().getCount() - 1; j >= 0; j--) {
                                if (checked.get(j)) {
                                    String buf = readFile(path+"/"+listView.getItemAtPosition(j));
                                    writeFile(pathToEx+"/" + dirName + "/"+listView.getItemAtPosition(j), buf);
                                }
                            }
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                    alert.create();
                    alert.show();
                }
            }
        });


        buttonPacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(locations_list.this);


                ArrayList<String> str_list_packs = str_list;
//                str_list_packs.sort(Comparator.naturalOrder());



                String prevs, nexts;
                for(int i=0; i< str_list_packs.size()-1; i++){

                    prevs = str_list_packs.get(i);
                    nexts = str_list_packs.get(i+1);

                    prevs.substring(0,prevs.indexOf(".txt"));


                }





                String[] prestrarr = new String[str_list.size()];

                for (int i = 0; i < prestrarr.length; i++ ){
                    prestrarr[i] =str_list.get(i);

                    prevs = prestrarr[i];
                    prevs = prevs.substring(0,prevs.indexOf(".txt"));

                    while(Character.isDigit(prevs.charAt(prevs.length()-1))){
                        prevs = prevs.substring(0, prevs.length()-1);
                    }

                    prestrarr[i] = prevs;
                }

                String[] strarr = Arrays.stream(prestrarr).distinct().toArray(String[]::new);

                boolean[] mCheckedItems = new boolean[strarr.length];

                for (int i = 0; i < strarr.length; i++ ){
                    mCheckedItems[i] =false;
                }

                builder.setTitle("Выбирай паки")
                        .setCancelable(false)

                        .setMultiChoiceItems(strarr, mCheckedItems,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which, boolean isChecked) {
                                        mCheckedItems[which] = isChecked;
                                    }
                                })

                        // Добавляем кнопки
                        .setPositiveButton("Готово",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        boolean isNeed = false;

                                        for(int j = 0; j < listView.getCount(); j++)
                                        {
                                            isNeed = false;
                                            for (int i = 0; i < strarr.length; i++) {
                                                if (mCheckedItems[i]) {

                                                    String listViewSample = (String) listView.getItemAtPosition(j);

                                                    listViewSample = listViewSample.substring(0,listViewSample.indexOf(".txt"));

                                                    while(Character.isDigit(listViewSample.charAt(listViewSample.length()-1))){
                                                        listViewSample = listViewSample.substring(0, listViewSample.length()-1);
                                                    }

                                                    if (strarr[i].equals(listViewSample)){
                                                        isNeed = true;
                                                    }
                                                }



                                                if (isNeed){
                                                    listView.setItemChecked(j, true);

                                                    String data = readFile(path+"/"+listView.getItemAtPosition(j));

                                                    if(data.startsWith("+")){
                                                        String buf = readFile(path + "/" + listView.getItemAtPosition(j).toString());
                                                        writeFile(path + "/" + listView.getItemAtPosition(j).toString(), "-" + buf.substring(1));
                                                    }

                                                }else{
                                                    listView.setItemChecked(j, false);

                                                    String data = readFile(path+"/"+listView.getItemAtPosition(j));

                                                    if(data.startsWith("-")){
                                                        String buf = readFile(path + "/" + listView.getItemAtPosition(j).toString());
                                                        writeFile(path + "/" + listView.getItemAtPosition(j).toString(), "+" + buf.substring(1));
                                                    }
                                                }



                                            }

                                        }
                                    }
                                })

                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();

                                    }

                                });












                ;
                builder.create().show();
            }
        });


    }

    boolean wipeDir()
    {
        for (int i = 0; i<str_list.size(); i++)
        {
            File file = new File(path, str_list.get(i));
            boolean deleted = file.delete();
            if (deleted)
            {
                //
            }else{
                return false;
            }
        }
        return false;
    }

    boolean deleteFile(String path, String filename)
    {
        File file = new File(path, filename);
        boolean deleted = file.delete();
        if (deleted)
        {
            //
        }else{
            return false;
        }
        return false;
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
                    Toast.makeText(getApplicationContext(), "Read exception " + e.toString(), Toast.LENGTH_SHORT).show();
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
}