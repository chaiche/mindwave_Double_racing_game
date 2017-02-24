package com.neurosky.mindwavemobiledemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import java.util.Map;

/**
 * Created by chaiche on 16/10/14.
 */
public class DataBase {

    SQLiteDatabase db;
    static final String db_name = "GameInforMation";
    static final String tb_name = "record";

    public DataBase(Context context){
        db = context.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        open_tb_record();
    }

    public void open_tb_record(){
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(32),"+
                "attenation text not null,"+
                "medation text not null)";
        db.execSQL(createTable);

        //delete_db();
    }
    public void addData(String name ,String attention,String medation) {
        ContentValues cv = new ContentValues(3);
        cv.put("name",name);
        cv.put("attenation", attention);
        cv.put("medation", medation);
        db.insert(tb_name, null, cv);
    }
    public Cursor getData_attenation(){


        Cursor c = db.rawQuery("SELECT * FROM "+tb_name, null);

        return c;
    }
    public void delete_db(){
        db.delete(tb_name,null,null);
    }
    public void close_db(){
        db.close();
    }


}
