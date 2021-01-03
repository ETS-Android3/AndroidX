package com.androidx.net;

import android.os.Message;

import com.androidx.json.Json;
import com.androidx.text.Number;
import com.androidx.util.Decoder;
import com.androidx.util.Encoder;
import com.androidx.util.Log;
import com.androidx.util.Size;

import java.util.List;

/**
 * Author: Relin
 * Describe:
 * Date:2020/12/3 0:33
 */
public class ResponseHelper {

    /**
     * 互换头部参数
     *
     * @param options 配置参数
     * @param params  请求参数
     */
    public static void swapHeader(RequestOptions options, RequestParams params) {
        for (String key : params.header().keySet()) {
            options.header().add(key, params.header().get(key));
        }
        for (String key : options.header().keySet()) {
            params.addHeader(key, options.header().get(key));
        }
    }

    /**
     * 网络是否可用
     *
     * @param handler  处理
     * @param options  配置
     * @param url      地址
     * @param params   参数
     * @param listener 网络监听
     * @return
     */
    public static boolean isNoNetwork(ResponseHandler handler, RequestOptions options, String url, RequestParams params, OnHttpListener listener) {
        if (!options.isCache() && !Network.isAvailable(options.context())) {
            sendMessage(handler, ResponseHandler.WHAT_FAILURE, url, params, ResponseCode.NO_NET, new ResponseException(ResponseException.NOT_NET), createBody(ResponseCode.NO_NET), listener);
            return true;
        }
        return false;
    }

    /**
     * 是否使用缓存
     *
     * @param handler  处理
     * @param options  配置
     * @param url      地址
     * @param params   参数
     * @param listener 网络监听
     * @return
     */
    public static boolean isUseCache(ResponseHandler handler, RequestOptions options, String url, RequestParams params, OnHttpListener listener) {
        if (options.isCache() && !Network.isAvailable(options.context())) {
            ResponseBody responseBody = selectCache(url, params, listener);
            sendMessage(handler, responseBody);
            return true;
        }
        return false;
    }

    /**
     * 是否通过网络和缓存的检查
     *
     * @param handler  处理
     * @param options  配置
     * @param url      地址
     * @param params   参数
     * @param listener 网络监听
     * @return
     */
    public static boolean isPassNetworkAndCacheCheck(ResponseHandler handler, RequestOptions options, String url, RequestParams params, OnHttpListener listener) {
        if (!Http.isInit()) {
            Log.e(Http.TAG, Http.NOT_INIT, Http.MSG_NOT_INIT);
            return false;
        }
        swapHeader(options, params);
        if (isNoNetwork(handler, options, url, params, listener)) {
            return false;
        }
        if (isUseCache(handler, options, url, params, listener)) {
            return false;
        }
        return true;
    }

    /**
     * 构建请求数据
     *
     * @param responseCode 请求码
     * @return
     */
    public static String createBody(int responseCode) {
        Body body = new Body();
        body.setCode(String.valueOf(responseCode));
        body.setMsg(ResponseCode.parse(responseCode));
        body.setData("");
        return Json.parseObject(body);
    }

    /**
     * 创建请求返回内容
     *
     * @param url          地址
     * @param params       参数
     * @param responseCode 请求代码
     * @param exception    异常
     * @param body         数据
     * @param listener     网络监听
     * @return
     */
    public static ResponseBody createResponseBody(String url, RequestParams params, int responseCode, String exception, String body, OnHttpListener listener) {
        ResponseException responseException = new ResponseException(exception);
        ResponseBody response = new ResponseBody();
        response.url(url);
        response.listener(listener);
        response.code(responseCode);
        response.requestParams(params);
        response.body(body);
        response.exception(responseException);
        return response;
    }

    /**
     * 发送消息
     *
     * @param what     标识
     * @param params   请求参数
     * @param url      请求地址
     * @param code     请求结果代码
     * @param e        异常
     * @param body     内容
     * @param listener 网络监听
     */
    public static void sendMessage(ResponseHandler handler, int what, String url, RequestParams params, int code, Exception e, String body, OnHttpListener listener) {
        ResponseBody response = createResponseBody(url, params, code, e.getMessage(), body, listener);
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = response;
        handler.sendMessage(msg);
    }

    /**
     * 发送信息 - 缓存信息
     *
     * @param handler  处理
     * @param response 返回数据
     */
    public static void sendMessage(ResponseHandler handler, ResponseBody response) {
        Message message = handler.obtainMessage();
        message.what = response.code() == ResponseCode.OK ? ResponseHandler.WHAT_SUCCEED : ResponseHandler.WHAT_FAILURE;
        message.obj = response;
        handler.sendMessage(message);
    }

    /**
     * 选择缓存
     *
     * @param url      地址
     * @param params   参数
     * @param listener 网络监听
     * @return
     */
    public static ResponseBody selectCache(String url, RequestParams params, OnHttpListener listener) {
        String paramsEncode = Encoder.encode(params.params().toString());
        List<ResponseCacheBody> cacheBodies = ResponseCache.query(url, params.params() == null ? "" :paramsEncode);
        ResponseBody response;
        if (Size.of(cacheBodies) != 0) {
            ResponseCacheBody cacheBody = cacheBodies.get(0);
            response = new ResponseBody();
            response.setCache(true);
            response.url(url);
            String decode = Decoder.decode(cacheBody.getBody());
            response.body(decode);
            response.code(Number.parseInt(cacheBody.getCode()));
            response.requestParams(params);
            response.listener(listener);
            response.exception(new Exception(cacheBody.getException()));
        } else {
            response = new ResponseBody();
            response.setCache(true);
            response.url(url);
            response.body(createBody(ResponseCode.NO_NET));
            response.code(ResponseCode.NO_NET);
            response.requestParams(params);
            response.listener(listener);
            response.exception(new ResponseException(ResponseException.NOT_NET));
        }
        return response;
    }


}
