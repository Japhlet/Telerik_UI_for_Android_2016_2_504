package com.telerik.widget.chart.engine.elementTree.events;

/**
 * Encapsulates all the data associated with a change in a {@link com.telerik.widget.chart.engine.propertyStore.PropertyBagObject} property store.
 */
public class RadPropertyEventArgs {

    /**
     * Determines if the event was cancelled or not.
     */
    public boolean Cancel = false;

    private Object newValue;
    private Object oldValue;

    private int key;
    private String propertyName;

    /**
     * Initializes a new instance of the {@link RadPropertyEventArgs} class.
     *
     * @param key      The property key.
     * @param oldValue The old property value.
     * @param newValue The new property value.
     */
    public RadPropertyEventArgs(int key, Object oldValue, Object newValue) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Initializes a new instance of the {@link RadPropertyEventArgs} class.
     *
     * @param name     The property name.
     * @param oldValue The old property value.
     * @param newValue The new property value.
     */
    public RadPropertyEventArgs(String name, Object oldValue, Object newValue) {
        this.propertyName = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Gets the new value.
     */
    public Object newValue() {
        return this.newValue;
    }

    /**
     * Gets the old value.
     */
    public Object oldValue() {
        return this.oldValue;
    }

    /**
     * Gets the key of the property associated with the event.
     */
    public int getKey() {
        return this.key;
    }

    /**
     * Gets the name of the property that has changed. This member is not set if the property is associated with a valid key.
     */
    public String getPropertyName() {
        return this.propertyName;
    }

}

