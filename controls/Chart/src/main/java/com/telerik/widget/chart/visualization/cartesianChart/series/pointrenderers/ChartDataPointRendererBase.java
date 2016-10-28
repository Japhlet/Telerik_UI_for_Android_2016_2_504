package com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers;

import android.graphics.Canvas;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

public abstract class ChartDataPointRendererBase<T> implements ChartDataPointRenderer {
    private T series;

    public ChartDataPointRendererBase(T series) {
        if (series == null) {
            throw new IllegalArgumentException("series cannot be null.");
        }

        this.series = series;
    }

    protected T getSeries() {
        return this.series;
    }

    @Override
    public void renderPoint(Canvas canvas, DataPoint point) {
        if (!point.isEmpty) {
            this.renderPointCore(canvas, point);
        }
    }

    protected abstract void renderPointCore(Canvas canvas, DataPoint point);
}
