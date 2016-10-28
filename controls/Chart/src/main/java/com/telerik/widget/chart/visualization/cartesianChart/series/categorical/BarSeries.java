package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.series.BarSeriesModel;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.behaviors.DataPointInfo;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.BarPointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * The bar series renders information as bars or columns.
 */
public class BarSeries extends CategoricalSeries {

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BarSeries series = (BarSeries) sender;

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

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BarSeries series = (BarSeries) sender;

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
            BarSeries series = (BarSeries) sender;

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

    int fillColor = Color.RED;
    int strokeColor = Color.BLACK;
    float strokeWidth;
    Paint fillPaint = new Paint();
    Paint strokePaint = new Paint();
    Shader strokeShader;
    Shader fillShader;
    PathEffect strokeEffect;
    boolean areBarsRounded = false;
    float roundBarsRadius;
    private BarPointRenderer barRenderer;

    /**
     * Creates a new instance of the {@link BarSeries} class.
     */
    public BarSeries() {
        this(null, null, null);
    }

    public BarSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(valueBinding, categoryBinding, data);

        this.strokeWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 2);
        this.roundBarsRadius = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5);

        this.fillPaint.setColor(this.fillColor);
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setAntiAlias(true);

        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setAntiAlias(true);
        this.strokePaint.setStrokeWidth(this.strokeWidth);
        this.strokePaint.setColor(this.strokeColor);

        this.legendItem.setStrokeColor(this.strokeColor);
        this.legendItem.setFillColor(this.fillColor);
    }

    public void setMaxBarWidth(double value) {
        ((BarSeriesModel) model()).setMaxBarWidth(value);
    }

    public double getMaxBarWidth() {
        return ((BarSeriesModel) model()).getMaxBarWidth();
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
        if (entry == null) {
            return this.fillColor;
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
        if (entry == null) {
            return this.strokeColor;
        }

        return entry.getStroke();
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.BAR_FAMILY;
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

    /**
     * Gets a value that determines if the bars will be drawn with rounded corners.
     *
     * @return <code>true</code> if the bars will be with rounded corners and <code>false</code> otherwise.
     */
    public boolean getAreBarsRounded() {
        return this.areBarsRounded;
    }

    /**
     * Sets a value that determines if the bars will be drawn with rounded corners.
     *
     * @param value <code>true</code> if the bars will be with rounded corners and <code>false</code> otherwise.
     */
    public void setAreBarsRounded(boolean value) {
        this.areBarsRounded = value;
        this.requestRender();
    }

    /**
     * Gets a value that determines how much to round the corners of the bars.
     *
     * @return A value that determines how much to round the corners of the bars.
     * @see #setAreBarsRounded(boolean)
     */
    public float getRoundBarsRadius() {
        return this.roundBarsRadius;
    }

    /**
     * Sets a value that determines how much to round the corners of the bars.
     *
     * @param value A value that determines how much to round the corners of the bars.
     * @see #setAreBarsRounded(boolean)
     */
    public void setRoundBarsRadius(float value) {
        if (value < 0)
            throw new IllegalArgumentException("value cannot be negative.");

        this.roundBarsRadius = value;
        this.requestRender();
    }

    /**
     * Gets the fill color of the bars.
     *
     * @return The fill color of the bars.
     */
    public int getFillColor() {
        return (int) this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    /**
     * Gets the fill color of the bars.
     *
     * @param fillColor The fill color of the bars.
     */
    public void setFillColor(int fillColor) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, fillColor);
    }

    /**
     * Gets the stroke color of the bars.
     *
     * @return The stroke color of the bars.
     */
    public int getStrokeColor() {
        return (int) this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the stroke color of the bars.
     *
     * @param strokeColor The stroke color of the bars.
     */
    public void setStrokeColor(int strokeColor) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, strokeColor);
    }

    /**
     * Gets the stroke width of the bars.
     *
     * @return The stroke width of the bars.
     */
    public float getStrokeWidth() {
        return (float) this.getValue(STROKE_WIDTH_PROPERTY_KEY);
    }

    /**
     * Sets the stroke width of the bars.
     *
     * @param strokeWidth The stroke width of the bars.
     */
    public void setStrokeWidth(float strokeWidth) {
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, strokeWidth);
    }

    @Override
    public DataPointInfo findClosestPoint(Point location) {
        for (Object point : this.model().visibleDataPoints()) {
            DataPoint dataPoint = (DataPoint) point;
            if (dataPoint.getLayoutSlot().contains(location.x, location.y)) {
                DataPointInfo info = new DataPointInfo();
                info.setDataPoint(dataPoint);
                info.setDistanceToTouchLocation(this.getDistanceToPoint(location, dataPoint.getCenter()));
                info.setSeriesModel(this.model());
                return info;
            }
        }

        return null;
    }

    @Override
    protected ChartSeriesModel createModel() {
        return new BarSeriesModel();
    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        this.barRenderer = new BarPointRenderer(this);
        return this.barRenderer;
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public Paint getStrokePaint() {
        return this.strokePaint;
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        this.barRenderer.pointColors().clear();

        PaletteEntry defaultEntry = this.getDefaultEntry();
        if (defaultEntry != null) {
            this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, defaultEntry.getFill());
            this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, defaultEntry.getStroke());
            this.setValue(STROKE_WIDTH_PROPERTY_KEY, PALETTE_VALUE, defaultEntry.getStrokeWidth());
        }

        super.applyPaletteCore(palette);
    }

    @Override
    public void setCanApplyPalette(boolean value) {
        super.setCanApplyPalette(value);

        if (!value) {
            this.barRenderer.pointColors().clear();
        }
    }

    @Override
    public void setData(Iterable data) {
        super.setData(data);

        this.invalidatePalette();
    }

    @Override
    protected void applyPaletteToDefaultVisual(DataPoint point, PaletteEntry entry) {
        this.barRenderer.pointColors().put(point, entry);
    }

    @Override
    protected void clearPaletteFromDefaultVisual(DataPoint point) {
        this.barRenderer.pointColors().remove(point);
    }
}
