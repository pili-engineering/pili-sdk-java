package com.pili.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pili.Pili;
import com.pili.PiliException;
import com.pili.Stream;
import com.pili.Stream.Segment;
import com.pili.Stream.SegmentList;
import com.pili.Stream.Status;
import com.pili.Stream.StreamList;

import common.Config;
import common.MessageConfig;

public class PiliTest {
    // Replace with your keys
    public static final String ACCESS_KEY = "QiniuAccessKey";
    public static final String SECRET_KEY = "QiniuSecretKey";

    // Replace with your customized domains
    public static final String RTMP_PUBLISH_HOST = "xxx.pub.z1.pili.qiniup.com";
    public static final String RTMP_PLAY_HOST = "xxx.live1.z1.pili.qiniucdn.com";
    public static final String HLS_PLAY_HOST = "xxx.hls1.z1.pili.qiniucdn.com";

    // Replace with your hub name
    public static final String HUB_NAME = "hubName";

    private static final String INVALID_STREAM_ID = "_invalidStreamId";
    private static final String INVALID_HUB_NAME = "_invalidHubName";
    private static final String INVALID_PUBLISH_SECURITY = "_invalidPublisSecurity";

    private static final String NULL_STREAM_ID_EXCEPTION_MSG = "FATAL EXCEPTION: streamId is null!";
    private static final String NULL_HUBNAME_EXCEPTION_MSG = "FATAL EXCEPTION: hubName is null!";
    private static final String OK_DELETE_STREAM_RES_MSG = "No Content";
    private static final String NOT_FOUND_MSG = "Not Found";
    private static final String BAD_REQ_MSG = "Bad Request";
    private static final String ILLEGAL_TIME_MSG = "Illegal startTime or endTime!";
    private static final String SEGMENTS_IS_NULL = "Segments is null";

    private static final String[] DEFAULT_PRESETS = new String[] {null, "240p", "360p", "480p"};

    private static final String STREAM_STATUS_DISCONNECTED = "disconnected";
    private static final String STREAM_STATUS_CONNECTED = "connected";
    private static final String PRE_STREAM_PRESET_PUBLISH_SECURITY = "static";
    private static final String PRE_STREAM_PRESET_TITLE = "testTitle";
    private static final String EXPECTED_BASE_PUBLISH_URL = "rtmp://" + RTMP_PUBLISH_HOST + "/" + HUB_NAME + "/" + PRE_STREAM_PRESET_TITLE;
    private static final String EXPECTED_BASE_RTMP_LIVEURL = "rtmp://" + RTMP_PLAY_HOST + "/" + HUB_NAME;
    private static final String EXPECTED_BASE_HLS_LIVEURL = "http://" + HLS_PLAY_HOST + "/" + HUB_NAME;

    private Pili mPili = new Pili(ACCESS_KEY, SECRET_KEY, HUB_NAME);
    private Stream mStream = null;
    private List<Stream> mTestCreatedStreamList = new ArrayList<Stream>();;

