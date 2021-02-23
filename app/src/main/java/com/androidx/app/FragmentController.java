package com.androidx.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Author: Relin
 * Describe:Fragment管理器<br/>
 * Date:2015-11-28.<br/>
 */
public class FragmentController {

    /**
     * Fragment类
     */
    private Class<?> fragmentClass;
    /**
     * 页面
     */
    private AppCompatActivity activity;
    /**
     * 页面
     */
    private Fragment fragment;
    /**
     * Fragment列表
     */
    private List<Fragment> fragments;
    /**
     * Fragment页面
     */
    private CoreFragment stackTopFragment;

    /**
     * Fragment控制类型枚举
     */
    public enum Type {
        /**
         * 添加方式
         */
        ADD,
        /**
         * 替代方式
         */
        REPLACE
    }

    public FragmentController(AppCompatActivity activity) {
        this.activity = activity;
    }

    public FragmentController(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * 获取Fragment管理器
     *
     * @return
     */
    public FragmentManager getFragmentManager() {
        if (activity != null) {
            return activity.getSupportFragmentManager();
        }
        if (fragment != null) {
            return fragment.getChildFragmentManager();
        }
        return null;
    }

    /**
     * 处理Fragment
     *
     * @param fragmentClass   Fragment类
     * @param args            数据
     * @param params          参数
     * @param callback        网络回调
     * @param containerViewId 布局ID
     * @param type            类型
     * @param addToBackStack  添加到回退栈
     */
    public void commit(Class fragmentClass, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId, Type type, boolean addToBackStack) {
        String tag = getTag(fragmentClass);
        CoreFragment fragment = (CoreFragment) getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            try {
                fragment = (CoreFragment) fragmentClass.newInstance();
                commit(fragment, args, params, callback, containerViewId, type, addToBackStack);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            commit(fragment, args, params, callback, containerViewId, type, addToBackStack);
        }
    }

    /**
     * 处理Fragment
     *
     * @param fragment        Fragment类
     * @param args            数据
     * @param params          参数
     * @param callback        网络回调
     * @param containerViewId 布局ID
     * @param type            类型
     * @param addToBackStack  添加到回退栈
     */
    public void commit(CoreFragment fragment, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId, Type type, boolean addToBackStack) {
        if (getFragmentManager() == null) {
            new RuntimeException("Commit failed, activity or fragment is null.").printStackTrace();
            return;
        }
        this.fragmentClass = fragment.getClass();
        if (fragmentClass != null) {
            String tag = getTag(fragmentClass);
            fragment.setArguments(args);
            fragment.setParameters(params);
            fragment.setFragmentHttpCallback(callback);
            if (stackTopFragment != null) {
                stackTopFragment.onBeforeProcessing();
            }
            if (fragments == null) {
                fragments = new ArrayList();
            }
            if (!fragments.contains(fragment)) {
                fragments.add(fragment);
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (type != Type.ADD) {
                transaction.replace(containerViewId, fragment, tag);
            } else if (!fragment.isAdded()) {
                transaction.add(containerViewId, fragment, tag);
            } else {
                Iterator iterator = this.fragments.iterator();
                while (iterator.hasNext()) {
                    CoreFragment lastFragment = (CoreFragment) iterator.next();
                    transaction.hide(lastFragment);
                }
                if (stackTopFragment != null) {
                    stackTopFragment.onPause();
                }
                transaction.show(fragment);
                fragment.onResume();
            }
            stackTopFragment = fragment;
            if (addToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 添加
     *
     * @param fragmentClass   Fragment
     * @param args            参数
     * @param callback        网络
     * @param containerViewId 布局id
     */
    public void add(Class<?> fragmentClass, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId) {
        commit(fragmentClass, args, params, callback, containerViewId, Type.ADD, false);
    }

    /**
     * 添加
     *
     * @param fragment        Fragment
     * @param args            参数
     * @param callback        网络
     * @param containerViewId 布局id
     */
    public void add(CoreFragment fragment, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId) {
        commit(fragment, args, params, callback, containerViewId, Type.ADD, false);
    }

    /**
     * 替代
     *
     * @param fragmentClass   Fragment
     * @param args            参数
     * @param callback        网络回调
     * @param containerViewId 布局id
     */
    public void replace(Class<?> fragmentClass, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId) {
        commit(fragmentClass, args, params, callback, containerViewId, Type.REPLACE, false);
    }

    /**
     * 替代
     *
     * @param fragment        Fragment
     * @param args            参数
     * @param callback        网络回调
     * @param containerViewId 布局id
     */
    public void replace(CoreFragment fragment, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId) {
        commit(fragment, args, params, callback, containerViewId, Type.REPLACE, false);
    }


    /**
     * 获取标识
     *
     * @param fragmentClass Fragment类
     * @return
     */
    private String getTag(Class fragmentClass) {
        StringBuilder sb = new StringBuilder(fragmentClass.toString());
        return sb.toString();
    }

    /**
     * 添加到回退栈
     *
     * @param fragmentClass   Fragment
     * @param args            参数
     * @param callback        网络回调
     * @param containerViewId 布局id
     */
    public void addToBackStack(Class<?> fragmentClass, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId) {
        commit(fragmentClass, args, params, callback, containerViewId, Type.ADD, true);
    }

    /**
     * 添加到回退栈
     *
     * @param fragment        Fragment
     * @param args            参数
     * @param callback        网络回调
     * @param containerViewId 布局id
     */
    public void addToBackStack(CoreFragment fragment, Bundle args, Object params, FragmentHttpCallback callback, int containerViewId) {
        commit(fragment, args, params, callback, containerViewId, Type.ADD, true);
    }

    /**
     * 回退显示<br/>
     * 注意：添加方式必须addToBackStack，才可以回退。<br/>
     *
     * @param fragmentClass Fragment
     */
    public void popBackStack(Class<?> fragmentClass) {
        popBackStack(fragmentClass, null);
    }

    /**
     * 回退显示<br/>
     * 注意：添加方式必须addToBackStack，才可以回退。<br/>
     *
     * @param fragmentClass Fragment
     * @param args          数据
     */
    public void popBackStack(Class<?> fragmentClass, Bundle args) {
        if (getFragmentManager() == null) {
            new RuntimeException("popBackStack failed , activity or fragment is null.").printStackTrace();
            return;
        }
        if (fragmentClass != null) {
            CoreFragment fragment = (CoreFragment) getFragmentManager().findFragmentByTag(fragmentClass.toString());
            if (fragment != null) {
                stackTopFragment = fragment;
                fragment.onPopBackStack(args);
            }
            getFragmentManager().popBackStackImmediate(fragmentClass.toString(), 0);
        }
    }

    /**
     * 弹出栈顶
     */
    public void popTop() {
        popTop(null);
    }

    /**
     * 弹出栈顶
     *
     * @param args 数据传递对象
     */
    public void popTop(Bundle args) {
        if (getFragmentManager() == null) {
            new RuntimeException("popTop failed , activity or fragment is null.").printStackTrace();
            return;
        }
        FragmentManager manager = getFragmentManager();
        manager.popBackStackImmediate();
        int count = manager.getBackStackEntryCount();
        String name = manager.getBackStackEntryAt(count - 1).getName();
        stackTopFragment = (CoreFragment) manager.findFragmentByTag(name);
        stackTopFragment.onPopBackStack(args);
    }

    /**
     * 弹出
     *
     * @param position 位置
     */
    public void pop(int position) {
        pop(position, null);
    }

    /**
     * 弹出
     *
     * @param position 位置
     * @param args     参数
     */
    public void pop(int position, Bundle args) {
        if (getFragmentManager() == null) {
            new RuntimeException("pop failed , activity or fragment is null.").printStackTrace();
            return;
        }
        FragmentManager manager = getFragmentManager();
        manager.popBackStackImmediate();
        int count = manager.getBackStackEntryCount();
        if (position >= count) {
            return;
        }
        String name = manager.getBackStackEntryAt(position).getName();
        getFragmentManager().executePendingTransactions();
        stackTopFragment = (CoreFragment) manager.findFragmentByTag(name);
        stackTopFragment.onPopBackStack(args);
    }

    /**
     * 获取
     *
     * @return
     */
    public FragmentManager getSupportFragmentManager() {
        return getFragmentManager();
    }

    /**
     * 获取回退栈个数
     *
     * @return
     */
    public int getBackStackEntryCount() {
        if (getFragmentManager() == null) {
            new RuntimeException("getBackStackEntryCount failed , activity or fragment is null.").printStackTrace();
            return 0;
        }
        FragmentManager manager = getFragmentManager();
        return manager.getBackStackEntryCount();
    }

    /**
     * 弹出
     *
     * @param args 数据传递对象
     */
    public void popBottomTopAll(Bundle args) {
        if (getFragmentManager() == null) {
            new RuntimeException("popBottomTopAll failed , activity or fragment is null.").printStackTrace();
            return;
        }
        FragmentManager manager = getFragmentManager();
        int count = manager.getBackStackEntryCount();
        while (count > 1) {
            manager.popBackStackImmediate();
        }
        popTop(args);
    }

    /**
     * 查找
     *
     * @param fragmentClass
     * @return
     */
    public <T extends CoreFragment> T find(Class<T> fragmentClass) {
        if (getFragmentManager() == null) {
            new RuntimeException("find failed , activity or fragment is null.").printStackTrace();
            return null;
        }
        getFragmentManager().executePendingTransactions();
        String tag = getTag(fragmentClass);
        T fragment = (T) getFragmentManager().findFragmentByTag(tag);
        return fragment;
    }

    /**
     * 获取
     *
     * @return CoreFragment
     */
    public CoreFragment getStackTop() {
        return stackTopFragment;
    }

}
