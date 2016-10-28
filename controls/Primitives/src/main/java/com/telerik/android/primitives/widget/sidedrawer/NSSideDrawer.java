package com.telerik.android.primitives.widget.sidedrawer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.android.common.licensing.TelerikLicense;

import java.util.ArrayList;

/**
 * RadSideDrawer is a UI component that behaves similarly to DrawerLayout. It displays a drawer
 * content on top of the main page content and opens/closes with an animation (transitions).
 */
public class NSSideDrawer extends FrameLayout implements DrawerTransitionEndedListener {
    public static final float OPEN_THRESHOLD = 0.2f;
    public static final float CLOSE_THRESHOLD = 0.8f;
    private ArrayList<NSDrawerChangeListener> changeListeners = new ArrayList<>();
    DrawerLocation drawerLocation = DrawerLocation.LEFT;
    private DrawerTransition drawerTransition;
    View drawerContent;
    boolean isOpen;
    int drawerSize;
    private boolean isLocked;
    private DrawerTransition defaultTransition = new com.telerik.android.primitives.widget.sidedrawer.transitions.SlideInOnTopTransition();
    DrawerFadeLayer fadeLayer;
    private DrawerFadeLayer defaultFadeLayer;
    private int touchTargetThreshold;
    private boolean tapOutsideToClose = true;
    private boolean onDown;
    private boolean canNotifyOpenedClosed = false;
    private boolean closeOnBackPress = true;
    private MotionEvent previousEvent;
    private ViewGroup decorRoot;
    private ViewGroup fadeLayerContainer;
    private View mainView;

    private NSSideDrawerLayout globalContainer;
    private WindowManager windowManager;

    public NSSideDrawer(Context context) {
        this(context, null);
    }

