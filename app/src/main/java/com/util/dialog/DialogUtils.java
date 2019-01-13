package com.util.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {


    private Context mContext;
    private LoadingDialog loadingDialog;

    public DialogUtils(Context context) {
        this.mContext = context;
    }

    /**
     * 显示菊花以及菊花下方的文字提示，点击外部不可取消，点击返回可以取消
     * 不接收回调接收回调
     */
    public void showLoadingWithLabel(String text) {
        loadingDialog = LoadingDialog.create(mContext).setLabel(text).show();
    }

    /**
     * 显示菊花以及菊花下方的文字提示，点击外部不可取消，点击返回可以取消
     * 接收回调
     */
    public void showLoadingWithLabel(String text, DialogInterface.OnCancelListener onCancelListener) {
        loadingDialog = LoadingDialog.create(mContext).setLabel(text).setCancellableListener(onCancelListener).show();
    }

    /**
     * @Param cancelable 设置为false 返回按钮不可用 若为true 直接调用{@link #showLoadingWithLabel}的监听方法
     * 显示菊花以及菊花下方的文字提示，点击外部不可取消，点击返回可以取消
     * 不接收回调接收回调
     */
    public void showLoadingWithLabel(String text, boolean cancelable) {
        loadingDialog = LoadingDialog.create(mContext).setLabel(text).setCancellable(cancelable).show();
    }

    /**
     * 仅显示一个菊花 不接收取消回调
     * 默认点击外部不可取消 ，点击返回按钮可以dismiss
     */
    public void showLoading() {
        loadingDialog = LoadingDialog.create(mContext).show();
    }

    /**
     * 仅显示一个菊花 并且有  cancel回调
     * 默认点击外部不可取消 ，点击返回按钮可以dismiss
     */
    public void showLoading(DialogInterface.OnCancelListener onCancelListener) {
        loadingDialog = LoadingDialog.create(mContext).setCancellableListener(onCancelListener).show();
    }

    /**
     * @Param cancelable 设置为false 返回按钮不可用 若为true 直接调用{@link #showLoading}的监听方法
     * 显示菊花，点击外部不可取消
     * 不接收回调
     */
    public void showLoading(boolean cancelable) {
        loadingDialog = LoadingDialog.create(mContext).setCancellable(cancelable).show();
    }

    /**
     * dismiss
     */
    public void dismissLoading() {
        if (loadingDialog != null)
            loadingDialog.dismiss();
    }


    /**
     * 无title 一个positivebutton  点击外部以及返回按钮均不可取消
     * 点击button消失
     *
     * @param message
     * @param textPositiveButton
     * @param onDismissListener  null时不监听dismiss
     */
    public void showAlertDialog(String message, String textPositiveButton, DialogInterface.OnDismissListener onDismissListener) {
        if (!((Activity) mContext).isFinishing()) {

            new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setMessage(message)
                    .setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnDismissListener(onDismissListener)
                    .show();
        }

    }
}
