package com.swayamauth.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RegularRadioButton extends RadioButton {

    public RegularRadioButton(Context context) {
        super(context);
    }

    public RegularRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public RegularRadioButton(Context context, AttributeSet attrs, int defStyle) {
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