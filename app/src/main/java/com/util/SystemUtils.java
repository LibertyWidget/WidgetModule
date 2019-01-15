package com.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.util.widget.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SystemUtils {

    public static boolean isAppBroughtToBackground(Context context) {
        boolean isInBackground = false;
        if (context == null)
            return isInBackground;

        Object systemService = context.getSystemService(Context.ACTIVITY_SERVICE);
        if (systemService instanceof ActivityManager) {
            isInBackground = true;
            ActivityManager am = (ActivityManager) systemService;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                if (!CollectionUtils.isEmpty(runningProcesses)) {
                    String packageName = context.getPackageName();
                    for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                        //前台程序
                        if (processInfo != null && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            String[] pkgList = processInfo.pkgList;
                            if (pkgList != null) {
                                for (String activeProcess : pkgList) {
                                    if (TextUtils.equals(packageName, activeProcess)) {
                                        isInBackground = false;
                                        break;
                                    }
                                }
                            }
                        }

                        if (!isInBackground)
                            break;
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskList = null;
                try {
                    taskList = am.getRunningTasks(1);
                } catch (SecurityException se) {
                    taskList = null;
                }
                if (!CollectionUtils.isEmpty(taskList)) {
                    ActivityManager.RunningTaskInfo runningTaskInfo = taskList.get(0);
                    if (runningTaskInfo != null) {
                        ComponentName componentInfo = runningTaskInfo.topActivity;
                        if (TextUtils.equals(componentInfo.getPackageName(), context.getPackageName()))
                            isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }

    public static boolean isAppRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        if (!CollectionUtils.isEmpty(list)) {
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info != null && (TextUtils.equals(BuildConfig.APPLICATION_ID, info.topActivity.getPackageName()) || TextUtils.equals(BuildConfig.APPLICATION_ID, info.baseActivity.getPackageName()))) {
                    isAppRunning = true;
                    break;
                }
            }
        }
        return isAppRunning;
    }

    /**
     * 设置状态栏的颜色
     */
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));
                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Dialog状态颜色
     */
    public static void setWindowStatusBarColor(Dialog dialog, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = dialog.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(dialog.getContext().getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取VersionName(版本名称)
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = getPackageManager(context);
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(context), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取VersionCode(版本号)
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = getPackageManager(context);
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(context), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取所有安装的应用程序,不包含系统应用
     */
    public static List<PackageInfo> getInstalledPackages(Context context) {
        PackageManager packageManager = getPackageManager(context);
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<PackageInfo> packageInfoList = new ArrayList<>();
        for (int i = 0; i < packageInfos.size(); i++) {
            if ((packageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                packageInfoList.add(packageInfos.get(i));
            }
        }
        return packageInfoList;
    }

    /**
     * 获取应用程序的icon图标
     *
     * @return 当包名错误时，返回null
     */
    public static Drawable getApplicationIcon(Context context) {
        PackageManager packageManager = getPackageManager(context);
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(context), 0);
            return packageInfo.applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 启动安装应用程序
     *
     * @param path 应用程序路径
     */
    public static void installApk(Activity activity, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }

    /**
     * 获取PackageManager对象
     */
    private static PackageManager getPackageManager(Context context) {
        return context.getPackageManager();
    }
}