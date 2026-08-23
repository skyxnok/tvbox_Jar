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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SanHaoDongMan extends Spider {

    private static final String FILTER_JSON = "{\"1\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"v\":\"全部年代\",\"n\":\"全部\"},{\"v\":\"2026\",\"n\":\"2026\"},{\"v\":\"2025\",\"n\":\"2025\"},{\"v\":\"2024\",\"n\":\"2024\"},{\"v\":\"2023\",\"n\":\"2023\"},{\"v\":\"2022\",\"n\":\"2022\"},{\"v\":\"2021\",\"n\":\"2021\"},{\"v\":\"2020\",\"n\":\"2020\"},{\"v\":\"2019\",\"n\":\"2019\"},{\"v\":\"2018\",\"n\":\"2018\"},{\"v\":\"2017\",\"n\":\"2017\"},{\"v\":\"2016\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015\"},{\"v\":\"2014\",\"n\":\"2014\"},{\"v\":\"2013\",\"n\":\"2013\"},{\"v\":\"2012\",\"n\":\"2012\"},{\"v\":\"2011\",\"n\":\"2011\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"最新\",\"n\":\"最新\"},{\"v\":\"最热\",\"n\":\"最热\"},{\"v\":\"最赞\",\"n\":\"最赞\"}]}],\"2\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"v\":\"全部年代\",\"n\":\"全部\"},{\"v\":\"2026\",\"n\":\"2026\"},{\"v\":\"2025\",\"n\":\"2025\"},{\"v\":\"2024\",\"n\":\"2024\"},{\"v\":\"2023\",\"n\":\"2023\"},{\"v\":\"2022\",\"n\":\"2022\"},{\"v\":\"2021\",\"n\":\"2021\"},{\"v\":\"2020\",\"n\":\"2020\"},{\"v\":\"2019\",\"n\":\"2019\"},{\"v\":\"2018\",\"n\":\"2018\"},{\"v\":\"2017\",\"n\":\"2017\"},{\"v\":\"2016\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015\"},{\"v\":\"2014\",\"n\":\"2014\"},{\"v\":\"2013\",\"n\":\"2013\"},{\"v\":\"2012\",\"n\":\"2012\"},{\"v\":\"2011\",\"n\":\"2011\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"最新\",\"n\":\"最新\"},{\"v\":\"最热\",\"n\":\"最热\"},{\"v\":\"最赞\",\"n\":\"最赞\"}]}],\"3\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"v\":\"全部年代\",\"n\":\"全部\"},{\"v\":\"2026\",\"n\":\"2026\"},{\"v\":\"2025\",\"n\":\"2025\"},{\"v\":\"2024\",\"n\":\"2024\"},{\"v\":\"2023\",\"n\":\"2023\"},{\"v\":\"2022\",\"n\":\"2022\"},{\"v\":\"2021\",\"n\":\"2021\"},{\"v\":\"2020\",\"n\":\"2020\"},{\"v\":\"2019\",\"n\":\"2019\"},{\"v\":\"2018\",\"n\":\"2018\"},{\"v\":\"2017\",\"n\":\"2017\"},{\"v\":\"2016\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015\"},{\"v\":\"2014\",\"n\":\"2014\"},{\"v\":\"2013\",\"n\":\"2013\"},{\"v\":\"2012\",\"n\":\"2012\"},{\"v\":\"2011\",\"n\":\"2011\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"最新\",\"n\":\"最新\"},{\"v\":\"最热\",\"n\":\"最热\"},{\"v\":\"最赞\",\"n\":\"最赞\"}]}],\"4\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"v\":\"全部年代\",\"n\":\"全部\"},{\"v\":\"2026\",\"n\":\"2026\"},{\"v\":\"2025\",\"n\":\"2025\"},{\"v\":\"2024\",\"n\":\"2024\"},{\"v\":\"2023\",\"n\":\"2023\"},{\"v\":\"2022\",\"n\":\"2022\"},{\"v\":\"2021\",\"n\":\"2021\"},{\"v\":\"2020\",\"n\":\"2020\"},{\"v\":\"2019\",\"n\":\"2019\"},{\"v\":\"2018\",\"n\":\"2018\"},{\"v\":\"2017\",\"n\":\"2017\"},{\"v\":\"2016\",\"n\":\"2016\"},{\"v\":\"2015\",\"n\":\"2015\"},{\"v\":\"2014\",\"n\":\"2014\"},{\"v\":\"2013\",\"n\":\"2013\"},{\"v\":\"2012\",\"n\":\"2012\"},{\"v\":\"2011\",\"n\":\"2011\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"最新\",\"n\":\"最新\"},{\"v\":\"最热\",\"n\":\"最热\"},{\"v\":\"最赞\",\"n\":\"最赞\"}]}]}";

    private String host;
    private String playkey;
    private JSONArray playinfo = new JSONArray();

    @Override
    public void init(Context context, String extend) throws Exception {
        String txt = OkHttp.string("https://shdm-1319164733.cos.ap-nanjing.myqcloud.com/api.txt");
        host = new JSONArray(AES1(txt)).getString(0);
        JSONObject info = new JSONObject(AES2(OkHttp.post(host + "/shark/api.php?action=configs", "username=&token=", hh()).getBody()));
        playkey = info.getJSONObject("config").optString("hulue").split("&")[0];
        playinfo = info.getJSONArray("playerinfos");
    }

    private String AES1(String data) {
        return JUtil.aesDecryptEcb(data.replaceAll("<.*>|\\n", "").trim(), "rectangleadsadxa");
    }

    private String AES2(String data) {
        return JUtil.aesDecryptEcb(data.replaceAll("<.*>|\\n", "").trim(), "aassddwwxxllsx1x");
    }

    private String AES3(String data) {
        return JUtil.aesDecryptEcb(data.replaceAll("<.*>|\\n", "").trim(), playkey);
    }

    private Map<String, String> hh() {
        Map<String, String> h = new HashMap<>();
        h.put("user-agent", "Dalvik/1.4.0 (Linux; U; Android 9; Xiaomi Build/23116PN5BC)");
        h.put("version", "1.4.0");
        h.put("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        return h;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] cs = {{"1", "日漫"}, {"2", "国漫"}, {"3", "剧场"}, {"4", "番剧"}};
        for (String[] c : cs) {
            classes.put(new JSONObject().put("type_id", c[0]).put("type_name", c[1]));
        }
        JSONObject html = new JSONObject(AES2(OkHttp.string(host + "/api.php/v1.rank/RankData?page=1&type_id=2", hh())));
        JSONArray videos = new JSONArray();
        JSONArray vlist = html.getJSONObject("data").optJSONArray("videos");
        if (vlist != null) {
            for (int i = 0; i < vlist.length(); i++) {
                JSONObject item = vlist.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_year", item.optString("vod_year")));
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
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "全部年代";
        String rank = extend != null && extend.containsKey("sort") ? extend.get("sort") : "最新";
        JSONObject body = new JSONObject()
                .put("area", "全部地区").put("lang", "全部语言").put("type", "全部类型")
                .put("year", year).put("type_id", tid).put("rank", rank);
        Map<String, String> ch = new HashMap<>();
        ch.put("user-agent", "Dalvik/1.4.0 (Linux; U; Android 9; Xiaomi Build/23116PN5BC)");
        ch.put("version", "1.4.0");
        ch.put("content-type", "application/json; charset=utf-8");
        JSONObject html = new JSONObject(AES2(OkHttp.post(host + "/api.php/v1.classify/content?page=" + pg, body.toString(), ch).getBody()));
        JSONArray videos = new JSONArray();
        JSONArray vlist = html.getJSONObject("data").optJSONArray("video_list");
        if (vlist != null) {
            for (int i = 0; i < vlist.length(); i++) {
                JSONObject item = vlist.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("vod_score") + "分"));
            }
        }
        return new JSONObject().put("page", Integer.parseInt(pg)).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = new JSONObject(AES2(OkHttp.string(host + "/api.php/v1.player/details?vod_id=" + ids.get(0), hh())));
        JSONObject res = html.getJSONObject("data").getJSONObject("detail");
        JSONArray playUrlList = res.getJSONArray("play_url_list");
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < playUrlList.length(); i++) {
            JSONObject play = playUrlList.getJSONObject(i);
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            String from = play.optString("from");
            playFrom.append(from);
            JSONArray urls = play.getJSONArray("urls");
            for (int j = 0; j < urls.length(); j++) {
                JSONObject item = urls.getJSONObject(j);
                if (j > 0) playUrl.append("#");
                playUrl.append(item.optString("name")).append("$").append(from).append("@@").append(item.optString("url"));
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", ids.get(0));
        vod.put("vod_name", res.optString("vod_name"));
        vod.put("vod_pic", res.optString("vod_pic"));
        vod.put("type_name", res.optString("vod_class"));
        vod.put("vod_year", res.optString("vod_year"));
        vod.put("vod_remarks", res.optString("vod_remarks"));
        vod.put("vod_actor", "");
        vod.put("vod_director", "");
        vod.put("vod_content", res.optString("vod_content"));
        String pf = playFrom.toString();
        String[][] replaces = {{"dyttm3u8", "天堂云[稳定]"}, {"lmm", "路漫漫[全面]"}, {"dmbs", "动漫巴士[全面]"},
                {"CYDD1", "樱花云[原画]"}, {"dxt", "猫盘[电信]"}, {"ndx", "猫盘[简中]"}, {"dbz", "猫盘[极速]"},
                {"tkk", "AA-04[备用]"}, {"7se", "七色番[简中]"}, {"aafun2", "BB-02[New线]"}, {"aafun1", "BB-03[New线]"},
                {"iyf", "爱一帆"}, {"aowu", "嗷呜[New线]"}};
        for (String[] r : replaces) {
            pf = pf.replace(r[0], r[1]);
        }
        vod.put("vod_play_from", pf);
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] ids = id.split("@@");
        String name = ids[0];
        String vid = ids.length > 1 ? ids[1] : "";
        JSONObject plays = null;
        for (int i = 0; i < playinfo.length(); i++) {
            JSONObject p = playinfo.getJSONObject(i);
            if (name.equals(p.optString("playername"))) {
                plays = p;
                break;
            }
        }
        Map<String, String> phh = new HashMap<>();
        phh.put("Icy-MetaData", "1");
        phh.put("allowCrossProtocolRedirects", "true");
        phh.put("Accept-Encoding", "identity");
        phh.put("User-Agent", "Dalvik/1.4.0 (Linux; U; Android 9; Xiaomi Build/23116PN5BC)");
        phh.put("Connection", "Keep-Alive");
        if (plays != null) {
            Matcher m = Pattern.compile("referer:(.*?)(;|$)").matcher(plays.optString("playerua"));
            if (m.find()) phh.put("Referer", m.group(1).trim());
            String jiekou = plays.optString("playerjiekou");
            if (!jiekou.isEmpty()) {
                String body = "parse=" + URLEncoder.encode(AES3(jiekou), "UTF-8") + "&url=" + URLEncoder.encode(vid, "UTF-8") + "&matching=";
                JSONObject res = new JSONObject(AES2(OkHttp.post(host + "/shark/api.php?action=parsevod", body, hh()).getBody()));
                return new JSONObject().put("parse", 0).put("url", res.optString("url")).put("header", new JSONObject(phh)).toString();
            }
        }
        return new JSONObject().put("parse", 0).put("url", vid).put("header", new JSONObject(phh)).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = new JSONObject(AES2(OkHttp.string(
                host + "/api.php/v1.search/data?wd=" + URLEncoder.encode(key, "UTF-8") + "&type_id=0&page=1", hh())));
        JSONArray videos = new JSONArray();
        JSONArray list = html.getJSONObject("data").optJSONArray("search_data");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("vod_remarks")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
