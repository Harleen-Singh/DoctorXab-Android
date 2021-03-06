package com.example.doc_app_android.actvities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.doc_app_android.R;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences sp = getSharedPreferences("tokenFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (sp.contains("id")) {
                    Intent intent = new Intent(splashScreen.this, Home.class);
                    editor.putBoolean("hasLoggedIn", true);
                    editor.apply();
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Intent i;
                    i = new Intent(splashScreen.this, MainActivity.class);
                    startActivity(i);
                }
                finishAffinity();
            }
        }, 2000);
    }
}