package com.telerik.android.primitives.widget.sidedrawer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DrawerFadeLayerBase extends FrameLayout implements DrawerFadeLayer {
    public DrawerFadeLayerBase(Context context) {
        super(context);
        this.setVisibility(INVISIBLE);
    }

    @Override
    public void show() {
        this.setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        this.setVisibility(INVISIBLE);
    }

    @Override
    public View view() {
        return this;
    }
}
