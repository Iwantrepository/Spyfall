package com.example.spyfall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ListViewCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class locations_list extends AppCompatActivity {

    String[] str_arr;

    ArrayList<String> str_list;
    String path;
    String pathExternalStorage;

    ArrayAdapter<String> listAdapter;

    ListView listView;

    Button buttonCheckAll;
    Button buttonUncheckAll;
    Button buttonGetFromPool;
    Button buttonDeleteChecked;
    Button buttonSetToPool;
    Button buttonCreatePack;
    Button buttonCheckPool;



/*      TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loc_menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

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
        }

        listView = (ListView) findViewById(R.id.locationsListView);

        listAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice, str_list){
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
                return view;
            }
        };

        listView.setAdapter(listAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(true);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), readFile(path+"/"+str_list.get(i)) , Toast.LENGTH_SHORT).show();

                if (listView.isItemChecked(i)){
                    listView.setItemChecked(i,false);
                }else{
                    listView.setItemChecked(i,true);
                }

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
                final SparseBooleanArray checked = listView.getCheckedItemPositions();
                if(listView.getCheckedItemCount() > 0)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(locations_list.this);
                    alert.setTitle("Создание пака");
                    alert.setMessage("Выбранные локации будут скопированы в /SpyfallPack в память телефона\n("+listView.getCheckedItemCount()+" локаций)");
                    alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    });
                    alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            File dir = new File(pathExternalStorage+"/SpyfallPack");
                            dir.mkdir();

                            for (int j = listView.getAdapter().getCount() - 1; j >= 0; j--) {
                                if (checked.get(j)) {
                                    String buf = readFile(path+"/"+listView.getItemAtPosition(j));
                                    writeFile(pathExternalStorage+"/SpyfallPack/"+listView.getItemAtPosition(j), buf);
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