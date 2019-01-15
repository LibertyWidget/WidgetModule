package com.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class ShareUtils {
    public static final String NAME = "config";

    /**
     * 存储String类型的值
     *
     * @param mContext this
     * @param key      key值
     * @param value    要存储的String值
     */
    public static void putString(Context mContext, String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedPreferences.edit().putString(key, value).apply();
        } else {
            sharedPreferences.edit().putString(key, value).commit();
        }
    }

    /**
     * 获取String类型的值
     *
     * @param mContext this
     * @param key      key
     * @param defValue 默认值
     * @return
     */
    public static String getString(Context mContext, String key, String defValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * 存储Int类型的值
     *
     * @param mContext this
     * @param key      key
     * @param value    要存储的Int值
     */
    public static void putInt(Context mContext, String key, int value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedPreferences.edit().putInt(key, value).apply();
        } else {
            sharedPreferences.edit().putInt(key, value).commit();
        }
    }

    /**
     * 获取Int类型的值
     *
     * @param mContext this
     * @param key      key
     * @param defValue 默认值
     * @return
     */
    public static int getInt(Context mContext, String key, int defValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defValue);
    }


    /**
     * 存储Boolean类型的值
     *
     * @param mContext this
     * @param key      key
     * @param value    要存储Boolean值
     */
    public static void putBoolean(Context mContext, String key, boolean value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedPreferences.edit().putBoolean(key, value).apply();
        } else {
            sharedPreferences.edit().putBoolean(key, value).commit();
        }
    }

    /**
     * 获取Boolean类型的值
     *
     * @param mContext this
     * @param key      key
     * @param defValue 默认值
     * @return
     */
    public static boolean getBoolean(Context mContext, String key, Boolean defValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 删除 单个 key
     */
    public static void deleShare(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedPreferences.edit().remove(key).apply();
        } else {
            sharedPreferences.edit().remove(key).commit();
        }
    }

    /**
     * 删除全部 key
     */

    public static void deleAll(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedPreferences.edit().clear().apply();
        } else {
            sharedPreferences.edit().clear().commit();
        }
    }
}
