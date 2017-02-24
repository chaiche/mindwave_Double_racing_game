package com.neurosky.mindwavemobiledemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by chaiche on 16/8/26.
 */
public class Bluetooth {

    BluetoothAdapter mBtAdapter;

    GameTestActivity myActivity;

    BluetoothConnect btc;

    String tmp;

    ServeThread serveThread = null;
    ClientThread clientThread = null;


    public boolean isClient = false;
    public Bluetooth(GameTestActivity myActivity){

        this.myActivity = myActivity;

        mBtAdapter =  BluetoothAdapter.getDefaultAdapter();
        if(!mBtAdapter.isEnabled()) {
            Intent it = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            myActivity.startActivityForResult(it,101);
        }
        else {
            connect();
        }
    }

    public void connect(){

        Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        it.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        myActivity.startActivity(it);

        btc = new BluetoothConnect();

        serveThread = new ServeThread();
        serveThread.start();

    }


    public class BluetoothConnect extends BroadcastReceiver {

        private ArrayAdapter<String> deviceArrayAdapter;

        public ArrayList<DeviceInfo> deviceInfo = null;
        IntentFilter filter;

        public BluetoothConnect(){

            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            myActivity.registerReceiver(this, filter);

            deviceArrayAdapter = new ArrayAdapter<String>(myActivity,android.R.layout.simple_list_item_1);
            myActivity.liv.setAdapter(deviceArrayAdapter);
            myActivity.liv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    connect(position);
                }
            });
            deviceInfo = new ArrayList<DeviceInfo>();
            showDevice();

        }
        public void unregisterRe(){
            myActivity.unregisterReceiver(this);
        }
        public ArrayAdapter getDeviceArrayAdapter(){
            return deviceArrayAdapter;
        }
        public String getDeviceArrayAdapterInfo(int i){
            return deviceArrayAdapter.getItem(i);
        }

        public void searchBluetooth()
        {
            deviceArrayAdapter.clear();
            deviceInfo.clear();
            Log.d("BluetoothConnect","Start search");
            if (mBtAdapter.isDiscovering())
                mBtAdapter.cancelDiscovery();
            mBtAdapter.startDiscovery();
        }
        public void showDevice(){

            deviceArrayAdapter.clear();
            deviceInfo.clear();

            Set<BluetoothDevice> devices = mBtAdapter.getBondedDevices();
            if(devices.size()>0){
                for (BluetoothDevice device : devices) {
                    DeviceInfo info = new DeviceInfo(device.getName(),device.getAddress());
                    deviceInfo.add(info);
                    deviceArrayAdapter.add("name:" + device.getName());
                }
            }
            deviceArrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("BluetoothConnect", "Action Found");
                DeviceInfo info = new DeviceInfo(device.getName(),device.getAddress());
                deviceInfo.add(info);
                deviceArrayAdapter.add("name:" + device.getName());
                deviceArrayAdapter.notifyDataSetChanged();
            }
            else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                if (mBtAdapter.getState() == BluetoothAdapter.STATE_OFF)
                {
                    Log.d("BroadcastReceiver", "BluetoothOFF");
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Log.d("BroadcastReceiver", "search finish");
                ((TextView)myActivity.findViewById(R.id.connect_list_txv_show)).setText("搜尋裝置結果");
            }
            else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                Log.d("4","ACTION_ACL_CONNECTED");
            }
        }
        BluetoothDevice btDevice;

        public void connect(int i){

            btDevice = mBtAdapter.getRemoteDevice(deviceInfo.get(i).getDeviceAddress());

            Set devices = mBtAdapter.getBondedDevices();

            if(devices.contains(btDevice)){
                Log.d("has","has");

                if (mBtAdapter.isDiscovering())
                    mBtAdapter.cancelDiscovery();
//                try {
//                    ClsUtils.removeBond(btDevice.getClass(), btDevice);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }if (null == clientSocket) {
                clientThread = new ClientThread(btDevice);
                clientThread.start();
            }
            else{
                Log.d("not has","not has");
                try {
                    ClsUtils.createBond(btDevice.getClass(), btDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        public class DeviceInfo{
            String devicename;
            String deviceaddress;
            UUID deviceUUID;
            public DeviceInfo(String name,String address){
                this.devicename = name;
                this.deviceaddress = address;
            }
            public String getDevicenName(){
                return devicename;
            }
            public String getDeviceAddress(){
                return deviceaddress;
            }
            public UUID getDeviceUUID(){
                return deviceUUID;
            }
        }

    }

    private InputStream is = null;
    private OutputStream os = null;
    private CutThread cut = null;

    public class ServeThread extends Thread {

        private BluetoothServerSocket serverSocket;

        private BluetoothSocket serveSocket = null;

        ReadThread rd = null;

        public ServeThread() {
            //创建BluetoothServerSocket对象
            try {
                serverSocket = mBtAdapter.listenUsingInsecureRfcommWithServiceRecord("name", UUID.fromString("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Log.d("ServeThread", "create");
                serveSocket = serverSocket.accept();
                serverSocket.close();
                is = serveSocket.getInputStream();
                os = serveSocket.getOutputStream();

                rd = new ReadThread(serveSocket);
                rd.start();
                cut = new CutThread();
                cut.start();
                if (null != os) {
                    try {
                        os.write("從serve端發送成功".getBytes("utf-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("ServeThread","create error");
                e.printStackTrace();
            }
            myActivity.setView(1);


        }
        public void close(){
            if(rd!=null)
                rd.interrupt();
            closeSocket();
        }
        public void closeSocket(){
            if(serveSocket!=null) {
                try {
                    serveSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("serveSocketcloseSocket", "IOE");

                }
            }
        }
    }

    public class ClientThread extends Thread{

        BluetoothSocket clientsocket = null;
        BluetoothDevice btDevice;

        ReadThread rd = null;

        public ClientThread(BluetoothDevice device){
            if(serveThread!=null) serveThread.interrupt();
            this.btDevice = device;
        }
        @Override
        public void run() {
            try {
                Log.d("client","create");
                clientsocket = btDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));
                clientsocket.connect();

                 is = clientsocket.getInputStream();
                 os = clientsocket.getOutputStream();
                isClient = true;
                rd = new ReadThread(clientsocket);
                rd.start();

                myActivity.setView(1);


                cut = new CutThread();
                cut.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("client","create error");
                e.printStackTrace();
            }
            if (null != os) {
                try {
                    os.write("從client端發送成功".getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public void close(){
            if(rd!=null)
                rd.interrupt();
            closeSocket();
        }
        public void closeSocket(){
            if(clientsocket!=null) {
                try {
                    clientsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("clientSocketcloseSocket", "IOE");
                }
            }
        }
    }
    String all="";

    public void closeOsIs(){
        try {
            if(os!=null) {
                os.close();
                os = null;
            }
            if(is!=null) {
                is.close();
                is = null;
            }
            myActivity.finish();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("CloseOsIs", "IOE");
        }

    }

    public class ReadThread extends Thread{
        public BluetoothSocket socket;
        public ReadThread(BluetoothSocket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            while (socket.isConnected()) {
                byte[] buffer = new byte[128];
                int count = 0;
                if(socket!=null) {
                    try {
                        count = is.read(buffer);
                        tmp = new String(buffer, 0, count, "utf-8");
                        if(tmp.contains("從client端發送成功")) {
                            myActivity.thisObject = 0;
                            myActivity.anotherObject = 1;
                            Log.d("serve", "123");
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myActivity.setTxv4("client端發送成功");
                                }
                            });
                            send("model" + Settings.getWhich_model() + "");
                        }
                        else if(tmp.contains("從serve端發送成功")){
                            myActivity.thisObject = 1;
                            myActivity.anotherObject = 0;
                            Log.d("client", "123");
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myActivity.setTxv4("serve端發送成功");
                                }
                            });
                            send("model"+Settings.getWhich_model()+"");
                        }
                        else if(tmp.contains("model")){
                            myActivity.thisObject_model = Settings.getWhich_model();
                            myActivity.anotherObject_model = Integer.parseInt(tmp.substring(tmp.indexOf("model") + 5));
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myActivity.setTxv4("thisModel:" + Settings.getWhich_model() + " anotherModel:" + myActivity.anotherObject_model);
                                }
                            });
                            send("another_name:" + Settings.play_name + "");

                        }
                        else if(tmp.contains("another_name")){
                            Settings.play_another_name = tmp.substring(tmp.indexOf(":")+1);
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myActivity.setTxv4("another_name:" + Settings.play_another_name);
                                }
                            });
                            myActivity.createGame();
                            //myActivity.createGameWeb();

                            tmp = "";
                        }
                        else if(tmp.contains("createGame")){
                            myActivity.thisGame = Integer.parseInt(tmp.substring(tmp.indexOf("createGame") + 10));
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myActivity.setTxv4("createGame id : "+myActivity.thisGame);
                                }
                            });
                            tmp = "";
                        }
                        else if(tmp.contains("anothor_player_already")){
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myActivity.txv_status.setText(myActivity.txv_status.getText()+"anothor_player_already"+"\n");
                                    myActivity.another_already = 1;
                                    myActivity.cs.check();
                                }
                            });
                            tmp = "";
                        }
                        all+=tmp;
                        tmpBoolean = true;
                    } catch (IOException e) {
                        //e.printStackTrace();
                        cut.interrupt();
                        Log.d("ReadThread", "IOE");
                        break;
                    }
                }
            }
        }
    }
    Boolean tmpBoolean = false;
    public class CutThread extends Thread{
        @Override
        public void run() {
            while (true){
                if(tmpBoolean==true) break;
            }
           while(is!=null){
               if(all.contains("從client端發送成功")) all = all.substring(all.indexOf('功')+1);
               if(all.contains("從serve端發送成功")) all = all.substring(all.indexOf('功')+1);
               if(all.contains("END")) {
                   all = all.substring(all.indexOf('D')+1);
                   myActivity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           myActivity.showend("you lost");
                       }
                   });
               }

               try {
                   if (all.contains("x") && all.contains("z") && all.contains("r") && all.contains("a")) {
                      //Log.d("all", all);
                       float cutx, cutz, cutr;
                       cutx = Float.parseFloat(all.substring(all.indexOf('x') + 1, all.indexOf('z')));
                       cutz = Float.parseFloat(all.substring(all.indexOf('z') + 1, all.indexOf('r')));
                       cutr = Float.parseFloat(all.substring(all.indexOf('r') + 1, all.indexOf('a')));

                       myActivity.changeOtherCarPosition(cutx, cutz, cutr);

                   }
                   if (all.contains("a")) {
                       all = all.substring(all.indexOf('a') + 1);
                   }
               }
               catch(Exception e){
                    e.printStackTrace();
               }
           }
        }
    }

    public void send(final String tmp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (os!=null) {
                    try {
                        os.write(tmp.getBytes("utf-8"));
                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeOsIs();
                        Log.d("send","IOE");
                    }
                }
            }
        }).start();
    }

}
