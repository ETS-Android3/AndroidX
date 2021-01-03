package com.androidx.net;

import android.content.Context;

import com.androidx.net.ssl.Certificate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: Relin
 * Describe:客户端请求参数
 * Date:2020/11/29 14:34
 */
public class RequestOptions {

    /**
     * OkHttp
     */
    public final static int OK_HTTP = 1;
    /**
     * HttpURLConnection
     */
    public final static int HUC_HTTP = 2;
    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 网络请求类
     */
    private int type = OK_HTTP;
    /**
     * 线程数量
     */
    private int threadNum = 10;
    /**
     * 连接池
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    /**
     * 网络Handler
     */
    private ResponseHandler handler = new ResponseHandler();
    /**
     * Header参数
     */
    private Header header = new Header();
    /**
     * 转义插件
     */
    private EscapeJar escapeJar = new JsonEscapeJar();
    /**
     * 请求消息
     */
    private ResponseException responseException;
    /**
     * Https证书
     */
    private Certificate cert = new Certificate.Builder().build();
    /**
     * 调试
     */
    private boolean debug;
    /**
     * 缓存
     */
    private boolean cache;
    /**
     * 链接超时
     */
    private long connectTimeOut = 60;
    /**
     * 读取超时
     */
    private long readTimeOut = 60;
    /**
     * 写入超时
     */
    private long writeTimeOut = 60;
    /**
     * 内容类型
     */
    private String contentType = Header.CONTENT_JSON;
    /**
     * 用户代理
     */
    private String userAgent = "Android";
    /**
     * 上传最大值，单位kb
     */
    private long uploadMaxSize = 1024;
    /**
     * 最大连接数
     */
    private int maxIdleConnections = 10;
    /**
     * 保活时常，单位秒
     */
    private long keepAliveDuration = 10;


    public RequestOptions(Context context) {
        this.context = context;
    }

    public Context context() {
        return context;
    }

    public int type() {
        return type;
    }

    public RequestOptions type(int type) {
        this.type = type;
        return this;
    }

    public int threadNum() {
        return threadNum;
    }

    public RequestOptions threadNum(int threadNum) {
        this.threadNum = threadNum;
        threadPool = Executors.newFixedThreadPool(threadNum);
        return this;
    }

    public ExecutorService threadPool() {
        return threadPool;
    }

    public RequestOptions threadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
        return this;
    }

    public ResponseHandler handler() {
        return handler;
    }

    public RequestOptions handler(ResponseHandler handler) {
        this.handler = handler;
        return this;
    }

    public Header header() {
        return header;
    }

    public RequestOptions header(Header header) {
        this.header = header;
        return this;
    }

    public EscapeJar escapeJar() {
        return escapeJar;
    }

    public RequestOptions escapeJar(EscapeJar escapeJar) {
        this.escapeJar = escapeJar;
        return this;
    }

    public ResponseException responseException() {
        return responseException;
    }

    public RequestOptions responseException(ResponseException responseException) {
        this.responseException = responseException;
        return this;
    }

    public Certificate cert() {
        return cert;
    }

    public RequestOptions cert(Certificate certificates) {
        this.cert = certificates;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public RequestOptions debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean isCache() {
        return cache;
    }

    public RequestOptions cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public long connectTimeOut() {
        return connectTimeOut;
    }

    public RequestOptions connectTimeOut(long connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return this;
    }

    public long readTimeOut() {
        return readTimeOut;
    }

    public RequestOptions readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public long writeTimeOut() {
        return writeTimeOut;
    }

    public RequestOptions writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public String contentType() {
        return contentType;
    }

    public RequestOptions contentType(String contentType) {
        this.contentType = contentType;
        header().add(Header.CONTENT_TYPE, contentType);
        return this;
    }

    public String userAgent() {
        return userAgent;
    }

    public RequestOptions userAgent(String userAgent) {
        this.userAgent = userAgent;
        header().add(Header.USER_AGENT, userAgent);
        return this;
    }

    public long uploadMaxSize() {
        return uploadMaxSize;
    }

    public RequestOptions uploadMaxSize(long uploadMaxSize) {
        this.uploadMaxSize = uploadMaxSize;
        return this;
    }

    public int maxIdleConnections() {
        return maxIdleConnections;
    }

    public RequestOptions maxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
        return this;
    }

    public long keepAliveDuration() {
        return keepAliveDuration;
    }

    public RequestOptions keepAliveDuration(long keepAliveDuration) {
        this.keepAliveDuration = keepAliveDuration;
        return this;
    }

    @Override
    public String toString() {
        return "RequestOptions{" +
                "context=" + context +
                ", type=" + type +
                ", threadNum=" + threadNum +
                ", threadPool=" + threadPool +
                ", handler=" + handler +
                ", header=" + header +
                ", escapeJar=" + escapeJar +
                ", responseException=" + responseException +
                ", cert=" + cert +
                ", debug=" + debug +
                ", cache=" + cache +
                ", connectTimeOut=" + connectTimeOut +
                ", readTimeOut=" + readTimeOut +
                ", writeTimeOut=" + writeTimeOut +
                ", contentType='" + contentType + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", uploadMaxSize=" + uploadMaxSize +
                ", maxIdleConnections=" + maxIdleConnections +
                ", keepAliveDuration=" + keepAliveDuration +
                '}';
    }
}
