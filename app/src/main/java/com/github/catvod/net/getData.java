package com.github.catvod.net;

import okhttp3.Headers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class getData {
    public static String UA="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36";
    public static String req(String url, Map<String, String> header) {
        return OkHttp.string(url, header);
    }
    public static String req(String url, String key) throws IOException {
        return OkHttp.newCall(url).headers().get(key);
    }
    public static String req(String url) throws IOException {
        return OkHttp.string(url);
    }
    public static  Map<String, String> getHeader(String url) {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", UA);
        header.put("HOST", url);
        return header;
    }
}
