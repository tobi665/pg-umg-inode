package com.example.tesknotam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mbuttonStart;
    Button mbuttonInfo;
    Button mbuttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbuttonStart = (Button) findViewById(R.id.button_start);
        mbuttonInfo = (Button) findViewById(R.id.button_info);
        mbuttonInfo = (Button) findViewById(R.id.button_checkBluetoothConnection);
        mbuttonExit = (Button)findViewById(R.id.button_exit);

        mbuttonStart.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PaintingActivity.class);
            startActivity(intent);
        });

        mbuttonInfo.setOnClickListener (v -> {
            Intent intent = new Intent (getApplicationContext(), InfoActivity.class);
            startActivity(intent);
        });

        mbuttonInfo.setOnClickListener (v -> {
            Intent intent = new Intent (getApplicationContext(), BluetoothConnectionActivity.class);
            startActivity(intent);
        });

        mbuttonExit.setOnClickListener (v -> {
            MainActivity.this.finish();
            System.exit(0);
        });
    }
}