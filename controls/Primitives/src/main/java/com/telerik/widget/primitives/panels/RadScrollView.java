package com.telerik.widget.primitives.panels;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.telerik.android.primitives.R;

import java.util.List;

/**
 * Represents a container that accommodates UI hierarchy that can be scrolled
 * by the user in horizontal and vertical manner. Only one {@link android.view.View} can be a direct
 * child of this container. This control is widely based on the native
 * {@link android.widget.ScrollView} widget coming with the Android framework.
 *
 * Portions of ScrollView code from Android Open Source project (as modified by Telerik AD and renamed RadScrollView) - Copyright © 2016 Telerik AD. All rights reserved.
 */
public class RadScrollView extends FrameLayout {

    /**
     * Used to activate the vertical scrolling mode in {@link RadScrollView}
     */
    public static final int SCROLLING_MODE_VERTICAL = 1;

    /**
     * Used to activate the horizontal scrolling mode in {@link RadScrollView}
     */
    public static final int SCROLLING_MODE_HORIZONTAL = 2;

    private int scrollMode = SCROLLING_MODE_HORIZONTAL | SCROLLING_MODE_VERTICAL;

    static final int ANIMATED_SCROLL_GAP = 250;

    static final float MAX_SCROLL_FACTOR = 0.5f;

    private static final String TAG = "RadScrollView";

    private long mLastScroll;

    private final Rect mTempRect = new Rect();
    private OverScroller mScroller;
    private EdgeEffectCompat mEdgeGlowTop;
    private EdgeEffectCompat mEdgeGlowBottom;
    private EdgeEffectCompat mEdgeGlowLeft;
    private EdgeEffectCompat mEdgeGlowRight;


    /**
     * X Position of the last motion event.
     */
    private int mLastMotionX;

    /**
     * Y Position of the last motion event.
     */
    private int mLastMotionY;

    /**
     * True when the layout has changed but the traversal has not come through yet.
     * Ideally the view hierarchy would keep track of this for us.
     */
    private boolean mIsLayoutDirty = true;

    /**
     * The child to give focus to in the event that a child has requested focus while the
     * layout is dirty. This prevents the scroll from being wrong if the child has not been
     * laid out before requesting focus.
     */
    private View mChildToScrollTo = null;

    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private boolean mIsBeingDragged = false;

    /**
     * Determines the Y speed during touch scrolling.
     */
    private VelocityTracker mYVelocityTracker;

    /**
     * Determines the X speed during touch scrolling.
     */
    private VelocityTracker mXVelocityTracker;

    /**
     * When set to true, the scroll view measure its child to make it fill the currently
     * visible area.
     */
    @ViewDebug.ExportedProperty(category = "layout")
    private boolean mFillViewport;

    /**
     * Whether arrow scrolling is animated.
     */
    private boolean mSmoothScrollingEnabled = true;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private int mOverscrollDistance;
    private int mOverflingDistance;
    private boolean isLaidOut = false;

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    private SavedState mSavedState;

