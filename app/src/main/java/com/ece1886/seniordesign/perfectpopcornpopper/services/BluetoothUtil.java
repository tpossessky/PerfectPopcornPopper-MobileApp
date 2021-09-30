package com.ece1886.seniordesign.perfectpopcornpopper.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

public class BluetoothUtil {

    public static final int STATUS_BLE_ENABLED = 0;
    public static final int STATUS_BLUETOOTH_NOT_AVAILABLE = 1;
    public static final int STATUS_BLE_NOT_AVAILABLE = 2;
    public static final int STATUS_BLUETOOTH_DISABLED = 3;

    public static int getBleStatus(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return STATUS_BLE_NOT_AVAILABLE;
        }

        final BluetoothAdapter adapter = getBluetoothAdapter(context);
        // Checks if Bluetooth is supported on the device.
        if (adapter == null) {
            return STATUS_BLUETOOTH_NOT_AVAILABLE;
        }

        if (!adapter.isEnabled()) {
            return STATUS_BLUETOOTH_DISABLED;
        }

        return STATUS_BLE_ENABLED;
    }

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null) {
            return null;
        } else {
            return bluetoothManager.getAdapter();
        }
    }
}
