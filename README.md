# Pili Streaming Cloud Server-Side Library For JAVA

## Features

- URL
	- [x] RTMP推流地址: client.RTMPPublishURL(domain, hub, streamKey, expireAfterDays)
	- [x] RTMP直播地址: RTMPPlayURL(domain, hub, streamKey)
	- [x] HLS直播地址: HLSPlayURL(domain, hub, streamKey)
	- [x] HDL直播地址: HDLPlayURL(domain, hub, streamKey)
	- [x] 截图直播地址: SnapshotPlayURL(domain, hub, streamKey)
- Hub
	- [x] 创建流: hub.create(streamKey)
	- [x] 查询流: hub.get(streamKey)
	- [x] 列出流: hub.list(prefix, limit, marker)
	- [x] 列出正在直播的流: hub.listLive(prefix, limit, marker)
- Stream
	- [x] 禁用流: stream.disable()
	- [x] 启用流: stream.enable()
 	- [x] 查询直播状态: stream.liveStatus()
	- [x] 保存直播回放: stream.save(key, start, end)
	- [x] 查询直播历史: stream.historyRecord(start, end)

## Contents

- [Installation](#installation)
- [Usage](#usage)
    - [Configuration](#configuration)
	- [URL](#url)
		- [Generate RTMP publish URL](#generate-rtmp-publish-url)
		- [Generate RTMP play URL](#generate-rtmp-play-url)
		- [Generate HLS play URL](#generate-hls-play-url)
		- [Generate HDL play URL](#generate-hdl-play-url)
		- [Generate Snapshot play URL](#generate-snapshot-play-url)
	- [Hub](#hub)
		- [Instantiate a Pili Hub object](#instantiate-a-pili-hub-object)
		- [Create a new Stream](#create-a-new-stream)
		- [Get a Stream](#get-a-stream)
		- [List Streams](#list-streams)
		- [List live Streams](#list-live-streams)
	- [Stream](#stream)
		- [Disable a Stream](#disable-a-stream)
		- [Enable a Stream](#enable-a-stream)
		- [Get Stream live status](#get-stream-live-status)
		- [Get Stream history record](#get-stream-history-record)
		- [Save Stream live playback](#save-stream-live-playback)

## Java version

The project is built with java 1.7.

## Compile JAR

Firstly, make sure you have [gradle](http://gradle.org/gradle-download/) on your machine.

Then all you have to do is just 

```
gradle build
```

## Dependencies

`okhttp`, `okio`, `Gson`

## Install via gradle

```
compile 'com.qiniu.pili:pili-sdk-java:2.0.0'
```

## Usage

### Init

```java
Client cli = new Client(accessKey,secretKey);
```

### URL

#### Generate RTMP publish URL

```java
String url = cli.RTMPPublishURL("publish-rtmp.test.com", "PiliSDKTest", "streamkey", 60);
/*
rtmp://publish-rtmp.test.com/PiliSDKTest/streamkey?e=1463023142&token=7O7hf7Ld1RrC_fpZdFvU8aCgOPuhw2K4eapYOdII:-5IVlpFNNGJHwv-2qKwVIakC0ME=
*/
```

#### Generate RTMP play URL

```java
String url = cli.RTMPPlayURL("live-rtmp.test.com", "PiliSDKTest", "streamkey");
/*
rtmp://live-rtmp.test.com/PiliSDKTest/streamkey
*/
```

#### Generate HLS play URL

```java
url = cli.HLSPlayURL("live-hls.test.com", "PiliSDKTest", "streamkey");
/*
http://live-hls.test.com/PiliSDKTest/streamkey.m3u8
*/
```

#### Generate HDL play URL

```java
url = cli.HDLPlayURL("live-hdl.test.com", "PiliSDKTest", "streamkey");
/*
http://live-hdl.test.com/PiliSDKTest/streamkey.flv
*/
```

#### Generate Snapshot play URL

```java
url = cli.SnapshotPlayURL("live-snapshot.test.com", "PiliSDKTest", "streamkey");
/*
http://live-snapshot.test.com/PiliSDKTest/streamkey.jpg
*/
```

### Hub

#### Instantiate a Pili Hub object

```java
public static void main(String args[]) { 
	Client cli = new Client(accessKey, secretKey);
	Hub hub = cli.newHub("PiliSDKTest");
	// ...
}
```

#### Create a new Stream

```java
Stream stream = hub.create("streamkey")
System.out.println(stream.toJson());
/*
{"Hub":"PiliSDKTest","Key":"streamkey","DisabledTill":0}
*/
```

#### Get a Stream

```java
Stream stream = hub.get("streamkey")
System.out.println(stream.toJson())
/*
{"Hub":"PiliSDKTest","Key":"streamkey","DisabledTill":0}
*/
```

#### List Streams

```java
Hub.ListRet listRet = hub.list("str", 10, "")
/*
keys=[streamkey] marker=
*/
```

#### List live Streams

```java
Hub.ListRet listRet = hub.listLive("str", 10, "")
/*
keys=[] marker=
*/
```

### Stream

#### Disable a Stream

```java
Stream stream = hub.get("streamkey")
stream.disable()
stream = hub.get("streamkey")
/*
before disable: {"Hub":"PiliSDKTest","Key":"streamkey","DisabledTill":0}
after disable: {"Hub":"PiliSDKTest","Key":"streamkey","DisabledTill":-1}
*/
```


#### Enable a Stream

```java
Stream stream = hub.get("streamkey")
stream.enable()
stream = hub.get("streamkey")
/*
before disable: {"Hub":"PiliSDKTest","Key":"streamkey","DisabledTill":-1}
after disable: {"Hub":"PiliSDKTest","Key":"streamkey","DisabledTill":0}
*/
```

#### Get Stream live status

```java
Stream.LiveStatus status = stream.liveStatus();
/*
{"startAt":1463022236,"clientIP":"222.73.202.226","bps":248,"fps":{"audio":45,"vedio":28,"data":0}}
*/
```

#### Get Stream history record

```java
Stream.Record[] records = stream.historyRecord(0, 0)
/*
[{1463022236,1463022518}]
*/
```

#### Save Stream live playback

```java
String fname = stream.save(0, 0)
/*
FtVdro8JYNwq4uVFALsKGWDRVaiN
*/
```
