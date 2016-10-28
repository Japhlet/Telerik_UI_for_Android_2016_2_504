package com.telerik.android.data;

import com.telerik.android.common.CollectionChangeAction;
import com.telerik.android.common.CollectionChangeListener;
import com.telerik.android.common.CollectionChangedEvent;
import com.telerik.android.common.Function;
import com.telerik.android.common.Function2;
import com.telerik.android.common.ObservableCollection;
import com.telerik.android.common.Procedure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * RadDataSource can filter group and sort any {@link java.lang.Iterable} implementation.
 */
public class RadDataSource<E> implements CollectionChangeListener<E>, Iterable<DataItem<E>> {
    Iterable<E> rawSource;
    ArrayList<DataItem<E>> sourceCollection = new ArrayList<>();
    List<DataItem<E>> view;

    ObservableCollection<Function<E, Boolean>> filterDescriptors = new ObservableCollection<>();
    ObservableCollection<Function<E, Object>> groupDescriptors = new ObservableCollection<>();
    ObservableCollection<Function2<E, E, Integer>> sortDescriptors = new ObservableCollection<>();
    ArrayList<DataChangedListener<E>> changedListeners = new ArrayList<>();

    Comparator<DataItem<E>> itemComparator = new Comparator<DataItem<E>>() {
        @Override
        public int compare(DataItem<E> item1, DataItem<E> item2) {
            int result = 0;
            for(Function2<E, E, Integer> sortDescriptor : sortDescriptors) {
                result = sortDescriptor.apply(item1.entity(), item2.entity());
                if(result != 0) {
                    break;
                }
            }

            return result;
        }};

    boolean suspend = false;

    public void addDataChangeListener(DataChangedListener<E> listener) {
        this.changedListeners.add(listener);
    }

    @SuppressWarnings("unused")
    public void removeDataChangeListener(DataChangedListener<E> listener) {
        this.changedListeners.remove(listener);
    }

    /**
     * Creates an instance of the {@link com.telerik.android.data.RadDataSource}
     * class.
     */
    public RadDataSource() {
        this.initializeDescriptorCollectionObservers();
    }

    /**
     * Creates an instance of the {@link com.telerik.android.data.RadDataSource} class
     * and initializes it with the provided source.
     *
     * @param source an {@link java.lang.Iterable} implementation providing the data source
     *               to perform the data operations on.
     */
    public RadDataSource(Iterable<E> source) {
        this();
        this.setSource(source);
    }

    public static RadDataSource<JSONObject> createFromJson(String json) throws JSONException {
        JSONArray array = new JSONArray(json);

        ArrayList<JSONObject> items = new ArrayList<>();
        for (int i = 0; i < array.length(); ++i) {
            items.add(array.getJSONObject(i));
        }

        return new RadDataSource<>(items);
    }

    public static void createFromJsonUrl(URL url, final OnJSONDataSourceCreated result) {
        LoadJSONTask task = new LoadJSONTask();
        task.setFinishedListener(new Procedure() {
            @Override
            public void apply(Object json) {
                try {
                    RadDataSource<JSONObject> dataSource = createFromJson(json.toString());
                    result.onDataSourceCreated(dataSource);
                } catch (JSONException ex) {
                    result.onError(ex);
                }
            }
        });
        task.execute(url);
    }

    @Override
    public Iterator<DataItem<E>> iterator() {
        return this.view.iterator();
    }

    /**
     * Gets the data source on which this {@link com.telerik.android.data.RadDataSource}
     * instance operates.
     *
     * @return an instance of {@link java.lang.Iterable} that represents
     */
    public Iterable<E> getSource() {
        return this.rawSource;
    }

    /**
     * Provides a collection of filter descriptors that
     * are used to apply filtering operations on the {@link java.lang.Iterable} source currently
     * initialized with this {@link com.telerik.android.data.RadDataSource} instance.
     *
     * @return an instance of {@link com.telerik.android.common.ObservableCollection} containing
     * the filter descriptors.
     */
    public ObservableCollection<Function<E, Boolean>> filterDescriptors() {
        return this.filterDescriptors;
    }

    /**
     * Provides a collection of group descriptors that
     * are used to apply grouping operations on the {@link java.lang.Iterable} source currently
     * initialized with this {@link com.telerik.android.data.RadDataSource} instance.
     *
     * @return an instance of {@link com.telerik.android.common.ObservableCollection} containing
     * the group descriptors.
     */
    public ObservableCollection<Function<E, Object>> groupDescriptors() {
        return this.groupDescriptors;
    }

