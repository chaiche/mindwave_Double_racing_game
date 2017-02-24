package com.neurosky.mindwavemobiledemo;

import android.app.Activity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothLivActivity extends Activity {

    private BluetoothAdapter myBluetooth = null;
    private Set pairedDevices;

    private ArrayAdapter<String> deviceArrayAdapter;
    private ArrayList<DeviceInfo> deviceInfo = new ArrayList();

    ListView liv;
    Button btn_showDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_liv);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        }
        else
        {
            if (myBluetooth.isEnabled()) {

            }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }

        liv = (ListView)findViewById(R.id.main_liv);
        btn_showDevice = (Button)findViewById(R.id.main_btn);
        btn_showDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });


        deviceArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        liv.setAdapter(deviceArrayAdapter);
    }

    private void pairedDevicesList()
    {

        deviceArrayAdapter.clear();
        deviceInfo.clear();

        Set<BluetoothDevice> devices = myBluetooth.getBondedDevices();

        if(devices.size()>0){
            for (BluetoothDevice device : devices) {
                DeviceInfo info = new DeviceInfo(device.getName(),device.getAddress());
                deviceInfo.add(info);
                deviceArrayAdapter.add("name:" + device.getName());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        deviceArrayAdapter.notifyDataSetChanged();

        liv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Make an intent to start next activity.
                Intent i = new Intent(BluetoothLivActivity.this, SendDataToArduinoActivity.class);
                //Change the activity.
                i.putExtra("address", deviceInfo.get(position).getDeviceAddress()); //this will be received at ledControl (class) Activity
                startActivity(i);
            }
        });

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
