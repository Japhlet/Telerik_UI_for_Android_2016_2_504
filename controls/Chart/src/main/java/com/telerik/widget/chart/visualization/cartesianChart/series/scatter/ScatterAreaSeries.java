package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import android.graphics.Color;
import android.graphics.Paint;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.widget.chart.visualization.common.FilledSeries;
import com.telerik.widget.chart.visualization.common.renderers.AreaRendererBase;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.palettes.ChartPalette;

/**
 * Renders data points as a scatter area.
 */
public class ScatterAreaSeries extends ScatterLineSeries implements FilledSeries {


    /**
     * Creates a new instance of the ScatterAreaSeries class.
     */
    public ScatterAreaSeries() {
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.AREA_FAMILY;
    }

    @Override
    protected LineRenderer createRenderer() {
        return new AreaRendererBase();
    }

    /**
     * Sets the fill color of the area.
     */
    public void setFillColor(int value) {
        super.setFillColor(value);

        AreaRendererBase renderer = (AreaRendererBase) this.getRenderer();
        renderer.setFillColor(value);
    }

    @Override
    public void setFillPaint(Paint value) {
        super.setFillPaint(value);

        AreaRendererBase renderer = (AreaRendererBase) this.getRenderer();
        renderer.setFillPaint(value);
    }
}
