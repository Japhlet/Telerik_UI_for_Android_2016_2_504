package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.StrokedSeries;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.chart.visualization.common.renderers.ScatterLineRenderer;
import com.telerik.widget.palettes.ChartPalette;

/**
 * Renders data points as a scatter line.
 */
public class ScatterLineSeries extends ScatterPointSeries implements StrokedSeries {
    private LineRenderer renderer;

    /**
     * Creates a new instance of the ScatterLineSeries class.
     */
    public ScatterLineSeries() {
    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        super.createDataPointRenderer();

        return null;
    }

    @Override
    protected void initFields() {
        super.initFields();
        this.getRenderer().setModel(this.model());
    }

    /**
     * Sets the new stroke color.
     */
    public void setStrokeColor(int value) {
        super.setStrokeColor(value);
        this.getRenderer().setStrokeColor(value);
    }

    /**
     * Sets the stroke thickness.
     */
    public void setStrokeThickness(float value) {
        super.setStrokeThickness(value);
        this.getRenderer().setStrokeThickness(value);
    }

    @Override
    public void setStrokePaint(Paint value) {
        this.getRenderer().setStrokePaint(value);

        super.setStrokePaint(value);
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);
        this.getRenderer().layoutContext = context;
        this.getRenderer().prepare();
    }

    @Override
    protected void drawCore(Canvas canvas) {
        super.drawCore(canvas);
        this.getRenderer().render(canvas);
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);

        this.getRenderer().applyPalette(palette);
        this.getLabelRenderer().applyPalette(palette);
    }

    public boolean hitTest(PointF touchLocation) {
        return this.getRenderer().hitTest(touchLocation);
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.LINE_FAMILY;
    }

    /**
     * Gets the line renderer for this series.
     *
     * @return A {@link com.telerik.widget.chart.visualization.common.renderers.LineRenderer} instance.
     */
    protected LineRenderer getRenderer() {
        if (this.renderer == null) {
            this.renderer = this.createRenderer();
        }
        return this.renderer;
    }

    /**
     * Creates the {@link LineRenderer}.
     *
     * @return A new {@link LineRenderer} instance.
     */
    protected LineRenderer createRenderer() {
        return new ScatterLineRenderer();
    }
}
