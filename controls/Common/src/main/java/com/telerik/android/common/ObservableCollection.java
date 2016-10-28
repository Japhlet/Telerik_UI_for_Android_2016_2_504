package com.telerik.android.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a generic collection of type {@link ArrayList} which
 * can notify listeners about changes in the collection. For example when
 * items are added, removed, replaced etc.
 *
 * @param <E> the type of the collection.
 */
public class ObservableCollection<E> extends ArrayList<E> {
    private boolean updateSuspended;

    public void beginUpdate() {
        this.updateSuspended = true;
    }

    public void endUpdate() {
        this.updateSuspended = false;

        this.notifyListeners(new CollectionChangedEvent<E>(this, CollectionChangeAction.RESET));
    }

    /**
     * Holds a collection of event listeners that are ready to respond to a change in the collection.
     */
    private ArrayList<CollectionChangeListener<E>> changeListeners = new ArrayList<CollectionChangeListener<E>>();

    /**
     * Adds a new listener to the collection of change listeners that will be notified when a change
     * in the collection occurs using the {@link #notifyListeners(CollectionChangedEvent)} method.
     *
     * @param listener the new listener to be added.
     */
    public void addCollectionChangeListener(CollectionChangeListener<E> listener) {
        changeListeners.add(listener);
    }

    /**
     * Removes a specific change listener from the listeners collection.
     *
     * @param listener the listener to be removed.
     */
    public void removeCollectionChangeListener(CollectionChangeListener<E> listener) {
        changeListeners.remove(listener);
    }

    @Override
    public boolean add(E item) {
        boolean result = super.add(item);

        ArrayList<E> newItem = new ArrayList<E>();
        newItem.add(item);

        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.ADD, null, newItem, -1, this.size() - 1);
        this.notifyListeners(event);
        return result;
    }

    @Override
    public void add(int index, E item) {
        super.add(index, item);

        ArrayList<E> newItem = new ArrayList<E>();
        newItem.add(item);
        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.ADD, null, newItem, -1, index);
        this.notifyListeners(event);
    }

    @Override
    public boolean remove(Object item) {
        E typedItem = (E) item;
        int oldIndex = this.indexOf(item);
        boolean result = super.remove(item);

        ArrayList<E> newItem = new ArrayList<E>();
        newItem.add(typedItem);
        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.REMOVE, newItem, null, oldIndex, -1);
        this.notifyListeners(event);
        return result;
    }

    @Override
    public E remove(int index) {
        E result = super.remove(index);

        ArrayList<E> newItem = new ArrayList<E>();
        newItem.add(result);
        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.REMOVE, newItem, null, index, -1);
        this.notifyListeners(event);
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.RESET);
        this.notifyListeners(event);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        int index = this.size();
        boolean result = super.addAll(collection);

        List<? extends E> list = createList(collection);
        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.ADD, null, list, -1, index);
        this.notifyListeners(event);
        return result;
    }

    // WTF START - The Java generics SUCK!
    private List<? extends E> createList(Collection<? extends E> collection) {
        ArrayList<E> result = new ArrayList<E>();
        for(E item : collection) {
            result.add(item);
        }

        return result;
    }

    private List<? extends E> createList2(Collection<?> collection) {
        ArrayList<E> result = new ArrayList<E>();
        for(Object item : collection) {
            result.add((E)item);
        }

        return result;
    }
    // WTF END

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        boolean result = super.addAll(index, collection);

        List<? extends E> list = createList(collection);
        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.ADD, null, list, -1, index);
        this.notifyListeners(event);

        return result;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = super.removeAll(collection);

        List<? extends E> list = createList2(collection);
        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.REMOVE, list, null, -1, -1);
        this.notifyListeners(event);

        return result;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        if(fromIndex == toIndex) {
            return;
        }

        ArrayList<E> removedItems = new ArrayList<E>();
        for(int i = fromIndex; i < toIndex; ++i) {
            removedItems.add(this.get(i));
        }

        super.removeRange(fromIndex, toIndex);

        CollectionChangedEvent<E> event = new CollectionChangedEvent<E>(this, CollectionChangeAction.REMOVE, removedItems, null, fromIndex, toIndex);
        this.notifyListeners(event);
    }

    /**
     * Invokes the {@link CollectionChangeListener#collectionChanged(CollectionChangedEvent)} method on all subscribed change listeners.
     *
     * @param event the event that occurred in the collection.
     * @see CollectionChangedEvent
     * @see CollectionChangeListener
     */
    protected void notifyListeners(CollectionChangedEvent<E> event) {
        if (this.updateSuspended) {
            return;
        }

        for (CollectionChangeListener<E> listener : this.changeListeners) {
            listener.collectionChanged(event);
        }
    }
}
