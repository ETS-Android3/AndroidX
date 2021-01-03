package com.androidx.net;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求码
 */
public class ResponseCode {

    /**
     * 无网络连接
     */
    public static final int NO_NET = 1004;
    /**
     * 服务器异常
     */
    public static final int SERVER_EXCEPTION = 0;
    /**
     * 请求成功
     */
    public static final int OK = 200;
    /**
     * 服务器资源创建
     */
    public static final int SERVER_RESOURCE_CREATION = 201;
    /**
     * 服务器已接受请求
     */
    public static final int ACCEPTED_REQUEST = 202;
    /**
     * 非授权信息
     */
    public static final int UNAUTHORIZED_INFO = 203;
    /**
     * 无请求结果
     */
    public static final int NO_REQUEST_RESULT = 204;
    /**
     * 重置内容
     */
    public static final int RESET_CONTENT = 205;
    /**
     * 重置内容
     */
    public static final int PROCESS_PART_GET_REQUEST = 206;

    /**
     * 错误请求
     */
    public static final int BAD_REQUEST = 400;
    /**
     * 请求未授权
     */
    public static final int REQUEST_UNAUTHORIZED = 401;
    /**
     * 请求被拒绝
     */
    public static final int REQUEST_DENIED = 403;
    /**
     * 请求不存在
     */
    public static final int REQUEST_NOT_EXIST = 404;
    /**
     * 请求方法禁用
     */
    public static final int REQUEST_METHOD_DISABLED = 405;
    /**
     * 请求不被接受该
     */
    public static final int REQUEST_NO_ACCEPTED = 406;
    /**
     * 请求需要代理
     */
    public static final int REQUESTS_REQUIRE_PROXY = 407;
    /**
     * 请求超时
     */
    public static final int REQUEST_TIMEOUT = 408;
    /**
     * 请求冲突
     */
    public static final int REQUEST_CONFLICT = 409;
    /**
     * 请求资源被删
     */
    public static final int REQUEST_RESOURCE_DELETED = 410;
    /**
     * 请求标头长度错误
     */
    public static final int REQUEST_HEADER_LENGTH_ERROR = 411;
    /**
     * 请求条件未满足
     */
    public static final int REQUEST_CONDITION_NOT_MET = 412;
    /**
     * 请求实体过大
     */
    public static final int REQUESTING_ENTITY_LARGE = 413;
    /**
     * 请求URL过长
     */
    public static final int REQUEST_URL_LONG = 414;
    /**
     * 请求媒体类型不支持
     */
    public static final int REQUEST_MEDIA_TYPE_NOT_SUPPORTED = 415;
    /**
     * 请求范围不符
     */
    public static final int REQUEST_SCOPE_ERROR = 416;
    /**
     * 请求标头不满足要求
     */
    public static final int REQUEST_HEADER_ERROR = 417;

    /**
     * 服务器无法识别请求方法
     */
    public static final int SERVER_NOT_IDENTIFY = 501;
    /**
     * 错误网关
     */
    public static final int BAD_GATEWAY = 502;
    /**
     * 服务不可用
     */
    public static final int SERVICE_UNAVAILABLE = 503;
    /**
     * 网关超时
     */
    public static final int GATEWAY_TIMEOUT = 504;
    /**
     * HTTP版本不受支持
     */
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    public static Map<Integer, String> codeMap;

    public static int code[] = {
            NO_NET,
            SERVER_EXCEPTION,

            OK,
            SERVER_RESOURCE_CREATION,
            ACCEPTED_REQUEST,
            UNAUTHORIZED_INFO,
            NO_REQUEST_RESULT,
            RESET_CONTENT,
            PROCESS_PART_GET_REQUEST,

            BAD_REQUEST,
            REQUEST_UNAUTHORIZED,
            REQUEST_DENIED,
            REQUEST_NOT_EXIST,
            REQUEST_METHOD_DISABLED,
            REQUEST_NO_ACCEPTED,
            REQUESTS_REQUIRE_PROXY,
            REQUEST_TIMEOUT,
            REQUEST_CONFLICT,
            REQUEST_RESOURCE_DELETED,
            REQUEST_HEADER_LENGTH_ERROR,
            REQUEST_CONDITION_NOT_MET,
            REQUESTING_ENTITY_LARGE,
            REQUEST_URL_LONG,
            REQUEST_MEDIA_TYPE_NOT_SUPPORTED,
            REQUEST_SCOPE_ERROR,
            REQUEST_HEADER_ERROR,

            SERVER_NOT_IDENTIFY,
            BAD_GATEWAY,
            SERVICE_UNAVAILABLE,
            GATEWAY_TIMEOUT,
            HTTP_VERSION_NOT_SUPPORTED,
    };

    public static String message[] = {
            "无网络连接",//-1
            "服务器异常",//0

            "请求成功",//200
            "服务器资源创建",//201
            "服务器已接受请求",//202
            "非授权信息",//203
            "无请求结果",//204
            "重置内容",//205
            "处理部分GET请求",//206

            "错误请求",//400
            "请求未授权",//401
            "请求被拒绝",//403
            "请求不存在",//404
            "请求方法禁用",//405
            "请求不被接受该",//406
            "请求需要代理",//407
            "请求超时",//408
            "请求冲突",//409
            "请求资源被删",//410
            "请求标头长度错误",//411
            "请求条件未满足",//412
            "请求实体过大",//413
            "请求URL过长",//414
            "请求媒体类型不支持",//415
            "请求范围不符",//416
            "请求标头不满足要求",//417

            "服务器无法识别请求方法",//501
            "错误网关",//502
            "服务不可用",//503
            "网关超时",//504
            "HTTP版本不受支持"//505
    };

    static {
        codeMap = new HashMap<>();
        for (int i = 0; i < code.length; i++) {
            codeMap.put(code[i], message[i]);
        }
    }

    /**
     * 转换代码信息
     *
     * @param code 请求代码
     * @return
     */
    public static String parse(int code) {
        for (int key : codeMap.keySet()) {
            if (code == key) {
                return codeMap.get(key);
            }
        }
        return "服务器异常";
    }

}
