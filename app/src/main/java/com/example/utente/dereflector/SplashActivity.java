package com.example.utente.dereflector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by feder on 14/12/2018.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            wait(2000);
        }
        catch (Exception e){}
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
