package com.telerik.widget.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telerik.android.common.Function;
import com.telerik.android.common.Function2;
import com.telerik.android.data.DataChangeInfo;
import com.telerik.android.data.DataChangedListener;
import com.telerik.android.data.DataItem;
import com.telerik.android.data.RadDataSource;

import java.util.List;

/**
 * Base class for an Adapter used by {@link com.telerik.widget.list.RadListView} that supports
 * data operations like grouping, filtering and sorting.
 *
 * <p>Adapters provide a binding from an app-specific data set to views that are displayed
 * within a {@link com.telerik.widget.list.RadListView}.</p>
 */
public class ListViewDataSourceAdapter extends ListViewAdapter
                        implements DataChangedListener{

    protected static int ITEM_VIEW_TYPE_GROUP = -123;

    private List flatView;
    private RadDataSource dataSource;

    private boolean skipNextDataChangedEvent = false;

    /**
     * Creates an instance of the {@link com.telerik.widget.list.ListViewDataSourceAdapter} class.
     *
     * @param items a list of items that will be handled by this adapter
     */
    public ListViewDataSourceAdapter(List items) {
        super(items);

        this.dataSource = new RadDataSource();
        this.dataSource.addDataChangeListener(this);
        this.dataSource.setSource(items);
    }

    /**
     * Adds a group descriptor that specifies how the items on this list can be grouped.
     * For example, here's how you can group a list of strings by their first letter:
     * <pre>
     * <code>myAdapter.addGroupDescriptor(new Function<String, Character>() {
     *       {@literal @}Override
     *       public Character apply(String argument) {
     *         return argument.charAt(0);
     *       }
     *    });
     * </code>
     * </pre>
     *
     * @param groupDescriptor  the group descriptor to use.
     */
    public void addGroupDescriptor(Function<Object, Object> groupDescriptor) {
        dataSource().groupDescriptors().add(groupDescriptor);
    }

    /**
     * Removes the specified group descriptor from the descriptors used by this instance.
     *
     * @param groupDescriptor  the group descriptor to remove.
     */
    public void removeGroupDescriptor(Function<Object, Object> groupDescriptor) {
        dataSource().groupDescriptors().remove(groupDescriptor);
    }

    /**
     * Clears the list of group descriptors used by this instance.
     */
    public void clearGroupDescriptors() {
        dataSource().groupDescriptors().clear();
    }

    /**
     * Adds a sort descriptor that specifies how the items on this list can be sorted.
     * For example, here's how you can sort a list of items by the value returned by
     * their getName() method:
     * <pre>
     * <code>myAdapter.addGroupDescriptor(new Function2<MyItem, MyItem, Integer>() {
     *       {@literal @}Override
     *       public Integer apply(MyItem argument1, MyItem argument2) {
     *         return argument1.getName().compareTo(argument2.getName);
     *       }
     *    });
     * </code>
     * </pre>
     *
     * @param sortDescriptor  the sort descriptor to use.
     */
    public void addSortDescriptor(Function2<Object, Object, Integer> sortDescriptor) {
        dataSource().sortDescriptors().add(sortDescriptor);
    }

    /**
     * Removes the specified sort descriptor from the descriptors used by this instance.
     *
     * @param sortDescriptor  the sort descriptor to remove.
     */
    public void removeSortDescriptor(Function2<Object, Object, Integer> sortDescriptor) {
        dataSource().sortDescriptors().remove(sortDescriptor);
    }

    /**
     * Clears the list of sort descriptors used by this instance.
     */
    public void clearSortDescriptors() {
        dataSource().sortDescriptors().clear();
    }

    /**
     * Adds a filter descriptor that specifies how the items on this list can be filtered.
     * For example, here's how you can filter a list of string to those starting with the letter "s":
     * <pre>
     * <code>myAdapter.addFilterDescriptor(new Function<String, Boolean>() {
     *       {@literal @}Override
     *       public Boolean apply(String argument) {
     *         return argument.startsWith("s");
     *       }
     *    });
     * </code>
     * </pre>
     *
     * @param filterDescriptor  the filter descriptor to use.
     */
    public void addFilterDescriptor(Function<Object, Boolean> filterDescriptor) {
        dataSource().filterDescriptors().add(filterDescriptor);
    }

    /**
     * Removes the specified filter descriptor from the descriptors used by this instance.
     *
     * @param filterDescriptor  the filter descriptor to remove.
     */
    public void removeFilterDescriptor(Function<Object, Boolean> filterDescriptor) {
        dataSource().filterDescriptors().remove(filterDescriptor);
    }

    /**
     * Clears the list of filter descriptors used by this instance.
     */
    public void clearFilterDescriptors() {
        dataSource().filterDescriptors().clear();
    }

    /**
     * Invalidates the descriptors and updates the list with any updates on the list.
     */
    public void invalidateDescriptors() {
        dataSource().invalidateDescriptors();
    }

    @Override
    public void add(Object item) {
        add(item, true);
    }

    /**
     * Adds the specified item at the end of the items in
     * this {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @param item                  the object to add.
     * @param invalidateDescriptors specifies whether the descriptors will be
     *                              invalidated after this change.
     */
    public void add(Object item, boolean invalidateDescriptors) {
        add(getItems().size(), item, invalidateDescriptors);
    }

    @Override
    public void add(int index, Object item) {
        add(index, item, true);
    }

    /**
     * Inserts the specified object into this {@link com.telerik.widget.list.ListViewAdapter}
     * at the specified index. The object is inserted before the current element at the
     * specified index.
     *
     * @param index                 the index at which to insert.
     * @param item                  the object to add.
     * @param invalidateDescriptors specifies whether the descriptors will be
     *                              invalidated after this change.
     */
    public void add(int index, Object item, boolean invalidateDescriptors) {
        getItems().add(index, item);
        if(!invalidateDescriptors) {
            DataItem dataItem = new DataItem(item);
            flatView.add(index, dataItem);
            notifyItemInserted(index);
            skipNextDataChangedEvent = true;
        }
        dataSource().setSource(getItems());
    }

    @Override
    public boolean remove(Object item) {
        return remove(item, true);
    }

    /**
     * Removes the first occurrence of the specified object from the list
     * of items in this {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @param item                  the object to remove.
     * @param invalidateDescriptors specifies whether the descriptors will be
     *                              invalidated after this change.
     */
    public boolean remove(Object item, boolean invalidateDescriptors) {
        int itemIndex = -1;
        for (int i = 0; i < flatView.size(); i++) {
            DataItem dataItem = (DataItem)flatView.get(i);
            if(item.equals(dataItem.entity())) {
                itemIndex = i;
                break;
            }
        }
        if(itemIndex == -1) {
            return getItems().remove(item);
        }
        int headerPosition = getHeaderPosition(itemIndex);
        boolean result = getItems().remove(item);
        if(!result) {
            return result;
        }
        flatView.remove(itemIndex);
        notifyItemRemoved(itemIndex);
        skipNextDataChangedEvent = true;
        if(invalidateDescriptors) {
            if(headerPosition != INVALID_ID) {
                // If next item's header differs from the current item's header then,
                // the current item's header has no items and we remove it.
                if(headerPosition + 1 >= getItemCount() ||
                        getHeaderPosition(headerPosition + 1) != headerPosition) {
                    flatView.remove(headerPosition);
                    notifyItemRemoved(headerPosition);
                }
            }
        }
        dataSource().setSource(getItems());
        return true;
    }

    @Override
    public Object remove(int index) {
        return remove(index, true);
    }

    /**
     * Removes the object at the specified index from the list
     * of items in this {@link com.telerik.widget.list.ListViewAdapter}.
     *
     * @param index                 the index of the object to remove.
     * @param invalidateDescriptors specifies whether the descriptors will be
     *                              invalidated after this change.
     */
    public Object remove(int index, boolean invalidateDescriptors) {
        Object itemToRemove = getItems().get(index);
        if(itemToRemove == null) {
            return null;
        }
        boolean result = remove(itemToRemove, invalidateDescriptors);
        if(result) {
            return itemToRemove;
        }
        return null;
    }

    /**
     * Return the {@link com.telerik.android.data.DataItem} on the provided position.
     * Returns null if the position exceeds the bounds of the list of items.
     *
     * @param position the position of the data item
     * @return the item on the specified position or null if not found
     */
    public DataItem getDataItem(int position) {
        if(!isPositionValid(position)) {
            return null;
        }
        return (DataItem) flatView.get(position);
    }

    /**
     * Return <code>true</code> if the item at the specified position is a group header
     * and <code>false</code> otherwise.
     *
     * @param position the position of the item
     */
    public boolean isGroupHeader(int position) {
        DataItem dataItem = getDataItem(position);
        return isGroupHeader(dataItem);
    }

    @Override
    public void setItems(List items) {
        super.setItems(items);
        dataSource().setSource(items);
    }

    @Override
    public void dataChanged(DataChangeInfo info) {
        if(skipNextDataChangedEvent) {
            skipNextDataChangedEvent = false;
            return;
        }
        updateFlatView();
    }

    @Override
    public Object getItem(int position) {
        DataItem dataItem = getDataItem(position);
        if(dataItem == null) {
            return null;
        }
        if(dataItem.entity() != null) {
            return dataItem.entity();
        }
        return dataItem.groupKey();
    }

    @Override
    public boolean canSwipe(int position) {
        if(!isPositionValid(position)) {
            return false;
        }
        return !isGroupHeader(position);
    }

    @Override
    public boolean canSelect(int position) {
        if(!isPositionValid(position)) {
            return false;
        }
        return !isGroupHeader(position);
    }

    @Override
    public int getPosition(long id) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemId(i) == id) {
                return i;
            }
        }
        return INVALID_ID;
    }

    @Override
    public int getPosition(Object searchItem) {
        if(searchItem == null) {
            return INVALID_ID;
        }
        long id = getItemId(searchItem);
        return getPosition(id);
    }

    @Override
    public boolean canReorder(int position) {
        if(!isPositionValid(position)) {
            return false;
        }
        return !isGroupHeader(position);
    }

    @Override
    public boolean reorderItem(int oldPosition, int newPosition) {
        List items = flatView;
        Object removedItem = items.remove(oldPosition);
        items.add(newPosition, removedItem);
        notifyItemMoved(oldPosition, newPosition);
        return true;
    }

    @Override
    public long getItemId(int position) {
        DataItem dataItem = (DataItem)flatView.get(position);
        Object item = isGroupHeader(dataItem) ? dataItem.groupKey() : dataItem.entity();
        return getItemId(item);
    }

    @Override
    /**
     * Called when {@link RadListView} needs a new {@link com.telerik.widget.list.ListViewHolder}
     * of the given type to represent an item.
     *
     * The method is declared final since it separates the type if items to groups and actual items
     * and calls {@link #onCreateGroupViewHolder(android.view.ViewGroup, int)} or
     * {@link #onCreateItemViewHolder(android.view.ViewGroup, int)} respectively.
     *
     * Note that if the itemView type is negative, the view will be treated like a group header.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     *
     * @see #onCreateGroupViewHolder(android.view.ViewGroup, int)
     * @see #onCreateItemViewHolder(android.view.ViewGroup, int)
     */
    public final ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType < 0) {
            return onCreateGroupViewHolder(parent, viewType);
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    @Override
    /**
     * Called by {@link com.telerik.widget.list.RadListView} to display the data at the specified position. This method
     * should update the contents of the {@link ListViewHolder#itemView} to reflect the item at
     * the given position.
     *
     * Note that the method is declared final as it will simply determine if the item at the specified position
     * is a simple item or a group header and call {@link #onBindItemViewHolder(ListViewHolder, Object)} or
     * {@link #onBindGroupViewHolder(ListViewHolder, Object)} respectively.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    public final void onBindViewHolder(ListViewHolder holder, int position) {
        Object o = flatView.get(position);
        DataItem dataItem = (DataItem)o;
        if(isGroupHeader(dataItem)) {
            onBindGroupViewHolder(holder, dataItem.groupKey());
        } else {
            onBindItemViewHolder(holder, dataItem.entity());
        }
    }

    @Override
    public final int getItemViewType(int position) {
        Object o = flatView.get(position);
        DataItem dataItem = (DataItem)o;
        if(isGroupHeader(dataItem)) {
            return getGroupViewType(dataItem.groupKey());
        } else {
            return getItemViewType(dataItem.entity());
        }
    }

    /**
     * Return the view type of provided item for the purposes
     * of view recycling.
     *
     * Please note that the negative values are preserved for view types of the group headers.
     *
     * The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Consider using id resources to
     * uniquely identify item view types.
     *
     * @see  #getGroupViewType(Object)
     *
     * @param item item to query
     * @return  integer value identifying the type of the view needed to represent the item.
     *          Type codes need not be contiguous.
     */
    public int getItemViewType(Object item) {
        return 0;
    }

    /**
     * Return the view type of provided group header item for the purposes
     * of view recycling.
     *
     * Please note that the negative values are required for view types of the group headers.
     *
     * The default implementation of this method returns <code>ITEM_VIEW_TYPE_GROUP</code>,
     * making the assumption of a single view type for the headers in this adapter.
     * Consider using id resources to uniquely identify group header item view types.
     *
     * @see  #getItemViewType(Object)
     *
     * @param group group header to query
     * @return  integer value identifying the type of the view needed to represent the item.
     *          Type codes need not be contiguous.
     */
    public int getGroupViewType(Object group) {
        return ITEM_VIEW_TYPE_GROUP;
    }

    @Override
    /**
     * Returns the total number of items handled by the adapter after the data operation are performed.
     * This means that if you are having 10 items and they are grouped in 3 groups,
     * this method will return 13. For the actual number of items (10) use {@link #getBaseItemCount()}.
     *
     * @see #getBaseItemCount()
     *
     * @return The total number of items in this adapter after the grouping operations.
     */
    public final int getItemCount() {
        return flatView.size();
    }

    /**
     * Returns the total number of items handled by the adapter after the data operation are performed.
     * This means that if you are having 10 items and they are grouped in 3 groups,
     * this method will return 10, but the list will display 13 items - 10 for the actual items and
     * 3 for the group headers. For the total number of items (13) use {@link #getItemCount()}.
     *
     * @see #getBaseItemCount()
     *
     * @return The total number of items in this adapter.
     */
    public int getBaseItemCount() {
        return getItems().size();
    }

    /**
     * Called when {@link RadListView} needs a new {@link com.telerik.widget.list.ListViewHolder}
     * of the given type to represent a group header item.
     *
     * The method will be called for negative values of viewType.
     *
     * Note that if the itemView type is negative, the view will be treated like a group header.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindGroupViewHolder(ListViewHolder, Object)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     *
     * @see #onBindGroupViewHolder(ListViewHolder, Object)
     * @see #onCreateItemViewHolder(android.view.ViewGroup, int)
     */
    public ListViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType){
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_group_header_item, parent, false);
        return new ListViewTextHolder(view);
    }

    /**
     * Called by {@link com.telerik.widget.list.RadListView} to display the specified group header.
     * This method should update the contents of the {@link com.telerik.widget.list.ListViewHolder#itemView} to
     * reflect the given group header item.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               group key that is provided.
     * @param groupKey The group key that should be bound.
     *
     * @see #onBindItemViewHolder(ListViewHolder, Object)
     * @see #onCreateGroupViewHolder(android.view.ViewGroup, int)
     */
    public void onBindGroupViewHolder(ListViewHolder holder, Object groupKey) {
        if(holder instanceof ListViewTextHolder) {
            ((ListViewTextHolder)holder).textView.setText(String.valueOf(groupKey));
        }
    }

    /**
     * Called when {@link RadListView} needs a new {@link com.telerik.widget.list.ListViewHolder}
     * of the given type to represent an item from the list.
     *
     * The method will be called for non-negative values of viewType.
     *
     * Note that if the itemView type is negative, the view will be treated like a group header.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindItemViewHolder(ListViewHolder, Object)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     *
     * @see #onBindItemViewHolder(ListViewHolder, Object)
     * @see #onCreateGroupViewHolder(android.view.ViewGroup, int)
     */
    public ListViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_list_item, parent, false);
        return new ListViewTextHolder(view, R.id.text1);
    }

    /**
     * Called by {@link com.telerik.widget.list.RadListView} to display the specified item.
     * This method should update the contents of the {@link com.telerik.widget.list.ListViewHolder#itemView} to
     * reflect the given item.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               group key that is provided.
     * @param entity The entity that should be bound.
     *
     * @see #onBindGroupViewHolder(ListViewHolder, Object)
     * @see #onCreateItemViewHolder(android.view.ViewGroup, int)
     */
    public void onBindItemViewHolder(ListViewHolder holder, Object entity) {
        if(!(holder instanceof ListViewTextHolder)) {
            return;
        }
        ListViewTextHolder textHolder = (ListViewTextHolder)holder;
        textHolder.textView.setText(String.valueOf(entity));
    }

    public int getHeaderPosition(int position) {
        for(int i = position; i >= 0; i--) {
            if(isGroupHeader(i)) {
                return i;
            }
        }
        return INVALID_ID;
    }

    private RadDataSource dataSource() {
        return this.dataSource;
    }

    private boolean isGroupHeader(DataItem dataItem) {
        if(dataItem == null || dataItem.getItems().isEmpty()) {
            return false;
        }
        return true;
    }

    private void updateFlatView() {
        flatView = dataSource().flatView();
        notifyDataSetChanged();
    }

    private boolean isPositionValid(int position) {
        if(position < 0 || position >= getItemCount()) {
            return false;
        }
        return true;
    }
}
