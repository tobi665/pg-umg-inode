package com.example.tesknotam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class BluetoothConnectionActivity extends AppCompatActivity {
    private static final String TAG = "ConnectionActivity";
    private static final int INODES_NUM = 8;

    public ArrayList<BluetoothDevice> mbluetoothDevices = new ArrayList<>();
    public Map<BluetoothDevice, Integer> mbluetoothDevicesAndRSSI = new HashMap<BluetoothDevice, Integer>();
    TextView[] textViewINodeRSSI = new TextView[INODES_NUM];
    BluetoothConnection mbluetoothConnection;

    Button mbuttonCheckBluetoothConnection;
    Button mbuttonBack;
    Button buttonOnOf;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        mbluetoothConnection = new BluetoothConnection(this);

        buttonOnOf = (Button) findViewById(R.id.button_onOf);
        mbuttonCheckBluetoothConnection = (Button) findViewById(R.id.button_checkBluetoothConnection);
        mbuttonBack = (Button) findViewById(R.id.button_back);

        textViewINodeRSSI[0] = (TextView) findViewById(R.id.textView_iNode0RSSI);
        textViewINodeRSSI[1] = (TextView) findViewById(R.id.textView_iNode1RSSI);
        textViewINodeRSSI[2] = (TextView) findViewById(R.id.textView_iNode2RSSI);
        textViewINodeRSSI[3] = (TextView) findViewById(R.id.textView_iNode3RSSI);
        textViewINodeRSSI[4] = (TextView) findViewById(R.id.textView_iNode4RSSI);
        textViewINodeRSSI[5] = (TextView) findViewById(R.id.textView_iNode5RSSI);
        textViewINodeRSSI[6] = (TextView) findViewById(R.id.textView_iNode6RSSI);
        textViewINodeRSSI[7] = (TextView) findViewById(R.id.textView_iNode7RSSI);

        buttonOnOf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                if (mbluetoothConnection.mbluetoothAdapter != null ) {
                    if (!mbluetoothConnection.mbluetoothAdapter.isEnabled()) {
                        mbluetoothConnection.enableBluetooth(getApplicationContext());
                    } else {
                        mbluetoothConnection.disableBluetooth(getApplicationContext());
                    }
                }

            }
        });

        mbuttonCheckBluetoothConnection.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mbluetoothConnection.checkBluetoothConnection(getApplicationContext());
            }
        });

        mbuttonBack.setOnClickListener (v -> {
            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbluetoothConnection.unregisterReceiver(this);
//        mbluetoothAdapter.cancelDiscovery();
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
}