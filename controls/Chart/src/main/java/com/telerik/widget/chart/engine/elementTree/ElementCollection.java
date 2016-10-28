package com.telerik.widget.chart.engine.elementTree;

import java.util.ArrayList;

/**
 * Represents a collection of {@link ChartElement} instances that is used by the Chart engine to build up the parent-child relations
 * between the separate elements comprising a single Chart.
 *
 * @param <T> the type of the elements which this collection can store.
 */
public class ElementCollection<T extends ChartNode> extends ArrayList<T> {
    private ChartElement owner;

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.engine.elementTree.ElementCollection} class
     * with a specified owner.
     *
     * @param owner an instance of the {@link ChartElement} that will be owner of this collection.
     */
    public ElementCollection(ChartElement owner) {
        this.owner = owner;
    }

    @Override
    public boolean add(T object) {
        this.add(this.size(), object);
        return true;
    }

    @Override
    public void add(int index, T object) {
        insertItem(index, object);
    }

    @Override
    public void clear() {
        clearItems();
    }

    @Override
    public boolean remove(Object obj) {
        int index = this.indexOf(obj);
        if (index < 0) {
            return false;
        }

        this.remove(index);

        return true;
    }

    @Override
    public T remove(int index) {
        T node = this.get(index);

        super.remove(index);

        this.owner.children.remove(node);
        this.shiftNodesIndexes(index - 1, -1);
        return node;
    }

    /**
     * Inserts the provided item at the provided index.
     *
     * @param index the index where to insert the item.
     * @param item  the item to be inserted.
     */
    protected void insertItem(int index, T item) {
        super.add(index, item);

        this.owner.children.add(item);
        item.collectionIndex = index;
        this.shiftNodesIndexes(index, 1);
    }

    /**
     * Clears all items in the collection.
     */
    protected void clearItems() {
        // start from the end of the collection -> better performance since indexes in the owner children's collection will not be shifted
        this.owner.children.clear();
        /*for (int i = this.size() - 1; i >= 0; i--) {
            this.owner.children.remove(this.get(i));
        }*/

        super.clear();
    }

    private void shiftNodesIndexes(int index, int offset) {
        int count = this.size();

        for (int i = index + 1; i < count; i++) {
            this.get(i).collectionIndex += offset;
        }
    }
}

