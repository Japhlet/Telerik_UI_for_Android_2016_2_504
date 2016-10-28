package com.telerik.widget.chart.visualization.common.renderers;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

public class ScatterLineRenderer extends LineRenderer {
    @Override
    protected boolean shouldDrawPoint(DataPoint point) {
        return true;
    }
}
