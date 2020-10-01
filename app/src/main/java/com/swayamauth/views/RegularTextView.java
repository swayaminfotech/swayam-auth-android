package com.swayamauth.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RegularTextView extends TextView {

    public RegularTextView(Context context) {
        super(context);
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    public boolean setCustomFont(Context context) {
        Typeface mTypeface = null;

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "Poppins-Regular.ttf");
        }
        setTypeface(mTypeface);
        return true;
    }
}