package com.pili;

import com.pili.Stream.StreamList;
import com.pili.common.MessageConfig;
import com.qiniu.Credentials;

public final class Hub {

    private Credentials mCredentials;
    private String mHubName;

    public Hub(Credentials credentials, final String hubName) {
        if (hubName == null) {
            throw new IllegalArgumentException(MessageConfig.NULL_HUBNAME_EXCEPTION_MSG);
        }
        if (credentials == null) {
            throw new IllegalArgumentException(MessageConfig.NULL_CREDENTIALS_EXCEPTION_MSG);
        }
        mCredentials = credentials;
        mHubName = hubName;
    }

    public Stream createStream() throws PiliException {
        return API.createStream(mCredentials, mHubName, null, null, null);
    }

    public Stream createStream(String title, String publishKey, String publishSecurity) throws PiliException {
        return API.createStream(mCredentials, mHubName, title, publishKey, publishSecurity);
    }

    public Stream getStream(String streamId) throws PiliException {
        return API.getStream(mCredentials, streamId);
    }

    public StreamList listStreams() throws PiliException {
        return API.listStreams(mCredentials, mHubName, null, null, 0, null);
    }

    public StreamList listStreams(String marker, long limit) throws PiliException {
        return API.listStreams(mCredentials, mHubName, null, marker, limit, null);
    }

    public StreamList listStreams(String marker, long limit, String titlePrefix) throws PiliException {
        return API.listStreams(mCredentials, mHubName, null, marker, limit, titlePrefix);
    }

    // status can only be connected
    public StreamList listStreams(String status, String marker, long limit, String titlePrefix) throws PiliException {
        return API.listStreams(mCredentials, mHubName, status, marker, limit, titlePrefix);
    }

    //-----------
    static class StreamStatuses {
        public StreamStatus[] items;
    }
    public static class StreamStatus {
        public String streamId;
        public String addr;
        public String startFrom;
        public String updatedAt;
        public Float bytesPerSecond;
        public Fps framesPerSecond;
        public String reqId;
        public String status;
    }
    public static class Fps {
        public Float audio;
        public Float video;
        public Float data;
    }

    static class StreamStatusesArgs {
        public String[] streamIds;
        StreamStatusesArgs(String[] ids) {
            this.streamIds = ids;
        }
    }

    public StreamStatus[] batchStreamStatuses(String... streamIds) throws PiliException{
        return API.batchStreamStatuses(mCredentials, new StreamStatusesArgs(streamIds));
    }

}
