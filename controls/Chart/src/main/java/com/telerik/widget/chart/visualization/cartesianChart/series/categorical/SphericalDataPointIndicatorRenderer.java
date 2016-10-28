package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Canvas;
import android.util.TypedValue;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.visualization.common.ChartSeries;

/**
 * Renders a spherical indicator in the location of the data points of the owning series.
 */
public class SphericalDataPointIndicatorRenderer extends DataPointIndicatorRenderer {

    /**
     * The radius of the indicator.
     */
    protected float pointerIndicatorRadius;

    /**
     * Creates a new instance of the {@link SphericalDataPointIndicatorRenderer} class.
     *
     * @param owner the series that own the indicator.
     */
    public SphericalDataPointIndicatorRenderer(ChartSeries owner) {
        super(owner);
        this.pointerIndicatorRadius = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 3);
    }

    /**
     * Gets the radius of the indicator.
     *
     * @return the current radius.
     */
    public float getPointerIndicatorRadius() {
        return this.pointerIndicatorRadius;
    }

    /**
     * Sets the radius of the indicator.
     *
     * @param radius the new radius.
     */
    public void setPointerIndicatorRadius(float radius) {
        if (radius < 0)
            throw new IllegalArgumentException("radius cannot be negative");

        if (this.pointerIndicatorRadius == radius)
            return;

        this.pointerIndicatorRadius = radius;
    }

    @Override
    protected void drawDataPointIndicator(Canvas canvas, float x, float y) {
        canvas.drawCircle(x, y, this.pointerIndicatorRadius, this.pointIndicatorPaint);
        canvas.drawCircle(x, y, this.pointerIndicatorRadius, this.pointIndicatorStrokePaint);
    }
}
