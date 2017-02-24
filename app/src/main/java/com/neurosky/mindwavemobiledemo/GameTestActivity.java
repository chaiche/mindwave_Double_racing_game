package com.neurosky.mindwavemobiledemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GameTestActivity extends Activity implements SensorEventListener {


    private static final String TAG = "gameTest";

    private TgStreamReader tgStreamReader;

    private BluetoothAdapter mBluetoothAdapter;

    SQLiteDatabase db = null;
    static final String db_name = "Test";
    static final String tb_name = "Attenation";
    static final String tb_name_mediation = "Mediaion";

    DataBase mdb = null;
    ArrayList array_attenation = new ArrayList();
    ArrayList array_medation = new ArrayList();


    boolean isGroupOwner = false;
    Client client = null;
    Serve serve = null;


    final JsInterface myJavaScriptInterface = new JsInterface(this);

    private boolean isBluetooth = false;

    public Music music = null;
    private class JsInterface {
        public JsInterface(GameTestActivity mainActivity) {

        }

        @JavascriptInterface
        public void prepare() {
            prepareAleardy();
        }
        @JavascriptInterface
        public void sendDataByHTTP(String message) {
            sendDataForHTTP(message);
        }

        @JavascriptInterface
        public void sendData(String message) {
            if(isBluetooth){
                if(bluetooth!=null)
                    bluetooth.send(message);

            }
            else{
                if(isGroupOwner) {
                    if (client != null) {
                        client.sendData(message);
                    }
                }
                else{
                    if (serve != null) {
                        serve.sendData(message);
                    }
                }
            }
        }

        @JavascriptInterface
        public void sendEnd() {
            showend("you win");
        }

        @JavascriptInterface
        public void sendCreateRe(String tmp) {
            cs.getRe(tmp);
        }

        @JavascriptInterface
        public void sendSoundPool(int i){
            play_soundPool(i);
        }


    }

    View layoutGame, layoutConnect;

    SoundPool sp;
    int sp_collision,sp_victory,sp_lose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        LayoutInflater inflater = LayoutInflater.from(this);
        layoutGame = inflater.inflate(R.layout.activity_game_test, null);
        layoutConnect = inflater.inflate(R.layout.connect_list, null);



        try {
            // (1) Make sure that the device supports Bluetooth and Bluetooth is on
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        this,
                        "請打開藍芽！",
                        Toast.LENGTH_LONG).show();
                finish();
				return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "error:" + e.getMessage());
            return;
        }

        init();


        if(Settings.getConnect_way() == 0){
            Toast.makeText(GameTestActivity.this, "您選擇藍芽", Toast.LENGTH_SHORT).show();
            setView(2);
            bluetooth = new Bluetooth(GameTestActivity.this);
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView)findViewById(R.id.connect_list_txv_show)).setText("正在搜尋裝置");
                    bluetooth.btc.searchBluetooth();
                }
            });
            isBluetooth = true;
        }
        else{
            setView(1);
            initView1();
            thisObject = 0;
            thisObject_model = Settings.getWhich_model();
            anotherObject = 1;
            anotherObject_model = 2;
            createGame();
            //createGameWeb();
        }

        music = new Music(this);


    }
    public void setView(int i){
        if(i==1){
            GameTestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(layoutGame);
                    initView1();
                    if(bluetooth!=null){

                    }
                }
            });
        }
        else{
            setContentView(layoutConnect);
            initView2();
        }
    }


    private Long startTime;


    private Handler handler = new Handler();

    HttpUrlConnection hp;

    String url_senddata = "",url_sendcreatgame="",url_sendDeletegame = "",url_sendsaveMindwavedata="";
    Map<String,String> m_map;



    public HandlerThread mHandlerThread = new HandlerThread("chaiche");
    Handler h;


    public void init(){

        initSensor();

        url_senddata = "http://www.chaiche.tk/updatedata.php";
        url_sendcreatgame ="http://www.chaiche.tk/creatGame.php";
        url_sendDeletegame = "http://www.chaiche.tk/deleteGame.php";
        url_sendsaveMindwavedata = "http://www.chaiche.tk/saveData_mindwave.php";

//        url_senddata = "http://chaiche.esy.es/updatedata.php";
//        url_sendcreatgame ="http://chaiche.esy.es/creatGame.php";
//        url_sendDeletegame = "http://chaiche.esy.es/deleteGame.php";


        m_map = new HashMap<String, String>();
        hp = new HttpUrlConnection(url_senddata,m_map);
        mHandlerThread.start();
        h =  new Handler(mHandlerThread.getLooper());

        // Example of constructor public TgStreamReader(BluetoothAdapter ba, TgStreamHandler tgStreamHandler)
        tgStreamReader = new TgStreamReader(mBluetoothAdapter, callback);
        // (2) Demo of setGetDataTimeOutTime, the default time is 5s, please call it before connect() of connectAndStart()
        tgStreamReader.setGetDataTimeOutTime(6);
        // (3) Demo of startLog, you will get more sdk log by logcat if you call this function
        tgStreamReader.startLog();


        //goStart(null);

        //////////// SQLite
        openOrcreatdb();

        sp = new SoundPool(10, AudioManager.STREAM_MUSIC,5);

        sp_collision = sp.load(this,R.raw.collision,1);
        sp_victory = sp.load(this,R.raw.victory1,1);
        sp_lose = sp.load(this,R.raw.lose1,1);



        mdb = new DataBase(this);
        array_attenation.clear();
        array_medation.clear();
    }

    public ListView liv;
    public Button btn_search;
    Bluetooth bluetooth = null;

    public void initView2(){
        liv = (ListView)findViewById(R.id.connect_list_liv);
        btn_search = (Button)findViewById(R.id.connect_list_btn_search);
    }
    private AlertDialog getAlertDialog(String title,String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

        builder.setMessage(message);

        builder.setPositiveButton("wifi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(GameTestActivity.this, "您選擇wifi", Toast.LENGTH_SHORT).show();

                Intent it = new Intent(GameTestActivity.this,ConnectWifiActivity.class);
                startActivityForResult(it,102);

            }
        });
        builder.setNegativeButton("藍芽", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(GameTestActivity.this, "您選擇藍芽", Toast.LENGTH_SHORT).show();
                setView(2);
                initView2();
                bluetooth = new Bluetooth(GameTestActivity.this);
                btn_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TextView)findViewById(R.id.connect_list_txv_show)).setText("正在搜尋裝置");
                        bluetooth.btc.searchBluetooth();
                    }
                });
                isBluetooth = true;
            }
        });
        return builder.create();
    }

    private TextView txv_signal, txv_attention, txv_mediation;
    private TextView txv4;
    private TextView txv_x, txv_y, txv_z;
    private WebView web;
    private int badPacketCount = 0;

    private LinearLayout wave_layout;

    private LinearLayout pb_layout;

    private ProgressBar pb_create;

    public TextView txv_status,txv_pb_num;


    private void initView1() {

        txv_signal = (TextView) findViewById(R.id.gameTest_txv_signal);
        txv_attention = (TextView) findViewById(R.id.gameTest_txv_attention);
        txv_mediation = (TextView) findViewById(R.id.gameTest_txv_mediation);

        txv4 = (TextView) findViewById(R.id.textView4);

        web = (WebView) findViewById(R.id.webView);
        //setWeb();
        //setInterface();

        txv_x = (TextView) findViewById(R.id.gameTest_txv_x);
        txv_y = (TextView) findViewById(R.id.gameTest_txv_y);
        txv_z = (TextView) findViewById(R.id.gameTest_txv_z);

        wave_layout = (LinearLayout) findViewById(R.id.wave_layout);

        pb_layout = (LinearLayout)findViewById(R.id.game_layout_pb);

        pb_create = (ProgressBar)findViewById(R.id.game_pb_create);
        pb_create.setMax(100);
        pb_create.setProgress(0);

        txv_status = (TextView)findViewById(R.id.game_txv_status);

        txv_pb_num = (TextView)findViewById(R.id.game_txv_pb_num);
        txv_pb_num.setText("0/100");




    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWeb() {

        final WebViewClient client = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

        };

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.setWebViewClient(client);
        web.loadUrl("file:///android_asset/index.html");

        //web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    @SuppressLint("JavaScriptInterface")
    private void setInterface() {
        web.addJavascriptInterface(myJavaScriptInterface, "GameJsToJava");
    }

    private SensorManager sm;
    Sensor sr;

    private void initSensor() {
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE); // 取的sensor管理器
        sr = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  // 設定需要的感測器  三軸加速度計
    }


    public void goStart(View v) {
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
        setUpDrawWaveView();

//				tgStreamReader.connectAndStart();
    }

    public void goStop(View v) {
        tgStreamReader.stop();
        tgStreamReader.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sr, SensorManager.SENSOR_DELAY_GAME);
        //sm.registerListener(this, sr2, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
        music.pause();
    }

    @Override
    protected void onDestroy() {
        //(6) use close() to release resource
        if (tgStreamReader != null) {
            tgStreamReader.close();
            tgStreamReader = null;
        }
        if(db!=null)
            db.close();
        if(mdb!=null)
            mdb.close_db();
        if(bluetooth!=null){
            Log.d("close","bluetooth");
            if(bluetooth.serveThread!=null) {
                bluetooth.serveThread.close();
                bluetooth.serveThread.interrupt();
            }
            if(bluetooth.clientThread!=null) {
                bluetooth.clientThread.close();
                bluetooth.clientThread.interrupt();
            }
            bluetooth.closeOsIs();
            bluetooth.btc.unregisterRe();
        }

        music.stop();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();


    }

    public void stop() {
        if (tgStreamReader != null) {
            tgStreamReader.stop();
            tgStreamReader.close();
        }
        Log.d("stop", "stop");
        sm.unregisterListener(this);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                web.loadUrl("");
            }
        });

        //sendDeleteGame();
        finish();
    }

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

    DrawWaveView waveView = null;

    public void setUpDrawWaveView() {
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

    private boolean isPressing = false;
    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;

    int raw;
    private Handler LinkDetectedHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // (8) demo of MindDataType
            switch (msg.what) {
                case MindDataType.CODE_RAW:   // 扎眼

                    break;
                case MindDataType.CODE_MEDITATION:
                    Log.d(TAG, "HeadDataType.CODE_MEDITATION " + msg.arg1);
                    txv_mediation.setText("" + msg.arg1);
                    addDataForMediation(msg.arg1);
                    array_medation.add(":" + msg.arg1 + "D");
                    if(waveView!=null)
                        waveView.updateDataformediation(msg.arg1);
                    break;
                case MindDataType.CODE_ATTENTION:
                    Log.d(TAG, "CODE_ATTENTION " + msg.arg1);
                    txv_attention.setText("" + msg.arg1);

                    updateWaveView(msg.arg1);
                    addData(msg.arg1);
                    array_attenation.add(":" + msg.arg1 + "D");

                    changeSpeed((int) msg.arg1);

                    break;
                case MindDataType.CODE_POOR_SIGNAL://
                    int poorSignal = msg.arg1;
                    Log.d(TAG, "poorSignal:" + poorSignal);
                    txv_signal.setText("" + msg.arg1);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        //txv_x.setText("x = " + String.valueOf(values[0]));
        //txv_y.setText("y = " + String.valueOf(values[1]));
        //txv_z.setText("z = " + String.valueOf(values[2]));

        if (values[1] < -7) changeCarTheate(1);
        else if (values[1] < -5) changeCarTheate(2);
        else if (values[1] < -3) changeCarTheate(3);
        else if (values[1] < -1) changeCarTheate(4);
        else if (values[1] > -1 && values[1] < 1) changeCarTheate(5);
        else if (values[1] < 3) changeCarTheate(6);
        else if (values[1] < 5) changeCarTheate(7);
        else if (values[1] < 7) changeCarTheate(8);
        else if (values[1] < 9) changeCarTheate(9);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void changeCarTheate(final int i) {
//        Message msg = SendToWebHandler.obtainMessage();
//        msg.what = 0;
//        msg.arg1 = i;
//        SendToWebHandler.handleMessage(msg);
        if(web!=null && my_already==1) {
            web.post(new Runnable() {
                @Override
                public void run() {
                    web.loadUrl("javascript:carcontroller(" +i+ ")");
                }
            });
        }
    }

    public void changeSpeed(int i) {
        Message msg = SendToWebHandler.obtainMessage();
        msg.what = 1;
        msg.arg1 = i;
        SendToWebHandler.handleMessage(msg);
    }

    public void changeOtherCarPosition(final float x,final float z,final  float r){
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:changeOtherCarPosition(" + x + ","+z+","+r+")");
            }
        });
    }

    public void openOrcreatdb() {
        db = this.openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);

        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "attention INTEGER NOT NULL)";
        db.execSQL(createTable);

        createTable = "CREATE TABLE IF NOT EXISTS " + tb_name_mediation +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mediation INTEGER NOT NULL)";
        db.execSQL(createTable);
        db.delete(tb_name, null, null);
        db.delete(tb_name_mediation, null, null);
        //Cursor c = db.rawQuery("SELECT * FROM "+tb_name, null);
    }

    public void addData(int attention) {
        ContentValues cv = new ContentValues(1);
        cv.put("attention", attention);
        db.insert(tb_name, null, cv);
    }

    public void addDataForMediation(int attention) {
        ContentValues cv = new ContentValues(1);
        cv.put("mediation", attention);
        db.insert(tb_name_mediation, null, cv);
    }


    public void finishGame(View v) {
        //h.removeCallbacks(mRunnable_sendDeleteGame);
        stop();
        //finish();
    }

    public void setTxv4(final String a) {
        GameTestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txv4.setText(txv4.getText() + "\n" + a);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode ==RESULT_OK){
            if(requestCode==101){
                bluetooth.connect();
            }
            else if(requestCode ==102){

                isGroupOwner = data.getBooleanExtra("isGroupOwner", false);
                if (isGroupOwner) {
                    client = new Client(this, data.getStringExtra("address"));
                } else {
                    serve = new Serve(this);
                }
            }
        }
    }
    public Handler SendToWebHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    web.loadUrl("javascript:carcontroller(" + msg.arg1 + ")");
                    break;
                case 1:
                    web.loadUrl("javascript:carChangeSpeed(" + msg.arg1 + ")");
                    break;
                case 3:
                    web.loadUrl("javascript:changeOtherCarPosition(" + msg.arg1 + ","+msg.arg2+")");
                    break;

            }
        }
    };


    //////////////////////////////////
    // 結束畫面

    AlertDialog alertDialog;
    AlertDialog.Builder alert;

    public void showend(String messenge){
        if(alert==null) {
            showEndDialog(messenge);
            if(messenge.contains("win")){
                play_soundPool(2);
            }
            else{
                play_soundPool(3);
            }
            if(bluetooth!=null)
                bluetooth.send("END");
        }
    }
    public void showEndDialog(String messenge){
        String a = ((TextView)findViewById(R.id.txv_game_time)).getText().toString();
        
        saveDataToDatabase();
        sendDeleteGame();

        alert = new AlertDialog.Builder(GameTestActivity.this)
                .setMessage(a+"\n"+messenge)
                .setPositiveButton("結束", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(), R.string.gogo, Toast.LENGTH_SHORT).show();
                        sp.stop(sp_victory);
                        sp.stop(sp_lose);
                        setResult(RESULT_OK);
                        stop();
                    }
                });
        alertDialog = alert.create();
        alertDialog.show();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = alertDialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.45);   //高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 0.3);

        alertDialog.getWindow().setAttributes(p);
    }

    //儲存資料到本地資料庫

    public void saveDataToDatabase(){

        mdb.addData(Settings.play_name, getStingFromArrayList(array_attenation), getStingFromArrayList(array_medation));

        if(array_attenation.size()>0 && array_medation.size()>0)
            sendSaveMindwavedata();
    }
    public String getStingFromArrayList(ArrayList array){
        String tmp ="";
        for(int i =0;i<array.size();i++){
            tmp+= array.get(i);
        }
        return tmp;
    }


    //////////////////////////////
    //傳輸Data 至網路資料庫


    public String to_message="";

    public int thisGame;

    public boolean isCreatGame = false;

    public Runnable mRunnable_sendData = new Runnable() {
        @Override
        public void run() {

            if(Settings.isConnectNetWork) {
                String id = to_message.substring(to_message.indexOf(":") + 1, to_message.indexOf("x"));
                Double x = Double.parseDouble(to_message.substring(to_message.indexOf("x") + 1, to_message.indexOf("z")));
                Double z = Double.parseDouble(to_message.substring(to_message.indexOf("z") + 1, to_message.indexOf("r")));
                Double r = Double.parseDouble(to_message.substring(to_message.indexOf("r") + 1));

                m_map.clear();
                m_map.put("whichGame", thisGame + "");
                m_map.put("id", id);
                m_map.put("position_x", Double.toString(x));
                m_map.put("position_z", Double.toString(z));
                m_map.put("rotation", Double.toString(r));
                hp.setMap(m_map);

                String a = hp.sendHttpURLConnectionPOST(url_senddata);
            }

        }
    };
    public Runnable mRunnable_sendCreatGame = new Runnable() {
        @Override
        public void run() {

            if(Settings.isConnectNetWork) {
                m_map.clear();
                m_map.put("player1", Settings.play_name);
                m_map.put("player1_model", thisObject_model + "");
                m_map.put("player2", Settings.play_another_name);
                m_map.put("player2_model", anotherObject_model + "");
                hp.setMap(m_map);

                String a = hp.sendHttpURLConnectionPOST(url_sendcreatgame);
                isCreatGame = true;
                a = a.substring(0, a.indexOf("\t"));
                thisGame = Integer.parseInt(a);
                if (bluetooth != null) {
                    bluetooth.send("createGame" + thisGame);
                }
                setTxv4("createGame id : " + thisGame);
            }
        }
    };
    public Runnable mRunnable_sendDeleteGame = new Runnable() {
        @Override
        public void run() {

            if(Settings.isConnectNetWork) {
                m_map.clear();
                m_map.put("whichGame", thisGame + "");
                hp.setMap(m_map);

                String a = hp.sendHttpURLConnectionGet(url_sendDeletegame);
                Log.d("deleteGame", a);
                if (a.contains("delete success")) stop();
            }
        }
    };

    public Runnable mRunnable_sendSaveMindwavedata = new Runnable() {
        @Override
        public void run() {

            if(Settings.isConnectNetWork) {
                m_map.clear();
                m_map.put("name", Settings.play_name);
                m_map.put("attenation", getStingFromArrayList(array_attenation));
                m_map.put("medation", getStingFromArrayList(array_medation));
                hp.setMap(m_map);

                String a = hp.sendHttpURLConnectionPOST(url_sendsaveMindwavedata);
                Log.d("savemindwave_data", a);
            }
        }
    };

    public void sendDataForHTTP(String message){
        to_message = message;
        h.post(mRunnable_sendData);
    }

    public void sendCreatGame(){
        if(thisObject == 0 && !isCreatGame)
            h.post(mRunnable_sendCreatGame);
    }

    public void sendDeleteGame(){
        h.removeCallbacks(mRunnable_sendData);
        if(thisObject == 0)
            h.post(mRunnable_sendDeleteGame);
    }

    public void sendSaveMindwavedata(){
        h.post(mRunnable_sendSaveMindwavedata);
    }


    public int thisObject = -1, anotherObject = -1;
    public int thisObject_model = -1, anotherObject_model = -1;


    //////////////////////////////////////////
    // 建立場景
    public void createGame(){

        GameTestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                goStart(null);
            }
        });

        sendCreatGame();
        createGameWeb();
    }

    public void createGameWeb(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setWeb();
                setInterface();
            }
        });

    }

    CreateScene cs = null;
    public void prepareAleardy(){
        cs = new CreateScene();
    }

    public int my_already=0,another_already=0;

    class CreateScene {

        Runnable setGameStart = new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:setGameStart()");
            }
        };
        Runnable setmyObject = new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:setmyObject(" + thisObject + ")");
            }
        };
        Runnable createObject = new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:createObject(" + thisObject + "," + thisObject_model + ")");
                web.loadUrl("javascript:createObject(" + anotherObject + "," + anotherObject_model + ")");
            }
        };
        Runnable createSky = new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:createSky()");
            }
        };
        Runnable createBackground = new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:createBackground()");
            }
        };
        Runnable createRoad = new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:createRoad()");
            }
        };

        Runnable callRender = new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:callRender()");
            }
        };


        int status_num = 0;

        CreateScene() {
            web.post(setmyObject);
            web.post(createObject);
            web.post(createSky);
            web.post(createBackground);
            web.post(createRoad);
        }

        public void getRe(final String tmp) {
            Log.d("getRe", tmp);
            GameTestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txv_status.setText(txv_status.getText() + tmp + "\n");
                }
            });
            status_num += 17;
            if (status_num < 100) {
                pb_create.setProgress(status_num);
            } else {
                pb_create.setProgress(100);
            }
            GameTestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txv_pb_num.setText(pb_create.getProgress() + "/100");
                    if (pb_create.getProgress() == 100) {
                        if (tmp.contains("render")) {
                            my_already = 1;
                            if (Settings.getConnect_way() == 0) {
                                bluetooth.send("anothor_player_already");
                            } else {
                                changelayout();
                            }
                            return;
                        } else {
                            web.post(callRender);
                        }

                    }
                }
            });

        }
        Runnable check_runnable = new Runnable() {
            @Override
            public void run() {
                if(my_already==1 && another_already==1) changelayout();
                else {
                    Log.d("check","not change");
                    h.post(check_runnable);
                }
            }
        };

        public void check(){
            h.post(check_runnable);

            Log.d("check my_already:", my_already + "    another_already:" + another_already);
        }

        public void changelayout(){
            Log.d("change", "1");
            GameTestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Settings.play_sound) {
                        music.start();
                    }
                    pb_layout.setVisibility(View.GONE);
                    startgame();
                }
            });

        }
        public void startgame(){
            web.post(setGameStart);

            //取得目前時間
            startTime = System.currentTimeMillis();
            //設定定時要執行的方法
            //handler.removeCallbacks(updateTimer);
            //設定Delay的時間
            //handler.postDelayed(updateTimer, 1000);
        }
        //固定要執行的方法
        private Runnable updateTimer = new Runnable() {
            public void run() {
                final TextView time = (TextView) findViewById(R.id.txv_game_time);
                time.setTextColor(Color.RED);
                Long spentTime = System.currentTimeMillis() - startTime;
                //計算目前已過分鐘數
                Long minius = (spentTime/1000)/60;
                //計算目前已過秒數
                Long seconds = (spentTime/1000) % 60;

                Long ms = (spentTime%1000)/10;

                time.setText(String.format("%02d:%02d:%02d",minius,seconds,ms));
                handler.postDelayed(this,1000/30);
            }
        };
    }

    // 音效

    public void play_soundPool(int i){
        if(Settings.play_sound) {
            switch (i) {
                case 1:    // 碰撞
                    sp.play(sp_collision, 1, 1, 0, 0, 1);
                    break;
                case 2:  // 勝利
                    sp.play(sp_victory, 1, 1, 0, 0, 1);
                    break;
                case 3:  // 失敗
                    sp.play(sp_lose, 1, 1, 0, 0, 1);
                    break;
                default:
            }
        }
    }


}