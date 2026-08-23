package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class KuaiYing4K extends Spider {

    private static final String AES_KEY = "3dd7d42dc2496f1d";
    private static final String AES_IV = "d1f6942cd24d7dd3";
    private static final String SIGN_KEY = "47aa22547fcada31dd7bd35cab492326kuaiying4k";
    private static final List<String> EXCLUDE = Arrays.asList(
            "bfzym3u8", "tym3u8", "zjm3u8", "lzm3u8", "sdm3u8", "kbm3u8", "bjm3u8", "xkm3u8",
            "tpm3u8", "hnm3u8", "wjm3u8", "ffm3u8", "99m3u8", "dbm3u8", "rym3u8", "mzm3u8",
            "mym3u8", "wwm3u8", "mtm3u8", "snm3u8", "okm3u8", "wolong", "http", "ruyi", "rym3u8",
            "yym3u8", "ikm3u8", "jsm3u8", "wjwsym3u8");
    private static final List<String> HD_KEYS = Arrays.asList("4K", "4k", "2K", "2k", "臻彩", "真彩");

    private String host = "https://www.kanzurm65ak.top";
    private JSONObject playinfo;

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        try {
            Map<String, String> ua = new HashMap<>();
            ua.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 9; 23116PN5BC Build/PQ3B.190801.04011825)");
            String config = JUtil.get("https://www.pjb777.top/ky4kbgq7b273.json", ua);
            host = new JSONObject(config).optString("apiDomain", "https://www.kanzurm65ak.top");
            String content = JUtil.get(host + "/api.php/appfoxs/config", gethh(null));
            playinfo = new JSONObject(de(content)).getJSONObject("data");
        } catch (Exception ignored) {
        }
    }

    private String de(String data) {
        return JUtil.aesDecrypt(data, AES_KEY, AES_IV);
    }

    private Map<String, String> hh() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        return headers;
    }

    private Map<String, String> gethh(String data) {
        String t = String.valueOf(System.currentTimeMillis());
        String suiji = String.valueOf(100000 + new Random().nextInt(900000));
        String sign = JUtil.md5(SIGN_KEY + t + suiji + (data == null ? "" : data));
        Map<String, String> headers = new HashMap<>();
        headers.put("x-security-auth", t + "|" + suiji + "|" + sign);
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        headers.put("content-type", "application/json; charset=utf-8");
        return headers;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject ccc = new JSONObject(JUtil.get(host + "/api.php/appfox/init", hh()));
        JSONArray typeList = ccc.getJSONObject("data").getJSONArray("type_list");
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        for (int i = 0; i < typeList.length(); i++) {
            JSONObject tp = typeList.getJSONObject(i);
            String typeName = tp.optString("type_name");
            if (typeName.equals("学日语") || typeName.equals("全部")) continue;
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
        JSONArray home = new JSONObject(JUtil.get(host + "/api.php/appfox/nav_video?id=2", hh())).getJSONArray("data");
        JSONArray videos = new JSONArray();
        JSONArray cats = home.getJSONObject(0).getJSONArray("categories");
        for (int i = 0; i < cats.length(); i++) {
            JSONArray vids = cats.getJSONObject(i).getJSONArray("videos");
            for (int j = 0; j < vids.length(); j++) {
                JSONObject item = vids.getJSONObject(j);
                JSONObject v = new JSONObject();
                v.put("vod_id", item.optString("vod_id"));
                v.put("vod_name", item.optString("vod_name"));
                v.put("vod_pic", item.optString("vod_pic"));
                v.put("vod_remarks", item.optString("vod_remarks"));
                String pubdate = item.optString("vod_pubdate");
                v.put("vod_year", pubdate.contains("-") ? pubdate.split("-")[0] : "");
                videos.put(v);
            }
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
        String url = host + "/api.php/appfox/vodList?type_id=" + tid + "&class=" + opt(extend, "class", "全部")
                + "&area=" + opt(extend, "area", "全部") + "&lang=" + opt(extend, "lang", "全部")
                + "&year=" + opt(extend, "year", "全部") + "&sort=" + opt(extend, "sort", "最热") + "&page=" + pg;
        JSONObject html = new JSONObject(JUtil.get(url, hh()));
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("recommend_list");
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
        String id = ids.get(0);
        JSONObject html;
        if (id.contains("vod_play_from")) {
            html = new JSONObject(id);
        } else {
            String body = "{\"ac\":\"detail\",\"ids\":\"" + id + "\"}";
            String content = de(JUtil.post(host + "/api.php/appfoxs/vod", body, gethh(body)));
            html = new JSONObject(content).getJSONArray("list").getJSONObject(0);
        }
        Map<String, String> playerMap = new HashMap<>();
        JSONArray playerList = playinfo.getJSONArray("playerList");
        for (int i = 0; i < playerList.length(); i++) {
            JSONObject p = playerList.getJSONObject(i);
            playerMap.put(p.optString("playerCode"), p.optString("playerName"));
        }
        String[] lines = html.optString("vod_play_from").split("\\$\\$\\$");
        String[] urls = html.optString("vod_play_url").split("\\$\\$\\$");
        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String code = lines[i];
            if (EXCLUDE.contains(code)) continue;
            String name = playerMap.getOrDefault(code, code);
            StringBuilder sb = new StringBuilder();
            String[] eps = urls[i].split("#");
            for (int j = 0; j < eps.length; j++) {
                if (j > 0) sb.append("#");
                int idx = eps[j].indexOf("$");
                String epName = eps[j].substring(0, idx);
                String epUrl = eps[j].substring(idx + 1);
                sb.append(epName).append("$").append(code).append("@@").append(epUrl);
            }
            list.add(new String[]{name, sb.toString()});
        }
        list.sort((a, b) -> hdScore(b[0]) - hdScore(a[0]));
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            playFrom.append(list.get(i)[0]);
            playUrl.append(list.get(i)[1]);
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", id);
        vod.put("vod_name", html.optString("vod_name"));
        vod.put("vod_pic", html.optString("vod_pic"));
        vod.put("type_name", html.optString("vod_class"));
        vod.put("vod_year", html.optString("vod_year"));
        vod.put("vod_area", html.optString("vod_area"));
        vod.put("vod_remarks", html.optString("vod_remarks"));
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_content", html.optString("vod_content"));
        vod.put("vod_play_from", playFrom.toString());
        vod.put("vod_play_url", playUrl.toString());
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] ids = id.split("@@");
        String plays = "";
        JSONArray jiexiDataList = playinfo.getJSONArray("jiexiDataList");
        for (int i = 0; i < jiexiDataList.length(); i++) {
            JSONObject p = jiexiDataList.getJSONObject(i);
            String[] codes = p.optString("playerCode").split(",");
            for (String c : codes) {
                if (c.trim().equals(ids[0])) {
                    plays = p.optString("url");
                    break;
                }
            }
        }
        String url;
        if (!plays.isEmpty() && ids.length > 1) {
            Map<String, String> ua = new HashMap<>();
            ua.put("User-Agent", "Mozilla/5.0 (Linux; Android 4.2.1; M040 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.59 Mobile Safari/537.36");
            url = new JSONObject(JUtil.get(plays + ids[1], ua)).optString("url");
        } else {
            url = ids.length > 1 ? ids[1] : id;
        }
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", url);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String body = "{\"ac\":\"detail\",\"wd\":\"" + key + "\",\"pg\":\"1\"}";
        String content = de(JUtil.post(host + "/api.php/appfoxs/vod", body, gethh(body)));
        JSONObject html = new JSONObject(content);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            JSONObject vid = new JSONObject();
            vid.put("vod_play_from", item.optString("vod_play_from"));
            vid.put("vod_play_url", item.optString("vod_play_url"));
            v.put("vod_id", vid.toString());
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

    private int hdScore(String name) {
        for (String tag : HD_KEYS) {
            if (name.contains(tag)) return 1;
        }
        return 0;
    }

    private String opt(HashMap<String, String> extend, String key, String def) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : def;
    }
}
