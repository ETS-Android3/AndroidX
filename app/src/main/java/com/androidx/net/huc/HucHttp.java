package com.androidx.net.huc;

import android.text.TextUtils;

import com.androidx.io.Mime;
import com.androidx.json.Json;
import com.androidx.net.Header;
import com.androidx.net.Http;
import com.androidx.net.OnHttpListener;
import com.androidx.net.Request;
import com.androidx.net.RequestOptions;
import com.androidx.net.RequestParams;
import com.androidx.net.ResponseException;
import com.androidx.net.ResponseHandler;
import com.androidx.net.ResponseHelper;
import com.androidx.net.ssl.HttpsHostnameVerifier;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Relin
 * General purpose http
 * on 2018-09-10.
 */
public class HucHttp implements Request {

    /**
     * 编码格式
     */
    private static final String CHARSET = "utf-8";
    /**
     * 前缀
     */
    private static final String PREFIX = "--";
    /**
     * 换行
     */
    private static final String LINE_FEED = "\r\n";
    /**
     * 边界标识 随机生成
     */
    private static final String BOUNDARY = UUID.randomUUID().toString();
    /**
     * 地址对象
     */
    private static URL httpUrl;
    /**
     * Cookie对象
     */
    private static HucCookie cookie;

    /**
     * Http异步请求数据处理
     */
    protected ResponseHandler handler;
    /**
     * 连接池
     */
    protected ExecutorService threadPool;
    /**
     * 配置参数
     */
    protected RequestOptions options;
    /**
     * 数据返回
     */
    protected HashMap<String, Boolean> callback;


    public HucHttp() {
        super();
        callback = new HashMap<>();
        cookie = new HucCookie();
        options = Http.options();
        if (options != null) {
            handler = options.handler();
            threadPool = options.threadPool();
        }
    }


    /**
     * 创建服务器连接
     *
     * @param url           地址
     * @param requestMethod 请求方式 GET / POST
     * @param params        请求参数
     * @return
     */
    protected HttpURLConnection createHttpURLConnection(String url, String requestMethod, RequestParams params) {
        HttpURLConnection conn = null;
        try {
            httpUrl = new URL(url);
            boolean isHttps = url.startsWith("https");
            //https
            if (isHttps) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpUrl.openConnection();
                httpsURLConnection.setSSLSocketFactory(options.cert().getSSLSocketFactory());
                httpsURLConnection.setHostnameVerifier(new HttpsHostnameVerifier());
                conn = httpsURLConnection;
            } else {
                conn = (HttpURLConnection) httpUrl.openConnection();
            }
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod(requestMethod);
            conn.setReadTimeout((int) (options.readTimeOut() * 1000));
            conn.setConnectTimeout((int) (options.connectTimeOut() * 1000));
            conn.setDoInput(true);
            conn.setUseCaches(false);
            if (requestMethod.equals("POST")) {
                conn.setDoOutput(true);
            }
            //加载缓存的Cookie
            conn.setRequestProperty("Cookie", cookie.loadCookie(httpUrl));
            //设置请求头参数
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            String agent = params.header().get(Header.USER_AGENT);
            conn.setRequestProperty("User-Agent", TextUtils.isEmpty(agent) ? "Android" : agent);
            String contentType = params.header().get(Header.CONTENT_TYPE);
            if (contentType.equals(Header.CONTENT_FORM)) {
                conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + BOUNDARY);
            }
            if (contentType.equals(Header.CONTENT_JSON)) {
                conn.setRequestProperty("Content-Type", "application/json");
            }
            if (contentType.equals(Header.CONTENT_STRING)) {
                conn.setRequestProperty("Content-Type", "application/octet-stream" + ";boundary=" + BOUNDARY);
            }
            //添加头部
            if (params != null && params.header() != null) {
                for (Map.Entry<String, String> entry : params.header().entrySet()) {
                    conn.setRequestProperty("\"" + entry.getKey() + "\"", "\"" + entry.getValue() + "\"");
                }
            }
            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 创建Get请求url地址
     *
     * @param url    地址
     * @param params 请求参数
     * @return
     */
    protected String createGetUrl(String url, RequestParams params) {
        StringBuffer requestUrl = new StringBuffer();
        requestUrl.append(url);
        requestUrl.append("?");
        if (params != null && params.params() != null) {
            Map<String, String> stringParams = params.params();
            for (String key : stringParams.keySet()) {
                String value = stringParams.get(key);
                requestUrl.append(key);
                requestUrl.append("=");
                requestUrl.append(value);
                requestUrl.append("&");
            }
        }
        String combinationUrl = requestUrl.toString().substring(0, requestUrl.lastIndexOf("&"));
        return combinationUrl;
    }

