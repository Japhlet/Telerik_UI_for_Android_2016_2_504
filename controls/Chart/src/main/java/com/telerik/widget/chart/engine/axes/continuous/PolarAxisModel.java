//package com.telerik.widget.chart.engine.axes.continuous;
//
//import com.telerik.widget.chart.engine.axes.AxisUpdateContext;
//import com.telerik.widget.chart.engine.axes.common.layout.AxisModelLayoutStrategy;
//import com.telerik.widget.chart.engine.axes.common.layout.PolarAxisLayoutStrategy;
//
///**
// * This class contains the core functionality of the polar axis.
// */
//public class PolarAxisModel extends LinearAxisModel {
//
//    /**
//     * Creates a new instance of the {@link PolarAxisModel} class.
//     */
//    public PolarAxisModel() {
//    }
//
//    @Override
//    int getDefaultTickCount() {
//        return 5;
//    }
//
//    @Override
//    protected AxisModelLayoutStrategy createLayoutStrategy() {
//        return new PolarAxisLayoutStrategy(this);
//    }
//
//    @Override
//    void updateActualRange(AxisUpdateContext context) {
//        super.updateActualRange(context);
//
//        // actual range always starts from zero
//        this.getActualRange().minimum = 0.0;
//    }
//
//    @Override
//    double transformValue(double value) {
//        // negative values are not defined in polar coordinates
//        if (value < 0) {
//            // take the absolute value
//            value *= -1;
//        }
//
//        return value;
//    }
//
//    @Override
//    public Object convertPhysicalUnitsToData(double coordinate) {
//        throw new UnsupportedOperationException();
//    }
//}
//
