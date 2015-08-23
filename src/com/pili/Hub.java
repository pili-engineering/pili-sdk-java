package com.pili;

import com.pili.Stream.StreamList;
import com.qiniu.Credentials;

import common.MessageConfig;

public class Hub {

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

    public void config(String key, Object value) {
        Configuration.getInstance().setValue(key, value);
        API.config();
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
        return API.listStreams(mCredentials, mHubName, null, 0, null);
    }

    public StreamList listStreams(String marker, long limit) throws PiliException {
        return API.listStreams(mCredentials, mHubName, marker, limit, null);
    }

    public StreamList listStreams(String marker, long limit, String titlePrefix) throws PiliException {
        return API.listStreams(mCredentials, mHubName, marker, limit, titlePrefix);
    }

}
