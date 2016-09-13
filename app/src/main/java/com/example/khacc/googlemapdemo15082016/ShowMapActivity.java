package com.example.khacc.googlemapdemo15082016;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class ShowMapActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String value;
    private GoogleMap myMap;
    private ArrayList<LatLng> latLngs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Package");
        value = bundle.getString("name_route");

        latLngs = getData(value);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });
    }

    private void onMyMapReady(GoogleMap googleMap){
        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                addMarketToMap(latLngs);
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);

        myMap.setMyLocationEnabled(false);
    }

    private void addMarketToMap(ArrayList<LatLng> latLngs ){
        for (int i= 0 ; i < latLngs.size() ; i++) {
            MarkerOptions option = new MarkerOptions();
            if (i == 0)
                option.title("Start");
            else if (i == latLngs.size() - 1)
                option.title("End");
            else{
                option.title("Moving");
                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
            option.position(latLngs.get(i));
            Marker currentMarker = myMap.addMarker(option);
            currentMarker.showInfoWindow();
            if ( i != 0){
                myMap.addPolyline(new PolylineOptions().add(latLngs.get(i-1), latLngs.get(i)).width(5).color(Color.BLUE).geodesic(true));
            }
        }
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 16));
    }

    private ArrayList<LatLng> getData(String value){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        Cursor res = db.getDataByRoute(value);
        if (res.getCount() == 0) {
            latLngs.add(new LatLng(0, 0));
            return latLngs;
        }
        while (res.moveToNext()){
            latLngs.add(new LatLng(res.getDouble(0), res.getDouble(1)));
        }
        return latLngs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_map, menu);
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
