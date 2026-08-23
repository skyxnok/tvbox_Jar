package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;

public class DuoDuoZhuiJu extends Spider {

    private static final String[] RESP_KEYS = {
            "A7mQ9vL2pX4rZ8tN", "b3Tn6Yq8Kp2Vx5Ls", "R5cH2wN9eM7qP4vD", "x8Lk1Zp6Cw3Nq9Ty",
            "M4vS7rQ2bT9hX6kE", "p9Dq3Lx8Vn5Cz2Ra", "K2wF6tM8yQ4sH7Np", "z6Pj9Rb3Lc8Vx1Tm",
            "N8qC4yL7pS2dK5Wa", "t3Vx9Mn6Qp4Rs8Yk", "C7hL2qT5vN9xB3Wp", "y4Rk8Pz1Md6Lq3Vs",
            "L9pX2cQ7tV4nR8Hy", "q5Nw8Zr3Kp6Mt2Va", "V2cT9yL5Rq8Nw4Ks", "s8Kp4Xn7Cw2Vq9Md",
            "D6rM1tY8pL3zQ5Vx", "w9Qv5Ck2Nr8Ty4Lp", "P3xL7mR9qV2cN6Ty", "h2Zq8Vn4Kp7Sx5Mc",
            "T8mC3yQ6rL9pV2Nw", "n5Rk2Pz8Xq4Vt7Ls", "Q4vN9cL3Mp6Yx2Rk", "k7Tq1Wv5Zn8Pc4Ms",
            "X6pL3rV8Cq2Ny9Kt", "m2Qz7Kp4Vx9Ts5Rc", "Y9cV5nL2Rq8Pw3Kx", "r4Mp8Tq1Zc6Vn9Ly",
            "B7xQ2vK9pR5Lm3Ts", "u8Lr4Cq7Nw2Vp5Yz"};
    private static final Random RANDOM = new Random();

    private String host = "https://bubutv.top";
    private String finger = "SF-C3B2B41F6EFFFF9869176CF68F6790E8F07506FC88632C94B4F5F0430D5498CA";
    private String sk = "SK-random";
    private String ave = "8";
    private String avn = "1.6.1";
    private String aid = "com.sunshine.tv";

    @Override
    public void init(Context context, String extend) throws Exception {
        if (extend != null && !extend.isEmpty()) {
            JSONObject ext = new JSONObject(extend);
            if (ext.has("host")) host = ext.optString("host");
            if (ext.has("finger")) finger = ext.optString("finger");
            if (ext.has("sk")) sk = ext.optString("sk");
            if (ext.has("ave")) ave = ext.optString("ave");
            if (ext.has("avn")) avn = ext.optString("avn");
            if (ext.has("aid")) aid = ext.optString("aid");
        }
    }

