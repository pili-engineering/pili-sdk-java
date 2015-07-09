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
    private String playHlsHost;
    private String playRtmpHost;

    public Stream(JsonObject jsonObj) {
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
        JsonObject play = hosts.getAsJsonObject("play");

        publishRtmpHost = publish.get("rtmp").getAsString();
        playHlsHost = play.get("hls").getAsString();
        playRtmpHost = play.get("rtmp").getAsString();

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
    public String getPlayHlsHost() {
        return playHlsHost;
    }
    public String getPlayRtmpHost() {
        return playRtmpHost;
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
        public Status(JsonObject jsonObj) {
            addr = jsonObj.get("addr").getAsString();
            status = jsonObj.get("status").getAsString();
        }
        public String getAddr() {
            return addr;
        }
        public String getStatus() {
            return status;
        }
    }

    public static class StreamList {
        private String marker;
        private List<Stream> itemList;
        public StreamList(JsonObject jsonObj) {
            this.marker = jsonObj.get("marker").getAsString();

            JsonArray respArray = jsonObj.getAsJsonArray("items");
            Iterator<JsonElement> it = respArray.iterator();
            itemList = new ArrayList<Stream>();
            while (it.hasNext()) {
              JsonObject json = it.next().getAsJsonObject();
              itemList.add(new Stream(json));
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
        return API.getStreamSegments(mAuth, this.id, 0, 0);
    }

    public SegmentList segments(long start, long end) throws PiliException {
        return API.getStreamSegments(mAuth, this.id, start, end);
    }

    public Status status() throws PiliException {
        return API.getStreamStatus(mAuth, this.id);
    }

    public String rtmpPublishUrl() throws PiliException {
        return API.publishUrl(this.publishRtmpHost, this.id, this.publishKey, this.publishSecurity, 0);
    }
    public Map<String, String> rtmpLiveUrls() {
        return API.rtmpLiveUrl(this.playRtmpHost, this.id, this.profiles);
    }
    public Map<String, String> hlsLiveUrls() {
        return API.hlsLiveUrl(this.playHlsHost, this.id, this.profiles);
    }
    public Map<String, String> hlsPlaybackUrls(long start, long end) throws PiliException {
        return API.hlsPlaybackUrl(this.playHlsHost,  this.id, start, end,  this.profiles);
    }
    public String delete() throws PiliException {
        return API.deleteStream(mAuth, this.id);
    }

    public String toJsonString() {
        return mStreamJsonStr;
    }
}

