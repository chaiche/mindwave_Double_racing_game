package com.neurosky.mindwavemobiledemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameStartActivity extends Activity {



    final static String gameName = "結合腦波儀之手機賽車遊戲";
    LinearLayout lin;
    ArrayList<TextView> txv_array;
    ObjectAnimator anim = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_start);

        lin = (LinearLayout)findViewById(R.id.ganeStart_lin);

        txv_array = new ArrayList<>();


        for(int i=0;i<gameName.length();i++){
            String c = gameName.substring(i,i+1);
            TextView tmp = getView(c);
            txv_array.add(tmp);
            lin.addView(tmp);
            Log.d("123",c);
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GameStartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runGo();
                    }
                });

            }
        },200);
    }


    public TextView getView(String a){

        TextView txv = new TextView(this);

        txv.setText(a);
        txv.setTextColor(Color.WHITE);
        txv.setAlpha((float) 0.0);
        txv.setTextSize((float)24);

        return txv;
    }

    int j = 0;


    public void runGo(){

        anim = ObjectAnimator//
                .ofFloat(txv_array.get(j), "alpha", 0.0F, 1.0F)//
                .setDuration(100);//
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                j++;
                if(j>txv_array.size()-1){
                    Intent it = new Intent(GameStartActivity.this,MainActivity.class);
                    startActivity(it);
                    finish();
                    return;
                }
                runGo();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(anim.isRunning()){
            anim.pause();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(anim!=null){
            anim.start();
        }
    }



}
