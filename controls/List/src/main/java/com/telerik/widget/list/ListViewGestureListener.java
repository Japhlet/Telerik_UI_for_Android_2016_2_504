package com.telerik.widget.list;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Represents the default gesture listener which is responsible by the gesture handling in
 * {@link com.telerik.widget.list.RadListView}.
 */
public class ListViewGestureListener extends GestureDetector.SimpleOnGestureListener {

    protected RadListView owner;

    private static final int INVALID_ID = -1;

    private int activePointerId = INVALID_ID;
    private GestureDetector detector;
    private float gestureStartX;
    private float gestureStartY;
    private int touchSlopSquare;

    private boolean hasMoved = false;
    private boolean isLongPress = false;

    /**
     * Creates a new instance of ListViewGestureListener with the provided context.
     *
     * @param context context to be used.
     */
    public ListViewGestureListener(Context context) {
        this.detector = new GestureDetector(context, this);
        this.initTouchSlop(context);
    }

    /**
     * Called when a touch event occurs on the parent list view instance.
     *
     * @param sender    the parent list view instance.
     * @param e         the motion event that occurred.
     */
    public boolean onTouchEvent(RadListView sender, MotionEvent e) {

        this.owner = sender;

        int action = e.getAction() & MotionEvent.ACTION_MASK;
        float currentX;
        float currentY;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                int pointerIndex = e.getActionIndex();
                gestureStartX = e.getX(pointerIndex);
                gestureStartY = e.getY(pointerIndex);
                activePointerId = e.getPointerId(0);
                owner.notifyOnDown(e);
                hasMoved = false;
                isLongPress = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if(activePointerId == INVALID_ID) {
                    return false;
                }
                int pointerIndex = e.findPointerIndex(activePointerId);
                currentX = e.getX(pointerIndex);
                currentY = e.getY(pointerIndex);

                float distanceX = Math.abs(currentX - gestureStartX);
                float distanceY = Math.abs(currentY - gestureStartY);

                final int deltaX = (int) (distanceX);
                final int deltaY = (int) (distanceY);
                int distance = (deltaX * deltaX) + (deltaY * deltaY);
                if (distance < touchSlopSquare) {
                    break;
                }
                hasMoved = true;
                if(isLongPress) {
                    this.onLongPressDrag(gestureStartX, gestureStartY, currentX, currentY);
                    return true;
                } else {
                    boolean isDragHandled = this.onShortPressDrag(gestureStartX, gestureStartY, currentX, currentY);
                    if(isDragHandled) {
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if(activePointerId == INVALID_ID) {
                    return false;
                }
                activePointerId = INVALID_ID;
                boolean result;
                if(isLongPress) {
                    result = this.onLongPressDragEnded(false);
                    isLongPress = false;
                } else {
                    result = this.onActionUpOrCancel(false);
                }
                if(result) {
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if(activePointerId == INVALID_ID) {
                    return false;
                }
                activePointerId = INVALID_ID;
                boolean result;
                if(isLongPress) {
                    result = this.onLongPressDragEnded(true);
                    isLongPress = false;
                } else {
                    result = this.onActionUpOrCancel(true);
                }
                if(result) {
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = e.getActionIndex();
                final int pointerId = e.getPointerId(pointerIndex);

                if (pointerId == activePointerId) {

                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    currentX = e.getX(newPointerIndex);
                    currentY = e.getY(newPointerIndex);
                    gestureStartX += currentX - e.getX(pointerIndex);
                    gestureStartY += currentY - e.getY(pointerIndex);
                    activePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return this.detector.onTouchEvent(e);
    }

    public boolean onInterceptTouchEvent(RadListView owner, MotionEvent motionEvent) {
        for(ListViewBehavior behavior : owner.behaviors()) {
            if(behavior.onInterceptTouchEvent(motionEvent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A convenience method called when {@link #onSingleTapUp(android.view.MotionEvent)} is detected.
     *
     * @param motionEvent    the motion event that occurred.
     */
    public void onTapUp(MotionEvent motionEvent){
        owner.notifyOnTapUp(motionEvent);
        for(ListViewBehavior behavior : owner.behaviors()) {
            behavior.onTapUp(motionEvent);
        }
    }

    /**
     * Called when the drag gesture is detected without a long press before that.
     *
     * @param startX the x coordinate where the gesture started
     * @param startY the y coordinate where the gesture started
     * @param currentX the x coordinate where the gesture occurring now
     * @param currentY the y coordinate where the gesture occurring now
     */
    public boolean onShortPressDrag(float startX, float startY, float currentX, float currentY) {
        boolean result = false;
        owner.notifyMove();
        for(ListViewBehavior behavior : owner.behaviors()) {
            boolean behaviorResult = behavior.onShortPressDrag(startX, startY, currentX, currentY);
            result = result || behaviorResult;
        }
        return result;
    }

    /**
     * Called when a previous gesture ends on up or cancel.
     *
     * @param isCanceled whether the gesture was cancelled
     */
    public boolean onActionUpOrCancel(boolean isCanceled) {
        owner.notifyOnUpOrCancel(isCanceled);
        boolean result = false;
        for(ListViewBehavior behavior : owner.behaviors()) {
            boolean behaviorResult = behavior.onActionUpOrCancel(isCanceled);
            result = result || behaviorResult;
        }
        return result;
    }

    /**
     * Called when the drag gesture is detected after a long press.
     *
     * @param startX the x coordinate where the gesture started
     * @param startY the y coordinate where the gesture started
     * @param currentX the x coordinate where the gesture occurring now
     * @param currentY the y coordinate where the gesture occurring now
     */
    public void onLongPressDrag(float startX, float startY, float currentX, float currentY) {
        owner.notifyMove();
        for(ListViewBehavior behavior : owner.behaviors()) {
            behavior.onLongPressDrag(startX, startY, currentX, currentY);
        }
    }

    /**
     * Called when a current gesture combination of long press and drag ends on up or cancel.
     *
     * @param isCanceled whether the gesture was cancelled
     */
    public boolean onLongPressDragEnded(boolean isCanceled) {
        owner.notifyOnUpOrCancel(isCanceled);

        boolean result = false;
        for(ListViewBehavior behavior : owner.behaviors()) {
            boolean behaviorResult = behavior.onLongPressDragEnded(isCanceled);
            result = result || behaviorResult;
        }
        return result;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        this.onTapUp(motionEvent);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

        if(hasMoved) {
            return;
        }
        isLongPress = true;

        owner.notifyOnLongPress(motionEvent);
        for(ListViewBehavior behavior : owner.behaviors()) {
            behavior.onLongPress(motionEvent);
        }
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        boolean result = false;
        for(ListViewBehavior behavior : owner.behaviors()) {
            boolean behaviorResult = behavior.onFling(motionEvent, motionEvent2, v, v2);
            result = result || behaviorResult;
        }
        return result;
    }

    private void initTouchSlop(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        int touchSlop = configuration.getScaledTouchSlop();
        touchSlopSquare = touchSlop * touchSlop;
    }
}
