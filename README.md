#Pili server-side library for JAVA

##Installation
You can download **pili-sdk-java-v1.2.0.jar** file in the **release** folder.

##dependency
You also need [okhttp][1], [okio][2], [Gson][3]

[1]: http://square.github.io/okhttp/
[2]: https://github.com/square/okio
[3]: https://code.google.com/p/google-gson/downloads/detail?name=google-gson-2.2.4-release.zip&

##Runtime Requirement
For Java, the minimum requirement is 1.7.

##Unit Test
The jar doesn't contain the unit test code. If you want to run the unit test code, you should download the whole code first, and then replace the following configuration before running:
```JAVA
  // src/com/pili/test/PiliTest.java

  // Replace with your keys
  public static final String ACCESS_KEY = "QiniuAccessKey";
  public static final String SECRET_KEY = "QiniuSecretKey";

  // Replace with your customized domains
  public static final String RTMP_PUBLISH_HOST = "xxx.pub.z1.pili.qiniup.com";
  public static final String RTMP_PLAY_HOST = "xxx.live1.z1.pili.qiniucdn.com";
  public static final String HLS_PLAY_HOST = "xxx.hls1.z1.pili.qiniucdn.com";

  // Replace with your hub name
  public static final String HUB_NAME = "hubName";
```

##Usage
###Configuration
```JAVA
  // Replace with your keys
  public static final String ACCESS_KEY = "QiniuAccessKey";
  public static final String SECRET_KEY = "QiniuSecretKey";
  
  // Replace with your customized domains
  public static final String RTMP_PUBLISH_HOST = "xxx.pub.z1.pili.qiniup.com";
  public static final String RTMP_PLAY_HOST = "xxx.live1.z1.pili.qiniucdn.com";
  public static final String HLS_PLAY_HOST = "xxx.hls1.z1.pili.qiniucdn.com";
  
  // Replace with your hub name
  public static final String HUB = "hubName";
```

###Instantiate a Pili client
```JAVA
import com.pili.Pili;
import com.pili.Auth.MacKeys;
...

  Pili mPili = new Pili(ACCESS_KEY, SECRET_KEY, HUB);

```

###Create a new stream
```JAVA
import com.pili.Stream;
import com.pili.PiliException;

...
  String title           = null;            // optional, default is auto-generated. Setting title to null or "" or " ", default you choosed. The length of title should be at least 5 and at most 200.
  String publishKey      = null;            // optional, a secret key for signing the <publishToken>, default is   auto-generated. Setting publishKey to null or "" or " ", default you choosed.
  String publishSecurity = null;            // optional, can be "dynamic" or "static", default is "dynamic"
  try {
    Stream stream = mPili.createStream(title, publishKey, publishSecurity);
  } catch (PiliException e) {
    e.printStackTrace();
  }
```
or
```JAVA
  try {
    Stream stream = mPili.createStream();
    printStream(stream);
  } catch (PiliException e) {
    e.printStackTrace();
  }
```

###Get an exist stream
```JAVA
  try {
    Stream retStream = mPili.getStream(mStream.getStreamId());
    printStream(retStream);
  } catch (PiliException e) {
    e.printStackTrace();
  }
```

###List stream
```JAVA
import com.pili.Stream.StreamList;
  ...
  String marker = null;          // optional. Setting marker to null or "" or " ", default you choosed.
  long limit    = 0;             // optional. Setting limit to value(<=0), default you choosed.
  
  try {
      StreamList list = mPili.listStreams(marker, limit);
      if (list != null) {
          for (Stream stream : list.getStreams()) {
              printStream(stream);
          }
      }
  } catch (PiliException e) {
      e.printStackTrace();
  }
```
or
```JAVA
  try {
      StreamList list = mPili.listStreams();
      if (list != null) {
          for (Stream stream : list.getStreams()) {
              printStream(stream);
          }
      }
  } catch (PiliException e) {
      e.printStackTrace();
  }
```

###Get recording segments from an exist stream
```JAVA
import com.pili.Stream.SegmentList;
...
  long startSecond = 0; // optional. Setting startSecond to value(<=0), default you choosed.
  long endSecond  = 0;  // optional. Setting endSecond to value(<=0), default you choosed.
  try {
      SegmentList ssList = mStream.segments(startSecond, endSecond);
      if (ssList != null) {
          List<Segment> list = ssList.getSegmentList();
          for (Segment ss : list) {
              System.out.println(ss.getStart() + "," + ss.getEnd());
          }
      }
  } catch (PiliException e) {
      e.printStackTrace();
  }
```
or
```JAVA
  try {
      StreamSegmentList ssList = mStream.segments();
      if (ssList != null) {
          List<Segment> list = ssList.getSegmentList();
          for (Segment ss : list) {
              System.out.println(ss.getStart() + "," + ss.getEnd());
          }
      }
  } catch (PiliException e) {
      e.printStackTrace();
  }
```

###Get Stream Status
```JAVA
  try {
      Status streamStatus = mStream.status();
      System.out.println("addr:" + streamStatus.getAddr() + ", status:" + streamStatus.getStatus());
  } catch (PiliException e) {
      e.printStackTrace();
  }
```
###Update an exist stream
```JAVA
  String newPublishKey      = "new_secret_words";
  String newPublishSecurity = "dynamic";
  boolean disabled = false;

  try {
      Stream retStream = mStream.updateStream(newPublishKey, newPublishSecurity, disabled);
      printStream(retStream);
  } catch (PiliException e) {
      e.printStackTrace();
  }
```
###Delete stream
```JAVA
  try {
      String res = mStream.delete();
  } catch (PiliException e) {
      e.printStackTrace();
  }
```

###Generate RTMP publish URL
```JAVA
  try {
      String publishUrl = mStream.rtmpPublishUrl();
  } catch (PiliException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  }
```

###Generate RTMP live play URLs
```JAVA
  Map<String, String> rtmpLiveUrl = mStream.rtmpLiveUrls();
  for (String key : rtmpLiveUrl.keySet()) {
    System.out.println("key:" + key + ", rtmpLiveUrl:" + rtmpLiveUrl.get(key));
  }
```

###Generate HLS live play URLs
```JAVA
  Map<String, String> hlsLiveUrl = mStream.hlsLiveUrls();
  for (String key : hlsLiveUrl.keySet()) {
    System.out.println("key:" + key + ", hlsLiveUrl:" + hlsLiveUrl.get(key));
  }
```

###Generate HLS playback URLs
```JAVA
  // startSecond and endSecond should be llegal(>0) and startSecond < endSecond, otherwise PiliException will be thrown
  // the unit of startSecond and endSecond is second.
  long startSecond = System.currentTimeMillis() / 1000 - 3600; 
  long endSecond = System.currentTimeMillis()) / 1000;
  try {
    Map<String, String> hlsPlaybackUrls = mStream.hlsPlaybackUrls(startSecond, endSecond);
    for (String key : hlsPlaybackUrls.keySet()) {
      System.out.println("key:" + key + ", hlsPlaybackUrls:" + hlsPlaybackUrls.get(key));
    }
  } catch (PiliException e) {
    e.printStackTrace();
  }
```

###To JSON String
```JAVA
System.out.println(mStream.toJsonString());
```
