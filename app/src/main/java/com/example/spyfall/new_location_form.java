package com.example.spyfall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class new_location_form extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location_form);




        Button buttonNewLoc = (Button) findViewById(R.id.buttonNewLoc);

        final EditText editFileName = (EditText) findViewById(R.id.editTextFileName);
        final EditText editLocName = (EditText) findViewById(R.id.editTextLocationName);
        final EditText editRole1 = (EditText) findViewById(R.id.editTextPersonName1);
        final EditText editRole2 = (EditText) findViewById(R.id.editTextPersonName2);
        final EditText editRole3 = (EditText) findViewById(R.id.editTextPersonName3);
        final EditText editRole4 = (EditText) findViewById(R.id.editTextPersonName4);
        final EditText editRole5 = (EditText) findViewById(R.id.editTextPersonName5);
        final EditText editRole6 = (EditText) findViewById(R.id.editTextPersonName6);
        final EditText editRole7 = (EditText) findViewById(R.id.editTextPersonName7);


        buttonNewLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isEmptyPtEx = false;

                if(editFileName.getText().toString().isEmpty() ||
                        editLocName.getText().toString().isEmpty() ||
                        editRole1.getText().toString().isEmpty() ||
                        editRole2.getText().toString().isEmpty() ||
                        editRole3.getText().toString().isEmpty() ||
                        editRole4.getText().toString().isEmpty() ||
                        editRole5.getText().toString().isEmpty() ||
                        editRole6.getText().toString().isEmpty() ||
                        editRole7.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "Есть пустые поля" , Toast.LENGTH_SHORT).show();
                else
                {
                    String file = editFileName.getText().toString().replace(".txt", "");
                    String result = "";
                    result += editLocName.getText().toString() + "\n";
                    result += editRole1.getText().toString() + "\n";
                    result += editRole2.getText().toString() + "\n";
                    result += editRole3.getText().toString() + "\n";
                    result += editRole4.getText().toString() + "\n";
                    result += editRole5.getText().toString() + "\n";
                    result += editRole6.getText().toString() + "\n";
                    result += editRole7.getText().toString();


                    String path = "";
                    Bundle arguments = getIntent().getExtras();
                    if(arguments!=null){
                        path = (String) arguments.get("path");
                    }

                    writeFile(path + "/" + file + ".txt", result);
                    Toast.makeText(getApplicationContext(), "Создание локации\n"+result , Toast.LENGTH_SHORT).show();

                    editFileName.setText("");
                    editLocName.setText("");
                    editRole1.setText("");
                    editRole2.setText("");
                    editRole3.setText("");
                    editRole4.setText("");
                    editRole5.setText("");
                    editRole6.setText("");
                    editRole7.setText("");
                }
            }

        });
    }

    void writeFile(String filename, String input) {
        File fileName = new File(filename);

        if (!fileName.exists()) {
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter f = new FileWriter(fileName);
            f.write(input);
            f.flush();
            f.close();
        } catch (Exception e) {

        }
    }
}