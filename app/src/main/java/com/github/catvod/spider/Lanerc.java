package com.github.catvod.spider;

import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import okhttp3.Response;

public class Lanerc extends Spider {

    private static final String HOST = "https://lol.jngaoke.cn/";
    private static final String AES_KEY = "8f81c2519e3b661834219e7142000093";
    private static final String PLAY_PATH = "app/proxyx4x";
    private static final String PLAY_KEY_HEX = "5a31fe3201838a69e8f9c135f7905db25208fbc6bc3f0a9b017fc5139a451108";
    private static final String[] OFFICIAL_HOSTS = {"https://file.shangji.asia", "http://static.shangji.asia"};
    private static final String[] OFFICIAL_BUCKETS = {"10", "13", "2", "8"};
    private static final Random RANDOM = new Random();

    private JSONObject request(String url, JSONObject body) throws Exception {
        Map<String, String> h = new HashMap<>();
        h.put("User-Agent", "Dart/3.5 (dart:io)");
        h.put("upgrade-insecure-requests", "1");
        h.put("Accept", "application/json");
        String content;
        if (body != null) {
            h.put("Content-Type", "application/json");
            content = OkHttp.post(url, body.toString(), h).getBody();
        } else {
            content = OkHttp.string(url, h);
        }
        return new JSONObject(de(new JSONObject(content).optString("data")));
    }

