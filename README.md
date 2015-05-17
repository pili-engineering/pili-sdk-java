#Pili server-side library for JAVA

##Installation
You can download **pili-sdk-java-v1.0.1.jar** file in the **release** folder.

##dependency
You also need [okhttp][1], [okio][2], [Gson][3]

[1]: http://square.github.io/okhttp/
[2]: https://github.com/square/okio
[3]: https://code.google.com/p/google-gson/downloads/detail?name=google-gson-2.2.4-release.zip&

##Runtime Requirement
For Java, the minimum requirement is 1.7.

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
  String title           = null;              // optional, default is auto-generated. Setting title to null or "" or " ", default you choosed.
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

###Update an exist stream
```JAVA
String newPublishKey      = "new_secret_words";
String newPublishSecurity = "dynamic";

  try {
      Stream retStream = pili.updateStream(stream.getStreamId(), newPublishKey, newPublishSecurity);
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
  long nonce = 1; // optional, for "dynamic" only, default is: System.currentTimeMillis(). Setting nonce to value(<=0), default you choosed. 
  try {
      String publishUrl = pili.publishUrl(RTMP_PUBLISH_HOST, stream.getStreamId(), stream.getPublishKey(), stream.getPublishSecurity(), nonce);
  } catch (PiliException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  }
```

###Generate Play URL
```JAVA
  String preset = "720p"; // optional, just like '720p', '480p', '360p', '240p'. All presets should be defined first. Setting preset to null or "" or " " ..., default you choosed.
  
  String playUrl = pili.rtmpLiveUrl(RTMP_PLAY_HOST, stream.getStreamId(), preset);
  String hlsUrl = pili.hlsLiveUrl(HLS_PLAY_HOST, stream.getStreamId(), preset);
  
  // startSecond and endSecond should be llegal(>0) and startSecond < endSecond, otherwise PiliException will be thrown
  // the unit of startSecond and endSecond is second.
  long startSecond = System.currentTimeMillis() / 1000 - 3600; 
  long endSecond = System.currentTimeMillis()) / 1000;
  try {
    String hlsPlaybackUrl = pili.hlsPlaybackUrl(HLS_PLAY_HOST, stream.getStreamId(), startSecond, endSecond, preset);
  } catch (PiliException e) {
    e.printStackTrace();
  }
```
