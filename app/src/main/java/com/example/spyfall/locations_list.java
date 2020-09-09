package com.example.spyfall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ListViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class locations_list extends AppCompatActivity {

    String[] str_list;
    String path;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loc_menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            str_list = (String[]) arguments.get("str_list");
            path = (String) arguments.get("path");
        }

            ListView listView = (ListView) findViewById(R.id.locations_list);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, str_list);

            listView.setAdapter(adapter);
        }

    boolean wipeDir()
    {
        for (int i = 0; i<str_list.length; i++)
        {
            //File dir = getFilesDir();
            File file = new File(path, str_list[i]);
            boolean deleted = file.delete();
        }
        return false;
    }
}