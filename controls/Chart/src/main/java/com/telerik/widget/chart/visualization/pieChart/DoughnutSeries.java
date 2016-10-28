package com.telerik.widget.chart.visualization.pieChart;

import android.content.Context;

import com.telerik.android.common.math.RadRect;

/**
 * Represents a special type of {@link com.telerik.widget.chart.visualization.common.ChartSeries}
 * which is used in {@link RadPieChartView}.
 * <p/>
 * Each data items provided through DoughnutSeries is visually represented by an arc.
 * All arcs together form a shape similar to a doughnut.
 * These series extend the {@link PieSeries}.
 */
public class DoughnutSeries extends PieSeries {

    private final float DEFAULT_INNER_RADIUS_FACTOR = 0.5F;
    private float innerRadiusFactor = DEFAULT_INNER_RADIUS_FACTOR;

    /**
     * Initializes a new instance of the {@link DoughnutSeries} class with passed
     * {@link Context} as argument.
     */
    public DoughnutSeries() {
    }

    /**
     * Gets the inner radius factor that will be used for these series.
     * This factor defines the ratio between the radius of the inner circle of the
     * doughnut and the actual radius.
     * The default value is <code>0.5</code>. This means that the radius of the inner circle
     * will be half of the radius of the actual circle.
     * The possible values are between <code>0</code> and <code>1.0</code> excluded.
     *
     * @return the inner radius factor
     */
    public float getInnerRadiusFactor() {
        return this.innerRadiusFactor;
    }

    /**
     * Sets the inner radius factor that will be used for these series.
     * This factor defines the ratio between the radius of the inner circle of the
     * doughnut and the actual radius.
     * The default value is <code>0.5</code>. This means that the radius of the inner circle
     * will be half of the radius of the actual circle.
     * The possible values are between <code>0</code> and <code>1.0</code> excluded.
     *
     * @param value the inner radius factor
     */
    public void setInnerRadiusFactor(float value) {

        if (value <= 0 || value >= 1) {
            throw new IllegalArgumentException(
                    "The inner radius factor is not valid. " +
                            "The possible values are in the (0,1) interval."
            );
        }

        this.innerRadiusFactor = value;
    }

    @Override
    protected void setupUpdateContext(final RadRect availableSize) {
        super.setupUpdateContext(availableSize);

        DoughnutUpdateContext doughnutUpdateContext = (DoughnutUpdateContext) this.updateContext;
        if (doughnutUpdateContext != null) {
            doughnutUpdateContext.innerRadiusFactor = this.getInnerRadiusFactor();
        }
    }

    @Override
    protected PieSegment createSegment() {
        return new DoughnutSegment(this);
    }

    @Override
    protected PieUpdateContext createUpdateContext() {
        return new DoughnutUpdateContext();
    }
}
