package com.pili;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import common.Config;
import common.UrlSafeBase64;

public class Auth {
    private static final String DIGEST_AUTH_PREFIX = "Qiniu";
    private SecretKeySpec mSkSpec;
    private MacKeys mMacKeys;
    public static class MacKeys {
        private String accessKey;
        private String secretKey;
        
        public MacKeys(String ak, String sk) {
            if (ak == null || sk == null) {
                throw new IllegalArgumentException("Invalid accessKey or secretKey!!");
            }
            accessKey = ak;
            secretKey = sk;
        }
    }

    private Auth() {
    }

    public Auth(MacKeys macKeys) {
        if (macKeys == null) {
            throw new NullPointerException("Invalid macKeys:" + macKeys);
        }
        mMacKeys = macKeys;
        try {
            mSkSpec = new SecretKeySpec(mMacKeys.secretKey.getBytes(Config.UTF8), "HmacSHA1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String signRequest(URL url, String method, byte[] body, String contentType) 
            throws SignatureException {
        StringBuilder sb = new StringBuilder();
        
        // <Method> <Path><?Query>
        String line = String.format("%s %s", method, url.getPath());
        sb.append(line);
        if (url.getQuery() != null) {
            sb.append("?" + url.getQuery());
        }

        // Host: <Host>
        sb.append(String.format("\nHost: %s", url.getHost()));
        if (url.getPort() > 0) {
            sb.append(String.format(":%d", url.getPort()));
        }

        // Content-Type: <Content-Type>
        if (contentType != null) {
            sb.append(String.format("\nContent-Type: %s", contentType));
        }

        // body
        sb.append("\n\n");
        if (body != null && contentType != null && 
                !"application/octet-stream".equals(contentType)) {
            sb.append(new String(body));
        }
        return String.format("%s %s:%s", DIGEST_AUTH_PREFIX, mMacKeys.accessKey, signData(sb.toString()));
    }

    private static byte[] digest(String secret, String data) throws SignatureException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(Config.UTF8), "HmacSHA1");
            Mac mac = createMac(secretKeySpec);
            mac.update(data.getBytes(Config.UTF8));
            return mac.doFinal();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SignatureException("Failed to digest: " + e.getMessage());
        } 
    }

    public static String sign(String secret, String data) throws SignatureException {
        return UrlSafeBase64.encodeToString(digest(secret, data));
    }

    private String signData(String data) throws SignatureException {
        String sign = null;
        try {
            Mac mac = createMac(mSkSpec);
            sign = UrlSafeBase64.encodeToString(mac.doFinal(data.getBytes(Config.UTF8)));
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return sign;
    }

    private static Mac createMac(SecretKeySpec secretKeySpec) throws GeneralSecurityException {
        Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(secretKeySpec);
        return mac;
    }
}
