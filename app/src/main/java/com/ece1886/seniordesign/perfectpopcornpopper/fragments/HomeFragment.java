package com.ece1886.seniordesign.perfectpopcornpopper.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ece1886.seniordesign.perfectpopcornpopper.R;
import com.ece1886.seniordesign.perfectpopcornpopper.services.BluetoothService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FloatingActionButton connectBT;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //NIGHT MODE CODE
        SharedPreferences preferences = getActivity()
                .getSharedPreferences(getString(R.string.night_mode), Context.MODE_PRIVATE);
        boolean nightMode = preferences.getBoolean(getString(R.string.night_mode), false);

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if(nightMode && !(currentNightMode == Configuration.UI_MODE_NIGHT_YES)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getActivity().recreate();
        }
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // BLUETOOTH SERVICE STUFF
        // FAB onClick listener
        //startService(new Intent(this, BluetoothService.class));
        //stopService(new Intent(this, BluetoothService.class));

        //find button in view
        connectBT = view.findViewById(R.id.connectBT);

        //starts BT service when button clicked
//        connectBT.setOnClickListener(v ->
//                getActivity().startForegroundService(new Intent(getActivity(), BluetoothService.class)));



    }
}