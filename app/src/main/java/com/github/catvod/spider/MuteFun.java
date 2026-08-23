package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuteFun extends Spider {

    private static final String HOST = "https://go.5idm.top";
    private static final String KEY = "b04089bdeffe24ccea1df4ed16205e23";

    private JSONObject request(String url, JSONObject body) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("User-Agent", "Dart/3.5 (dart:io)");
        String content;
        if (body != null) {
            content = OkHttp.post(url, body.toString(), headers).getBody();
        } else {
            content = OkHttp.string(url, headers);
        }
        String data = new JSONObject(content).optString("data");
        return new JSONObject(JUtil.aesDecryptEcb(data, KEY));
    }

    private JSONObject vod(String id) throws Exception {
        JSONObject v = new JSONObject();
        v.put("vod_id", id);
        return v;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(HOST + "/app/api/config?platform=android", null);
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray types = html.getJSONArray("ac_vod_type");
        for (int i = 0; i < types.length(); i++) {
            JSONObject tp = types.getJSONObject(i);
            if (tp.optString("type_id").isEmpty()) continue;
            classes.put(vod(tp.optString("type_id")).put("type_name", tp.optString("type_name")));
            JSONArray filters = new JSONArray();
            JSONObject extend = tp.optJSONObject("type_extend");
            if (extend != null) {
                JSONArray classFilters = new JSONArray();
                for (String v : extend.optString("class", "").split(",")) {
                    if (!v.isEmpty()) classFilters.put(new JSONObject().put("n", v).put("v", v));
                }
                if (classFilters.length() > 0) {
                    filters.put(new JSONObject().put("key", "class").put("name", "类型").put("value", classFilters));
                }
                JSONArray yearFilters = new JSONArray();
                for (String v : extend.optString("year", "").split(",")) {
                    if (!v.isEmpty()) yearFilters.put(new JSONObject().put("n", v).put("v", v));
                }
                if (yearFilters.length() > 0) {
                    filters.put(new JSONObject().put("key", "year").put("name", "年份").put("value", yearFilters));
                }
            }
            JSONArray sorts = new JSONArray();
            sorts.put(new JSONObject().put("n", "最新").put("v", "0"));
            sorts.put(new JSONObject().put("n", "热度").put("v", "1"));
            sorts.put(new JSONObject().put("n", "好评").put("v", "2"));
            filters.put(new JSONObject().put("key", "sort").put("name", "排序").put("value", sorts));
            filterObj.put(tp.optString("type_id"), filters);
        }
        return new JSONObject().put("class", classes).put("filters", filterObj).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "0";
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "";
        String cls = extend != null && extend.containsKey("class") ? extend.get("class") : "";
        JSONObject html = request(HOST + "/app/api/content/filter?type=" + tid + "&page=" + pg + "&sort=" + sort
                + "&year=" + year + "&class=" + cls, null);
        JSONArray videos = new JSONArray();
        JSONArray list = html.optJSONArray("filter_vods");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String pic = item.optString("vod_pic");
                videos.put(vod(item.optString("id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", pic + "@Referer=" + pic)
                        .put("vod_remarks", item.optString("vod_remarks")));
            }
        }
        return new JSONObject().put("page", Integer.parseInt(pg)).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = request(HOST + "/app/api/vod/" + ids.get(0), null);
        JSONArray playerData = html.getJSONArray("playerData");
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < playerData.length(); i++) {
            JSONObject play = playerData.getJSONObject(i);
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            playFrom.append(play.optString("name")).append(" [").append(play.optString("player")).append("]");
            JSONArray vids = play.getJSONArray("vids");
            for (int j = 0; j < vids.length(); j++) {
                if (j > 0) playUrl.append("#");
                String vid = vids.optString(j);
                playUrl.append(vid.contains("$") ? vid : "第" + (j + 1) + "集$" + vid).append("@@").append(play.optString("player"));
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", html.optString("vod_name"));
        vod.put("vod_pic", html.optString("vod_pic"));
        vod.put("vod_year", html.optString("vod_year"));
        vod.put("vod_remarks", html.optString("vod_remarks"));
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_content", html.optString("vod_content").replaceAll("<.*?>", ""));
        vod.put("vod_play_from", playFrom.toString().replace("-首次加载缓慢请耐心等待", ""));
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        JSONObject data = request(HOST + "/app/api/vod/parse",
                new JSONObject().put("vid", parts[0]).put("player", parts[1]));
        return new JSONObject().put("parse", 0).put("url", data.optString("play_url")).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = request(HOST + "/app/api/search/full?q=" + key, null);
        JSONArray videos = new JSONArray();
        JSONArray list = html.optJSONArray("search_full");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String pic = item.optString("vod_pic");
                videos.put(vod(item.optString("id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", pic + "@Referer=" + pic)
                        .put("vod_remarks", item.optString("vod_remarks"))
                        .put("vod_year", item.optString("vod_year")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
