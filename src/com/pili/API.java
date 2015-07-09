package com.pili;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pili.Stream.SegmentList;
import com.pili.Stream.Status;
import com.pili.Stream.StreamList;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import common.Config;
import common.MessageConfig;
import common.Utils;

public class API {
    private static final String API_BASE_URL = 
            String.format("http://%s/%s", Config.DEFAULT_API_HOST, Config.API_VERSION);

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    // Create a new stream
    public static Stream createStream(Auth auth, String hubName, String title, String publishKey, String publishSecurity) throws PiliException {
        String urlStr = API_BASE_URL + "/streams";
        JsonObject json = new JsonObject();
        json.addProperty("hub", hubName);
        if (Utils.isArgNotEmpty(title)) {
            if (title.length() < Config.TITLE_MIN_LENGTH || title.length() > Config.TITLE_MAX_LENGTH) {
                throw new PiliException(MessageConfig.ILLEGAL_TITLE_MSG);
            }
            json.addProperty("title", title);
        }
        if (Utils.isArgNotEmpty(publishKey)) {
            json.addProperty("publishKey", publishKey);
        }
        if (Utils.isArgNotEmpty(publishSecurity)) {
            json.addProperty("publishSecurity", publishSecurity);
        }
        Response response = null;
        try {
            URL url = new URL(urlStr);
            
            String contentType = "application/json";
            byte[] body = json.toString().getBytes(Config.UTF8);
            String macToken = auth.signRequest(url, "POST", body, contentType);
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
                return new Stream(jsonObj, auth);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Get an exist stream
    public static Stream getStream(Auth auth, String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);
            
            String macToken = auth.signRequest(url, "GET", null, null);
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
                return new Stream(jsonObj, auth);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // List stream
    public static StreamList listStreams(Auth auth, String hubName, String startMarker, long limitCount) throws PiliException {
        try {
            hubName = URLEncoder.encode(hubName, Config.UTF8);
            if (Utils.isArgNotEmpty(startMarker)) {
                startMarker = URLEncoder.encode(startMarker, Config.UTF8);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
        String urlStr = String.format("%s/streams?hub=%s", API_BASE_URL, hubName);
        if (Utils.isArgNotEmpty(startMarker)) {
            urlStr += "&marker=" + startMarker;
        }
        if (limitCount > 0) {
            urlStr += "&limit=" + limitCount;
        }
        Response response = null;
        try {
            URL url = new URL(urlStr);
            String macToken = auth.signRequest(url, "GET", null, null);
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

    // get stream status
    public static Status getStreamStatus(Auth auth, String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        String urlStr = String.format("%s/streams/%s/status", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);

            String macToken = auth.signRequest(url, "GET", null, null);
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
                return new Status(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Update an exist stream
    public static Stream updateStream(Auth auth, String streamId, String publishKey, String publishSecurity, boolean disabled) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        
        JsonObject json = new JsonObject();
        if (Utils.isArgNotEmpty(publishKey)) {
            json.addProperty("publishKey", publishKey);
        }
        if (Utils.isArgNotEmpty(publishSecurity)) {
            json.addProperty("publishSecurity", publishSecurity);
        }
        json.addProperty("disabled", disabled);
        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            byte body[] = json.toString().getBytes(Config.UTF8);
            URL url = new URL(urlStr);

            String contentType = "application/json";
            String macToken = auth.signRequest(url, "POST", body, contentType);
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
                return new Stream(jsonObj, auth);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Delete stream
    public static String deleteStream(Auth auth, String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        
        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);
            
            String macToken = auth.signRequest(url, "DELETE", null, null);
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
    public static SegmentList getStreamSegments(Auth auth, String streamId, long startTime, long endTime) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        String urlStr = String.format("%s/streams/%s/segments", API_BASE_URL, streamId);
        if (startTime > 0 && endTime > 0 && startTime < endTime) {
            urlStr += "?start=" + startTime + "&end=" + endTime;
        }
        Response response = null;
        try {
            URL url = new URL(urlStr);
            String macToken = auth.signRequest(url, "GET", null, null);
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
                if (jsonObj.get("segments") instanceof JsonNull) {
                    throw new PiliException("Segments is null");
                }
                return new SegmentList(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    //Generate a RTMP publish URL
    public static String publishUrl(String rtmpPubHost, String streamId, String publishKey, String publishSecurity, long nonce) 
            throws PiliException {
        if (!Utils.isArgNotEmpty(rtmpPubHost)) {
            throw new PiliException(MessageConfig.ILLEGAL_RTMP_PUBLISH_URL_MSG);
        }
        final String defaultScheme = "rtmp";
        if ("dynamic".equals(publishSecurity)) {
            return generateDynamicUrl(rtmpPubHost, streamId, publishKey, nonce, defaultScheme);
        } else if ("static".equals(publishSecurity)) {
            return generateStaticUrl(rtmpPubHost, streamId, publishKey, defaultScheme);
        } else {
            // "dynamic" as default 
            return generateDynamicUrl(rtmpPubHost, streamId, publishKey, nonce, defaultScheme);
        }
    }

    //Generate RTMP live play URL
    public static Map<String, String> rtmpLiveUrl(String rtmpPlayHost, String streamId, String[] profiles) {
        final String defaultScheme = "rtmp";
        String baseUri = Utils.getPath(streamId);
        String url = defaultScheme + "://" + rtmpPlayHost + baseUri;
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(Stream.ORIGIN, url);
        for (String p : profiles) {
            dictionary.put(p, url + '@' + p);
        }
        return dictionary;
    }

    //Generate HLS live play URL
    public static Map<String, String> hlsLiveUrl(String hlsPlayHost, String streamId, String[] profiles) {
        final String defaultScheme = "http";
        String baseUri = Utils.getPath(streamId);
        final String url = defaultScheme + "://" + hlsPlayHost + baseUri;
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(Stream.ORIGIN, url + ".m3u8");
        for (String p : profiles) {
            dictionary.put(p, url + '@' + p + ".m3u8");
        }
        return dictionary;
    }

    //Generate HLS playback URL
    public static Map<String, String> hlsPlaybackUrl(String hlsPlayHost, String streamId, long startTime, long endTime, String[] profiles) 
            throws PiliException {
        final String defaultScheme = "http";
        String baseUri = Utils.getPath(streamId);
        final String url = defaultScheme + "://" + hlsPlayHost + baseUri;
        String queryPara = null;
        if (startTime > 0 && endTime > 0 && startTime < endTime) {
            queryPara = "?start=" +startTime + "&end=" +endTime;
        } else {
            throw new PiliException(MessageConfig.ILLEGAL_TIME_MSG);
        }
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(Stream.ORIGIN, url + ".m3u8" + queryPara);
        for (String p : profiles) {
            dictionary.put(p, url + '@' + p + ".m3u8" + queryPara);
        }
        return dictionary;
    }

    private static String generateStaticUrl(String rtmpPubHost, String streamId, String publishKey, String scheme) {
        return String.format("%s://%s%s?key=%s", scheme, rtmpPubHost, Utils.getPath(streamId), publishKey);
    }

    private static String generateDynamicUrl(String rtmpPubHost, String streamId, String publishKey, long nonce, String scheme) throws PiliException {
        if (nonce <= 0) {
            nonce = System.currentTimeMillis();
        }
        String baseUri = Utils.getPath(streamId) + "?nonce=" + nonce;
        String publishToken = null;
        try {
            publishToken = Auth.sign(publishKey, baseUri);
        } catch (SignatureException e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
        return String.format("%s://%s%s&token=%s", scheme, rtmpPubHost, baseUri, publishToken);
    }
}
