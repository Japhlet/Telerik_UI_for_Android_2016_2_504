package com.telerik.widget.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link RecyclerView.LayoutManager} implementation that starts the layout of items on one line
 * until space is available. When the is no space on the current line to accommodate the next item
 * a new line is created and the layout process continues there.
 */
public class WrapLayoutManager extends RecyclerView.LayoutManager {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int lineSpacing = 0;
    private int minimumItemSpacing = 0;

    private int scrollRangeCache = -1;

    private int gravity = Gravity.TOP | Gravity.START;

    private int orientation = VERTICAL;

    private int availableWidth;
    private int availableHeight;

    private int layoutStart;
    private int layoutEnd;

    private int currentTop;
    private int currentLeft;

    private int firstVisiblePosition = 0;
    private int lastVisiblePosition = -1;

    private int maxSizeForCurrentLine = 0;
    private int firstVisibleLine = 0;
    private int lastVisibleLine = -1;

    private int scrollOffset = -1;

    private RecyclerView.State state;
    private RecyclerView.Recycler recycler;

    private SparseIntArray lineIndexToItemIndex = new SparseIntArray();
    private SparseIntArray lineIndexToLineSize = new SparseIntArray();

    /**
     * Creates an instance of the {@link com.telerik.widget.list.WrapLayoutManager} class.
     *
     * @param context the context to be used
     */
    public WrapLayoutManager(Context context) {
        this(context, VERTICAL);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.list.WrapLayoutManager} class.
     *
     * @param context       the context to be used
     * @param orientation   specifies the layout orientation - {@link #HORIZONTAL} or {@link
     *                      #VERTICAL}
     */
    public WrapLayoutManager(Context context, int orientation) {
        setOrientation(orientation);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.list.WrapLayoutManager} class.
     */
    public WrapLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Properties properties = getProperties(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(properties.orientation);
    }

    /**
     * Gets the current {@link Gravity} that specifies how the empty space left after items
     * are laid out is redistributed.
     *
     * @return  the current {@link Gravity}
     */
    public int getGravity() {
        return gravity;
    }

    /**
     * Sets a new {@link Gravity} that will determine how the empty space left after items
     * are laid out will be redistributed.
     *
     * @param gravity the new gravity
     */
    public void setGravity(int gravity) {
        this.gravity = gravity;
        removeAllViews();
    }

    /**
     * Gets a value in pixels indicating the space that will be left between each line with items.
     *
     * @return  the current line spacing
     */
    public int getLineSpacing() {
        return lineSpacing;
    }

    /**
     * Sets a new value in pixels indicating the space that will be left between each line with items.
     *
     * @param lineSpacing the line spacing
     */
    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
        removeAllViews();
    }

    /**
     * Gets the minimum item spacing between items that are laid out on a single line.
     * The spacing is minimum because it may increase depending on the current gravity.
     *
     * @return  the current minimum item spacing
     */
    public int getMinimumItemSpacing() {
        return minimumItemSpacing;
    }

