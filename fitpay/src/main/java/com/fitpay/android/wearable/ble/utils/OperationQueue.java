package com.fitpay.android.wearable.ble.utils;

import com.fitpay.android.wearable.ble.operations.GattOperation;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Vlad on 26.03.2016.
 */
public class OperationQueue extends ConcurrentLinkedQueue<GattOperation> {

    public GattOperation getFirst() {
        GattOperation operation = null;

        Iterator iter = this.iterator();
        if (iter.hasNext()) {
            operation = (GattOperation) iter.next();

            if (!operation.hasNested()) {
                return poll();
            }

            OperationQueue bundle = operation.getNestedQueue();
            return bundle.getFirst();
        }

        return operation;
    }
}
