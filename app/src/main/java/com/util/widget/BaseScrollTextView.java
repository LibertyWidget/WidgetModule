package com.util.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class BaseScrollTextView extends android.support.v7.widget.AppCompatTextView
{
	public BaseScrollTextView(Context context)
	{
		super(context);
	}

	public BaseScrollTextView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
	}

	public BaseScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}
	@Override
	public boolean isFocused() {
		return true;
	}
}
