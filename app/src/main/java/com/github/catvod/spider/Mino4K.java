package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mino4K extends Spider {

    private String host;
    private final Map<String, String> hh = new HashMap<>();
    private final Map<String, String> playinfo = new HashMap<>();

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        hh.put("user-agent", "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
        hh.put("x-device-model", "23116PN5BC");
        hh.put("x-device-name", "Xiaomi");
        hh.put("x-device-id", "mobile_mszo6gs7_avcian");
        hh.put("content-type", "application/json");
        hh.put("x-device-type", "android");
        hh.put("x-platform-sig", "zbZOM5jVxeequ4uwwVqreb1hnJAMfJEqfosu6YhbesFvMkHeM347l/qXYp3TcpH4jPGKxcR5cBfDH1dL3PmPAw==");
        hh.put("x-platform", "android");
        JSONObject resp = new JSONObject(JUtil.get("https://xmino.oss-cn-beijing.aliyuncs.com/xmino.json", null));
        host = resp.getJSONArray("endpoints").optString(0);
        String login = JUtil.post(host + "/api/auth/login-password",
                "{\"phone\":\"13544125511\",\"password\":\"100200300\"}", hh);
        String token = new JSONObject(login).getJSONObject("data").getJSONObject("token").optString("access_token");
        hh.put("authorization", "Bearer " + token);
        JSONObject players = new JSONObject(JUtil.get(host + "/api/players", hh));
        JSONObject data = players.getJSONObject("data");
        java.util.Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            playinfo.put(key, data.getJSONObject(key).optString("name"));
        }
    }

    private JSONObject request(String url, JSONObject body) throws Exception {
        String content = body == null ? JUtil.get(url, hh) : JUtil.post(url, body.toString(), hh);
        return new JSONObject(content);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject categories = request(host + "/api/categories", null);
        JSONObject banners = request(host + "/api/home/banners", null);
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray data = categories.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject tp = data.getJSONObject(i);
            JSONObject c = new JSONObject();
            c.put("type_id", tp.optString("type_id"));
            c.put("type_name", tp.optString("type_name"));
            classes.put(c);
            JSONArray filters = new JSONArray();
            if (!tp.isNull("type_extend")) {
                JSONObject extend = new JSONObject(tp.optString("type_extend"));
                if (extend.has("class")) {
                    JSONArray classValues = new JSONArray();
                    JSONArray cls = extend.getJSONArray("class");
                    for (int j = 0; j < cls.length(); j++) {
                        JSONObject item = new JSONObject();
                        item.put("n", cls.optString(j));
                        item.put("v", cls.optString(j));
                        classValues.put(item);
                    }
                    JSONObject o = new JSONObject();
                    o.put("key", "class");
                    o.put("name", "分类");
                    o.put("value", classValues);
                    filters.put(o);
                }
            }
            JSONObject sort = new JSONObject();
            sort.put("key", "sort");
            sort.put("name", "排序");
            JSONArray sortValues = new JSONArray();
            String[][] sorts = {{"最热", "hits"}, {"最新", "time"}, {"评分", "score"}};
            for (String[] s : sorts) {
                JSONObject item = new JSONObject();
                item.put("n", s[0]);
                item.put("v", s[1]);
                sortValues.put(item);
            }
            sort.put("value", sortValues);
            filters.put(sort);
            filterObj.put(tp.optString("type_id"), filters);
        }
        JSONArray videos = new JSONArray();
        JSONArray bannerData = banners.getJSONArray("data");
        for (int i = 0; i < bannerData.length(); i++) {
            JSONObject item = bannerData.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vod_id"));
            v.put("vod_name", item.optString("vod_name"));
            v.put("vod_pic", item.optString("vod_pic"));
            v.put("vod_remarks", item.optString("vod_class"));
            v.put("vod_year", item.optString("vod_year"));
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
        String url = host + "/api/videos?page=" + pg + "&limit=18&sort=" + opt(extend, "sort", "hits")
                + "&t=" + tid + "&class=" + opt(extend, "class");
        JSONObject html = request(url, null);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vod_id"));
            v.put("vod_name", item.optString("vod_name"));
            v.put("vod_pic", item.optString("vod_pic"));
            v.put("vod_remarks", item.optString("vod_remarks"));
            v.put("vod_year", item.optString("vod_year"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("page", Integer.parseInt(pg));
        result.put("pagecount", 99999);
        result.put("limit", videos.length());
        result.put("total", 99999);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String id = ids.get(0);
        JSONObject res = request(host + "/api/videos/" + id, null).getJSONObject("data");
        JSONArray playFrom = new JSONArray();
        JSONArray playUrl = new JSONArray();
        JSONArray playList = res.getJSONArray("play_list");
        for (int i = 0; i < playList.length(); i++) {
            JSONObject playItem = playList.getJSONObject(i);
            String from = playItem.optString("from");
            playFrom.put((playinfo.containsKey(from) ? playinfo.get(from) : from) + " [" + from + "]");
            StringBuilder sb = new StringBuilder();
            JSONArray epList = playItem.getJSONArray("episodes");
            for (int j = 0; j < epList.length(); j++) {
                JSONObject ep = epList.getJSONObject(j);
                if (j > 0) sb.append("#");
                sb.append(ep.optString("name")).append("$").append(from).append("@@").append(ep.optString("url")).append("@@").append(id);
            }
            playUrl.put(sb.toString());
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", id);
        vod.put("vod_name", res.optString("vod_name"));
        vod.put("vod_pic", res.optString("vod_pic"));
        vod.put("type_name", res.optString("vod_tag"));
        vod.put("vod_year", res.optString("vod_year"));
        vod.put("vod_area", res.optString("vod_area"));
        vod.put("vod_remarks", res.optString("vod_remarks"));
        vod.put("vod_actor", res.optString("vod_actor"));
        vod.put("vod_director", res.optString("vod_director"));
        vod.put("vod_content", res.optString("vod_content").replaceAll("<.*?>", ""));
        vod.put("vod_play_from", join(playFrom, "$$$"));
        vod.put("vod_play_url", join(playUrl, "$$$"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        JSONObject body = new JSONObject();
        body.put("from", parts[0]);
        body.put("url", parts[1]);
        body.put("vod_id", Integer.parseInt(parts[2]));
        JSONObject data = request(host + "/api/parse", body).getJSONObject("data");
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", data.optString("url"));
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String url = host + "/api/search?wd=" + URLEncoder.encode(key, "UTF-8") + "&page=1&limit=20";
        JSONObject html = request(url, null);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
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

    private String opt(HashMap<String, String> extend, String key, String def) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : def;
    }

    private String opt(HashMap<String, String> extend, String key) {
        return opt(extend, key, "");
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
