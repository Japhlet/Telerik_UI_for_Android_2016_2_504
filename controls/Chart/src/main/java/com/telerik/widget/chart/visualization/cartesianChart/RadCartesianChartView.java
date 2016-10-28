package com.telerik.widget.chart.visualization.cartesianChart;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;

import com.telerik.android.common.DataTuple;
import com.telerik.android.common.ObservableCollection;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.chartAreas.CartesianChartAreaModel;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;
import com.telerik.widget.chart.visualization.annotations.cartesian.CartesianChartAnnotation;
import com.telerik.widget.chart.visualization.cartesianChart.series.CartesianSeries;
import com.telerik.widget.chart.visualization.common.AnnotationCollection;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.chart.visualization.common.PresenterCollection;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.primitives.legend.LegendItem;

/**
 * Represents a {@link com.telerik.widget.chart.visualization.common.RadChartViewBase} instance that uses a Cartesian Coordinate System
 * to plot the associated data points.
 */
public class RadCartesianChartView extends RadChartViewBase<CartesianSeries> {

    private CartesianAxis horizontalAxis;
    private CartesianAxis verticalAxis;
    private CartesianChartGrid grid;
    private AnnotationCollection<CartesianChartAnnotation> annotations;
    private ObservableCollection<LegendItem> legendInfos = new ObservableCollection<>();

    /**
     * Creates an instance of the {@link RadCartesianChartView} class.
     *
     * @param context instance that will be holding the chart elements.
     * @param attrs   optional attributes.
     */
    public RadCartesianChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setSeries(new PresenterCollection<CartesianSeries>(null));
        this.annotations = new AnnotationCollection<>(this);
    }

    /**
     * Creates an instance of the {@link RadCartesianChartView} class.
     *
     * @param context instance that will be holding the chart elements.
     */
    public RadCartesianChartView(Context context) {
        this(context, null);
    }

    /**
     * Gets the current collection of legend infos.
     *
     * @return the current collection of legend infos.
     */
    public ObservableCollection<LegendItem> getLegendInfos() {
        return this.legendInfos;
    }

    /**
     * Gets the collection containing all the annotations presented by this instance.
     *
     * @return the annotations for this instance.
     */
    public PresenterCollection<CartesianChartAnnotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * Gets the {@link CartesianChartGrid} used to decorate the widget plot area with major/minor
     * grid and strip lines.
     *
     * @return the current grid.
     */
    public CartesianChartGrid getGrid() {
        return this.grid;
    }

    /**
     * Sets the {@link CartesianChartGrid} used to decorate the widget plot area with major/minor
     * grid and strip lines.
     *
     * @param value the new grid.
     */
    public void setGrid(CartesianChartGrid value) {
        if (this.grid != null) {
            this.onPresenterRemoved(this.grid);
        }

        this.grid = value;

        if (this.grid != null) {
            this.onPresenterAdded(this.grid);
        }
    }

    /**
     * Gets the visual {@link CartesianAxis} instance that will be used to plot points along the
     * horizontal (x) axis.
     *
     * @return the current horizontal axis.
     */
    public CartesianAxis getHorizontalAxis() {
        return this.horizontalAxis;
    }

    /**
     * Sets the visual {@link CartesianAxis} instance that will be used to plot points along the
     * horizontal (x) axis.
     *
     * @param value the new horizontal axis.
     */
    public void setHorizontalAxis(CartesianAxis value) {
        RadCartesianChartView chart = this;
        CartesianAxis oldAxis = this.horizontalAxis;

        chart.horizontalAxis = value;
        if (chart.horizontalAxis != null) {
            chart.horizontalAxis.setAxisType(AxisType.FIRST);
        }

        chart.onAxisChanged(oldAxis, value);
    }

    /**
     * Gets the visual {@link CartesianAxis} instance that will be used to plot points
     * along the vertical (y) axis.
     *
     * @return the current vertical axis.
     */
    public CartesianAxis getVerticalAxis() {
        return this.verticalAxis;
    }

    /**
     * Sets the visual {@link CartesianAxis} instance that will be used to plot points
     * along the vertical (y) axis.
     *
     * @param value the new vertical axis.
     */
    public void setVerticalAxis(CartesianAxis value) {
        RadCartesianChartView chart = this;
        CartesianAxis oldAxis = this.verticalAxis;

        chart.verticalAxis = value;
        if (chart.verticalAxis != null) {
            chart.verticalAxis.setAxisType(AxisType.SECOND);
        }

        chart.onAxisChanged(oldAxis, value);
    }

    @Override
    public RadRect getPlotAreaClip() {
        return this.chartAreaModel().getPlotArea().getLayoutSlot();
    }

    @Override
    public ChartAreaModel createChartAreaModel() {
        return new CartesianChartAreaModel();
    }

    /**
     * Converts the specified coordinates to data using the primary axes (if any).
     *
     * @param coordinates the coordinates to be converted.
     * @return the converted coordinates.
     */
    public DataTuple convertPointToData(Point coordinates) {
        return this.convertPointToData(coordinates, this.horizontalAxis, this.verticalAxis);
    }

    /**
     * Converts the specified coordinates to data using the specified axes.
     *
     * @param coordinates    the coordinates to be converted.
     * @param horizontalAxis the horizontal axis to be used in the conversion.
     * @param verticalAxis   the vertical axis to be used in the conversion.
     * @return The data objects that correspond to the provided point.
     */
    public DataTuple convertPointToData(Point coordinates, CartesianAxis horizontalAxis,
                                        CartesianAxis verticalAxis) {
        AxisModel firstAxis = null, secondAxis = null;

        if (horizontalAxis != null) {
            firstAxis = horizontalAxis.getModel();
        }
        if (verticalAxis != null) {
            secondAxis = verticalAxis.getModel();
        }

        RadPoint point = new RadPoint(coordinates.x, coordinates.y);
        return ((CartesianChartAreaModel) this.chartAreaModel()).convertPointToData(point, firstAxis,
                secondAxis);
    }

    /**
     * Handles the change of an axis.
     *
     * @param oldAxis the axis to be changed.
     * @param newAxis the axis to be changed with.
     */
    private void onAxisChanged(CartesianAxis oldAxis, CartesianAxis newAxis) {
        PresenterCollection<CartesianSeries> allSeries = this.getSeries();
        for (CartesianSeries series : allSeries) {
            series.chartAxisChanged(oldAxis, newAxis);
        }

        if (oldAxis != null) {
            oldAxis.getModel().setIsPrimary(false);
            this.onPresenterRemoved(oldAxis);
        }
        if (newAxis != null) {
            newAxis.getModel().setIsPrimary(true);
            this.onPresenterAdded(newAxis);
        }
    }
}
