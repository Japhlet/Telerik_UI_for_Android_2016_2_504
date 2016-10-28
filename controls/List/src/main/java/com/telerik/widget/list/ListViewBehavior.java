package com.telerik.widget.list;

import android.graphics.Canvas;
import android.os.Parcelable;
import android.view.MotionEvent;

/**
 * Represents a base class for behaviors used by {@link com.telerik.widget.list.RadListView}.
 */
public class ListViewBehavior {

    private RadListView owner;

    /**
     * Called when {@link com.telerik.widget.list.RadListView}'s onLayout method is called.
     *
     * Derived classes with children should override
     * this method and call layout on each of
     * their children.
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    /**
     * Called when the behavior is attached to {@link com.telerik.widget.list.RadListView} with
     * addBehavior(ListViewBehavior).
     *
     * @param listView The parent list view
     */
    public void onAttached(RadListView listView) {
        this.owner = listView;
    }

    /**
     * Called when the behavior is detached to {@link com.telerik.widget.list.RadListView} with
     * removeBehavior(ListViewBehavior).
     *
     * @param listView The parent list view
     */
    public void onDetached(RadListView listView) {
        this.owner = null;
    }

    /**
     * Called when the tap up gesture is detected by {@link com.telerik.widget.list.RadListView}'s
     * {@link com.telerik.widget.list.ListViewGestureListener}.
     *
     * @param motionEvent The motion event
     */
    public void onTapUp(MotionEvent motionEvent){
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent){
        return false;
    }

    /**
     * Called when the drag gesture is detected by {@link com.telerik.widget.list.RadListView}'s
     * {@link com.telerik.widget.list.ListViewGestureListener}.
     *
     * @param startX the x coordinate where the gesture started
     * @param startY the y coordinate where the gesture started
     * @param currentX the x coordinate where the gesture occurring now
     * @param currentY the y coordinate where the gesture occurring now
     */
    public boolean onShortPressDrag(float startX, float startY, float currentX, float currentY) {
        return false;
    }

    /**
     * Called when a previous gesture ends on up or cancel.
     *
     * @param isCanceled whether the gesture was cancelled
     */
    public boolean onActionUpOrCancel(boolean isCanceled) {
        return false;
    }

    /**
     * Called when the long press gesture is detected by {@link com.telerik.widget.list.RadListView}'s
     * {@link com.telerik.widget.list.ListViewGestureListener}.
     *
     * @param motionEvent The motion event
     */
    public void onLongPress(MotionEvent motionEvent) {
    }

    /**
     * Called when the drag gesture is detected by {@link com.telerik.widget.list.RadListView}'s
     * {@link com.telerik.widget.list.ListViewGestureListener} which started with long press.
     *
     * @param startX the x coordinate where the gesture started
     * @param startY the y coordinate where the gesture started
     * @param currentX the x coordinate where the gesture occurring now
     * @param currentY the y coordinate where the gesture occurring now
     */
    public void onLongPressDrag(float startX, float startY, float currentX, float currentY) {
    }

    /**
     * Called when a current gesture combination of long press and drag ends on up or cancel.
     *
     * @param isCanceled whether the gesture was cancelled
     */
    public boolean onLongPressDragEnded(boolean isCanceled) {
        return false;
    }

    /**
     * Called when the fling gesture is detected by {@link com.telerik.widget.list.RadListView}'s
     * {@link com.telerik.widget.list.ListViewGestureListener}.
     *
     * @param motionEvent   The first down motion event that started the fling.
     * @param motionEvent2  The move motion event that triggered the current onFling.
     * @param velocityX     The velocity of this fling measured in pixels per second
     *                      along the x axis.
     * @param velocityY     The velocity of this fling measured in pixels per second
     *                      along the y axis.
     */
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float velocityX, float velocityY) {
        return false;
    }

    public void onScrolled(int dx, int dy) {
    }

    /**
     * Called when {@link com.telerik.widget.list.RadListView}'s onDispatchDraw method is called.
     *
     * @param canvas the canvas on which to draw the view
     */
    public void onDispatchDraw(Canvas canvas) {
    }

    /**
     * Returns true if the current behavior is in progress.
     */
    public boolean isInProgress() {
        return false;
    }

    /**
     * Returns the list view instance where the current behavior is attached.
     */
    protected RadListView owner() {
        if(owner == null) {
            throw new UnsupportedOperationException("Behavior is not attached to RadListView. Use RadListView's addBehavior method to attach it.");
        }
        return owner;
    }

    /**
     * Called when {@link com.telerik.widget.list.RadListView}'s onSaveInstanceState method is called.
     */
    protected void onSaveInstanceState(Parcelable state) {
    }

    /**
     * Called when {@link com.telerik.widget.list.RadListView}'s onRestoreInstanceState method is called.
     */
    protected void onRestoreInstanceState(Parcelable state) {
    }

    void onAdapterChanged(ListViewWrapperAdapter adapter) {
    }
}
