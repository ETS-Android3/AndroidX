package com.androidx.net;

import com.androidx.json.Json;

import java.util.List;
import java.util.Map;

/**
 * Author: Relin
 * Describe:
 * Date:2020/11/29 21:24
 */
public class Body {

    /**
     * 成功标识
     */
    public static String SUCCEED = "0";

    /**
     * 消息
     */
    private String msg;
    /**
     * 代码
     */
    private String code;
    /**
     * 数据
     */
    private String data;

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSucceed() {
        if (code != null && code.equals(SUCCEED)) {
            return true;
        }
        return false;
    }

    /**
     * 获取信息
     *
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置信息
     *
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取标识代码
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置标识代码
     *
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public String getData() {
        return data;
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 获取数据Map
     *
     * @return
     */
    public Map<String, String> getDataMap() {
        if (data == null) {
            return null;
        }
        return Json.parseJSONObject(data);
    }

    /**
     * 获取字符列表，["xxx","xxx","xxx"]
     *
     * @return
     */
    public List<String> getDataList() {
        if (data == null) {
            return null;
        }
        return Json.parseJSONList(data);
    }

}
