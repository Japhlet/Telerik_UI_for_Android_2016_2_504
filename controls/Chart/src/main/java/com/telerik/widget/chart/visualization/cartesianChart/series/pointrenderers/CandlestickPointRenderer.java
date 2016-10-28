package com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisOhlcPlotInfo;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.CandlestickSeries;
import com.telerik.widget.palettes.PaletteEntry;

public class CandlestickPointRenderer extends OhlcPointRendererBase {

    private Paint bodyPaint = new Paint();

    /**
     * Creates a new instance of the {@link CandlestickPointRenderer} class.
     *
     * @param series owner series.
     */
    public CandlestickPointRenderer(CandlestickSeries series) {
        super(series);
    }

    /**
     * Gets the current body paint.
     *
     * @return the current body paint.
     */
    public Paint getBodyPaint() {
        return this.bodyPaint;
    }

    /**
     * Sets the current body paint.
     *
     * @param paint the new body paint.
     */
    public void setBodyPaint(Paint paint) {
        if (paint == null)
            throw new NullPointerException("paint");

        this.bodyPaint = paint;
    }

    @Override
    protected void renderPointCore(Canvas canvas, DataPoint point) {
        OhlcDataPoint ohlcPoint = (OhlcDataPoint) point;
        NumericalAxisOhlcPlotInfo plotInfo = ohlcPoint.getNumericalPlot();

        if(plotInfo == null) {
            return;
        }

        RectF pointSlot = Util.convertToRectF(point.getLayoutSlot());

        Paint strokePaint;
        Paint bodyPaint;
        if (getSeries().isPaletteApplied() && this.pointColors().containsKey(ohlcPoint)) {
            PaletteEntry entry = this.pointColors().get(ohlcPoint);
            int color = entry.getStroke();
            float strokeWidth = entry.getStrokeWidth();
            bodyPaint = new Paint();
            bodyPaint.setColor(color);
            bodyPaint.setStrokeWidth(strokeWidth);
            strokePaint = new Paint();
            strokePaint.setColor(color);
            strokePaint.setStrokeWidth(strokeWidth);
        } else {
            strokePaint = this.upStrokePaint;
            bodyPaint = this.bodyPaint;
        }

        if (ohlcPoint.isFalling()) {
            bodyPaint.setStyle(Paint.Style.STROKE);
        } else {
            bodyPaint.setStyle(Paint.Style.FILL);
        }

        float stickUpperMiddle = (float) (Math.min(plotInfo.physicalOpen, plotInfo.physicalClose));
        float stickLowerMiddle = (float) (Math.max(plotInfo.physicalOpen, plotInfo.physicalClose));

        canvas.drawLine(pointSlot.centerX(), pointSlot.top, pointSlot.centerX(), stickUpperMiddle, strokePaint);
        canvas.drawLine(pointSlot.centerX(), stickLowerMiddle, pointSlot.centerX(), pointSlot.bottom, strokePaint);

        canvas.drawRect(pointSlot.left, stickUpperMiddle, pointSlot.right, stickLowerMiddle, bodyPaint);
    }
}
