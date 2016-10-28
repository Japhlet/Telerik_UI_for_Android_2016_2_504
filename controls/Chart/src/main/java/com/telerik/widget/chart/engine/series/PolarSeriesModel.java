//package com.telerik.widget.chart.engine.series;
//
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.widget.chart.engine.dataPoints.PolarDataPoint;
//import com.telerik.widget.chart.engine.elementTree.ChartNode;
//import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
//
///**
// * Calculates layout information for all polar series.
// */
//public class PolarSeriesModel extends SeriesModelWithAxes<PolarDataPoint> {
//
//    /**
//     * Creates a new instance of the {@link PolarSeriesModel} class.
//     */
//    public PolarSeriesModel() {
//    }
//
//    @Override
//    public ModifyChildrenResult canAddChild(ChartNode child) {
//        if (child instanceof PolarDataPoint) {
//            return ModifyChildrenResult.ACCEPT;
//        }
//
//        return super.canAddChild(child);
//    }
//
//    @Override
//    protected RadRect arrangeOverride(final RadRect rect) {
//        double radius = rect.width / 2;
//        RadPoint arcPosition;
//        RadPoint center = rect.getCenter();
//
//        for (PolarDataPoint point : this.visibleDataPoints()) {
//            if (point.valuePlot() == null || point.getAnglePlot() == null) {
//                continue;
//            }
//
//            double pointRadius = point.valuePlot().normalizedValue * radius;
//            double angle = point.getAnglePlot().convertToAngle();
//            arcPosition = RadMath.getArcPoint(angle, center, pointRadius);
//
//            point.arrange(new RadRect(arcPosition.x, arcPosition.y,
//                    0, 0), false);
//        }
//
//        return rect.clone();
//    }
//}
//
