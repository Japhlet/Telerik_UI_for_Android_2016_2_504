//package com.telerik.widget.chart.engine.axes.categorical;
//
//import com.telerik.android.common.math.RadMath;
//import com.telerik.widget.chart.engine.axes.AxisTickModel;
//import com.telerik.widget.chart.engine.axes.MajorTickModel;
//import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
//import com.telerik.widget.chart.engine.axes.common.RadialAxis;
//import com.telerik.widget.chart.engine.axes.common.layout.AxisModelLayoutStrategy;
//import com.telerik.widget.chart.engine.axes.common.layout.RadialAxisLayoutStrategy;
//import com.telerik.widget.chart.engine.axes.continuous.ValueRange;
//import com.telerik.widget.chart.engine.series.ChartSeriesModel;
//
//import java.util.ArrayList;
//
///**
// * Contains the layout and logic of the categorical radial axis in RadChartView.
// */
//public class CategoricalRadialAxisModel extends CategoricalAxisModel implements RadialAxis {
//    RadialAxisLayoutStrategy radialLayoutStrategy;
//
//    /**
//     * Creates a new instance of the {@link CategoricalRadialAxisModel} class.
//     */
//    public CategoricalRadialAxisModel() {
//    }
//
//    @Override
//    public boolean getIsLargeArc() {
//        return this.categories.size() <= 1;
//    }
//
//    @Override
//    void updateActualPlotMode(Iterable<ChartSeriesModel> seriesModels) {
//        this.actualPlotMode = AxisPlotMode.ON_TICKS;
//    }
//
//    @Override
//    protected Iterable<AxisTickModel> generateTicks(final ValueRange<Double> range) {
//        ArrayList<AxisTickModel> axis = new ArrayList<AxisTickModel>();
//        int categoryCount = this.categories.size();
//        if (categoryCount == 0) {
//            return axis;
//        }
//
//        int tickInterval = this.calculateMajorTickInterval();
//        int emptyTickCount = 0;
//
//        double tickStep = categoryCount == 1 ? 1.0 : 1.0 / categoryCount;
//        double normalizedTickStep = tickStep * 360;
//
//        double startTick = 0.0;
//        double endTick = 1.0 - tickStep;
//        double currentTick = startTick;
//        double value = 0.0;
//
//        // TODO startTick/tickStep might not be the right approach here!!!
//        int virtualIndex = (int) (startTick / tickStep);
//        while (currentTick < endTick || RadMath.areClose(currentTick, endTick)) {
//            if (emptyTickCount == 0) {
//                AxisTickModel tick = new MajorTickModel(value, currentTick, virtualIndex);
//
//                emptyTickCount = tickInterval - 1;
//
//                axis.add(tick);
//            } else {
//                emptyTickCount--;
//            }
//
//            currentTick = currentTick + tickStep;
//            value = value + normalizedTickStep;
//            virtualIndex++;
//        }
//        return axis;
//    }
//
//    @Override
//    protected Object getLabelContentCore(AxisTickModel tick) {
//        if (tick.virtualIndex() < this.categories.size()) {
//            return this.categories.get(tick.virtualIndex()).key;
//        }
//
//        return null;
//    }
//
//    @Override
//    protected AxisModelLayoutStrategy createLayoutStrategy() {
//        this.radialLayoutStrategy = new RadialAxisLayoutStrategy(this);
//        return this.radialLayoutStrategy;
//    }
//
//    @Override
//    public Object convertPhysicalUnitsToData(double coordinate) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    protected double calculateRelativeStep(int count) {
//        return 1.0 / count;
//    }
//}
