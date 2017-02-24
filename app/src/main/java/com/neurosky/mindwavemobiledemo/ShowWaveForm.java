package com.neurosky.mindwavemobiledemo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowWaveForm extends Activity {


    private int num = 0;

    MyThread mt;
    MyHandler mh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_wave_form);

        initView();
        openOrcreatdb();
        setUpDrawWaveView();

    }
    @Override
    protected void onResume() {
        super.onResume();
        mh=new MyHandler();
        mt=new MyThread();
        mt.start();
    }


    private TextView txv_attenation,txv_medition;

    private LinearLayout wave_layout;
    private void initView(){

        txv_attenation = (TextView)findViewById(R.id.showWaveForm_txv_attenation);
        txv_medition = (TextView)findViewById(R.id.showWaveForm_txv_medition);
        wave_layout = (LinearLayout) findViewById(R.id.wave_layout);

    }

    DrawWaveView waveView = null;

    public void setUpDrawWaveView() {

        waveView = new DrawWaveView(getApplicationContext());
        wave_layout.addView(waveView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        waveView.setValue(num, 101, -100);

        //draw();


    }

    SQLiteDatabase db;
    static final String db_name = "Test";
    static final String tb_name = "Attenation";
    static final String tb_name_mediation = "Mediaion";

    public void openOrcreatdb(){
        db = this.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);

        String createTable = "CREATE TABLE IF NOT EXISTS "+ tb_name+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "attention INTEGER NOT NULL)";
        db.execSQL(createTable);
        Cursor c = db.rawQuery("SELECT * FROM "+tb_name, null);
        num = c.getCount();

        createTable = "CREATE TABLE IF NOT EXISTS "+ tb_name_mediation+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "mediation INTEGER NOT NULL)";
        db.execSQL(createTable);
        c = db.rawQuery("SELECT * FROM "+tb_name_mediation, null);
        if(num<c.getCount()) num = c.getCount();
        Log.d("count num",num+"");
        txv_attenation.setText(num+"");

    }
    public void drawWaveForm(){
        Cursor c = db.rawQuery("SELECT * FROM "+tb_name, null);
        if(c.moveToFirst()){
            Double temp = 0.0;
            do{
                if(waveView!=null)
                    waveView.updateData(c.getInt(1));
                temp +=c.getInt(1);
            }while(c.moveToNext());
            Double a = temp /num;
            txv_attenation.setText("Attention平均："+String.format("%.2f",a));
        }
    }

    public void drawWaveFormFormedition(){
        Cursor c = db.rawQuery("SELECT * FROM "+tb_name_mediation, null);
        if(c.moveToFirst()){
            Double temp = 0.0;
            do{
                if(waveView!=null)
                    waveView.updateDataformediation(c.getInt(1));
                temp +=c.getInt(1);
            }while(c.moveToNext());
            Double a = temp /num;
            txv_medition.setText("Meditation平均："+String.format("%.2f",a));
        }

    }

    public void draw(){
        waveView.postInvalidate();


        while(!waveView.isReady()) Log.d("draw","not ready");

        drawWaveForm();
        drawWaveFormFormedition();


        Log.d("show",num+"");
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            Message msg = new Message();
            mh.sendMessage(msg);
        }
    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            draw();
        }
    }


    public void goBack(View v){
        finish();
    }





}
