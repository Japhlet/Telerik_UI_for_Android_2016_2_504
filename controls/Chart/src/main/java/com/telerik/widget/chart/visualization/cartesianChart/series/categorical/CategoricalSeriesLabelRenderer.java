package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Rect;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.renderers.PointingLabelRenderer;

/**
 * Label renderer handling the labels in the {@link CategoricalSeries} instances.
 */
public class CategoricalSeriesLabelRenderer extends PointingLabelRenderer {

    /**
     * Creates a new instance of the {@link CategoricalSeriesLabelRenderer} class.
     *
     * @param owner the chart series owning this renderer instance.
     */
    public CategoricalSeriesLabelRenderer(ChartSeries owner) {
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
        return String.format(this.getLabelFormat(), ((CategoricalDataPoint) dataPoint).getValue());
    }
}
