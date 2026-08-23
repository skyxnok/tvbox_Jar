package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CiYuanCheng extends Spider {

    private String host;
    private final Map<String, String> headers = new HashMap<>();

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        headers.put("x-app-name", "cyc_android");
        headers.put("accept", "application/json");
        headers.put("user-agent", "ktor-client");
        String txt = JUtil.get("https://doh.pub/dns-query?name=newapp.cycapp.org&type=txt", null);
        host = new JSONObject(txt).getJSONArray("Answer").getJSONObject(0).optString("data").replace("\"", "");
        Map<String, String> loginHeaders = new HashMap<>(headers);
        loginHeaders.put("content-type", "application/json");
        String body = "{\"username\":\"2948853431\",\"password\":\"zz77226\"}";
        String resp = JUtil.post(host + "/auth/login", body, loginHeaders);
        String token = new JSONObject(resp).optJSONObject("data").optString("token");
        headers.put("authorization", token);
    }

    private JSONObject request(String url) throws Exception {
        return new JSONObject(JUtil.get(url, headers));
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(host + "/app/adverts?position=banner");
        JSONArray classes = new JSONArray();
        String[][] types = {{"1", "TV番"}, {"2", "剧场版"}};
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
            JSONObject year = new JSONObject();
            year.put("key", "year");
            year.put("name", "年份");
            year.put("value", values);
            JSONObject sort = new JSONObject();
            sort.put("key", "sort");
            sort.put("name", "排序");
            JSONArray sortValues = new JSONArray();
            String[][] sorts = {{"最新", "update_time"}, {"最热", "hits"}, {"评分", "score"}};
            for (String[] s : sorts) {
                JSONObject o = new JSONObject();
                o.put("n", s[0]);
                o.put("v", s[1]);
                sortValues.put(o);
            }
            sort.put("value", sortValues);
            filterObj.put(t[0], new JSONArray().put(year).put(sort));
        }
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("action_value"));
            v.put("vod_name", item.optString("name"));
            v.put("vod_pic", item.optString("content"));
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
        StringBuilder query = new StringBuilder();
        appendParam(query, "zone_id", tid);
        appendParam(query, "page", pg);
        appendParam(query, "page_size", "20");
        appendParam(query, "order_by", opt(extend, "sort", "update_time"));
        appendParam(query, "year", opt(extend, "year", ""));
        JSONObject html = request(host + "/videos?" + query);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("video_id"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", item.optString("cover_url"));
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
        String id = ids.get(0);
        JSONObject html = request(host + "/videos/" + id).getJSONObject("data");
        JSONArray playSources = html.getJSONArray("play_from");
        JSONArray playFrom = new JSONArray();
        JSONArray playUrl = new JSONArray();
        for (int i = 0; i < playSources.length(); i++) {
            JSONObject source = playSources.getJSONObject(i);
            String code = source.optString("code");
            playFrom.put(source.optString("title"));
            int total = source.optInt("count");
            int totalPage = (int) Math.ceil(total / 100.0);
            if (totalPage < 1) totalPage = 1;
            StringBuilder allEp = new StringBuilder();
            for (int page = 1; page <= totalPage; page++) {
                String url = host + "/videos/" + id + "/sections?player_code=" + code + "&page=" + page + "&page_size=100";
                JSONObject resp = request(url);
                JSONArray items = resp.getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject sub = items.getJSONObject(j);
                    if (allEp.length() > 0) allEp.append("#");
                    allEp.append(sub.optString("title")).append("$").append(sub.optString("id"));
                }
            }
            playUrl.put(allEp.toString());
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_name", html.optString("title"));
        vod.put("type_name", html.optString("state"));
        vod.put("vod_year", html.optString("year"));
        vod.put("vod_area", html.optString("area"));
        vod.put("vod_remarks", html.optString("vod_remarks"));
        vod.put("vod_actor", joinArr(html.optJSONArray("actor"), " / "));
        vod.put("vod_director", joinArr(html.optJSONArray("director"), " / "));
        vod.put("vod_content", html.optString("description"));
        vod.put("vod_play_from", join(playFrom, "$$$"));
        vod.put("vod_play_url", join(playUrl, "$$$"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject data = request(host + "/v2/sections/" + id + "/play-url").getJSONObject("data");
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", data.optString("url"));
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String url = host + "/videos/search?q=" + URLEncoder.encode(key, "UTF-8") + "&page=1&page_size=20";
        JSONObject html = request(url);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("video_id"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", item.optString("cover_url"));
            v.put("vod_remarks", item.optString("remarks"));
            v.put("vod_year", item.optString("year"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    private void appendParam(StringBuilder sb, String k, String v) {
        if (v == null || v.isEmpty()) return;
        if (sb.length() > 0) sb.append("&");
        sb.append(k).append("=").append(v);
    }

    private String opt(HashMap<String, String> extend, String key, String def) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : def;
    }

    private String joinArr(JSONArray arr, String sep) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(arr.optString(i));
        }
        return sb.toString();
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
