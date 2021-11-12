package com.ece1886.seniordesign.perfectpopcornpopper.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
import com.ece1886.seniordesign.perfectpopcornpopper.services.BluetoothLeService;
import com.ece1886.seniordesign.perfectpopcornpopper.services.NotificationHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context mContext;
    private FloatingActionButton connectBT;
    private ImageButton disconnectBT;
    private ProgressBar progressBar;
    private TextView startConnectText, searchingText, connectLabel, macAddress, bleDataTester;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private AlertDialog alertDialog;
    private BluetoothManager manager;
    private BluetoothAdapter mBLEAdapter;
    private Handler mHandler;
    private final String TAG = "HomeFragment";
    private static final long SCAN_PERIOD = 2500;
    private boolean scanning = false;
    private BluetoothLeService bleService;
    private CaptainsLog captainsLog = CaptainsLog.getInstance();
    private int mPops = 0;
    private String deviceAddress;
    private String deviceName;
    private NotificationHandler mNotificationHandler;

    //Expected Data from Bluetooth Characteristic
    private static final String INCREMENT_POPS = "41414141";
    private static final String THIRTY_SECONDS = "42424242";
    private static final String TEN_SECONDS = "43434343";

    ArrayList<BluetoothDevice> bleDevices = new ArrayList<>();

    private void scanBleDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                scanning = false;
                mBLEAdapter.stopLeScan(mBleScanCallback);
                progressBar.setVisibility(View.GONE);
                showAlertDialog();
            }, SCAN_PERIOD);

            scanning = true;
            mBLEAdapter.startLeScan(mBleScanCallback);
                progressBar.setVisibility(View.VISIBLE);
                searchingText.setVisibility(View.VISIBLE);
                startConnectText.setVisibility(View.GONE);
                connectLabel.setVisibility(View.GONE);
                connectBT.setVisibility(View.GONE);

        } else {
            scanning = false;
            mBLEAdapter.stopLeScan(mBleScanCallback);
            progressBar.setVisibility(View.GONE);
        }
    }


    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    /**
     * Only handles SharedPreferences for NightMode
     * @param inflater don't care
     * @param container don't care
     * @param savedInstanceState don't care
     * @return don't care
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getContext();
        assert mContext != null;

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
        //set onClickListeners for UI buttons
        connectBT.setOnClickListener(this);
        disconnectBT.setOnClickListener(this);

        mNotificationHandler = NotificationHandler.getInstance(getActivity());



        //ble data
        bleDataTester = view.findViewById(R.id.bleData);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBLEAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler(Looper.getMainLooper());
    }


    //works
    private BluetoothAdapter.LeScanCallback mBleScanCallback = (device, rssi, scanRecord) -> {
        if(device.getName() != null && !bleDevices.contains(device)){
            bleDevices.add(device);
            deviceNames.add(device.getName());
            captainsLog.log("SCAN CALLBACK", device.getName() + " " + device.getAddress(), CaptainsLog.LogLevel.WTF);
        }
    };

    //works
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.connectBT){
            scanBleDevice(true);
        }
        else if(v.getId() == R.id.disconnectBT){
            disconnectBLEDevice();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bleService.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanBleDevice(false);
    }


    private void disconnectBLEDevice(){
       AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
               .setTitle("Disconnect Bluetooth")
               .setMessage("Are you sure you would like to disconnect?")
               .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.cancel();
                    bleService.disconnect();
               })
               .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                builder.show();
    }


    /**
     * Main Alert Dialog for Connecting to a BLE device
     */
    private void showAlertDialog(){

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
        deviceName = bleDevices.get(position).getName();
        deviceAddress = bleDevices.get(position).getAddress();
        bleService.connect(deviceAddress);
        deviceConnectedUIUpdates();
    }


    private void deviceConnectedUIUpdates(){
        requireActivity().runOnUiThread(() -> {
                if (alertDialog.isShowing())
                    alertDialog.cancel();
                connectLabel.setVisibility(View.VISIBLE);
                macAddress.setVisibility(View.VISIBLE);
                macAddress.setText(deviceAddress);
                searchingText.setVisibility(View.GONE);
                disconnectBT.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "Device Connected!", Toast.LENGTH_LONG).show();
                Set<BluetoothDevice> pairedDevices = mBLEAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        Log.wtf("Connected Devices", deviceName + " " + deviceHardwareAddress);
                    }
                }
        });
    }

    private void deviceDisconnectedUIUpdates(){
        requireActivity().runOnUiThread(() -> {
            connectLabel.setVisibility(View.GONE);
            macAddress.setVisibility(View.GONE);
            disconnectBT.setVisibility(View.GONE);
            connectBT.setVisibility(View.VISIBLE);
            startConnectText.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                deviceConnectedUIUpdates();
                Log.wtf("BROADCAST RECEIVER", "CONNECTED");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                deviceDisconnectedUIUpdates();
                Log.wtf("BROADCAST RECEIVER", "DISCONNECTED");
            }

            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.wtf(TAG, "Services were discovered");
                bleService.getSupportedGattServices();
            }
            else if (BluetoothLeService.ACTION_DATA_READ_COMPLETED.equals(action)) {
                Log.wtf(TAG, "Data Read Completed");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String bleData = intent.getStringExtra(BluetoothLeService.ACTION_BATTERY_LEVEL);

                bleData = bleData.replaceAll("\\s", "");
                Log.wtf(TAG, "Received data: " + bleData);

                switch (bleData){
                    case INCREMENT_POPS:
                        mPops++;
                        bleDataTester.setText(mPops);
                        break;
                    case THIRTY_SECONDS:
                        mNotificationHandler.createNotification(requireActivity(), "30 seconds remaining on your popcorn!");
                        break;
                    case TEN_SECONDS:
                        mNotificationHandler.createNotification(requireActivity(), "10 seconds remaining on your popcorn!");
                        break;
                    default:
                        Log.wtf("Data Received not correct", bleData);
                }
            }
        }
    };



    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.wtf("SERVICE_CONNECTION", "SERVICE CONNECTED");
            bleService.initialize();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bleService = null;
            Log.wtf("SERVICE_CONNECTION", "SERVICE DISCONNECTED");

        }
    };


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ_COMPLETED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}