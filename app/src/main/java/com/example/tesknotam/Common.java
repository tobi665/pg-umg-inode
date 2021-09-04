package com.example.tesknotam;

import android.content.Context;
import android.content.Intent;

public class Common {

    public final int mINODES_NUM = 3;

    public void goToMainActivity(Context context) {
        Intent intent = new Intent (context, MainActivity.class);
        context.startActivity(intent);
    }
}
