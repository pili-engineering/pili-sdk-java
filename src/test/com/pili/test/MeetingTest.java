package com.pili.test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.qiniu.Credentials;
import com.pili.Meeting.*;
import com.pili.Meeting;
import java.util.Date;
import com.pili.PiliException;

import static org.junit.Assert.fail;

public class MeetingTest{
    String accessKey;
    String secretKey;
    Meeting meeting;

    @Before
    public void prepare() {
        accessKey = "";
        secretKey = "";

        Credentials credentials = new Credentials(accessKey, secretKey);
        meeting = new Meeting(credentials);
    }

    @Test
    public void testRoom() {
        String roomName = "test123room";//String.valueOf(new Date().getTime());
//        //get un-existed room
//        try {
//            meeting.getRoom(roomName);
//            fail("should not exist");
//        } catch (PiliException e) {
//            assertEquals(612, e.code());
//        }

        // create room with name
        try {
            String r1 =  meeting.createRoom("123",roomName,6);
            assertEquals(roomName,r1);

            Room room = meeting.getRoom(roomName);
            assertEquals(roomName, room.name);
            assertEquals("admin",room.ownerId);
            assertEquals(Meeting.RoomStatus.NEW,room.status);

        } catch (PiliException e){
            fail();
        }

        // recreate room with the same name
        try {
            String r1 =  meeting.createRoom("123",roomName);
            fail();
        }catch (PiliException e){
            assertEquals(611, e.code());
        }

        // create room without name
        try {
            String r2 = meeting.createRoom("123");

            Room room = meeting.getRoom(r2);
            assertEquals(r2, room.name);
            assertEquals("admin",room.ownerId);
            assertEquals(RoomStatus.NEW,room.status);
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
            assertEquals(612, e.code());
        }

        // delete un-existed room
        try {
            meeting.getRoom(roomName);
            fail("should not exist");
        } catch (PiliException e) {
            assertEquals(612, e.code());
        }

    }

    @Test
    public void testRoomToken(){
        try {
            String token = meeting.roomToken("room1", "123", "admin", new Date(1785600000000L));
            assertEquals("MqF35-H32j1PH8igh-am7aEkduP511g-5-F7j47Z:kLUoWSMhvKLAlgkTzcefipNB91o=:eyJyb29tX25hbWUiOiJyb29tMSIsInVzZXJfaWQiOiIxMjMiLCJwZXJtIjoiYWRtaW4iLCJleHBpcmVfYXQiOjE3ODU2MDAwMDAsInZlcnNpb24iOiIyLjAifQ==",
                    token);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testRoomDelete(){
        String roomName = "test123room";
        try{
            meeting.deleteRoom(roomName);
        }catch (PiliException e){
            fail(e.toString());
        }
    }

//    @Test
//    public void testActiveUsers(){
//        String room = "roomName";
//        try {
//            Meeting.AllActiveUser users = meeting.activeUsers(room);
//            System.out.println(users.users.length);
//            for (int i = 0 ; i < users.users.length; i++){
//                System.out.println(users.users[i].UserId +":" + users.users[i].UserName);
//            }
//        } catch (PiliException e) {
//            fail();
//        }
//    }

//    @Test
//    public void testRejectUser(){
//        String room = "roomName";
//        String userId = "userId";
//        try {
//            meeting.rejectUser(room, userId);
//        } catch (PiliException e) {
//            fail();
//        }
//    }
}
