package com.ece1886.seniordesign.perfectpopcornpopper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.ece1886.seniordesign.perfectpopcornpopper.R;

public class MainActivity extends AppCompatActivity {

    private Button connectBT, discconnectBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //add button and text elements


    }
}