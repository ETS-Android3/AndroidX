package com.androidx.net;

import com.androidx.text.Null;
import com.androidx.text.Time;

import java.io.Serializable;

/**
 * Created by Relin on 2017/8/6.
 */

public class ResponseBody implements Serializable {

    /**
     * 页面
     */
    private String page;
    /**
     * 接口返回时间
     */
    private String time;
    /**
     * 是否是缓存
     */
    private boolean isCache;
    /**
     * 返回的数据
     */
    private String body;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 请求的结果code
     */
    private int code;
    /**
     * 请求参数
     */
    private RequestParams requestParams;
    /**
     * 文件流异常
     */
    private Exception exception;
    /**
     * 回调接口
     */
    private OnHttpListener httpListener;

    public String time() {
        if (Null.isNull(time)){
            return Time.now();
        }
        return time;
    }

    public String page() {
        return Null.value(page);
    }

    public void page(String page) {
        this.page = page;
    }

    public void time(String time) {
        this.time = time;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public String body() {
        return body;
    }

    public void body(String body) {
        this.body = body;
    }

    public String url() {
        return url;
    }

    public void url(String url) {
        this.url = url;
    }

    public Exception exception() {
        return exception;
    }

    public void exception(Exception exception) {
        this.exception = exception;
    }

    public OnHttpListener listener() {
        return httpListener;
    }

    public void listener(OnHttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public RequestParams requestParams() {
        return requestParams;
    }

    public void requestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
    }

    public int code() {
        return code;
    }

    public void code(int code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return "ResponseBody{" +
                "page='" + page + '\'' +
                ", time='" + time + '\'' +
                ", isCache=" + isCache +
                ", body='" + body + '\'' +
                ", url='" + url + '\'' +
                ", code=" + code +
                ", requestParams=" + requestParams +
                ", exception=" + exception +
                ", httpListener=" + httpListener +
                '}';
    }
}
