package com.telerik.widget.chart.engine.decorations.annotations.plotBand;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.view.ChartView;

/**
 * Represents a {@link PlotBandAnnotationModel} that displays an annotation in cartesian charts.
 */
public class CartesianPlotBandAnnotationModel extends PlotBandAnnotationModel {

    /**
     * Creates a new instance of the {@link CartesianPlotBandAnnotationModel} class.
     */
    public CartesianPlotBandAnnotationModel() {
    }

    @Override
    protected RadRect arrangeCore(final RadRect rect) {
        final ChartView view = this.chartArea.getView();
        final RadRect plotAreaVirtualSize = new RadRect(rect.getX(), rect.getY(), rect.getWidth() * view.getZoomWidth(), rect.getHeight() * view.getZoomHeight());
        final RadPoint point1, point2;

        if (this.getAxis().getType() == AxisType.FIRST) {
            point1 = new RadPoint(view.getPanOffsetX() + this.firstPlotInfo.centerX(plotAreaVirtualSize), plotAreaVirtualSize.getY() + view.getPanOffsetY());
            point2 = new RadPoint(view.getPanOffsetX() + this.secondPlotInfo.centerX(plotAreaVirtualSize), plotAreaVirtualSize.getBottom() + view.getPanOffsetY());
        } else {
            point1 = new RadPoint(rect.getX(), view.getPanOffsetY() + this.firstPlotInfo.centerY(plotAreaVirtualSize));
            point2 = new RadPoint(rect.getRight(), view.getPanOffsetY() + this.secondPlotInfo.centerY(plotAreaVirtualSize));
        }

        return new RadRect(point1, point2);
    }
}