    /**
     * Provides a collection of sort descriptors that
     * are used to apply grouping operations on the {@link java.lang.Iterable} source currently
     * initialized with this {@link com.telerik.android.data.RadDataSource} instance.
     *
     * @return an instance of {@link com.telerik.android.common.ObservableCollection} containing
     * the sort descriptors.
     */
    public ObservableCollection<Function2<E, E, Integer>> sortDescriptors() {
        return this.sortDescriptors;
    }

    /**
     * Returns a List that is filtered, sorted and grouped according to the current descriptors.
     *
     * @return the {@link java.lang.Iterable} implementation representing the data view.
     */
    public List<DataItem<E>> view() {
        return this.view;
    }

    /**
     * Returns a List that is filtered, sorted and grouped according to the current descriptors. All groups
     * are expanded and the hierarchy is returned as flat view.
     */
    public List<DataItem<E>> flatView() {
        ArrayList<DataItem<E>> result = new ArrayList<>();
        this.flattenView(result, this.view);
        return result;
    }

    private void flattenView(List<DataItem<E>> result, List<DataItem<E>> view) {
        for(DataItem<E> item : view) {
            result.add(item);
            this.flattenView(result, item.getItems());
        }
    }

    /**
     * Suspends the tracking of data descriptor collection changes for this {@link com.telerik.android.data.RadDataSource}
     * instance.
     */
    @SuppressWarnings("unused")
    public void suspendUpdate() {
        this.suspend = true;
    }

    @SuppressWarnings("unused")
    public void resumeUpdate() {
        this.resumeUpdate(false);
    }

    /**
     * Resumes the tracking of data descriptor collection changes for this {@link com.telerik.android.data.RadDataSource}
     * instance.
     *
     * @param refresh if set to{@code true} refreshes the view immediately.
     */
    public void resumeUpdate(boolean refresh) {
        this.suspend = false;
        if (refresh) {

            this.refresh(this.rawSource);
        }
    }

    @Override
    public void collectionChanged(CollectionChangedEvent<E> info) {
        CollectionChangeAction collectionChangeAction = info.action();

        if (collectionChangeAction.equals(CollectionChangeAction.ADD)) {
            this.insertNewItem(info);

        } else if (collectionChangeAction.equals(CollectionChangeAction.MOVE)) {
            this.moveItem(info);

        } else if (collectionChangeAction.equals(CollectionChangeAction.REPLACE)) {
            this.replaceItem(info);

        } else if (collectionChangeAction.equals(CollectionChangeAction.REMOVE)) {
            this.removeItem(info);

        } else if (collectionChangeAction.equals(CollectionChangeAction.RESET)) {
            this.refresh(this.rawSource);
        }
    }

    void refresh(Iterable<E> newSource) {
        this.sourceCollection.clear();

        this.rawSource = newSource;

        this.view = null;

        if (this.rawSource == null) {
            return;
        }

        for (E rawEntity : this.rawSource) {
            DataItem<E> dataItem = new DataItem<>(rawEntity);
            this.sourceCollection.add(dataItem);
        }

        this.invalidateDescriptors();
    }

    /**
     * Sets an {@link java.lang.Iterable} implementation representing the data source on which
     * the current {@link com.telerik.android.data.RadDataSource} will operate on.
     *
     * @param source the {@link java.lang.Iterable} implementation.
     */
    public void setSource(Iterable<E> source) {
        if (this.rawSource instanceof ObservableCollection) {
            ((ObservableCollection<E>) this.rawSource).removeCollectionChangeListener(this);
        }

        this.refresh(source);

        if (source instanceof ObservableCollection) {
            ((ObservableCollection<E>) source).addCollectionChangeListener(this);
        }
    }

