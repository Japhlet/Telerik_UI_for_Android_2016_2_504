package com.telerik.widget.chart.engine.view;

import com.telerik.android.common.math.RadRect;

/**
 * Defines the base api of RadChartView.
 */
public interface ChartView extends ChartElementPresenter {
    /**
     * Gets the visible width of the widget view.
     */
    public double getViewportWidth();

    /**
     * Gets the visible height of the widget view.
     */
    public double getViewportHeight();

    /**
     * Gets the current scale applied along the horizontal direction.
     */
    public double getZoomWidth();

    /**
     * Gets the current scale applied along the vertical direction.
     */
    public double getZoomHeight();

    /**
     * Gets the x-coordinate of the top-left corner where the layout should start from.
     */
    public double getPanOffsetX();

    /**
     * Gets the y-coordinate of the top-left corner where the layout should start from.
     */
    public double getPanOffsetY();

    /**
     * Gets the rect that encloses the plot area in view coordinates - that is without the zoom
     * factor applied and with the pan offset calculated.
     */
    public RadRect getPlotAreaClip();
}

