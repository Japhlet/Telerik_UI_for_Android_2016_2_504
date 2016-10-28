package com.telerik.android.primitives.widget.sidedrawer;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.android.common.licensing.TelerikLicense;

import java.util.ArrayList;

/**
 * RadSideDrawer is a UI component that behaves similarly to DrawerLayout. It displays a drawer
 * content on top of the main page content and opens/closes with an animation (transitions).
 */
public class RadSideDrawer extends FrameLayout implements DrawerTransitionEndedListener {
    public static final float OPEN_THRESHOLD = 0.2f;
    public static final float CLOSE_THRESHOLD = 0.8f;
    private ArrayList<DrawerChangeListener> changeListeners = new ArrayList<DrawerChangeListener>();
    private DrawerLocation drawerLocation = DrawerLocation.LEFT;
    private DrawerTransition drawerTransition;
    private View mainContent;
    private View drawerContent;
    private boolean isOpen;
    private int drawerSize;
    private boolean isLocked;
    private FrameLayout drawerContainer;
    private FrameLayout mainContainer;
    private DrawerTransition defaultTransition = new com.telerik.android.primitives.widget.sidedrawer.transitions.SlideInOnTopTransition();
    private DrawerFadeLayer fadeLayer;
    private DrawerFadeLayer defaultFadeLayer;
    private int touchTargetThreshold;
    private boolean tapOutsideToClose = true;
    private boolean onDown;
    private boolean canNotifyOpenedClosed = false;
    private boolean closeOnBackPress = true;
    private MotionEvent previousEvent;

    public RadSideDrawer(Context context) {
        this(context, null);
    }

