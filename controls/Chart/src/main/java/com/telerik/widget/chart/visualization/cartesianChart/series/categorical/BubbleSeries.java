package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Shader;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalBubbleSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.PointSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.CategoricalBubblePointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.RangeBarPointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.scatter.BubbleSeriesLabelRenderer;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

public class BubbleSeries extends CategoricalSeries {

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BubbleSeries series = (BubbleSeries)sender;

            int fill = (int)propertyValue;
            if(fill == series.fillColor) {
                return;
            }

            series.fillColor = fill;
            series.fillPaint.setColor(fill);
            series.requestRender();
        }
    });

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BubbleSeries series = (BubbleSeries)sender;
            int stroke = (int)propertyValue;
            if(stroke == series.strokeColor) {
                return;
            }

            series.strokeColor = stroke;
            series.strokePaint.setColor(stroke);
            series.requestRender();
        }
    });

    public static final int STROKE_WIDTH_PROPERTY_KEY = registerProperty(Util.getDP(2.0f), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BubbleSeries series = (BubbleSeries)sender;

            float width = (float)propertyValue;
            if(width == series.strokeWidth) {
                return;
            }

            series.strokeWidth = width;
            series.strokePaint.setStrokeWidth(width);
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

    private CategoricalBubblePointRenderer pointRenderer;

    private DataPointBinding bubbleSizeBinding;
    private float bubbleScale = 1.0f;

    public BubbleSeries() {
        this(null, null, null);
    }

    public BubbleSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(valueBinding, categoryBinding, data);

        this.fillPaint.setColor(this.fillColor);
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setAntiAlias(true);

        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setAntiAlias(true);
        this.strokePaint.setStrokeWidth(this.strokeWidth);
        this.strokePaint.setColor(this.strokeColor);

        this.legendItem.setStrokeColor(this.strokeColor);
        this.legendItem.setFillColor(this.fillColor);

        this.strokeWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 1);

        this.getLabelRenderer().setLabelFormat("X: %.2f, Y: %.2f, Area: %.2f");
    }

    @Override
    protected ChartSeriesModel createModel() {
        return new PointSeriesModel();
    }

    public float getBubbleScale() {
        return this.bubbleScale;
    }

    public void setBubbleScale(float value) {
        this.bubbleScale = value;
    }

    public DataPointBinding getBubbleSizeBinding() {
        return this.bubbleSizeBinding;
    }

    public void setBubbleSizeBinding(DataPointBinding value) {
        if (this.bubbleSizeBinding == value) {
            return;
        }

        this.bubbleSizeBinding = value;

        this.onBubbleSizeBindingChanged(value);
    }

    public int getFillColor(){
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    public void setFillColor(int value) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, value);
    }

    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    public void setStrokeColor(int value) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, value);
    }

    public float getStrokeWidth() {
        return (float)this.getValue(STROKE_WIDTH_PROPERTY_KEY);
    }

    public void setStrokeWidth(float value) {
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, value);
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public Paint getStrokePaint() {
        return strokePaint;
    }

    @Override
    public int getLegendFillColor() {
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

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.POINT_FAMILY;
    }

    public Shader getFillShader() {
        return this.fillShader;
    }

    public void setFillShader(Shader value) {
        if (this.fillShader == value) {
            return;
        }

        this.fillShader = value;
        this.fillPaint.setShader(value);
    }

    public Shader getStrokeShader() {
        return this.strokeShader;
    }

    public void setStrokeShader(Shader value) {
        if (this.strokeShader == value) {
            return;
        }

        this.strokeShader = value;
        this.strokePaint.setShader(value);
    }

    public PathEffect getStrokeEffect() {
        return this.strokeEffect;
    }

    public void setStrokeEffect(PathEffect value) {
        if (this.strokeEffect == value) {
            return;
        }

        this.strokeEffect = value;
        this.strokePaint.setPathEffect(this.strokeEffect);
    }

    @Override
    public void setCanApplyPalette(boolean value) {
        if (!value) {
            this.pointRenderer.pointColors().clear();
        }

        super.setCanApplyPalette(value);
    }

    @Override
    public void applyPaletteCore(ChartPalette palette) {

        PaletteEntry defaultEntry = this.getDefaultEntry();
        if (defaultEntry != null) {
            this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, defaultEntry.getFill());
            this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, defaultEntry.getStroke());
            this.setValue(STROKE_WIDTH_PROPERTY_KEY, PALETTE_VALUE, defaultEntry.getStrokeWidth());
        }
        this.pointRenderer.pointColors().clear();
        super.applyPaletteCore(palette);
    }

    @Override
    protected void applyPaletteToDefaultVisual(DataPoint point, PaletteEntry entry) {
        this.pointRenderer.pointColors().put(point, entry);
    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        this.pointRenderer = new CategoricalBubblePointRenderer(this);
        return this.pointRenderer;
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new CategoricalBubbleSeriesDataSource(this.model());
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new BubbleSeriesLabelRenderer(this);
    }

    protected void onBubbleSizeBindingChanged(DataPointBinding value) {
        CategoricalBubbleSeriesDataSource dataSource = (CategoricalBubbleSeriesDataSource) this.dataSource();
        dataSource.setBubbleSizeBinding(value);
    }
}
