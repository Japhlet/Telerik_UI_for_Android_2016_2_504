package com.telerik.android.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class wrap raw instances from the source provided to the associated
 * {@link com.telerik.android.data.RadDataSource} object. When a {@link com.telerik.android.data.RadDataSource}
 * instance is populated with data, each entity from the original data source is wrapped in a {@link com.telerik.android.data.DataItem}
 * instance end exposed by the API of this class.
 */
public class DataItem<E> {

    private E sourceObject;
    private ArrayList<DataItem<E>> items = new ArrayList<DataItem<E>>();
    private Object groupKey;

    /**
     * Creates an instance of the {@link com.telerik.android.data.DataItem}
     * class with a provided source object.
     * @param fromObject the source object used to create the group.
     */
    public DataItem(E fromObject, Object groupKey){
        this.sourceObject = fromObject;
        this.groupKey = groupKey;
    }

    /**
     * Creates an instance of the {@link com.telerik.android.data.DataItem} class
     * with the provided source entity from the original data used to populate the associated
     * {@link com.telerik.android.data.RadDataSource} object.
     *
     * @param forObject the raw entity object.
     */
    public DataItem(E forObject) {
        this.sourceObject = forObject;
    }

    /**
     * Returns the raw object wrapped by this {@link com.telerik.android.data.DataItem} instance.
     *
     * @return the original data entity.
     */
    public E entity(){
        return this.sourceObject;
    }

    /**
     * Gets the group key of this item. If this item does not have child items this method will return null.
     */
    public Object groupKey() {
        return this.groupKey;
    }

    /**
     * Returns the child items containing within this {@link com.telerik.android.data.DataItem}
     * instance. These might either be further {@link com.telerik.android.data.DataItem} instances
     * or plain {@link com.telerik.android.data.DataItem} instances.
     *
     * @return an {@link java.lang.Iterable} implementation containing the child items.
     */
    public List<DataItem<E>> getItems(){
        return this.items;
    }

    void setItems(Iterable<DataItem<E>> items) {
        if(items == null) {
            throw new IllegalArgumentException("items cannot be null.");
        }

        if(items == this.items) {
            return;
        }

        this.items.clear();
        for(DataItem<E> item : items) {
            this.items.add(item);
        }
    }

    @Override
    public String toString() {
        if(this.groupKey != null) {
            return this.groupKey.toString();
        } else if (this.sourceObject != null) {
            return this.sourceObject.toString();
        }

        return super.toString();
    }
}
