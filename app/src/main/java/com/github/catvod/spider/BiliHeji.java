package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BiliHeji extends Spider {

    private static final String[][] CLASSES = {
            {"沙雕动漫", "一口气沙雕动漫"}, {"纪录片", "纪录片超清"}, {"演唱会", "演唱会超清"},
            {"风景", "风景4K"}, {"说案", "说案"}, {"鬼畜", "鬼畜"}, {"搞笑", "搞笑超清"},
            {"儿童", "儿童超清"}, {"动物世界", "动物世界超清"}, {"相声小品", "相声小品超清"}, {"音乐", "音乐"}
    };

    @Override
    public void init(Context context, String extend) throws Exception {
        BiliCommon.initCookie();
    }

    private Map<String, String> header() {
        Map<String, String> h = new HashMap<>();
        h.put("Cookie", BiliCommon.cookies);
        h.put("User-Agent", BiliCommon.UA);
        h.put("Referer", "https://search.bilibili.com");
        return h;
    }

    private String gettime(String s) {
        if (s == null || !s.contains(":")) return "";
        String[] parts = s.split(":");
        int m = 0, sec = 0;
        try {
            m = Integer.parseInt(parts[0]);
            if (parts.length > 1) sec = Integer.parseInt(parts[1]);
        } catch (Exception ignored) {
        }
        return m < 60
                ? String.format("%02d:%02d", m, sec)
                : (m / 60) + ":" + String.format("%02d", m % 60) + ":" + String.format("%02d", sec);
    }

    private String cleanTitle(String title) {
        return Pattern.compile("<[^>]*>").matcher(title).replaceAll("");
    }

    private JSONObject search(String keyword, String pg) throws Exception {
        String url = BiliCommon.HOST + "/x/web-interface/search/type?search_type=video&keyword="
                + URLEncoder.encode(keyword, "UTF-8") + "&page=" + pg;
        return BiliCommon.request(url);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        for (String[] c : CLASSES) {
            classes.put(new JSONObject().put("type_name", c[0]).put("type_id", c[1]));
        }
        return new JSONObject().put("class", classes).put("filters", new JSONObject()).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        JSONObject html = search(tid, pg);
        JSONArray videos = new JSONArray();
        JSONArray result = html.getJSONObject("data").optJSONArray("result");
        if (result != null) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject item = result.getJSONObject(i);
                String pic = item.optString("pic");
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("bvid"))
                        .put("vod_name", cleanTitle(item.optString("title")))
                        .put("vod_pic", pic.startsWith("http") ? pic : "https:" + pic)
                        .put("vod_remarks", gettime(item.optString("duration"))));
            }
        }
        return new JSONObject().put("page", Integer.parseInt(pg)).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = BiliCommon.request(BiliCommon.HOST + "/x/web-interface/view?bvid=" + ids.get(0));
        JSONObject data = html.getJSONObject("data");
        JSONArray pages = data.getJSONArray("pages");
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < pages.length(); i++) {
            JSONObject p = pages.getJSONObject(i);
            if (i > 0) playUrl.append("#");
            playUrl.append(p.optString("part").replace("#", "")).append("$").append(ids.get(0)).append("@@").append(p.optInt("cid"));
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", data.optString("title"));
        String bpic = data.optString("pic");
        vod.put("vod_pic", bpic.startsWith("http") ? bpic : "https:" + bpic);
        vod.put("vod_director", data.optJSONObject("owner") == null ? "" : data.optJSONObject("owner").optString("name"));
        vod.put("vod_content", data.optString("desc"));
        vod.put("vod_play_from", "哔哩");
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        String bvid = parts[0];
        String cid = parts.length > 1 ? parts[1] : "";
        JSONObject html = BiliCommon.request(BiliCommon.playUrl(bvid, cid, "", ""));
        JSONObject data = html.getJSONObject("data");
        JSONArray acceptQuality = data.getJSONArray("accept_quality");
        JSONArray acceptDesc = data.getJSONArray("accept_description");
        JSONArray dashVideos = data.getJSONObject("dash").getJSONArray("video");
        JSONArray url = new JSONArray();
        String dlurl = Proxy.localProxyUrl() + "?do=bili";
        for (int i = 0; i < acceptQuality.length(); i++) {
            String qn = String.valueOf(acceptQuality.optInt(i));
            boolean found = false;
            for (int j = 0; j < dashVideos.length(); j++) {
                if (String.valueOf(dashVideos.getJSONObject(j).optInt("id")).equals(qn)) {
                    found = true;
                    break;
                }
            }
            if (!found) continue;
            url.put(acceptDesc.optString(i));
            url.put(dlurl + "&bvid=" + bvid + "&cid=" + cid + "&qn=" + qn);
        }
        JSONObject result = new JSONObject();
        result.put("header", new JSONObject()
                .put("Cookie", BiliCommon.cookies)
                .put("User-Agent", BiliCommon.UA)
                .put("Referer", "https://www.bilibili.com"));
        result.put("parse", 0);
        result.put("url", url);
        result.put("danmaku", "https://api.bilibili.com/x/v1/dm/list.so?oid=" + cid);
        result.put("format", "application/dash+xml");
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = search(key, "1");
        JSONArray videos = new JSONArray();
        JSONArray result = html.getJSONObject("data").optJSONArray("result");
        if (result != null) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject item = result.getJSONObject(i);
                String pic = item.optString("pic");
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("bvid"))
                        .put("vod_name", cleanTitle(item.optString("title")))
                        .put("vod_pic", pic.startsWith("http") ? pic : "https:" + pic)
                        .put("vod_remarks", gettime(item.optString("duration"))));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
