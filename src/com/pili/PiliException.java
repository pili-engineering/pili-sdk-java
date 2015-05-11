package com.pili;

import com.squareup.okhttp.Response;

public class PiliException extends Exception {
    public final Response response;

    public PiliException(Response response) {
        this.response = response;
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
}
