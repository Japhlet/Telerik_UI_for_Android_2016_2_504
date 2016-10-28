package com.telerik.widget.chart.visualization.cartesianChart.axes;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.chartAreas.CartesianChartAreaModel;
import com.telerik.widget.chart.engine.chartAreas.ChartPlotAreaModel;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.common.CartesianAxis;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a categorical axis on a cartesian chart and is used
 * as a base for all types of categorical axes. This class should not be used directly in your chart.
 */
public class CategoricalAxis extends CartesianAxis {
    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.axes.CategoricalAxis}
     * class with a specified context, set of styleable attributes and default style ID.
     */
    public CategoricalAxis() {
    }

    /**
     * Gets the interval between two visible ticks. The interval is an integer value that represents the
     * amount of ticks skipped between two visible ticks on the axis.
     *
     * @return the amount of hidden ticks between to visible ticks.
     */
    public int getMajorTickInterval() {
        return ((CategoricalAxisModel) this.getModel()).getMajorTickInterval();
    }

    /**
     * Sets the interval between two visible ticks. The interval is an integer value that represents the
     * amount of ticks skipped between two visible ticks on the axis.
     *
     * @param value the amount of hidden ticks between to visible ticks.
     */
    public void setMajorTickInterval(int value) {
        ((CategoricalAxisModel) this.getModel()).setMajorTickInterval(value);
    }


    /**
     * Gets a value from the {@link AxisPlotMode} enum which determines in what way the axis will
     * be plotted on the viewport of the Chart.
     *
     * @return the current {@link com.telerik.widget.chart.engine.axes.common.AxisPlotMode} value.
     */
    public AxisPlotMode getPlotMode() {
        return ((CategoricalAxisModel) this.getModel()).getPlotMode();
    }

    /**
     * Sets a value from the {@link AxisPlotMode} enum which determines in what way the axis will
     * be plotted on the viewport of the Chart.
     *
     * @param value the current {@link com.telerik.widget.chart.engine.axes.common.AxisPlotMode} value.
     */
    public void setPlotMode(AxisPlotMode value) {
        ((CategoricalAxisModel) this.getModel()).setPlotMode(value);
    }

    /**
     * Gets the distance in pixels between a tick and the adjacent data point.
     *
     * @return the distance in pixels.
     */
    public float getGapLength() {
        return ((CategoricalAxisModel) this.getModel()).getGapLength();
    }

    /**
     * Sets the distance in pixels between a tick and the adjacent data point.
     *
     * @param value the distance in pixels.
     */
    public void setGapLength(float value) {
        if (value < 0 || value >= 1)
            throw new IllegalArgumentException("value must be in the range [0, 1)");

        ((CategoricalAxisModel) this.getModel()).setGapLength(value);
    }

    @Override
    public List<DataPoint> getDataPointsForValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null.");
        }

        ArrayList<DataPoint> result = new ArrayList<DataPoint>();

        ChartPlotAreaModel plotAreaModel = ((CartesianChartAreaModel) this.getModel().getParent()).getPlotArea();
        for (ChartSeriesModel series : plotAreaModel.getSeries()) {
            for (Object point : series.visibleDataPoints()) {
                if (!(point instanceof CategoricalDataPointBase)) {
                    continue;
                }

                CategoricalDataPointBase categoricalPoint = (CategoricalDataPointBase) point;
                if (!categoricalPoint.getCategory().equals(value)) {
                    continue;
                }

                result.add(categoricalPoint);
            }
        }

        return result;
    }

    @Override
    protected AxisModel createModel() {
        return new CategoricalAxisModel();
    }
}
