package com.telerik.widget.chart.engine.axes.continuous;

import com.telerik.android.common.Function2;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.AxisUpdateContext;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.Ohlc;
import com.telerik.widget.chart.engine.series.combination.CombineGroup;
import com.telerik.widget.chart.engine.series.combination.CombineStack;
import com.telerik.widget.chart.engine.series.combination.CombinedSeries;
import com.telerik.widget.chart.engine.series.rangeSeries.Range;
import com.telerik.widget.chart.engine.view.ChartView;

/**
 * Base class for all numeric axis models.
 */
public abstract class NumericalAxisModel extends AxisModel implements ContinuousAxisModel {

    static final int MINIMUM_PROPERTY_KEY = PropertyKeys.register(NumericalAxisModel.class, "Minimum", ChartAreaInvalidateFlags.ALL);
    static final int MAXIMUM_PROPERTY_KEY = PropertyKeys.register(NumericalAxisModel.class, "Maximum", ChartAreaInvalidateFlags.ALL);
    static final int MAJOR_STEP_PROPERTY_KEY = PropertyKeys.register(NumericalAxisModel.class, "MajorStep", ChartAreaInvalidateFlags.ALL);
    static final int RANGE_EXTEND_DIRECTION_PROPERTY_KEY = PropertyKeys.register(NumericalAxisModel.class, "RangeExtendDirection", ChartAreaInvalidateFlags.ALL);
    static final int DESIRED_TICK_COUNT_PROPERTY_KEY = PropertyKeys.register(NumericalAxisModel.class, "DesiredTickCount", ChartAreaInvalidateFlags.ALL);
    private static final double DEFAULT_ORIGIN = 0;
    private static final double DEFAULT_MINIMUM = Double.NEGATIVE_INFINITY;
    private static final double DEFAULT_MAXIMUM = Double.POSITIVE_INFINITY;
    protected ValueRange<Double> actualRange = new ValueRange<Double>(-1.0, -1.0);
    public ValueRange<Double> pointMinMax;
    double majorStep = 0.0;
    double normalizedOrigin = 0.0;
    int extendDirection;
    int userTickCount = 0;
    private byte percentDecimalOffset;
    private boolean isStacked100 = false;

    /**
     * Creates a new instance of the {@link NumericalAxisModel} class.
     */
    protected NumericalAxisModel() {
        this.extendDirection = NumericalAxisRangeExtendDirection.BOTH | NumericalAxisRangeExtendDirection.NEGATIVE | NumericalAxisRangeExtendDirection.POSITIVE;
    }

    /**
     * Gets the number of the ticks available on the axis. If a value less than 2 is set, the property is reset to its default value.
     *
     * @return The number of the ticks available on the axis. If a value less than 2 is set, the property is reset to its default value.
     */
    public int getDesiredTickCount() {
        return this.userTickCount == 0 ? this.getDefaultTickCount() : this.userTickCount;
    }

