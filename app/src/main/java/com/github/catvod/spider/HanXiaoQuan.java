package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HanXiaoQuan extends Spider {

    private static final String HOST = "https://hxqapi.hiyun.tv";
    private static final Random RANDOM = new Random();

    private JSONObject de(String data) throws Exception {
        JSONObject items = new JSONObject(data);
        String inner = JUtil.md5("ikk1Kuq1E4T018TUnSQ6" + items.optString("ts"));
        String md5Str = JUtil.md5(inner + "34F9Q53w/HJW8E6Q");
        String key = md5Str.substring(0, 16);
        String iv = md5Str.substring(16);
        return new JSONObject(JUtil.aesDecrypt(items.optString("data"), key, iv));
    }

    private JSONObject deplay(String data) throws Exception {
        JSONObject items = new JSONObject(data);
        String inner = JUtil.md5("ikk1Kuq1E4T018TUnSQ6");
        String md5Str = JUtil.md5(inner + "34F9Q53w/HJW8E6Q");
        String key = md5Str.substring(0, 16);
        String iv = md5Str.substring(16);
        return new JSONObject(JUtil.aesDecrypt(items.getJSONArray("datas").getJSONObject(0).optString("data"), key, iv));
    }

    private String en(String data) {
        return JUtil.aesEncrypt(data, "a9fc04840498848e", "3cb63eec5e162717");
    }

    private Map<String, String> gethh() throws Exception {
        long t = System.currentTimeMillis();
        String data = new JSONObject()
                .put("emu", 0).put("ou", 0).put("it", t).put("iit", t).put("bs", 0).put("uid", "ikk1Kuq1E4T018TUnSQ6")
                .put("pc", 0).put("tm", 60).put("d8m", "0,0,0,0,0,0,0,0").put("md", "23113RKC6C").put("maker", "Redmi")
                .put("osv", "9").put("br", -2147483648).put("rpc", 0).put("scc", 1).put("plc", 0).put("toc", 9)
                .put("tsc", 1).put("ts", t).put("pa", 1).put("nw", 2).put("px", "0").put("isp", "")
                .put("ai", "b7ca10733358e7ca").put("ii", "").put("dpc", 0).put("dsc", 0).put("qpc", 0).put("apad", 0)
                .put("pk", "com.babycloud.hanju").toString();
        Map<String, String> h = new HashMap<>();
        h.put("vc", "a_8110");
        h.put("vn", "6.6.5");
        h.put("ch", "xiaomi");
        h.put("app", "hj");
        h.put("User-Agent", "HanjuTV/6.6.5 (23113RKC6C; Android 9; Scale/2.00)");
        h.put("said", "68babf529c1e02ba");
        h.put("uk", "T98Aa/zIMX3qoHOqvCgJwCOkl8Fvqa2GOrSBz/g37YE=");
        h.put("auth-token", "");
        h.put("auth-uid", "");
        h.put("sign", en(data));
        return h;
    }

    private String getRandStr(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(62)));
        }
        return sb.toString();
    }

    public static String m3u8(String pid, String sq) {
        try {
            HanXiaoQuan s = new HanXiaoQuan();
            JSONObject res1 = s.de(OkHttp.string(HOST + "/api/series2/episode/detail?pid=" + pid, s.gethh()));
            JSONObject playItem = res1.getJSONObject("playItem");
            String realPid = playItem.optString("pid");
            String scid = playItem.getJSONArray("sources").getJSONObject(0).optString("scid");
            String uuid = s.getRandStr(32);
            String traceId = s.de(OkHttp.string(HOST + "/api/carp/reward/v2?scene=ad_series_play", s.gethh())).optString("traceId");
            String bodydata = s.en("{\"pid\":\"" + realPid + "\",\"scene\":\"ad_series_play\",\"traceId\":\"" + traceId + "\"}");
            Map<String, String> headers = s.gethh();
            String aps = JUtil.md5("{\"data\":\"" + bodydata + "\"}GIpxY0JPylRx").toLowerCase();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("aps", aps);
            String ttk = s.de(OkHttp.post(HOST + "/api/carp/reward/rp/v2", "{\"data\":\"" + bodydata + "\"}", headers).getBody())
                    .getJSONObject("rewardTokenInfo").optString("token");
            long t = System.currentTimeMillis() / 1000;
            String sign = JUtil.md5("&version=6.6.5&uuid=" + uuid + "&udid=a9fc04840498848e3cb63eec5e162717&ttk=" + ttk
                    + "&t=" + t + "&sq=" + sq + "&scid=" + scid + "&re=1&pid=" + realPid + "&dt=android&2E159Q/Z8979WckQ");
            String playUrl = HOST + "/api/series/rslvV4?t=" + t + "&dt=android&version=6.6.5&uuid=" + uuid
                    + "&pid=" + realPid + "&scid=" + scid + "&sq=" + sq + "&re=1&ttk=" + ttk + "&sign=" + sign;
            JSONObject play = s.deplay(OkHttp.string(playUrl, s.gethh()));
            Map<String, String> ph = new HashMap<>();
            JSONObject header = play.optJSONObject("header");
            if (header != null) {
                JSONArray names = header.names();
                for (int i = 0; i < names.length(); i++) {
                    ph.put(names.getString(i), header.optString(names.getString(i)));
                }
            }
            String m3u8 = OkHttp.string(play.optString("playUrl"), ph);
            String[] urlParts = play.optString("playUrl").split("\\?");
            String a = ".ts?" + (urlParts.length > 1 ? urlParts[1] : "");
            String[] bParts = play.optString("playUrl").split("/");
            String b = "https://" + bParts[2];
            m3u8 = m3u8.replaceAll("(?m)^/.+\\.ts$", b + "$0").replace(".ts", a);
            return m3u8;
        } catch (Exception e) {
            return "#EXTM3U";
        }
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = de(OkHttp.string(HOST + "/api/series2/arrange/cate?stype=1", gethh()));
        JSONArray groups = html.getJSONArray("groups");
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            classes.put(new JSONObject().put("type_id", group.optString("stype")).put("type_name", group.optString("name")));
            JSONArray classV = new JSONArray();
            JSONArray cates = group.optJSONArray("cates");
            if (cates != null) {
                for (int j = 0; j < cates.length(); j++) {
                    JSONObject c = cates.getJSONObject(j);
                    String v = c.optString("value");
                    classV.put(new JSONObject().put("n", c.optString("name")).put("v", v.isEmpty() ? c.optString("name") : v));
                }
            }
            JSONArray yearV = new JSONArray();
            JSONArray years = html.optJSONArray("years");
            if (years != null) {
                for (int j = 0; j < years.length(); j++) {
                    JSONObject y = years.getJSONObject(j);
                    yearV.put(new JSONObject().put("n", y.optString("name")).put("v", y.optString("value")));
                }
            }
            JSONArray sortV = new JSONArray();
            JSONArray sorts = html.optJSONArray("sorts");
            if (sorts != null) {
                for (int j = 0; j < sorts.length(); j++) {
                    JSONObject s = sorts.getJSONObject(j);
                    sortV.put(new JSONObject().put("n", s.optString("name")).put("v", s.optString("value")));
                }
            }
            JSONArray filters = new JSONArray();
            filters.put(new JSONObject().put("key", "class").put("name", "剧情").put("value", classV));
            filters.put(new JSONObject().put("key", "year").put("name", "年份").put("value", yearV));
            filters.put(new JSONObject().put("key", "sort").put("name", "排序").put("value", sortV));
            filterObj.put(group.optString("stype"), filters);
        }
        JSONObject tj = de(OkHttp.string(HOST + "/api/index/recommend_v5?page=1", gethh()));
        JSONArray videos = new JSONArray();
        JSONArray seriesList = tj.optJSONArray("mediaBlocks") == null ? null
                : tj.getJSONArray("mediaBlocks").getJSONObject(0).optJSONArray("seriesList");
        if (seriesList != null) {
            for (int i = 0; i < seriesList.length(); i++) {
                JSONObject item = seriesList.getJSONObject(i);
                JSONObject image = item.optJSONObject("image");
                String thumb = image == null ? "" : image.optString("thumb");
                String poster = image == null ? "" : image.optString("poster");
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("sid"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", thumb.isEmpty() ? poster : thumb)
                        .put("vod_remarks", item.optString("detailMemo"))
                        .put("vod_year", item.optString("shorthand")));
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
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "hot";
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "-1";
        String cls = extend != null && extend.containsKey("class") ? extend.get("class") : "-1";
        JSONObject html = de(OkHttp.string(HOST + "/api/series2/arrange/cate?stype=" + tid + "&sort=" + sort
                + "&year=" + year + "&cid=" + cls + "&page=" + pg, gethh()));
        JSONArray videos = new JSONArray();
        JSONArray seriesList = html.optJSONArray("seriesList");
        if (seriesList != null) {
            for (int i = 0; i < seriesList.length(); i++) {
                JSONObject item = seriesList.getJSONObject(i);
                String thumb = item.optJSONObject("image") == null ? "" : item.getJSONObject("image").optString("thumb");
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("sid"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", thumb)
                        .put("vod_remarks", item.optString("detailMemo"))
                        .put("vod_year", item.optString("shorthand")));
            }
        }
        return new JSONObject().put("page", Integer.parseInt(pg)).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = de(OkHttp.string(HOST + "/api/series2/detail/normal?sid=" + ids.get(0), gethh()));
        JSONArray qualities = html.optJSONArray("scopeQualities");
        JSONArray playItems = html.optJSONArray("playItems");
        if (qualities == null) qualities = new JSONArray();
        if (playItems == null) playItems = new JSONArray();
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < qualities.length(); i++) {
            JSONObject q = qualities.getJSONObject(i);
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            playFrom.append(q.optString("name")).append(q.optString("resolution"));
            for (int j = 0; j < playItems.length(); j++) {
                JSONObject play = playItems.getJSONObject(j);
                if (j > 0) playUrl.append("#");
                playUrl.append(play.optString("serialNo")).append("$").append(play.optString("pid")).append("@@").append(q.optString("value"));
            }
        }
        JSONObject series = html.optJSONObject("series");
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", series == null ? "" : series.optString("name"));
        String thumb = series != null && series.optJSONObject("image") != null ? series.getJSONObject("image").optString("thumb") : "";
        if (thumb.isEmpty() && series != null && series.optJSONObject("image") != null) thumb = series.getJSONObject("image").optString("poster");
        vod.put("vod_pic", thumb);
        vod.put("type_name", "");
        vod.put("vod_year", "");
        vod.put("vod_area", "");
        vod.put("vod_remarks", series == null ? "" : series.optString("detailMemo"));
        vod.put("vod_actor", series == null ? "" : series.optString("crew").replaceFirst(".+?:|.+?：", ""));
        vod.put("vod_director", series == null ? "" : series.optString("shorthand"));
        vod.put("vod_content", series == null ? "" : series.optString("intro").replaceAll("<.+?>|&nbsp;", " "));
        vod.put("vod_play_from", playFrom.toString());
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        String url = Proxy.localProxyUrl() + "?do=hanju&pid=" + parts[0] + "&sq=" + (parts.length > 1 ? parts[1] : "");
        return new JSONObject().put("parse", 0).put("url", url).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = de(OkHttp.string(HOST + "/api/search/s5?k=" + URLEncoder.encode(key, "UTF-8")
                + "&srefer=search_input&type=2&page=1", gethh()));
        JSONArray videos = new JSONArray();
        JSONArray seriesList = html.optJSONArray("seriesList");
        if (seriesList != null) {
            for (int i = 0; i < seriesList.length(); i++) {
                JSONObject item = seriesList.getJSONObject(i);
                String thumb = item.optJSONObject("image") == null ? "" : item.getJSONObject("image").optString("thumb");
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("sid"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", thumb)
                        .put("vod_remarks", item.optString("detailMemo"))
                        .put("vod_year", item.optString("shorthand")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
