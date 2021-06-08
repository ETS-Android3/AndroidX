package com.androidx.app;

import android.app.Application;

import com.androidx.net.Header;
import com.androidx.net.Http;
import com.androidx.net.RequestOptions;

/**
 * Author: Relin
 * Describe:核心应用程序，如果没有实现此核心应用程序，
 * 请再自己的Application里面初始化Http.
 * Date:2020/11/28 16:33
 */
public class CoreApplication extends Application {

    /**
     * 应用对象
     */
    public static CoreApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    /**
     * 初始化Http
     *
     * @param debug 调试模式
     */
    protected void initHttp(boolean debug) {
        RequestOptions options = new RequestOptions(this);
        options.debug(debug);
        options.cache(true);
        options.contentType(Header.CONTENT_JSON);
        Http.init(options);
    }

}
