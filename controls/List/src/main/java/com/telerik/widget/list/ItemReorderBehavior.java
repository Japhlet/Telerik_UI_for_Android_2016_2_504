package com.telerik.widget.list;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link com.telerik.widget.list.ListViewBehavior} that can be used to allow the end user
 * to reorder items from the list with a combination of long press and drag.
 */
public class ItemReorderBehavior extends ListViewBehavior {

    private static final int SMOOTH_SCROLL_DISTANCE = 1000;
    private static final int SMOOTH_SCROLL_TIMER_DELAY = 50;
    private static final int ANIMATION_DURATION = 300;
    private static final int SHADE_WIDTH = 8;

    private Paint shadePaint;
    private Drawable shadeBottom;
    private Drawable shadeCorner;
    private Drawable shadeRight;

    private List<ItemReorderListener> listeners = new ArrayList<ItemReorderListener>();

    private Handler timerHandler;
    private Runnable timerRunnable;
    private boolean timerIsRunning;
    private View reorderView;
    private BitmapDrawable reorderImage;
    private boolean isReorderInProgress;
    private int scrollValue;
    private float scrollSpeedFactor = 1;
    private int currentScrollDirection;
    private View lastFoundView;
    private Rect reorderImageBoundsOriginal;
    private Rect reorderImageBoundsCurrent;
    private boolean shouldUpdateReorderView = false;
    private int reorderFromPosition = -1;
    private int previousReorderFromPosition = -1;

    /**
     * Creates a new instance of the SelectionBehavior.
     */
    public ItemReorderBehavior() {
    }

