package com.qiniu.pili.utils;

import java.io.UnsupportedEncodingException;

public final class UrlSafeBase64 {

    private UrlSafeBase64() {
    }

    public static String encodeToString(String data) {
        try {
            return encodeToString(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeToString(byte[] data) {
        return Base64.encodeToString(data, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static byte[] decode(String data) {
        return Base64.decode(data, Base64.URL_SAFE | Base64.NO_WRAP);
    }
}
