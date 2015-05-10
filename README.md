#Pili server-side library for JAVA

##Installation
You can downlaod pili-sdk-java-v0.1.0.jar file in the release folder.

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
  String title           = null;              // optional, default is auto-generated
  String publishKey      = null;            // optional, a secret key for signing the <publishToken>, default is   auto-generated
  String publishSecurity = null;            // optional, can be "dynamic" or "static", default is "dynamic"
    try {
      Stream stream = pili.createStream(hub, null, null, null);
    } catch (PiliException e) {
      e.printStackTrace();
    }
```

###Get an exist stream
```JAVA
  try {
    Stream stream = pili.getStream(stream.getStreamId());
  } catch (PiliException e) {
    e.printStackTrace();
  }
```

###List stream
```JAVA
import com.pili.Pili.StreamList;
...
String hub    = "YOUR_HUB_NAME"; // required
String marker = null;            // optional
String limit  = 0;               // optional

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
  long startTime = 0; // optional
  long endTime   = 0; // optional
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
      Stream stream = pili.updateStream(stream.getStreamId(), newPublishKey, newPublishSecurity);
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
