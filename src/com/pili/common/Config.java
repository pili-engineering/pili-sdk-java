// CHECKSTYLE:OFF

package com.pili.common;

public final class Config {
    public static final String SDK_VERSION = "1.5.3";

    public static final String USER_AGENT = "pili-sdk-java";

    public static final String UTF8 = "UTF-8";

    public static final String DEFAULT_API_VERSION = "v2";

    public static final String DEFAULT_RTC_VERSION = "v2";

    @Deprecated
    public static final String API_VERSION = DEFAULT_API_VERSION;

    public static final String DEFAULT_API_HOST = "pili.qiniuapi.com";
    public static final String DEFAULT_RTC_HOST = "rtc.qiniuapi.com";
    public static final boolean DEFAULT_USE_HTTPS = false;

    public static final int TITLE_MIN_LENGTH = 5;
    public static final int TITLE_MAX_LENGTH = 200;

    private Config() {

    }
}

// CHECKSTYLE:ON
