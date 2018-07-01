package com.khammami.imerolium.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class NavHeaderLayout extends LinearLayout implements Target {

    public NavHeaderLayout(Context context) {
        super(context);
    }

    public NavHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        //set error here
    }


    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        //Set placeholder here
    }
}

