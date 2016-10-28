package com.telerik.widget.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.android.common.licensing.TelerikLicense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a control that displays a list of scrollable items.
 */
public class RadListView extends RecyclerView {

    static final int SCROLL_DIRECTION_NONE = 0;
    static final int SCROLL_DIRECTION_LEFT = 1;
    static final int SCROLL_DIRECTION_UP = 2;
    static final int SCROLL_DIRECTION_RIGHT = 3;
    static final int SCROLL_DIRECTION_DOWN = 4;

    private ListViewGestureListener gestureListener;
    private List<ListViewBehavior> behaviors;
    private ListViewWrapperAdapter wrapperAdapter;

    private Runnable actionOnPressed;
    private View pressedView;

    private int stateToSave;

    private List<ItemClickListener> itemClickListeners = new ArrayList<ItemClickListener>();
    private List<IsEmptyChangedListener> isEmptyChangedListeners = new ArrayList<IsEmptyChangedListener>();

    private View headerView;
    private View footerView;

    private View emptyContent;
    private boolean isEmptyContentEnabled;

    /**
     * Creates an instance of the {@link com.telerik.widget.list.RadListView} class.
     *
     * @param context the context to be used
     */
    public RadListView(Context context) {
        this(context, null);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.list.RadListView} class.
     *
     * @param context the context to be used
     * @param attrs   the attributes
     */
    public RadListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.list.RadListView} class.
     *
     * @param context  the context to be used
     * @param attrs    the attributes
     * @param defStyle the default style
     */
    public RadListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.gestureListener = new ListViewGestureListener(context);

        this.behaviors = new ArrayList<ListViewBehavior>();

        this.actionOnPressed = new Runnable() {
            @Override
            public void run() {
                if (pressedView != null) {
                    pressedView.setPressed(true);
                }
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        this.setLayoutManager(layoutManager);

        this.setAdapter(null);
    }

    /**
     * Adds a listener to be called when an item is clicked.
     *
     * @param listener the new listener.
     */
    public void addItemClickListener(ItemClickListener listener) {
        this.itemClickListeners.add(listener);
    }

    /**
     * Removes a listener that is called when an item is clicked.
     *
     * @param listener the listener to remove.
     */
    public void removeItemClickListener(ItemClickListener listener) {
        this.itemClickListeners.remove(listener);
    }

    /**
     * Adds a listener to be called when the items count changes to or from `0`.
     *
     * @param listener the new listener.
     */
    public void addIsEmptyChangedListener(IsEmptyChangedListener listener) {
        this.isEmptyChangedListeners.add(listener);
        this.wrapperAdapter().addIsEmptyChangedListener(listener);
    }

    /**
     * Removes a listener that is called when the items count changes to or from `0`.
     *
     * @param listener the listener to remove.
     */
    public void removeIsEmptyChangedListener(IsEmptyChangedListener listener) {
        this.isEmptyChangedListeners.remove(listener);
        this.wrapperAdapter().removeIsEmptyChangedListener(listener);
    }

    /**
     * Convenience method to scroll to the beginning of the list.
     *
     * @see #scrollToEnd()
     * @see #scrollToPosition(int)
     */
    public void scrollToStart() {
        scrollToActualPosition(0);
    }

    /**
     * Convenience method to scroll to the end of the list.
     *
     * @see #scrollToStart()
     * @see #scrollToPosition(int)
     */
    public void scrollToEnd() {
        if (getAdapter() == null) {
            return;
        }
        int lastPosition = wrapperAdapter().getItemCount() - 1;
        scrollToActualPosition(lastPosition);
    }

    /**
     * Adds a {@link com.telerik.widget.list.ListViewBehavior} instance to this instance.
     * Note that, you can add only one instance of a certain behavior. For example, if you have
     * already added a {@link com.telerik.widget.list.SelectionBehavior} you can't add another
     * {@link com.telerik.widget.list.SelectionBehavior} before removing the old one.
     *
     * @param behavior the behavior that is added.
     * @see #removeBehavior(ListViewBehavior) ()
     * @see #clearBehaviors()
     */
    public void addBehavior(ListViewBehavior behavior) {

        ensureCompatible(getLayoutManager(), behavior);

        for (ListViewBehavior listViewBehavior : this.behaviors) {
            if (behavior.getClass() == listViewBehavior.getClass()) {
                throw new IllegalArgumentException("RadListView already contains a " + listViewBehavior.getClass().getSimpleName() + " instance");
            }
        }
        this.addListenersForBehavior(behavior);
        this.behaviors.add(behavior);
        this.sortBehaviors();
        behavior.onAttached(this);
    }

    private void sortBehaviors() {
        // The sorting logic is as follows:
        // If there is a ItemReorderBehavior it should be first.
        // If there is a SwipeExecuteBehavior it should be last.
        // These rule guarantee that the SelectionBehavior (if exists) will be
        // between them and will be able to correctly decide whether to handle the
        // gestures which are common for these behaviors too.
        Collections.sort(behaviors, new Comparator<ListViewBehavior>() {
            @Override
            public int compare(ListViewBehavior lhs, ListViewBehavior rhs) {
                if (lhs instanceof ItemReorderBehavior)
                    return -1;
                if (lhs instanceof SwipeExecuteBehavior) {
                    return 1;
                }
                if (rhs instanceof ItemReorderBehavior) {
                    return 1;
                }
                if (rhs instanceof SwipeExecuteBehavior) {
                    return -1;
                }
                return 0;
            }
        });
    }

    private void ensureCompatible(LayoutManager layoutManager, ListViewBehavior behavior) {
        if ((layoutManager instanceof SlideLayoutManager) && (behavior instanceof ItemReorderBehavior)) {
            throw new IllegalArgumentException("SlideLayoutManager currently doesn't support ItemReorderBehavior.");
        }
        if ((layoutManager instanceof DeckOfCardsLayoutManager) && (behavior instanceof ItemReorderBehavior)) {
            throw new IllegalArgumentException("DeckOfCardsLayoutManager currently doesn't support ItemReorderBehavior.");
        }
        if ((layoutManager instanceof DeckOfCardsLayoutManager) && (behavior instanceof SwipeRefreshBehavior)) {
            throw new IllegalArgumentException("DeckOfCardsLayoutManager currently doesn't support SwipeRefreshBehavior.");
        }
        if ((layoutManager instanceof WrapLayoutManager) && (behavior instanceof ItemReorderBehavior)) {
            throw new IllegalArgumentException("WrapLayoutManager currently doesn't support ItemReorderBehavior.");
        }
        if ((layoutManager instanceof WrapLayoutManager) && (behavior instanceof SwipeExecuteBehavior)) {
            throw new IllegalArgumentException("WrapLayoutManager currently doesn't support SwipeExecuteBehavior.");
        }
    }

    private void addListenersForBehavior(ListViewBehavior behavior) {
        if (behavior instanceof SelectionBehavior) {
            SelectionBehavior selectionBehavior = (SelectionBehavior) behavior;
            for (ListViewBehavior listViewBehavior : behaviors()) {
                if (listViewBehavior instanceof ItemReorderBehavior) {
                    ((ItemReorderBehavior) listViewBehavior).addListener(selectionBehavior);
                }
                if (listViewBehavior instanceof SwipeExecuteBehavior) {
                    ((SwipeExecuteBehavior) listViewBehavior).addListener(selectionBehavior);
                }
            }
        }
        if (behavior instanceof ItemReorderBehavior) {
            ItemReorderBehavior itemReorderBehavior = (ItemReorderBehavior) behavior;
            for (ListViewBehavior listViewBehavior : behaviors()) {
                if (listViewBehavior instanceof SelectionBehavior) {
                    itemReorderBehavior.addListener((SelectionBehavior) listViewBehavior);
                }
            }
        }
        if (behavior instanceof SwipeExecuteBehavior) {
            SwipeExecuteBehavior swipeExecuteBehavior = (SwipeExecuteBehavior) behavior;
            for (ListViewBehavior listViewBehavior : behaviors()) {
                if (listViewBehavior instanceof SelectionBehavior) {
                    swipeExecuteBehavior.addListener((SelectionBehavior) listViewBehavior);
                }
            }
        }
    }

    private void removeListenersForBehavior(ListViewBehavior behavior) {
        if (behavior instanceof SelectionBehavior) {
            SelectionBehavior selectionBehavior = (SelectionBehavior) behavior;
            for (ListViewBehavior listViewBehavior : behaviors()) {
                if (listViewBehavior instanceof ItemReorderBehavior) {
                    ((ItemReorderBehavior) listViewBehavior).removeListener(selectionBehavior);
                }
                if (listViewBehavior instanceof SwipeExecuteBehavior) {
                    ((SwipeExecuteBehavior) listViewBehavior).removeListener(selectionBehavior);
                }
            }
        }
        if (behavior instanceof ItemReorderBehavior) {
            ItemReorderBehavior itemReorderBehavior = (ItemReorderBehavior) behavior;
            for (ListViewBehavior listViewBehavior : behaviors()) {
                if (listViewBehavior instanceof SelectionBehavior) {
                    itemReorderBehavior.removeListener((SelectionBehavior) listViewBehavior);
                }
            }
        }
        if (behavior instanceof SwipeExecuteBehavior) {
            SwipeExecuteBehavior swipeExecuteBehavior = (SwipeExecuteBehavior) behavior;
            for (ListViewBehavior listViewBehavior : behaviors()) {
                if (listViewBehavior instanceof SelectionBehavior) {
                    swipeExecuteBehavior.removeListener((SelectionBehavior) listViewBehavior);
                }
            }
        }
    }

    /**
     * Removes a {@link com.telerik.widget.list.ListViewBehavior} instance from this instance.
     *
     * @param behavior the behavior that is removed.
     * @see #addBehavior(ListViewBehavior)
     * @see #clearBehaviors()
     */
    public void removeBehavior(ListViewBehavior behavior) {
        this.behaviors.remove(behavior);
        this.removeListenersForBehavior(behavior);
        behavior.onDetached(this);
    }

    /**
     * Clears all {@link com.telerik.widget.list.ListViewBehavior} that have been added to this instance.
     *
     * @see #addBehavior(ListViewBehavior)
     * @see #removeBehavior(ListViewBehavior)
     */
    public void clearBehaviors() {
        for (ListViewBehavior behavior : this.behaviors) {
            this.removeListenersForBehavior(behavior);
            behavior.onDetached(this);
        }
        this.behaviors.clear();
    }

    /**
     * Sets a new {@link com.telerik.widget.list.ListViewGestureListener} that will be responsible
     * for handling of the user gestures and notifying the currently added behaviors.
     *
     * @see #getGestureListener()
     */
    public void setGestureListener(ListViewGestureListener gestureListener) {
        this.gestureListener = gestureListener;
    }

    /**
     * Gets the current {@link com.telerik.widget.list.ListViewGestureListener} that handles
     * the user gestures and notifies the currently added behaviors.
     *
     * @see #setGestureListener(ListViewGestureListener)
     */
    public ListViewGestureListener getGestureListener() {
        return gestureListener;
    }

    public void setEmptyContent(View emptyContent) {
        if (this.emptyContent != null) {
            throw new UnsupportedOperationException("RadListView already has an empty content.");
        }

        this.emptyContent = emptyContent;
        this.isEmptyContentEnabled = true;
        this.wrapperAdapter.setEmptyView(this.emptyContent);
    }

    public View getEmptyContent() {
        if (this.emptyContent == null) {
            this.emptyContent = createDefaultEmptyContentView();
        }
        return this.emptyContent;
    }

    public void setEmptyContentEnabled(boolean emptyContentEnabled) {
        this.isEmptyContentEnabled = emptyContentEnabled;
        if (emptyContentEnabled) {
            this.wrapperAdapter.setEmptyView(getEmptyContent());
        } else {
            this.wrapperAdapter.setEmptyView(null);
        }
    }

    public boolean isEmptyContentEnabled() {
        return isEmptyContentEnabled;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (TelerikLicense.licenseRequired()) {
            LicensingProvider.verify(this.getContext());
        }
    }

    private View createDefaultEmptyContentView() {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        View emptyContent = inflater.inflate(R.layout.simple_empty_content, this, false);
        return emptyContent;
    }

    /**
     * Sets a {@link android.view.View} that will be displayed in the beginning of the list of the
     * items that are provided by the {@link com.telerik.widget.list.ListViewAdapter}.
     * Please note that you may not be able to change the header once it is set.
     * Instead, you can get the view with {@link #getHeaderView()} and change its appearance if necessary.
     *
     * @see #getHeaderView()
     */
    public void setHeaderView(View headerView) {

        if (this.headerView != null && headerView != null) {
            throw new UnsupportedOperationException("RadListView already has a headerView. Use setHeaderView(null) to remove the old header first.");
        }

        this.headerView = headerView;

        if (this.wrapperAdapter != null) {
            this.wrapperAdapter.setHeader(headerView);
        }
    }

    /**
     * Gets the {@link android.view.View} that is displayed in the beginning of the list of the
     * items that are provided by the {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @see #setHeaderView(android.view.View)
     */
    public View getHeaderView() {
        return this.headerView;
    }

    /**
     * Sets a {@link android.view.View} that will be displayed on the bottom of the list of the
     * items that are provided by the {@link com.telerik.widget.list.ListViewAdapter}.
     * Please note that you may not be able to change the footer once it is set.
     * Instead, you can get the view with {@link #getFooterView()} and change its appearance if necessary.
     *
     * @see #getFooterView()
     */
    public void setFooterView(View footerView) {

        if (this.footerView != null && footerView != null) {
            throw new UnsupportedOperationException("RadListView already has a footerView. Use setFooterView(null) to remove the old footer first.");
        }

        this.footerView = footerView;

        if (this.wrapperAdapter != null) {
            this.wrapperAdapter.setFooter(footerView);
        }
    }

    /**
     * Gets the {@link android.view.View} that is displayed on the bottom of the list of the
     * items that are provided by the {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @see #setFooterView(android.view.View)
     */
    public View getFooterView() {
        return this.footerView;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {

        for (ListViewBehavior behavior : behaviors) {
            ensureCompatible(layout, behavior);
        }

        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layout;
            if (gridLayoutManager.getSpanSizeLookup() instanceof GridLayoutManager.DefaultSpanSizeLookup) {
                GridLayoutManager.SpanSizeLookup lookup = new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return wrapperAdapter().getSpanSize(position, gridLayoutManager.getSpanCount());
                    }
                };
                gridLayoutManager.setSpanSizeLookup(lookup);
            }
        }
        super.setLayoutManager(layout);
    }

    @Override
    public void scrollToPosition(int position) {
        if (getAdapter() == null) {
            return;
        }
        int maxValue = getAdapter().getItemCount() - 1;
        if (position < 0 || position > maxValue) {
            throw new IndexOutOfBoundsException("position should be in the interval [0, " + maxValue + "]");
        }

        int actualPosition = wrapperAdapter.getPositionInWrapperAdapter(position);
        scrollToActualPosition(actualPosition);
    }

    @Override
    public int getChildAdapterPosition(View child) {
        int position = super.getChildAdapterPosition(child);
        if (wrapperAdapter() != null) {
            position = wrapperAdapter().getPositionInOriginalAdapter(position);
        }
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return this.gestureListener.onTouchEvent(this, e) || super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return this.gestureListener.onInterceptTouchEvent(this, e) || super.onInterceptTouchEvent(e);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.setAdapterInternal(adapter);
        super.setAdapter(this.wrapperAdapter);
    }

    @Override
    public Adapter getAdapter() {
        if (this.wrapperAdapter == null) {
            return null;
        }
        return this.wrapperAdapter.adapter;
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        this.setAdapterInternal(adapter);
        super.swapAdapter(this.wrapperAdapter, removeAndRecycleExistingViews);

    }

    @Override
    public void setItemAnimator(ItemAnimator animator) {
        ItemAnimator oldAnimator = getItemAnimator();
        if (oldAnimator instanceof ListViewItemAnimator) {
            ((ListViewItemAnimator) oldAnimator).onDetached(this);
        }

        super.setItemAnimator(animator);

        if (animator instanceof ListViewItemAnimator) {
            ((ListViewItemAnimator) animator).onAttached(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        for (ListViewBehavior behavior : behaviors) {
            behavior.onLayout(changed, l, t, r, b);
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        int width = View.MeasureSpec.getSize(widthSpec);
        int height = View.MeasureSpec.getSize(heightSpec);
        this.wrapperAdapter().onMeasure(width, height);

        ItemAnimator itemAnimator = getItemAnimator();
        if (itemAnimator != null && itemAnimator instanceof ListViewItemAnimator) {
            ((ListViewItemAnimator) itemAnimator).onMeasure();
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        for (ListViewBehavior behavior : behaviors) {
            behavior.onScrolled(dx, dy);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for (ListViewBehavior behavior : behaviors) {
            behavior.onDispatchDraw(canvas);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("stateToSave", this.stateToSave);

        for (ListViewBehavior behavior : behaviors()) {
            behavior.onSaveInstanceState(bundle);
        }

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            this.stateToSave = bundle.getInt("stateToSave");

            for (ListViewBehavior behavior : behaviors()) {
                behavior.onRestoreInstanceState(bundle);
            }

            state = bundle.getParcelable("instanceState");
        }

        super.onRestoreInstanceState(state);
    }

    List<ListViewBehavior> behaviors() {
        return behaviors;
    }

    boolean canScroll(int direction) {
        switch (direction) {
            case SCROLL_DIRECTION_UP:
                return canScrollUp();
            case SCROLL_DIRECTION_DOWN:
                return canScrollDown();
            case SCROLL_DIRECTION_LEFT:
                return canScrollLeft();
            case SCROLL_DIRECTION_RIGHT:
                return canScrollRight();
        }
        return false;
    }

    void notifyOnDown(final MotionEvent motionEvent) {
        View view = findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (view != null) {
            pressedView = view;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                trySetHotspot(pressedView, motionEvent);
            }
            pressedView.postDelayed(actionOnPressed, 45);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void trySetHotspot(View view, MotionEvent motionEvent) {
        View defaultTextView = view.findViewById(R.id.text1);
        if (defaultTextView == null) {
            return;
        }

        float x = motionEvent.getX() - view.getLeft();
        float y = motionEvent.getY() - view.getTop();

        defaultTextView.getBackground().setHotspot(x, y);
    }

    void notifyMove() {
        if (pressedView != null) {
            pressedView.removeCallbacks(actionOnPressed);
            pressedView.setPressed(false);
            pressedView = null;
        }
    }

    void notifyOnUpOrCancel(boolean isCanceled) {
        if (pressedView != null) {
            pressedView.removeCallbacks(actionOnPressed);
            pressedView.setPressed(false);
            pressedView = null;
        }
    }

    void notifyOnTapUp(MotionEvent motionEvent) {
        View view = findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int position = getChildAdapterPosition(view);
        if (getAdapter() == null || position < 0 || position >= getAdapter().getItemCount()) {
            return;
        }
        for (ItemClickListener listener : this.itemClickListeners) {
            listener.onItemClick(position, motionEvent);
        }
        if (getLayoutManager() instanceof SlideLayoutManager) {
            ((SlideLayoutManager) getLayoutManager()).onTap(position);
        }
    }

    void notifyOnLongPress(MotionEvent motionEvent) {
        View view = findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int position = getChildAdapterPosition(view);
        if (getAdapter() == null || position < 0 || position >= getAdapter().getItemCount()) {
            return;
        }
        for (ItemClickListener listener : this.itemClickListeners) {
            listener.onItemLongClick(position, motionEvent);
        }
    }

    ListViewWrapperAdapter wrapperAdapter() {
        return this.wrapperAdapter;
    }

    private boolean canScrollLeft() {
        int horizontalScrollOffset = computeHorizontalScrollOffset();
        return horizontalScrollOffset > 0;
    }

    private boolean canScrollUp() {
        int verticalScrollOffset = computeVerticalScrollOffset();
        if (getLayoutManager() instanceof DeckOfCardsLayoutManager) {
            int verticalScrollExtent = computeVerticalScrollExtent();
            int verticalScrollRange = computeVerticalScrollRange();
            return verticalScrollOffset + verticalScrollExtent < verticalScrollRange;
        }
        return verticalScrollOffset > 0;
    }

    private boolean canScrollRight() {
        int horizontalScrollExtent = computeHorizontalScrollExtent();
        int horizontalScrollOffset = computeHorizontalScrollOffset();
        int horizontalScrollRange = computeHorizontalScrollRange();
        return horizontalScrollOffset + horizontalScrollExtent < horizontalScrollRange;
    }

    private boolean canScrollDown() {
        int verticalScrollExtent = computeVerticalScrollExtent();
        int verticalScrollOffset = computeVerticalScrollOffset();
        int verticalScrollRange = computeVerticalScrollRange();
        if (getLayoutManager() instanceof DeckOfCardsLayoutManager) {
            return verticalScrollOffset > 0;
        }
        return verticalScrollOffset + verticalScrollExtent < verticalScrollRange;
    }

    private void setAdapterInternal(Adapter adapter) {
        if (adapter == null) {
            this.wrapperAdapter = new ListViewWrapperAdapter();
        } else {
            if (!(adapter instanceof ListViewAdapter)) {
                throw new IllegalArgumentException("adapter should extend ListViewAdapter");
            }
            ListViewAdapter listViewAdapter = (ListViewAdapter) adapter;
            this.wrapperAdapter = new ListViewWrapperAdapter(listViewAdapter);
        }

        if (headerView != null) {
            this.wrapperAdapter.setHeader(headerView);
        }
        if (footerView != null) {
            this.wrapperAdapter.setFooter(footerView);
        }
        if (emptyContent != null) {
            this.wrapperAdapter.setEmptyView(emptyContent);
        }
        for (IsEmptyChangedListener listener : isEmptyChangedListeners) {
            this.wrapperAdapter.addIsEmptyChangedListener(listener);
        }
        for (ListViewBehavior behavior : behaviors) {
            behavior.onAdapterChanged(this.wrapperAdapter);
        }
    }

    private void scrollToActualPosition(int position) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
        }
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
        }
        layoutManager.scrollToPosition(position);
    }

    /**
     * Interface definition for a callback to be invoked when an item is clicked or long-clicked.
     */
    public interface ItemClickListener {
        void onItemClick(int itemPosition, MotionEvent motionEvent);

        void onItemLongClick(int itemPosition, MotionEvent motionEvent);
    }

    /**
     * Interface definition for a callback to be invoked when listview's isEmpty state gets changed.
     */
    public interface IsEmptyChangedListener {
        void onChanged(boolean isEmpty);
    }
}
