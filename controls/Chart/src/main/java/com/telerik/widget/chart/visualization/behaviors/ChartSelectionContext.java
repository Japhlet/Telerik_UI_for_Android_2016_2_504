package com.telerik.widget.chart.visualization.behaviors;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.common.ChartSeries;

/**
 * This class provides information about the selection of ChartSelectionBehavior.
 */
public class ChartSelectionContext {
    private DataPoint deselectedDataPoint;
    private ChartSeries deselectedSeries;
    private ChartSelectionContext previousSelection;
    private DataPoint selectedDataPoints;
    private ChartSeries selectedSeries;
    private ChartSelectionBehavior owner;

    /**
     * Initializes a new instance of the ChartSelectionContext class.
     *
     * @param owner               The selection behavior that created this context.
     * @param selectedPoint       The newly selected data point.
     * @param deselectedDataPoint The previously selected data point.
     * @param selectedSeries      The newly selected series.
     * @param deselectedSeries    The previously selected series.
     * @param previousSelection   The previous selection context.
     */
    public ChartSelectionContext(ChartSelectionBehavior owner, DataPoint selectedPoint, DataPoint deselectedDataPoint, ChartSeries selectedSeries, ChartSeries deselectedSeries, ChartSelectionContext previousSelection) {
        this.selectedDataPoints = selectedPoint;
        this.selectedSeries = selectedSeries;
        this.deselectedDataPoint = deselectedDataPoint;
        this.deselectedSeries = deselectedSeries;
        this.previousSelection = previousSelection;
        this.owner = owner;
    }

    /**
     * Gets the selection behavior that created this context.
     */
    public ChartSelectionBehavior selectionBehavior() {
        return this.owner;
    }

    /**
     * Gets the previous selection context.
     */
    public ChartSelectionContext previousSelection() {
        return this.previousSelection;
    }

    /**
     * Gets the selected data point.
     */
    public DataPoint selectedDataPoint() {
        return this.selectedDataPoints;
    }

    /**
     * Gets the deselected data point.
     */
    public DataPoint deselectedDataPoint() {
        return this.deselectedDataPoint;
    }

    /**
     * Gets the selected series.
     */
    public ChartSeries selectedSeries() {
        return this.selectedSeries;
    }

    /**
     * Gets the deselected series.
     */
    public ChartSeries deselectedSeries() {
        return this.deselectedSeries;
    }
}
