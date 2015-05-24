package com.pili.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pili.Auth.MacKeys;
import com.pili.Pili.Stream;
import com.pili.Pili.StreamList;
import com.pili.Pili.StreamSegment;
import com.pili.Pili;
import com.pili.Pili.StreamSegmentList;
import com.pili.PiliException;

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

    private static final String[] DEFAULT_PRESETS = new String[] {null, "240p", "480p", "720p"};

    private static final String PRE_STREAM_PRESET_PUBLISH_SECURITY = "static";
    private static final String PRE_STREAM_PRESET_TITLE = "title4test";
    private static final String EXPECTED_BASE_PUBLISH_URL = "rtmp://" + RTMP_PUBLISH_HOST + "/" + HUB_NAME + "/" + PRE_STREAM_PRESET_TITLE;
    private static final String EXPECTED_BASE_RTMP_LIVEURL = "rtmp://" + RTMP_PLAY_HOST + "/" + HUB_NAME;
    private static final String EXPECTED_BASE_HLS_LIVEURL = "http://" + HLS_PLAY_HOST + "/" + HUB_NAME;

    private Pili mPili = new Pili(new MacKeys(ACCESS_KEY, SECRET_KEY));
    private Stream mStream = null;
    private List<Stream> mTestCreatedStreamList = new ArrayList<Pili.Stream>();;

    @Before
    public void prepareStream() {
        try {
            mStream = mPili.createStream(HUB_NAME, PRE_STREAM_PRESET_TITLE, null, PRE_STREAM_PRESET_PUBLISH_SECURITY);
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateStream() {
        try {
            Stream stream = mPili.createStream(null, null, null, null);
            mTestCreatedStreamList.add(stream);
            fail();
        } catch (PiliException e) {
            assertTrue(NULL_HUBNAME_EXCEPTION_MSG.equals(e.getMessage()));
        }

        try {
            Stream stream = mPili.createStream(HUB_NAME, null, null, null);
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
        String[] titles = new String[] {"1", "12", "123", "1234", "t", "-", "_", "titl"};
        for (String title : titles) {
            try {
                Stream stream = mPili.createStream(HUB_NAME, title, null, null);
                mTestCreatedStreamList.add(stream);
                fail();
            } catch (PiliException e) {
                assertEquals("The length of title should be at least 5", e.getMessage());
            }
        }

        try {
            Stream stream1 = mPili.createStream(HUB_NAME, "test1", null, null);
            mTestCreatedStreamList.add(stream1);
            Stream stream2 = mPili.createStream(HUB_NAME, "test1", null, null);
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
            mPili.listStreams(null, null, 0);
            fail();
        } catch (PiliException e) {
            assertEquals(NULL_HUBNAME_EXCEPTION_MSG, e.getMessage());
        }

        try {
            mPili.listStreams(INVALID_HUB_NAME, null, 0);
            fail();
        } catch (PiliException e) {
            assertEquals(BAD_REQ_MSG, e.getMessage());
        }

        String[] markers = new String[] {null, "", " ", "  "};
        for (String marker : markers) {
            try {
                StreamList streamList = mPili.listStreams(HUB_NAME, marker, 0);
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
                StreamList streamList = mPili.listStreams(HUB_NAME, null, limitCount);
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
    public void testUpdateStream() {
        try {
            mPili.updateStream(null, null, null);
            fail();
        } catch (PiliException e) {
            assertEquals(NULL_STREAM_ID_EXCEPTION_MSG, e.getMessage());
        }

        // the test case order cannot be guaranteed
        if (mStream == null) {
            prepareStream();
        }

        try {
            mPili.updateStream(mStream.getStreamId(), null, INVALID_PUBLISH_SECURITY);
            fail();
        } catch (PiliException e) {
            assertEquals(BAD_REQ_MSG, e.getMessage());
        }

        try {
            String publishSecurity = "static";
            Stream stream = mPili.updateStream(mStream.getStreamId(), null, publishSecurity);
            assertNotNull(stream);
            assertNotNull(stream.getPublishKey());
            assertEquals(mStream.getStreamId(), stream.getStreamId());
            assertEquals(publishSecurity, stream.getPublishSecurity());
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteStream() {
        try {
            mPili.deleteStream(null);
            fail();
        } catch (PiliException e) {
            assertEquals(NULL_STREAM_ID_EXCEPTION_MSG, e.getMessage());
        }

        try {
            mPili.deleteStream(INVALID_STREAM_ID);
            fail();
        } catch (PiliException e) {
            assertEquals(NOT_FOUND_MSG, e.getMessage());
        }

        try {
            String res = mPili.deleteStream(mStream.getStreamId());
            assertEquals(OK_DELETE_STREAM_RES_MSG, res);
            mStream = null;
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStreamSegments() {
        try {
            mPili.getStreamSegments(null, 0, 0);
        } catch (PiliException e) {
            assertEquals(NULL_STREAM_ID_EXCEPTION_MSG, e.getMessage());
        }

        try {
            mPili.getStreamSegments(INVALID_STREAM_ID, 0, 0);
        } catch (PiliException e) {
            assertEquals(NOT_FOUND_MSG, e.getMessage());
        }

        try {
            StreamSegmentList ssList = mPili.getStreamSegments(mStream.getStreamId(), 0, 0);
            if (ssList != null) {
                List<StreamSegment> list = ssList.getStreamSegmentList();
                for (StreamSegment ss : list) {
                    assertTrue(ss.getStart() < ss.getEnd());
                }
            }
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPublishUrl() {
        long nonce = 0;
        try {
            String publishUrl = mPili.publishUrl(RTMP_PUBLISH_HOST, mStream.getStreamId(), 
                    mStream.getPublishKey(), mStream.getPublishSecurity(), nonce);
            String expectedUrl = EXPECTED_BASE_PUBLISH_URL + "?key=" + mStream.getPublishKey();
            assertEquals(expectedUrl, publishUrl);
        } catch (PiliException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRtmpLiveUrl() {
        String expectedUrl =  null;
        for (String preset : DEFAULT_PRESETS) {
            String rtmpLiveUrl =  mPili.rtmpLiveUrl(RTMP_PLAY_HOST, mStream.getStreamId(), preset);
            if (preset != null) {
                expectedUrl = EXPECTED_BASE_RTMP_LIVEURL + "/" + mStream.getTitle() + "@" + preset;
            } else {
                expectedUrl = EXPECTED_BASE_RTMP_LIVEURL + "/" + mStream.getTitle();
            }
            assertEquals(expectedUrl, rtmpLiveUrl);
        }
    }

    @Test
    public void testHlsLiveUrl() {
        String expectedUrl = null;
        for (String preset : DEFAULT_PRESETS) {
            String hlsLiveUrl = mPili.hlsLiveUrl(HLS_PLAY_HOST, mStream.getStreamId(), preset);
            if (preset != null) {
                expectedUrl = EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + "@" + preset + ".m3u8";
            } else {
                expectedUrl = EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + ".m3u8";
            }
            assertEquals(expectedUrl, hlsLiveUrl);
        }
        
    }

    @Test
    public void testHlsPlaybackUrl() {
        try {
            mPili.hlsPlaybackUrl(HLS_PLAY_HOST, mStream.getStreamId(), -1, -2, null);
        } catch (PiliException e) {
            assertEquals(ILLEGAL_TIME_MSG, e.getMessage());
        }

        try {
            mPili.hlsPlaybackUrl(HLS_PLAY_HOST, mStream.getStreamId(), 0, 0, null);
        } catch (PiliException e) {
            assertEquals(ILLEGAL_TIME_MSG, e.getMessage());
        }

        long currentSecond = System.currentTimeMillis() / 1000;
        try {
            mPili.hlsPlaybackUrl(HLS_PLAY_HOST, mStream.getStreamId(),  currentSecond + 3600, currentSecond, null);
        } catch (PiliException e) {
            assertEquals(ILLEGAL_TIME_MSG, e.getMessage());
        }

        String expectedUrl = null;
        long startSec = currentSecond - 3600;
        long endSec = currentSecond;
        for (String preset : DEFAULT_PRESETS) {
            try {
                String hlsPlaybackUrl = mPili.hlsPlaybackUrl(HLS_PLAY_HOST, mStream.getStreamId(), 
                        startSec, endSec, preset);
                if (preset != null) {
                    expectedUrl = EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + "@" + preset + ".m3u8";
                } else {
                    expectedUrl = EXPECTED_BASE_HLS_LIVEURL + "/" + mStream.getTitle() + ".m3u8";
                }
                expectedUrl += "?start=" + startSec + "&end=" + endSec;
                assertEquals(expectedUrl, hlsPlaybackUrl);
            } catch (PiliException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void clear() {
        if (mStream != null) {
            try {
                mPili.deleteStream(mStream.getStreamId());
            } catch (PiliException e) {
                e.printStackTrace();
            }
        }

        if (mTestCreatedStreamList != null) {
            for (Stream s : mTestCreatedStreamList) {
                try {
                    mPili.deleteStream(s.getStreamId());
                } catch (PiliException e) {
                }
            }
        }
    }
}
