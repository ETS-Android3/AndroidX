package com.androidx.net;

public class ResponseTable {

    /**
     * 请求码
     */
    private String code;
    /**
     * 请求url
     */
    private String url;
    /**
     * 参数
     */
    private String params;
    /**
     * 结果
     */
    private String body;
    /**
     * 异常
     */
    private String exception;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
