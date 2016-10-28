package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.PieSeriesModel;

/**
 * Represents a single-value data point plotted by a pie widget.
 */
public class PieDataPoint extends SingleValueDataPoint {

    private static final int RELATIVE_OFFSET_FROM_CENTER_PROPERTY_KEY = PropertyKeys.register(PieDataPoint.class, "RelativeOffsetFromCenter", ChartAreaInvalidateFlags.NONE);

    private double startAngle;
    private double sweepAngle;
    private double normalizedValue;

    /**
     * Gets the ratio between the value of this data point and the total value of all data points
     * converted to percentage.
     *
     * @return the percentage of the total value that this data point's value holds
     */
    public double percent() {
        return this.normalizedValue * 100;
    }

    /**
     * Gets the offset of the point from the center of the pie relative to the radius.
     * The value is between <code>0</code> and <code>1.0</code>, where <code>0</code> means
     * that there is no offset and <code>1.0</code> means that the offset is the same as the
     * radius.
     *
     * @return the offset from the center relative to the radius
     */
    public double getRelativeOffsetFromCenter() {
        return this.getTypedValue(RELATIVE_OFFSET_FROM_CENTER_PROPERTY_KEY, 0D);
    }

    /**
     * Sets the offset of the point from the center of the pie relative to the radius.
     * The value is between <code>0</code> and <code>1.0</code>, where <code>0</code> means
     * that there is no offset and <code>1.0</code> means that the offset is the same as the
     * radius.
     *
     * @param value the new offset
     */
    public void setRelativeOffsetFromCenter(double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("RelativeOffsetFromCenter is not valid! The possible values are in the [0, 1] interval.");
        }

        this.setValue(RELATIVE_OFFSET_FROM_CENTER_PROPERTY_KEY, value);
    }

    /**
     * Gets the value of the angle where the first side of the pie slice,
     * that presents this data point, resides.
     * The value is in the [0, 360) interval.
     *
     * @return the starting angle of this data point
     */
    public double startAngle() {
        return this.startAngle;
    }

    /**
     * Gets the value of the central angle of the pie slice that represents this data point.
     * The value is in the [0, 360] interval.
     *
     * @return the sweep angle of this data point
     */
    public double sweepAngle() {
        return this.sweepAngle;
    }

    /**
     * Gets the current normalized value. The normalized value is from the [0, 1] interval
     * and represents the ratio between this data point's actual value and the total value
     * of all data points from the same series.
     *
     * @return the normalized value
     */
    public double normalizedValue() {
        return this.normalizedValue;
    }

    /**
     * Updates the values of this data point. The params indicate the angle where this data point
     * starts, the angle that it consumes and the ratio of its value to the total value of all
     * data points from the same series.
     *
     * @param startAngle      the start angle of this data point
     * @param sweepAngle      the sweep angle of this data point
     * @param normalizedValue the normalized value for this data point
     */
    public void update(double startAngle, double sweepAngle, double normalizedValue) {
        if (startAngle < 0 || startAngle > 360) {
            throw new IllegalArgumentException("The value for startAngle is not valid! " +
                    "The possible values are in the [0, 360] interval.");
        }

        if (sweepAngle < 0 || sweepAngle > 360) {
            throw new IllegalArgumentException("The value for sweepAngle is not valid! " +
                    "The possible values are in the [0, 360] interval.");
        }

        if (normalizedValue < 0 || normalizedValue > 1) {
            throw new IllegalArgumentException("The value for normalizedValue is not valid! " +
                    "The possible values are in the [0, 1] interval.");
        }

        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
        this.normalizedValue = normalizedValue;
    }

    @Override
    Object getDefaultLabel() {
        String format;
        PieSeriesModel model = (PieSeriesModel) this.getParent();
        if (model != null) {
            format = model.getLabelFormat();
        } else {
            format = PieSeriesModel.DEFAULT_LABEL_FORMAT;
        }

        return String.format(format, this.percent());
    }
}

