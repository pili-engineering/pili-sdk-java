package com.qiniu.pili;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

import static org.junit.Assert.assertTrue;
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

        accessKey = "7O7hf7Ld1RrC_fpZdFvU8aCgOPuhw2K4eapYOdII";
        secretKey = "6Rq7rMSUHHqOgo0DJjh15tHsGUBEH9QhWqqyj4ka";

        cli = new Client(accessKey, secretKey);
        meeting = cli.newMeeting();
    }

    private boolean skip() {
        return Config.RTCAPIHost != "rtc.qiniuapi.com";
    }

    @Test
    public void testRoom() {
        Assume.assumeTrue(skip());

        String roomName = String.valueOf(new Date().getTime());
        //get un-existed room
        try {
            meeting.getRoom(roomName);
            fail("should not exist");
        } catch (PiliException e) {
            assertTrue(e.isNotFound());
        }

        // create room with name
        try {
            String r1 =  meeting.createRoom("123",roomName);
            assertEquals(roomName,r1);

            Meeting.Room room = meeting.getRoom(roomName);
            assertEquals(roomName, room.name);
            assertEquals("123",room.ownerId);
            assertEquals(Meeting.Status.NEW,room.status);

        } catch (PiliException e){
            fail();
        }

        // recreate room with the same name
        try {
            String r1 =  meeting.createRoom("123",roomName);
            fail();
        }catch (PiliException e){
            assertEquals(611,e.code());
        }

        // create room without name
        try {
            String r2 = meeting.createRoom("123");

            Meeting.Room room = meeting.getRoom(r2);
            assertEquals(r2, room.name);
            assertEquals("123",room.ownerId);
            assertEquals(Meeting.Status.NEW,room.status);
        }catch (PiliException e){
            fail();
        }

        //delete room
        try{
            meeting.deleteRoom(roomName);
        }catch (PiliException e){
            e.printStackTrace();
            fail();
        }
        try {
            meeting.getRoom(roomName);
            fail("should not exist");
        } catch (PiliException e) {
            assertTrue(e.isNotFound());
        }

        // delete un-existed room
        try {
            meeting.getRoom(roomName);
            fail("should not exist");
        } catch (PiliException e) {
            assertTrue(e.isNotFound());
        }

    }

    @Test
    public void testRoomToken(){
//        String roomName = "room1";
        // create room with name
//        try {
//            String r1 =  meeting.createRoom("123",roomName);
//            assertEquals(roomName,r1);
//
//            Meeting.Room room = meeting.getRoom(roomName);
//            assertEquals(roomName, room.name);
//            assertEquals("123",room.ownerId);
//            assertEquals(Meeting.Status.NEW,room.status);
//
//        } catch (PiliException e){
//            fail();
//        }

        try {
            String token = meeting.roomToken("room1", "123", "admin", new Date(1785600000000L));
            assertEquals("7O7hf7Ld1RrC_fpZdFvU8aCgOPuhw2K4eapYOdII:jltpX6P42j2fH3ErOp7Zj7RyaeE=:eyJyb29tX25hbWUiOiJyb29tMSIsInVzZXJfaWQiOiIxMjMiLCJwZXJtIjoiYWRtaW4iLCJleHBpcmVfYXQiOjE3ODU2MDAwMDB9",
                    token);
        } catch (Exception e) {
            fail();
        }
    }
}
