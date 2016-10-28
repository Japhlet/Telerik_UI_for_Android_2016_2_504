package com.telerik.widget.chart.engine.series.rangeSeries;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;
import com.telerik.widget.chart.engine.dataPoints.RangeDataPoint;
import com.telerik.widget.chart.engine.series.combination.CombineGroup;
import com.telerik.widget.chart.engine.series.combination.CombineStack;
import com.telerik.widget.chart.engine.series.combination.CombinedSeries;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesRoundLayoutStrategy;

import java.util.HashMap;
import java.util.Map;

/**
* Defines the layout algorithm for combined range bars.
*/
public class CombinedRangeBarSeriesRoundLayoutStrategy extends CombinedSeriesRoundLayoutStrategy {
    /**
     * Creates a new instance of the {@link CombinedRangeBarSeriesRoundLayoutStrategy} class.
     */
    public CombinedRangeBarSeriesRoundLayoutStrategy() {
    }

    @Override
    public void applyLayoutRounding(ChartAreaModel chartArea, CombinedSeries series) {
        RangeBarSeriesModel rangeSeriesModel = (RangeBarSeriesModel) series.series().get(0);
        if (rangeSeriesModel == null) {
            return;
        }

        RangeSeriesBaseRoundLayoutContext info = new RangeSeriesBaseRoundLayoutContext(rangeSeriesModel);
        if (info.plotDirection == AxisPlotDirection.VERTICAL) {
            this.applyLayoutRoundingVertical(series, info);
        } else {
            this.applyLayoutRoundingHorizontal(series, info);
        }
    }

    private void applyLayoutRoundingVertical(CombinedSeries series, RangeSeriesBaseRoundLayoutContext roundLayoutContext) {
        double previousStackRight = -1.0;
        RangeDataPoint firstPoint;

        Map<Double, Double> normalizedValueToY = new HashMap<Double, Double>();

        for (CombineGroup group : series.groups()) {
            for (CombineStack stack : group.stacks()) {
                firstPoint = (RangeDataPoint) stack.points().get(0);
                if (!firstPoint.isEmpty) {
                    roundLayoutContext.snapPointToGridLine(firstPoint);

                    // Handles visual glitches that might occur between clustered range bars.
                    roundLayoutContext.snapNormalizedValueToPreviousY(firstPoint, normalizedValueToY);

                    this.snapLeftToPreviousRight(firstPoint, previousStackRight);
                    previousStackRight = firstPoint.getLayoutSlot().getRight();
                } else {
                    previousStackRight = -1.0;
                }
            }
            previousStackRight = -1.0;
        }
    }

    private void snapLeftToPreviousRight(RangeDataPoint firstPoint, double previousStackRight) {
        if (previousStackRight != -1) {
            RadRect slot = firstPoint.getLayoutSlot();
            double x = slot.getX();
            double w = slot.getWidth();
            double difference = previousStackRight - x;
            x += difference;
            w -= difference;

            firstPoint.arrange(new RadRect(x, slot.getY(), w, slot.getHeight()));
        }
    }

    private void applyLayoutRoundingHorizontal(CombinedSeries series, RangeSeriesBaseRoundLayoutContext roundLayoutContext) {
        double previousStackTop = -1.0;
        RangeDataPoint firstPoint;

        Map<Double, Double> normalizedValueToX = new HashMap<Double, Double>();

        for (CombineGroup group : series.groups()) {
            for (CombineStack stack : group.stacks()) {
                firstPoint = (RangeDataPoint) stack.points().get(0);

                if (!firstPoint.isEmpty) {
                    roundLayoutContext.snapPointToGridLine(firstPoint);

                    // Handles visual glitches that might occur between clustered range bars.
                    roundLayoutContext.snapNormalizedValueToPreviousX(firstPoint, normalizedValueToX);

                    this.snapBottomToPreviousTop(firstPoint, previousStackTop);
                    previousStackTop = firstPoint.getLayoutSlot().getY();
                } else {
                    previousStackTop = -1.0;
                }
            }
            previousStackTop = -1.0;
        }
    }

    private void snapBottomToPreviousTop(RangeDataPoint firstPoint, double previousStackTop) {
        if (previousStackTop != -1) {
            RadRect slot = firstPoint.getLayoutSlot();
            double difference = previousStackTop - slot.getBottom();
            double h = (slot.getHeight() + difference);

            firstPoint.arrange(new RadRect(slot.getX(), slot.getY(), slot.getWidth(), h));
        }
    }
}