package com.example.tesknotam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button mbuttonStart;
    Button mbuttonInfo;
    Button mbuttonExit;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbuttonStart = (Button) findViewById(R.id.button_start);
        mbuttonInfo = (Button) findViewById(R.id.button_info);
        mbuttonExit = (Button)findViewById(R.id.button_exit);

        mbuttonStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaintingActivity.class);
            startActivity(intent);
        });

        mbuttonInfo.setOnClickListener (v -> {
            Intent intent = new Intent (this, InfoActivity.class);
            startActivity(intent);
        });

        mbuttonExit.setOnClickListener (v -> {
            MainActivity.this.finish();
            System.exit(0);
        });
    }
}