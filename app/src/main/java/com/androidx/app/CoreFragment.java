package com.androidx.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.androidx.R;
import com.androidx.annotation.ViewUtils;
import com.androidx.net.OnHttpListener;
import com.androidx.net.ResponseBody;
import com.androidx.widget.SwipeLayout;

/**
 * Author: Relin
 * Describe:核心Fragment
 * Date:2020/12/13 15:24
 */
public abstract class CoreFragment extends Fragment implements OnHttpListener,
        SwipeLayout.OnSwipeRefreshListener, SwipeLayout.OnSwipeLoadListener,
        PermissionManager.OnRequestPermissionsListener, FragmentHttpCallback {

    /**
     * 数据加载
     */
    private Loading loading;
    /**
     * Fragment控制器
     */
    private FragmentController fragmentController;
    /**
     * 导航栏
     */
    private NavigationBar navigationBar;
    /**
     * 根视图
     */
    private View rootView;
    /**
     * 根区域
     */
    private LinearLayout root;
    /**
     * 标题区域
     */
    private FrameLayout title;
    /**
     * 内容区域
     */
    private FrameLayout content;
    /**
     * 内容视图
     */
    private View contentView;
    /**
     * 是否苏醒
     */
    private boolean onResume;
    /**
     * Fragment回调
     */
    private FragmentHttpCallback fragmentHttpCallback;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentController = new FragmentController(this);
        //初始化
        rootView = inflater.inflate(R.layout.android_content, container, false);
        root = rootView.findViewById(R.id.root);
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        //标题栏
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) title.getLayoutParams();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        params.height = activity.getSupportActionBar().getHeight();
        title.setLayoutParams(params);
        setNavigationBar(new NavigationBar(getContext(), getNavigationBarLayoutResId(), false));
        //内容区域
        contentView = inflater.inflate(getContentViewResId(), container, false);
        setContentView(contentView);
        setLoadingView(getLoadingLayoutResId());
        ViewUtils.inject(this, rootView);
        onCreate(savedInstanceState, getNavigationBar());
        onHttpRequest();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onResume) {
            onRestart();
        }
        onResume = true;
    }

    /**
     * 重新回到此页面
     */
    public void onRestart() {

    }

    /**
     * 设置Fragment网络回调
     *
     * @param fragmentHttpCallback
     */
    public void setFragmentHttpCallback(FragmentHttpCallback fragmentHttpCallback) {
        this.fragmentHttpCallback = fragmentHttpCallback;
    }

    /**
     * 设置参数
     *
     * @param params 参数
     */
    public void setParameters(Object params) {

    }

    /**
     * 获取控件
     *
     * @param id 控件id
     * @return
     */
    public <T extends View> T findViewById(@IdRes int id) {
        return contentView.findViewById(id);
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
        return contentView.findViewById(id);
    }

    /**
     * 弹出栈
     *
     * @param args 参数
     */
    public void onPopBackStack(Bundle args) {

    }

    /**
     * Fragment处理之前
     */
    public void onBeforeProcessing() {

    }

    /**
     * 获取根区域
     *
     * @return
     */
    public LinearLayout getRoot() {
        return root;
    }

    /**
     * 获取标题区域
     *
     * @return
     */
    public FrameLayout getTitle() {
        return title;
    }

    /**
     * 获取根视图
     *
     * @return
     */
    public View getRootView() {
        return rootView;
    }

    /**
     * 获取内容区域
     *
     * @return
     */
    public FrameLayout getContent() {
        return content;
    }

    /**
     * 获取内容区域
     *
     * @param contentView
     */
    protected void setContentView(View contentView) {
        content.addView(contentView);
        this.contentView = contentView;
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
        return contentView;
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
     * 设置导航栏
     *
     * @param navigationBar
     */
    public void setNavigationBar(NavigationBar navigationBar) {
        title.addView(navigationBar.getContentView());
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
        FrameLayout parent = rootView.findViewById(R.id.content);
        loading = new Loading(getContext(), parent, layoutResId);
    }

    /**
     * 设置加载View
     *
     * @param loadingView
     */
    protected void setLoadingView(View loadingView) {
        FrameLayout parent = rootView.findViewById(R.id.content);
        loading = new Loading(getContext(), parent, loadingView);
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
        if (getContext() == null) {
            return;
        }
        Toast toast = new Toast(getContext(), type);
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
        if (getContext() == null) {
            return;
        }
        dismissLoading();
        if (fragmentHttpCallback != null) {
            responseBody.page(getClass().getCanonicalName());
            fragmentHttpCallback.onFragmentHttpSucceed(responseBody);
        }
    }

    @Override
    public void onHttpFailure(ResponseBody responseBody) {
        if (getContext() == null) {
            return;
        }
        dismissLoading();
        if (fragmentHttpCallback != null) {
            responseBody.page(getClass().getCanonicalName());
            fragmentHttpCallback.onFragmentHttpFailure(responseBody);
        }
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
            getActivity().overridePendingTransition(R.anim.android_anim_right_in, R.anim.android_anim_left_exit);
        }
    }

    /**
     * 页面结束动画
     */
    public void overridePendingFinishTransition() {
        if (isOverridePendingFinishTransition()) {
            getActivity().overridePendingTransition(R.anim.android_anim_left_in, R.anim.android_anim_right_exit);
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
        Intent intent = new Intent(getContext(), cls);
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
        Intent intent = new Intent(getContext(), cls);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 设置Fragment容器视图id
     *
     * @return
     */
    protected int getContainerViewId() {
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
     * @param fragment 类名
     */
    protected void addFragment(CoreFragment fragment) {
        addFragment(fragment, null);
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     */
    protected void addFragment(Class fragmentClass, Bundle args) {
        getFragmentController().add(fragmentClass, args, null, this, getContainerViewId());
    }

    /**
     * 添加Fragment
     *
     * @param fragment 类名
     * @param args     参数
     */
    protected void addFragment(CoreFragment fragment, Bundle args) {
        getFragmentController().add(fragment, args, null, this, getContainerViewId());
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass 类名
     * @param params        参数
     */
    protected void addFragment(Class fragmentClass, Object params) {
        getFragmentController().add(fragmentClass, null, params, this, getContainerViewId());
    }

    /**
     * 添加Fragment
     *
     * @param fragment 类名
     * @param params   参数
     */
    protected void addFragment(CoreFragment fragment, Object params) {
        getFragmentController().add(fragment, null, params, this, getContainerViewId());
    }

    /**
     * 添加Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     * @param params        参数
     */
    protected void addFragment(Class fragmentClass, Bundle args, Object params) {
        getFragmentController().add(fragmentClass, args, params, this, getContainerViewId());
    }

    /**
     * 添加Fragment
     *
     * @param fragment 类名
     * @param args     参数
     * @param params   参数
     */
    protected void addFragment(CoreFragment fragment, Bundle args, Object params) {
        getFragmentController().add(fragment, args, params, this, getContainerViewId());
    }


    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     */
    protected void replaceFragment(Class fragmentClass) {
        getFragmentController().replace(fragmentClass, null, null, this, getContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     */
    protected void replaceFragment(Class fragmentClass, Bundle args) {
        getFragmentController().replace(fragmentClass, args, null, this, getContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragment 类名
     * @param args     参数
     */
    protected void replaceFragment(CoreFragment fragment, Bundle args) {
        getFragmentController().replace(fragment, args, null, this, getContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     * @param params        参数
     */
    protected void replaceFragment(Class fragmentClass, Object params) {
        getFragmentController().replace(fragmentClass, null, params, this, getContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragment 类名
     * @param params   参数
     */
    protected void replaceFragment(CoreFragment fragment, Object params) {
        getFragmentController().replace(fragment, null, params, this, getContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragmentClass 类名
     * @param args          参数
     * @param params        参数
     */
    protected void replaceFragment(Class fragmentClass, Bundle args, Object params) {
        getFragmentController().replace(fragmentClass, args, params, this, getContainerViewId());
    }

    /**
     * 替换Fragment
     *
     * @param fragment 类名
     * @param args     参数
     * @param params   参数
     */
    protected void replaceFragment(CoreFragment fragment, Bundle args, Object params) {
        getFragmentController().replace(fragment, args, params, this, getContainerViewId());
    }

    /**
     * Fragment网络失败
     *
     * @param responseBody
     */
    @Override
    public void onFragmentHttpFailure(ResponseBody responseBody) {
        if (fragmentHttpCallback != null) {
            responseBody.page(getClass().getCanonicalName());
            fragmentHttpCallback.onFragmentHttpFailure(responseBody);
        }
    }

    /**
     * Fragment成功
     *
     * @param responseBody
     */
    @Override
    public void onFragmentHttpSucceed(ResponseBody responseBody) {
        if (fragmentHttpCallback != null) {
            responseBody.page(getClass().getCanonicalName());
            fragmentHttpCallback.onFragmentHttpSucceed(responseBody);
        }
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
