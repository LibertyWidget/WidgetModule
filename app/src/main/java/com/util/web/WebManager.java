package com.util.web;

import android.content.Context;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

public class WebManager {
    private final static WebManager INSTANCE = new WebManager();

    private WebManager() {

    }

    public static WebManager $() {
        return INSTANCE;
    }

    public void init(Context context) {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("tag", " Initialization - whether > was successfully initialized " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                Log.e("tag", "Initialization --> failed");
            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.e("tag", "onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                Log.e("tag", "onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                Log.e("tag", "onDownloadProgress" + i);
            }
        });
        TbsDownloader.needDownload(context, false);
        //x5内核初始化接口
        QbSdk.initX5Environment(context, cb);
    }

}
