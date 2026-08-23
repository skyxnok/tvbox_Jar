package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeMaDuanJu extends Spider {

    private static final String SITE = "https://www.kuaikaw.cn";
    private static final String[][] CATE = {
            {"甜宠", "462"}, {"古装仙侠", "1102"}, {"现代言情", "1145"}, {"青春", "1170"}, {"豪门恩怨", "585"},
            {"逆袭", "417-464"}, {"重生", "439-465"}, {"系统", "1159"}, {"总裁", "1147"}, {"职场商战", "943"}
    };

    private Map<String, String> headers() {
        Map<String, String> h = new HashMap<>();
        h.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0");
        h.put("Referer", SITE);
        h.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8");
        return h;
    }

    private String fetch(String url) {
        try {
            return OkHttp.string(url, headers());
        } catch (Exception e) {
            return "";
        }
    }

    private JSONObject nextData(String html) {
        Matcher m = Pattern.compile("<script id=\"__NEXT_DATA__\"[^>]*>(.*?)</script>", Pattern.DOTALL).matcher(html);
        if (!m.find()) return new JSONObject();
        try {
            return new JSONObject(m.group(1));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private JSONObject pageProps(String html) throws Exception {
        JSONObject nd = nextData(html);
        return nd.optJSONObject("props") == null ? new JSONObject() : nd.getJSONObject("props").optJSONObject("pageProps");
    }

    private JSONObject bookItem(JSONObject book) throws Exception {
        String remarks = (book.optString("statusDesc") + " " + book.optString("totalChapterNum") + "集").trim();
        return new JSONObject()
                .put("vod_id", "/drama/" + book.optString("bookId"))
                .put("vod_name", book.optString("bookName"))
                .put("vod_pic", book.optString("coverWap"))
                .put("vod_remarks", remarks);
    }

    private String directVideoUrl(JSONObject chapter) throws Exception {
        JSONObject vi = chapter.optJSONObject("chapterVideoVo");
        if (vi == null) return null;
        String[] keys = {"mp4", "mp4720p", "vodMp4Url"};
        for (String k : keys) {
            String v = vi.optString(k);
            if (!v.isEmpty() && v.toLowerCase().contains(".mp4")) return v;
        }
        return null;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        JSONArray classes = new JSONArray();
        for (String[] c : CATE) {
            classes.put(new JSONObject().put("type_name", c[0]).put("type_id", c[1]));
        }
        JSONObject result = new JSONObject();
        result.put("class", classes);
        result.put("list", new JSONObject(homeVideoContent()).optJSONArray("list"));
        return result.toString();
    }

    @Override
    public String homeVideoContent() throws Exception {
        JSONArray videos = new JSONArray();
        JSONObject pp = pageProps(fetch(SITE));
        JSONArray banner = pp.optJSONArray("bannerList");
        if (banner != null) {
            for (int i = 0; i < banner.length(); i++) {
                JSONObject b = banner.getJSONObject(i);
                if (!b.optString("bookId").isEmpty()) videos.put(bookItem(b));
            }
        }
        JSONArray seo = pp.optJSONArray("seoColumnVos");
        if (seo != null) {
            for (int i = 0; i < seo.length(); i++) {
                JSONArray infos = seo.getJSONObject(i).optJSONArray("bookInfos");
                if (infos == null) continue;
                for (int j = 0; j < infos.length(); j++) {
                    JSONObject book = infos.getJSONObject(j);
                    if (!book.optString("bookId").isEmpty()) videos.put(bookItem(book));
                }
            }
        }
        Set<String> seen = new HashSet<>();
        JSONArray unique = new JSONArray();
        for (int i = 0; i < videos.length(); i++) {
            JSONObject v = videos.getJSONObject(i);
            String key = v.optString("vod_id") + "|" + v.optString("vod_name");
            if (seen.add(key)) unique.put(v);
        }
        return new JSONObject().put("list", unique).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        JSONObject pp = pageProps(fetch(SITE + "/browse/" + tid + "/" + pg));
        JSONArray bookList = pp.optJSONArray("bookList");
        JSONArray videos = new JSONArray();
        if (bookList != null) {
            for (int i = 0; i < bookList.length(); i++) {
                JSONObject book = bookList.getJSONObject(i);
                if (!book.optString("bookId").isEmpty()) videos.put(bookItem(book));
            }
        }
        int current = pp.optInt("page", 1);
        int total = pp.optInt("pages", 1);
        return new JSONObject().put("list", videos).put("page", current).put("pagecount", total)
                .put("limit", videos.length()).put("total", videos.length() * total).toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);
        if (!vodId.startsWith("/drama/")) vodId = "/drama/" + vodId;
        JSONObject pp = pageProps(fetch(SITE + vodId));
        JSONObject bookInfo = pp.optJSONObject("bookInfoVo");
        JSONArray chapterList = pp.optJSONArray("chapterList");
        if (bookInfo == null || bookInfo.optString("bookId").isEmpty() || chapterList == null) {
            return new JSONObject().put("list", new JSONArray()).toString();
        }
        JSONArray categories = bookInfo.optJSONArray("categoryList");
        JSONArray performers = bookInfo.optJSONArray("performerList");
        StringBuilder typeName = new StringBuilder();
        StringBuilder actor = new StringBuilder();
        if (categories != null) {
            for (int i = 0; i < categories.length(); i++) {
                if (i > 0) typeName.append(",");
                typeName.append(categories.getJSONObject(i).optString("name"));
            }
        }
        if (performers != null) {
            for (int i = 0; i < performers.length(); i++) {
                if (i > 0) actor.append(", ");
                actor.append(performers.getJSONObject(i).optString("name"));
            }
        }
        StringBuilder episodes = new StringBuilder();
        for (int i = 0; i < chapterList.length(); i++) {
            JSONObject chapter = chapterList.getJSONObject(i);
            String chapterId = chapter.optString("chapterId");
            String chapterName = chapter.optString("chapterName");
            if (chapterId.isEmpty() || chapterName.isEmpty()) continue;
            if (i > 0) episodes.append("#");
            String direct = directVideoUrl(chapter);
            if (direct != null) {
                episodes.append(chapterName).append("$").append(direct);
            } else {
                episodes.append(chapterName).append("$").append(vodId).append("$").append(chapterId).append("$").append(chapterName);
            }
        }
        JSONObject vod = new JSONObject();
        vod.put("vod_id", vodId);
        vod.put("vod_name", bookInfo.optString("title"));
        vod.put("vod_pic", bookInfo.optString("coverWap"));
        vod.put("type_name", typeName.toString());
        vod.put("vod_year", "");
        vod.put("vod_area", bookInfo.optString("countryName"));
        vod.put("vod_remarks", (bookInfo.optString("statusDesc") + " " + bookInfo.optString("totalChapterNum") + "集").trim());
        vod.put("vod_actor", actor.toString());
        vod.put("vod_director", "");
        vod.put("vod_content", bookInfo.optString("introduction"));
        if (episodes.length() > 0) {
            vod.put("vod_play_from", "河马剧场");
            vod.put("vod_play_url", episodes.toString());
        }
        return new JSONObject().put("list", new JSONArray().put(vod)).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("url", id);
        result.put("header", new JSONObject(headers()).toString());
        if (id.contains("http") && (id.contains(".mp4") || id.contains(".m3u8"))) {
            return result.toString();
        }
        String[] parts = id.split("\\$");
        if (parts.length < 2) return result.toString();
        String dramaId = parts[0].replace("/drama/", "");
        String chapterId = parts[1];
        String videoUrl = getEpisodeVideoUrl(dramaId, chapterId);
        if (videoUrl != null) result.put("url", videoUrl);
        return result.toString();
    }

    private String getEpisodeVideoUrl(String dramaId, String chapterId) {
        try {
            String html = fetch(SITE + "/episode/" + dramaId + "/" + chapterId);
            JSONObject pp = pageProps(html);
            JSONObject chapterInfo = pp.optJSONObject("chapterInfo");
            if (chapterInfo != null && chapterInfo.has("chapterVideoVo")) {
                String direct = directVideoUrl(chapterInfo);
                if (direct != null) return direct;
            }
            Matcher m = Pattern.compile("(https?://[^\"']+\\.mp4)").matcher(html);
            String first = null;
            while (m.find()) {
                String url = m.group(1);
                if (first == null) first = url;
                if (url.contains(chapterId) || url.contains(dramaId)) return url;
            }
            return first;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContentPage(key, quick, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        return searchContentPage(key, quick, pg);
    }

    private String searchContentPage(String key, boolean quick, String pg) throws Exception {
        JSONObject pp = pageProps(fetch(SITE + "/search?searchValue=" + URLEncoder.encode(key, "UTF-8") + "&page=" + pg));
        JSONArray bookList = pp.optJSONArray("bookList");
        JSONArray videos = new JSONArray();
        if (bookList != null) {
            for (int i = 0; i < bookList.length(); i++) {
                JSONObject book = bookList.getJSONObject(i);
                if (!book.optString("bookId").isEmpty()) videos.put(bookItem(book));
            }
        }
        int total = pp.optInt("pages", 1);
        return new JSONObject().put("list", videos).put("page", pg).put("pagecount", total)
                .put("limit", videos.length()).put("total", videos.length() * total).toString();
    }
}
