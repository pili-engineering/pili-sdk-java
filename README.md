#Pili server-side library for JAVA

##Installation
You can download **pili-sdk-java-v1.0.0.jar** file in the **release** folder.

##dependency
You also need [okhttp][1], [okio][2], [Gson][3]

[1]: http://square.github.io/okhttp/
[2]: https://github.com/square/okio
[3]: https://code.google.com/p/google-gson/downloads/detail?name=google-gson-2.2.4-release.zip&

##Runtime Requirement
For Java, the minimum requirement is 1.7.

##Usage
###Instantiate a Pili client
```JAVA
import com.pili.Pili;
import com.pili.Auth.MacKeys;
...

// Replace with your keys
public static final String ACCESS_KEY = "YOUR_ACCESS_KEY";
public static final String ACCESS_KEY = "YOUR_SECRET_KEY";

Pili pili = new Pili(new MacKeys(ACCESS_KEY, ACCESS_KEY));

```

###Create a new stream
```JAVA
import com.pili.Pili.Stream;
import com.pili.PiliException;

...
  String hub             = "YOUR_HUB_NAME"; // required, <Hub> must be an exists one
  String title           = null;              // optional, default is auto-generated. Setting title to null or "" or " ", default you choosed.
  String publishKey      = null;            // optional, a secret key for signing the <publishToken>, default is   auto-generated. Setting publishKey to null or "" or " ", default you choosed.
  String publishSecurity = null;            // optional, can be "dynamic" or "static", default is "dynamic"
    try {
      Stream stream = pili.createStream(hub, title, publishKey, publishSecurity);
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
String hub    = "YOUR_HUB_NAME"; // required
String marker = null;            // optional. Setting marker to null or "" or " ", default you choosed.
long limit  = 0;               // optional. Setting limit to value(<=0), default you choosed.

  try {
      StreamList list = pili.listStreams(hub, marker, limit);
  } catch (PiliException e) {
      e.printStackTrace();
  }
```

###Get recording segments from an exist stream
```JAVA
import com.pili.Pili.StreamSegmentList;
...
  long startTime = 0; // optional. Setting startTime to value(<=0), default you choosed.
  long endTime   = 0; // optional. Setting endTime to value(<=0), default you choosed.
  try {
      StreamSegmentList ssList = pili.getStreamSegments(stream.getStreamId(), startTime, endTime);
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
  long nonce = 1; // // optional, for "dynamic" only, default is: System.currentTimeMillis(). Setting nonce to value(<=0), default you choosed. 
  try {
      String publishUrl = pili.publishUrl(stream.getStreamId(), stream.getPublishKey(), stream.getPublishSecurity(), nonce);
      System.out.println(publishUrl);
  } catch (PiliException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  }
```

###Generate Play URL
```JAVA
  String rtmpPlayHost = "live.z1.glb.pili.qiniucdn.com"; // required, replace with your customized domain
  String hlsPlayHost = "hls1.z1.glb.pili.qiniuapi.com"; // required, replace with your customized domain
  
  String preset = "720p"; // optional, just like '720p', '480p', '360p', '240p'. All presets should be defined first. Setting preset to null or "" or " " ..., default you choosed.
  
  String playUrl = pili.rtmpLiveUrl(rtmpPlayHost, stream.getStreamId(), preset);
  String hlsUrl = pili.hlsLiveUrl(hlsPlayHost, stream.getStreamId(), preset);
  
  // startTime and endTime should be llegal(>0) and startTime < endTime, otherwise PiliException will be thrown
  // startTime and endTime are from streamSegment
  long startTime = streamSegment.getStart(); 
  long endTime = streamSegment.getEnd();
  try {
    String hlsPlaybackUrl = pili.hlsPlaybackUrl(hlsPlayHost, stream.getStreamId(), startTime, endTime, preset);
  } catch (PiliException e) {
    e.printStackTrace();
  }
```
