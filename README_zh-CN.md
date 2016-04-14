# PILI直播 JAVA服务端SDK 使用指南

## 功能列表

- 直播流的创建、获取和列举
    - [x] hub.createStream()  // 创建流
    - [x] hub.getStream()  // 获取流
    - [x] hub.listStreams()  // 列举流
- 直播流的其他功能
    - [x] stream.toJsonString()  // 流信息转为json
    - [x] stream.update()      // 更新流
    - [x] stream.disable()      // 禁用流
    - [x] stream.enable()    // 启用流
    - [x] stream.status()     // 获取流状态
    - [x] stream.rtmpPublishUrl()   // 生成推流地址
    - [x] stream.rtmpLiveUrls()    // 生成rtmp播放地址
    - [x] stream.hlsLiveUrls()   // 生成hls播放地址
    - [x] stream.httpFlvLiveUrls()   // 生成flv播放地址
    - [x] stream.segments()      // 获取流片段
    - [x] stream.hlsPlaybackUrls()  // 生成hls回看地址
    - [x] stream.saveAs()        // 流另存为文件
    - [x] stream.snapshot()      // 获取快照
    - [x] stream.delete()    // 删除流

## 目录
- [安装](#installation)
- [依赖包](#dependencies)
- [运行要求](#runtime-requirements)
- [用法](#usage)
    - [配置](#configuration)
    - [Hub](#hub)
        - [实例化hub对象](#instantiate-a-pili-hub-object)
        - [创建流](#create-a-new-stream)
        - [获取流](#get-an-exist-stream)
        - [列举流](#list-streams)
    - [直播流](#stream)
        - [流信息转为json](#to-json-string)
        - [更新流](#update-a-stream)
        - [禁用流](#disable-a-stream)
        - [启用流](#enable-a-stream)
        - [获取流状态](#get-stream-status)
        - [生成推流地址](#generate-rtmp-publish-url)
        - [生成rtmp播放地址](#generate-rtmp-live-play-urls)
        - [生成hls播放地址](#generate-hls-play-urls)
        - [生成flv播放地址](#generate-http-flv-live-play-urls)
        - [获取流片段](#get-stream-segments)
        - [生成hls回看地址](#generate-hls-playback-urls)
        - [流另存为文件](#save-stream-as-a-file)
        - [获取快照](#snapshot-stream)
        - [删除流](#delete-a-stream)
- [History](#history)

<a id="installation"></a>
### 安装

你可以在当前发布的页面中下载 **pili-sdk-java-v1.5.0.jar** 
或者使用 gradle: `compile 'com.qiniu:pili-sdk-java:1.5.3'`
<a id="dependencies"></a>
### 依赖包

你需要下载并依赖 [okhttp][1], [okio][2], [Gson][3]

[1]: http://square.github.io/okhttp/
[2]: https://github.com/square/okio
[3]: https://code.google.com/p/google-gson/downloads/detail?name=google-gson-2.2.4-release.zip&
<a id="runtime-requirements"></a>
### 运行要求

最低要求JDK 1.7

如果你需要在1.6的JDK下使用, 你需要下载并依赖这些jar包: [okhttp](https://raw.githubusercontent.com/qiniu/java-sdk/master/libs/okhttp-2.3.0-SNAPSHOT.jar) 和 [okio](https://raw.githubusercontent.com/qiniu/java-sdk/master/libs/okio-1.3.0-SNAPSHOT.jar).
<a id="usage"></a>
### 用法
<a id="configuration"></a>
#### 配置

```JAVA
  // 替换为你的AK和SK
  public static final String ACCESS_KEY = "Qiniu_AccessKey";
  public static final String SECRET_KEY = "Qiniu_SecretKey";
  
  // 替换为你的HUB
  public static final String HUB = "Pili_Hub_Name"; // The Hub must be exists before use
  
  // 如果需要可以更改 API host
  //
  // 默认为 pili.qiniuapi.com
  // pili-lte.qiniuapi.com 为最新版本
  //
  // static {
  //    Configuration.getInstance().setAPIHost("pili.qiniuapi.com"); // 默认
  // }
```

#### Hub
<a id="instantiate-a-pili-hub-object"></a>
##### 实例化hub对象
```JAVA
  // 实例化hub对象
  Credentials credentials = new Credentials(ACCESS_KEY, SECRET_KEY); 
  Hub hub = new Hub(credentials, HUB_NAME); 
```
<a id="create-a-new-stream"></a>
##### 创建流

```JAVA
// Create a new Stream
  String title           = null;     // 可选，默认自动生成
  String publishKey      = null;     // 可选，默认自动生成
  String publishSecurity = null;     // 可选, 可以为 "dynamic" 或 "static", 默认为 "dynamic"
  Stream stream = null;
  try {
      stream = hub.createStream(title, publishKey, publishSecurity);
      System.out.println("hub.createStream:");
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
                   "http":"ey636h.playback1.z1.pili.qiniucdn.com"
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
<a id="get-an-exist-stream"></a>
##### 获取流

```JAVA
  String streamId = stream.getStreamId();
  try {
    stream = hub.getStream(streamId);
    System.out.println("hub.getStream:");
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
                 "http":"ey636h.playback1.z1.pili.qiniucdn.com"
             }
         }
     }
     */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```
<a id="list-streams"></a>
##### 列举流

```JAVA
  try {
      String marker      = null;      // 可选
      long limit         = 0;         // 可选
      String titlePrefix = null;      // 可选

      StreamList streamList = hub.listStreams(marker, limit, titlePrefix);
      System.out.println("hub.listStreams()");
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
    StreamList list = hub.listStreams();
    if (list != null) {
        for (Stream stream : list.getStreams()) {
            printStream(stream);
        }
    }
  } catch (PiliException e) {
    e.printStackTrace();
  }
```
<a id="stream"></a>
#### 直播流
<a id="to-json-string"></a>
##### 流信息转为json

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
                 "http":"ey636h.playback1.z1.pili.qiniucdn.com"
             }
         }
     }
 */
```
<a id="update-a-stream"></a>
##### 更新流

```JAVA
// 更新流属性，可以更改PublishKey、PublishSecurity以及Disabled状态
String newPublishKey       = "new_secret_words"; // 可选
String newPublishSecurity  = "static";           // 可选, 可以是"dynamic" 或 "static"
boolean newDisabled        = true;               // 可选, 可以是"true" 或 "false"
try {
    Stream newStream = stream.update(newPublishKey, newPublishSecurity, newDisabled);
    System.out.println("Stream update()");
    System.out.println(newStream.toJsonString());
    stream = newStream;
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
             }
         }
     }
 */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```
<a id="disable-a-stream"></a>
##### 禁用流

```JAVA
// 禁用流
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
<a id="enable-a-stream"></a>
##### 启用流

```JAVA
// 启用流
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
<a id="get-stream-status"></a>
##### 获取流状态

```JAVA
// 获取流的状态判定推流端是否断开
try {
    Status status = stream.status();
    System.out.println("Stream status()");
    System.out.println(status.toString());
    /*
    {
        "addr":"222.73.202.226:2572",
        "status":"connected",
        "bytesPerSecond":16870.200000000001,
        "framesPerSecond":{
            "audio":42.200000000000003,
            "video":14.733333333333333,
            "data":0.066666666666666666
         }
     }
    */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```
<a id="generate-rtmp-publish-url"></a>
##### 生成推流地址

```JAVA
// 生成推流地址
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
<a id="generate-rtmp-live-play-urls"></a>
##### 生成rtmp播放地址

```JAVA
// 生成rtmp播放地址
String originUrl = stream.rtmpLiveUrls().get(Stream.ORIGIN);
System.out.println("Stream rtmpLiveUrls()");
System.out.println(originUrl);
// rtmp://ey636h.live1-rtmp.z1.pili.qiniucdn.com/test-hub/55d8113ee3ba5723280000dc
```
<a id="generate-hls-play-urls"></a>
##### 生成hls播放地址

```JAVA
// 生成hls播放地址
String originLiveHlsUrl = stream.hlsLiveUrls().get(Stream.ORIGIN);
System.out.println("Stream hlsLiveUrls()");
System.out.println(originLiveHlsUrl);
// http://ey636h.live1-http.z1.pili.qiniucdn.com/test-hub/55d8119ee3ba5723280000dd.m3u8
```
<a id="generate-http-flv-live-play-urls"></a>
##### 生成flv播放地址

```JAVA
// 生成flv播放地址
String originLiveFlvUrl = stream.httpFlvLiveUrls().get(Stream.ORIGIN);
System.out.println("Stream httpFlvLiveUrls()");
System.out.println(originLiveFlvUrl);
// http://ey636h.live1-http.z1.pili.qiniucdn.com/test-hub/55d8119ee3ba5723280000dd.flv
```
<a id="get-stream-segments"></a>
##### 获取流片段

```JAVA
// 获取直播流每次推流开始和结束时间点的集合
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
<a id="generate-hls-playback-urls"></a>
##### 生成hls回看地址

```JAVA
// 生成hls回看地址
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
<a id="save-stream-as-a-file"></a>
##### 流另存为文件

```JAVA
// 将流另存为一个文件
String saveAsFormat    = "mp4";                            // required
String saveAsName      = "videoName" + "." + saveAsFormat; // required
long saveAsStart       = 1440315411;                       // required, in second, unix timestamp
long saveAsEnd         = 1440315435;                       // required, in second, unix timestamp
String saveAsNotifyUrl = null;                             // optional
try {
    SaveAsResponse response = stream.saveAs(saveAsName, saveAsFormat, saveAsStart, saveAsEnd, saveAsNotifyUrl);
    System.out.println("Stream saveAs()");
    System.out.println(response.toString());
    /*
     {
         "url":"http://ey636h.vod1.z1.pili.qiniucdn.com/recordings/z1.test-hub.55d81a72e3ba5723280000ec/videoName.m3u8",
         "targetUrl":"http://ey636h.vod1.z1.pili.qiniucdn.com/recordings/z1.test-hub.55d81a72e3ba5723280000ec/videoName.mp4",
         "persistentId":"z1.55d81c6c7823de5a49ad77b3"
     }
    */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```

当使用 `saveAs()` 和 `snapshot()` 的时候, 由于是异步处理， 你可以在七牛的FOP接口上使用 `persistentId`来获取处理进度.参考如下：   
API: `curl -D GET http://api.qiniu.com/status/get/prefop?id={persistentId}`  
文档说明: <http://developer.qiniu.com/docs/v6/api/overview/fop/persistent-fop.html#pfop-status>  
<a id="snapshot-stream"></a>
##### 获取快照

```JAVA
// Snapshot Stream
String format    = "jpg";                      // 必须
String name      = "imageName" + "." + format; // 必须
long time        = 1440315411;                 // 可选, 单位是秒, 数值是unix时间戳
String notifyUrl = null;                       // 可选

try {
    SnapshotResponse response = stream.snapshot(name, format, time, notifyUrl);
    System.out.println("Stream snapshot()");
    System.out.println(response.toString());
    /*
     {
         "targetUrl":"http://ey636h.static1.z1.pili.qiniucdn.com/snapshots/z1.test-hub.55d81a72e3ba5723280000ec/imageName.jpg",
         "persistentId":"z1.55d81c247823de5a49ad729c"
     }
     */
} catch (PiliException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
```
<a id="delete-a-stream"></a>
##### 删除流

```JAVA
// 删除流
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