    private String rand6() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        return sb.toString();
    }

    private String getSign(String path) {
        String clean = path.replaceAll("^/+", "");
        long time = System.currentTimeMillis() / 1000;
        String nonce = rand6();
        String digest = JUtil.md5("/" + clean + "@" + time + "@" + nonce + "@4x2g5efd84fb46a9");
        return HOST + clean + "?sign=" + time + "-" + nonce + "-" + digest;
    }

    private String de(String data) {
        String datas = data.replace('1', '9').replace('5', '1').replace('9', '5').replace('/', '+').replace('-', '/');
        return JUtil.aesDecryptEcb(datas, AES_KEY);
    }

    private String pic(String p) {
        return p.contains("doubanio.com")
                ? p + "@Referer=https://movie.douban.com@User-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
                : p;
    }

    // ---------- protobuf ----------
    private static class Field {
        int wireType;
        byte[] value;
    }

    private static Map<Integer, List<Field>> protoFields(byte[] bytes) {
        Map<Integer, List<Field>> fields = new HashMap<>();
        int off = 0;
        while (off < bytes.length) {
            long[] tagRes = readVarint(bytes, off);
            off = (int) tagRes[1];
            long tag = tagRes[0];
            int fieldNo = (int) (tag / 8);
            int wireType = (int) (tag & 7);
            if (fieldNo == 0) break;
            byte[] val;
            if (wireType == 0) {
                long[] vr = readVarint(bytes, off);
                off = (int) vr[1];
                val = String.valueOf(vr[0]).getBytes();
            } else if (wireType == 2) {
                long[] lr = readVarint(bytes, off);
                off = (int) lr[1];
                int len = (int) lr[0];
                val = new byte[len];
                System.arraycopy(bytes, off, val, 0, len);
                off += len;
            } else if (wireType == 5) {
                val = new byte[4];
                System.arraycopy(bytes, off, val, 0, 4);
                off += 4;
            } else if (wireType == 1) {
                val = new byte[8];
                System.arraycopy(bytes, off, val, 0, 8);
                off += 8;
            } else {
                break;
            }
            Field f = new Field();
            f.wireType = wireType;
            f.value = val;
            fields.computeIfAbsent(fieldNo, k -> new ArrayList<>()).add(f);
        }
        return fields;
    }

    private static long[] readVarint(byte[] bytes, int off) {
        long result = 0;
        int shift = 0;
        while (off < bytes.length && shift < 56) {
            int cur = bytes[off++] & 0xff;
            result += (long) (cur & 0x7f) << shift;
            if ((cur & 0x80) == 0) return new long[]{result, off};
            shift += 7;
        }
        return new long[]{result, off};
    }

    private static String aesGcmDecryptB64(String cipherB64, String keyHex, String nonceB64) {
        try {
            byte[] key = new byte[keyHex.length() / 2];
            for (int i = 0; i < key.length; i++) {
                key[i] = (byte) Integer.parseInt(keyHex.substring(i * 2, i * 2 + 2), 16);
            }
            byte[] cipher = Base64.decode(cipherB64, Base64.DEFAULT);
            byte[] iv = Base64.decode(nonceB64, Base64.DEFAULT);
            byte[] tag = new byte[16];
            System.arraycopy(cipher, cipher.length - 16, tag, 0, 16);
            byte[] ct = new byte[cipher.length - 16];
            System.arraycopy(cipher, 0, ct, 0, ct.length);
            byte[] combined = new byte[ct.length + tag.length];
            System.arraycopy(ct, 0, combined, 0, ct.length);
            System.arraycopy(tag, 0, combined, ct.length, tag.length);
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
            c.init(javax.crypto.Cipher.DECRYPT_MODE, new javax.crypto.spec.SecretKeySpec(key, "AES"),
                    new javax.crypto.spec.GCMParameterSpec(128, iv));
            byte[] out = c.doFinal(combined);
            return Base64.encodeToString(out, Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    private String fetchText(String url) {
        try {
            Map<String, String> h = new HashMap<>();
            h.put("User-Agent", "Dart/3.9.2");
            h.put("Accept", "*/*");
            return OkHttp.string(url, h);
        } catch (Exception e) {
            return "";
        }
    }

    private int inspectPlaylist(String url) {
        if (url == null || !url.matches("(?i)^https?://.*")) return -1;
        if (!url.matches("(?i).*\\.m3u8($|[?#]).*")) return 0;
        try {
            String playlist = fetchText(url);
            if (!playlist.matches("(?m)^#EXTM3U.*")) return -1;
            Pattern p = Pattern.compile("#EXTINF:\\s*([0-9]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(playlist);
            int count = 0;
            double duration = 0;
            while (m.find()) {
                count++;
                duration += Double.parseDouble(m.group(1));
            }
            String[] lines = playlist.split("\\r?\\n");
            int segmentCount = 0, disguised = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') continue;
                segmentCount++;
                if (line.matches("(?i).*\\.(png|jpe?g|webp|gif)($|[?#]).*")) disguised++;
            }
            if (segmentCount < 1) return -1;
            if (count >= 10 && duration >= 179 && duration <= 181) return 1;
            if (count >= 10 && duration >= 239 && duration <= 241 && disguised > 0) return 1;
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private String resolveOfficial(String vid) {
        String episodeId = vid.trim().toLowerCase();
        if (!episodeId.matches("[0-9a-f]{32}")) return "";
        for (String hostUrl : OFFICIAL_HOSTS) {
            if (!hostUrl.matches("(?i)^https?://.*")) continue;
            for (String bucket : OFFICIAL_BUCKETS) {
                String url = hostUrl.replaceAll("/+$", "") + "/" + bucket + "/" + episodeId + ".m3u8";
                try {
                    if (inspectPlaylist(url) == 0) return url;
                } catch (Exception ignored) {
                }
            }
        }
        return "";
    }

    private String resolveProxyX4X(String vid, String pid, String sign, String auth) throws Exception {
        JSONObject body = new JSONObject().put("vid", vid).put("player", pid).put("sign", sign).put("auth", auth);
        Map<String, String> h = new HashMap<>();
        h.put("User-Agent", "Dart/3.9.2");
        h.put("Content-Type", "application/json");
        h.put("Accept", "application/x-protobuf");
        h.put("Accept-Encoding", "gzip");
        Request.Builder builder = new Request.Builder().url(getSign(PLAY_PATH))
                .headers(okhttp3.Headers.of(h))
                .post(okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), body.toString()));
        Response resp = OkHttp.newCall(builder.build());
        byte[] bytes = resp.body() == null ? new byte[0] : resp.body().bytes();
        resp.close();
        if ("gzip".equalsIgnoreCase(resp.headers().get("Content-Encoding"))) {
            bytes = gunzip(bytes);
        }
        Map<Integer, List<Field>> envelope = protoFields(bytes);
        List<Field> f4 = envelope.get(4);
        if (f4 == null || f4.isEmpty() || f4.get(0).wireType != 2) return "";
        byte[] encBytes = f4.get(0).value;
        if (encBytes.length < 28) return "";
        byte[] nonce = new byte[12];
        System.arraycopy(encBytes, 0, nonce, 0, 12);
        byte[] cipherAndTag = new byte[encBytes.length - 12];
        System.arraycopy(encBytes, 12, cipherAndTag, 0, cipherAndTag.length);
        String cipherB64 = Base64.encodeToString(cipherAndTag, Base64.NO_WRAP);
        String nonceB64 = Base64.encodeToString(nonce, Base64.NO_WRAP);
        String plainB64 = aesGcmDecryptB64(cipherB64, PLAY_KEY_HEX, nonceB64);
        byte[] plainBytes = Base64.decode(plainB64, Base64.DEFAULT);
        Map<Integer, List<Field>> inner = protoFields(plainBytes);
        List<Field> f1 = inner.get(1);
        if (f1 == null || f1.isEmpty() || f1.get(0).wireType != 2) return "";
        return new String(f1.get(0).value, StandardCharsets.UTF_8);
    }

    private static byte[] gunzip(byte[] data) {
        try {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = gis.read(buf)) > 0) out.write(buf, 0, n);
            gis.close();
            return out.toByteArray();
        } catch (Exception e) {
            return data;
        }
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONObject html = request(HOST + "app/home", null);
        JSONArray classes = new JSONArray();
        JSONObject filterObj = new JSONObject();
        JSONArray vodList = html.getJSONArray("vod_list");
        for (int i = 0; i < vodList.length(); i++) {
            JSONObject item = vodList.getJSONObject(i);
            classes.put(new JSONObject().put("type_id", item.optString("sort_id")).put("type_name", item.optString("sort_name")));
            JSONArray filters = new JSONArray();
            JSONArray classV = new JSONArray();
            for (String v : item.optString("type_class", "").split(",")) {
                if (!v.trim().isEmpty()) classV.put(new JSONObject().put("n", v.trim()).put("v", v.trim()));
            }
            JSONArray yearV = new JSONArray();
            for (String v : item.optString("type_year", "").split(",")) {
                if (!v.trim().isEmpty()) yearV.put(new JSONObject().put("n", v.trim()).put("v", v.trim()));
            }
            filters.put(new JSONObject().put("key", "class").put("name", "类型").put("value", classV));
            filters.put(new JSONObject().put("key", "year").put("name", "年份").put("value", yearV));
            filterObj.put(item.optString("sort_id"), filters);
        }
        JSONArray videos = new JSONArray();
        JSONArray banner = html.optJSONArray("banner");
        if (banner != null) {
            for (int i = 0; i < banner.length(); i++) {
                JSONObject item = banner.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("vod_id"))
                        .put("vod_name", item.optString("title"))
                        .put("vod_pic", pic(item.optString("image"))));
            }
        }
        return new JSONObject().put("class", classes).put("filters", filterObj).put("list", videos).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        return new JSONObject().put("list", new JSONArray()).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String cls = extend != null && extend.containsKey("class") ? extend.get("class") : "";
        String year = extend != null && extend.containsKey("year") ? extend.get("year") : "";
        JSONObject html = request(getSign("app/vod/filter") + "&page=" + pg + "&class_id=" + tid + "&vod_class=" + cls + "&year=" + year, null);
        JSONArray videos = new JSONArray();
        JSONArray list = html.optJSONArray("filter_vods");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", pic(item.optString("vod_pic"))));
            }
        }
        return new JSONObject().put("page", pg).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject html = request(getSign("app/getvod/" + ids.get(0)), null);
        JSONArray playList = html.getJSONArray("video_play_list");
        StringBuilder playFrom = new StringBuilder();
        StringBuilder playUrl = new StringBuilder();
        for (int i = 0; i < playList.length(); i++) {
            JSONObject play = playList.getJSONObject(i);
            if (i > 0) {
                playFrom.append("$$$");
                playUrl.append("$$$");
            }
            playFrom.append(play.optString("name"));
            String player = play.optString("player");
            JSONArray video = play.getJSONArray("video");
            for (int j = 0; j < video.length(); j++) {
                if (j > 0) playUrl.append("#");
                playUrl.append(video.optString(j)).append("@@").append(player);
            }
        }
        JSONObject info = html.getJSONObject("video_play_info");
        JSONObject vod = new JSONObject();
        vod.put("type_name", info.optString("vod_class"));
        vod.put("vod_year", info.optString("vod_year"));
        vod.put("vod_area", "");
        vod.put("vod_remarks", info.optString("vod_remarks"));
        vod.put("vod_actor", "");
        vod.put("vod_director", info.optString("vod_author"));
        vod.put("vod_content", info.optString("vod_blurb").replaceAll("<.*?>", ""));
        vod.put("vod_play_from", playFrom.toString().replace("-首次加载缓慢请耐心等待", ""));
        vod.put("vod_play_url", playUrl.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String[] parts = id.split("@@");
        String vid = parts[0];
        String pid = parts.length > 1 ? parts[1] : "";
        String official = resolveOfficial(vid);
        if (!official.isEmpty()) {
            return new JSONObject().put("parse", 0).put("url", official).toString();
        }
        String url = resolveProxyX4X(vid, pid, "74322D4D62B9F4A986DFA8973EE70EBC034E74551B8715C755EDD9ED18E6820B", "com.clggjv.xcjfmd.ffo");
        if (url.matches("(?i).*\\.m3u8($|[?#]).*") && inspectPlaylist(url) == 1) {
            return new JSONObject().put("url", "").put("type", "auto").put("error", "检测到防盗提示片，当前线路不可播放").toString();
        }
        return new JSONObject().put("parse", 0).put("url", url).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject html = request(HOST + "app/vod/search?keyword=" + URLEncoder.encode(key, "UTF-8"), null);
        JSONArray videos = new JSONArray();
        JSONArray list = html.optJSONArray("search_vods");
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                videos.put(new JSONObject()
                        .put("vod_id", item.optString("id"))
                        .put("vod_name", item.optString("vod_name"))
                        .put("vod_pic", pic(item.optString("vod_pic"))));
            }
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
