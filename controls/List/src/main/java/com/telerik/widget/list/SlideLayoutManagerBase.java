package com.telerik.widget.list;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class for layout managers that have a concept of a current item.
 * And users can easily swipe to change the current item to an adjacent item.
 */
public abstract class SlideLayoutManagerBase extends RecyclerView.LayoutManager {

    static final int HORIZONTAL = 0;
    static final int VERTICAL = 1;

    static final int DIRECTION_NONE = 0;
    static final int DIRECTION_FORWARD = 1;
    static final int DIRECTION_BACKWARD = 2;

    static final int ADJACENT_ITEM_COUNT = 1;

    static final long DEFAULT_DURATION = 200l;

    private boolean scrollEnabled = true;

    private List<CurrentPositionChangeListener> listeners = new ArrayList<CurrentPositionChangeListener>();

    private boolean isScrolling = false;

    private int adapterChangeFirstDeletedPosition = -1;
    private int adapterChangeDeletedPositionsCount = -1;

    private int pendingScrollPosition = -1;

    private int orientation;

    private int lastScrollValue;
    private int totalScrollValue;
    private boolean isScrollNew;
    private int currentScrollDirection;

    private RecyclerView.Recycler recycler;
    private RecyclerView.State state;

    protected int frontViewWidth;
    protected int frontViewHeight;

    protected int frontViewPosition = 0;

