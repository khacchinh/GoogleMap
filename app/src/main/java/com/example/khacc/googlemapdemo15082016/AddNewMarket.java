package com.example.khacc.googlemapdemo15082016;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewMarket extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText txtLat, txtLong, txtNamePostion, txtContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_market);

        txtLat = (EditText) findViewById(R.id.txtLat);
        txtLong = (EditText) findViewById(R.id.txtLong);
        txtNamePostion = (EditText) findViewById(R.id.txtNamePosition);
        txtContext = (EditText) findViewById(R.id.txtContext);

        db = new DatabaseHelper(this);
    }

    public void onCancel(View v){
        super.onBackPressed();
    }

    public void onAddMarket(View v){

        if (txtLat.getText().toString().equals("") || txtLat.getText() == null ){
            Toast.makeText(this, "Nhập vĩ độ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtLong.getText().toString().equals("") || txtLong.getText() == null ){
            Toast.makeText(this,"Nhập kinh độ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtNamePostion.getText().toString().equals("") || txtNamePostion.getText() == null ){
            Toast.makeText(this,"Nhập địa điểm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtContext.getText().toString().equals("") || txtContext.getText() == null ){
            Toast.makeText(this,"Nhập nội dung", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strDate = sdf.format(c.getTime());

        if (db.insertDataMarket(txtNamePostion.getText().toString(), txtContext.getText().toString(),"","", Double.parseDouble(txtLat.getText().toString()), Double.parseDouble(txtLong.getText().toString()),strDate)){
            Toast.makeText(this,"Thêm thành công", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this,"Thất bại", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_market, menu);
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
