package com.github.catvod.spider;

import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class BiliCommon {

    public static final String HOST = "https://api.bilibili.com";
    public static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36";
    public static volatile String cookies = "buvid3=522AB244-3E11-43A5-8C46-62934836C10240947infoc";

    public static void initCookie() {
        try {
            Map<String, String> header = new HashMap<>();
            header.put("User-Agent", UA);
            Response resp = OkHttp.newCall("https://www.bilibili.com", header);
            String setCookie = resp.headers().get("Set-Cookie");
            resp.close();
            if (setCookie != null && setCookie.contains(";")) {
                cookies = setCookie.split(";")[0];
            }
        } catch (Exception ignored) {
        }
    }

    public static JSONObject request(String url) throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", cookies);
        header.put("User-Agent", UA);
        header.put("Referer", "https://www.bilibili.com");
        return new JSONObject(OkHttp.string(url, header));
    }

    public static String playUrl(String bvid, String cid, String epId, String seasonId) {
        String url = HOST + "/x/player/wbi/playurl?bvid=" + bvid + "&cid=" + cid + "&qn=127&fnval=4048&fourk=1&fnver=0"
                + "&voice_balance=0&gaia_source=pre-load&isGaiaAvoided=true&web_location=1315873&try_look=1"
                + "&dm_img_list=%5B%5D&dm_img_str=Jz93SzgwQU1kaUl1QE93VTEvaTdUKj85QmNQWk"
                + "&dm_cover_img_str=QzxoLUY%2FL1lGWjM1RlgpTWhXKEJAXz5dXENzL0g3fUl%2BQlVkfTd3LlxcK3JHb1gtdSZgSDopWFM0NDhET19je2RcL3t8T2ZWK3FSXS82dFgpaDd0Ll1rZ1QxMUJSc3YwKW54dDFOQSx5O3RDOUR8fHxLQ0ZZc3NFd2FdWSw%2BSA"
                + "&dm_img_inter=%7B%22ds%22%3A%5B%5D%2C%22wh%22%3A%5B0%2C0%2C0%5D%2C%22of%22%3A%5B0%2C0%2C0%5D%7D";
        if (epId != null && !epId.isEmpty() && !"null".equals(epId)) url += "&ep_id=" + epId;
        if (seasonId != null && !seasonId.isEmpty() && !"null".equals(seasonId)) url += "&season_id=" + seasonId;
        return url;
    }

    public static String buildMpd(JSONObject playinfo, String qn) throws Exception {
        JSONObject data = playinfo.getJSONObject("data");
        JSONObject dash = data.getJSONObject("dash");
        int duration = dash.optInt("duration", 0);
        StringBuilder videoXml = new StringBuilder();
        StringBuilder seen = new StringBuilder();
        JSONArray videos = dash.getJSONArray("video");
        for (int i = 0; i < videos.length(); i++) {
            JSONObject video = videos.getJSONObject(i);
            if (!String.valueOf(video.optInt("id")).equals(qn)) continue;
            String codec = String.valueOf(video.optInt("codecid"));
            if (seen.indexOf("," + codec + ",") >= 0) continue;
            seen.append(",").append(codec).append(",");
            StringBuilder xml = new StringBuilder("<AdaptationSet>\n<ContentComponent contentType=\"video\"/>\n");
            for (int j = 0; j < videos.length(); j++) {
                JSONObject v = videos.getJSONObject(j);
                if (!String.valueOf(v.optInt("id")).equals(qn) || !String.valueOf(v.optInt("codecid")).equals(codec)) continue;
                String u = v.optString("baseUrl").replace("&", "&amp;");
                String rid = v.optInt("id") + "_" + v.optInt("codecid");
                xml.append("<Representation id=\"").append(rid).append("\" bandwidth=\"").append(v.optString("bandwidth"))
                        .append("\" codecs=\"").append(v.optString("codecs")).append("\" mimeType=\"video/mp4\"")
                        .append(" height='").append(v.optString("height")).append("' width='").append(v.optString("width"))
                        .append("' frameRate='").append(v.optString("frameRate")).append("' sar='").append(v.optString("sar"))
                        .append("' startWithSAP=\"").append(v.optString("startWithSap")).append("\">\n")
                        .append("<BaseURL>").append(u).append("</BaseURL>\n")
                        .append("<SegmentBase indexRange=\"").append(v.optJSONObject("SegmentBase").optString("indexRange")).append("\">\n")
                        .append("<Initialization range=\"").append(v.optJSONObject("SegmentBase").optString("Initialization")).append("\"/></SegmentBase>\n")
                        .append("</Representation>\n");
            }
            xml.append("</AdaptationSet>\n");
            videoXml.append(xml);
        }
        String[] audioOrder = {"30280", "30232", "30216"};
        String[] audioRate = {"192000", "132000", "64000"};
        StringBuilder audioXml = new StringBuilder();
        JSONArray audios = dash.getJSONArray("audio");
        for (int i = 0; i < audioOrder.length; i++) {
            JSONObject audio = null;
            for (int j = 0; j < audios.length(); j++) {
                if (String.valueOf(audios.getJSONObject(j).optInt("id")).equals(audioOrder[i])) {
                    audio = audios.getJSONObject(j);
                    break;
                }
            }
            if (audio == null) continue;
            String u = audio.optString("baseUrl").replace("&", "&amp;");
            String rid = audio.optInt("id") + "_" + audio.optInt("codecid");
            audioXml.append("<AdaptationSet>\n<ContentComponent contentType=\"audio\"/>\n")
                    .append("<Representation id=\"").append(rid).append("\" bandwidth=\"").append(audio.optString("bandwidth"))
                    .append("\" codecs=\"").append(audio.optString("codecs")).append("\" mimeType=\"audio/mp4\"")
                    .append(" numChannels='2' sampleRate='").append(audioRate[i])
                    .append("' startWithSAP=\"").append(audio.optString("startWithSap")).append("\">\n")
                    .append("<BaseURL>").append(u).append("</BaseURL>\n")
                    .append("<SegmentBase indexRange=\"").append(audio.optJSONObject("SegmentBase").optString("indexRange")).append("\">\n")
                    .append("<Initialization range=\"").append(audio.optJSONObject("SegmentBase").optString("Initialization")).append("\"/></SegmentBase>\n")
                    .append("</Representation>\n</AdaptationSet>\n");
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<MPD xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:mpeg:dash:schema:mpd:2011\""
                + " xsi:schemaLocation=\"urn:mpeg:dash:schema:mpd:2011 DASH-MPD.xsd\" type=\"static\""
                + " mediaPresentationDuration=\"PT" + duration + "S\" minBufferTime=\"PT5S\""
                + " profiles=\"urn:mpeg:dash:profile:isoff-on-demand:2011\">\n"
                + "<Period duration=\"PT" + duration + "S\" start=\"PT0S\">\n"
                + videoXml + audioXml
                + "</Period>\n</MPD>";
    }
}
