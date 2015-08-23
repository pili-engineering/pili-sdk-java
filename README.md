# Pili Streaming Cloud server-side library for JAVA

## Features

- Stream Create,Get,List
    - [x] hub.createStream()
    - [x] hub.getStream()
    - [x] hub.listStreams()
- Stream operations else
    - [x] stream.toJsonString()
    - [x] stream.update()
    - [x] stream.disable()
    - [x] stream.enable()
    - [x] stream.status()
    - [x] stream.rtmpPublishUrl()
    - [x] stream.rtmpLiveUrls()
    - [x] stream.hlsLiveUrls()
    - [x] stream.httpFlvLiveUrls()
    - [x] stream.segments()
    - [x] stream.hlsPlaybackUrls()
    - [x] stream.snapshot()
    - [x] stream.saveAs()
    - [x] stream.delete()

## Contents
- [Installation](#installation)
- [Dependency](#dependency)
- [Runtime Requirement](#runtime-requirement)
- [Usage](#usage)
    - [Configuration](#configuration)
    - [Hub](#hub)
        - [Instantiate a Pili Hub object](#instantiate-a-pili-hub-object)
        - [Create a new Stream](#create-a-new-stream)
        - [Get an exit Stream](#get-an-exist-stream)
        - [List streams](#list-streams)
    - [Stream](#stream)
        - [To JSON string](#to-json-string)
        - [Update a Stream](#update-a-stream)
        - [Disable a Stream](#disable-a-stream)
        - [Enable a Stream](#enable-a-stream)
        - [Get Stream status](#get-stream-status)
        - [Generate RTMP publish URL](#generate-rtmp-publish-url)
        - [Generate RTMP live play URLs](#generate-rtmp-live-play-urls)
        - [Generate HLS play URLs](#generate-hls-play-urls)
        - [Generate Http-Flv live play URLs](#generate-http-flv-live-play-urls)
        - [Get Stream segments](#get-stream-segments)
        - [Generate HLS playback URLs](#generate-hls-playback-urls)
        - [Snapshot Stream](#snapshot-stream)
        - [Save Stream as a file](#save-stream-as-a-file)
        - [Delete a Stream](#delete-a-stream)
- [History](#history)

### Installation
You can download **pili-sdk-java-v1.5.0.jar** file in the **release** folder.

### Dependency
You also need [okhttp][1], [okio][2], [Gson][3]

[1]: http://square.github.io/okhttp/
[2]: https://github.com/square/okio
[3]: https://code.google.com/p/google-gson/downloads/detail?name=google-gson-2.2.4-release.zip&

### Runtime Requirement
For Java, the minimum requirement is 1.7.

If you want to run the SDK on JDK 1.6 environment, you can download the compatible jar of  [okhttp](https://raw.githubusercontent.com/qiniu/java-sdk/master/libs/okhttp-2.3.0-SNAPSHOT.jar) and [okio](https://raw.githubusercontent.com/qiniu/java-sdk/master/libs/okio-1.3.0-SNAPSHOT.jar).

### Usage
#### Configuration
```JAVA
  // Replace with your keys
  public static final String ACCESS_KEY = "Qiniu_AccessKey";
  public static final String SECRET_KEY = "Qiniu_SecretKey";
  
  // Replace with your hub name
  public static final String HUB = "Pili_HubName";
  
  // Change API host as necessary
  //
  // pili.qiniuapi.com as deafult
  // pili-lte.qiniuapi.com is the latest RC version
  //
  static {
    Configuration.getInstance().setAPIHost("pili-lte.qiniuapi.com");
  }
```
#### Hub
##### Instantiate a Pili Hub object
```JAVA
    // Instantiate an Hub object
    Credentials credentials = new Credentials(new MacKeys(AK, SK)); // Credentials Object
    Hub hub = new Hub(credentials, HUB_NAME);
```

##### Create a new stream
```JAVA
// Create a new Stream
  String title           = null;     // optional, auto-generated as default
  String publishKey      = null;     // optional, auto-generated as default
  String publishSecurity = null;     // optional, can be "dynamic" or "static", "dynamic" as default
  Stream stream = null;
  try {
      stream = hub.createStream(title, publishKey, publishSecurity);
      System.out.println("Client createStream:");
      System.out.println(stream.toJsonString());
      /*
      {
          "id":"z1.test-hub.55d80075e3ba5723280000d2",
          "createdAt":"2015-08-22T04:54:13.539Z",
          "updatedAt":"2015-08-22T04:54:13.539Z",
          "title":"55d80075e3ba5723280000d2",
          "hub":"test-hub",
          "disabled":false,
          "publishKey":"ca11e07f094c3a6e",
          "publishSecurity":"dynamic",
          "hosts":{
              "publish":{
                  "rtmp":"ey636h.publish.z1.pili.qiniup.com"
               },
               "live":{
                   "http":"ey636h.live1-http.z1.pili.qiniucdn.com",
                   "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
               },
               "playback":{
                   "http":"ey636h.hls.z1.pili.qiniucdn.com"
               },
               "play":{
                   "hls":"ey636h.live1-http.z1.pili.qiniucdn.com",
                   "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
               }
           }
       }
       */
  } catch (PiliException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  }
```
or

```JAVA
  try {
    Stream stream = hub.createStream();
  } catch (PiliException e) {
    e.printStackTrace();
  }
```

##### Get an exist stream
```JAVA
  String streamId = mStream.getStreamId();
  try {
    stream = client.getStream(streamId);
    System.out.println("Client getStream:");
    System.out.println(stream.toJsonString());
    /*
    {
        "id":"z1.test-hub.55d80075e3ba5723280000d2",
        "createdAt":"2015-08-22T04:54:13.539Z",
        "updatedAt":"2015-08-22T04:54:13.539Z",
        "title":"55d80075e3ba5723280000d2",
        "hub":"test-hub",
        "disabled":false,
        "publishKey":"ca11e07f094c3a6e",
        "publishSecurity":"dynamic",
        "hosts":{
            "publish":{
                "rtmp":"ey636h.publish.z1.pili.qiniup.com"
             },
             "live":{
                 "http":"ey636h.live1-http.z1.pili.qiniucdn.com",
                 "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
             },
             "playback":{
                 "http":"ey636h.hls.z1.pili.qiniucdn.com"
             },
             "play":{
                 "hls":"ey636h.live1-http.z1.pili.qiniucdn.com",
                 "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
             }
         }
     }
     */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### List streams
```JAVA
  try {
      String marker      = null;      // optional
      long limit         = 0;         // optional
      String titlePrefix = null;      // optional

      StreamList streamList = client.listStreams(marker, limit, titlePrefix);
      System.out.println("Client listStreams()");
      System.out.println("marker:" + streamList.getMarker());
      List<Stream> list = streamList.getStreams();
      for (Stream s : list) {
          // access the stream
      }
      
      /*
       marker:10
       stream object
       */
  } catch (PiliException e) {
      // TODO Auto-generated catch block
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
#### Stream
##### To JSON string
```JAVA
String streamJsonString = stream.toJsonString();
System.out.println("Stream toJSONString()");
System.out.println(streamJsonString);

/*
    {
        "id":"z1.test-hub.55d80075e3ba5723280000d2",
        "createdAt":"2015-08-22T04:54:13.539Z",
        "updatedAt":"2015-08-22T04:54:13.539Z",
        "title":"55d80075e3ba5723280000d2",
        "hub":"test-hub",
        "disabled":false,
        "publishKey":"ca11e07f094c3a6e",
        "publishSecurity":"dynamic",
        "hosts":{
            "publish":{
                "rtmp":"ey636h.publish.z1.pili.qiniup.com"
             },
             "live":{
                 "http":"ey636h.live1-http.z1.pili.qiniucdn.com",
                 "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
             },
             "playback":{
                 "http":"ey636h.hls.z1.pili.qiniucdn.com"
             },
             "play":{
                 "hls":"ey636h.live1-http.z1.pili.qiniucdn.com",
                 "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
             }
         }
     }
 */
```
##### Update a Stream
```JAVA
// Update a Stream
String newPublishKey       = "new_secret_words"; // optional
String newPublishSecurity  = "static";           // optional, can be "dynamic" or "static"
boolean newDisabled        = true;               // optional, can be "true" of "false"
try {
    Stream newStream = stream.update(newPublishKey, newPublishSecurity, newDisabled);
    System.out.println("Stream update()");
    System.out.println(newStream.toJsonString());
    /*
    {
        "id":"z1.test-hub.55d80075e3ba5723280000d2",
        "createdAt":"2015-08-22T04:54:13.539Z",
        "updatedAt":"2015-08-22T01:53:02.738973745-04:00",
        "title":"55d80075e3ba5723280000d2",
        "hub":"test-hub",
        "disabled":true,
        "publishKey":"new_secret_words",
        "publishSecurity":"static",
        "hosts":{
            "publish":{
                "rtmp":"ey636h.publish.z1.pili.qiniup.com"
             },
             "live":{
                 "http":"ey636h.live1-http.z1.pili.qiniucdn.com",
                 "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
             },
             "playback":{
                 "http":"ey636h.hls.z1.pili.qiniucdn.com"
             },
             "play":{
                 "hls":"ey636h.live1-http.z1.pili.qiniucdn.com",
                 "rtmp":"ey636h.live1-rtmp.z1.pili.qiniucdn.com"
             }
         }
     }
 */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Disable a Stream
```JAVA
// Disable a Stream
try {
    Stream disabledStream = stream.disable();
    System.out.println("Stream disable()");
    System.out.println(disabledStream.isDisabled());
    /*
     * true
     * 
     * */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Enable a Stream
```JAVA
// Enable a Stream
try {
    Stream enabledStream = stream.enable();
    System.out.println("Stream enable()");
    System.out.println(enabledStream.isDisabled());
    /*
     * false
     * 
     * */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Get Stream status
```JAVA
// Get Stream status
try {
    Status status = stream.status();
    System.out.println("Stream status()");
    System.out.println(status.toString());
    /*
    {
        "addr":"222.73.202.226:2572",
        "status":"disconnected",
        "bytesPerSecond":0,
        "framesPerSecond":{
            "audio":0,
            "video":0,
            "data":0
         }
     }
    */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Generate RTMP publish URL
```JAVA
// Generate RTMP publish URL
try {
    String publishUrl = stream.rtmpPublishUrl();
    System.out.println("Stream rtmpPublishUrl()");
    System.out.println(publishUrl);
    // rtmp://ey636h.publish.z1.pili.qiniup.com/test-hub/55d810aae3ba5723280000db?nonce=1440223404&token=hIVJje0ZOX9hp7yPIvGBmJ_6Qxo=
     
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Generate RTMP live play URLs
```JAVA
// Generate RTMP live play URLs
String originUrl = stream.rtmpLiveUrls().get(Stream.ORIGIN);
System.out.println("Stream rtmpLiveUrls()");
System.out.println(originUrl);
// rtmp://ey636h.live1-rtmp.z1.pili.qiniucdn.com/test-hub/55d8113ee3ba5723280000dc
```

##### Generate HLS play URLs
```JAVA
// Generate HLS play URLs
String originLiveHlsUrl = stream.hlsLiveUrls().get(Stream.ORIGIN);
System.out.println("Stream hlsLiveUrls()");
System.out.println(originLiveHlsUrl);
// http://ey636h.live1-http.z1.pili.qiniucdn.com/test-hub/55d8119ee3ba5723280000dd.m3u8
```

##### Generate Http Flv live play URLs
```JAVA
// Generate Http-Flv live play URLs
String originLiveFlvUrl = stream.httpFlvLiveUrls().get(Stream.ORIGIN);
System.out.println("Stream httpFlvLiveUrls()");
System.out.println(originLiveFlvUrl);
// http://ey636h.live1-http.z1.pili.qiniucdn.com/test-hub/55d8119ee3ba5723280000dd.flv
```

##### Get Stream segments
```JAVA
// Get Stream segments
long start = 0;    // optional, in second, unix timestamp
long end   = 0;    // optional, in second, unix timestamp
int limit  = 0;    // optional, int
try {
    SegmentList segmentList = stream.segments(start, end, limit);

    System.out.println("Stream segments()");
    for (Segment segment : segmentList.getSegmentList()) {
        System.out.println("start:" + segment.getStart() + ",end:" + segment.getEnd());
    }
    /*
         start:1440315411,end:1440315435
     */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Generate HLS playback URLs
```JAVA
// Generate HLS playback URLs
long startHlsPlayback     = 1440315411;  // required, in second, unix timestamp
long endHlsPlayback       = 1440315435;  // required, in second, unix timestamp
try {
    String hlsPlaybackUrl = stream.hlsPlaybackUrls(startHlsPlayback, endHlsPlayback).get(Stream.ORIGIN);
    
    System.out.println("Stream hlsPlaybackUrls()");
    System.out.println(hlsPlaybackUrl);
    // http://ey636h.playback1.z1.pili.qiniucdn.com/test-hub/55d8119ee3ba5723280000dd.m3u8?start=1440315411&end=1440315435
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Snapshot Stream
```JAVA
// Snapshot Stream
String format    = "jpg";                      // required
String name      = "imageName" + "." + format; // required
long time        = 1440315411;  // optional, in second, unix timestamp
String notifyUrl = null;        // optional

try {
    SnapshotResponse response = stream.snapshot(name, format, time, notifyUrl);
    System.out.println("Stream snapshot()");
    System.out.println(response.toString());
    /*
     {
         "targetUrl":"http://ey636h.ts1.z1.pili.qiniucdn.com/snapshots/z1.test-hub.55d81a72e3ba5723280000ec/imageName.jpg",
         "persistentId":"z1.55d81c247823de5a49ad729c"
     }
     */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

##### Save Stream as a file
```JAVA
// Save Stream as a file
String saveAsFormat    = "mp4";                            // required
String saveAsName      = "videoName" + "." + saveAsFormat; // required
long saveAsStart       = 1440315411;  // required, in second, unix timestamp
long saveAsEnd         = 1440315435;  // required, in second, unix timestamp
String saveAsNotifyUrl = null;        // optional
try {
    SaveAsResponse response = stream.saveAs(saveAsName, saveAsFormat, saveAsStart, saveAsEnd, saveAsNotifyUrl);
    System.out.println("Stream saveAs()");
    System.out.println(response.toString());
    /*
     {
         "url":"http://ey636h.ts1.z1.pili.qiniucdn.com/recordings/z1.test-hub.55d81a72e3ba5723280000ec/videoName.m3u8",
         "targetUrl":"http://ey636h.ts1.z1.pili.qiniucdn.com/recordings/z1.test-hub.55d81a72e3ba5723280000ec/videoName.mp4",
         "persistentId":"z1.55d81c6c7823de5a49ad77b3"
     }
    */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```
> While invoking `saveAs` and `snapshot`, you can get processing state via Qiniu fop service using persistentId. </p>
> API: `curl -D GET http://api.qiniu.com/status/get/prefop?id=<persistentId>`  </p>
> Doc reference: `http://developer.qiniu.com/docs/v6/api/overview/fop/persistent-fop.html#pfop-status`

##### Delete a stream
```JAVA
// Delete a Stream
try {
    String res = stream.delete();
    System.out.println("Stream delete()");
    System.out.println(res);
    // No Content
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

## History
- 1.5.0
  - Add Stream Create,Get,List
    - hub.createStream()
    - hub.getStream()
    - hub.listStreams()
  - Add Stream operations else
    - stream.toJsonString()
    - stream.update()
    - stream.disable()
    - stream.enable()
    - stream.status()
    - stream.segments()
    - stream.rtmpPublishUrl()
    - stream.rtmpLiveUrls()
    - stream.hlsLiveUrls()
    - stream.httpFlvLiveUrls()
    - stream.hlsPlaybackUrls()
    - stream.snapshot()
    - stream.saveAs()
    - stream.delete()
