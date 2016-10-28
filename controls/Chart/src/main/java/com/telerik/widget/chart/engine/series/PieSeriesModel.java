package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.chartAreas.AngleRange;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.ChartMessage;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * This class calculates all layout information for {@link com.telerik.widget.chart.visualization.pieChart.RadPieChartView}'s {@link com.telerik.widget.chart.visualization.pieChart.PieSeries}.
 */
public class PieSeriesModel extends DataPointSeriesModel<PieDataPoint> {

    static final int RANGE_PROPERTY_KEY = PropertyKeys.register(PieSeriesModel.class, "Range", ChartAreaInvalidateFlags.ALL);
    static final int LABEL_FORMAT_PROPERTY_KEY = PropertyKeys.register(PieSeriesModel.class, "LabelFormat", ChartAreaInvalidateFlags.ALL);

    public static final String DEFAULT_LABEL_FORMAT = "%.0f %%";

    String labelFormat = DEFAULT_LABEL_FORMAT;
    boolean isDataPrepared;

    private double total;
    private double maxRelativeOffset;

    /**
     * Creates a new instance of the {@link PieSeriesModel} class.
     */
    public PieSeriesModel() {
        this.trackPropertyChanged = true;
        this.setVirtualizationEnabled(false);
    }

    /**
     * Gets the String used to format the Percent value of each data point. Defaults to '%d0'.
     */
    public String getLabelFormat() {
        return this.labelFormat;
    }

    /**
     * Sets the String used to format the Percent value of each data point. Defaults to '%d0'.
     */
    public void setLabelFormat(String value) {
        this.setValue(LABEL_FORMAT_PROPERTY_KEY, value);
    }

    /**
     * Gets the {@link AngleRange} structure that defines the starting and sweep angles of the pie.
     */
    public AngleRange getRange() {
        return this.getTypedValue(RANGE_PROPERTY_KEY, AngleRange.getDefault());
    }

    /**
     * Sets the {@link AngleRange} structure that defines the starting and sweep angles of the pie.
     */
    public void setRange(final AngleRange value) {
        this.setValue(RANGE_PROPERTY_KEY, value);
    }


    /**
     * Gets the maximum point offset from the center.
     */
    public double maxRelativeOffsetFromCenter() {
        return this.maxRelativeOffset;
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        if (e.getKey() == LABEL_FORMAT_PROPERTY_KEY) {
            this.labelFormat = (String) e.newValue();
        }

        super.onPropertyChanged(e);
    }

    @Override
    protected void processMessage(ChartMessage message) {
        super.processMessage(message);

        if (message.getSender() instanceof PieDataPoint && message.getId() == ChartNode.PROPERTY_CHANGED_MESSAGE) {
            this.isDataPrepared = false;
        }
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        if (!this.isDataPrepared) {
            this.updateTotal();
            this.updateDataPoints();
            this.isDataPrepared = true;
        } else {
            this.updateMaxOffset();
        }

        return rect;
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child instanceof PieDataPoint) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }

    @Override
    void onDataPointsModified() {
        super.onDataPointsModified();

        this.isDataPrepared = false;
    }

    private void updateDataPoints() {
        AngleRange range = this.getRange();
        double startAngle = range.getStartAngle();

        for (PieDataPoint point : this.visibleDataPoints()) {
            Double pointValue = point.getValue();
            if (pointValue < 0) {
                throw new IllegalArgumentException(pointValue + " is not a valid value. Negative values are not supported in RadPieChartView.");
            }

            double normalizedValue = pointValue / this.total;
            double sweepAngle = normalizedValue * range.getSweepAngle();

            startAngle = startAngle % 360;

            if(this.visibleDataPoints().size() > 1 && sweepAngle > 360) {
                sweepAngle = sweepAngle % 360;
            }

            point.update(startAngle, sweepAngle, normalizedValue);

            startAngle += point.sweepAngle();
        }
    }

    private void updateTotal() {
        this.total = 0;
        this.maxRelativeOffset = 0;

        for (PieDataPoint point : this.visibleDataPoints()) {
            if (point.isEmpty) {
                continue;
            }

            this.total += point.getValue();
            this.maxRelativeOffset = Math.max(this.maxRelativeOffset, point.getRelativeOffsetFromCenter());
        }
    }

    private void updateMaxOffset() {
        this.maxRelativeOffset = 0;

        for (PieDataPoint point : this.visibleDataPoints()) {
            this.maxRelativeOffset = Math.max(this.maxRelativeOffset, point.getRelativeOffsetFromCenter());
        }
    }
}

