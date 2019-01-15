package com.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.util.widget.R;

import java.lang.reflect.Field;

public class ToastUtils {
    private volatile static ToastUtils globalBoast = null;
    private final static int posY = 300;
    private Toast internalToast;

    private ToastUtils(Toast toast) {
        if (toast == null) {
            throw new NullPointerException("Boast.Boast(Toast) requires a non-null parameter.");
        }
        internalToast = toast;
    }
    private static ToastUtils makeText(Context context, CharSequence text, int duration) {
        return new ToastUtils(innerCreator(context, text, duration));
    }

    private static ToastUtils makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return new ToastUtils(innerCreator(context, resId, duration));
    }
    private static ToastUtils makeText(Context context, CharSequence text) {
        return new ToastUtils(innerCreator(context, text, Toast.LENGTH_SHORT));
    }
    private static ToastUtils makeText(Context context, int resId) throws Resources.NotFoundException {
        return new ToastUtils(innerCreator(context, resId, Toast.LENGTH_SHORT));
    }

    private static ToastUtils make(Context context, View vContent, int duration) {
        return new ToastUtils(innerCreator(context, vContent, duration));
    }

    @SuppressLint("InflateParams")
    private static Toast innerCreator(Context context, int resId, int duration) {
        Toast returnValue = null;
        if (context != null)
            returnValue = innerCreator(context, context.getString(resId), duration);
        return returnValue;
    }
    private static Toast innerCreator(Context context, CharSequence info, int duration) {
        Toast returnValue = null;
        if (context != null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View vContent = inflater.inflate(R.layout.common_toast_layout, null);
            TextView tvContent = vContent.findViewById(R.id.tvContent);
            tvContent.setText(info);
            tvContent.bringToFront();
            returnValue = new Toast(context.getApplicationContext());
            returnValue.setGravity(Gravity.BOTTOM, 0, posY);
            returnValue.setDuration(duration);
            returnValue.setView(vContent);

            innerFixToastFor25(returnValue, context);
        }
        return returnValue;
    }

    private static Toast innerCreator(Context context, View vContent, int duration) {
        Toast returnValue = null;
        if (context != null && vContent != null) {
            returnValue = new Toast(context.getApplicationContext());
            returnValue.setGravity(Gravity.CENTER, 0, posY);
            returnValue.setDuration(duration);
            returnValue.setView(vContent);
            innerFixToastFor25(returnValue, context);
        }
        return returnValue;
    }
    private static void innerFixToastFor25(Toast toast, Context context) {
        if (Build.VERSION.SDK_INT == 25 && toast != null && toast.getView() != null) {
            Context newContext = new SafeToastContext(context, toast);
            try {
                Field field = View.class.getDeclaredField("mContext");
                field.setAccessible(true);
                field.set(toast.getView(), newContext);
            } catch (Throwable ignore) {
            }
        }
    }
    private static void showText(Context context, CharSequence text, int duration) {
        if (!TextUtils.isEmpty(text) && !SystemUtils.isAppBroughtToBackground(context)) {
            ToastUtils.makeText(context, text, duration).show();
        }
    }
    public static void showText(Context context, CharSequence text) {
        if (!TextUtils.isEmpty(text) && !SystemUtils.isAppBroughtToBackground(context)) {
            ToastUtils.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }
    public static void showText(Context context, int resId) throws Resources.NotFoundException {
        if (!SystemUtils.isAppBroughtToBackground(context)) {
            ToastUtils.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }

    private static void show(Context context, View view, int duration) {
        if (context != null && view != null)
            ToastUtils.make(context, view, duration).show();
    }

    private void cancel() {
        internalToast.cancel();
    }

    private void show() {
        show(true);
    }

    private void show(boolean cancelCurrent) {
        if (cancelCurrent && (globalBoast != null)) {
            globalBoast.cancel();
        }
        globalBoast = this;

        try {
            internalToast.show();
        } catch (Exception ignore) {

        }
    }

    public static class SafeToastContext extends ContextWrapper {

        SafeToastContext(@NonNull Context base, @NonNull Toast toast) {
            super(base);
        }

        @Override
        public Context getApplicationContext() {
            return new ApplicationContextWrapper(getBaseContext().getApplicationContext());
        }

        private final static class ApplicationContextWrapper extends ContextWrapper {

            private ApplicationContextWrapper(@NonNull Context base) {
                super(base);
            }

            @Override
            public Object getSystemService(@NonNull String name) {
                if (Context.WINDOW_SERVICE.equals(name)) {
                    return new WindowManagerWrapper((WindowManager) getBaseContext().getSystemService(name));
                }
                return super.getSystemService(name);
            }
        }

        private final static class WindowManagerWrapper implements WindowManager {

            private static final String TAG = "WindowManagerWrapper";
            private final @NonNull
            WindowManager base;


            private WindowManagerWrapper(@NonNull WindowManager base) {
                this.base = base;
            }


            @Override
            public Display getDefaultDisplay() {
                return base.getDefaultDisplay();
            }


            @Override
            public void removeViewImmediate(View view) {
                base.removeViewImmediate(view);
            }


            @Override
            public void addView(View view, ViewGroup.LayoutParams params) {
                try {
                    base.addView(view, params);
                } catch (BadTokenException ignore) {
                } catch (Throwable ignored) {
                }
            }

            @Override
            public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
                base.updateViewLayout(view, params);
            }


            @Override
            public void removeView(View view) {
                base.removeView(view);
            }
        }
    }
}
