package com.ece1886.seniordesign.perfectpopcornpopper.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ece1886.seniordesign.perfectpopcornpopper.services.GattAttributes.POPCORN_LEVEL_READ;

/*
 * This is a service to handle the BLE interactions.
 *
 * */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private String bleDeviceAddress;
    private BluetoothGatt bleGatt;
    private BluetoothGattCharacteristic notifyCharacteristics;

    public int connectionState = STATE_DISCONNECTED;
    List<BluetoothGattCharacteristic> chars = new ArrayList<>();

    private int batteryLevel;
    private boolean sweepComplete = false;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_READ_COMPLETED = "ACTION_DATA_READ_COMPLETED";
    public final static String ACTION_BATTERY_LEVEL = "ACTION_BATTERY_LEVEL";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";

    public final static UUID UUID_BATTERY_LEVEL = UUID.fromString(GattAttributes.BATTERY_LEVEL);
    public final static UUID UUID_BATTERY_STATUS = UUID.fromString(GattAttributes.BATTERY_STATUS);


    public final static UUID UUID_POPCORN_DATA = UUID.fromString(GattAttributes.POPCORN_CHARACTERISTIC);
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;

                broadcastUpdate(ACTION_GATT_CONNECTED);
                Log.wtf(TAG, "Attempting to start service discovery.");
                gatt.discoverServices();
                Log.wtf(TAG, "Connected to GATT server.");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                Log.wtf(TAG, "Disconnected from GATT server.");
                broadcastUpdate(ACTION_GATT_DISCONNECTED);

            } else {
                Log.wtf(TAG, "Other State");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);


                List<BluetoothGattService> services = gatt.getServices();

                // Loops through available GATT Services.
                for (BluetoothGattService gattService : services) {
                    List<BluetoothGattCharacteristic> gattCharacteristicsList = gattService.getCharacteristics();

//                     Loops through available Characteristics.
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristicsList) {
                        if (isDataCharacteristic(gattCharacteristic) != 0) {
                            chars.add(gattCharacteristic);
                        }
                    }
                }

                requestCharacteristics(gatt);

            } else {
                Log.wtf(TAG, "onServicesDiscovered received: " + status);
            }
        }

        public void requestCharacteristics(BluetoothGatt gatt) {
            gatt.readCharacteristic(chars.get(chars.size()-1));
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            Log.wtf("onPhyRead called", "on phy read");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.wtf("onCharacteristicRead called", "oncharacteristicRead called");

                switch (isDataCharacteristic(characteristic)) {
                    case POPCORN_LEVEL_READ:
                        if (sweepComplete) {
                            chars.remove(chars.get(chars.size() - 1));
                            sweepComplete = false;
                        }
                        break;
                    default:
                        chars.remove(chars.get(chars.size() - 1));
                        break;
                }

                if (chars.size() > 0) {
                    requestCharacteristics(gatt);

                } else {
                    Log.wtf(TAG, "Gatt server data read completed.");
                    broadcastUpdate(ACTION_DATA_READ_COMPLETED);
                }
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.wtf("ONCHARACTERISTICCHANGED", "SOMETHING HAPPENED");
            readCharacteristic(characteristic);
//            final byte[] data = characteristic.getValue();
//
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//
//                for (byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                Log.wtf("BROADCAST UPDATE BLEService", stringBuilder.toString());
//                setCharacteristicNotification(characteristic, true);
//            }
        }
    };

    public int isDataCharacteristic(BluetoothGattCharacteristic characteristic) {
//        if (UUID_BATTERY_LEVEL.equals(characteristic.getUuid())) {
//            return BATTERY_LEVEL_READ;
//
//        } else if (UUID_X_ACCELERATION.equals(characteristic.getUuid())) {
//            return X_ACCELERATION_READ;
//
//        } else if (UUID_Y_ACCELERATION.equals(characteristic.getUuid())) {
//            return Y_ACCELERATION_READ;
//
//
//        } else {
            return 0;
//        }
    }



    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {

        final Intent intent = new Intent(action);
        int charWhat = isDataCharacteristic(characteristic);
        int count;
        Log.wtf("Broadcast update called", "broadcast update called");

        switch (charWhat) {
            case POPCORN_LEVEL_READ:
                batteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0);
                Log.wtf(TAG, "Received popcorn: " + batteryLevel);
                intent.putExtra(ACTION_BATTERY_LEVEL, String.valueOf(batteryLevel));
                break;
            default:
                // For all other profiles, writes the data formatted in HEX.
                final byte[] data = characteristic.getValue();

                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);

                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));

                    Log.wtf("BROADCAST UPDATE BLEService", stringBuilder.toString());
                    Log.wtf("Length of Data", String.valueOf(data.length));
                    String s = new String(data, StandardCharsets.UTF_8);
                    Log.wtf("String Value of Data", s);

                    intent.putExtra(ACTION_BATTERY_LEVEL, stringBuilder.toString());
                    setCharacteristicNotification(characteristic, true);
                }
                break;
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.wtf("onBind Service", "service bound");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder binder = new LocalBinder();


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (bleManager == null) {
            bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (bleManager == null) {
                Log.wtf(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        bleAdapter = bleManager.getAdapter();

        if (bleAdapter == null) {
            Log.wtf(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }


    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (bleAdapter == null || address == null) {
            Log.wtf(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = bleAdapter.getRemoteDevice(address);

        if (device == null) {
            Log.wtf(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bleGatt = device.connectGatt(this, false, gattCallback);
        Log.wtf(TAG, "Trying to create a new connection.");
        bleDeviceAddress = address;
        connectionState = STATE_CONNECTING;
        return true;
    }


    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (bleAdapter == null || bleGatt == null) {
            Log.wtf(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bleGatt.disconnect();
    }


    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (bleGatt == null) {
            return;
        }
        bleGatt.close();
        bleGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (bleAdapter == null || bleGatt == null) {
            Log.wtf(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.wtf("ReadCharacteristic called", "read characteristic called");
        bleGatt.readCharacteristic(characteristic);
    }
    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (bleAdapter == null || bleGatt == null) {
            Log.wtf(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if(characteristic == null)
            Log.wtf("setCharacteristicNotification", "NULL CHARACTERISTIC");

        Log.wtf("setCharacteristicNotification", String.valueOf(characteristic.getUuid()));
        bleGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bleGatt.writeDescriptor(descriptor);
    }


    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (bleGatt == null) return null;
        List<BluetoothGattService> services = bleGatt.getServices();
        for(BluetoothGattService service : services){
            Log.wtf("Services", String.valueOf(service.getUuid()));
            if(String.valueOf(service.getUuid()).equals("00110011-4455-6677-8899-aabbccddeeff")){
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for(BluetoothGattCharacteristic characteristic : characteristics){
                    Log.wtf("Characteristic", String.valueOf(characteristic.getUuid()));
                    if(String.valueOf(characteristic.getUuid()).equals("00000002-0000-1000-8000-00805f9b34fb")){
                        setCharacteristicNotification(characteristic, true);
                        break;
                    }
                }
                break;
            }
        }
        return services;
    }
}