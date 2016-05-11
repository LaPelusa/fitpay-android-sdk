package com.fitpay.android.paymentdevice.impl.ble;

import com.fitpay.android.paymentdevice.interfaces.IApduMessage;

import java.util.Arrays;

/**
 * Single apdu command execution result
 */
public class ApduCommandResultBytes {

    private String commandId;
    private byte[] responseCode;
    private byte[] responseData;

    public ApduCommandResultBytes(String commandId, IApduMessage message) {
        this.commandId = commandId;

        byte[] data = message.getData();
        responseCode = Arrays.copyOfRange(data, data.length - 2, data.length);
        responseData = data;
    }

    public String getCommandId() {
        return commandId;
    }

    public byte[] getResponseData() {
        return responseData;
    }

    public byte[] getResponseCode() {
        return responseCode;
    }
}
