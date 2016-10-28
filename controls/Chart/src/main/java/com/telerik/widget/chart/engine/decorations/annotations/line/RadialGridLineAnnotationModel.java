//package com.telerik.widget.chart.engine.decorations.annotations.line;
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
// * Grid line annotation for the radar chart.
// */
//public class RadialGridLineAnnotationModel extends GridLineAnnotationModel {
//
//    /**
//     * Creates a new instance of the {@link RadialGridLineAnnotationModel} class.
//     */
//    public RadialGridLineAnnotationModel() {
//    }
//
//    @Override
//    protected RadRect arrangeCore(final RadRect rect) {
//        double radius = rect.width / 2;
//        RadPoint center = rect.getCenter();
//        AxisPlotInfo anglePlot = this.plotInfo;
//        double angle = 0;
//
//        if (anglePlot instanceof NumericalAxisPlotInfo) {
//            angle = ((NumericalAxisPlotInfo) anglePlot).convertToAngle();
//        } else if (anglePlot instanceof CategoricalAxisPlotInfo) {
//            angle = ((CategoricalAxisPlotInfo) anglePlot).convertToAngle((PolarChartAreaModel) this.chartArea);
//        }
//
//        RadPoint arcPoint = RadMath.getArcPoint(angle, center, radius);
//        RadPolarVector radialLine = new RadPolarVector();
//        radialLine.pointX = arcPoint.x;
//        radialLine.pointY = arcPoint.y;
//        radialLine.angle = angle;
//        radialLine.centerX = center.x;
//        radialLine.centerY = center.y;
//
//        return new RadRect(center, arcPoint);
//    }
//}
//
