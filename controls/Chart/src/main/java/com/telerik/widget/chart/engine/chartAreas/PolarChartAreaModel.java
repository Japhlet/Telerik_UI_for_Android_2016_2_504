//package com.telerik.widget.chart.engine.chartAreas;
//
//import com.telerik.android.common.DataTuple;
//import com.telerik.android.common.RadThickness;
//import com.telerik.android.common.math.RadMath;
//import com.telerik.android.common.math.RadPoint;
//import com.telerik.android.common.math.RadPolarCoordinates;
//import com.telerik.android.common.math.RadRect;
//import com.telerik.android.common.math.RadSize;
//import com.telerik.widget.chart.engine.axes.AxisModel;
//import com.telerik.widget.chart.engine.axes.AxisType;
//import com.telerik.widget.chart.engine.axes.categorical.AxisCategory;
//import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel;
//import com.telerik.widget.chart.engine.axes.categorical.CategoricalRadialAxisModel;
//import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
//import com.telerik.widget.chart.engine.axes.common.RadialAxis;
//import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisModel;
//import com.telerik.widget.chart.engine.axes.continuous.PolarAxisModel;
//import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
//import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
//import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
//import com.telerik.widget.chart.engine.series.ChartSeriesModel;
//
//import java.util.ArrayList;
//
///**
// * Represents a {@link ChartAreaModelWithAxes}
// */
//public class PolarChartAreaModel extends ChartAreaModelWithAxes {
//
//    static final String NO_POLAR_AXIS_KEY = "NoPolarAxis";
//    static final String NO_ANGLE_AXIS_KEY = "NoAngleAxis";
//
//    static int RotateRadialLabelsPropertyKey = PropertyKeys.register(PolarChartAreaModel.class, "RotateRadialLabels", ChartAreaInvalidateFlags.ALL);
//    static int StartAnglePropertyKey = PropertyKeys.register(PolarChartAreaModel.class, "StartAngle", ChartAreaInvalidateFlags.ALL);
//
//    private boolean rotateLabels = false;
//    private double startAngle;
//
//
//    /**
//     * Initializes a new instance of the {@link PolarChartAreaModel} class.
//     */
//    public PolarChartAreaModel() {
//        this.trackPropertyChanged = true;
//    }
//
//    /**
//     * Gets the angle, measured counter-clockwise at which the PolarAxis is anchored.
//     *
//     * @return the angle.
//     */
//    public double getStartAngle() {
//        return this.startAngle;
//    }
//
//    /**
//     * Sets the angle, measured counter-clockwise at which the PolarAxis is anchored.
//     *
//     * @param value the new angle.
//     */
//    public void setStartAngle(double value) {
//        this.setValue(StartAnglePropertyKey, value);
//    }
//
//    /**
//     * Gets the radius axis of the polar coordinate system.
//     *
//     * @return the radius axis.
//     * @see PolarAxisModel
//     */
//    public PolarAxisModel getPolarAxis() {
//        return (PolarAxisModel) this.primaryFirstAxis;
//    }
//
//    /**
//     * Sets the radius axis of the polar coordinate system.
//     *
//     * @param value the new radius axis.
//     * @see PolarAxisModel
//     */
//    public void setPolarAxis(PolarAxisModel value) {
//        if (this.primaryFirstAxis == value) {
//            return;
//        }
//
//        if (this.primaryFirstAxis != null) {
//            this.detachAxis(this.primaryFirstAxis);
//        }
//
//        this.primaryFirstAxis = value;
//
//        if (this.primaryFirstAxis != null) {
//            this.primaryFirstAxis.setType(AxisType.FIRST);
//            this.attachAxis(this.primaryFirstAxis);
//        }
//    }
//
//    /**
//     * Gets the angle axis of the polar coordinate system.
//     *
//     * @return the angle axis.
//     */
//    public AxisModel getAngleAxis() {
//        return this.primarySecondAxis;
//    }
//
//    /**
//     * Sets the angle axis of the polar coordinate system.
//     *
//     * @param value the new angle axis.
//     */
//    public void setAngleAxis(AxisModel value) {
//        if (this.primarySecondAxis == value) {
//            return;
//        }
//
//        if (this.primarySecondAxis != null) {
//            this.detachAxis(this.primarySecondAxis);
//        }
//
//        this.primarySecondAxis = value;
//
//        if (this.primarySecondAxis != null) {
//            this.primarySecondAxis.setType(AxisType.SECOND);
//            this.attachAxis(this.primarySecondAxis);
//        }
//    }
//
//    /**
//     * Normalizes the specified angle so that it reflects the counter-clockwise plot direction
//     * as well as the starting angle of the polar axis.
//     *
//     * @param angle angle to be used in the normalization.
//     * @return the normalized angle.
//     */
//    public double normalizeAngle(double angle) {
//        // normalization uses the following formula: 360 - StartAngle - angle
//        // rendering uses clockwise direction, we want to work in counter-clockwise direction, that is why we subtract the angle from 360
//        double normalizedAngle = 360 - this.startAngle - angle;
//        normalizedAngle = normalizedAngle % 360;
//
//        if (normalizedAngle < 0) {
//            normalizedAngle += 360;
//        }
//
//        return normalizedAngle;
//    }
//
//    @Override
//    public void applyLayoutRounding() {
//        this.primaryFirstAxis.applyLayoutRounding();
//
//        // ask each series to apply layout rounding
//        for (ChartSeriesModel series : this.getPlotArea().series) {
//            series.applyLayoutRounding();
//        }
//    }
//
//    @Override
//    protected void onPropertyChanged(RadPropertyEventArgs e) {
//        if (e.getKey() == RotateRadialLabelsPropertyKey) {
//            this.rotateLabels = (Boolean) e.newValue();
//        } else if (e.getKey() == StartAnglePropertyKey) {
//            this.startAngle = ((Number) e.newValue()).doubleValue();
//        }
//
//        super.onPropertyChanged(e);
//    }
//
//
//    @Override
//    public Iterable<String> getNotLoadedReasons() {
//        ArrayList<String> reasons = new ArrayList<String>();
//
//        if (this.primaryFirstAxis == null) {
//            reasons.add(NO_POLAR_AXIS_KEY);
//        }
//
//        if (this.primarySecondAxis == null) {
//            reasons.add(NO_ANGLE_AXIS_KEY);
//        }
//
//        return reasons;
//    }
//
//    @Override
//    protected RadRect arrangeOverride(final RadRect rect) {
//        return super.arrangeOverride(new RadRect(rect.x, rect.y,
//                rect.width * this.getView().getZoomWidth(), rect.height * this.getView().getZoomHeight()));
//    }
//
//    @Override
//    DataTuple convertPointToData(final RadPoint coordinates, AxisModel firstAxis, AxisModel secondAxis) {
//        if (this.chartArea == null) {
//            return super.convertPointToData(coordinates, firstAxis, secondAxis);
//        }
//
//        RadRect plotAreaRect = this.chartArea.getPlotArea().getLayoutSlot().clone();
//        RadPoint center = plotAreaRect.getCenter();
//        double radius = plotAreaRect.width / 2;
//
//        RadPolarCoordinates polarCoordinates = RadMath.getPolarCoordinates(coordinates, center);
//        double distance = polarCoordinates.radius;
//        double angle = polarCoordinates.angle;
//
//        Object polarValue = null;
//        if (firstAxis != null && firstAxis.isUpdated()) {
//            NumericalAxisModel polarAxis = (NumericalAxisModel) firstAxis;
//            double polarPosition = distance / radius;
//            double delta = polarAxis.getActualRange().getMaximum() - polarAxis.getActualRange().getMinimum();
//            polarValue = (polarPosition * delta) + polarAxis.getActualRange().getMinimum();
//        }
//
//        Object radialValue = angle;
//        if (secondAxis instanceof CategoricalAxisModel && secondAxis.isUpdated()) {
//            CategoricalAxisModel radarAxis = (CategoricalRadialAxisModel) secondAxis;
//            int categoriesCount = radarAxis.categories.size();
//            if (categoriesCount != 0) {
//                double position = angle / 360;
//                double step = 1.0 / categoriesCount;
//
//                if (radarAxis.getActualPlotMode() == AxisPlotMode.ON_TICKS) {
//                    position += 0.5 * step;
//                }
//
//                int categoryIndex = (int) (position / step);
//                AxisCategory category = radarAxis.categories.get(categoryIndex % categoriesCount);
//                radialValue = category.keySource;
//            }
//        }
//
//        return new DataTuple(polarValue, radialValue);
//    }
//
//    @Override
//    protected RadRect arrangeAxes(final RadRect rect) {
//
//        RadialAxis angleAxis = (RadialAxis) this.primarySecondAxis;
//        if (angleAxis == null) {
//            throw new IllegalStateException("AngleAxis of a polar widget area must be radial.");
//        }
//
//        RadRect ellipseRect = RadRect.toSquare(rect, false);
//        ellipseRect = RadRect.centerRect(ellipseRect, rect);
//        RadSize ellipseSize = new RadSize(ellipseRect.width, ellipseRect.height);
//
//        // measure the second (radial) axis first; it will inflate the plot area
//        this.primarySecondAxis.measure(ellipseSize);
//        this.primaryFirstAxis.measure(ellipseSize);
//
//        RadRect remaining = ellipseRect.clone();
//        RadThickness margins = this.primarySecondAxis.desiredMargin();
//        remaining = RadRect.inflate(remaining, margins);
//        remaining = RadRect.toSquare(remaining, false);
//        remaining = RadRect.centerRect(remaining, ellipseRect);
//
//        this.primarySecondAxis.arrange(remaining);
//        this.primaryFirstAxis.arrange(remaining);
//
//        return remaining;
//    }
//}
