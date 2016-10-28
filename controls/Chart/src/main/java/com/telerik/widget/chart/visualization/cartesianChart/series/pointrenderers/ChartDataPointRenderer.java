package com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers;

import android.graphics.Canvas;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

public interface ChartDataPointRenderer {
    void renderPoint(Canvas canvas, DataPoint point);
}
