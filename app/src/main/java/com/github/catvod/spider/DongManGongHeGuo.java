package com.github.catvod.spider;

import android.content.Context;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DongManGongHeGuo extends Spider {

    private static final String AES_KEY = "ziISjqkXPsGUMRNGyWigxDGtJbfTdcGv";
    private static final String AES_IV = "WonrnVkxeIxDcFbv";
    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n"
            + "MIIEowIBAAKCAQEAo0aDQMwcWpsCrvI0J222vaB6zRuAZ1U9VSjhCKwqicXodMlgfwhKhjoZ7jVoRKZ6hZ8GNDoANJ7FyV23Cet++aDot4JhkbL4RHBfi8sqUnjWYf+jGI/WWgIpGiJKTvzsG+by3Wr0f4c9Ajb334Kwu7bLFpU3nKqDDIfTYrF0hsDiWzJ4CN9pOnYa4FZ+pfPy0YaFxLT5MDbsBQ2lqWNxb+OVrjVX+8hiaYnWjwmHlIDAao2irnu+YIGduwv+/PNK6DXl0NN3W0S7PRT9wyBX8j1Tus230dAlHRj0hfCbrqfAX1vVTsHxgUnLgUQyQ4PITbJ7SRUqVsY8Y/Y9T4eVNQIDAQABAoIBAFj0HKQbz8LJOvAHQsTMcEfle6HtPsqNVQnlaJyp987xxNCDug/be4afdushq7njHVNZLS8c/mmsqsMnTIaaB6aGtOLtpKyVXc8jjdqCiH9AGERx2vCRxM1q6eu0DNn1z8jvzRc2oxgrnOBtBCSAdjr+vqyCBTdUamtUQKU/WuXip1LwjQ7C2L7AoILBmIFGAjN1rXQqsWi73No1/Xvov9y25DZnIgnVTGi++Ue9FXh3qfV7/CQkZ1X6dqQ01dW4PZtpjdI2Wr6A+IC8iRaj5HZYcJkJUmNHrFKxL0Z7b2yH/mmr9YXAccIk7yFMFSyhGSoqT1RYheOi5vvSVE0WY6ECgYEA0mQb6lBI87FYG79pc+Cb/G/3idZAT9DoTT2DaCFHLG1QKHEabLj9vad3v6AjRnFRRoUv3WSuLLIfha5vcpVZ13TvSlqVVd6n7XwSIfnthDQ2ypB75VJT//DWYJL4gexGZt0s/smQpK8iB1lKGfKqkOFU+J88wPopyN1QMuYBmVkCgYEAxqurUF+abXcYvTwdAbAGH0gkaXAbeLbrwDKZ/qcW+QGUvxoin5j6ZKUUhR1UMD+bF78jEQXM07+tpxcl1jM67aYWZTsPtqKi571Y2urIRmiBDDSb5B4AxrUSYAqq6RkA6lXDUxAaDT3Tz/TC1V2dtxwJssS/bEOwr/Z127nqAz0CgYAsZDsPoYkDAjRZBnY1oPrItMdCKha/wJCDW6tSWVMvKJF1NwggUJgZYDCAGkXXIynG+2syB4BIpfzItBmHz8N5Fo823Q4NZEGCdl9NE/Ltpia5bur1Y/2dTy+siNYuc7AXHCvWRqliViGT818TQoSCtUi8fLzQ6vfODgRR+P31YQKBgCDg/EMa71W6Zg+7SRmkZf77U3tXoFREAZQXS8EHKhgfmNxfmOdMy/OoFlNJXUt221X8vfLtQM6yZCzI+ewPImt+Fyq9sYYKOGedwHzKakasuN6qPjpsdLht8xKN8WcOSkZ91wuCGK2kU8+QtEXXbmiFbV12ji9+rFkSssKgbAgJAoGBAI1uglzCpiaLDUpMjqnk83xWu6rujFEWdyLHmzS9QNtQfbXNq4zCdPp8tofBtmz+7g30RwFYSJCHUp+kp0rwiNjj4JOMf9x4UtGMel8+CghY4uZP05GWjqRM51iy21+njFSSFxbs6PZTMcrNS5lQH0l0gxXmBa6m6bFFGRBMbR9s\n"
            + "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq+BSQiNSojdRQf5Ie9VC+jmlCkHbE93ei0Tl2AtaBSOxovTR3o8KCQtJF4FBwpC3k6UYJAdIq6nXA+zfJv0ptF9Ow6TQUjGytLUX5S0NNyOADGV07eIuBtA6j+l6vZ+T1iikeEkSjZkrhmpm1yh/PTA8VaDSN1EOS3NWZWk56LKofvET12n88mJgBpWwyqD6iImzwLdwWHbWtk7xSI2+zENffzP6LJk5PApYQtIXaR1nCJ/TCXgbqWRFjXpT9kiaID4cvqMT7WnBxX1zSlj0e0PYMOEWxt6fioo/ksnWoyAK8hpbgHgDuPe6mqEvLPR8tAPMhDP46+yEOLNwexzr9wIDAQAB\n"
            + "-----END PUBLIC KEY-----";
    private static final Random RANDOM = new Random();

    private String host = "http://bljhm.xn--vhqr42drhf5k7b.com";

    @Override
    public void init(Context context, String extend) throws Exception {
        String res = new JSONObject(OkHttp.string("http://175.178.11.16:7862/app/config/host", gethh())).optString("data");
        String decrypted = JUtil.aesDecrypt(res, AES_KEY, AES_IV);
        try {
            String cc = new JSONObject(new String(Base64.decode(decrypted, Base64.DEFAULT), "UTF-8")).optString("host");
            if (!cc.isEmpty()) host = cc;
        } catch (Exception ignored) {
        }
    }

    private JSONObject rsaDecrypt(String data) throws Exception {
        String[] datas = data.split("\\.");
        String key = JUtil.rsaDecrypt(datas[0], PRIVATE_KEY);
        String iv = new StringBuilder(key).reverse().toString();
        return new JSONObject(JUtil.aesDecrypt(datas[1], key, iv));
    }

    private String de(String data) {
        return JUtil.aesDecrypt(data, "J5jQnzGVRfCe4CUk", "UY9kxQEtk8Dn08Kr");
    }

    private String en(String data) {
        return JUtil.aesEncrypt(data, "UY9kxQEtk8Dn08Kr", "J5jQnzGVRfCe4CUk");
    }

    private Map<String, String> gethh() {
        long t = System.currentTimeMillis();
        String data = Base64.encodeToString(("3.0.0.3-" + t + "-Android-1.0.0.7-6bd5d038ab0f4ab8b7de630bbff75e7b").getBytes(), Base64.NO_WRAP);
        String au = JUtil.aesEncrypt(data, AES_KEY, AES_IV);
        Map<String, String> h = new HashMap<>();
        h.put("user-agent", "Dart/3.6 (dart:io)");
        h.put("x-version", "2024-09-24");
        h.put("appid", "4150439554430627");
        h.put("ts", String.valueOf(t));
        h.put("authentication", au);
        return h;
    }

    private String getbody() {
        String key = getrand(16);
        String iv = new StringBuilder(key).reverse().toString();
        String res = "{\"checkAD\":{\"SplashAD\":{\"show\":0,\"load\":0},\"InteractionAD\":{\"show\":0,\"load\":0},\"BannerAD\":{\"show\":0,\"load\":0},\"FullScreenVideoAD\":{\"show\":0,\"load\":0},\"RewardVideoAD\":{\"show\":0,\"load\":0}}}";
        return JUtil.rsaEncrypt(res, PUBLIC_KEY) + "." + JUtil.aesEncrypt(res, key, iv);
    }

    private String getrand(int length) {
        if (length == 4) {
            return String.format("%04d", RANDOM.nextInt(10000));
        }
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String getTimestampStr() {
        long target = System.currentTimeMillis() + 8 * 60 * 60 * 1000 + 10 * 60 * 1000;
        java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(target);
        return String.format("%04d%02d%02d%02d%02d", cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH), cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE));
    }

    private String match(String data, String regex) {
        Matcher m = Pattern.compile(regex, Pattern.DOTALL).matcher(data);
        return m.find() ? m.group(1) : "";
    }

    private String getM3u8(String url, String data) {
        if (!url.endsWith(".m3u8")) return url;
        Matcher m = Pattern.compile("(https?://[^/]+)(/.+)").matcher(url);
        if (!m.find()) return url;
        String domain = m.group(1);
        String path = m.group(2);
        String md5key = match(data, "M3U8_AUTH\\s*=\\s*\\{[\\s\\S]*?KEY\\s*=\\s*\"([^\"]+)\"");
        String signStr = md5key + getTimestampStr() + path;
        String md5hash = JUtil.md5(signStr).toLowerCase();
        return replaceDomain(domain + "/" + getTimestampStr() + "/" + md5hash + path, data);
    }

    private String replaceDomain(String url, String data) {
        Pattern ruleReg = Pattern.compile("\\{\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*\\}");
        Matcher m = ruleReg.matcher(data == null ? "" : data);
        while (m.find()) {
            String aa = m.group(1).replace("%.", ".");
            String bb = m.group(2);
            url = url.replace(aa, bb);
        }
        if (url.contains("anixx.r2")) {
            return "https://sns-music.xhscdn.com/104002e031m0qe7o84s0m6saf3o";
        }
        return url;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = rsaDecrypt(OkHttp.string(host + "/app/channel?top-level=true", gethh()));
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.getJSONObject(i);
            if (!"猜你想看".equals(item.optString("name"))) {
                classes.put(new JSONObject().put("type_id", item.optString("id")).put("type_name", item.optString("name")));
            }
            JSONArray classV = new JSONArray();
            for (String v : item.optString("types", "").split(",")) {
                if (!v.isEmpty()) classV.put(new JSONObject().put("n", v).put("v", v));
            }
            JSONArray areaV = new JSONArray();
            for (String v : item.optString("areas", "").split(",")) {
                if (!v.isEmpty()) areaV.put(new JSONObject().put("n", v).put("v", v));
            }
            JSONArray yearV = new JSONArray();
            for (String v : item.optString("years", "").split(",")) {
                if (!v.isEmpty()) yearV.put(new JSONObject().put("n", v).put("v", v));
            }
            JSONArray sorts = new JSONArray();
            sorts.put(new JSONObject().put("n", "最新").put("v", "addtime"));
            sorts.put(new JSONObject().put("n", "最热").put("v", "hits"));
            sorts.put(new JSONObject().put("n", "评分").put("v", "gold"));
            JSONArray filters = new JSONArray();
            filters.put(new JSONObject().put("key", "class").put("name", "剧情").put("value", classV));
            filters.put(new JSONObject().put("key", "area").put("name", "地区").put("value", areaV));
            filters.put(new JSONObject().put("key", "year").put("name", "年份").put("value", yearV));
            filters.put(new JSONObject().put("key", "sort").put("name", "排序").put("value", sorts));
            filterObj.put(item.optString("id"), filters);
        }
        return new JSONObject().put("class", classes).put("filters", filterObj).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cls = extend != null && extend.containsKey("class") ? extend.get("class") : "";
        String area = extend != null && extend.containsKey("area") ? extend.get("area") : "";
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "";
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "addtime";
        String url = host + "/app/video/list?channel=" + tid + "&type=" + cls + "&area=" + area + "&year=" + year
                + "&sort=" + sort + "&limit=30&page=" + pg;
        JSONObject html = rsaDecrypt(OkHttp.string(url, gethh()));
        JSONArray videos = new JSONArray();
        JSONArray items = html.getJSONObject("data").optJSONArray("items");
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", item.optString("pic"))
                        .put("vod_remarks", item.optString("continu"))
                        .put("vod_year", item.optString("year")));
            }
        }
        return new JSONObject().put("page", Integer.parseInt(pg)).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String id = ids.get(0);
        JSONObject html = rsaDecrypt(OkHttp.string(host + "/app/video/detail?id=" + id, gethh())).getJSONObject("data");
        JSONArray parts = html.getJSONArray("parts");
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < parts.length(); i++) {
            JSONObject play = parts.getJSONObject(i);
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            String p = play.optString("play");
            playFrom.append(p);
            JSONArray part = play.getJSONArray("part");
            for (int j = 0; j < part.length(); j++) {
                if (j > 0) playUrl.append("#");
                playUrl.append(part.optString(j)).append("$").append(id).append("@@").append(p).append("@@").append(part.optString(j));
            }
        }
        String pf = playFrom.toString().replace("cn", "国语").replace("en", "英语").replace("newup-jp", "日语");
        JSONObject vod = new JSONObject();
        vod.put("vod_id", id);
        vod.put("vod_name", html.optString("name"));
        vod.put("vod_pic", html.optString("pic"));
        vod.put("type_name", html.optString("type"));
        vod.put("vod_year", html.optString("year"));
        vod.put("vod_area", html.optString("area"));
        vod.put("vod_remarks", html.optString("continu"));
        vod.put("vod_actor", html.optString("actor"));
        vod.put("vod_director", html.optString("director"));
        vod.put("vod_content", html.optString("content").replace("&nbsp;", ""));
        vod.put("vod_play_from", pf.contains("$$$") ? pf : "动漫共和国APP");
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] ids = id.split("@@");
        String body = getbody();
        JSONObject html = rsaDecrypt(OkHttp.post(host + "/app/video/play?id=" + ids[0] + "&play=" + ids[1] + "&part=" + ids[2], body, gethh()).getBody());
        Object dataObj = html.get("data");
        JSONObject first;
        if (dataObj instanceof JSONArray) {
            first = ((JSONArray) dataObj).getJSONObject(0);
        } else {
            first = ((JSONObject) dataObj).getJSONArray("0").getJSONObject(0);
        }
        String[] urlParts = first.optString("url").split("-");
        if ("new".equals(urlParts[0])) {
            String p = first.optString("parse");
            String md5rand = getrand(4);
            String aa = new StringBuilder(urlParts[1]).reverse().toString();
            String bb = JUtil.md5(md5rand + ":" + aa + ":UY9kxQEtk8Dn08Kr:J5jQnzGVRfCe4CUk").substring(0, 2);
            String urlmd5 = en(md5rand + "-" + aa.substring(0, 16) + bb + aa.substring(16));
            long t = System.currentTimeMillis() / 1000 + 600;
            String signrand = getrand(16);
            String authrand = getrand(20);
            String proofrand = getrand(20);
            String checkrand = getrand(20);
            String apiAuthKey = match(p, "API_AUTH_CONFIG[\\s\\S]*?KEY = \"(.*?)\"");
            String authKey = match(p, "API_HEADER_AUTH[\\s\\S]*?KEY = \"(.*?)\"");
            String extraKey = match(p, "API_HEADER_AUTH[\\s\\S]*?EXTRA_KEY = \"(.*?)\"");
            String checkKey = match(p, "API_HEADER_AUTH[\\s\\S]*?CHECK_KEY = \"(.*?)\"");
            String endpoint = match(p, "CONSTANTS[\\s\\S]*?API_ENDPOINT = \"(.*?)\"");
            String probe = match(p, "API_HEADER_AUTH[\\s\\S]*?PROBE_KEY = \"(.*?)\"");
            String path = "/";
            String sign = "sign=" + t + "-" + signrand + "-0-" + JUtil.md5(path + "-" + t + "-" + signrand + "-0-" + apiAuthKey);
            String auth = "v1:" + t + ":" + authrand + ":" + JUtil.md5(path + "|" + urlmd5 + "|" + sign + "|" + t + "|" + authrand + "|" + authKey);
            String proof = "v1:" + proofrand + ":" + JUtil.md5(path + "|" + urlmd5 + "|" + sign + "|" + auth + "|" + proofrand + "|" + extraKey);
            String check = "v1:" + checkrand + ":" + JUtil.md5(path + "|" + urlmd5 + "|" + sign + "|" + auth + "|" + proof + "|" + checkrand + "|" + checkKey);
            Map<String, String> ph = new HashMap<>();
            ph.put("user-agent", "Dart/3.6 (dart:io)");
            ph.put("x-goepp-client-probe", probe);
            ph.put("x-goepp-client-auth", auth);
            ph.put("x-goepp-client-proof", proof);
            ph.put("x-goepp-client-check", check);
            JSONObject res = new JSONObject(OkHttp.string(endpoint + urlmd5 + "&" + sign, ph));
            JSONArray list = new JSONArray();
            JSONArray playAddr = res.getJSONObject("data").getJSONArray("playAddr");
            for (int i = 0; i < playAddr.length(); i++) {
                JSONObject item = playAddr.getJSONObject(i);
                list.put(item.optString("desc"));
                list.put(getM3u8(de(item.optString("m3u8FileDomain")) + de(item.optString("addr")), p));
            }
            return new JSONObject().put("parse", 0).put("url", list).toString();
        }
        return new JSONObject().put("parse", 0).put("url", first.optString("url")).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = rsaDecrypt(OkHttp.string(host + "/app/video/search?key=" + URLEncoder.encode(key, "UTF-8") + "&limit=25&page=1", gethh()));
        JSONArray videos = new JSONArray();
        JSONArray items = html.getJSONObject("data").optJSONArray("items");
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", item.optString("pic"))
                        .put("vod_remarks", item.optString("continu"))
                        .put("vod_year", item.optString("year")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
