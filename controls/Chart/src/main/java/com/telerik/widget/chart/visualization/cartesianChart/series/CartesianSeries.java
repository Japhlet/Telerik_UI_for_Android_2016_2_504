package com.telerik.widget.chart.visualization.cartesianChart.series;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.common.SeriesModelWithAxes;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.chart.visualization.common.PointTemplateSeries;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents {@link com.telerik.widget.chart.visualization.common.ChartSeries} that may be visualized by a {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView} instance.
 */
public abstract class CartesianSeries extends PointTemplateSeries {

    private CartesianAxis horizontalAxis;
    private CartesianAxis verticalAxis;

    private List<CartesianAxis> unattachedAxes;
    private SeriesModelWithAxes seriesModel;

    /**
     * Initializes a new instance of the {@link CartesianSeries} class.
     */
    protected CartesianSeries() {
        this.unattachedAxes = new ArrayList<CartesianAxis>();
    }

    /**
     * Gets the visual {@link CartesianAxis} instance that will be used to plot points along the horizontal (x) axis.
     *
     * @return The visual {@link CartesianAxis} instance that will be used to plot points along the horizontal (x) axis.
     */
    public CartesianAxis getHorizontalAxis() {
        return this.horizontalAxis;
    }

    /**
     * Sets the visual {@link CartesianAxis} instance that will be used to plot points along the horizontal (x) axis.
     *
     * @param value The visual {@link CartesianAxis} instance that will be used to plot points along the horizontal (x) axis.
     */
    public void setHorizontalAxis(CartesianAxis value) {
        CartesianAxis oldAxis = this.horizontalAxis;

        this.horizontalAxis = value;
        if (this.horizontalAxis != null) {
            this.horizontalAxis.setAxisType(AxisType.FIRST);
        }

        this.onAxisChanged(oldAxis, value);
    }

    /**
     * Gets the visual {@link CartesianAxis} instance that will be used to plot points along the vertical (y) axis.
     *
     * @return The visual {@link CartesianAxis} instance that will be used to plot points along the vertical (y) axis.
     */
    public CartesianAxis getVerticalAxis() {
        return this.verticalAxis;
    }

    /**
     * Sets the visual {@link CartesianAxis} instance that will be used to plot points along the vertical (y) axis.
     *
     * @param value The visual {@link CartesianAxis} instance that will be used to plot points along the vertical (y) axis.
     */
    public void setVerticalAxis(CartesianAxis value) {
        CartesianAxis oldAxis = this.verticalAxis;

        this.verticalAxis = value;
        if (this.verticalAxis != null) {
            this.verticalAxis.setAxisType(AxisType.SECOND);
        }

        this.onAxisChanged(oldAxis, value);
    }

    private SeriesModelWithAxes getSeriesModel() {
        if (this.seriesModel == null) {
            this.seriesModel = (SeriesModelWithAxes) this.model();
        }

        return this.seriesModel;
    }

    /**
     * Occurs when one of the axes of the owning {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView} has been changed.
     *
     * @param oldAxis The old axis.
     * @param newAxis The new axis.
     */
    public void chartAxisChanged(CartesianAxis oldAxis, CartesianAxis newAxis) {

        SeriesModelWithAxes seriesModel = this.getSeriesModel();

        if (this.horizontalAxis == null) {
            if (oldAxis != null && oldAxis.getAxisType() == AxisType.FIRST) {
                seriesModel.detachAxis(oldAxis.getModel());
            }
            if (newAxis != null && newAxis.getAxisType() == AxisType.FIRST) {
                seriesModel.attachAxis(newAxis.getModel(), AxisType.FIRST);
            }
        }
        if (this.verticalAxis == null) {
            if (oldAxis != null && oldAxis.getAxisType() == AxisType.SECOND) {
                seriesModel.detachAxis(oldAxis.getModel());
            }
            if (newAxis != null && newAxis.getAxisType() == AxisType.SECOND) {
                seriesModel.attachAxis(newAxis.getModel(), AxisType.SECOND);
            }
        }
    }

