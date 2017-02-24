package com.neurosky.mindwavemobiledemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends Activity {


    FrameLayout frl_vir,frl_real;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        frl_vir = (FrameLayout)findViewById(R.id.main_frlvir);
        frl_vir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,DemoActivity.class);
                startActivity(it);
            }
        });

        frl_real = (FrameLayout)findViewById(R.id.main_frlreal);
        frl_real.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,BluetoothLivActivity.class);
                startActivity(it);
            }
        });
    }



}
