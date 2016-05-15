package com.qiniu.pili;

import com.google.gson.Gson;

import javax.xml.bind.DatatypeConverter;

public class Hub {

    private RPC cli;
    private String hub;
    private String baseUrl;
    private Gson gson;

    private Hub() {
    }

    Hub(RPC cli, String hub) {
        this.cli = cli;
        this.hub = hub;
        this.baseUrl = Config.APIHTTPScheme + Config.APIHost + "/v2/hubs/" + hub;
        this.gson = new Gson();
    }

    /*
        Create
     */
    private class CreateArgs {
        String key;

        public CreateArgs(String key) {
            this.key = key;
        }
    }

    public Stream create(String streamKey) throws PiliException {
        String path = this.baseUrl + "/streams";
        CreateArgs args = new CreateArgs(streamKey);
        String json = gson.toJson(args);

        try {
            cli.callWithJson(path, json);
            StreamInfo streamInfo = new StreamInfo(hub, streamKey, 0);
            return new Stream(streamInfo, cli);
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }
    }

    /*
        Get
     */
    private class GetRet {
        long disabledTill;
    }

    public Stream get(String streamKey) throws PiliException {

        try {
            String ekey = DatatypeConverter.printBase64Binary(streamKey.getBytes("UTF-8"));
            String path = baseUrl + "/streams/" + ekey;

            String resp = cli.callWithGet(path);
            GetRet ret = gson.fromJson(resp, GetRet.class);
            StreamInfo streamInfo = new StreamInfo(hub, streamKey, ret.disabledTill);
            return new Stream(streamInfo, cli);
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

    }

    /*
        List
     */
    public class ListRet {
        public String[] keys;
        public String omarker;
    }

    private class ListItem {
        String key;
    }

    private class ApiRet {
        ListItem[] items;
        String marker;
    }

    private ListRet list(boolean live, String prefix, int limit, String marker) throws PiliException {
        String path = String.format("%s/streams?liveonly=%s&prefix=%s&limit=%d&marker=%s", baseUrl, live, prefix, limit, marker);
        try {
            String resp = cli.callWithGet(path);

            ApiRet ret = gson.fromJson(resp, ApiRet.class);

            ListRet listRet = new ListRet();
            listRet.keys = new String[ret.items.length];
            for (int i = 0; i < ret.items.length; i++) {
                listRet.keys[i] = ret.items[i].key;
            }
            listRet.omarker = ret.marker;
            return listRet;
        } catch (PiliException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PiliException(e);
        }

    }

    public ListRet list(String prefix, int limit, String marker) throws PiliException {
        return list(false, prefix, limit, marker);
    }

    public ListRet listLive(String prefix, int limit, String marker) throws PiliException {
        return list(true, prefix, limit, marker);
    }


}
