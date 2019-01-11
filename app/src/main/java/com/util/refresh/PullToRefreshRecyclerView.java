package com.util.refresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 封装了RecycleView的下拉刷新
 */
public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {
	public PullToRefreshRecyclerView(Context context) {
		super(context);
	}

	public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshRecyclerView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}
	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected RecyclerView createRefreshableView(Context context,
												 AttributeSet attrs) {
		return new RecyclerView(context, attrs);
	}
	@Override
	protected boolean isReadyForPullStart() {
		return isFirstItemVisible();
	}

	@Override
	protected boolean isReadyForPullEnd() {
		return isLastItemVisible();
	}

	/**
	 * 判断第一个item是否可见了
	 */
	private boolean isFirstItemVisible() {
		final RecyclerView.Adapter<?> adapter = getRefreshableView().getAdapter();

		// 如果未设置Adapter或者Adapter没有数据可以下拉刷新
		if (null == adapter || adapter.getItemCount() == 0) {
			return true;
		} else {
			// 第一个条目完全展示,可以刷新
			if (getFirstVisiblePosition() == 0) {
				return mRefreshableView.getChildAt(0).getTop() >= mRefreshableView
						.getTop();
			}
		}
		return false;
	}
	/**
	 *  获取第一个可见子View的位置下标
	 */
	private int getFirstVisiblePosition() {
		View firstVisibleChild = mRefreshableView.getChildAt(0);
		return firstVisibleChild != null ? mRefreshableView
				.getChildAdapterPosition(firstVisibleChild) : -1;
	}
	/**
	 *  判断最后一个item是否完全可见
	 */
	private boolean isLastItemVisible() {
		final RecyclerView.Adapter<?> adapter = getRefreshableView().getAdapter();

		// 如果未设置Adapter或者Adapter没有数据可以上拉刷新
		if (null == adapter || adapter.getItemCount() == 0) {
			return true;

		} else {
			// 最后一个条目View完全展示,可以刷新
			int lastVisiblePosition = getLastVisiblePosition();
			if(lastVisiblePosition >= mRefreshableView.getAdapter().getItemCount()-1) {
				return mRefreshableView.getChildAt(
						mRefreshableView.getChildCount() - 1).getBottom() <= mRefreshableView
						.getBottom();
			}
		}
		return false;
	}
	private int getLastVisiblePosition() {
		View lastVisibleChild = mRefreshableView.getChildAt(mRefreshableView
				.getChildCount() - 1);
		return lastVisibleChild != null ? mRefreshableView
				.getChildAdapterPosition(lastVisibleChild) : -1;
	}

}
