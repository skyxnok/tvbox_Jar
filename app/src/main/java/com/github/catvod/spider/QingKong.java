package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QingKong extends Spider {

    private static final String HOST = "https://api.sorani.cc";

    private JSONObject request(String url, JSONObject body) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Origin", "https://www.sorani.net");
        headers.put("user-agent", "Dart/3.11 (dart:io)");
        headers.put("accept", "application/json");
        headers.put("x-sorani-app-version", "1.0.3+4");
        headers.put("content-type", "application/json");
        headers.put("x-sorani-guest-key", "e2ae63b73b544dc09a1f10e046d5c9a9");
        headers.put("x-sorani-device-id", "sorani-Z4QUqszCGnGcodfzqm7-_mBRhDiJVQdU");
        String content = body == null ? JUtil.get(url, headers) : JUtil.post(url, body.toString(), headers);
        return new JSONObject(content);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(HOST + "/sorani-cms/api/video/home-page?platform=2", null).getJSONObject("data");
        JSONArray classes = new JSONArray();
        JSONArray categories = html.getJSONArray("categories");
        for (int i = 0; i < categories.length(); i++) {
            JSONObject tp = categories.getJSONObject(i);
            JSONObject c = new JSONObject();
            c.put("type_id", tp.optString("id"));
            c.put("type_name", tp.optString("name"));
            classes.put(c);
        }
        JSONArray videos = new JSONArray();
        JSONArray banners = html.getJSONArray("banners");
        for (int i = 0; i < banners.length(); i++) {
            JSONObject item = banners.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("contentId"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", item.optString("contentCover"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", new JSONObject());
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String url = HOST + "/sorani-cms/api/video?page=" + pg + "&size=20&enabled=true&sortMode=latest&sortDesc=true&categoryId=" + tid;
        JSONObject html = request(url, null).getJSONObject("data");
        JSONArray videos = new JSONArray();
        JSONArray records = html.getJSONArray("records");
        for (int i = 0; i < records.length(); i++) {
            JSONObject item = records.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("id"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", item.optString("cover"));
            v.put("vod_remarks", item.optString("statusText"));
            v.put("vod_year", item.optString("year"));
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
        JSONObject data = request(HOST + "/sorani-cms/api/video/" + ids.get(0) + "/play-page", null).getJSONObject("data");
        JSONArray sources = data.getJSONArray("playLines");
        JSONArray episodes = data.getJSONArray("episodes");
        JSONArray playFrom = new JSONArray();
        JSONArray playUrl = new JSONArray();
        for (int i = 0; i < sources.length(); i++) {
            JSONObject source = sources.getJSONObject(i);
            String code = source.optString("code");
            playFrom.put(source.optString("name") + " [" + code + "]");
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < episodes.length(); j++) {
                JSONObject ep = episodes.getJSONObject(j);
                if (j > 0) sb.append("#");
                sb.append(ep.optString("title")).append("$").append(code).append("@@").append(ep.optString("episodeId"));
            }
            playUrl.put(sb.toString());
        }
        JSONObject res = data.getJSONObject("detail");
        JSONObject vod = new JSONObject();
        vod.put("type_name", res.optString("tags"));
        vod.put("vod_year", res.optString("year"));
        vod.put("vod_area", res.optString("area"));
        vod.put("vod_remarks", res.optString("statusText"));
        vod.put("vod_actor", "");
        vod.put("vod_director", res.optString("director"));
        vod.put("vod_content", res.optString("summary").replaceAll("<.*?>", ""));
        vod.put("vod_play_from", join(playFrom, "$$$"));
        vod.put("vod_play_url", join(playUrl, "$$$"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        String code = parts[0];
        String ids = parts[1];
        JSONObject data = request(HOST + "/sorani-cms/api/video/episode/" + ids + "/play?lineCode=" + code, null).getJSONObject("data");
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", data.optString("playUrl"));
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String url = HOST + "/sorani-cms/api/video/search?keyword=" + URLEncoder.encode(key, "UTF-8")
                + "&sortMode=relevance_popular&limit=20&offset=0";
        JSONArray data = request(url, null).getJSONArray("data");
        JSONArray videos = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("id"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", item.optString("cover"));
            v.put("vod_remarks", item.optString("statusText"));
            v.put("vod_year", item.optString("year"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    private String join(JSONArray arr, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(arr.optString(i));
        }
        return sb.toString();
    }
}
