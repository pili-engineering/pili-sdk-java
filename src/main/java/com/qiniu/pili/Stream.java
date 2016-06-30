package com.qiniu.pili;

import com.google.gson.Gson;
import com.qiniu.pili.utils.UrlSafeBase64;

import java.io.UnsupportedEncodingException;

public final class Stream {
    private StreamInfo info;
    private String baseUrl;
    private RPC cli;
    private Gson gson;

    private Stream() {
    }

    Stream(StreamInfo info, RPC cli) throws UnsupportedEncodingException {
        this.info = info;
        String ekey = UrlSafeBase64.encodeToString(info.getKey());
        this.baseUrl = String.format("%s%s/v2/hubs/%s/streams/%s", Config.APIHTTPScheme, Config.APIHost, info.getHub(), ekey);
        this.cli = cli;
        this.gson = new Gson();
    }

    public String getHub() {
        return info.getHub();
    }

    public long getDisabledTill() {
        return info.getDisabledTill();
    }

    private void setDisabledTill(long disabledTill) throws PiliException {
        DisabledArgs args = new DisabledArgs(disabledTill);
        String path = baseUrl + "/disabled";
        String json = gson.toJson(args);

        try {
            cli.callWithJson(path, json);
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
    }

    public String getKey() {
        return info.getKey();
    }

    public String toJson() {
        return gson.toJson(info);
    }

    /*
        disable
     */
    public void disable() throws PiliException {
        setDisabledTill(-1);
    }

    /*
        enable
     */
    public void enable() throws PiliException {
        setDisabledTill(0);
    }

    public LiveStatus liveStatus() throws PiliException {
        String path = baseUrl + "/live";
        try {
            String resp = cli.callWithGet(path);
            LiveStatus status = gson.fromJson(resp, LiveStatus.class);
            return status;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
    }

    /*
        Save
     */
    private String appendQuery(String path, long start, long end) {
        String flag = "?";
        if (start > 0) {
            path += String.format("%sstart=%d", flag, start);
            flag = "&";
        }
        if (end > 0) {
            path += String.format("%send=%d", flag, end);
        }
        return path;
    }

    public String save(long start, long end) throws PiliException {
        String path = appendQuery(baseUrl + "/saveas", start, end);
        SaveArgs args = new SaveArgs(start, end);
        String json = gson.toJson(args);

        try {
            String resp = cli.callWithJson(path, json);
            SaveRet ret = gson.fromJson(resp, SaveRet.class);
            return ret.fname;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

    }

    public Record[] historyRecord(long start, long end) throws PiliException {
        String path = appendQuery(baseUrl + "/historyrecord", start, end);
        try {
            String resp = cli.callWithGet(path);
            HistoryRet ret = gson.fromJson(resp, HistoryRet.class);
            return ret.items;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
    }

    private class DisabledArgs {
        long disabledTill;

        public DisabledArgs(long disabledTill) {
            this.disabledTill = disabledTill;
        }
    }

    /*
        LiveStatus
     */
    public class FPSStatus {
        public int audio;
        public int video;
        public int data;
    }

    public class LiveStatus {
        public long startAt;
        public String clientIP;
        public int bps;
        public FPSStatus fps;

        public String toJson() {
            return new Gson().toJson(this);
        }
    }

    private class SaveArgs {
        long start;
        long end;

        public SaveArgs(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    private class SaveRet {
        String fname;
    }

    /*
        history
     */
    public class Record {
        public long start;
        public long end;
    }

    private class HistoryRet {
        Record[] items;
    }


}
