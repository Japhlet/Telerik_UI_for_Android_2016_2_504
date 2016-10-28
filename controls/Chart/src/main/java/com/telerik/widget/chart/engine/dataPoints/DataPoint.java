package com.telerik.widget.chart.engine.dataPoints;

import android.graphics.Point;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.view.ChartSeries;

/**
 * Base class for all points that may be plotted by a
 * {@link com.telerik.widget.chart.engine.chartAreas.ChartPlotAreaModel}.
 */
public abstract class DataPoint extends ChartNode {
    private boolean isSelected;

    /**
     * Registered property key for IsSelected.
     */
    protected static final int IS_SELECTED_PROPERTY_KEY =
            PropertyKeys.register(DataPoint.class, "IsSelected");

    /**
     * Registered property key for Label.
     */
    protected static final int LABEL_PROPERTY_KEY = PropertyKeys.register(DataPoint.class, "Label");

    /**
     * States whether the current data point is positive.
     */
    public boolean isPositive = false;

    private void notifySelectionChanged() {
        if (!this.isTreeLoaded()) {
            return;
        }

        ChartSeries series = (ChartSeries) this.getPresenter();
        if (series != null) {
            series.onDataPointIsSelectedChanged(this);
        }
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        if (e.getKey() == LABEL_PROPERTY_KEY) {
            this.label = e.newValue();
        } else if (e.getKey() == IS_SELECTED_PROPERTY_KEY) {
            this.isSelected = (Boolean) e.newValue();
            this.notifySelectionChanged();
        }

        super.onPropertyChanged(e);
    }

    @Override
    protected void unloadCore() {
        super.unloadCore();

        this.dataItem = null;
    }

    /**
     * States whether the current data point is empty.
     */
    public boolean isEmpty = false;

    /**
     * The actual size of the data point.
     */
    public RadSize desiredSize;

    /**
     * The label for the current data point instance.
     */
    protected Object label;

    /**
     * The data item for the current data point instance.
     */
    protected Object dataItem;

    /**
     * Initializes a new instance of the {@link DataPoint} class.
     */
    protected DataPoint() {
        this.desiredSize = RadSize.getInvalid();
        this.trackPropertyChanged = true;
        this.isEmpty = true;
    }

    /**
     * Gets the label associated with this point.
     *
     * @return the associated label.
     */
    public Object getLabel() {
        if (this.label != null) {
            return this.label;
        }

        return this.getDefaultLabel();
    }

    /**
     * Sets the label associated with this point.
     *
     * @param value the new label.
     */
    public void setLabel(Object value) {
        this.setValue(LABEL_PROPERTY_KEY, value);
    }

    /**
     * Gets the Object instance that represents the data associated with this point.&nbsp;
     * Valid when the owning ChartSeries is data-bound.
     *
     * @return the current data item.
     */
    public Object getDataItem() {
        return this.dataItem;
    }

    /**
     * Gets the Object instance that represents the data associated with this point.&nbsp;
     * Valid when the owning ChartSeries is data-bound.
     *
     * @param value the new data item.
     */
    public void setDataItem(Object value) {
        this.dataItem = value;
    }

    /**
     * Gets a value indicating whether the data point is currently in a "Selected" state.
     *
     * @return the selected state of the point.
     */
    public boolean getIsSelected() {
        return this.isSelected;
    }

    /**
     * Sets a value indicating whether the data point is currently in a "Selected" state.
     *
     * @param value the new state of the point.
     */
    public void setIsSelected(boolean value) {
        this.setValue(IS_SELECTED_PROPERTY_KEY, value);
    }

    /**
     * Checks if the given value instance is empty.
     *
     * @param value the value to be checked.
     * @return <code>true</code> if the value is empty and <code>false</code> otherwise.
     */
    protected static boolean checkIsEmpty(Object value) {
        return (value == null) || (value instanceof Double && Double.isNaN(((Number) value).doubleValue()));
    }

    /**
     * Gets the Object that may be displayed for this data point by the widget tooltip.
     *
     * @return the object to display for the current data point.
     */
    public Object[] getTooltipTokens() {
        return null;
    }

    /**
     * Gets value corresponding to the given axis which varies among the different types of axes
     * and chart implementations.
     *
     * @param axis the axis to which the value is associated.
     * @return the corresponding value.
     */
    public Object getValueForAxis(AxisModel axis) {
        return null;
    }

    /**
     * Sets a value by converting it to a plot instance corresponding to the current chart type
     * and assigns it accordingly to the type of the given axis.
     *
     * @param axis  the axis providing the type of assignment that will occur.
     * @param value the value from which to generate the plot info.
     */
    public void setValueFromAxis(AxisModel axis, Object value) {
    }

    /**
     * Gets the corresponding default label.
     *
     * @return the default label.
     */
    Object getDefaultLabel() {
        return null;
    }

    /**
     * Gets the center along the x axis.
     *
     * @return the center along the x axis.
     */
    public double getCenterX() {
        return this.getLayoutSlot().getX() + (int) (this.getLayoutSlot().getWidth() / 2);
    }

    /**
     * Gets the center along the y axis.
     *
     * @return the center along the y axis.
     */
    public double getCenterY() {
        return this.getLayoutSlot().getY() + (int) (this.getLayoutSlot().getHeight() / 2);
    }

    /**
     * Gets the center of the data point.
     *
     * @return the center of the data point.
     */
    public Point getCenter() {
        return new Point((int) getCenterX(), (int) getCenterY());
    }
}