package com.telerik.widget.chart.visualization.pieChart;

import android.content.Context;
import android.util.AttributeSet;

import com.telerik.android.common.ObservableCollection;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;
import com.telerik.widget.chart.visualization.behaviors.ChartBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartTrackBallBehavior;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.PresenterCollection;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.primitives.legend.LegendItem;

/**
 * Represents a ChartView that visualizes its data points in a shape similar to pie.
 * Each data point is represented as a slice in the pie.
 * <p/>
 * Depending on the desired visualization, the data points can be provided through
 * {@link PieSeries} or {@link DoughnutSeries}. The difference between the two series is the visual
 * representation. RadPieChartView with DoughnutSeries will have a "hole" in its center,
 * while RadPieChartView with PieSeries will be filled solidly.
 *
 * @see PieSeries
 * @see DoughnutSeries
 */
public class RadPieChartView extends RadChartViewBase<PieSeries> {
    private ObservableCollection<LegendItem> legendItems = new ObservableCollection<LegendItem>();

    /**
     * Initializes a new instance of the {@link RadPieChartView} class with passed
     * {@link Context} as argument.
     *
     * @param context context to be used
     */
    public RadPieChartView(Context context) {
        this(context, null);
    }

    /**
     * Initializes a new instance of the {@link RadPieChartView} class with passed
     * {@link Context} and {@link android.util.AttributeSet} as arguments.
     *
     * @param context context to be used
     * @param attrs   attributes to be used
     */
    public RadPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setSeries(new PresenterCollection<PieSeries>(this));
    }

    /**
     * Gets the legend info items for this instance of {@link RadPieChartView}
     *
     * @return an {@link com.telerik.android.common.ObservableCollection} of legend items
     */
    public ObservableCollection<LegendItem> getLegendInfos() {
        return this.legendItems;
    }

    @Override
    public void validateBehaviourSupport(ChartBehavior behavior) {
        if (behavior instanceof ChartPanAndZoomBehavior)
            throw new IllegalArgumentException("RadPieChartView does not support ChartPanAndZoomBehavior.");

        if(behavior instanceof ChartTrackBallBehavior) {
            throw new IllegalArgumentException("RadPieChartView does not support ChartTrackballBehavior.");
        }
    }

    @Override
    public ChartAreaModel createChartAreaModel() {
        return new ChartAreaModel();
    }
}

