package com.neurosky.mindwavemobiledemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by chaiche on 16/12/12.
 */
public class Music {

    GameTestActivity activity = null;

    AudioManager adm;
    MediaPlayer mdp = null;

    Music(GameTestActivity activity){
        this.activity  = activity;
        adm = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

    }

    public void play() {
        mdp = MediaPlayer.create(activity, R.raw.jay_jay);
        mdp.setLooping(true);
        try {
            mdp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        if(mdp!=null){
            mdp.start();
        }
        else{
            play();
        }
    }
    public void pause(){
        if(mdp!=null){
            mdp.pause();
        }
    }
    public void stop(){
        if(mdp!=null){
            mdp.stop();
        }
    }

}
