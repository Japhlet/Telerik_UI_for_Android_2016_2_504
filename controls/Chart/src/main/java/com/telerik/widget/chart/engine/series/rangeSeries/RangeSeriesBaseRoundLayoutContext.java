package com.telerik.widget.chart.engine.series.rangeSeries;

import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.chartAreas.CartesianChartAreaModel;
import com.telerik.widget.chart.engine.dataPoints.RangeDataPoint;

import java.util.Map;

/**
* Contains layout information for the range series.
*/
public class RangeSeriesBaseRoundLayoutContext {

    private RangeSeriesBaseModel series;
    public double plotLine;
    public double plotOrigin;
    public AxisPlotDirection plotDirection;
    public RadRect plotArea;

    /**
     * Creates a new instance of the {@link RangeSeriesBaseRoundLayoutContext} class.
     *
     * @param series The series to initialize the context with.
     */
    public RangeSeriesBaseRoundLayoutContext(RangeSeriesBaseModel series) {
        CartesianChartAreaModel cartesianChartArea = (CartesianChartAreaModel) series.chartArea();
        if (cartesianChartArea == null) {
            return;
        }

        this.series = series;
        this.plotDirection = series.getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);
        this.plotOrigin = series.getTypedValue(AxisModel.PLOT_ORIGIN_PROPERTY_KEY, 0D);
        this.plotArea = cartesianChartArea.getPlotArea().getLayoutSlot();
        double width = ((int) ((this.plotArea.getWidth() * series.chartArea().getView().getZoomWidth()) + .5));
        double height = ((int) ((this.plotArea.getHeight() * series.chartArea().getView().getZoomHeight()) + .5));
        this.plotArea = new RadRect(this.plotArea.getX(), this.plotArea.getY(), width, height);

