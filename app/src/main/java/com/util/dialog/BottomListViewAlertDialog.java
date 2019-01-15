package com.util.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.util.widget.BaseTextView;
import com.util.widget.R;


public class BottomListViewAlertDialog {

    private AlertDialog mAlertDialog;
    private ListView mListView;
    private View bottomVisibility;
    private BaseTextView mTitleView;
    private BaseTextView mCancelView;
    private BaseTextView mDefineView;
    private IOnBottomListClickListener mIOnBottomListClickListener;


    public void initList(Activity context) {
        View view = this.init(context);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(view);
        this.mAlertDialog = mBuilder.create();
        this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != mIOnBottomListClickListener) {
                    mIOnBottomListClickListener.onDismiss();
                }
            }
        });

    }

    public void initView(Activity context, View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(view);
        this.mAlertDialog = mBuilder.create();
        this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != mIOnBottomListClickListener) {
                    mIOnBottomListClickListener.onDismiss();
                }
            }
        });
    }

    public void show() {
        if (null != mAlertDialog) {
            mAlertDialog.show();
            Window window = mAlertDialog.getWindow();//获取当前的Window对象，然后下面进行窗口属性的设置
            if (null != window) {
                window.setBackgroundDrawableResource(android.R.color.transparent);//这个很重要，将背景设为透明
                // 这样子 第二和第三个按钮的空隙才会显示出来
                window.setGravity(Gravity.BOTTOM);//这个也很重要，将弹出菜单的位置设置为底部
                window.setWindowAnimations(R.style.animation_bottom_menu);//菜单进入和退出屏幕的动画，实现了上下滑动的动画效果
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);//设置菜单的尺寸     }
            }

        }
    }

    public void dismiss() {
        if (null != mAlertDialog)
            mAlertDialog.dismiss();
    }

    /**
     * 设置
     */
    public void setAdapter(ListAdapter adapter) {
        this.mListView.setAdapter(adapter);
    }

    public ListView getListView() {
        return mListView;
    }

    public void setTitleText(String text) {
        this.mTitleView.setVisibility(View.VISIBLE);
        this.mTitleView.setText(text);
    }

    public void setCancelVisibility(boolean v) {
        this.mCancelView.setVisibility(v ? View.VISIBLE : View.GONE);
    }

    public void bottomVisibility(boolean v) {
        this.bottomVisibility.setVisibility(v ? View.VISIBLE : View.GONE);
    }

    public void setDefineVisibility(boolean v) {
        this.mDefineView.setVisibility(v ? View.VISIBLE : View.GONE);
    }

    private View init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.util_bottom_list_view_layout, null);
        this.mListView = inflate.findViewById(R.id.listView);
        this.bottomVisibility = inflate.findViewById(R.id.bottomVisibility);
        this.mTitleView = inflate.findViewById(R.id.titleView);
        this.mTitleView.setVisibility(View.GONE);
        this.mCancelView = inflate.findViewById(R.id.cancelView);
        this.mDefineView = inflate.findViewById(R.id.defineView);
        this.mCancelView.setOnClickListener(onClickListener);
        this.mDefineView.setOnClickListener(onClickListener);

        return inflate;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.cancelView) {
                if (null != mIOnBottomListClickListener)
                    mIOnBottomListClickListener.onCancel();

            } else if (i == R.id.defineView) {
                if (null != mIOnBottomListClickListener)
                    mIOnBottomListClickListener.onDefine();

            }
            if (null != mAlertDialog)
                mAlertDialog.dismiss();
        }
    };

    /**
     * 回调
     */
    public void setIOnBottomListClickrListener(IOnBottomListClickListener listener) {
        this.mIOnBottomListClickListener = listener;
    }

    /**
     * 监听器
     */
    public interface IOnBottomListClickListener {
        void onCancel();

        void onDefine();

        void onDismiss();
    }
}
