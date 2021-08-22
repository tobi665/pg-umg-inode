package com.example.tesknotam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BluetoothConnectionActivity extends AppCompatActivity {
    private static final String TAG = "ConnectionActivity";

    BluetoothAdapter mbluetoothAdapter;
    public ArrayList<BluetoothDevice> mbluetoothDevices = new ArrayList<>();
    public Map<BluetoothDevice, Integer> mbluetoothDevicesAndRSSI = new HashMap<BluetoothDevice, Integer>();
    public DeviceListAdapter mdeviceListAdapter;
    ListView mlistViewNewDevices;

    Button mbuttonCheckBluetoothConnection;
    Button mbuttonBack;


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mbluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mbluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by buttonCheckBluetoothConnection_OnClick() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mbluetoothDevices.add(device);
                int deviceRSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                mbluetoothDevicesAndRSSI.put(device, deviceRSSI);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mdeviceListAdapter = new DeviceListAdapter(context, R.layout.activity_device_list_adapter, mbluetoothDevices, mbluetoothDevicesAndRSSI);
                mlistViewNewDevices.setAdapter(mdeviceListAdapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
//        mbluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        Button buttonOnOf = (Button) findViewById(R.id.button_onOf);
        mbuttonCheckBluetoothConnection = (Button) findViewById(R.id.button_checkBluetoothConnection);
        mlistViewNewDevices = (ListView) findViewById(R.id.listView_newDevices);
        mbluetoothDevices = new ArrayList<>();
        mbluetoothDevicesAndRSSI = new HashMap<BluetoothDevice, Integer>();
        mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetooth();

        buttonOnOf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                if (mbluetoothAdapter != null ) {
                    if (!mbluetoothAdapter.isEnabled()) {
                        enableBluetooth();
                    } else {
                        disableBluetooth();
                    }
                }

            }
        });

        mbuttonBack.setOnClickListener (v -> {
            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

    }

    public void enableBluetooth() {
        if(mbluetoothAdapter == null){
            msg("Urządzenie nie wspiera technologii Bluetooth.");
            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        if(!mbluetoothAdapter.isEnabled()){
            msg("Do prawidłowej pracy aplikacji potrzebne jest włączenie Bluetooth.");
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetoothIntent);

            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, bluetoothIntent);
        }
    }

    public void disableBluetooth() {
        if(mbluetoothAdapter == null){
            msg("Urządzenie nie wspiera technologii Bluetooth.");
            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        if(mbluetoothAdapter.isEnabled()){
            msg("Wyłączanie bluetooth.");
            mbluetoothAdapter.disable();

            IntentFilter bluetoothIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, bluetoothIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void buttonCheckBluetoothConnection_OnClick(View view) {
        if(mbluetoothAdapter.isDiscovering()) {
            mbluetoothAdapter.cancelDiscovery();
        }
            //check BT permissions in manifest
            checkBluetoothPermissions();

            mbluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBluetoothPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }
}