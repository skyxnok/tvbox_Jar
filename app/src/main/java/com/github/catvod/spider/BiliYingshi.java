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

public class BiliYingshi extends Spider {

    private static final String[][] CLASSES = {
            {"番剧", "1"}, {"国创", "4"}, {"剧集", "5"}, {"综艺", "7"}, {"纪录", "3"}
    };
    private static final String FILTER_JSON = "{\"2\":[{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"播放数量\",\"v\":\"2\"},{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"最高评分\",\"v\":\"4\"},{\"n\":\"弹幕数量\",\"v\":\"1\"},{\"n\":\"追看人数\",\"v\":\"3\"},{\"n\":\"开播时间\",\"v\":\"5\"},{\"n\":\"上映时间\",\"v\":\"6\"}]}],\"5\":[{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"播放数量\",\"v\":\"2\"},{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"最高评分\",\"v\":\"4\"},{\"n\":\"弹幕数量\",\"v\":\"1\"},{\"n\":\"追看人数\",\"v\":\"3\"},{\"n\":\"开播时间\",\"v\":\"5\"},{\"n\":\"上映时间\",\"v\":\"6\"}]}],\"7\":[{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"播放数量\",\"v\":\"2\"},{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"最高评分\",\"v\":\"4\"},{\"n\":\"弹幕数量\",\"v\":\"1\"},{\"n\":\"追看人数\",\"v\":\"3\"},{\"n\":\"开播时间\",\"v\":\"5\"},{\"n\":\"上映时间\",\"v\":\"6\"}]}],\"3\":[{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"播放数量\",\"v\":\"2\"},{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"最高评分\",\"v\":\"4\"},{\"n\":\"弹幕数量\",\"v\":\"1\"},{\"n\":\"追看人数\",\"v\":\"3\"},{\"n\":\"开播时间\",\"v\":\"5\"},{\"n\":\"上映时间\",\"v\":\"6\"}]}],\"1\":[{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"播放数量\",\"v\":\"2\"},{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"最高评分\",\"v\":\"4\"},{\"n\":\"弹幕数量\",\"v\":\"1\"},{\"n\":\"追看人数\",\"v\":\"3\"},{\"n\":\"开播时间\",\"v\":\"5\"},{\"n\":\"上映时间\",\"v\":\"6\"}]}],\"4\":[{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"播放数量\",\"v\":\"2\"},{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"最高评分\",\"v\":\"4\"},{\"n\":\"弹幕数量\",\"v\":\"1\"},{\"n\":\"追看人数\",\"v\":\"3\"},{\"n\":\"开播时间\",\"v\":\"5\"},{\"n\":\"上映时间\",\"v\":\"6\"}]}]}";

    @Override
    public void init(Context context, String extend) throws Exception {
        BiliCommon.initCookie();
    }

