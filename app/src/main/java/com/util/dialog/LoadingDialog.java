package com.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.util.widget.R;

public class LoadingDialog {


    private ProgressDialog mProgressDialog;
    private float mDimAmount;
    private int mWindowColor;
    private float mCornerRadius;


    public LoadingDialog(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mDimAmount = 0;
        mWindowColor = context.getResources().getColor(R.color.colorLoadingProgressBg);
        mCornerRadius = 10;
        ImageView view = new SpinView(context);
        mProgressDialog.setView(view);
    }


    public static LoadingDialog create(Context context) {
        return new LoadingDialog(context);
    }


    /**
     * 设置背景透明度
     *
     * @param dimAmount
     * @return
     */
    public LoadingDialog setDimAmount(float dimAmount) {
        if (dimAmount >= 0 && dimAmount <= 1) {
            mDimAmount = dimAmount;
        }
        return this;
    }


    /**
     * @param color ARGB color
     * @return Current HUD
     * @deprecated As of release 1.1.0, replaced by {@link #setBackgroundColor(int)}
     */
    @Deprecated
    public LoadingDialog setWindowColor(int color) {
        mWindowColor = color;
        return this;
    }

    /**
     * Specify the HUD background color
     *
     * @param color ARGB color
     * @return Current HUD
     */
    public LoadingDialog setBackgroundColor(int color) {
        mWindowColor = color;
        return this;
    }

    /**
     * Specify corner radius of the HUD (default is 10)
     *
     * @param radius Corner radius in dp
     * @return Current HUD
     */
    public LoadingDialog setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }


    /**
     * 设置下方文字 默认白色
     *
     * @param label
     * @return
     */
    public LoadingDialog setLabel(String label) {
        mProgressDialog.setLabel(label);
        return this;
    }

    /**
     * 设置文字及其颜色
     *
     * @param label
     * @param color
     * @return
     */
    public LoadingDialog setLabel(String label, int color) {
        mProgressDialog.setLabel(label, color);
        return this;
    }


    /**
     * Provide a custom view to be displayed.
     *
     * @param view Must not be null
     * @return Current HUD
     */
    public LoadingDialog setCustomView(View view) {
        if (view != null) {
            mProgressDialog.setView(view);
        } else {
            throw new RuntimeException("Custom view must not be null!");
        }
        return this;
    }

    /**
     * 设置是否可取消
     *
     * @param isCancellable
     * @return
     */
    public LoadingDialog setCancellable(boolean isCancellable) {
        mProgressDialog.setCancelable(isCancellable);
        mProgressDialog.setOnCancelListener(null);
        return this;
    }

    /**
     * 设置取消监听
     *
     * @param listener
     * @return
     */
    public LoadingDialog setCancellableListener(DialogInterface.OnCancelListener listener) {
        mProgressDialog.setCancelable(null != listener);
        mProgressDialog.setOnCancelListener(listener);
        return this;
    }


    public LoadingDialog show() {
        if (!isShowing()) {
            mProgressDialog.show();
        }
        return this;
    }

    public boolean isShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }

    private class ProgressDialog extends Dialog {

        private View mView;
        private TextView mLabelText;
        private String mLabel;
        private FrameLayout mCustomViewContainer;
        private BackgroundLayout mBackgroundLayout;
        private int mLabelColor = Color.WHITE;


        public ProgressDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.util_loading_progress_layout);

            Window window = getWindow();
            if (null != window) {
                window.setBackgroundDrawable(new ColorDrawable(0));
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.dimAmount = mDimAmount;
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
            }
            setCanceledOnTouchOutside(false);
            initViews();
        }

        private void initViews() {
            mBackgroundLayout = findViewById(R.id.background);
            mBackgroundLayout.setBaseColor(mWindowColor);
            mBackgroundLayout.setCornerRadius(mCornerRadius);
            mCustomViewContainer = findViewById(R.id.container);
            addViewToFrame(mView);
            mLabelText = findViewById(R.id.label);
            setLabel(mLabel, mLabelColor);
        }

        private void addViewToFrame(View view) {
            if (view == null) return;
            int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapParam, wrapParam);
            mCustomViewContainer.addView(view, params);
        }


        public void setView(View view) {
            if (view != null) {

                mView = view;
                if (isShowing()) {
                    mCustomViewContainer.removeAllViews();
                    addViewToFrame(view);
                }
            }
        }

        public void setLabel(String label) {
            mLabel = label;
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }


        public void setLabel(String label, int color) {
            mLabel = label;
            mLabelColor = color;
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setTextColor(color);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }

    }
}
