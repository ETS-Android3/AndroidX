package com.androidx.net;

import android.text.TextUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by Relin
 * on 2018-11-01.
 */
public class HttpsHostnameVerifier implements HostnameVerifier {

    public static String HOST_NAME = "";

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (TextUtils.isEmpty(hostname)) {
            return false;
        }
        return hostname.equals(HOST_NAME);
    }
}

