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
import java.util.regex.Pattern;

public class new_location_form extends AppCompatActivity {

    String path = "";

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
                    //file = file.replaceAll("[0-9]+", "");
                    file = file.replaceAll(" ", "");
                    String result = "";
                    result += editLocName.getText().toString() + "\n";
                    result += editRole1.getText().toString() + "\n";
                    result += editRole2.getText().toString() + "\n";
                    result += editRole3.getText().toString() + "\n";
                    result += editRole4.getText().toString() + "\n";
                    result += editRole5.getText().toString() + "\n";
                    result += editRole6.getText().toString() + "\n";
                    result += editRole7.getText().toString();

                    Bundle arguments = getIntent().getExtras();
                    if(arguments!=null){
                        path = (String) arguments.get("path");
                    }

                    file = getNextFileName(file);
                    //Toast.makeText(getApplicationContext(), "Создание файла "+file , Toast.LENGTH_SHORT).show();

                    writeFile(path + "/" + file + ".txt", result);


                    Toast.makeText(getApplicationContext(), file+":\n"+result , Toast.LENGTH_SHORT).show();

                    //editFileName.setText();
                    editLocName.setText("");
                    editRole1.setText("");
                    editRole2.setText("");
                    editRole3.setText("");
                    editRole4.setText("");
                    editRole5.setText("");
                    editRole6.setText("");
                    editRole7.setText("");

                    editLocName.requestFocus();
                }
            }

        });
    }

    String getNextFileName(String filename){
        String buf = filename;

        if(Pattern.matches("[A-zА-я]+", buf))
        {
            File fileCheck = new File(path + "/" + buf + ".txt");
            if(fileCheck.exists())
            {
                filename = getNextFileName(filename+"1");
            }else{
                //
            }
        }else{

            if(Pattern.matches("[A-zА-я]+[0-9]+", buf))
            {

                File fileCheck = new File(path + "/" + buf + ".txt");
                if(!fileCheck.exists())
                {
                    //
                }else{
                    String fileBuf = filename;
                    String letters = filename.replaceAll("[0-9]+", "");
                    filename = fileBuf;
                    String digits = filename.replaceAll("[^0-9]+", "");

                    buf = letters+Integer.toString(Integer.parseInt(digits)+1);

                    fileCheck = new File(path + "/" + buf + ".txt");
                    if(fileCheck.exists())
                    {
                        buf = getNextFileName(buf);
                    }
                }
            }

            filename = buf;
        }

        return filename;
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