package com.androidx.net;

/**
 * Author: Relin
 * Describe:请求信息
 * Date:2020/11/29 20:29
 */
public class ResponseException extends RuntimeException {

    /**
     * 没有异常
     */
    public final static String NO_EXP = "No exception information.";
    /**
     * 无网络
     */
    public final static String NOT_NET = "No network, no connection to server.";
    /**
     * 服务端异常
     */
    public final static String NOT_OK = "The server request failed.";


    public ResponseException() {
        super();
    }

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }


}
