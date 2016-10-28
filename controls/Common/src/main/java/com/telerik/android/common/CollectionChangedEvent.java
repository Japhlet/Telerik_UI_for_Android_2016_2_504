package com.telerik.android.common;

import java.util.List;

/**
 * Simple class holding the data about a change that occurred in an {@link ObservableCollection}.
 */
public class CollectionChangedEvent<E> {

    private CollectionChangeAction action;
    private List<? extends E> newItems;
    private List<? extends E> oldItems;
    private int newIndex;
    private int oldIndex;
    ObservableCollection<? extends E> source;

    /**
     * Creates an instance of the {@link CollectionChangedEvent} class.
     *
     * @param action   The action with which this event is associated with.
     * @param oldItems  The old item if an item was replaced or removed.
     * @param newItems  The newly added item.
     * @param oldIndex The index of the removed or replaced item.
     * @param newIndex The index of the newly added item.
     */
    public CollectionChangedEvent(ObservableCollection<? extends E> source, CollectionChangeAction action, List<? extends E> oldItems, List<? extends E> newItems, int oldIndex, int newIndex) {
        this.action = action;
        this.oldItems = oldItems;
        this.newItems = newItems;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
        this.source = source;
    }

    /**
     * Creates an instance of the {@link CollectionChangedEvent} class.
     *
     * @param action The action which this event is associated with.
     */
    public CollectionChangedEvent(ObservableCollection<E> source, CollectionChangeAction action) {
        this(source, action, null, null, -1, -1);
    }

    public ObservableCollection<? extends E> getSource() {
        return this.source;
    }

    /**
     * Gets the current action.
     *
     * @return the current action.
     * @see CollectionChangeAction
     */
    public CollectionChangeAction action() {
        return action;
    }

    /**
     * Gets the new item.
     *
     * @return the new item.
     */
    public List<? extends E> getNewItems() {
        return newItems;
    }

    /**
     * Gets the old item.
     *
     * @return the old item.
     */
    public List<? extends E> getOldItems() {
        return oldItems;
    }

    /**
     * Gets the new index of the item.
     *
     * @return the new index of the item.
     */
    public int getNewIndex() {
        return newIndex;
    }

    /**
     * Gets the old index of the item.
     *
     * @return the old index of the item.
     */
    public int getOldIndex() {
        return oldIndex;
    }
}
