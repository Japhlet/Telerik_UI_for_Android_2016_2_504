package com.telerik.android.primitives.widget.sidedrawer;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.view.View;
import android.view.animation.Interpolator;

import java.io.Serializable;

public interface DrawerTransition {
    void animateOpen();
    void animateClose();

    void addTransitionEndedListener(DrawerTransitionEndedListener listener);
    void removeTransitionEndedListener(DrawerTransitionEndedListener listener);

    void setDuration(long duration);
    long getDuration();

    void setInterpolator(Interpolator interpolator);
    Interpolator getInterpolator();

    void setFadeLayerOpacity(float opacity);
    float getFadeLayerOpacity();

    void setProgress(float value);
    float getProgress();

    void setMainContent(View value);
    void setDrawerContent(View value);
    void setFadeLayer(View value);
    void setLocation(DrawerLocation value);

    void saveInstanceState(Parcel parcel, int flags);
    void restoreInstanceState(Parcel parcel);

    void clear();
}
