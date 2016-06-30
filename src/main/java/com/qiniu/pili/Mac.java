package com.qiniu.pili;

import com.qiniu.pili.utils.HMac;
import com.qiniu.pili.utils.UrlSafeBase64;

import java.net.URL;

final class Mac {
    private String accessKey;
    private String secretKey;

    public Mac(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String sign(String data) throws Exception {
        byte[] sum = HMac.HmacSHA1Encrypt(data, this.secretKey);
        String sign = UrlSafeBase64.encodeToString(sum);
        return this.accessKey + ":" + sign;
    }

    public String signRequest(URL url, String method, byte[] body, String contentType) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%s %s", method, url.getPath()));
        if (url.getQuery() != null) {
            sb.append(String.format("?%s", url.getQuery()));
        }

        sb.append(String.format("\nHost: %s", url.getHost()));
        if (url.getPort() > 0) {
            sb.append(String.format(":%d", url.getPort()));
        }

        if (contentType != null) {
            sb.append(String.format("\nContent-Type: %s", contentType));
        }

        // body
        sb.append("\n\n");
        if (incBody(body, contentType)) {
            sb.append(new String(body));
        }

        byte[] sum = HMac.HmacSHA1Encrypt(sb.toString(), this.secretKey);
        String sign = UrlSafeBase64.encodeToString(sum);
        return this.accessKey + ":" + sign;

    }

    private boolean incBody(byte[] body, String contentType) {
        int maxContentLength = 1024 * 1024;
        boolean typeOK = contentType != null && !contentType.equals("application/octet-stream");
        boolean lengthOK = body != null && body.length > 0 && body.length < maxContentLength;
        return typeOK && lengthOK;
    }

}
