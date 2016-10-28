package com.telerik.widget.chart.visualization.cartesianChart.series.scatter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Function;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadMath;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.dataPoints.ScatterDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.GenericDataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.ScatterSeriesDataSource;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.ScatterSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.CartesianSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ScatterPointRenderer;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Represents a chart series which can visualize {@link com.telerik.widget.chart.engine.dataPoints.ScatterDataPoint} instances.
 */
public class ScatterPointSeries extends CartesianSeries {

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ScatterPointSeries series = (ScatterPointSeries)sender;

            series.fillPaint.setColor(series.getFillColor());
            series.requestRender();
        }
    });

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ScatterPointSeries series = (ScatterPointSeries)sender;

            series.strokePaint.setColor(series.getStrokeColor());
            series.requestRender();
        }
    });

    public static final int STROKE_THICKNESS_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            ScatterPointSeries series = (ScatterPointSeries)sender;

            float value = series.getStrokeThickness();
            if (value < 0) {
                throw new IllegalArgumentException("value cannot be less than 0");
            }

            series.strokePaint.setStrokeWidth(value);
            series.requestRender();
        }
    });

    private ScatterSeriesModel model;
    private DataPointBinding xValueBinding;
    private DataPointBinding yValueBinding;

    private Paint fillPaint;
    private Paint strokePaint;
    protected ScatterPointRenderer scatterPointRenderer;
    private float pointSize;
    private float touchTargetSize;

    /**
     * Initializes a new instance of the ScatterPointSeries class.
     */
    public ScatterPointSeries() {
        this.getLabelRenderer().setLabelFormat("X: %.2f, Y: %.2f");
    }

    @Override
    protected void initFields() {
        super.initFields();

        this.pointSize = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5);
        this.touchTargetSize = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 24);
        this.fillPaint = new Paint();
        this.fillPaint.setColor(Color.BLACK);
        this.fillPaint.setAntiAlias(true);
        this.strokePaint = new Paint();
        this.strokePaint.setAntiAlias(true);
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setColor(Color.WHITE);
        this.strokePaint.setStrokeWidth(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 1));
    }

    /**
     * Gets the fill paint of the scatter points.
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Sets the fill paint of the scatter points.
     */
    public void setFillPaint(Paint value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (value == this.fillPaint) {
            return;
        }

        this.fillPaint = value;
        value.setStyle(Paint.Style.FILL);

        this.requestRender();
    }

    /**
     * Gets the stroke paint of the scatter points.
     */
    public Paint getStrokePaint() {
        return this.strokePaint;
    }

    /**
     * Sets the fill color of the scatter points.
     */
    public void setFillColor(int value) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, value);
    }

    /**
     * Gets the fill color of the scatter points.
     */
    public int getFillColor() {
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the stroke color of the scatter points.
     */
    public void setStrokeColor(int value) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, value);
    }

    /**
     * Gets the stroke color of the scatter points.
     */
    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the stroke width of the scatter points.
     */
    public void setStrokeThickness(float value) {
        this.setValue(STROKE_THICKNESS_PROPERTY_KEY, value);
    }

    /**
     * Gets the stroke width of the scatter points.
     */
    public float getStrokeThickness() {
        return (float)this.getValue(STROKE_THICKNESS_PROPERTY_KEY);
    }

    /**
     * Sets the stroke paint of the scatter points.
     */
    public void setStrokePaint(Paint value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (value == this.strokePaint) {
            return;
        }

        this.strokePaint = value;
        value.setStyle(Paint.Style.STROKE);
        this.requestRender();
    }

    protected float getTouchTargetSize() {
        return this.touchTargetSize;
    }

    protected void setTouchTargetSize(float value) {
        if (this.touchTargetSize == value) {
            return;
        }

        this.touchTargetSize = value;
    }

    protected boolean hitTestDataPoint(PointF touchLocation, DataPoint point) {
        RectF rect = Util.convertToRectF(point.getLayoutSlot());
        float halfPointSize = this.getTouchTargetSize() / 2.0f;
        rect.left -= halfPointSize;
        rect.right += halfPointSize;
        rect.top -= halfPointSize;
        rect.bottom += halfPointSize;

        return rect.contains(touchLocation.x, touchLocation.y);
    }

    @Override
    public int getLegendFillColor() {
        int modelIndex = this.model().collectionIndex();
        ChartPalette palette = this.getPalette();
        if (palette == null || modelIndex == -1) {
            return this.fillPaint.getColor();
        }

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), modelIndex);
        if(entry == null) {
            return Color.RED;
        }

        return entry.getFill();
    }

    @Override
    public int getLegendStrokeColor() {
        int modelIndex = this.model().collectionIndex();
        ChartPalette palette = this.getPalette();
        if (palette == null || modelIndex == -1) {
            return this.strokePaint.getColor();
        }

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), modelIndex);
        if(entry == null) {
            return Color.RED;
        }

        return entry.getStroke();
    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        this.scatterPointRenderer = new ScatterPointRenderer(this);
        return this.scatterPointRenderer;
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {

        PaletteEntry entry = this.getDefaultEntry();
        if (entry != null) {
            this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
            this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
            this.setValue(STROKE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
        }

        this.scatterPointRenderer.pointColors().clear();
        super.applyPaletteCore(palette);
    }

    @Override
    public void setCanApplyPalette(boolean value) {
        if (!value) {
            this.scatterPointRenderer.pointColors().clear();
        }

        super.setCanApplyPalette(value);
    }

    @Override
    public void setData(Iterable data) {
        super.setData(data);

        this.invalidatePalette();
    }

    @Override
    protected void applyPaletteToDefaultVisual(DataPoint point, PaletteEntry entry) {
        this.scatterPointRenderer.pointColors().put(point, entry);
    }

    /**
     * Gets the collection of data points associated with the series.
     */
    public DataPointCollection<ScatterDataPoint> dataPoints() {
        return this.model.dataPoints();
    }

    /**
     * Gets the binding that will be used to fill the x value member of the contained data points.
     */
    public DataPointBinding getXValueBinding() {
        return this.xValueBinding;
    }

    /**
     * Sets the binding that will be used to fill the x value the contained data points.
     */
    public void setXValueBinding(DataPointBinding value) {
        if (this.xValueBinding == value) {
            return;
        }

        this.xValueBinding = value;
        this.onXValueBindingChanged(value);
    }

    /**
     * Sets the binding that will be used to fill the x value the contained data points.
     */
    public <T, U> void setXValueBinding(Function<T, U> valueSelector) {
        this.setXValueBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    /**
     * Gets the binding that will be used to fill the y value of the contained data points.
     */
    public DataPointBinding getYValueBinding() {
        return this.yValueBinding;
    }

    /**
     * Sets the binding that will be used to fill the y value of the contained data points.
     */
    public void setYValueBinding(DataPointBinding value) {
        if (this.yValueBinding == value) {
            return;
        }

        this.yValueBinding = value;
        this.onYValueBindingChanged(value);
    }

    /**
     * Sets the binding that will be used to fill the y value of the contained data points.
     */
    public <T, U> void setYValueBinding(Function<T, U> valueSelector) {
        this.setYValueBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    /**
     * Gets the size of the scatter points.
     */
    public float getPointSize() {
        return this.pointSize;
    }

    /**
     * Sets the size of the scatter points.
     */
    public void setPointSize(float value) {
        if (value == this.pointSize) {
            return;
        }

        this.pointSize = value;
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.POINT_FAMILY;
    }

    @Override
    public double getDistanceToPoint(Point dataPointLocation, Point tapLocation) {
        return RadMath.getPointDistance(dataPointLocation.x, tapLocation.x, dataPointLocation.y, tapLocation.y);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new ScatterSeriesDataSource(this.model());
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new ScatterSeriesLabelRenderer(this);
    }

    @Override
    protected void initDataBinding() {
        ScatterSeriesDataSource source = (ScatterSeriesDataSource) this.dataSource();
        source.setXValueBinding(this.xValueBinding);
        source.setYValueBinding(this.yValueBinding);

        super.initDataBinding();
    }

    @Override
    protected ChartSeriesModel createModel() {
        this.model = new ScatterSeriesModel();
        return this.model;
    }

    private void onXValueBindingChanged(DataPointBinding newValue) {
        ScatterSeriesDataSource source = (ScatterSeriesDataSource) this.dataSource();
        source.setXValueBinding(newValue);
    }

    private void onYValueBindingChanged(DataPointBinding newValue) {
        ScatterSeriesDataSource source = (ScatterSeriesDataSource) this.dataSource();
        source.setYValueBinding(newValue);
    }
}
