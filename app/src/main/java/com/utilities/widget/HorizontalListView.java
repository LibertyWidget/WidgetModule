package com.utilities.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.Queue;

public class HorizontalListView extends AdapterView<ListAdapter> {
    public boolean mAlwaysOverrideTouch = true;
    protected ListAdapter mAdapter;
    private int mLeftViewIndex = -1;
    private int mRightViewIndex = 0;
    protected int mCurrentX;
    protected int mNextX;
    private int mMaxX = Integer.MAX_VALUE;
    private int mDisplayOffset = 0;
    protected Scroller mScroller;
    private GestureDetector mGesture;
    private Queue<View> mRemovedViewQueue = new LinkedList<>();
    private OnItemSelectedListener mOnItemSelected;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    private boolean mDataChanged = false;

    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private synchronized void initView() {
        mLeftViewIndex = -1;
        mRightViewIndex = 0;
        mDisplayOffset = 0;
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        mScroller = new Scroller(getContext());
        mGesture = new GestureDetector(getContext(), mOnGesture);
        mGesture.setIsLongpressEnabled(true);
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelected = listener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClicked = listener;
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClicked = listener;
    }

    private DataSetObserver mDataObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            Log.d("test_tag", "function: DataSetObserver.onChanged");
            synchronized (HorizontalListView.this) {
                mDataChanged = true;
            }
            invalidate();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            Log.d("test_tag", "function: DataSetObserver.onInvalidated");
            reset();
            invalidate();
            requestLayout();
        }

    };
    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataObserver);
        reset();
    }

    private synchronized void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public void setSelection(int position) {
        if (getChildCount() == 0)
            return;
        Log.d("test_tag", "function: firstTimeScroll(x=" + String.valueOf(position) + ")");
        if (mScroller.computeScrollOffset()) {
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
        }
        Log.d("test_tag", "mnextx=" + mNextX);
        if (mNextX <= 0) {
            mNextX = 0;
            mScroller.forceFinished(true);
        }
        int childWidth = getChildAt(0).getMeasuredWidth();
        int curScrollWidth = childWidth * position;
        int maxScrollWidth;
        if (curScrollWidth >= mMaxX) {
            maxScrollWidth = curScrollWidth - getWidth() / 2;
        } else if ((curScrollWidth + getWidth()) >= mMaxX) {
            maxScrollWidth = mMaxX - getWidth() / 2;
        } else {
            maxScrollWidth = curScrollWidth - getWidth() / 2 + childWidth / 2;
        }
        mScroller.startScroll(1, 0, maxScrollWidth, 0);
        requestLayout();
    }

    private void addAndMeasureChild(final View child, int viewPos) {
        Log.d("test_tag", "function: addAndMeasureChild");
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        addViewInLayout(child, viewPos, params, true);
        child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
    }

    @Override
    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("test_tag", "function: onLayout(" + String.valueOf(changed) + ", " + String.valueOf(left) + ", " + String.valueOf(top) + ", "
                + String.valueOf(right) + ", " + String.valueOf(bottom) + ")");

        if (mAdapter == null) {
            return;
        }

        if (mDataChanged) {
            int oldCurrentX = mCurrentX;
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        if (mScroller.computeScrollOffset()) {
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
        }

        if (mNextX <= 0) {
            mNextX = 0;
            mScroller.forceFinished(true);
        }
        if (mNextX >= mMaxX) {
            mNextX = mMaxX;
            mScroller.forceFinished(true);
        }
        Log.d("test_tag", "mCurrentX=" + mCurrentX + " mNext=" + mNextX);
        int dx = mCurrentX - mNextX;

        removeNonVisibleItems(dx);
        fillList(dx);
        positionItems(dx);

        mCurrentX = mNextX;

        if (!mScroller.isFinished()) {
            post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });

        }
    }

    private void fillList(final int dx) {
        Log.d("test_tag", "function: fillList(dx=" + String.valueOf(dx) + ")");
        int edge = 0;
        View child = getChildAt(getChildCount() - 1);
        if (child != null) {
            edge = child.getRight();
        }
        fillListRight(edge, dx);

        edge = 0;
        child = getChildAt(0);
        if (child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);

    }

    private void fillListRight(int rightEdge, final int dx) {
        Log.d("test_tag", "function: fillListRight(rightEdge=" + String.valueOf(rightEdge) + ", dx=" + String.valueOf(dx) + ")");
        int count = mAdapter.getCount();
        while (rightEdge + dx < getWidth() && mRightViewIndex < count) {

            View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, -1);
            rightEdge += child.getMeasuredWidth();
            Log.d("test_tag", "mRightViewIndex=" + mRightViewIndex + "   mCurrentX=" + mCurrentX + "  rightEdge=" + rightEdge + "  width=" + getWidth());
            if (mRightViewIndex == mAdapter.getCount() - 1) {
                // mMaxX = mCurrentX + rightEdge - getWidth();
                int rightMax = mCurrentX + rightEdge - getWidth();
                int childWidth = getChildAt(0).getMeasuredWidth();
                int realMax = childWidth * count - getWidth();
                if (rightMax <= realMax) {
                    mMaxX = realMax + 10;
                } else {
                    mMaxX = rightMax;
                }
                // mMaxX = childWidth * count - getWidth();
                // mMaxX = rightMax > realMax ? rightMax : realMax;
                Log.d("test_tag", "childWidth=" + childWidth + "screenWidth=" + getWidth());
                Log.d("test_tag", "mRightViewIndex=" + mRightViewIndex + "  mMaxX=" + mMaxX);
            }

            if (mMaxX < 0) {
                mMaxX = 0;
            }
            mRightViewIndex++;
        }

    }

    private void fillListLeft(int leftEdge, final int dx) {
        Log.d("test_tag", "function: fillListLeft(leftEdge=" + String.valueOf(leftEdge) + ", dx=" + String.valueOf(dx) + ")");
        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
            View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, 0);
            leftEdge -= child.getMeasuredWidth();
            mLeftViewIndex--;
            mDisplayOffset -= child.getMeasuredWidth();
        }
    }

    private void removeNonVisibleItems(final int dx) {
        Log.d("test_tag", "function: removeNonVisibleItems(dx=" + String.valueOf(dx) + ")");
        View child = getChildAt(0);
        while (child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += child.getMeasuredWidth();
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mLeftViewIndex++;
            child = getChildAt(0);

        }

        child = getChildAt(getChildCount() - 1);
        while (child != null && child.getLeft() + dx >= getWidth()) {
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mRightViewIndex--;
            child = getChildAt(getChildCount() - 1);
        }
    }

    public void positionItems(final int dx) {
        Log.d("test_tag", "function: positionItems(dx=" + String.valueOf(dx) + ")");
        if (getChildCount() > 0) {
            mDisplayOffset += dx;
            int left = mDisplayOffset;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int childWidth = child.getMeasuredWidth();
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
                left += childWidth + child.getPaddingRight();
            }
        }
    }

    public synchronized void scrollTo(int x) {
        Log.d("test_tag", "function: scrollTo(x=" + String.valueOf(x) + ")");
        mScroller.startScroll(mNextX, 0, x - mNextX, 0);
        requestLayout();
    }

    /*
     * 第一次显示界面时，滚动到指定位置
     */
    public synchronized void firstTimeScroll(int x) {
        Log.d("test_tag", "function: firstTimeScroll(x=" + String.valueOf(x) + ")");
        if (mScroller.computeScrollOffset()) {
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
        }
        Log.d("test_tag", "mnextx=" + mNextX);
        if (mNextX <= 0) {
            mNextX = 0;
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(1, 0, x, 0);
        requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("test_tag", "function: dispatchTouchEvent");
        boolean handled = super.dispatchTouchEvent(ev);
        handled |= mGesture.onTouchEvent(ev);
        return handled;
    }

    private float mLastMotionX;// 滑动过程中，x方向的初始坐标
    private float mLastMotionY;// 滑动过程中，y方向的初始坐标
    private int mTouchSlop;// 手指大小的距离
    private boolean xMoved = false;

    private boolean mScrolling;
    private float touchDownX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                mScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mScrolling = Math.abs(touchDownX - event.getX()) >= ViewConfiguration.get(
                        getContext()).getScaledTouchSlop();
                break;
            case MotionEvent.ACTION_UP:
                mScrolling = false;
                break;
        }
        return mScrolling;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("test_tag", "function: onTouchEvent");
        final int action = event.getAction();

        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                xMoved = false;
                mLastMotionX = x;// 初始化每次触摸事件的x方向的初始坐标，即手指按下的x方向坐标
                mLastMotionY = y;// 初始化每次触摸事件的y方向的初始坐标，即手指按下的y方向坐标
                break;

            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (mLastMotionX - x);// 每次滑动事件x方向坐标与触摸事件x方向初始坐标的距离
                final int deltaY = (int) (mLastMotionY - y);// 每次滑动事件y方向坐标与触摸事件y方向初始坐标的距离
                xMoved = Math.abs(deltaX) > mTouchSlop && Math.abs(deltaY / deltaX) < 1;
                Log.d("test_tag", "xMoved=" + xMoved + "  mTouchSlop=" + mTouchSlop);
                // x方向的距离大于手指，并且y方向滑动的距离小于x方向的滑动距离时，Gallery消费掉此次触摸事件
                if (xMoved) {// Gallery需要消费掉此次触摸事件
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return true;// 返回true就不会将此次触摸事件传递给子View了
                }
                break;
            case MotionEvent.ACTION_UP:
                xMoved = false;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("test_tag", "function: onFling mNextX=" + String.valueOf(mNextX) + ", mMaxX=" + String.valueOf(mMaxX));
        synchronized (HorizontalListView.this) {
            mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
        }
        requestLayout();

        return true;
    }

    protected boolean onDown(MotionEvent e) {
        Log.d("test_tag", "function: onDown");
        mScroller.forceFinished(true);
        return true;
    }

    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return HorizontalListView.this.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            synchronized (HorizontalListView.this) {
                mNextX += (int) distanceX;
            }
            requestLayout();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("test_tag", "function: onSingleTapConfirmed");
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    if (mOnItemClicked != null) {
                        mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    if (mOnItemSelected != null) {
                        mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    break;
                }

            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    if (mOnItemLongClicked != null) {
                        mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    break;
                }

            }
        }

        private boolean isEventWithinView(MotionEvent e, View child) {
            Rect viewRect = new Rect();
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            int left = childPosition[0];
            int right = left + child.getWidth();
            int top = childPosition[1];
            int bottom = top + child.getHeight();
            viewRect.set(left, top, right, bottom);
            return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
        }
    };
}