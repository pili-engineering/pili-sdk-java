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

    public String[] getConverts() {
        return info.getConverts();
    }

    public String getKey() {
        return info.getKey();
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
            throw new PiliException(e);
        }
    }

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

    public String toJson() {
        return gson.toJson(info);
    }

    /**
     * fetch stream info
     *
     * @return
     * @throws PiliException
     */
    public Stream info() throws PiliException {
        try {
            String resp = cli.callWithGet(baseUrl);
            StreamInfo ret = gson.fromJson(resp, StreamInfo.class);
            ret.setMeta(info.getHub(), info.getKey());
            this.info = ret;
            return this;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            throw new PiliException(e);
        }
    }

    /**
     * diable stream
     *
     * @throws PiliException
     */
    public void disable() throws PiliException {
        setDisabledTill(-1);
    }

    /**
     * disable stream
     *
     * @param disabledTill
     * @throws PiliException
     */
    public void disable(long disabledTill) throws PiliException {
        setDisabledTill(disabledTill);
    }

    /**
     * enable stream
     *
     * @throws PiliException
     */
    public void enable() throws PiliException {
        setDisabledTill(0);
    }

    /**
     * get the status of live stream
     *
     * @return
     * @throws PiliException
     */
    public LiveStatus liveStatus() throws PiliException {
        String path = baseUrl + "/live";
        try {
            String resp = cli.callWithGet(path);
            LiveStatus status = gson.fromJson(resp, LiveStatus.class);
            return status;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            throw new PiliException(e);
        }
    }

    /**
     * save playback
     *
     * @param start
     * @param end
     * @return
     * @throws PiliException
     */
    public String save(long start, long end) throws PiliException {
        SaveOptions args = new SaveOptions(start, end);
        return save(args);
    }

    /**
     * save playback with more options
     *
     * @param opts
     * @return
     * @throws PiliException
     */
    public String save(SaveOptions opts) throws PiliException {
        String path = baseUrl + "/saveas";
        String json = gson.toJson(opts);

        try {
            String resp = cli.callWithJson(path, json);
            SaveRet ret = gson.fromJson(resp, SaveRet.class);
            return ret.fname;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            throw new PiliException(e);
        }
    }

    /**
     * snapshot the live stream
     *
     * @param opts
     * @return
     * @throws PiliException
     */
    public String snapshot(SnapshotOptions opts) throws PiliException {
        String path = baseUrl + "/snapshot";
        String json = gson.toJson(opts);
        try {
            String resp = cli.callWithJson(path, json);
            SnapshotRet ret = gson.fromJson(resp, SnapshotRet.class);
            return ret.fname;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            throw new PiliException(e);
        }
    }

    /**
     * update convert configs
     *
     * @param profiles
     * @throws PiliException
     */
    public void updateConverts(String[] profiles) throws PiliException {
        String path = baseUrl + "/converts";
        String json = gson.toJson(new ConvertsOptions(profiles));
        try {
            cli.callWithJson(path, json);
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            throw new PiliException(e);
        }

    }

    /**
     * query the stream history
     *
     * @param start
     * @param end
     * @return
     * @throws PiliException
     */
    public Record[] historyRecord(long start, long end) throws PiliException {
        String path = appendQuery(baseUrl + "/historyrecord", start, end);
        try {
            String resp = cli.callWithGet(path);
            HistoryRet ret = gson.fromJson(resp, HistoryRet.class);
            return ret.items;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
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

    public static class LiveStatus {
        public long startAt;
        public String clientIP;
        public int bps;
        public FPSStatus fps;

        public String toJson() {
            return new Gson().toJson(this);
        }
    }

    public static class SaveOptions {
        /**
         * start unix time
         */
        public long start;
        /**
         * end unix time. 0 means current time
         */
        public long end;
        /**
         * the saved file name
         */
        public String fname;
        /**
         * file format. default in m3u8
         */
        public String format;
        /**
         * if qiniu dora pipeline is needed, assign this value
         */
        public String pipeline;
        /**
         * URL address. After dora asynchronous operation is done, will notify this address
         */
        public String notify;
        /**
         * ts's expiration days
         * -1 means no change of ts's expiration
         * 0 means storing forever
         * any other positive number can change the ts's expiration days
         */
        public long expireDays;

        public SaveOptions() {
        }

        public SaveOptions(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    private class SaveRet {
        String fname;
    }

    public static class SnapshotOptions {
        /**
         * the saved file name
         */
        public String fname;
        /**
         * the unix time of snapshot. 0 means the current time
         */
        public long time;
        /**
         * file format. default in jpg
         */
        public String format;

        public SnapshotOptions() {
        }

        public SnapshotOptions(String fname, long time, String format) {
            this.fname = fname;
            this.time = time;
            this.format = format;
        }
    }

    private class SnapshotRet {
        String fname;
    }

    private class ConvertsOptions {
        String[] converts;

        public ConvertsOptions(String[] converts) {
            this.converts = converts;
        }
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
