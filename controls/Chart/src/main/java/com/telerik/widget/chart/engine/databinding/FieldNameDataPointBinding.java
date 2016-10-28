package com.telerik.widget.chart.engine.databinding;

import java.lang.reflect.Field;

/**
 * This class uses reflection to extract values from public fields.
 * This class is used in RadChartView's data binding mechanism.
 *
 * @see ReflectiveDataPointBinding
 */
public class FieldNameDataPointBinding extends ReflectiveDataPointBinding {

    /**
     * Creates an instance of the {@link FieldNameDataPointBinding} class.
     *
     * @param fieldName The name of the bound field.
     * @see ReflectiveDataPointBinding
     * @see DataPointBinding
     */
    public FieldNameDataPointBinding(String fieldName) {
        super(fieldName);
    }

    /**
     * Gets the name of the bound field.
     *
     * @return The name of the bound field.
     */
    public String getFieldName() {
        return this.getName();
    }

    /**
     * Sets the name of the bound field.
     *
     * @param fieldName The name of the bound field.
     */
    public void setFieldName(String fieldName) {
        super.setName(fieldName);
    }

    @Override
    protected Object getMemberValue(Object instance) {
        Object result = null;
        try {
            Field getter = instance.getClass().getDeclaredField(this.getFieldName());
            getter.setAccessible(true);
            result = getter.get(instance);
        } catch (Exception e) {
            throw new Error(e);
        }
        return result;
    }
}
