package com.ece1886.seniordesign.perfectpopcornpopper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ece1886.seniordesign.perfectpopcornpopper.R;

/**
 * @author Tristan Possessky
 * 9/20/21
 * Simple splash screen activity linked to res/layout/activity_splash.xml
 * Uses Handler to wait 2 seconds before transitioning to MainActivity
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //timer set at 2 seconds, starts main activity after time expires with custom transition
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2000);
    }
}