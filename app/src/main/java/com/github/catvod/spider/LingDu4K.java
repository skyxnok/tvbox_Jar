package com.github.catvod.spider;

import android.content.Context;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class LingDu4K extends Spider {

    private static final String HOST = "http://43.248.128.165:9000";
    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n"
            + "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoYt0BP77U+DM08BiI/QbSRIfxijXo85BTPqIM1Ow8BNwhLETzRIZ+dEwdWDbydG/PspgBAfRpGaYVdJYtvaC2JnoO8+Ik6qMWojfEJxSFLa0Pb0A892tun4gsxoEMjcreZ+YGyaBxAfqX0BSMfdrOgIYaZQjYrw9TRLlUT31QoQIDAQAB\n"
            + "-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n"
            + "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCquQQ5r6+yJI8CDFkXRp8vUsdD45ov8EP12ooLs56ca2DQXaSN\n"
            + "GS9910bAPVA9chkp0mKIvKqjAsHz5Tl9EeNPblarGEeJUIxpxZtiSqNTpvtiD/TjhpzuHYic7RAfQ/h7p/ypE8ymU42pYjsB5t26\n"
            + "Mv6XgkLV+jzrSf73HlCuS0iMyLmt6zz3Mw9izM13EpB8iFLtfbbYymycKTx4RAmPQLwhNGex/AlUIYxXP4R2yyaa4W6mEtc6aME2\n"
            + "QuzJFxPgP3HJ9NBx/LWVn4skxWjZ7zg+VRQRHnjyVaSLu3Z5gN5ITWCyE32qaHJa6WBahZj5jWhRyAG1bQ+xKJa8lBL5AgMBAAEC\n"
            + "ggEAUwv9SjJ0PSwbhNuM2w23kcWquROWhYtTA91zGY4esehqB/IFgb2mpIh8Gje5OKqwIu/8jpd4SiOlRYdUF8sD0DfUYRZGdj2A\n"
            + "kFNX6tBz8tVfo6wvbB6naA1lzzBij1L5JO3qsjS3cJFkb+kg2yP66AC2Z+0tpfk8eRhdtshAZwfcd1DEGt1uAvYL1eaUK9HRvpt9\n"
            + "lPeGcHERDl2hBd4uyaF0K1O+zF9y59nYbTySWPxRZq3sFEE85xRMlstD7YZi7W2gKvMFRD4/FKmrZ3m7aKJRITtyKOyyPcYmepNv\n"
            + "3Qv7kk59Pg38n2WWQ0Ra/bCH3E48YNCnQvZMpitkTfJhoQKBgQDbnROOYTP8OTJ6f/qhoGjxeO3x1VOaOp8l0x7b0SCfoqNGS0Cy\n"
            + "iqj72BmJtPMPqSTjn6MmNzqbg1KOdhXyzNozs+i5ccW1M56j96mr5I/Z0FpE3oyIHNfDDBlf9M8YQqEF9oYxniYYft9oapO7cRQk\n"
            + "HER6qpvnHTavwlv4m78CXwKBgQDHAjs2YlpKDdI1lcbZJCc7TwtH+Pd2bUki8YXafWNcPhITQHbOZjr310eK1QJC6GJncjkOqbX7\n"
            + "yv3ivvTO35FZTQhuA1xEG1P00FG8bE0tHYPIwQHi9y0eA5cieMdo8E6XYria1mw/3fqSQEsfZyJlR32JQIoGAipM8iO1X2nZpwKB\n"
            + "gDkMFIhnt5lNQk+P7wsNIDWZtDWdtJnboHuy29E+Abt2A/O+mI/IdRz2hau/1WO8DFkUnszOi+rZshhPlGP90rCbi1igtTrcrdjp\n"
            + "/KkqNjPea5R4OwkgdOu1uOG0NheXNzzVTQaWjk7Opjn5dWa7eP/oV+GFb/oZHJuLYVizHGsBAoGADA7rjZEKDYCm4w5PPSr+oY5Z\n"
            + "jaPdQrS+gLqHtMRyN82fBMGcMUdqfUfzEstzVqCEDeaS5HuOBlK3bXzKkppjUTjksN3NQmcxgBz7RuJ9DqXCLXDcb2cwuafYCYOt\n"
            + "+YLOEEgwDVm+t2P44dG5e46hO+fICH/7nP+WlpD5buz4GfMCgYB57r3g/6hi9WUDnfc7ZAzWMqR0EhJVYKYy+KFEtdIPzhkkIHq5\n"
            + "RASe88E9kzoGoZFdb3tIjvGZWcHerirrqWkMsuQtP/Qi0zjieid5tAPj+r4kbiCVTw0E0jnmPBzGInQi7lpeTTKnG1fbyS5lBS+W\n"
            + "mHfIuzpECgCkxhaT+LJJkg==\n"
            + "-----END PRIVATE KEY-----";
    private static final String SKEY = "MIIDPDCCAiQCAQEwDQYJKoZIhvcNAQELBQAwYzEOMAwGA1UEAwwFemhhbmcxEDAOBgNVBAsMB2xpbmd6aGkxDzANBgNVBAoMBmdvbmdzaTERMA8GA1UEBwwIbGFuZ2ZhbmcxDjAMBgNVBAgMBWhlYmVpMQswCQYDVQQGEwJjbjAgFw0yNjA1MjIwMjMyNDJaGA8yMDc2MDUwOTAyMzI0MlowYzEOMAwGA1UEAwwFemhhbmcxEDAOBgNVBAsMB2xpbmd6aGkxDzANBgNVBAoMBmdvbmdzaTERMA8GA1UEBwwIbGFuZ2ZhbmcxDjAMBgNVBAgMBWhlYmVpMQswCQYDVQQGEwJjbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK4jK+Rl7YFZZ8GZ/Auxc0fmll1XC1+MTqhegCrQRFm78lOmUq2iYhRFKrbL3thzmH672d5eZbLhVjWbZAkfga3aF6mO6qaZygTRYAMTYOqeZKRadqy0AxEvs0SLNlfCTQb3//u15egcJgxgH6F8vCCPd4ILhMiuj1nrJG3mJJoLiCTILR+V+uJv/1qJO457tTz5rlH0ntbsvO2zDRCDEkGtGp+eM37AALJB+M3LKL+r5mvThXTVs/zyBECA2PzP8Q5U5cgDyxL2B2ivWleI8YrUOtb7qwNKHaSi7SDu2WbVfVRXYZ5I+3HSncNNGBuz62geKq63qSaH3eDQtFYjavsCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAhixGSQ4lQPgdz4wEPxoKpSYr+njaVSN1lxQwmLzh40E1cvJtvC/TN4JjlFvgXgMDf+Zpftr+zXW20HHnRFaGSFZKByFRMcZfpkInSAfiuL7kKg7vt9jcuTsRkCoDDje93DaVlIUhHJcNuzvdhzZclIsA38Iej8Cb8D6u+fflt09fu9u98A1nsweIYzE+k3uorMsWvIld2KeVCeYKMhMfQUwW3AnXVJ7dK5F7sn6TG9cOERU945Gy3NADcrIlAeWIXf3x2sB/d5nWsfY6sVVJIaRYOEVTrSybMeSYXkvWAenfFPN3YBXhtXW+vzuYlsKmx9KoivHm5KYUvuQamJkt0Q==";

    private String deviceId;
    private String token = "";
    private String userId = "";

    @Override
    public void init(Context context, String extend) throws Exception {
        deviceId = UUID.randomUUID().toString();
        Map<String, String> h = new HashMap<>();
        h.put("Content-Type", "application/json");
        h.put("User-Agent", "okhttp/4.12.0");
        h.put("deviceId", deviceId);
        h.put("client", "app");
        h.put("deviceType", "Android");
        JSONObject res = new JSONObject(OkHttp.string(HOST + "/v1/app/user/visitorInfo", h)).getJSONObject("data");
        token = res.optString("token");
        userId = res.optString("userId");
    }

    private Map<String, String> hh() {
        Map<String, String> h = new HashMap<>();
        h.put("Cache-Control", "no-cache");
        h.put("token", token == null ? "" : token);
        h.put("deviceId", deviceId);
        h.put("client", "app");
        h.put("deviceType", "Android");
        h.put("Content-Type", "application/json;charset=UTF-8");
        h.put("User-Agent", "okhttp/4.12.0");
        return h;
    }

    private Map<String, String> hh2(String data) {
        long t = System.currentTimeMillis() / 1000;
        String sha1hex = JUtil.sha1(SKEY);
        String signb64 = Base64.encodeToString(("SaltLSFBTimestamp" + t + "Params" + data + "ClientappDeviceId" + deviceId).getBytes(), Base64.NO_WRAP);
        Map<String, String> h = new HashMap<>();
        h.put("snjm", rsaEn("116"));
        h.put("appsign", rsaEn(sha1hex));
        h.put("timestamp", String.valueOf(t));
        h.put("sign", JUtil.md5(signb64).toUpperCase());
        h.put("Cache-Control", "no-cache");
        h.put("token", token == null ? "" : token);
        h.put("deviceId", deviceId);
        h.put("client", "app");
        h.put("deviceType", "Android");
        h.put("Content-Type", "application/json;charset=UTF-8");
        h.put("User-Agent", "okhttp/4.12.0");
        return h;
    }

    private String rsaEn(String data) {
        return JUtil.rsaEncrypt(data, PUBLIC_KEY);
    }

    private String rsaDe(String data) {
        return JUtil.rsaDecrypt(data, PRIVATE_KEY);
    }

    private JSONObject request(String url, Object body, boolean encrypt, String signStr) throws Exception {
        if (encrypt) {
            String plain = body instanceof String ? (String) body : body.toString();
            String ss = signStr != null ? signStr : plain;
            Map<String, String> o = hh2(ss);
            String resp = OkHttp.post(url, new JSONObject().put("key", rsaEn(plain)).toString(), o).getBody();
            return new JSONObject(rsaDe(new JSONObject(resp).optString("data")));
        }
        Map<String, String> o = hh();
        String resp;
        if (body != null) {
            resp = OkHttp.post(url, body instanceof String ? (String) body : body.toString(), o).getBody();
        } else {
            resp = OkHttp.string(url, o);
        }
        return new JSONObject(resp);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(HOST + "/v1/app/screen/screenType", "POST", false, null);
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray data = html.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject tp = data.getJSONObject(i);
            classes.put(new JSONObject().put("type_id", tp.optString("id")).put("type_name", tp.optString("name")));
            JSONArray filters = new JSONArray();
            JSONArray children = tp.optJSONArray("children");
            if (children != null) {
                for (int j = 0; j < children.length(); j++) {
                    JSONObject group = children.getJSONObject(j);
                    JSONArray gc = group.optJSONArray("children");
                    if (gc != null && gc.length() > 0) {
                        JSONArray value = new JSONArray();
                        for (int k = 0; k < gc.length(); k++) {
                            JSONObject c = gc.getJSONObject(k);
                            value.put(new JSONObject().put("n", c.optString("name")).put("v", c.optString("id")));
                        }
                        String gname = group.optString("name");
                        String key = "地区".equals(gname) ? "area" : "类型".equals(gname) ? "class" : "年份".equals(gname) ? "year" : gname;
                        filters.put(new JSONObject().put("key", key).put("name", gname).put("value", value));
                    }
                }
            }
            JSONArray sorts = new JSONArray();
            sorts.put(new JSONObject().put("v", "NEWEST").put("n", "最新"));
            sorts.put(new JSONObject().put("v", "COLLECT").put("n", "评分"));
            sorts.put(new JSONObject().put("v", "HOT").put("n", "热搜"));
            filters.put(new JSONObject().put("key", "sort").put("name", "排序").put("value", sorts));
            filterObj.put(tp.optString("id"), filters);
        }
        return new JSONObject().put("class", classes).put("filters", filterObj).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        JSONObject data = new JSONObject().put("condition", "64").put("pageNum", "1").put("pageSize", "6");
        JSONObject tj = request(HOST + "/v1/app/recommend/recommendSubList", data, false, null);
        JSONArray videos = new JSONArray();
        JSONArray records = tj.getJSONObject("data").optJSONArray("records");
        if (records != null) {
            for (int i = 0; i < records.length(); i++) {
                JSONObject item = records.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id") + "@@" + item.optString("typeId"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", item.optString("cover"))
                        .put("vod_remarks", item.optString("remarks")));
            }
        }
        return new JSONObject().put("list", videos).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cls = extend != null && extend.containsKey("class") ? extend.get("class") : "";
        String area = extend != null && extend.containsKey("area") ? extend.get("area") : "";
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "POPULARITY";
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "";
        JSONObject body = new JSONObject().put("condition", new JSONObject()
                        .put("classify", cls).put("region", area).put("sreecnTypeEnum", sort).put("typeId", tid).put("year", year))
                .put("pageNum", pg).put("pageSize", 40);
        JSONObject html = request(HOST + "/v1/app/screen/screenMovie", body, false, null);
        JSONArray videos = new JSONArray();
        JSONArray records = html.getJSONObject("data").optJSONArray("records");
        if (records != null) {
            for (int i = 0; i < records.length(); i++) {
                JSONObject item = records.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id") + "@@" + item.optString("typeId"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", item.optString("cover"))
                        .put("vod_remarks", item.optString("remarks"))
                        .put("vod_year", item.optString("year")));
            }
        }
        return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    private String joinArr(String[] k, String[] v) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < k.length; i++) {
            sb.append(k[i]).append(v[i]);
        }
        return sb.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String[] parts = ids.get(0).split("@@");
        String vid = parts[0];
        String typeId = parts[1];
        JSONObject res = request(HOST + "/v1/app/play/movieDesc", new JSONObject().put("id", Long.parseLong(vid)).put("typeId", typeId).toString(), false, null).getJSONObject("data");
        JSONObject data = new JSONObject().put("episodeId", "").put("episodeIndex", "").put("id", Long.parseLong(vid))
                .put("playerId", "").put("source", 0).put("typeId", typeId).put("userId", userId);
        String arr = joinArr(new String[]{"episodeId", "episodeIndex", "id", "playerId", "source", "typeId", "userId"},
                new String[]{"", "", vid, "", "0", typeId, userId});
        JSONObject html = request(HOST + "/v1/app/play/movieDetails", data, true, arr);
        StringBuilder playFrom = new StringBuilder();
        JSONArray playerList = html.getJSONArray("moviePlayerList");
        for (int i = 0; i < playerList.length(); i++) {
            JSONObject item = playerList.getJSONObject(i);
            if (i > 0) playFrom.append("$$$");
            playFrom.append(item.optString("moviePlayerName").replaceAll("【.*?】", "")).append(" [").append(item.optString("code")).append("]");
        }
        StringBuilder playUrl = new StringBuilder();
        JSONArray episodeList = html.getJSONArray("episodeList");
        for (int i = 0; i < episodeList.length(); i++) {
            JSONObject item = episodeList.getJSONObject(i);
            if (i > 0) playUrl.append("#");
            playUrl.append(item.optString("episode").replaceAll("(?i) 4k", "")).append("$").append(item.optString("id"))
                    .append("@@").append(ids.get(0)).append("@@").append(html.optString("playerId"));
        }
        JSONArray pids = new JSONArray();
        for (int i = 0; i < playerList.length(); i++) pids.put(playerList.getJSONObject(i).optString("id"));
        for (int i = 1; i < pids.length(); i++) {
            String pid = pids.optString(i);
            JSONObject d2 = new JSONObject().put("episodeId", "").put("episodeIndex", "").put("id", Long.parseLong(vid))
                    .put("playerId", pid).put("source", 0).put("typeId", typeId).put("userId", userId);
            String arr2 = joinArr(new String[]{"episodeId", "episodeIndex", "id", "playerId", "source", "typeId", "userId"},
                    new String[]{"", "", vid, pid, "0", typeId, userId});
            JSONObject lineRes = request(HOST + "/v1/app/play/movieDetails", d2, true, arr2);
            playUrl.append("$$$");
            JSONArray lineEp = lineRes.getJSONArray("episodeList");
            for (int j = 0; j < lineEp.length(); j++) {
                JSONObject item = lineEp.getJSONObject(j);
                if (j > 0) playUrl.append("#");
                playUrl.append(item.optString("episode").replaceAll("(?i) 4k", "")).append("$").append(item.optString("id"))
                        .append("@@").append(ids.get(0)).append("@@").append(lineRes.optString("playerId"));
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", res.optString("classify"));
        vod.put("vod_year", res.optString("year"));
        vod.put("vod_area", res.optString("area"));
        vod.put("vod_actor", res.optString("star"));
        vod.put("vod_director", res.optString("director"));
        vod.put("vod_content", res.optString("introduce"));
        vod.put("vod_play_from", playFrom.toString());
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        String episodeId = parts[0];
        String vid = parts[1];
        String typeId = parts[2];
        String playerId = parts[3];
        JSONObject data = new JSONObject().put("episodeId", episodeId).put("episodeIndex", "").put("id", Long.parseLong(vid))
                .put("playerId", playerId).put("source", 0).put("typeId", typeId).put("userId", userId);
        String arr = joinArr(new String[]{"episodeId", "episodeIndex", "id", "playerId", "source", "typeId", "userId"},
                new String[]{episodeId, "", vid, playerId, "0", typeId, userId});
        JSONObject res = request(HOST + "/v1/app/play/movieDetails", data, true, arr);
        JSONObject url = request(HOST + "/v1/app/play/analysisMovieUrl?playerUrl=" + res.optString("url") + "&playerId=" + res.optString("playerId"), null, false, null).getJSONObject("data");
        return new JSONObject().put("parse", 0).put("url", url.optString("url")).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject data = new JSONObject().put("condition", new JSONObject().put("value", key)).put("pageNum", 1).put("pageSize", 40);
        JSONObject html = request(HOST + "/v1/app/search/searchMovie", data, false, null);
        JSONArray videos = new JSONArray();
        JSONArray records = html.getJSONObject("data").optJSONArray("records");
        if (records != null) {
            for (int i = 0; i < records.length(); i++) {
                JSONObject item = records.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id") + "@@" + item.optString("typeId"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", item.optString("cover"))
                        .put("vod_remarks", item.optString("remarks"))
                        .put("vod_year", item.optString("year")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