    /**
     * Adds a listener to be called when the current position changes.
     *
     * @param listener the new listener.
     */
    public void addListener(CurrentPositionChangeListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener that is called when the current position changes.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(CurrentPositionChangeListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Scroll to the next position of the list and makes the next item current.
     */
    public void scrollToNext() {
        if(!canScroll(DIRECTION_FORWARD)) {
            return;
        }

        int oldPosition = frontViewPosition;
        frontViewPosition++;
        notifyListeners(oldPosition, frontViewPosition);
        fill(DIRECTION_FORWARD, recycler, state);
    }

    /**
     * Scroll to the previous position of the list and makes the next item current.
     */
    public void scrollToPrevious() {
        if(!canScroll(DIRECTION_BACKWARD)) {
            return;
        }

        int oldPosition = frontViewPosition;
        frontViewPosition--;
        notifyListeners(oldPosition, frontViewPosition);
        fill(DIRECTION_BACKWARD, recycler, state);
    }

    /**
     * Gets a value indicating whether the users can use a gesture to move to adjacent items.
     *
     * @return  whether users can swipe to move to adjacent items.
     */
    public boolean isScrollEnabled() {
        return scrollEnabled;
    }

    /**
     * Sets a value indicating whether the users can use a gesture to move to adjacent items.
     *
     * @param scrollEnabled the new value that will determine if swipes will trigger current item changes.
     */
    public void setScrollEnabled(boolean scrollEnabled) {
        this.scrollEnabled = scrollEnabled;
    }

    /**
     * Gets the position of the current item.
     *
     * @return  the adapter position of the current item.
     */
    public int getCurrentPosition() {
        return frontViewPosition;
    }

    /**
     * Sets the position of an item that should become current.
     *
     * @param position the adapter position of an item that should become selected.
     */
    public void setCurrentPosition(int position) {
        if(!isPositionValid(position)) {
            throw new IllegalArgumentException("position can't be less than 0 or more than the number of items");
        }
        scrollToPosition(position);
    }

    @Override
    public boolean canScrollHorizontally() {
        return orientation == HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return orientation == VERTICAL;
    }

    @Override
    public void scrollToPosition(int position) {
        if(!isPositionValid(position)) {
            return;
        }
        if(getChildCount() == 0) {
            notifyListeners(0, position);
            frontViewPosition = position;
        } else {
            pendingScrollPosition = position;
            requestLayout();
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if(this.isScrolling) {
                    this.onScrollEnded();
                }
                this.isScrolling = false;
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                this.isScrolling = true;
                this.isScrollNew = true;
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                if(this.isScrolling) {
                    this.onScrollEnded();
                }
                this.isScrolling = false;
                break;
        }
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        int lastAdapterPosition = adapterPositionForLayoutIndex(nextItemsCount());
        int firstAdapterPosition = adapterPositionForLayoutIndex(-previousItemsCount());
        if(positionStart > lastAdapterPosition) {
            return;
        }
        if(positionStart + itemCount - 1 < firstAdapterPosition) {
            int oldPosition = frontViewPosition;
            frontViewPosition += itemCount;
            notifyListeners(oldPosition, frontViewPosition);
            return;
        }
        if(positionStart + itemCount - 1 <= frontViewPosition && frontViewPosition != 0) {
            int oldPosition = frontViewPosition;
            frontViewPosition += itemCount;
            notifyListeners(oldPosition, frontViewPosition);
        }
        detachAndScrapAttachedViews(recycler);
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        adapterChangeFirstDeletedPosition = positionStart;
        adapterChangeDeletedPositionsCount = itemCount;
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        removeAllViews();
    }

    @Override
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        removeAllViews();
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        int childIndex = 0;
        for(int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(!isPositionValid(adapterPosition)) {
                continue;
            }
            if(adapterPosition >= positionStart && adapterPosition < positionStart + itemCount) {
                if(((RecyclerView.LayoutParams)getChildAt(childIndex).getLayoutParams()).isItemRemoved()) {
                    return;
                }
                recycler.bindViewToPosition(getChildAt(childIndex), adapterPosition);
            }
            childIndex++;
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(state.isPreLayout()) {
            return;
        }
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        this.recycler = recycler;
        this.state = state;

        if(getChildCount() == 0) {
            calculateFrontViewSize();
            fill(DIRECTION_NONE, recycler, state);
        } else if(pendingScrollPosition != -1) {
            if(pendingScrollPosition != frontViewPosition) {
                int oldPosition = frontViewPosition;
                frontViewPosition = pendingScrollPosition;
                notifyListeners(oldPosition, frontViewPosition);
            }
            pendingScrollPosition = -1;
            detachAndScrapAttachedViews(recycler);
            fill(DIRECTION_NONE, recycler, state);
        } else {
            if(adapterChangeFirstDeletedPosition != -1) {
                int lastAdapterPosition = adapterPositionForLayoutIndex(nextItemsCount());
                int firstAdapterPosition = adapterPositionForLayoutIndex(-previousItemsCount());
                int minVisiblePosition = Math.min(firstAdapterPosition, lastAdapterPosition);
                int maxVisiblePosition = Math.max(firstAdapterPosition, lastAdapterPosition);

                for(int i = 0; i < adapterChangeDeletedPositionsCount; i++) {
                    if(adapterChangeFirstDeletedPosition + i >= minVisiblePosition && adapterChangeFirstDeletedPosition + i <= maxVisiblePosition) {
                        int layoutIndex = layoutIndexForAdapterPosition(adapterChangeFirstDeletedPosition + i);
                        handleItemRemoved(layoutIndex, recycler, state);
                    } else if(adapterChangeFirstDeletedPosition + i < frontViewPosition) {
                        int oldPosition = frontViewPosition;
                        frontViewPosition--;
                        notifyListeners(oldPosition, frontViewPosition);
                    }
                }
                adapterChangeFirstDeletedPosition = -1;
                adapterChangeDeletedPositionsCount = -1;
            } else {
                int childIndex = 0;
                for (int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
                    int adapterPosition = adapterPositionForLayoutIndex(i);
                    if (!isPositionValid(adapterPosition)) {
                        continue;
                    }
                    View childView = getChildAt(childIndex);
                    childIndex++;
                    recycler.bindViewToPosition(childView, adapterPosition);
                }
            }
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if(orientation != HORIZONTAL) {
            return 0;
        }

        return scrollBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(orientation != VERTICAL) {
            return 0;
        }

        return scrollBy(dy, recycler, state);
    }

    protected int getStateItemCount() {
        return state != null ? state.getItemCount() : getItemCount();
    }

    protected Interpolator animationInterpolator() {
        return new DecelerateInterpolator();
    }

    protected void notifyListeners(int oldPosition, int newPosition) {
        for(CurrentPositionChangeListener listener : listeners) {
            listener.onCurrentPositionChanged(oldPosition, newPosition);
        }
    }

    protected void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation:" + orientation);
        }

        this.orientation = orientation;
    }

    protected int getOrientation() {
        return this.orientation;
    }

    protected void calculateFrontViewSize() {
        frontViewWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        frontViewHeight = getHeight() - getPaddingBottom() - getPaddingTop();
    }

    protected int previousItemsCount() {
        return ADJACENT_ITEM_COUNT;
    }

    protected int nextItemsCount() {
        return ADJACENT_ITEM_COUNT;
    }

    protected void handleItemRemoved(int layoutIndex, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if(layoutIndex < 0) {
            int oldPosition = frontViewPosition;
            frontViewPosition--;
            notifyListeners(oldPosition, frontViewPosition);
            fillAtStart(recycler, state, layoutIndex);
        }
        if(layoutIndex == 0) {
            if(frontViewPosition < getStateItemCount()) {
                fillAtEnd(recycler, state, layoutIndex);
            } else {
                int oldPosition = frontViewPosition;
                frontViewPosition--;
                notifyListeners(oldPosition, frontViewPosition);
                fillAtStart(recycler, state, layoutIndex);
            }
        }
        if(layoutIndex > 0) {
            fillAtEnd(recycler, state, layoutIndex);
        }
    }

    protected void fill(int direction, RecyclerView.Recycler recycler, RecyclerView.State state) {
        switch (direction) {
            case DIRECTION_NONE:
                fillAll(recycler, state);
                break;
            case DIRECTION_FORWARD:
                fillAtEnd(recycler, state);
                break;
            case DIRECTION_BACKWARD:
                fillAtStart(recycler, state);
                break;
        }
    }

    protected void fillAll(RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
            int position = adapterPositionForLayoutIndex(i);
            if(!isPositionValid(position)) {
                continue;
            }
            View view = recycler.getViewForPosition(position);
            updateViewLayoutParams(view, frontViewWidth, frontViewHeight);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            layoutView(view);
            applyLayoutTransformations(view, i, false);
        }
    }

