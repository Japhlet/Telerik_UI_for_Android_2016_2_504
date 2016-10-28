package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.PointF;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.palettes.ChartPalette;

/**
 * Visualizes a collection of data points using a line.
 */
public class LineSeries extends CategoricalStrokedSeries {

    /**
     * Creates a new instance of the {@link LineSeries} class.
     */
    public LineSeries() {
        this(null, null, null);
    }

    public LineSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(valueBinding, categoryBinding, data);
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.LINE_FAMILY;
    }

    @Override
    public boolean hitTest(PointF touchLocation) {
        return this.renderer.hitTest(touchLocation);
    }
}
