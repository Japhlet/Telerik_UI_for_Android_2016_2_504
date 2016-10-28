package com.telerik.widget.list;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a {@link com.telerik.widget.list.ListViewBehavior} that can be used to allow the end user
 * to refresh a list of items by swiping from the top of the list.
 */
public class SwipeExecuteBehavior extends ListViewBehavior {

    public static final int DEFAULT_SWIPE_OFFSET = -1;
    public static final int DEFAULT_SWIPE_LIMIT = -1;

    private static final int ANIMATION_DURATION = 300;

    private List<SwipeExecuteListener> listeners = new ArrayList<SwipeExecuteListener>();
    private boolean autoDissolve = true;
    private boolean isAttached = false;

    private Rect swipeImageBoundsOriginal;
    private Rect swipeImageBoundsCurrent;
    private Drawable swipeImage;
    private SwipeState state = SwipeState.IDLE;
    private int swipeViewIndexInLayout = -1;
    private EventInfo currentEventInfo;
    private View pressedView;
    private boolean isLayoutVertical;
    private int touchSlopSquare;
    private int swipedItemPosition;
    private int swipeOffset = DEFAULT_SWIPE_OFFSET;
    private int swipeLimitStart = DEFAULT_SWIPE_LIMIT;
    private int swipeLimitEnd = DEFAULT_SWIPE_LIMIT;
    private SwipeExecuteDismissedListener swipeExecuteDismissedListener;
    private TreeMap<Integer, Drawable> overlayDrawables;
    private long cachedChangeDuration = 0;
    private SwipeExecuteDataObserver dataObserver = new SwipeExecuteDataObserver();
    private boolean isRecentlyRemoved = false;
    ListViewAdapter registeredAdapter;