    protected void fillAtEnd(RecyclerView.Recycler recycler, RecyclerView.State state, int layoutIndex) {
        int removedChildIndex = 0;
        for(int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(i == layoutIndex) {
                if(adapterPosition < 0 || adapterPosition > getStateItemCount()) {
                    removedChildIndex = -1;
                }
                break;
            } else {
                if(adapterPosition >= 0 && adapterPosition <= getStateItemCount()) {
                    removedChildIndex++;
                }
            }
        }
        if(removedChildIndex >= 0) {
            detachAndScrapViewAt(removedChildIndex, recycler);
        }
        int itemsToMoveCount = Math.min(nextItemsCount() - layoutIndex, getChildCount() - removedChildIndex);
        for(int i = 0; i < itemsToMoveCount; i++) {
            applyLayoutTransformations(getChildAt(removedChildIndex + i), layoutIndex + i, true);
        }
        int position = adapterPositionForLayoutIndex(nextItemsCount());
        if(!isPositionValid(position)) {
            return;
        }
        View view = recycler.getViewForPosition(position);
        updateViewLayoutParams(view, frontViewWidth, frontViewHeight);
        addView(view);
        measureChildWithMargins(view, 0, 0);
        layoutView(view);
        applyLayoutTransformations(view, nextItemsCount(), false);
    }

