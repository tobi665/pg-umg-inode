package com.example.tesknotam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BluetoothConnection {
    int mINODES_NUM = 8;
    BluetoothAdapter mbluetoothAdapter;
//    Context mcontext;
    public ArrayList<BluetoothDevice> mbluetoothDevices = new ArrayList<>();

    public ArrayList<String> mINodesAddresses = new ArrayList<String>() {{
        add("D0:F0:18:44:0C:4B"); // iNode-440D74, p0
        add("D0:F0:18:44:0D:73"); // iNode-440C4B, p1
        add("D0:F0:18:44:0D:74"); // iNode-440D73, p2
    }};

    public int[] mRSSI = new int[mINODES_NUM];

    public BluetoothConnection(Context context) {
        mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetooth(context);
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    public final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mbluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mbluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
//                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
//                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    public final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
//                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
//                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
//                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by buttonCheckBluetoothConnection_OnClick() method.
     */
    public BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

                String deviceAddress = device.getAddress().toUpperCase();
                int index = mINodesAddresses.indexOf(deviceAddress);
                if (index != -1) {
                    mbluetoothDevices.add(device);
                    Integer deviceRSSI = Integer.valueOf(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                    mRSSI[index] = deviceRSSI;
                }
                //mbluetoothDevicesAndRSSI.put(device, deviceRSSI);
                //mdeviceListAdapter = new DeviceListAdapter(context, R.layout.activity_device_list_adapter, mbluetoothDevices, mbluetoothDevicesAndRSSI);
                //mlistViewNewDevices.setAdapter(mdeviceListAdapter);
            }
        }
    };

    public void enableBluetooth(Context context) {
        if(mbluetoothAdapter == null){
            msg(context,"Urządzenie nie wspiera technologii Bluetooth.");
            Intent intent = new Intent (context, MainActivity.class);
            context.startActivity(intent);
        }
        if(!mbluetoothAdapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBluetoothIntent);

            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.registerReceiver(mBroadcastReceiver1, bluetoothIntent);
        }
    }

    public void disableBluetooth(Context context) {
        if(mbluetoothAdapter == null){
            msg(context, "Urządzenie nie wspiera technologii Bluetooth.");
            Intent intent = new Intent (context, MainActivity.class);
            context.startActivity(intent);
        }
        if(mbluetoothAdapter.isEnabled()){
            mbluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkBluetoothConnection(Context context) {
        if(mbluetoothAdapter.isDiscovering()) {
            mbluetoothAdapter.cancelDiscovery();
        }
        //check BT permissions in manifest
//        checkBluetoothPermissions();

        mbluetoothAdapter.startDiscovery();
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mBroadcastReceiver1);
        context.unregisterReceiver(mBroadcastReceiver2);
    }

    public void msg(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
