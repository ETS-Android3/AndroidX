package com.androidx.util;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * Author: Relin
 * Describe: 动画
 * Date:2020/6/29 16:06
 */
public class Animation {

    /**
     * 位移动画
     *
     * @param view 控件
     * @param endX 结束位置
     */
    public static void startTranslate(final View view, float endX) {
        float x = view.getX();
        ValueAnimator animator = ValueAnimator.ofFloat(x, endX);
        animator.setTarget(view);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

}
