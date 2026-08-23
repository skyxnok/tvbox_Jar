package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XiaFan4K extends Spider {

    private static final String HOST = "http://194.147.100.155:7744";
    private static final String AES_KEY = "kZ6fT8oF6oM8eX6lF7eH2rJ3pW7gW0kC";
    private static final List<String> EXCLUDE = Arrays.asList(
            "bfzym3u8", "tym3u8", "zjm3u8", "lzm3u8", "sdm3u8", "kbm3u8", "bjm3u8", "xkm3u8",
            "tpm3u8", "hnm3u8", "wjm3u8", "ffm3u8", "99m3u8", "dbm3u8", "rym3u8", "mzm3u8",
            "mym3u8", "wwm3u8", "mtm3u8", "modum3u8", "360zy");

    private String en(String data) {
        return JUtil.aesEncryptEcb(data, AES_KEY);
    }

    private JSONObject request(String url, Object body) throws Exception {
        Map<String, String> jsonHead = new HashMap<>();
        jsonHead.put("User-Agent", "okhttp/5.3.2");
        jsonHead.put("Content-Type", "application/json;charset=utf-8");
        Map<String, String> formHead = new HashMap<>();
        formHead.put("Content-Type", "application/x-www-form-urlencoded");
        formHead.put("User-Agent", "okhttp/5.3.2");
        String content;
        if (body instanceof String && ((String) body).length() > 0) {
            content = JUtil.post(url, (String) body, formHead);
        } else if (body instanceof JSONObject) {
            content = JUtil.post(url, body.toString(), jsonHead);
        } else {
            content = JUtil.post(url, "", jsonHead);
        }
        return new JSONObject(content);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray html = request(HOST + "/api/v1/video/classifies", "").getJSONArray("data");
        List<JSONObject> sorted = new ArrayList<>();
        for (int i = 0; i < html.length(); i++) sorted.add(html.getJSONObject(i));
        sorted.sort((a, b) -> a.optInt("sort") - b.optInt("sort"));
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        for (JSONObject tp : sorted) {
            JSONObject c = new JSONObject();
            c.put("type_id", tp.optString("id"));
            c.put("type_name", tp.optString("name"));
            classes.put(c);
            JSONObject extend = tp.getJSONObject("extend");
            JSONArray filters = new JSONArray();
            String[][] keys = {{"class", "类型"}, {"area", "地区"}, {"year", "年份"}};
            for (String[] k : keys) {
                JSONObject o = new JSONObject();
                o.put("key", k[0]);
                o.put("name", k[1]);
                JSONArray value = new JSONArray();
                String[] vs = extend.optString(k[0]).split(",");
                for (String v : vs) {
                    JSONObject item = new JSONObject();
                    item.put("n", v);
                    item.put("v", v);
                    value.put(item);
                }
                o.put("value", value);
                filters.put(o);
            }
            filterObj.put(tp.optString("id"), filters);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterObj);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        long t = System.currentTimeMillis() / 1000;
        JSONObject body = new JSONObject();
        body.put("area", opt(extend, "area"));
        body.put("classify", opt(extend, "class"));
        body.put("pageNum", pg);
        body.put("pageSize", 40);
        body.put("typeId", tid);
        body.put("year", opt(extend, "year"));
        body.put("timestamp", t);
        body.put("datasign", en("pageNum=" + pg + "&pageSize=40&timestamp=" + t + "&typeId=" + tid));
        JSONObject html = request(HOST + "/api/v1/video/index", body);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("id"));
            v.put("vod_name", item.optString("name"));
            v.put("vod_pic", item.optString("videoPic"));
            v.put("vod_remarks", item.optString("remarks"));
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
        long t = System.currentTimeMillis() / 1000;
        String id = ids.get(0);
        String body = "datasign=" + URLEncoder.encode(en("id=" + id + "&timestamp=" + t), "UTF-8") + "&id=" + id + "&timestamp=" + t;
        JSONObject html = request(HOST + "/api/v1/video/videoDetails", body).getJSONObject("data");
        List<JSONObject> playlist = filterUrls(html.getJSONArray("playerSource"));
        JSONArray playFrom = new JSONArray();
        JSONArray playUrl = new JSONArray();
        for (JSONObject play : playlist) {
            playFrom.put(play.optString("sourceName") + "(" + play.optString("sourceCode") + ")");
            String p = play.optString("parseUrl");
            String code = play.optString("sourceCode");
            StringBuilder sb = new StringBuilder();
            JSONArray eps = play.getJSONArray("episodes");
            for (int i = 0; i < eps.length(); i++) {
                JSONObject item = eps.getJSONObject(i);
                if (sb.length() > 0) sb.append("#");
                String urls = p != null && !p.isEmpty() ? p + item.optString("playerCode") : code + "@@" + item.optString("playerCode");
                sb.append(item.optString("episodeName")).append("$").append(urls);
            }
            playUrl.put(sb.toString());
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", html.optString("classify"));
        vod.put("vod_year", html.optString("year"));
        vod.put("vod_area", html.optString("area"));
        vod.put("vod_remarks", html.optString("remarks"));
        vod.put("vod_actor", html.optString("actor"));
        vod.put("vod_director", html.optString("director"));
        vod.put("vod_content", html.optString("content"));
        vod.put("vod_play_from", join(playFrom, "$$$"));
        vod.put("vod_play_url", join(playUrl, "$$$"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String url;
        if (id.contains("m3u8")) {
            url = id.contains("@@") ? id.split("@@")[1] : id;
        } else if (id.contains("@@")) {
            String[] ids = id.split("@@");
            long t = System.currentTimeMillis() / 1000;
            String sign = URLEncoder.encode(en("code=" + ids[1] + "&from=" + ids[0] + "&timestamp=" + t), "UTF-8");
            String body = "code=" + ids[1] + "&datasign=" + sign + "&from=" + ids[0] + "&timestamp=" + t;
            url = request(HOST + "/api/v1/player/analysisUrl", body).optString("data");
        } else if (id.contains("url=")) {
            url = new JSONObject(JUtil.get(id, null)).optString("url");
        } else {
            url = id;
        }
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", url);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        long t = System.currentTimeMillis() / 1000;
        JSONObject body = new JSONObject();
        body.put("keyword", key);
        body.put("pageNum", 1);
        body.put("pageSize", 40);
        body.put("timestamp", t);
        body.put("datasign", en("keyword=" + key + "&pageNum=1&pageSize=40&timestamp=" + t));
        JSONObject html = request(HOST + "/api/v1/video/search", body);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("id"));
            v.put("vod_name", item.optString("name"));
            v.put("vod_pic", item.optString("videoPic"));
            v.put("vod_remarks", item.optString("remarks"));
            v.put("vod_year", item.optString("year"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    private List<JSONObject> filterUrls(JSONArray data) {
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.optJSONObject(i);
            if (item != null && !EXCLUDE.contains(item.optString("sourceCode"))) list.add(item);
        }
        return list;
    }

    private String opt(HashMap<String, String> extend, String key) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : "";
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
