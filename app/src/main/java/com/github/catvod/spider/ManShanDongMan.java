package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManShanDongMan extends Spider {

    private static final String HOST = "https://app.manshan.fun";

    private String de(String data) {
        return JUtil.aesDecryptEcb(data.replace("\"", ""), "zhuhongleipeipei");
    }

    private JSONObject request(String data, String path) throws Exception {
        long t = System.currentTimeMillis() / 1000;
        String sign = JUtil.md5Base64Url(t + path + "zhl's river app");
        String urls = data.contains("?") ? "&sign=" + sign + "&time=" + t : "?sign=" + sign + "&time=" + t;
        Map<String, String> h = new HashMap<>();
        h.put("user-agent", "Dart/3.11 (dart:io)");
        return new JSONObject(de(OkHttp.string(data + urls, h)));
    }

    private String pic(String p) {
        return p + "@Referer=https://douban.com@User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(HOST + "/app/tab/getList", "/app/tab/getList");
        JSONArray classes = new JSONArray();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            classes.put(new JSONObject().put("type_id", data.getJSONObject(i).optString("id")).put("type_name", data.getJSONObject(i).optString("title")));
        }
        classes.put(new JSONObject().put("type_id", "片库").put("type_name", "片库"));
        JSONObject html2 = request(HOST + "/app/category/getList", "/app/category/getList");
        JSONArray cdata = html2.getJSONArray("data");
        JSONArray filters = new JSONArray();
        int[] idx = {1, 2, 3, 0};
        String[] keys = {"type", "class", "year", "sort"};
        String[] names = {"类型", "剧情", "年份", "排序"};
        for (int k = 0; k < idx.length; k++) {
            JSONArray values = cdata.getJSONObject(idx[k]).getJSONArray("values");
            JSONArray value = new JSONArray();
            for (int i = 0; i < values.length(); i++) {
                value.put(new JSONObject().put("n", values.getString(i)).put("v", values.getString(i)));
            }
            filters.put(new JSONObject().put("key", keys[k]).put("name", names[k]).put("value", value));
        }
        JSONObject filterObj = new JSONObject().put("片库", filters);
        return new JSONObject().put("class", classes).put("filters", filterObj).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        JSONArray videos = new JSONArray();
        if ("片库".equals(tid)) {
            String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "最热";
            String category = extend != null && extend.containsKey("type") ? extend.get("type") : "全部";
            String genres = extend != null && extend.containsKey("class") ? extend.get("class") : "全部";
            String year = extend != null && extend.containsKey("year") ? extend.get("year") : "全部";
            JSONObject html = request(HOST + "/app/category/getVideoList?sort=" + sort + "&category=" + category
                    + "&genres=" + genres + "&year=" + year + "&pageNo=" + pg + "&pageSize=21", "/app/category/getVideoList");
            JSONArray data = html.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("title"))
                        .put("vod_pic", pic(item.optString("pic")))
                        .put("vod_remarks", item.optString("remarks"))
                        .put("vod_year", item.optString("year")));
            }
            return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                    .put("total", 99999).put("list", videos).toString();
        }
        JSONObject html = request(HOST + "/app/video/getList?tabId=" + tid, "/app/video/getList");
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONArray vlist = data.getJSONObject(i).getJSONArray("videoList");
            for (int j = 0; j < vlist.length(); j++) {
                JSONObject item = vlist.getJSONObject(j);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("title"))
                        .put("vod_pic", pic(item.optString("pic")))
                        .put("vod_remarks", item.optString("remarks"))
                        .put("vod_year", item.optString("year")));
            }
        }
        return new JSONObject().put("page", 1).put("pagecount", 1).put("limit", videos.length())
                .put("total", 1).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = request(HOST + "/app/video/getDetail?videoId=" + ids.get(0), "/app/video/getDetail").getJSONObject("data");
        String title = html.optString("title");
        StringBuilder playUrl = new StringBuilder();
        JSONArray episodeList = html.getJSONArray("episodeList");
        boolean movie = "movie".equals(html.optString("douBanType"));
        for (int i = 0; i < episodeList.length(); i++) {
            if (i > 0) playUrl.append("#");
            JSONObject ep = episodeList.getJSONObject(i);
            String seq = movie ? "0001" : String.format("%04d", i + 1);
            playUrl.append(ep.optString("title")).append("$").append(title).append("@@").append(ep.optString("id")).append("@@").append(seq);
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", html.optString("genres"));
        vod.put("vod_year", html.optString("year"));
        vod.put("vod_area", html.optString("area"));
        vod.put("vod_remarks", html.optString("remarks"));
        vod.put("vod_actor", html.optString("actor"));
        vod.put("vod_director", html.optString("director"));
        vod.put("vod_content", html.optString("description"));
        vod.put("vod_play_from", "漫闪");
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] ids = id.split("@@");
        JSONObject html = request(HOST + "/app/episode/jx?videoTitle=" + URLEncoder.encode(ids[0], "UTF-8") + "&episodeId=" + ids[1], "/app/episode/jx");
        JSONObject data = html.getJSONObject("data");
        JSONArray resolutionList = data.getJSONArray("resolutionList");
        JSONArray url = new JSONArray();
        for (int i = 0; i < resolutionList.length(); i++) {
            JSONObject item = resolutionList.getJSONObject(i);
            url.put(item.optString("name").replace("super", "超清").replace("high", "高清").replace("low", "标清"));
            url.put(item.optString("url"));
        }
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", url);
        String hh = data.optString("playHeader");
        if (!hh.isEmpty()) result.put("header", hh);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = request(HOST + "/app/video/search?keyWord=" + URLEncoder.encode(key, "UTF-8"), "/app/video/search");
        JSONArray videos = new JSONArray();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            videos.put(new JSONObject()
                    .put("vod_id", item.optString("id"))
                    .put("vod_name", item.optString("title"))
                    .put("vod_pic", pic(item.optString("pic")))
                    .put("vod_remarks", item.optString("remarks"))
                    .put("vod_year", item.optString("year")));
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
