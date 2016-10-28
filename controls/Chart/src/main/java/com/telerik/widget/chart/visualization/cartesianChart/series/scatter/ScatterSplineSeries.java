package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.chart.visualization.common.renderers.SplineRenderer;

/**
 * Renders data points as a scatter spline.
 */
public class ScatterSplineSeries extends ScatterLineSeries {

    /**
     * Creates a new instance of the ScatterSplineSeries class.
     */
    public ScatterSplineSeries() {
    }

    @Override
    protected LineRenderer createRenderer() {
        return new SplineRenderer();
    }
}
