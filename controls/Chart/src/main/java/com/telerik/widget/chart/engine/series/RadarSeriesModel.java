//package com.telerik.widget.chart.engine.series;
//
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
//import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
//import com.telerik.widget.chart.engine.chartAreas.PolarChartAreaModel;
//import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
//import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
//
///**
// * Calculates layout information for the radar series in RadChartView.
// */
//class RadarSeriesModel extends CategoricalSeriesModel {
//
//    /**
//     * Creates a new instance of the {@link RadarSeriesModel} class.
//     */
//    public RadarSeriesModel() {
//    }
//
//    @Override
//    AxisPlotMode getDefaultPlotMode() {
//        return AxisPlotMode.ON_TICKS;
//    }
//
//    @Override
//    protected RadRect arrangeOverride(final RadRect rect) {
//        double radius = rect.width / 2;
//        RadPoint arcPosition;
//        RadPoint center = rect.getCenter();
//
//        for (CategoricalDataPointBase point : this.visibleDataPoints()) {
//
//            if (point.numericalPlot == null || point.categoricalPlot == null) {
//                continue;
//            }
//
//            NumericalAxisPlotInfo numericalPlot = (NumericalAxisPlotInfo)point.numericalPlot;
//
//            double pointRadius = numericalPlot.normalizedValue * radius;
//            double angle = point.categoricalPlot.convertToAngle((PolarChartAreaModel) this.chartArea);
//            arcPosition = RadMath.getArcPoint(angle, center, pointRadius);
//
//            point.arrange(new RadRect(arcPosition.x, arcPosition.y, 0, 0), false);
//        }
//
//        return rect.clone();
//    }
//}
//
