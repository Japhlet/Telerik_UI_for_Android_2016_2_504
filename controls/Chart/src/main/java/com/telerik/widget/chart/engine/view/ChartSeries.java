package com.telerik.widget.chart.engine.view;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

/**
 * Identifies a special {@link ChartElementPresenter} that visualizes {@link DataPoint} instances.
 */
public interface ChartSeries extends ChartElementPresenter {
    /**
     * Occurs when a {@link DataPoint} owned by the series has its IsSelected property changed.
     */
    void onDataPointIsSelectedChanged(DataPoint point);
}

