package common;

public class MessageConfig {
    public static final String NULL_STREAM_ID_EXCEPTION_MSG = "FATAL EXCEPTION: streamId is null!";
    public static final String NULL_HUBNAME_EXCEPTION_MSG = "FATAL EXCEPTION: hubName is null!";
    public static final String ILLEGAL_RTMP_PUBLISH_URL_MSG = "Illegal rtmp publish url!";
    public static final String ILLEGAL_TIME_MSG = "Illegal startTime or endTime!";
    public static final String ILLEGAL_TITLE_MSG = "The length of title should be at least:" + Config.TITLE_MIN_LENGTH 
            + ",or at most:" + Config.TITLE_MAX_LENGTH;
    public static final String ILLEGAL_FILE_NAME_EXCEPTION_MSG = "Illegal file name !";
    public static final String ILLEGAL_FORMAT_EXCEPTION_MSG = "Illegal format !";
}
