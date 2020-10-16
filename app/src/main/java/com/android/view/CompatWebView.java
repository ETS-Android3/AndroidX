package com.android.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 适配版本WebView
 */
public class CompatWebView extends WebView {

    public CompatWebView(Context context) {
        super(getFixedContext(context));
    }

    public CompatWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
    }

    public CompatWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.createConfigurationContext(new Configuration());
        } else {
            return context;
        }
    }


}
