package com.github.catvod.spider;

import android.content.Context;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class JiuJiuYingShi extends Spider {

    private static final Random RANDOM = new Random();
    private String host;
    private String appkey;
    private String versionName;
    private String name;
    private String buildSignature;
    private String uuid;
    private JSONArray player = new JSONArray();
    private JSONArray parserApi = new JSONArray();

    @Override
    public void init(Context context, String extend) throws Exception {
        uuid = UUID.randomUUID().toString();
        JSONObject ext = new JSONObject(extend);
        host = ext.optString("host");
        appkey = ext.optString("appkey");
        versionName = ext.optString("versionName");
        name = ext.optString("name");
        buildSignature = ext.optString("buildSignature");
    }

    private String randomHex(int len) {
        String chars = "0123456789abcdef";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(chars.charAt(RANDOM.nextInt(16)));
        return sb.toString();
    }

    private String key() {
        return uuid.replace("-", "");
    }

    private String en(String data) {
        try {
            String iv = randomHex(32);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key().getBytes("UTF-8"), "AES"), new IvParameterSpec(hexToBytes(iv)));
            String cipherHex = bytesToHex(cipher.doFinal(data.getBytes("UTF-8")));
            return Base64.encodeToString(hexToBytes(iv + cipherHex), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    private String de(String data) {
        try {
            String hex = bytesToHex(Base64.decode(data, Base64.DEFAULT));
            String iv = hex.substring(0, 32);
            String cipherHex = hex.substring(32);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key().getBytes("UTF-8"), "AES"), new IvParameterSpec(hexToBytes(iv)));
            return bytesToHex(cipher.doFinal(hexToBytes(cipherHex)));
        } catch (Exception e) {
            return "";
        }
    }

    private JSONObject request(JSONObject body, String url) throws Exception {
        body.put("timestamp", System.currentTimeMillis());
        body.put("nonce", Base64.encodeToString(hexToBytes(randomHex(32)), Base64.NO_WRAP));
        String enbody = en(body.toString());
        long ts = body.optLong("timestamp");
        String nonce = body.optString("nonce");
        Map<String, String> h = new HashMap<>();
        h.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6299.95 Safari/537.36");
        h.put("sign", JUtil.sha256(enbody + ":" + ts + ":" + nonce + ":" + body.optString("token") + ":" + appkey));
        h.put("appkey", appkey);
        h.put("client_type", "android");
        h.put("api_version", "v1");
        h.put("uuid", uuid);
        h.put("nonce", nonce);
        h.put("version", "");
        h.put("timestamp", String.valueOf(ts));
        h.put("Content-Type", "application/json; charset=utf-8");
        String dd = OkHttp.post(url, enbody, h).getBody();
        byte[] gz = hexToBytes(de(dd));
        return new JSONObject(gunzip(gz));
    }

    private static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private static String gunzip(byte[] data) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = gis.read(buf)) > 0) out.write(buf, 0, n);
        gis.close();
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject data = new JSONObject()
                .put("v", versionName).put("n", name).put("s", buildSignature)
                .put("pl", "1").put("apiVersion", "v2")
                .put("token", "").put("timestamp", "").put("nonce", "");
        JSONObject res = request(data, host + "/app/systemInit");
        player = res.getJSONObject("player").names() != null ? collectPlayers(res.getJSONObject("player")) : new JSONArray();
        parserApi = res.optJSONArray("parser_api") == null ? new JSONArray() : res.getJSONArray("parser_api");
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray cats = res.getJSONObject("categorys").getJSONArray("data");
        for (int i = 0; i < cats.length(); i++) {
            JSONObject tp = cats.getJSONObject(i);
            String tn = tp.optString("name").replace(" ", "");
            if ("公告".equals(tn) || "动漫资讯".equals(tn)) continue;
            classes.put(new JSONObject().put("type_id", tp.optString("id")).put("type_name", tn));
            JSONArray filters = new JSONArray();
            JSONObject dd = new JSONObject(tp.optString("type_extend"));
            String[] keys = {"class", "area", "year"};
            String[] names = {"类型", "地区", "年份"};
            for (int k = 0; k < keys.length; k++) {
                String val = dd.optString(keys[k]).trim();
                if (!val.isEmpty()) {
                    JSONArray value = new JSONArray();
                    for (String v : val.split(",")) value.put(new JSONObject().put("n", v).put("v", v));
                    filters.put(new JSONObject().put("key", keys[k]).put("name", names[k]).put("value", value));
                }
            }
            JSONArray sorts = new JSONArray();
            sorts.put(new JSONObject().put("n", "最热").put("v", "vod_hits"));
            sorts.put(new JSONObject().put("n", "最新").put("v", "vod_time"));
            sorts.put(new JSONObject().put("n", "高分").put("v", "vod_score"));
            filters.put(new JSONObject().put("key", "sort").put("name", "排序").put("value", sorts));
            filterObj.put(tp.optString("id"), filters);
        }
        return new JSONObject().put("class", classes).put("filters", filterObj).toString();
    }

    private JSONArray collectPlayers(JSONObject playerObj) throws Exception {
        JSONArray out = new JSONArray();
        JSONArray names = playerObj.names();
        for (int i = 0; i < names.length(); i++) {
            out.put(playerObj.getJSONObject(names.getString(i)));
        }
        return out;
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "vod_hits";
        JSONObject data = new JSONObject()
                .put("kw", "").put("page", pg).put("limit", 21).put("pid", tid)
                .put("orderBy", sort).put("isCategory", 1)
                .put("token", "").put("timestamp", "").put("nonce", "");
        if (extend != null && extend.containsKey("class")) data.put("class", extend.get("class"));
        if (extend != null && extend.containsKey("area")) data.put("area", extend.get("area"));
        if (extend != null && extend.containsKey("year")) data.put("year", extend.get("year"));
        JSONObject res = request(data, host + "/vod/search");
        JSONArray videos = new JSONArray();
        JSONArray list = res.optJSONArray("data");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String pic = item.optString("pic");
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", pic + "@Referer=" + pic + "@User-Agent=Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
                        .put("vod_remarks", item.optString("remarks"))
                        .put("vod_year", item.optString("year")));
            }
        }
        return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject data = new JSONObject()
                .put("id", ids.get(0)).put("eps", "").put("v", versionName).put("pl", 1)
                .put("token", "").put("timestamp", "").put("nonce", "");
        JSONObject res = request(data, host + "/vod/detail").getJSONObject("data");
        Map<String, JSONObject> pMap = new HashMap<>();
        for (int i = 0; i < player.length(); i++) {
            JSONObject p = player.getJSONObject(i);
            pMap.put(p.optString("code"), p);
        }
        String[] fromArr = res.optString("play_from").split("\\$\\$\\$");
        String[] urlArr = res.optString("play_url").split("\\$\\$\\$");
        String[] exclude = {"bfzym3u8", "tym3u8", "zjm3u8", "lzm3u8", "sdm3u8", "kbm3u8", "bjm3u8", "xkm3u8", "tpm3u8",
                "hnm3u8", "wjm3u8", "ffm3u8", "99m3u8", "dbm3u8", "rym3u8", "mzm3u8", "mym3u8", "wwm3u8", "mtm3u8",
                "snm3u8", "okm3u8", "wolong", "http", "ruyi", "yym3u8", "ikm3u8", "jsm3u8", "co_egg", "NSYS"};
        List<Map.Entry<String, String>> filtered = new ArrayList<>();
        for (int i = 0; i < fromArr.length && i < urlArr.length; i++) {
            boolean skip = false;
            for (String e : exclude) {
                if (e.equals(fromArr[i])) {
                    skip = true;
                    break;
                }
            }
            if (!skip) filtered.add(new java.util.AbstractMap.SimpleEntry<>(fromArr[i], urlArr[i]));
        }
        filtered.sort((a, b) -> {
            int sa = pMap.containsKey(b.getKey()) ? pMap.get(b.getKey()).optInt("sort", 0) : 0;
            int sb = pMap.containsKey(a.getKey()) ? pMap.get(a.getKey()).optInt("sort", 0) : 0;
            return sa - sb;
        });
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < filtered.size(); i++) {
            String code = filtered.get(i).getKey();
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            JSONObject p = pMap.get(code);
            String pname = p == null ? "" : p.optString("name").trim();
            playFrom.append(pname.isEmpty() ? code : pname + " [" + code + "]");
            String parseUrl = p == null ? "" : p.optString("parseUrl");
            String[] eps = filtered.get(i).getValue().split("#");
            for (int j = 0; j < eps.length; j++) {
                if (j > 0) playUrl.append("#");
                String[] np = eps[j].split("\\$", 2);
                playUrl.append(np[0]).append("$").append(np.length > 1 ? np[1] : "").append("@@").append(parseUrl);
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("type_name", res.optString("class"));
        vod.put("vod_year", res.optString("year"));
        vod.put("vod_area", res.optString("area"));
        vod.put("vod_remarks", res.optString("remarks"));
        vod.put("vod_actor", res.optString("actor"));
        vod.put("vod_director", res.optString("director"));
        vod.put("vod_content", res.optString("content"));
        vod.put("vod_play_from", playFrom.toString());
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        String url = parts[0];
        String parseId = parts.length > 1 ? parts[1] : "";
        if (url.matches("(?i).*\\.(m3u8|mp4).*") || parseId.trim().isEmpty()) {
            return new JSONObject().put("parse", 0).put("url", url).toString();
        }
        String playUrl = "";
        for (String pid : parseId.split(",")) {
            pid = pid.trim();
            if (pid.isEmpty()) continue;
            for (int i = 0; i < parserApi.length(); i++) {
                JSONObject pa = parserApi.getJSONObject(i);
                if (String.valueOf(pa.optInt("id")).equals(pid)) {
                    try {
                        JSONObject res = new JSONObject(OkHttp.string(pa.optString("api_url") + url));
                        if (res.has("url")) {
                            playUrl = res.optString("url");
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            if (!playUrl.isEmpty()) break;
        }
        return new JSONObject().put("parse", 0).put("url", playUrl).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject data = new JSONObject()
                .put("kw", key).put("page", 1).put("limit", 21).put("orderBy", "vod_hits_month")
                .put("sort", "desc").put("token", "").put("timestamp", "").put("nonce", "");
        JSONObject res = request(data, host + "/vod/search");
        JSONArray videos = new JSONArray();
        JSONArray list = res.optJSONArray("data");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String pic = item.optString("pic");
                if (pic.contains("url=")) {
                    String real = pic.split("url=")[1];
                    pic = real + "@Referer=" + real + "@User-Agent=Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
                }
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("name"))
                        .put("vod_pic", pic)
                        .put("vod_remarks", item.optString("remarks"))
                        .put("vod_year", item.optString("year")));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
