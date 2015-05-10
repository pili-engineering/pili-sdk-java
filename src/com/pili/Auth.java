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
    private static SecretKeySpec mSkSpec;
    private static MacKeys mMacKeys;
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

    private static class AuthHolder {
        public final static Auth instance = new Auth();
    }

    private Auth() {
    }

    public static Auth getAuthInstance(MacKeys macKeys) {
        if (macKeys == null)
            return null;
        if (macKeys == mMacKeys) {
            return AuthHolder.instance;
        }
        mMacKeys = macKeys;
        try {
            mSkSpec = new SecretKeySpec(mMacKeys.secretKey.getBytes(Config.UTF8), "HmacSHA1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return AuthHolder.instance;
    }

    public String signRequest(URL url, String method, byte[] body, String contentType) 
            throws SignatureException {
        StringBuilder sb = new StringBuilder();
        
        // <Method> <Path><?Query>
        String line = String.format("%s %s", method, url.getPath());
        sb.append(line);
        if (url.getQuery() != null) {
            System.out.println("url.getQuery()="+url.getQuery());
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

        return signData(sb.toString());
    }

    private String signData(String data) throws SignatureException {
        String token = null;
        try {
            Mac mac = createMac();
            String sign = UrlSafeBase64.encodeToString(mac.doFinal(data.getBytes()));
            token = String.format("%s %s:%s", DIGEST_AUTH_PREFIX, mMacKeys.accessKey, sign);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return token;
    }

    private Mac createMac() throws GeneralSecurityException {
        Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(mSkSpec);
        return mac;
    }
}