    /**
     * Sets the number of the ticks available on the axis. If a value less than 2 is set, the property is reset to its default value.
     *
     * @param value The number of the ticks available on the axis. If a value less than 2 is set, the property is reset to its default value.
     */
    public void setDesiredTickCount(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("value cannot be less than 1.");
        } else if (value == 1) {
            this.clearValue(DESIRED_TICK_COUNT_PROPERTY_KEY);
        } else {
            this.setValue(DESIRED_TICK_COUNT_PROPERTY_KEY, value);
        }
    }

    /**
     * Gets a value that specifies how the auto-range of this axis will be extended so that each data point is visualized in the best possible way.
     *
     * @return A value that specifies how the auto-range of this axis will be extended so that each data point is visualized in the best possible way.
     */
    public int getRangeExtendDirection() {
        return this.extendDirection;
    }

    /**
     * Sets a value that specifies how the auto-range of this axis will be extended so that each data point is visualized in the best possible way.
     *
     * @param value A value that specifies how the auto-range of this axis will be extended so that each data point is visualized in the best possible way.
     */
    public void setRangeExtendDirection(int value) {
        this.setValue(RANGE_EXTEND_DIRECTION_PROPERTY_KEY, value);
    }

    /**
     * Gets the user-defined major step of the axis.
     *
     * @return The user-defined major step of the axis.
     */
    public double getMajorStep() {
        return this.getTypedValue(MAJOR_STEP_PROPERTY_KEY, this.majorStep);
    }

    /**
     * Sets a custom major step of the axis.
     * If the major step is set to <code>0.0</code>, the value is reset and the steps are generated automatically.
     *
     * @param value The user-defined major step of the axis
     */
    public void setMajorStep(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Major step may not be less than 0");
        }

        double defaultMajorStep = 0;
        if (value != defaultMajorStep) {
            this.setValue(MAJOR_STEP_PROPERTY_KEY, value);
        } else {
            this.clearValue(MAJOR_STEP_PROPERTY_KEY);
        }
    }

    /**
     * Gets the user-defined minimum of the axis.
     *
     * @return The user-defined minimum of the axis.
     */
    public double getMinimum() {
        return this.getTypedValue(MINIMUM_PROPERTY_KEY, DEFAULT_MINIMUM);
    }

    /**
     * Sets the user-defined minimum of the axis.
     *
     * @param value The user-defined minimum of the axis.
     */
    public void setMinimum(double value) {
        if (value != DEFAULT_MINIMUM) {
            if (value > getMaximum())
                throw new IllegalArgumentException("minimum for an axis cannot be greater than its current maximum");

            if (value == getMaximum())
                throw new IllegalArgumentException("minimum for an axis cannot be equal to its current maximum");

            this.setValue(MINIMUM_PROPERTY_KEY, value);
        } else {
            this.clearValue(MINIMUM_PROPERTY_KEY);
        }
    }

    /**
     * Gets the user-defined maximum of the axis.
     *
     * @return The user-defined maximum of the axis.
     */
    public double getMaximum() {
        return this.getTypedValue(MAXIMUM_PROPERTY_KEY, DEFAULT_MAXIMUM);
    }

    /**
     * Sets the user-defined maximum of the axis.
     *
     * @param value The user-defined maximum of the axis.
     */
    public void setMaximum(double value) {
        if (value != DEFAULT_MAXIMUM) {
            if (value < getMinimum())
                throw new IllegalArgumentException("maximum for an axis cannot be lesser than its current minimum");

            if (value == getMinimum())
                throw new IllegalArgumentException("maximum for an axis cannot be equal to its current minimum");

            this.setValue(MAXIMUM_PROPERTY_KEY, value);
        } else {
            this.clearValue(MAXIMUM_PROPERTY_KEY);
        }
    }

    /**
     * Gets the actual range (minimum and maximum values) used by the axis.
     *
     * @return The actual range (minimum and maximum values) used by the axis.
     */
    public ValueRange<Double> getActualRange() {
        return this.actualRange;
    }

    int getDefaultTickCount() {
        return 8;
    }

    @Override
    public void onZoomChanged() {
        super.onZoomChanged();

        if (this.isStacked100) {
            double zoom = this.getLayoutStrategy().getZoom();
            this.percentDecimalOffset = 0;
            double step = this.normalizeStep(this.majorStep / zoom) * 100;

            while (step < 1 && step > 0) {
                this.percentDecimalOffset++;
                step *= 10;
            }
        }
    }

    @Override
    public StackValue getStackValue(DataPoint point) {
        StackValue returnValue = new StackValue();

        double doubleValue = DEFAULT_ORIGIN;
        if (!point.isEmpty) {
            Object objectValue = point.getValueForAxis(this);
            if (!(objectValue instanceof Range)) {
                Double valueForAxis = ((Number) point.getValueForAxis(this)).doubleValue();
                if (valueForAxis != null) {
                    doubleValue = valueForAxis;
                }
            }
        }

        double value = this.transformValue(doubleValue);

        returnValue.value = value;
        returnValue.positive = value >= DEFAULT_ORIGIN;

        return returnValue;
    }

    @Override
    public AxisPlotInfo createPlotInfo(Object value) {
        double doubleValue = 0.0;
        if (value instanceof Double) {
            doubleValue = (Double) value;
        } else {
            boolean parsed = true;
            try {
                doubleValue = ((Number) value).doubleValue();
            } catch (NumberFormatException ex) {
                parsed = false;
            }

            if (value == null || !parsed) {
                return super.createPlotInfo(value);
            }
        }

        Object transformedValue = this.transformValue(doubleValue);
        double delta = this.actualRange.maximum - this.actualRange.minimum;
        return this.createAxisPlotInfo(delta, (Double)transformedValue);
    }

    @Override
    protected void updateCore(AxisUpdateContext context) {
        super.updateCore(context);

        this.updateActualRange(context);
        this.updatePlotOrigin(context);

        this.isStacked100 = context.isStacked100();
    }

    @Override
    protected void plotCore(AxisUpdateContext context) {
        if (context.isStacked()) {
            this.plotStacked(context);
        } else if (context.isStacked100()) {
            this.plotStacked100(context);
        } else if (context.series() != null) {
            this.plotNormal(context.series());
        }
    }

    /**
     * Gets the label content for the given tick.
     *
     * @param tick The {@link AxisTickModel} instance for which to get the label content.
     * @return The label content for the given tick.
     */
    protected Object getLabelContentCore(AxisTickModel tick) {
        String format = this.getLabelFormat();
        double tickValue = tick.value();
        if (format == null || format.equals("")) {
            String defaultNumericalLabelFormat = "%.2f";
            String defaultPercentLabelFormat = "%." + Byte.toString(this.percentDecimalOffset) + "f";

            if (this.isStacked100) {
                tickValue *= 100;
                format = defaultPercentLabelFormat + "%%";
            } else {
                format = defaultNumericalLabelFormat;
            }
        }
        return String.format(format, tickValue);
    }

    double calculateAutoStep(final ValueRange<Double> range) {
        double step = (range.maximum - range.minimum) / (this.getDesiredTickCount() - 1);
        return this.normalizeStep(step);
    }

    double normalizeStep(double initialStep) {
        double magnitude = Math.floor(Math.log10(initialStep));
        double magnitudePower = Math.pow(10.0, magnitude);

        // Calculate most significant digit of the new step size
        int magnitudeDigit = (int) ((initialStep / magnitudePower) + .5);

        if (magnitudeDigit > 5) {
            magnitudeDigit = 10;
        } else if (magnitudeDigit > 2) {
            magnitudeDigit = 5;
        } else if (magnitudeDigit > 1) {
            magnitudeDigit = 2;
        }

        return magnitudeDigit * magnitudePower;
    }

    Object transformValue(Object value) {
        if (value instanceof Number) {
            return this.transformValue(((Number)value).doubleValue());
        } else if (value instanceof Ohlc)
            return this.transformValue((Ohlc) value);
        else
            return value;
    }

    double transformValue(double value) {
        return value;
    }

    Ohlc transformValue(final Ohlc value) {
        return value;
    }

    double reverseTransformValue(double value) {
        return value;
    }

    void updateActualRange(AxisUpdateContext context) {
        this.pointMinMax = this.calculateRange(context);
        this.actualRange = this.pointMinMax;

        Object userMin = this.getValue(MINIMUM_PROPERTY_KEY);
        if (userMin != null) {
            this.actualRange.minimum = ((Number) this.transformValue(userMin)).doubleValue();
        }
        Object userMax = this.getValue(MAXIMUM_PROPERTY_KEY);
        if (userMax != null) {
            this.actualRange.maximum = ((Number) this.transformValue(userMax)).doubleValue();
        }

        this.actualRange.maximum = Math.max(this.actualRange.minimum, this.actualRange.maximum);

        RangeCalculator calculator = new RangeCalculator(this, userMin != null, userMax != null);
        if (!context.isStacked100()) {
            this.actualRange = calculator.extend();
        }

        Object userStep = this.getValue(MAJOR_STEP_PROPERTY_KEY);
        if (userStep != null) {
            // LogarithmicAxis.ExponentStep now specifies the actual exponent so we should not "transform" the step value here.
            this.majorStep = ((Number) userStep).doubleValue();
        } else {
            this.majorStep = this.calculateAutoStep(this.actualRange);
        }

        this.actualRange = calculator.roundToMajorStep(this.majorStep);
        if (this.userTickCount > 0 && userStep == null) {
            this.roundToUserTicks();
        }
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == RANGE_EXTEND_DIRECTION_PROPERTY_KEY) {
            this.extendDirection = (Integer) e.newValue();
        } else if (e.getKey() == DESIRED_TICK_COUNT_PROPERTY_KEY) {
            this.userTickCount = e.newValue() == null ? 0 : (Integer) e.newValue();
        }

        super.onPropertyChanged(e);
    }

    @Override
    public Object convertPhysicalUnitsToData(double coordinate) {
        if (!this.isUpdated()) {
            return super.convertPhysicalUnitsToData(coordinate);
        }

        RadRect plotArea = this.chartArea.getPlotArea().getLayoutSlot();
        ChartView view = this.chartArea.getView();
        RadRect plotAreaVirtualSize = new RadRect(plotArea.getX(), plotArea.getY(),
                plotArea.getWidth() * view.getZoomWidth(), plotArea.getHeight() * view.getZoomHeight());

        double relativeValue;

        if (this.getType() == AxisType.FIRST) {
            coordinate += Math.abs(view.getPanOffsetX());
            relativeValue = (coordinate - plotAreaVirtualSize.getX()) / plotAreaVirtualSize.getWidth();
        } else {
            coordinate += Math.abs(view.getPanOffsetY());
            relativeValue = (coordinate - plotAreaVirtualSize.getY()) / plotAreaVirtualSize.getHeight();
            relativeValue = 1 - relativeValue;
        }

        double delta = this.actualRange.maximum - this.actualRange.minimum;
        double value = (relativeValue * delta) + this.actualRange.minimum;
        value = this.reverseTransformValue(value);
        return value;
    }

    private void roundToUserTicks() {
        if (this.majorStep == 0)
            return;

        int fractionalDigits = 0;
        double tempStep = this.majorStep;
        while (tempStep < 1) {
            fractionalDigits++;
            tempStep *= 10;
        }

        double newStep = (this.actualRange.maximum - this.actualRange.minimum) / (this.userTickCount - 1);
        double multiplier = Math.pow(10, fractionalDigits);

        newStep *= multiplier;
        newStep += Math.ceil(newStep) - newStep;
        newStep /= multiplier;

        this.majorStep = newStep;
        this.actualRange.maximum = this.actualRange.minimum + ((this.userTickCount - 1) * this.majorStep);
    }

    private ValueRange<Double> calculateRange(AxisUpdateContext context) {
        ValueRange<Double> range;

        if (context.isStacked()) {
            range = this.calculateStackedRange(context);
        } else if (context.isStacked100()) {
            range = this.calculateStacked100Range(context);
        } else {
            range = this.calculateNormalRange(context.series());
        }

        return range;
    }

    private ValueRange<Double> calculateNormalRange(Iterable<ChartSeriesModel> series) {
        ValueRange<Double> range = new ValueRange<Double>(-1.0, -1.0);
        range.minimum = Double.POSITIVE_INFINITY;
        range.maximum = 0.0;

        // retrieve core range
        if (series != null) {
            for (ChartSeriesModel model : series) {
                for (Object point : model.visibleDataPoints()) {
                    DataPoint dataPoint = (DataPoint) point;
                    Object value = dataPoint.getValueForAxis(this);
                    Object transformedValue = this.transformValue(value);
                    range = this.adjustRange(transformedValue, range);
                }
            }
        }

        if (range.minimum == Double.POSITIVE_INFINITY) {
            range.minimum = 0.0;
        } else if (range.minimum.equals(range.maximum)) {
            if (range.minimum != 0) {
                range.minimum = 0.0;
            } else {
                range.maximum = 1.0;
            }
        }

        return range;
    }

    private ValueRange<Double> adjustRange(Object value, final ValueRange<Double> range) {
        if (value instanceof Double) {
            return this.adjustRange((Double)value, range);
        } else if (value instanceof Ohlc) {
            return this.adjustRange((Ohlc) value, range);
        } else if (value instanceof Range) {
            return this.adjustRange((Range) value, range);
        } else return range;
    }

    private ValueRange<Double> adjustRange(final Range value, final ValueRange<Double> range) {
        ValueRange<Double> newRange = range.clone();
        double high = value.high();
        double low = value.low();

        if (high > newRange.maximum) {
            newRange.maximum = high;
        }
        if (low < newRange.minimum) {
            newRange.minimum = low;
        }

        return newRange;
    }

    private ValueRange<Double> adjustRange(final Ohlc value, final ValueRange<Double> range) {
        ValueRange<Double> newRange = range.clone();
        double high = value.high();
        double low = value.low();

        if (high > newRange.maximum) {
            newRange.maximum = high;
        }
        if (low < newRange.minimum) {
            newRange.minimum = low;
        }

        return newRange;
    }

    private ValueRange<Double> adjustRange(Double value, final ValueRange<Double> range) {
        ValueRange<Double> newRange = range.clone();
        if (value > newRange.maximum) {
            newRange.maximum = value;
        }
        if (value < newRange.minimum) {
            newRange.minimum = value;
        }

        return newRange;
    }

    private ValueRange<Double> calculateStackedRange(AxisUpdateContext context) {
        ValueRange<Double> stackedRange = new ValueRange<Double>();
        stackedRange.minimum = context.getMinimumStackSum();
        stackedRange.maximum = context.maximumStackSum();

        // loop through non-combined series to check min/max value in their points
        ValueRange<Double> nonCombinedRange = this.calculateNormalRange(context.nonCombinedSeries());
        if (stackedRange.minimum > nonCombinedRange.minimum) {
            stackedRange.minimum = nonCombinedRange.minimum;
        }
        if (stackedRange.maximum < nonCombinedRange.maximum) {
            stackedRange.maximum = nonCombinedRange.maximum;
        }

        return stackedRange;
    }

    private ValueRange<Double> calculateStacked100Range(AxisUpdateContext context) {
        // Note: Stacked100 series cannot be combined with stacked & NORMAL series
        // so we should not loop through any potentially non-combined series here.
        ValueRange<Double> stacked100Range = new ValueRange<Double>(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (CombinedSeries series : context.combinedSeries()) {
            for (CombineGroup group : series.groups()) {
                for (CombineStack stack : group.stacks()) {
                    if (stack.positiveSum == 0 && stack.negativeSum == 0) {
                        continue;
                    }

                    if (stack.positiveSum == 0) {
                        if (stacked100Range.maximum < 0) {
                            stacked100Range.maximum = 0.0;
                        }

                        if (stacked100Range.minimum > -1) {
                            stacked100Range.minimum = -1.0;
                        }
                    } else if (stack.negativeSum == 0) {
                        if (stacked100Range.maximum < 1) {
                            stacked100Range.maximum = 1.0;
                        }

                        if (stacked100Range.minimum > 0) {
                            stacked100Range.minimum = 0.0;
                        }
                    } else {
                        double calculatedValue = stack.positiveSum / (stack.positiveSum - stack.negativeSum);
                        if (calculatedValue > stacked100Range.maximum) {
                            stacked100Range.maximum = calculatedValue;
                        }

                        calculatedValue = stack.negativeSum / (stack.positiveSum - stack.negativeSum);
                        if (calculatedValue < stacked100Range.minimum) {
                            stacked100Range.minimum = calculatedValue;
                        }
                    }

                    if (stacked100Range.minimum == -1 && stacked100Range.maximum == 1) {
                        return stacked100Range;
                    }
                }
            }
        }

        // Stacked100 series may have had no data points added
        if (stacked100Range.minimum == Double.POSITIVE_INFINITY) {
            stacked100Range.minimum = 0.0;
        }

        if (stacked100Range.maximum == Double.NEGATIVE_INFINITY) {
            stacked100Range.maximum = 0.0;
        }

        return stacked100Range;
    }

    private void plotNormal(Iterable<ChartSeriesModel> series) {
        double delta = this.actualRange.maximum - this.actualRange.minimum;
        Object value, transformedValue;
        NumericalAxisPlotInfoBase plotInfo;

        // update points values
        for (ChartSeriesModel model : series) {
            for (Object point : model.visibleDataPoints()) {
                DataPoint dataPoint = (DataPoint) point;
                if (dataPoint.isEmpty) {
                    continue;
                }
                value = dataPoint.getValueForAxis(this);
                transformedValue = this.transformValue(value);
                if (transformedValue instanceof Double) {
                    plotInfo = this.createAxisPlotInfo(delta, (Double) transformedValue);
                } else if (transformedValue instanceof Ohlc) {
                    Ohlc ohlcValue = (Ohlc) transformedValue;
                    plotInfo = this.createAxisOhlcPlotInfo(delta, ohlcValue);
                } else if (transformedValue instanceof Range) {
                    Range rangeValue = (Range) transformedValue;
                    plotInfo = this.createAxisRangePlotInfo(delta, rangeValue);
                } else {
                    continue;
                }

                dataPoint.setValueFromAxis(this, plotInfo);
            }
        }
    }

    private NumericalAxisRangePlotInfo createAxisRangePlotInfo(double delta, final Range value) {
        NumericalAxisRangePlotInfo plotInfo;
        double normalizedHigh, normalizedLow;
        if (delta == 0) {
            normalizedHigh = 0;
            normalizedLow = 0;
        } else {
            normalizedHigh = (value.high() - this.actualRange.minimum) / delta;
            normalizedLow = (value.low() - this.actualRange.minimum) / delta;
        }

        if (value.high() < value.low()) {
            normalizedLow = normalizedHigh;
        }

        plotInfo = NumericalAxisRangePlotInfo.create(this, this.normalizedOrigin, normalizedHigh, normalizedLow, this.normalizedOrigin);
        plotInfo.setSnapTickIndex(this.getSnapTickIndex(value.high()));
        plotInfo.snapBaseTickIndex = this.getSnapTickIndex(value.low());

        return plotInfo;
    }

    private NumericalAxisOhlcPlotInfo createAxisOhlcPlotInfo(double delta, final Ohlc value) {
        NumericalAxisOhlcPlotInfo plotInfo;
        double normalizedHigh, normalizedLow, normalizedOpen, normalizedClose;
        if (delta == 0) {
            normalizedHigh = 0;
            normalizedLow = 0;
            normalizedOpen = 0;
            normalizedClose = 0;
        } else {
            normalizedHigh = (value.high() - this.actualRange.minimum) / delta;
            normalizedLow = (value.low() - this.actualRange.minimum) / delta;
            normalizedOpen = (value.open() - this.actualRange.minimum) / delta;
            normalizedClose = (value.close() - this.actualRange.minimum) / delta;
        }

        plotInfo = NumericalAxisOhlcPlotInfo.create(this, this.normalizedOrigin, normalizedHigh, normalizedLow, normalizedOpen, normalizedClose, this.normalizedOrigin);
        plotInfo.setSnapTickIndex(this.getSnapTickIndex(value.high()));
        plotInfo.snapBaseTickIndex = this.getSnapTickIndex(value.low());
        plotInfo.snapOpenTickIndex = this.getSnapTickIndex(value.open());
        plotInfo.snapCloseTickIndex = this.getSnapTickIndex(value.close());

        return plotInfo;
    }

    private NumericalAxisPlotInfo createAxisPlotInfo(double delta, double doubleValue) {
        NumericalAxisPlotInfo plotInfo;
        double normalizedValue;
        if (delta == 0) {
            normalizedValue = 0;
        } else {
            normalizedValue = (doubleValue - this.actualRange.minimum) / delta;
        }
        plotInfo = NumericalAxisPlotInfo.create(this, this.normalizedOrigin, normalizedValue, this.normalizedOrigin);
        plotInfo.setSnapTickIndex(this.getSnapTickIndex(doubleValue));

        return plotInfo;
    }

    private void plotStacked(AxisUpdateContext context) {
        for (CombinedSeries series : context.combinedSeries()) {
            for (CombineGroup group : series.groups()) {
                this.plotCombineGroup(group, new Function2<CombineStack, Double, Double>() {
                    @Override
                    public Double apply(CombineStack argument1, Double argument2) {
                        return argument2;
                    }
                });
            }
        }

        this.plotNormal(context.nonCombinedSeries());
    }

    private void plotStacked100(AxisUpdateContext context) {
        // Note: Stacked100 series cannot be combined with stacked & NORMAL series
        // so we should not plot any non-combined series here.
        for (CombinedSeries series : context.combinedSeries()) {
            for (CombineGroup group : series.groups()) {
                this.plotCombineGroup(group, new Function2<CombineStack, Double, Double>() {
                    @Override
                    public Double apply(CombineStack stack, Double value) {
                        return value / (stack.positiveSum - stack.negativeSum);
                    }
                });
            }
        }
    }

    private void plotCombineGroup(CombineGroup group, Function2<CombineStack, Double, Double> stackValueProcessor) {
        double plotPositionPositiveStack, plotPositionNegativeStack;
        double positiveStackSum, negativeStackSum;
        double value, plotOriginOffset, normalizedValue;

        double stackSum;
        double delta = this.actualRange.maximum - this.actualRange.minimum;

        for (CombineStack stack : group.stacks()) {
            positiveStackSum = negativeStackSum = 0.0;
            plotPositionPositiveStack = plotPositionNegativeStack = this.normalizedOrigin;

            for (DataPoint point : stack.points()) {
                if (point.isEmpty) {
                    continue;
                }

                value = ((Number) point.getValueForAxis(this)).doubleValue();

                if (value >= DEFAULT_ORIGIN) {
                    stackSum = positiveStackSum;
                    plotOriginOffset = plotPositionPositiveStack;
                } else {
                    stackSum = negativeStackSum;
                    plotOriginOffset = plotPositionNegativeStack;
                }

                double transformedValue = this.transformValue((double)stackValueProcessor.apply(stack, value));

                stackSum += transformedValue;

                if (delta == 0) {
                    normalizedValue = 0;
                } else {
                    normalizedValue = (stackSum - this.actualRange.minimum) / delta;
                }

                NumericalAxisPlotInfo plotInfo = NumericalAxisPlotInfo.create(this, plotOriginOffset, normalizedValue, this.normalizedOrigin);
                plotInfo.setSnapTickIndex(this.getSnapTickIndex(stackSum));
                point.setValueFromAxis(this, plotInfo);

                if (value >= DEFAULT_ORIGIN) {
                    positiveStackSum = stackSum;
                    plotPositionPositiveStack = normalizedValue;
                } else {
                    negativeStackSum = stackSum;
                    plotPositionNegativeStack = normalizedValue;
                }
            }
        }
    }

    private void updatePlotOrigin(AxisUpdateContext context) {
        if (DEFAULT_ORIGIN >= this.actualRange.maximum) {
            this.normalizedOrigin = 1;
        } else if (DEFAULT_ORIGIN > this.actualRange.minimum) {
            this.normalizedOrigin = (DEFAULT_ORIGIN - this.actualRange.minimum) / (this.actualRange.maximum - this.actualRange.minimum);
        } else {
            this.normalizedOrigin = 0;
        }

        // apply the plot origin to the series
        if (context.series() != null) {
            for (ChartSeriesModel model : context.series()) {
                model.setValue(PLOT_ORIGIN_PROPERTY_KEY, this.normalizedOrigin);
            }
        }
    }

    private int getSnapTickIndex(double value) {
        if (value < this.actualRange.minimum || (value % this.majorStep) != 0) {
            return -1;
        }

        return (int) ((value - this.actualRange.minimum) / this.majorStep);
    }

    private class RangeCalculator {
        private final double deltaPercent = 16.667 / 100;
        private final double extendFactor = 0.05;
        public double minimum;
        public double maximum;
        private ValueRange<Double> range;
        private int extendDirection;
        private boolean userMin = false;
        private boolean userMax = false;

        public RangeCalculator(NumericalAxisModel axis, boolean userMin, boolean userMax) {
            this.range = axis.actualRange;
            this.extendDirection = axis.extendDirection;

            this.userMin = userMin;
            this.userMax = userMax;

            this.minimum = this.range.minimum;
            this.maximum = this.range.maximum;
        }

        public ValueRange<Double> extend() {
            //// we are using the same logic as within MS Excel to calculate the min and max values of the auto-range
            //// more details at http://support.microsoft.com/kb/214075

            boolean extendPositive = (this.extendDirection & NumericalAxisRangeExtendDirection.POSITIVE) == NumericalAxisRangeExtendDirection.POSITIVE;
            boolean extendNegative = (this.extendDirection & NumericalAxisRangeExtendDirection.NEGATIVE) == NumericalAxisRangeExtendDirection.NEGATIVE;

            if (extendNegative && !this.userMin) {
                this.extendNegative();
            }

            if (extendPositive && !this.userMax) {
                this.extendPositive();
            }

            return new ValueRange<Double>(this.minimum, this.maximum);
        }

        public ValueRange<Double> roundToMajorStep(double step) {
            double mod;
            if (!this.userMax) {
                mod = this.maximum % step;
                if (!RadMath.isZero(mod)) {
                    if (mod > 0) {
                        this.maximum += step - mod;
                    } else if (mod < 0) {
                        this.maximum += step + mod;
                    }
                }
            }

            if (!this.userMin) {
                mod = this.minimum % step;
                if (!RadMath.isZero(mod)) {
                    if (mod > 0) {
                        this.minimum -= mod;
                    } else if (mod < 0) {
                        this.minimum -= step + mod;
                    }
                }
            }

            return new ValueRange<Double>(this.minimum, this.maximum);
        }

        private void extendPositive() {
            double delta = this.range.maximum - this.range.minimum;

            if (this.range.minimum <= 0 && this.range.maximum <= 0) {
                if (delta > deltaPercent * -this.range.minimum) {
                    this.maximum = 0;
                } else {
                    this.maximum = this.range.maximum - ((this.range.minimum - this.range.maximum) / 2);
                }
            } else {
                this.maximum = this.range.maximum + (extendFactor * delta);
            }
        }

        private void extendNegative() {
            double delta = this.range.maximum - this.range.minimum;

            if (this.range.minimum >= 0 && this.range.maximum >= 0) {
                if (delta > deltaPercent * this.range.maximum) {
                    this.minimum = 0;
                } else {
                    this.minimum = this.range.minimum - (delta / 2);
                }
            } else {
                this.minimum = this.range.minimum + (extendFactor * (this.range.minimum - this.range.maximum));
            }
        }
    }
}

