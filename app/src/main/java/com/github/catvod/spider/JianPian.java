package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JianPian extends Spider {

    private static final String FILTER_JSON = "{\"1\":[{\"key\":\"cateId\",\"name\":\"分类\",\"value\":[{\"v\":\"1\",\"n\":\"剧情\"},{\"v\":\"2\",\"n\":\"爱情\"},{\"v\":\"3\",\"n\":\"动画\"},{\"v\":\"4\",\"n\":\"喜剧\"},{\"v\":\"5\",\"n\":\"战争\"},{\"v\":\"6\",\"n\":\"歌舞\"},{\"v\":\"7\",\"n\":\"古装\"},{\"v\":\"8\",\"n\":\"奇幻\"},{\"v\":\"9\",\"n\":\"冒险\"},{\"v\":\"10\",\"n\":\"动作\"},{\"v\":\"11\",\"n\":\"科幻\"},{\"v\":\"12\",\"n\":\"悬疑\"},{\"v\":\"13\",\"n\":\"犯罪\"},{\"v\":\"14\",\"n\":\"家庭\"},{\"v\":\"15\",\"n\":\"传记\"},{\"v\":\"16\",\"n\":\"运动\"},{\"v\":\"18\",\"n\":\"惊悚\"},{\"v\":\"20\",\"n\":\"短片\"},{\"v\":\"21\",\"n\":\"历史\"},{\"v\":\"22\",\"n\":\"音乐\"},{\"v\":\"23\",\"n\":\"西部\"},{\"v\":\"24\",\"n\":\"武侠\"},{\"v\":\"25\",\"n\":\"恐怖\"}]},{\"key\":\"area\",\"name\":\"地區\",\"value\":[{\"v\":\"1\",\"n\":\"国产\"},{\"v\":\"3\",\"n\":\"中国香港\"},{\"v\":\"6\",\"n\":\"中国台湾\"},{\"v\":\"5\",\"n\":\"美国\"},{\"v\":\"18\",\"n\":\"韩国\"},{\"v\":\"2\",\"n\":\"日本\"}]},{\"key\":\"year\",\"name\":\"年代\",\"value\":[{\"v\":\"162\",\"n\":\"2026\"},{\"v\":\"107\",\"n\":\"2025\"},{\"v\":\"119\",\"n\":\"2024\"},{\"v\":\"153\",\"n\":\"2023\"},{\"v\":\"101\",\"n\":\"2022\"},{\"v\":\"118\",\"n\":\"2021\"},{\"v\":\"16\",\"n\":\"2020\"},{\"v\":\"7\",\"n\":\"2019\"},{\"v\":\"2\",\"n\":\"2018\"},{\"v\":\"3\",\"n\":\"2017\"},{\"v\":\"22\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015以前\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"update\",\"n\":\"最新\"},{\"v\":\"hot\",\"n\":\"最热\"},{\"v\":\"rating\",\"n\":\"评分\"}]}],\"2\":[{\"key\":\"cateId\",\"name\":\"分类\",\"value\":[{\"v\":\"1\",\"n\":\"剧情\"},{\"v\":\"2\",\"n\":\"爱情\"},{\"v\":\"3\",\"n\":\"动画\"},{\"v\":\"4\",\"n\":\"喜剧\"},{\"v\":\"5\",\"n\":\"战争\"},{\"v\":\"6\",\"n\":\"歌舞\"},{\"v\":\"7\",\"n\":\"古装\"},{\"v\":\"8\",\"n\":\"奇幻\"},{\"v\":\"9\",\"n\":\"冒险\"},{\"v\":\"10\",\"n\":\"动作\"},{\"v\":\"11\",\"n\":\"科幻\"},{\"v\":\"12\",\"n\":\"悬疑\"},{\"v\":\"13\",\"n\":\"犯罪\"},{\"v\":\"14\",\"n\":\"家庭\"},{\"v\":\"15\",\"n\":\"传记\"},{\"v\":\"16\",\"n\":\"运动\"},{\"v\":\"18\",\"n\":\"惊悚\"},{\"v\":\"20\",\"n\":\"短片\"},{\"v\":\"21\",\"n\":\"历史\"},{\"v\":\"22\",\"n\":\"音乐\"},{\"v\":\"23\",\"n\":\"西部\"},{\"v\":\"24\",\"n\":\"武侠\"},{\"v\":\"25\",\"n\":\"恐怖\"}]},{\"key\":\"area\",\"name\":\"地區\",\"value\":[{\"v\":\"1\",\"n\":\"国产\"},{\"v\":\"3\",\"n\":\"中国香港\"},{\"v\":\"6\",\"n\":\"中国台湾\"},{\"v\":\"5\",\"n\":\"美国\"},{\"v\":\"18\",\"n\":\"韩国\"},{\"v\":\"2\",\"n\":\"日本\"}]},{\"key\":\"year\",\"name\":\"年代\",\"value\":[{\"v\":\"162\",\"n\":\"2026\"},{\"v\":\"107\",\"n\":\"2025\"},{\"v\":\"119\",\"n\":\"2024\"},{\"v\":\"153\",\"n\":\"2023\"},{\"v\":\"101\",\"n\":\"2022\"},{\"v\":\"118\",\"n\":\"2021\"},{\"v\":\"16\",\"n\":\"2020\"},{\"v\":\"7\",\"n\":\"2019\"},{\"v\":\"2\",\"n\":\"2018\"},{\"v\":\"3\",\"n\":\"2017\"},{\"v\":\"22\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015以前\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"update\",\"n\":\"最新\"},{\"v\":\"hot\",\"n\":\"最热\"},{\"v\":\"rating\",\"n\":\"评分\"}]}],\"3\":[{\"key\":\"cateId\",\"name\":\"分类\",\"value\":[{\"v\":\"1\",\"n\":\"剧情\"},{\"v\":\"2\",\"n\":\"爱情\"},{\"v\":\"3\",\"n\":\"动画\"},{\"v\":\"4\",\"n\":\"喜剧\"},{\"v\":\"5\",\"n\":\"战争\"},{\"v\":\"6\",\"n\":\"歌舞\"},{\"v\":\"7\",\"n\":\"古装\"},{\"v\":\"8\",\"n\":\"奇幻\"},{\"v\":\"9\",\"n\":\"冒险\"},{\"v\":\"10\",\"n\":\"动作\"},{\"v\":\"11\",\"n\":\"科幻\"},{\"v\":\"12\",\"n\":\"悬疑\"},{\"v\":\"13\",\"n\":\"犯罪\"},{\"v\":\"14\",\"n\":\"家庭\"},{\"v\":\"15\",\"n\":\"传记\"},{\"v\":\"16\",\"n\":\"运动\"},{\"v\":\"18\",\"n\":\"惊悚\"},{\"v\":\"20\",\"n\":\"短片\"},{\"v\":\"21\",\"n\":\"历史\"},{\"v\":\"22\",\"n\":\"音乐\"},{\"v\":\"23\",\"n\":\"西部\"},{\"v\":\"24\",\"n\":\"武侠\"},{\"v\":\"25\",\"n\":\"恐怖\"}]},{\"key\":\"area\",\"name\":\"地區\",\"value\":[{\"v\":\"1\",\"n\":\"国产\"},{\"v\":\"3\",\"n\":\"中国香港\"},{\"v\":\"6\",\"n\":\"中国台湾\"},{\"v\":\"5\",\"n\":\"美国\"},{\"v\":\"18\",\"n\":\"韩国\"},{\"v\":\"2\",\"n\":\"日本\"}]},{\"key\":\"year\",\"name\":\"年代\",\"value\":[{\"v\":\"162\",\"n\":\"2026\"},{\"v\":\"107\",\"n\":\"2025\"},{\"v\":\"119\",\"n\":\"2024\"},{\"v\":\"153\",\"n\":\"2023\"},{\"v\":\"101\",\"n\":\"2022\"},{\"v\":\"118\",\"n\":\"2021\"},{\"v\":\"16\",\"n\":\"2020\"},{\"v\":\"7\",\"n\":\"2019\"},{\"v\":\"2\",\"n\":\"2018\"},{\"v\":\"3\",\"n\":\"2017\"},{\"v\":\"22\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015以前\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"update\",\"n\":\"最新\"},{\"v\":\"hot\",\"n\":\"最热\"},{\"v\":\"rating\",\"n\":\"评分\"}]}],\"4\":[{\"key\":\"cateId\",\"name\":\"分类\",\"value\":[{\"v\":\"1\",\"n\":\"剧情\"},{\"v\":\"2\",\"n\":\"爱情\"},{\"v\":\"3\",\"n\":\"动画\"},{\"v\":\"4\",\"n\":\"喜剧\"},{\"v\":\"5\",\"n\":\"战争\"},{\"v\":\"6\",\"n\":\"歌舞\"},{\"v\":\"7\",\"n\":\"古装\"},{\"v\":\"8\",\"n\":\"奇幻\"},{\"v\":\"9\",\"n\":\"冒险\"},{\"v\":\"10\",\"n\":\"动作\"},{\"v\":\"11\",\"n\":\"科幻\"},{\"v\":\"12\",\"n\":\"悬疑\"},{\"v\":\"13\",\"n\":\"犯罪\"},{\"v\":\"14\",\"n\":\"家庭\"},{\"v\":\"15\",\"n\":\"传记\"},{\"v\":\"16\",\"n\":\"运动\"},{\"v\":\"18\",\"n\":\"惊悚\"},{\"v\":\"20\",\"n\":\"短片\"},{\"v\":\"21\",\"n\":\"历史\"},{\"v\":\"22\",\"n\":\"音乐\"},{\"v\":\"23\",\"n\":\"西部\"},{\"v\":\"24\",\"n\":\"武侠\"},{\"v\":\"25\",\"n\":\"恐怖\"}]},{\"key\":\"area\",\"name\":\"地區\",\"value\":[{\"v\":\"1\",\"n\":\"国产\"},{\"v\":\"3\",\"n\":\"中国香港\"},{\"v\":\"6\",\"n\":\"中国台湾\"},{\"v\":\"5\",\"n\":\"美国\"},{\"v\":\"18\",\"n\":\"韩国\"},{\"v\":\"2\",\"n\":\"日本\"}]},{\"key\":\"year\",\"name\":\"年代\",\"value\":[{\"v\":\"162\",\"n\":\"2026\"},{\"v\":\"107\",\"n\":\"2025\"},{\"v\":\"119\",\"n\":\"2024\"},{\"v\":\"153\",\"n\":\"2023\"},{\"v\":\"101\",\"n\":\"2022\"},{\"v\":\"118\",\"n\":\"2021\"},{\"v\":\"16\",\"n\":\"2020\"},{\"v\":\"7\",\"n\":\"2019\"},{\"v\":\"2\",\"n\":\"2018\"},{\"v\":\"3\",\"n\":\"2017\"},{\"v\":\"22\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015以前\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"update\",\"n\":\"最新\"},{\"v\":\"hot\",\"n\":\"最热\"},{\"v\":\"rating\",\"n\":\"评分\"}]}]}";
    private String host = "https://japi.zxfmj.com";
    private String imghost = "";

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        if (extend != null && !extend.isEmpty()) {
            host = new JSONObject(extend).optString("host", host);
        }
        JSONObject res = new JSONObject(request(host + "/api/v2/settings/packageDomainConfig"));
        String imgDomain = res.getJSONObject("data").optString("imgDomain");
        imghost = "https://" + (imgDomain.contains(",") ? imgDomain.split(",")[0] : imgDomain);
    }

    private String request(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 9; V2196A Build/PQ3A.190705.08211809; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36;webank/h5face;webank/1.0;netType:NETWORK_WIFI;appVersion:416;packageName:com.jp3.xg3");
        headers.put("Referer", host);
        return JUtil.get(url, headers);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] types = {{"1", "电影"}, {"2", "电视剧"}, {"3", "动漫"}, {"4", "综艺"}};
        for (String[] tp : types) {
            JSONObject c = new JSONObject();
            c.put("type_id", tp[0]);
            c.put("type_name", tp[1]);
            classes.put(c);
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", new JSONObject(FILTER_JSON));
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        JSONObject html = new JSONObject(request(host + "/api/slide/list?pos_id=88"));
        JSONArray videos = new JSONArray();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("jump_id"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", imghost + item.optString("thumbnail"));
            v.put("vod_remarks", "");
            JSONObject style = new JSONObject();
            style.put("type", "rect");
            style.put("ratio", 1.485);
            v.put("style", style);
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String url = host + "/api/crumb/list?fcate_pid=" + tid + "&category_id=&area=" + opt(extend, "area")
                + "&year=" + opt(extend, "year") + "&type=" + opt(extend, "cateId")
                + "&sort=" + opt(extend, "sort") + "&page=" + pg;
        JSONObject html = new JSONObject(request(url));
        JSONArray videos = new JSONArray();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("id"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", imghost + item.optString("path"));
            v.put("vod_remarks", item.optString("mask"));
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
        JSONObject html = new JSONObject(request(host + "/api/video/detailv2?id=" + ids.get(0))).getJSONObject("data");
        JSONArray arr = new JSONArray();
        if (html.has("ftp_list")) {
            JSONArray ftp = html.getJSONArray("ftp_list");
            if (ftp.length() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ftp.length(); i++) {
                    JSONObject item = ftp.getJSONObject(i);
                    if (i > 0) sb.append("#");
                    sb.append(item.optString("title")).append("$").append(item.optString("url"));
                }
                JSONObject line = new JSONObject();
                line.put("from", "荐片");
                line.put("url", sb.toString());
                arr.put(line);
            }
        }
        if (html.has("vip_source_list_source")) {
            JSONArray sources = html.getJSONArray("vip_source_list_source");
            for (int i = 0; i < sources.length(); i++) {
                JSONObject src = sources.getJSONObject(i);
                StringBuilder sb = new StringBuilder();
                JSONArray list = src.getJSONArray("source_list");
                for (int j = 0; j < list.length(); j++) {
                    JSONObject item = list.getJSONObject(j);
                    if (j > 0) sb.append("#");
                    sb.append(item.optString("source_name")).append("$").append(item.optString("url"));
                }
                JSONObject line = new JSONObject();
                line.put("from", src.optString("name"));
                line.put("url", sb.toString());
                arr.put(line);
            }
        }
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject item = arr.getJSONObject(i);
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            playFrom.append(item.optString("from"));
            playUrl.append(item.optString("url"));
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", html.optString("title"));
        vod.put("vod_pic", imghost + html.optString("path", html.optString("thumbnail")));
        String typeName = "";
        if (html.has("category")) {
            JSONArray cats = html.getJSONArray("category");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cats.length(); i++) {
                if (i > 0) sb.append(" / ");
                sb.append(cats.getJSONObject(i).optString("title"));
            }
            typeName = sb.toString();
        }
        vod.put("type_name", typeName);
        vod.put("vod_year", html.optString("year"));
        vod.put("vod_area", html.optString("area"));
        vod.put("vod_remarks", html.optString("mask"));
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_content", html.optString("description"));
        vod.put("vod_play_from", playFrom.toString());
        vod.put("vod_play_url", playUrl.toString());
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", id.contains(".m3u8") ? id : "tvbox-xg:" + id);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String url = host + "/api/v2/search/videoV2?key=" + URLEncoder.encode(key, "UTF-8") + "&category_id=88&page=1&pageSize=20";
        JSONObject html = new JSONObject(request(url));
        JSONArray videos = new JSONArray();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("id"));
            v.put("vod_name", item.optString("title"));
            v.put("vod_pic", imghost + item.optString("thumbnail"));
            v.put("vod_remarks", item.optString("mask"));
            v.put("vod_year", "");
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    private String opt(HashMap<String, String> extend, String key) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : "";
    }
}
