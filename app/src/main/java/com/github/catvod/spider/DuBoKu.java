package com.github.catvod.spider;

import android.util.Base64;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DuBoKu extends Spider {

    private static final String HOST = "https://api.dbokutv.com";
    private static final String FILTER_JSON = "{\"1\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"剧情\",\"v\":\"\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"警匪\",\"v\":\"警匪\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动画\",\"v\":\"动画\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"古装\",\"v\":\"古装\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"澳大利亚\",\"v\":\"澳大利亚\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"语言\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"法语\",\"v\":\"法语\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"\"},{\"n\":\"人气\",\"v\":\"人气\"},{\"n\":\"评分\",\"v\":\"评分\"}]}],\"2\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"剧情\",\"v\":\"\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"都市\",\"v\":\"都市\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"青春\",\"v\":\"青春\"},{\"n\":\"偶像\",\"v\":\"偶像\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"乡村\",\"v\":\"乡村\"},{\"n\":\"年代\",\"v\":\"年代\"},{\"n\":\"警匪\",\"v\":\"警匪\"},{\"n\":\"谍战\",\"v\":\"谍战\"},{\"n\":\"历险\",\"v\":\"历险\"},{\"n\":\"罪案\",\"v\":\"罪案\"},{\"n\":\"宫廷\",\"v\":\"宫廷\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"穿越\",\"v\":\"穿越\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"泰国\",\"v\":\"泰国\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"语言\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"泰语\",\"v\":\"泰语\"},{\"n\":\"日语\",\"v\":\"日语\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"\"},{\"n\":\"人气\",\"v\":\"人气\"},{\"n\":\"评分\",\"v\":\"评分\"}]}],\"3\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"剧情\",\"v\":\"\"},{\"n\":\"真人秀\",\"v\":\"真人秀\"},{\"n\":\"选秀\",\"v\":\"选秀\"},{\"n\":\"竞演\",\"v\":\"竞演\"},{\"n\":\"情感\",\"v\":\"情感\"},{\"n\":\"旅游\",\"v\":\"旅游\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"美食\",\"v\":\"美食\"},{\"n\":\"纪实\",\"v\":\"纪实\"},{\"n\":\"生活\",\"v\":\"生活\"},{\"n\":\"游戏互动\",\"v\":\"游戏互动\"},{\"n\":\"竞技\",\"v\":\"竞技\"},{\"n\":\"搞笑\",\"v\":\"搞笑\"},{\"n\":\"脱口秀\",\"v\":\"脱口秀\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"韩国\",\"v\":\"韩国\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"语言\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"韩语\",\"v\":\"韩语\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"\"},{\"n\":\"人气\",\"v\":\"人气\"},{\"n\":\"评分\",\"v\":\"评分\"}]}],\"4\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"剧情\",\"v\":\"\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"热血\",\"v\":\"热血\"},{\"n\":\"推理\",\"v\":\"推理\"},{\"n\":\"爆笑\",\"v\":\"爆笑\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"校园\",\"v\":\"校园\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"机战\",\"v\":\"机战\"},{\"n\":\"竞技\",\"v\":\"竞技\"},{\"n\":\"少女\",\"v\":\"少女\"},{\"n\":\"格斗\",\"v\":\"格斗\"},{\"n\":\"恋爱\",\"v\":\"恋爱\"},{\"n\":\"魔幻\",\"v\":\"魔幻\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"美国\",\"v\":\"美国\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"语言\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"英语\",\"v\":\"英语\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"\"},{\"n\":\"人气\",\"v\":\"人气\"},{\"n\":\"评分\",\"v\":\"评分\"}]}],\"20\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"剧情\",\"v\":\"\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"都市\",\"v\":\"都市\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"青春\",\"v\":\"青春\"},{\"n\":\"偶像\",\"v\":\"偶像\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"乡村\",\"v\":\"乡村\"},{\"n\":\"年代\",\"v\":\"年代\"},{\"n\":\"警匪\",\"v\":\"警匪\"},{\"n\":\"谍战\",\"v\":\"谍战\"},{\"n\":\"历险\",\"v\":\"历险\"},{\"n\":\"罪案\",\"v\":\"罪案\"},{\"n\":\"宫廷\",\"v\":\"宫廷\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"穿越\",\"v\":\"穿越\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"泰国\",\"v\":\"泰国\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"语言\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"泰语\",\"v\":\"泰语\"},{\"n\":\"日语\",\"v\":\"日语\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"\"},{\"n\":\"人气\",\"v\":\"人气\"},{\"n\":\"评分\",\"v\":\"评分\"}]}]}";
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private Map<String, String> hh() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        headers.put("Referer", "https://www.duboku.tv/");
        return headers;
    }

    private String randomStr(int len) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String getsign(String rawUrl) {
        long timestamp = System.currentTimeMillis() / 1000;
        int randomNum = new Random().nextInt(800000001);
        long valueA = randomNum + 100000000L;
        long valueB = 900000000L - randomNum;
        String a = String.valueOf(valueA) + valueB;
        String b = String.valueOf(timestamp);
        StringBuilder interleaved = new StringBuilder();
        int minLen = Math.min(a.length(), b.length());
        for (int i = 0; i < minLen; i++) {
            interleaved.append(a.charAt(i)).append(b.charAt(i));
        }
        interleaved.append(a.substring(minLen)).append(b.substring(minLen));
        String ssid = Base64.encodeToString(interleaved.toString().getBytes(), Base64.NO_WRAP).replace("=", ".");
        return rawUrl + "?sign=" + randomStr(60) + "&token=" + randomStr(38) + "&ssid=" + ssid;
    }

    private String decode(String data) {
        String str = data.replaceAll("^['\"]+|['\"]+$", "").replace(".", "=");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i += 10) {
            int end = Math.min(i + 10, str.length());
            sb.append(new StringBuilder(str.substring(i, end)).reverse());
        }
        return new String(Base64.decode(sb.toString(), Base64.DEFAULT));
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] types = {{"1", "电影"}, {"2", "剧集"}, {"3", "综艺"}, {"4", "动漫"}, {"20", "港剧"}};
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
        JSONArray html = new JSONArray(JUtil.get(getsign(HOST + "/home"), hh()));
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject(0).getJSONArray("VodList");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", decode(item.optString("DId")));
            v.put("vod_name", item.optString("Name"));
            v.put("vod_pic", decode(item.optString("TnId")));
            v.put("vod_remarks", "评分:" + item.optString("Rating") + " | " + item.optString("Tag"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String url = HOST + "/vodshow/" + tid + "-" + opt(extend, "area") + "-" + opt(extend, "sort")
                + "-" + opt(extend, "class") + "-" + opt(extend, "lang") + "----" + pg + "---" + opt(extend, "year");
        JSONObject html = new JSONObject(JUtil.get(getsign(url), hh()));
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONArray("VodList");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", decode(item.optString("DId")));
            v.put("vod_name", item.optString("Name"));
            v.put("vod_pic", decode(item.optString("TnId")));
            v.put("vod_remarks", "评分:" + item.optString("Rating") + " | " + item.optString("Tag"));
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
        JSONObject html = new JSONObject(JUtil.get(getsign(HOST + ids.get(0)), hh()));
        StringBuilder playUrl = new StringBuilder();
        JSONArray playlist = html.getJSONArray("Playlist");
        for (int i = 0; i < playlist.length(); i++) {
            JSONObject item = playlist.getJSONObject(i);
            if (i > 0) playUrl.append("#");
            playUrl.append(item.optString("EpisodeName")).append("$").append(decode(item.optString("VId")));
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", html.optString("Genre"));
        vod.put("vod_year", html.optString("ReleaseYear"));
        vod.put("vod_area", html.optString("Region"));
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_content", html.optString("Description"));
        vod.put("vod_play_from", "独播库");
        vod.put("vod_play_url", playUrl.toString());
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject html = new JSONObject(JUtil.get(getsign(HOST + id), hh()));
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", decode(html.optString("HId")));
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String url = getsign(HOST + "/vodsearch") + "&wd=" + URLEncoder.encode(key, "UTF-8");
        JSONArray html = new JSONArray(JUtil.get(url, hh()));
        JSONArray videos = new JSONArray();
        for (int i = 0; i < html.length(); i++) {
            JSONObject item = html.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", decode(item.optString("DId")));
            v.put("vod_name", item.optString("Name"));
            v.put("vod_pic", decode(item.optString("TnId")));
            v.put("vod_remarks", "评分:" + item.optString("Rating") + " | " + item.optString("Tag"));
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
