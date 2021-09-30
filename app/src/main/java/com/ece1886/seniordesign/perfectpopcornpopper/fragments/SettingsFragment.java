package com.ece1886.seniordesign.perfectpopcornpopper.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ece1886.seniordesign.perfectpopcornpopper.R;
import com.ece1886.seniordesign.perfectpopcornpopper.activities.MainActivity;

import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    MediaPlayer mediaPlayer;
    Button playMedia;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //NIGHT MODE HANDLING
        preferences = getActivity()
                .getSharedPreferences(getString(R.string.night_mode), Context.MODE_PRIVATE);
        boolean nightMode = preferences.getBoolean(getString(R.string.night_mode), false);

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        if(nightMode && !(currentNightMode == Configuration.UI_MODE_NIGHT_YES)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getActivity().recreate();
        }

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView pp = view.findViewById(R.id.privacypolicy);
        TextView terms = view.findViewById(R.id.terms);
        SwitchCompat nightModeSwitch = view.findViewById(R.id.night_mode);
        Button locServ = view.findViewById(R.id.locServ);


        /**media**/
        mediaPlayer = MediaPlayer.create (getContext(), R.raw.x);
        playMedia = view.findViewById(R.id.ohBoy);
        playMedia.setOnClickListener(v -> mediaPlayer.start());


        preferences = getActivity()
                .getSharedPreferences(getString(R.string.night_mode), Context.MODE_PRIVATE);
        boolean nightMode = preferences.getBoolean(getString(R.string.night_mode), false);

        if(nightMode)
            nightModeSwitch.setChecked(true);

        editor = preferences.edit();

        pp.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://perfect-popcorn-popp.flycricket.io/privacy.html");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        terms.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://perfect-popcorn-popp.flycricket.io/terms.html");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor1 = preferences.edit();
            editor1.putBoolean(getString(R.string.night_mode), isChecked)
                    .apply();

            if(isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            getActivity().recreate();
        });


        locServ.setOnClickListener(v -> {
            String requiredPermission = Manifest.permission.ACCESS_FINE_LOCATION;
            int checkVal = getContext().checkCallingOrSelfPermission(requiredPermission);

            if(checkVal == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getActivity(),"Permission already granted!", Toast.LENGTH_LONG)
                        .show();
            else
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
        });




    }
}