    /**
     * 添加Post请求参数
     *
     * @param conn   服务器连接对象
     * @param params 参数
     */
    protected void addPostParams(HttpURLConnection conn, RequestParams params) {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(conn.getOutputStream());
            //文字参数
            if (params != null) {
                //单个数据字符 --- 一般支付上面需要
                if (params.params() != null) {
                    String contentType = params.header().get(Header.CONTENT_TYPE);
                    //表单数据
                    if (contentType.equals(Header.CONTENT_FORM)) {
                        Map<String, String> map = params.params();
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            sb.append(buildPostStringParams(entry.getKey(), entry.getValue()));
                        }
                        dos.writeBytes(sb.toString());
                    }
                    //纯字符参数
                    if (contentType.equals(Header.CONTENT_STRING)) {
                        if (params.body() != null) {
                            dos.writeBytes(params.body());
                        }
                    }
                    //json文字参数
                    if (contentType.equals(Header.CONTENT_JSON)) {
                        String stringParams = options.escapeJar().escape(Json.parseMap(params.params()));
                        dos.writeBytes(stringParams);
                    }
                }
            }
            //文件上传
            if (params != null && params.files() != null) {
                StringBuilder sb = new StringBuilder();
                for (TreeMap.Entry<String, File> fileEntry : params.files().entrySet()) {
                    sb.append(buildPostFileParams(fileEntry.getKey(), fileEntry.getValue().getName()));
                    dos.writeBytes(sb.toString());
                    dos.flush();
                    InputStream is = new FileInputStream(fileEntry.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        dos.write(buffer, 0, len);
                    }
                    is.close();
                    dos.writeBytes(LINE_FEED);
                }
            }
            //请求结束标志
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_FEED);
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求
     *
     * @param requestMethod
     * @param url
     * @param params
     * @param listener
     */
    protected void request(final String requestMethod, final String url, final RequestParams params, final OnHttpListener listener) {
        callback.put(params.tag(), true);
        if (!ResponseHelper.isPassNetworkAndCacheCheck(handler, options, url, params, listener)) {
            return;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                if (requestMethod.equals("GET")) {
                    conn = createHttpURLConnection(createGetUrl(url, params), requestMethod, params);
                }
                if (requestMethod.equals("POST")) {
                    conn = createHttpURLConnection(url, requestMethod, params);
                    addPostParams(conn, params);
                }
                int code = -1;
                try {
                    code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        //保存Cookie
                        cookie.saveCookie(httpUrl, conn, 60 * 1000 * 60 * 2);
                        //获取返回数据
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        if (callback.get(params.tag())) {
                            ResponseHelper.sendMessage(handler, ResponseHandler.WHAT_SUCCEED, url, params, code, new ResponseException(ResponseException.NO_EXP), sb.toString(), listener);
                        }
                    } else {
                        if (callback.get(params.tag())) {
                            ResponseHelper.sendMessage(handler, ResponseHandler.WHAT_FAILURE, url, params, code, new ResponseException(ResponseException.NOT_OK), ResponseHelper.createBody(code), listener);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback.get(params.tag())) {
                        ResponseHelper.sendMessage(handler, ResponseHandler.WHAT_FAILURE, url, params, code, new ResponseException(ResponseException.NOT_OK), ResponseHelper.createBody(code), listener);
                    }
                } finally {
                    conn.disconnect();
                }
            }
        });
    }

    @Override
    public void cancel(String tag) {
        if (callback != null) {
            callback.put(tag, false);
        }
    }

    /**
     * Get请求参数
     *
     * @param url      请求地址
     * @param params   参数
     * @param listener 监听
     */
    @Override
    public void get(String url, RequestParams params, OnHttpListener listener) {
        request("GET", url, params, listener);
    }

    /**
     * Post请求
     *
     * @param url      请求地址
     * @param params   参数
     * @param listener 监听
     */
    @Override
    public void post(String url, RequestParams params, OnHttpListener listener) {
        request("POST", url, params, listener);
    }

    /**
     * 创建Post请求文字参数
     *
     * @param key
     * @param value
     * @return
     */
    protected StringBuilder buildPostStringParams(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(BOUNDARY);
        sb.append(LINE_FEED);
        sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_FEED);
        sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_FEED);
        sb.append("Content-Transfer-Encoding: 8bit" + LINE_FEED);
        // 参数头设置完以后需要两个换行，然后才是参数内容
        sb.append(LINE_FEED);
        sb.append(value);
        sb.append(LINE_FEED);
        return sb;
    }

    /**
     * 创建Post请求的文件参数
     *
     * @param key
     * @param value
     * @return
     */
    protected StringBuilder buildPostFileParams(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(BOUNDARY);
        sb.append(LINE_FEED);
        sb.append("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + value + "\"" + LINE_FEED);
        //此处的ContentType不同于 请求头 中Content-Type
        sb.append("Content-Type: " + Mime.value(value) + LINE_FEED);
        sb.append("Content-Transfer-Encoding: 8bit" + LINE_FEED);
        // 参数头设置完以后需要两个换行，然后才是参数内容
        sb.append(LINE_FEED);
        return sb;
    }

}