    /**
     * Sets the minimum item spacing between items that are laid out on a single line.
     * The spacing is minimum because it may increase depending on the current gravity.
     *
     * @param minimumItemSpacing the minimum item spacing
     */
    public void setMinimumItemSpacing(int minimumItemSpacing) {
        this.minimumItemSpacing = minimumItemSpacing;
        removeAllViews();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.recycler = recycler;
        this.state = state;

        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if(state.isPreLayout()) {
            return;
        }
        if (getChildCount() == 0) {
            calculateAvailableSpace();

            scrollRangeCache = -1;

            layoutStart = 0;
            layoutEnd = orientation == VERTICAL ? availableHeight : availableWidth;
            currentTop = 0;
            currentLeft = 0;

            firstVisiblePosition = 0;
            lastVisiblePosition = -1;
            firstVisibleLine = 0;
            lastVisibleLine = -1;

            lineIndexToItemIndex.clear();
            lineIndexToLineSize.clear();

            while(spaceIsAvailable() && thereAreMoreItems()) {
                if(lastVisibleLine >= 0) {
                    if(orientation == VERTICAL) {
                        currentTop += lineSpacing;
                    } else {
                        currentLeft += lineSpacing;
                    }
                }
                realizeOneLineAtEnd(recycler);
            }
        } else {
            for(int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                recycler.bindViewToPosition(view, firstVisiblePosition + i);
            }
        }
        if(scrollOffset != -1 && !state.isPreLayout()) {
            if(orientation == VERTICAL) {
                scrollVerticallyBy(scrollOffset, recycler, state);
            } else {
                scrollHorizontallyBy(scrollOffset, recycler, state);
            }
            scrollOffset = -1;
        }
    }

    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        if(orientation != HORIZONTAL) {
            return 0;
        }
        return calculateOffset();
    }

    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        if(orientation != VERTICAL) {
            return 0;
        }
        return calculateOffset();
    }

    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return availableWidth;
    }

    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        return availableHeight;
    }

    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        return scrollRangeCache == -1 ? makeRangeEstimate() : scrollRangeCache;
    }

    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        return scrollRangeCache == -1 ? makeRangeEstimate() : scrollRangeCache;
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
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(orientation != HORIZONTAL) {
            return 0;
        }
        int scrollDistance = dx;
        if(dx > 0) {
            while(layoutEnd - availableWidth < scrollDistance) {
                if(lastVisiblePosition < state.getItemCount() - 1) {
                    currentLeft = layoutEnd + lineSpacing;
                    realizeOneLineAtEnd(recycler);
                } else {
                    scrollDistance = layoutEnd - availableWidth;
                    scrollRangeCache = computeHorizontalScrollOffset(state) + computeHorizontalScrollExtent(state);
                }
            }
        } else {
            while(scrollDistance < this.layoutStart) {
                if(firstVisiblePosition > 0) {
                    realizeOneLineAtStart(recycler);
                } else {
                    scrollDistance = layoutStart;
                }
            }
        }
        offsetChildrenHorizontal(-scrollDistance);
        layoutStart -= scrollDistance;
        layoutEnd -= scrollDistance;

        int firstLineWidth = lineIndexToLineSize.get(firstVisibleLine);
        while(firstVisibleLine <= lastVisibleLine && firstLineWidth < Math.abs(layoutStart)) {
            recycleLine(recycler, firstVisibleLine);
            firstVisibleLine++;
            layoutStart += firstLineWidth + lineSpacing;
            firstVisiblePosition = lineIndexToItemIndex.get(firstVisibleLine);
            firstLineWidth = lineIndexToLineSize.get(firstVisibleLine);
        }
        int lastLineWidth = lineIndexToLineSize.get(lastVisibleLine);
        while(lastVisibleLine >= firstVisibleLine && lastLineWidth < layoutEnd - availableWidth) {
            recycleLine(recycler, lastVisibleLine);
            layoutEnd -= (lastLineWidth + lineSpacing);
            lastVisiblePosition = lineIndexToItemIndex.get(lastVisibleLine)-1;
            lastVisibleLine--;
            lastLineWidth = lineIndexToLineSize.get(lastVisibleLine);
        }
        return scrollDistance;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(orientation != VERTICAL) {
            return 0;
        }
        int scrollDistance = dy;
        if(dy > 0) {
            if(thereAreMoreItems()) {
                while (layoutEnd - availableHeight < scrollDistance) {
                    if (lastVisiblePosition < state.getItemCount() - 1) {
                        currentTop = layoutEnd + lineSpacing;
                        realizeOneLineAtEnd(recycler);
                    } else {
                        scrollDistance = layoutEnd - availableHeight;
                        scrollRangeCache = computeVerticalScrollOffset(state) + computeVerticalScrollExtent(state);
                    }
                }
            } else {
                int maxAvailableScroll = Math.max(layoutEnd - availableHeight, 0);
                scrollDistance = Math.min(scrollDistance, maxAvailableScroll);
            }
        } else {
            while(scrollDistance < this.layoutStart) {
                if(firstVisiblePosition > 0) {
                    realizeOneLineAtStart(recycler);
                } else {
                    scrollDistance = layoutStart;
                }
            }
        }
        offsetChildrenVertical(-scrollDistance);
        layoutStart -= scrollDistance;
        layoutEnd -= scrollDistance;

        int firstRowHeight = lineIndexToLineSize.get(firstVisibleLine);
        while(firstVisibleLine <= lastVisibleLine && firstRowHeight < Math.abs(layoutStart)) {
            recycleLine(recycler, firstVisibleLine);
            firstVisibleLine++;
            layoutStart += firstRowHeight + lineSpacing;
            firstVisiblePosition = lineIndexToItemIndex.get(firstVisibleLine);
            firstRowHeight = lineIndexToLineSize.get(firstVisibleLine);
        }
        int lastRowHeight = lineIndexToLineSize.get(lastVisibleLine);
        while(lastVisibleLine >= firstVisibleLine && lastRowHeight < layoutEnd - availableHeight) {
            recycleLine(recycler, lastVisibleLine);
            layoutEnd -= (lastRowHeight + lineSpacing);
            lastVisiblePosition = lineIndexToItemIndex.get(lastVisibleLine)-1;
            lastVisibleLine--;
            lastRowHeight = lineIndexToLineSize.get(lastVisibleLine);
        }
        return scrollDistance;
    }

    @Override
    public void scrollToPosition(int position) {
        if(position < 0 || position > getItemCount() - 1) {
            return;
        }
        ensureVisible(position);
        boolean isOnFirstRow = ensureOnFirstRow(position);
        ensureOnEdge(isOnFirstRow);
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        if(positionStart > lastVisiblePosition) {
            return;
        }
        scrollOffset = computeScrollOffset();
        removeAllViews();
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        removeAllViews();
    }

    @Override
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        if(from > lastVisiblePosition) {
            return;
        }
        scrollOffset = computeScrollOffset();
        removeAllViews();
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        if(positionStart > lastVisiblePosition) {
            return;
        }
        scrollOffset = computeScrollOffset();
        removeAllViews();
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
    }

    private void ensureVisible(int position) {
        int scrollValue = lineIndexToLineSize.get(firstVisibleLine);
        while(position < firstVisiblePosition) {
            scrollInCorrectDirection(-scrollValue);
        }
        while (position > lastVisiblePosition) {
            scrollInCorrectDirection(scrollValue);
        }
    }

    private boolean ensureOnFirstRow(int position) {
        if(lineIndexToItemIndex.size() <= firstVisibleLine + 1) {
            return true;
        }
        int secondLineFirst = lineIndexToItemIndex.get(firstVisibleLine + 1);
        int scrollValue = 1;
        while (secondLineFirst <= position && scrollValue != 0) {
            scrollValue = scrollInCorrectDirection(lineIndexToLineSize.get(firstVisibleLine));
            secondLineFirst = lineIndexToItemIndex.get(firstVisibleLine + 1);
        }
        return scrollValue != 0;
    }

    private void ensureOnEdge(boolean isOnFirstRow) {
        if(isOnFirstRow) {
            scrollInCorrectDirection(layoutStart);
        } else if(layoutEnd > availableHeight){
            scrollInCorrectDirection(layoutEnd - availableHeight);
        }
    }

    private int scrollInCorrectDirection(int d) {
        if(orientation == VERTICAL) {
            return scrollVerticallyBy(d, recycler, state);
        } else {
            return scrollHorizontallyBy(d, recycler, state);
        }
    }

    private int computeScrollOffset() {
        if(orientation == VERTICAL) {
            return computeVerticalScrollOffset(state);
        }
        return computeHorizontalScrollOffset(state);
    }

    private void calculateAvailableSpace() {
        availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        availableHeight = getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation:" + orientation);
        }

        this.orientation = orientation;
    }

    private int calculateOffset() {
        int totalOffset = 0;
        for(int i = 0; i < firstVisibleLine; i++) {
            int offset = lineIndexToLineSize.get(i) + lineSpacing;
            totalOffset += offset;
        }
        totalOffset += -layoutStart;
        return totalOffset;
    }

    private void recycleLine(RecyclerView.Recycler recycler, int lineIndex) {
        int firstIndex = lineIndexToItemIndex.get(lineIndex);
        int nextLineIndex;
        if(lineIndexToItemIndex.size() > lineIndex + 1) {
            nextLineIndex = lineIndexToItemIndex.get(lineIndex + 1);
        }
        else {
            nextLineIndex = getItemCount();
        }
        int removedChildren = nextLineIndex - firstIndex;
        for(int i = 0; i < removedChildren; i++) {
            final View child = getChildAt(firstIndex - firstVisiblePosition);
            if(child != null) {
                removeAndRecycleView(child, recycler);
            }
        }
    }

    private void realizeOneLineAtStart(RecyclerView.Recycler recycler) {
        int previousLineIndex = firstVisibleLine - 1;
        int previousLineFirstPosition = lineIndexToItemIndex.get(previousLineIndex);
        int previousLineSize = lineIndexToLineSize.get(previousLineIndex);

        if(orientation == VERTICAL) {
            currentLeft = 0;
            currentTop = layoutStart - previousLineSize - lineSpacing;
            layoutStart = currentTop;
        } else {
            currentTop = 0;
            currentLeft = layoutStart - previousLineSize - lineSpacing;
            layoutStart = currentLeft;
        }

        int firstPosition = previousLineFirstPosition;
        int lastPosition = firstVisiblePosition;

        int childCount = getChildCount();
        SparseArray<View> viewCache = new SparseArray<View>(childCount);
        for(int i = 0; i < childCount; i++) {
            viewCache.put(i, getChildAt(i));
        }
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        for(int i = firstPosition; i < lastPosition; i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            layoutView(view);
            if(orientation == VERTICAL) {
                currentLeft += view.getMeasuredWidth() + minimumItemSpacing;
            } else {
                currentTop += view.getMeasuredHeight() + minimumItemSpacing;
            }
        }

        for(int i = 0; i < viewCache.size(); i++) {
            attachView(viewCache.valueAt(i));
        }

        firstVisiblePosition = firstPosition;
        firstVisibleLine--;
        adjustLineForGravity(firstVisibleLine, false);
    }

    private void realizeOneLineAtEnd(RecyclerView.Recycler recycler) {
        int position = lastVisiblePosition + 1;
        lastVisibleLine++;
        lineIndexToItemIndex.put(lastVisibleLine, position);
        if(orientation == VERTICAL) {
            currentLeft = 0;
        } else {
            currentTop = 0;
        }
        while (position < state.getItemCount()) {
            View view = recycler.getViewForPosition(position);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            if (isThereEnoughSpaceForView(view)) {
                moveToNextLine();
                adjustLineForGravity(lastVisibleLine, true);
                removeView(view);
                return;
            }
            layoutView(view);
            lastVisiblePosition = position;
            if(orientation == VERTICAL) {
                currentLeft += view.getMeasuredWidth() + minimumItemSpacing;
                maxSizeForCurrentLine = Math.max(maxSizeForCurrentLine, view.getMeasuredHeight());
                layoutEnd = currentTop + maxSizeForCurrentLine;
            } else {
                currentTop += view.getMeasuredHeight() + minimumItemSpacing;
                maxSizeForCurrentLine = Math.max(maxSizeForCurrentLine, view.getMeasuredWidth());
                layoutEnd = currentLeft + maxSizeForCurrentLine;
            }
            position++;
        }
        moveToNextLine();
        adjustLineForGravity(lastVisibleLine, true);
    }

    private void moveToNextLine() {
        if(orientation == VERTICAL) {
            currentTop += maxSizeForCurrentLine;
        } else {
            currentLeft += maxSizeForCurrentLine;
        }
        lineIndexToLineSize.put(lastVisibleLine, maxSizeForCurrentLine);
        maxSizeForCurrentLine = 0;
    }

    private int calculateUsedSpace(int firstIndex, int count) {
        int space = 0;
        for(int i = 0; i < count; i++) {
            View view = getChildAt(firstIndex + i);
            if(orientation == VERTICAL) {
                space += view.getMeasuredWidth();
            } else {
                space += view.getMeasuredHeight();
            }
            if(i > 0) {
                space += minimumItemSpacing;
            }
        }
        return space;
    }

    private boolean viewsShouldBeStretched() {
        if (orientation == VERTICAL) {
            return (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL;
        } else {
            return (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL;
        }
    }

    private void adjustLineForGravity(int lineIndex, boolean addedToEnd) {
        int firstPosition = lineIndexToItemIndex.get(lineIndex);
        int count = addedToEnd ?
                lastVisiblePosition - firstPosition + 1 :
                lineIndexToItemIndex.get(lineIndex + 1) - firstPosition;
        int lineSize = lineIndexToLineSize.get(lineIndex);
        int firstLayoutIndex = firstPosition - firstVisiblePosition;

        adjustLineInLayoutDirection(firstLayoutIndex, count, lineSize);
        adjustLineInOrthogonalDirection(firstLayoutIndex, count);
    }

    private void adjustLineInOrthogonalDirection(int firstLayoutIndex, int count) {
        if(!viewsShouldBeMoved(false)) {
            return;
        }

        int usedSpace = calculateUsedSpace(firstLayoutIndex, count);
        int availableSize = orientation == VERTICAL ? availableWidth : availableHeight;

        if(viewsShouldBeStretched()) {
            int remainingSpace = availableSize - usedSpace;
            int spacePerItem = count > 1 ? remainingSpace / (count - 1) : 0;
            int remainingPixels = remainingSpace - spacePerItem * (count - 1);
            for (int i = 0; i < count; i++) {
                int position = firstLayoutIndex + i;
                View view = getChildAt(position);
                int offset = spacePerItem * i;
                if (i > 0) {
                    offset += Math.min(i, remainingPixels);
                }
                offsetView(view, offset, false);
            }
            return;
        }

        if(viewsShouldBeCentered(false)) {
            for (int i = 0; i < count; i++) {
                int position = firstLayoutIndex + i;
                View view = getChildAt(position);
                int offset = (availableSize - usedSpace) / 2;
                offsetView(view, offset, false);
            }
            return;
        }

        for (int i = 0; i < count; i++) {
            int position = firstLayoutIndex + i;
            View view = getChildAt(position);
            int offset = availableSize - usedSpace;
            offsetView(view, offset, false);
        }
    }

    private void adjustLineInLayoutDirection(int firstLayoutIndex, int count, int lineSize) {

        if(!viewsShouldBeMoved(true)) {
            return;
        }

        for(int i = 0; i < count; i++) {
            int position = firstLayoutIndex + i;
            View view = getChildAt(position);
            int viewSize = orientation == VERTICAL ? view.getMeasuredHeight() : view.getMeasuredWidth();
            int difference = lineSize - viewSize;

            if(viewsShouldBeCentered(true)) {
                difference /= 2;
            }

            offsetView(view, difference, true);
        }
    }

    private boolean isThereEnoughSpaceForView(View view) {
        if (orientation == VERTICAL) {
            return currentLeft + view.getMeasuredWidth() > availableWidth;
        }
        return currentTop + view.getMeasuredHeight() > availableHeight;
    }

    private boolean viewsShouldBeMoved(boolean inLayoutDirection) {
        if ((orientation == VERTICAL && inLayoutDirection) ||
                (orientation == HORIZONTAL && !inLayoutDirection)) {
            int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
            if(!inLayoutDirection && verticalGravity == Gravity.FILL_VERTICAL) {
                return true;
            }
            return verticalGravity == Gravity.CENTER_VERTICAL || verticalGravity == Gravity.BOTTOM;
        } else {
            int horizontalGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            if(!inLayoutDirection && horizontalGravity == Gravity.FILL_HORIZONTAL) {
                return true;
            }
            return horizontalGravity == Gravity.CENTER_HORIZONTAL || horizontalGravity == Gravity.RIGHT;
        }
    }

    private boolean viewsShouldBeCentered(boolean inLayoutDirection) {
        if ((orientation == VERTICAL && inLayoutDirection) ||
                orientation == HORIZONTAL && !inLayoutDirection) {
            return (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL;
        } else {
            return (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL;
        }
    }

    private boolean spaceIsAvailable() {
        if(orientation == VERTICAL) {
            return currentTop < availableHeight;
        }
        return currentLeft < availableWidth;
    }

    private boolean thereAreMoreItems() {
        return lastVisiblePosition < state.getItemCount() - 1;
    }

    private int makeRangeEstimate() {
        int size = orientation == VERTICAL ? availableHeight : availableWidth;
        int count = lastVisiblePosition - firstVisiblePosition + 1;
        if (count <= 0) {
            return 0;
        }
        float factor = (float)size / count;
        return (int)(factor * getItemCount());
    }

    private void offsetView(View view, int offset, boolean offsetInLayoutDirection) {
        if((orientation == VERTICAL && offsetInLayoutDirection) ||
                (orientation == HORIZONTAL && !offsetInLayoutDirection)) {
            view.offsetTopAndBottom(offset);
        } else {
            view.offsetLeftAndRight(offset);
        }
    }

    private void layoutView(View view) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        int left = getPaddingLeft() + currentLeft;
        int top = getPaddingTop() + currentTop;
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
}
