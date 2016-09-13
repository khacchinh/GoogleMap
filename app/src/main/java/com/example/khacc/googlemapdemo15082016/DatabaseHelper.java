package com.example.khacc.googlemapdemo15082016;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by khacc on 17-Aug-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mymap.db";
    public static final String TABLE_NAME = "mapdata";
    public  static  final String col_id ="id";
    public  static  final String col_route_name ="name_route";
    public  static  final String col_latitude ="latitute";
    public  static  final String col_longitude ="longitude";
    public  static  final String col_create_at ="create_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String str = "create table " + TABLE_NAME + " (id integer primary key autoincrement, name_route text, latitute double, longitude double, create_at text)";
        db.execSQL(str);
        str = "create table config (id integer primary key autoincrement, value integer)";
        db.execSQL(str);
        str = "create table market (id integer primary key autoincrement, latitude double, longitude double, name_position text, context text, city text, district text, create_at text, update_at text)";
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public void insertConfig(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor res = DB.rawQuery("select * from config", null);
        if (res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("value", 1);
            DB.insert("config", null, contentValues);
        }
    }

    public int updateConfig(int value) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("value", value);
        return db.update("config", values, "id" + " = ?",
                new String[] { String.valueOf("1") });
    }

    public boolean insertData(String route_name, double latitude, double longitude, String create_at){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_route_name, route_name);
        contentValues.put(col_latitude, latitude);
        contentValues.put(col_longitude, longitude);
        contentValues.put(col_create_at, create_at);

        long result = DB.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return  false;
        else return  true;
    }

    public Cursor getDataConfig(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from config", null);
        return res;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DISTINCT name_route, create_at from " +TABLE_NAME, null);
        return res;
    }

    public Cursor getDataByRoute(String route_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select latitute, longitude from " +TABLE_NAME + " where name_route = '" + route_name + "'", null);
        return res;
    }


    public Integer deleteData(String route_name){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "name_route = '" + route_name + "'", null);
    }

    public boolean insertDataMarket(String name_position, String context, String city_name,String district, double latitude, double longitude, String create_at){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("name_position", name_position);
        contentValues.put("context", context);
        contentValues.put("city", city_name);
        contentValues.put("district", district);
        contentValues.put("create_at", create_at);

        long result = DB.insert("market", null, contentValues);
        if (result == -1)
            return  false;
        else return  true;
    }

    public Cursor getAllDataMarket(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from market", null);
        return res;
    }
}
