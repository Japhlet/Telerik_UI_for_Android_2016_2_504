package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.chart.visualization.common.renderers.SplineAreaRenderer;

/**
 * Renders data points a scatter spline area.
 */
public class ScatterSplineAreaSeries extends ScatterAreaSeries {

    /**
     * Creates a new instance of the ScatterSplineAreaSeries class.
     */
    public ScatterSplineAreaSeries() {
    }

    @Override
    protected LineRenderer createRenderer() {
        return new SplineAreaRenderer();
    }
}
