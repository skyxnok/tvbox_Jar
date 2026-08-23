package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JinPaiYingYuan extends Spider {

    private static final String HOST = "https://www.hskjjglo.com";
    private static final String SIGN_KEY = "cb808529bae6b6be45ecfab29a4889bc";
    private static final String FILTER_JSON = "{\"1\":[{\"key\":\"type\",\"name\":\"类型\",\"value\":[{\"n\":\"喜剧\",\"v\":\"22\"},{\"n\":\"动作\",\"v\":\"23\"},{\"n\":\"科幻\",\"v\":\"30\"},{\"n\":\"爱情\",\"v\":\"26\"},{\"n\":\"悬疑\",\"v\":\"27\"},{\"n\":\"奇幻\",\"v\":\"87\"},{\"n\":\"剧情\",\"v\":\"37\"},{\"n\":\"恐怖\",\"v\":\"36\"},{\"n\":\"犯罪\",\"v\":\"35\"},{\"n\":\"动画\",\"v\":\"33\"},{\"n\":\"惊悚\",\"v\":\"34\"},{\"n\":\"战争\",\"v\":\"25\"},{\"n\":\"冒险\",\"v\":\"31\"},{\"n\":\"灾难\",\"v\":\"81\"},{\"n\":\"伦理\",\"v\":\"83\"},{\"n\":\"其他\",\"v\":\"43\"}]},{\"key\":\"class\",\"name\":\"剧情\",\"value\":[{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"枪战\",\"v\":\"枪战\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"微电影\",\"v\":\"微电影\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009~2000\",\"v\":\"2009~2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"更新\",\"v\":\"2\"},{\"n\":\"人气\",\"v\":\"3\"},{\"n\":\"评分\",\"v\":\"4\"}]}],\"2\":[{\"key\":\"type\",\"name\":\"类型\",\"value\":[{\"n\":\"国产剧\",\"v\":\"14\"},{\"n\":\"欧美剧\",\"v\":\"15\"},{\"n\":\"港台剧\",\"v\":\"16\"},{\"n\":\"日韩剧\",\"v\":\"62\"},{\"n\":\"其他剧\",\"v\":\"68\"}]},{\"key\":\"class\",\"name\":\"剧情\",\"value\":[{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"短片\",\"v\":\"短片\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"普通话\",\"v\":\"普通话\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"泰语\",\"v\":\"泰语\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"更新\",\"v\":\"2\"},{\"n\":\"人气\",\"v\":\"3\"},{\"n\":\"评分\",\"v\":\"4\"}]}],\"3\":[{\"key\":\"type\",\"name\":\"类型\",\"value\":[{\"n\":\"国产综艺\",\"v\":\"69\"},{\"n\":\"港台综艺\",\"v\":\"70\"},{\"n\":\"日韩综艺\",\"v\":\"72\"},{\"n\":\"欧美综艺\",\"v\":\"73\"}]},{\"key\":\"class\",\"name\":\"剧情\",\"value\":[{\"n\":\"真人秀\",\"v\":\"真人秀\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"脱口秀\",\"v\":\"脱口秀\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"更新\",\"v\":\"2\"},{\"n\":\"人气\",\"v\":\"3\"},{\"n\":\"评分\",\"v\":\"4\"}]}],\"4\":[{\"key\":\"type\",\"name\":\"类型\",\"value\":[{\"n\":\"国产动漫\",\"v\":\"75\"},{\"n\":\"日韩动漫\",\"v\":\"76\"},{\"n\":\"欧美动漫\",\"v\":\"77\"}]},{\"key\":\"class\",\"name\":\"剧情\",\"value\":[{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"热血\",\"v\":\"热血\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"少女\",\"v\":\"少女\"},{\"n\":\"动画\",\"v\":\"动画\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"更新\",\"v\":\"2\"},{\"n\":\"人气\",\"v\":\"3\"},{\"n\":\"评分\",\"v\":\"4\"}]}]}";

    private JSONObject request(String url) throws Exception {
        String t = String.valueOf(System.currentTimeMillis());
        String query = url.contains("?") ? url.split("\\?")[1] : "";
        String signStr = query.isEmpty()
                ? "key=" + SIGN_KEY + "&t=" + t
                : query + "&key=" + SIGN_KEY + "&t=" + t;
        String sign = JUtil.sha1(JUtil.md5(signStr));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36");
        headers.put("Referer", HOST);
        headers.put("t", t);
        headers.put("sign", sign);
        return new JSONObject(JUtil.get(url, headers));
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] types = {{"1", "电影"}, {"2", "电视"}, {"3", "综艺"}, {"4", "动漫"}, {"88", "短剧"}};
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
        JSONObject html = request(HOST + "/api/mw-movie/anonymous/home/hotSearch?");
        JSONArray videos = new JSONArray();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vodId"));
            v.put("vod_name", item.optString("vodName"));
            v.put("vod_pic", item.optString("vodPic"));
            v.put("vod_remarks", item.optString("vodRemarks"));
            v.put("vod_year", "");
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String url = HOST + "/api/mw-movie/anonymous/video/list?area=" + opt(extend, "area")
                + "&lang=" + opt(extend, "lang") + "&pageNum=" + pg + "&pageSize=30&sort="
                + opt(extend, "sort", "3") + "&sortBy=1&type=" + opt(extend, "type") + "&type1=" + tid
                + "&v_class=" + opt(extend, "class") + "&year=" + opt(extend, "year");
        JSONObject html = request(url);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vodId"));
            v.put("vod_name", item.optString("vodName"));
            v.put("vod_pic", item.optString("vodPic"));
            v.put("vod_remarks", item.optString("vodRemarks"));
            v.put("vod_year", "");
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("page", pg);
        result.put("pagecount", 99999);
        result.put("limit", videos.length());
        result.put("total", 99999);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = request(HOST + "/api/mw-movie/anonymous/video/detail?id=" + ids.get(0)).getJSONObject("data");
        StringBuilder playUrl = new StringBuilder();
        JSONArray episodeList = html.getJSONArray("episodeList");
        for (int i = 0; i < episodeList.length(); i++) {
            JSONObject ep = episodeList.getJSONObject(i);
            if (i > 0) playUrl.append("#");
            playUrl.append(ep.optString("name")).append("$").append(ids.get(0)).append("@@").append(ep.optString("nid"));
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", html.optString("typeName"));
        vod.put("vod_year", html.optString("vodYear"));
        vod.put("vod_area", html.optString("vodArea"));
        vod.put("vod_actor", html.optString("vodActor"));
        vod.put("vod_director", html.optString("vodDirector"));
        vod.put("vod_remarks", html.optString("vodRemarks"));
        vod.put("vod_content", html.optString("vodContent"));
        vod.put("vod_play_from", html.optString("vodVersion"));
        vod.put("vod_play_url", playUrl.toString());
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        JSONObject html = request(HOST + "/api/mw-movie/anonymous/v2/video/episode/url?clientType=1&id=" + parts[0] + "&nid=" + parts[1]);
        JSONArray list = html.getJSONObject("data").getJSONArray("list");
        JSONArray url = new JSONArray();
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            url.put(item.optString("resolutionName"));
            url.put(item.optString("url"));
        }
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", url);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String url = HOST + "/api/mw-movie/anonymous/video/searchByWord?keyword=" + URLEncoder.encode(key, "UTF-8")
                + "&pageNum=1&pageSize=12&sourceCode=1";
        JSONObject html = request(url);
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").getJSONObject("result").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", item.optString("vodId"));
            v.put("vod_name", item.optString("vodName"));
            v.put("vod_pic", item.optString("vodPic"));
            v.put("vod_remarks", item.optString("vodRemarks"));
            v.put("vod_year", item.optString("vodYear"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    private String opt(HashMap<String, String> extend, String key) {
        return opt(extend, key, "");
    }

    private String opt(HashMap<String, String> extend, String key, String def) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : def;
    }
}
