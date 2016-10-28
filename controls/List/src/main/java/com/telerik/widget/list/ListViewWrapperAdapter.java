package com.telerik.widget.list;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.telerik.android.data.DataItem;
import com.telerik.android.data.SelectionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a wrapper for the actual adapter in order to add additional view and features
 * without compromising the item count of the actual items.
 */
class ListViewWrapperAdapter extends ListViewAdapter {

    private int swipePosition = INVALID_ID;
    private int swipeElementWidth = INVALID_ID;
    private int swipeElementHeight = INVALID_ID;

    private int listViewHeight;
    private int listViewWidth;

    ListViewAdapter adapter;
    CollapsibleGroupsBehavior collapsibleGroupsBehavior;

    private int remainingItemsToLoad;
    private boolean isLoadAutomatic;

    private HashMap<RecyclerView.AdapterDataObserver, WrappedDataObserver> dataObservers =
            new HashMap<RecyclerView.AdapterDataObserver, WrappedDataObserver>();

    private View headerView;
    private View footerView;
    private View topIndicatorView;
    private View bottomIndicatorView;
    private View emptyView;
    private List<RadListView.IsEmptyChangedListener> isEmptyChangedListeners =
            new ArrayList<RadListView.IsEmptyChangedListener>();

    ListViewWrapperAdapter(ListViewAdapter adapter) {
        super(adapter.getItems());
        this.adapter = adapter;
    }

    ListViewWrapperAdapter() {
        super(null);
    }

    void addIsEmptyChangedListener(RadListView.IsEmptyChangedListener listener) {
        this.isEmptyChangedListeners.add(listener);
    }

    void removeIsEmptyChangedListener(RadListView.IsEmptyChangedListener listener) {
        this.isEmptyChangedListeners.remove(listener);
    }

    void setEmptyView(View emptyView) {
        if(this.emptyView == emptyView) {
            return;
        }
        this.emptyView = emptyView;
        notifyDataSetChanged();
    }

    void setTopIndicatorView(View topIndicatorView) {
        if(this.topIndicatorView == topIndicatorView) {
            return;
        }
        this.topIndicatorView = topIndicatorView;
        notifyDataSetChanged();
    }

    int getSpanSize(int position, int max) {
        int originalPosition = getPositionInOriginalAdapter(position);
        if(originalPosition < 0 || originalPosition >= adapter.getItemCount()) {
            return max;
        }
        if(adapter instanceof ListViewDataSourceAdapter) {
            ListViewDataSourceAdapter dataSourceAdapter = (ListViewDataSourceAdapter)adapter;
            DataItem dataItem = dataSourceAdapter.getDataItem(originalPosition);
            if(dataItem.groupKey() != null) {
                return max;
            }
        }
        return 1;
    }

    int getPositionInOriginalAdapter(int positionInWrapperAdapter) {
        return positionInWrapperAdapter - getTopViewsCount();
    }

    int getPositionInWrapperAdapter(int positionInOriginalAdapter) {
        return positionInOriginalAdapter + getTopViewsCount();
    }

    private void setIsLoadAutomatic(boolean isLoadAutomatic) {
        this.isLoadAutomatic = isLoadAutomatic;
    }

    private void setRemainingItemsToLoad(int remainingItemsToLoad) {
        this.remainingItemsToLoad = remainingItemsToLoad;
    }

    void setBottomIndicatorView(View bottomIndicatorView) {
        if(this.bottomIndicatorView == bottomIndicatorView) {
            return;
        }
        this.bottomIndicatorView = bottomIndicatorView;
        notifyDataSetChanged();
    }

    @Override
    public void addLoadingListener(LoadOnDemandBehavior.LoadingListener listener) {
        adapter.addLoadingListener(listener);
    }

    @Override
    public void removeLoadingListener(LoadOnDemandBehavior.LoadingListener listener) {
        adapter.removeLoadingListener(listener);
    }

    @Override
    public void addRefreshListener(SwipeRefreshBehavior.RefreshListener listener) {
        adapter.addRefreshListener(listener);
    }

    @Override
    public void removeRefreshListener(SwipeRefreshBehavior.RefreshListener listener) {
        adapter.removeRefreshListener(listener);
    }

    @Override
    public void addSwipeExecuteDismissedListener(SwipeExecuteBehavior.SwipeExecuteDismissedListener listener) {
        adapter.addSwipeExecuteDismissedListener(listener);
    }

    @Override
    public void removeSwipeExecuteDismissedListener(SwipeExecuteBehavior.SwipeExecuteDismissedListener listener) {
        adapter.removeSwipeExecuteDismissedListener(listener);
    }

    void setHeader(View header) {
        if(this.headerView == header) {
            return;
        }
        this.headerView = header;
        notifyDataSetChanged();
    }

    void setFooter(View footer) {
        if(this.footerView == footer) {
            return;
        }
        this.footerView = footer;
        notifyDataSetChanged();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);

