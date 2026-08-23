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
import java.util.Random;

public class GuaZiYingShi extends Spider {

    private static final String FILTER_JSON = "{\"1\":[{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"0\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"欧美\",\"v\":\"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"其他\",\"v\":\"其他,印度,新加坡,马来西亚\"}]},{\"key\":\"sub\",\"name\":\"类型\",\"value\":[{\"n\":\"动作片\",\"v\":5},{\"n\":\"悬疑片\",\"v\":29},{\"n\":\"喜剧片\",\"v\":6},{\"n\":\"爱情片\",\"v\":7},{\"n\":\"科幻片\",\"v\":8},{\"n\":\"恐怖片\",\"v\":9},{\"n\":\"剧情片\",\"v\":10},{\"n\":\"战争片\",\"v\":11},{\"n\":\"动画片\",\"v\":36},{\"n\":\"纪录片\",\"v\":20},{\"n\":\"灾难片\",\"v\":38},{\"n\":\"犯罪片\",\"v\":61}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"0\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"10-15年\",\"v\":\"2015,2014,2013,2012,2011,2010\"},{\"n\":\"00年代\",\"v\":\"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009\"},{\"n\":\"90年代\",\"v\":\"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999\"},{\"n\":\"80年代\",\"v\":\"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989\"},{\"n\":\"更早\",\"v\":\"2\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"综合\",\"v\":\"d_id\"},{\"n\":\"最新\",\"v\":\"d_addtime\"},{\"n\":\"高分\",\"v\":\"d_score\"}]}],\"2\":[{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"0\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"欧美\",\"v\":\"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"其他\",\"v\":\"其他,印度,新加坡,马来西亚\"}]},{\"key\":\"sub\",\"name\":\"类型\",\"value\":[{\"n\":\"国产剧\",\"v\":12},{\"n\":\"香港剧\",\"v\":13},{\"n\":\"台湾剧\",\"v\":14},{\"n\":\"欧美剧\",\"v\":15},{\"n\":\"日本剧\",\"v\":16},{\"n\":\"韩国剧\",\"v\":17},{\"n\":\"海外剧\",\"v\":18},{\"n\":\"泰国剧\",\"v\":19},{\"n\":\"新加坡\",\"v\":69}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"0\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"10-15年\",\"v\":\"2015,2014,2013,2012,2011,2010\"},{\"n\":\"00年代\",\"v\":\"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009\"},{\"n\":\"90年代\",\"v\":\"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999\"},{\"n\":\"80年代\",\"v\":\"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989\"},{\"n\":\"更早\",\"v\":\"2\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"综合\",\"v\":\"d_id\"},{\"n\":\"最新\",\"v\":\"d_addtime\"},{\"n\":\"高分\",\"v\":\"d_score\"}]}],\"4\":[{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"0\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"欧美\",\"v\":\"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"其他\",\"v\":\"其他,印度,新加坡,马来西亚\"}]},{\"key\":\"sub\",\"name\":\"类型\",\"value\":[{\"n\":\"中国动漫\",\"v\":30},{\"n\":\"日本动漫\",\"v\":31},{\"n\":\" 欧美动漫\",\"v\":33}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"0\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"10-15年\",\"v\":\"2015,2014,2013,2012,2011,2010\"},{\"n\":\"00年代\",\"v\":\"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009\"},{\"n\":\"90年代\",\"v\":\"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999\"},{\"n\":\"80年代\",\"v\":\"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989\"},{\"n\":\"更早\",\"v\":\"2\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"综合\",\"v\":\"d_id\"},{\"n\":\"最新\",\"v\":\"d_addtime\"},{\"n\":\"高分\",\"v\":\"d_score\"}]}],\"3\":[{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"0\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"欧美\",\"v\":\"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"其他\",\"v\":\"其他,印度,新加坡,马来西亚\"}]},{\"key\":\"sub\",\"name\":\"类型\",\"value\":[{\"n\":\"大陆综艺\",\"v\":22},{\"n\":\"港台综艺\",\"v\":23},{\"n\":\"日韩综艺\",\"v\":24},{\"n\":\"欧美综艺\",\"v\":25}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"年份\",\"v\":\"0\"},{\"n\":\"2026\",\"v\":\"2026\"},{\"n\":\"2025\",\"v\":\"2025\"},{\"n\":\"2024\",\"v\":\"2024\"},{\"n\":\"2023\",\"v\":\"2023\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"10-15年\",\"v\":\"2015,2014,2013,2012,2011,2010\"},{\"n\":\"00年代\",\"v\":\"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009\"},{\"n\":\"90年代\",\"v\":\"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999\"},{\"n\":\"80年代\",\"v\":\"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989\"},{\"n\":\"更早\",\"v\":\"2\"}]},{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"综合\",\"v\":\"d_id\"},{\"n\":\"最新\",\"v\":\"d_addtime\"},{\"n\":\"高分\",\"v\":\"d_score\"}]}],\"64\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"n\":\"综合\",\"v\":\"d_id\"},{\"n\":\"最新\",\"v\":\"d_addtime\"},{\"n\":\"高分\",\"v\":\"d_score\"}]}]}";
    private static final String AES_KEY = "mvXBSW7ekreItNsT";
    private static final String AES_IV = "2U3IrJL8szAKp0Fj";
    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n"
            + "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDUM5+/y8sPsWkd1/RQS64X259EUwxFXFE5HlA65MqrxnPs0JqoSRojSDy5QhwvROlaD6TwRQHKMY2OAZ6SnQeUJsChTEFIR9qUkwrs3/MVUMxjsv6JS6Oe/juclyJGTgVmDhB55EafXsD0SQYVj/QXXsxR6ewR5E2kL52yAAD4yQIDAQAB\n"
            + "-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n"
            + "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGAe6hKrWLi1zQmjTT1ozbE4QdFeJGNxubxld6GrFGximxfMsMB6BpJhpcTouAqywAFppiKetUBBbXwYsYU1wNr648XVmPmCMCy4rY8vdliFnbMUj086DU6Z+/oXBdWU3/b1G0DN3E9wULRSwcKZT3wj/cCI1vsCm3gj2R5SqkA9Y0CAwEAAQKBgAJH+4CxV0/zBVcLiBCHvSANm0l7HetybTh/j2p0Y1sTXro4ALwAaCTUeqdBjWiLSo9lNwDHFyq8zX90+gNxa7c5EqcWV9FmlVXr8VhfBzcZo1nXeNdXFT7tQ2yah/odtdcx+vRMSGJd1t/5k5bDd9wAvYdIDblMAg+wiKKZ5KcdAkEA1cCakEN4NexkF5tHPRrR6XOY/XHfkqXxEhMqmNbB9U34saTJnLWIHC8IXys6Qmzz30TtzCjuOqKRRy+FMM4TdwJBAJQZFPjsGC+RqcG5UvVMiMPhnwe/bXEehShK86yJK/g/UiKrO87h3aEu5gcJqBygTq3BBBoH2md3pr/W+hUMWBsCQQChfhTIrdDinKi6lRxrdBnn0Ohjg2cwuqK5zzU9p/N+S9x7Ck8wUI53DKm8jUJE8WAG7WLj/oCOWEh+ic6NIwTdAkEAj0X8nhx6AXsgCYRql1klbqtVmL8+95KZK7PnLWG/IfjQUy3pPGoSaZ7fdquG8bq8oyf5+dzjE/oTXcByS+6XRQJAP/5ciy1bL3NhUhsaOVy55MHXnPjdcTX0FaLi+ybXZIfIQ2P4rb19mVq1feMbCXhz+L1rG8oat5lYKfpe8k83ZA==\n"
            + "-----END PRIVATE KEY-----";
    private static final String DEFAULT_TOKEN = "bd9be2be616d26492bb71879795511cc.0e4d6c97da0ecb2281f77ca977212f4afb233a30cdd7d18652bc632e658710347be33b01ef5ee47df719ef21a0a50b9bd7a645504a9cd50167be16d5d0e763a159d6e900a2923a414d3f5616d987aa10edf68817d0a18a2beff894840d2dbb0c22ce6a3a4f1de4cebb52171fee07d1f9cf1d5590385f5f7ef6e01d1850974aa220eb5178c89e61c24411af9b9a19435e.82f5703aade6eb6dcb5f20d29b1fd75e3213ea2df683a8a5581740312b564579";
    private static final Random RANDOM = new Random();