    public RadSideDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.touchTargetThreshold = Math.round(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 20));
        this.defaultFadeLayer = new DrawerFadeLayerBase(context);
        this.defaultFadeLayer.view().setBackgroundColor(0xFF000000);
        this.defaultTransition.setFadeLayer(this.resolveFadeLayer().view());
        this.defaultTransition.setLocation(this.drawerLocation);
        this.setFocusableInTouchMode(true);
    }

    public void setCloseOnBackPress(boolean value) {
        closeOnBackPress = value;
    }

    public boolean getCloseOnBackPress() {
        return closeOnBackPress;
    }

    /**
     * Adds a change listener that will be notified when the drawer opens or closes.
     */
    public void addChangeListener(DrawerChangeListener listener) {
        this.changeListeners.add(listener);
    }

    /**
     * Removes the provided listener so that it doesn't get change notifications anymore.
     */
    public void removeChangeListener(DrawerChangeListener listener) {
        this.changeListeners.remove(listener);
    }

    /**
     * Gets the drawer fade layer.
     */
    public DrawerFadeLayer getFadeLayer() {
        return this.fadeLayer;
    }

    /**
     * Sets the drawer fade layer.
     */
    public void setFadeLayer(DrawerFadeLayer value) {
        if(value == this.fadeLayer) {
            return;
        }

        this.fadeLayer = value;
        this.resolveTransition().setFadeLayer(this.resolveFadeLayer().view());

        this.setMainContent(this.mainContent);
    }

    /**
     * Gets a value that determines if the drawer will be closed when the user taps outside it.
     */
    public boolean getTapOutsideToClose() {
        return this.tapOutsideToClose;
    }

    /**
     * Sets a value that determines if the drawer will be closed when the user taps outside it.
     */
    public void setTapOutsideToClose(boolean value) {
        this.tapOutsideToClose = value;
    }

    /**
     * Gets a value that determines the thickness of the area on screen which can be swiped to
     * open the drawer.
     */
    public int getTouchTargetThreshold() {
        return this.touchTargetThreshold;
    }

    /**
     * Sets a value that determines the thickness of the area on screen which can be swiped to
     * open the drawer.
     */
    public void setTouchTargetThreshold(int value) {
        this.touchTargetThreshold = value;
    }

    /**
     * Sets the drawer location.
     */
    public void setDrawerLocation(DrawerLocation value) {
        if(this.drawerLocation == value) {
            return;
        }

        this.resolveTransition().setLocation(value);

        this.setIsOpen(false);

        this.drawerLocation = value;

        this.setDrawerContent(this.drawerContent);
    }

    /**
     * Gets the drawer location.
     */
    public DrawerLocation getDrawerLocation() {
        return this.drawerLocation;
    }

    /**
     * Gets a value that determines if the drawer will listen for gestures or not.
     */
    public void setIsLocked(boolean value) {
        this.isLocked = value;
    }

    /**
     * Sets a value that determines if the drawer will listen for gestures or not.
     */
    public boolean getIsLocked() {
        return this.isLocked;
    }

    /**
     * Gets the drawer size.
     */
    public int getDrawerSize() {
        return this.drawerSize;
    }

    /**
     * Sets the drawer size.
     */
    public void setDrawerSize(int value) {
        if(value < 0) {
            throw new IllegalArgumentException("Value cannot be less than 0.");
        }

        this.drawerSize = value;

        this.setDrawerContent(this.drawerContent);
    }

    /**
     * Sets the main content to a View object inflated with the specified layout.
     */
    public void setMainContent(int resId) {
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setMainContent(inflater.inflate(resId, null));
    }

    /**
     * Sets the main content to the specified View object.
     */
    public void setMainContent(View value) {
        if(this.mainContent != null) {
            this.mainContainer.removeAllViews();
            this.removeView(this.mainContainer);
        }

        this.mainContent = value;

        if(value != null) {
            this.mainContainer = new FrameLayout(this.getContext());
            this.mainContainer.addView(this.mainContent);

            DrawerFadeLayer fadeLayer = this.resolveFadeLayer();
            this.mainContainer.addView(fadeLayer.view());
            if(!isOpen) {
                fadeLayer.hide();
            }
            this.addView(this.mainContainer);
        }

        if(this.drawerContainer != null) {
            this.drawerContainer.bringToFront();
        }

        this.resolveTransition().setMainContent(this.mainContainer);
    }

    /**
     * Gets the main content.
     */
    public View getMainContent() {
        return this.mainContent;
    }

    /**
     * Sets the drawer content to a View object inflated with the specified layout resource.
     */
    public void setDrawerContent(int resId) {
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setDrawerContent(inflater.inflate(resId, null));
    }

    /**
     * Sets the drawer content to the specified View object.
     */
    public void setDrawerContent(View value) {
        if(this.drawerContent != null) {
            this.drawerContainer.removeView(this.drawerContent);
            this.removeView(this.drawerContainer);
        }

        this.drawerContent = value;

        if(value != null) {
            this.drawerContainer = new FrameLayout(this.getContext());
            this.drawerContainer.setClickable(true);

            if(!isOpen) {
                this.drawerContainer.setVisibility(INVISIBLE);
            }
            this.drawerContainer.addView(value);
            FrameLayout.LayoutParams params;

            int mainDimension = this.drawerSize == 0 ? LayoutParams.WRAP_CONTENT : this.drawerSize;
            if(this.drawerLocation == DrawerLocation.TOP || this.drawerLocation == DrawerLocation.BOTTOM) {
                params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mainDimension);
            } else {
                params = new FrameLayout.LayoutParams(mainDimension, FrameLayout.LayoutParams.MATCH_PARENT);
            }

            params.gravity = this.getGravity();
            this.addView(this.drawerContainer, params);
        }

        this.resolveTransition().setDrawerContent(this.drawerContainer);
    }

    /**
     * Gets the drawer content.
     */
    public View getDrawerContent() {
        return this.drawerContent;
    }

    /**
     * Gets a value that indicates whether the drawer is open.
     */
    public boolean getIsOpen() {
        return this.isOpen;
    }

    /**
     * Sets a value that indicates whether the drawer is open.
     */
    public void setIsOpen(boolean value, boolean animate) {
        if(value == this.isOpen) {
            return;
        }

        if(this.drawerContent == null) {
            return;
        }

        if(value && this.notifyOpening()) {
            closeDrawerCore(true);
            return;
        }

        if(!value && this.notifyClosing()) {
            openDrawerCore(true);
            return;
        }
        canNotifyOpenedClosed = true;
        this.isOpen = value;

        if(this.isOpen) {
            this.openDrawerCore(animate);
        } else {
            this.closeDrawerCore(animate);
        }
    }

    /**
     * Sets a value that indicates whether the drawer is open.
     */
    public void setIsOpen(boolean value) {
        this.setIsOpen(value, true);
    }

    /**
     * Gets the drawer transition.
     */
    public DrawerTransition getDrawerTransition() {
        return this.drawerTransition;
    }

    /**
     * Sets the drawer transition.
     * The default transitions are located in the com.telerik.android.primitives.widget.sidedrawer.transitions
     * package.
     */
    public void setDrawerTransition(DrawerTransition value) {
        if(this.drawerTransition == value) {
            return;
        }

        this.drawerTransition = value;

        DrawerTransition actualTransition = resolveTransition();
        actualTransition.setFadeLayer(resolveFadeLayer().view());
        actualTransition.setLocation(drawerLocation);

        this.setMainContent(this.mainContent);
        this.setDrawerContent(this.drawerContent);

        if(this.isOpen) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    resolveTransition().setProgress(1.0f);
                }
            });
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(!closeOnBackPress || isLocked || !isOpen) {
            return super.onKeyUp(keyCode, event);
        }

        setIsOpen(false);

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.onGesture(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.onGesture(event) || super.onInterceptTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(TelerikLicense.licenseRequired()) {
            LicensingProvider.verify(this.getContext());
        }
    }

    protected boolean onGesture(MotionEvent event) {
        if(this.isLocked) {
            return false;
        }

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return this.handleOnDown(event);
            case MotionEvent.ACTION_UP:
                return this.handleOnUp(event);
            case MotionEvent.ACTION_MOVE:
                return this.handleOnMove(event);
        }

        return false;
    }

    protected boolean handleOnDown(MotionEvent event) {
        onDown = true;
        previousEvent = MotionEvent.obtain(event);

        if(isOpen) {
            if(isOutsideTap(event)) {
                return true;
            }
        } else {
            if (shouldOpen(event)) {
                this.drawerContainer.setVisibility(VISIBLE);
                resolveFadeLayer().show();
                this.resolveTransition().setProgress(0.05f);
                return true;
            }
        }

        return false;
    }

    protected boolean handleOnUp(MotionEvent event) {
        previousEvent = null;
        if(onDown) {
            onDown = false;
            if(isOpen) {
                if(isOutsideTap(event)) {
                    if (tapOutsideToClose) {
                        setIsOpen(false, true);
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                this.resolveTransition().setProgress(0);
                return false;
            }
        }

        float currentProgress = this.resolveTransition().getProgress();
        if(this.isOpen) {
            if(currentProgress < CLOSE_THRESHOLD) {
                this.setIsOpen(false);
            } else {
                openDrawerCore(true);
            }
        } else {
            if(currentProgress > OPEN_THRESHOLD) {
                this.setIsOpen(true);
            } else {
                closeDrawerCore(true);
            }
        }

        return true;
    }

    protected boolean handleOnMove(MotionEvent event) {
        if(previousEvent != null) {
            float xDelta = Math.abs(event.getX() - previousEvent.getX());
            float yDelta = Math.abs(event.getY() - previousEvent.getY());

            if(xDelta < 10 && yDelta < 10) {
                return false;
            }
        }

        return drawerContainer.getVisibility() == VISIBLE && handlePan(event);
    }

    protected boolean handlePan(MotionEvent event) {
        onDown = false;

        float xDelta = event.getX() - previousEvent.getX();
        float yDelta = event.getY() - previousEvent.getY();

        float absXDelta = Math.abs(xDelta);
        float absYDelta = Math.abs(yDelta);

        if((drawerLocation == DrawerLocation.LEFT || drawerLocation == DrawerLocation.RIGHT) && (absYDelta > absXDelta)) {
            return false;
        }

        if((drawerLocation == DrawerLocation.TOP || drawerLocation == DrawerLocation.BOTTOM) && (absXDelta > absYDelta)) {
            return false;
        }

        float progress = 0;
        switch (drawerLocation) {
            case LEFT:
                progress = xDelta / this.drawerContent.getMeasuredWidth();
                break;
            case RIGHT:
                progress = -xDelta / this.drawerContent.getMeasuredWidth();
                break;
            case TOP:
                progress = yDelta / this.drawerContent.getMeasuredHeight();
                break;
            case BOTTOM:
                progress = -yDelta / this.drawerContent.getMeasuredHeight();
                break;
        }

        DrawerTransition transition = resolveTransition();
        transition.setProgress(transition.getProgress() + progress);

        previousEvent = MotionEvent.obtain(event);
        return true;
    }

    private boolean shouldOpen(MotionEvent event) {
        switch (this.drawerLocation) {
            case LEFT:
                return event.getX() <= this.touchTargetThreshold;
            case RIGHT:
                return event.getX() >= this.getMeasuredWidth() - this.touchTargetThreshold;
            case TOP:
                return event.getY() <= this.touchTargetThreshold;
            case BOTTOM:
                return event.getY() >= this.getMeasuredHeight() - this.touchTargetThreshold;
            default:
                return false;
        }
    }

    private boolean isOutsideTap(MotionEvent event) {
        Rect rect = new Rect();
        drawerContainer.getHitRect(rect);

        // If onDown is false it means it is not tap, the user has moved the "cursor".
        return !rect.contains(Math.round(event.getX()), Math.round(event.getY()));
    }

    @Override
    public void onTransitionEnded(DrawerTransition transition) {
        transition.removeTransitionEndedListener(this);

        if(this.isOpen) {
            this.notifyOpened();
        } else {
            this.drawerContainer.setVisibility(INVISIBLE);
            this.resolveFadeLayer().hide();
            this.notifyClosed();
        }
    }

    protected void openDrawerCore(boolean animate) {
        this.drawerContainer.setVisibility(VISIBLE);

        this.resolveFadeLayer().show();

        final DrawerTransition transition = this.resolveTransition();
        if(!animate) {
            post(new Runnable() {
                @Override
                public void run() {
                    transition.setProgress(1);
                }
            });

            this.notifyOpened();
            return;
        }

        transition.addTransitionEndedListener(this);
        transition.animateOpen();
    }

    protected void closeDrawerCore(boolean animate) {
        final DrawerTransition transition = this.resolveTransition();
        if(!animate) {
            this.drawerContainer.setVisibility(INVISIBLE);
            this.resolveFadeLayer().hide();
            post(new Runnable() {
                @Override
                public void run() {
                    transition.setProgress(0);
                }
            });

            this.notifyClosed();
            return;
        }

        transition.addTransitionEndedListener(this);
        transition.animateClose();
    }

    protected DrawerFadeLayer resolveFadeLayer() {
        if(this.fadeLayer == null) {
            return this.defaultFadeLayer;
        }

        return this.fadeLayer;
    }

    protected DrawerTransition resolveTransition() {
        if(this.drawerTransition != null) {
            return this.drawerTransition;
        }

        return this.defaultTransition;
    }

    private int getGravity() {
        switch(this.drawerLocation) {
            case LEFT:
                return Gravity.LEFT;
            case RIGHT:
                return Gravity.RIGHT;
            case TOP:
                return Gravity.TOP;
            case BOTTOM:
                return Gravity.BOTTOM;
        }

        return Gravity.NO_GRAVITY;
    }

    protected void notifyOpened() {
        if(!canNotifyOpenedClosed) {
            return;
        }

        // Copy the listeners because if someone removes themselves from the listeners collection while we are
        // iterating over it the app crashes.
        DrawerChangeListener[] listeners = new DrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(DrawerChangeListener listener : listeners) {
            listener.onDrawerOpened(this);
        }
        canNotifyOpenedClosed = false;
    }

    protected void notifyClosed() {
        if(!canNotifyOpenedClosed) {
            return;
        }

        DrawerChangeListener[] listeners = new DrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(DrawerChangeListener listener : listeners) {
            listener.onDrawerClosed(this);
        }
        canNotifyOpenedClosed = false;
    }

    protected boolean notifyOpening() {
        boolean result = false;

        DrawerChangeListener[] listeners = new DrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(DrawerChangeListener listener : listeners) {
            if(listener.onDrawerOpening(this)) {
                result = true;
            }
        }

        return result;
    }

    protected boolean notifyClosing() {
        boolean result = false;

        DrawerChangeListener[] listeners = new DrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(DrawerChangeListener listener : listeners) {
            if(listener.onDrawerClosing(this)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        return new SideDrawerState(this, parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SideDrawerState drawerState = (SideDrawerState) state;
        super.onRestoreInstanceState(drawerState.getSuperState());

        this.restoreState(drawerState);
    }

    protected void restoreState(SideDrawerState state) {
        this.isLocked = state.getIsLocked();
        this.drawerLocation = state.getDrawerLocation();
        this.touchTargetThreshold = state.getTouchTargetThreshold();
        this.tapOutsideToClose = state.getTapOutsideToClose();
        this.setIsOpen(state.getIsOpen(), false);
        this.setDrawerTransition(state.getTransition());
    }
}
