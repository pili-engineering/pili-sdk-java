package com.pili;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pili.Stream.*;
import com.pili.common.Config;
import com.pili.common.MessageConfig;
import com.pili.common.Utils;
import com.qiniu.Credentials;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

public final class API {
    private static final String API_BASE_URL = String.format("%s://%s/%s",
            Configuration.getInstance().USE_HTTPS ? "https" : "http",
            Configuration.getInstance().API_HOST,
            Configuration.getInstance().API_VERSION);

    private static final String RTC_BASE_URL = String.format("%s://%s/%s",
            Configuration.getInstance().USE_HTTPS ? "https" : "http",
            Configuration.getInstance().RTC_HOST,
            Configuration.getInstance().RTC_VERSION);

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    private API() {
    }

    // Create a new stream
    public static Stream createStream(Credentials credentials, String hubName, String title, String publishKey,
                                      String publishSecurity) throws PiliException {
//        System.out.println("createStream:" + API_BASE_URL);
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
            String macToken = credentials.signRequest(url, "POST", body, contentType);
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
                return new Stream(jsonObj, credentials);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Get an exist stream
    public static Stream getStream(Credentials credentials, String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);

            String macToken = credentials.signRequest(url, "GET", null, null);
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
                return new Stream(jsonObj, credentials);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // List stream
    public static StreamList listStreams(Credentials credentials, String hubName, String status,
                                         String startMarker, long limitCount, String titlePrefix) throws PiliException {
        try {
            hubName = URLEncoder.encode(hubName, Config.UTF8);
            if (Utils.isArgNotEmpty(status)) {
                if ("connected".equals(status)) {
                    status = URLEncoder.encode(status, Config.UTF8);
                } else {
                    throw new PiliException("status can only be connected");
                }
            }
            if (Utils.isArgNotEmpty(startMarker)) {
                startMarker = URLEncoder.encode(startMarker, Config.UTF8);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
        String urlStr = String.format("%s/streams?hub=%s", API_BASE_URL, hubName);
        if (Utils.isArgNotEmpty(status)) {
            urlStr += "&status=" + status;
        }
        if (Utils.isArgNotEmpty(startMarker)) {
            urlStr += "&marker=" + startMarker;
        }
        if (limitCount > 0) {
            urlStr += "&limit=" + limitCount;
        }
        if (Utils.isArgNotEmpty(titlePrefix)) {
            urlStr += "&title=" + titlePrefix;
        }
        Response response = null;
        try {
            URL url = new URL(urlStr);
            String macToken = credentials.signRequest(url, "GET", null, null);
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
                return new StreamList(jsonObj, credentials);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // get stream status
    public static Status getStreamStatus(Credentials credentials, String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        String urlStr = String.format("%s/streams/%s/status", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);

            String macToken = credentials.signRequest(url, "GET", null, null);
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
    public static Stream updateStream(Credentials credentials, String streamId, String publishKey,
                                      String publishSecurity, boolean disabled) throws PiliException {
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
            byte[] body = json.toString().getBytes(Config.UTF8);
            URL url = new URL(urlStr);

            String contentType = "application/json";
            String macToken = credentials.signRequest(url, "POST", body, contentType);
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
                return new Stream(jsonObj, credentials);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    public static Hub.StreamStatus[] batchStreamStatuses(Credentials credentials, Hub.StreamStatusesArgs args) throws PiliException{
        String urlStr = String.format("%s/statuses", API_BASE_URL);

        Gson gson = new Gson();
        String reqJson = gson.toJson(args);

        Response response = null;
        try {
            byte[] body = reqJson.toString().getBytes(Config.UTF8);
            URL url = new URL(urlStr);

            String contentType = "application/json";
            String macToken = credentials.signRequest(url, "POST", body, contentType);
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
            try {
                Hub.StreamStatuses ret = gson.fromJson(response.body().string(),  Hub.StreamStatuses.class);
                return ret.items;
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }

    }

    // Delete stream
    public static String deleteStream(Credentials credentials, String streamId) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }

        String urlStr = String.format("%s/streams/%s", API_BASE_URL, streamId);
        Response response = null;
        try {
            URL url = new URL(urlStr);

            String macToken = credentials.signRequest(url, "DELETE", null, null);
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

    public static SaveAsResponse saveAs(Credentials credentials, String streamId, String fileName, String format,
                                        long start, long end, String notifyUrl, String pipeline) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }

        if (!Utils.isArgNotEmpty(fileName)) {
            throw new PiliException(MessageConfig.ILLEGAL_FILE_NAME_EXCEPTION_MSG);
        }

        if (start <= 0 || end <= 0 || start > end) {
            throw new PiliException(MessageConfig.ILLEGAL_TIME_MSG);
        }

        String urlStr = String.format("%s/streams/%s/saveas", API_BASE_URL, streamId);
        Response response = null;
        JsonObject json = new JsonObject();
        json.addProperty("name", fileName);
        if (Utils.isArgNotEmpty(notifyUrl)) {
            json.addProperty("notifyUrl", notifyUrl);
        }
        if (Utils.isArgNotEmpty(pipeline)) {
            json.addProperty("pipeline", pipeline);
        }
        json.addProperty("start", start);
        json.addProperty("end", end);

        if (Utils.isArgNotEmpty(format)) {
            json.addProperty("format", format);
        }

        try {
            URL url = new URL(urlStr);

            String contentType = "application/json";
            byte[] body = json.toString().getBytes(Config.UTF8);
            String macToken = credentials.signRequest(url, "POST", body, contentType);
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
        }
        // response never be null
        if (response.isSuccessful()) {
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
//                System.out.println(jsonObj.toString());
                return new SaveAsResponse(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    public static SnapshotResponse snapshot(Credentials credentials, String streamId, String fileName, String format,
                                            long time, String notifyUrl) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }

        if (!Utils.isArgNotEmpty(fileName)) {
            throw new PiliException(MessageConfig.ILLEGAL_FILE_NAME_EXCEPTION_MSG);
        }

        if (!Utils.isArgNotEmpty(format)) {
            throw new PiliException(MessageConfig.ILLEGAL_FORMAT_EXCEPTION_MSG);
        }

        String urlStr = String.format("%s/streams/%s/snapshot", API_BASE_URL, streamId);
        Response response = null;
        JsonObject json = new JsonObject();
        json.addProperty("name", fileName);
        json.addProperty("format", format);
        if (time > 0) {
            json.addProperty("time", time);
        }
        if (Utils.isArgNotEmpty(notifyUrl)) {
            json.addProperty("notifyUrl", notifyUrl);  // optional
        }

        try {
            URL url = new URL(urlStr);

            String contentType = "application/json";
            byte[] body = json.toString().getBytes(Config.UTF8);
            String macToken = credentials.signRequest(url, "POST", body, contentType);
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
        }
        // response never be null
        if (response.isSuccessful()) {
            JsonParser parser = new JsonParser();
            try {
                JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
                System.out.println(jsonObj.toString());
                return new SnapshotResponse(jsonObj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    // Get recording segments from an exist stream
    public static SegmentList getStreamSegments(Credentials credentials, String streamId, long startTime, long endTime,
                                                int limitCount) throws PiliException {
        if (streamId == null) {
            throw new PiliException(MessageConfig.NULL_STREAM_ID_EXCEPTION_MSG);
        }
        String urlStr = String.format("%s/streams/%s/segments", API_BASE_URL, streamId);
        if (startTime > 0 && endTime > 0 && startTime < endTime) {
            urlStr += "?start=" + startTime + "&end=" + endTime;
        }
        if (limitCount > 0) {
            urlStr += "&limit=" + limitCount;
        }
        Response response = null;
        try {
            URL url = new URL(urlStr);
            String macToken = credentials.signRequest(url, "GET", null, null);
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
    public static String publishUrl(Stream stream, long nonce, String accessKey, String secretKey)
            throws PiliException {
        final String defaultScheme = "rtmp";
        if ("dynamic".equals(stream.getPublishSecurity())) {
            return generateDynamicUrl(stream, nonce, defaultScheme, accessKey, secretKey);
        } else if ("static".equals(stream.getPublishSecurity())) {
            return generateStaticUrl(stream, defaultScheme);
        } else {
            // "dynamic" as default
            return generateDynamicUrl(stream, nonce, defaultScheme, accessKey, secretKey);
        }
    }

    //Generate RTMP live play URL
    public static Map<String, String> rtmpLiveUrl(Stream stream) {
        final String defaultScheme = "rtmp";
        final String url = String.format("%s://%s/%s/%s", defaultScheme, stream.getLiveRtmpHost(), stream.getHubName(),
                stream.getTitle());
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(Stream.ORIGIN, url);
        String[] profiles = stream.getProfiles();
        if (profiles != null) {
            for (String p : profiles) {
                dictionary.put(p, url + '@' + p);
            }
        }
        return dictionary;
    }

    //Generate HLS live play URL
    public static Map<String, String> hlsLiveUrl(Stream stream) {
        final String defaultScheme = "http";
        final String url = String.format("%s://%s/%s/%s", defaultScheme, stream.getLiveHlsHost(), stream.getHubName(),
                stream.getTitle());
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(Stream.ORIGIN, url + ".m3u8");
        String[] profiles = stream.getProfiles();
        if (profiles != null) {
            for (String p : profiles) {
                dictionary.put(p, url + '@' + p + ".m3u8");
            }
        }
        return dictionary;
    }

    //Generate HLS playback URL
    public static Map<String, String> hlsPlaybackUrl(Credentials credentials, Stream stream,
                                                     long startTime, long endTime)
            throws PiliException {
        final SaveAsResponse response = saveAs(credentials, stream.getStreamId(),
                "" + System.currentTimeMillis() / 1000, null, startTime, endTime, null, null);
        Map<String, String> dictionary = new HashMap<String, String>();
        if (response != null) {
            dictionary.put(Stream.ORIGIN, response.getUrl());
        }
        return dictionary;
    }

    public static Map<String, String> httpFlvLiveUrl(Stream stream) {
        /*
         * http://liveHttpFlvHost/hub/title@480p.flv
         */
        final String defaultScheme = "http";
        final String url = String.format("%s://%s/%s/%s", defaultScheme, stream.getLiveHdlHost(), stream.getHubName(),
                stream.getTitle());
        Map<String, String> dictionary = new HashMap<String, String>();
        dictionary.put(Stream.ORIGIN, url + ".flv");
        String[] profiles = stream.getProfiles();
        if (profiles != null) {
            for (String p : profiles) {
                dictionary.put(p, url + '@' + p + ".flv");
            }
        }
        return dictionary;
    }

    private static String generateStaticUrl(Stream stream, String scheme) {
        return String.format("%s://%s/%s/%s?key=%s", scheme, stream.getPublishRtmpHost(), stream.getHubName(),
                stream.getTitle(), stream.getPublishKey());
    }

    private static String generateDynamicUrl(Stream stream, long nonce, String scheme, String accessKey, String secretKey) throws PiliException {
        if (nonce <= 0) {
            nonce = System.currentTimeMillis() / 1000 + 60 * 60; // the unit should be second
        }
        final String baseUri = "/" + stream.getHubName() + "/" + stream.getTitle() + "?e=" + nonce;
        String publishToken = null;
        try {
            publishToken = Credentials.sign(secretKey, baseUri);
        } catch (SignatureException e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
        return String.format("%s://%s%s&token=%s:%s", scheme, stream.getPublishRtmpHost(), baseUri, accessKey, publishToken);
    }

    //----------------------------rtc
    public static String createRoom(Credentials credentials, Meeting.CreateArgs args) throws PiliException {
        if (args.ownerId == null) {
            throw new PiliException(MessageConfig.ILLEGAL_OWNER_ID);
        }
        Gson gson = new Gson();

        String json = gson.toJson(args);
        String urlStr = String.format("%s/rooms", RTC_BASE_URL);
        Response response = null;
        try {
            byte[] body = json.toString().getBytes(Config.UTF8);
            URL url = new URL(urlStr);

            String contentType = "application/json";
            String macToken = credentials.signRequest(url, "POST", body, contentType);
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
            try {
                Meeting.RoomName ret = gson.fromJson(response.body().string(), Meeting.RoomName.class);
                return ret.roomName;
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    public static Meeting.Room getRoom(Credentials credentials, String room) throws PiliException {
        if (room == null) {
            throw new PiliException(MessageConfig.ILLEGAL_ROOM);
        }
        Gson gson = new Gson();

        String urlStr = String.format("%s/rooms/%s", RTC_BASE_URL, room);
        Response response = null;
        try {
            URL url = new URL(urlStr);

            String macToken = credentials.signRequest(url, "GET", null, null);
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
            try {
                Meeting.Room ret = gson.fromJson(response.body().string(), Meeting.Room.class);
                return ret;
            } catch (IOException e) {
                e.printStackTrace();
                throw new PiliException(e);
            }
        } else {
            throw new PiliException(response);
        }
    }

    public static void deleteRoom(Credentials credentials, String room) throws PiliException {
        if (room == null) {
            throw new PiliException(MessageConfig.ILLEGAL_ROOM);
        }
        Gson gson = new Gson();

        String urlStr = String.format("%s/rooms/%s", RTC_BASE_URL, room);
        Response response = null;
        try {
            URL url = new URL(urlStr);

            String macToken = credentials.signRequest(url, "DELETE", null, null);
            Request request = new Request.Builder()
                    .url(url)
                    .delete()
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
            return;
        } else {
            throw new PiliException(response);
        }
    }
}
