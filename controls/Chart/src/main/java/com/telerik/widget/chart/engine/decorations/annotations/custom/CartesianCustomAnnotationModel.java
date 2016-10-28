package com.telerik.widget.chart.engine.decorations.annotations.custom;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.view.ChartView;

/**
 * Custom annotation for {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 */
public class CartesianCustomAnnotationModel extends CustomAnnotationModel {

    /**
     * Creates a new instance of the {@link CartesianCustomAnnotationModel} class.
     */
    public CartesianCustomAnnotationModel() {
    }

    @Override
    protected RadRect arrangeCore(final RadRect rect) {
        ChartView view = this.chartArea.getView();
        RadRect plotAreaVirtualSize = new RadRect(rect.getX(), rect.getY(), rect.getWidth() * view.getZoomWidth(), rect.getHeight() * view.getZoomHeight());
        RadPoint centerPoint = new RadPoint(view.getPanOffsetX() + this.firstPlotInfo.centerX(plotAreaVirtualSize), view.getPanOffsetY() + this.secondPlotInfo.centerY(plotAreaVirtualSize));
        RadSize desiredSize = this.measure();

        return new RadRect(centerPoint.getX(), centerPoint.getY(), desiredSize.getWidth(), desiredSize.getHeight());
    }
}