        WrappedDataObserver dataObserver = new WrappedDataObserver(observer);
        dataObservers.put(observer, dataObserver);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver);
        }
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        WrappedDataObserver dataObserver = null;
        if(dataObservers.containsKey(observer)) {
            dataObserver = dataObservers.get(observer);
            dataObservers.remove(observer);
        }
        if (adapter != null && dataObserver != null) {
            adapter.unregisterAdapterDataObserver(dataObserver);
        }
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int i) {

        if(getItemViewType(i) == ITEM_VIEW_TYPE_COLLAPSED) {
            return;
        }
        if (getItemViewType(i) == ITEM_VIEW_TYPE_EMPTY_CONTENT) {
            bindEmptyContent(holder);
            return;
        }

        if(i < getTopViewsCount()) {
            return;
        }

        int adjustedPosition = i - getTopViewsCount();

        if(adapter != null) {
            int itemsCount = adapter.getItemCount();
            if(adjustedPosition < itemsCount) {
                boolean isSelected = selectionService().isItemSelected(adapter.getItem(adjustedPosition));
                holder.itemView.setSelected(isSelected);
                if(adjustedPosition == swipePosition) {
                    bindSwipeContent(holder, adjustedPosition);
                } else {
                    if(isLoadAutomatic && adjustedPosition > itemsCount - remainingItemsToLoad) {
                        for(LoadOnDemandBehavior.LoadingListener listener : adapter.loadingListeners) {
                            listener.onLoadingRequested();
                        }
                    }
                    adapter.onBindViewHolder(holder, adjustedPosition);
                    if(collapsibleGroupsBehavior != null) {
                        collapsibleGroupsBehavior.handleIsCollapsed(holder.itemView, adjustedPosition);
                    }
                }
            }
        }
    }

    void setCollapsibleBehavior(CollapsibleGroupsBehavior behavior) {
        collapsibleGroupsBehavior = behavior;
    }

    void updateOnDemandSettings(ViewGroup indicator, int remainingItemsToLoad, boolean isLoadAutomatic) {
        setRemainingItemsToLoad(remainingItemsToLoad);
        setIsLoadAutomatic(isLoadAutomatic);
        setBottomIndicatorView(indicator);
        notifyDataSetChanged();
    }

    private void bindEmptyContent(final ListViewHolder holder) {
        final int emptyViewVisibility = adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE;
        boolean emptyViewShouldBeVisible = emptyViewVisibility == View.VISIBLE;

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if(layoutParams == null) {
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        layoutParams.width = emptyViewShouldBeVisible ? listViewWidth : 0;
        layoutParams.height = emptyViewShouldBeVisible ? listViewHeight : 0;
        holder.itemView.setLayoutParams(layoutParams);

        if(holder.itemView.getVisibility() == emptyViewVisibility) {
            return;
        }

        for(RadListView.IsEmptyChangedListener listener : isEmptyChangedListeners) {
            listener.onChanged(emptyViewShouldBeVisible);
        }

        if(emptyViewShouldBeVisible){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    holder.itemView.setVisibility(emptyViewVisibility);
                }
            });
        } else {
            holder.itemView.setVisibility(emptyViewVisibility);
        }
    }

    private void bindSwipeContent(ListViewHolder holder, int position) {
        adapter.onBindSwipeContentHolder(holder, position);

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        if(params == null) {
            if(swipeElementHeight != 0) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, swipeElementHeight);
            } else {
                params = new ViewGroup.LayoutParams(swipeElementWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        } else {
            if(swipeElementHeight != 0) {
                params.height = swipeElementHeight;
            } else {
                params.width = swipeElementWidth;
            }
        }
        holder.itemView.setLayoutParams(params);
    }

    @Override
    public SelectionService selectionService() {
        return adapter.selectionService();
    }

    @Override
    public int getItemViewType(int position) {
        int adjustedPosition = position;
        if(headerView != null && adjustedPosition == 0) {
            return ITEM_VIEW_TYPE_HEADER;
        }
        adjustedPosition -= (headerView != null ? 1 : 0);
        if(topIndicatorView != null && adjustedPosition == 0) {
            return ITEM_VIEW_TYPE_TOP_INDICATOR;
        }
        adjustedPosition -= (topIndicatorView != null ? 1 : 0);
        if(adjustedPosition == 0 && emptyViewIsVisible()) {
            return ITEM_VIEW_TYPE_EMPTY_CONTENT;
        }
        adjustedPosition -= (emptyViewIsVisible() ? 1 : 0);
        if(adapter != null) {
            if(adjustedPosition < adapter.getItemCount()) {
                if(adjustedPosition == swipePosition) {
                    return ITEM_VIEW_TYPE_SWIPE_CONTENT;
                }
                if(collapsibleGroupsBehavior != null && adapter instanceof ListViewDataSourceAdapter) {
                    int groupHeader = ((ListViewDataSourceAdapter)adapter).getHeaderPosition(adjustedPosition);
                    if (groupHeader != adjustedPosition && collapsibleGroupsBehavior.isGroupCollapsed(groupHeader)) {
                        return ITEM_VIEW_TYPE_COLLAPSED;
                    }
                }
                return adapter.getItemViewType(adjustedPosition);
            }
            adjustedPosition -= adapter.getItemCount();
        }
        if(bottomIndicatorView != null && adjustedPosition == 0) {
            return ITEM_VIEW_TYPE_BOTTOM_INDICATOR;
        }
        return ITEM_VIEW_TYPE_FOOTER;
    }

    @Override
    public int getPosition(long id) {
        int originalPosition = adapter.getPosition(id);
        if(originalPosition == INVALID_ID) {
            return INVALID_ID;
        }
        return originalPosition + getTopViewsCount();
    }

    @Override
    public int getPosition(Object searchItem) {
        int originalPosition = adapter.getPosition(searchItem);
        if(originalPosition == INVALID_ID) {
            return INVALID_ID;
        }
        return originalPosition + getTopViewsCount();
    }

    @Override
    public long getItemId(int position) {
        if(adapter == null || adapter.getItemCount() == 0) {
            return INVALID_ID;
        }
        int adjustedPosition = position - getTopViewsCount();
        if(0 <= adjustedPosition && adjustedPosition < adapter.getItemCount()) {
            return adapter.getItemId(adjustedPosition);
        }
        return INVALID_ID;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_HEADER) {
            return new ListViewHolder(headerView);
        }

        if(viewType == ITEM_VIEW_TYPE_FOOTER) {
            return new ListViewHolder(footerView);
        }

        if(viewType == ITEM_VIEW_TYPE_TOP_INDICATOR) {
            return new ListViewHolder(topIndicatorView);
        }

        if(viewType == ITEM_VIEW_TYPE_BOTTOM_INDICATOR) {
            return new ListViewHolder(bottomIndicatorView);
        }

        if(viewType == ITEM_VIEW_TYPE_SWIPE_CONTENT) {
            return adapter.onCreateSwipeContentHolder(viewGroup);
        }

        if(viewType == ITEM_VIEW_TYPE_EMPTY_CONTENT) {
            return new ListViewHolder(createEmptyContentLayout());
        }

        if(viewType == ITEM_VIEW_TYPE_COLLAPSED) {
            View v = new View(viewGroup.getContext());
            return new CollapsedViewHolder(v);
        }
        return adapter.onCreateViewHolder(viewGroup, viewType);
    }

    private View createEmptyContentLayout() {
        FrameLayout frameLayout = new FrameLayout(emptyView.getContext());
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(params);

        if(emptyView.getParent() != null) {
            ViewGroup parent = (ViewGroup)emptyView.getParent();
            frameLayout.setVisibility(parent.getVisibility());
            parent.removeView(emptyView);
        }
        frameLayout.addView(emptyView);
        return frameLayout;
    }

    @Override
    public List getItems() {
        if(adapter != null) {
            return adapter.getItems();
        }
        return new ArrayList();
    }

    @Override
    public boolean reorderItem(int oldPosition, int newPosition) {
        if(adapter != null) {
            return adapter.reorderItem(oldPosition, newPosition);
        }
        return false;
    }

    @Override
    public int getItemCount() {
        int additionalItemCount = getTopViewsCount() + getBottomViewsCount();
        if(emptyViewIsVisible()) {
            additionalItemCount++;
        }
        return adapter != null ? adapter.getItemCount() + additionalItemCount : additionalItemCount;
    }

    private boolean emptyViewIsVisible() {
        if((adapter == null || adapter.getItemCount() == 0) && emptyView != null) {
            return true;
        }
        return false;
    }

    void onMeasure(int width, int height) {

        listViewWidth = width;
        listViewHeight = height;

        if(adapter == null || adapter.getItemCount() == 0) {
            notifyDataSetChanged();
        }
    }

    public void handleSwipeStart(int swipePosition, int swipeElementWidth, int swipeElementHeight) {
        this.swipePosition = swipePosition;
        this.swipeElementWidth = swipeElementWidth;
        this.swipeElementHeight = swipeElementHeight;
    }

    void handleSwipeEnd() {
        this.swipePosition = INVALID_ID;
    }

    int getTopViewsCount() {
        return (headerView != null ? 1 : 0) + (topIndicatorView != null ? 1 : 0);
    }

    int getBottomViewsCount() {
        return (footerView != null ? 1 : 0) + (bottomIndicatorView != null ? 1 : 0);
    }

    class WrappedDataObserver extends RecyclerView.AdapterDataObserver {

        RecyclerView.AdapterDataObserver observer;

        public WrappedDataObserver(RecyclerView.AdapterDataObserver observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged() {
            this.observer.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            this.observer.onItemRangeChanged(positionStart + getTopViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            this.observer.onItemRangeChanged(positionStart + getTopViewsCount(), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if(adapter != null && adapter.getItemCount() == itemCount) {
                notifyDataSetChanged();
            }
            this.observer.onItemRangeInserted(positionStart + getTopViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if(adapter != null) {
                adapter.recentAdapterChange = true;
                if(adapter.getItemCount() == 0) {
                    notifyDataSetChanged();
                }
            }
            this.observer.onItemRangeRemoved(positionStart + getTopViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            this.observer.onItemRangeMoved(fromPosition + getTopViewsCount(), toPosition + getTopViewsCount(), itemCount);
        }
    }
}