    void initializeDescriptorCollectionObservers() {
        this.filterDescriptors.addCollectionChangeListener(new CollectionChangeListener<Function<E, Boolean>>() {
            @Override
            public void collectionChanged(CollectionChangedEvent<Function<E, Boolean>> info) {
                RadDataSource.this.invalidateDescriptors();
            }
        });

        this.groupDescriptors.addCollectionChangeListener(new CollectionChangeListener<Function<E, Object>>() {
            @Override
            public void collectionChanged(CollectionChangedEvent<Function<E, Object>> info) {
                RadDataSource.this.invalidateDescriptors();
            }
        });

        this.sortDescriptors.addCollectionChangeListener(new CollectionChangeListener<Function2<E, E, Integer>>() {
            @Override
            public void collectionChanged(CollectionChangedEvent<Function2<E, E, Integer>> info) {
                RadDataSource.this.invalidateDescriptors();
            }
        });
    }

    public void invalidateDescriptors() {
        if (this.suspend) {
            return;
        }

        List<DataItem<E>> view = this.filterItems(this.sourceCollection);
        view = this.sortItems(view);
        view = this.groupItems(view, 0);

        this.view = view;

        this.onDataChanged();
    }

    List<DataItem<E>> filterItems(Iterable<DataItem<E>> view) {
        LinkedList<DataItem<E>> result = new LinkedList<>();

        for(DataItem<E> item : view) {
            boolean passesFilters = true;

            for (Function<E, Boolean> filter : this.filterDescriptors) {
                passesFilters = passesFilters && filter.apply(item.entity());
            }

            if(passesFilters) {
                result.add(item);
            }
        }

        return result;
    }

    List<DataItem<E>> groupItems(List<DataItem<E>> view, int descriptorIndex) {
        if(this.groupDescriptors.size() == 0) {
            return view;
        }

        if(descriptorIndex >= this.groupDescriptors.size()) {
            return view;
        }

        Function<E, Object> groupDescriptor = this.groupDescriptors.get(descriptorIndex);
        view = this.groupBy(view, groupDescriptor);


        for(DataItem<E> group : view) {
            group.setItems(this.groupItems(group.getItems(), descriptorIndex + 1));
        }

        return view;
    }

    List<DataItem<E>> groupBy(List<DataItem<E>> view, Function<E, Object> groupDescriptor) {
        LinkedHashMap<Object, DataItem<E>> groupsMap = new LinkedHashMap<>();
        for (DataItem<E> sourceItem : view) {
            Object groupKey = groupDescriptor.apply(sourceItem.entity());
            if (!groupsMap.containsKey(groupKey)) {
                DataItem<E> newGroup = new DataItem<>(null, groupKey);
                newGroup.getItems().add(sourceItem);
                groupsMap.put(groupKey, newGroup);
            } else {
                DataItem<E> existingGroup = groupsMap.get(groupKey);
                existingGroup.getItems().add(sourceItem);
            }
        }

        return new ArrayList<>(groupsMap.values());
    }

    List<DataItem<E>> sortItems(List<DataItem<E>> view) {
        if(this.sortDescriptors.isEmpty()) {
            return view;
        }

        List<DataItem<E>> list = new ArrayList<>(view.size());
        for(DataItem<E> item : view) {
            list.add(item);
        }

        Collections.sort(list, this.itemComparator);

        return list;
    }

    void removeItem(CollectionChangedEvent<E> info) {
        for(E item : info.getOldItems()) {
            DataItem<E> dataItem = this.findDataItem(this.sourceCollection, item);
            this.sourceCollection.remove(dataItem);

            if (this.groupDescriptors.isEmpty()) {
                dataItem = this.findDataItem(this.view, item);
                this.view.remove(dataItem);
            } else {
                this.removeFromGroup(dataItem, this.view);
            }
        }

        this.removeEmptyGroups(this.view);
    }

    void removeEmptyGroups(List<DataItem<E>> items) {
        LinkedList<DataItem<E>> groupsToRemove = new LinkedList<>();
        for(DataItem<E> item : items) {
            if(item.groupKey() != null) {
                if(item.getItems().isEmpty()) {
                    groupsToRemove.add(item);
                } else {
                    this.removeEmptyGroups(item.getItems());
                    if(item.getItems().isEmpty()) {
                        groupsToRemove.add(item);
                    }
                }
            }
        }

        for(DataItem<E> group : groupsToRemove) {
            items.remove(group);
        }
    }

    void removeFromGroup(DataItem<E> dataItem, List<DataItem<E>> groups) {
        for(DataItem<E> group : groups) {
            if(group.getItems().remove(dataItem)) {
                return;
            }

            this.removeFromGroup(dataItem, group.getItems());
        }
    }

