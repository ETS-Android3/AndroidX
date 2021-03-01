package com.androidx.util;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.androidx.app.CoreApplication;
import com.androidx.content.DataStorage;

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
    public static Locale getSystem() {
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
     * @param language  语言
     * @param mainClass 主页面
     */
    public static void update(Context context, Locale language, Class mainClass) {
        setCache(context, language);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        Locale contextLocale = config.locale;
        if (compare(language, contextLocale)) {
            return;
        }
        DisplayMetrics metrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(language);
            CoreApplication.app.createConfigurationContext(config);
        } else {
            config.locale = language;
        }
        resources.updateConfiguration(config, metrics);
        if (mainClass != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(CoreApplication.app, mainClass.getName()));
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
        update(context, language, null);
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

    /**
     * 是否是中文
     *
     * @param locale 语言
     * @return
     */
    public static boolean isChinese(Locale locale) {
        return compare(locale, ZH);
    }

    /**
     * 判断语言是否相等
     *
     * @param source 原语言
     * @param target 目标语言
     * @return
     */
    public static boolean compare(Locale source, Locale target) {
        String sourceLanguage = source.getLanguage();
        String sourceCountry = source.getCountry();
        String targetLanguage = target.getLanguage();
        String targetCountry = target.getCountry();
        if (sourceLanguage.equals(targetLanguage) && sourceCountry.equals(targetCountry)) {
            return true;
        }
        return false;
    }

}
