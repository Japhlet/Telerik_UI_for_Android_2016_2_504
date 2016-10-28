package com.telerik.widget.list;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link com.telerik.widget.list.ListViewBehavior} that can be used to allow the end user
 * to refresh a list of items by swiping from the top of the list.
 */
public class SwipeRefreshBehavior extends ListViewBehavior {

    private RadListView owner;
    private SwipeRefreshIndicator swipeRefreshIndicator;
    private boolean isAttached = false;
    private boolean isLongPress = false;
    private boolean isRefreshing = false;
    private List<SwipeRefreshListener> listeners = new ArrayList<SwipeRefreshListener>();
    private RefreshListener refreshListener;

    /**
     * Gets the instance of {@link android.support.v4.widget.SwipeRefreshLayout} that is used by
     * this behavior.
     *
     * Note that this layout will be initialized when the behavior is attached to {@link com.telerik.widget.list.RadListView}.
     * If you need to access it before that you can manually initialize it with {@link #init(android.content.Context)}.
     *
     * @return the swipe refresh layout
     */
    public SwipeRefreshLayout swipeRefresh() {
        return swipeRefreshIndicator;
    }

    /**
     * Adds a listener to be called when items refresh is requested.
     *
     * @param listener the new listener.
     */
    public void addListener(SwipeRefreshListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener that is called when items refresh is requested.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(SwipeRefreshListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * A method that is used to notify the behavior that the refresh is complete.
     * This will hide the loading indicator.
     *
     * @param scrollToStart a boolean value which defines whether the list should be
     *                      automatically scrolled to top to display the
     *                      refreshed list from the beginning.
     */
    public void endRefresh(boolean scrollToStart) {
        if(!isRefreshing) {
            return;
        }
        isRefreshing = false;
        if(owner == null || owner.getAdapter() == null) {
            return;
        }
        ListViewAdapter adapter = (ListViewAdapter) owner.getAdapter();
        adapter.removeRefreshListener(refreshListener);
        swipeRefreshIndicator.setRefreshing(false);
        if(scrollToStart) {
            owner.scrollToStart();
        }
    }

    /**
     * Method that can be used to force the initialization of the {@link #swipeRefresh()}
     * before the behavior is attached to {@link com.telerik.widget.list.RadListView} instance.
     *
     * @param context context to be used
     */
    public void init(Context context) {
        swipeRefreshIndicator = new SwipeRefreshIndicator(context);
        refreshListener = new RefreshListener() {
            @Override
            public void onRefreshFinished() {
                endRefresh(true);
            }
        };
    }

    @Override
    public boolean isInProgress() {
        return isRefreshing;
    }

    @Override
    public void onAttached(RadListView listView) {
        attachIndicator(listView);
    }

    @Override
    public void onDetached(RadListView listView) {
        detachIndicator(listView);
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        isLongPress = true;
    }

    @Override
    public boolean onActionUpOrCancel(boolean isCanceled) {
        isLongPress = false;
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(isAttached) {
            return;
        }
        attachIndicator(owner);
    }

    @Override
    protected RadListView owner() {
        if(this.owner == null) {
            throw new UnsupportedOperationException("Behavior is not attached to RadListView. Use RadListView's addBehavior method to attach it.");
        }
        return owner;
    }

    /**
     * A method that initiates refresh.
     */
    protected void startRefresh() {
        if(isRefreshing) {
            return;
        }
        if(owner().getAdapter() == null) {
            return;
        }
        isRefreshing = true;
        ListViewAdapter adapter = (ListViewAdapter) owner.getAdapter();
        adapter.addRefreshListener(refreshListener);
        for(SwipeRefreshListener listener : listeners) {
            listener.onRefreshRequested();
        }
        adapter.notifySwipeExecuteFinished();
    }

    protected void insertRefreshLayout(RadListView listView, SwipeRefreshLayout swipeRefreshIndicator) {

        ViewGroup viewGroup = (ViewGroup)listView.getParent();
        int listViewIndex = viewGroup.indexOfChild(listView);

        viewGroup.removeViewAt(listViewIndex);
        swipeRefreshIndicator.addView(listView);
        viewGroup.addView(swipeRefreshIndicator, listViewIndex);

    }

    private void attachIndicator(RadListView listView) {
        if(isAttached) {
            return;
        }

        this.owner = listView;

        if(listView.getParent() == null) {
            return;
        }

        if(swipeRefreshIndicator == null) {
            init(listView.getContext());
        }

        swipeRefreshIndicator.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startRefresh();
            }
        });

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        swipeRefreshIndicator.setLayoutParams(params);

        insertRefreshLayout(listView, swipeRefreshIndicator);

        isAttached = true;
    }

    private void detachIndicator(RadListView listView) {
        if(!isAttached) {
            return;
        }
        if(swipeRefreshIndicator.isRefreshing()) {
            swipeRefreshIndicator.hasToBeDetached = true;
            swipeRefreshIndicator.setRefreshing(false);
        } else {
            swipeRefreshIndicator.detach();
        }
    }

    class SwipeRefreshIndicator extends SwipeRefreshLayout {
        public SwipeRefreshIndicator(Context context) {
            super(context);
        }

        boolean hasToBeDetached = false;

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            if(hasToBeDetached) {
                detach();
                hasToBeDetached = false;
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if(isLongPress) {
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if(isLongPress) {
                return false;
            }
            return super.onTouchEvent(ev);
        }

        void detach() {

            final ViewGroup parent = (ViewGroup)this.getParent();
            int swipeIndicatorIndex = parent.indexOfChild(this);
            int listViewIndex = this.indexOfChild(owner);

            if(swipeIndicatorIndex == -1 || listViewIndex == -1) {
                return;
            }

            parent.removeViewAt(swipeIndicatorIndex);
            this.removeViewAt(listViewIndex);
            parent.addView(owner, swipeIndicatorIndex);

            setOnRefreshListener(null);
            isAttached = false;
            owner = null;
        }
    }

    static interface RefreshListener {
        void onRefreshFinished();
    }

    /**
     * Interface definition for a callback to be invoked when a refresh is requested.
     */
    public static interface SwipeRefreshListener {

        /**
         * Called when refresh is requested.
         */
        void onRefreshRequested();
    }
}
