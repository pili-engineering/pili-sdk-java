package com.qiniu.pili;


public class StreamInfo {
    private String hub;
    private String key;
    private String[] converts;
    private long disabledTill;

    public StreamInfo(String hub, String key, long disabledTill) {
        this(hub,key,null,disabledTill);
    }

    public StreamInfo(String hub, String key, String[] converts, long disabledTill) {
        this.hub = hub;
        this.key = key;
        this.converts = converts;
        this.disabledTill = disabledTill;
    }

    public String getHub() {
        return hub;
    }

    public String getKey() {
        return key;
    }

    public String[] getConverts() {
        return converts;
    }

    public void setConverts(String[] converts) {
        this.converts = converts;
    }

    public long getDisabledTill() {
        return disabledTill;
    }

    public void setDisabledTill(long disabledTill) {
        this.disabledTill = disabledTill;
    }
}
