package com.qiniu.pili;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ClientTest {
    String accessKey;
    String secretKey;
    String streamKeyPrefix;
    String hubName;
    Client cli;
    Hub hub;
    String keyA = "A";


    @Before
    public void prepare() {
        // local test environment
        accessKey = "Ge_kRfuV_4JW0hOCOnRq5_kD1sX53bKVht8FNdd3";
        secretKey = "0fU92CSrvgNJTVCXqbuRVqkntPFJLFERGa4akpko";
        hubName = "PiliSDKTest";
        streamKeyPrefix = "javasdktest" + String.valueOf(new Date().getTime());

        cli = new Client(accessKey, secretKey);
        hub = cli.newHub(hubName);
    }

    private boolean skip() {
        return Config.APIHost != "pili.qiniuapi.com";
    }

    @Test
    public void testGetNoExistStream() {
        Assume.assumeTrue(skip());

        try {
            hub.get("nnnoexist");
            fail("should not exist");
        } catch (PiliException e) {
            assertTrue(e.isNotFound());
        }
    }

    @Test
    public void testStreamOperate() {
        Assume.assumeTrue(skip());

        String streamKey = streamKeyPrefix + keyA;
        // create
        try {
            hub.create(streamKey);
        } catch (PiliException e) {
            fail();
        }

        // get
        Stream stream = null;
        try {
            stream = hub.get(streamKey);
            assertEquals(0, stream.getDisabledTill());
            assertEquals(hubName, stream.getHub());
            assertEquals(streamKey, stream.getKey());
        } catch (PiliException e) {
            fail();
        }

        // create again
        try {
            hub.create(streamKey);
            fail("has already existed");
        } catch (PiliException e) {
            assertTrue(e.isDuplicate());
        }

        //disable
        try {
            stream = hub.get(streamKey);
            stream.disable();
            stream = hub.get(streamKey);
            assertEquals(-1, stream.getDisabledTill());
            assertEquals(hubName, stream.getHub());
            assertEquals(streamKey, stream.getKey());
        } catch (PiliException e) {
            fail();
        }

        //enable
        try {
            stream = hub.get(streamKey);
            stream.enable();
            stream.info();
            assertEquals(0, stream.getDisabledTill());
            assertEquals(hubName, stream.getHub());
            assertEquals(streamKey, stream.getKey());
        } catch (PiliException e) {
            fail();
        }

    }

    @Test
    public void testUpdateConverts(){
        Assume.assumeTrue(skip());

        String streamKey = streamKeyPrefix + "converts";
        try {
            Stream stream = hub.create(streamKey);
            assertEquals(null, stream.getConverts());

            String[] profiles = {"480p", "720p"};
            stream.updateConverts(profiles);
            stream = stream.info();
            assertEquals(profiles, stream.getConverts());
        }catch (PiliException e) {
            fail();
        }
    }

    @Test
    public void testLiveStatus() {
        Assume.assumeTrue(skip());

        String streamKey = streamKeyPrefix + "livestatus";
        try {
            Stream stream = hub.create(streamKey);
            Stream.LiveStatus status = stream.liveStatus();
            fail();
        } catch (PiliException e) {
            assertTrue(e.isNotInLive());
        }
    }

    @Test
    public void testSave() {
        Assume.assumeTrue(skip());

        String streamKey = streamKeyPrefix + "save";
        try {
            Stream stream = hub.create(streamKey);
            stream.save(0, 0);
            fail();
        } catch (PiliException e) {
            assertTrue(e.isNotInLive());
        }
    }

    @Test
    public void testHistory() {
        Assume.assumeTrue(skip());

        String streamKey = streamKeyPrefix + "history";
        try {
            Stream stream = hub.create(streamKey);
            Stream.Record[] records = stream.historyRecord(0, 0);
            assertEquals(0, records.length);
        } catch (PiliException e) {
            fail();
        }
    }

    @Test
    public void testList() {
        Assume.assumeTrue(skip());

        String streamKeyB = streamKeyPrefix + "B";
        try {
            hub.create(streamKeyB + "1");
            hub.create(streamKeyB + "2");
        } catch (PiliException e) {
            fail();
        }

        try {
            Hub.ListRet listRet = hub.list(streamKeyB, 0, "");
            assertEquals(2, listRet.keys.length);
            assertEquals("", listRet.omarker);
        } catch (PiliException e) {
            fail();
        }
    }

    @Test
    public void testListLive() {
        Assume.assumeTrue(skip());

        try {
            Hub.ListRet listRet = hub.listLive(streamKeyPrefix, 0, "");
            assertEquals(0, listRet.keys.length);
        } catch (PiliException e) {
            fail();
        }
    }

    @Test
    public void TestURL() {
        String expect = "rtmp://publish-rtmp.test.com/" + hubName + "/key?e=";
        String url = cli.RTMPPublishURL("publish-rtmp.test.com", hubName, "key", 3600);
        System.out.println(url);
        assertTrue(url.startsWith(expect));

        expect = "rtmp://live-rtmp.test.com/" + hubName + "/key";
        url = cli.RTMPPlayURL("live-rtmp.test.com", hubName, "key");
        assertTrue(url.startsWith(expect));

        expect = "http://live-hls.test.com/" + hubName + "/key.m3u8";
        url = cli.HLSPlayURL("live-hls.test.com", hubName, "key");
        assertTrue(url.startsWith(expect));

        expect = "http://live-hdl.test.com/" + hubName + "/key.flv";
        url = cli.HDLPlayURL("live-hdl.test.com", hubName, "key");
        assertTrue(url.startsWith(expect));

        expect = "http://live-snapshot.test.com/" + hubName + "/key.jpg";
        url = cli.SnapshotPlayURL("live-snapshot.test.com", hubName, "key");
        assertTrue(url.startsWith(expect));
    }
}