    /**
     * Creates an instance of (@link RadScrollView) with the provided {@link android.content.Context}
     * and {@link android.util.AttributeSet}
     */
    public RadScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radScrollViewStyle);
    }

    /**
     * Creates an instance of (@link RadScrollView) with the provided {@link android.content.Context},
     * {@link android.util.AttributeSet}.
     */
    public RadScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initScrollView();

        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.RadScrollView, defStyle, R.style.RadScrollViewStyle);
        setFillViewport(a.getBoolean(R.styleable.RadScrollView_fillViewport, false));
        setScrollMode(a.getInt(R.styleable.RadScrollView_scrollMode, SCROLLING_MODE_VERTICAL));
        a.recycle();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getVerticalFadingEdgeLength();
        if (this.getScrollY() < length) {
            return this.getScrollY() / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getHorizontalFadingEdgeLength();
        if (this.getScrollX() < length) {
            return this.getScrollX() / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getHorizontalFadingEdgeLength();
        final int rightEdge = getWidth() - this.getPaddingRight();
        final int span = getChildAt(0).getBottom() - this.getScrollX() - rightEdge;
        if (span < length) {
            return span / (float) length;
        }

        return 1.0f;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }

        final int length = getVerticalFadingEdgeLength();
        final int bottomEdge = getHeight() - this.getPaddingBottom();
        final int span = getChildAt(0).getBottom() - this.getScrollY() - bottomEdge;
        if (span < length) {
            return span / (float) length;
        }

        return 1.0f;
    }

    /**
     * @return The maximum amount this scroll view will scroll in response to
     * an arrow event.
     */
    public int getMaxVerticalScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * (this.getBottom() - this.getTop()));
    }

    /**
     * @return The maximum amount this scroll view will scroll in response to
     * an arrow event.
     */
    public int getMaxHorizontalScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * (this.getRight() - this.getLeft()));
    }

    private void initScrollView() {
        mScroller = new OverScroller(getContext());
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mOverscrollDistance = configuration.getScaledOverscrollDistance();
        mOverflingDistance = configuration.getScaledOverflingDistance();
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child, index, params);
    }

    /**
     * @return Returns true this ScrollView can be scrolled
     */
    private boolean canScroll() {
        View child = getChildAt(0);
        if (child != null) {
            int childHeight = child.getHeight();
            int childWidth = child.getWidth();
            return getHeight() < childHeight + this.getPaddingTop() + this.getPaddingBottom() ||
                    getWidth() < childWidth + this.getPaddingLeft() + this.getPaddingRight();
        }
        return false;
    }

    /**
     * Indicates whether this ScrollView's content is stretched to fill the viewport.
     *
     * @return True if the content fills the viewport, false otherwise.
     * @attr ref android.R.styleable#ScrollView_fillViewport
     */
    public boolean isFillViewport() {
        return mFillViewport;
    }

    /**
     * Indicates this ScrollView whether it should stretch its content height to fill
     * the viewport or not.
     *
     * @param fillViewport True to stretch the content's height to the viewport's
     *                     boundaries, false otherwise.
     * @attr ref android.R.styleable#ScrollView_fillViewport
     */
    public void setFillViewport(boolean fillViewport) {
        if (fillViewport != mFillViewport) {
            mFillViewport = fillViewport;
            requestLayout();
        }
    }


    /**
     * Sets the active scroll mode. The returned integer
     * is one of the {@link #SCROLLING_MODE_VERTICAL}
     * or {@link #SCROLLING_MODE_HORIZONTAL} or combination of both.
     * The scroll mode defines whether the {@link RadScrollView} instance will scroll vertically, horizontally
     * or in both directions.
     *
     * @return the scroll mode value.
     */
    public void setScrollMode(int scrollMode) {
        if (scrollMode != 2 && scrollMode != 1 && scrollMode != (SCROLLING_MODE_VERTICAL | SCROLLING_MODE_HORIZONTAL)) {
            throw new IllegalArgumentException("Value must be one of or combination of RadScrollView.SCROLLING_MODE_VERTICAL or RadScrollView.SCROLLING_MODE_HORIZONTAL constants");
        }

        if (this.scrollMode != scrollMode) {
            this.scrollMode = scrollMode;
            this.requestLayout();
        }
    }

    /**
     * Returns the currently active scroll mode. The returned integer
     * is one of the {@link #SCROLLING_MODE_VERTICAL}
     * or {@link #SCROLLING_MODE_HORIZONTAL} or combination of both.
     *
     * @return the scroll mode value.
     */
    public int getScrollMode() {
        return this.scrollMode;
    }

    /**
     * @return Whether arrow scrolling will animate its transition.
     */
    public boolean isSmoothScrollingEnabled() {
        return mSmoothScrollingEnabled;
    }

    /**
     * Set whether arrow scrolling will animate its transition.
     *
     * @param smoothScrollingEnabled whether arrow scrolling will animate its transition
     */
    public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        mSmoothScrollingEnabled = smoothScrollingEnabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!mFillViewport) {
            return;
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (getChildCount() > 0) {
            if (heightMode != MeasureSpec.UNSPECIFIED) {
                final View child = getChildAt(0);
                int height = getMeasuredHeight();
                if (child.getMeasuredHeight() < height) {
                    final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();

                    int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            this.getPaddingLeft() + this.getPaddingRight(), lp.width);
                    height -= this.getPaddingTop();
                    height -= this.getPaddingBottom();
                    int childHeightMeasureSpec =
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }

            if (widthMode != MeasureSpec.UNSPECIFIED) {
                final View child = getChildAt(0);
                int width = getMeasuredWidth();
                if (child.getMeasuredWidth() < width) {
                    final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();

                    int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            this.getPaddingTop() + this.getPaddingBottom(), lp.height);
                    width -= this.getPaddingLeft();
                    width -= this.getPaddingRight();
                    int childWidthMeasureSpec =
                            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Let the focused view and/or our descendants get the key first
        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.
     *
     * @param event The key event to execute.
     * @return Return true if the event was handled, else false.
     */
    public boolean executeKeyEvent(KeyEvent event) {
        mTempRect.setEmpty();

        if (!canScroll()) {
            if (isFocused() && event.getKeyCode() != KeyEvent.KEYCODE_BACK) {
                View currentFocused = findFocus();
                if (currentFocused == this) currentFocused = null;
                View nextFocused = FocusFinder.getInstance().findNextFocus(this,
                        currentFocused, View.FOCUS_DOWN);
                return nextFocused != null
                        && nextFocused != this
                        && nextFocused.requestFocus(View.FOCUS_DOWN);
            }
            return false;
        }

        boolean handled = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(View.FOCUS_UP);
                    } else {
                        handled = fullScroll(View.FOCUS_UP);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(View.FOCUS_DOWN);
                    } else {
                        handled = fullScroll(View.FOCUS_DOWN);
                    }
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    pageScroll(event.isShiftPressed() ? View.FOCUS_UP : View.FOCUS_DOWN);
                    break;
            }
        }

        return handled;
    }

    private boolean inChild(int x, int y) {
        if (getChildCount() > 0) {
            final int scrollY = this.getScrollY();

            final View child = getChildAt(0);
            boolean inChildY = !(y < child.getTop() - scrollY
                    || y >= child.getBottom() - scrollY
                    || x < child.getLeft()
                    || x >= child.getRight());
            if (inChildY) {
                return true;
            }
            final int scrollX = this.getScrollX();
            boolean inChildX = !(x < child.getLeft() - scrollX
                    || x >= child.getRight() - scrollX
                    || x < child.getTop()
                    || x >= child.getBottom());

            if (inChildX) {
                return true;
            }
        }
        return false;
    }

    private void initOrResetVelocityTracker() {
        if (mYVelocityTracker == null) {
            mYVelocityTracker = VelocityTracker.obtain();
        } else {
            mYVelocityTracker.clear();
        }

        if (mXVelocityTracker == null) {
            mXVelocityTracker = VelocityTracker.obtain();
        } else {
            mXVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mYVelocityTracker == null) {
            mYVelocityTracker = VelocityTracker.obtain();
        }

        if (mXVelocityTracker == null) {
            mXVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mYVelocityTracker != null) {
            mYVelocityTracker.recycle();
            mYVelocityTracker = null;
        }

        if (mXVelocityTracker != null) {
            mXVelocityTracker.recycle();
            mXVelocityTracker = null;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        /*
        * Shortcut the most recurring case: the user is in the dragging
        * state and he is moving his finger.  We want to intercept this
        * motion.
        */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }

        /*
         * Don't try to intercept touch if we can't scroll anyway.
         */
        int direction = 1;


        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + activePointerId
                            + " in onInterceptTouchEvent");
                    break;
                }

                boolean canScroll = false;
                boolean canScrollVertically = true;
                final int verticalOffset = computeVerticalScrollOffset();
                final int verticalRange = computeVerticalScrollRange() - computeVerticalScrollExtent();
                if (verticalRange != 0) {
                    if (direction < 0) {
                        canScrollVertically = verticalOffset > 0;
                    } else {
                        canScrollVertically = verticalOffset < verticalRange - 1;
                    }

                    if (getScrollY() == 0 && !canScrollVertically) {
                        canScroll |= false;
                    } else {
                        canScroll |= true;
                        applyVerticalMoveAction(ev, pointerIndex);
                    }
                }

                boolean canScrollHorizontally = true;
                final int horizontalOffset = computeHorizontalScrollOffset();
                final int horizontalRange = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
                if (horizontalRange != 0) {
                    if (direction < 0) {
                        canScrollHorizontally = horizontalOffset > 0;
                    } else {
                        canScrollHorizontally = horizontalOffset < horizontalRange - 1;
                    }

                    if (getScrollX() == 0 && !canScrollHorizontally) {
                        canScroll |= false;
                    } else {
                        canScroll |= true;
                        applyHorizontalMoveAction(ev, pointerIndex);
                    }
                }

                if (!canScroll) {
                    return false;
                }

                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final int y = (int) ev.getY();
                final int x = (int) ev.getX();
                if (!inChild(x, y)) {
                    mIsBeingDragged = false;
                    recycleVelocityTracker();
                    break;
                }

                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionY = y;
                mLastMotionX = x;

                mActivePointerId = ev.getPointerId(0);

                initOrResetVelocityTracker();
                mYVelocityTracker.addMovement(ev);
                mXVelocityTracker.addMovement(ev);

                /*
                * If being flinged and user touches the screen, initiate drag;
                * otherwise don't.  mScroller.isFinished should be false when
                * being flinged.
                */
                mIsBeingDragged = !mScroller.isFinished();
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                recycleVelocityTracker();
                if (mScroller.springBack(this.getScrollX(), this.getScrollY(), 0, 0, getHorizontalScrollRange(), getVerticalScrollRange())) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        /*
        * The only time we want to intercept motion events is if we are in the
        * drag mode.
        */
        return mIsBeingDragged;
    }

    private void applyHorizontalMoveAction(MotionEvent ev, int pointerIndex) {
        final int x = (int) ev.getX(pointerIndex);
        final int xDiff = Math.abs(x - mLastMotionX);
        if (xDiff > mTouchSlop) {
            mIsBeingDragged = true;
            mLastMotionX = x;
            initVelocityTrackerIfNotExists();
            mXVelocityTracker.addMovement(ev);
            final ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    private void applyVerticalMoveAction(MotionEvent ev, int pointerIndex) {
        final int y = (int) ev.getY(pointerIndex);
        final int yDiff = Math.abs(y - mLastMotionY);
        if (yDiff > mTouchSlop) {
            mIsBeingDragged = true;
            mLastMotionY = y;
            initVelocityTrackerIfNotExists();
            mYVelocityTracker.addMovement(ev);
            final ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        mXVelocityTracker.addMovement(ev);
        mYVelocityTracker.addMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (getChildCount() == 0) {
                    return false;
                }
                if ((mIsBeingDragged = !mScroller.isFinished())) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionY = (int) ev.getY();
                mLastMotionX = (int) ev.getX();
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }

                final int y = (int) ev.getY(activePointerIndex);
                final int x = (int) ev.getX(activePointerIndex);
                int deltaY = mLastMotionY - y;

                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }

                int deltaX = mLastMotionX - x;

                if (!mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (deltaX > 0) {
                        deltaX -= mTouchSlop;
                    } else {
                        deltaX += mTouchSlop;
                    }
                }

                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    mLastMotionY = y;
                    mLastMotionX = x;

                    final int oldX = this.getScrollX();
                    final int oldY = this.getScrollY();

                    final int verticalRange = getVerticalScrollRange();
                    final int horizontalRange = getHorizontalScrollRange();

                    final int overscrollMode = getOverScrollMode();

                    final boolean canOverscroll = overscrollMode == OVER_SCROLL_ALWAYS ||
                            (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && (verticalRange > 0 || horizontalRange > 0));

                    // Calling overScrollBy will call onOverScrolled, which
                    // calls onScrollChanged if applicable.
                    if (overScrollBy(deltaX, 0, this.getScrollX(), this.getScrollY(),
                            horizontalRange, verticalRange, mOverscrollDistance, 0, true)) {
                        // Break our velocity if we hit a scroll barrier.
                        mXVelocityTracker.clear();
                    }

                    if (overScrollBy(0, deltaY, this.getScrollX(), this.getScrollY(),
                            horizontalRange, verticalRange, 0, mOverscrollDistance, true)) {
                        // Break our velocity if we hit a scroll barrier.
                        mYVelocityTracker.clear();
                    }

                    if (canOverscroll) {
                        final int pulledToY = oldY + deltaY;
                        final int pulledToX = oldX + deltaX;
                        if (pulledToY < 0) {
                            mEdgeGlowTop.onPull((float) deltaY / getHeight());
                            if (!mEdgeGlowBottom.isFinished()) {
                                mEdgeGlowBottom.onRelease();
                            }
                        } else if (pulledToY > verticalRange) {
                            mEdgeGlowBottom.onPull((float) deltaY / getHeight());
                            if (!mEdgeGlowTop.isFinished()) {
                                mEdgeGlowTop.onRelease();
                            }
                        }

                        if (pulledToX < 0) {
                            mEdgeGlowLeft.onPull((float) deltaX / getWidth());
                            if (!mEdgeGlowRight.isFinished()) {
                                mEdgeGlowRight.onRelease();
                            }
                        } else if (pulledToX > horizontalRange) {
                            mEdgeGlowRight.onPull((float) deltaX / getWidth());
                            if (!mEdgeGlowLeft.isFinished()) {
                                mEdgeGlowLeft.onRelease();
                            }
                        }

                        if (mEdgeGlowTop != null
                                && (!mEdgeGlowTop.isFinished() || !mEdgeGlowBottom.isFinished())) {
                            ViewCompat.postInvalidateOnAnimation(this);
                        }

                        if (mEdgeGlowLeft != null
                                && (!mEdgeGlowLeft.isFinished() || !mEdgeGlowRight.isFinished())) {
                            ViewCompat.postInvalidateOnAnimation(this);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker yVelocityTracker = mYVelocityTracker;
                    final VelocityTracker xVelocityTracker = mXVelocityTracker;
                    xVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    yVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                    int initialXVelocity = (int) xVelocityTracker.getXVelocity(mActivePointerId);
                    int initialYVelocity = (int) yVelocityTracker.getYVelocity(mActivePointerId);

                    if (getChildCount() > 0) {
                        if ((Math.abs(initialXVelocity) > mMinimumVelocity) || (Math.abs(initialYVelocity) > mMinimumVelocity)) {
                            fling(-initialXVelocity, -initialYVelocity);
                        } else {
                            if (mScroller.springBack(this.getScrollX(), this.getScrollY(), 0, getHorizontalScrollRange(), 0,
                                    getVerticalScrollRange())) {
                                ViewCompat.postInvalidateOnAnimation(this);
                            }
                        }
                    }

                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged && getChildCount() > 0) {
                    if (mScroller.springBack(this.getScrollX(), this.getScrollY(), 0, getHorizontalScrollRange(), 0, getVerticalScrollRange())) {
                        ViewCompat.postInvalidateOnAnimation(this);

                    }
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                mLastMotionX = (int) ev.getX(index);
                mLastMotionY = (int) ev.getY(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionX = (int) ev.getX(ev.findPointerIndex(mActivePointerId));
                mLastMotionY = (int) ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = (int) ev.getX(newPointerIndex);
            mLastMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mXVelocityTracker != null) {
                mXVelocityTracker.clear();
            }

            if (mYVelocityTracker != null) {
                mYVelocityTracker.clear();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_SCROLL: {
                    if (!mIsBeingDragged) {
                        final float hscroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
                        final float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                        boolean hasScrolled = false;
                        if (vscroll != 0) {
                            final int delta = (int) (vscroll * 0.2f/*getVerticalScrollFactor()*/);
                            final int range = getVerticalScrollRange();
                            int oldScrollY = this.getScrollY();
                            int newScrollY = oldScrollY - delta;
                            if (newScrollY < 0) {
                                newScrollY = 0;
                            } else if (newScrollY > range) {
                                newScrollY = range;
                            }
                            if (newScrollY != oldScrollY) {
                                super.scrollTo(this.getScrollX(), newScrollY);
                                hasScrolled |= true;
                            }
                        }

                        if (hscroll != 0) {
                            final int delta = (int) (hscroll * 0.2f/*getVerticalScrollFactor()*/);
                            final int range = getHorizontalScrollRange();
                            int oldScrollX = this.getScrollY();
                            int newScrollX = oldScrollX - delta;
                            if (newScrollX < 0) {
                                newScrollX = 0;
                            } else if (newScrollX > range) {
                                newScrollX = range;
                            }
                            if (newScrollX != oldScrollX) {
                                super.scrollTo(newScrollX, this.getScrollY());
                                hasScrolled |= true;
                            }
                        }

                        if (hasScrolled) {
                            return true;
                        }
                    }
                }
            }
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY,
                                  boolean clampedX, boolean clampedY) {
        // Treat animating scrolls differently; see #computeScroll() for why.
        if (!mScroller.isFinished()) {
            final int oldX = this.getScrollX();
            final int oldY = this.getScrollY();
            this.scrollTo(scrollX, scrollY);
            //this.inv
            onScrollChanged(this.getScrollX(), this.getScrollY(), oldX, oldY);
            if (clampedX || clampedY) {
                mScroller.springBack(this.getScrollX(), this.getScrollY(), 0, getHorizontalScrollRange(), 0, getVerticalScrollRange());
            }
        } else {
            super.scrollTo(scrollX, scrollY);
        }

        awakenScrollBars();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        if (!isEnabled()) {
            return false;
        }
        switch (action) {
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD: {
                final int viewportWidth = getWidth() - this.getPaddingRight() - this.getPaddingLeft();
                final int viewportHeight = getHeight() - this.getPaddingBottom() - this.getPaddingTop();
                final int targetScrollX = Math.min(this.getScrollX() + viewportWidth, getHorizontalScrollRange());
                final int targetScrollY = Math.min(this.getScrollY() + viewportHeight, getVerticalScrollRange());
                int smoothScrollToX = 0;
                int smoothScrollToY = 0;
                boolean smoothScrollTo = false;
                if (targetScrollY != this.getScrollY()) {
                    smoothScrollToY = targetScrollY;
                    smoothScrollTo |= true;
                }

                if (targetScrollX != this.getScrollX()) {
                    smoothScrollToX = targetScrollX;
                    smoothScrollTo |= true;
                }

                if (smoothScrollTo) {
                    smoothScrollTo(smoothScrollToX, smoothScrollToY);
                }
            }
            return false;
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD: {
                final int viewportWidth = getWidth() - this.getPaddingRight() - this.getPaddingLeft();
                final int viewportHeight = getHeight() - this.getPaddingBottom() - this.getPaddingTop();
                final int targetScrollX = Math.max(this.getScrollX() - viewportWidth, 0);
                final int targetScrollY = Math.max(this.getScrollY() - viewportHeight, 0);

                int smoothScrollToX = 0;
                int smoothScrollToY = 0;
                boolean smoothScrollTo = false;
                if (targetScrollY != this.getScrollY()) {
                    smoothScrollToY = targetScrollY;
                    smoothScrollTo |= true;
                }

                if (targetScrollX != this.getScrollX()) {
                    smoothScrollToX = targetScrollX;
                    smoothScrollTo |= true;
                }

                if (smoothScrollTo) {
                    smoothScrollTo(smoothScrollToX, smoothScrollToY);
                }
            }
            return false;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(RadScrollView.class.getName());
        if (isEnabled()) {
            final int hScrollRange = getHorizontalScrollRange();
            final int vScrollRange = getVerticalScrollRange();

            if (hScrollRange > 0) {
                info.setScrollable(true);
                if (this.getScrollX() > 0) {
                    info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                }
                if (this.getScrollX() < hScrollRange) {
                    info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }
            }

            if (vScrollRange > 0) {
                info.setScrollable(true);
                if (this.getScrollY() > 0) {
                    info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                }
                if (this.getScrollY() < vScrollRange) {
                    info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(RadScrollView.class.getName());
        final boolean hScrollable = getHorizontalScrollRange() > 0;
        final boolean vScrollable = getVerticalScrollRange() > 0;
        event.setScrollable(hScrollable || vScrollable);
        event.setScrollX(this.getScrollX());
        event.setScrollY(this.getScrollY());
        event.setMaxScrollX(getHorizontalScrollRange());
        event.setMaxScrollY(getVerticalScrollRange());
    }

    private int getVerticalScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0,
                    child.getHeight() - (getHeight() - this.getPaddingBottom() - this.getPaddingTop()));
        }
        return scrollRange;
    }

    private int getHorizontalScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0,
                    child.getWidth() - (getWidth() - this.getPaddingRight() - this.getPaddingLeft()));
        }
        return scrollRange;
    }

    /**
     * <p>
     * Finds the next focusable component that fits in the specified bounds.
     * </p>
     *
     * @param topFocus look for a candidate is the one at the top of the bounds
     *                 if topFocus is true, or at the bottom of the bounds if topFocus is
     *                 false
     * @param top      the top offset of the bounds in which a focusable must be
     *                 found
     * @param bottom   the bottom offset of the bounds in which a focusable must
     *                 be found
     * @return the next focusable component in the bounds or null if none can
     * be found
     */
    private View findFocusableViewInBoundsY(boolean topFocus, int top, int bottom) {

        List<View> focusables = getFocusables(View.FOCUS_FORWARD);
        View focusCandidate = null;

        /*
         * A fully contained focusable is one where its top is below the bound's
         * top, and its bottom is above the bound's bottom. A partially
         * contained focusable is one where some part of it is within the
         * bounds, but it also has some part that is not within bounds.  A fully contained
         * focusable is preferred to a partially contained focusable.
         */
        boolean foundFullyContainedFocusable = false;

        int count = focusables.size();
        for (int i = 0; i < count; i++) {
            View view = focusables.get(i);
            int viewTop = view.getTop();
            int viewBottom = view.getBottom();

            if (top < viewBottom && viewTop < bottom) {
                /*
                 * the focusable is in the target area, it is a candidate for
                 * focusing
                 */

                final boolean viewIsFullyContained = (top < viewTop) &&
                        (viewBottom < bottom);

                if (focusCandidate == null) {
                    /* No candidate, take this one */
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    final boolean viewIsCloserToBoundary =
                            (topFocus && viewTop < focusCandidate.getTop()) ||
                                    (!topFocus && viewBottom > focusCandidate
                                            .getBottom());

                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            /*
                             * We're dealing with only fully contained views, so
                             * it has to be closer to the boundary to beat our
                             * candidate
                             */
                            focusCandidate = view;
                        }
                    } else {
                        if (viewIsFullyContained) {
                            /* Any fully contained view beats a partially contained view */
                            focusCandidate = view;
                            foundFullyContainedFocusable = true;
                        } else if (viewIsCloserToBoundary) {
                            /*
                             * Partially contained view beats another partially
                             * contained view if it's closer
                             */
                            focusCandidate = view;
                        }
                    }
                }
            }
        }

        return focusCandidate;
    }

    /**
     * <p>
     * Finds the next focusable component that fits in the specified bounds.
     * </p>
     *
     * @param topFocus look for a candidate is the one at the top of the bounds
     *                 if topFocus is true, or at the bottom of the bounds if topFocus is
     *                 false
     * @param left     the top offset of the bounds in which a focusable must be
     *                 found
     * @param right    the bottom offset of the bounds in which a focusable must
     *                 be found
     * @return the next focusable component in the bounds or null if none can
     * be found
     */
    private View findFocusableViewInBoundsX(boolean topFocus, int left, int right) {

        List<View> focusables = getFocusables(View.FOCUS_FORWARD);
        View focusCandidate = null;

        /*
         * A fully contained focusable is one where its top is below the bound's
         * top, and its bottom is above the bound's bottom. A partially
         * contained focusable is one where some part of it is within the
         * bounds, but it also has some part that is not within bounds.  A fully contained
         * focusable is preferred to a partially contained focusable.
         */
        boolean foundFullyContainedFocusable = false;

        int count = focusables.size();
        for (int i = 0; i < count; i++) {
            View view = focusables.get(i);
            int viewLeft = view.getLeft();
            int viewRight = view.getRight();

            if (viewLeft < viewRight && viewLeft < right) {
                /*
                 * the focusable is in the target area, it is a candidate for
                 * focusing
                 */

                final boolean viewIsFullyContained = (left < viewLeft) &&
                        (viewRight < right);

                if (focusCandidate == null) {
                    /* No candidate, take this one */
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    final boolean viewIsCloserToBoundary =
                            (topFocus && viewLeft < focusCandidate.getLeft()) ||
                                    (!topFocus && viewRight > focusCandidate
                                            .getRight());

                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            /*
                             * We're dealing with only fully contained views, so
                             * it has to be closer to the boundary to beat our
                             * candidate
                             */
                            focusCandidate = view;
                        }
                    } else {
                        if (viewIsFullyContained) {
                            /* Any fully contained view beats a partially contained view */
                            focusCandidate = view;
                            foundFullyContainedFocusable = true;
                        } else if (viewIsCloserToBoundary) {
                            /*
                             * Partially contained view beats another partially
                             * contained view if it's closer
                             */
                            focusCandidate = view;
                        }
                    }
                }
            }
        }

        return focusCandidate;
    }

    /**
     * <p>Handles scrolling in response to a "page up/down" shortcut press. This
     * method will scroll the view by one page up or down and give the focus
     * to the topmost/bottommost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_UP}
     *                  to go one page up or
     *                  {@link android.view.View#FOCUS_DOWN} to go one page down
     * @return true if the key event is consumed by this method, false otherwise
     */
    public boolean pageScroll(int direction) {
        boolean down = direction == View.FOCUS_DOWN;
        int height = getHeight();
        int width = getWidth();

        if (down) {
            mTempRect.top = getScrollY() + height;
            mTempRect.left = getScrollX() + width;
            int count = getChildCount();
            if (count > 0) {
                View view = getChildAt(count - 1);
                if (mTempRect.top + height > view.getBottom()) {
                    mTempRect.top = view.getBottom() - height;
                }

                if (mTempRect.left + width > view.getRight()) {
                    mTempRect.left = view.getRight() - width;
                }
            }
        } else {
            mTempRect.top = getScrollY() - height;
            if (mTempRect.top < 0) {
                mTempRect.top = 0;
            }

            mTempRect.left = getScrollX() - width;
            if (mTempRect.left < 0) {
                mTempRect.left = 0;
            }
        }
        mTempRect.bottom = mTempRect.top + height;
        mTempRect.right = mTempRect.left + width;

        return scrollAndFocus(direction, mTempRect.top, mTempRect.bottom, mTempRect.left, mTempRect.right);
    }

    /**
     * <p>Handles scrolling in response to a "home/end" shortcut press. This
     * method will scroll the view to the top or bottom and give the focus
     * to the topmost/bottommost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_UP}
     *                  to go the top of the view or
     *                  {@link android.view.View#FOCUS_DOWN} to go the bottom
     * @return true if the key event is consumed by this method, false otherwise
     */
    public boolean fullScroll(int direction) {
        boolean down = direction == View.FOCUS_DOWN;
        int height = getHeight();

        mTempRect.top = 0;
        mTempRect.bottom = height;

        if (down) {
            int count = getChildCount();
            if (count > 0) {
                View view = getChildAt(count - 1);
                mTempRect.bottom = view.getBottom() + this.getPaddingBottom();
                mTempRect.top = mTempRect.bottom - height;
            }
        }

        return scrollAndFocus(direction, mTempRect.top, mTempRect.bottom, mTempRect.left, mTempRect.right);
    }

    /**
     * <p>Scrolls the view to make the area defined by <code>top</code> and
     * <code>bottom</code> visible. This method attempts to give the focus
     * to a component visible in this area. If no component can be focused in
     * the new visible area, the focus is reclaimed by this ScrollView.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_UP}
     *                  to go upward, {@link android.view.View#FOCUS_DOWN} to downward
     * @param top       the top offset of the new area to be made visible
     * @param bottom    the bottom offset of the new area to be made visible
     * @return true if the key event is consumed by this method, false otherwise
     */
    private boolean scrollAndFocus(int direction, int top, int bottom, int left, int right) {
        boolean handled = true;

        int height = getHeight();
        int width = getWidth();
        int containerLeft = getScrollX();
        int containerTop = getScrollY();
        int containerRight = containerLeft + width;
        int containerBottom = containerTop + height;
        boolean up = direction == View.FOCUS_UP;

        View newFocused = findFocusableViewInBoundsY(up, top, bottom);
        if (newFocused == null) {
            newFocused = findFocusableViewInBoundsX(up, left, right);
            if (newFocused == null) {
                newFocused = this;
            }
        }

        if (top >= containerTop && bottom <= containerBottom || left >= containerLeft && right <= containerRight) {
            handled = false;
        } else {
            int deltaX = up ? (left - containerLeft) : (right - containerRight);
            int deltaY = up ? (top - containerTop) : (bottom - containerBottom);
            doScroll(deltaX, deltaY);
        }

        if (newFocused != findFocus()) newFocused.requestFocus(direction);

        return handled;
    }

    /**
     * Handle scrolling in response to an up or down arrow click.
     *
     * @param direction The direction corresponding to the arrow key that was
     *                  pressed
     * @return True if we consumed the event, false otherwise
     */
    public boolean arrowScroll(int direction) {

        View currentFocused = findFocus();
        if (currentFocused == this) currentFocused = null;

        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);

        final int maxHorizontalJump = getMaxHorizontalScrollAmount();
        final int maxVerticalJump = getMaxVerticalScrollAmount();

        if (nextFocused != null && (isWithinDeltaOfScreenX(nextFocused, maxHorizontalJump, getHeight()) || isWithinDeltaOfScreenY(nextFocused, maxVerticalJump, getHeight()))) {
            nextFocused.getDrawingRect(mTempRect);
            offsetDescendantRectToMyCoords(nextFocused, mTempRect);
            Point scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);
            doScroll(scrollDelta.x, scrollDelta.y);
            nextFocused.requestFocus(direction);
        } else {
            // no new focus
            int scrollDeltaX = maxHorizontalJump;
            int scrollDeltaY = maxVerticalJump;

            if (direction == View.FOCUS_UP && (getScrollY() < scrollDeltaY || getScrollX() < scrollDeltaX)) {
                scrollDeltaX = getScrollX();
                scrollDeltaY = getScrollY();
            } else if (direction == View.FOCUS_DOWN) {
                if (getChildCount() > 0) {
                    int daBottom = getChildAt(0).getBottom();
                    int daRight = getChildAt(0).getRight();
                    int screenRight = getScrollX() + getWidth() - this.getPaddingRight();
                    int screenBottom = getScrollY() + getHeight() - this.getPaddingBottom();
                    if (daBottom - screenBottom < maxVerticalJump) {
                        scrollDeltaY = daBottom - screenBottom;
                    }

                    if (daRight - screenRight < maxHorizontalJump) {
                        scrollDeltaX = daRight - screenRight;
                    }
                }
            }
            if (scrollDeltaX == 0 && scrollDeltaY == 0) {
                return false;
            }
            doScroll(direction == View.FOCUS_DOWN ? scrollDeltaX : -scrollDeltaX, direction == View.FOCUS_DOWN ? scrollDeltaY : -scrollDeltaY);
        }

        if (currentFocused != null && currentFocused.isFocused()
                && isOffScreen(currentFocused)) {
            // previously focused item still has focus and is off screen, give
            // it up (take it back to ourselves)
            // (also, need to temporarily force FOCUS_BEFORE_DESCENDANTS so we are
            // sure to
            // get it)
            final int descendantFocusability = getDescendantFocusability();  // save
            setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            requestFocus();
            setDescendantFocusability(descendantFocusability);  // restore
        }
        return true;
    }

    /**
     * @return whether the descendant of this scroll view is scrolled off
     * screen.
     */
    private boolean isOffScreen(View descendant) {
        return !(isWithinDeltaOfScreenY(descendant, 0, getHeight()) || isWithinDeltaOfScreenX(descendant, 0, getWidth()));
    }

    /**
     * @return whether the descendant of this scroll view is within delta
     * pixels of being on the screen.
     */
    private boolean isWithinDeltaOfScreenY(View descendant, int delta, int height) {
        descendant.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(descendant, mTempRect);

        return (mTempRect.bottom + delta) >= getScrollY()
                && (mTempRect.top - delta) <= (getScrollY() + height);
    }

    /**
     * @return whether the descendant of this scroll view is within delta
     * pixels of being on the screen.
     */
    private boolean isWithinDeltaOfScreenX(View descendant, int delta, int width) {
        descendant.getDrawingRect(mTempRect);
        offsetDescendantRectToMyCoords(descendant, mTempRect);

        return (mTempRect.right + delta) >= getScrollX()
                && (mTempRect.left - delta) <= (getScrollX() + width);
    }

    /**
     * Smooth scroll by a X and Y delta
     */
    private void doScroll(int xDelta, int yDelta) {
        if (xDelta != 0 || yDelta != 0) {
            if (mSmoothScrollingEnabled) {
                smoothScrollBy(xDelta, yDelta);
            } else {
                scrollBy(xDelta, yDelta);
            }
        }
    }

    /**
     * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
     *
     * @param dx the number of pixels to scroll by on the X axis
     * @param dy the number of pixels to scroll by on the Y axis
     */
    public final void smoothScrollBy(int dx, int dy) {
        if (getChildCount() == 0) {
            // Nothing to do.
            return;
        }
        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
        if (duration > ANIMATED_SCROLL_GAP) {
            final int height = getHeight() - this.getPaddingBottom() - this.getPaddingTop();
            final int width = getWidth() - this.getPaddingRight() - this.getPaddingLeft();
            final int bottom = getChildAt(0).getHeight();
            final int right = getChildAt(0).getWidth();
            final int maxY = Math.max(0, bottom - height);
            final int maxX = Math.max(0, right - width);
            final int scrollY = this.getScrollY();
            final int scrollX = this.getScrollX();
            dy = Math.max(0, Math.min(scrollY + dy, maxY)) - scrollY;
            dx = Math.max(0, Math.min(scrollX + dx, maxX)) - scrollX;
            mScroller.startScroll(scrollX, scrollY, dx, dy);
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            scrollBy(dx, dy);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }

    /**
     * Like {@link #scrollTo}, but scroll smoothly instead of immediately.
     *
     * @param x the position where to scroll on the X axis
     * @param y the position where to scroll on the Y axis
     */
    public final void smoothScrollTo(int x, int y) {
        smoothScrollBy(x - this.getScrollX(), y - this.getScrollY());
    }

    /**
     * <p>The scroll range of a scroll view is the overall height of all of its
     * children.</p>
     */
    @Override
    protected int computeHorizontalScrollRange() {
        final int count = getChildCount();
        final int contentWidth = getWidth() - this.getPaddingRight() - this.getPaddingLeft();
        if (count == 0) {
            return contentWidth;
        }

        int scrollRange = getChildAt(0).getRight();
        final int scrollX = this.getScrollX();
        final int overScrollRight = Math.max(0, scrollRange - contentWidth);
        if (scrollX < 0) {
            scrollRange -= scrollX;
        } else if (scrollX > overScrollRight) {
            scrollRange += scrollX - overScrollRight;
        }

        return scrollRange;
    }

    /**
     * <p>The scroll range of a scroll view is the overall height of all of its
     * children.</p>
     */
    @Override
    protected int computeVerticalScrollRange() {
        final int count = getChildCount();
        final int contentHeight = getHeight() - this.getPaddingBottom() - this.getPaddingTop();
        if (count == 0) {
            return contentHeight;
        }

        int scrollRange = getChildAt(0).getBottom();
        final int scrollY = this.getScrollY();
        final int overScrollBottom = Math.max(0, scrollRange - contentHeight);
        if (scrollY < 0) {
            scrollRange -= scrollY;
        } else if (scrollY > overScrollBottom) {
            scrollRange += scrollY - overScrollBottom;
        }

        return scrollRange;
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return Math.max(0, super.computeHorizontalScrollOffset());
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, this.getPaddingLeft()
                + this.getPaddingRight(), lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, this.getPaddingTop()
                + this.getPaddingBottom(), lp.height);
        ;

        if ((this.scrollMode & SCROLLING_MODE_VERTICAL) != 0) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        if ((this.scrollMode & SCROLLING_MODE_HORIZONTAL) != 0) {
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                this.getPaddingLeft() + this.getPaddingRight() + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width
        );
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                this.getPaddingTop() + this.getPaddingBottom() + lp.topMargin + lp.bottomMargin
                        + heightUsed, lp.height
        );

        if ((this.scrollMode & SCROLLING_MODE_VERTICAL) != 0) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);
        }

        if ((this.scrollMode & SCROLLING_MODE_HORIZONTAL) != 0) {
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.leftMargin + lp.rightMargin, MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // This is called at drawing time by ViewGroup.  We don't want to
            // re-show the scrollbars at this point, which scrollTo will do,
            // so we replicate most of scrollTo here.
            //
            //         It's a little odd to call onScrollChanged from inside the drawing.
            //
            //         It is, except when you remember that computeScroll() is used to
            //         animate scrolling. So unless we want to defer the onScrollChanged()
            //         until the end of the animated scrolling, we don't really have a
            //         choice here.
            //
            //         I agree.  The alternative, which I think would be worse, is to post
            //         something and tell the subclasses later.  This is bad because there
            //         will be a window where this.getScrollX()/Y is different from what the app
            //         thinks it is.
            //

            int oldX = this.getScrollX();
            int oldY = this.getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (oldX != x || oldY != y || true) {
                final int xRange = getHorizontalScrollRange();
                final int yRange = getVerticalScrollRange();

                final int overscrollMode = getOverScrollMode();
                final boolean canOverscrollVertically = overscrollMode == OVER_SCROLL_ALWAYS ||
                        (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && yRange > 0);
                final boolean canOverscrollHorizontally = overscrollMode == OVER_SCROLL_ALWAYS ||
                        (overscrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && xRange > 0);

                overScrollBy(x - oldX, y - oldY, oldX, oldY, xRange, yRange,
                        mOverflingDistance, mOverflingDistance, false);
                onScrollChanged(this.getScrollX(), this.getScrollY(), oldX, oldY);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    if (canOverscrollHorizontally) {
                        doOverScrollHorizontally(oldX, 0, x, 0, xRange, 0);
                    }

                    if (canOverscrollVertically) {
                        doOverScrollVertically(0, oldY, 0, y, 0, yRange);
                    }
                }
            }

            if (!awakenScrollBars()) {
                // Keep on drawing until the animation has finished.
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void doOverScrollHorizontally(int oldX, int oldY, int x, int y, int xRange, int yRange) {
        if (x < 0 && oldX >= 0) {
            mEdgeGlowLeft.onAbsorb((int) mScroller.getCurrVelocity());
        } else if (x > xRange && oldX <= xRange) {
            mEdgeGlowRight.onAbsorb((int) mScroller.getCurrVelocity());
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void doOverScrollVertically(int oldX, int oldY, int x, int y, int xRange, int yRange) {
        if (y < 0 && oldY >= 0) {
            mEdgeGlowTop.onAbsorb((int) mScroller.getCurrVelocity());
        } else if (y > yRange && oldY <= yRange) {
            mEdgeGlowBottom.onAbsorb((int) mScroller.getCurrVelocity());
        }
    }

    /**
     * Scrolls the view to the given child.
     *
     * @param child the View to scroll to
     */
    private void scrollToChild(View child) {
        child.getDrawingRect(mTempRect);

        /* Offset from child's local coordinates to ScrollView coordinates */
        offsetDescendantRectToMyCoords(child, mTempRect);

        Point scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);

        if (scrollDelta != new Point(0, 0)) {
            scrollBy(scrollDelta.x, scrollDelta.y);
        }
    }

    /**
     * If rect is off screen, scroll just enough to get it (or at least the
     * first screen size chunk of it) on screen.
     *
     * @param rect      The rectangle.
     * @param immediate True to scroll immediately without animation
     * @return true if scrolling was performed
     */
    private boolean scrollToChildRect(Rect rect, boolean immediate) {
        final Point delta = computeScrollDeltaToGetChildRectOnScreen(rect);
        final boolean scroll = delta != new Point(0, 0);
        if (scroll) {
            if (immediate) {
                scrollBy(delta.x, delta.y);
            } else {
                smoothScrollBy(delta.x, delta.y);
            }
        }
        return scroll;
    }

    /**
     * Compute the amount to scroll in the Y direction in order to get
     * a rectangle completely on the screen (or, if taller than the screen,
     * at least the first screen size chunk of it).
     *
     * @param rect The rect.
     * @return The scroll delta.
     */
    protected Point computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        if (getChildCount() == 0) return new Point(0, 0);

        int height = getHeight();
        int width = getWidth();
        int screenTop = getScrollY();
        int screenLeft = getScrollX();
        int screenBottom = screenTop + height;
        int screenRight = screenLeft + width;

        int xFadingEdge = getHorizontalFadingEdgeLength();
        int yFadingEdge = getVerticalFadingEdgeLength();

        // leave room for top fading edge as long as rect isn't at very top
        if (rect.top > 0) {
            screenTop += yFadingEdge;
        }

        // leave room for top fading edge as long as rect isn't at very top
        if (rect.left > 0) {
            screenLeft += xFadingEdge;
        }

        // leave room for bottom fading edge as long as rect isn't at very bottom
        if (rect.bottom < getChildAt(0).getHeight()) {
            screenBottom -= yFadingEdge;
        }

        // leave room for bottom fading edge as long as rect isn't at very bottom
        if (rect.right < getChildAt(0).getWidth()) {
            screenRight -= xFadingEdge;
        }

        int scrollXDelta = 0;
        int scrollYDelta = 0;

        if (rect.bottom > screenBottom && rect.top > screenTop) {
            // need to move down to get it in view: move down just enough so
            // that the entire rectangle is in view (or at least the first
            // screen size chunk).

            if (rect.height() > height) {
                // just enough to get screen size chunk on
                scrollYDelta += (rect.top - screenTop);
            } else {
                // get entire rect at bottom of screen
                scrollYDelta += (rect.bottom - screenBottom);
            }

            // make sure we aren't scrolling beyond the end of our content
            int bottom = getChildAt(0).getBottom();
            int distanceToBottom = bottom - screenBottom;
            scrollYDelta = Math.min(scrollYDelta, distanceToBottom);

        } else if (rect.top < screenTop && rect.bottom < screenBottom) {
            // need to move up to get it in view: move up just enough so that
            // entire rectangle is in view (or at least the first screen
            // size chunk of it).

            if (rect.height() > height) {
                // screen size chunk
                scrollYDelta -= (screenBottom - rect.bottom);
            } else {
                // entire rect at top
                scrollYDelta -= (screenTop - rect.top);
            }

            // make sure we aren't scrolling any further than the top our content
            scrollYDelta = Math.max(scrollYDelta, -getScrollY());
        }

        if (rect.right > screenRight && rect.left > screenLeft) {
            // need to move down to get it in view: move down just enough so
            // that the entire rectangle is in view (or at least the first
            // screen size chunk).

            if (rect.width() > width) {
                // just enough to get screen size chunk on
                scrollXDelta += (rect.left - screenLeft);
            } else {
                // get entire rect at bottom of screen
                scrollXDelta += (rect.right - screenRight);
            }

            // make sure we aren't scrolling beyond the end of our content
            int right = getChildAt(0).getRight();
            int distanceToRight = right - screenRight;
            scrollXDelta = Math.min(scrollXDelta, distanceToRight);

        } else if (rect.left < screenLeft && rect.right < screenRight) {
            // need to move up to get it in view: move up just enough so that
            // entire rectangle is in view (or at least the first screen
            // size chunk of it).

            if (rect.width() > width) {
                // screen size chunk
                scrollXDelta -= (screenRight - rect.right);
            } else {
                // entire rect at top
                scrollXDelta -= (screenLeft - rect.left);
            }

            // make sure we aren't scrolling any further than the top our content
            scrollXDelta = Math.max(scrollXDelta, -getScrollX());
        }

        return new Point(scrollXDelta, scrollYDelta);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (!mIsLayoutDirty) {
            scrollToChild(focused);
        } else {
            // The child may not be laid out yet, we can't compute the scroll yet
            mChildToScrollTo = focused;
        }
        super.requestChildFocus(child, focused);
    }


    /**
     * When looking for focus in children of a scroll view, need to be a little
     * more careful not to give focus to something that is scrolled off screen.
     * <p/>
     * This is more expensive than the default {@link android.view.ViewGroup}
     * implementation, otherwise this behavior might have been made the default.
     */
    @Override
    protected boolean onRequestFocusInDescendants(int direction,
                                                  Rect previouslyFocusedRect) {

        // convert from forward / backward notation to up / down / left / right
        // (ugh).
        if (direction == View.FOCUS_FORWARD) {
            direction = View.FOCUS_DOWN;
        } else if (direction == View.FOCUS_BACKWARD) {
            direction = View.FOCUS_UP;
        }

        final View nextFocus = previouslyFocusedRect == null ?
                FocusFinder.getInstance().findNextFocus(this, null, direction) :
                FocusFinder.getInstance().findNextFocusFromRect(this,
                        previouslyFocusedRect, direction);

        if (nextFocus == null) {
            return false;
        }

        if (isOffScreen(nextFocus)) {
            return false;
        }

        return nextFocus.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle,
                                                 boolean immediate) {
        // offset into coordinate space of this scroll view
        rectangle.offset(child.getLeft() - child.getScrollX(),
                child.getTop() - child.getScrollY());

        return scrollToChildRect(rectangle, immediate);
    }

    @Override
    public void requestLayout() {
        mIsLayoutDirty = true;
        super.requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isLaidOut = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mIsLayoutDirty = false;
        // Give a child focus if it needs it
        if (mChildToScrollTo != null && isViewDescendantOf(mChildToScrollTo, this)) {
            scrollToChild(mChildToScrollTo);
        }
        mChildToScrollTo = null;

        if (!this.isLaidOut) {
            if (mSavedState != null) {
                this.scrollTo(mSavedState.xScrollPosition, mSavedState.yScrollPosition);
                mSavedState = null;
            } // this.getScrollY() default value is "0"

            final int childHeight = (getChildCount() > 0) ? getChildAt(0).getMeasuredHeight() : 0;
            final int childWidth = (getChildCount() > 0) ? getChildAt(0).getMeasuredWidth() : 0;
            final int verticalScrollRange = Math.max(0,
                    childHeight - (b - t - this.getPaddingBottom() - this.getPaddingTop()));
            final int horizontalScrollRange = Math.max(0,
                    childWidth - (r - l - this.getPaddingRight() - this.getPaddingLeft()));

            int xScroll = 0;
            int yScroll = 0;
            // Don't forget to clamp
            if (this.getScrollY() > verticalScrollRange) {
                yScroll = verticalScrollRange;
            } else if (this.getScrollY() < 0) {
                yScroll = 0;
            }

            if (this.getScrollX() > horizontalScrollRange) {
                xScroll = horizontalScrollRange;
            } else if (this.getScrollX() < 0) {
                xScroll = 0;
            }

            this.scrollTo(xScroll, yScroll);
        } else {
            this.isLaidOut = true;
        }

        // Calling this with the present values causes it to re-claim them
        scrollTo(this.getScrollX(), this.getScrollY());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        View currentFocused = findFocus();
        if (null == currentFocused || this == currentFocused)
            return;

        // If the currently-focused view was visible on the screen when the
        // screen was at the old height, then scroll the screen to make that
        // view visible with the new screen height.
        if (isWithinDeltaOfScreenX(currentFocused, 0, oldw) || isWithinDeltaOfScreenY(currentFocused, 0, oldh)) {
            currentFocused.getDrawingRect(mTempRect);
            offsetDescendantRectToMyCoords(currentFocused, mTempRect);
            Point scrollDelta = computeScrollDeltaToGetChildRectOnScreen(mTempRect);
            doScroll(scrollDelta.x, scrollDelta.y);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isLaidOut = false;
    }


    /**
     * Return true if child is a descendant of parent, (or equal to the parent).
     */
    private static boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }

        final ViewParent theParent = child.getParent();
        return (theParent instanceof ViewGroup) && isViewDescendantOf((View) theParent, parent);
    }

    /**
     * Fling the scroll view
     *
     * @param velocityY The initial velocity in the Y direction. Positive
     *                  numbers mean that the finger/cursor is moving down the screen,
     *                  which means we want to scroll towards the top.
     */
    public void fling(int velocityX, int velocityY) {
        if (getChildCount() > 0) {
            int height = getHeight() - this.getPaddingBottom() - this.getPaddingTop();
            int width = getWidth() - this.getPaddingRight() - this.getPaddingLeft();
            int bottom = getChildAt(0).getHeight();
            int right = getChildAt(0).getWidth();

            mScroller.fling(this.getScrollX(), this.getScrollY(), velocityX, velocityY, 0, Math.max(0, right - width), 0,
                    Math.max(0, bottom - height), width / 2, height / 2);

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;

        recycleVelocityTracker();

        if (mEdgeGlowTop != null) {
            mEdgeGlowTop.onRelease();
            mEdgeGlowBottom.onRelease();
        }

        if (mEdgeGlowLeft != null) {
            mEdgeGlowLeft.onRelease();
            mEdgeGlowRight.onRelease();
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>This version also clamps the scrolling to the bounds of our child.
     */
    @Override
    public void scrollTo(int x, int y) {
        // we rely on the fact the View.scrollBy calls scrollTo.
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            x = clamp(x, getWidth() - this.getPaddingRight() - this.getPaddingLeft(), child.getWidth());
            y = clamp(y, getHeight() - this.getPaddingBottom() - this.getPaddingTop(), child.getHeight());
            if (x != this.getScrollX() || y != this.getScrollY()) {
                super.scrollTo(x, y);
            }
        }
    }

    @Override
    public void setOverScrollMode(int mode) {
        if (mode != OVER_SCROLL_NEVER) {
            if (mEdgeGlowTop == null) {
                Context context = getContext();
                mEdgeGlowTop = new EdgeEffectCompat(context);
                mEdgeGlowBottom = new EdgeEffectCompat(context);
            }

            if (mEdgeGlowLeft == null) {
                Context context = getContext();
                mEdgeGlowLeft = new EdgeEffectCompat(context);
                mEdgeGlowRight = new EdgeEffectCompat(context);
            }
        } else {
            mEdgeGlowTop = null;
            mEdgeGlowBottom = null;
            mEdgeGlowLeft = null;
            mEdgeGlowRight = null;
        }
        super.setOverScrollMode(mode);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mEdgeGlowTop != null) {
            final int scrollY = this.getScrollY();
            if (!mEdgeGlowTop.isFinished()) {
                final int restoreCount = canvas.save();
                final int width = getWidth() - this.getPaddingLeft() - this.getPaddingRight();

                canvas.translate(this.getPaddingLeft() + getScrollX(), Math.max(0, scrollY));
                mEdgeGlowTop.setSize(width, getHeight());
                if (mEdgeGlowTop.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!mEdgeGlowBottom.isFinished()) {
                final int restoreCount = canvas.save();
                final int width = getWidth() - this.getPaddingLeft() - this.getPaddingRight();
                final int height = getHeight();

                canvas.translate(-width + this.getPaddingLeft() + this.getScrollX(),
                        Math.max(getVerticalScrollRange(), scrollY) + height);
                canvas.rotate(180, width, 0);
                mEdgeGlowBottom.setSize(width, height);
                if (mEdgeGlowBottom.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount);
            }
        }

        if (mEdgeGlowLeft != null) {
            final int scrollX = this.getScrollX();
            if (!mEdgeGlowLeft.isFinished()) {
                final int restoreCount = canvas.save();
                final int height = getHeight() - this.getPaddingTop() - this.getPaddingBottom();

                canvas.translate(Math.min(0, scrollX), Math.max(0, getScrollY()) + this.getPaddingTop() + height);
                canvas.rotate(270);
                mEdgeGlowLeft.setSize(height, getWidth());
                if (mEdgeGlowLeft.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!mEdgeGlowRight.isFinished()) {
                final int restoreCount = canvas.save();
                final int height = getHeight() - this.getPaddingTop() - this.getPaddingBottom();
                final int width = getWidth();

                canvas.translate(Math.min(getHorizontalScrollRange(), scrollX) + width, this.getPaddingTop() + Math.max(0, this.getScrollY()));
                canvas.rotate(90);
                mEdgeGlowRight.setSize(height, width);
                if (mEdgeGlowRight.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount);
            }
        }
    }

    private static int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            /* my >= child is this case:
             *                    |--------------- me ---------------|
             *     |------ child ------|
             * or
             *     |--------------- me ---------------|
             *            |------ child ------|
             * or
             *     |--------------- me ---------------|
             *                                  |------ child ------|
             *
             * n < 0 is this case:
             *     |------ me ------|
             *                    |-------- child --------|
             *     |-- this.getScrollX() --|
             */
            return 0;
        }
        if ((my + n) > child) {
            /* this case:
             *                    |------ me ------|
             *     |------ child ------|
             *     |-- this.getScrollX() --|
             */
            return child - my;
        }
        return n;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (this.getContext().getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Some old apps reused IDs in ways they shouldn't have.
            // Don't break them, but they don't get scroll state restoration.
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mSavedState = ss;
        requestLayout();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (this.getContext().getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Some old apps reused IDs in ways they shouldn't have.
            // Don't break them, but they don't get scroll state restoration.
            return super.onSaveInstanceState();
        }
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.xScrollPosition = this.getScrollX();
        ss.yScrollPosition = this.getScrollY();
        return ss;
    }

    static class SavedState extends BaseSavedState {
        public int xScrollPosition;
        public int yScrollPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            int[] values = new int[2];
            source.readIntArray(values);
            this.xScrollPosition = values[0];
            this.xScrollPosition = values[1];
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            int[] values = new int[2];
            values[0] = this.xScrollPosition;
            values[1] = this.yScrollPosition;
            dest.writeIntArray(values);

        }

        @Override
        public String toString() {
            return "RadScrollView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " scrollPositionX=" + xScrollPosition + ", scrollPositionY=" + yScrollPosition + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
