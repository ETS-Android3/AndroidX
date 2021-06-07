package com.androidx.net;

import com.androidx.app.CoreApplication;
import com.androidx.sqlite.SQLite;
import com.androidx.util.Log;
import com.androidx.util.Size;

import java.util.List;

/**
 * Author: Relin
 * Describe:缓存工具
 * Date:2020/11/29 13:47
 */
public class ResponseCache {

    /**
     * 日志标识
     */
    public static String TAG = ResponseCache.class.getSimpleName();

    /**
     * 插入缓存
     *
     * @param body
     */
    public static void insert(ResponseTable body) {
        if (!Http.isInit()) {
            Log.e(TAG, Http. NOT_INIT, Http.MSG_NOT_INIT);
            return;
        }
        List<ResponseTable> bodies = query(body.getUrl(), body.getParams());
        int size = Size.of(bodies);
        if (size == 0) {
            SQLite.with(CoreApplication.app).insert(body);
        } else {
            update(body);
        }
    }

    /**
     * 更新缓存
     *
     * @param body
     */
    public static void update(ResponseTable body) {
        if (!Http.isInit()) {
            Log.e(TAG, Http. NOT_INIT, Http.MSG_NOT_INIT);
            return;
        }
        SQLite.with(CoreApplication.app).update(body, " url=? and params=? ", new String[]{body.getUrl(), body.getParams()});
    }

    /**
     * 获取缓存
     *
     * @param url    服务器地址
     * @param params 参数
     * @return
     */
    public static List<ResponseTable> query(String url, String params) {
        if (!Http.isInit()) {
            Log.e(TAG, Http. NOT_INIT, Http.MSG_NOT_INIT);
            return null;
        }
        List<ResponseTable> items = SQLite.with(CoreApplication.app).query(ResponseTable.class, "select * from " + ResponseTable.class.getSimpleName() + " where params = \'" + params + "\' and url = \'" + url + "\'");
        return items;
    }

    /**
     * 清除缓存
     */
    public static void delete() {
        if (!Http.isInit()) {
            Log.e(TAG, Http. NOT_INIT, Http.MSG_NOT_INIT);
            return;
        }
        SQLite.with(CoreApplication.app).deleteTable(ResponseTable.class.getSimpleName());
    }

}
