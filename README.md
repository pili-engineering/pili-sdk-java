#Pili server-side library for JAVA

##Installation
You can download **pili-sdk-java-v1.1.0.jar** file in the **release** folder.

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

Pili pili = new Pili(new MacKeys(ACCESS_KEY, SECRET_KEY));

```

###Create a new stream
```JAVA
import com.pili.Pili.Stream;
import com.pili.PiliException;

...
  String title           = null;              // optional, default is auto-generated. Setting title to null or "" or " ", default you choosed. The length of title should be at least 5.
  String publishKey      = null;            // optional, a secret key for signing the <publishToken>, default is   auto-generated. Setting publishKey to null or "" or " ", default you choosed.
  String publishSecurity = null;            // optional, can be "dynamic" or "static", default is "dynamic"
    try {
      Stream stream = pili.createStream(HUB, title, publishKey, publishSecurity);
    } catch (PiliException e) {
      e.printStackTrace();
    }
```

###Get an exist stream
```JAVA
  try {
    Stream retStream = pili.getStream(stream.getStreamId());
  } catch (PiliException e) {
    e.printStackTrace();
  }
```

###List stream
```JAVA
import com.pili.Pili.StreamList;
...
String marker = null;          // optional. Setting marker to null or "" or " ", default you choosed.
long limit  = 0;               // optional. Setting limit to value(<=0), default you choosed.

  try {
      StreamList list = pili.listStreams(HUB, marker, limit);
  } catch (PiliException e) {
      e.printStackTrace();
  }
```

###Get recording segments from an exist stream
```JAVA
import com.pili.Pili.StreamSegmentList;
...
  long startSecond = 0; // optional. Setting startSecond to value(<=0), default you choosed.
  long endSecond  = 0; // optional. Setting endSecond to value(<=0), default you choosed.
  try {
      StreamSegmentList ssList = pili.getStreamSegments(stream.getStreamId(), startSecond, endSecond);
  } catch (PiliException e) {
      e.printStackTrace();
  }
```
###Get Stream Status
```JAVA
  try {
      StreamStatus streamStatus = pili.getStreamStatus(stream.getStreamId());
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
      Stream retStream = pili.updateStream(stream.getStreamId(), newPublishKey, newPublishSecurity, disabled);
      printStream(stream);
  } catch (PiliException e) {
      e.printStackTrace();
  }
```
###Delete stream
```JAVA
  try {
      String res = pili.deleteStream(stream.getStreamId());
  } catch (PiliException e) {
      e.printStackTrace();
  }
```

###Generate a RTMP publish URL
```JAVA
  long nonce = 0; // optional, for "dynamic" only, default is: System.currentTimeMillis(). Setting nonce to value(<=0), default you choosed. 
  try {
      String publishUrl = pili.publishUrl(RTMP_PUBLISH_HOST, stream.getStreamId(), stream.getPublishKey(), stream.getPublishSecurity(), nonce);
  } catch (PiliException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  }
```

###Generate Play URL
```JAVA
  String profile = "720p"; // optional, such as '720p', '480p', '360p', '240p'. All profiles should be defined first. Setting profile to null or "" or " " ..., default you choosed.
  
  String playUrl = pili.rtmpLiveUrl(RTMP_PLAY_HOST, stream.getStreamId(), profile);
  String hlsUrl = pili.hlsLiveUrl(HLS_PLAY_HOST, stream.getStreamId(), profile);
  
  // startSecond and endSecond should be llegal(>0) and startSecond < endSecond, otherwise PiliException will be thrown
  // the unit of startSecond and endSecond is second.
  long startSecond = System.currentTimeMillis() / 1000 - 3600; 
  long endSecond = System.currentTimeMillis()) / 1000;
  try {
    String hlsPlaybackUrl = pili.hlsPlaybackUrl(HLS_PLAY_HOST, stream.getStreamId(), startSecond, endSecond, profile);
  } catch (PiliException e) {
    e.printStackTrace();
  }
```