    protected void fillAtStart(RecyclerView.Recycler recycler, RecyclerView.State state, int layoutIndex) {
        int removedChildIndex = 0;
        for(int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(i == layoutIndex) {
                if(adapterPosition < 0 || adapterPosition > getStateItemCount()) {
                    removedChildIndex = -1;
                }
                break;
            } else {
                if(adapterPosition >= 0 && adapterPosition <= getStateItemCount()) {
                    removedChildIndex++;
                }
            }
        }
        detachAndScrapViewAt(removedChildIndex, recycler);

        for(int i = removedChildIndex - 1; i >= 0; i--) {
            applyLayoutTransformations(getChildAt(i), i + 1 + layoutIndex - removedChildIndex, true);
        }
        int position = adapterPositionForLayoutIndex(-previousItemsCount());
        if(!isPositionValid(position)) {
            return;
        }
        View view = recycler.getViewForPosition(position);
        updateViewLayoutParams(view, frontViewWidth, frontViewHeight);
        addView(view, 0);
        measureChildWithMargins(view, 0, 0);
        layoutView(view);
        applyLayoutTransformations(view, -previousItemsCount(), false);
    }

    protected int adapterPositionForLayoutIndex(int layoutIndex) {
        return frontViewPosition + layoutIndex;
    }

    protected int layoutIndexForAdapterPosition(int adapterPosition) {
        return adapterPosition - frontViewPosition;
    }

    protected void fillAtEnd(RecyclerView.Recycler recycler, RecyclerView.State state) {

        int firstItemAdapterPosition = adapterPositionForLayoutIndex(-previousItemsCount()-1);
        if(isPositionValid(firstItemAdapterPosition)) {
            detachAndScrapViewAt(0, recycler);
        }

        int childIndex = 0;
        for(int i = -previousItemsCount(); i <= nextItemsCount() - 1; i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(!isPositionValid(adapterPosition)) {
                continue;
            }
            View childView = getChildAt(childIndex);
            childIndex++;
            applyLayoutTransformations(childView, i, true);
        }
        int lastPosition = adapterPositionForLayoutIndex(nextItemsCount());

        if(!isPositionValid(lastPosition)) {
            return;
        }
        View view = recycler.getViewForPosition(lastPosition);
        updateViewLayoutParams(view, frontViewWidth, frontViewHeight);
        addView(view);
        measureChildWithMargins(view, 0, 0);
        layoutView(view);
        applyLayoutTransformations(view, nextItemsCount(), false);
    }

    protected void fillAtStart(RecyclerView.Recycler recycler, RecyclerView.State state) {

        int lastItemAdapterPosition = adapterPositionForLayoutIndex(nextItemsCount() + 1);
        if(isPositionValid(lastItemAdapterPosition)) {
            detachAndScrapViewAt(getChildCount() - 1, recycler);
        }

        int childIndex = 0;
        for(int i = -previousItemsCount() + 1; i <= nextItemsCount(); i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(!isPositionValid(adapterPosition)) {
                continue;
            }
            View childView = getChildAt(childIndex);
            childIndex++;
            applyLayoutTransformations(childView, i, true);
        }

        int firstPosition = adapterPositionForLayoutIndex(-previousItemsCount());
        if(!isPositionValid(firstPosition)) {
            return;
        }
        View view = recycler.getViewForPosition(firstPosition);
        updateViewLayoutParams(view, frontViewWidth, frontViewHeight);
        addView(view, 0);
        measureChildWithMargins(view, 0, 0);
        layoutView(view);
        applyLayoutTransformations(view, -previousItemsCount(), false);
    }

    protected void updateViewLayoutParams(View view, int width, int height) {
        if(view.getLayoutParams() != null) {
            view.getLayoutParams().width = width;
            view.getLayoutParams().height = height;
        } else {
            view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        }
    }

    protected long animationDuration() {
        return DEFAULT_DURATION;
    }

    protected int getDirection(int scrollValue) {
        if(scrollValue > 0) {
            return DIRECTION_FORWARD;
        }
        if(scrollValue < 0) {
            return DIRECTION_BACKWARD;
        }
        return DIRECTION_NONE;
    }

    protected boolean canScroll(int direction) {
        int itemCount = getStateItemCount();
        if(direction == DIRECTION_FORWARD && this.frontViewPosition >= itemCount - 1) {
            return false;
        }
        if(direction == DIRECTION_BACKWARD && this.frontViewPosition <= 0) {
            return false;
        }
        return true;
    }

