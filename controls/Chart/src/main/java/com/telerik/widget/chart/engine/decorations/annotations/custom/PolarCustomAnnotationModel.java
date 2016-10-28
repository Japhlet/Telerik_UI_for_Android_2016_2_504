//package com.telerik.widget.chart.engine.decorations.annotations.custom;
//
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
//import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
//import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
//import com.telerik.widget.chart.engine.chartAreas.PolarChartAreaModel;
//
///**
// * Custom annotation for RadPolarChartView.
// */
//public class PolarCustomAnnotationModel extends CustomAnnotationModel {
//
//    /**
//     * Creates a new instance of the {@link PolarCustomAnnotationModel} class.
//     */
//    public PolarCustomAnnotationModel() {
//    }
//
//    @Override
//    protected RadRect arrangeCore(final RadRect rect) {
//        double radius = rect.width / 2;
//        RadPoint center = rect.getCenter();
//        NumericalAxisPlotInfo polarPlot = (NumericalAxisPlotInfo) this.firstPlotInfo;
//        double pointRadius = polarPlot.normalizedValue * radius;
//        AxisPlotInfo anglePlot = this.secondPlotInfo;
//        double angle = 0;
//
//        if (anglePlot instanceof NumericalAxisPlotInfo) {
//            angle = ((NumericalAxisPlotInfo) anglePlot).convertToAngle();
//        } else if (anglePlot instanceof CategoricalAxisPlotInfo) {
//            angle = ((CategoricalAxisPlotInfo) anglePlot).convertToAngle((PolarChartAreaModel) this.chartArea);
//        }
//
//        RadPoint arcPosition = RadMath.getArcPoint(angle, center, pointRadius);
//
//        return new RadRect(arcPosition, arcPosition);
//    }
//}
