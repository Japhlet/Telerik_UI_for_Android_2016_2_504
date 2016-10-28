package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterBubbleDataPoint;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public class BubbleSeriesLabelRenderer extends ScatterSeriesLabelRenderer {
    /**
     * Creates a new instance of the BubbleSeriesLabelRenderer class.
     *
     * @param owner The owner series of this label renderer.
     */
    public BubbleSeriesLabelRenderer(ChartSeries owner) {
        super(owner);
    }

    @Override
    protected String getLabelText(DataPoint dataPoint) {
        ScatterBubbleDataPoint point = (ScatterBubbleDataPoint) dataPoint;
        return String.format(this.getLabelFormat(), point.getXValue(), point.getYValue(), point.getSize());
    }
}
