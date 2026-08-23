package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.decrpy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;


public class Jpyy extends Spider {
    private String url="https://www.cqzuoer.com";
    private String UA="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36";
    private String keymm = "cb808529bae6b6be45ecfab29a4889bc";
    private  long t = System.currentTimeMillis();
    private String req(String url,Map<String, String> header) {
        return OkHttp.string(url, header);
    }
    private Response req(Request request) throws Exception {
        return okClient().newCall(request).execute();
    }
    private OkHttpClient okClient() {
        return OkHttp.client();
    }
    private String req(Response response) throws Exception {
        if (!response.isSuccessful()) return "";
        String content = response.body().string();
        response.close();
        return content;
    }

    private Map<String, String> getHeader(long t, String sign) {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", UA);
        header.put("Referer", url);
        header.put("t", String.valueOf(t));
        header.put("sign", sign);
        return header;
    }
    private  String generateSign(String md5Input) {
        String md5Hash = decrpy.md5(md5Input);
        if (md5Hash != null) {
            return decrpy.sha1(md5Hash);
        }
        return null;
    }
    @Override
    public String homeContent(boolean filter) throws Exception {
        String md5Input = "key=" + keymm + "&t=" + t;
        String sign = generateSign(md5Input);
        String site=url+"/api/mw-movie/anonymous/home/hotSearch?";
        String html = req(site, getHeader(t, sign));
        JSONObject jsonArray = new JSONObject(html);
        JSONArray videos=new JSONArray();
        JSONArray classes = new JSONArray();
        classes.put(new JSONObject().put("type_id", "1").put("type_name", "电影"));
        classes.put(new JSONObject().put("type_id", "2").put("type_name", "电视"));
        classes.put(new JSONObject().put("type_id", "3").put("type_name", "综艺"));
        classes.put(new JSONObject().put("type_id", "4").put("type_name", "动漫"));
        if (jsonArray.has("data")) {
            JSONArray data = jsonArray.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject  item = data.getJSONObject(i);
                JSONObject vod = new JSONObject();
                vod.put("vod_id", (item.get("vodId")).toString());
                vod.put("vod_name", item.get("vodName").toString());
                vod.put("vod_pic", item.get("vodPic").toString());
                vod.put("vod_remarks", item.get("vodBlurb").toString());
                vod.put("vod_year", item.get("vodPubdate").toString());
                videos.put(vod);
            }
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("list", videos);
        return result.toString();
    }
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateUrl = url + "/api/mw-movie/anonymous/video/list?type1="+tid+"&pageNum="+pg+"&area=&year=";
        String md5Input = "area=&pageNum="+pg+"&type1="+tid+"&year=&key="+keymm+"&t="+t;
        String sign = generateSign(md5Input);
        String html = req(cateUrl, getHeader(t, sign));
        JSONArray list= new JSONObject(html).getJSONObject("data").getJSONArray("list");
        JSONArray videos = new JSONArray();
        for (int i = 0; i < list.length(); i++) {
            JSONObject vod = new JSONObject();
            JSONObject  item = list.getJSONObject(i);
            vod.put("vod_id", item.get("vodId").toString());
            vod.put("vod_name", item.get("vodName").toString());
            vod.put("vod_pic", item.get("vodPic").toString());
            vod.put("vod_remarks", item.get("vodSerial").toString());
            videos.put(vod);
        }
        String count = new JSONObject(html).getJSONObject("data").get("totalPage").toString();
        JSONObject result = new JSONObject();
        result.put("page", Integer.parseInt(pg));
        result.put("pagecount", Integer.parseInt(count));
        result.put("limit", list.length());
        result.put("total", Integer.MAX_VALUE);
        result.put("list", videos);
        return result.toString();
    }
    @Override
    public String detailContent(List<String> ids) throws Exception {
        String igd = ids.get(0);
        System.out.println(igd);
        String site = url + "/api/mw-movie/anonymous/video/detail?id="+igd;
        String md5Input = "id=" + igd +"&key="+keymm+ "&t=" + t;
        String sign = generateSign(md5Input);
        String html = req(site, getHeader(t, sign));
        Map<String, String> playMap = new LinkedHashMap<>();
        JSONObject jsonObject = new JSONObject(html).getJSONObject("data");
        String name = jsonObject.get("vodName").toString();
        String pic = jsonObject.get("vodPic").toString();
        String typeName = jsonObject.get("vodClass").toString();
        String year = jsonObject.get("vodYear").toString();
        String area= jsonObject.get("vodArea").toString();
        String remark = jsonObject.get("vodRemarks").toString();
        String actor = jsonObject.get("vodActor").toString();
        String director = jsonObject.get("vodDirector").toString();
        String description = jsonObject.get("vodContent").toString();
        String circuitName = jsonObject.get("vodVersion").toString();
        JSONArray episodeList = jsonObject.getJSONArray("episodeList");
        List<String> vodItems = new ArrayList<>();
        if (episodeList.length() > 0){
            for (int i = 0; i < episodeList.length(); i++) {
                JSONObject list= episodeList.getJSONObject(i);
                vodItems.add(list.get("name")+"$"+list.get("nid").toString()+"@"+igd);
                if (vodItems.size() > 0) {
               playMap.put(circuitName, TextUtils.join("#", vodItems));
                }
            }
        }

        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name); // 影片名称
        vod.put("vod_pic", pic); // 图片
        vod.put("type_name", typeName); // 影片类型 选填
        vod.put("vod_year", year); // 年份 选填
        vod.put("vod_area", area); // 地区 选填
        vod.put("vod_remarks", remark); // 备注 选填
        vod.put("vod_actor", actor); // 主演 选填
        vod.put("vod_director", director); // 导演 选填
        vod.put("vod_content", description); // 简介 选填
       if (playMap.size() > 0) {
            vod.put("vod_play_from", TextUtils.join("$$$", playMap.keySet()));
            vod.put("vod_play_url", TextUtils.join("$$$", playMap.values()));
       }
        JSONArray jsonArray = new JSONArray().put(vod);
        JSONObject result = new JSONObject().put("list", jsonArray);
        return result.toString();
    }
    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }
    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String site = url + "/api/mw-movie/anonymous/video/searchByWord?keyword="+key+"&pageNum="+pg+"&pageSize=12&sourceCode=1";
        String md5Input = "keyword="+key+"&pageNum="+pg+"&pageSize=12&sourceCode=1&key="+keymm+"&t="+t;
        String sign = generateSign(md5Input);
        JSONArray data =new JSONObject(req(site, getHeader(t, sign))).getJSONObject("data").getJSONObject("result").getJSONArray("list");
        JSONArray videos=new JSONArray();
        System.out.println(data);
            for (int i = 0; i < data.length(); i++) {
                JSONObject  item = data.getJSONObject(i);
                JSONObject vod = new JSONObject();
                vod.put("vod_id", (item.get("vodId")).toString());
                vod.put("vod_name", item.get("vodName").toString());
                vod.put("vod_pic", item.get("vodPic").toString());
                vod.put("vod_remarks", item.get("vodBlurb").toString());
                vod.put("vod_year", item.get("vodPubdate").toString());
                videos.put(vod);
            }

        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts= id.split("@");
        String nid = parts[0].trim();
        String vid = parts[1].trim();
        String site = url + "/api/mw-movie/anonymous/v1/video/episode/url?id="+vid+"&nid="+nid;
        String md5Input = "id="+vid+"&nid="+nid+"&key="+keymm+"&t="+ t;
        String sign = generateSign(md5Input);
        String html = req(site, getHeader(t, sign));
        String url = new JSONObject(html).getJSONObject("data").get("playUrl").toString();
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("header", "");
        result.put("playUrl", "");
        result.put("url", url);
        return result.toString();
    }
   
}
