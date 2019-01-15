package com.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.util.widget.BuildConfig;

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
}