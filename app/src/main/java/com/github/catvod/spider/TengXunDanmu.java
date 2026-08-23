package com.github.catvod.spider;

import android.util.Base64;

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

public class TengXunDanmu extends Spider {

    private static final String HOST = "https://pbaccess.video.qq.com";
    private static final String FILTER_JSON = "{\"100113\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"75\",\"n\":\"最热\"},{\"v\":\"79\",\"n\":\"最新\"},{\"v\":\"85\",\"n\":\"高分\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"-1\"},{\"n\":\"中国\",\"v\":\"0\"},{\"n\":\"中国香港\",\"v\":\"14\"},{\"n\":\"中国台湾\",\"v\":\"4\"},{\"n\":\"美国\",\"v\":\"8\"},{\"n\":\"韩国\",\"v\":\"5\"},{\"n\":\"日本\",\"v\":\"10\"}]}],\"100173\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"75\",\"n\":\"最热\"},{\"n\":\"最新\",\"v\":\"83\"},{\"n\":\"高分\",\"v\":\"81\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"-1\"},{\"n\":\"中国\",\"v\":\"100024\"},{\"n\":\"中国香港\",\"v\":\"100025\"},{\"n\":\"中国台湾\",\"v\":\"100026\"},{\"n\":\"美国\",\"v\":\"100029\"},{\"n\":\"韩国\",\"v\":\"100028\"},{\"n\":\"日本\",\"v\":\"100027\"}]}],\"100119\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"75\",\"n\":\"最热\"},{\"v\":\"23\",\"n\":\"更新\"},{\"v\":\"85\",\"n\":\"高分\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"-1\"},{\"n\":\"国漫\",\"v\":\"1\"},{\"n\":\"日漫\",\"v\":\"2\"},{\"n\":\"欧美\",\"v\":\"3\"},{\"n\":\"其他\",\"v\":\"4\"}]}],\"100109\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"75\",\"n\":\"最热\"},{\"v\":\"23\",\"n\":\"更新\"},{\"v\":\"85\",\"n\":\"高分\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"-1\"},{\"n\":\"国内\",\"v\":\"1\"},{\"n\":\"海外\",\"v\":\"2\"}]}],\"100105\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"75\",\"n\":\"最热\"},{\"n\":\"最新\",\"v\":\"74\"},{\"v\":\"85\",\"n\":\"高分\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"地区\",\"v\":\"-1\"},{\"n\":\"国内\",\"v\":\"1\"},{\"n\":\"国外\",\"v\":\"2\"}]}],\"100150\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"75\",\"n\":\"最热\"},{\"n\":\"最新\",\"v\":\"76\"}]}],\"110755\":[{\"key\":\"sort\",\"name\":\"排序\",\"value\":[{\"v\":\"75\",\"n\":\"最热\"},{\"n\":\"最新\",\"v\":\"76\"}]}]}";
    private static final Map<String, String> HH = new HashMap<>();

    static {
        HH.put("accept", "application/json");
        HH.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        HH.put("content-type", "application/json");
        HH.put("origin", "https://v.qq.com");
        HH.put("referer", "https://v.qq.com/");
    }

    private JSONObject request(String url, JSONObject body) throws Exception {
        return new JSONObject(OkHttp.post(url, body.toString(), HH).getBody());
    }

