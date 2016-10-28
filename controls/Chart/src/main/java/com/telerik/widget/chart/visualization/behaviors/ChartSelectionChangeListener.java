package com.telerik.widget.chart.visualization.behaviors;

/**
 * Implementers of this interface can listen for selection changes on RadCartesianChartView and RadPieChartView.
 */
public interface ChartSelectionChangeListener {

    /**
     * Called when the selection of the chart changes.
     * @param selectionContext A context object which selection information about the chart.
     */
    public void onSelectionChanged(ChartSelectionContext selectionContext);
}
