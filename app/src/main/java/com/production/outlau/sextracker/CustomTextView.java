package com.production.outlau.sextracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomTextView, 0, 0);

        String font = a.getString(R.styleable.CustomTextView_font);
        setFont(font);
    }

    private void setFont(String font) {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), ("font/"+font));
        setTypeface(typeface, Typeface.NORMAL);
    }
}