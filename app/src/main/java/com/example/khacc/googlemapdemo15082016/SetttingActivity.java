package com.example.khacc.googlemapdemo15082016;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class SetttingActivity extends AppCompatActivity {

    private EditText editText;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settting);
        db = new DatabaseHelper(this);
        Cursor res =  db.getDataConfig();
        if (editText == null)
            editText = (EditText) findViewById(R.id.editText);

        if (res.getCount() > 0)
            while (res.moveToNext())
                editText.setText(res.getInt(1) + "");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().equals("")) {
                    if (Integer.parseInt(editText.getText().toString()) < 1 || Integer.parseInt(editText.getText().toString()) > 200)
                        editText.setText("1");
                    db.updateConfig(Integer.parseInt(editText.getText().toString()));
                }
            }
        });
    }

}
