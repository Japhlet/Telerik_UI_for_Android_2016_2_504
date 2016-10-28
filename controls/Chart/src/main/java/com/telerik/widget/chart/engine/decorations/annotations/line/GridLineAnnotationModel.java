package com.telerik.widget.chart.engine.decorations.annotations.line;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.SingleAxisAnnotationModel;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.propertyStore.ValueExtractor;

/**
 * The base class for line annotations on a single axis.
 */
public abstract class GridLineAnnotationModel extends SingleAxisAnnotationModel {
    public static final int VALUE_PROPERTY_KEY = PropertyKeys.register(GridLineAnnotationModel.class, "Value", ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);
    AxisPlotInfo plotInfo;
    Object value;

    /**
     * Creates a new instance of the {@link GridLineAnnotationModel} class.
     */
    public GridLineAnnotationModel() {
    }

    /**
     * Gets the value on which this annotation will be plotted.
     *
     * @return The annotation value.
     */
    public Object getValue() {
        return this.getValue(VALUE_PROPERTY_KEY);
    }

    /**
     * Sets the value on which this annotation will be plotted.
     *
     * @param value The annotation value.
     */
    public void setValue(Object value) {
        this.setValue(VALUE_PROPERTY_KEY, value);
    }

    @Override
    public void resetState() {
        this.isUpdated = false;
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == VALUE_PROPERTY_KEY) {
            this.value = e.newValue();
            this.isUpdated = false;
        }

        super.onPropertyChanged(e);
    }

    @Override
    protected void updateCore() {
        final AxisModel axis = getAxis();
        if (axis == null || value == null || !axis.isUpdated() || !axis.isDataReady())
            return;

        final ValueExtractor<AxisPlotInfo> extractor = new ValueExtractor<AxisPlotInfo>();
        extractor.value = this.plotInfo;

        this.isUpdated = ChartAnnotationModel.tryCreatePlotInfo(axis, this.value, extractor);
        if (!isUpdated)
            throw new IllegalArgumentException(String.format("The value: '%s' provided for the annotation is incompatible with the selected axis.", value));

        this.plotInfo = extractor.value;
    }
}
