package com.telerik.widget.chart.engine.decorations.annotations;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * Represents {@link ChartAnnotationModel} and serves as base for all annotations that use a single
 * axis.
 */
public abstract class SingleAxisAnnotationModel extends ChartAnnotationModel {
    private static final int AXIS_PROPERTY_KEY = PropertyKeys.register(SingleAxisAnnotationModel.class, "axis", ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);

    protected boolean isUpdated;

    /**
     * Initializes a new instance of the {@link SingleAxisAnnotationModel} class.
     */
    public SingleAxisAnnotationModel() {
    }

    /**
     * Gets the current axis instance.
     *
     * @return the current instance.
     */
    public AxisModel getAxis() {
        return this.getTypedValue(AXIS_PROPERTY_KEY, null);
    }

    /**
     * Sets the current axis instance.
     *
     * @param value the new axis.
     */
    public void setAxis(AxisModel value) {
        this.setValue(AXIS_PROPERTY_KEY, value);
        this.isUpdated = false;
    }

    @Override
    public boolean isUpdated() {
        return this.isUpdated;
    }
}

