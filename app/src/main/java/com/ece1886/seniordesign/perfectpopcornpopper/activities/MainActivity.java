package com.ece1886.seniordesign.perfectpopcornpopper.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.ece1886.seniordesign.perfectpopcornpopper.R;
import com.ece1886.seniordesign.perfectpopcornpopper.fragments.HomeFragment;
import com.ece1886.seniordesign.perfectpopcornpopper.fragments.SettingsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author Tristan Possessky
 * 9/20/21
 * MainActivity acts as a container for fragment views that will be used to display the actual UI.
 */
public class MainActivity extends AppCompatActivity {

    private ImageButton homeBtn, settingsBtn;
    private FloatingActionButton connectBT;
    private FrameLayout fragmentContainer;
    private Fragment homeFragment, settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set UI variables
        homeBtn = findViewById(R.id.homeBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        fragmentContainer = findViewById(R.id.fragmentContainer);

        //set fragments
        homeFragment = new HomeFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        settingsFragment = new SettingsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment).commit();

        //onClick Listeners for navigation
        homeBtn.setOnClickListener(v -> {
            if(!homeFragment.isResumed())
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, homeFragment).commit();
        });

        settingsBtn.setOnClickListener(v -> {
            if(!settingsFragment.isResumed())
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, settingsFragment).commit();
        });

    }
}