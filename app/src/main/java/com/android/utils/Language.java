package com.android.utils;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.app.BaseApplication;

import java.util.Locale;

/**
 * 语言工具
 */
public class Language {

    private static final String LANGUAGE_LANG_COUNTRY = "language_lang_country";
    /**
     * 中文
     */
    public static final Locale ZH = Locale.SIMPLIFIED_CHINESE;
    /**
     * 英文
     */
    public static final Locale US = Locale.US;

    /**
     * 获取当前APP语言
     *
     * @param context
     * @return
     */
    public static Locale getApplication(Context context) {
        // 获得res资源对象
        Resources resources = context.getResources();
        // 获得设置对象
        Configuration config = resources.getConfiguration();
        return config.locale;
    }


    /**
     * 获取当前系统语言
     *
     * @return
     */
    public static Locale getDefault() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    /**
     * 更新语言
     *
     * @param context   上下文对象
     * @param mainClass 主页面
     * @param language  语言
     */
    public static void update(Context context, Class mainClass, Locale language) {
        setCache(context.getApplicationContext(), language);
        Resources resources = context.getApplicationContext().getResources();
        Configuration config = resources.getConfiguration();
        Locale contextLocale = config.locale;
        if (contextLocale.equals(language)) {
            return;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(language);
            BaseApplication.app.createConfigurationContext(config);
        } else {
            config.locale = language;
        }
        resources.updateConfiguration(config, dm);
        if (mainClass != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(BaseApplication.app, mainClass.getName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 更新语言
     *
     * @param context  上下文
     * @param language 语言
     */
    public static void update(Context context, Locale language) {
        update(context, null, language);
    }

    /**
     * 添加缓存
     *
     * @param context  上下文
     * @param language 语言
     */
    public static void setCache(Context context, Locale language) {
        DataStorage.with(context.getApplicationContext()).put(LANGUAGE_LANG_COUNTRY, language.getLanguage() + "," + language.getCountry());
    }

    /**
     * 获取缓存语言
     *
     * @param context
     * @return
     */
    public static Locale getCache(Context context) {
        String cache = DataStorage.with(context.getApplicationContext()).getString(LANGUAGE_LANG_COUNTRY, "");
        if (TextUtils.isEmpty(cache)) {
            return getApplication(context);
        }
        return new Locale(cache.split(",")[0], cache.split(",")[1]);
    }

}