    DataItem<E> findDataItem(Iterable<DataItem<E>> items, E item) {
        for(DataItem<E> dataItem : items) {
            if(dataItem.entity().equals(item)) {
                return dataItem;
            }
        }

        return null;
    }

    void moveItem(CollectionChangedEvent<E> info) {
        DataItem<E> itemToMove = this.sourceCollection.get(info.getOldIndex());
        this.sourceCollection.remove(info.getOldIndex());
        this.sourceCollection.add(info.getNewIndex() - 1, itemToMove);
    }

    void replaceItem(CollectionChangedEvent<E> info) {
        this.removeItem(info);
        this.insertNewItem(info);
    }

    void insertNewItem(CollectionChangedEvent<E> info) {
        for(E item : info.getNewItems()) {
            for(Function<E, Boolean> filter : this.filterDescriptors) {
                if(!filter.apply(item)){
                    return;
                }
            }

            DataItem<E> dataItem = new DataItem<>(item);

            int newIndex = info.getNewIndex();
            if(this.groupDescriptors.size() > 0) {
                this.groupItem(dataItem);
            } else {
                this.insertInCollection(dataItem, newIndex, this.view);
            }

            this.sourceCollection.add(newIndex, dataItem);
        }
    }

    void insertInCollection(DataItem<E> dataItem, int index, List<DataItem<E>> collection) {
        int sortIndex = this.findSortIndex(dataItem, collection);
        if(sortIndex != -1) {
            index = sortIndex;
        }

        collection.add(index, dataItem);
    }

    int findSortIndex(DataItem<E> dataItem, List<DataItem<E>> collection) {
        if(this.sortDescriptors.size() == 0) {
            return -1;
        }

        int sortIndex = binarySearch(collection, 0, collection.size() - 1, dataItem, this.itemComparator);
        if(sortIndex == -1) {
            return 0;
        }

        return sortIndex;
    }

    /**
     * Searches for the index at which the given DataItem should be inserted in the items collection.
     * @param items The items collection in which the given item should be inserted.
     * @param startIndex The start index of the collection.
     * @param endIndex The end index of the collection.
     * @param item The item to insert.
     * @param comparator A comparator to compare adjacent items.
     * @param <E> The type of data.
     * @return Returns the insert index of the given item.
     */
    static <E> int binarySearch(List<DataItem<E>> items, int startIndex, int endIndex, DataItem<E> item, Comparator<DataItem<E>> comparator) {
        if(items.size() == 0) {
            return 0;
        }

        if(startIndex >= endIndex) {
            if(startIndex == items.size()) {
                return startIndex;
            }

            DataItem<E> existingItem = items.get(startIndex);
            int comparison = comparator.compare(item, existingItem);
            if(comparison <= 0) {
                return startIndex;
            } else {
                return startIndex + 1;
            }
        }

        int middleIndex = startIndex + (endIndex - startIndex) / 2;
        DataItem<E> middleItem = items.get(middleIndex);

        int comparison = comparator.compare(item, middleItem);
        if(comparison == -1) {
            return binarySearch(items, startIndex, middleIndex - 1, item, comparator);
        } else if (comparison == 1) {
            return binarySearch(items, middleIndex + 1, endIndex, item, comparator);
        }

        return middleIndex;
    }

    DataItem<E> findGroup(Iterable<DataItem<E>> groups, Object... keys) {
        DataItem<E> result = null;
        for(Object key : keys) {
            for(DataItem<E> group : groups) {
                if(group.groupKey() != null && group.groupKey().equals(key)) {
                    groups = group.getItems();
                    result = group;
                }
            }
        }

        return result;
    }

    void groupItem(DataItem<E> dataItem) {
        List<DataItem<E>> groups = this.view;
        DataItem<E> group = null;
        for(Function<E, Object> groupDescriptor : this.groupDescriptors) {
            Object key = groupDescriptor.apply(dataItem.entity());
            group = this.findGroup(groups, key);
            if(group == null) {
                group = new DataItem<>(null, key);
                this.insertInCollection(group, groups.size(), groups);
            }
            groups = group.getItems();
        }

        this.insertInCollection(dataItem, group.getItems().size(), group.getItems());
    }

    protected void onDataChanged() {
        for(DataChangedListener<E> listener : this.changedListeners) {
            listener.dataChanged(new DataChangeInfo<E>());
        }
    }
}
