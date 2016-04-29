package com.pili;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.qiniu.Credentials;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Stream {
    public static final String ORIGIN = "ORIGIN";
    private String mStreamJsonStr;
    private Credentials mCredentials;

    private String id;
    private String createdAt;       // Time ISO 8601
    private String updatedAt;       // Time ISO 8601
    private String title;           // Length[4-200]
    private String hub;
    private String publishKey;
    private String publishSecurity; // "static" or "dynamic"
    private boolean disabled;
    private String[] profiles;
    private String publishRtmpHost;
    private String liveRtmpHost;
    private String liveHdlHost;
    private String liveHlsHost;
    private String playbackHlshost;

    public Stream(JsonObject jsonObj) {
//        System.out.println("Stream:" + jsonObj.toString());
        id = jsonObj.get("id").getAsString();
        hub = jsonObj.get("hub").getAsString();
        createdAt = jsonObj.get("createdAt").getAsString();
        updatedAt = jsonObj.get("updatedAt").getAsString();
        title = jsonObj.get("title").getAsString();
        publishKey = jsonObj.get("publishKey").getAsString();
        publishSecurity = jsonObj.get("publishSecurity").getAsString();
        disabled = jsonObj.get("disabled").getAsBoolean();

        Type arrType = new TypeToken<String[]>() {
        }.getType();
        profiles = new Gson().fromJson(jsonObj.get("profiles"), arrType);

        JsonObject hosts = jsonObj.getAsJsonObject("hosts");
        JsonObject publish = hosts.getAsJsonObject("publish");
        JsonObject live = hosts.getAsJsonObject("live");
        JsonObject playback = hosts.getAsJsonObject("playback");

        if (publish != null) {
            publishRtmpHost = publish.get("rtmp").getAsString();
        }
        if (live != null) {
            liveHdlHost = live.get("hdl").getAsString();
            liveHlsHost = live.get("hls").getAsString();
            liveRtmpHost = live.get("rtmp").getAsString();
        }
        if (playback != null) {
            playbackHlshost = playback.get("hls").getAsString();
        }

        mStreamJsonStr = jsonObj.toString();
    }

    public Stream(JsonObject jsonObject, Credentials credentials) {
        this(jsonObject);
        mCredentials = credentials;
    }

    public String[] getProfiles() {
        return profiles;
    }

    public String getPublishRtmpHost() {
        return publishRtmpHost;
    }

    public String getLiveRtmpHost() {
        return liveRtmpHost;
    }

    public String getPlaybackHlshost() {
        return playbackHlshost;
    }

    public String getLiveHdlHost() {
        return liveHdlHost;
    }

    public String getLiveHlsHost() {
        return liveHlsHost;
    }

    public String getStreamId() {
        return id;
    }

    public String getHubName() {
        return hub;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishKey() {
        return publishKey;
    }

    public String getPublishSecurity() {
        return publishSecurity;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public Stream update(String publishKey, String publishSecrity, boolean disabled) throws PiliException {
        return API.updateStream(mCredentials, this.id, publishKey, publishSecrity, disabled);
    }

    public SegmentList segments() throws PiliException {
        return API.getStreamSegments(mCredentials, this.id, 0, 0, 0);
    }

    public SegmentList segments(long start, long end) throws PiliException {
        return API.getStreamSegments(mCredentials, this.id, start, end, 0);
    }

    public SegmentList segments(long start, long end, int limit) throws PiliException {
        return API.getStreamSegments(mCredentials, this.id, start, end, limit);
    }

    public Status status() throws PiliException {
        return API.getStreamStatus(mCredentials, this.id);
    }

    public String rtmpPublishUrl() throws PiliException {
        return API.publishUrl(this, 0);
    }

    public Map<String, String> rtmpLiveUrls() {
        return API.rtmpLiveUrl(this);
    }

    public Map<String, String> hlsLiveUrls() {
        return API.hlsLiveUrl(this);
    }

    public Map<String, String> hlsPlaybackUrls(long start, long end) throws PiliException {
        return API.hlsPlaybackUrl(mCredentials, this, start, end);
    }

    public Map<String, String> httpFlvLiveUrls() {
        return API.httpFlvLiveUrl(this);
    }

    public String delete() throws PiliException {
        return API.deleteStream(mCredentials, this.id);
    }

    public String toJsonString() {
        return mStreamJsonStr;
    }

    public SaveAsResponse saveAs(String fileName, String format, long startTime, long endTime, String notifyUrl, String pipeline)
            throws PiliException {
        return API.saveAs(mCredentials, this.id, fileName, format, startTime, endTime, notifyUrl, pipeline);
    }

    public SaveAsResponse saveAs(String fileName, String format, long startTime, long endTime) throws PiliException {
        return saveAs(fileName, format, startTime, endTime, null);
    }

    public SaveAsResponse saveAs(String fileName, long startTime, long endTime) throws PiliException {
        return saveAs(fileName, null, startTime, endTime, null);
    }

    public SnapshotResponse snapshot(String name, String format) throws PiliException {
        return API.snapshot(mCredentials, this.id, name, format, 0, null);
    }

    public SnapshotResponse snapshot(String name, String format, long time, String notifyUrl) throws PiliException {
        return API.snapshot(mCredentials, this.id, name, format, time, notifyUrl);
    }

    public Stream enable() throws PiliException {
        return API.updateStream(mCredentials, this.id, null, null, false);
    }

    public Stream disable() throws PiliException {
        return API.updateStream(mCredentials, this.id, null, null, true);
    }

    public static class Segment {
        private long start;
        private long end;

        public Segment(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }
    }

    public static class SaveAsResponse {
        private String url;
        private String targetUrl;
        private String persistentId;
        private String mJsonString;

        public SaveAsResponse(JsonObject jsonObj) {
            url = jsonObj.get("url").getAsString();
            try {
                targetUrl = jsonObj.get("targetUrl").getAsString();
            } catch (java.lang.NullPointerException e) {
                // do nothing. ignore.
            }
            persistentId = jsonObj.get("persistentId").getAsString();
            mJsonString = jsonObj.toString();
        }

        public String getUrl() {
            return url;
        }

        public String getTargetUrl() {
            return targetUrl;
        }

        public String getPersistentId() {
            return persistentId;
        }

        @Override
        public String toString() {
            return mJsonString;
        }
    }

    public static class SnapshotResponse {
        private String targetUrl;
        private String persistentId;
        private String mJsonString;

        public SnapshotResponse(JsonObject jsonObj) {
            targetUrl = jsonObj.get("targetUrl").getAsString();
            persistentId = jsonObj.get("persistentId") == null ? null : jsonObj.get("persistentId").getAsString();
            mJsonString = jsonObj.toString();
        }

        public String getTargetUrl() {
            return targetUrl;
        }

        public String getPersistentId() {
            return persistentId;
        }

        @Override
        public String toString() {
            return mJsonString;
        }
    }

    public static class FramesPerSecond {
        private float audio;
        private float video;
        private float data;

        public FramesPerSecond(float audio, float video, float data) {
            this.audio = audio;
            this.video = video;
            this.data = data;
        }

        public float getAudio() {
            return audio;
        }

        public float getVideo() {
            return video;
        }

        public float getData() {
            return data;
        }
    }

    public static class SegmentList {
        private long start;
        private long end;
        private int duration;

        private List<Segment> segmentList;

        public SegmentList(JsonObject jsonObj) {
            start = jsonObj.get("start").getAsLong();
            end = jsonObj.get("end").getAsLong();
            duration = jsonObj.get("duration").getAsInt();

            JsonArray respArray = jsonObj.getAsJsonArray("segments");
            Iterator<JsonElement> it = respArray.iterator();
            segmentList = new ArrayList<Segment>();

            while (it.hasNext()) {
                JsonObject json = it.next().getAsJsonObject();
                segmentList.add(new Segment(json.get("start").getAsLong(), json.get("end").getAsLong()));
            }
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        public int getDuration() {
            return duration;
        }

        public List<Segment> getSegmentList() {
            return segmentList;
        }
    }

    public static class Status {
        private String addr;
        private String status;
        private float bytesPerSecond;
        private FramesPerSecond framesPerSecond;
        private String startFrom;
        private String mJsonString;

        public Status(JsonObject jsonObj) {
            addr = jsonObj.get("addr").getAsString();
            status = jsonObj.get("status").getAsString();
            try {
                startFrom = jsonObj.get("startFrom").getAsString();
                bytesPerSecond = jsonObj.get("bytesPerSecond").getAsFloat();

                JsonObject framesPerSecondJsonObj = jsonObj.getAsJsonObject("framesPerSecond");
                float audio = framesPerSecondJsonObj.get("audio").getAsFloat();
                float video = framesPerSecondJsonObj.get("video").getAsFloat();
                float data = framesPerSecondJsonObj.get("data").getAsFloat();
                framesPerSecond = new FramesPerSecond(audio, video, data);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            mJsonString = jsonObj.toString();
        }

        public String getAddr() {
            return addr;
        }

        public String getStatus() {
            return status;
        }

        public float getBytesPerSecond() {
            return bytesPerSecond;
        }

        public FramesPerSecond getFramesPerSecond() {
            return framesPerSecond;
        }

        public String getStartFrom() {
            return startFrom;
        }

        @Override
        public String toString() {
            return mJsonString;
        }
    }

    public static class StreamList {
        private String marker;
        private boolean end;
        private List<Stream> itemList;

        public StreamList(JsonObject jsonObj, Credentials auth) {
            this.marker = jsonObj.get("marker").getAsString();
            this.end = jsonObj.get("end").getAsBoolean();
            itemList = new ArrayList<Stream>();

            try {
                JsonArray respArray = jsonObj.getAsJsonArray("items");
                Iterator<JsonElement> it = respArray.iterator();
                while (it.hasNext()) {
                    JsonObject json = it.next().getAsJsonObject();
                    itemList.add(new Stream(json, auth));
                }
            } catch (java.lang.ClassCastException e) {
//                e.printStackTrace();
            }
        }

        public String getMarker() {
            return marker;
        }

        public boolean isEnd() {
            return end;
        }

        public List<Stream> getStreams() {
            return itemList;
        }
    }
}
