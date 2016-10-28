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
// * Layout algorithm for radial axes.
// */
//public class RadialAxisLayoutStrategy extends AxisModelLayoutStrategy {
//
//    RadThickness margins;
//
//    /**
//     * Creates a new instance of the {@link com.telerik.widget.chart.engine.axes.common.layout.RadialAxisLayoutStrategy} class.
//     *
//     * @param owner The owner of the layout algorithm.
//     */
//    public RadialAxisLayoutStrategy(AxisModel owner) {
//        super(owner);
//    }
//
//    @Override
//    public AxisLastLabelVisibility getDefaultLastLabelVisibility() {
//        return AxisLastLabelVisibility.CLIP;
//    }
//
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
//        PolarChartAreaModel polarArea = (PolarChartAreaModel) this.owner.chartArea();
//        RadPoint tickCenter = availableRect.getCenter();
//        double tickRadius = availableRect.width / 2;
//
//        for (AxisTickModel tick : this.owner.ticks()) {
//            if (!tick.isVisible()) {
//                continue;
//            }
//
//            double angle = polarArea.normalizeAngle(tick.value());
//            RadPoint tickPosition = RadMath.getArcPoint(angle, tickCenter, tickRadius);
//            tick.arrange(new RadRect(tickPosition.x, tickPosition.y, thickness, this.owner.getMajorTickLength()));
//
//            if (tick.associatedLabel() == null || !tick.associatedLabel().isVisible()) {
//                continue;
//            }
//
//            RadSize desiredSize = tick.associatedLabel().desiredSize.clone();
//            RadPoint labelPosition = RadMath.getArcPoint(angle, tickCenter, tickRadius);
//            labelPosition.x += desiredSize.width * Math.cos(angle * RadMath.DEG_TO_RAD_FACTOR) / 2;
//            labelPosition.y += desiredSize.height * Math.sin(angle * RadMath.DEG_TO_RAD_FACTOR) / 2;
//            tick.associatedLabel().arrange(new RadRect(labelPosition.x - (desiredSize.width / 2),
//                    labelPosition.y - (desiredSize.height / 2), desiredSize.width, desiredSize.height));
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
//        this.margins = new RadThickness();
//        this.updateLabels(availableSize);
//        this.margins.left = Math.max(this.margins.left, this.margins.right);
//        this.margins.right = Math.max(this.margins.left, this.margins.right);
//        this.margins.top = Math.max(this.margins.top, this.margins.bottom);
//        this.margins.bottom = Math.max(this.margins.top, this.margins.bottom);
//        return this.margins.clone();
//    }
//
//    @Override
//    public RadSize getDesiredSize(final RadSize availableSize) {
//        return new RadSize(availableSize.width, availableSize.height);
//    }
//
//    @Override
//    public void arrangeLabelMultiline(AxisLabelModel label, final RadRect rect) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public void arrangeLabelNone(AxisLabelModel label, final RadRect rect) {
//    }
//
//    private void updateLabels(final RadSize availableSize) {
//        RadRect availableRect = new RadRect(availableSize.width, availableSize.height);
//        RadRect ellipseRect = RadRect.toSquare(availableRect, false);
//        ellipseRect = RadRect.centerRect(ellipseRect, availableRect);
//        double radius = ellipseRect.width / 2;
//        RadPoint center = ellipseRect.getCenter();
//        RadPoint arcPosition;
//
//        for (AxisLabelModel label : this.owner.getLabels()) {
//            if (!label.isVisible()) {
//                continue;
//            }
//
//            double angle = label.normalizedPosition() * 360;
//            arcPosition = RadMath.getArcPoint(angle, center, radius);
//
//            this.updateMargins(ellipseRect, label.desiredSize, arcPosition);
//        }
//
//        double offset = this.owner.getLineThickness() + this.owner.getMajorTickLength();
//        this.margins.left += offset;
//        this.margins.top += offset;
//        this.margins.right += offset;
//        this.margins.bottom += offset;
//    }
//
//    private void updateMargins(final RadRect availableRect, final RadSize labelSize, final RadPoint arcPosition) {
//        double left = arcPosition.x - labelSize.width;
//        if (left < availableRect.x) {
//            this.margins.left = Math.max(this.margins.left, availableRect.x - left);
//        }
//
//        double top = arcPosition.y - labelSize.height;
//        if (top < availableRect.y) {
//            this.margins.top = Math.max(this.margins.top, availableRect.y - top);
//        }
//
//        double right = arcPosition.x + labelSize.width;
//        if (right > availableRect.getRight()) {
//            this.margins.right = Math.max(this.margins.right, right - availableRect.getRight());
//        }
//
//        double bottom = arcPosition.y + labelSize.height;
//        if (bottom > availableRect.getBottom()) {
//            this.margins.bottom = Math.max(this.margins.bottom, bottom - availableRect.getBottom());
//        }
//    }
//}
