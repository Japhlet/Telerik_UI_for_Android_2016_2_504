package com.telerik.widget.chart.engine.axes;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.elementTree.ContentNode;

/**
 * This class represents the axis' title in the {@link com.telerik.widget.chart.engine.chartAreas.ChartAreaModel} infrastructure.
 */
public class AxisTitleModel extends ContentNode {

    /**
     * Creates a new instance of the {@link AxisTitleModel} class.
     */
    public AxisTitleModel() {
        this.trackPropertyChanged = true;
    }

    @Override
    protected void unloadCore() {
        this.desiredSize = RadSize.getEmpty();
        super.unloadCore();
    }
}

