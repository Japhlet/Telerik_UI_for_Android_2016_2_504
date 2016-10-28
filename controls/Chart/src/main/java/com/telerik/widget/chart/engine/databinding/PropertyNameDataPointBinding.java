package com.telerik.widget.chart.engine.databinding;

import java.lang.reflect.Method;

/**
 * This class uses reflection to get the value of a field that has a public getter.
 *
 * @see ReflectiveDataPointBinding
 */
public class PropertyNameDataPointBinding extends ReflectiveDataPointBinding {

    /**
     * Creates an instance of the {@link PropertyNameDataPointBinding} class.
     *
     * @param propertyName The name of the bound property.
     * @see ReflectiveDataPointBinding
     * @see DataPointBinding
     */
    public PropertyNameDataPointBinding(String propertyName) {
        super(propertyName);
    }

    /**
     * Gets the name of the bound property.
     *
     * @return The name of the bound property.
     */
    public String getPropertyName() {
        return this.getName();
    }

    /**
     * Sets the name of the bound property.
     *
     * @param propertyName The name of the bound property.
     */
    public void setPropertyName(String propertyName) {
        if (this.setName(propertyName)) {
            this.onPropertyChanged("PropertyName");
        }
    }

    @Override
    protected Object getMemberValue(Object instance) {
        Object result = null;
        try {
            final String get = "get";
            Method getter = instance.getClass().getMethod(get.concat(this.getPropertyName()));
            result = getter.invoke(instance);
        } catch (Exception e) {
            throw new Error(e);
        }
        return result;
    }
}