    protected int findStartOffset() {
        if(orientation == HORIZONTAL) {
            return -getDecoratedLeft(getChildAt(0)) + getPaddingLeft();
        }
        return -getDecoratedTop(getChildAt(0)) + getPaddingTop();
    }

    protected int findEndOffset() {
        int frontViewSize = orientation == HORIZONTAL ? frontViewWidth : frontViewHeight;
        int padding = orientation == HORIZONTAL ? getPaddingEnd() : getPaddingBottom();
        int lastIndex = getChildCount() - 1;
        View lastView = getChildAt(lastIndex);
        int edge = orientation == HORIZONTAL ? getDecoratedRight(lastView) : getDecoratedBottom(lastView);
        return frontViewSize - edge + padding;
    }

    protected float calculateScrollProgress() {
        int frontViewSize = orientation == HORIZONTAL ? frontViewWidth : frontViewHeight;
        if(currentScrollDirection == DIRECTION_FORWARD) {
            return totalScrollValue / (float)frontViewSize;
        } else {
            return  -totalScrollValue / (float)frontViewSize;
        }
    }

    protected void layoutView(View view) {
        int width = frontViewWidth;
        int height = frontViewHeight;

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = left + width;
        int bottom = top + height;

        ViewGroup.LayoutParams params = view.getLayoutParams();

        if(params != null && params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
            left += marginParams.leftMargin;
            top += marginParams.topMargin;
            right -= marginParams.rightMargin;
            bottom -= marginParams.bottomMargin;
        }

        layoutDecorated(view, left, top, right, bottom);
    }

    protected void applyLayoutTransformations(View view, int layoutIndex, boolean animate) {
        float alpha = alphaForIndex(layoutIndex);
        float translationX = translationXForIndex(layoutIndex);
        float translationY = translationYForIndex(layoutIndex);
        float translationZ = translationZForIndex(layoutIndex);
        float elevation = elevationForIndex(layoutIndex);
        float scaleX = scaleXForIndex(layoutIndex);
        float scaleY = scaleYForIndex(layoutIndex);
        float rotation = rotationForIndex(layoutIndex);
        float rotationX = rotationXForIndex(layoutIndex);
        float rotationY = rotationYForIndex(layoutIndex);

        ViewCompat.setTranslationZ(view, translationZ);
        ViewCompat.setElevation(view, elevation);

        if(animate) {
            long duration = animationDuration();
            Interpolator interpolator = animationInterpolator();

            ViewCompat.animate(view).setDuration(duration)
                    .setInterpolator(interpolator)
                    .alpha(alpha)
                    .translationX(translationX)
                    .translationY(translationY)
                    .scaleX(scaleX)
                    .scaleY(scaleY)
                    .rotation(rotation)
                    .rotationX(rotationX)
                    .rotationY(rotationY)
                    .start();
        } else {
            ViewCompat.setAlpha(view, alpha);
            ViewCompat.setTranslationX(view, translationX);
            ViewCompat.setTranslationY(view, translationY);
            ViewCompat.setScaleX(view, scaleX);
            ViewCompat.setScaleY(view, scaleY);
            ViewCompat.setRotation(view, rotation);
            ViewCompat.setRotationX(view, rotationX);
            ViewCompat.setRotationY(view, rotationY);
        }
    }

    protected int elevationForIndex(int layoutIndex) {
        return 0;
    }

    protected float alphaForIndex(int layoutIndex) {
        return 1;
    }

    protected float translationXForIndex(int layoutIndex) {
        if(orientation == HORIZONTAL) {
            return layoutIndex * frontViewWidth;
        }
        return 0;
    }

    protected float translationYForIndex(int layoutIndex) {
        if(orientation == VERTICAL) {
            return layoutIndex * frontViewHeight;
        }
        return 0;
    }

    protected float translationZForIndex(int layoutIndex) {
        return 0;
    }

