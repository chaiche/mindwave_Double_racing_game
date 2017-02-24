package com.neurosky.mindwavemobiledemo;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by chaiche on 16/10/7.
 */
public class Settings {

    private static int connect_way;
    private static int which_model;

    public static boolean play_sound = false;

    public static String play_name = "";

    public static String play_another_name = "";

    public static boolean isConnectNetWork = false;

    DemoActivity activity;

    Settings(DemoActivity activity){

        this.activity = activity;
    }
    public void setConnect_way(int i){
        connect_way = i;

        if(connect_way==0){
            ((TextView)activity.findViewById(R.id.prepare_txv_theway)).setText("藍芽");
        }
        else{
            ((TextView)activity.findViewById(R.id.prepare_txv_theway)).setText("單人遊玩");
        }

        // 0 藍芽
        // 1 wifi_direct
    }
    public void setWhich_model(int i) {
        which_model = i;
    }
    public static int getConnect_way(){
        return connect_way;
    }
    public static int getWhich_model(){
        return which_model;
    }

}
