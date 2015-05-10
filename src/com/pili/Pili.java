package com.pili;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pili.Auth.MacKeys;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import common.Config;
import common.Utils;

public class Pili {

    private static final String API_BASE_URL = 
            String.format("http://%s/%s", Config.DEFAULT_API_HOST, Config.API_VERSION);

    public class Stream {
        private String streamId;
        private String hubName;
        private String createdAt;
        private String updatedAt;
        private String title;
        private String publishKey;
        private String publishSecurity;

        public Stream(JsonObject jsonObj) {
            streamId = jsonObj.get("id").getAsString();
            hubName = jsonObj.get("hub").getAsString();
            createdAt = jsonObj.get("createdAt").getAsString();
            updatedAt = jsonObj.get("updatedAt").getAsString();
            title = jsonObj.get("title").getAsString();
            publishKey = jsonObj.get("publishKey").getAsString();
            publishSecurity = jsonObj.get("publishSecurity").getAsString();
        }

        public String getStreamId() {
            return streamId;
        }
        public String getHubName() {
            return hubName;
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
    }

    public class StreamList {
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

    public class StreamSegment {
        private long start;
        private long end;

        public StreamSegment(long start, long end) {
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

    public class StreamSegmentList {
        private List<StreamSegment> segmentList;

        public StreamSegmentList(JsonObject jsonObj) {
            JsonArray respArray = jsonObj.getAsJsonArray("segments");
            Iterator<JsonElement> it = respArray.iterator();
            segmentList = new ArrayList<StreamSegment>();
            while (it.hasNext()) {
                JsonObject json = it.next().getAsJsonObject();
                segmentList.add(new StreamSegment(json.get("start").getAsLong(), json.get("end").getAsLong()));
            }
        }

        public List<StreamSegment> getStreamSegmentList() {
            return segmentList;
        }
    }

    private final OkHttpClient mOkHttpClient;
    private static Auth mAuth;
    public Pili(MacKeys macKeys) {
        mAuth = Auth.getAuthInstance(macKeys);
        mOkHttpClient = new OkHttpClient();
    }

    // Create a new stream
    public Stream createStream(String hubName, String title, String publishKey, String publishSecurity) throws PiliException {
        if (hubName == null) {
            throw new PiliException("FATAL EXCEPTION: hubName is null!");
        }

        String urlStr = API_BASE_URL + "/streams";
        System.out.println(urlStr);
        JsonObject json = new JsonObject();
        json.addProperty("hub", hubName);
        if (title != null) {
            json.addProperty("title", title);
        }
        if (publishKey != null) {
            json.addProperty("publishKey", publishKey);
        }
        if (publishSecurity != null) {
            json.addProperty("publishSecurity", publishSecurity);
        }
        Response response = null;
        try {
            URL url = new URL(urlStr);
            
            String contentType = "application/json";
            byte[] body = json.toString().getBytes(Config.UTF8);
            String macToken = mAuth.signRequest(url, "POST", body, contentType);
            RequestBody rBody = RequestBody.create(MediaType.parse(contentType), body);
            Request request = new Request.Builder()
            .url(url)
            .post(rBody)
            .header("User-Agent", Utils.getUserAgent())
            .addHeader("Authorization", macToken)
            .addHeader("Content-Type", contentType)
            .build();

            response = mOkHttpClient.newCall(request).execute();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

        // response never be null
        if (response.isSuccessful()) {
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
                return new Stream(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Get an exist stream
    public Stream getStream(String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException("FATAL EXCEPTION: streamId is null!");
        }
        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);
            
            String macToken = mAuth.signRequest(url, "GET", null, null);
            Request request = new Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", Utils.getUserAgent())
            .addHeader("Authorization", macToken)
            .build();

            response = mOkHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // response never be null
        if (response.isSuccessful()) {
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
                return new Stream(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // List stream
    public StreamList listStreams(String hubName, String startMarker, long limitCount) throws PiliException {
        if (hubName == null) {
            throw new PiliException("FATAL EXCEPTION: streamId is null!");
        }
        try {
            hubName = URLEncoder.encode(hubName, Config.UTF8);
            if (startMarker != null && !" ".equals(startMarker)) {
                startMarker = URLEncoder.encode(startMarker, Config.UTF8);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
        String urlStr = null;
        
        if (startMarker != null && !"".equals(startMarker) && limitCount != 0) {
            urlStr = String.format("%s/streams?hub=%s&marker=%s&limit=%d", API_BASE_URL, hubName, startMarker, limitCount);
        } else if (startMarker == null || "".equals(startMarker) || " ".equals(startMarker)) {
            urlStr = String.format("%s/streams?hub=%s&limit=%d", API_BASE_URL, hubName, limitCount);
        } else if (limitCount == 0) {
            urlStr = String.format("%s/streams?hub=%s&marker=%s", API_BASE_URL, hubName, startMarker);
        } else {
            System.out.println("never go here!");
        }
        Response response = null;
        try {
            URL url = new URL(urlStr);
            String macToken = mAuth.signRequest(url, "GET", null, null);
            Request request = new Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", Utils.getUserAgent())
            .header("Authorization", macToken)
            .build();
            response = mOkHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

        // response never be null
        if (response.isSuccessful()) {
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
                return new StreamList(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Update an exist stream
    public Stream updateStream(String streamId, String publishKey, String publishSecurity) throws PiliException {
        if (streamId == null) {
            throw new PiliException("FATAL EXCEPTION: streamId is null!");
        }
        
        JsonObject json = new JsonObject();
        if (publishKey != null) {
            json.addProperty("publishKey", publishKey);
        }
        if (publishSecurity != null) {
            json.addProperty("publishSecurity", publishSecurity);
        }
        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            byte body[] = json.toString().getBytes(Config.UTF8);
            URL url = new URL(urlStr);

            String contentType = "application/json";
            String macToken = mAuth.signRequest(url, "POST", body, contentType);
            MediaType type = MediaType.parse(contentType);
            RequestBody rBody = RequestBody.create(type, body);
            Request request = new Request.Builder()
            .post(rBody)
            .url(url)
            .header("User-Agent", Utils.getUserAgent())
            .addHeader("Authorization", macToken)
            .build();

            response = mOkHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

        // response never be null
        if (response.isSuccessful()) {
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
                return new Stream(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Delete stream
    public String deleteStream(String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException("FATAL EXCEPTION: streamId is null!");
        }
        
        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);
            
            String macToken = mAuth.signRequest(url, "DELETE", null, null);
            Request request = new Request.Builder()
            .url(url)
            .delete()
            .header("User-Agent", Utils.getUserAgent())
            .header("Authorization", macToken)
            .build();

            response = mOkHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

        // response never be null
        if (response.isSuccessful()) {
            return new String(response.message());
        } else {
            throw new PiliException(response);
        }
    }

    // Get recording segments from an exist stream
    public StreamSegmentList getStreamSegments(String streamId, long startTime, long endTime) throws PiliException {
        if (streamId == null) {
            throw new PiliException("FATAL EXCEPTION: streamId is null!");
        }
        String urlStr = String.format("%s/streams/%s/segments?start=%d&end=%d", API_BASE_URL, streamId, startTime, endTime);

        Response response = null;
        try {
            URL url = new URL(urlStr);
            String macToken = mAuth.signRequest(url, "GET", null, null);
            Request request = new Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", Utils.getUserAgent())
            .addHeader("Authorization", macToken)
            .build();

            response = mOkHttpClient.newCall(request).execute();
           
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

        // response never be null
        if (response.isSuccessful()) {
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
                System.out.println(jsonObj);
                System.out.println(jsonObj.get("segments"));
                if (jsonObj.get("segments") instanceof JsonNull) {
                    throw new PiliException(response);
                }
                return new StreamSegmentList(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

}
