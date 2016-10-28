package com.telerik.widget.chart.engine.decorations.annotations.line;

import com.telerik.android.common.math.RadLine;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.view.ChartView;

/**
 * Represents a chart annotation that is plotted on a grid line on a single axis.
 */
public class CartesianGridLineAnnotationModel extends GridLineAnnotationModel {
    RadLine line;

    /**
     * Creates a new instance of the {@link CartesianGridLineAnnotationModel} class.
     */
    public CartesianGridLineAnnotationModel() {
    }

    @Override
    protected RadRect arrangeCore(final RadRect rect) {
        ChartView view = this.chartArea.getView();

        if (getAxis() != null && value != null && plotInfo == null) {
            throw new RuntimeException("the current axis and value for the grid annotation is incompatible");
        }

        RadRect plotAreaVirtualSize = new RadRect(rect.getX(), rect.getY(), rect.getWidth() * view.getZoomWidth(),
                rect.getHeight() * view.getZoomHeight());
        RadPoint point1, point2;

        if (this.getAxis().getType() == AxisType.FIRST) {
            point1 = new RadPoint(view.getPanOffsetX() + this.plotInfo.centerX(plotAreaVirtualSize), rect.getY());
            point2 = new RadPoint(point1.getX(), rect.getBottom());
        } else {
            point1 = new RadPoint(rect.getX(), view.getPanOffsetY() + this.plotInfo.centerY(plotAreaVirtualSize));
            point2 = new RadPoint(rect.getRight(), point1.getY());
        }

        this.line = new RadLine(point1, point2);
        this.line = RadLine.round(this.line);

        return new RadRect(point1, point2);
    }
}

