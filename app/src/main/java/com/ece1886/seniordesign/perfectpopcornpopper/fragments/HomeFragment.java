package com.ece1886.seniordesign.perfectpopcornpopper.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.ece1886.seniordesign.perfectpopcornpopper.R;
import com.ece1886.seniordesign.perfectpopcornpopper.logs.CaptainsLog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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
     */
    private Context mContext;
    private FloatingActionButton connectBT;
    private ImageButton disconnectBT;
    private ProgressBar progressBar;
    private TextView startConnectText, searchingText, connectLabel, macAddress;
    private ArrayList<String> deviceNames = new ArrayList<>();
//    private ArrayList<BluetoothDevice> devices;
    private BluetoothGatt mBluetoothGatt;
    private AlertDialog alertDialog;
    private BluetoothManager manager;

    private CaptainsLog captainsLog = CaptainsLog.getInstance();

    ArrayList<BluetoothDevice> bleDevices = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //add BLE device to list
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName() != null){
                    bleDevices.add(device);
                    deviceNames.add(device.getName());
                }
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                progressBar.setVisibility(View.GONE);
                showAlertDialog();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //start looking for BLE devices
                progressBar.setVisibility(View.VISIBLE);
                searchingText.setVisibility(View.VISIBLE);
                startConnectText.setVisibility(View.GONE);
                connectLabel.setVisibility(View.GONE);
                connectBT.setVisibility(View.GONE);
            }
        }
    };


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
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getContext();
        assert mContext != null;
        manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        requireActivity().registerReceiver(devicesFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));
        requireActivity().registerReceiver(devicesFoundReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        requireActivity().registerReceiver(devicesFoundReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        //NIGHT MODE CODE
        SharedPreferences preferences = requireActivity()
                .getSharedPreferences(getString(R.string.night_mode), Context.MODE_PRIVATE);
        boolean nightMode = preferences.getBoolean(getString(R.string.night_mode), false);

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if(nightMode && !(currentNightMode == Configuration.UI_MODE_NIGHT_YES)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            requireActivity().recreate();
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
        //FAB
        connectBT = view.findViewById(R.id.connectBT);
        //ImageButton
        disconnectBT = view.findViewById(R.id.disconnectBT);
        progressBar = view.findViewById(R.id.progressBar);
        //Text that starts on screen
        startConnectText = view.findViewById(R.id.connectBTText);
        //show when searching is in progress
        searchingText = view.findViewById(R.id.searchLabel);

        //stuff at top for when device is connected
        connectLabel = view.findViewById(R.id.connectLabel);
        macAddress = view.findViewById(R.id.macAddress);
        connectLabel.setVisibility(View.GONE);
        macAddress.setVisibility(View.GONE);
        disconnectBT.setVisibility(View.GONE);

        connectBT.setOnClickListener(this);
        disconnectBT.setOnClickListener(this);
        //starts BT service when button clicked
//        connectBT.setOnClickListener(v ->
//                getActivity().startForegroundService(new Intent(getActivity(), BluetoothService.class)));
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.connectBT){
            bluetoothAdapter.startDiscovery();
        }
        else if(v.getId() == R.id.disconnectBT){
            disconnectBLEDevice();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mContext.unregisterReceiver(devicesFoundReceiver);
        super.onPause();
    }
    //CLASSIC BT SCAN
//    private void discoverBT(){
//        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        IntentFilter filter = new IntentFilter();
//
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//
//        requireActivity().registerReceiver(mReceiver, filter);
//        adapter.startDiscovery();
//    }
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                progressBar.setVisibility(View.VISIBLE);
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                progressBar.setVisibility(View.GONE);
//                showAlertDialog();
//            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                //bluetooth device found
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                if(device.getName() != null && !deviceNames.contains(device.getName())){
//                    Log.wtf("HomeFragment", device.getName());
//                    deviceNames.add(device.getName());
//                    devices.add(device);
//                }
//            }
//        }
//    };

    private void disconnectBLEDevice(){
       AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
               .setTitle("Disconnect Bluetooth")
               .setMessage("Are you sure you would like to disconnect?")
               .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                   mBluetoothGatt.disconnect();
                    dialog.cancel();
                    connectLabel.setVisibility(View.GONE);
                    macAddress.setVisibility(View.GONE);
                    disconnectBT.setVisibility(View.GONE);
                    connectBT.setVisibility(View.VISIBLE);
                    startConnectText.setVisibility(View.VISIBLE);
               })
               .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                builder.show();
    }


    /**
     * Main Alert Dialog for Connecting to a BLE device
     */
    private void showAlertDialog(){

        for(BluetoothDevice device : bleDevices)
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
        alertDialog = builder.create();
        //layout formatting
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        alertDialog.show();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.height = 700;
        lp.width = 500;
        alertDialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.wtf("ItemClick", String.valueOf(position));
        Log.wtf("ID", String.valueOf(id));

        Log.wtf("Name of Item", bleDevices.get(position).getName());
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bleDevices.get(position).getAddress());
        device.connectGatt(mContext,false, bluetoothGattCallback);

    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.wtf("GATT CALLBACK", "CONNECTED");
                mBluetoothGatt = gatt;
                deviceConnectedUIUpdates();

                List<BluetoothDevice> connectedDevices
                        = manager.getConnectedDevices(BluetoothProfile.GATT);

                for(BluetoothDevice device : connectedDevices)
                    captainsLog.log("Connected Devices",
                            device.getName()+" "+device.getAddress(),CaptainsLog.LogLevel.WTF);


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.wtf("GATT CALLBACK", "DISCONNECTED");
                requireActivity().runOnUiThread(() ->{
                    Toast.makeText(mContext, "Device Disconnected", Toast.LENGTH_LONG).show();
                });
            }
        }
    };

    private void deviceConnectedUIUpdates(){
        requireActivity().runOnUiThread(() -> {
            if(alertDialog.isShowing())
                alertDialog.cancel();
            connectLabel.setVisibility(View.VISIBLE);
            macAddress.setVisibility(View.VISIBLE);
            searchingText.setVisibility(View.GONE);
            macAddress.setText(mBluetoothGatt.getDevice().getAddress());
            disconnectBT.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "Device Connected!", Toast.LENGTH_LONG).show();
        });
    }
}