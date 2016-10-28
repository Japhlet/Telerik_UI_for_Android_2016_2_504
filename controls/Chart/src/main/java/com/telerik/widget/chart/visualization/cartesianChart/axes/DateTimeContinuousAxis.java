package com.telerik.widget.chart.visualization.cartesianChart.axes;

import com.telerik.android.common.Function2;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.common.TimeInterval;
import com.telerik.widget.chart.engine.axes.continuous.DateTimeContinuousAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.ValueRange;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ElementCollection;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.common.CartesianAxis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a continuous axis used in a cartesian chart on which date-time values are plotted.
 */
public class DateTimeContinuousAxis extends CartesianAxis {
    private Function2<Calendar, Calendar, Boolean> dateComparator;
    private Function2<Calendar, Calendar, Boolean> defaultDateComparator = new Function2<Calendar, Calendar, Boolean>() {
        @Override
        public Boolean apply(Calendar lhs, Calendar rhs) {
            return compareDates(lhs, rhs, DateTimeContinuousAxis.this.getMajorStepUnit());
        }
    };

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.axes.DateTimeContinuousAxis}
     * class with a specified context, a set of styleable attributes and an id of the default style.
     */
    public DateTimeContinuousAxis() {
    }

    private DateTimeContinuousAxisModel getTypedModel() {
        return (DateTimeContinuousAxisModel) this.getModel();
    }

    /**
     * Gets the maximum ticks that might be displayed on the axis.
     * This property is useful in some corner cases when ticks' count may become a really big number.
     *
     * @return the maximum count of ticks for this axis.
     */
    public int getMaximumTicks() {
        return this.getTypedModel().getMaximumTicks();
    }

    /**
     * Sets the maximum ticks that might be displayed on the axis.
     * This property is useful in some corner cases when ticks' count may become a really big number.
     *
     * @param value the maximum count of ticks for this axis.
     */
    public void setMaximumTicks(int value) {
        this.getTypedModel().setMaximumTicks(value);
    }

    /**
     * Gets the distance in pixels between a tick and the adjacent data point.
     *
     * @return the distance in pixels.
     */
    public double getGapLength() {
        return getTypedModel().getGapLength();
    }

    /**
     * Sets the distance in pixels between a tick and the adjacent data point.
     *
     * @param value the distance in pixels.
     */
    public void setGapLength(double value) {
        this.getTypedModel().setGapLength(value);
    }

    /**
     * Gets the major step between two ticks. The major step represents the value difference
     * between two visible ticks on the axis.
     *
     * @return the value of the major step.
     */
    public double getMajorStep() {
        return this.getTypedModel().getMajorStep();
    }

    /**
     * Sets the major step between two ticks. The major step represents the value difference
     * between two visible ticks on the axis.
     *
     * @param value the value of the major step.
     */
    public void setMajorStep(double value) {
        this.getTypedModel().setMajorStep(value);
    }

    /**
     * Gets the unit that defines the custom major step of the axis.
     * If no explicit step is defined, the axis will automatically calculate one,
     * depending on the smallest difference between any two dates.
     *
     * @return the custom major step unit.
     */
    public TimeInterval getMajorStepUnit() {
        return this.getTypedModel().getMajorStepUnit();
    }


    /**
     * Sets the unit that defines the custom major step of the axis.
     * If no explicit step is defined, the axis will automatically calculate one,
     * depending on the smallest difference between any two dates.
     *
     * @param value the custom major step unit.
     */
    public void setMajorStepUnit(TimeInterval value) {
        this.getTypedModel().setMajorStepUnit(value);
    }

    /**
     * Gets an instance of the {@link java.util.Calendar} class representing
     * the smallest value which this axis represents.
     *
     * @return the smallest value.
     */
    public Calendar getMinimum() {
        return this.getTypedModel().getMinimum();
    }

    /**
     * Sets an instance of the {@link java.util.Calendar} class representing
     * the smallest value which this axis represents.
     *
     * @param value the smallest value.
     */
    public void setMinimum(Calendar value) {
        this.getTypedModel().setMinimum(value);
    }

    /**
     * Gets an instance of the {@link java.util.Calendar} class representing
     * the biggest value which this axis represents.
     *
     * @return the biggest value.
     */
    public Calendar getMaximum() {
        return this.getTypedModel().getMaximum();
    }

    /**
     * Sets an instance of the {@link java.util.Calendar} class representing
     * the biggest value which this axis represents.
     *
     * @param value the biggest value.
     */
    public void setMaximum(Calendar value) {
        this.getTypedModel().setMaximum(value);
    }

    /**
     * Gets a value from the {@link AxisPlotMode} enum which determines in what way the axis will
     * be plotted on the viewport of the Chart.
     *
     * @return the current {@link com.telerik.widget.chart.engine.axes.common.AxisPlotMode} value.
     */
    public AxisPlotMode getPlotMode() {
        return this.getTypedModel().getPlotMode();
    }

    /**
     * Sets a value from the {@link AxisPlotMode} enum which determines in what way the axis will
     * be plotted on the viewport of the Chart.
     *
     * @param value the current {@link com.telerik.widget.chart.engine.axes.common.AxisPlotMode} value.
     */
    public void setPlotMode(AxisPlotMode value) {
        this.getTypedModel().setPlotMode(value);
    }

    public void setLabelFormat(String value) {
        super.resolveLabelRenderer().setLabelFormat(value);
        this.setDateTimeFormat(new SimpleDateFormat(value));
    }

    /**
     * Sets a custom date comparator. It is used by the trackball to determine if two dates should
     * be considered the same and displayed in the same popup.
     */
    public void setDateComparer(Function2<Calendar, Calendar, Boolean> value) {
        this.dateComparator = value;
    }

    /**
     * Gets a custom date comparator. It is used by the trackball to determine if two dates should
     * be considered the same and displayed in the same popup.
     */
    public Function2<Calendar, Calendar, Boolean> getDateComparer() {
        return this.dateComparator;
    }

    /**
     * Sets the date format object that will be used to convert the label dates to string.
     */
    public void setDateTimeFormat(DateFormat value) {
        this.getTypedModel().setDateFormat(value);
    }

    /**
     * Gets the date format object that will be used to convert the label dates to string.
     */
    public DateFormat getDateTimeFormat() {
        return this.getTypedModel().getDateFormat();
    }

    /**
     * Gets an instance of the {@link com.telerik.widget.chart.engine.axes.continuous.ValueRange}
     * class representing the value range currently represented by this axis.
     *
     * @return the value range.
     */
    public ValueRange<Calendar> getActualRange() {
        return getTypedModel().getActualRange();
    }

    @Override
    protected AxisModel createModel() {
        return new DateTimeContinuousAxisModel();
    }

    @Override
    public List<DataPoint> getDataPointsForValue(Object value) {
        Calendar date = (Calendar) value;

        ElementCollection<ChartSeriesModel> series = this.getModel().chartArea().getSeries();

        ArrayList<DataPoint> result = new ArrayList<>();
        CategoricalDataPointBase closestPoint = findClosestPoint(date, series);
        Calendar closestCategory = (Calendar)closestPoint.getCategory();
        result.add(closestPoint);

        Function2<Calendar, Calendar, Boolean> comparator = this.dateComparator;
        if(comparator == null) {
            comparator = defaultDateComparator;
        }

        for (ChartSeriesModel seriesModel : series) {
            for(Object point : seriesModel.visibleDataPoints()) {
                if(point == closestPoint) {
                    continue;
                }

                CategoricalDataPointBase dataPoint = (CategoricalDataPointBase)point;
                Calendar category = (Calendar)dataPoint.getCategory();

                if(comparator.apply(category, closestCategory)) {
                    result.add(dataPoint);
                }
            }
        }

        return result;
    }

    private CategoricalDataPointBase findClosestPoint(Calendar date, ElementCollection<ChartSeriesModel> series) {
        long min = Long.MAX_VALUE;
        CategoricalDataPointBase closestPoint = null;

        for(ChartSeriesModel seriesModel : series) {
            for (Object point : seriesModel.visibleDataPoints()) {
                CategoricalDataPointBase dataPoint = (CategoricalDataPointBase) point;
                Calendar category = (Calendar) dataPoint.getCategory();

                long diff = Math.abs(category.getTimeInMillis() - date.getTimeInMillis());
                if (diff < min) {
                    min = diff;
                    closestPoint = dataPoint;
                }
            }
        }

        return closestPoint;
    }

    private static boolean compareDates(Calendar category, Calendar date, TimeInterval stepUnit) {
        if (stepUnit == TimeInterval.MILLISECOND) {
            return category.get(Calendar.MILLISECOND) == date.get(Calendar.MILLISECOND);
        }

        if (stepUnit == TimeInterval.TIME_IN_MILLIS) {
            return category.getTimeInMillis() == date.getTimeInMillis();
        }

        if (stepUnit == TimeInterval.SECOND) {
            return category.get(Calendar.SECOND) == date.get(Calendar.SECOND);
        }

        if (stepUnit == TimeInterval.MINUTE) {
            return category.get(Calendar.MINUTE) == date.get(Calendar.MINUTE);
        }

        if (stepUnit == TimeInterval.HOUR) {
            return category.get(Calendar.HOUR) == date.get(Calendar.HOUR);
        }

        if (stepUnit == TimeInterval.DAY) {
            return category.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH);
        }

        if(stepUnit == TimeInterval.DAY_OF_WEEK) {
            return category.get(Calendar.DAY_OF_WEEK) == date.get(Calendar.DAY_OF_WEEK);
        }

        if(stepUnit == TimeInterval.DAY_OF_WEEK_IN_MONTH) {
            return category.get(Calendar.DAY_OF_WEEK_IN_MONTH) == date.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        }

        if (stepUnit == TimeInterval.DAY_OF_YEAR) {
            return category.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
        }

        if (stepUnit == TimeInterval.WEEK) {
            return category.get(Calendar.WEEK_OF_MONTH) == date.get(Calendar.WEEK_OF_MONTH);
        }

        if(stepUnit == TimeInterval.WEEK_OF_YEAR) {
            return category.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR);
        }

        if (stepUnit == TimeInterval.MONTH) {
            return category.get(Calendar.MONTH) == date.get(Calendar.MONTH);
        }

        if (stepUnit == TimeInterval.YEAR) {
            return category.get(Calendar.YEAR) == date.get(Calendar.YEAR);
        }

        // stepUnit == TimeInterval.QUARTER
        return getQuarter(category) == getQuarter(date);
    }

    private static int getQuarter(Calendar date) {
        int month = date.get(Calendar.MONTH);

        // Q1
        if (month >= 0 && month < 3) {
            return 1;
        }

        // Q2
        if (month > 2 && month < 6) {
            return 2;
        }

        // Q3
        if (month > 5 && month < 9) {
            return 3;
        }

        // Q4
        return 4;
    }
}
