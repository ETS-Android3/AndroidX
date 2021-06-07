package com.androidx.net;

import android.text.TextUtils;

import com.androidx.app.CoreApplication;
import com.androidx.io.Mime;
import com.androidx.json.Json;
import com.androidx.text.Null;
import com.androidx.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;

/**
 * Created by Relin
 * on 2018-09-10.
 */
public class OkHttp implements Request {

    /**
     * 日志参数
     */
    public static String TAG = OkHttp.class.getSimpleName();
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;
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
     * 客户端
     */
    private OkHttpClient okHttpClient;


    public OkHttp() {
        options = Http.options();
        if (options != null) {
            handler = options.handler();
            threadPool = options.threadPool();
        }
        okHttpClient = buildOkHttpClient(options);
    }

    @Override
    public void cancel(Class tag) {
        if (okHttpClient != null) {
            List<Call> calls = okHttpClient.dispatcher().runningCalls();
            for (Call call : calls) {
                String prefix = tag.getSimpleName();
                String requestTag = (String) call.request().tag();
                if (requestTag.startsWith(prefix)) {
                    call.cancel();
                }
            }
        }
    }

    /**
     * Get请求方式
     *
     * @param url
     * @param params
     */
    @Override
    public void get(final String url, final RequestParams params, final OnHttpListener listener) {
        if (!ResponseHelper.isPassNetworkAndCacheCheck(handler, options, url, params, listener)) {
            return;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String getUrl = url;
                StringBuffer requestUrl = new StringBuffer();
                requestUrl.append(url);
                if (params != null && params.params() != null) {
                    requestUrl.append("?");
                    TreeMap<String, String> stringParams = params.params();
                    for (String key : stringParams.keySet()) {
                        String value = stringParams.get(key);
                        requestUrl.append(key);
                        requestUrl.append("=");
                        requestUrl.append(value);
                        requestUrl.append("&");
                    }
                    getUrl = requestUrl.toString().substring(0, requestUrl.lastIndexOf("&"));
                }
                //创建一个请求
                okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
                requestBuilder.addHeader("Connection", "close");
                //添加头文件
                if (params != null && params.header() != null) {
                    String agent = params.header().get(Header.USER_AGENT);
                    requestBuilder.addHeader(Header.USER_AGENT, agent);
                    TreeMap<String, String> headerParams = params.header();
                    for (String key : headerParams.keySet()) {
                        String value = headerParams.get(key);
                        requestBuilder.addHeader(key, value);
                    }
                }
                requestBuilder.url(getUrl);
                //如果没有网络就使用缓存
                if (!Network.isAvailable(CoreApplication.app)) {
                    requestBuilder.cacheControl(CacheControl.FORCE_CACHE);
                } else {
                    requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                }
                String tag = params == null ? url + "" : params.tag();
                okhttp3.Request request = requestBuilder.tag(TextUtils.isEmpty(tag) ? url : tag).build();
                //请求加入调度
                Call call = okHttpClient.newCall(request);
                call.enqueue(new OnOkHttpListener(handler, params, url, listener));
            }
        });
    }

    /**
     * Post请求方式
     *
     * @param url    地址
     * @param params 参数
     */
    @Override
    public void post(final String url, final RequestParams params, final OnHttpListener listener) {
        request(POST, url, params, listener);
    }

    /**
     * Put请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 回调
     */
    @Override
    public void put(String url, RequestParams params, OnHttpListener listener) {
        request(PUT, url, params, listener);
    }

    /**
     * Delete请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 回调
     */
    @Override
    public void delete(String url, RequestParams params, OnHttpListener listener) {
        request(DELETE, url, params, listener);
    }

    /**
     * 请求数据
     *
     * @param method   方法
     * @param url      接口
     * @param params   参数
     * @param listener 回调
     */
    public void request(final int method, final String url, final RequestParams params, final OnHttpListener listener) {
        if (!ResponseHelper.isPassNetworkAndCacheCheck(handler, options, url, params, listener)) {
            return;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String contentType = params.header().get(Header.CONTENT_TYPE);
                if (contentType.equals(Header.CONTENT_JSON) || contentType.equals(Header.CONTENT_STRING)) {
                    jsonRequest(method, url, params, listener);
                } else {
                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                    multipartBodyBuilder.setType(MultipartBody.FORM);
                    multipartBodyBuilder.addFormDataPart("", "");
                    //一般参数
                    if (params != null && params.params() != null) {
                        TreeMap<String, String> stringParams = params.params();
                        for (String key : stringParams.keySet()) {
                            String value = stringParams.get(key);
                            multipartBodyBuilder.addFormDataPart(key, value);
                        }
                    }
                    //文件参数
                    if (params != null && params.files() != null) {
                        TreeMap<String, File> fileParams = params.files();
                        //设置文件处理类型
                        for (String key : fileParams.keySet()) {
                            File file = fileParams.get(key);
                            String fileName = file.getName();
                            MediaType type = MediaType.parse(Mime.value(file));
                            RequestBody fileBody = RequestBody.create(type, file);
                            multipartBodyBuilder.addFormDataPart(key, fileName, fileBody);
                        }
                    }
                    //生成请求体
                    RequestBody requestBody = multipartBodyBuilder.build();
                    okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
                    //添加Header
                    requestBuilder.addHeader("Connection", "close");
                    if (params != null && params.header() != null) {
                        String agent = params.header().get(Header.USER_AGENT);
                        requestBuilder.addHeader(Header.USER_AGENT, TextUtils.isEmpty(agent) ? "Android" : agent);
                        TreeMap<String, String> headerParams = params.header();
                        for (String key : headerParams.keySet()) {
                            requestBuilder.addHeader(key, headerParams.get(key));
                        }
                    }
                    requestBuilder.url(url);
                    requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
                    requestBuilder.post(requestBody);//传参数、文件或者混合
                    String tag = params == null ? url + "" : params.tag();
                    okhttp3.Request request = requestBuilder.tag(TextUtils.isEmpty(tag) ? url : tag).build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new OnOkHttpListener(handler, params, url, listener));
                }
            }
        });
    }

    /**
     * Json内容请求
     *
     * @param method
     * @param url
     * @param params
     * @param listener
     */
    private void jsonRequest(int method, String url, RequestParams params, OnHttpListener listener) {
        String contentType = params.header().get(Header.CONTENT_TYPE);
        MediaType mediaType = MediaType.parse(Header.MEDIA_JSON);
        if (contentType.equals(Header.CONTENT_JSON)) {
            mediaType = MediaType.parse(Header.MEDIA_JSON);
        }
        if (contentType.equals(Header.CONTENT_STRING)) {
            mediaType = MediaType.parse(Header.MEDIA_STREAM);
        }
        String stringParams;
        String jsonContent = params.body();
        if (Null.isNull(jsonContent)) {
            stringParams = Json.parseMap(params.params());
        } else {
            stringParams = jsonContent;
        }
        stringParams = options.escapeJar().escape(stringParams);
        RequestBody body = RequestBody.create(mediaType, stringParams);
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        //添加Header
        if (params != null && params.header() != null) {
            String agent = params.header().get(Header.USER_AGENT);
            requestBuilder.addHeader(Header.USER_AGENT, TextUtils.isEmpty(agent) ? "Android" : agent);
            TreeMap<String, String> headerParams = params.header();
            for (String key : headerParams.keySet()) {
                requestBuilder.addHeader(key, headerParams.get(key));
            }
        }
        String tag = params.tag();
        okhttp3.Request request = requestBuilder.url(url).post(body).tag(TextUtils.isEmpty(tag) ? url : tag).build();
        if (method == POST) {
            request = requestBuilder.url(url).post(body).tag(TextUtils.isEmpty(tag) ? url : tag).build();
        }
        if (method == PUT) {
            request = requestBuilder.url(url).put(body).tag(TextUtils.isEmpty(tag) ? url : tag).build();
        }
        if (method == DELETE) {
            request = requestBuilder.url(url).delete(body).tag(TextUtils.isEmpty(tag) ? url : tag).build();
        }
        Call call = okHttpClient.newCall(request);
        call.enqueue(new OnOkHttpListener(handler, params, url, listener));
    }

    /**
     * 创建Http客户端对象
     *
     * @return OkHttpClient
     */
    private OkHttpClient buildOkHttpClient(RequestOptions options) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
        okBuilder.connectTimeout(options.connectTimeOut(), TimeUnit.SECONDS);
        okBuilder.readTimeout(options.readTimeOut(), TimeUnit.SECONDS);
        okBuilder.writeTimeout(options.writeTimeOut(), TimeUnit.SECONDS);
        okBuilder.connectionPool(new ConnectionPool(options.maxIdleConnections(), options.keepAliveDuration(), TimeUnit.SECONDS));
        okBuilder.cookieJar(new OkCookieJar());
        okBuilder.dispatcher(new Dispatcher());
        if (options.interceptor() != null) {
            okBuilder.addInterceptor(options.interceptor());
        }
        okBuilder.retryOnConnectionFailure(false);
        //Https证书配置
        okBuilder.sslSocketFactory(options.cert().getSSLSocketFactory(), new HttpsX509TrustManager());
        okBuilder.hostnameVerifier(new HttpsHostnameVerifier());
        return okBuilder.build();
    }

}
