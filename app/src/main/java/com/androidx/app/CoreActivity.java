package com.androidx.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidx.R;
import com.androidx.annotation.ViewUtils;
import com.androidx.net.OnHttpListener;
import com.androidx.net.ResponseBody;
import com.androidx.widget.SwipeLayout;

/**
 * Author: Relin
 * Describe:核心Activity<br/>
 * 初始化需要重写onCreate(@Nullable Bundle savedInstanceState)<br/>
 * 该类已实现OnHttpListener、OnSwipeRefreshListener、OnSwipeLoadListener<br/>
 * 同时实现了状态（网络、警告、成功）提示、普通提示<br/>
 * Date:2020/12/13 11:03
 */
public abstract class CoreActivity extends AppCompatActivity implements OnHttpListener,
        SwipeLayout.OnSwipeRefreshListener, SwipeLayout.OnSwipeLoadListener,
        PermissionManager.OnRequestPermissionsListener, FragmentHttpCallback {

    /**
     * 导航栏
     */
    private NavigationBar navigationBar;

    /**
     * 数据加载
     */
    private Loading loading;
    /**
     * Fragment控制器
     */
    private FragmentController fragmentController;
    /**
     * 调试
     */
    private Debug debug;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentController = new FragmentController(this);
        ActivityManager.getInstance().add(this);
        setNavigationBar(new NavigationBar(this, getNavigationBarLayoutResId(), true));
        setLoadingView(getLoadingLayoutResId());
        setContentView(new ContentView(this, getContentViewResId()).getContentView());
        setDebug(new Debug(this, (ViewGroup) getContentView()));
        ViewUtils.inject(this, getContentView());
        onCreate(savedInstanceState, getNavigationBar());
        onHttpRequest();
    }

    /**
     * 获取控件
     *
     * @param cls 类名
     * @param id  控件id
     * @param <T> 控件
     * @return
     */
    public <T extends View> T findViewById(Class<T> cls, @IdRes int id) {
        return findViewById(id);
    }

    /**
     * 设置内容视图布局资源id
     *
     * @return
     */
    protected abstract int getContentViewResId();

    /**
     * 获取内容视图
     *
     * @return
     */
    protected View getContentView() {
        return findViewById(android.R.id.content);
    }

    /**
     * 设置导航栏布局资源id
     *
     * @return
     */
    protected int getNavigationBarLayoutResId() {
        return R.layout.android_navigation_bar;
    }

    /**
     * 设置导航栏视图
     *
     * @param navigationBar
     * @return
     */
    public void setNavigationBar(NavigationBar navigationBar) {
        this.navigationBar = navigationBar;
    }

    /**
     * 获取导航栏
     *
     * @return
     */
    public NavigationBar getNavigationBar() {
        return navigationBar;
    }

    /**
     * 页面创建成功
     *
     * @param savedInstanceState 保存实列状态
     * @param bar                导航栏工具
     */
    protected abstract void onCreate(Bundle savedInstanceState, NavigationBar bar);

    /**
     * 设置数据加载布局资源id
     *
     * @return
     */
    protected int getLoadingLayoutResId() {
        return R.layout.android_loading;
    }

    /**
     * 设置Loading资源布局id
     *
     * @param layoutResId
     */
    protected void setLoadingView(int layoutResId) {
        FrameLayout parent = findViewById(android.R.id.content);
        loading = new Loading(this, parent, layoutResId);
    }

    /**
     * 设置加载View
     *
     * @param loadingView
     */
    protected void setLoadingView(View loadingView) {
        FrameLayout parent = findViewById(android.R.id.content);
        loading = new Loading(this, parent, loadingView);
    }

    /**
     * 设置加载器
     *
     * @param loading
     */
    protected void setLoading(Loading loading) {
        this.loading = loading;
    }

    /**
     * 获取加载器
     *
     * @return
     */
    protected Loading getLoading() {
        return loading;
    }

    /**
     * 设置加载背景资源
     *
     * @param resId 资源id
     */
    protected void setLoadingBackgroundResource(@DrawableRes int resId) {
        if (loading == null) {
            return;
        }
        loading.setLoadingBackgroundResource(resId);
    }

    /**
     * 设置加载背景
     *
     * @param background 背景Drawable
     */
    protected void setLoadingBackground(Drawable background) {
        if (loading == null) {
            return;
        }
        loading.setLoadingBackground(background);
    }

    /**
     * 显示loading
     */
    public void showLoading() {
        if (loading == null) {
            return;
        }
        loading.show();
    }

    /**
     * 显示loading
     *
     * @param location 位置 <br/>
     *                 上方：{@link Loading#TOP} <br/>
     *                 中间：{@link Loading#CENTER}
     */
    public void showLoading(int location) {
        if (loading == null) {
            return;
        }
        loading.show(location);
    }

    /**
     * 隐藏loading
     */
    public void dismissLoading() {
        if (loading == null) {
            return;
        }
        loading.dismiss();
    }

    /**
     * 设置调试视图
     *
     * @param debug
     */
    public void setDebug(Debug debug) {
        this.debug = debug;
    }

    /**
     * 获取调试视图
     *
     * @return
     */
    public Debug getDebug() {
        return debug;
    }

    /**
     * 显示提示,默认普通模式
     *
     * @param message 信息
     */
    protected void showToast(String message) {
        showToast(Toast.Type.NORMAL, 0, Gravity.BOTTOM, message, -1, -1);
    }

    /**
     * 显示提示信息
     *
     * @param gravity 位置
     * @param message 信息
     */
    protected void showToast(int gravity, String message) {
        showToast(Toast.Type.NORMAL, 0, gravity, message, -1, -1);
    }

    /**
     * 显示状态
     *
     * @param status  状态
     * @param message 信息
     */
    protected void showStatus(int status, String message) {
        showToast(Toast.Type.STATUS, status, Gravity.CENTER, message, -1, -1);
    }

    /**
     * 显示状态
     *
     * @param status  状态<br/>
     *                成功：{@link Toast.Status#SUCCESS};<br/>
     *                警告：{@link Toast.Status#WARNING};<br/>
     *                网络：{@link Toast.Status#WIRELESS};
     * @param gravity 位置
     * @param message 信息
     */
    protected void showStatus(int status, int gravity, String message) {
        showToast(Toast.Type.STATUS, status, gravity, message, -1, -1);
    }

    /**
     * 显示提示
     *
     * @param type            类型<br/>
     *                        普通：{@link Toast.Type#NORMAL}<br/>
     *                        状态：{@link Toast.Type#STATUS}
     * @param status          状态<br/>
     *                        成功：{@link Toast.Status#SUCCESS};<br/>
     *                        警告：{@link Toast.Status#WARNING};<br/>
     *                        网络：{@link Toast.Status#WIRELESS};
     * @param gravity         位置
     * @param message         信息
     * @param textColor       文字颜色
     * @param backgroundResId 背景资源
     */
    protected void showToast(int type, int status, int gravity, String message, @ColorInt int textColor, @DrawableRes int backgroundResId) {
        Toast toast = new Toast(this, type);
        toast.setGravity(gravity);
        toast.setStatus(status);
        toast.setMessage(message);
        if (textColor != -1) {
            toast.setTextColor(textColor);
        }
        if (backgroundResId != -1) {
            toast.setBackgroundResource(backgroundResId);
        }
        toast.show();
    }

    /**
     * 网络请求
     */
    public void onHttpRequest() {

    }

    @Override
    public void onSwipeRefresh() {
        onSwipeRequest(true);
    }

    @Override
    public void onSwipeLoad() {
        onSwipeRequest(false);
    }

    /**
     * 上拉下拉请求
     *
     * @param isRefresh
     */
    public void onSwipeRequest(boolean isRefresh) {

    }

    @Override
    public void onHttpSucceed(ResponseBody responseBody) {
        dismissLoading();
        responseBody.page(getClass().getCanonicalName());
        debug.addResponseBody(responseBody);
    }

    @Override
    public void onHttpFailure(ResponseBody responseBody) {
        dismissLoading();
        responseBody.page(getClass().getCanonicalName());
        debug.addResponseBody(responseBody);
    }

    /**
     * 是否覆盖启动过渡动画
     */
    private boolean overridePendingStartTransition = true;

    /**
     * 设置是否覆盖启动过渡动画
     *
     * @param overridePendingStartTransition
     */
    public void setOverridePendingStartTransition(boolean overridePendingStartTransition) {
        this.overridePendingStartTransition = overridePendingStartTransition;
    }

    /**
     * 是否覆盖启动过渡动画
     */
    public boolean isOverridePendingStartTransition() {
        return overridePendingStartTransition;
    }

    /**
     * 是否覆盖结束过渡动画
     */
    private boolean overridePendingFinishTransition = true;

    /**
     * 设置是否覆盖结束过渡动画
     *
     * @param overridePendingFinishTransition
     */
    public void setOverridePendingFinishTransition(boolean overridePendingFinishTransition) {
        this.overridePendingFinishTransition = overridePendingFinishTransition;
    }

    /**
     * 是否覆盖结束过渡动画
     *
     * @return
     */
    public boolean isOverridePendingFinishTransition() {
        return overridePendingFinishTransition;
    }

    /**
     * 页面跳转动画
     */
    public void overridePendingStartTransition() {
        if (isOverridePendingStartTransition()) {
            overridePendingTransition(R.anim.android_anim_right_in, R.anim.android_anim_left_exit);
        }
    }

    /**
     * 页面结束动画
     */
    public void overridePendingFinishTransition() {
        if (isOverridePendingFinishTransition()) {
            overridePendingTransition(R.anim.android_anim_left_in, R.anim.android_anim_right_exit);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingStartTransition();
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
        overridePendingStartTransition();
    }

    /**
     * 启动页面
     *
     * @param cls
     */
    public void startActivity(Class cls) {
        startActivity(cls, null);
    }

    /**
     * 启动页面
     *
     * @param cls     页面类
     * @param options 参数
     */
    public void startActivity(Class cls, Bundle options) {
        Intent intent = new Intent(this, cls);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivity(intent, options);
    }

    /**
     * 启动有返回结果的页面
     *
     * @param cls         页面
     * @param requestCode 请求代码
     */
    public void startActivityForResult(Class cls, int requestCode) {
        startActivityForResult(cls, requestCode, null);
    }

    /**
     * 请求有返回结果的页面
     *
     * @param cls         页面
     * @param requestCode 请求代码
     * @param options     参数
     */
    public void startActivityForResult(Class cls, int requestCode, Bundle options) {
        Intent intent = new Intent(this, cls);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingFinishTransition();
    }

    /**
     * 设置Fragment容器视图id
     *
     * @return
     */
    protected int setContainerViewId() {
        return 0;
    }

    /**
     * 设置Fragment控制器
     *
     * @param fragmentController
     */
    protected void setFragmentController(FragmentController fragmentController) {
        this.fragmentController = fragmentController;
    }

    /**
     * 获取Fragment控制器
     *
     * @return
     */
    protected FragmentController getFragmentController() {
        return fragmentController;
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass 类名
     */
    protected void addFragment(Class fragmentClass) {
        addFragment(fragmentClass, null);
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     */
    protected void addFragment(Class fragmentClass, Bundle args) {
        getFragmentController().add(fragmentClass, args, null, this, setContainerViewId());
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass 类名
     * @param params        参数
     */
    protected void addFragment(Class fragmentClass, Object params) {
        getFragmentController().add(fragmentClass, null, params, this, setContainerViewId());
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     * @param params        参数
     */
    protected void addFragment(Class fragmentClass, Bundle args, Object params) {
        getFragmentController().add(fragmentClass, args, params, this, setContainerViewId());
    }


    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     */
    protected void replaceFragment(Class fragmentClass) {
        getFragmentController().replace(fragmentClass, null, null, this, setContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     */
    protected void replaceFragment(Class fragmentClass, Bundle args) {
        getFragmentController().replace(fragmentClass, args, null, this, setContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     * @param params        参数
     */
    protected void replaceFragment(Class fragmentClass, Object params) {
        getFragmentController().replace(fragmentClass, null, params, this, setContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     * @param params        参数
     */
    protected void replaceFragment(Class fragmentClass, Bundle args, Object params) {
        getFragmentController().replace(fragmentClass, args, params, this, setContainerViewId());
    }

    /**
     * Fragment网络失败
     *
     * @param responseBody
     */
    @Override
    public void onFragmentHttpFailure(ResponseBody responseBody) {
        debug.addResponseBody(responseBody);
    }

    /**
     * Fragment成功
     *
     * @param responseBody
     */
    @Override
    public void onFragmentHttpSucceed(ResponseBody responseBody) {
        debug.addResponseBody(responseBody);
    }

    /**
     * 查找Fragment
     *
     * @param fragmentClass 类名
     * @return
     */
    public <T extends CoreFragment> T findFragment(Class<T> fragmentClass) {
        return getFragmentController().find(fragmentClass);
    }

    /**
     * 获取栈顶Fragment
     *
     * @return
     */
    protected CoreFragment getStackTopFragment() {
        return getFragmentController().getStackTop();
    }

    /**
     * 检查运行权限
     *
     * @param permissions 权限
     * @param requestCode 请求
     */
    protected void checkPermissions(String[] permissions, int requestCode) {
        PermissionManager.checkPermissions(this, permissions, requestCode, this);
    }

    @Override
    public void onRequestPermissionsSucceed(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onRequestPermissionsFailed(int requestCode, String[] permissions, int[] grantResults) {

    }
}
