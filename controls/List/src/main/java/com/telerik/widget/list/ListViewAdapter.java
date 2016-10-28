package com.telerik.widget.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.telerik.android.data.SelectionService;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for an Adapter used by {@link com.telerik.widget.list.RadListView}.
 *
 * <p>Adapters provide a binding from an app-specific data set to views that are displayed
 * within a {@link com.telerik.widget.list.RadListView}.</p>
 */
public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    protected static final int ITEM_VIEW_TYPE_HEADER = -103;
    protected static final int ITEM_VIEW_TYPE_FOOTER = -104;
    protected static final int ITEM_VIEW_TYPE_TOP_INDICATOR = -105;
    protected static final int ITEM_VIEW_TYPE_BOTTOM_INDICATOR = -106;
    protected static final int ITEM_VIEW_TYPE_SWIPE_CONTENT = -107;
    protected static final int ITEM_VIEW_TYPE_EMPTY_CONTENT = -108;
    protected static final int ITEM_VIEW_TYPE_COLLAPSED = -109;

    protected final static int INVALID_ID = -1;

    List<LoadOnDemandBehavior.LoadingListener> loadingListeners;
    boolean recentAdapterChange = false;

    private List items;
    private SelectionService selectionService;
    private List<SwipeRefreshBehavior.RefreshListener> refreshListeners;
    private List<SwipeExecuteBehavior.SwipeExecuteDismissedListener> swipeListeners;

    /**
     * Creates an instance of the {@link com.telerik.widget.list.ListViewAdapter} class.
     *
     * @param items a list of items that will be handled by this adapter
     */
    public ListViewAdapter(List items) {
        this.items = items;
        this.loadingListeners = new ArrayList<LoadOnDemandBehavior.LoadingListener>();
        this.refreshListeners = new ArrayList<SwipeRefreshBehavior.RefreshListener>();
        this.swipeListeners = new ArrayList<SwipeExecuteBehavior.SwipeExecuteDismissedListener>();

        this.selectionService = new SelectionService();

        this.setHasStableIds(true);
    }

    /**
     * Adds the specified item at the end of the items in
     * this {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @param item  the object to add.
     */
    public void add(Object item) {
        add(getItemCount(), item);
    }

    /**
     * Inserts the specified object into this {@link com.telerik.widget.list.ListViewAdapter}
     * at the specified index. The object is inserted before the current element at the
     * specified index.
     *
     * @param index the index at which to insert.
     * @param item  the object to add.
     */
    public void add(int index, Object item) {
        getItems().add(index, item);
        notifyItemInserted(index);
    }

    /**
     * Removes the first occurrence of the specified object from the list
     * of items in this {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @param item the object to remove.
     */
    public boolean remove(Object item) {
        int position = getPosition(item);
        Object removedItem = null;
        if(isPositionValid(position)) {
            removedItem = remove(position);
        }
        return removedItem != null;
    }

    /**
     * Removes the object at the specified index from the list
     * of items in this {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @param index the index of the object to remove.
     */
    public Object remove(int index) {
        Object result = getItems().remove(index);
        notifyItemRemoved(index);
        return result;
    }

    /**
     * Notify an instance of {@link com.telerik.widget.list.LoadOnDemandBehavior} if attached,
     * that the loading operation is complete.
     *
     * @see com.telerik.widget.list.LoadOnDemandBehavior
     */
    public void notifyLoadingFinished() {
        for(LoadOnDemandBehavior.LoadingListener listener : loadingListeners) {
            listener.onLoadingFinished();
        }
    }

    /**
     * Notify an instance of {@link com.telerik.widget.list.SwipeRefreshBehavior} if attached,
     * that the refresh operation is complete.
     *
     * @see com.telerik.widget.list.SwipeRefreshBehavior
     */
    public void notifyRefreshFinished() {
        for(SwipeRefreshBehavior.RefreshListener listener : refreshListeners) {
            listener.onRefreshFinished();
        }
    }

    /**
     * Determines if the item at the provided position is selectable.
     *
     * @see com.telerik.widget.list.SelectionBehavior
     */
    public boolean canSelect(int position) {
        return isPositionValid(position);
    }

    /**
     * Determines if the item at the provided position can be deselected.
     *
     * @see com.telerik.widget.list.SelectionBehavior
     */
    public boolean canDeselect(int position) {
        return canSelect(position);
    }

    /**
     * Determines if the item at the provided position can be swiped.
     *
     * @see com.telerik.widget.list.SwipeRefreshBehavior
     */
    public boolean canSwipe(int position) {
        return isPositionValid(position);
    }

    /**
     * Called when {@link com.telerik.widget.list.RadListView} needs a
     * new {@link com.telerik.widget.list.ListViewHolder} to present any swipe content.
     * <p>
     * Note that the default holder that is created contains a
     * {@link android.widget.FrameLayout} with two children of type {@link android.widget.LinearLayout}.
     * The first is aligned to the left and the second is aligned to the right. In most cases you won't
     * need to override this method as you can simply add your desired content to each of the
     * child layouts in order to show that content when the user swipes to the right or to the left.
     * </p>
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     *
     * @see com.telerik.widget.list.SwipeExecuteBehavior
     * @see #onBindSwipeContentHolder(ListViewHolder, int)
     *
     * @return A new ListViewHolder that holds a View for swipe content.
     */
    public ListViewHolder onCreateSwipeContentHolder(ViewGroup parent) {
        RelativeLayout relativeLayout = new RelativeLayout(parent.getContext());
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout leftLayout = new LinearLayout(parent.getContext());
        RelativeLayout.LayoutParams paramsLeft =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        paramsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        leftLayout.setLayoutParams(paramsLeft);
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout rightLayout = new LinearLayout(parent.getContext());
        RelativeLayout.LayoutParams paramsRight =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        paramsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        rightLayout.setLayoutParams(paramsRight);
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);

        relativeLayout.addView(leftLayout);
        relativeLayout.addView(rightLayout);

        return new ListViewHolder(relativeLayout);
    }

    /**
     * Called by {@link com.telerik.widget.list.RadListView} to display the swipe content
     * behind an item at the specified position. This method
     * should update the contents of the {@link com.telerik.widget.list.ListViewHolder#itemView}
     * to reflect the swipe content behind the item at the given position.
     * <p>
     * Note that the default holder that is created in the original
     * {@link #onCreateSwipeContentHolder(android.view.ViewGroup)} method contains a
     * {@link android.widget.FrameLayout} with two children of type {@link android.widget.LinearLayout}.
     * The first is aligned to the left and the second is aligned to the right. In most cases you won't
     * need to override {@link #onCreateSwipeContentHolder(android.view.ViewGroup)} as you can simply
     * add your desired content to each of the child layouts in order to show that content
     * when the user swipes to the right or to the left.
     * </p>
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     *
     * @see com.telerik.widget.list.SwipeExecuteBehavior
     * @see #onCreateSwipeContentHolder(android.view.ViewGroup)
     */
    public void onBindSwipeContentHolder(ListViewHolder holder, int position) {
    }

    /**
     * Notify an instance of {@link com.telerik.widget.list.SwipeExecuteBehavior} if attached,
     * that the execute operation is complete so that the item can hide the swipe content.
     *
     * @see com.telerik.widget.list.SwipeRefreshBehavior
     */
    public void notifySwipeExecuteFinished() {
        for(SwipeExecuteBehavior.SwipeExecuteDismissedListener listener : swipeListeners) {
            listener.onSwipeContentDismissed();
        }
    }

    /**
     * Determines if the item at the provided position can be reordered.
     *
     * @see com.telerik.widget.list.ItemReorderBehavior
     */
    public boolean canReorder(int position) {
        return isPositionValid(position);
    }

    /**
     * Provides the reorder operation as requested by an
     * {@link com.telerik.widget.list.ItemReorderBehavior} instance.
     *
     * @see com.telerik.widget.list.ItemReorderBehavior
     */
    public boolean reorderItem(int oldPosition, int newPosition) {
        List items = getItems();
        Object removedItem = items.remove(oldPosition);
        items.add(newPosition, removedItem);
        notifyItemMoved(oldPosition, newPosition);
        return true;
    }

    /**
     * Gets the list of items handled by this adapter instance.
     *
     * @see #setItems(java.util.List)
     */
    public List getItems() {
        return this.items;
    }

    /**
     * Sets a new list of items to be handled by this adapter instance.
     *
     * @see #getItems()
     */
    public void setItems(List items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * Return the item with the provided id.
     * Returns null if item with that id is not found.
     *
     * @param id the item id to query
     * @return the item with the provided id or null if not found
     */
    public Object getItem(long id) {
        for(Object item : items) {
            if(getItemId(item) == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Return the item on the provided position.
     * Returns null if the position exceeds the bounds of the list of items.
     *
     * @param position the position of the item
     * @return the item on the specified position or null if not found
     */
    public Object getItem(int position) {
        if(!isPositionValid(position)) {
            return null;
        }
        return items.get(position);
    }

    /**
     * Return the position of the item with the provided id.
     * Returns {@code INVALID_ID} if an item with that id is not found.
     *
     * @param id the item id to query
     * @return the position of the item with the provided id
     */
    public int getPosition(long id) {
        int position = 0;
        for(Object item : items) {
            if(item.hashCode() == id) {
                return position;
            }
            position++;
        }
        return INVALID_ID;
    }

    /**
     * Return the position of the item that is provided.
     * Returns {@code -1} if the item is not found.
     *
     * @param searchItem the item to query
     * @return the index of the first occurrence of the object or -1 if the
     *         object was not found.
     */
    public int getPosition(Object searchItem) {
        return items.indexOf(searchItem);
    }

    /**
     * Return the id of the item that is provided.
     * Returns {@code -1} if the item is not found.
     *
     * @param item the item to query
     * @return the id of the that item.
     */
    public long getItemId(Object item) {
        return item.hashCode();
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_list_item, parent, false);
        return new ListViewTextHolder(view, R.id.text1);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        if(!(holder instanceof ListViewTextHolder)) {
            return;
        }
        ListViewTextHolder textHolder = (ListViewTextHolder)holder;
        Object item = items.get(position);
        textHolder.textView.setText(String.valueOf(item));
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    public long getItemId(int position) {
        if(0 <= position && position < items.size()) {
            return items.get(position).hashCode();
        }
        return INVALID_ID;
    }


    SelectionService selectionService() {
        return selectionService;
    }

    void addLoadingListener(LoadOnDemandBehavior.LoadingListener listener) {
        loadingListeners.add(listener);
    }

    void removeLoadingListener(LoadOnDemandBehavior.LoadingListener listener) {
        loadingListeners.remove(listener);
    }

    void addSwipeExecuteDismissedListener(SwipeExecuteBehavior.SwipeExecuteDismissedListener listener) {
        swipeListeners.add(listener);
    }

    void removeSwipeExecuteDismissedListener(SwipeExecuteBehavior.SwipeExecuteDismissedListener listener) {
        swipeListeners.remove(listener);
    }

    void addRefreshListener(SwipeRefreshBehavior.RefreshListener listener) {
        refreshListeners.add(listener);
    }

    void removeRefreshListener(SwipeRefreshBehavior.RefreshListener listener) {
        refreshListeners.remove(listener);
    }

    private boolean isPositionValid(int position) {
        if(position < 0 || position >= getItemCount()) {
            return false;
        }
        return true;
    }
}
