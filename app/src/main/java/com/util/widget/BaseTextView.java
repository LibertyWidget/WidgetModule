package com.util.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class BaseTextView extends android.support.v7.widget.AppCompatTextView
{
	public BaseTextView(Context context)
	{
		super(context);
	}

	public BaseTextView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
	}

	public BaseTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}
}
