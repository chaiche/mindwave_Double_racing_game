package com.neurosky.mindwavemobiledemo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

public class RecordActivity extends Activity implements Frg_showrecordlist.Mylistener {

    public void show_waveForm(InforMation inforMation){
        frg_showWaveForm.set(inforMation);
    }

    Frg_showrecordlist frg_showrecordlist = new Frg_showrecordlist();
    Frg_showWaveForm frg_showWaveForm = new Frg_showWaveForm();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);

        setUpFrg();
    }


    public void setUpFrg(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.add(R.id.record_lin_showlist, frg_showrecordlist, "showlist");

        transaction.add(R.id.record_lin_showWaveForm, frg_showWaveForm, "showWaveForm");
        transaction.commit();
    }


}