    protected float scaleXForIndex(int layoutIndex) {
        return 1;
    }

    protected float scaleYForIndex(int layoutIndex) {
        return 1;
    }

    protected float rotationForIndex(int layoutIndex) {
        return 0;
    }

    protected float rotationXForIndex(int layoutIndex) {
        return 0;
    }

    protected float rotationYForIndex(int layoutIndex) {
        return 0;
    }

    protected int previousIndex(int index) {
        return index - 1;
    }

    protected int nextIndex(int index) {
        return index + 1;
    }

    protected void scrollViews(int direction, float progress) {
        int childIndex = 0;
        for(int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(!isPositionValid(adapterPosition)) {
                continue;
            }

            View view = getChildAt(childIndex);
            childIndex++;

            ViewCompat.setAlpha(view, alphaForIndex(i, direction, progress));

            ViewCompat.setScaleX(view, scaleXForIndex(i, direction, progress));
            ViewCompat.setScaleY(view, scaleYForIndex(i, direction, progress));

            ViewCompat.setTranslationX(view, translationXForIndex(i, direction, progress));
            ViewCompat.setTranslationY(view, translationYForIndex(i, direction, progress));
            ViewCompat.setTranslationZ(view, translationZForIndex(i, direction, progress));

            ViewCompat.setRotation(view, rotationForIndex(i, direction, progress));
            ViewCompat.setRotationX(view, rotationXForIndex(i, direction, progress));
            ViewCompat.setRotationY(view, rotationYForIndex(i, direction, progress));
        }
    }

    View getChildAtFront() {
        int childIndex = 0;
        for(int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(!isPositionValid(adapterPosition)) {
                continue;
            }
            if(i == 0) {
                return getChildAt(childIndex);
            }
            childIndex++;
        }
        return null;
    }

    private boolean isPositionValid(int position) {
        return position >= 0 && position < getStateItemCount();
    }

    private void onScrollEnded() {
        if(getDirection(lastScrollValue) == currentScrollDirection) {
            if (currentScrollDirection == DIRECTION_FORWARD) {
                scrollToNext();
            } else {
                scrollToPrevious();
            }
        } else {
            scrollReset();
        }
        this.currentScrollDirection = DIRECTION_NONE;
    }

    private void scrollReset() {
        int childIndex = 0;
        for(int i = -previousItemsCount(); i <= nextItemsCount(); i++) {
            int adapterPosition = adapterPositionForLayoutIndex(i);
            if(!isPositionValid(adapterPosition)) {
                continue;
            }

            View view = getChildAt(childIndex);
            childIndex++;

            float alpha = alphaForIndex(i);
            float translationX = translationXForIndex(i);
            float translationY = translationYForIndex(i);
            float scaleX = scaleXForIndex(i);
            float scaleY = scaleYForIndex(i);
            float rotation = rotationForIndex(i);
            float rotationX = rotationXForIndex(i);
            float rotationY = rotationYForIndex(i);

            long duration = animationDuration();
            Interpolator interpolator = animationInterpolator();

            if (ViewCompat.getAlpha(view) != alpha) {
                ViewCompat.animate(view).alpha(alpha).setDuration(duration).setInterpolator(interpolator).start();
            }
            if (ViewCompat.getTranslationX(view) != translationX) {
                ViewCompat.animate(view).translationX(translationX).setDuration(duration).setInterpolator(interpolator).start();
            }
            if (ViewCompat.getTranslationY(view) != translationY) {
                ViewCompat.animate(view).translationY(translationY).setDuration(duration).setInterpolator(interpolator).start();
            }
            if (ViewCompat.getScaleX(view) != scaleX) {
                ViewCompat.animate(view).scaleX(scaleX).setDuration(duration).setInterpolator(interpolator).start();
            }
            if (ViewCompat.getScaleY(view) != scaleY) {
                ViewCompat.animate(view).scaleY(scaleY).setDuration(duration).setInterpolator(interpolator).start();
            }
            if (ViewCompat.getRotation(view) != rotation) {
                ViewCompat.animate(view).rotation(rotation).setDuration(duration).setInterpolator(interpolator).start();
            }
            if (ViewCompat.getRotationX(view) != rotationX) {
                ViewCompat.animate(view).rotationX(rotationX).setDuration(duration).setInterpolator(interpolator).start();
            }
            if (ViewCompat.getRotationY(view) != rotationY) {
                ViewCompat.animate(view).rotationY(rotationY).setDuration(duration).setInterpolator(interpolator).start();
            }
        }
    }

