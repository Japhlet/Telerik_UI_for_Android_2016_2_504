package com.telerik.widget.chart.engine.propertyStore;

/**
 * This class provides infrastructure for storing properties within a property bag for faster retrieval. This class
 * is abstract and cannot be used directly within your application.
 */
public abstract class PropertyBagObject {

    protected FastPropertyStore propertyStore;

    /**
     * Creates a new instance of the {@link PropertyBagObject} class
     * with an empty property bag.
     */
    protected PropertyBagObject() {
        this.propertyStore = new FastPropertyStore();
    }

    /**
     * Determines whether the property with the specified key has a value stored
     * in the property bag.
     *
     * @param key the key of the property to check.
     * @return <code>true<code/> if there is a value for the property, otherwise <code>false</code>.
     */
    public boolean isLocalValue(int key) {
        return this.propertyStore.containsEntry(key);
    }

    /**
     * Gets the value for the property with the specified key.
     *
     * @param key the key of the property.
     * @return the value of the property.
     */
    public Object getValue(int key) {
        return this.propertyStore.getEntry(key);
    }

    /**
     * Sets the property specified by the provided key to the provided object.
     *
     * @param key   the key of the property to set.
     * @param value the value of the property.
     * @return <code>true</code> if the property has been successfully set, otherwise <code>false</code>.
     */
    public boolean setValue(int key, Object value) {
        this.propertyStore.setEntry(key, value);
        return true;
    }

    /**
     * Clears the current value for the specified property key.
     *
     * @param key the key of the property to clear.
     * @return <code>true</code> if the property has been successfully cleared, otherwise <code>false</code>.
     */
    public boolean clearValue(int key) {
        this.propertyStore.removeEntry(key);
        return true;
    }

    /**
     * Gets the value for the specified property key whereby if no value is present the specified default value is returned.
     *
     * @param key          the key of the property.
     * @param defaultValue the default value.
     * @param <T>          the type of the expected value.
     * @return the value from the property store if present, otherwise the provided default one.
     */
    public <T> T getTypedValue(int key, T defaultValue) {
        Object localValue = this.getValue(key);
        if (localValue != null) {
            return (T) localValue;
        }

        return defaultValue;
    }
}

