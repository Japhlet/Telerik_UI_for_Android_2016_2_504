package com.telerik.widget.chart.engine.decorations.annotations.custom;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.MultipleAxesAnnotationModel;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.propertyStore.ValueExtractor;

/**
 * Base class for all custom annotation models.
 */
public abstract class CustomAnnotationModel extends MultipleAxesAnnotationModel {

    static final int FIRST_VALUE_PROPERTY_KEY = PropertyKeys.register(CustomAnnotationModel.class, "FirstValue", ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);
    static final int SECOND_VALUE_PROPERTY_KEY = PropertyKeys.register(CustomAnnotationModel.class, "SecondValue", ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);

    Object firstValue;
    Object secondValue;
    AxisPlotInfo firstPlotInfo;
    AxisPlotInfo secondPlotInfo;
    boolean isFirstPlotUpdated;
    boolean isSecondPlotUpdated;

    public RadSize desiredSize;

    /**
     * Creates a new instance of the {@link CustomAnnotationModel} class.
     */
    public CustomAnnotationModel() {
        this.desiredSize = RadSize.getInvalid();
    }

    /**
     * Gets the first value.
     *
     * @return The first value.
     */
    public Object getFirstValue() {
        return this.getValue(FIRST_VALUE_PROPERTY_KEY);
    }

    /**
     * Sets the first value.
     *
     * @param value The first value.
     */
    public void setFirstValue(Object value) {
        this.setValue(FIRST_VALUE_PROPERTY_KEY, value);
    }

    /**
     * Gets the second value.
     *
     * @return The second value.
     */
    public Object getSecondValue() {
        return this.getValue(SECOND_VALUE_PROPERTY_KEY);
    }

    /**
     * Sets the second value.
     *
     * @param value The second value.
     */
    public void setSecondValue(Object value) {
        this.setValue(SECOND_VALUE_PROPERTY_KEY, value);
    }


    @Override
    public boolean isUpdated() {
        return this.isFirstPlotUpdated && this.isSecondPlotUpdated;
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == FIRST_VALUE_PROPERTY_KEY) {
            this.firstValue = e.newValue();

            this.updateFirstPlot();
        } else if (e.getKey() == SECOND_VALUE_PROPERTY_KEY) {
            this.secondValue = e.newValue();

            this.updateSecondPlot();
        }

        super.onPropertyChanged(e);
    }

    @Override
    public void resetState() {
        this.isFirstPlotUpdated = false;
        this.isSecondPlotUpdated = false;
    }

    RadSize measure() {
        if (this.desiredSize.equals(RadSize.getInvalid())) {
            this.desiredSize = this.getPresenter().measureContent(this, this);
        }

        return this.desiredSize;
    }

    @Override
    protected void updateCore() {
        this.updateFirstPlot();
        this.updateSecondPlot();
    }

    @Override
    protected void onFirstAxisChanged() {
        super.onFirstAxisChanged();

        this.updateFirstPlot();
    }

    @Override
    protected void onSecondAxisChanged() {
        super.onSecondAxisChanged();

        this.updateSecondPlot();
    }

    private void updateFirstPlot() {
        final AxisModel axis = this.getFirstAxis();
        if (!validateUpdateTokens(axis, this.firstValue))
            return;

        final ValueExtractor<AxisPlotInfo> extractor = new ValueExtractor<AxisPlotInfo>();
        extractor.value = this.firstPlotInfo;

        this.isFirstPlotUpdated = ChartAnnotationModel.tryCreatePlotInfo(axis, this.firstValue, extractor);
        if (!isFirstPlotUpdated)
            throw new IllegalArgumentException(String.format("the value: %s proved to be incompatible with its corresponding axis", firstValue.toString()));

        this.firstPlotInfo = extractor.value;
    }

    private void updateSecondPlot() {
        final AxisModel axis = this.getSecondAxis();
        if (!validateUpdateTokens(axis, this.secondValue))
            return;

        final ValueExtractor<AxisPlotInfo> extractor = new ValueExtractor<AxisPlotInfo>();
        extractor.value = this.secondPlotInfo;
        this.isSecondPlotUpdated = ChartAnnotationModel.tryCreatePlotInfo(this.getSecondAxis(), this.secondValue, extractor);
        if (!isSecondPlotUpdated)
            throw new IllegalArgumentException(String.format("the value: %s proved to be incompatible with its corresponding axis", secondValue.toString()));

        this.secondPlotInfo = extractor.value;
    }

    private boolean validateUpdateTokens(final AxisModel axis, final Object value) {
        return value != null && axis != null && axis.isUpdated() && axis.isDataReady();
    }
}

