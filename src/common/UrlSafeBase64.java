package common;

import java.io.UnsupportedEncodingException;

public class UrlSafeBase64 {

    public static String encodeToString(String data) {
        try {
            return encodeToString(data.getBytes(Config.UTF8));
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
