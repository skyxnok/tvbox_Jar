package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XingYaDuanJu extends Spider {

    private static final String XURL = "https://app.whjzjx.cn";
    private static final String KEY = "B@ecf920Od8A4df7";
    private static final String JIDUO = "https://fs-im-kefu.7moor-fs1.com/ly/4d2c3f00-7d4c-11e5-af15-41bf63ae4ea0/1732707176882/jiduo.txt";

    private String authorization = "";

    @Override
    public void init(Context context, String extend) throws Exception {
        Map<String, String> h = new HashMap<>();
        h.put("platform", "1");
        h.put("user_agent", "Mozilla/5.0 (Linux; Android 9; V1938T Build/PQ3A.190705.08211809; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Safari/537.36");
        h.put("content-type", "application/json; charset=utf-8");
        JSONObject data = new JSONObject()
                .put("device", "2a50580e69d38388c94c93605241fb306")
                .put("package_name", "com.jz.xydj")
                .put("android_id", "ec1280db12795506")
                .put("install_first_open", true)
                .put("first_install_time", 1752505243345L)
                .put("last_update_time", 1752505243345L)
                .put("report_link_url", "")
                .put("authorization", "")
                .put("timestamp", System.currentTimeMillis());
        String encrypted = JUtil.aesEncryptEcb(data.toString(), KEY);
        JSONObject resp = new JSONObject(OkHttp.post("https://u.shytkjgs.com/user/v3/account/login", encrypted, h).getBody());
        JSONObject d = resp.optJSONObject("data");
        if (d != null) authorization = d.optString("token");
    }

    private Map<String, String> headerx() {
        Map<String, String> h = new HashMap<>();
        h.put("authorization", authorization);
        h.put("platform", "1");
        h.put("version_name", "3.8.3.1");
        return h;
    }

    private String extractMiddle(String text, String startStr, String endStr) {
        int start = text.indexOf(startStr);
        if (start == -1) return "";
        int end = text.indexOf(endStr, start + startStr.length());
        if (end == -1) return "";
        return text.substring(start + startStr.length(), end).replace("\\", "");
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] cs = {{"1", "剧场"}, {"3", "新剧"}, {"2", "热播"}, {"7", "星选"}, {"5", "阳光"}};
        for (String[] c : cs) {
            classes.put(new JSONObject().put("type_id", c[0]).put("type_name", c[1]));
        }
        return new JSONObject().put("class", classes).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        JSONObject data = new JSONObject(OkHttp.string(XURL + "/v1/theater/home_page?theater_class_id=1&class2_id=4&page_num=1&page_size=24", headerx()));
        JSONArray videos = new JSONArray();
        JSONArray list = data.getJSONObject("data").optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject t = list.getJSONObject(i).getJSONObject("theater");
                videos.put(new JSONObject()
                        .put("vod_id", t.optString("id"))
                        .put("vod_name", t.optString("title"))
                        .put("vod_pic", t.optString("cover_url"))
                        .put("vod_remarks", t.optString("play_amount_str")));
            }
        }
        return new JSONObject().put("list", videos).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        JSONObject data = new JSONObject(OkHttp.string(XURL + "/v1/theater/home_page?theater_class_id=" + tid + "&page_num=" + pg + "&page_size=24", headerx()));
        JSONArray videos = new JSONArray();
        JSONArray list = data.getJSONObject("data").optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject t = list.getJSONObject(i).getJSONObject("theater");
                videos.put(new JSONObject()
                        .put("vod_id", t.optString("id"))
                        .put("vod_name", t.optString("title"))
                        .put("vod_pic", t.optString("cover_url"))
                        .put("vod_remarks", t.optString("theme")));
            }
        }
        return new JSONObject().put("list", videos).put("page", Integer.parseInt(pg)).put("pagecount", 9999)
                .put("limit", 90).put("total", 999999).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String did = ids.get(0);
        JSONObject data = new JSONObject(OkHttp.string(XURL + "/v2/theater_parent/detail?theater_parent_id=" + did, headerx())).getJSONObject("data");
        String code = "";
        try {
            code = OkHttp.string(JIDUO);
        } catch (Exception ignored) {
        }
        String jumps = extractMiddle(code, "s2='", "'");
        String content = "剧情：" + data.optString("introduction");
        String area = data.optJSONArray("desc_tags") != null && data.getJSONArray("desc_tags").length() > 0
                ? data.getJSONArray("desc_tags").optString(0) : "";
        String remarks = data.optString("filing");
        String bofang = "";
        String xianlu = "";
        JSONArray theaters = data.optJSONArray("theaters");
        if (theaters != null && theaters.length() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < theaters.length(); i++) {
                JSONObject sou = theaters.getJSONObject(i);
                sb.append(sou.optString("num")).append("$").append(sou.optString("son_video_url")).append("#");
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 1);
            bofang = sb.toString();
            xianlu = "星芽";
        } else {
            String videoUrl = data.optString("video_url");
            if (!videoUrl.isEmpty()) {
                bofang = "1$" + videoUrl;
                xianlu = "星芽";
            } else {
                bofang = jumps;
                xianlu = "1";
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", did);
        vod.put("vod_name", data.optString("title"));
        vod.put("vod_pic", data.optString("cover_url"));
        vod.put("vod_content", content);
        vod.put("vod_remarks", remarks);
        vod.put("vod_area", area);
        vod.put("vod_play_from", xianlu);
        vod.put("vod_play_url", bofang);
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        Map<String, String> h = new HashMap<>();
        h.put("User-Agent", "Linux; Android 12; Pixel 3 XL) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.101 Mobile Safari/537.36");
        return new JSONObject().put("parse", 0).put("playUrl", "").put("url", id).put("header", new JSONObject(h)).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject payload = new JSONObject().put("text", key);
        Map<String, String> h = headerx();
        h.put("Content-Type", "application/json; charset=utf-8");
        JSONObject data = new JSONObject(OkHttp.post(XURL + "/v3/search", payload.toString(), h).getBody()).getJSONObject("data");
        JSONArray videos = new JSONArray();
        JSONArray list = data.getJSONObject("theater").optJSONArray("search_data");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject vod = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", vod.optString("id"))
                        .put("vod_name", vod.optString("title"))
                        .put("vod_pic", vod.optString("cover_url"))
                        .put("vod_remarks", vod.optString("score_str")));
            }
        }
        return new JSONObject().put("list", videos).put("page", 1).put("pagecount", 9999)
                .put("limit", 90).put("total", 999999).toString();
    }
}
