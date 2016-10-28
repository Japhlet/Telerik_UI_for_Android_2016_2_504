package com.telerik.widget.list;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewCompatFixes;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link RecyclerView.LayoutManager} implementation that lays out items in a way
 * which resembles a deck of cards where one item is fully visible and there are additional
 * items which are visible in perspective.
 */
public class DeckOfCardsLayoutManager extends SlideLayoutManagerBase {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int DEFAULT_PERSPECTIVES_COUNT = 2;

    private static final int DIRECTION_NONE = 0;
    private static final int DIRECTION_FORWARD = 1;
    private static final int DIRECTION_BACKWARD = 2;

    private PerspectiveChangeInfo perspectiveChanges;
    private boolean autoDissolve = false;
    private int perspectiveItemsCount = DEFAULT_PERSPECTIVES_COUNT;

    private float actualTranslateLeft;
    private float actualTranslateTop;
    private float actualTranslateRight;
    private float actualTranslateBottom;

    private float defaultTranslation;

    private boolean reverseLayout;

    /**
     * Creates an instance of the {@link com.telerik.widget.list.DeckOfCardsLayoutManager} class.
     *
     * @param context the context to be used
     */
    public DeckOfCardsLayoutManager(Context context) {
        this(context, VERTICAL, false);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.list.DeckOfCardsLayoutManager} class.
     *
     * @param context       the context to be used
     * @param orientation   specifies the layout orientation - {@link #HORIZONTAL} or {@link
     *                      #VERTICAL}
     * @param reverseLayout when set to true, layouts from end to start
     */
    public DeckOfCardsLayoutManager(Context context, int orientation, boolean reverseLayout) {
        setOrientation(orientation);
        this.reverseLayout = reverseLayout;
        this.perspectiveChanges = new PerspectiveChangeInfo(this);
        this.defaultTranslation = context.getResources().getDimension(R.dimen.card_deck_translation);
    }

    /**
     * Provides access to a {@link PerspectiveChangeInfo} instance which contains information
     * about the transformations that will be applied to items in perspective.
     */
    public PerspectiveChangeInfo perspective() {
        return perspectiveChanges;
    }

    /**
     * Indicates whether the current item should automatically dissolve when swiped away.
     *
     * @return  whether the current item should automatically dissolve when swiped away.
     */
    public boolean isAutoDissolveFrontView() {
        return autoDissolve;
    }

    /**
     * Sets a value indicating whether the current item should automatically dissolve when swiped away.
     *
     * @param changeFrontViewAlpha the new value that will determine if the current item should automatically dissolve when swiped away.
     */
    public void setAutoDissolveFrontView(boolean changeFrontViewAlpha) {
        this.autoDissolve = changeFrontViewAlpha;
    }

    /**
     * Gets the number of items which should be seen in perspective.
     *
     * @return  the current number of items seen in perspective.
     */
    public int getPerspectiveItemsCount() {
        return perspectiveItemsCount;
    }

    /**
     * Sets a number which indicates how many items should be seen in perspective.
     *
     * @param perspectiveItemsCount number of items that should be seen in perspective.
     */
    public void setPerspectiveItemsCount(int perspectiveItemsCount) {
        if(perspectiveItemsCount < 0) {
            throw new IllegalArgumentException("The perspective items count can't be negative");
        }
        if(this.perspectiveItemsCount == perspectiveItemsCount) {
            return;
        }
        this.perspectiveItemsCount = perspectiveItemsCount;
        calculateFrontViewSize();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt("currentPosition", frontViewPosition);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            int currentPosition = bundle.getInt("currentPosition");
            scrollToPosition(currentPosition);
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return DeckOfCardsLayoutManager.this
                        .computeScrollVectorForPosition(targetPosition);
            }
        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos ? -1 : 1;
        return new PointF(0, direction);
    }

    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return frontViewWidth;
    }

    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        if(!canScrollHorizontally()) {
            return 0;
        }
        if(reverseLayout) {
            return (int)((-actualTranslateRight) * frontViewPosition);
        }
        return (int)((-actualTranslateLeft) * frontViewPosition);
    }

    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        if(!canScrollHorizontally()) {
            return 0;
        }
        if(reverseLayout) {
            return (int)(frontViewWidth + (state.getItemCount() - 1) * (-actualTranslateRight));
        }
        return (int)(frontViewWidth + (state.getItemCount() - 1) * (-actualTranslateLeft));
    }

    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        return frontViewHeight;
    }

    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        if(!canScrollVertically()) {
            return 0;
        }
        if(reverseLayout) {
            return (int)((-actualTranslateBottom) * frontViewPosition);
        }
        return (int)((-actualTranslateTop) * frontViewPosition);
    }

    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        if(!canScrollVertically()) {
            return 0;
        }
        if(reverseLayout) {
            return (int)(frontViewHeight + (state.getItemCount() - 1) * (-actualTranslateBottom));
        }
        return (int)(frontViewHeight + (state.getItemCount() - 1) * (-actualTranslateTop));
    }

    protected int getDirection(int scrollValue) {
        if((scrollValue > 0 && !reverseLayout) || (scrollValue < 0 && reverseLayout)) {
            return DIRECTION_BACKWARD;
        }
        if((scrollValue < 0 && !reverseLayout) || (scrollValue > 0 && reverseLayout)) {
            return DIRECTION_FORWARD;
        }
        return DIRECTION_NONE;
    }

    @Override
    protected void calculateFrontViewSize() {
        removeAllViews();
        if(perspectiveChanges.getTranslateStart() != PerspectiveChangeInfo.DEFAULT_TRANSLATION) {
            actualTranslateLeft = perspectiveChanges.getTranslateStart();
        } else {
            actualTranslateLeft = getOrientation() == HORIZONTAL && !reverseLayout ? -defaultTranslation : defaultTranslation;
        }
        if(perspectiveChanges.getTranslateTop() != PerspectiveChangeInfo.DEFAULT_TRANSLATION) {
            actualTranslateTop = perspectiveChanges.getTranslateTop();
        } else {
            actualTranslateTop = getOrientation() == VERTICAL && !reverseLayout ? -defaultTranslation : defaultTranslation;
        }
        if(perspectiveChanges.getTranslateEnd() != PerspectiveChangeInfo.DEFAULT_TRANSLATION) {
            actualTranslateRight = perspectiveChanges.getTranslateEnd();
        } else {
            actualTranslateRight = getOrientation() == HORIZONTAL ? actualTranslateLeft : -actualTranslateLeft;
        }
        if(perspectiveChanges.getTranslateBottom() != PerspectiveChangeInfo.DEFAULT_TRANSLATION) {
            actualTranslateBottom = perspectiveChanges.getTranslateBottom();
        } else {
            actualTranslateBottom = getOrientation() == VERTICAL ? actualTranslateTop : -actualTranslateTop;
        }
        float horizontalOffset = Math.min(actualTranslateLeft, 0) + Math.min(-actualTranslateRight, 0);
        frontViewWidth = getHorizontalSpace() - (int)Math.abs(horizontalOffset) * perspectiveItemsCount;
        float verticalOffset = Math.min(actualTranslateTop, 0) + Math.min(-actualTranslateBottom, 0);
        frontViewHeight = getVerticalSpace() - (int)Math.abs(verticalOffset) * perspectiveItemsCount;
    }

    @Override
    protected float calculateScrollProgress() {
        int sign = reverseLayout ? 1 : -1;
        return sign * super.calculateScrollProgress();
    }

    @Override
    protected long animationDuration() {
        return perspective().getAnimationDuration();
    }

    @Override
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

    @Override
    protected int elevationForIndex(int index) {
        return -perspective().getElevation() * index;
    }

    @Override
    protected float alphaForIndex(int index) {
        if(index <= -previousItemsCount()) {
            return 0;
        }
        if(autoDissolve && index >= nextItemsCount()) {
            return 0;
        }
        float alpha = perspectiveChanges.getAlpha();
        return (float)Math.pow(alpha, -index);
    }

    @Override
    protected float scaleXForIndex(int index) {
        return scaleForIndex(index);
    }

    @Override
    protected float scaleYForIndex(int index) {
        return scaleForIndex(index);
    }

    @Override
    protected float translationXForIndex(int index) {
        if(index <= -previousItemsCount()) {
            index = -previousItemsCount() + 1;
        }

        if(index >= nextItemsCount()) {
            if (getOrientation() == HORIZONTAL) {
                if (!reverseLayout) {
                    return getHorizontalSpace();
                } else {
                    return -getHorizontalSpace();
                }
            } else {
                return 0;
            }
        }

        return -index * actualTranslateLeft;
    }

    @Override
    protected float translationYForIndex(int index) {
        if(index <= -previousItemsCount()) {
            index = -previousItemsCount() + 1;
        }

        if(index >= nextItemsCount()) {
            if (getOrientation() == VERTICAL) {
                if (!reverseLayout) {
                    return getVerticalSpace();
                } else {
                    return -getVerticalSpace();
                }
            } else {
                return 0;
            }
        }

        return -index * actualTranslateTop;
    }

    @Override
    protected int previousIndex(int index) {
        return index + 1;
    }

    @Override
    protected int nextIndex(int index) {
        return index - 1;
    }

    @Override
    protected void layoutView(View view) {
        int width = frontViewWidth;
        int height = frontViewHeight;
        int left = getPaddingLeft() + perspectiveItemsCount * (int)Math.max(-actualTranslateLeft, 0);
        int top = getPaddingTop() + perspectiveItemsCount * (int)Math.max(-actualTranslateTop, 0);
        int right = left + width;
        int bottom = top + height;

        int pivotX = getOrientation() == HORIZONTAL && reverseLayout ? frontViewWidth : 0;
        int pivotY = getOrientation() == VERTICAL && reverseLayout ? frontViewHeight : 0;

        ViewCompat.setPivotX(view, pivotX);
        ViewCompatFixes.setPivotY(view, pivotY);

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

    @Override
    protected int nextItemsCount() {
        return 1;
    }

    @Override
    protected int previousItemsCount() {
        return perspectiveItemsCount + 1;
    }

    @Override
    protected int adapterPositionForLayoutIndex(int layoutIndex) {
        return frontViewPosition - layoutIndex;
    }

    @Override
    protected int layoutIndexForAdapterPosition(int adapterPosition) {
        return frontViewPosition - adapterPosition;
    }

    @Override
    protected void fill(int direction, RecyclerView.Recycler recycler, RecyclerView.State state) {
        switch (direction) {
            case DIRECTION_NONE:
                fillAll(recycler, state);
                break;
            case DIRECTION_FORWARD:
                fillAtStart(recycler, state);
                break;
            case DIRECTION_BACKWARD:
                fillAtEnd(recycler, state);
                break;
        }
    }

    @Override
    protected void handleItemRemoved(int layoutIndex, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if(layoutIndex < 0) {
            fillAtStart(recycler, state, layoutIndex);
        }
        if(layoutIndex == 0) {
            if(getStateItemCount() == frontViewPosition) {
                int oldPosition = frontViewPosition;
                frontViewPosition--;
                notifyListeners(oldPosition, frontViewPosition);
                fillAtEnd(recycler, state, layoutIndex);
            } else {
                fillAtStart(recycler, state, layoutIndex);
            }
        }
        if(layoutIndex > 0) {
            int oldPosition = frontViewPosition;
            frontViewPosition--;
            notifyListeners(oldPosition, frontViewPosition);
            fillAtEnd(recycler, state, layoutIndex);
        }
    }

    private float alphaForIndex(int index, int direction, float progress) {
        if(progress < 0) progress = 0;
        if(progress > 1) progress = 1;
        float alphaStart = alphaForIndex(index);
        float alphaEnd = alphaForIndex(direction == DIRECTION_FORWARD ? index + 1 : index - 1);
        float delta = alphaEnd - alphaStart;
        return alphaStart + delta * progress;
    }

    private float scaleForIndex(int index) {
        if(index >= 0) {
            return 1;
        }
        if(index < -previousItemsCount()) {
            index = 1;
        }
        if(getOrientation() == VERTICAL) {
            float targetWidth = frontViewWidth + index * (actualTranslateLeft - actualTranslateRight);
            return targetWidth / frontViewWidth;
        } else {
            float targetHeight = frontViewHeight + index * (actualTranslateTop - actualTranslateBottom);
            return targetHeight / frontViewHeight;
        }
    }

    private float scaleXForIndex(int index, int direction, float progress) {
        if(progress < 0) progress = 0;
        if(progress > 1) progress = 1;
        float scaleStart = scaleXForIndex(index);
        float scaleEnd = scaleXForIndex(direction == DIRECTION_FORWARD ? index + 1 : index - 1);
        float delta = scaleEnd - scaleStart;
        return scaleStart + delta * progress;
    }

    private float scaleYForIndex(int index, int direction, float progress) {
        if(progress < 0) progress = 0;
        if(progress > 1) progress = 1;
        float scaleStart = scaleYForIndex(index);
        float scaleEnd = scaleYForIndex(direction == DIRECTION_FORWARD ? index + 1 : index - 1);
        float delta = scaleEnd - scaleStart;
        return scaleStart + delta * progress;
    }

    private float translationXForIndex(int index, int direction, float progress) {
        if(progress < 0) progress = 0;
        if(progress > 1) progress = 1;
        float translateXStart = translationXForIndex(index);
        float translateXEnd = this.translationXForIndex(direction == DIRECTION_FORWARD ? index + 1 : index - 1);
        float delta = translateXEnd - translateXStart;
        return translateXStart + delta * progress;
    }

    private float translationYForIndex(int index, int direction, float progress) {
        if(progress < 0) progress = 0;
        if(progress > 1) progress = 1;
        float translateYStart = translationYForIndex(index);
        float translateYEnd = this.translationYForIndex(direction == DIRECTION_FORWARD ? index + 1 : index - 1);
        float delta = translateYEnd - translateYStart;
        return translateYStart + delta * progress;
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
}