    private JSONObject request(String url, JSONObject body) throws Exception {
        long t = System.currentTimeMillis();
        StringBuilder nonc = new StringBuilder();
        String hex = "0123456789ABCDEF";
        for (int i = 0; i < 16; i++) nonc.append(hex.charAt(RANDOM.nextInt(16)));
        String sign = JUtil.sha256("finger=" + finger + "&id=" + aid + "&nonce=" + nonc + "&sk=" + sk + "&time=" + t + "&v=" + ave).toUpperCase();
        Map<String, String> h = new HashMap<>();
        h.put("x-aid", aid);
        h.put("x-ave", ave);
        h.put("x-avn", avn);
        h.put("x-time", String.valueOf(t));
        h.put("x-nonc", nonc.toString());
        h.put("x-sign", sign);
        h.put("accept", "application/json");
        h.put("x-device-id", "68babf529c1e02ba");
        h.put("x-device-brand", "Xiaomi");
        h.put("x-device-model", "23116PN5BC");
        h.put("x-update-id", "7a24e3d3-c8f9-109a-0ce3-67e6bd094e01");
        h.put("user-agent", "okhttp/4.12.0");
        Request.Builder builder = new Request.Builder().url(url).headers(okhttp3.Headers.of(h));
        if (body != null) {
            builder.post(okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), body.toString()));
        } else {
            builder.get();
        }
        Response resp = OkHttp.newCall(builder.build());
        byte[] bytes = resp.body() == null ? new byte[0] : resp.body().bytes();
        String keyIndex = resp.headers().get("mcg821-a");
        if (keyIndex == null) keyIndex = resp.headers().get("Mcg821-A");
        resp.close();
        if (keyIndex == null) keyIndex = "0";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        String content = sb.toString();
        int idx;
        try {
            idx = Integer.parseInt(keyIndex.trim());
        } catch (Exception e) {
            idx = 0;
        }
        if (idx >= 0 && idx < RESP_KEYS.length) {
            content = JUtil.aesGcmDecrypt(content, RESP_KEYS[idx]);
        }
        return new JSONObject(content);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(host + "/api.php/app/index/home", null);
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray categories = html.getJSONObject("data").getJSONArray("categories");
        for (int i = 0; i < categories.length(); i++) {
            String name = categories.getJSONObject(i).optString("type_name");
            classes.put(new JSONObject().put("type_id", name).put("type_name", name));
            JSONArray sorts = new JSONArray();
            sorts.put(new JSONObject().put("n", "人气").put("v", "hits"));
            sorts.put(new JSONObject().put("n", "最新").put("v", "time"));
            sorts.put(new JSONObject().put("n", "评分").put("v", "score"));
            sorts.put(new JSONObject().put("n", "年份").put("v", "year"));
            JSONArray filters = new JSONArray();
            filters.put(new JSONObject().put("key", "sort").put("name", "排序").put("value", sorts));
            filterObj.put(name, filters);
        }
        JSONArray videos = new JSONArray();
        JSONArray recommend = html.getJSONObject("data").optJSONArray("recommend");
        if (recommend != null) {
            for (int i = 0; i < recommend.length(); i++) {
                JSONObject item = recommend.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("vod_remarks")));
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
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "hits";
        JSONObject html = request(host + "/api.php/app/filter/vod?type_name=" + URLEncoder.encode(tid, "UTF-8")
                + "&page=" + pg + "&sort=" + sort, null);
        JSONArray videos = new JSONArray();
        JSONArray data = html.optJSONArray("data");
        if (data != null) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("vod_remarks"))
                        .put("vod_year", item.optString("vod_year")));
            }
        }
        return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String id = ids.get(0);
        JSONObject html = request(host + "/api.php/app/vod/get_detail?vod_id=" + id, null);
        JSONObject cj = request(host + "/api.php/app/internal/search_aggregate?vod_id=" + id, null);
        StringBuilder cjfrom = new StringBuilder();
        StringBuilder cjurl = new StringBuilder();
        JSONArray cjData = cj.optJSONObject("data") == null ? null : cj.getJSONObject("data").optJSONArray("data");
        if (cjData != null) {
            for (int i = 0; i < cjData.length(); i++) {
                JSONObject item = cjData.getJSONObject(i);
                if (i > 0) {
                    cjfrom.append("$$$");
                    cjurl.append("$$$");
                }
                cjfrom.append(item.optString("site_name"));
                cjurl.append(item.optString("vod_play_url"));
            }
        }
        JSONArray play = html.getJSONArray("vodplayer");
        List<JSONObject> sorted = new java.util.ArrayList<>();
        for (int i = 0; i < play.length(); i++) sorted.add(play.getJSONObject(i));
        sorted.sort((a, b) -> {
            int ra = rank(a.optString("from")), rb = rank(b.optString("from"));
            if (ra != rb) return ra - rb;
            return (int) (a.optDouble("sort", 0) - b.optDouble("sort", 0));
        });
        JSONObject res = html.getJSONArray("data").getJSONObject(0);
        String[] fromArr = res.optString("vod_play_from").split("\\$\\$\\$");
        String[] urlArr = res.optString("vod_play_url").split("\\$\\$\\$");
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        int count = 0;
        for (JSONObject p : sorted) {
            String from = p.optString("from");
            int idx = -1;
            for (int i = 0; i < fromArr.length; i++) {
                if (from.equals(fromArr[i])) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) continue;
            if (count > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            playFrom.append(p.optString("show")).append(" [").append(from).append("]");
            String[] urls = (idx < urlArr.length ? urlArr[idx] : "").split("#");
            for (int i = 0; i < urls.length; i++) {
                if (i > 0) playUrl.append("#");
                playUrl.append(urls[i]).append("@@").append(from);
            }
            count++;
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", res.optString("vod_class"));
        vod.put("vod_year", res.optString("vod_year"));
        vod.put("vod_area", res.optString("vod_area"));
        vod.put("vod_remarks", res.optString("vod_remarks"));
        vod.put("vod_actor", res.optString("vod_actor"));
        vod.put("vod_director", res.optString("vod_director"));
        vod.put("vod_content", res.optString("vod_content").replaceAll("<.*?>", ""));
        vod.put("vod_play_from", playFrom + "$$$" + cjfrom);
        vod.put("vod_play_url", playUrl + "$$$" + cjurl);
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    private int rank(String f) {
        String s = f.toLowerCase();
        if (s.contains("4k")) return 1;
        if (s.contains("2k")) return 2;
        if (s.contains("臻彩") || s.contains("真彩")) return 3;
        return 99;
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        if (id.contains("@@")) {
            String[] parts = id.split("@@");
            JSONObject res = request(host + "/api.php/app/decode/url/?url=" + URLEncoder.encode(parts[0], "UTF-8")
                    + "&vodFrom=" + parts[1], null);
            return new JSONObject().put("parse", 0).put("url", res.optString("data")).toString();
        }
        return new JSONObject().put("parse", 0).put("url", id).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = request(host + "/api.php/app/search/index?wd=" + URLEncoder.encode(key, "UTF-8") + "&page=1&limit=15", null);
        JSONArray videos = new JSONArray();
        JSONArray data = html.optJSONArray("data");
        if (data != null) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
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