    public NSSideDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.globalContainer = new NSSideDrawerLayout(context, this);
        FrameLayout statusBarShim = new FrameLayout(context);
        FrameLayout.LayoutParams shimParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, this.getStatusBarHeight());
        this.globalContainer.addView(statusBarShim, shimParams);

        //statusBarShim.setBackgroundColor(0xff000000);

        ViewGroup decorView = (ViewGroup)((Activity)this.getContext()).getWindow().getDecorView();
        this.decorRoot = (ViewGroup)decorView.getChildAt(0);

        this.touchTargetThreshold = Math.round(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 20));

        this.defaultFadeLayer = new DrawerFadeLayerBase(context);
        this.defaultFadeLayer.view().setBackgroundColor(0xFF000000);

        this.defaultTransition.setFadeLayer(this.resolveFadeLayer().view());
        this.defaultTransition.setLocation(this.drawerLocation);
        this.defaultTransition.setMainContent(this.decorRoot);
        this.setFocusableInTouchMode(true);

        Activity activity = (Activity)context;
        this.windowManager = activity.getWindowManager();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        this.fadeLayerContainer = (ViewGroup)this.decorRoot.getChildAt(1);
        this.fadeLayerContainer.addView(this.defaultFadeLayer.view(), params);

        this.post(new Runnable() {
            @Override
            public void run() {
                insertInWindow();
            }
        });
    }

    /**
     * Sets the drawer content to a View object inflated with the specified layout resource.
     */
    public void setMainContent(int resId) {
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setMainContent(inflater.inflate(resId, null));
    }

    /**
     * Sets the drawer content to a View object inflated with the specified view.
     */
    public void setMainContent(View view) {
        this.mainView = view;

        if(this.getChildCount() > 0) {
            this.removeAllViews();
        }

        if(view == null) {
            return;
        }

        this.addView(view);
    }

    public View getMainContent() {
        return this.mainView;
    }

    private void insertInWindow() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((ViewGroup)((Activity)this.getContext()).getWindow().getDecorView()).addView(this.globalContainer, params);
    }

    private int getActionBarHeight() {
        Activity activity = (Activity)this.getContext();
        int result = 0;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (activity.getActionBar() != null && activity.getActionBar().isShowing()) {
                result = activity.getActionBar().getHeight();
            }

            if(activity instanceof AppCompatActivity) {
                AppCompatActivity tmp = (AppCompatActivity)activity;
                if(tmp.getSupportActionBar() != null && tmp.getSupportActionBar().isShowing()) {
                    result = tmp.getSupportActionBar().getHeight();
                }
            }
        }

        return result;
    }

    private void updatePadding() {
        View rootView = ((Activity)this.getContext()).findViewById(android.R.id.content);
        int rootHeight = rootView.getMeasuredHeight();

        Point displaySize = new Point();
        Display dsp = this.windowManager.getDefaultDisplay();
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dsp.getRealSize(displaySize);
        } else {
            displaySize.x = dsp.getWidth();
            displaySize.y = dsp.getHeight();
        }

        int right = 0;
        int bottom = 0;
        int actionBarHeight = this.getActionBarHeight();
        int statusBarHeight = this.getStatusBarHeight();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            bottom = displaySize.y - rootHeight - statusBarHeight - actionBarHeight;
        }

        this.globalContainer.setPadding(0, 0, right, Math.max(0, bottom));
    }

    @SuppressWarnings("unused")
    public void setCloseOnBackPress(boolean value) {
        closeOnBackPress = value;
    }

    @SuppressWarnings("unused")
    public boolean getCloseOnBackPress() {
        return closeOnBackPress;
    }

    /**
     * Adds a change listener that will be notified when the drawer opens or closes.
     */
    @SuppressWarnings("unused")
    public void addChangeListener(NSDrawerChangeListener listener) {
        this.changeListeners.add(listener);
    }

    /**
     * Removes the provided listener so that it doesn't get change notifications anymore.
     */
    @SuppressWarnings("unused")
    public void removeChangeListener(NSDrawerChangeListener listener) {
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

        if(this.fadeLayer != null) {
            this.fadeLayerContainer.removeView(this.fadeLayer.view());
        }

        this.fadeLayer = value;

        this.fadeLayerContainer.addView(this.fadeLayer.view());
        this.resolveTransition().setFadeLayer(this.resolveFadeLayer().view());
    }

    /**
     * Gets a value that determines if the drawer will be closed when the user taps outside it.
     */
    @SuppressWarnings("unused")
    public boolean getTapOutsideToClose() {
        return this.tapOutsideToClose;
    }

    /**
     * Sets a value that determines if the drawer will be closed when the user taps outside it.
     */
    @SuppressWarnings("unused")
    public void setTapOutsideToClose(boolean value) {
        this.tapOutsideToClose = value;
    }

    /**
     * Gets a value that determines the thickness of the area on screen which can be swiped to
     * open the drawer.
     */
    @SuppressWarnings("unused")
    public int getTouchTargetThreshold() {
        return this.touchTargetThreshold;
    }

    /**
     * Sets a value that determines the thickness of the area on screen which can be swiped to
     * open the drawer.
     */
    @SuppressWarnings("unused")
    public void setTouchTargetThreshold(int value) {
        this.touchTargetThreshold = value;
    }

    /**
     * Sets the drawer location.
     */
    @SuppressWarnings("unused")
    public void setDrawerLocation(DrawerLocation value) {
        if(this.drawerLocation == value) {
            return;
        }

        this.resolveTransition().setLocation(value);

        this.setIsOpen(false);

        this.drawerLocation = value;

        View content = this.getDrawerContent();
        this.setDrawerContent(null);
        this.setDrawerContent(content);
    }

    /**
     * Gets the drawer location.
     */
    @SuppressWarnings("unused")
    public DrawerLocation getDrawerLocation() {
        return this.drawerLocation;
    }

    /**
     * Gets a value that determines if the drawer will listen for gestures or not.
     */
    @SuppressWarnings("unused")
    public void setIsLocked(boolean value) {
        this.isLocked = value;
    }

    /**
     * Sets a value that determines if the drawer will listen for gestures or not.
     */
    @SuppressWarnings("unused")
    public boolean getIsLocked() {
        return this.isLocked;
    }

    /**
     * Gets the drawer size.
     */
    @SuppressWarnings("unused")
    public int getDrawerSize() {
        return this.drawerSize;
    }

    /**
     * Sets the drawer size.
     */
    @SuppressWarnings("unused")
    public void setDrawerSize(int value) {
        if(value < 0) {
            throw new IllegalArgumentException("Value cannot be less than 0.");
        }

        this.drawerSize = value;
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
            this.globalContainer.removeView(this.drawerContent);
        }

        this.drawerContent = value;

        if(this.drawerContent != null) {
            if(!isOpen) {
                this.drawerContent.setVisibility(INVISIBLE);
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int mainDimension = this.drawerSize == 0 ? FrameLayout.LayoutParams.WRAP_CONTENT : this.drawerSize;
            if(this.drawerLocation == DrawerLocation.TOP || this.drawerLocation == DrawerLocation.BOTTOM) {
                params.height = mainDimension;
            } else {
                params.width = mainDimension;
            }

            params.bottomMargin = this.getStatusBarHeight();
            params.gravity = this.getGravity();

            this.globalContainer.addView(this.drawerContent, 0, params);
        }

        this.resolveTransition().setDrawerContent(this.drawerContent);
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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

        this.resolveTransition().clear();

        this.drawerTransition = value;

        DrawerTransition actualTransition = resolveTransition();
        actualTransition.setFadeLayer(resolveFadeLayer().view());
        actualTransition.setLocation(drawerLocation);

        actualTransition.setDrawerContent(this.drawerContent);
        actualTransition.setMainContent(this.decorRoot);

        View content = this.getDrawerContent();
        this.setDrawerContent(null);
        this.setDrawerContent(content);

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
    @SuppressWarnings("all")
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(TelerikLicense.licenseRequired()) {
            LicensingProvider.verify(this.getContext());
        }
    }

    public boolean onGesture(MotionEvent event) {
        if(this.drawerContent == null) {
            return false;
        }

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
        this.updatePadding();

        if(isOpen) {
            if(isOutsideTap(event)) {
                return true;
            }
        } else {
            if (shouldOpen(event)) {
                this.drawerContent.setVisibility(VISIBLE);
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

        return drawerContent.getVisibility() == VISIBLE && handlePan(event);
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
                return event.getX() <= this.touchTargetThreshold + this.globalContainer.getPaddingLeft();
            case RIGHT:
                return event.getX() >= this.globalContainer.getMeasuredWidth() - this.touchTargetThreshold - this.globalContainer.getPaddingRight();
            case TOP:
                return event.getY() <= this.getStatusBarHeight() + this.touchTargetThreshold + this.globalContainer.getPaddingTop();
            case BOTTOM:
                return event.getY() >= this.globalContainer.getMeasuredHeight() - this.touchTargetThreshold - this.globalContainer.getPaddingBottom();
            default:
                return false;
        }
    }

    private boolean isOutsideTap(MotionEvent event) {
        Rect rect = new Rect();
        this.drawerContent.getHitRect(rect);

        // If onDown is false it means it is not tap, the user has moved the "cursor".
        return !rect.contains(Math.round(event.getX()), Math.round(event.getY()));
    }

    @Override
    public void onTransitionEnded(DrawerTransition transition) {
        transition.removeTransitionEndedListener(this);

        if(this.isOpen) {
            this.notifyOpened();
        } else {
            this.drawerContent.setVisibility(INVISIBLE);
            this.resolveFadeLayer().hide();
            this.notifyClosed();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void openDrawerCore(boolean animate) {
        if(this.drawerContent == null) {
            return;
        }

        this.updatePadding();

        this.drawerContent.setVisibility(VISIBLE);
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
            this.drawerContent.setVisibility(INVISIBLE);
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

    int getGravity() {
        switch(this.drawerLocation) {
            case LEFT:
                return Gravity.START;
            case RIGHT:
                return Gravity.END;
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
        NSDrawerChangeListener[] listeners = new NSDrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(NSDrawerChangeListener listener : listeners) {
            listener.onDrawerOpened(this);
        }
        canNotifyOpenedClosed = false;
    }

    protected void notifyClosed() {
        if(!canNotifyOpenedClosed) {
            return;
        }

        NSDrawerChangeListener[] listeners = new NSDrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(NSDrawerChangeListener listener : listeners) {
            listener.onDrawerClosed(this);
        }
        canNotifyOpenedClosed = false;
    }

    protected boolean notifyOpening() {
        boolean result = false;

        NSDrawerChangeListener[] listeners = new NSDrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(NSDrawerChangeListener listener : listeners) {
            if(listener.onDrawerOpening(this)) {
                result = true;
            }
        }

        return result;
    }

    protected boolean notifyClosing() {
        boolean result = false;

        NSDrawerChangeListener[] listeners = new NSDrawerChangeListener[this.changeListeners.size()];
        this.changeListeners.toArray(listeners);

        for(NSDrawerChangeListener listener : listeners) {
            if(listener.onDrawerClosing(this)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        return new NSSideDrawerState(this, parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        NSSideDrawerState drawerState = (NSSideDrawerState) state;
        super.onRestoreInstanceState(drawerState.getSuperState());

        this.restoreState(drawerState);
    }

    protected void restoreState(final NSSideDrawerState state) {
        this.isLocked = state.getIsLocked();

        this.touchTargetThreshold = state.getTouchTargetThreshold();
        this.tapOutsideToClose = state.getTapOutsideToClose();

        this.post(new Runnable() {
            @Override
            public void run() {
                NSSideDrawer.this.setDrawerLocation(state.getDrawerLocation());
                NSSideDrawer.this.setIsOpen(state.getIsOpen(), false);
                NSSideDrawer.this.setDrawerTransition(state.getTransition());
            }
        });
    }
}