    /**
     * Adds a listener to be called during the cycle of a swipe gesture.
     *
     * @param listener the new listener.
     */
    public void addListener(SwipeExecuteListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener that is called during the cycle of a swipe gesture.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(SwipeExecuteListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Gets the current value for the final offset that is set to a swiped item when the swipe gesture ends.
     * For example if you need to display a 300 pixel wide button on the left, that is part of
     * your swipe content, you can set the offset to <code>300</code>. If your button is on the right,
     * then the offset should be set to <code>-300</code>.
     *
     * Note that when this is set to the default value which is <code>DEFAULT_SWIPE_OFFSET</code>,
     * the behavior will try to manually calculate the final offset.
     *
     * @return  the current value for swipe offset for a swiped item.
     */
    public int getSwipeOffset() {
        return swipeOffset;
    }

    /**
     * Sets a value for the final offset that is set to a swiped item when the swipe gesture ends.
     * For example if you need to display a 300 pixel wide button on the left, that is part of
     * your swipe content, you can set the offset to <code>300</code>. If your button is on the right,
     * then the offset should be set to <code>-300</code>.
     *
     * Note that when this is set to the default value which is <code>DEFAULT_SWIPE_OFFSET</code>,
     * the behavior will try to manually calculate the final offset.
     *
     * @param swipeOffset the final swipe offset for a swiped item.
     */
    public void setSwipeOffset(int swipeOffset) {
        this.swipeOffset = swipeOffset;
    }

    public int getSwipeLimitStart() {
        return swipeLimitStart;
    }

    public void setSwipeLimitStart(int swipeLimitStart) {
        this.swipeLimitStart = swipeLimitStart;
    }

    public int getSwipeLimitEnd() {
        return swipeLimitEnd;
    }

    public void setSwipeLimitEnd(int swipeLimitEnd) {
        this.swipeLimitEnd = swipeLimitEnd;
    }

    /**
     * Gets a value that determines if the swiped item will automatically fade as it reaches the end.
     * This can be useful to hint the end user that the item is about to be removed.
     * The default value is <code>true</code>.
     *
     * @return whether the swiped item will auto fade.
     */
    public boolean isAutoDissolve() {
        return autoDissolve;
    }

    /**
     * Sets a value that determines if the swiped item will automatically fade as it reaches the end.
     * This can be useful to hint the end user that the item is about to be removed.
     * The default value is <code>true</code>.
     *
     * @param autoDissolve whether the swiped item will auto fade.
     */
    public void setAutoDissolve(boolean autoDissolve) {
        this.autoDissolve = autoDissolve;
    }

    private void handleEndExecuteInCustomLayout(int finalOffsetHorizontal, int finalOffsetVertical) {
        if(isRecentlyRemoved) {
            ListViewAdapter originalAdapter = (ListViewAdapter)owner().getAdapter();
            RecyclerView.ViewHolder testHolder = originalAdapter.onCreateSwipeContentHolder(owner());
            if(testHolder != null && testHolder.itemView != null && testHolder.itemView.getVisibility() == View.INVISIBLE) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pressedView.setVisibility(View.VISIBLE);
                    }
                },ANIMATION_DURATION);
            } else {
                pressedView.setVisibility(View.VISIBLE);
            }
        } else {
            animateBack(finalOffsetHorizontal, finalOffsetVertical);
            pressedView.setVisibility(View.VISIBLE);
        }
        isRecentlyRemoved = false;
    }

    private void handleEndExecute(final int finalOffsetHorizontal, final int finalOffsetVertical) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                animateBack(finalOffsetHorizontal, finalOffsetVertical);
            }
        });
        pressedView.setVisibility(View.VISIBLE);
    }

    /**
     * A method that is used to notify the behavior that the execution is complete.
     * This will return the swiped item to its initial position.
     */
    public void endExecute() {
        if(state == SwipeState.STARTED) {
            state = SwipeState.ENDED;
            if(currentEventInfo != null) {
                currentEventInfo.shouldBeHandled = false;
            }
        }
        if(state != SwipeState.ENDED || owner().getAdapter() == null) {
            return;
        }

        TranslateAnimation translateAnimation = new TranslateAnimation(
                swipeImageBoundsCurrent.left - pressedView.getLeft(),
                0,
                swipeImageBoundsCurrent.top - pressedView.getTop(),
                0);
        translateAnimation.setDuration(ANIMATION_DURATION);

        ListViewWrapperAdapter wrapperAdapter = owner().wrapperAdapter();
        ListViewAdapter originalAdapter = (ListViewAdapter)owner().getAdapter();

        final View swipeContentView = owner().getLayoutManager().getChildAt(swipeViewIndexInLayout);
        if(swipeContentView != null) {
            swipeContentView.setVisibility(View.INVISIBLE);
        }

        wrapperAdapter.handleSwipeEnd();
        originalAdapter.removeSwipeExecuteDismissedListener(swipeExecuteDismissedListener);

        int finalOffsetHorizontal = swipeImageBoundsCurrent.left - pressedView.getLeft();
        int finalOffsetVertical = swipeImageBoundsCurrent.top - pressedView.getTop();

        state = SwipeState.IDLE;
        swipeImage = null;
        swipeImageBoundsOriginal = null;
        swipeImageBoundsCurrent = null;

        for (SwipeExecuteListener listener : listeners) {
            listener.onExecuteFinished(this.swipedItemPosition);
        }
        if(!isRecentlyRemoved) {
            originalAdapter.notifyItemChanged(this.swipedItemPosition);
        }
        if(owner().getLayoutManager() instanceof SlideLayoutManagerBase) {
            handleEndExecuteInCustomLayout(finalOffsetHorizontal, finalOffsetVertical);
        } else {
            handleEndExecute(finalOffsetHorizontal, finalOffsetVertical);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(swipeContentView != null) {
                    swipeContentView.setVisibility(View.VISIBLE);
                }
                if(isAttached && owner().getItemAnimator() != null) {
                    owner().getItemAnimator().setChangeDuration(cachedChangeDuration);
                }
            }
        }, ANIMATION_DURATION);
    }

    private void animateBack(int horizontalPosition, int verticalPosition) {
        if(!isRecentlyRemoved) {
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    horizontalPosition, 0,
                    verticalPosition, 0);
            translateAnimation.setDuration(ANIMATION_DURATION);
            pressedView.startAnimation(translateAnimation);
        }
        isRecentlyRemoved = false;
    }

    public void addSwipeDrawable(int offset, Drawable drawable) {
        if(overlayDrawables == null) {
            overlayDrawables = new TreeMap<Integer, Drawable>();
        }
        overlayDrawables.put(offset, drawable);
    }

    public boolean removeSwipeDrawable(int offset) {
        if (overlayDrawables == null) {
            return false;
        }
        if (overlayDrawables.containsKey(offset)) {
            overlayDrawables.remove(offset);
            return true;
        }
        return false;
    }

    public void clearSwipeDrawables() {
        if (overlayDrawables != null) {
            overlayDrawables.clear();
        }
    }

    @Override
    public boolean isInProgress() {
        return state != SwipeState.IDLE;
    }

    @Override
    public void onAttached(RadListView listView) {
        super.onAttached(listView);
        isAttached = true;
        init();
        if(listView.getAdapter() != null) {
            registeredAdapter = (ListViewAdapter)listView.getAdapter();
            registeredAdapter.registerAdapterDataObserver(dataObserver);
        }
    }

    @Override
    public void onDetached(RadListView listView) {
        super.onDetached(listView);
        isAttached = false;
        if(registeredAdapter != null) {
            registeredAdapter.unregisterAdapterDataObserver(dataObserver);
            registeredAdapter = null;
        }
    }

    @Override
    void onAdapterChanged(ListViewWrapperAdapter adapter) {
        if(registeredAdapter != null) {
            registeredAdapter.unregisterAdapterDataObserver(dataObserver);
            registeredAdapter = null;
        }
        super.onAdapterChanged(adapter);

        if(registeredAdapter == null && owner().getAdapter() != null) {
            registeredAdapter = (ListViewAdapter)owner().getAdapter();
            registeredAdapter.registerAdapterDataObserver(dataObserver);
        }
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        if(state == SwipeState.ENDED) {
            endExecute();
        }
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return state != SwipeState.IDLE;
    }

    @Override
    public boolean onActionUpOrCancel(boolean isCanceled) {
        currentEventInfo = null;
        if(state == SwipeState.STARTED) {
            endSwipe();
            return true;
        }
        return false;
    }

    @Override
    public boolean onShortPressDrag(float startX, float startY, float currentX, float currentY) {

        float distanceX = Math.abs(currentX - startX);
        float distanceY = Math.abs(currentY - startY);

        final int deltaX = (int) (distanceX);
        final int deltaY = (int) (distanceY);
        int distance = (deltaX * deltaX) + (deltaY * deltaY);

        if(distance < touchSlopSquare) {
            return state == SwipeState.ENDED;
        }

        if(currentEventInfo == null || currentEventInfo.eventX != startX || currentEventInfo.eventY != startY) {
            initEventInfo(startX, startY, currentX, currentY);
        }

        if(state == SwipeState.ENDED) {
            currentEventInfo.shouldBeHandled = false;
            endExecute();
            return true;
        }

        if(!currentEventInfo.shouldBeHandled) {
            return false;
        }

        if(state == SwipeState.IDLE) {
            startSwipe(startX, startY);
        }
        if(state == SwipeState.STARTED) {
            moveSwipe(startX, startY, currentX, currentY);
        } else {
            endExecute();
        }

        return true;
    }

    @Override
    public void onTapUp(MotionEvent motionEvent) {
        if(state == SwipeState.ENDED) {
            endExecute();
        }
    }

    @Override
    public void onDispatchDraw(Canvas canvas) {
        if(state != SwipeState.IDLE) {
            swipeImage.draw(canvas);
        }
    }

    /**
     * A method that initiates swipe for the item at the provided coordinates.
     */
    protected void startSwipe(float startX, float startY) {
        if(state != SwipeState.IDLE) {
            return;
        }

        View view = findViewByCoordinates(startX, startY);
        if(view == null) {
            return;
        }
        if(owner().getLayoutManager() instanceof SlideLayoutManagerBase &&
                ((SlideLayoutManagerBase)owner().getLayoutManager()).getChildAtFront() != view) {
            return;
        }

        int position = owner().getChildAdapterPosition(view);
        if(owner().getAdapter() == null || !((ListViewAdapter)owner().getAdapter()).canSwipe(position)) {
            return;
        }
        view.setVisibility(View.INVISIBLE);

        if(owner().getItemAnimator() != null) {
            cachedChangeDuration = owner().getItemAnimator().getChangeDuration();
            owner().getItemAnimator().setChangeDuration(0);
        }

        this.pressedView = view;

        int size = owner().getLayoutManager().getChildCount();
        for (int i = 0; i < size; i++) {
            View childView = owner().getLayoutManager().getChildAt(i);
            if(childView == pressedView) {
                swipeViewIndexInLayout = i;
                break;
            }
        }

        state = SwipeState.STARTED;

        this.swipeImageBoundsOriginal = new Rect(pressedView.getLeft(), pressedView.getTop(), pressedView.getRight(), pressedView.getBottom());
        this.swipeImageBoundsCurrent = new Rect(this.swipeImageBoundsOriginal);

        pressedView.setPressed(false);
        this.swipeImage = this.createSwipeImage(pressedView);
        this.swipeImage.setBounds(swipeImageBoundsCurrent);

        ListViewWrapperAdapter wrapperAdapter = owner().wrapperAdapter();
        ListViewAdapter originalAdapter = (ListViewAdapter)owner().getAdapter();
        originalAdapter.recentAdapterChange = false;

        originalAdapter.addSwipeExecuteDismissedListener(swipeExecuteDismissedListener);

        int swipePosition = owner().getChildAdapterPosition(pressedView);

        for(SwipeExecuteListener listener : listeners) {
            listener.onSwipeStarted(swipePosition);
        }

        this.swipedItemPosition = swipePosition;
        if(isLayoutVertical) {
            wrapperAdapter.handleSwipeStart(this.swipedItemPosition, 0, this.swipeImageBoundsOriginal.height());
        } else {
            wrapperAdapter.handleSwipeStart(this.swipedItemPosition, this.swipeImageBoundsOriginal.width(), 0);
        }

        originalAdapter.notifyItemChanged(this.swipedItemPosition);
    }

    public int ensureWithinSwipeLimits(int currentSwipe) {
        int swipeWithinLimits = currentSwipe;
        if(swipeLimitEnd != DEFAULT_SWIPE_LIMIT) {
            swipeWithinLimits = Math.min(swipeWithinLimits, swipeLimitEnd);
        }
        if(swipeLimitStart != DEFAULT_SWIPE_LIMIT) {
            swipeWithinLimits = Math.max(swipeWithinLimits, swipeLimitStart);
        }
        return swipeWithinLimits;
    }

    /**
     * A method that moves the swiped item for the provided start coordinates to the provided current coordinates.
     */
    protected void moveSwipe(float startX, float startY, float currentX, float currentY) {
        int swipeImageX = this.swipeImageBoundsOriginal.left;
        int swipeImageY = this.swipeImageBoundsOriginal.top;

        int distanceX = (int)currentX - (int)startX;
        distanceX = ensureWithinSwipeLimits(distanceX);

        int distanceY = (int)currentY - (int)startY;
        distanceY = ensureWithinSwipeLimits(distanceY);

        if(isLayoutVertical) {
            swipeImageX += distanceX;
        } else {
            swipeImageY += distanceY;
        }
        int distance = isLayoutVertical ? distanceX : distanceY;
        handleLayersVisibility(distance);

        this.swipeImageBoundsCurrent.offsetTo(swipeImageX, swipeImageY);
        this.swipeImage.setBounds(swipeImageBoundsCurrent);

        if(isAutoDissolve()) {
            float relativeOffset;
            if(isLayoutVertical) {
                relativeOffset = (float) Math.abs(distanceX) / swipeImageBoundsOriginal.width();
            } else {
                relativeOffset = (float) Math.abs(distanceY) / swipeImageBoundsOriginal.height();
            }
            if(relativeOffset > 1) {
                relativeOffset = 1;
            }
            if(relativeOffset < 0) {
                relativeOffset = 0;
            }
            int alphaValue = ((int) ((1 - relativeOffset) * 255));
            swipeImage.setAlpha(alphaValue);
        }

        this.owner().invalidate();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (state == SwipeState.STARTED) {
                    View swipeView = owner().getLayoutManager().getChildAt(swipeViewIndexInLayout);
                    int currentOffset = getCurrentBoundsStart() - getOriginalBoundsStart();
                    for (SwipeExecuteListener listener : listeners) {
                        listener.onSwipeProgressChanged(swipedItemPosition, currentOffset, swipeView);
                    }
                }
            }
        });
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent){
        if(swipeImageBoundsCurrent == null) {
            return false;
        }
        if(swipeImageBoundsCurrent.left < motionEvent.getX() && motionEvent.getX() < swipeImageBoundsCurrent.right &&
                swipeImageBoundsCurrent.top < motionEvent.getY() && motionEvent.getY() < swipeImageBoundsCurrent.bottom) {
            return true;
        }
        return false;
    }

    /**
     * A method that notifies that the swipe gesture is finished.
     */
    protected void endSwipe() {

        state = SwipeState.ENDED;

        int currentOffset = getCurrentBoundsStart() - getOriginalBoundsStart();

        for (SwipeExecuteListener listener : listeners) {
            listener.onSwipeEnded(this.swipedItemPosition, currentOffset);
        }

        if (state != SwipeState.ENDED) {
            return;
        }

        int finalOffset = this.swipeOffset;

        if (finalOffset == DEFAULT_SWIPE_OFFSET) {
            int autoOffset = calculateDefaultOffset();
            float distanceToTarget = Math.abs(autoOffset - getCurrentBoundsStart());
            float distanceToStart = Math.abs(getCurrentBoundsStart() - getOriginalBoundsStart());
            if (distanceToStart < distanceToTarget) {
                endExecute();
                return;
            }
            finalOffset = autoOffset;
        }
        if(isLayoutVertical) {
            swipeImageBoundsCurrent.offsetTo(swipeImageBoundsOriginal.left + finalOffset, swipeImageBoundsOriginal.top);
        } else {
            swipeImageBoundsCurrent.offsetTo(swipeImageBoundsOriginal.left, swipeImageBoundsOriginal.top + finalOffset);
        }

        if (isAutoDissolve()) {
            float distance = Math.abs(getCurrentBoundsStart() - getOriginalBoundsStart());
            float relativeOffset = isLayoutVertical ?
                    distance / swipeImageBoundsOriginal.width() :
                    distance / swipeImageBoundsOriginal.height();
            int alphaValue = ((int) ((1 - relativeOffset) * 255));
            swipeImage.setAlpha(alphaValue);
        }

        if(getCurrentBoundsStart() == getOriginalBoundsStart()) {
            endExecute();
            return;
        }
        swipeImage.setBounds(swipeImageBoundsCurrent);
        owner().invalidate();
    }

    /**
     * A method that is responsible for creating an image that will be used for swiping for the view that is provided.
     */
    protected Drawable createSwipeImage(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        BitmapDrawable swipeImage = new BitmapDrawable(this.owner().getResources(), bitmap);

        if(overlayDrawables == null || overlayDrawables.size() == 0) {
            return swipeImage;
        }

        int count = 1 + overlayDrawables.size();
        Drawable[] layers = new Drawable[count];
        layers[0] = swipeImage;
        int index = 1;
        for(Map.Entry<Integer,Drawable> entry : overlayDrawables.entrySet()) {
            Drawable value = entry.getValue();
            layers[index] = value;
            index++;
        }
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        return layerDrawable;
    }

    private void handleLayersVisibility(int distance) {
        if(!(this.swipeImage instanceof LayerDrawable)) {
            return;
        }
        LayerDrawable swipeImageDrawable = (LayerDrawable)this.swipeImage;
        Map.Entry<Integer, Drawable> entry;
        if(distance < 0) {
            entry = overlayDrawables.ceilingEntry(distance);
        } else {
            entry = overlayDrawables.floorEntry(distance);
        }

        int numberOfLayers = swipeImageDrawable.getNumberOfLayers();
        for(int i = 1; i < numberOfLayers; i++) {
            if(swipeImageDrawable.getDrawable(i) == entry.getValue() &&
                    ((entry.getKey() < 0) == (distance < 0))  ) {
                swipeImageDrawable.getDrawable(i).setAlpha(255);
            } else {
                swipeImageDrawable.getDrawable(i).setAlpha(0);
            }
        }
    }

    private int calculateDefaultOffset() {
        int autoCalculatedOffset = DEFAULT_SWIPE_OFFSET;
        if(swipeImageBoundsCurrent == null || swipeImageBoundsOriginal == null) {
            return DEFAULT_SWIPE_OFFSET;
        }
        View view = owner().getLayoutManager().getChildAt(swipeViewIndexInLayout);
        if(view instanceof RelativeLayout) {
            if(((RelativeLayout) view).getChildCount() == 2) {
                View v1 = ((RelativeLayout) view).getChildAt(0);
                if(v1 instanceof LinearLayout) {
                    if (getCurrentBoundsStart() > getOriginalBoundsStart()) {
                        autoCalculatedOffset = getViewSizeInCurrentOrientation(v1);
                    }
                }
                View v2 = ((RelativeLayout) view).getChildAt(1);
                if(v2 instanceof LinearLayout) {
                    if (getCurrentBoundsStart() < getOriginalBoundsStart()) {
                        autoCalculatedOffset = -getViewSizeInCurrentOrientation(v2);
                    }
                }
            }
        }
        if(autoCalculatedOffset == DEFAULT_SWIPE_OFFSET) {
            return owner().getWidth();
        }
        return autoCalculatedOffset;
    }

    private void initEventInfo(float startX, float startY, float currentX, float currentY) {
        currentEventInfo = new EventInfo();
        currentEventInfo.eventX = startX;
        currentEventInfo.eventY = startY;

        float horizontalDistance = Math.abs(currentX - startX);
        float verticalDistance = Math.abs(currentY - startY);

        initLayoutDirection();
        boolean isDirectionCorrect = isLayoutVertical ? horizontalDistance > verticalDistance : horizontalDistance < verticalDistance;
        currentEventInfo.shouldBeHandled = isDirectionCorrect;
    }

    private void initLayoutDirection() {
        isLayoutVertical = owner().getLayoutManager().canScrollVertically();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(owner().getContext());
        int touchSlop = configuration.getScaledTouchSlop();
        touchSlopSquare = touchSlop * touchSlop;

        swipeExecuteDismissedListener = new SwipeExecuteDismissedListener() {
            @Override
            public void onSwipeContentDismissed() {
                endExecute();
            }
        };
    }

    private int getCurrentBoundsStart() {
        if(isLayoutVertical) {
            return swipeImageBoundsCurrent.left;
        } else {
            return swipeImageBoundsCurrent.top;
        }
    }

    private int getOriginalBoundsStart() {
        if(isLayoutVertical) {
            return swipeImageBoundsOriginal.left;
        } else {
            return swipeImageBoundsOriginal.top;
        }
    }

    private int getViewSizeInCurrentOrientation(View view) {
        if(isLayoutVertical) {
            return view.getWidth();
        } else {
            return view.getHeight();
        }
    }

    private View findViewByCoordinates(float x, float y) {
        return owner().findChildViewUnder(x, y);
    }

    private class EventInfo {
        float eventX;
        float eventY;
        boolean shouldBeHandled;
    }

    private enum SwipeState {
        IDLE,
        STARTED,
        ENDED
    }

    interface SwipeExecuteDismissedListener {
        void onSwipeContentDismissed();
    }

    /**
     * Interface definition for a callback to be invoked when swipe behavior is working.
     */
    public interface SwipeExecuteListener {

        /**
         * Called when the swipe gesture starts for the item at the specified position.
         *
         * @param position the position of the item that is swiped.
         */
        void onSwipeStarted(int position);

        /**
         * Called when the progress of the swipe for the item at the specified position changes.
         *
         * @param position      the position of the item that is swiped.
         * @param currentOffset the current offset from the start that the swiped item has made.
         * @param swipeContent  the swipe content that is shown behind the item.
         */
        void onSwipeProgressChanged(int position, int currentOffset, View swipeContent);

        /**
         * Called when swipe gesture ends.
         *
         * @param position      the position of the item that is swiped.
         * @param finalOffset   the final offset from the start that the swiped item has made.
         */
        void onSwipeEnded(int position, int finalOffset);

        /**
         * Called when the swipe content is dismissed.
         *
         * @param position      the position of the item that is swiped.
         */
        void onExecuteFinished(int position);
    }

    class SwipeExecuteDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if(positionStart + itemCount > swipedItemPosition && swipedItemPosition >= positionStart) {
                isRecentlyRemoved = true;
                endExecute();
            }
        }

        @Override
        public void onChanged() {
            endExecute();
        }
    }
}
