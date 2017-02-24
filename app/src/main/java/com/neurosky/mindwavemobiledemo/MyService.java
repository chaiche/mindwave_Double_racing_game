package com.neurosky.mindwavemobiledemo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MyService extends Service {

    private static final String TAG ="MyService";

    BluetoothAdapter myBluetooth =null;
    String address;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket btSocket = null;

    Handler handler = new Handler();


    Boolean isStart = false;


    int speed =0;
    int dir = -1;
    float rotation = 0;

    Boolean isgoBack = false;


    public MyService() {
    }

    private LocalBinder mLocBin = new LocalBinder();

    public class LocalBinder extends Binder //宣告一個繼承 Binder 的類別 LocalBinder
    {
        MyService getService()
        {
            return  MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "onBind");

        return mLocBin;
    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        Log.d(TAG, "onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {

        Log.d(TAG, "onUnbind");

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(btSocket == null) {
            Log.d(TAG, "onStartCommand");
            address = intent.getStringExtra("address");
            Log.d(TAG,address);
            myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
            BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
            try {
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void sendstart(){
        handler.postDelayed(runnable,0);
        isStart = true;
    }
    public void sendstop(){

        handler.removeCallbacks(runnable);

        speed = 0;
        dir = 0;
        rotation = 0;
        if(btSocket!=null){
            try {
                btSocket.getOutputStream().write(("speed:"+speed+"dir:"+dir+"rotation:"+rotation+"+").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        isStart = false;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();


        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e) {
                Log.d(TAG, "onDestroy Error");
            }
        }

        handler.removeCallbacks(runnable);

        Log.d(TAG, "onDestroy");
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendDataToArduino();
            handler.postDelayed(runnable, 300);
        }
    };
    public void sendDataToArduino(){
        if(btSocket!=null){
            try {
                int tmp_speed = speed;
                if(isgoBack){
                    tmp_speed = (tmp_speed > 0) ? -tmp_speed : tmp_speed;
                }
                btSocket.getOutputStream().write((
                        "speed:" + tmp_speed + "dir:" + dir + "rotation:" + rotation + "+").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
