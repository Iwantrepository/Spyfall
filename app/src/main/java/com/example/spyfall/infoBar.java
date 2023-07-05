package com.example.spyfall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class infoBar extends AppCompatActivity {


    String path;
    String pathFromToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bar);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            path = (String) arguments.get("path");
            pathFromToUpload = (String) arguments.get("pathFromToUpload");
        }

        TextView textView_info = (TextView) findViewById(R.id.textView_info);
        textView_info.setText("Path: " + path+"\n"
                +"PathFromToUpload: "+pathFromToUpload+"\n"
                +"Version: " + BuildConfig.VERSION_NAME);
    }
}