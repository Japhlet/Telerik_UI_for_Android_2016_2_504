package com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisOhlcPlotInfo;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.OhlcSeriesBase;
import com.telerik.widget.palettes.PaletteEntry;

public class OhlcPointRenderer extends OhlcPointRendererBase {

    private float tickLength;
    protected Paint downStrokePaint = new Paint();

    public OhlcPointRenderer(OhlcSeriesBase series) {
        super(series);
        this.tickLength = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5);
        this.downStrokePaint.setColor(Color.parseColor("#d70202"));
        this.downStrokePaint.setStrokeWidth(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 2));
    }

    public void setDownStroke(Paint value) {
        this.downStrokePaint = value;
    }

    public Paint getDownStroke() {
        return this.downStrokePaint;
    }

    @Override
    protected void renderPointCore(Canvas canvas, DataPoint point) {
        OhlcDataPoint ohlcPoint = (OhlcDataPoint) point;
        NumericalAxisOhlcPlotInfo plotInfo = ohlcPoint.getNumericalPlot();
        if(plotInfo == null) {
            return;
        }

        Paint paint;
        if(getSeries().isPaletteApplied() && this.pointColors().containsKey(ohlcPoint)) {
            PaletteEntry entry = this.pointColors().get(ohlcPoint);

            if(!ohlcPoint.getIsSelected() && ohlcPoint.isFalling()) {
               paint = this.downStrokePaint;
            } else {
                paint = new Paint();
                paint.setColor(entry.getStroke());
                paint.setStrokeWidth(entry.getStrokeWidth());
            }
            this.strokePaint = paint;

        } else {
            if (ohlcPoint.isFalling()) {
                this.strokePaint = this.downStrokePaint;
            } else {
                this.strokePaint = this.upStrokePaint;
            }

            paint = this.strokePaint;
        }

        RectF pointSlot = Util.convertToRectF(point.getLayoutSlot());

        this.renderTicks(canvas, pointSlot, plotInfo, paint);

        canvas.drawLine(pointSlot.centerX(), pointSlot.top, pointSlot.centerX(), pointSlot.bottom, paint);
    }

    private void renderTicks(Canvas canvas, RectF pointSlot, NumericalAxisOhlcPlotInfo plotInfo, Paint strokePaint) {
        float halfStrokeWidth = this.strokePaint.getStrokeWidth() / 2;
        canvas.drawLine(pointSlot.centerX() - tickLength, (float)plotInfo.physicalOpen, pointSlot.centerX() + halfStrokeWidth, (float)plotInfo.physicalOpen, strokePaint);
        canvas.drawLine(pointSlot.centerX() + tickLength, (float)plotInfo.physicalClose, pointSlot.centerX() - halfStrokeWidth, (float)plotInfo.physicalClose, strokePaint);
    }
}
