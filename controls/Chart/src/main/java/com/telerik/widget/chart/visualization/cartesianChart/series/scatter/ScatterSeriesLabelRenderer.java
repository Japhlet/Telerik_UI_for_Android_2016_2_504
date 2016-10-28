package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import android.graphics.Rect;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterDataPoint;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.renderers.PointingLabelRenderer;

/**
 * Renders the data point labels for all scatter series.
 */
public class ScatterSeriesLabelRenderer extends PointingLabelRenderer {

    /**
     * Creates a new instance of the ScatterSeriesLabelRenderer class.
     *
     * @param owner The owner series of this label renderer.
     */
    public ScatterSeriesLabelRenderer(ChartSeries owner) {
        super(owner);
    }

    @Override
    protected RadPoint calculateLabelPoint(DataPoint point, Rect textSize) {
        final RadRect pointSlot = point.getLayoutSlot();

        return new RadPoint((pointSlot.getX() + pointSlot.getWidth() / 2) - (textSize.width() / 2),
                pointSlot.getY() - (this.offsetBottom() + this.labelMargin));
    }

    @Override
    protected String getLabelText(DataPoint dataPoint) {
        ScatterDataPoint point = (ScatterDataPoint) dataPoint;
        return String.format(this.getLabelFormat(), point.getXValue(), point.getYValue());
    }
}
