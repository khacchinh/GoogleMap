package com.example.khacc.googlemapdemo15082016;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener{

    Button btnStart, btnEnd, btnReset, btnSave;
    //create and init database
    DatabaseHelper db;

    static int distance = 1;
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private ArrayList <LatLng> arr_Location = new ArrayList<>();
    private boolean flatStart = false;

    float mDeclination;
    private SensorManager mSensorManager;
    Sensor sensor;
    boolean isCompassOn = false;

    private static final String MYTAG = "MYTAG";

    // Mã yêu cầu uhỏi người dùng cho phép xem vị trí hiện tại của họ (***).
    // Giá trị mã 8bit (value < 256).
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferencesx
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {
                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, MyIntro.class);
                    startActivity(i);
                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();
                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);
                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnReset = (Button) findViewById(R.id.btnResert);
        btnSave = (Button) findViewById(R.id.btnSave);

        btnStart.setEnabled(true);
        btnSave.setEnabled(false);
        btnReset.setEnabled(false);
        btnEnd.setEnabled(false);
        //init database
        db = new DatabaseHelper(this);
        db.insertConfig();
        // Tạo Progress Bar
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Hiển thị Progress Bar
        myProgress.show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void obbtnResetClick(View v){
        arr_Location.clear();
        flatStart = false;
        myMap.clear();

        btnStart.setEnabled(true);
        btnEnd.setEnabled(false);
        btnSave.setEnabled(false);
        btnReset.setEnabled(false);
    }



    public void onbtnStartClick(View v){
        flatStart = true;
        Location location = myMap.getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        arr_Location.clear();
        arr_Location.add(latLng);
        MarkerOptions option = new MarkerOptions();
        option.title("Start");
        option.position(latLng);
        Marker currentMarker = myMap.addMarker(option);
        currentMarker.showInfoWindow();

        btnStart.setEnabled(false);
        btnEnd.setEnabled(true);
        btnReset.setEnabled(true);
    }

    public void onbtnStopClick(View v){
        flatStart = false;
        Location location = myMap.getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        arr_Location.add(latLng);
        MarkerOptions option = new MarkerOptions();
        option.title("End");
        option.position(latLng);
        Marker currentMarker = myMap.addMarker(option);
        currentMarker.showInfoWindow();

        btnReset.setEnabled(true);
        btnSave.setEnabled(true);
        btnEnd.setEnabled(false);
    }

    private void onMyMapReady(GoogleMap googleMap){
        // Lấy đối tượng Google Map ra:
        myMap = googleMap;

        // Thiết lập sự kiện đã tải Map thành công
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {

                // Đã tải thành công thì tắt Dialog Progress đi
                myProgress.dismiss();

                // Hiển thị vị trí người dùng.
                askPermissionsAndShowMyLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);

        myMap.setMyLocationEnabled(true);
    }

    private void askPermissionsAndShowMyLocation(){
        // Với API >= 23, bạn phải hỏi người dùng cho phép xem vị trí của họ.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};

                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }

        // Hiển thị vị trí hiện thời trên bản đồ.
        this.showMyLocation();
    }

    // Khi người dùng trả lời yêu cầu cấp quyền (cho phép hoặc từ chối).
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Chú ý: Nếu yêu cầu bị bỏ qua, mảng kết quả là rỗng.
                if (grantResults.length > 23
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Hiển thị vị trí hiện thời trên bản đồ.
                    this.showMyLocation();
                }
                // Hủy bỏ hoặc từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // Tìm một nhà cung cấp vị trị hiện thời đang được mở.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Tiêu chí để tìm một nhà cung cấp vị trí.
        Criteria criteria = new Criteria();

        // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    // Chỉ gọi phương thức này khi đã có quyền xem vị trí người dùng.
    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;
        try {

            // Đoạn code nay cần người dùng cho phép (Hỏi ở trên ***).
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

            // Lấy ra vị trí.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // Với Android API >= 23 phải catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {

            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            arr_Location.add(latLng);
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng latLng1 = marker.getPosition();
                    Toast.makeText(MainActivity.this, latLng1.latitude + " " + latLng1.longitude, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
            Log.i(MYTAG, "Location not found");
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //zoom to current position:
        float zoom = myMap.getCameraPosition().zoom;
        zoom = ( zoom > 16 ) ? zoom : 16;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom);
        myMap.animateCamera(cameraUpdate);

        if (flatStart) {

            Cursor res = db.getDataConfig();
            if (res.getCount() > 0)
                while (res.moveToNext())
                    distance = res.getInt(1);

            LatLng pre_LatLng = arr_Location.get(arr_Location.size() - 1);
            float result[] = new float[5];
            Location.distanceBetween(latLng.latitude, latLng.longitude, pre_LatLng.latitude, pre_LatLng.longitude, result);
            if (result[0] > distance) {
                // Thêm Marker cho Map:
                arr_Location.add(latLng);
                MarkerOptions option = new MarkerOptions();
                option.title("Moving");
                option.snippet("....");
                option.position(latLng);
                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                Marker currentMarker = myMap.addMarker(option);
                currentMarker.showInfoWindow();
                myMap.addPolyline(new PolylineOptions().add(latLng, pre_LatLng).width(5).color(Color.BLUE).geodesic(true));
            }
        }

        GeomagneticField field = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis()
        );

        mDeclination = field.getDeclination();
        isCompassOn =  true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION && isCompassOn) {
            updateCamera(event.values[0] + mDeclination);
        }
    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = myMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        GoogleMap.CancelableCallback callback = new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        };
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 24, callback);
        //isCompassOn = false;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onbtnSaveClick(View v){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strDate = sdf.format(c.getTime());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Lưu lộ trình");
        alertDialog.setMessage("Nhập tên lộ trình: ");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() > 0) {
                            for (int i = 0; i < arr_Location.size(); i++) {
                                if (db.insertData(input.getText().toString(), arr_Location.get(i).latitude,
                                        arr_Location.get(i).longitude, strDate)) {
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed insert data", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            Toast.makeText(MainActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                            arr_Location.clear();
                            flatStart = false;
                            myMap.clear();

                            btnEnd.setEnabled(false);
                            btnReset.setEnabled(false);
                            btnSave.setEnabled(false);
                            btnStart.setEnabled(true);
                        } else
                            Toast.makeText(MainActivity.this, "Bạn phải nhập tên", Toast.LENGTH_LONG).show();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.ki The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SetttingActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.route_name){
            Intent intent = new Intent(this, RouteActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.intro){
            Intent intent = new Intent(this, MyIntro.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.readfile){
            Intent intent = new Intent(this, ReadFileSDCardActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.showMarket){
            Intent intent = new Intent(this, ShowMarketAroundMe.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
