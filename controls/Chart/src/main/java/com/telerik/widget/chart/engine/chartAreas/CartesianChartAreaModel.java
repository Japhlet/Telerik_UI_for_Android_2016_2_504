package com.telerik.widget.chart.engine.chartAreas;

import com.telerik.android.common.DataTuple;
import com.telerik.android.common.RadThickness;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation;
import com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation;
import com.telerik.widget.chart.engine.decorations.CartesianChartGridModel;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineStrategy;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link ChartAreaModelWithAxes} that introduces areas.
 */
public class CartesianChartAreaModel extends ChartAreaModelWithAxes {

    private final static String NO_HORIZONTAL_AXIS_KEY = "NoHorizontalAxis";
    private final static String NO_VERTICAL_AXIS_KEY = "NoVerticalAxis";
    private final static String NO_SERIES_KEY = "NoSeries";
    private final static String NO_DATA_KEY = "NoData";

    /**
     * Creates an instance of the {@link CartesianChartAreaModel} class.
     */
    public CartesianChartAreaModel() {
    }

    /**
     * Gets the {@link CartesianChartGridModel} instance that decorates the background of this plot area.
     *
     * @return the current grid model.
     */
    public CartesianChartGridModel getGrid() {
        return (CartesianChartGridModel) this.grid;
    }

    @Override
    public void applyLayoutRounding() {
        for (AxisModel axis : this.firstAxes) {
            axis.applyLayoutRounding();
        }
        for (AxisModel axis : this.secondAxes) {
            axis.applyLayoutRounding();
        }

        for (ChartSeriesCombineStrategy seriesCombineStrategy : this.seriesCombineStrategies.values()) {
            if (seriesCombineStrategy.hasCombination) {
                seriesCombineStrategy.applyLayoutRounding(this);
            } else {
                // ask each series to apply layout rounding
                for (ChartSeriesModel series : seriesCombineStrategy.nonCombinedSeries) {
                    series.applyLayoutRounding();
                }
            }
        }
    }

    @Override
    public Iterable<String> getNotLoadedReasons() {
        ArrayList<String> reasons = new ArrayList<String>();
        if (this.firstAxes.size() == 0) {
            reasons.add(NO_HORIZONTAL_AXIS_KEY);
        }

        if (this.secondAxes.size() == 0) {
            reasons.add(NO_VERTICAL_AXIS_KEY);
        }

        if (this.getSeries().size() == 0) {
            reasons.add(NO_SERIES_KEY);
        } else {
            boolean noData = true;
            for (ChartSeriesModel series : this.getSeries()) {
                if (series.dataPoints().size() > 0) {
                    noData = false;
                    break;
                }
            }

            if (noData) {
                reasons.add(NO_DATA_KEY);
            }
        }

        return reasons;
    }

    @Override
    public DataTuple convertPointToData(final RadPoint coordinates, AxisModel firstAxis, AxisModel secondAxis) {
        Object x = null, y = null;

        if (firstAxis != null) {
            x = firstAxis.convertPhysicalUnitsToData(coordinates.getX());
        }
        if (secondAxis != null) {
            y = secondAxis.convertPhysicalUnitsToData(coordinates.getY());
        }

        return new DataTuple<Object, Object, Object>(x, y);
    }

