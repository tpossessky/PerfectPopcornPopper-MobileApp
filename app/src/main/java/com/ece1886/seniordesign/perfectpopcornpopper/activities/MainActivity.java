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
import com.ece1886.seniordesign.perfectpopcornpopper.logs.CaptainsLog;
import com.ece1886.seniordesign.perfectpopcornpopper.services.NotificationHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author Tristan Possessky
 * 9/20/21
 * MainActivity acts as a container for fragment views that will be used to display the actual UI.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton homeBtn, settingsBtn;
    private FloatingActionButton connectBT;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment homeFragment, settingsFragment;
    private NotificationHandler notificationHandler;
    private CaptainsLog logger = CaptainsLog.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set UI variables
        homeBtn = findViewById(R.id.homeBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        //set fragments
        homeFragment = new HomeFragment();

        settingsFragment = new SettingsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment).commit();

        homeBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        //set instance of Notification class
        notificationHandler = NotificationHandler.getInstance(this);

    }

    @Override
    public void onClick(View v) {
        logger.log("View ID", String.valueOf(v.getId()));
        logger.log("Home ID", String.valueOf(v.getId()));
        logger.log("Setting ID", String.valueOf(settingsBtn.getId()));

        final int btnID = v.getId();
        if(btnID == homeBtn.getId()){
            notificationHandler.createNotification(this, "Your popcorn is ready!");
            if(!homeFragment.isResumed())
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragmentContainer, homeFragment).commit();
        }
        else if(btnID == settingsBtn.getId()){
            if(!settingsFragment.isResumed())
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, settingsFragment).commit();
        }
    }
}