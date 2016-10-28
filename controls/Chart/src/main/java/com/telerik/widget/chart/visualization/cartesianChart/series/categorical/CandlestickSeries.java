package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.CandlestickPointRenderer;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;

/**
 * Used to render candle stick financial data. Also see {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 */
public class CandlestickSeries extends OhlcSeriesBase {

    /**
     * Initializes a new instance of the {@link CandlestickSeries} class.
     */
    public CandlestickSeries() {
    }

    @Override
    protected ChartDataPointRenderer createDataPointRenderer() {
        return new CandlestickPointRenderer(this);
    }

    @Override
    protected void onStrokeChanged(int strokeColor) {
        super.onStrokeChanged(strokeColor);

        ((CandlestickPointRenderer) this.renderer).getBodyPaint().setColor(strokeColor);
    }

    @Override
    protected void onStrokeWidthChanged(float strokeWidth) {
        super.onStrokeWidthChanged(strokeWidth);

        ((CandlestickPointRenderer) this.renderer).getBodyPaint().setStrokeWidth(strokeWidth);
    }
}
