package com.pili;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SignatureException;
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
        if (isArgNotEmpty(title)) {
            json.addProperty("title", title);
        }
        if (isArgNotEmpty(publishKey)) {
            json.addProperty("publishKey", publishKey);
        }
        if (isArgNotEmpty(publishSecurity)) {
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
            if (isArgNotEmpty(startMarker)) {
                startMarker = URLEncoder.encode(startMarker, Config.UTF8);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
        String urlStr = String.format("%s/streams?hub=%s", API_BASE_URL, hubName);
        if (isArgNotEmpty(startMarker)) {
            urlStr += "&marker=" + startMarker;
        }
        if (limitCount > 0) {
            urlStr += "&limit=%d" + limitCount;
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
        if (isArgNotEmpty(publishKey)) {
            json.addProperty("publishKey", publishKey);
        }
        if (isArgNotEmpty(publishSecurity)) {
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
        String urlStr = String.format("%s/streams/%s/segments", API_BASE_URL, streamId);
        if (startTime > 0 && endTime > 0 && startTime < endTime) {
            urlStr += "?start=" + startTime + "&end=" + endTime;
        } else {
            throw new PiliException("Illegal startTime or endTime!");
        }
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
                    throw new PiliException("Segments is null");
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

    //Generate a RTMP publish URL
    public String publishUrl(String streamId, String publishKey, String publishSecurity, long nonce) throws PiliException {
        final String defaultScheme = "rtmp";
        if ("dynamic".equals(publishSecurity)) {
            return generateDynamicUrl(streamId, publishKey, nonce, defaultScheme);
        } else if ("static".equals(publishSecurity)) {
            return generateStaticUrl(streamId, publishKey, defaultScheme);
        } else {
            throw new PiliException("Illegal publishSecurity:" + publishSecurity);
        }
    }

    //Generate RTMP live play URL
    public String rtmpLiveUrl(String rtmpPlayHost, String streamId, String preset) {
        final String defaultScheme = "rtmp";
        String baseUri = getPath(streamId);
        String url = defaultScheme + "://" + rtmpPlayHost + baseUri;
        if (isArgNotEmpty(preset)) {
            url += '@' + preset;
        }
        return url;
    }

    //Generate HLS live play URL
    public String hlsLiveUrl(String hlsPlayHost, String streamId, String preset) {
        final String defaultScheme = "http";
        String baseUri = getPath(streamId);
        String url = defaultScheme + "://" + hlsPlayHost + baseUri;
        if (isArgNotEmpty(preset)) {
            url += '@' + preset;
        }
        url += ".m3u8";
        return url;
    }

    //Generate HLS playback URL
    public String hlsPlaybackUrl(String hlsPlayHost, String streamId, long startTime, long endTime, String preset) 
            throws PiliException {
        final String defaultScheme = "http";
        String baseUri = getPath(streamId);
        String url = defaultScheme + "://" + hlsPlayHost + baseUri;
        if (isArgNotEmpty(preset)) {
            url += '@' + preset;
        }
        url += ".m3u8";
        if (startTime > 0 && endTime > 0 && startTime < endTime) {
            url += "?start=" +startTime + "&end=" +endTime;
        } else {
            throw new PiliException("Illegal startTime or endTime!");
        }
        return url;
    }

    private String getPath(String streamId) {
        String[] res = streamId.split("\\.");
        // res[1] -> hub, res[2] -> title
        return String.format("/%s/%s", res[1], res[2]);
    }

    private String generateStaticUrl(String streamId, String publishKey, String scheme) {
        return String.format("%s://%s%s?key=%s", scheme, Config.DEFAULT_RTMP_PUBLISH_HOST, getPath(streamId), publishKey);
    }

    private String generateDynamicUrl(String streamId, String publishKey, long nonce, String scheme) throws PiliException {
        if (nonce <= 0) {
            nonce = System.currentTimeMillis();
        }
        String baseUri = getPath(streamId) + "?nonce=" + nonce;
        String publishToken = null;
        try {
            publishToken = Auth.sign(publishKey, baseUri);
        } catch (SignatureException e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
        return String.format("%s://%s%s&token=%s", scheme, Config.DEFAULT_RTMP_PUBLISH_HOST, baseUri, publishToken);
    }

    /*
     * check the arg.
     * 1. arg == null, return false, treat as empty situation
     * 2. arg == "", return false, treat as empty situation
     * 3. arg == " " or arg == "   ", return false, treat as empty situation
     * 4. return true, only if the arg is a illegal string
     *
     * */
    private boolean isArgNotEmpty(String arg) {
        return arg != null && !arg.trim().isEmpty();
    }
}
