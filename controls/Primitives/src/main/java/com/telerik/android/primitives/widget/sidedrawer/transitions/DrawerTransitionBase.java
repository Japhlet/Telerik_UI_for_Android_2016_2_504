package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.os.Parcel;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.telerik.android.primitives.widget.sidedrawer.DrawerLocation;
import com.telerik.android.primitives.widget.sidedrawer.DrawerTransition;
import com.telerik.android.primitives.widget.sidedrawer.DrawerTransitionEndedListener;

import java.util.ArrayList;

public abstract class DrawerTransitionBase implements DrawerTransition, Runnable {
    private ArrayList<DrawerTransitionEndedListener> endedListeners = new ArrayList<DrawerTransitionEndedListener>();
    private long duration = 500;
    private Interpolator interpolator = new DecelerateInterpolator(2);
    private float fadeLayerOpacity  = 0.6f;
    private View mainContent;
    private View drawerContent;
    private View fadeLayer;
    private DrawerLocation location = DrawerLocation.LEFT;
    private float progress;
    private boolean opening;
    private boolean closing;

    public DrawerTransitionBase() {
    }

    public void clear() {
        this.clearCore(this.drawerContent, this.mainContent);
    }

    protected void clearCore(View drawerContent, View mainContent) {
    }

    public void saveInstanceState(Parcel parcel, int flags) {
        parcel.writeFloat(fadeLayerOpacity);
        parcel.writeLong(duration);
    }

    public void restoreInstanceState(Parcel parcel) {
        fadeLayerOpacity = parcel.readFloat();
        duration = parcel.readLong();
    }

    public void setMainContent(View value) {
        this.clear();
        mainContent = value;
    }

    public View getDrawerContent() {
        return this.drawerContent;
    }

    public View getMainContent() {
        return this.mainContent;
    }

    public void setDrawerContent(View value) {
        this.clear();
        drawerContent = value;
    }

    public void setFadeLayer(View value) {
        fadeLayer = value;
    }

    public void setLocation(DrawerLocation value) {
        location = value;
    }

    @Override
    public void setProgress(float value) {
        View frontView = this.getFrontView();
        frontView.bringToFront();

        if(value > 1) {
            value = 1;
        }

        if(value < 0) {
            value = 0;
        }

        progress = value;

        ViewCompat.setAlpha(fadeLayer, value * getFadeLayerOpacity());

        switch (location) {
            case LEFT:
                setProgressLeft(value, mainContent, drawerContent, fadeLayer);
                break;
            case RIGHT:
                setProgressRight(value, mainContent, drawerContent, fadeLayer);
                break;
            case TOP:
                setProgressTop(value, mainContent, drawerContent, fadeLayer);
                break;
            case BOTTOM:
                setProgressBottom(value, mainContent, drawerContent, fadeLayer);
                break;
        }
    }

    @Override
    public float getProgress() {
        return progress;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long value) {
        this.duration = value;
    }

    public void setInterpolator(Interpolator value) {
        this.interpolator = value;
    }

    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    public void addTransitionEndedListener(DrawerTransitionEndedListener listener) {
        this.endedListeners.add(listener);
    }

    public void removeTransitionEndedListener(DrawerTransitionEndedListener listener) {
        this.endedListeners.remove(listener);
    }

    public float getFadeLayerOpacity() {
        return this.fadeLayerOpacity;
    }

    public void setFadeLayerOpacity(float value) {
        this.fadeLayerOpacity = value;
    }

    @Override
    public void animateOpen() {
        View frontView = this.getFrontView();
        frontView.bringToFront();

        opening = true;
        ViewCompat.setAlpha(fadeLayer, this.getProgress() * fadeLayerOpacity);

        switch (location) {
            case LEFT:
                this.animateOpenLeft(mainContent, drawerContent, fadeLayer);
                break;
            case RIGHT:
                this.animateOpenRight(mainContent, drawerContent, fadeLayer);
                break;
            case TOP:
                this.animateOpenTop(mainContent, drawerContent, fadeLayer);
                break;
            case BOTTOM:
                this.animateOpenBottom(mainContent, drawerContent, fadeLayer);
                break;
        }

        ViewCompat.animate(fadeLayer).alpha(this.fadeLayerOpacity).setDuration(this.duration).setInterpolator(this.interpolator);
    }

    protected View getFrontView() {
        return (View)this.drawerContent.getParent();
    }

    @Override
    public void animateClose() {
        closing = true;
        ViewCompat.animate(fadeLayer).alpha(0).setDuration(this.duration).setInterpolator(this.interpolator);

        switch (location) {
            case LEFT:
                this.animateCloseLeft(mainContent, drawerContent, fadeLayer);
                break;
            case RIGHT:
                this.animateCloseRight(mainContent, drawerContent, fadeLayer);
                break;
            case TOP:
                this.animateCloseTop(mainContent, drawerContent, fadeLayer);
                break;
            case BOTTOM:
                this.animateCloseBottom(mainContent, drawerContent, fadeLayer);
                break;
        }
    }

    protected abstract void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void animateOpenRight(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void animateOpenTop(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void animateCloseRight(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void animateCloseTop(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer);

    protected abstract void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer);

    protected abstract void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer);

    protected abstract void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer);

    protected abstract void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer);

    @Override
    public void run() {
        this.onEnded();
    }

    protected void onEnded() {
        if(opening) {
            this.setProgress(1.0f);
        }

        if(closing) {
            this.setProgress(0.0f);
        }

        opening = false;
        closing = false;
        DrawerTransitionEndedListener[] listeners = new DrawerTransitionEndedListener[this.endedListeners.size()];
        this.endedListeners.toArray(listeners);
        for(DrawerTransitionEndedListener listener : listeners) {
            listener.onTransitionEnded(this);
        }
    }
}
