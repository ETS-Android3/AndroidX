package com.androidx.net;

import android.os.Handler;
import android.os.Message;

import com.androidx.json.Json;
import com.androidx.text.Time;
import com.androidx.util.Encoder;
import com.androidx.util.Log;


/**
 * Created by Relin
 * on 2018-09-10.
 * Http异步处理类
 */
public class ResponseHandler extends Handler {

    /**
     * 网络请求失败的what
     */
    public static final int WHAT_FAILURE = 0xa01;
    /**
     * 网络请求成功的what
     */
    public static final int WHAT_SUCCEED = 0xb02;


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        ResponseBody responseBody = (ResponseBody) msg.obj;
        responseBody.time(Time.now());
        OnHttpListener listener = responseBody.listener();
        insertCache(responseBody);
        print(responseBody);
        switch (msg.what) {
            case WHAT_FAILURE:
                if (listener != null && responseBody != null && responseBody.body() != null) {
                    listener.onHttpFailure(responseBody);
                }
                break;
            case WHAT_SUCCEED:
                if (listener != null && responseBody != null && responseBody.body() != null) {
                    listener.onHttpSucceed(responseBody);
                }
                break;
        }
    }

    /**
     * 插入缓存
     *
     * @param responseBody 请求结果
     */
    private void insertCache(ResponseBody responseBody) {
        if (Http.options().isCache()) {
            String exception = responseBody.code() != ResponseCode.OK ? ResponseException.NOT_OK : "";
            ResponseTable cacheBody = new ResponseTable();
            cacheBody.setUrl(responseBody.url());
            cacheBody.setCode(String.valueOf(responseBody.code()));
            String encodeBody = Encoder.encode(responseBody.body());
            cacheBody.setBody(encodeBody);
            cacheBody.setException(exception);
            cacheBody.setParams(responseBody.requestParams() == null || responseBody.requestParams().params() == null ? "" : Encoder.encode(responseBody.requestParams().params().toString()));
            ResponseCache.insert(cacheBody);
        }
    }


    /**
     * 打印调试日志
     *
     * @param body
     */
    private void print(ResponseBody body) {
        if (Http.options().isDebug()) {
            StringBuffer sb = new StringBuffer("Program interface debug mode");
            sb.append(Log.NEW_LINE);
            sb.append(Log.HEAD_LINE);
            sb.append(Log.NEW_LINE);
            sb.append(Log.LEFT_LINE + body.url());
            sb.append(Log.NEW_LINE);
            sb.append(Log.MIDDLE_LINE);
            StringBuffer paramsBuffer = new StringBuffer("");
            StringBuffer headerBuffer = new StringBuffer("");
            if (body.requestParams().header() != null) {
                for (String key : body.requestParams().header().keySet()) {
                    headerBuffer.append(Log.LEFT_LINE);
                    headerBuffer.append("\"" + key + "\":" + "\"" + body.requestParams().header().get(key) + "\"");
                    headerBuffer.append(Log.NEW_LINE);
                }
            }
            if (headerBuffer.toString().length() != 0) {
                sb.append(Log.NEW_LINE);
                sb.append(headerBuffer);
                sb.append(Log.MIDDLE_LINE);
            }
            if (body.requestParams().params() != null) {
                for (String key : body.requestParams().params().keySet()) {
                    String value = body.requestParams().params().get(key);
                    if (!value.startsWith("{") && !value.startsWith("[")) {
                        value = "\"" + value + "\"";
                    }
                    paramsBuffer.append(Log.LEFT_LINE);
                    paramsBuffer.append("\"" + key + "\":" + value);
                    paramsBuffer.append(Log.NEW_LINE);
                }
            }
            if (body.requestParams().body() != null) {
                paramsBuffer.append(body.requestParams().body());
                paramsBuffer.append(",");
                paramsBuffer.append(Log.LEFT_LINE);
                paramsBuffer.append("\"" + body.requestParams().body() + "\"");
                paramsBuffer.append(Log.NEW_LINE);
            }
            if (body.requestParams().files() != null) {
                for (String key : body.requestParams().files().keySet()) {
                    paramsBuffer.append("│\"" + key + "\":" + "\"" + body.requestParams().files().get(key).getAbsolutePath() + "\"");
                    paramsBuffer.append(Log.NEW_LINE);
                }
            }
            if (paramsBuffer.toString().length() != 0) {
                sb.append(Log.NEW_LINE);
                sb.append(paramsBuffer);
                sb.append(Log.MIDDLE_LINE);
            }
            if (body != null) {
                sb.append(Log.NEW_LINE);
                sb.append(Log.LEFT_LINE);
                sb.append("\"" + "code:" + "\"" + body.code() + "\"");
                sb.append("\n");
                if (body.exception() != null) {
                    sb.append(Log.LEFT_LINE);
                    sb.append("\"" + "exception:" + "\"" + body.exception().getMessage() + "\"");
                    sb.append(Log.NEW_LINE);
                }
            }
            String content = body.body();
            sb.append(Log.LEFT_LINE);
            sb.append("\"" + "body:" + (content != null && content.startsWith("{") ? Json.format(content) : content));
            sb.append(Log.NEW_LINE);
            sb.append(Log.BOTTOM_LINE);
            Log.i(Http.class.getSimpleName(), sb.toString());
        }
    }

}