    @Override
    protected RadRect arrangeAxes(final RadRect rect) {
        RadSize availableSize = new RadSize(rect.getWidth(), rect.getHeight());

        // Populate stacks
        AxisStack[] stacks = this.prepareAxesStacks(availableSize);
        AxisStack leftStack = stacks[0];
        AxisStack topStack = stacks[1];
        AxisStack rightStack = stacks[2];
        AxisStack bottomStack = stacks[3];
        RadRect plotAreaRect = this.calculatePlotAreaRect(rect, leftStack, topStack, rightStack, bottomStack);

        final int maxIterations = 10;
        int currentIteration = 0;
        // axes may need several passes to adjust their desired size due to label fit mode
        boolean isArrangeValid;
        RadRect finalRect;
        RadSize lastAxisDesiredSize;
        do {
            isArrangeValid = true;
            // Although this seems an anti-pattern, it actually is safety coding
            // The logic behind axes layout is not completely verified yet and we do not want to enter an endless loop
            if (currentIteration > maxIterations) {
                Assert.fail("Entering endless loop");
                break;
            }

            if (!leftStack.getIsEmpty()) {
                double lastRightPoint = plotAreaRect.getX();
                for (AxisModel axis : leftStack.axes) {
                    finalRect = new RadRect(lastRightPoint - axis.getDesiredSize().getWidth(), plotAreaRect.getY(), axis.getDesiredSize().getWidth(), plotAreaRect.getHeight());
                    lastRightPoint = finalRect.getX();
                    lastAxisDesiredSize = axis.getDesiredSize();
                    axis.arrange(finalRect);
                    if (axis.getDesiredSize().getWidth() != lastAxisDesiredSize.getWidth()) {
                        leftStack.desiredWidth += axis.getDesiredSize().getWidth() - lastAxisDesiredSize.getWidth();
                        isArrangeValid = false;
                    }
                }
            }

            if (!topStack.getIsEmpty()) {
                double lastBottomPoint = plotAreaRect.getY();
                for (AxisModel axis : topStack.axes) {
                    finalRect = new RadRect(plotAreaRect.getX(), lastBottomPoint - axis.getDesiredSize().getHeight(), plotAreaRect.getWidth(), axis.getDesiredSize().getHeight());
                    lastBottomPoint = finalRect.getY();
                    lastAxisDesiredSize = axis.getDesiredSize();
                    axis.arrange(finalRect);
                    if (axis.getDesiredSize().getHeight() != lastAxisDesiredSize.getHeight()) {
                        topStack.desiredHeight += axis.getDesiredSize().getHeight() - lastAxisDesiredSize.getHeight();
                        isArrangeValid = false;
                    }
                }
            }

            if (!rightStack.getIsEmpty()) {
                double lastLeftPoint = plotAreaRect.getRight();
                for (AxisModel axis : rightStack.axes) {
                    finalRect = new RadRect(lastLeftPoint, plotAreaRect.getY(), axis.getDesiredSize().getWidth(), plotAreaRect.getHeight());
                    lastLeftPoint = finalRect.getRight();
                    lastAxisDesiredSize = axis.getDesiredSize();
                    axis.arrange(finalRect);
                    if (axis.getDesiredSize().getWidth() != lastAxisDesiredSize.getWidth()) {
                        rightStack.desiredWidth += axis.getDesiredSize().getWidth() - lastAxisDesiredSize.getWidth();
                        isArrangeValid = false;
                    }
                }
            }

            if (!bottomStack.getIsEmpty()) {
                double lastTopPoint = plotAreaRect.getBottom();
                for (AxisModel axis : bottomStack.axes) {
                    finalRect = new RadRect(plotAreaRect.getX(), lastTopPoint, plotAreaRect.getWidth(), axis.getDesiredSize().getHeight());
                    lastTopPoint = finalRect.getBottom();
                    lastAxisDesiredSize = axis.getDesiredSize();
                    axis.arrange(finalRect);
                    if (axis.getDesiredSize().getHeight() != finalRect.getHeight()) {
                        bottomStack.desiredHeight += axis.getDesiredSize().getHeight() - lastAxisDesiredSize.getHeight();
                        isArrangeValid = false;
                    }
                }
            }

            if (!isArrangeValid) {
                plotAreaRect = this.calculatePlotAreaRect(rect, leftStack, topStack, rightStack, bottomStack);
            }
            currentIteration++;
        }
        while (!isArrangeValid);
        return plotAreaRect;
    }

