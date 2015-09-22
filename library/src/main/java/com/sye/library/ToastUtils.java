package com.sye.library;

import android.content.Context;

/**
 * Created by Sye on 2015/9/22.
 */
public class ToastUtils {

    private static HeaderToast headerToast;

    public static void showWithCustomIcon(final Context context, final int icon, final String toast){
        headerToast = new HeaderToast(context);
        headerToast.showWithCustomIcon(icon, toast);
    }

    public static void show(final Context context, final String toast){
        headerToast = new HeaderToast(context);
        headerToast.show(toast);
    }
}
