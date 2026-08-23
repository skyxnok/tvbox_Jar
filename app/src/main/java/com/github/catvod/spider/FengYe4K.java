package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FengYe4K extends Spider {

    private String host = "https://www.cd-zj.com";
    private JSONObject config;

    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);
        if (extend != null && !extend.isEmpty()) {
            host = new JSONObject(extend).optString("host", host);
        }
        String js = request(host + "/static/js/playerconfig.js");
        Matcher m = Pattern.compile("player_list=(.*?),MacP").matcher(js);
        if (m.find()) {
            config = new JSONObject(m.group(1));
        }
    }

    private String request(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 9; SHARK PRS-A0 Build/PQ3B.190801.12191711) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Mobile Safari/537.36");
        headers.put("Referer", host);
        return JUtil.get(url, headers);
    }

    private String getlist(String html, String startFlag, String closeTag) {
        int start = html.indexOf(startFlag);
        if (start == -1) return null;
        Matcher openMatcher = Pattern.compile("^<[a-zA-Z0-9]+").matcher(startFlag);
        if (!openMatcher.find()) return null;
        String openTag = openMatcher.group();
        int count = 1;
        int pos = start + startFlag.length();
        while (count > 0) {
            int nextOpen = html.indexOf(openTag, pos);
            int nextClose = html.indexOf(closeTag, pos);
            if (nextClose == -1) return null;
            if (nextOpen != -1 && nextOpen < nextClose) {
                count++;
                pos = nextOpen + openTag.length();
            } else {
                count--;
                pos = nextClose + closeTag.length();
                if (count == 0) return html.substring(start, pos);
            }
        }
        return null;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] types = {{"/label/qq", "腾讯精选"}, {"/label/bli", "哔哩精选"}, {"/label/youku", "优酷精选"},
                {"2", "剧集"}, {"1", "电影"}, {"4", "动漫"}, {"3", "综艺"}, {"5", "短剧"}};
        for (String[] tp : types) {
            JSONObject c = new JSONObject();
            c.put("type_id", tp[0]);
            c.put("type_name", tp[1]);
            classes.put(c);
        }
        JSONObject filterObj = new JSONObject();
        for (String[] tp : types) {
            if (tp[0].startsWith("/")) continue;
            JSONObject sort = new JSONObject();
            sort.put("key", "sort");
            sort.put("name", "sort");
            JSONArray value = new JSONArray();
            String[][] sorts = {{"人气", "hits"}, {"评分", "score"}};
            for (String[] s : sorts) {
                JSONObject o = new JSONObject();
                o.put("n", s[0]);
                o.put("v", s[1]);
                value.put(o);
            }
            sort.put("value", value);
            filterObj.put(tp[0], new JSONArray().put(sort));
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("filters", filterObj);
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "time";
        String url = tid.contains("/") ? host + tid + "/page/" + pg + ".html" : host + "/cupfox-list/" + tid + "--" + sort + "------" + pg + "---.html";
        String html = request(url);
        String res = getlist(html, "<div class=\"box-width wow", "</div>");
        JSONArray videos = new JSONArray();
        if (res != null) {
            Pattern regex = Pattern.compile("<img.*?data-src=\"(.*?)\"[\\s\\S]*?<i.*?>(.*?)<[\\s\\S]*?<a.*?href=\"(.*?)\" title=\"(.*?)\">");
            Matcher m = regex.matcher(res);
            while (m.find()) {
                JSONObject v = new JSONObject();
                v.put("vod_id", m.group(3));
                v.put("vod_name", m.group(4));
                v.put("vod_pic", m.group(1).replace("&amp;", "&"));
                v.put("vod_remarks", m.group(2));
                videos.put(v);
            }
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
        String html = request(host + ids.get(0));
        String res = getlist(html, "<div class=\"anthology wow fadeInUp animated\"", "</div>");
        JSONArray playFrom = new JSONArray();
        if (res != null) {
            Matcher fromMatcher = Pattern.compile("</i>(.*?)<").matcher(res);
            while (fromMatcher.find()) {
                playFrom.put(fromMatcher.group(1).trim().replace("&nbsp;", ""));
            }
        }
        JSONArray playUrl = new JSONArray();
        if (res != null) {
            Matcher lineMatcher = Pattern.compile("<ul.+?>[\\s\\S]*?</ul>").matcher(res);
            while (lineMatcher.find()) {
                JSONArray ep = new JSONArray();
                Matcher epMatcher = Pattern.compile("href=\"(.*?)\".*?>(.*?)<").matcher(lineMatcher.group());
                while (epMatcher.find()) {
                    ep.put(epMatcher.group(2).replaceAll("\\b0+(?=[1-9])", "") + "$" + epMatcher.group(1));
                }
                StringBuilder sb = new StringBuilder();
                for (int i = ep.length() - 1; i >= 0; i--) {
                    if (sb.length() > 0) sb.append("#");
                    sb.append(ep.optString(i));
                }
                playUrl.put(sb.toString());
            }
        }
        String content = "";
        Matcher cm = Pattern.compile("<div id=\"height_limit\".*?>([\\s\\S]*?)<").matcher(html);
        if (cm.find()) content = cm.group(1).replace("&amp;", "&").replace("&nbsp;", "&");
        JSONObject vod = new JSONObject();
        String vTitle = "";
        Matcher tm = Pattern.compile("<h3 class=\"slide-info-title[^\"]*\">(.*?)</h3>").matcher(html);
        if (tm.find()) vTitle = tm.group(1).trim();
        String vPic = "";
        Matcher pm = Pattern.compile("data-src=\"(.*?)\"").matcher(html);
        if (pm.find()) vPic = pm.group(1).replace("&amp;", "&");
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", vTitle);
        vod.put("vod_pic", vPic);
        vod.put("type_name", "");
        vod.put("vod_year", "");
        vod.put("vod_area", "");
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_remarks", "");
        vod.put("vod_content", content);
        vod.put("vod_play_from", join(playFrom, "$$$"));
        vod.put("vod_play_url", join(playUrl, "$$$"));
        JSONObject result = new JSONObject();
        result.put("list", new JSONArray().put(vod));
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String html = request(host + id);
        Matcher m = Pattern.compile("var player_aaaa=(.*?)<").matcher(html);
        String url;
        if (m.find()) {
            JSONObject res = new JSONObject(m.group(1));
            url = res.optString("url");
            if (!url.contains("m3u8")) {
                String from = res.optString("from");
                String jxhost = config != null && config.has(from) ? config.getJSONObject(from).optString("parse") : "";
                if (!jxhost.isEmpty()) {
                    String phost = jxhost.split("/")[2];
                    String res2 = request(jxhost + url);
                    String token = "";
                    Matcher tm = Pattern.compile("data-te=\"(.*?)\"").matcher(res2);
                    if (tm.find()) token = tm.group(1);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    String playurl = JUtil.post("https://" + phost + "/player/mplayer.php", "url=" + url + "&token=" + token, headers);
                    url = new JSONObject(playurl).optString("url");
                }
            }
        } else {
            url = id;
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
            v.put("vod_id", "/detail/" + item.optString("id") + ".html");
            v.put("vod_name", item.optString("name"));
            v.put("vod_pic", item.optString("pic").replace("&amp;", "&"));
            videos.put(v);
        }
        JSONObject result = new JSONObject();
        result.put("limit", videos.length());
        result.put("list", videos);
        return result.toString();
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
