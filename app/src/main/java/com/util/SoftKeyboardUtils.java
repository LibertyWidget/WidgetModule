package com.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SoftKeyboardUtils {

    /**
     * 关闭软键盘
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void closeSoftKeyBoard(Context context, EditText editText) {
        if (context != null && editText != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    /**
     * 关闭软键盘
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void closeSoftKeyBoard(Context context) {
        if (context != null && ((Activity) context).getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 触发软键盘开关
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void toggleSoftInput(Context context) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开键盘
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void showSoftInput(Context context, View view) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }
}
