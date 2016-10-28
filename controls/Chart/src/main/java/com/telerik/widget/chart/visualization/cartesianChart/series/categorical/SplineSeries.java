package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.chart.visualization.common.renderers.SplineRenderer;

/**
 * Renders data as a smooth curve.
 */
public class SplineSeries extends LineSeries {

    /**
     * Creates a new instance of the {@link LineSeries} class.
     */
    public SplineSeries() {
        this(null, null, null);
    }

    public SplineSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(valueBinding, categoryBinding, data);
    }

    @Override
    protected LineRenderer createRenderer() {
        return new SplineRenderer();
    }
}