    /**
     * Adds a listener to be called when reorder occurs.
     *
     * @param listener the new listener.
     */
    public void addListener(ItemReorderListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener that is called when reorder occurs.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(ItemReorderListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public boolean isInProgress() {
        return isReorderInProgress;
    }

    @Override
    public void onAttached(RadListView listView) {
        super.onAttached(listView);
        init();
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        startReorder(motionEvent.getX(), motionEvent.getY());
    }

    @Override
    public void onLongPressDrag(float startX, float startY, float currentX, float currentY) {
        moveReorderImage(startX, startY, currentX, currentY);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return isReorderInProgress;
    }

    @Override
    public boolean onLongPressDragEnded(boolean isCanceled) {
        if(isInProgress()) {
            endReorder(isCanceled);
            return true;
        }
        return false;
    }

    @Override
    public void onDispatchDraw(Canvas canvas) {

        if(isReorderInProgress) {
            reorderImage.draw(canvas);
        }

        if(shouldUpdateReorderView) {
            long id = owner().getChildViewHolder(reorderView).getItemId();
            View reorderedView = findViewById(id);
            if(reorderedView != null) {

                RecyclerView.ViewHolder reorderedViewHolder = owner().getChildViewHolder(reorderedView);
                reorderedViewHolder.setIsRecyclable(false);
                reorderedView.setVisibility(View.INVISIBLE);

                RecyclerView.ViewHolder reorderViewHolder = owner().getChildViewHolder(reorderView);
                reorderViewHolder.setIsRecyclable(true);
                reorderView.setVisibility(View.VISIBLE);

                reorderView = reorderedView;
                reorderFromPosition = owner().getChildAdapterPosition(reorderView);
                shouldUpdateReorderView = false;
            }
        }
    }

    public int getScrollValue() {
        return scrollValue;
    }

    public void setScrollValue(int scrollValue) {
        this.scrollValue = scrollValue;
    }

    /**
     * A method that initiates reorder for the item at the provided coordinates.
     */
    protected void startReorder(float x, float y) {

        View view = findViewByCoordinates(x, y, true);
        if(view == null || owner().wrapperAdapter() == null || owner().getAdapter() == null) {
            return;
        }

        int reorderFromPosition = owner().getChildAdapterPosition(view);
        if(!((ListViewAdapter)owner().getAdapter()).canReorder(reorderFromPosition)) {
            return;
        }

        this.reorderView = view;
        this.reorderFromPosition = owner().getChildAdapterPosition(reorderView);

        this.isReorderInProgress = true;

        for(ItemReorderListener listener : this.listeners) {
            listener.onReorderStarted(reorderFromPosition);
        }
        int shadeOffset = SHADE_WIDTH / 2;
        int translateX = (int)ViewCompat.getTranslationX(view);
        int translateY = (int)ViewCompat.getTranslationY(view);

        int left = view.getLeft() + translateX - shadeOffset;
        int top = view.getTop() + translateY - shadeOffset;

        float scaleX = ViewCompat.getScaleX(view);
        float scaleY = ViewCompat.getScaleY(view);

        int right = view.getLeft() + translateX + (int)(view.getWidth() * scaleX);
        int bottom = view.getTop() + translateY + (int)(view.getHeight() * scaleY);

        this.reorderImageBoundsOriginal = new Rect(left, top, right, bottom);
        this.reorderImageBoundsCurrent = new Rect(reorderImageBoundsOriginal);

        view.setPressed(false);
        this.reorderImage = this.createReorderImage(view);
        this.reorderImage.setBounds(reorderImageBoundsCurrent);

        RecyclerView.ViewHolder viewHolder = this.owner().getChildViewHolder(view);
        viewHolder.setIsRecyclable(false);
        view.setVisibility(View.INVISIBLE);
    }

    /**
     * A method that moves the reordered item for the provided start coordinates to the provided current coordinates.
     */
    protected void moveReorderImage(float startX, float startY, float currentX, float currentY) {
        if(!isReorderInProgress) {
            return;
        }

        float distanceX = currentX - startX;
        float distanceY = currentY - startY;

        int reorderImageX = this.reorderImageBoundsOriginal.left + (int)distanceX;
        int reorderImageY = this.reorderImageBoundsOriginal.top + (int)distanceY;

        this.reorderImageBoundsCurrent.offsetTo(reorderImageX, reorderImageY);
        this.reorderImage.setBounds(reorderImageBoundsCurrent);
        this.owner().invalidate();

        View view = this.findViewByCoordinates(currentX, currentY, false);

        if(!tryScroll()) {
            if (this.timerIsRunning) {
                this.timerIsRunning = false;
                this.timerHandler.removeCallbacks(timerRunnable);
            }

            this.reorderToView(view);
        }
    }

    /**
     * A method that notifies that the reorder is finished.
     */
    protected void endReorder(boolean isCanceled) {
        if(!isReorderInProgress) {
            return;
        }
        if(timerIsRunning) {
            timerIsRunning = false;
            timerHandler.removeCallbacks(timerRunnable);
        }

        for(ItemReorderListener listener : this.listeners) {
            listener.onReorderFinished();
        }

        if(!isCanceled) {

            TranslateAnimation translateAnimation = new TranslateAnimation(
                    reorderImageBoundsCurrent.left - reorderView.getLeft(),
                    0,
                    reorderImageBoundsCurrent.top - reorderView.getTop(),
                    0);
            translateAnimation.setDuration(ANIMATION_DURATION);
            reorderView.startAnimation(translateAnimation);

        }

        resetReordering();
    }

    /**
     * A method that is responsible for creating an image that will be used for reorder of the view that is provided.
     */
    protected BitmapDrawable createReorderImage(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth() + SHADE_WIDTH, view.getHeight() + SHADE_WIDTH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        int offset = SHADE_WIDTH / 2;
        shadeBottom.setBounds(offset, view.getHeight(), view.getWidth(), view.getHeight() + SHADE_WIDTH);
        shadeCorner.setBounds(view.getWidth(), view.getHeight(), view.getWidth() + SHADE_WIDTH, view.getHeight() + SHADE_WIDTH);
        shadeRight.setBounds(view.getWidth(), offset, view.getWidth() + SHADE_WIDTH, view.getHeight());

        canvas.drawLine(0, 0, view.getWidth(), 1, this.shadePaint);
        canvas.drawLine(0, 0, 1, view.getHeight(), this.shadePaint);
        canvas.drawLine(view.getWidth(), 0, view.getWidth()+1, view.getHeight(), this.shadePaint);
        canvas.drawLine(0, view.getHeight(), view.getWidth(), view.getHeight()+1, this.shadePaint);

        shadeBottom.draw(canvas);
        shadeCorner.draw(canvas);
        shadeRight.draw(canvas);

        BitmapDrawable reorderImage = new BitmapDrawable(this.owner().getResources(), bitmap);

        return reorderImage;
    }

    private void resetReordering() {
        isReorderInProgress = false;
        reorderImage = null;
        reorderImageBoundsOriginal = null;
        reorderImageBoundsCurrent = null;
        lastFoundView = null;

        RecyclerView.ViewHolder reorderViewHolder = owner().getChildViewHolder(reorderView);
        reorderViewHolder.setIsRecyclable(true);
        reorderView.setVisibility(View.VISIBLE);
    }

    private void performSmoothScroll() {
        int distance = (int)(this.scrollValue * this.scrollSpeedFactor);
        final int scrollDistanceX = currentScrollDirection == RadListView.SCROLL_DIRECTION_LEFT ?
                -distance :
                currentScrollDirection == RadListView.SCROLL_DIRECTION_RIGHT ?
                        distance :
                        0;
        final int scrollDistanceY = currentScrollDirection == RadListView.SCROLL_DIRECTION_UP ?
                -distance :
                currentScrollDirection == RadListView.SCROLL_DIRECTION_DOWN ?
                        distance :
                        0;

        owner().smoothScrollBy(scrollDistanceX, scrollDistanceY);
    }

    private boolean tryScroll() {
        boolean result = false;
        if(this.owner().getLayoutManager().canScrollVertically()) {
            result = tryScroll(RadListView.SCROLL_DIRECTION_UP) || tryScroll(RadListView.SCROLL_DIRECTION_DOWN);
        }
        if(this.owner().getLayoutManager().canScrollHorizontally()) {
            result = result || tryScroll(RadListView.SCROLL_DIRECTION_LEFT) || tryScroll(RadListView.SCROLL_DIRECTION_RIGHT);
        }
        return result;
    }

    private boolean tryScroll(int direction) {
        boolean isImageOnEdge = false;
        float partOut = 0;
        switch (direction) {
            case RadListView.SCROLL_DIRECTION_LEFT:
                isImageOnEdge = reorderImageBoundsCurrent.left < 0 && reorderImageBoundsCurrent.right <= owner().getWidth();
                partOut = (-reorderImageBoundsCurrent.left) / (float)reorderImageBoundsCurrent.width();
                break;
            case RadListView.SCROLL_DIRECTION_RIGHT:
                isImageOnEdge = reorderImageBoundsCurrent.right > owner().getWidth() && reorderImageBoundsCurrent.left >= 0;
                partOut = (reorderImageBoundsCurrent.right - owner().getWidth()) / (float)reorderImageBoundsCurrent.width();
                break;
            case RadListView.SCROLL_DIRECTION_UP:
                isImageOnEdge = reorderImageBoundsCurrent.top < 0 && reorderImageBoundsCurrent.bottom <= owner().getHeight();
                partOut = (-reorderImageBoundsCurrent.top) / (float)reorderImageBoundsCurrent.height();
                break;
            case RadListView.SCROLL_DIRECTION_DOWN:
                isImageOnEdge = reorderImageBoundsCurrent.bottom > owner().getHeight() && reorderImageBoundsCurrent.top >= 0;
                partOut = (reorderImageBoundsCurrent.bottom - owner().getHeight()) / (float)reorderImageBoundsCurrent.height();
                break;
        }
        if(isImageOnEdge && owner().canScroll(direction)) {
            this.currentScrollDirection = direction;
            this.scrollSpeedFactor = partOut;

            if(!timerIsRunning) {
                this.timerIsRunning = true;
                this.timerHandler.postDelayed(timerRunnable, 0);
            }
            return true;
        }
        return false;
    }

    private void reorderOnEdge() {
        if(currentScrollDirection == RadListView.SCROLL_DIRECTION_NONE) {
            return;
        }
        int elementIndex = currentScrollDirection == RadListView.SCROLL_DIRECTION_RIGHT || currentScrollDirection == RadListView.SCROLL_DIRECTION_DOWN ?
                owner().getLayoutManager().getChildCount() - 1 :
                0;

        ListViewWrapperAdapter wrapperAdapter = owner().wrapperAdapter();
        int correction = elementIndex == 0 ?
                wrapperAdapter.getTopViewsCount() :
                - wrapperAdapter.getBottomViewsCount();


        elementIndex += correction;

        View view = owner().getLayoutManager().getChildAt(elementIndex);
        reorderToView(view);
    }

    private boolean isChangingPositions = false;

    private void reorderToView(View view) {

        if(view == null || view == reorderView) {
            return;
        }

        ListViewAdapter originalAdapter = (ListViewAdapter)owner().getAdapter();

        //int reorderFromPosition = owner().getChildAdapterPosition(reorderView);
        int reorderToPosition = owner().getChildAdapterPosition(view);
        if(reorderToPosition == previousReorderFromPosition && isChangingPositions) {
            // When reordering operation starts, an animation for each view from old to new state starts.
            // This leads to the situation that shortly after the animation has started one view has its
            // old coordinates - that is still under the reordering view and this way it is again
            // 'detected' as view that can be reordered. This return here stops the reorder operation
            // if a view is to be reordered to the position that it has just been on.
            return;
        }

        isChangingPositions = true;
        if(reorderFromPosition < 0 || reorderToPosition < 0) {
            return;
        }

        for(ItemReorderListener listener : this.listeners) {
            listener.onReorderItem(reorderFromPosition, reorderToPosition);
        }
        boolean isReordered = originalAdapter.reorderItem(reorderFromPosition, reorderToPosition);
        if(!isReordered) {
            return;
        }
        if(isReorderViewUpdated()) {
            shouldUpdateReorderView = true;
        }

        if(reorderFromPosition == 0 || reorderToPosition == 0) {
            this.owner().scrollToStart();
        } else {
            int lastIndexInOriginal = originalAdapter.getItemCount() - 1;
            if(reorderToPosition == lastIndexInOriginal || reorderFromPosition == lastIndexInOriginal) {
                View firstView = owner().getLayoutManager().getChildAt(0);
                if(view == firstView) {
                    this.owner().scrollToPosition(reorderToPosition);
                } else if (reorderView == firstView) {
                    this.owner().scrollToPosition(reorderToPosition - 1);
                } else {
                    this.owner().scrollToEnd();
                }
            }
        }
        previousReorderFromPosition = reorderFromPosition;
        reorderFromPosition = reorderToPosition;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isChangingPositions = false;
            }
        }, ANIMATION_DURATION);
    }

    private boolean isReorderViewUpdated() {
        RecyclerView.LayoutManager manager = this.owner().getLayoutManager();
        int childCount = manager.getChildCount();
        for(int i = 0; i < childCount; i++) {
            if(manager.getChildAt(i) == reorderView) {
                return false;
            }
        }
        return true;
    }

    private View findViewById(long id) {
        RecyclerView.LayoutManager layoutManager = owner().getLayoutManager();
        int childCount = layoutManager.getChildCount();
        for(int i = 0; i < childCount; i++) {
            View childView = layoutManager.getChildAt(i);
            RecyclerView.ViewHolder childViewHolder = owner().getChildViewHolder(childView);
            if(childViewHolder.getItemId() == id) {
                return childView;
            }
        }
        return null;
    }

    private View findViewByCoordinates(float x, float y, boolean returnNullOnHeaderViews) {
        if(lastFoundView != null && isPointWithinView(x, y, lastFoundView) && returnNullOnHeaderViews) {
            return lastFoundView;
        }
        RecyclerView.LayoutManager layoutManager = this.owner().getLayoutManager();
        int viewsCount = layoutManager.getChildCount();
        for(int i = viewsCount - 1; i >= 0; i--) {
            View view = layoutManager.getChildAt(i);
            if(isPointWithinView(x, y, view)) {
                int itemViewType = owner().getChildViewHolder(view).getItemViewType();
                if(itemViewType == ListViewWrapperAdapter.ITEM_VIEW_TYPE_HEADER ||
                        itemViewType == ListViewWrapperAdapter.ITEM_VIEW_TYPE_FOOTER ||
                        itemViewType == ListViewWrapperAdapter.ITEM_VIEW_TYPE_TOP_INDICATOR ||
                        itemViewType == ListViewWrapperAdapter.ITEM_VIEW_TYPE_BOTTOM_INDICATOR ||
                        itemViewType == ListViewWrapperAdapter.ITEM_VIEW_TYPE_EMPTY_CONTENT) {
                    return returnNullOnHeaderViews ? null : lastFoundView;
                }
                lastFoundView = view;
                return view;
            }
        }
        return null;
    }

    private void init() {

        this.scrollValue = (int)(SMOOTH_SCROLL_DISTANCE / this.owner().getResources().getDisplayMetrics().density);
        this.currentScrollDirection = RadListView.SCROLL_DIRECTION_NONE;

        this.timerHandler = new Handler();
        this.timerRunnable = new Runnable() {
            @Override
            public void run() {
                if(owner().canScroll(currentScrollDirection)) {
                    performSmoothScroll();
                    timerHandler.postDelayed(this, SMOOTH_SCROLL_TIMER_DELAY);
                } else {
                    reorderOnEdge();
                }
            }
        };

        this.shadePaint = new Paint();
        int shadeColor = owner().getResources().getColor(R.color.shadeColor);
        this.shadePaint.setColor(shadeColor);

        this.shadeBottom = owner().getContext().getResources().getDrawable(R.drawable.shade_bottom);
        this.shadeCorner = owner().getContext().getResources().getDrawable(R.drawable.shade_corner);
        this.shadeRight = owner().getContext().getResources().getDrawable(R.drawable.shade_right);
    }

    private boolean isPointWithinView(float pointX, float pointY, View view) {
        final float translationX = ViewCompat.getTranslationX(view);
        final float translationY = ViewCompat.getTranslationY(view);
        return pointX >= view.getLeft() + translationX &&
                pointX <= view.getRight() + translationX &&
                pointY >= view.getTop() + translationY &&
                pointY <= view.getBottom() + translationY;
    }

    /**
     * Interface definition for a callback to be invoked when item reorder is occurring.
     */
    public interface ItemReorderListener {

        /**
         * Called when the reorder starts for the item at the specified position.
         *
         * @param position the position of the item that is reordered.
         */
        void onReorderStarted(int position);

        /**
         * Called when the item at positionFrom needs to be reordered to positionTo.
         *
         * @param positionFrom  the previous position of the reordered item.
         * @param positionTo    the new position of the reordered item.
         */
        void onReorderItem(int positionFrom, int positionTo);

        /**
         * Called when reorder is finished.
         */
        void onReorderFinished();
    }
}
