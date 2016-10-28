package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Shader;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.rangeSeries.RangeBarSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.RangeBarPointRenderer;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * This chart series renders data as range bars. Bars with start and end values.
 */
public class RangeBarSeries extends RangeSeriesBase {
    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            RangeBarSeries series = (RangeBarSeries)sender;

            int fillColor = (int) propertyValue;
            if (fillColor == series.fillColor) {
                return;
            }

            series.fillColor = fillColor;
            series.fillPaint.setColor(fillColor);
            series.legendItem.setFillColor(fillColor);
            series.requestRender();
        }
    });

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            RangeBarSeries series = (RangeBarSeries)sender;

            int strokeColor = (int) propertyValue;
            if (series.strokeColor == strokeColor) {
                return;
            }

            series.strokeColor = strokeColor;
            series.strokePaint.setColor(strokeColor);
            series.legendItem.setStrokeColor(strokeColor);
            series.requestRender();
        }
    });

    public static final int STROKE_WIDTH_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            RangeBarSeries series = (RangeBarSeries)sender;

            float strokeWidth = (float) propertyValue;
            if (strokeWidth < 0)
                throw new IllegalArgumentException("strokeWidth cannot be negative");

            if (strokeWidth == series.strokeWidth) {
                return;
            }

            series.strokeWidth = strokeWidth;
            series.strokePaint.setStrokeWidth(strokeWidth);
            series.requestRender();
        }
    });

    private int fillColor = Color.RED;
    private int strokeColor = Color.BLACK;
    private boolean areBarsRounded;
    private float roundBarsRadius;
    private float strokeWidth;
    Shader strokeShader;
    Shader fillShader;
    PathEffect strokeEffect;
    private Paint fillPaint;
    private Paint strokePaint;
    private RangeBarPointRenderer pointRenderer;

    /**
     * Creates a new instance of the {@link RangeBarSeries} class.
     */
    public RangeBarSeries() {
        this.fillPaint = new Paint();
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setColor(this.fillColor);
        this.fillPaint.setAntiAlias(true);

        this.strokePaint = new Paint();
        this.strokeWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 2);
        this.strokePaint.setStrokeWidth(this.strokeWidth);
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setColor(this.strokeColor);
        this.strokePaint.setAntiAlias(true);
        this.roundBarsRadius = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5);

        this.legendItem.setStrokeColor(this.strokeColor);
        this.legendItem.setFillColor(this.fillColor);

    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        this.pointRenderer = new RangeBarPointRenderer(this);
        return this.pointRenderer;
    }

    @Override
    public int getLegendFillColor() {
        if (!this.getCanApplyPalette()) {
            return this.fillColor;
        }

        int modelIndex = this.model().collectionIndex();
        ChartPalette palette = this.getPalette();
        if (palette == null || modelIndex == -1) {
            return this.fillColor;
        }

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), modelIndex);
        if(entry == null) {
            return Color.RED;
        }

        return entry.getFill();
    }

    @Override
    public int getLegendStrokeColor() {
        if (!this.getCanApplyPalette()) {
            return this.strokeColor;
        }

        int modelIndex = this.model().collectionIndex();
        ChartPalette palette = this.getPalette();
        if (palette == null || modelIndex == -1) {
            return this.strokeColor;
        }

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), modelIndex);
        if(entry == null) {
            return Color.RED;
        }

        return entry.getStroke();
    }

    /**
     * Gets the stroke color of the bars.
     *
     * @return The stroke color.
     */
    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the stroke color of the bars.
     *
     * @param color The stroke color.
     */
    public void setStrokeColor(int color) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Sets the bars stroke width.
     *
     * @param value The stroke width.
     */
    public void setStrokeWidth(float value) {
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, value);
    }

    /**
     * Gets the bars stroke width.
     *
     * @return The bars stroke width.
     */
    public float getStrokeWidth() {
        return (float)this.getValue(STROKE_WIDTH_PROPERTY_KEY);
    }

    /**
     * Gets a value that determines if the bars are drawn with rounded corners.
     *
     * @return <code>true</code> if the bars are drawn rounded and false otherwise.
     */
    public boolean getAreBarsRounded() {
        return this.areBarsRounded;
    }

    /**
     * Sets a value that determines if the bars are drawn with rounded corners.
     *
     * @param value <code>true</code> if the bars are to be drawn rounded and false otherwise.
     */
    public void setAreBarsRounded(boolean value) {
        this.areBarsRounded = value;
        this.requestRender();
    }

    /**
     * Gets the radius of the rounding if the bars are drawn with rounded corners.
     *
     * @return The rounding radius.
     */
    public float getRoundBarsRadius() {
        return this.roundBarsRadius;
    }

    /**
     * Sets the radius of the rounding if the bars are drawn with rounded corners.
     *
     * @param value The rounding radius.
     */
    public void setRoundBarsRadius(float value) {
        if (value < 0)
            throw new IllegalArgumentException("value for round bars radius cannot be negative");

        this.roundBarsRadius = value;
        this.requestRender();
    }

    /**
     * Gets the fill color of the bars.
     *
     * @return The fill color.
     */
    public int getFillColor() {
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the fill color of the bars.
     *
     * @param color The fill color.
     */
    public void setFillColor(int color) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets the {@link Shader} that is used when the fill of the bars is drawn.
     *
     * @return The {@link Shader} that is used when the fill of the bars is drawn.
     */
    public Shader getFillShader() {
        return this.fillShader;
    }

    /**
     * Sets the {@link Shader} that is used when the fill of the bars is drawn.
     *
     * @param value The {@link Shader} that is used when the fill of the bars is drawn.
     */
    public void setFillShader(Shader value) {
        if (this.fillShader == value) {
            return;
        }

        this.fillShader = value;
        this.fillPaint.setShader(value);
    }

    /**
     * Gets the {@link Shader} that is used when the stroke of the bars is drawn.
     *
     * @return The {@link Shader} that is used when the stroke of the bars is drawn.
     */
    public Shader getStrokeShader() {
        return this.strokeShader;
    }

    /**
     * Sets the {@link Shader} that is used when the fill of the bars is drawn.
     *
     * @param value The {@link Shader} that is used when the fill of the bars is drawn.
     */
    public void setStrokeShader(Shader value) {
        if (this.strokeShader == value) {
            return;
        }

        this.strokeShader = value;
        this.strokePaint.setShader(value);
    }

    /**
     * Gets the {@link PathEffect} that is used when stroke of the bars is drawn.
     *
     * @return The {@link PathEffect} that is used when stroke of the bars is drawn.
     */
    public PathEffect getStrokeEffect() {
        return this.strokeEffect;
    }

    /**
     * Sets the {@link PathEffect} that is used when stroke of the bars is drawn.
     *
     * @param value The {@link PathEffect} that is used when stroke of the bars is drawn.
     */
    public void setStrokeEffect(PathEffect value) {
        if (this.strokeEffect == value) {
            return;
        }

        this.strokeEffect = value;
        this.strokePaint.setPathEffect(this.strokeEffect);
    }


    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.BAR_FAMILY;
    }

    @Override
    protected ChartSeriesModel createModel() {
        return new RangeBarSeriesModel();
    }

    @Override
    public void setData(Iterable data) {
        super.setData(data);

        this.invalidatePalette();
    }

    public Paint getStrokePaint() {
        return this.strokePaint;
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new RangeBarSeriesLabelRenderer(this);
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        this.pointRenderer.pointColors().clear();

        PaletteEntry entry = this.getDefaultEntry();
        if (entry != null) {
            this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
            this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
            this.setValue(STROKE_WIDTH_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
        }

        super.applyPaletteCore(palette);
    }

    @Override
    public void setCanApplyPalette(boolean value) {
        super.setCanApplyPalette(value);

        if (!value) {
            this.pointRenderer.pointColors().clear();
        }
    }

    @Override
    protected void applyPaletteToDefaultVisual(DataPoint point, PaletteEntry entry) {
        this.pointRenderer.pointColors().put(point, entry);
    }

    @Override
    protected void clearPaletteFromDefaultVisual(DataPoint visual) {
    }
}
