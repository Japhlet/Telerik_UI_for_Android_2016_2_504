package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import android.graphics.Canvas;
import android.graphics.Color;

import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

public abstract class LineIndicatorBase extends IndicatorBase {

    private static final String PALETTE_FAMILY_NAME = "Indicator";
    private LineRenderer renderer;

    /**
     * Creates a new instance of the {@link LineIndicatorBase} class.
     */
    public LineIndicatorBase() {
this.ensureRenderer();
    }

    /**
     * Gets the stroke color of the indicator line.
     */
    public int getStrokeColor() {
        return this.getRenderer().getStrokeColor();
    }

    /**
     * Sets the stroke color of the indicator line.
     *
     * @param value The new stroke color.
     */
    public void setStrokeColor(int value) {
        this.getRenderer().setStrokeColor(value);
        this.onStrokeChanged(value);
    }

    /**
     * Gets the width of the line used to present the indicator.
     */
    public float getStrokeThickness() {
        return this.getRenderer().getStrokeThickness();
    }

    /**
     * Sets the thickness of the line used to present the indicator.
     *
     * @param value The new stroke width.
     */
    public void setStrokeThickness(float value) {
        this.getRenderer().setStrokeThickness(value);
    }

    /**
     * Gets the dash array of the path effect used to render the indicator line.
     */
    public float[] getDashArray() {
        return this.getRenderer().getDashArray();
    }

    /**
     * Sets the dash array of the {@link android.graphics.DashPathEffect} used to render the indicator line.
     *
     * @param value The new dash array.
     */
    public void setDashArray(float[] value) {
        this.getRenderer().setDashArray(value);
    }

    /**
     * Creates a new {@link LineRenderer} object with the stroke properties of this {@link LineIndicatorBase} object.
     */
    public LineRenderer createRenderer() {
        return new LineRenderer();
    }


    private void ensureRenderer(){
        if (this.renderer == null) {
            this.renderer = this.createRenderer();
            this.renderer.setModel(this.model());
        }
    }
    /**
     * Calls {@link #createRenderer()} and prepares the result to be used with the current {@link LineIndicatorBase}.
     */
    protected LineRenderer getRenderer(){
        this.ensureRenderer();
        return this.renderer;
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);

        this.getRenderer().layoutContext = this.lastLayoutContext;
        this.getRenderer().prepare();
    }

    @Override
    protected void drawCore(Canvas canvas) {
        super.drawCore(canvas);

        this.getRenderer().render(canvas);
    }

    @Override
    protected void onAttached() {
        super.onAttached();
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);
        oldChart.chartAreaModel().getSeries().remove(this.model());
    }

    @Override
    public int getLegendFillColor() {
        return this.getStrokeColor();
    }

    @Override
    public int getLegendStrokeColor() {
        return this.getStrokeColor();
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        if (palette == null)
            return;

        super.applyPaletteCore(palette);

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), this.getChart().chartAreaModel().getSeries().indexOf(this.model));

        if (entry == null)
            return;

        this.getRenderer().setValue(LineRenderer.STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
        this.getRenderer().setValue(LineRenderer.STROKE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
    }

    @Override
    protected String defaultPaletteFamily() {
        return PALETTE_FAMILY_NAME;
    }

    private void onStrokeChanged(int newStroke) {
        this.legendItem.setFillColor(newStroke);
        this.legendItem.setStrokeColor(newStroke);
    }
}
