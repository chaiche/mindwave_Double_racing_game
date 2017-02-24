package com.neurosky.mindwavemobiledemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ConnectWifiActivity extends Activity implements WifiP2pManager.ConnectionInfoListener{

    private WifiP2pManager manager;

    private boolean isWifiP2pEnabled = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;

    private BroadcastReceiver receiver = null;

    ProgressDialog progressDialog = null;

    private List peers = new ArrayList();

    WifiP2pManager.PeerListListener myPeerListListener = null;

    ListView liv;

    ArrayAdapter livAdapter;

    private boolean isConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        channel = manager.initialize(this, getMainLooper(), null);

        livAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,peers);
        liv = (ListView) findViewById(R.id.listView);
        liv.setAdapter(livAdapter);
        liv.setOnItemClickListener(LivOnitemClick);

    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void resetData() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(this, "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
        findPeer();

    }
    public void findPeer(){

        myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                peers.clear();   // peers是一个全局ArrayList对象，用于保存发现的peers的信息
                peers.addAll(peerList.getDeviceList());
                // 如果你有一个控件用来显示这些peers的信息，就可以再这里更新了
                livAdapter.notifyDataSetChanged();
                Log.d("peer", peers.size() + "");
            }
        };
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("discover", "success");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("discover", "failure");
            }
        });
    }
    ListView.OnItemClickListener LivOnitemClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            connect(position);
        }
    };
    public void connect(int pos) {
        final WifiP2pDevice device = (WifiP2pDevice) peers.get(pos); //从peers列表中获取发现来的第一个设备
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // 连接成功
                isConnect = true;
                Toast.makeText(getApplicationContext(), "與設備" + device.deviceName + "連接成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int arg0) {
                // 连接失败
                Toast.makeText(getApplicationContext(), "與設備" + device.deviceName + "連接失败", Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d("mainactivity","connect");

        InetAddress address = null;
        boolean isGroupOwner = false;
        if (info.groupFormed && info.isGroupOwner) {
            address = info.groupOwnerAddress;
            isGroupOwner = false;
//            Intent it = new Intent(this,ServeActivity.class);
//            startActivity(it);
        } else if (info.groupFormed) {
            address = info.groupOwnerAddress;
            isGroupOwner = true;
//            Intent it = new Intent(this,ClientActivity.class);
//            it.putExtra("address",address.getHostAddress());
//            startActivity(it);
        }
        Intent it = new Intent();
        it.putExtra("address",address.getHostAddress());
        it.putExtra("isGroupOwner",isGroupOwner);
        setResult(RESULT_OK,it);
        Log.d("this Address", address.getHostAddress() + "");
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("dis connect", "sucess1111");
        if(resultCode == RESULT_OK){
            if(isConnect) manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("dis connect","sucess");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("dis connect","false");
                }
            });
            finish();
        }
    }


}
