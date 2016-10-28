package com.telerik.widget.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link RecyclerView.LayoutManager} implementation that lays out items in a way
 * which resembles an image gallery where one item is shown in front as current and user can swipe
 * to move to an adjacent item.
 */
public class SlideLayoutManager extends SlideLayoutManagerBase {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    static final float SLIDE_TRANSITION_SCALE = 0.7f;

    private Transition transitionMode = Transition.SLIDE_AWAY;
    private int itemSpacing = 0;

    private int previousItemPreview = 0;
    private int nextItemPreview = 0;

    private boolean scrollOnTap = true;

    /**
     * Creates an instance of the {@link com.telerik.widget.list.SlideLayoutManager} class.
     *
     * @param context the context to be used
     */
    public SlideLayoutManager(Context context) {
        this(context, HORIZONTAL);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.list.SlideLayoutManager} class.
     *
     * @param context       the context to be used
     * @param orientation   specifies the layout orientation - {@link #HORIZONTAL} or {@link
     *                      #VERTICAL}
     */
    public SlideLayoutManager(Context context, int orientation) {
        setOrientation(orientation);
    }

    /**
     * Indicates whether the adjacent items should become current when tapped.
     *
     * @return  whether the adjacent items should become current when tapped
     */
    public boolean isScrollOnTap() {
        return scrollOnTap;
    }

    /**
     * Sets a value indicating whether the adjacent items should become current when tapped.
     *
     * @param scrollOnTap the new value that will determine if the adjacent items should become current when tapped
     */
    public void setScrollOnTap(boolean scrollOnTap) {
        this.scrollOnTap = scrollOnTap;
    }

    /**
     * Gets the current item spacing in pixels between the items.
     *
     * @return  the current item spacing in pixels between the items
     */
    public int getItemSpacing() {
        return itemSpacing;
    }

    /**
     * Sets a value indicating how many pixels should be drawn between two items.
     *
     * @param itemSpacing the new item spacing between items
     */
    public void setItemSpacing(int itemSpacing) {
        if(itemSpacing < 0) {
            throw new IllegalArgumentException("The Item Spacing can't be negative.");
        }
        this.itemSpacing = itemSpacing;
    }

    /**
     * Gets the current transition mode seen when the current item changes.
     *
     * @return  the current transition mode
     */
    public Transition getTransitionMode() {
        return transitionMode;
    }

    /**
     * Sets a new value for the transition mode seen when the current item changes.
     *
     * @param transitionMode the new transition mode
     */
    public void setTransitionMode(Transition transitionMode) {
        this.transitionMode = transitionMode;
    }

    /**
     * Gets a value indicating how many pixels from the next item should be visible without swiping.
     *
     * @return  the current value for pixels visible from the next item
     */
    public int getNextItemPreview() {
        return nextItemPreview;
    }

    /**
     * Sets a new value indicating how many pixels from the next item should be visible without swiping.
     *
     * @param nextItemPreview the new value for pixels visible from the next item
     */
    public void setNextItemPreview(int nextItemPreview) {
        if(nextItemPreview < 0) {
            throw new IllegalArgumentException("The preview of items can't be negative.");
        }
        this.nextItemPreview = nextItemPreview;
        removeAllViews();
    }

    /**
     * Gets a value indicating how many pixels from the previous item should be visible without swiping.
     *
     * @return  the current value for pixels visible from the previous item
     */
    public int getPreviousItemPreview() {
        return previousItemPreview;
    }

    /**
     * Sets a new value indicating how many pixels from the previous item should be visible without swiping.
     *
     * @param previousItemPreview the new value for pixels visible from the previous item
     */
    public void setPreviousItemPreview(int previousItemPreview) {
        if(previousItemPreview < 0) {
            throw new IllegalArgumentException("The preview of items can't be negative.");
        }
        this.previousItemPreview = previousItemPreview;
        removeAllViews();
    }

    @Override
    protected void calculateFrontViewSize() {
        frontViewWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        frontViewHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        if(getOrientation() == HORIZONTAL) {
            frontViewWidth -= (previousItemPreview + nextItemPreview);
        } else {
            frontViewHeight -= (previousItemPreview + nextItemPreview);
        }
    }

    @Override
    protected int previousItemsCount() {
        return super.previousItemsCount() + (this.previousItemPreview > 0 ? 1 : 0);
    }

    @Override
    protected int nextItemsCount() {
        return super.nextItemsCount() + (this.nextItemPreview > 0 ? 1 : 0);
    }

    @Override
    protected void scrollViews(int direction, float progress) {
        float actualProgress = Math.min(progress, 1);
        actualProgress = Math.max(actualProgress, -1);
        super.scrollViews(direction, actualProgress);
    }

    @Override
    protected void layoutView(View view) {
        int width = frontViewWidth;
        int height = frontViewHeight;

        int left = getPaddingLeft();
        int top = getPaddingTop();

        if(getOrientation() == HORIZONTAL) {
            left += previousItemPreview;
        } else {
            top += previousItemPreview;
        }

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

    @Override
    protected float alphaForIndex(int layoutIndex) {
        if(transitionMode == Transition.SLIDE_OVER) {
            if(layoutIndex >= 1) {
                return 0;
            }
        }
        return 1;
    }

    @Override
    protected float translationXForIndex(int layoutIndex) {
        if(getOrientation() == VERTICAL) {
            return 0;
        }
        if(transitionMode == Transition.SLIDE_OVER) {
            if(layoutIndex >= 0) {
                return 0;
            }
        }
        return layoutIndex * (frontViewWidth + itemSpacing);
    }

    @Override
    protected float translationYForIndex(int layoutIndex) {
        if(getOrientation() == HORIZONTAL) {
            return 0;
        }
        if(transitionMode == Transition.SLIDE_OVER) {
            if(layoutIndex >= 0) {
                return 0;
            }
        }
        return layoutIndex * (frontViewHeight + itemSpacing);
    }

    @Override
    protected float translationZForIndex(int layoutIndex) {
        if(transitionMode == Transition.SLIDE_OVER) {
            return -layoutIndex;
        }
        return 0;
    }

    @Override
    protected float scaleXForIndex(int layoutIndex) {
        if(transitionMode == Transition.SLIDE_OVER) {
            if(layoutIndex >= 1) {
                return SLIDE_TRANSITION_SCALE;
            }
        }
        return 1;
    }

    @Override
    protected float scaleYForIndex(int layoutIndex) {
        if(transitionMode == Transition.SLIDE_OVER) {
            if(layoutIndex >= 1) {
                return SLIDE_TRANSITION_SCALE;
            }
        }
        return 1;
    }

    void onTap(int position) {
        if(!scrollOnTap) {
            return;
        }
        if(position != frontViewPosition) {
            if(position == frontViewPosition - 1) {
                scrollToPrevious();
            } else if (position == frontViewPosition + 1) {
                scrollToNext();
            } else {
                scrollToPosition(position);
            }
        }
    }

    public enum Transition {
        SLIDE_AWAY, SLIDE_OVER
    }
}
