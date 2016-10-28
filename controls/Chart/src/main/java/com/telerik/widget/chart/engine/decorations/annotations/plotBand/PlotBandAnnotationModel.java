package com.telerik.widget.chart.engine.decorations.annotations.plotBand;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.SingleAxisAnnotationModel;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.propertyStore.ValueExtractor;

/**
 * Represents a {@link SingleAxisAnnotationModel} that introduces additional two plots and the
 * functionality to display a band annotation placed along two given objects.
 */
abstract class PlotBandAnnotationModel extends SingleAxisAnnotationModel {

    private static final int FROM_PROPERTY_KEY = PropertyKeys.register(PlotBandAnnotationModel.class, "From", ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);
    private static final int TO_PROPERTY_KEY = PropertyKeys.register(PlotBandAnnotationModel.class, "To", ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS);

    private Object from;
    private Object to;

    private boolean isFirstPlotUpdated;
    private boolean isSecondPlotUpdated;

    private void updateFirstPlot() {
        final AxisModel axis = getAxis();
        if (axis == null || this.from == null || !axis.isDataReady() || !axis.isUpdated())
            return;

        final ValueExtractor<AxisPlotInfo> extractor = new ValueExtractor<AxisPlotInfo>();
        extractor.value = this.firstPlotInfo;

        this.isFirstPlotUpdated = ChartAnnotationModel.tryCreatePlotInfo(axis, this.from, extractor);

        this.firstPlotInfo = extractor.value;
    }

    private void updateSecondPlot() {
        final AxisModel axis = getAxis();
        if (axis == null || this.to == null || !axis.isDataReady() || !axis.isUpdated())
            return;

        final ValueExtractor<AxisPlotInfo> extractor = new ValueExtractor<AxisPlotInfo>();
        extractor.value = this.secondPlotInfo;

        this.isSecondPlotUpdated = ChartAnnotationModel.tryCreatePlotInfo(axis, this.to, extractor);

        this.secondPlotInfo = extractor.value;
    }

    /**
     * Instance holding the info for the first plot.
     */
    protected AxisPlotInfo firstPlotInfo;

    /**
     * Instance holding the info for the second plot.
     */
    protected AxisPlotInfo secondPlotInfo;

    /**
     * Initializes a new instance of the {@link PlotBandAnnotationModel} class.
     */
    public PlotBandAnnotationModel() {
    }

    /**
     * Gets the object from which the band starts.
     *
     * @return the object from which the band starts.
     */
    public Object getFrom() {
        return this.getValue(FROM_PROPERTY_KEY);
    }

    /**
     * Sets the object from which the band starts.
     *
     * @param value the new starting object.
     */
    public void setFrom(Object value) {
        this.setValue(FROM_PROPERTY_KEY, value);
    }

    /**
     * Gets the object at which the band ends.
     *
     * @return the ending band object.
     */
    public Object getTo() {
        return this.getValue(TO_PROPERTY_KEY);
    }

    /**
     * Sets the object at which the band ends.
     *
     * @param value the new object at which the band ends.
     */
    public void setTo(Object value) {
        this.setValue(TO_PROPERTY_KEY, value);
    }

    /**
     * Used to reset the update state of the two plots.
     */
    public void resetState() {
        this.isFirstPlotUpdated = false;
        this.isSecondPlotUpdated = false;
    }

    @Override
    public boolean isUpdated() {
        return this.isFirstPlotUpdated && this.isSecondPlotUpdated;
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == FROM_PROPERTY_KEY) {
            this.from = e.newValue();
            this.isUpdated = false;

            this.updateFirstPlot();
        } else if (e.getKey() == TO_PROPERTY_KEY) {
            this.to = e.newValue();
            this.isUpdated = false;

            this.updateSecondPlot();
        }

        super.onPropertyChanged(e);
    }

    @Override
    protected void updateCore() {
        this.updateFirstPlot();
        this.updateSecondPlot();
    }
}

