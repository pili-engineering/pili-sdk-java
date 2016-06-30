package com.qiniu.pili;

import okhttp3.Response;

public class PiliException extends Exception {
    public final Response response;
    private final String ErrNotFound = "not found";
    private final String ErrDuplicate = "already exists";
    private final String ErrNotLive = "stream isn't in live";


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

    public boolean isDuplicate() {
        return code() == 614;
    }

    public boolean isNotFound() {
        return code() == 612;
    }

    public boolean isNotInLive() {
        return code() == 619;
    }

    public String getMessage() {
        if (response == null) {
            return super.getMessage();
        } else {
            switch (code()) {
                case 614:
                    return ErrDuplicate;
                case 612:
                    return ErrNotFound;
                case 619:
                    return ErrNotLive;
                default:
                    return response.message();
            }
        }
    }

}
