package com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.CategoricalBubbleDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterBubbleDataPoint;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.BarSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.BubbleSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.scatter.ScatterBubbleSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.scatter.ScatterPointSeries;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.HashMap;

public class CategoricalBubblePointRenderer  extends ChartDataPointRendererBase<BubbleSeries> {
    private HashMap<DataPoint, PaletteEntry> pointColors = new HashMap<DataPoint, PaletteEntry>();

    /**
     * Creates a new instance of the ScatterPointRenderer class.
     * @param owner The owner series.
     */
    public CategoricalBubblePointRenderer(BubbleSeries owner) {
        super(owner);
    }

    /**
     * Gets the palette entry map for the owner series' data points.
     */
    public HashMap<DataPoint, PaletteEntry> pointColors() {
        return this.pointColors;
    }

    @Override
    protected void renderPointCore(Canvas canvas, DataPoint point) {
        Paint fillPaint;
        Paint strokePaint;

        BubbleSeries series = this.getSeries();
        if(series.isPaletteApplied() && this.pointColors.containsKey(point)) {
            PaletteEntry entry = this.pointColors.get(point);
            fillPaint = new Paint();
            fillPaint.setAntiAlias(true);
            fillPaint.setColor(entry.getFill());

            strokePaint = new Paint();
            strokePaint.setAntiAlias(true);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(entry.getStroke());
            strokePaint.setStrokeWidth(entry.getStrokeWidth());
        } else {
            strokePaint = series.getStrokePaint();
            fillPaint = series.getFillPaint();
        }

        CategoricalBubbleDataPoint bubblePoint = (CategoricalBubbleDataPoint)point;
        double area = bubblePoint.getSize() * series.getBubbleScale();
        double radiusSquare = area / Math.PI;
        float radius = (float)Math.sqrt(radiusSquare);

        canvas.drawCircle((float)point.getCenterX(), (float)point.getCenterY(), radius, fillPaint);
        canvas.drawCircle((float)point.getCenterX(), (float)point.getCenterY(), radius, strokePaint);
    }
}
