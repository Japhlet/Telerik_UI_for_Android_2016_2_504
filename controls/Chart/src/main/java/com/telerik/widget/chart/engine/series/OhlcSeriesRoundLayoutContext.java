//package com.telerik.widget.chart.engine.series;
//
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.widget.chart.engine.axes.AxisTickModel;
//import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisOhlcPlotInfo;
//import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
//
///**
// * This class contains layout information for the Ohlc series.
// */
//public class OhlcSeriesRoundLayoutContext {
//
//    /**
//     * Snaps the provided {@link com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint} to the Ohlc grid.
//     *
//     * @param point The point to snap.
//     */
//    public static void snapPointToGrid(OhlcDataPoint point) {
//        if (point.getNumericalPlot() == null)
//            return;
//
//        snapToHighGridLine(point);
//        snapToLowGridLine(point);
//
//        snapToOpenGridLine(point);
//        snapToCloseGridLine(point);
//    }
//
//    private static void snapToHighGridLine(OhlcDataPoint point) {
//        NumericalAxisOhlcPlotInfo numericalPlot = point.getNumericalPlot();
//
//        if (numericalPlot.getSnapTickIndex() == -1 ||
//                numericalPlot.getSnapTickIndex() >= numericalPlot.getAxis().ticks().size())
//            return;
//
//        AxisTickModel topTick = numericalPlot.getAxis().ticks().get(numericalPlot.getSnapTickIndex());
//        if (!RadMath.areClose(numericalPlot.normalizedHigh, topTick.normalizedValue()))
//            return;
//
//        RadRect tickRect = topTick.getLayoutSlot().clone();
//        double gridLine = tickRect.getY() + (int) (tickRect.getHeight() / 2);
//
//        double difference = point.getLayoutSlot().getY() - gridLine;
//        point.getLayoutSlot().y = (point.getLayoutSlot().getY() - difference);
//        point.getLayoutSlot().height = (point.getLayoutSlot().getHeight() + difference);
//
//        if (point.getLayoutSlot().getHeight() < 0) {
//            point.getLayoutSlot().height = 0;
//        }
//    }
//
//    private static void snapToLowGridLine(OhlcDataPoint point) {
//        NumericalAxisOhlcPlotInfo numericalPlot = point.getNumericalPlot();
//        if (numericalPlot.snapBaseTickIndex == -1 ||
//                numericalPlot.snapBaseTickIndex >= numericalPlot.getAxis().ticks().size())
//            return;
//
//        AxisTickModel baseTick = numericalPlot.getAxis().ticks().get(numericalPlot.snapBaseTickIndex);
//        if (!RadMath.areClose(numericalPlot.normalizedLow, baseTick.normalizedValue()))
//            return;
//
//        RadRect tickRect = baseTick.getLayoutSlot().clone();
//        double gridLine = tickRect.getY() + (int) (tickRect.getHeight() / 2);
//
//        double difference = gridLine - point.getLayoutSlot().getBottom();
//        point.getLayoutSlot().height = (point.getLayoutSlot().getHeight() + difference);
//        if (point.getLayoutSlot().getHeight() < 0) {
//            point.getLayoutSlot().height = 0;
//        }
//    }
//
//    private static void snapToOpenGridLine(OhlcDataPoint point) {
//        NumericalAxisOhlcPlotInfo numericalPlot = point.getNumericalPlot();
//        if (numericalPlot.snapOpenTickIndex == -1 ||
//                numericalPlot.snapOpenTickIndex >= numericalPlot.getAxis().ticks().size())
//            return;
//
//        AxisTickModel openTick = numericalPlot.getAxis().ticks().get(numericalPlot.snapOpenTickIndex);
//        if (!RadMath.areClose(numericalPlot.normalizedOpen, openTick.normalizedValue()))
//            return;
//
//        RadRect tickRect = openTick.getLayoutSlot().clone();
//        double gridLine = tickRect.getY() + (int) (tickRect.getHeight() / 2);
//        numericalPlot.physicalOpen = gridLine - point.getLayoutSlot().getY();
//    }
//
//    private static void snapToCloseGridLine(OhlcDataPoint point) {
//        NumericalAxisOhlcPlotInfo numericalPlot = point.getNumericalPlot();
//        if (numericalPlot.snapCloseTickIndex == -1 ||
//                numericalPlot.snapCloseTickIndex >= numericalPlot.getAxis().ticks().size())
//            return;
//
//        AxisTickModel closeTick = numericalPlot.getAxis().ticks().get(numericalPlot.snapCloseTickIndex);
//        if (!RadMath.areClose(numericalPlot.normalizedClose, closeTick.normalizedValue()))
//            return;
//
//        RadRect tickRect = closeTick.getLayoutSlot();
//        double gridLine = tickRect.getY() + (int) (tickRect.getHeight() / 2);
//        numericalPlot.physicalClose = gridLine - point.getLayoutSlot().getY();
//    }
//}
//
