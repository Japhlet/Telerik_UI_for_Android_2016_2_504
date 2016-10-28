//package com.telerik.widget.chart.engine.decorations.annotations.line;
//
//import com.telerik.android.common.math.RadCircle;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
//
///**
// * Line annotation for the RadPolarChartView.
// */
//public class PolarGridLineAnnotationModel extends GridLineAnnotationModel {
//
//    /**
//     * Creates a new instance of the {@link PolarGridLineAnnotationModel} class.
//     */
//    public PolarGridLineAnnotationModel() {
//    }
//
//    @Override
//    protected RadRect arrangeCore(final RadRect rect) {
//        double radius = rect.width / 2;
//        RadPoint center = rect.getCenter();
//        NumericalAxisPlotInfo polarPlot = (NumericalAxisPlotInfo) this.plotInfo;
//        double pointRadius = polarPlot.normalizedValue * radius;
//
//        return new RadCircle(center, pointRadius).getBounds();
//    }
//}
//
