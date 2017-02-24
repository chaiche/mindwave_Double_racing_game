package com.neurosky.mindwavemobiledemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class SendDataToArduinoActivity extends Activity implements SensorEventListener{


    private final static String TAG = "SendDataToArduino";

    private MyService myService = null;

    String address = "";

    // sensor

    private SensorManager sm;
    private Sensor sr;
    TextView txv_dir;

    Boolean isStart = false;

    //
    Button btn_startOrstop;

    //
    Button btn_start;

    //
    Button btn_goBack;

    Button btn_addspeed,btn_subspeed;

    private TgStreamReader tgStreamReader;
    private BluetoothAdapter mBluetoothAdapter;

    int speed = 0;

    DataBase mdb = null;
    ArrayList array_attenation = new ArrayList();
    ArrayList array_medation = new ArrayList();

    EditText edt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_send_data_to_arduino);



        Intent newint = getIntent();
        address = newint.getStringExtra("address");
        Log.d("Show address", address);

        new MyAsyncTask(this).execute();


        txv_dir = (TextView)findViewById(R.id.SendData_txv_Dir);

        initSensor();


        btn_startOrstop = (Button)findViewById(R.id.SendData_startOrstop);
        btn_startOrstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStart == false) {
                    isStart = true;
                    btn_startOrstop.setText("Stop");
                    myService.sendstart();
                } else if (isStart == true) {
                    isStart = false;
                    btn_startOrstop.setText("Start");
                    myService.sendstop();
                }
            }
        });

        btn_start = (Button)findViewById(R.id.senddatatoarduino_btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                badPacketCount = 0;

                // (5) demo of isBTConnected
                if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

                    // Prepare for connecting
                    tgStreamReader.stop();
                    tgStreamReader.close();
                }

                // (4) Demo of  using connect() and start() to replace connectAndStart(),
                // please call start() when the state is changed to STATE_CONNECTED
                tgStreamReader.connect();
            }
        });
        btn_goBack = (Button)findViewById(R.id.SendData_btn_goBack);
        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_goBack.getText().equals("後退")) {
                    myService.isgoBack = true;
                    btn_goBack.setText("前進");
                } else if (btn_goBack.getText().equals("前進")) {
                    myService.isgoBack = false;
                    btn_goBack.setText("後退");
                }
            }
        });

        btn_addspeed = (Button)findViewById(R.id.sendData_btn_addspeed);

        btn_addspeed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if (myService.speed < 400) {
                        myService.speed+=25;
                        ((TextView)findViewById(R.id.senddatatoarduino_txv_mind)).setText("speed:"+ myService.speed);
                    }
                    return true;
                }
                return false;
            }
        });

        btn_subspeed = (Button)findViewById(R.id.sendData_btn_subspeed);
        btn_subspeed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if (myService.speed > 0) {
                        myService.speed-=25;
                        ((TextView)findViewById(R.id.senddatatoarduino_txv_mind)).setText("speed:"+ myService.speed);
                    }
                    return true;
                }
                return false;
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Example of constructor public TgStreamReader(BluetoothAdapter ba, TgStreamHandler tgStreamHandler)
        tgStreamReader = new TgStreamReader(mBluetoothAdapter, callback);
        // (2) Demo of setGetDataTimeOutTime, the default time is 5s, please call it before connect() of connectAndStart()
        tgStreamReader.setGetDataTimeOutTime(6);
        // (3) Demo of startLog, you will get more sdk log by logcat if you call this function
        tgStreamReader.startLog();

        setUpDrawWaveView();

        mdb = new DataBase(this);
        array_attenation.clear();
        array_medation.clear();

        edt = (EditText)findViewById(R.id.sendData_edt);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Settings.play_name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private class MyAsyncTask extends AsyncTask<Boolean, Void, Boolean> {

        private Context mContext;
        private ProgressDialog mDialog;

        public MyAsyncTask(Context mContext){
            this.mContext = mContext;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDialog.dismiss();
        }


        @Override
        protected Boolean doInBackground(Boolean... params) {


            Intent it_startservice = new Intent(SendDataToArduinoActivity.this,MyService.class);
            it_startservice.putExtra("address", address);
            startService(it_startservice);

            Intent it = new Intent(SendDataToArduinoActivity.this, MyService.class);
            bindService(it, mServiceConnection, BIND_AUTO_CREATE); //綁定Service

            return true;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
           // mDialog.setProgress(progress[0]);
        }
    }


    private void initSensor() {
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        sr = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    int dir = -1;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        //txv_x.setText("x = " + String.valueOf(values[0]));
        //txv_y.setText("y = " + String.valueOf(values[1]));
        //txv_z.setText("z = " + String.valueOf(values[2]));

        if(myService!=null && isStart) {
            float y = values[1];
            DecimalFormat df = new DecimalFormat("##.0");
            y = Float.parseFloat(df.format(y));
            if (y < -1) {
                txv_dir.setText("left:" + y);
                myService.dir = 1;
            } else if (y > -1.5 && y < 1.5) {
                txv_dir.setText("forward: " + y);
                myService.dir = 0;
            } else if (y > 1) {
                txv_dir.setText("right: " + y);
                myService.dir = 2;
            }
            y = (y < 0) ? -y : y;
            y = (98 - y * 10) / 100;
            //txv_dir.setText("y: "+ y);
            myService.rotation = y;
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onResume(){
        super.onResume();
        sm.registerListener(this, sr, SensorManager.SENSOR_DELAY_GAME);

    }
    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();


        unbindService(mServiceConnection);
        Intent it_stopservice = new Intent(this,MyService.class);
        stopService(it_stopservice);



        if (tgStreamReader != null) {
            tgStreamReader.close();
            tgStreamReader = null;
        }


        saveDataToDatabase();

        if(mdb!=null)
            mdb.close_db();

        finish(); //return to the first layout
    }

    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;

    private int badPacketCount = 0;

    // (7) demo of TgStreamHandler
    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            Log.d(TAG, "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    // Do something when connecting
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    tgStreamReader.start();
                    showToast("Connected", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working

                    //(9) demo of recording raw data , stop() will call stopRecordRawData,
                    //or you can add a button to control it.
                    //You can change the save path by calling setRecordStreamFilePath(String filePath) before startRecordRawData

                    tgStreamReader.startRecordRawData();

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout

                    //(9) demo of recording raw data, exception handling
                    tgStreamReader.stopRecordRawData();

                    showToast("Get data time out!", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_STOPPED:
                    // Do something when stopped
                    // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                    // tgStreamReader.connectAndstart(), because we have to prepare for that.

                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    break;
                case ConnectionStates.STATE_ERROR:
                    // Do something when you get error message
                    break;
                case ConnectionStates.STATE_FAILED:
                    // Do something when you get failed message
                    // It always happens when open the BluetoothSocket error or timeout
                    // Maybe the device is not working normal.
                    // Maybe you have to try again
                    break;
            }
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_STATE;
            msg.arg1 = connectionStates;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onRecordFail(int flag) {
            // You can handle the record error message here
            Log.e(TAG, "onRecordFail: " + flag);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // You can handle the bad packets here.
            badPacketCount++;
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_BAD_PACKET;
            msg.arg1 = badPacketCount;
            LinkDetectedHandler.sendMessage(msg);

        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            // You can handle the received data here
            // You can feed the raw data to algo sdk here if necessary.

            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = datatype;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);

            //Log.i(TAG,"onDataReceived");
        }

    };

    public boolean isRowCanChange = true;

    private Handler LinkDetectedHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // (8) demo of MindDataType
            switch (msg.what) {
                case MindDataType.CODE_RAW:   // 扎眼


                    if(msg.arg1<-500) {
                        if(isRowCanChange) {
                            isRowCanChange = false;
                            if (btn_goBack.getText().equals("後退")) {
                                myService.isgoBack = true;
                                btn_goBack.setText("前進");

                            } else if (btn_goBack.getText().equals("前進")) {
                                myService.isgoBack = false;
                                btn_goBack.setText("後退");
                            }
                            Log.d(TAG, msg.arg1 + "");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    isRowCanChange = true;
                                }
                            }).start();
                        }
                    }

                    break;
                case MindDataType.CODE_MEDITATION:
                    if(waveView!=null &&isStart) {
                        waveView.updateDataformediation(msg.arg1);
                        array_medation.add(":" + msg.arg1 + "D");
                    }

                    break;
                case MindDataType.CODE_ATTENTION:

                    myService.speed = msg.arg1*4;
                    ((TextView)findViewById(R.id.senddatatoarduino_txv_mind)).setText("speed:" + myService.speed);

                    if(isStart) {
                        updateWaveView(msg.arg1);
                        array_attenation.add(":" + msg.arg1 + "D");
                    }

                    break;
                case MindDataType.CODE_POOR_SIGNAL://
                    int poorSignal = msg.arg1;

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void showToast(final String msg, final int timeStyle) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), msg, timeStyle).show();
            }

        });
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected()" + name.getClassName());

            myService = ((MyService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected()" + name.getClassName());
            myService = null;
        }
    };

    LinearLayout wave_layout;
    DrawWaveView waveView = null;

    public void setUpDrawWaveView() {
        wave_layout = (LinearLayout) findViewById(R.id.sendData_wave_layout);
        waveView = new DrawWaveView(getApplicationContext());
        wave_layout.addView(waveView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        waveView.setValue(100, 100, 0);
    }

    public void updateWaveView(int data) {
        if (waveView != null) {
            waveView.updateData(data);
        }
    }

    public void saveDataToDatabase(){

        mdb.addData(Settings.play_name, getStingFromArrayList(array_attenation), getStingFromArrayList(array_medation));
    }
    public String getStingFromArrayList(ArrayList array){
        String tmp ="";
        for(int i =0;i<array.size();i++){
            tmp+= array.get(i);
        }
        return tmp;
    }




}
