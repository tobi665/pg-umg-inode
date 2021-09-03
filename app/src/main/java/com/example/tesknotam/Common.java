package com.example.tesknotam;

import android.content.Context;
import android.content.Intent;

public class Common {

    public void goToMainActivity(Context context) {
        Intent intent = new Intent (context, MainActivity.class);
        context.startActivity(intent);
    }
}
