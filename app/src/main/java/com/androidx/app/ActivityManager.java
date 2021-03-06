package com.androidx.app;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Relin on 2015/11/28.<br/>
 * Activity管理器<br/>
 * 对Activity页面进行管理，主要可以添加入栈，<br/>
 * 对栈里面的Activity进行管理，同时效果跟finish()<br/>
 * 方法一致，都是对页面进行销毁作用。<br/>
 */
public class ActivityManager {


    /**
     * 页面栈
     */
    private static Stack<AppCompatActivity> activityStack;

    /**
     * 实例对象
     */
    private volatile static ActivityManager activityManager;

    /**
     * 构造函数
     */
    private ActivityManager() {

    }

    /**
     * 单一实例
     */
    public static ActivityManager getInstance() {
        if (activityManager == null) {
            synchronized (ActivityManager.class) {
                if (activityManager == null) {
                    activityManager = new ActivityManager();
                }
            }
        }
        return activityManager;
    }

    /**
     * 添加到堆栈
     *
     * @param activity 页面
     */
    public void add(AppCompatActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<AppCompatActivity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取栈顶
     */
    public Activity getLast() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束栈顶
     */
    public void removeLast() {
        Activity activity = activityStack.lastElement();
        remove(activity);
    }

    /**
     * 结束
     *
     * @param activity 页面
     */
    public void remove(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls 类名的Activity
     */
    public void remove(Class<?> cls) {
        Iterator<AppCompatActivity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void clear() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     *
     * @param context 上下文
     */
    public void exit(Context context) {
        try {
            clear();
            android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            manager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }

}
