package com.qiniu.pili;


public final class StreamInfo {
    private String hub;
    private String key;
    // the disabled until when, 0 means no disabled, -1 means disabled forever.
    private long disabledTill;

    // codec profiles
    private String[] converts;

    StreamInfo(String hub, String key) {
        this.hub = hub;
        this.key = key;
    }

    void setMeta(String hub, String key) {
        this.key = key;
        this.hub = hub;
    }

    public String getHub() {
        return hub;
    }

    public long getDisabledTill() {
        return disabledTill;
    }

    public String getKey() {
        return key;
    }

    public String[] getConverts() {
        return converts;
    }
}
