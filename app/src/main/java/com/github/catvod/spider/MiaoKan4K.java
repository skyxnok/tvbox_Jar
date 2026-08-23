package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MiaoKan4K extends Spider {

    private static final String KEY = "c60d88b2eep53za8";
    private static final String IV = "c60d88b2eep53za8";
    private static final List<String> EXCLUDE = Arrays.asList(
            "bfzym3u8", "tym3u8", "zjm3u8", "lzm3u8", "sdm3u8", "kbm3u8", "bjm3u8", "xkm3u8",
            "tpm3u8", "hnm3u8", "wjm3u8", "ffm3u8", "99m3u8", "dbm3u8", "mzm3u8", "mym3u8",
            "wwm3u8", "mtm3u8", "NMYS", "YHDM", "m3u8", "zlyun", "KYLG", "LKDB", "xnk", "AK_4K");
    private static final List<String> HD_TAGS = Arrays.asList("4k", "4K", "2k", "2K", "臻彩");
    private static final Pattern PLAY_URL_PATTERN = Pattern.compile("http.*url=.*m3u8|url=http.*m3u8|url.*http.*m3u8|\\?url=");

    private String host = "https://mk1080.top";

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        try {
            String content = JUtil.get("https://mk1080.top/get.txt", null);
            String line = content.replaceAll("\\r\\n", "").replaceAll("\\n", "");
            host = line.contains("ok") ? "https://mk1080.top" : line;
        } catch (Exception e) {
            host = "https://mk1080.top";
        }
    }

    private String en(String data) {
        return JUtil.aesEncrypt(data, KEY, IV);
    }

    private String de(String data) {
        return JUtil.aesDecrypt(data, KEY, IV);
    }

    private Map<String, String> hh() {
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, String> headers = new HashMap<>();
        headers.put("app-version-code", "135");
        headers.put("app-ui-mode", "light");
        headers.put("app-user-device-id", "20e4e50fddcad37dfb5c7b10e344b29b3");
        headers.put("app-user-token", "9167bc7a247b7a8bb67942dabc903d6ba204b04623ae077252fb2ed860a72d6f");
        headers.put("app-api-verify-time", t);
        headers.put("app-api-verify-sign", en(t));
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("User-Agent", "okhttp/3.14.9");
        return headers;
    }

    private JSONObject request(String url, String body) throws Exception {
        String content = JUtil.post(url, body, hh());
        String data = new JSONObject(content).getString("data");
        return new JSONObject(de(data));
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject res = request(host + "/api.php/getappapi.index/initV119", "");
        JSONArray typeList = res.getJSONArray("type_list");
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        for (int i = 0; i < typeList.length(); i++) {
            JSONObject tp = typeList.getJSONObject(i);
            String typeName = tp.optString("type_name");
            if (typeName.equals("全部")) continue;
            JSONObject c = new JSONObject();
            c.put("type_id", tp.optString("type_id"));
            c.put("type_name", typeName);
            classes.put(c);
            if (!tp.isNull("filter_type_list")) {
                JSONArray filters = new JSONArray();
                JSONArray filterTypeList = tp.getJSONArray("filter_type_list");
                for (int j = 0; j < filterTypeList.length(); j++) {
                    JSONObject f = filterTypeList.getJSONObject(j);
                    JSONObject o = new JSONObject();
                    o.put("key", f.optString("name"));
                    o.put("name", f.optJSONArray("list").optString(0));
                    JSONArray value = new JSONArray();
                    JSONArray list = f.getJSONArray("list");
                    for (int k = 0; k < list.length(); k++) {
                        JSONObject item = new JSONObject();
                        item.put("n", list.optString(k));
                        item.put("v", list.optString(k));
                        value.put(item);
                    }
                    o.put("value", value);
                    filters.put(o);
                }
                filterObj.put(tp.optString("type_id"), filters);
            }
        }
        JSONArray videos = new JSONArray();
        JSONArray bannerList = res.getJSONArray("banner_list");
        for (int i = 0; i < bannerList.length(); i++) {
            JSONObject item = bannerList.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vod_id"));
            v.put("vod_name", item.optString("vod_name"));
            v.put("vod_pic", item.optString("vod_pic"));
            v.put("vod_remarks", item.optString("vod_remarks"));
            v.put("vod_year", item.optString("vod_year"));
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
        String body = "area=" + opt(extend, "area") + "&year=" + opt(extend, "year") + "&type_id=" + tid
                + "&page=" + pg + "&sort=" + opt(extend, "sort", "最热") + "&lang=" + opt(extend, "lang")
                + "&class=" + opt(extend, "class");
        JSONObject res = request(host + "/api.php/getappapi.index/typeFilterVodList", body);
        JSONArray videos = new JSONArray();
        JSONArray list = res.getJSONArray("recommend_list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vod_id"));
            v.put("vod_name", item.optString("vod_name"));
            v.put("vod_pic", item.optString("vod_pic"));
            v.put("vod_remarks", item.optString("vod_remarks"));
            v.put("vod_year", "");
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
        JSONObject res = request(host + "/api.php/getappapi.index/vodDetail", "vod_id=" + ids.get(0));
        List<JSONObject> playlist = filterUrls(res.getJSONArray("vod_play_list"));
        JSONArray playFrom = new JSONArray();
        JSONArray playUrl = new JSONArray();
        for (JSONObject item : playlist) {
            JSONObject playerInfo = item.getJSONObject("player_info");
            playFrom.put(playerInfo.optString("show") + " [" + item.getJSONArray("urls").getJSONObject(0).optString("from") + "]");
            String p = playerInfo.optString("parse");
            JSONArray epList = item.getJSONArray("urls");
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < epList.length(); j++) {
                JSONObject ep = epList.getJSONObject(j);
                if (sb.length() > 0) sb.append("#");
                String urls;
                if (!p.isEmpty()) {
                    if (p.contains("http")) {
                        urls = ep.optString("parse_api_url");
                    } else {
                        urls = p + "@@" + ep.optString("url") + "@@" + ep.optString("token");
                    }
                } else {
                    urls = ep.optString("url");
                }
                sb.append(ep.optString("name").replaceAll("\\b0+(?=[1-9])", "")).append("$").append(urls);
            }
            playUrl.put(sb.toString());
        }
        JSONObject vodInfo = res.getJSONObject("vod");
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", vodInfo.optString("vod_name"));
        vod.put("vod_pic", vodInfo.optString("vod_pic"));
        vod.put("type_name", vodInfo.optString("vod_class"));
        vod.put("vod_year", vodInfo.optString("vod_year"));
        vod.put("vod_area", vodInfo.optString("vod_area"));
        vod.put("vod_remarks", vodInfo.optString("vod_remarks"));
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_content", vodInfo.optString("vod_content"));
        vod.put("vod_play_from", join(playFrom, "$$$"));
        vod.put("vod_play_url", join(playUrl, "$$$"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String url;
        if (id.contains("@@")) {
            String[] parts = id.split("@@");
            String parseApi = parts[0];
            String videoUrl = parts.length > 1 ? parts[1] : "";
            String token = parts.length > 2 ? parts[2] : "";
            String body = "parse_api=" + parseApi + "&url=" + URLEncoder.encode(en(videoUrl), "UTF-8") + "&token=" + token;
            JSONObject res = request(host + "/api.php/getappapi.index/vodParse", body);
            url = new JSONObject(res.optString("json")).optString("url");
        } else if (PLAY_URL_PATTERN.matcher(id).find()) {
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
        String body = "keywords=" + URLEncoder.encode(key, "UTF-8") + "&type_id=0&page=1";
        JSONObject res = request(host + "/api.php/getappapi.index/searchList", body);
        JSONArray videos = new JSONArray();
        JSONArray list = res.getJSONArray("search_list");
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

    private List<JSONObject> filterUrls(JSONArray list) {
        List<JSONObject> filterList = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.optJSONObject(i);
            if (item == null) continue;
            JSONArray urls = item.optJSONArray("urls");
            String firstFrom = urls != null && urls.length() > 0 ? urls.optJSONObject(0).optString("from") : "";
            if (!EXCLUDE.contains(firstFrom)) filterList.add(item);
        }
        filterList.sort((a, b) -> {
            String showA = a.optJSONObject("player_info").optString("show");
            String showB = b.optJSONObject("player_info").optString("show");
            return score(showB) - score(showA);
        });
        return filterList;
    }

    private int score(String show) {
        for (String tag : HD_TAGS) {
            if (show.contains(tag)) return 1;
        }
        return 0;
    }

    private String opt(HashMap<String, String> extend, String key) {
        return opt(extend, key, "");
    }

    private String opt(HashMap<String, String> extend, String key, String def) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : def;
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
