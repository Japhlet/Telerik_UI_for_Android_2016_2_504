package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.PointSeriesModel;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.StrokedSeries;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Base class for all {@link CategoricalSeries} that have a stroke.
 */
public abstract class CategoricalStrokedSeries extends CategoricalSeries implements StrokedSeries {

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CategoricalStrokedSeries series = (CategoricalStrokedSeries)sender;

            int value = (int)propertyValue;
            series.renderer.setValue(LineRenderer.STROKE_COLOR_PROPERTY_KEY, propertyType, value);
            series.legendItem.setStrokeColor(value);
            series.requestRender();
        }
    });

    public static final int STROKE_THICKNESS_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CategoricalStrokedSeries series = (CategoricalStrokedSeries)sender;

            float value = (float)propertyValue;
            series.renderer.setValue(LineRenderer.STROKE_THICKNESS_PROPERTY_KEY, propertyType, value);
            series.requestRender();
        }
    });

    protected PointSeriesModel model;
    protected LineRenderer renderer;
    private float dataPointTouchTargetSize;

    /**
     * Creates a new instance of the {@link CategoricalStrokedSeries} class.
     */
    public CategoricalStrokedSeries() {
        this(null, null, null);
    }

    public CategoricalStrokedSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(valueBinding, categoryBinding, data);

        LineRenderer renderer = this.getRenderer();
        renderer.setModel(this.model);

        this.dataPointTouchTargetSize = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 24);
    }

    public void setTouchTargetRadius(float radius) {
        this.dataPointTouchTargetSize = radius;
    }

    public float getTouchTargetRadius() {
        return this.dataPointTouchTargetSize;
    }

    @Override
    protected boolean hitTestDataPoint(PointF touchLocation, DataPoint point) {
        RectF rect = Util.convertToRectF(point.getLayoutSlot());
        float x = rect.left;
        float y = rect.top;
        rect.left = x - this.dataPointTouchTargetSize;
        rect.top = y - this.dataPointTouchTargetSize;
        rect.right = x + this.dataPointTouchTargetSize;
        rect.bottom = y + this.dataPointTouchTargetSize;

        return rect.contains(touchLocation.x, touchLocation.y);
    }

    @Override
    public int getLegendFillColor() {
        if (!this.getCanApplyPalette()) {
            return this.getLegendStrokeColor();
        }

        int modelIndex = this.model().collectionIndex();
        ChartPalette palette = this.getPalette();
        if (palette == null || modelIndex == -1) {
            return this.getLegendStrokeColor();
        }

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), modelIndex);
        if (entry == null) {
            return this.getLegendStrokeColor();
        }

        return entry.getFill();
    }

    @Override
    public int getLegendStrokeColor() {
        if (!this.getCanApplyPalette()) {
            return this.getStrokeColor();
        }

        int modelIndex = this.model().collectionIndex();
        ChartPalette palette = this.getPalette();
        if (palette == null || modelIndex == -1) {
            return this.getStrokeColor();
        }

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), modelIndex);
        if (entry == null) {
            return this.getStrokeColor();
        }

        return entry.getStroke();
    }
    /**
     * Gets the data point indicator renderer.
     *
     * @return the current data point indicator renderer instance.
     * @see DataPointIndicatorRenderer
     */
    public DataPointIndicatorRenderer getDataPointIndicatorRenderer() {
        return getRenderer().getDataPointIndicatorRenderer();
    }

    /**
     * Sets the data point indicator renderer.
     *
     * @param dataPointIndicatorRenderer the new data point indicator renderer.
     */
    public void setDataPointIndicatorRenderer(DataPointIndicatorRenderer dataPointIndicatorRenderer) {
        getRenderer().setDataPointIndicatorRenderer(dataPointIndicatorRenderer);
        this.requestRender();
    }

    /**
     * Gets the stroke color.
     *
     * @return The stroke color.
     */
    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the stroke color.
     *
     * @param value The new stroke color.
     */
    public void setStrokeColor(int value) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, value);
    }

    /**
     * Gets the stroke thickness.
     *
     * @return The stroke thickness.
     */
    public float getStrokeThickness() {
        return (float)this.getValue(STROKE_THICKNESS_PROPERTY_KEY);
    }

    /**
     * Sets the stroke thickness.
     *
     * @param value The stroke thickness.
     */
    public void setStrokeThickness(float value) {
        this.setValue(STROKE_THICKNESS_PROPERTY_KEY, value);
    }

    /**
     * Gets the dash pattern of the stroke.
     *
     * @return The dash pattern of the stroke.
     */
    public float[] getDashArray() {
        return this.getRenderer().getDashArray();
    }

    /**
     * Sets the dash pattern of the stroke.
     *
     * @param dashArray The new dash pattern.
     */
    public void setDashArray(float[] dashArray) {
        this.getRenderer().setDashArray(dashArray);
        this.requestRender();
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);

        this.getRenderer().applyPalette(palette);
        this.getLabelRenderer().applyPalette(palette);
    }

    @Override
    protected ChartSeriesModel createModel() {
        this.model = new PointSeriesModel();
        return this.model;
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

    /**
     * Gets the line renderer for this series.
     *
     * @return A {@link LineRenderer} instance.
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
        return new LineRenderer();
    }
}
