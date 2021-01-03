package com.androidx.net.ok;


import com.androidx.json.Json;
import com.androidx.net.Body;
import com.androidx.net.OnHttpListener;
import com.androidx.net.RequestParams;
import com.androidx.net.ResponseCode;
import com.androidx.net.ResponseException;
import com.androidx.net.ResponseHandler;
import com.androidx.net.ResponseHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by Relin
 * on 2018-09-21.
 */

public class OnOkHttpListener implements Callback {

    private String url;
    private RequestParams params;
    private ResponseHandler handler;
    private OnHttpListener listener;

    public OnOkHttpListener(ResponseHandler handler, RequestParams params, String url, OnHttpListener listener) {
        this.params = params;
        this.url = url;
        this.listener = listener;
        this.handler = handler;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        ResponseHelper.sendMessage(handler, ResponseHandler.WHAT_FAILURE, url, params, ResponseCode.SERVER_EXCEPTION, new ResponseException(ResponseException.NOT_OK), ResponseHelper.createBody(ResponseCode.SERVER_EXCEPTION), listener);
    }

    @Override
    public void onResponse(Call call, okhttp3.Response response) {
        try {
            String responseBody = response.body().string();
            if (responseBody == null || responseBody.length() == 0) {
                responseBody = createBody(response.code());
            }
            if (response.code() != ResponseCode.OK) {
                Body body = new Body();
                body.setCode(ResponseCode.parse(response.code()));
                ResponseHelper.sendMessage(handler, ResponseHandler.WHAT_FAILURE, url, params, response.code(), new ResponseException(ResponseException.NOT_OK), responseBody, listener);
                return;
            }
            ResponseHelper.sendMessage(handler, ResponseHandler.WHAT_SUCCEED, url, params, response.code(), new ResponseException(ResponseException.NO_EXP), responseBody, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建请求数据
     *
     * @param responseCode 请求码
     * @return
     */
    public String createBody(int responseCode) {
        Body body = new Body();
        body.setCode(String.valueOf(responseCode));
        body.setMsg(ResponseCode.parse(responseCode));
        body.setData("");
        return Json.parseObject(body);
    }

}
