package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.dataPoints.RangeDataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;

/**
 * Label renderer handling the labels in the {@link RangeBarSeries} instances.
 */
public class RangeBarSeriesLabelRenderer extends BaseLabelRenderer {

    /**
     * Creates a new instance of the {@link RangeBarSeriesLabelRenderer} class.
     *
     * @param owner the chart series owning this renderer instance.
     */
    public RangeBarSeriesLabelRenderer(ChartSeries owner) {
        super(owner);
    }

    @Override
    public void renderLabel(Canvas canvas, ChartNode dataPoint) {
        RangeDataPoint rangePoint = (RangeDataPoint) dataPoint;

        String lowLabelText = String.format(this.getLabelFormat(), rangePoint.getLow());
        String highLabelText = String.format(this.getLabelFormat(), rangePoint.getHigh());

        Rect textBounds = getTextBounds(highLabelText, this.labelTextPaint);

        RadRect pointSlot = rangePoint.getLayoutSlot();
        RadRect parentSlot = rangePoint.getParent().getLayoutSlot();

        boolean isChartZoomedHorizontally = isChartZoomedHorizontally();
        boolean isChartZoomedVertically = isChartZoomedVertically();

        double x = ((pointSlot.getX() + (pointSlot.getWidth() / 2)) - textBounds.width() / 2);
        double y = pointSlot.getY() - (this.labelMargin + this.labelPaddingBottom + this.labelStrokeWidth);

        if (!isChartZoomedHorizontally) {
            x = preventClippingLeft(x, parentSlot);
            x = preventClippingRight(x, parentSlot, textBounds);
        }

        if (!isChartZoomedVertically)
            y = preventClippingTop(y, parentSlot, textBounds);

        Rect labelBackgroundRect = getLabelBackgroundBounds(new RadPoint(x, y), textBounds);
        Path shape = new Path();
        prepareLabel(shape, labelBackgroundRect, dataPoint.getLayoutSlot());

        drawLabelBackground(canvas, shape, dataPoint.index());
        drawLabelText(canvas, highLabelText, (float)x, (float)y);

        textBounds = getTextBounds(lowLabelText, this.labelTextPaint);

        x = ((pointSlot.getX() + (pointSlot.getWidth() / 2)) - textBounds.width() / 2);
        y = (pointSlot.getBottom() + textBounds.height() + (this.labelPaddingTop + this.labelStrokeWidth + this.labelMargin));

        if (!isChartZoomedHorizontally) {
            x = preventClippingLeft(x, parentSlot);
            x = preventClippingRight(x, parentSlot, textBounds);
        }

        if (!isChartZoomedVertically)
            y = preventClippingBottom(y, parentSlot);

        labelBackgroundRect = getLabelBackgroundBounds(new RadPoint(x, y), textBounds);
        shape.reset();
        prepareLabel(shape, labelBackgroundRect, dataPoint.getLayoutSlot());

        drawLabelBackground(canvas, shape, dataPoint.index());
        drawLabelText(canvas, lowLabelText, (float)x, (float)y);
    }
}
