package com.qiniu.pili;

public class Config {
    public static String APIHost = "pili.qiniuapi.com";
    static final String APIUserAgent = String.format("pili-sdk-java/v2 %s %s/%s", System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"));

    static final String APIHTTPScheme = "http://";
}