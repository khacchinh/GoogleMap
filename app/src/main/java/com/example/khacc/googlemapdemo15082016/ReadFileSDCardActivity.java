package com.example.khacc.googlemapdemo15082016;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadFileSDCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_file_sdcard);
        //Find the view by its id
        TextView tv = (TextView)findViewById(R.id.fileContent);

        File dir = Environment.getExternalStorageDirectory();
        //File yourFile = new File(dir, "path/to/the/file/inside/the/sdcard.ext");

        //Get the text file
        File file = new File(dir,"text.txt");
        // i have kept text.txt in the sd-card

        if(file.exists())   // check if file exist
        {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }
            //Set the text
            tv.setText(text);
        }
        else
        {
            tv.setText("Sorry file doesn't exist!!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_file_sdcard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
