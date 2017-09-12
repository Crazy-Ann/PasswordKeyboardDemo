package com.yjt.password.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.view.View;

public class ViewUtil {

    private static ViewUtil mViewUtil;

    private ViewUtil() {
        // cannot be instantiated
    }

    public static synchronized ViewUtil getInstance() {
        if (mViewUtil == null) {
            mViewUtil = new ViewUtil();
        }
        return mViewUtil;
    }

    public static void releaseInstance() {
        if (mViewUtil != null) {
            mViewUtil = null;
        }
    }

    public <V> V findView(View rootView, @IdRes int resId) {
        return (V) rootView.findViewById(resId);
    }

    public int dp2px(Context ctx, float dpValue) {
        return (int) (dpValue * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    public int px2dp(Context ctx, float pxValue) {
        return (int) (pxValue / ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    public float dp2px(Resources resources, float dp) {
        return dp * resources.getDisplayMetrics().density + 0.5f;
    }

    public float sp2px(Resources resources, float sp) {
        return sp * resources.getDisplayMetrics().scaledDensity;
    }

    public int px2sp(Context context, float px) {
        return (int) (px / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    public int px2sp(Resources resources, float px) {
        return (int) (px / resources.getDisplayMetrics().scaledDensity + 0.5f);
    }
}
