package com.example.tesknotam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;

public class BluetoothConnection {
    final String TAG = "BluetoothConnection";

    BluetoothAdapter mbluetoothAdapter;
    BluetoothLeScanner mbluetoothLeScanner;

    Common mcommon = new Common();
    public ArrayList<BluetoothDevice> mbluetoothDevices = new ArrayList<>();
    public ArrayList<BluetoothGatt> mbluetoothGattDevices = new ArrayList<>();

    public int[] mRSSI = new int[mcommon.NUMBER_OF_INODE_DEVICES];

    public BluetoothConnection(Context context) {
        checkIfBleSupported(context);
        initializeBluetoothManager(context);
        checkIfBluetoothEnabled(context);
    }

    public ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (mbluetoothDevices.size() >= mcommon.NUMBER_OF_INODE_DEVICES) return;

            Log.d(TAG, "onScanResult: " + result.toString());
            BluetoothDevice bluetoothDevice = result.getDevice();
            String deviceAddress = bluetoothDevice.getAddress().toUpperCase();
            int index = mcommon.LIST_OF_INODE_ADDRESSES.indexOf(deviceAddress);
            if (index != -1) {
                if (!mbluetoothDevices.contains(bluetoothDevice)) {
                    mbluetoothDevices.add(bluetoothDevice);
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(TAG, "onBatchScanResults: " + results.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed: " + errorCode);
        }
    };

    private void initializeBluetoothManager(Context context) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mbluetoothAdapter = bluetoothManager.getAdapter();
        mbluetoothLeScanner = mbluetoothAdapter.getBluetoothLeScanner();
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

    public void checkIfBluetoothEnabled(Context context) {
        if(mbluetoothAdapter == null){
            msg(context,"Urządzenie nie wspiera technologii Bluetooth.");
            mcommon.goToMainActivity(context);
        }
        if(!mbluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new  Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBluetoothIntent);

            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.registerReceiver(mBroadcastReceiver1, bluetoothIntent);
        }
    }

    private void checkIfBleSupported(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            msg(context,"Urządzenie nie wspiera technologii BluetoothLE.");
            mcommon.goToMainActivity(context);
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
        }
    }

    public void connectDevicesToGatt(Context context) {
        if (mbluetoothDevices != null) {
            for (BluetoothDevice bluetoothDevice: mbluetoothDevices) {
                mbluetoothGattDevices.add(bluetoothDevice.connectGatt(context, false, mGattCallback));
            }
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered: " + gatt.toString());
            Log.d(TAG, "onServicesDiscovered: " + status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            gatt.readRemoteRssi();
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d("OnRead remote RSSI:", "before if");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothDevice inode = gatt.getDevice();
                String deviceAddress = inode.getAddress().toUpperCase();
                Log.d("OnRead remote RSSI:", deviceAddress);
                Log.d("OnRead remote RSSI:", String.valueOf(rssi));
                int index = mcommon.LIST_OF_INODE_ADDRESSES.indexOf(deviceAddress);
                mRSSI[index] = rssi;
            }
        }
    };

    public void msg(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
