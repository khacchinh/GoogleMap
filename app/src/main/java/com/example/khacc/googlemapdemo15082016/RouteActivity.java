package com.example.khacc.googlemapdemo15082016;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        db = new DatabaseHelper(this);
        listView = (ListView) findViewById(R.id.listView);
        setListView();
    }

    public void setListView(){
        ListAdapter adapter = new ListAdapter(this, getData());
        listView.setAdapter(adapter);
    }

    private ArrayList<String> getData(){
        ArrayList<String> strings = new ArrayList<>();
        Cursor res = db.getAllData();
        if (res.getCount() == 0)
        {
            strings.add("No row to select");
            return strings;
        }
        while (res.moveToNext()){
            strings.add(res.getString(0)+"."+res.getString(1));
        }
        return strings;
    }


}
