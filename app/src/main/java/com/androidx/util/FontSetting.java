package com.androidx.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.androidx.app.CoreApplication;
import com.androidx.content.DataStorage;

import java.lang.reflect.InvocationTargetException;

/**
 * 字体设置
 */
public class FontSetting {


    /**
     * 重设字体大小
     *
     * @param context
     */
    public static void onResume(Context context) {
        Resources resource = context.getResources();
        Configuration configuration = resource.getConfiguration();
        float systemScale = getSystemFont();
        systemScale = systemScale > 1.3F ? 1.2F : systemScale;
        configuration.fontScale = FontSetting.isFollowSystem() ? systemScale : 1.0F;
        resource.updateConfiguration(configuration, resource.getDisplayMetrics());
    }

    /**
     * 获取系统字体大小
     *
     * @return
     */
    public static float getSystemFont() {
        Configuration configuration = new Configuration();
        try {
            Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Object obj = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
            Object config = obj.getClass().getMethod("getConfiguration").invoke(obj);
            configuration.updateFrom((Configuration) config);
            return configuration.fontScale;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 跟随系统
     *
     * @param follow
     */
    public static void setFollowSystem(boolean follow) {
        DataStorage.with(CoreApplication.app).put("APP_FOLLOW_SYS", follow);
    }

    /**
     * 是否跟随系统
     *
     * @return
     */
    public static boolean isFollowSystem() {
        return DataStorage.with(CoreApplication.app).getBoolean("APP_FOLLOW_SYS", true);
    }

    /**
     * 获取字体缩放值
     *
     * @param context
     * @return
     */
    public static float getFontScale(Context context) {
        Resources res = context.getResources();
        if (res != null) {
            Configuration config = res.getConfiguration();
            return config.fontScale;
        }
        return 1.0f;
    }

}
