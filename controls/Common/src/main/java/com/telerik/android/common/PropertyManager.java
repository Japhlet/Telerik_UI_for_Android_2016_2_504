package com.telerik.android.common;

import java.util.HashMap;

public abstract class PropertyManager {
    protected static final int LOCAL_VALUE = 0;
    protected static final int PALETTE_VALUE = 1;
    protected static final int DEFAULT_VALUE = 2;
    protected static final Object UNSET_VALUE = new Object();

    private static int propertyKeyCounter = 0;

    private static class PropertyBag {
        private Object[] propertyBag = new Object[3];

        public PropertyBag(Object defaultValue) {
            this.propertyBag[LOCAL_VALUE] = UNSET_VALUE;
            this.propertyBag[PALETTE_VALUE] = UNSET_VALUE;
            this.propertyBag[DEFAULT_VALUE] = defaultValue;
        }

        public Object resolveValue() {
            for (Object value : this.propertyBag) {
                if (value != UNSET_VALUE) {
                    return value;
                }
            }

            throw new IllegalStateException("This property has no default value assigned.");
        }

        public void setValueForKey(int key, Object value) {
            this.propertyBag[key] = value;
        }
    }

    private static class PropertyMetadata {
        public HashMap<Integer, PropertyBag> propertyBags = new HashMap<Integer, PropertyBag>();
        private DependencyPropertyChangedListener changedListener;
        private Object defaultValue;

        public PropertyMetadata(Object value, DependencyPropertyChangedListener listener) {
            if (value == UNSET_VALUE) {
                throw new IllegalArgumentException("Default value cannot be null or UNSET_VALUE.");
            }

            this.defaultValue = value;
            this.changedListener = listener;
        }

        public Object defaultValue() {
            return this.defaultValue;
        }

        public DependencyPropertyChangedListener getListener() {
            return this.changedListener;
        }
    }

    private static final HashMap<Integer, PropertyMetadata> propertiesMetadata = new HashMap<>();

    protected static int registerProperty(Object defaultValue, DependencyPropertyChangedListener changeListener) {
        propertiesMetadata.put(propertyKeyCounter, new PropertyMetadata(defaultValue, changeListener));

        int result = propertyKeyCounter;
        propertyKeyCounter++;
        return result;
    }

    public void setValue(int propertyKey, Object value) {
        this.setValue(propertyKey, LOCAL_VALUE, value);
    }

    public void setValue(int propertyKey, int valueTypeKey, Object value) {
        PropertyMetadata metadata = propertiesMetadata.get(propertyKey);

        PropertyBag bag;
        if (metadata.propertyBags.containsKey(this.hashCode())) {
            bag = metadata.propertyBags.get(this.hashCode());

        } else {
            bag = new PropertyBag(metadata.defaultValue);
            metadata.propertyBags.put(this.hashCode(), bag);
        }

        bag.setValueForKey(valueTypeKey, value);

        if (metadata.getListener() != null) {
            metadata.getListener().onPropertyChanged(this, valueTypeKey, value);
        }
    }

    public Object getValue(int propertyKey) {
        PropertyMetadata metadata = propertiesMetadata.get(propertyKey);
        if (metadata.propertyBags.containsKey(this.hashCode())) {
            PropertyBag bag = metadata.propertyBags.get(this.hashCode());

            return bag.resolveValue();
        }

        return propertiesMetadata.get(propertyKey).defaultValue();
    }

    public void resetPropertyValue(int propertyKey) {
        this.setValue(propertyKey, LOCAL_VALUE, UNSET_VALUE);
    }
}
