package com.swayamauth.views;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MediumTextView extends TextView {

    public MediumTextView(Context context) {
        super(context);
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public MediumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    public boolean setCustomFont(Context context) {
        Typeface mTypeface = null;

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "Poppins-Medium.ttf");
        }
        setTypeface(mTypeface);
        return true;
    }
}