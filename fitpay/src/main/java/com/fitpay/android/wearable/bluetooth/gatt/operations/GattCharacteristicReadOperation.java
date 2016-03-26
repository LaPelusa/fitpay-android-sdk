package com.fitpay.android.wearable.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.fitpay.android.wearable.bluetooth.gatt.callbacks.GattCharacteristicReadCallback;
import com.fitpay.android.wearable.bluetooth.gatt.interfaces.CharacteristicReader;
import com.orhanobut.logger.Logger;

import java.util.UUID;

public class GattCharacteristicReadOperation extends GattOperation implements CharacteristicReader {
    private final UUID mService;
    private final UUID mCharacteristic;
    private final GattCharacteristicReadCallback mCallback;

    public GattCharacteristicReadOperation(BluetoothDevice device, UUID service, UUID characteristic, GattCharacteristicReadCallback callback) {
        super(device);
        mService = service;
        mCharacteristic = characteristic;
        mCallback = callback;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Logger.d("writing to " + mCharacteristic);
        BluetoothGattCharacteristic characteristic = gatt.getService(mService).getCharacteristic(mCharacteristic);
        gatt.readCharacteristic(characteristic);
    }

    @Override
    public void onRead(BluetoothGattCharacteristic characteristic) {
        mCallback.call(characteristic.getValue());
    }
}
