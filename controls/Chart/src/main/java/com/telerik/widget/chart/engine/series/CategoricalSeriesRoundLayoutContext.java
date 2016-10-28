package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
import com.telerik.widget.chart.engine.chartAreas.CartesianChartAreaModel;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;

/**
* This class contains layout information for all categorical series.
*/
public class CategoricalSeriesRoundLayoutContext {

    private double plotLine;
    private double plotOrigin;
    private AxisPlotDirection plotDirection;
    private RadRect plotArea;
    private CategoricalSeriesModel series;

    /**
     * Creates a new instance of the {@link CategoricalSeriesRoundLayoutContext} class.
     *
     * @param series The series with which to initialize the context.
     */
    public CategoricalSeriesRoundLayoutContext(CategoricalSeriesModel series) {
        this.series = series;
        CartesianChartAreaModel cartesianChartArea = (CartesianChartAreaModel) series.chartArea();

        this.plotDirection = series.getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);
        this.plotOrigin = series.getTypedValue(AxisModel.PLOT_ORIGIN_PROPERTY_KEY, 0.0);
        RadRect slot = cartesianChartArea.getPlotArea().getLayoutSlot();
        double x = slot.getX() + series.chartArea().getView().getPanOffsetX();
        double y = slot.getY() + series.chartArea().getView().getPanOffsetY();
        this.plotArea = new RadRect(x, y, slot.getWidth(), slot.getHeight());

        this.plotArea = new RadRect(this.plotArea.getX(), this.plotArea.getY(),
                this.plotArea.getWidth() * series.chartArea().getView().getZoomWidth() + .5,
                this.plotArea.getHeight() * series.chartArea().getView().getZoomHeight() + .5);

        if (this.plotDirection == AxisPlotDirection.VERTICAL) {
            if (this.plotOrigin == 0) {
                this.plotLine = this.plotArea.getBottom();
            } else if (this.plotOrigin == 1) {
                this.plotLine = this.plotArea.getY();
            } else {
                double roundError = (series.getSecondAxis().majorTickCount() % 2) == 0 ? 0.5 : 0.0;
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
     * Gets the plot line.
     */
    public double plotLine() {
        return this.plotLine;
    }

    /**
     * Gets the plot origin.
     */
    public double plotOrigin() {
        return this.plotOrigin;
    }

    /**
     * Gets the plot direction.
     */
    public AxisPlotDirection plotDirection() {
        return this.plotDirection;
    }

    /**
     * Gets the plot area.
     *
     * @return A copy of the context's plot area.
     */
    public RadRect plotArea() {
        return this.plotArea;
    }

    /**
     * Snaps the given {@link CategoricalDataPoint} to the plot line.
     *
     * @param point The point to snap.
     */
    public void snapPointToPlotLine(CategoricalDataPointBase point) {
        RadRect pointLayoutSlot = point.getLayoutSlot();

        double x = pointLayoutSlot.getX();
        double y = pointLayoutSlot.getY();

        if (this.plotDirection == AxisPlotDirection.VERTICAL) {

            if (point.isPositive) {
                y = (Math.floor(this.plotLine) - pointLayoutSlot.getHeight());
            } else {
                y = Math.floor(this.plotLine);
            }

        } else {

            if (point.isPositive) {
                x = this.plotLine;
            } else {
                x = this.plotLine - pointLayoutSlot.getWidth();
            }
        }

        point.arrange(new RadRect(x, y, pointLayoutSlot.getWidth(), pointLayoutSlot.getHeight()));
    }

    /**
     * Snaps the given {@link CategoricalDataPoint} to the nearest grid line.
     *
     * @param point The point to snap.
     */
    public void snapPointToGridLine(CategoricalDataPointBase point) {
        if (point.numericalPlot == null) {
            return;
        }

        NumericalAxisPlotInfo plotInfo = (NumericalAxisPlotInfo) point.numericalPlot;

        if (plotInfo.getSnapTickIndex() == -1 ||
                plotInfo.getSnapTickIndex() >= plotInfo.getAxis().ticks().size()) {
            return;
        }

        AxisTickModel tick = plotInfo.getAxis().ticks().get(plotInfo.getSnapTickIndex());
        if (!RadMath.areClose(plotInfo.normalizedValue, tick.normalizedValue())) {
            return;
        }

        if (!point.getLayoutSlot().isSizeValid()) {
            return;
        }

        double panOffsetX = this.series.chartArea().getView().getPanOffsetX();
        double panOffsetY = this.series.chartArea().getView().getPanOffsetY();
        RadRect tickLayoutSlot = tick.getLayoutSlot();
        double tickX = tickLayoutSlot.getX() + panOffsetX;
        double tickY = tickLayoutSlot.getY() + panOffsetY;
        tickLayoutSlot = new RadRect(tickX, tickY, tickLayoutSlot.getWidth(), tickLayoutSlot.getHeight());

        if (this.plotDirection == AxisPlotDirection.VERTICAL) {
            this.snapToGridLineVertical(point, tickLayoutSlot);
        } else {
            this.snapToGridLineHorizontal(point, tickLayoutSlot);
        }
    }

    private void snapToGridLineHorizontal(CategoricalDataPointBase point, final RadRect tickRect) {
        double difference;
        double gridLine = tickRect.getX() + tickRect.getWidth() / 2;
        RadRect layoutSlot = point.getLayoutSlot();
        double right = layoutSlot.getRight();
        double x = layoutSlot.getX();
        double w = layoutSlot.getWidth();

        if (point.isPositive) {
            difference = right - gridLine;
            w = w - difference - 1;
        } else {
            difference = gridLine - x;
            x += difference;
            w -= difference;
        }

        if (w < 0) {
            w = 0;
        }

        point.arrange(new RadRect(x, layoutSlot.getY(), w, layoutSlot.getHeight()));
    }

    private void snapToGridLineVertical(CategoricalDataPointBase point, final RadRect tickRect) {
        double difference;
        double gridLine = tickRect.getY() + tickRect.getHeight() / 2;

        RadRect layoutSlot = point.getLayoutSlot();
        double x = layoutSlot.getX();
        double y = layoutSlot.getY();
        double w = layoutSlot.getWidth();
        double h = layoutSlot.getHeight();
        double bottom = layoutSlot.getBottom();

        if (point.isPositive) {
            difference = y - gridLine;
            y -= difference;
            h += difference;
        } else {
            difference = gridLine - bottom;
            h = h + difference + 1;
        }

        if (h < 0) {
            h = 0;
        }

        point.arrange(new RadRect(x, y, w, h));
    }
}

