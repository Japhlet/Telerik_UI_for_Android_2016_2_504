package com.telerik.widget.chart.engine.databinding;

import com.telerik.android.common.Function;

/**
 * Represents a generic callback that retrieves a value from and object.
 *
 * @param <T> The argument type of the generic callback.
 * @param <U> The return type of the generic callback.
 * @see DataPointBinding
 */
public class GenericDataPointBinding<T, U> extends DataPointBinding {

    /**
     * Holds the generic delegate used to retrieve bound objects values.
     */
    private Function<T, U> valueSelector;

    /**
     * Creates an instance of the {@link GenericDataPointBinding} class with specified delegate used
     * to retrieve the bound value.
     *
     * @param valueSelector generic delegate used to retrieve the bound value.
     */
    public GenericDataPointBinding(Function<T, U> valueSelector) {
        if (valueSelector == null) {
            throw new IllegalStateException(
                    "valueSelector can not be null. Please specify a valid valueSelector callback."
            );
        }

        this.valueSelector = valueSelector;
    }

    @Override
    public Object getValue(Object instance) {
        if (instance == null) {
            throw new IllegalArgumentException("instance cannot be null");
        }

        T typedInstance = (T) instance;
        return this.valueSelector.apply(typedInstance);
    }
}