        if (this.plotDirection == AxisPlotDirection.VERTICAL) {
            if (this.plotOrigin == 0) {
                this.plotLine = this.plotArea.getBottom();
            } else if (this.plotOrigin == 1) {
                this.plotLine = this.plotArea.getY();
            } else {
                double roundError = (series.getSecondAxis().majorTickCount() % 2) == 0 ? 0.5 : 0;
                this.plotLine = this.plotArea.getBottom() - (int) ((this.plotOrigin * this.plotArea.getHeight()) + roundError);
            }
        } else {
            if (this.plotOrigin == 0) {
                this.plotLine = this.plotArea.getX();
            } else if (this.plotOrigin == 1) {
                this.plotLine = this.plotArea.getRight();
            } else {
                double roundError = (series.getFirstAxis().majorTickCount() % 2) != 0 ? 0.5 : 0;
                this.plotLine = this.plotArea.getX() + (int) ((this.plotOrigin * this.plotArea.getWidth()) + roundError);
            }
        }
    }

    /**
     * Snaps the given {@link RangeDataPoint} to the grid line.
     *
     * @param point The point to snap.
     */
    public void snapPointToGridLine(RangeDataPoint point) {
        RadRect slot;
        double panOffsetX = this.series.chartArea().getView().getPanOffsetX();
        double panOffsetY = this.series.chartArea().getView().getPanOffsetY();

        if (point.numericalPlot().getSnapTickIndex() >= 0 && point.numericalPlot().getSnapTickIndex() < point.numericalPlot().getAxis().ticks().size()) {
            AxisTickModel highTick = point.numericalPlot().getAxis().ticks().get(point.numericalPlot().getSnapTickIndex());
            if (RadMath.areClose(point.numericalPlot().normalizedHigh, highTick.normalizedValue())) {
                slot = highTick.getLayoutSlot();
                double x = slot.getX() + panOffsetX;
                double y = slot.getY() + panOffsetY;
                slot = new RadRect(x, y, slot.getWidth(), slot.getHeight());
                if (this.plotDirection == AxisPlotDirection.VERTICAL) {
                    this.snapHighToVerticalGridLine(point, slot);
                } else {
                    this.snapHighToHorizontalGridLine(point, slot);
                }
            }
        }

        if (point.numericalPlot().snapBaseTickIndex >= 0 && point.numericalPlot().snapBaseTickIndex < point.numericalPlot().getAxis().ticks().size()) {
            AxisTickModel lowTick = point.numericalPlot().getAxis().ticks().get(point.numericalPlot().snapBaseTickIndex);
            if (RadMath.areClose(point.numericalPlot().normalizedLow, lowTick.normalizedValue())) {
                slot = lowTick.getLayoutSlot();
                double x = slot.getX() + panOffsetX;
                double y = slot.getY() + panOffsetY;
                slot = new RadRect(x, y, slot.getWidth(), slot.getHeight());
                if (this.plotDirection == AxisPlotDirection.VERTICAL) {
                    this.snapLowToVerticalGridLine(point, slot);
                } else {
                    this.snapLowToHorizontalGridLine(point, slot);
                }
            }
        }
    }

    void snapNormalizedValueToPreviousY(RangeDataPoint point, Map<Double, Double> normalizedValueToY) {

        RadRect slot = point.getLayoutSlot();
        if (!normalizedValueToY.containsKey(point.numericalPlot().normalizedLow)) {
            normalizedValueToY.put(point.numericalPlot().normalizedLow, slot.getBottom());
        }

        if (!normalizedValueToY.containsKey(point.numericalPlot().normalizedHigh)) {
            normalizedValueToY.put(point.numericalPlot().normalizedHigh, slot.getY());
        }

        double difference = normalizedValueToY.get(point.numericalPlot().normalizedLow) - slot.getBottom();
        double height = (slot.getHeight() + difference);

        difference = normalizedValueToY.get(point.numericalPlot().normalizedHigh) - slot.getY();
        double y = (slot.getY() + difference);
        height -= difference;

        point.arrange(new RadRect(slot.getX(), y, slot.getWidth(), height));
    }

    void snapNormalizedValueToPreviousX(RangeDataPoint point, Map<Double, Double> normalizedValueToX) {
        RadRect slot = point.getLayoutSlot();
        if (!normalizedValueToX.containsKey(point.numericalPlot().normalizedLow)) {
            normalizedValueToX.put(point.numericalPlot().normalizedLow, slot.getX());
        }

        if (!normalizedValueToX.containsKey(point.numericalPlot().normalizedHigh)) {
            normalizedValueToX.put(point.numericalPlot().normalizedHigh, slot.getRight());
        }

        double difference = normalizedValueToX.get(point.numericalPlot().normalizedLow) - slot.getX();
        double x = (slot.getX() + difference);
        double width = (slot.getWidth() - difference);

        difference = normalizedValueToX.get(point.numericalPlot().normalizedHigh) - slot.getRight();
        width += difference;

        point.arrange(new RadRect(x, slot.getY(), width, slot.getHeight()));
    }

    private void snapHighToHorizontalGridLine(RangeDataPoint point, final RadRect tickRect) {
        double difference;
        double gridLine = tickRect.getX() + (int) (tickRect.getWidth() / 2);

        RadRect slot = point.getLayoutSlot();
        difference = slot.getRight() - gridLine;
        double width = (slot.getWidth() - difference - 1);

        if (width < 0) {
            width = 0;
        }

        point.arrange(new RadRect(slot.getX(), slot.getY(), width, slot.getHeight()));
    }

    private void snapLowToHorizontalGridLine(RangeDataPoint point, final RadRect tickRect) {
        double difference;
        double gridLine = tickRect.getX() + (int) (tickRect.getWidth() / 2);

        RadRect slot = point.getLayoutSlot();
        difference = slot.getX() - gridLine;
        double x = (slot.getX() + 1 - difference);
        double width = (slot.getWidth() - 1 + difference);

        if (width < 0) {
            width = 0;
        }

        point.arrange(new RadRect(x, slot.getY(), width, slot.getHeight()));
    }

    private void snapHighToVerticalGridLine(RangeDataPoint point, final RadRect tickRect) {
        if (point.isEmpty) {
            return;
        }

        double difference;
        double gridLine = tickRect.getY() + (int) (tickRect.getHeight() / 2);

        RadRect slot = point.getLayoutSlot();
        difference = slot.getY() - gridLine;
        double y = (slot.getY() - difference);
        double height = (slot.getHeight() + difference);

        if (height < 0) {
            height = 0;
        }

        point.arrange(new RadRect(slot.getX(), y, slot.getWidth(), height));
    }

    private void snapLowToVerticalGridLine(RangeDataPoint point, final RadRect tickRect) {
        if (point.isEmpty) {
            return;
        }

        double difference;
        double gridLine = tickRect.getY() + (int) (tickRect.getHeight() / 2);

        RadRect slot = point.getLayoutSlot();
        difference = slot.getBottom() - gridLine;
        double height = (slot.getHeight() - difference);

        if (height < 0) {
            height = 0;
        }

        point.arrange(new RadRect(slot.getX(), slot.getY(), slot.getWidth(), height));
    }
}

