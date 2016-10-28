//package com.telerik.widget.chart.engine.axes.common.layout;
//
//import com.telerik.android.common.RadThickness;
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.android.common.math.RadSize;
//import com.telerik.widget.chart.engine.axes.AxisLabelModel;
//import com.telerik.widget.chart.engine.axes.AxisModel;
//import com.telerik.widget.chart.engine.axes.AxisTickModel;
//import com.telerik.widget.chart.engine.axes.common.AxisLastLabelVisibility;
//import com.telerik.widget.chart.engine.axes.continuous.ValueRange;
//import com.telerik.widget.chart.engine.chartAreas.PolarChartAreaModel;
//
///**
// * Layout algorithm for the polar axes.
// */
//public class PolarAxisLayoutStrategy extends AxisModelLayoutStrategy {
//
//    /**
//     * Creates a new instance of the {@link com.telerik.widget.chart.engine.axes.common.layout.PolarAxisLayoutStrategy} class.
//     *
//     * @param owner The layout algorithm owner.
//     */
//    public PolarAxisLayoutStrategy(AxisModel owner) {
//        super(owner);
//    }
//
//    /**
//     * Gets the start angle.
//     *
//     * @return The start angle.
//     */
//    double getStartAngle() {
//        if (this.owner == null || this.owner.chartArea() == null) {
//            return 0.0;
//        }
//
//        return ((PolarChartAreaModel) this.owner.chartArea()).normalizeAngle(0.0);
//    }
//
//    @Override
//    public AxisLastLabelVisibility getDefaultLastLabelVisibility() {
//        return AxisLastLabelVisibility.VISIBLE;
//    }
//
//    @Override
//    public double getZoom() {
//        return 1.0;
//    }
//
//    @Override
//    public void applyLayoutRounding() {
//    }
//
//    @Override
//    public void updateTicksVisibility(final RadRect clipRect) {
//    }
//
//    @Override
//    public void arrange(final RadRect availableRect) {
//        double thickness = this.owner.getTickThickness();
//        double startAngle = this.getStartAngle();
//        double radius = availableRect.width / 2;
//        RadPoint center = availableRect.getCenter();
//
//        double length = this.owner.getMajorTickLength();
//        RadPoint labelAxisCenter = RadMath.getArcPoint(startAngle + 90, center, length * 2);
//
//        double angleInRad = (360 - startAngle) * RadMath.DEG_TO_RAD_FACTOR;
//        double sin = Math.sin(angleInRad);
//        double cos = Math.cos(angleInRad);
//
//        for (AxisTickModel tick : this.owner.ticks()) {
//            double tickRadius = tick.normalizedValue() * radius;
//            double tickLength = this.owner.getMajorTickLength();
//            RadPoint tickPosition = RadMath.getArcPoint(startAngle, center, tickRadius);
//            tick.arrange(new RadRect(tickPosition.x, tickPosition.y, thickness, tickLength));
//
//            if (tick.associatedLabel() == null) {
//                continue;
//            }
//
//            RadSize desiredSize = tick.associatedLabel().desiredSize.clone();
//            double halfWidth = desiredSize.width / 2;
//            double halfHeight = desiredSize.height / 2;
//
//            RadPoint labelPosition = RadMath.getArcPoint(startAngle, labelAxisCenter, tickRadius);
//            RadRect bounds = new RadRect(labelPosition.x - halfWidth, labelPosition.y - halfHeight, desiredSize.width, desiredSize.height);
//
//            bounds.x = bounds.x + sin * halfWidth;
//            bounds.y = bounds.y + cos * halfHeight;
//
//            tick.associatedLabel().arrange(bounds);
//        }
//    }
//
//    @Override
//    public ValueRange<Double> getVisibleRange(final RadSize availableSize) {
//        return new ValueRange<Double>(0.0, 1.0);
//    }
//
//    @Override
//    public RadThickness getDesiredMargin(final RadSize availableSize) {
//        return RadThickness.getEmpty();
//    }
//
//    @Override
//    public RadSize getDesiredSize(final RadSize availableSize) {
//        return RadSize.getEmpty();
//    }
//
//    @Override
//    public void arrangeLabelMultiline(AxisLabelModel label, final RadRect rect) {
//    }
//
//    @Override
//    public void arrangeLabelNone(AxisLabelModel label, final RadRect rect) {
//    }
//}
//
