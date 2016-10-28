package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.chart.visualization.common.renderers.SplineAreaRenderer;

/**
 * Renders data as an area but the area surface is a smooth curve instead of a poly line.
 */
public class SplineAreaSeries extends AreaSeries {

    /**
     * Creates a new instance of the {@link SplineAreaSeries} class.
     */
    public SplineAreaSeries() {
        this(null, null, null);
    }

    public SplineAreaSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(valueBinding, categoryBinding, data);
    }

    @Override
    protected LineRenderer createRenderer() {
        return new SplineAreaRenderer();
    }
}
