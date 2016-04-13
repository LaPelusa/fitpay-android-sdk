package com.fitpay.android.api.models.device;

import android.support.annotation.NonNull;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.models.apdu.ApduExecutionResult;

/**
 * Commit
 */
public final class Commit extends CommitModel {
    private static final String PREVIOUS = "previous";
    private static final String APDU_RESPONSE = "apduResponse";

    /**
     * Get previous commit
     *
     * @param callback result callback
     */
    public void getPreviousCommit(@NonNull ApiCallback<Commit> callback) {
        makeGetCall(PREVIOUS, null, Commit.class, callback);
    }

    /**
     * Endpoint to confirm APDU execution.
     *
     * @param apduExecutionResult package confirmation data:(packageId, state, executedTs, executedDuration, apduResponses:(commandId, commandId, responseData))
     * @param callback   result callback
     */
    public void confirm(@NonNull ApduExecutionResult apduExecutionResult, @NonNull ApiCallback<Void> callback) {
        makePostCall(APDU_RESPONSE, apduExecutionResult, Void.class, callback);
    }
}