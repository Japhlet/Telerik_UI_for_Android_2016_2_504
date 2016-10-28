//package com.telerik.widget.chart.engine.decorations.annotations.plotBand;
//
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadPolarVector;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
//import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
//import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
//import com.telerik.widget.chart.engine.chartAreas.PolarChartAreaModel;
//
///**
// * Represents a {@link PlotBandAnnotationModel} that displays an annotation for radial charts.
// */
//public class RadialPlotBandAnnotationModel extends PlotBandAnnotationModel {
//
//    /**
//     * Creates a new instance of the {@link RadialPlotBandAnnotationModel} class.
//     */
//    public RadialPlotBandAnnotationModel() {
//    }
//
//    @Override
//    protected RadRect arrangeCore(final RadRect rect) {
//        double radius = rect.width / 2;
//        RadPoint center = rect.getCenter();
//
//        AxisPlotInfo anglePlot1 = this.firstPlotInfo;
//        double angle1 = 0;
//        if (anglePlot1 instanceof NumericalAxisPlotInfo) {
//            angle1 = ((NumericalAxisPlotInfo) anglePlot1).convertToAngle();
//        } else if (anglePlot1 instanceof CategoricalAxisPlotInfo) {
//            angle1 = ((CategoricalAxisPlotInfo) anglePlot1).convertToAngle((PolarChartAreaModel) this.chartArea);
//        }
//
//        RadPoint arcPoint1 = RadMath.getArcPoint(angle1, center, radius);
//        RadPolarVector polarVector1 = new RadPolarVector();
//        polarVector1.pointX = arcPoint1.x;
//        polarVector1.pointY = arcPoint1.y;
//        polarVector1.angle = angle1;
//        polarVector1.centerX = center.x;
//        polarVector1.centerY = center.y;
//
//        AxisPlotInfo anglePlot2 = this.secondPlotInfo;
//        double angle2 = 0;
//        if (anglePlot2 instanceof NumericalAxisPlotInfo) {
//            angle2 = ((NumericalAxisPlotInfo) anglePlot2).convertToAngle();
//        } else if (anglePlot2 instanceof CategoricalAxisPlotInfo) {
//            angle2 = ((CategoricalAxisPlotInfo) anglePlot2).convertToAngle((PolarChartAreaModel) this.chartArea);
//        }
//
//        RadPoint arcPoint2 = RadMath.getArcPoint(angle2, center, radius);
//        RadPolarVector polarVector2 = new RadPolarVector();
//        polarVector2.pointX = arcPoint2.x;
//        polarVector2.pointY = arcPoint2.y;
//        polarVector2.angle = angle2;
//        polarVector2.centerX = center.x;
//        polarVector2.centerY = center.y;
//
//        return rect.clone();
//    }
//}
//
