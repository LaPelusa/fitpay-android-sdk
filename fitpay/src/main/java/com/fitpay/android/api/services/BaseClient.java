package com.fitpay.android.api.services;

import android.os.Build;

import com.fitpay.android.api.ApiManager;
import com.fitpay.android.utils.FPLog;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;

import static okhttp3.internal.Util.assertionError;

/**
 * Created by tgs on 5/20/16.
 */
public class BaseClient {
    protected static final String FP_KEY_ID = "fp-key-id";
    protected static final String FP_KEY_SDK_VER = "X-FitPay-SDK";


    public static OkHttpClient.Builder getOkHttpClient() {
        OkHttpClient.Builder builder = null;
        if ("true".equalsIgnoreCase(ApiManager.getConfig().get(ApiManager.PROPERTY_DISABLE_SSL_VALIDATION))) {
            FPLog.e("##################################################################################################################");
            FPLog.e("WARNING!!!  SSL validation has been completely disabled, this should NEVER be utilized in a production environment");
            FPLog.e("##################################################################################################################");
            builder = getUnsafeOkHttpClient();
        } else {
            builder = getDefaultOkHttpClient();
        }

        int connectTimeout = Integer.valueOf(ApiManager.getConfig().get(ApiManager.PROPERTY_HTTP_CONNECT_TIMEOUT));
        int readTimeout = Integer.valueOf(ApiManager.getConfig().get(ApiManager.PROPERTY_HTTP_READ_TIMEOUT));
        int writeTimeout = Integer.valueOf(ApiManager.getConfig().get(ApiManager.PROPERTY_HTTP_WRITE_TIMEOUT));

        builder = builder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true);

        if (FPLog.showHttpLogs()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder = builder.addInterceptor(logging);
        }

        return enableTls12OnPreLollipop(builder);
    }

    private static OkHttpClient.Builder getDefaultOkHttpClient() {
        return new OkHttpClient.Builder();
    }

    /*
     * OKClientBuilder for untrusted ssl endpoints
     *
     * DO NOT USE IN PRODUCTION
     */
    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, null);
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return builder.sslSocketFactory(sslSocketFactory, determineTrustManager())
                    .hostnameVerifier(trustAllHostnames);

        } catch (Exception e) {
            FPLog.e(e);

            throw new RuntimeException(e);
        }
    }

    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                FPLog.i("pre lollipop ssl configuraiton being used");

                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), determineTrustManager());

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                FPLog.e("Error while setting TLS 1.2", exc);

                throw new RuntimeException(exc);
            }
        }

        return client;
    }

    private static X509TrustManager determineTrustManager() {
        if ("true".equalsIgnoreCase(ApiManager.getConfig().get(ApiManager.PROPERTY_DISABLE_SSL_VALIDATION))) {
            return trustAllCerts;
        } else {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                return (X509TrustManager) trustManagers[0];
            } catch (GeneralSecurityException e) {
                throw assertionError("No System TLS", e); // The system has no TLS. Just give up.
            }
        }
    }

    final static X509TrustManager trustAllCerts = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    };

    final static HostnameVerifier trustAllHostnames = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
