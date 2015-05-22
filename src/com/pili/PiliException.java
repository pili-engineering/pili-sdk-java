package com.pili;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Response;

public class PiliException extends Exception {
    public final Response response;

    private String mDetails = null;

    public PiliException(Response response) {
        this.response = response;
        JsonParser parser = new JsonParser();
        try {
            JsonObject jsonObj = parser.parse(response.body().string()).getAsJsonObject();
            mDetails = jsonObj.toString();
        } catch (IOException e) {
            e.printStackTrace();
            mDetails += e.getMessage();
        }
    }

    public PiliException(String msg) {
        super(msg);
        response = null;
    }

    public PiliException(Exception e) {
        super(e);
        this.response = null;
    }

    public int code() {
        return response == null ? -1 : response.code();
    }

    public String getMessage() {
        return response == null ? super.getMessage() : response.message();
    }

    public String getDetails() {
        return mDetails;
    }
}
