
import java.util.ArrayList;
import java.util.Date;
import com.qiniu.pili.*;

public class Example {
    public static void main(String[] args) {

        String accessKey = "7O7hf7Ld1RrC_fpZdFvU8aCgOPuhw2K4eapYOdII";
        String secretKey = "6Rq7rMSUHHqOgo0DJjh15tHsGUBEH9QhWqqyj4ka";
        String streamKeyPrefix = "javasdkexample" + String.valueOf(new Date().getTime());

        String hubName = "PiliSDKTest";
        Config.APIHost = "10.200.20.28:7778";

        //初始化client
        Client cli = new Client(accessKey,secretKey);

        //初始化Hub
        Hub hub = cli.newHub("PiliSDKTest");

        //获得不存在的流
        String keyA = streamKeyPrefix + "A";
        try{
            hub.get(keyA);
        }catch (PiliException e){
            if (e.isNotFound()) {
                System.out.printf("Stream %s doesn't exist\n", keyA);
            }else {
                System.out.println(keyA + " should not exist");
                e.printStackTrace();
                return;
            }
        }
        System.out.printf("keyA=%s 不存在\n",keyA);

        //创建流
        try {
            hub.create(keyA);
        }catch (PiliException e){
            e.printStackTrace();
            return;
        }
        System.out.printf("keyA=%s 创建\n", keyA);

        //获得流
        Stream streamA;
        try{
            streamA = hub.get(keyA);
        }catch (PiliException e){
            e.printStackTrace();
            return;
        }
        System.out.printf("keyA=%s 查询: %s\n", keyA, streamA.toJson());

        //创建重复的流
        try {
            hub.create(keyA);
        } catch (PiliException e) {
            if (!e.isDuplicate()){
                e.printStackTrace();
                return;
            }
        }
        System.out.printf("keyA=%s 已存在\n", keyA);

        //创建另一路流
        String keyB = streamKeyPrefix + "B";
        Stream streamB;
        try{
            streamB = hub.create(keyB);
        }catch(PiliException e){
            e.printStackTrace();
            return;
        }
        System.out.printf("keyB=%s 创建: %s\n", keyB, streamB.toJson());

        //列出所有流
        try{
            Hub.ListRet listRet = hub.list(streamKeyPrefix, 0, "");
            System.out.printf("hub=%s 列出流: keys=%s marker=%s\n", hubName,printArrary(listRet.keys) , listRet.omarker);
        }catch (PiliException e){
            e.printStackTrace();
            return;
        }

        //列出正在直播的流
        try{
            Hub.ListRet listRet = hub.listLive(streamKeyPrefix, 0, "");
            System.out.printf("hub=%s 列出正在直播的流: keys=%s marker=%s\n", hubName, printArrary(listRet.keys), listRet.omarker);
        }catch (PiliException e){
            e.printStackTrace();
            return;
        }

        //禁用流
        try{
            streamA.disable();
            streamA = hub.get(keyA);
        }catch (PiliException e ){
            e.printStackTrace();
            return;
        }
        System.out.printf("keyA=%s 禁用: %s\n", keyA, streamA.toJson());

        //启用流
        try{
            streamA.enable();
            streamA = hub.get(keyA);
        }catch (PiliException e ){
            e.printStackTrace();
            return;
        }
        System.out.printf("keyA=%s 启用: %s\n", keyA, streamA.toJson());

        //查询直播状态
        try{
            Stream.LiveStatus status = streamA.liveStatus();
            System.out.printf("keyA=%s 直播状态: status=%s\n", keyA, status.toJson());
        }catch (PiliException e){
            if (!e.isNotInLive()) {
                e.printStackTrace();
                return;
            }else{
                System.out.printf("keyA=%s 不在直播\n",keyA);
            }
        }

        //查询推流历史
        Stream.Record[] records;
        try{
            records = streamA.historyRecord(0, 0);
        }catch (PiliException e){
            e.printStackTrace();
            return;
        }
        System.out.printf("keyA=%s 推流历史: records=%s\n", keyA, printArrary(records));

        //保存直播数据
        String fname = null;
        try {
            fname = streamA.save(0,0);
        }catch (PiliException e){
            if (!e.isNotInLive()) {
                e.printStackTrace();
                return;
            }else{
                System.out.printf("keyA=%s 不在直播\n",keyA);
            }
        }
        System.out.printf("keyA=%s 保存直播数据: fname=%s\n", keyA, fname);

        // RTMP推流地址
        String url = cli.RTMPPublishURL("publish-rtmp.test.com", hubName, keyA, 3600);
        System.out.printf("keyA=%s RTMP推流地址=%s\n", keyA, url);

        //RTMP直播地址
        url = cli.RTMPPlayURL("live-rtmp.test.com", hubName, keyA);
        System.out.printf("keyA=%s RTMP直播地址=%s\n", keyA, url);

        //HLS直播地址
        url = cli.HLSPlayURL("live-hls.test.com", hubName, keyA);
        System.out.printf("keyA=%s HLS直播地址=%s\n", keyA, url);

        //HDL直播地址
        url = cli.HDLPlayURL("live-hdl.test.com", hubName, keyA);
        System.out.printf("keyA=%s HDL直播地址=%s\n", keyA, url);

        // 截图直播地址
        url = cli.SnapshotPlayURL("live-snapshot.test.com", hubName, keyA);
        System.out.printf("keyA=%s 截图直播地址=%s\n", keyA, url);


    }

    private static String printArrary(Object[] arr){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object a : arr){
            sb.append(a.toString()+" ");
        }
        sb.append("]");
        return sb.toString();
    }

}