    @Override
    protected void onAttached() {
        for (CartesianAxis axis : this.unattachedAxes) {
            this.addAxisToChart(axis, this.getChart());
        }

        SeriesModelWithAxes seriesModel = this.getSeriesModel();
        this.unattachedAxes.clear();

        RadCartesianChartView cartesianChart = (RadCartesianChartView) this.getChart();
        if (this.horizontalAxis == null) {
            if (cartesianChart.getHorizontalAxis() != null) {
                seriesModel.attachAxis(cartesianChart.getHorizontalAxis().getModel(), AxisType.FIRST);
            }
        } else {
            seriesModel.attachAxis(this.horizontalAxis.getModel(), AxisType.FIRST);
        }

        if (this.verticalAxis == null) {
            if (cartesianChart.getVerticalAxis() != null) {
                seriesModel.attachAxis(cartesianChart.getVerticalAxis().getModel(), AxisType.SECOND);
            }
        } else {
            seriesModel.attachAxis(this.verticalAxis.getModel(), AxisType.SECOND);
        }

        super.onAttached();
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        SeriesModelWithAxes seriesModel = this.getSeriesModel();

        AxisModel axisToDetach = seriesModel.getFirstAxis();
        if (axisToDetach != null) {
            seriesModel.detachAxis(axisToDetach);
        }

        axisToDetach = seriesModel.getSecondAxis();
        if (axisToDetach != null) {
            seriesModel.detachAxis(axisToDetach);
        }

        if (this.horizontalAxis != null) {
            this.removeAxisFromChart(this.horizontalAxis, oldChart);
            this.unattachedAxes.add(this.horizontalAxis);
        }

        if (this.verticalAxis != null) {
            this.removeAxisFromChart(this.verticalAxis, oldChart);
            this.unattachedAxes.add(this.verticalAxis);
        }
    }

    private void onAxisChanged(CartesianAxis oldAxis, CartesianAxis newAxis) {
        if (oldAxis != null) {
            this.getSeriesModel().detachAxis(oldAxis.getModel());

            if (newAxis == null) {
                RadCartesianChartView cartesianChart = (RadCartesianChartView) this.getChart();
                if (cartesianChart != null) {
                    if (oldAxis.getAxisType() == AxisType.FIRST && cartesianChart.getHorizontalAxis() != null) {
                        this.getSeriesModel().attachAxis(cartesianChart.getHorizontalAxis().getModel(), AxisType.FIRST);
                    } else if (oldAxis.getAxisType() == AxisType.SECOND && cartesianChart.getVerticalAxis() != null) {
                        this.getSeriesModel().attachAxis(cartesianChart.getVerticalAxis().getModel(), AxisType.SECOND);
                    }
                }
            }

            this.removeAxisFromChart(oldAxis, this.getChart());
        }

        if (newAxis != null) {
            this.getSeriesModel().attachAxis(newAxis.getModel(), newAxis.getAxisType());
            this.addAxisToChart(newAxis, this.getChart());
        }
    }

    private void addAxisToChart(CartesianAxis axis, RadChartViewBase chart) {
        if (chart == null) {
            this.unattachedAxes.add(axis);
        } else {
            if (axis.getChart() == null) {
                chart.onPresenterAdded(axis);
            } else {
                chart.chartAreaModel().invalidate(ChartAreaInvalidateFlags.ALL);
            }
            axis.linkedSeriesCount++;
        }
    }

    private void removeAxisFromChart(CartesianAxis axis, RadChartViewBase chart) {
        if (chart == null) {
            this.unattachedAxes.remove(axis);
        } else {
            if (axis.linkedSeriesCount == 1) {
                chart.onPresenterRemoved(axis);
            }
            axis.linkedSeriesCount--;
        }
    }
}