    private JSONArray getlist(JSONObject result) {
        JSONArray out = new JSONArray();
        try {
            JSONArray datas = result.getJSONObject("data").getJSONArray("module_list_datas").getJSONObject(0)
                    .getJSONArray("module_datas").getJSONObject(0).getJSONObject("item_data_lists").getJSONArray("item_datas");
            for (int i = 0; i < datas.length(); i++) {
                JSONObject params = datas.getJSONObject(i).optJSONObject("item_params");
                if (params == null) continue;
                if (!"0".equals(params.optString("is_trailer"))) continue;
                String cid = params.optString("cid");
                String vid = params.optString("vid");
                if (!cid.isEmpty() && !vid.isEmpty()) {
                    out.put(params.optString("title") + "$https://v.qq.com/x/cover/" + cid + "/" + vid + ".html");
                }
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    private String aesDecrypt(String data) {
        try {
            String datas = data.split("https://baidu.con/")[1];
            String key = datas.substring(0, 16);
            return JUtil.aesDecrypt(datas.substring(16), key, key);
        } catch (Exception e) {
            return "";
        }
    }

    private String changying(String data) {
        String cleaned = data.replaceAll("[^a-zA-Z0-9+/=:]|\"|\\\\|https:", "");
        String[] parts = cleaned.split(":");
        if (parts.length < 5) return "";
        return JUtil.aesDecrypt(parts[4], parts[1], parts[2]);
    }

    private String b64Decode(String s) {
        return new String(Base64.decode(s.replaceAll("\\s", ""), Base64.DEFAULT));
    }

    private String yulu(String data) {
        try {
            String datas = b64Decode(data).split("https://ldmax.cooom/")[1];
            String key = new StringBuilder(datas.substring(0, 16)).reverse().toString();
            return JUtil.aesDecrypt(datas.substring(16), key, key);
        } catch (Exception e) {
            return "";
        }
    }

    public static String danmuXml(String url) {
        try {
            String vid = url.substring(url.lastIndexOf('/') + 1);
            if (vid.contains(".")) vid = vid.substring(0, vid.indexOf('.'));
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.put("Referer", "https://v.qq.com/");
            JSONObject base = new JSONObject(OkHttp.string("https://dm.video.qq.com/barrage/base/" + vid, headers));
            JSONObject segmentIndex = base.optJSONObject("segment_index");
            if (segmentIndex == null) return "<i></i>";
            StringBuilder xml = new StringBuilder("<i>\n");
            JSONArray keys = segmentIndex.names();
            for (int i = 0; i < keys.length(); i++) {
                JSONObject seg = segmentIndex.getJSONObject(keys.getString(i));
                String segUrl = "https://dm.video.qq.com/barrage/segment/" + vid + "/" + seg.optString("segment_name");
                try {
                    JSONObject segRes = new JSONObject(OkHttp.string(segUrl, headers));
                    JSONArray list = segRes.optJSONArray("barrage_list");
                    if (list != null) {
                        for (int j = 0; j < list.length(); j++) {
                            JSONObject item = list.getJSONObject(j);
                            String time = String.format("%.2f", item.optLong("time_offset") / 1000.0);
                            String content = item.optString("content");
                            if (content.isEmpty()) continue;
                            int mode = 1;
                            int color = 16777215;
                            String fontSize = "24";
                            try {
                                JSONObject style = new JSONObject(item.optString("content_style", "{}"));
                                int position = style.optInt("position", 1);
                                mode = position == 2 ? 5 : position == 3 ? 4 : 1;
                                String colorHex = style.optString("color", "#FFFFFF");
                                if (style.has("gradient_colors") && style.getJSONArray("gradient_colors").length() > 0) {
                                    colorHex = style.getJSONArray("gradient_colors").getString(0);
                                }
                                try {
                                    color = (int) Long.parseLong(colorHex.replace("#", ""), 16);
                                } catch (Exception ignored) {
                                }
                                String raw = style.optString("font_size", "");
                                if (raw.isEmpty()) raw = item.optString("font_size", "24");
                                fontSize = raw.replace("px", "");
                            } catch (Exception ignored) {
                            }
                            String safe = content.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                            xml.append("    <d p=\"").append(time).append(",").append(mode).append(",")
                                    .append(fontSize).append(",").append(color).append("\">")
                                    .append(safe).append("</d>\n");
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            xml.append("</i>");
            return xml.toString();
        } catch (Exception e) {
            return "<i></i>";
        }
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        String[][] cs = {{"100113", "剧集"}, {"100173", "电影"}, {"100119", "动漫"}, {"100109", "综艺"},
                {"100105", "纪录"}, {"100150", "少儿"}, {"110755", "短剧"}};
        for (String[] c : cs) {
            classes.put(new JSONObject().put("type_id", c[0]).put("type_name", c[1]));
        }
        return new JSONObject().put("class", classes).put("filters", new JSONObject(FILTER_JSON)).toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        JSONObject body = new JSONObject()
                .put("page_params", new JSONObject()
                        .put("page_id", "100101").put("page_type", "channel")
                        .put("skip_privacy_types", "0").put("support_click_scan", "1").put("new_mark_label_enabled", "1"))
                .put("page_bypass_params", new JSONObject()
                        .put("params", new JSONObject()
                                .put("caller_id", "3000010").put("data_mode", "default")
                                .put("page_id", "100101").put("page_type", "channel")
                                .put("platform_id", "2").put("user_mode", "default"))
                        .put("scene", "channel").put("abtest_bypass_id", "bf94ad015ab2bbaf"));
        JSONObject html = request(HOST + "/trpc.vector_layout.page_view.PageService/getPage?video_appid=3000010", body);
        JSONArray videos = new JSONArray();
        JSONArray cards = html.getJSONObject("data").getJSONArray("CardList").getJSONObject(0)
                .getJSONObject("children_list").getJSONObject("list").getJSONArray("cards");
        for (int i = 0; i < cards.length(); i++) {
            JSONObject params = cards.getJSONObject(i).optJSONObject("params");
            if (params == null || params.optString("cid").isEmpty() || params.optString("title").isEmpty()) continue;
            videos.put(new JSONObject()
                    .put("vod_id", params.optString("cid"))
                    .put("vod_name", params.optString("title"))
                    .put("vod_pic", params.optString("image_url_vertical"))
                    .put("vod_remarks", params.optString("stitle_pc"))
                    .put("vod_year", ""));
        }
        return new JSONObject().put("list", videos).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String sort = extend != null && extend.containsKey("sort") ? extend.get("sort") : "75";
        String area = extend != null && extend.containsKey("area") ? extend.get("area") : "";
        int page = 1;
        try {
            page = Integer.parseInt(pg);
        } catch (Exception ignored) {
        }
        JSONObject body = new JSONObject()
                .put("page_params", new JSONObject()
                        .put("channel_id", tid)
                        .put("filter_params", "sort=" + sort + "&itype=-1&ipay=-1&iarea=" + area + "&iyear=-1&producer=-1&characteristic=-1")
                        .put("page_type", "operation").put("page_id", "channel_list"))
                .put("page_context", new JSONObject()
                        .put("_ctrl_page_index", String.valueOf(page - 1))
                        .put("_ctrl_showed_module_num", String.valueOf(page - 1))
                        .put("_ds_cli_6970df954e7a9803_poster_offset", String.valueOf((page - 1) * 12))
                        .put("_ds_cli_6970df954e7a9803_poster_size", "12")
                        .put("_merger_mod_cnt", String.valueOf(page - 1))
                        .put("page_index", String.valueOf(page - 1))
                        .put("video_un_page_index", String.valueOf(page - 1)));
        JSONObject html = request(HOST + "/trpc.multi_vector_layout.mvl_controller.MVLPageHTTPService/getMVLPage?&vversion_platform=2", body);
        JSONArray videos = new JSONArray();
        JSONArray cards = html.getJSONObject("data").getJSONObject("modules").getJSONObject("normal").getJSONArray("cards");
        for (int i = 0; i < cards.length(); i++) {
            JSONArray poster = cards.getJSONObject(i).getJSONObject("children_list").getJSONObject("poster_card").getJSONArray("cards");
            for (int j = 0; j < poster.length(); j++) {
                JSONObject params = poster.getJSONObject(j).optJSONObject("params");
                if (params == null) continue;
                if (params.optString("cid").isEmpty() && params.optString("title").isEmpty()) continue;
                videos.put(new JSONObject()
                        .put("vod_id", params.optString("cid"))
                        .put("vod_name", params.optString("title"))
                        .put("vod_pic", params.optString("new_pic_vt"))
                        .put("vod_remarks", params.optString("timelong"))
                        .put("vod_year", params.optString("year")));
            }
        }
        return new JSONObject().put("page", Integer.parseInt(pg)).put("pagecount", 99999).put("limit", videos.length())
                .put("total", 99999).put("list", videos).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String id = ids.get(0);
        JSONObject body = new JSONObject().put("page_params", new JSONObject()
                .put("page_id", "vsite_episode_list").put("page_type", "detail_operation")
                .put("page_size", "100").put("cid", id).put("page_num", "0"));
        JSONObject html = request(HOST + "/trpc.universal_backend_service.page_server_rpc.PageServer/GetPageData?video_appid=3000010&vplatform=2&vversion_name=8.2.96", body);
        JSONArray playUrl = getlist(html);
        try {
            String tabs = html.getJSONObject("data").getJSONArray("module_list_datas").getJSONObject(0)
                    .getJSONArray("module_datas").getJSONObject(0).getJSONObject("module_params").optString("tabs");
            if (!tabs.isEmpty()) {
                JSONArray pp = new JSONArray(tabs);
                JSONObject last = pp.getJSONObject(pp.length() - 1);
                int allPage = (int) Math.ceil(last.optInt("end_text") / 100.0) - 1;
                for (int p = 1; p <= allPage; p++) {
                    JSONObject bodys = new JSONObject().put("page_params", new JSONObject()
                            .put("page_id", "vsite_episode_list").put("page_type", "detail_operation")
                            .put("page_size", "100").put("cid", id).put("page_num", String.valueOf(p)));
                    JSONObject pageRes = request(HOST + "/trpc.universal_backend_service.page_server_rpc.PageServer/GetPageData?video_appid=3000010&vplatform=2&vversion_name=8.2.96", bodys);
                    JSONArray gl = getlist(pageRes);
                    for (int i = 0; i < gl.length(); i++) playUrl.put(gl.getString(i));
                }
            }
        } catch (Exception ignored) {
        }
        JSONObject xqbody = new JSONObject().put("page_params", new JSONObject()
                .put("req_from", "web").put("cid", id).put("vid", "").put("lid", "")
                .put("page_type", "detail_operation").put("page_id", "detail_page_introduction"))
                .put("has_cache", 1);
        JSONObject res = request(HOST + "/trpc.universal_backend_service.page_server_rpc.PageServer/GetPageData?video_appid=3000010&vversion_name=8.2.98&vversion_platform=2", xqbody);
        JSONObject xq = res.getJSONObject("data").getJSONArray("module_list_datas").getJSONObject(0)
                .getJSONArray("module_datas").getJSONObject(0).getJSONObject("item_data_lists")
                .getJSONArray("item_datas").getJSONObject(0).getJSONObject("item_params");
        JSONObject vod = new JSONObject();
        vod.put("vod_id", id);
        vod.put("vod_name", xq.optString("title"));
        vod.put("vod_pic", xq.optString("image_url_vertical"));
        vod.put("type_name", xq.optString("main_genres"));
        vod.put("vod_year", xq.optString("year"));
        vod.put("vod_area", xq.optString("area_name"));
        vod.put("vod_remarks", xq.optString("detail_info").replaceAll("<[^>]*>| · \\d+| ", ""));
        vod.put("vod_content", xq.optString("cover_description"));
        vod.put("vod_play_from", "正片");
        StringBuilder playUrlStr = new StringBuilder();
        for (int i = 0; i < playUrl.length(); i++) {
            if (i > 0) playUrlStr.append("#");
            playUrlStr.append(playUrl.getString(i));
        }
        vod.put("vod_play_url", playUrlStr.toString());
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String danmaku = Proxy.localProxyUrl() + "?do=tencent&url=" + URLEncoder.encode(id, "UTF-8");
        try {
            JSONObject jx7 = new JSONObject(OkHttp.string("http://103.236.55.163:6565/api/index?parsesId=4&appid=10000&videoUrl=" + URLEncoder.encode(id, "UTF-8")));
            if ("200".equals(jx7.optString("code"))) {
                return new JSONObject().put("parse", 0).put("url", yulu(jx7.optString("url"))).put("danmaku", danmaku).toString();
            }
        } catch (Exception ignored) {
        }
        try {
            Map<String, String> jxh = new HashMap<>();
            jxh.put("token", "fdfb1079-e6e2-46a4-ac6d-f88778faf455");
            jxh.put("User-Agent", "okhttp/4.12.0");
            JSONObject jx1 = new JSONObject(OkHttp.string(
                    "http://zz2.mftv.top/api/index?parsesId=3&appid=10004&videoUrl=" + URLEncoder.encode(id, "UTF-8"), jxh));
            if ("200".equals(jx1.optString("code"))) {
                return new JSONObject().put("parse", 0).put("url", changying(jx1.optString("url"))).put("danmaku", danmaku).toString();
            }
        } catch (Exception ignored) {
        }
        return new JSONObject().put("parse", 1).put("url", id).put("flag", "腾讯").put("danmaku", danmaku).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject body = new JSONObject()
                .put("version", "24060601").put("clientType", 1)
                .put("filterValue", "firstTabid=150").put("retry", 0)
                .put("query", key).put("pagenum", 0).put("pagesize", 14)
                .put("queryFrom", 4).put("searchDatakey", "").put("isneedQc", true)
                .put("adClientInfo", "").put("extraInfo", new JSONObject().put("isNewMarkLabel", "1"));
        JSONObject html = request(HOST + "/trpc.videosearch.mobile_search.HttpMobileRecall/MbSearchHttp?vplatform=5", body);
        JSONArray videos = new JSONArray();
        JSONArray itemList = html.getJSONObject("data").getJSONObject("normalList").getJSONArray("itemList");
        Pattern p = Pattern.compile("\"4\":\\s*{\"info\":.*?\"text\":\"([^\"]*)\"");
        for (int i = 0; i < itemList.length(); i++) {
            JSONObject item = itemList.getJSONObject(i);
            JSONObject doc = item.getJSONObject("doc");
            JSONObject vi = item.getJSONObject("videoInfo");
            String remarks = "";
            Matcher m = p.matcher(vi.optString("imgTag"));
            if (m.find()) remarks = m.group(1);
            videos.put(new JSONObject()
                    .put("vod_id", doc.optString("id"))
                    .put("vod_name", vi.optString("title").replaceAll("<em>|</em>", ""))
                    .put("vod_pic", vi.optString("imgUrl"))
                    .put("vod_remarks", remarks)
                    .put("vod_year", vi.optString("year")));
        }
        return new JSONObject().put("limit", videos.length()).put("list", videos).toString();
    }
}
