package com.qiniu.pili;

public final class Config {
    static final String APIHTTPScheme = "http://";
    public static String VERSION = "2.0.1";
    static final String APIUserAgent = String.format("pili-sdk-java/%s %s %s/%s", VERSION, System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"));
    public static String APIHost = "pili.qiniuapi.com";
}