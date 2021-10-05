package com.ece1886.seniordesign.perfectpopcornpopper.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

    //TODO: BLUETOOTH AVAILABILITY CHECK (is BT enabled on device)
    //TODO:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set UI variables
        homeBtn = findViewById(R.id.homeBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        //set fragments
        homeFragment = HomeFragment.newInstance();
        settingsFragment = SettingsFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment).commit();

        homeBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        //set instance of Notification class
        notificationHandler = NotificationHandler.getInstance(this);

        //check if user has given us permission
        if(!checkIfAlreadyGavePermission())
            requestLocationPermission();

        //TODO: ADD TO BLE BTN
        requestTurnOnBT();
    }

    /**
     * Global onClick listener for the activity handling app navigation
     * @param v button being clicked
     */
    @Override
    public void onClick(View v) {
        final int btnID = v.getId();
        if(btnID == homeBtn.getId()){
            notificationHandler.createNotification(this, "Your popcorn is ready!");
            if(!homeFragment.isResumed())
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right)
                        .replace(R.id.fragmentContainer, homeFragment).commit();
        }
        else if(btnID == settingsBtn.getId()){
            if(!settingsFragment.isResumed())
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, settingsFragment).commit();
        }
    }

    /**
     * If the user does not have Bluetooth enabled on their device, present a dialog to request
     * user turns on BLE.
     */
    public void requestTurnOnBT(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported!", Toast.LENGTH_LONG).show();
        }
        //Create dialog to ask user to turn on BT
        else if(!mBluetoothAdapter.isEnabled()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Please enable Bluetooth to use this application.");
            dialog.setCancelable(true);
            dialog.setPositiveButton(
                    "Yes",
                    (dialog12, id) -> {
                        mBluetoothAdapter.enable();
                        dialog12.cancel();
                    });
            dialog.setNegativeButton(
                    "No",
                    (dialog1, id) -> {
                        Toast.makeText(getApplicationContext(),
                                "Turn on Bluetooth to use the app!", Toast.LENGTH_LONG).show();
                        dialog1.cancel();
                    });

            AlertDialog alert11 = dialog.create();
            alert11.show();
        }
    }


    /**
     * Handles unfocusing text views and other objects that take screen focus
     * @param event tapping outside the bounds of the view
     * @return super.dispatchTouchEvent(event);
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if(v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * Requests user location permission for BLE
     */
    @TargetApi(26)
    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == 101)
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Location permission required for Bluetooth",
                        Toast.LENGTH_LONG).show();
         else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Checks if the user has granted location access required in API 26+
     * @return
     */
    @TargetApi(26)
    private boolean checkIfAlreadyGavePermission() {
        int result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

}