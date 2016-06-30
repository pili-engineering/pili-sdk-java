package com.qiniu.pili;


public final class StreamInfo {
    private String hub;
    private String key;
    private long disabledTill;


    public StreamInfo(String hub, String key, long disabledTill) {
        this.disabledTill = disabledTill;
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
}
