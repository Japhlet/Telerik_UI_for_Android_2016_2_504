package com.telerik.widget.chart.engine.series.combination.barSeries;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.series.CategoricalSeriesModel;
import com.telerik.widget.chart.engine.series.CategoricalSeriesRoundLayoutContext;
import com.telerik.widget.chart.engine.series.combination.CombineGroup;
import com.telerik.widget.chart.engine.series.combination.CombineStack;
import com.telerik.widget.chart.engine.series.combination.CombinedSeries;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesRoundLayoutStrategy;

/**
* Algorithm for rounding the layout of combined bar series.
*/
public class CombinedBarSeriesRoundLayoutStrategy extends CombinedSeriesRoundLayoutStrategy {

    /**
     * Creates a new instance of the {@link CombinedBarSeriesRoundLayoutStrategy} class.
     */
    public CombinedBarSeriesRoundLayoutStrategy() {
    }

    @Override
    public void applyLayoutRounding(ChartAreaModel chart, CombinedSeries series) {
        CategoricalSeriesModel categoricalSeries = (CategoricalSeriesModel) series.series().get(0);
        if (categoricalSeries == null) {
            return;
        }

        CategoricalSeriesRoundLayoutContext info = new CategoricalSeriesRoundLayoutContext(categoricalSeries);
        if (info.plotDirection() == AxisPlotDirection.VERTICAL) {
            this.applyLayoutRoundingVertical(series, info);
        } else {
            this.applyLayoutRoundingHorizontal(series, info);
        }
    }

    private void applyLayoutRoundingHorizontal(CombinedSeries series, CategoricalSeriesRoundLayoutContext info) {
        double previousStackTop = -1;
        CategoricalDataPoint firstPoint;
        CategoricalDataPoint point;
        CategoricalDataPoint previousPositivePoint = null;
        CategoricalDataPoint previousNegativePoint = null;

        for (CombineGroup group : series.groups()) {
            for (CombineStack stack : group.stacks()) {
                // first point is on the plot line
                firstPoint = (CategoricalDataPoint) stack.points().get(0);
                RadRect firstPointSlot = firstPoint.getLayoutSlot();
                double y = firstPointSlot.getY();
                if (previousStackTop != -1 && firstPoint.getParent().index() - 1 == previousPositivePoint.getParent().index()) {
                    y = (previousStackTop - firstPointSlot.getHeight());
                }

                firstPoint.arrange(new RadRect(firstPointSlot.getX(), y, firstPointSlot.getWidth(), firstPointSlot.getHeight()));
                info.snapPointToPlotLine(firstPoint);
                info.snapPointToGridLine(firstPoint);

                int count = stack.points().size();
                previousPositivePoint = firstPoint;
                previousNegativePoint = firstPoint;

                for (int i = 1; i < count; i++) {
                    point = (CategoricalDataPoint) stack.points().get(i);
                    RadRect slot = point.getLayoutSlot();
                    double x;
                    double localY = slot.getY();
                    if (previousStackTop != -1) {
                        localY = (previousStackTop - slot.getHeight());
                    }
                    if (point.isPositive) {
                        x = (previousPositivePoint.getLayoutSlot().getRight());
                        previousPositivePoint = point;
                    } else {
                        x = (previousNegativePoint.getLayoutSlot().getX() - slot.getWidth());
                        previousNegativePoint = point;
                    }

                    point.arrange(new RadRect(x, localY, slot.getWidth(), slot.getHeight()));
                    info.snapPointToGridLine(point);
                }

                previousStackTop = firstPoint.isEmpty ? -1 : firstPoint.getLayoutSlot().getY();
            }

            previousStackTop = -1;
        }
    }

    private void applyLayoutRoundingVertical(CombinedSeries series, CategoricalSeriesRoundLayoutContext info) {
        double previousStackRight = -1;
        CategoricalDataPoint firstPoint;
        CategoricalDataPoint point;
        CategoricalDataPoint previousPositivePoint = null;
        CategoricalDataPoint previousNegativePoint;

        for (CombineGroup group : series.groups()) {
            for (CombineStack stack : group.stacks()) {
                // first point is on the plot line
                firstPoint = (CategoricalDataPoint) stack.points().get(0);
                RadRect firstPointSlot = firstPoint.getLayoutSlot();
                double x = firstPointSlot.getX();
                if (previousStackRight != -1 && firstPoint.getParent().index() - 1 == previousPositivePoint.getParent().index()) {
                    x = previousStackRight;
                }

                firstPoint.arrange(new RadRect(x, firstPointSlot.getY(), firstPointSlot.getWidth(), firstPointSlot.getHeight()));
                info.snapPointToPlotLine(firstPoint);
                info.snapPointToGridLine(firstPoint);

                int count = stack.points().size();
                previousPositivePoint = firstPoint;
                previousNegativePoint = firstPoint;

                for (int i = 1; i < count; i++) {
                    point = (CategoricalDataPoint) stack.points().get(i);
                    RadRect slot = point.getLayoutSlot();
                    double localX = slot.getX();
                    if (previousStackRight != -1) {
                        localX = previousStackRight;
                    }

                    double localY;
                    if (point.isPositive) {
                        localY = (previousPositivePoint.getLayoutSlot().getY() - point.getLayoutSlot().getHeight());
                        previousPositivePoint = point;
                    } else {
                        localY = previousNegativePoint.getLayoutSlot().getBottom();
                        previousNegativePoint = point;
                    }

                    point.arrange(new RadRect(localX, localY, slot.getWidth(), slot.getHeight()));
                    info.snapPointToGridLine(point);
                }

                previousStackRight = firstPoint.isEmpty ? -1 : firstPoint.getLayoutSlot().getRight();
            }

            previousStackRight = -1;
        }
    }
}
