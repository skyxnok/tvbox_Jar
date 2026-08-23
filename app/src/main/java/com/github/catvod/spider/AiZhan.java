package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiZhan extends Spider {

    private static final String HOST = "https://m3u8.girigirilove.com";

    private JSONObject request(String url, JSONObject body) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Dart/3.11 (dart:io)");
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("cookie", "SITE_TOTAL_ID=67ee3ec6e87dfd18577904b81e8d4a40");
        String content;
        if (body != null) {
            content = JUtil.post(url, body.toString(), headers);
        } else {
            content = JUtil.get(url, headers);
        }
        return new JSONObject(content);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(HOST + "/api.php/App2/bannerList", null);
        JSONArray classes = new JSONArray();
        String[][] types = {{"2", "日番"}, {"3", "美番"}, {"21", "剧场"}};
        for (String[] t : types) {
            JSONObject c = new JSONObject();
            c.put("type_id", t[0]);
            c.put("type_name", t[1]);
            classes.put(c);
        }
        JSONObject filterObj = new JSONObject();
        for (String[] t : types) {
            JSONArray values = new JSONArray();
            JSONObject all = new JSONObject();
            all.put("n", "全部");
            all.put("v", "");
            values.put(all);
            for (int y = 2026; y >= 2010; y--) {
                JSONObject item = new JSONObject();
                item.put("n", String.valueOf(y));
                item.put("v", String.valueOf(y));
                values.put(item);
            }
            JSONArray yearFilter = new JSONArray();
            JSONObject year = new JSONObject();
            year.put("key", "year");
            year.put("name", "年份");
            year.put("value", values);
            yearFilter.put(year);
            JSONObject sort = new JSONObject();
            sort.put("key", "sort");
            sort.put("name", "排序");
            JSONArray sortValues = new JSONArray();
            String[][] sorts = {{"最新", ""}, {"最热", "hits"}, {"评分", "score"}};
            for (String[] s : sorts) {
                JSONObject o = new JSONObject();
                o.put("n", s[0]);
                o.put("v", s[1]);
                sortValues.put(o);
            }
            sort.put("value", sortValues);
            yearFilter.put(sort);
            filterObj.put(t[0], yearFilter);
        }
        JSONArray videos = new JSONArray();
        JSONArray info = html.getJSONArray("info");
        for (int i = 0; i < info.length(); i++) {
            JSONObject item = info.getJSONObject(i);
            String title = item.optString("title");
            if (title.isEmpty() || title.toLowerCase().contains("tg")) continue;
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("link").split("/")[2]);
            v.put("vod_name", title);
            v.put("vod_pic", item.optString("cover"));
            JSONObject style = new JSONObject();
            style.put("type", "rect");
            style.put("ratio", 1.485);
            v.put("style", style);
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterObj);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        int offset = (Integer.parseInt(pg) - 1) * 20;
        String url = HOST + "/api.php/Vod/get_list?offset=" + offset + "&limit=20&type_id=" + tid
                + "&vod_year=" + opt(extend, "year") + "&orderby=" + opt(extend, "sort");
        JSONObject html = request(url, null);
        JSONArray videos = new JSONArray();
        JSONArray rows = html.getJSONObject("info").getJSONArray("rows");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject item = rows.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vod_id"));
            v.put("vod_name", item.optString("vod_name"));
            v.put("vod_pic", item.optString("vod_pic"));
            v.put("vod_remarks", item.optString("vod_remarks"));
            v.put("vod_year", item.optString("vod_year"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("page", pg);
        result.put("pagecount", 99999);
        result.put("limit", videos.length());
        result.put("total", 99999);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = request(HOST + "/api.php/Vod/get_detail?vod_id=" + ids.get(0), null).getJSONObject("info");
        JSONObject vod = new JSONObject();
        vod.put("type_name", html.optString("vod_class"));
        vod.put("vod_year", html.optString("vod_year"));
        vod.put("vod_area", html.optString("vod_area"));
        vod.put("vod_remarks", html.optString("vod_remarks"));
        vod.put("vod_actor", html.optString("vod_actor"));
        vod.put("vod_director", html.optString("vod_director"));
        vod.put("vod_content", html.optString("vod_content").replaceAll("<.*?>", ""));
        vod.put("vod_play_from", html.optString("vod_play_from").replace("chs", "简体").replace("cht", "繁体"));
        vod.put("vod_play_url", html.optString("vod_play_url"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject body = new JSONObject();
        body.put("play_url", id);
        JSONObject html = request(HOST + "/api.php/Scrolling/getVodOutScrolling", body);
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", id);
        result.put("danmaku", html.optString("info"));
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        int offset = 0;
        String url = HOST + "/api.php/Vod/get_list?vod_name=" + URLEncoder.encode(key, "UTF-8") + "&offset=" + offset + "&limit=20";
        JSONObject html = request(url, null);
        JSONArray videos = new JSONArray();
        JSONArray rows = html.getJSONObject("info").getJSONArray("rows");
        for (int i = 0; i < rows.length(); i++) {
            JSONObject item = rows.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vod_id"));
            v.put("vod_name", item.optString("vod_name"));
            v.put("vod_pic", item.optString("vod_pic"));
            v.put("vod_remarks", item.optString("vod_remarks"));
            v.put("vod_year", item.optString("vod_year"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    private String opt(HashMap<String, String> extend, String key) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : "";
    }
}
