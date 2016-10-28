package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.dataPoints.ScatterDataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;

/**
 * This model arranges scatter points on the plot area.
 */
public class ScatterSeriesModel extends SeriesModelWithAxes<ScatterDataPoint> {
    public ScatterSeriesModel() {
        super();

        this.setVirtualizationEnabled(false);
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child instanceof ScatterDataPoint) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        RadRect rectZoomed = this.getZoomedRect(rect);

        double x, y;

        double panOffsetX = chartArea().getView().getPanOffsetX();
        double panOffsetY = chartArea().getView().getPanOffsetY();
        for (ScatterDataPoint point : this.visibleDataPoints()) {
            if (point.getXPlot() == null || point.getYPlot() == null) {
                continue;
            }

            x = point.getXPlot().centerX(rectZoomed);
            y = point.getYPlot().centerY(rectZoomed);

            point.arrange(new RadRect(x + panOffsetX, y + panOffsetY, 0, 0), false);
        }

        return rectZoomed;
    }
}

