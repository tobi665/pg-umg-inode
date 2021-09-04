package com.example.tesknotam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.os.Handler;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import static java.lang.Thread.sleep;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PaintingActivity extends AppCompatActivity {
    private static final long SCAN_PERIOD = 10 * 1000;

    Button mbuttonBack;
    ImageView mimageViewPainting;
    BluetoothConnection mbluetoothConnection;
    Common mcommon = new Common();
    Handler mhandler;

    public ArrayList<Integer> mpaintings = new ArrayList<Integer>() {
        {
            add(R.drawable.p1);
            add(R.drawable.p2);
//            add(R.drawable.p3);
            add(R.drawable.p4);
            add(R.drawable.p5);
            add(R.drawable.p6);
            add(R.drawable.p7);
            add(R.drawable.p8);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);

        mbluetoothConnection = new BluetoothConnection(this);
        checkBluetoothPermissions();

        mbuttonBack = (Button)findViewById(R.id.button_back);
        mimageViewPainting = (ImageView) findViewById(R.id.imageView_painting);

        mhandler = new Handler();

        scanLeDevice(this, true);

        mbuttonBack.setOnClickListener(v -> {
            mcommon.goToMainActivity(this);
        });
    }

    private void scanLeDevice(Context context, final boolean enable) {
        if (enable) {
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                                mbluetoothConnection.mbluetoothLeScanner.stopScan(mbluetoothConnection.mLeScanCallback);
                                Log.d("DONE BLE", String.valueOf(mbluetoothConnection.mbluetoothDevices.size()));
                            if (mbluetoothConnection.mbluetoothDevices.size() >= mcommon.mINODES_NUM &&
                                    mbluetoothConnection.mbluetoothGattDevices.size() < mcommon.mINODES_NUM) {
                                    mbluetoothConnection.connectDevicesToGatt(context);
                                    Log.d("DONE GATT", String.valueOf(mbluetoothConnection.mbluetoothGattDevices.size()));
                            }
                            if (mbluetoothConnection.mbluetoothGattDevices.size() >= mcommon.mINODES_NUM) {
                                startRepeatingTask();
                            } else {
                                scanLeDevice(context, true);
                            }
                        }
            }, SCAN_PERIOD);
        }
        mbluetoothConnection.mbluetoothLeScanner.startScan(mbluetoothConnection.mLeScanCallback);
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }
    void stopRepeatingTask() {
        mhandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (mbluetoothConnection.mbluetoothGattDevices != null && mbluetoothConnection.mbluetoothDevices.size() >=3) {
                    for (BluetoothGatt inode : mbluetoothConnection.mbluetoothGattDevices) {
                        inode.readRemoteRssi();
                    }
                }
                setPainting();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mhandler.postDelayed(mStatusChecker, 1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
//        mbluetoothAdapter.cancelDiscovery();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPainting() {
        if (mbluetoothConnection.mRSSI == null) return;

        int maxRSSI = 0;
        int maxRSSIIndex = -1;
        for(int i = 0; i < mbluetoothConnection.mRSSI.length; i++) {
            int RSSI = mbluetoothConnection.mRSSI[i];
            if(RSSI != 0) {
                if (maxRSSI == 0) {
                    maxRSSI = RSSI;
                    maxRSSIIndex = i;
                }
                else {
                    if (RSSI > maxRSSI) {
                        maxRSSI = RSSI;
                        maxRSSIIndex = i;
                    }
                }
            }
        }
        if (maxRSSI != 0) {
            mimageViewPainting.setImageResource(mpaintings.get(maxRSSIIndex));
        }
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }
        tryEnableLocation();
    }

    public void tryEnableLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do poprawnego działania aplikacji potrzebny jest GPS, czy chcesz włączyć?")
                .setCancelable(false)
                .setPositiveButton("Tak", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Nie", (dialog, id) -> {
                    mcommon.goToMainActivity(this);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}