package com.androidx.net.ok;

import com.androidx.app.CoreApplication;
import com.androidx.content.DataStorage;
import com.androidx.json.Json;
import com.androidx.util.Size;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Relin
 * on 2018-09-25.
 */
public class OkCookieJar implements Serializable, CookieJar {


    private List<Map<String, String>> cookieList = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        save(httpUrl, list);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        return load(httpUrl);
    }

    /**
     * 加载Cookie
     *
     * @param httpUrl http请求数据
     * @param list    cookie数据
     */
    private void save(HttpUrl httpUrl, List<Cookie> list) {
        int size = list == null ? 0 : list.size();
        for (int i = 0; i < size; i++) {
            Cookie cookie = list.get(i);
            Map<String, String> cookieMap = new HashMap<>();
            cookieMap.put("host", httpUrl.host());
            cookieMap.put("name", cookie.name());
            cookieMap.put("value", cookie.value());
            cookieMap.put("expiresAt", String.valueOf(cookie.expiresAt()));
            cookieMap.put("domain", cookie.domain());
            cookieMap.put("path", cookie.path());
            cookieMap.put("secure", cookie.secure() ? "1" : "0");
            cookieMap.put("httpOnly", cookie.httpOnly() ? "1" : "0");
            cookieMap.put("hostOnly", cookie.hostOnly() ? "1" : "0");
            cookieMap.put("persistent", cookie.persistent() ? "1" : "0");
            cookieList.add(cookieMap);
        }
        String cookieJson = Json.parseMapList(cookieList);
        DataStorage.with(CoreApplication.app).put("OK_" + httpUrl.host(), cookieJson);
    }

    /**
     * 保存Cookie
     *
     * @param httpUrl http请求数据
     * @return 缓存的Cookie数据
     */
    private List<Cookie> load(HttpUrl httpUrl) {
        String requestHost = httpUrl.host();
        String cache = DataStorage.with(CoreApplication.app).getString("OK_" + requestHost, "[]");
        List<Map<String, String>> list = Json.parseJSONArray(cache);
        List<Cookie> cookieList = new ArrayList<>();
        for (int i = 0; i < Size.of(list); i++) {
            Map<String, String> cookieMap = list.get(i);
            String host = cookieMap.get("host");
            String name = cookieMap.get("name");
            String value = cookieMap.get("value");
            String domain = cookieMap.get("domain");
            String path = cookieMap.get("path");
            if (cookieMap.get("expiresAt") != null) {
                long expiresAt = Long.parseLong(cookieMap.get("expiresAt"));
                if (expiresAt > System.currentTimeMillis() && requestHost.equals(host)) {
                    Cookie cookie = new Cookie.Builder()
                            .name(name)
                            .value(value)
                            .expiresAt(expiresAt)
                            .domain(domain)
                            .path(path)
                            .secure()
                            .httpOnly()
                            .build();
                    cookieList.add(cookie);
                }
            }
        }
        if (Size.of(cookieList) == 0) {
            save(httpUrl, cookieList);
        }
        return cookieList;
    }

    /**
     * 获取Cookie数据
     *
     * @param hostKey
     * @return
     */
    public static List<Cookie> getCookies(String hostKey) {
        String cache = DataStorage.with(CoreApplication.app).getString("OK_" + hostKey, "[]");
        List<Map<String, String>> list = Json.parseJSONArray(cache);
        List<Cookie> cookieList = new ArrayList<>();
        for (int i = 0; i < Size.of(list); i++) {
            Map<String, String> cookieMap = list.get(i);
            String host = cookieMap.get("host");
            String name = cookieMap.get("name");
            String value = cookieMap.get("value");
            long expiresAt = Long.parseLong(cookieMap.get("expiresAt"));
            String domain = cookieMap.get("domain");
            String path = cookieMap.get("path");
            if (expiresAt > System.currentTimeMillis()) {
                Cookie cookie = new Cookie.Builder()
                        .name(name)
                        .value(value)
                        .expiresAt(expiresAt)
                        .domain(domain)
                        .path(path)
                        .secure()
                        .httpOnly()
                        .build();
                cookieList.add(cookie);
            }
        }
        return cookieList;
    }

    /**
     * 删除Cookie缓存
     *
     * @param host
     */
    public static void removeCookie(String host) {
        DataStorage.with(CoreApplication.app).put("OK_" + host, "[]");
    }

}