    @Before
    public void prepareStream() {
        try {
            mStream = mPili.createStream(PRE_STREAM_PRESET_TITLE, null, PRE_STREAM_PRESET_PUBLISH_SECURITY);
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateStream() {
        try {
            Stream stream = mPili.createStream();
            mTestCreatedStreamList.add(stream);
            assertNotNull(stream);
            assertNotNull(stream.getStreamId());
            assertNotNull(stream.getCreatedAt());
            assertNotNull(stream.getUpdatedAt());
            assertNotNull(stream.getTitle());
            assertEquals(HUB_NAME, stream.getHubName());
            assertEquals("dynamic", stream.getPublishSecurity());
        } catch (PiliException e) {
            e.printStackTrace();
        }

        try {
            Stream stream = mPili.createStream(null, null, null);
            mTestCreatedStreamList.add(stream);
            assertNotNull(stream);
            assertNotNull(stream.getStreamId());
            assertNotNull(stream.getCreatedAt());
            assertNotNull(stream.getUpdatedAt());
            assertNotNull(stream.getTitle());
            assertEquals(HUB_NAME, stream.getHubName());
            assertEquals("dynamic", stream.getPublishSecurity());
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateStreamTitle() {
        // min title test
        String[] titles = new String[] {"1", "12", "123", "1234", "t", "-", "_", "titl"};
        for (String title : titles) {
            try {
                Stream stream = mPili.createStream(title, null, null);
                mTestCreatedStreamList.add(stream);
                fail();
            } catch (PiliException e) {
                assertEquals(MessageConfig.ILLEGAL_TITLE_MSG, e.getMessage());
            }
        }
        
        // max title test
        String maxTitleStr = "";
        for(int i = 0; i < Config.TITLE_MAX_LENGTH; i++) {
            maxTitleStr += i;
        }
        try {
            Stream stream = mPili.createStream(maxTitleStr, null, null);
            mTestCreatedStreamList.add(stream);
            fail();
        } catch (PiliException e) {
            assertEquals(MessageConfig.ILLEGAL_TITLE_MSG, e.getMessage());
        }

        // recreate stream test
        try {
            Stream stream1 = mPili.createStream("test1", null, null);
            mTestCreatedStreamList.add(stream1);
            Stream stream2 = mPili.createStream("test1", null, null);
            mTestCreatedStreamList.add(stream2);
            fail();
        } catch (PiliException e) {
            assertTrue(e.getDetails().contains("duplicated content"));
        }
    }

    @Test
    public void testGetStream() {
        try {
            mPili.getStream(null);
            fail();
        } catch (PiliException e) {
            assertEquals(NULL_STREAM_ID_EXCEPTION_MSG, e.getMessage());
        }

        try {
            mPili.getStream(INVALID_STREAM_ID);
            fail();
        } catch (PiliException e) {
            assertEquals(NOT_FOUND_MSG, e.getMessage());
        }

        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }

        try {
            Stream stream = mPili.getStream(mStream.getStreamId());
            assertNotNull(stream);
            assertNotNull(stream.getCreatedAt());
            assertNotNull(stream.getUpdatedAt());

            assertEquals(mStream.getStreamId(), stream.getStreamId());
            assertEquals(mStream.getHubName(), stream.getHubName());
            assertEquals(mStream.getTitle(), stream.getTitle());
            assertEquals(mStream.getPublishKey(), stream.getPublishKey());
            assertEquals(mStream.getPublishSecurity(), stream.getPublishSecurity());
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListStreams() {
        try {
            System.out.println("mPili=" + mPili);
            StreamList streamList = mPili.listStreams();
            if (streamList != null) {
                assertNotNull(streamList.getMarker());
                assertNotNull(streamList.getStreams());
                for (Stream stream : streamList.getStreams()) {
                    assertNotNull(stream);
                }
            }
        } catch (PiliException e) {
            e.printStackTrace();
        }

        String[] markers = new String[] {null, "", " ", "  "};
        for (String marker : markers) {
            try {
                StreamList streamList = mPili.listStreams(marker, 0);
                if (streamList != null) {
                    assertNotNull(streamList.getMarker());
                    assertNotNull(streamList.getStreams());
                    for (Stream stream : streamList.getStreams()) {
                        assertNotNull(stream);
                    }
                }
            } catch (PiliException e) {
                e.printStackTrace();
            }
        }

        int[] limitCounts = new int[] {-1, 0, 1};
        for (int limitCount : limitCounts) {
            try {
                StreamList streamList = mPili.listStreams(null, limitCount);
                if (streamList != null) {
                    assertNotNull(streamList.getMarker());
                    assertNotNull(streamList.getStreams());
                    for (Stream stream : streamList.getStreams()) {
                        assertNotNull(stream);
                    }
                }
            } catch (PiliException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void getStreamStatus() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }
        try {
            Status streamStatus = mStream.status();
            assertNotNull(streamStatus);
            assertEquals("", streamStatus.getAddr());
            assertEquals(STREAM_STATUS_DISCONNECTED, streamStatus.getStatus());
        } catch (PiliException e) {
        }
    }

    @Test
    public void testUpdateStream() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }

        try {
            mStream.update(null, INVALID_PUBLISH_SECURITY, false);
            fail();
        } catch (PiliException e) {
            assertEquals(BAD_REQ_MSG, e.getMessage());
        }

        try {
            Stream stream = mStream.update(null, null, false);
            assertNotNull(stream);
            assertEquals(mStream.getPublishKey(), stream.getPublishKey());
            assertEquals(mStream.getStreamId(), stream.getStreamId());
            assertEquals(mStream.getPublishSecurity(), stream.getPublishSecurity());
            assertEquals(false, stream.isDisabled());
        } catch (PiliException e) {
            e.printStackTrace();
        }

        try {
            String publishSecurity = "static";
            Stream stream = mStream.update(null, publishSecurity, false);
            assertNotNull(stream);
            assertNotNull(stream.getPublishKey());
            assertEquals(mStream.getStreamId(), stream.getStreamId());
            assertEquals(publishSecurity, stream.getPublishSecurity());
        } catch (PiliException e) {
            e.printStackTrace();
        }

        try {
            Stream stream = mStream.update(mStream.getPublishKey(), 
                    mStream.getPublishSecurity(), true);
            assertNotNull(stream);
            assertEquals(mStream.getPublishKey(), stream.getPublishKey());
            assertEquals(mStream.getStreamId(), stream.getStreamId());
            assertEquals(mStream.getPublishSecurity(), stream.getPublishSecurity());
            assertEquals(true, stream.isDisabled());
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteStream() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }

        try {
            String res = mStream.delete();
            assertEquals(OK_DELETE_STREAM_RES_MSG, res);
            mStream = null;
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStreamSegments() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }

        try {
            SegmentList ssList = mStream.segments();
            if (ssList != null) {
                List<Segment> list = ssList.getSegmentList();
                for (Segment ss : list) {
                    assertTrue(ss.getStart() < ss.getEnd());
                }
            }
        } catch (PiliException e) {
//            e.printStackTrace();
            assertEquals(SEGMENTS_IS_NULL, e.getMessage());
        }
    }

    @Test
    public void testPublishUrl() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }
        try {
            String publishUrl = mStream.rtmpPublishUrl();
            String expectedUrl = EXPECTED_BASE_PUBLISH_URL + "?key=" + mStream.getPublishKey();
            System.out.println("publishUrl:" + publishUrl + ",expectedUrl:" + expectedUrl);
            assertEquals(expectedUrl, publishUrl);
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRtmpLiveUrl() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }
        Map<String, String> expectedUrls =  new HashMap<String, String>();
        expectedUrls.put(Stream.ORIGIN, EXPECTED_BASE_RTMP_LIVEURL + "/" + mStream.getTitle());
        for (String p : mStream.getProfiles()) {
            expectedUrls.put(p, EXPECTED_BASE_RTMP_LIVEURL + "/" + mStream.getTitle() + "@" + p);
        }
        Map<String, String> rtmpLiveUrl;
        try {
            rtmpLiveUrl = mStream.rtmpLiveUrls();
            for (String key : expectedUrls.keySet()) {
                System.out.println("key:" + key + ", rtmpLiveUrl:" + rtmpLiveUrl.get(key) + ", expected:" + expectedUrls.get(key));
                if (rtmpLiveUrl.containsKey(key)) {
                    assertEquals(rtmpLiveUrl.get(key), expectedUrls.get(key));
                }
            }
        } catch (PiliException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testHlsLiveUrl() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }
        Map<String, String> expectedUrls =  new HashMap<String, String>();
        expectedUrls.put(Stream.ORIGIN, EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + ".m3u8");
        for (String p : mStream.getProfiles()) {
            expectedUrls.put(p, EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + "@" + p  + ".m3u8");
        }
        Map<String, String> hlsLiveUrl;
        hlsLiveUrl = mStream.hlsLiveUrls();
        for (String key : expectedUrls.keySet()) {
            System.out.println("key:" + key + ", hlsLiveUrl:" + hlsLiveUrl.get(key) + ", expected:" + expectedUrls.get(key));
            if (hlsLiveUrl.containsKey(key)) {
                assertEquals(hlsLiveUrl.get(key), expectedUrls.get(key));
            }
        }
    }

    @Test
    public void testHlsPlaybackUrl() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }
        try {
            mStream.hlsPlaybackUrls(-1, -2);
        } catch (PiliException e) {
            assertEquals(ILLEGAL_TIME_MSG, e.getMessage());
        }

        try {
            mStream.hlsPlaybackUrls(0, 0);
        } catch (PiliException e) {
            assertEquals(ILLEGAL_TIME_MSG, e.getMessage());
        }

        long currentSecond = System.currentTimeMillis() / 1000;
        try {
            mStream.hlsPlaybackUrls(currentSecond + 3600, currentSecond);
        } catch (PiliException e) {
            assertEquals(ILLEGAL_TIME_MSG, e.getMessage());
        }

        long startSec = currentSecond - 3600;
        long endSec = currentSecond;
        final String queryPara = "?start=" + startSec + "&end=" + endSec;
        Map<String, String> expectedUrls =  new HashMap<String, String>();
        expectedUrls.put(Stream.ORIGIN, EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + ".m3u8" + queryPara);
        for (String p : mStream.getProfiles()) {
            expectedUrls.put(p, EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + "@" + p + ".m3u8" + queryPara);
        }
        try {
            Map<String, String> hlsPlaybackUrls = mStream.hlsPlaybackUrls(startSec, endSec);
            for (String key : expectedUrls.keySet()) {
                System.out.println("key:" + key + ", hlsPlaybackUrls:" + hlsPlaybackUrls.get(key) + ", expected:" + expectedUrls.get(key));
                if (hlsPlaybackUrls.containsKey(key)) {
                    assertEquals(hlsPlaybackUrls.get(key), expectedUrls.get(key));
                }
            }
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testToJsonString() {
        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }
        assertNotNull(mStream.toJsonString());
    }

    @After
    public void clear() {
        if (mStream != null) {
            try {
                mStream.delete();
            } catch (PiliException e) {
                e.printStackTrace();
            }
        }

        if (mTestCreatedStreamList != null) {
            for (Stream s : mTestCreatedStreamList) {
                try {
                    s.delete();
                } catch (PiliException e) {
                }
            }
        }
    }
}
