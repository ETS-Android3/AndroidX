package com.androidx.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidx.util.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Relin
 * Describe:权限管理
 * Date:2021/01/01 16:25
 */
public class PermissionManager {

    /**
     * 存储读写权限
     */
    public final static String PERMISSIONS_STORAGE[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    /**
     * 存储+相机权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    /**
     * 存储+电话权限
     */
    public final static String PERMISSIONS_STORAGE_CALL[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
    };

    /**
     * 存储+录音权限+录音
     */
    public final static String PERMISSIONS_STORAGE_RECORD[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };

    /**
     * 存储+照相机+电话权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_CALL[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
    };

    /**
     * 存储+照相机+电话+震动权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_CALL_VIBRATE[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.VIBRATE,
    };

    /**
     * 存储+照相机+拨打电话权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_CALL_RECORD[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
    };

    /**
     * 存储+照相机+拨打电话+录音+震动权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_CALL_RECORD_VIBRATE[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.VIBRATE,
    };


    /**
     * 存储+照相机+录音权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_RECORD[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    /**
     * 存储+照相机+拨打电话+录音+震动权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_RECORD_VIBRATE[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.VIBRATE,
    };

    /**
     * 存储+照相机+拨打电话+录音+震动权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_RECORD_CALL[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
    };

    /**
     * 存储+照相机+拨打电话+录音+震动权限
     */
    public final static String PERMISSIONS_STORAGE_CAMERA_RECORD_CALL_VIBRATE[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.VIBRATE,
    };

    /**
     * 定位权限
     *
     * @return
     */
    public final static String[] PERMISSIONS_LOCATION() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        } else {
            return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        }
    }


    /**
     * 请求权限代码
     */
    public final static int REQUEST_CODE = 0x3245;

    /**
     * 是否需要检查运行时权限
     *
     * @return
     */
    public static boolean isRequestPermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查运行时权限,逻辑会判断是否需要请求权限的SDK版本
     *
     * @param activity    Activity页面
     * @param permissions 权限
     * @param requestCode 请求代码
     * @param listener    请求监听
     */
    public static void checkPermissions(AppCompatActivity activity, String[] permissions, int requestCode, OnRequestPermissionsListener listener) {
        checkClassPermissions(activity, permissions, requestCode, listener);
    }

    /**
     * 检查运行时权限
     *
     * @param fragment
     * @param permissions 权限
     * @param requestCode 请求代码
     * @param listener    请求监听
     */
    public static void checkPermissions(Fragment fragment, String[] permissions, int requestCode, OnRequestPermissionsListener listener) {
        checkClassPermissions(fragment, permissions, requestCode, listener);
    }

    /**
     * 检查运行时权限
     *
     * @param obj
     * @param permissions 权限
     * @param requestCode 请求代码
     * @param listener    请求监听
     */
    private static void checkClassPermissions(Object obj, String[] permissions, int requestCode, OnRequestPermissionsListener listener) {
        if (!isRequestPermissions()) {
            if (listener != null) {
                int grantResults[] = new int[permissions.length];
                for (int i = 0; i < permissions.length; i++) {
                    grantResults[i] = PackageManager.PERMISSION_DENIED;
                }
                listener.onRequestPermissionsSucceed(requestCode, permissions, grantResults);
            }
            return;
        }
        AppCompatActivity activity = null;
        Fragment fragment = null;
        if (obj instanceof AppCompatActivity) {
            activity = (AppCompatActivity) obj;
        }
        if (obj instanceof Fragment) {
            fragment = (Fragment) obj;
        }
        if (permissions == null) {
            return;
        }
        if (permissions.length == 0) {
            return;
        }
        List<String> deniedPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            Context context = activity == null ? fragment.getContext() : activity;
            if (ContextCompat.checkSelfPermission(context, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                deniedPermissionList.add(permissions[i]);
            }
        }
        int deniedSize = Size.of(deniedPermissionList);
        if (deniedSize == 0) {
            int grantResults[] = new int[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                grantResults[i] = PackageManager.PERMISSION_DENIED;
            }
            if (listener != null) {
                listener.onRequestPermissionsSucceed(requestCode, permissions, grantResults);
            }
        } else {
            String[] deniedPermissions = new String[deniedSize];
            for (int i = 0; i < deniedSize; i++) {
                deniedPermissions[i] = deniedPermissionList.get(i);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity != null) {
                    ActivityCompat.requestPermissions(activity, deniedPermissions, requestCode);
                }
                if (fragment != null) {
                    fragment.requestPermissions(deniedPermissions, requestCode);
                }
            }
        }
    }


    /**
     * 请求权限处理
     *
     * @param requestCode  请求代码
     * @param permissions  权限
     * @param grantResults 权限状态
     * @param listener     请求监听
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, OnRequestPermissionsListener listener) {
        if (listener == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE: {
                boolean isGrant = true;
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            isGrant = false;
                        }
                    }
                }
                //成功的
                if (grantResults.length > 0 && isGrant) {
                    listener.onRequestPermissionsSucceed(requestCode, permissions, grantResults);
                } else {
                    //失败
                    listener.onRequestPermissionsFailed(requestCode, permissions, grantResults);
                }
                return;
            }
        }
    }

    /**
     * 权限请求监听
     */
    public interface OnRequestPermissionsListener {

        /**
         * 请求权限成功
         *
         * @param requestCode  请求码
         * @param permissions  权限集合
         * @param grantResults 获取权限结果
         */
        void onRequestPermissionsSucceed(int requestCode, String[] permissions, int[] grantResults);

        /**
         * 请求权限失败
         *
         * @param requestCode  请求码
         * @param permissions  权限集合
         * @param grantResults 获取权限结果
         */
        void onRequestPermissionsFailed(int requestCode, String[] permissions, int[] grantResults);
    }

}
