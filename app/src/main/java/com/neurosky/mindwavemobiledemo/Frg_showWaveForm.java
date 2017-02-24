package com.neurosky.mindwavemobiledemo;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frg_showWaveForm extends Fragment {


    public Frg_showWaveForm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frg_show_wave_form, container, false);
    }

    LinearLayout wave_layout;
    @Override
    public void onResume(){
        super.onResume();

        wave_layout = (LinearLayout)getActivity().findViewById(R.id.record_wavelayout);
        setUpDrawWaveView();
    }

    DrawWaveView waveView = null;

    public void setUpDrawWaveView() {

        waveView = new DrawWaveView(getContext());
        wave_layout.addView(waveView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        waveView.setValue(150, 101, 0);

    }

    public void set(InforMation inforMation){
        waveView.clear();
        int tmp_attenation = inforMation.attenation.size();
        int tmp_medation = inforMation.medation.size();

        int count = 0;
        if(count<tmp_attenation) count = tmp_attenation;
        if(count<tmp_medation) count = tmp_medation;
        waveView.setValue(count,100,0);
        waveView.initView();

        for(int i =0;i<tmp_attenation;i++){
            waveView.updateData(inforMation.attenation.get(i));
        }
        for(int i =0;i<tmp_medation;i++){
            waveView.updateDataformediation(inforMation.medation.get(i));
        }
    }




}
