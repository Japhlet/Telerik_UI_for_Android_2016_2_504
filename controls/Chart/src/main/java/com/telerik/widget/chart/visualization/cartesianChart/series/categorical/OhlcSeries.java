package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Color;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.OhlcSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.OhlcPointRenderer;

/**
 * Used to render OHLC financial data. Also
 * see {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 */
public class OhlcSeries extends OhlcSeriesBase {

    public static final int DOWN_STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            OhlcSeries series = (OhlcSeries)sender;

            series.downStrokeColor = series.getDownStrokeColor();
            ((OhlcPointRenderer) series.renderer).getDownStroke().setColor(series.downStrokeColor);

            series.requestRender();

        }
    });

    public static final int DOWN_STROKE_WIDTH_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            OhlcSeries series = (OhlcSeries)sender;
            series.downStrokeWidth = series.getDownStrokeWidth();
            ((OhlcPointRenderer) series.renderer).getDownStroke().setStrokeWidth(series.downStrokeWidth);
        }
    });

    private int downStrokeColor = Color.RED;
    private float downStrokeWidth = 2;

    /**
     * Creates an instance of the {@link OhlcSeries} class.
     */
    public OhlcSeries() {
    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        return new OhlcPointRenderer(this);
    }

    @Override
    protected ChartSeriesModel createModel() {
        return new OhlcSeriesModel();
    }

    /**
     * Gets the current down stroke color.
     *
     * @return the current down stroke color.
     */
    public int getDownStrokeColor() {
        return (int)this.getValue(DOWN_STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the current down stroke color.
     *
     * @param color the new down stroke color.
     */
    public void setDownStrokeColor(int color) {
        this.setValue(DOWN_STROKE_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets the current down stroke width.
     *
     * @return the current down stroke width.
     */
    public float getDownStrokeWidth() {
        return (float)this.getValue(DOWN_STROKE_WIDTH_PROPERTY_KEY);
    }

    /**
     * Sets the current down stroke width.
     *
     * @param width the new down stroke width.
     */
    public void setDownStrokeWidth(float width) {
        this.setValue(DOWN_STROKE_WIDTH_PROPERTY_KEY, width);
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        setDownStrokeWidth(strokeWidth);

        super.setStrokeWidth(strokeWidth);
    }
}
