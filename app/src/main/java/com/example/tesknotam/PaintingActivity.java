package com.example.tesknotam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.logging.LogRecord;

import static java.lang.Thread.sleep;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PaintingActivity extends AppCompatActivity {
    Button mbuttonBack;
    ImageView mimageViewPainting;
    BluetoothConnection mbluetoothConnection;
    int minterval = 5000;
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
        mbuttonBack = (Button)findViewById(R.id.button_back);
        mimageViewPainting = (ImageView) findViewById(R.id.imageView_painting);

//        mbluetoothConnection.checkBluetoothConnection(this);

        mhandler = new Handler();
        startRepeatingTask();

        mbuttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

//        mbluetoothConnectionActivity.enableBluetooth();

    };

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                setPainting();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mhandler.postDelayed(mStatusChecker, minterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mhandler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbluetoothConnection.unregisterReceiver(this);
        stopRepeatingTask();
//        mbluetoothAdapter.cancelDiscovery();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPainting() {
        mbluetoothConnection.checkBluetoothConnection(this);
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
//            Integer maxRSSI = Collections.max(Arrays.asList(mbluetoothConnection.mRSSI));

//            int actualImageId = mimageViewPainting.getId();
//            Integer newImage = mpaintings.get(index);
    }
}