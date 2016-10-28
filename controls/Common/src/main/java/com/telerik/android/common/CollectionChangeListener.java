package com.telerik.android.common;

/**
 * Interface that bounds its implementers to notify the changes that occurred in a collection.
 */
public interface CollectionChangeListener<E> {
    /**
     * This method handles the occurred change and notifies the change listeners.
     *
     * @param info info about the occurred change holding the old and the new data.
     * @see CollectionChangedEvent
     */
    void collectionChanged(CollectionChangedEvent<E> info);
}
