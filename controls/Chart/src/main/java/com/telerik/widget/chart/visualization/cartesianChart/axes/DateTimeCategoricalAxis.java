package com.telerik.widget.chart.visualization.cartesianChart.axes;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel;
import com.telerik.widget.chart.engine.axes.categorical.DateTimeCategoricalAxisModel;
import com.telerik.widget.chart.engine.axes.common.DateTimeComponent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Represents a categorical axis in a cartesian chart that is used to display categorical date or time values.
 */
public class DateTimeCategoricalAxis extends CategoricalAxis {
    private DateTimeCategoricalAxisModel axisModel;

    public DateTimeCategoricalAxis() {
    }

    public void setLabelFormat(String value) {
        super.resolveLabelRenderer().setLabelFormat(value);
        this.setDateTimeFormat(new SimpleDateFormat(value));
    }

    /**
     * DEPRECATED! Use getDateTimeFormat() instead.
     *
     * @return an instance of the {@link DateFormat} class determining the way the datetime values
     * are displayed.
     */
    @Deprecated()
    public DateFormat getDateFormat() {
        return ((DateTimeCategoricalAxisModel)this.getModel()).getDateFormat();
    }

    /**
     * Gets the format in which the date-time values are displayed.
     *
     * @return an instance of the {@link DateFormat} class determining the way the datetime values
     * are displayed.
     */
    public DateFormat getDateTimeFormat() {
        return ((DateTimeCategoricalAxisModel)this.getModel()).getDateFormat();
    }

    /**
     * Sets the format in which the date-time values are displayed.
     *
     * @param format an instance of the {@link DateFormat} class determining the way the datetime values
     *               are displayed.
     */
    public void setDateTimeFormat(DateFormat format) {
        if (format == ((DateTimeCategoricalAxisModel)this.getModel()).getDateFormat()) {
            return;
        }

        ((DateTimeCategoricalAxisModel)this.getModel()).setDateFormat(format);
    }

    /**
     * Gets a value from the {@link DateTimeComponent} enum determining the component of the
     * {@link java.util.Calendar} structure that will be displayed on this axis.
     *
     * @return the component.
     */
    public DateTimeComponent getDateTimeComponent() {
        return ((DateTimeCategoricalAxisModel)this.getModel()).getDateTimeComponent();
    }

    /**
     * Sets a value from the {@link DateTimeComponent} enum determining the component of the
     * {@link java.util.Calendar} structure that will be displayed on this axis.
     */
    public void setDateTimeComponent(DateTimeComponent value) {
        ((DateTimeCategoricalAxisModel)this.getModel()).setDateTimeComponent(value);
    }

    @Override
    protected AxisModel createModel() {
        this.axisModel = new DateTimeCategoricalAxisModel();
        return this.axisModel;
    }
}

