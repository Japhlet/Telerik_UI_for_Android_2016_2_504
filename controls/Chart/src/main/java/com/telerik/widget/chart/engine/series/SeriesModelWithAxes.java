package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.DateTimeContinuousAxisModel;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.view.ChartView;

/**
 * Base class for series that can provide their own axes for the multiple axes feature of {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 *
 * @param <T> Must inherit from {@link DataPoint}
 */
public abstract class SeriesModelWithAxes<T extends DataPoint> extends DataPointSeriesModel<T> implements com.telerik.widget.chart.engine.axes.common.SeriesModelWithAxes {

    AxisModel firstAxis;
    AxisModel secondAxis;

    @Override
    public AxisModel getFirstAxis() {
        return this.firstAxis;
    }

    @Override
    public AxisModel getSecondAxis() {
        return this.secondAxis;
    }

    @Override
    public void attachAxis(AxisModel axis, AxisType type) {
        if (type == AxisType.FIRST) {
            this.firstAxis = axis;
        } else {
            this.secondAxis = axis;
        }
    }

    @Override
    public void detachAxis(AxisModel axis) {
        if (this.firstAxis == axis) {
            this.firstAxis = null;
        } else if (this.secondAxis == axis) {
            this.secondAxis = null;
        }
    }

    @Override
    protected void updateVisibleDataPointsCore() {
        super.updateVisibleDataPointsCore();

        int pointsCount = this.dataPoints.size();
        ChartView chart = this.chartArea.getView();
        RadRect plotAreaBounds = chart.getPlotAreaClip();
        double sizeFactor = plotAreaBounds.getWidth();

        int startIndex = 0;
        int endIndex;
        boolean horizontal = true;

        if (sizeFactor > 0) { // Covers the case where chart is initializing and is not yet measured.
            double zoomFactor;
            double panFactor;

            AxisModel firstAxis = this.getFirstAxis();
            if (firstAxis instanceof CategoricalAxisModel || firstAxis instanceof DateTimeContinuousAxisModel) {
                zoomFactor = sizeFactor * chart.getZoomWidth();
                panFactor = -chart.getPanOffsetX();
            } else {
                horizontal = false;
                sizeFactor = plotAreaBounds.getHeight();
                zoomFactor = sizeFactor * chart.getZoomHeight();
                panFactor = -chart.getPanOffsetY();
            }

            startIndex = (int) ((panFactor / zoomFactor) * pointsCount);
            startIndex -= 2; // -2 because we want to avoid precision errors at extreme zoom factors that cause the first point to be inside the plot area.
            // For example we want the first point of a line series in a zoomed chart to be outside the plot area so that panning is seamless.
            if (startIndex < 0)
                startIndex = 0;

            endIndex = (int) (((panFactor + sizeFactor) / zoomFactor) * pointsCount);
            endIndex += 3; // See comment above for start index.

            if (endIndex > pointsCount)
                endIndex = pointsCount;
        } else
            endIndex = pointsCount;

        this.visibleDataPoints.clear();
        if (horizontal)
            for (int i = startIndex; i < endIndex; i++)
                this.visibleDataPoints.add(this.dataPoints.get(i));
        else
            for (int i = this.dataPoints.size() - (1 + startIndex), end = i - (endIndex - startIndex); i > end; i--)
                this.visibleDataPoints.add(this.dataPoints.get(i));
    }
}
