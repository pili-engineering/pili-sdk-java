package com.qiniu.pili;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Meeting {
    private String baseUrl;
    private Gson gson;
    private RPC cli;

    private Meeting(){}

    Meeting(RPC cli ) {
        this.baseUrl = Config.APIHTTPScheme + Config.RTCAPIHost + "/v1";
        this.cli = cli;
        this.gson = new Gson();
    }

    public String createRoom(String ownerId ,String roomName, int userMax) throws PiliException{
        CreateArgs args = new CreateArgs(ownerId, roomName, userMax);
        return createRoom(args);
    }

    public String createRoom(String ownerId ,String roomName) throws PiliException{
        CreateArgs args = new CreateArgs(ownerId, roomName);
        return createRoom(args);
    }

    public String createRoom(String ownerId) throws PiliException{
        CreateArgs args = new CreateArgs(ownerId);
        return createRoom(args);
    }

    private String createRoom(CreateArgs args) throws PiliException{
        String path = this.baseUrl + "/rooms";
        String json = gson.toJson(args);

        try {
            String resp = cli.callWithJson(path, json);
            RoomName ret = gson.fromJson(resp, RoomName.class);
            return ret.roomName;
        }catch (PiliException e){
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
    }

    public Room getRoom(String roomName) throws PiliException{
        String path = this.baseUrl+ "/rooms/" + roomName;
        try {
            String resp = cli.callWithGet(path);
            Room room = gson.fromJson(resp, Room.class);
            return room;
        }catch (PiliException e){
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
    }

    public void deleteRoom(String room) throws PiliException{
        String path = this.baseUrl + "/rooms/"+room;
        try {
            cli.callWithDelete(path);
        }catch (PiliException e){
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
    }

    public String roomToken(String roomName, String userId, String perm, Date expireAt) throws Exception {
        RoomAccess access = new RoomAccess(roomName, userId, perm, expireAt);
        String json = gson.toJson(access);
        return this.cli.getMac().signRoomToken(json);
    }

    private class RoomAccess{
        @SerializedName("room_name")
        String roomName;
        @SerializedName("user_id")
        String userId;
        @SerializedName("perm")
        String perm;
        @SerializedName("expire_at")
        long expireAt;

        RoomAccess(String roomName, String userId, String perm, Date expireAt){
            this.roomName = roomName;
            this.userId = userId;
            this.perm = perm;
            this.expireAt = expireAt.getTime() / 1000;// seconds
        }
    }

    /**
     *
     */
    public enum Status {
        @SerializedName("0")
        NEW,
        @SerializedName("1")
        MEETING,
        @SerializedName("2")
        FINISHED,
    }

    public class Room {
        @SerializedName("room_name") public String name;
        @SerializedName("room_status") public Status status;
        @SerializedName("owner_id") public String ownerId;
        @SerializedName("user_max") public int userMaxe;
    }

    /**
     *
     */
    private class RoomName {
        @SerializedName("room_name") String roomName;
    }

    private class CreateArgs {
        @SerializedName("owner_id") String ownerId;
        @SerializedName("room_name") String room;
        @SerializedName("user_max") int userMax;

        public CreateArgs(String ownerId, String room, int userMax){
            this.ownerId = ownerId;
            this.room = room;
            this.userMax = userMax;
        }

        public CreateArgs(String ownerId, String room) {
            this.ownerId= ownerId;
            this.room = room;
        }
        public CreateArgs(String ownerId){
            this.ownerId = ownerId;
        }
    }

}
