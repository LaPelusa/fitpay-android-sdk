package com.fitpay.android.utils;

import com.fitpay.android.api.models.Links;
import com.fitpay.android.api.models.Payload;
import com.fitpay.android.api.models.apdu.ApduCommandResult;
import com.fitpay.android.api.models.card.CreditCardInfo;
import com.fitpay.android.api.models.user.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;

public final class Constants {

    public static final String FIT_PAY_TAG = "FitPay";

    static final String BASE_URL = "https://gi-de.pagare.me/";
    static final String API_URL = BASE_URL + "api/";
    static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";

    static final String PUSHER_KEY = "ef31c4c7ce55c574d8f9";

    static Gson gson;

    static Gson getGson() {
        if (gson == null){
            gson = new GsonBuilder()
                    .setDateFormat(Constants.DATE_FORMAT)
                    .registerTypeAdapter(ECCKeyPair.class, new ModelAdapter.KeyPairSerializer())
                    .registerTypeAdapter(Links.class, new ModelAdapter.LinksDeserializer())
                    .registerTypeAdapter(UserInfo.class, new ModelAdapter.DataSerializer<>())
                    .registerTypeAdapter(CreditCardInfo.class, new ModelAdapter.DataSerializer<>())
                    .registerTypeAdapter(Payload.class, new ModelAdapter.PayloadDeserializer())
                    .registerTypeAdapter(ApduCommandResult.class, new ModelAdapter.AdpuCommandResponseSerializer())
                    .create();
        }
        return gson;
    }


    public static void printError(Throwable error) {
        printError(error.toString());
    }

    public static void printError(String error) {
        Logger.t(FIT_PAY_TAG).e(error);
    }
}
