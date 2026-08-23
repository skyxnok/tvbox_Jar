package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XiFanDongMan extends Spider {

    private String host = "https://anime.xifanacg.com";

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        if (extend != null && !extend.isEmpty()) {
            host = new JSONObject(extend).optString("host", host);
        }
    }

    private String request(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36 Edg/100.0.1185.29");
        headers.put("Referer", host);
        return JUtil.get(url, headers);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        String html = request(host + "/label/rank.html");
        JSONArray classes = new JSONArray();
        String[][] types = {{"1", "连载新番"}, {"2", "完结旧番"}, {"3", "剧场版"}, {"21", "美漫"}};
        for (String[] tp : types) {
            JSONObject c = new JSONObject();
            c.put("type_id", tp[0]);
            c.put("type_name", tp[1]);
            classes.put(c);
        }
        JSONObject filterObj = new JSONObject();
        for (String[] tp : types) {
            JSONObject year = new JSONObject();
            year.put("key", "year");
            year.put("name", "年份");
            JSONArray yearValues = new JSONArray();
            JSONObject all = new JSONObject();
            all.put("n", "全部");
            all.put("v", "");
            yearValues.put(all);
            for (int y = 2026; y >= 2005; y--) {
                JSONObject o = new JSONObject();
                o.put("n", String.valueOf(y));
                o.put("v", String.valueOf(y));
                yearValues.put(o);
            }
            year.put("value", yearValues);
            JSONObject sort = new JSONObject();
            sort.put("key", "sort");
            sort.put("name", "排序");
            JSONArray sortValues = new JSONArray();
            String[][] sorts = {{"最新", "time"}, {"最热", "hits"}, {"评分", "score"}};
            for (String[] s : sorts) {
                JSONObject o = new JSONObject();
                o.put("n", s[0]);
                o.put("v", s[1]);
                sortValues.put(o);
            }
            sort.put("value", sortValues);
            filterObj.put(tp[0], new JSONArray().put(year).put(sort));
        }
        JSONArray videos = new JSONArray();
        Document doc = Jsoup.parse(html);
        Element box = doc.selectFirst("div.vod-rank-box");
        if (box != null) {
            Element d1 = box.selectFirst("> div");
            if (d1 != null) {
                Elements items = d1.select("div:nth-child(2) > a");
                for (Element a : items) {
                    JSONObject v = new JSONObject();
                    v.put("vod_id", a.attr("href"));
                    v.put("vod_name", a.attr("title"));
                    Element img = a.selectFirst("img");
                    v.put("vod_pic", img != null ? img.attr("data-src") : "");
                    Element remark = a.selectFirst("div.vod-rank-title-box.flex-auto > div:nth-child(2)");
                    v.put("vod_remarks", remark != null ? remark.text() : "");
                    videos.put(v);
                }
            }
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterObj);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        long t = System.currentTimeMillis() / 1000;
        String key = JUtil.md5("DS" + t + "DCC147D11943AF75");
        String body = "type=" + tid + "&class=&area=&year=" + opt(extend, "year")
                + "&lang=&version=&state=&letter=&time=&level=0&weekday=&by=" + opt(extend, "sort", "time")
                + "&page=" + pg + "&time=" + t + "&key=" + key;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        JSONObject html = new JSONObject(JUtil.post(host + "/index.php/api/vod", body, headers));
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", "/bangumi/" + item.optString("vod_id") + ".html");
            v.put("vod_name", item.optString("vod_name"));
            v.put("vod_pic", item.optString("vod_pic"));
            v.put("vod_remarks", item.optString("vod_remarks"));
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
        String html = request(host + ids.get(0));
        Document doc = Jsoup.parse(html);
        Element info = doc.selectFirst("div.detail-info.rel.flex-auto.wow.lightSpeedIn");
        JSONObject vod = new JSONObject();
        vod.put("type_name", "");
        vod.put("vod_year", "");
        vod.put("vod_area", "");
        vod.put("vod_actor", joinTexts(info, "div:nth-child(3) a"));
        vod.put("vod_director", joinTexts(info, "div:nth-child(2) a"));
        String remarks = "";
        if (info != null) {
            Element span = info.selectFirst("div:nth-child(1) span:nth-child(1)");
            if (span != null) remarks = span.text();
        }
        vod.put("vod_remarks", remarks);
        Element meta = doc.selectFirst("meta[name=description]");
        vod.put("vod_content", meta != null ? meta.attr("content") : "");
        JSONArray playFrom = new JSONArray();
        Elements roadNames = doc.select("div.swiper-wrapper a");
        for (Element a : roadNames) {
            playFrom.put(a.text());
        }
        JSONArray playUrl = new JSONArray();
        Elements roads = doc.select("div.anthology.wow.fadeInUp.animated ul");
        for (Element ul : roads) {
            StringBuilder sb = new StringBuilder();
            Elements eps = ul.select("a");
            for (Element a : eps) {
                if (sb.length() > 0) sb.append("#");
                sb.append(a.text()).append("$").append(a.attr("href"));
            }
            playUrl.put(sb.toString());
        }
        vod.put("vod_play_from", join(playFrom, "$$$"));
        vod.put("vod_play_url", join(playUrl, "$$$"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String html = request(host + id);
        String url = id;
        Matcher m = Pattern.compile("var player_aaaa=(.*?)<").matcher(html);
        if (m.find()) {
            url = new JSONObject(m.group(1)).optString("url");
        }
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", url);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String html = request(host + "/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(key, "UTF-8") + "&limit=500");
        JSONArray videos = new JSONArray();
        JSONArray list = new JSONObject(html).getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            JSONObject v = new JSONObject();
            v.put("vod_id", "/bangumi/" + item.optString("id") + ".html");
            v.put("vod_name", item.optString("name"));
            v.put("vod_pic", item.optString("pic"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    private String joinTexts(Element root, String selector) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        Elements els = root.select(selector);
        for (Element e : els) {
            String text = e.text().trim();
            if (text.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" / ");
            sb.append(text);
        }
        return sb.toString();
    }

    private String opt(HashMap<String, String> extend, String key) {
        return opt(extend, key, "");
    }

    private String opt(HashMap<String, String> extend, String key, String def) {
        return extend != null && extend.containsKey(key) ? extend.get(key) : def;
    }

    private String join(JSONArray arr, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(arr.optString(i));
        }
        return sb.toString();
    }
}
