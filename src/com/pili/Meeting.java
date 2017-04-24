package com.pili;

import java.util.Date;
import com.pili.common.MessageConfig;
import com.qiniu.Credentials;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Meeting {
    private Credentials mCredentials;

    public Meeting(Credentials credentials) {

        if (credentials == null) {
            throw new IllegalArgumentException(MessageConfig.NULL_CREDENTIALS_EXCEPTION_MSG);
        }
        mCredentials = credentials;
    }

    public String createRoom(String ownerId, String roomName) throws PiliException {
        return API.createRoom(this.mCredentials, new CreateArgs(ownerId, roomName));
    }

    public String createRoom(String ownerId) throws PiliException {
        return API.createRoom(this.mCredentials, new CreateArgs(ownerId));
    }

    public Room getRoom(String roomName) throws PiliException {
        return API.getRoom(this.mCredentials, roomName);
    }

    public void deleteRoom(String roomName) throws PiliException {
        API.deleteRoom(this.mCredentials, roomName);
    }

    public String roomToken(String roomName, String userId, String perm, Date expireAt) throws Exception {
        RoomAccess access = new RoomAccess(roomName, userId, perm, expireAt);
        String json = new Gson().toJson(access);
        return this.mCredentials.signRoomToken(json);
    }

    private class RoomAccess {
        @SerializedName("room_name")
        String roomName;
        @SerializedName("user_id")
        String userId;
        @SerializedName("perm")
        String perm;
        @SerializedName("expire_at")
        long expireAt;

        RoomAccess(String roomName, String userId, String perm, Date expireAt) {
            this.roomName = roomName;
            this.userId = userId;
            this.perm = perm;
            this.expireAt = expireAt.getTime() / 1000; // nanoseconds
        }
    }

    static class CreateArgs {
        @SerializedName("owner_id") String ownerId;
        @SerializedName("room_name") String room;

        public CreateArgs(String ownerId, String room) {
            this.ownerId = ownerId;
            this.room = room;
        }

        public CreateArgs(String ownerId) {
            this.ownerId = ownerId;
        }
    }

    static class RoomName {
        @SerializedName("room_name")
        String roomName;
    }

    public static enum RoomStatus {
        @SerializedName("0")
        NEW,
        @SerializedName("1")
        MEETING,
        @SerializedName("2")
        FINISHED,
    }

    public static class Room {
        @SerializedName("room_name")
        public String name;
        @SerializedName("room_status")
        public RoomStatus status;
        @SerializedName("owner_id")
        public String ownerId;
    }
}
