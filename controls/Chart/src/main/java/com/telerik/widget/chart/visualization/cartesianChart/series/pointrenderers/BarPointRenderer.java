package com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.BarSeries;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.HashMap;

public class BarPointRenderer extends ChartDataPointRendererBase<BarSeries> {
    private HashMap<DataPoint, PaletteEntry> pointColors = new HashMap<DataPoint, PaletteEntry>();

    public BarPointRenderer(BarSeries series) {
        super(series);
    }

    public HashMap<DataPoint, PaletteEntry> pointColors() {
        return this.pointColors;
    }

    @Override
    protected void renderPointCore(Canvas canvas, DataPoint point) {
        BarSeries series = this.getSeries();

        float roundBarsRadius = series.getRoundBarsRadius();
        RadRect layoutSlot = point.getLayoutSlot();
        if(layoutSlot.getHeight() == 0 || layoutSlot.getWidth() == 0) {
            return;
        }

        RectF pointRect = Util.convertToRectF(layoutSlot);

        Paint fillPaint;
        Paint strokePaint;
        if(series.isPaletteApplied() && this.pointColors.containsKey(point)) {
            PaletteEntry entry = this.pointColors.get(point);
            fillPaint = new Paint();
            fillPaint.setColor(entry.getFill());

            strokePaint = new Paint();
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(entry.getStroke());
        } else {
            strokePaint = series.getFillPaint();
            fillPaint = series.getStrokePaint();
        }

        float strokeWidth = this.getSeries().getStrokeWidth();
        strokePaint.setStrokeWidth(strokeWidth);

        strokeWidth /= 2.0f;
        pointRect.left += strokeWidth;
        pointRect.right -= strokeWidth;
        pointRect.top += strokeWidth;
        pointRect.bottom -= strokeWidth;

        if (series.getAreBarsRounded()) {
            canvas.drawRoundRect(pointRect, roundBarsRadius, roundBarsRadius, fillPaint);
            canvas.drawRoundRect(pointRect, roundBarsRadius, roundBarsRadius, strokePaint);
        } else {
            canvas.drawRect(pointRect, fillPaint);
            canvas.drawRect(pointRect, strokePaint);
        }
    }
}
