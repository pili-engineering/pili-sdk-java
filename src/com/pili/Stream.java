package com.pili;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Stream {
    public static final String ORIGIN = "ORIGIN";
    private String mStreamJsonStr;
    private Auth mAuth;

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
    private String liveHttpHost;
    private String playbackHttpHost;

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

        Type arrType = new TypeToken<String[]>() {}.getType();
        profiles = new Gson().fromJson(jsonObj.get("profiles"), arrType);

        JsonObject hosts = jsonObj.getAsJsonObject("hosts");
        JsonObject publish = hosts.getAsJsonObject("publish");
        JsonObject live = hosts.getAsJsonObject("live");
        JsonObject playback = hosts.getAsJsonObject("playback");

        publishRtmpHost = publish.get("rtmp").getAsString();
        liveRtmpHost = live.get("rtmp").getAsString();
        liveHttpHost = live.get("http").getAsString();
        playbackHttpHost = playback.get("http").getAsString();

        mStreamJsonStr = jsonObj.toString();
    }

    public Stream(JsonObject jsonObject, Auth auth) {
        this(jsonObject);
        mAuth = auth;
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
    public String getLiveHttpHost() {
        return liveHttpHost;
    }
    public String getPlaybackHttpHost() {
        return playbackHttpHost;
    }
    @Deprecated
    public String getPlayHlsHost() {
        return playbackHttpHost;
    }
    @Deprecated
    public String getPlayRtmpHost() {
        return liveRtmpHost;
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
            targetUrl = jsonObj.get("targetUrl").getAsString();
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
            persistentId = jsonObj.get("persistentId").getAsString();
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
        private int audio;
        private int video;
        private int data;
        public FramesPerSecond(int audio, int video, int data) {
            this.audio = audio;
            this.video = video;
            this.data = data;
        }
        
        public int getAudio() {
            return audio;
        }
        public int getVideo() {
            return video;
        }
        public int getData() {
            return data;
        }
    }

    public static class SegmentList {
        private List<Segment> segmentList;

        public SegmentList(JsonObject jsonObj) {
            JsonArray respArray = jsonObj.getAsJsonArray("segments");
            Iterator<JsonElement> it = respArray.iterator();
            segmentList = new ArrayList<Segment>();
            while (it.hasNext()) {
                JsonObject json = it.next().getAsJsonObject();
                segmentList.add(new Segment(json.get("start").getAsLong(), json.get("end").getAsLong()));
            }
        }

        public List<Segment> getSegmentList() {
            return segmentList;
        }
    }

    public static class Status {
        private String addr;
        private String status;
        private long bytesPerSecond;
        private FramesPerSecond framesPerSecond;
        private String mJsonString;
        public Status(JsonObject jsonObj) {
            addr = jsonObj.get("addr").getAsString();
            status = jsonObj.get("status").getAsString();
            bytesPerSecond = jsonObj.get("bytesPerSecond").getAsLong();
            
            JsonObject framesPerSecondJsonObj = jsonObj.getAsJsonObject("framesPerSecond");
            int audio = framesPerSecondJsonObj.get("audio").getAsInt();
            int video = framesPerSecondJsonObj.get("video").getAsInt();
            int data = framesPerSecondJsonObj.get("data").getAsInt();
            framesPerSecond = new FramesPerSecond(audio, video, data);
            mJsonString = jsonObj.toString();
        }
        public String getAddr() {
            return addr;
        }
        public String getStatus() {
            return status;
        }
        public long getBytesPerSecond() {
            return bytesPerSecond;
        }
        public FramesPerSecond getFramesPerSecond() {
            return framesPerSecond;
        }
        
        @Override
        public String toString() {
            return mJsonString;
        }
    }

    public static class StreamList {
        private String marker;
        private List<Stream> itemList;
        public StreamList(JsonObject jsonObj, Auth auth) {
            this.marker = jsonObj.get("marker").getAsString();

            try {
                JsonArray respArray = jsonObj.getAsJsonArray("items");
                Iterator<JsonElement> it = respArray.iterator();
                itemList = new ArrayList<Stream>();
                while (it.hasNext()) {
                  JsonObject json = it.next().getAsJsonObject();
                  itemList.add(new Stream(json, auth));
                }
            } catch (java.lang.ClassCastException e) {
                e.printStackTrace();
            }
        }

        public String getMarker() {
            return marker;
        }
        public List<Stream> getStreams() {
            return itemList;
        }
    }

    public Stream update(String publishKey, String publishSecrity, boolean disabled) throws PiliException {
        return API.updateStream(mAuth, this.id, publishKey, publishSecrity, disabled);
    }

    public SegmentList segments() throws PiliException {
        return API.getStreamSegments(mAuth, this.id, 0, 0, 0);
    }

    public SegmentList segments(long start, long end) throws PiliException {
        return API.getStreamSegments(mAuth, this.id, start, end, 0);
    }

    public SegmentList segments(long start, long end, int limit) throws PiliException {
        return API.getStreamSegments(mAuth, this.id, start, end, limit);
    }

    public Status status() throws PiliException {
        return API.getStreamStatus(mAuth, this.id);
    }

    public String rtmpPublishUrl() throws PiliException {
        return API.publishUrl(this.publishRtmpHost, this.id, this.publishKey, this.publishSecurity, 0);
    }
    public Map<String, String> rtmpLiveUrls() {
        return API.rtmpLiveUrl(this.liveRtmpHost, this.id, this.profiles);
    }
    public Map<String, String> hlsLiveUrls() {
        return API.hlsLiveUrl(this.liveHttpHost, this.id, this.profiles);
    }
    public Map<String, String> hlsPlaybackUrls(long start, long end) throws PiliException {
        return API.hlsPlaybackUrl(this.playbackHttpHost,  this.id, start, end,  this.profiles);
    }

    public Map<String, String> httpFlvLiveUrls() {
        return API.httpFlvLiveUrl(this.liveHttpHost, this.id, this.profiles);
    }

    public String delete() throws PiliException {
        return API.deleteStream(mAuth, this.id);
    }

    public String toJsonString() {
        return mStreamJsonStr;
    }

    public SaveAsResponse saveAs(String fileName, String format, long startTime, long endTime, String notifyUrl) throws PiliException {
        return API.saveAs(mAuth, this.id, fileName, format, startTime, endTime, notifyUrl);
    }
    public SaveAsResponse saveAs(String fileName, String format, long startTime, long endTime) throws PiliException {
        return saveAs(fileName, format, startTime, endTime, null);
    }

    public SnapshotResponse snapshot(String name, String format) throws PiliException {
        return API.snapshot(mAuth, this.id, name, format, 0, null);
    }
    public SnapshotResponse snapshot(String name, String format, long time, String notifyUrl) throws PiliException {
        return API.snapshot(mAuth, this.id, name, format, time, notifyUrl);
    }

    public Stream enable() throws PiliException {
        return API.updateStream(mAuth, this.id, null, null, false);
    }
    public Stream disable() throws PiliException {
        return API.updateStream(mAuth, this.id, null, null, true);
    }
}

