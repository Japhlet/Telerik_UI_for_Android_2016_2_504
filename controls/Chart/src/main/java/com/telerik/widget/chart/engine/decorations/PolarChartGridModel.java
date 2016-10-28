//package com.telerik.widget.chart.engine.decorations;
//
//import com.telerik.android.common.math.RadCircle;
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadPolarVector;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.widget.chart.engine.axes.AxisModel;
//import com.telerik.widget.chart.engine.axes.AxisTickModel;
//import com.telerik.widget.chart.engine.axes.continuous.PolarAxisModel;
//import com.telerik.widget.chart.engine.chartAreas.PolarChartAreaModel;
//
//import java.util.ArrayList;
//
//class PolarChartGridModel extends ChartGridModel {
//    ArrayList<RadCircle> radialLines;
//    ArrayList<RadPolarVector> polarLines;
//
//    public PolarChartGridModel() {
//        this.radialLines = new ArrayList<RadCircle>(4);
//        this.polarLines = new ArrayList<RadPolarVector>(16);
//    }
//
//    @Override
//    protected RadRect arrangeOverride(final RadRect rect) {
//        this.radialLines.clear();
//        this.polarLines.clear();
//
//        RadPoint center = RadPoint.round(rect.getCenter());
//        double radius = Math.max(0, rect.width / 2);
//
//        PolarChartAreaModel polarArea = (PolarChartAreaModel) this.chartArea;
//        PolarAxisModel polarAxis = polarArea.getPolarAxis();
//        for (AxisTickModel tick : polarAxis.getMajorTicks()) {
//            RadCircle circle = new RadCircle();
//            circle.centerX = center.x;
//            circle.centerY = center.y;
//            circle.radius = (int) ((tick.normalizedValue() * radius) + 0.5);
//            this.radialLines.add(circle);
//        }
//
//        AxisModel angleAxis = polarArea.getAngleAxis();
//        for (AxisTickModel tick : angleAxis.getMajorTicks()) {
//            // do not add a line for the last tick
//            if (tick.normalizedValue() == 1.0 || RadMath.isOne(tick.normalizedValue())) {
//                continue;
//            }
//
//            double angle = polarArea.normalizeAngle(tick.value());
//
//            RadPolarVector polarVector = new RadPolarVector();
//            polarVector.centerX = center.x;
//            polarVector.centerY = center.y;
//            polarVector.pointX = tick.getLayoutSlot().x;
//            polarVector.pointY = tick.getLayoutSlot().y;
//            polarVector.angle = angle;
//            this.polarLines.add(polarVector);
//        }
//
//        return rect.clone();
//    }
//}
//
