package com.telerik.widget.chart.engine.decorations.annotations;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * Represents the {@link ChartAnnotationModel} and serves as base for all annotations that use
 * multiple axes.
 */
public abstract class MultipleAxesAnnotationModel extends ChartAnnotationModel {

    private static final int FIRST_AXIS_PROPERTY_KEY =
            PropertyKeys.register(MultipleAxesAnnotationModel.class, "FirstAxis",
                    ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);

    private static final int SECOND_AXIS_PROPERTY_KEY =
            PropertyKeys.register(MultipleAxesAnnotationModel.class, "SecondAxis",
                    ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);

    /*private AxisModel firstAxis;
    private AxisModel secondAxis;*/

    /**
     * Initializes a new instance of the {@link MultipleAxesAnnotationModel} class.
     */
    public MultipleAxesAnnotationModel() {
    }

    /**
     * Gets the first axis instance for the current annotation.
     *
     * @return the current first axis.
     */
    public AxisModel getFirstAxis() {
        return this.getTypedValue(FIRST_AXIS_PROPERTY_KEY, null);
    }

    /**
     * Sets the first axis instance for the current annotation.
     *
     * @param value the new axis instance.
     */
    public void setFirstAxis(AxisModel value) {
        this.setValue(FIRST_AXIS_PROPERTY_KEY, value);
    }

    /**
     * Gets the second axis instance for the current annotation.
     *
     * @return the current second axis.
     */
    public AxisModel getSecondAxis() {
        return this.getTypedValue(SECOND_AXIS_PROPERTY_KEY, null);
    }

    /**
     * Sets the second axis instance for the current annotation.
     *
     * @param value the new axis instance.
     */
    public void setSecondAxis(AxisModel value) {
        this.setValue(SECOND_AXIS_PROPERTY_KEY, value);
    }

    /**
     * Gets called whenever a change has been made to the first axis.
     */
    protected void onFirstAxisChanged() {
    }

    /**
     * Gets called whenever a change has been made to the second axis.
     */
    protected void onSecondAxisChanged() {
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == FIRST_AXIS_PROPERTY_KEY) {
            //this.firstAxis = (AxisModel) e.newValue();

            this.onFirstAxisChanged();
        } else if (e.getKey() == SECOND_AXIS_PROPERTY_KEY) {
            //this.secondAxis = (AxisModel) e.newValue();

            this.onSecondAxisChanged();
        }

        super.onPropertyChanged(e);
    }
}

