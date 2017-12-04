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
    public String createRoom(String ownerId, String roomName,int userMax) throws PiliException {
        return API.createRoom(this.mCredentials, new CreateArgs(ownerId, roomName,userMax));
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

//    public AllActiveUser activeUsers(String roomName) throws PiliException{
//        return API.getActiveUsers(this.mCredentials,roomName);
//    }

//    public void rejectUser(String roomName, String userId) throws PiliException{
//        API.rejectUser(this.mCredentials, roomName, userId);
//    }

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
        @SerializedName("version")
        String version;

        RoomAccess(String roomName, String userId, String perm, Date expireAt) {
            this.roomName = roomName;
            this.userId = userId;
            this.perm = perm;
            this.expireAt = expireAt.getTime() / 1000; // nanoseconds
            this.version = "2.0";
        }

        RoomAccess(String roomName, String userId, String perm, Date expireAt, String version){
            this.roomName = roomName;
            this.userId = userId;
            this.perm = perm;
            this.expireAt = expireAt.getTime()/1000;
            this.version = version;
        }
    }

    static class CreateArgs {
        @SerializedName("owner_id") String ownerId;
        @SerializedName("room_name") String room;
        @SerializedName("user_max") int userMax;

        public CreateArgs(String ownerId, String room){
            this.ownerId = ownerId;
            this.room = room;
        }

        public CreateArgs(String ownerId, String room, int userMax) {
            this.ownerId = ownerId;
            this.room = room;
            this.userMax = userMax;
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
        @SerializedName("user_max")
        public int userMax;
    }

    public static class ActiveUser{
        @SerializedName("user_id")
        public String UserId;
        @SerializedName("user_name")
        public String UserName;
    }

    public static class AllActiveUser{
        @SerializedName("active_users")
        public ActiveUser[] users;
    }
}
