package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiFun extends Spider {

    private static final String KEY = "GETMIFUNGEIMIFUN";
    private String host = "https://getcn.mymifun.com";

    @Override
    public void init(Context context, String extend) throws Exception {
        try {
            String content = OkHttp.string("https://miget-1313189639.cos.ap-guangzhou.myqcloud.com/mifun.txt");
            String[] lines = content.split("\r\n");
            if (lines.length > 1 && lines[1].startsWith("http")) host = lines[1];
        } catch (Exception ignored) {
        }
    }

    private String de(String data) {
        return JUtil.aesDecrypt(data, KEY, KEY);
    }

    private String en(String data) {
        return JUtil.aesEncrypt(data, KEY, KEY);
    }

    private Map<String, String> hh() {
        long t = System.currentTimeMillis() / 1000;
        Map<String, String> h = new HashMap<>();
        h.put("app-version-code", "516");
        h.put("app-ui-mode", "light");
        h.put("app-user-device-id", "26e450813c3ad3936acb7fbecfc249d1b");
        h.put("app-api-verify-time", String.valueOf(t));
        h.put("app-api-verify-sign", en(String.valueOf(t)));
        h.put("Content-Type", "application/x-www-form-urlencoded");
        h.put("User-Agent", "okhttp/3.14.9");
        return h;
    }

    private JSONObject request(String url, String body) throws Exception {
        String content = OkHttp.post(url, body, hh()).getBody();
        String data = new JSONObject(content).optString("data");
        return new JSONObject(de(data));
    }

    private JSONArray filterUrls(JSONArray list) throws Exception {
        String[] exclude = {"bfzym3u8", "tym3u8", "zjm3u8", "lzm3u8", "sdm3u8", "kbm3u8", "bjm3u8", "xkm3u8", "tpm3u8",
                "hnm3u8", "wjm3u8", "ffm3u8", "99m3u8", "dbm3u8", "mzm3u8", "mym3u8", "wwm3u8", "mtm3u8", "NMYS",
                "YHDM", "m3u8", "zlyun", "KYLG", "LKDB", "xnk", "AK_4K"};
        String[] high = {"4k", "4K", "2k", "2K", "臻彩"};
        List<JSONObject> filtered = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONArray urls = item.optJSONArray("urls");
            String firstFrom = urls != null && urls.length() > 0 ? urls.getJSONObject(0).optString("from") : "";
            boolean skip = false;
            for (String e : exclude) {
                if (e.equals(firstFrom)) {
                    skip = true;
                    break;
                }
            }
            if (!skip) filtered.add(item);
        }
        filtered.sort((a, b) -> {
            int scoreA = 0, scoreB = 0;
            String showA = a.optJSONObject("player_info") == null ? "" : a.optJSONObject("player_info").optString("show");
            String showB = b.optJSONObject("player_info") == null ? "" : b.optJSONObject("player_info").optString("show");
            for (String tag : high) {
                if (showA.contains(tag)) scoreA = 1;
                if (showB.contains(tag)) scoreB = 1;
            }
            return scoreB - scoreA;
        });
        JSONArray out = new JSONArray();
        for (JSONObject item : filtered) out.put(item);
        return out;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject res = request(host + "/api.php/getappapi.index/initV119", "");
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray typeList = res.getJSONArray("type_list");
        for (int i = 0; i < typeList.length(); i++) {
            JSONObject tp = typeList.getJSONObject(i);
            if (!"全部".equals(tp.optString("type_name"))) {
                classes.put(new JSONObject().put("type_id", tp.optString("type_id")).put("type_name", tp.optString("type_name")));
            }
            JSONArray filterTypeList = tp.optJSONArray("filter_type_list");
            if (filterTypeList != null) {
                JSONArray filters = new JSONArray();
                for (int j = 0; j < filterTypeList.length(); j++) {
                    JSONObject ft = filterTypeList.getJSONObject(j);
                    JSONArray fl = ft.getJSONArray("list");
                    JSONArray value = new JSONArray();
                    for (int k = 0; k < fl.length(); k++) {
                        value.put(new JSONObject().put("n", fl.getString(k)).put("v", fl.getString(k)));
                    }
                    filters.put(new JSONObject().put("key", ft.optString("name")).put("name", fl.optString(0)).put("value", value));
                }
                filterObj.put(tp.optString("type_id"), filters);
            }
        }
        JSONArray videos = new JSONArray();
        JSONArray banner = res.optJSONArray("banner_list");
        if (banner != null) {
            for (int i = 0; i < banner.length(); i++) {
                JSONObject item = banner.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("vod_remarks"))
                        .put("vod_year", item.optString("vod_year")));
            }
        }
        return new JSONObject().put("class", classes).put("filters", filterObj).put("list", videos).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String area = extend != null && extend.containsKey("area") ? extend.get("area") : "";
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "";
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "最热";
        String lang = extend != null && extend.containsKey("lang") ? extend.get("lang") : "";
        String cls = extend != null && extend.containsKey("class") ? extend.get("class") : "";
        String body = "area=" + area + "&year=" + year + "&type_id=" + tid + "&page=" + pg + "&sort=" + sort
                + "&lang=" + lang + "&class=" + cls;
        JSONObject res = request(host + "/api.php/getappapi.index/typeFilterVodList", body);
        JSONArray videos = new JSONArray();
        JSONArray recommend = res.optJSONArray("recommend_list");
        if (recommend != null) {
            for (int i = 0; i < recommend.length(); i++) {
                JSONObject item = recommend.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("vod_remarks"))
                        .put("vod_year", ""));
            }
        }
        return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject res = request(host + "/api.php/getappapi.index/vodDetail", "vod_id=" + ids.get(0));
        JSONArray playlist = filterUrls(res.getJSONArray("vod_play_list"));
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < playlist.length(); i++) {
            JSONObject item = playlist.getJSONObject(i);
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            JSONArray urls = item.getJSONArray("urls");
            playFrom.append(item.optJSONObject("player_info").optString("show")).append(" [").append(urls.getJSONObject(0).optString("from")).append("]");
            String p = item.optJSONObject("player_info").optString("parse");
            StringBuilder urlArr = new StringBuilder();
            for (int j = 0; j < urls.length(); j++) {
                JSONObject ep = urls.getJSONObject(j);
                if (j > 0) urlArr.append("#");
                String urlsStr;
                if (!p.isEmpty()) {
                    urlsStr = p.startsWith("http") ? ep.optString("parse_api_url") : p + "@@" + ep.optString("url") + "@@" + ep.optString("token");
                } else {
                    urlsStr = ep.optString("url");
                }
                urlArr.append(ep.optString("name").replaceAll("\\b0+(?=[1-9])", "")).append("$").append(urlsStr);
            }
            playUrl.append(urlArr);
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", res.optJSONObject("vod") == null ? "" : res.getJSONObject("vod").optString("vod_class"));
        vod.put("vod_year", res.optJSONObject("vod") == null ? "" : res.getJSONObject("vod").optString("vod_year"));
        vod.put("vod_area", res.optJSONObject("vod") == null ? "" : res.getJSONObject("vod").optString("vod_area"));
        vod.put("vod_remarks", res.optJSONObject("vod") == null ? "" : res.getJSONObject("vod").optString("vod_remarks"));
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_content", res.optJSONObject("vod") == null ? "" : res.getJSONObject("vod").optString("vod_content"));
        vod.put("vod_play_from", playFrom.toString());
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        if (id.contains("@@")) {
            String[] parts = id.split("@@");
            String parseApi = parts[0];
            String url = parts[1];
            String token = parts.length > 2 ? parts[2] : "";
            JSONObject res = request(host + "/api.php/getappapi.index/vodParse",
                    "parse_api=" + parseApi + "&url=" + URLEncoder.encode(en(url), "UTF-8") + "&token=" + token);
            String j = res.optString("json");
            if (j.startsWith("\"")) j = new JSONObject(j).optString("url");
            return new JSONObject().put("parse", 0).put("url", j).toString();
        }
        if (id.matches("(?i).*http.*url=.*m3u8|url=http.*m3u8|url.*http.*m3u8|\\?url=.*")) {
            try {
                String url = new JSONObject(OkHttp.string(id)).optString("url");
                return new JSONObject().put("parse", 0).put("url", url).toString();
            } catch (Exception ignored) {
            }
        }
        return new JSONObject().put("parse", 0).put("url", id).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject res = request(host + "/api.php/getappapi.index/searchList", "keywords=" + URLEncoder.encode(key, "UTF-8") + "&type_id=0&page=1");
        JSONArray videos = new JSONArray();
        JSONArray list = res.optJSONArray("search_list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("vod_remarks"))
                        .put("vod_year", item.optString("vod_year")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
