package com.ece1886.seniordesign.perfectpopcornpopper.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.ece1886.seniordesign.perfectpopcornpopper.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    /**
     * TODO: After Clicking ConnectBT BTN, AlertDialog only showing OUR device
     *  (UUID?, Advertising Packet?). From there show connection on screen with option to disconnect
     *  Figure out UI for Popping/Getting Data
     *
     */
    private FloatingActionButton connectBT;
    private ImageButton disconnectBT;
    private ProgressBar progressBar;
    private ArrayList<String> deviceNames;
    private ArrayList<BluetoothDevice> devices;

    public HomeFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        devices = new ArrayList<>();
        deviceNames = new ArrayList<>();
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

        //find UI elements in view
        connectBT = view.findViewById(R.id.connectBT);
        disconnectBT = view.findViewById(R.id.disconnectBT);
        progressBar = view.findViewById(R.id.progressBar);

        connectBT.setOnClickListener(this);
        disconnectBT.setOnClickListener(this);
        //starts BT service when button clicked
//        connectBT.setOnClickListener(v ->
//                getActivity().startForegroundService(new Intent(getActivity(), BluetoothService.class)));
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.connectBT){
            discoverBT();
        }
        else if(v.getId() == R.id.disconnectBT){
            //TODO: Terminate BT Connection
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //TODO: To be moved to BT classes when ready
    private void discoverBT(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        requireActivity().registerReceiver(mReceiver, filter);
        adapter.startDiscovery();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressBar.setVisibility(View.GONE);
                showAlertDialog();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getName() != null && !deviceNames.contains(device.getName())){
                    Log.wtf("HomeFragment", device.getName());
                    deviceNames.add(device.getName());
                    devices.add(device);
                }
            }
        }
    };


    private void showAlertDialog(){

        for(BluetoothDevice device : devices)
            Log.wtf("Log ",device.getName());

        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.btlist_alert_dialog, null);
        ListView listView = dialog.findViewById(R.id.BTDeviceList);
        listView.setOnItemClickListener(this);
        //set the listview with data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, deviceNames);

        //populate listview
        listView.setAdapter(adapter);
        //start building alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialog);
        AlertDialog alertDialog = builder.create();
        //layout formatting
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        alertDialog.show();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.height = 700;
        lp.width = 500;
        alertDialog.getWindow().setAttributes(lp);

        //listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.wtf("ItemClick", String.valueOf(position));
        Log.wtf("ID", String.valueOf(id));

        Log.wtf("Name of Item", devices.get(position).getName());
    }


}