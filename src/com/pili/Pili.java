package com.pili;

import com.pili.Auth.MacKeys;
import com.pili.Stream.StreamList;

import common.MessageConfig;

public class Pili {

    private Auth mAuth;
    private String mHubName;

    public Pili(final String ak, final String sk, final String hubName) {
        MacKeys macKeys = new MacKeys(ak, sk);
        mAuth = new Auth(macKeys);
        if (hubName == null) {
            throw new IllegalArgumentException(MessageConfig.NULL_HUBNAME_EXCEPTION_MSG);
        }
        mHubName = hubName;
    }

    public void config() {
        API.config();
    }

    public Stream createStream() throws PiliException {
        return API.createStream(mAuth, mHubName, null, null, null);
    }

    public Stream createStream(String title, String publishKey, String publishSecurity) throws PiliException {
        return API.createStream(mAuth, mHubName, title, publishKey, publishSecurity);
    }

    public Stream getStream(String streamId) throws PiliException {
        return API.getStream(mAuth, streamId);
    }

    public StreamList listStreams() throws PiliException {
        return API.listStreams(mAuth, mHubName, null, 0, null);
    }

    public StreamList listStreams(String marker, long limit) throws PiliException {
        return API.listStreams(mAuth, mHubName, marker, limit, null);
    }

    public StreamList listStreams(String marker, long limit, String titlePrefix) throws PiliException {
        return API.listStreams(mAuth, mHubName, marker, limit, titlePrefix);
    }

}