    private String host = "https://api.36kzbh85.com";
    private String TOKEN = DEFAULT_TOKEN;
    private String KEYS = rsaKeys();
    private String deviceId = String.valueOf(864150060000000L + RANDOM.nextInt(10000));
    private String deviceKey;

    public GuaZiYingShi() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 40; i++) sb.append("0123456789ABCDEF".charAt(RANDOM.nextInt(16)));
        deviceKey = sb.toString();
    }


    private static String rsaKeys() {
        try {
            return JUtil.rsaEncrypt(new JSONObject().put("iv", AES_IV).put("key", AES_KEY).toString(), PUBLIC_KEY);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void init(Context context, String extend) throws Exception {
        if (extend != null && !extend.isEmpty()) {
            JSONObject ext = new JSONObject(extend);
            if (ext.has("host")) host = ext.optString("host");
        }
        try {
            signUp();
            refreshToken();
        } catch (Exception ignored) {
        }
    }

    private void signUp() throws Exception {
        JSONObject res = request(host + "/App/Authentication/Device/signUp", new JSONObject()
                .put("new_key", deviceKey).put("old_key", "aLFBMWpxBrIDAD1Si/KVvm41").put("phone_type", 1).put("code", ""));
        applyAuth(res);
    }

    private void refreshToken() throws Exception {
        JSONObject res = request(host + "/App/Authentication/Authenticator/refresh", new JSONObject());
        applyAuth(res);
    }

    private void applyAuth(JSONObject res) {
        if (res.has("token")) {
            TOKEN = res.optString("token");
            if (res.has("app_user_id")) deviceId = res.optString("app_user_id");
        }
    }

    private String en(String data) {
        return JUtil.aesEncryptHex(data, AES_KEY, AES_IV);
    }

    private JSONObject request(String urls, JSONObject params) throws Exception {
        long t = System.currentTimeMillis() / 1000;
        String requestKey = en(params.toString());
        Map<String, String> h = new HashMap<>();
        h.put("api-ver", "3.0.3.2");
        h.put("Ver", "3.0.3.2");
        h.put("lang", "zh_cn");
        h.put("Cache-Control", "no-cache");
        h.put("Version", "2509018");
        h.put("PackageName", "com.nfc2e6fc21.d6a64f4fd1.p004a191e220251009");
        h.put("code", "GZ0369");
        h.put("deviceId", deviceId);
        h.put("Referer", host);
        h.put("User-Agent", "okhttp/3.12.0");
        h.put("Content-Type", "application/x-www-form-urlencoded");
        String sign = JUtil.md5("token_id=,token=" + TOKEN + ",phone_type=1,request_key=" + requestKey
                + ",app_id=1,time=" + t + ",keys=" + KEYS + "*&zvdvdvddbfikkkumtmdwqppp?|4Y!s!2br").toUpperCase();
        String body = "token=" + TOKEN + "&token_id=&phone_type=1&time=" + t + "&phone_model=xiaomi-22021211rc"
                + "&keys=" + URLEncoder.encode(KEYS, "UTF-8") + "&request_key=" + requestKey
                + "&signature=" + sign + "&app_id=1&ad_version=1";
        JSONObject html = new JSONObject(OkHttp.post(urls, body, h).getBody());
        JSONObject keyInfo = new JSONObject(JUtil.rsaDecrypt(html.getJSONObject("data").optString("keys"), PRIVATE_KEY));
        String decrypted = JUtil.aesDecryptHexCbc(html.getJSONObject("data").optString("response_key"), keyInfo.optString("key"), keyInfo.optString("iv"));
        return new JSONObject(decrypted);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] cs = {{"1", "电影"}, {"2", "电视剧"}, {"4", "动漫"}, {"3", "综艺"}, {"64", "短剧"}};
        for (String[] c : cs) {
            classes.put(new JSONObject().put("type_id", c[0]).put("type_name", c[1]));
        }
        return new JSONObject().put("class", classes).put("filters", new JSONObject(FILTER_JSON)).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        long t = System.currentTimeMillis() / 1000;
        JSONObject html = request(host + "/App/IndexList/choiceList", new JSONObject().put("ns", "").put("nt", t).put("pid", "1"));
        JSONArray videos = new JSONArray();
        JSONArray list = html.optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("c_name"))
                        .put("vod_pic", item.optString("c_pic"))
                        .put("vod_remarks", item.optString("cf_name"))
                        .put("vod_year", item.optString("new_continue")));
            }
        }
        return new JSONObject().put("list", videos).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "d_id";
        String area = extend != null && extend.containsKey("area") ? extend.get("area") : "";
        String sub = extend != null && extend.containsKey("sub") ? extend.get("sub") : "";
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "";
        JSONObject html = request(host + "/App/IndexList/indexList", new JSONObject()
                .put("tid", tid).put("page", pg).put("sort", sort).put("area", area)
                .put("sub", sub).put("year", year).put("pageSize", "30"));
        JSONArray videos = new JSONArray();
        JSONArray list = html.optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("new_continue"))
                        .put("vod_year", item.optString("vod_year")));
            }
        }
        return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String id = ids.get(0);
        long t = System.currentTimeMillis() / 1000;
        JSONObject html = request(host + "/App/Resource/Vurl/show", new JSONObject().put("vurl_cloud_id", "2").put("vod_d_id", id));
        JSONObject info = request(host + "/App/IndexPlay/playInfo", new JSONObject().put("mobile_time", t).put("vod_id", id)).getJSONObject("vodInfo");
        StringBuilder playUrl = new StringBuilder();
        JSONArray list = html.optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                if (i > 0) playUrl.append("#");
                String lastParam = "";
                JSONObject play = item.optJSONObject("play");
                if (play != null) {
                    JSONArray names = play.names();
                    for (int j = 0; j < names.length(); j++) {
                        String p = play.getJSONObject(names.getString(j)).optString("param");
                        if (!p.isEmpty()) lastParam = p;
                    }
                }
                playUrl.append(item.optString("title")).append("$").append(lastParam);
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_name", info.optString("vod_name"));
        JSONArray videoTag = info.optJSONArray("videoTag");
        String typeName = "";
        if (videoTag != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < videoTag.length(); i++) {
                if (i > 0) sb.append(" / ");
                sb.append(videoTag.optString(i));
            }
            typeName = sb.toString();
        }
        vod.put("type_name", typeName);
        String year = info.optString("vod_year");
        vod.put("vod_year", year.contains("-") ? year.split("-")[0] : year);
        vod.put("vod_area", info.optString("vod_area"));
        vod.put("vod_remarks", info.optString("new_continue"));
        vod.put("vod_actor", info.optString("vod_actor"));
        vod.put("vod_director", info.optString("vod_director"));
        vod.put("vod_content", info.optString("vod_use_content"));
        vod.put("vod_play_from", "瓜子");
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject params = new JSONObject();
        for (String item : id.split("&")) {
            String[] kv = item.split("=", 2);
            if (kv.length == 2) params.put(java.net.URLDecoder.decode(kv[0], "UTF-8"), java.net.URLDecoder.decode(kv[1], "UTF-8"));
        }
        JSONObject html = request(host + "/App/Resource/VurlDetail/showOne", params);
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", html.optString("url"));
        result.put("header", new JSONObject().put("User-Agent", "Lavf/57.83.100").put("Referer", "http://WJiZxLXA2.com/"));
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        long t = System.currentTimeMillis() / 1000;
        JSONObject html = request(host + "/App/Index/findMoreVod", new JSONObject()
                .put("keywords", key).put("ns", "").put("nt", t).put("order_val", "1"));
        JSONArray videos = new JSONArray();
        JSONArray list = html.optJSONArray("list");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String year = item.optString("vod_year");
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", item.optString("vod_pic"))
                        .put("vod_remarks", item.optString("new_continue"))
                        .put("vod_year", year.contains("-") ? year.split("-")[0] : ""));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
