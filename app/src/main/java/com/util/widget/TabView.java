package com.util.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.util.widget.R;

public class TabView extends LinearLayout {
    public TabView(Context context) {
        super(context);
        init(context);
    }

    public TabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public TabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ImageView ivTabView;
    private BaseTextView ivTabTextView;

    public void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.tab_view_layout, null);
        this.ivTabView = inflate.findViewById(R.id.ivTabView);
        this.ivTabTextView = inflate.findViewById(R.id.ivTabTextView);
        this.addView(inflate);
    }

    /**
     * 选中
     *
     * @param resId 图标
     * @param resid 文字
     */
    public void setTabRes(@DrawableRes int resId, @StringRes int resid, @ColorInt int color) {
        if (null != ivTabView) {
            ivTabView.setImageResource(resId);
        }
        if (null != ivTabTextView) {
            ivTabTextView.setText(resid);
            ivTabTextView.setTextColor(color);
        }
    }
}
