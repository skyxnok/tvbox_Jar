package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.decrpy;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;



public class Mb extends Spider {
    private String url="https://app.omofun1.top";
    private String UA="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36";
    private String dm(String data) throws Exception {
       return decrpy.decrypt(data);
    }
    private String em(String data) throws Exception {
        return decrpy.encrypt(data);
    }
    private String req(String url, Map<String, String> headers) throws Exception {
       return OkHttp.string(url, headers);
    }
    private Map<String, String> getheader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", UA);
        header.put("referer", url);
        return header;
    }
    @Override
    public String homeContent(boolean filter) throws Exception {
        String site=url+"/api.php/getappapi.index/initV119";
        String html = new JSONObject(req(site,getheader())).get("data").toString();
        JSONObject data = new JSONObject(dm(html));
        JSONArray obj = data.getJSONArray("type_list");
        JSONArray videos=new JSONArray();
        JSONArray classes = new JSONArray();
        for (int i = 0; i < obj.length(); i++) {
            String type_id=obj.getJSONObject(i).get("type_id").toString();
            if(!type_id.equals("0")){
                classes.put(new JSONObject().put("type_id",type_id).put("type_name",obj.getJSONObject(i).getString("type_name")));
            }
        }
        JSONArray video = data.getJSONArray("banner_list");
            for (int i = 0; i < video.length(); i++) {
                JSONObject  item = video.getJSONObject(i);
                JSONObject vod = new JSONObject();
                vod.put("vod_id", item.get("vod_id").toString());
                vod.put("vod_name", item.get("vod_name").toString());
                vod.put("vod_pic", item.get("vod_pic").toString());
                vod.put("vod_remarks", item.get("vod_remarks").toString());
                videos.put(vod);
            }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("list", videos);
        return result.toString();
    }
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cateUrl = url + "/api.php/getappapi.index/typeFilterVodList?area=全部&year=全部&type_id="+tid+"&&page="+pg+"&sort=最新&class=全部";
        String html = new JSONObject(req(cateUrl,getheader())).get("data").toString();
        JSONArray list = new JSONObject(dm(html)).getJSONArray("recommend_list");
        JSONArray videos = new JSONArray();
        for (int i = 0; i < list.length(); i++) {
            JSONObject vod = new JSONObject();
            JSONObject  item = list.getJSONObject(i);
            vod.put("vod_id", item.get("vod_id").toString());
            vod.put("vod_name", item.get("vod_name").toString());
            vod.put("vod_pic", item.get("vod_pic").toString());
            vod.put("vod_remarks", item.get("vod_remarks").toString());
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("page", Integer.parseInt(pg));
        result.put("pagecount", 999);
        result.put("limit", list.length());
        result.put("total", Integer.MAX_VALUE);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String igd = ids.get(0);
        String site = url + "/api.php/getappapi.index/vodDetail?vod_id="+igd;
        String html = new JSONObject(req(site,getheader())).get("data").toString();
       JSONArray list= new JSONObject(dm(html)).getJSONArray("vod_play_list");
       String vod_play_from="";
       String vod_play_url="";
       System.out.println("data:"+dm(html));
       for (int i = 0; i < list.length(); i++) {
           JSONObject item = list.getJSONObject(i).getJSONObject("player_info");
           String p=item.get("parse").toString();
           JSONArray vod1 = list.getJSONObject(i).getJSONArray("urls");
           String play_url="";
           for(int j=0;j<vod1.length();j++){
               String ur;
               String vod_name = vod1.getJSONObject(j).getString("name");
               String vod_url = vod1.getJSONObject(j).getString("url");
               if(p.isEmpty()){
                   ur=vod_url;

               }else {
                   if (p.contains("php?url=")||p.contains("cycanime")) {
                       ur =p+vod_url;
                   }else {
                       ur=p+"|"+vod_url;
                   }
               }
               if(j==vod1.length()-1){
                   play_url+=vod_name+"$"+ur;
               }else {
                   play_url+=vod_name+"$"+ur+"#";
               }

           }
           if(i==list.length()-1){
               vod_play_from+=item.get("show").toString();
               vod_play_url+=play_url;
           }else {
               vod_play_from+=item.get("show").toString()+"$$$";
               vod_play_url+=play_url+"$$$";
           }

       }
        vod_play_from=vod_play_from.replace("oi/失败就重启软件", "异次元");
        JSONObject jsonObject=new JSONObject(dm(html)).getJSONObject("vod");
        String name = jsonObject.get("vod_name").toString();
        String year = jsonObject.get("vod_year").toString();
        String area= jsonObject.get("vod_area").toString();
        String remark = jsonObject.get("vod_remarks").toString();
        String description = jsonObject.get("vod_content").toString();
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", name); // 影片名称
        vod.put("vod_year", year); // 年份 选填
        vod.put("vod_area", area); // 地区 选填
        vod.put("vod_remarks", remark); // 备注 选填
        vod.put("vod_content", description); // 简介 选填
        vod.put("vod_play_from", vod_play_from);
        vod.put("vod_play_url", vod_play_url);
        JSONArray jsonArray = new JSONArray().put(vod);
        JSONObject result = new JSONObject().put("list", jsonArray);
        return result.toString();
    }
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        if(id.contains("cycanime")){
            result.put("parse", 1);
            result.put("header", "");
            result.put("playUrl", "");
            result.put("url", id);
        }else if(id.contains("|")){
            String[] teyp= id.split("\\|");
            String jm=em(teyp[1]);
            String data=new JSONObject(req(url+"/api.php/getappapi.index/vodParse?parse_api="+teyp[0]+"&url="+jm,getheader())).get("data").toString();
            String url =new JSONObject(dm(data)).getString("json");
            url=new JSONObject(url).getString("url");
            result.put("parse", 0);
            result.put("header", "");
            result.put("playUrl", "");
            result.put("url", url);
        }else if (id.contains("php?url=dm295")){
            String data=req(id,getheader());
            System.out.println("data:"+data);
            String url=new JSONObject(data).get("url").toString();
            result.put("parse", 0);
            result.put("header", "");
            result.put("playUrl", "");
            result.put("url", url);
        }else {
            result.put("parse", 0);
            result.put("header", "");
            result.put("playUrl", "");
            result.put("url", id);
        }
        return result.toString();
    }
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }


    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String data=req(url+"/api.php/getappapi.index/searchList?keywords="+key+"&type_id=0&page="+pg,getheader());
        JSONArray res=  new JSONObject(dm(new JSONObject(data).getString("data"))).getJSONArray("search_list");
        JSONArray videos=new JSONArray();
        for(int i=0;i<res.length();i++){
            JSONObject item = res.getJSONObject(i);
            JSONObject vod = new JSONObject();
            vod.put("vod_id", item.get("vod_id").toString());
            vod.put("vod_name", item.get("vod_name").toString());
            vod.put("vod_pic", item.get("vod_pic").toString());
            vod.put("vod_remarks", item.get("vod_remarks").toString());
            videos.put(vod);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }
}