package com.neurosky.mindwavemobiledemo;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frg_showrecordlist extends Fragment {


    public Frg_showrecordlist() {
        // Required empty public constructor
    }
    Mylistener mylistener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        mylistener = (Mylistener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frg_showrecordlist, container, false);
    }

    private ListView liv;
    private MyAdapter adapter;
    ArrayList<InforMation> arrayList_in = null;

    Double attenationLow = 100.0,attenationHeight = 0.0 ;
    Double medationLow = 100.0,medationHeight =0.0;

    int which_isWinner = 0;

    DataBase db = null;



    @Override
    public void onResume(){
        super.onResume();

        db = new DataBase(getActivity());


        liv = (ListView)getActivity().findViewById(R.id.frg_showrecord_liv);
        arrayList_in = new ArrayList<>();
        get_db_data();
        adapter = new MyAdapter(getActivity(),arrayList_in);

        liv.setAdapter(adapter);

        liv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mylistener.show_waveForm(arrayList_in.get(position));
            }
        });
    }

    public void get_db_data(){

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


    private class MyAdapter extends BaseAdapter{

        LayoutInflater lf;
        ArrayList<InforMation> array;

        public MyAdapter(Context context,ArrayList<InforMation> array){
            lf = LayoutInflater.from(context);
            this.array = array;
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = lf.inflate(R.layout.layout_wave,null);
            TextView txv_id = (TextView)convertView.findViewById(R.id.test_txv_layoutid);
            txv_id.setText("Round: "+array.get(position).id);


            TextView txv_name = (TextView)convertView.findViewById(R.id.test_txv_layoutname);
            txv_name.setText("Name: "+array.get(position).name);

            if(position == which_isWinner){
                txv_name.setTextColor(Color.YELLOW);
            }
            else {
                txv_name.setTextColor(Color.BLACK);
            }
            TextView txv_attenation = (TextView)convertView.findViewById(R.id.test_txv_layoutattenation);
            txv_attenation.setText("Attention: "+array.get(position).avg_attenation);
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
            txv_medation.setText("Meditation: "+array.get(position).avg_medation);

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

    public interface Mylistener{

        void show_waveForm(InforMation inforMation);

    }

    @Override
    public void onStop(){
        super.onStop();

        if(db!=null)
            db.close_db();
    }


}
