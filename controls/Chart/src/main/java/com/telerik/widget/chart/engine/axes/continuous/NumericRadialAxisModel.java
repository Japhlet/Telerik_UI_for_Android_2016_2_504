//package com.telerik.widget.chart.engine.axes.continuous;
//
//import com.telerik.widget.chart.engine.axes.AxisUpdateContext;
//import com.telerik.widget.chart.engine.axes.common.RadialAxis;
//import com.telerik.widget.chart.engine.axes.common.layout.AxisModelLayoutStrategy;
//import com.telerik.widget.chart.engine.axes.common.layout.RadialAxisLayoutStrategy;
//import com.telerik.widget.chart.engine.axes.AxisTickModel;
//import com.telerik.widget.chart.engine.axes.MajorTickModel;
//import com.telerik.widget.chart.engine.chartAreas.PolarChartAreaModel;
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadSize;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//
//public class NumericRadialAxisModel extends NumericalAxisModel implements RadialAxis {
//    RadialAxisLayoutStrategy radialLayoutStrategy;
//
//    @Override
//    public boolean getIsLargeArc() {
//        return this.majorStep > 180;
//    }
//
//    @Override
//    protected Iterable<AxisTickModel> generateTicks(final ValueRange<BigDecimal> visibleRange) {
//        ArrayList<AxisTickModel> ticks = new ArrayList<>();
//        if (this.majorStep <= 0 || this.actualRange.maximum.equals(this.actualRange.minimum)) {
//            return ticks;
//        }
//
//        BigDecimal tickStep = BigDecimal.valueOf(this.majorStep);
//        BigDecimal normalizedTickStep = tickStep.divide(BigDecimal.valueOf(360));
//
//        BigDecimal startTick = BigDecimal.ZERO;
//        BigDecimal endTick = BigDecimal.ONE;
//        BigDecimal currentTick = startTick;
//        BigDecimal value = BigDecimal.ZERO;
//
//        while (currentTick.compareTo(endTick) < 0 || RadMath.areClose(currentTick.doubleValue(), endTick.doubleValue())) {
//            AxisTickModel tick = new MajorTickModel(value, currentTick, -1);
//            currentTick = currentTick.add(normalizedTickStep);
//            value = value.add(tickStep);
//            ticks.add(tick);
//        }
//        return ticks;
//    }
//
//    @Override
//    void updateActualRange(AxisUpdateContext context) {
//        this.actualRange = new ValueRange<>((double) 0, (double) 360);
//
//        Object userStep = this.getValue(MAJOR_STEP_PROPERTY_KEY);
//        if (userStep != null) {
//            this.majorStep = (Double)userStep;
//        } else {
//            this.majorStep = (double) 30;
//        }
//    }
//
//    @Override
//    protected Object getLabelContentCore(AxisTickModel tick) {
//        double angle = tick.value().doubleValue();
//
//        String labelFormat = this.getLabelFormat();
//
//        if (labelFormat == null || labelFormat.equals("")) {
//            return angle;
//        }
//
//        return String.format(this.getLabelFormat(), angle);
//    }
//
//    protected AxisModelLayoutStrategy createLayoutStrategy() {
//        this.radialLayoutStrategy = new RadialAxisLayoutStrategy();
//        return this.radialLayoutStrategy;
//    }
//
//    @Override
//    double transformValue(double value) {
//        return ((PolarChartAreaModel) this.chartArea).normalizeAngle(value);
//    }
//
//    @Override
//    public Object convertPhysicalUnitsToData(double coordinate) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    protected boolean buildTicksAndLabels(final RadSize availableSize) {
//        boolean result = super.buildTicksAndLabels(availableSize);
//
//        // last tick and label should not be visible
//        AxisTickModel lastTick = this.getLastTick();
//        if (lastTick != null) {
//            lastTick.isVisible = false;
//            if (lastTick.associatedLabel() != null) {
//                lastTick.associatedLabel().isVisible = false;
//            }
//        }
//
//        return result;
//    }
//}
//
