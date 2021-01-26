package com.jc.luckywheellib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class LaunchView extends View {
    public LaunchView(Context context) {
        this(context,null);
    }

    public LaunchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LaunchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
