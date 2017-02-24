package com.neurosky.mindwavemobiledemo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class TestSqliteActivity extends Activity {


    DataBase db;

    private LinearLayout wave_layout;

    private ListView liv;

    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test_sqlite);


        db = new DataBase(getApplicationContext());

        liv = (ListView)findViewById(R.id.test_liv);

        get();
        mAdapter = new MyAdapter(this,arrayList_in);
        liv.setAdapter(mAdapter);

        wave_layout = (LinearLayout)findViewById(R.id.wave_layout);
        setUpDrawWaveView();
        liv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                waveView.clear();
                int tmp_attenation = arrayList_in.get(position).attenation.size();
                int tmp_medation = arrayList_in.get(position).medation.size();

                int count = 0;
                if (count < tmp_attenation) count = tmp_attenation;
                if (count < tmp_medation) count = tmp_medation;
                waveView.setValue(count, 100, 0);
                waveView.initView();

                for (int i = 0; i < tmp_attenation; i++) {
                    waveView.updateData(arrayList_in.get(position).attenation.get(i));
                }
                for (int i = 0; i < tmp_medation; i++) {
                    waveView.updateDataformediation(arrayList_in.get(position).medation.get(i));
                }

            }
        });



    }



    DrawWaveView waveView = null;

    public void setUpDrawWaveView() {

        waveView = new DrawWaveView(getApplicationContext());
        wave_layout.addView(waveView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        waveView.setValue(150, 101, 0);

    }

    ArrayList array_attenation = new ArrayList();
    ArrayList array_medation = new ArrayList();

    Random ra  = new Random();

    ArrayList<InforMation> arrayList_in = new ArrayList<>();



    public void add(View v){

        array_attenation.clear();

        for(int i =0;i<150;i++){
            array_attenation.add(":"+ra.nextInt(100)+"D");
            array_medation.add(":"+ra.nextInt(100)+"D");
        }

        String tmp_attenation = "";


        for(int i =0;i<array_attenation.size();i++){
            tmp_attenation+= array_attenation.get(i);
        }


        String tmp_medation = "";

        for(int i =0;i<array_medation.size();i++){
            tmp_medation+= array_medation.get(i);
        }

        db.addData("chaiche",tmp_attenation,tmp_medation);

    }

    Double attenationLow = 100.0,attenationHeight = 0.0 ;
    Double medationLow = 100.0,medationHeight =0.0;

    int which_isWinner;


    public void get(){

        arrayList_in.clear();

        Cursor c = db.getData_attenation();
        if(c.moveToFirst()){
            do{
                InforMation in = new InforMation(c.getInt(0),c.getString(1),c.getString(2),c.getString(3));
                arrayList_in.add(in);
            }while(c.moveToNext());
        }
        Double tmp = 0.0;
        for(int i =0;i<arrayList_in.size();i++){
            if(attenationLow>arrayList_in.get(i).avg_attenation) attenationLow = arrayList_in.get(i).avg_attenation;
            if(attenationHeight<arrayList_in.get(i).avg_attenation) attenationHeight = arrayList_in.get(i).avg_attenation;
            if(medationLow>arrayList_in.get(i).avg_medation) medationLow = arrayList_in.get(i).avg_medation;
            if(medationHeight<arrayList_in.get(i).avg_medation) medationHeight = arrayList_in.get(i).avg_medation;

            if(tmp<(arrayList_in.get(i).avg_attenation+arrayList_in.get(i).avg_medation)/2) {
                tmp = (arrayList_in.get(i).avg_attenation + arrayList_in.get(i).avg_medation) / 2;
                which_isWinner = i;
            }
        }
    }

    public void show(View v){

        get();
        mAdapter.array = arrayList_in;
        mAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.close_db();
    }

    class InforMation{

        int id;
        String name;

        ArrayList<Integer> attenation = new ArrayList();

        ArrayList<Integer> medation = new ArrayList();
        Double avg_attenation,avg_medation;

        DecimalFormat df = new DecimalFormat("#.00");

        InforMation(int id,String name,String attenation,String medation){
            this.id = id;
            this.name = name;

            avg_attenation = set_att(attenation);
            avg_attenation = Double.parseDouble(df.format(avg_attenation));
            avg_medation = set_med(medation);
            avg_medation = Double.parseDouble(df.format(avg_medation));
        }
        public Double set_att(String tmp){
            Double count=0.0;
            while (tmp.contains("D")) {
                int a = Integer.parseInt(tmp.substring(tmp.indexOf(":") + 1, tmp.indexOf("D")));
                count+=a;
                tmp = tmp.substring(tmp.indexOf("D") + 1);
                attenation.add(a);
            }
            if(attenation.size()==0) return 0.0;
            return count/attenation.size();
        }
        public Double set_med(String tmp){
            Double count=0.0;
            while (tmp.contains("D")) {
                int a = Integer.parseInt(tmp.substring(tmp.indexOf(":") + 1, tmp.indexOf("D")));
                count+=a;
                tmp = tmp.substring(tmp.indexOf("D") + 1);
                medation.add(a);
            }
            if(medation.size()==0) return 0.0;
            return count/medation.size();
        }
    }


    class MyAdapter extends BaseAdapter{

        LayoutInflater lif;

        ArrayList<InforMation> array;


        public MyAdapter(Context context,ArrayList c){
            lif = LayoutInflater.from(context);
            this.array = c;
        }
        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int position) {
            return array.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = lif.inflate(R.layout.layout_wave,null);

            TextView txv_id = (TextView)convertView.findViewById(R.id.test_txv_layoutid);
            txv_id.setText("id: "+array.get(position).id);


            TextView txv_name = (TextView)convertView.findViewById(R.id.test_txv_layoutname);
            txv_name.setText("name: "+array.get(position).name);

            if(position == which_isWinner){
                txv_name.setTextColor(Color.YELLOW);
            }
            else {
                txv_name.setTextColor(Color.BLACK);
            }
            TextView txv_attenation = (TextView)convertView.findViewById(R.id.test_txv_layoutattenation);
            txv_attenation.setText("Attenation: "+array.get(position).avg_attenation);
            if(array.get(position).avg_attenation == attenationLow){
                txv_attenation.setTextColor(Color.GREEN);
            }
            else if(array.get(position).avg_attenation == attenationHeight){
                txv_attenation.setTextColor(Color.RED);
            }
            else{
                txv_attenation.setTextColor(Color.BLACK);
            }

            TextView txv_medation = (TextView)convertView.findViewById(R.id.test_txv_layoutmedation);
            txv_medation.setText("Medation: "+array.get(position).avg_medation);

            if(array.get(position).avg_medation == medationLow){
                txv_medation.setTextColor(Color.GREEN);
            }
            else if(array.get(position).avg_medation == medationHeight){
                txv_medation.setTextColor(Color.RED);
            }
            else{
                txv_medation.setTextColor(Color.BLACK);
            }


            return convertView;
        }
    }


}
