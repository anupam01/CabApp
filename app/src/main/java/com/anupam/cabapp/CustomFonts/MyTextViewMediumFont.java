package com.anupam.cabapp.CustomFonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Anupam on 09-06-17.
 */
public class MyTextViewMediumFont extends TextView {
    private Context c;
    public MyTextViewMediumFont(Context c) {
        super(c);
        this.c = c;
        Typeface tfs = Typeface.createFromAsset(c.getAssets(), "Raleway-Medium.ttf");
        setTypeface(tfs);

    }
    public MyTextViewMediumFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.c = context;
        Typeface tfs = Typeface.createFromAsset(c.getAssets(), "Raleway-Medium.ttf");
        setTypeface(tfs);
    }

    public MyTextViewMediumFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.c = context;
        Typeface tfs = Typeface.createFromAsset(c.getAssets(), "Raleway-Medium.ttf");
        setTypeface(tfs);

    }
}
