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

}