    private JSONObject pgc(String url) throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", BiliCommon.cookies);
        header.put("User-Agent", BiliCommon.UA);
        header.put("Referer", "https://www.bilibili.com");
        return new JSONObject(com.github.catvod.net.OkHttp.string(url, header));
    }

    private String cleanTitle(String title) {
        return Pattern.compile("<[^>]*>").matcher(title).replaceAll("");
    }

    private String joinArray(Object obj, String sep) {
        if (obj instanceof JSONArray) {
            JSONArray arr = (JSONArray) obj;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length(); i++) {
                if (i > 0) sb.append(sep);
                sb.append(arr.optString(i));
            }
            return sb.toString();
        }
        return String.valueOf(obj);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        for (String[] c : CLASSES) {
            classes.put(new JSONObject().put("type_name", c[0]).put("type_id", c[1]));
        }
        JSONObject html = pgc(BiliCommon.HOST + "/pgc/season/index/result?order=4&area=-1&style_id=-1&season_version=-1"
                + "&season_status=-1&spoken_language_type=-1&copyright=-1&is_finish=-1&year=-1&season_month=-1"
                + "&season_type=1&type=0&page=1&pagesize=21");
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("season_id"))
                        .put("vod_name", item.optString("title"))
                        .put("vod_pic", item.optString("cover"))
                        .put("vod_remarks", item.optString("index_show"))
                        .put("vod_year", item.optString("order")));
            }
        }
        return new JSONObject().put("class", classes).put("filters", new JSONObject(FILTER_JSON)).put("list", videos).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String order = extend != null && extend.containsKey("order") ? extend.get("order") : "3";
        JSONObject html = pgc(BiliCommon.HOST + "/pgc/season/index/result?order=" + order
                + "&season_status=-1&style_id=-1&sort=-1&area=-1&pagesize=20&type=1&season_type=" + tid + "&page=" + pg);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("season_id"))
                        .put("vod_name", item.optString("title"))
                        .put("vod_pic", item.optString("cover"))
                        .put("vod_remarks", item.optString("index_show"))
                        .put("vod_year", item.optString("order")));
            }
        }
        return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = pgc(BiliCommon.HOST + "/pgc/view/web/season?season_id=" + ids.get(0));
        JSONObject r = html.getJSONObject("result");
        JSONArray episodes = r.getJSONArray("episodes");
        StringBuilder playUrl = new StringBuilder();
        int count = 0;
        for (int i = 0; i < episodes.length(); i++) {
            JSONObject item = episodes.getJSONObject(i);
            if ("预告".equals(item.optString("badge"))) continue;
            if (count > 0) playUrl.append("#");
            playUrl.append(item.optString("show_title")).append("$").append(item.optString("bvid")).append("@@")
                    .append(item.optString("ep_id")).append("@@").append(item.optString("id")).append("@@")
                    .append(item.optString("cid")).append("@@").append(item.optString("link")).append("@@")
                    .append(item.optString("badge"));
            count++;
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", joinArray(r.opt("styles"), "\n"));
        String pubTime = r.optJSONObject("publish") == null ? "" : r.optJSONObject("publish").optString("pub_time");
        vod.put("vod_year", pubTime.contains("-") ? pubTime.split("-")[0] : "");
        vod.put("vod_area", joinArray(r.opt("areas"), ""));
        vod.put("vod_remarks", r.optJSONObject("new_ep") == null ? "" : r.optJSONObject("new_ep").optString("desc"));
        vod.put("vod_actor", joinArray(r.opt("actors"), ","));
        vod.put("vod_director", joinArray(r.opt("staff"), ","));
        vod.put("vod_content", r.optString("evaluate"));
        vod.put("vod_play_from", "哔哩");
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        String bvid = parts.length > 0 ? parts[0] : "";
        String epId = parts.length > 1 ? parts[1] : "";
        String seasonId = parts.length > 2 ? parts[2] : "";
        String cid = parts.length > 3 ? parts[3] : "";
        String link = parts.length > 4 ? parts[4] : "";
        String badge = parts.length > 5 ? parts[5] : "";
        String danmaku = "https://api.bilibili.com/x/v1/dm/list.so?oid=" + cid;
        Map<String, String> phh = new HashMap<>();
        phh.put("Cookie", BiliCommon.cookies);
        phh.put("User-Agent", BiliCommon.UA);
        phh.put("Referer", "https://www.bilibili.com");

        if (!"会员".equals(badge)) {
            try {
                JSONObject html = BiliCommon.request(BiliCommon.playUrl(bvid, cid, epId, seasonId));
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
                    url.put(dlurl + "&bvid=" + bvid + "&ep_id=" + epId + "&season_id=" + seasonId + "&cid=" + cid + "&qn=" + qn);
                }
                JSONObject result = new JSONObject();
                result.put("header", new JSONObject(phh));
                result.put("parse", 0);
                result.put("url", url);
                result.put("danmaku", danmaku);
                result.put("format", "application/dash+xml");
                return result.toString();
            } catch (Exception ignored) {
            }
        }
        try {
            Map<String, String> jxh = new HashMap<>();
            jxh.put("token", "fdfb1079-e6e2-46a4-ac6d-f88778faf455");
            jxh.put("User-Agent", "okhttp/4.12.0");
            JSONObject jx1 = new JSONObject(com.github.catvod.net.OkHttp.string(
                    "http://zz2.mftv.top/api/index?parsesId=3&appid=10004&videoUrl=" + URLEncoder.encode(link, "UTF-8"), jxh));
            if ("200".equals(jx1.optString("code"))) {
                JSONObject result = new JSONObject();
                result.put("header", new JSONObject(phh));
                result.put("parse", 0);
                result.put("url", aesDecrypt(jx1.optString("url")));
                result.put("danmaku", danmaku);
                return result.toString();
            }
        } catch (Exception ignored) {
        }
        return new JSONObject().put("parse", 0).put("url", link).put("jx", 1).put("danmaku", danmaku).toString();
    }

    private String aesDecrypt(String data) {
        String cleaned = data.replaceAll("[^a-zA-Z0-9+/=:]|\"|\\\\|https:", "");
        String[] parts = cleaned.split(":");
        if (parts.length < 5) return "";
        String key = parts[1];
        String iv = parts[2];
        return JUtil.aesDecrypt(parts[4], key, iv);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        Map<String, String> sshh = new HashMap<>();
        sshh.put("Cookie", BiliCommon.cookies);
        sshh.put("User-Agent", BiliCommon.UA);
        sshh.put("Referer", "https://search.bilibili.com");
        JSONObject html = new JSONObject(com.github.catvod.net.OkHttp.string(
                BiliCommon.HOST + "/x/web-interface/search/type?search_type=media_ft&keyword="
                        + URLEncoder.encode(key, "UTF-8") + "&page=1", sshh));
        JSONObject html2 = new JSONObject(com.github.catvod.net.OkHttp.string(
                BiliCommon.HOST + "/x/web-interface/search/type?search_type=media_bangumi&keyword="
                        + URLEncoder.encode(key, "UTF-8") + "&page=1", sshh));
        JSONArray videos = new JSONArray();
        JSONArray r1 = html.optJSONObject("data") == null ? null : html.getJSONObject("data").optJSONArray("result");
        JSONArray r2 = html2.optJSONObject("data") == null ? null : html2.getJSONObject("data").optJSONArray("result");
        if (r1 != null) {
            for (int i = 0; i < r1.length(); i++) {
                JSONObject item = r1.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("season_id"))
                        .put("vod_name", cleanTitle(item.optString("title")))
                        .put("vod_pic", item.optString("cover"))
                        .put("vod_remarks", item.optString("index_show")));
            }
        }
        if (r2 != null) {
            for (int i = 0; i < r2.length(); i++) {
                JSONObject item = r2.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("season_id"))
                        .put("vod_name", cleanTitle(item.optString("title")))
                        .put("vod_pic", item.optString("cover"))
                        .put("vod_remarks", item.optString("index_show")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
