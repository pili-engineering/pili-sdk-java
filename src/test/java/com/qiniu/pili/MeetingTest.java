package com.qiniu.pili;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

import static org.junit.Assert.fail;

public class MeetingTest {
    String accessKey;
    String secretKey;
    Client cli;
    Meeting meeting;



    @Before
    public void prepare() {
        // local test environment
//        Config.APIHost = "10.200.20.28:7778";

        accessKey = "";
        secretKey = "";

        cli = new Client(accessKey, secretKey);
        meeting = cli.newMeeting();
    }

    private boolean skip() {
        return Config.RTCAPIHost.equals("rtc.qiniuapi.com");
//        return Config.RTCAPIHost != "rtc.qiniuapi.com";
    }

    @Test
    public void testCreateRoom(){
        // create room with name
        try {
            String roomName = "test12Room";
            String r1 =  meeting.createRoom("123",roomName,12);
            assertEquals(roomName,r1);

            Meeting.Room room = meeting.getRoom(roomName);
            System.out.println("roomName:"+room.name);
            assertEquals(roomName, room.name);
            assertEquals("admin",room.ownerId);
        } catch (PiliException e){
            e.printStackTrace();
//            fail();
        }
    }

    @Test
    public void testRoomDelete(){
        String roomName = "test12Room";
        try {
            meeting.deleteRoom(roomName);
        } catch (PiliException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testRoomToken(){
//        String roomName = "room1";
        try {
            String token = meeting.roomToken("room1", "123", "admin", new Date(1785600000000L));
            assertEquals("7O7hf7Ld1RrC_fpZdFvU8aCgOPuhw2K4eapYOdII:jltpX6P42j2fH3ErOp7Zj7RyaeE=:eyJyb29tX25hbWUiOiJyb29tMSIsInVzZXJfaWQiOiIxMjMiLCJwZXJtIjoiYWRtaW4iLCJleHBpcmVfYXQiOjE3ODU2MDAwMDB9",
                    token);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testActiveUsers(){
        String roomName = "qs";
        try {
            Meeting.AllActiveUsers users = meeting.activeUsers(roomName);
            System.out.println(users.users.length);
            for (int i = 0 ; i < users.users.length; i++){
                System.out.println(users.users[i].userId);
            }
        }catch (PiliException e){
            fail();
        }
    }

    @Test
    public void testRejectUser(){
        String roomName = "qs";
        String userId = "qiniu-1afc2fa3-6e89-4453-8171-1e4bbf628fa2";
        try {
            meeting.rejectUser(roomName, userId);
        }catch (PiliException e){
            fail();
        }
    }
}