    private int scrollBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(!isScrollEnabled()) {
            isScrolling = false;
            return d;
        }

        if(getChildCount() == 0 || !this.isScrolling || d == 0) {
            return 0;
        }

        this.lastScrollValue = d;

        if(this.isScrollNew) {
            this.isScrollNew = false;
            int direction = getDirection(d);
            if(!canScroll(direction)) {
                isScrolling = false;
                return 0;
            }
            this.recycler = recycler;
            this.currentScrollDirection = direction;
            this.totalScrollValue = 0;
        }

        this.totalScrollValue += d;

        int delta;

        boolean startBoundReached = !canScroll(DIRECTION_BACKWARD);
        boolean endBoundReached = !canScroll(DIRECTION_FORWARD);

        if (getDirection(d) == DIRECTION_FORWARD) {
            if (endBoundReached) {
                int endOffset = findEndOffset();
                delta = Math.max(-d, endOffset);
            } else {
                delta = -d;
            }
        } else {
            if (startBoundReached) {
                int startOffset = findStartOffset();
                delta = Math.min(-d, startOffset);
            } else {
                delta = -d;
            }
        }

        float progress = calculateScrollProgress();
        scrollViews(currentScrollDirection, progress);

        return -delta;
    }

    private float alphaForIndex(int index, int direction, float progress) {
        float alphaStart = alphaForIndex(index);
        float alphaEnd = alphaForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = alphaEnd - alphaStart;
        return alphaStart + delta * progress;
    }

    private float scaleXForIndex(int index, int direction, float progress) {
        float scaleStart = scaleXForIndex(index);
        float scaleEnd = scaleXForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = scaleEnd - scaleStart;
        return scaleStart + delta * progress;
    }

    private float scaleYForIndex(int index, int direction, float progress) {
        float scaleStart = scaleYForIndex(index);
        float scaleEnd = scaleYForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = scaleEnd - scaleStart;
        return scaleStart + delta * progress;
    }

    private float translationXForIndex(int index, int direction, float progress) {
        float translateXStart = translationXForIndex(index);
        float translateXEnd = translationXForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = translateXEnd - translateXStart;
        return translateXStart + delta * progress;
    }

    private float translationYForIndex(int index, int direction, float progress) {
        float translateYStart = translationYForIndex(index);
        float translateYEnd = translationYForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = translateYEnd - translateYStart;
        return translateYStart + delta * progress;
    }

    private float translationZForIndex(int index, int direction, float progress) {
        float translateZStart = translationZForIndex(index);
        float translateZEnd = translationZForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = translateZEnd - translateZStart;
        return translateZStart + delta * progress;
    }

    private float rotationXForIndex(int index, int direction, float progress) {
        float rotationXStart = rotationXForIndex(index);
        float rotationXEnd = rotationXForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = rotationXEnd - rotationXStart;
        return rotationXStart + delta * progress;
    }

    private float rotationYForIndex(int index, int direction, float progress) {
        float rotationYStart = rotationYForIndex(index);
        float rotationYEnd = rotationYForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = rotationYEnd - rotationYStart;
        return rotationYStart + delta * progress;
    }

    private float rotationForIndex(int index, int direction, float progress) {
        float rotationStart = rotationForIndex(index);
        float rotationEnd = rotationForIndex(direction == DIRECTION_FORWARD ? previousIndex(index) : nextIndex(index));
        float delta = rotationEnd - rotationStart;
        return rotationStart + delta * progress;
    }
}
