package com.telerik.widget.chart.visualization.common;

import android.graphics.Point;

import java.util.List;

/**
 * Stores information about stacked series during an updateUI pass.
 */
public class StackedSeriesContext {

    private List<Point> previousStackedArea;

    /**
     * Initializes a new instance of the {@link StackedSeriesContext} class.
     */
    public StackedSeriesContext() {
    }

    /**
     * Clears the current stacked area.
     */
    public void clear() {
        this.previousStackedArea = null;
    }

    /**
     * Gets the previously stacked area.
     *
     * @return the stacked area.
     */
    public List<Point> getPreviousStackedArea() {
        return previousStackedArea;
    }

    /**
     * Sets the previously stacked area.
     *
     * @param previousStackedArea the new stacked area.
     */
    public void setPreviousStackedArea(List<Point> previousStackedArea) {
        this.previousStackedArea = previousStackedArea;
    }
}