    private AxisStack[] prepareAxesStacks(final RadSize availableSize) {
        // horizontal stacks
        AxisStack leftStack;
        AxisStack rightStack;
        ArrayList<AxisModel> leftAxes = new ArrayList<AxisModel>();
        ArrayList<AxisModel> rightAxes = new ArrayList<AxisModel>();
        for (AxisModel axis : this.secondAxes) {
            if (axis.getHorizontalLocation() == AxisHorizontalLocation.LEFT) {
                leftAxes.add(axis);
            } else {
                rightAxes.add(axis);
            }
        }

        leftStack = new AxisStack(leftAxes);
        rightStack = new AxisStack(rightAxes);

        // vertical stacks
        AxisStack topStack;
        AxisStack bottomStack;
        ArrayList<AxisModel> topAxes = new ArrayList<AxisModel>();
        ArrayList<AxisModel> bottomAxes = new ArrayList<AxisModel>();
        for (AxisModel axis : this.firstAxes) {
            if (axis.getVerticalLocation() == AxisVerticalLocation.BOTTOM) {
                bottomAxes.add(axis);
            } else {
                topAxes.add(axis);
            }
        }
        bottomStack = new AxisStack(bottomAxes);
        topStack = new AxisStack(topAxes);

        leftStack.measure(availableSize);
        topStack.measure(availableSize);
        rightStack.measure(availableSize);
        bottomStack.measure(availableSize);

        return new AxisStack[]{leftStack, topStack, rightStack, bottomStack};
    }

    private RadRect calculatePlotAreaRect(final RadRect availableRect, AxisStack leftStack, AxisStack topStack,
                                          AxisStack rightStack, AxisStack bottomStack) {

        double finalLeftRectWidth = leftStack.desiredWidth + leftStack.desiredMargin.left + leftStack.desiredMargin.right;
        double maxLeftMargin = Math.max(topStack.desiredMargin.left, bottomStack.desiredMargin.left);
        double x = Math.max(finalLeftRectWidth, maxLeftMargin) + availableRect.getX();

        double finalTopRectHeight = topStack.desiredHeight + topStack.desiredMargin.top + topStack.desiredMargin.bottom;
        double maxTopMargin = Math.max(leftStack.desiredMargin.top, rightStack.desiredMargin.top);
        double y = Math.max(finalTopRectHeight, maxTopMargin) + availableRect.getY();

        RadPoint topLeft = new RadPoint(x, y);

        double finalRightRectWidth = rightStack.desiredWidth + rightStack.desiredMargin.left + rightStack.desiredMargin.right;
        double maxRightMargin = Math.max(topStack.desiredMargin.right, bottomStack.desiredMargin.right);
        x = availableRect.getWidth() - Math.max(finalRightRectWidth, maxRightMargin) + availableRect.getX();

        double finalBottomRectHeight = bottomStack.desiredHeight + bottomStack.desiredMargin.top + bottomStack.desiredMargin.bottom;
        double maxBottomMargin = Math.max(leftStack.desiredMargin.bottom, rightStack.desiredMargin.bottom);
        y = availableRect.getHeight() - Math.max(finalBottomRectHeight, maxBottomMargin) + availableRect.getY();

        RadPoint bottomRight = new RadPoint(x, y);

        RadRect plotAreaRect = new RadRect(topLeft, bottomRight);
        return RadRect.round(plotAreaRect);
    }

    private class AxisStack {
        RadThickness desiredMargin = RadThickness.getEmpty();
        double desiredWidth = Double.NaN, desiredHeight = Double.NaN;
        List<AxisModel> axes;

        public AxisStack(List<AxisModel> axes) {
            this.axes = axes;
        }

        public boolean getIsEmpty() {
            return this.axes.size() == 0;
        }

        public void measure(final RadSize availableSize) {
            this.desiredWidth = 0.0;
            this.desiredHeight = 0.0;
            for (AxisModel axis : this.axes) {
                axis.measure(availableSize);
                this.desiredWidth = this.desiredWidth + axis.getDesiredSize().getWidth();
                this.desiredHeight = this.desiredHeight + axis.getDesiredSize().getHeight();

                this.desiredMargin.left = Math.max(this.desiredMargin.left, axis.desiredMargin().left);
                this.desiredMargin.top = Math.max(this.desiredMargin.top, axis.desiredMargin().top);
                this.desiredMargin.right = Math.max(this.desiredMargin.right, axis.desiredMargin().right);
                this.desiredMargin.bottom = Math.max(this.desiredMargin.bottom, axis.desiredMargin().bottom);
            }
        }
    }
}

