package com.telerik.widget.chart.engine.axes.continuous;

import android.nfc.FormatException;

import com.telerik.android.common.DateTimeExtensions;
import com.telerik.android.common.TimeSpan;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.AxisUpdateContext;
import com.telerik.widget.chart.engine.axes.MajorTickModel;
import com.telerik.widget.chart.engine.axes.categorical.AxisSupportsCombinedSeriesPlot;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.common.TimeInterval;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.view.ChartView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a date axis that uses the actual time-line to plot series points.
 */
public class DateTimeContinuousAxisModel extends AxisModel implements ContinuousAxisModel, AxisSupportsCombinedSeriesPlot {

    static final int MAJOR_STEP_PROPERTY_KEY = PropertyKeys.register(DateTimeContinuousAxisModel.class, "MajorStep", ChartAreaInvalidateFlags.ALL);
    static final int MAJOR_STEP_UNIT_PROPERTY_KEY = PropertyKeys.register(DateTimeContinuousAxisModel.class, "MajorStepUnit", ChartAreaInvalidateFlags.ALL);
    static final int GAP_LENGTH_PROPERTY_KEY = PropertyKeys.register(DateTimeContinuousAxisModel.class, "GapLength", ChartAreaInvalidateFlags.ALL);
    static final int PLOT_MODE_PROPERTY_KEY = PropertyKeys.register(DateTimeContinuousAxisModel.class, "PlotMode", ChartAreaInvalidateFlags.ALL);
    static final int MINIMUM_PROPERTY_KEY = PropertyKeys.register(DateTimeContinuousAxisModel.class, "Minimum", ChartAreaInvalidateFlags.ALL);
    static final int MAXIMUM_PROPERTY_KEY = PropertyKeys.register(DateTimeContinuousAxisModel.class, "Maximum", ChartAreaInvalidateFlags.ALL);
    static final int MAXIMUM_TICKS_PROPERTY_KEY = PropertyKeys.register(DateTimeContinuousAxisModel.class, "MaximumTicks", ChartAreaInvalidateFlags.ALL);

    ValueRange<Calendar> actualRange;

    private static final String ISO8601DateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private double majorStep;
    long monthStep;
    long yearStep;
    TimeSpan tickInterval;
    TimeSpan minDelta;
    double tickZoomFactor;
    ValueRange<Long> visibleTicks;
    Calendar minDate;
    Calendar maxDate;
    DateFormat dateFormat = DateFormat.getDateInstance();

    AxisPlotMode actualPlotMode;
    ArrayList<DateTimePoint> values;
    PlotInfo plotInfo;

    /**
     * Creates a new instance of the {@link DateTimeContinuousAxisModel}.
     */
    public DateTimeContinuousAxisModel() {
        this.actualRange = new ValueRange<>();

        this.visibleTicks = new ValueRange<>(-1L, -1L);
        this.tickZoomFactor = 1.0;

        this.values = new ArrayList<>(16);

        Calendar min = Calendar.getInstance();
        min.setTime(new Date(Long.MIN_VALUE));
        minDate = min;

        Calendar max = Calendar.getInstance();
        max.setTime(new Date(Long.MAX_VALUE));
        maxDate = max;
    }

    @Override
    public int majorTickCount() {
        List<AxisTickModel> ticks = (List<AxisTickModel>) generateTicks(new ValueRange<>(0.0, 1.0));
        return ticks.size();
    }

    /**
     * Gets the gap length.
     *
     * @return The gap length.
     */
    public double getGapLength() {
        return this.getTypedValue(DateTimeContinuousAxisModel.GAP_LENGTH_PROPERTY_KEY, 0.3);
    }

    /**
     * Sets the gap length.
     *
     * @param value The gap length.
     */
    public void setGapLength(double value) {
        if (value < 0 || value > 1)
            throw new IllegalArgumentException("value for gap length cannot be negative or greater than 1");

        this.setValue(GAP_LENGTH_PROPERTY_KEY, value);
    }

    /**
     * Gets the {@link AxisPlotMode} used to position points along the axis.
     *
     * @return The plot mode.
     */
    public AxisPlotMode getPlotMode() {
        return this.getTypedValue(PLOT_MODE_PROPERTY_KEY, AxisPlotMode.BETWEEN_TICKS);
    }

    /**
     * Sets the {@link AxisPlotMode} used to position points along the axis.
     *
     * @param value The plot mode.
     */
    public void setPlotMode(AxisPlotMode value) {
        this.setValue(PLOT_MODE_PROPERTY_KEY, value);
    }

    /**
     * Gets a custom major step of the axis. The {@link TimeInterval} between each tick is calculated by using this value and the {@link #setMajorStepUnit(TimeInterval)}.
     * Specify {@link Double#POSITIVE_INFINITY} or {@link Double#NEGATIVE_INFINITY} to clear the custom value and to generate the step automatically.
     *
     * @return The major step.
     */
    public double getMajorStep() {
        return this.getTypedValue(MAJOR_STEP_PROPERTY_KEY, 0D);
    }

    /**
     * Sets a custom major step of the axis. The {@link com.telerik.widget.chart.engine.axes.common.TimeInterval} between each tick
     * depends on the major step and the major step unit, set through
     * {@link #setMajorStepUnit(com.telerik.widget.chart.engine.axes.common.TimeInterval)}.
     * If the major step is set to <code>0.0</code>, the value is reset and the steps are generated automatically.
     *
     * @param value The user-defined major step of the axis
     */
    public void setMajorStep(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Major step may not be less than 0");
        }

        double defaultMajorStep = 0.0;
        if (value != defaultMajorStep) {
            this.setValue(MAJOR_STEP_PROPERTY_KEY, value);
        } else {
            this.clearValue(MAJOR_STEP_PROPERTY_KEY);
        }
    }

    /**
     * Gets the unit that defines the custom major step of the axis.
     * If no explicit step is defined, the axis will automatically calculate one, depending on the smallest difference between any two dates.
     *
     * @return The major step unit.
     */
    public TimeInterval getMajorStepUnit() {
        return this.getTypedValue(MAJOR_STEP_UNIT_PROPERTY_KEY, TimeInterval.YEAR);
    }

    /**
     * Sets the unit that defines the custom major step of the axis.
     * If no explicit step is defined, the axis will automatically calculate one, depending on the smallest difference between any two dates.
     *
     * @param value The major step unit.
     */
    public void setMajorStepUnit(TimeInterval value) {
        this.setValue(MAJOR_STEP_UNIT_PROPERTY_KEY, value);
    }

    /**
     * Gets the actual {@link ValueRange} of this axis.
     *
     * @return The value range.
     */
    public ValueRange<Calendar> getActualRange() {
        return this.actualRange;
    }

    /**
     * Gets the custom minimum of the axis.
     * Specify the calendar min value to clear the property value so that the minimum is auto-generated.
     *
     * @return The minimum value of the axis.
     */
    public Calendar getMinimum() {
        return this.getTypedValue(MINIMUM_PROPERTY_KEY, minDate);
    }

    /**
     * Sets the custom minimum of the axis.
     * Specify the {@link Calendar} min value to clear the property value so that the minimum is auto-generated.
     *
     * @param value The minimum value of the axis.
     */
    public void setMinimum(Calendar value) {
        if (value.compareTo(minDate) == 0) {
            this.clearValue(MINIMUM_PROPERTY_KEY);
        } else {
            this.setValue(MINIMUM_PROPERTY_KEY, value);
        }
    }

    /**
     * Gets the custom maximum of the axis.
     * Specify {@link Calendar} max value to clear the property value so that the maximum is auto-generated.
     *
     * @return The maximum date of the axis.
     */
    public Calendar getMaximum() {
        return this.getTypedValue(MAXIMUM_PROPERTY_KEY, maxDate);
    }

    /**
     * Sets the custom maximum of the axis.
     * Specify {@link Calendar} max value to clear the property value so that the maximum is auto-generated.
     *
     * @param value The maximum date of the axis.
     */
    public void setMaximum(Calendar value) {
        if (value.compareTo(maxDate) == 0) {
            this.clearValue(MAXIMUM_PROPERTY_KEY);
        } else {
            this.setValue(MAXIMUM_PROPERTY_KEY, value);
        }
    }

    /**
     * Gets the maximum ticks that might be displayed on the axis. There are corner cases when ticks may become really huge number. Defaults to 31.
     *
     * @return The maximum ticks.
     */
    public int getMaximumTicks() {
        return this.getTypedValue(MAXIMUM_TICKS_PROPERTY_KEY, 31);
    }

    /**
     * Sets the maximum ticks that might be displayed on the axis. There are corner cases when ticks may become really huge number. Defaults to 31.
     *
     * @param value The maximum ticks.
     */
    public void setMaximumTicks(int value) {
        if (value <= 1)
            throw new IllegalArgumentException("value must be greater than one");

        this.setValue(MAXIMUM_TICKS_PROPERTY_KEY, value);
    }

    @Override
    public AxisPlotMode getActualPlotMode() {
        return this.actualPlotMode;
    }

    @Override
    public void resetState() {
        super.resetState();

        this.visibleTicks = new ValueRange<Long>(-1L, -1L);
    }

    @Override
    protected void updateCore(AxisUpdateContext context) {
        this.buildValues(context);
        if (this.values.size() == 0) {
            return;
        }

        this.updateActualPlotMode(context.series());
        this.updateActualRange();
        this.findMinDelta();

        if (!this.getCanPlot()) {
            return;
        }

        this.updateUnits();
        this.updatePlotInfo();

        this.buildTimeSlots();
    }

    void updateActualPlotMode(Iterable<ChartSeriesModel> seriesModels) {
        if (this.isLocalValue(DateTimeContinuousAxisModel.PLOT_MODE_PROPERTY_KEY)) {
            this.actualPlotMode = this.getPlotMode();
        } else {
            this.actualPlotMode = ChartSeriesModel.selectPlotMode(seriesModels);
        }
    }

    @Override
    protected void plotCore(AxisUpdateContext context) {
        if (!this.getCanPlot()) {
            return;
        }

        double delta = this.plotInfo.max - this.plotInfo.min;
        if (delta == 0) {
            return;
        }

        double pointPosition, timeSlotPosition, timeSlotLength;
        double pointSlotLength;
        double extend = this.plotInfo.extend / 2;

        for (DateTimePoint value : this.values) {
            if (value.slot == null) {
                continue;
            }

            double pointTicks = value.date.getTimeInMillis();

            pointPosition = ((pointTicks - this.plotInfo.min) + extend) / delta;
            timeSlotLength = value.slot.ticks / delta;
            timeSlotPosition = pointPosition - timeSlotLength / 2;

            double tmp = this.getGapLength() * timeSlotLength;
            pointSlotLength = timeSlotLength - tmp;

            CategoricalAxisPlotInfo plotInfo = CategoricalAxisPlotInfo.create(this, timeSlotPosition, timeSlotLength);
            plotInfo.categoryKey = value.date;
            plotInfo.position = pointPosition - (pointSlotLength / 2);
            plotInfo.length = pointSlotLength;

            value.point.setValueFromAxis(this, plotInfo);
        }
    }

    @Override
    public void onZoomChanged() {
        super.onZoomChanged();

        if (!this.getCanPlot()) {
            return;
        }

        double oldZoom = this.tickZoomFactor;
        this.updateUnits();

        if (oldZoom != this.tickZoomFactor) {
            // reset the visible ticks
            // TODO: Possible optimization - may subtract the difference, depending on the new zoom factor
            this.visibleTicks = new ValueRange<Long>(-1L, -1L);

            if (this.actualPlotMode == AxisPlotMode.BETWEEN_TICKS) {
                this.updatePlotInfo();
                this.isPlotValid = false;
            }
        }
    }

    @Override
    protected Iterable<AxisTickModel> generateTicks(final ValueRange<Double> visibleRange) {
        if (!this.getCanPlot()) {
            return new ArrayList<>();
        }

        this.updateVisibleTicks(visibleRange);
        double plotDelta = this.plotInfo.max - this.plotInfo.min;

        double startTicks = Math.max(0, (this.visibleTicks.minimum - this.plotInfo.min) / plotDelta);
        double endTicks = Math.min(1, (this.visibleTicks.maximum - this.plotInfo.min) / plotDelta);
        double currentTicks = startTicks;
        double paddedCurrentTicks = currentTicks;

        int virtualIndex = (int) (startTicks * this.values.size());

        if (this.actualPlotMode == AxisPlotMode.ON_TICKS_PADDED) {
            double nextTicks = this.getNextTicks(this.plotInfo.min, this.tickZoomFactor);
            paddedCurrentTicks += (nextTicks - this.plotInfo.min) / plotDelta / 2;
        }

        LinkedList<AxisTickModel> tickList = new LinkedList<>();
        while (paddedCurrentTicks < endTicks || RadMath.areClose(paddedCurrentTicks, endTicks)) {
            MajorTickModel tick = new MajorTickModel(
                    this.plotInfo.min + currentTicks * plotDelta,
                    paddedCurrentTicks,
                    virtualIndex);

            double nextTicks = this.getNextTicks((long) tick.value(), this.tickZoomFactor);

            double step = (nextTicks - tick.value()) / plotDelta;
            currentTicks += step;
            paddedCurrentTicks += step;
            virtualIndex++;

            tickList.add(tick);
        }

        return tickList;
    }

    @Override
    protected Object getLabelContentCore(AxisTickModel tick) {
        Date date = new Date((long) tick.value());

        if (this.dateFormat == null) {
            return DateFormat.getDateInstance().format(date);
        }

        return this.dateFormat.format(date);
    }

    public void setDateFormat(DateFormat value) {
        this.dateFormat = value;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    @Override
    public AxisPlotInfo createPlotInfo(Object value) {
        if (!(value instanceof Calendar) || this.plotInfo == null) {
            return super.createPlotInfo(value);
        }

        Calendar date = (Calendar) value;
        double delta = this.plotInfo.max - this.plotInfo.min;
        double extend = this.plotInfo.extend / 2;
        double pointTicks = date.getTimeInMillis();
        double pointPosition = (pointTicks - this.plotInfo.min + extend) / delta;

        CategoricalAxisPlotInfo plotInfo = CategoricalAxisPlotInfo.create(this, pointPosition, 0.0);
        plotInfo.categoryKey = date;
        plotInfo.position = pointPosition;

        return plotInfo;
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

        double position;

        if (this.getType() == AxisType.FIRST) {
            coordinate += Math.abs(view.getPanOffsetX());
            position = (coordinate - plotAreaVirtualSize.getX()) / plotAreaVirtualSize.getWidth();
        } else {
            coordinate += Math.abs(view.getPanOffsetY());
            position = (coordinate - plotAreaVirtualSize.getY()) / plotAreaVirtualSize.getHeight();
            position = 1 - position;
        }

        double delta = this.plotInfo.max - this.plotInfo.min;
        double extend = this.plotInfo.extend - 2;
        double min = this.plotInfo.min;

        double tmp = position * delta;
        double pointTicks = (tmp + min) - extend / 2.0;

        tmp = pointTicks + 0.5;

        Calendar result = Calendar.getInstance();
        result.setTime(new Date(Math.round(tmp)));
        return result;
    }

    @Override
    public AxisLabelModel generateLastLabel() {
        AxisLabelModel lastLabel = new AxisLabelModel(1, RadPoint.getEmpty(), RadSize.getEmpty());

        Object content = "";
        if (values.size() > 0) {
            PlotInfo info = createPlotInfo(calculateActualRange());
            content = getLabelContent(new MajorTickModel(info.max, 1, 0));
        }

        lastLabel.setContent(content);

        lastLabel.desiredSize = this.getPresenter().measureContent(lastLabel, lastLabel.getContent());
        return lastLabel;
    }

    @Override
    public Object getCombineGroupKey(DataPoint point) {
        Object value = point.getValueForAxis(this);
        if (value != null) {
            return value;
        }

        return super.getCombineGroupKey(point);
    }

    private boolean getCanPlot() {
        return this.values.size() > 0 && this.minDelta.getMillis() > 0;
    }

    private void updateVisibleTicks(final ValueRange<Double> visibleRange) {

        long delta = this.plotInfo.max - this.plotInfo.min;
        long visibleTicksStart = this.plotInfo.min + Math.round(visibleRange.minimum * delta);
        long visibleTicksEnd = this.plotInfo.min + Math.round(visibleRange.maximum * delta);

        // check whether this is the first time we are initializing the ticks
        if (this.visibleTicks.minimum == -1) {
            this.visibleTicks.minimum = this.plotInfo.min;
        }

        if (this.visibleTicks.maximum == -1) {
            this.visibleTicks.maximum = this.plotInfo.max;
        }

        // find minimum
        while (this.visibleTicks.minimum > visibleTicksStart) {
            this.visibleTicks.minimum = this.getPreviousTicks(this.visibleTicks.minimum, this.tickZoomFactor);
        }
        while (this.visibleTicks.minimum < visibleTicksStart) {
            this.visibleTicks.minimum = this.getNextTicks(this.visibleTicks.minimum, this.tickZoomFactor);
        }

        // find maximum
        while (this.visibleTicks.maximum < visibleTicksEnd) {
            this.visibleTicks.maximum = this.getNextTicks(this.visibleTicks.maximum, this.tickZoomFactor);
        }
        while (this.visibleTicks.maximum > visibleTicksEnd) {
            this.visibleTicks.maximum = this.getPreviousTicks(this.visibleTicks.maximum, this.tickZoomFactor);
        }

        // add one additional tick at start and one at end
        this.visibleTicks.minimum = this.getPreviousTicks(this.visibleTicks.minimum, this.tickZoomFactor);
        this.visibleTicks.maximum = this.getNextTicks(this.visibleTicks.maximum, this.tickZoomFactor);
    }

    private double calculateTickZoomFactor() {
        double zoomFactor = this.getLayoutStrategy().getZoom();

        // minimum delta is the maximum available zoom
        long tickIntervalTicks = this.tickInterval.getMillis();
        long minDeltaTicks = this.minDelta.getMillis();
        if (tickIntervalTicks / zoomFactor < minDeltaTicks) {
            zoomFactor = tickIntervalTicks / minDeltaTicks;
        } else {
            zoomFactor -= zoomFactor % 2;
        }

        return Math.max(1.0, zoomFactor);
    }

    private void findMinDelta() {
        // find the smallest difference between any two dates - this will give us the major and minor unit components
        // since values are sorted, all we need to do is loop the list once and compare two adjacent values
        this.minDelta = TimeSpan.getZero();
        DateTimePoint prevPoint = null;

        for (DateTimePoint point : this.values) {
            if (prevPoint != null && !prevPoint.date.equals(point.date)) {
                TimeSpan diff = DateTimeExtensions.subtract(point.date, prevPoint.date);
                if (diff.compareTo(TimeSpan.getZero()) != 0 && diff.compareTo(this.minDelta) < 0 || this.minDelta.compareTo(TimeSpan.getZero()) == 0) {
                    this.minDelta = diff;
                }
            }

            prevPoint = point;
        }

        // min delta will not be initialized if only one point is present in the widget
        if (this.minDelta.compareTo(TimeSpan.getZero()) == 0) {
            this.minDelta = TimeSpan.fromMilliseconds(this.values.get(0).date.getTimeInMillis());
        }
    }

    private void updateUnits() {
        this.monthStep = -1;
        this.yearStep = -1;

        Number userStep = (Number) this.getValue(MAJOR_STEP_PROPERTY_KEY);

        long maxTicks = this.actualRange.maximum.getTimeInMillis();
        long minTicks = this.actualRange.minimum.getTimeInMillis();

        TimeSpan range = TimeSpan.fromMilliseconds(maxTicks - minTicks);
        if (userStep != null) {
            long stepLong = userStep.longValue();
            this.tickInterval = this.getUserStep(stepLong);

            if (this.tickInterval.compareTo(range) > 0) {
                this.tickInterval = range;
            }
        } else {
            this.tickInterval = this.minDelta;
            if (this.getValue(MAXIMUM_TICKS_PROPERTY_KEY) == null) {
                int totalDays = this.tickInterval.getTotalDays();
                if ((totalDays >= 28 && totalDays <= 31) ||
                        (totalDays >= 59 && totalDays <= 62) ||
                        (totalDays >= 89 && totalDays <= 92) ||
                        (totalDays >= 120 && totalDays <= 122) ||
                        (totalDays >= 181 && totalDays <= 184)) {
                    // tickInterval represents something like 1, 2, 3, 4, or 6 months.
                    this.monthStep = 1;
                } else if ((totalDays >= 365 && totalDays <= 366) ||
                        (totalDays >= 730 && totalDays <= 731)) {
                    // tickInterval represents something like 1 or 2 years.
                    this.yearStep = 1;
                }
            }

            double zoomFactor = this.getLayoutStrategy().getZoom();
            double tmp = this.tickInterval.getMillis() / zoomFactor;
            double tickCount = range.getMillis() / tmp;
            double maximumTicks = this.getMaximumTicks();
            if (tickCount > maximumTicks - 1) {
                this.tickInterval = TimeSpan.fromMilliseconds(range.getMillis() / (long) (maximumTicks - 1));
            }
        }

        this.majorStep = this.tickInterval.getMillis();

        // we can have zero as a step when adding points with same DATE.
        if (this.majorStep == 0) {
            // use one-month step as default
            this.monthStep = 1;
        }

        this.tickZoomFactor = this.calculateTickZoomFactor();

    }

    private TimeSpan getUserStep(long step) {
        switch (this.getMajorStepUnit()) {
            case DAY:
                return TimeSpan.fromDays(step);
            case HOUR:
                return TimeSpan.fromHours(step);
            case MILLISECOND:
                return TimeSpan.fromMilliseconds(step);
            case MINUTE:
                return TimeSpan.fromMinutes(step);
            case MONTH:
                this.monthStep = step;
                return TimeSpan.fromDays(365 / 12 * step);
            case QUARTER:
                this.monthStep = step * 3;
                return TimeSpan.fromDays(365 / 12 * step);
            case SECOND:
                return TimeSpan.fromSeconds(step);
            case WEEK:
            case WEEK_OF_YEAR:
                return TimeSpan.fromDays(7 * step);
            case DAY_OF_WEEK:
            case DAY_OF_WEEK_IN_MONTH:
            case DAY_OF_YEAR:
                return TimeSpan.fromDays(step);
            case TIME_IN_MILLIS:
                return TimeSpan.fromMilliseconds(step);
            default:
                // YEAR.
                this.yearStep = step;
                return TimeSpan.fromDays(365 * step);
        }
    }

    private void updateActualRange() {
        this.actualRange = calculateActualRange();
    }

    private ValueRange<Calendar> calculateActualRange() {
        ValueRange<Calendar> actualRange = this.getAutoRange();

        Object userMin = this.getValue(MINIMUM_PROPERTY_KEY);
        if (userMin != null) {
            actualRange.minimum = (Calendar) userMin;
        }

        Object userMax = this.getValue(MAXIMUM_PROPERTY_KEY);
        if (userMax != null) {
            actualRange.maximum = (Calendar) userMax;
        }

        return actualRange;
    }

    private ValueRange<Calendar> getAutoRange() {
        ValueRange<Calendar> autoRange = new ValueRange<Calendar>();
        autoRange.minimum = this.values.get(0).date;
        autoRange.maximum = this.values.get(this.values.size() - 1).date;

        return autoRange;
    }

    private void updatePlotInfo() {
        this.plotInfo = createPlotInfo(this.actualRange);
    }

    private PlotInfo createPlotInfo(ValueRange<Calendar> actualRange) {
        PlotInfo plotInfo = new PlotInfo();

        plotInfo.min = actualRange.minimum.getTimeInMillis();
        plotInfo.max = actualRange.maximum.getTimeInMillis();

        if (plotInfo.min > plotInfo.max) {
            throw new IllegalStateException("The axis minimum is greater than the axis maximum.");
        }

        if (this.actualPlotMode == AxisPlotMode.BETWEEN_TICKS || this.actualPlotMode == AxisPlotMode.ON_TICKS_PADDED) {
            // add one additional tick at the end
            long nextTicks = this.getNextTicks(plotInfo.max, this.tickZoomFactor);
            plotInfo.extend = nextTicks - plotInfo.max;
            plotInfo.max = plotInfo.max + plotInfo.extend;
        } else if (plotInfo.min == plotInfo.max) {
            plotInfo.max = this.getNextTicks(plotInfo.min, this.tickZoomFactor);
        }

        return plotInfo;
    }

    private void buildTimeSlots() {
        long startTicks = this.values.get(0).date.getTimeInMillis();
        long endTicks = this.values.get(this.values.size() - 1).date.getTimeInMillis();

        if (startTicks == endTicks) {
            this.buildSingleTimeSlot();
            return;
        }

        int pointCount = this.values.size();
        int pointIndex = 0;
        long currentTicks = startTicks;
        long nextTicks;

        while (currentTicks <= endTicks) {
            nextTicks = this.getNextTicks(currentTicks, 1.0);

            if (pointIndex < pointCount) {
                DateTimePoint point = this.values.get(pointIndex);

                // value falls within the slot
                if (point.date.getTimeInMillis() < nextTicks) {
                    TimeSlot slot = new TimeSlot();
                    slot.startTicks = currentTicks;
                    slot.ticks = nextTicks - currentTicks;

                    point.slot = slot;

                    // move to next point index
                    pointIndex++;
                    while (pointIndex < pointCount) {
                        DateTimePoint nextPoint = this.values.get(pointIndex);
                        if (nextPoint.date.getTimeInMillis() >= nextTicks) {
                            break;
                        }

                        nextPoint.slot = slot;
                        pointIndex++;
                    }
                }
            }

            currentTicks = nextTicks;
        }
    }

    private void buildSingleTimeSlot() {
        long startTicks = this.actualRange.minimum.getTimeInMillis();
        long ticks = this.getNextTicks(startTicks, 1.0) - startTicks;

        TimeSlot slot = new TimeSlot();
        slot.startTicks = startTicks;
        slot.ticks = ticks;

        for (DateTimePoint point : this.values) {
            point.slot = slot;
        }
    }

    private void buildValues(AxisUpdateContext context) {
        this.values.clear();

        if (context.series() == null) {
            return;
        }

        AxisPlotDirection direction = this.getType() == AxisType.FIRST ? AxisPlotDirection.VERTICAL : AxisPlotDirection.HORIZONTAL;

        for (ChartSeriesModel series : context.series()) {
            // tell each series what is the plot direction
            series.setValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, direction);

            for (Object obj : series.visibleDataPoints()) {
                DataPoint point = (DataPoint) obj;
                Object value = point.getValueForAxis(this);
                if (!(value instanceof Calendar)) {

                    if (value instanceof Long) {

                        Calendar newInstance = Calendar.getInstance();
                        newInstance.setTimeInMillis((Long) value);
                        value = newInstance;
                    } else if (value instanceof String) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(ISO8601DateFormat);
                            Date parsedDate = sdf.parse((String) value);
                            Calendar newInstance = Calendar.getInstance();
                            newInstance.setTimeInMillis(parsedDate.getTime());
                            value = newInstance;
                        } catch (Exception e) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }

                DateTimePoint datePoint = new DateTimePoint();
                datePoint.point = point;
                datePoint.date = (Calendar) value;

                this.values.add(datePoint);
            }
        }

        // sort all the values chronologically

        Collections.sort(this.values, new Comparator<DateTimePoint>() {
            @Override
            public int compare(DateTimePoint dateTimePoint, DateTimePoint dateTimePoint2) {
                return dateTimePoint.compareTo(dateTimePoint2);
            }
        });
    }

    private long getNextTicks(long currentTicks, Double zoomFactor) {
        if (this.monthStep != -1) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(currentTicks);
            double tmp = this.monthStep / zoomFactor;
            int months = Math.max(1, (int) tmp);
            date.add(Calendar.MONTH, months);

            return date.getTimeInMillis();
        } else if (this.yearStep != -1) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(currentTicks);
            double tmp = this.yearStep / zoomFactor;
            int years = Math.max(1, (int) tmp);
            date.add(Calendar.YEAR, years);

            return date.getTimeInMillis();
        }

        double tmp = this.majorStep / zoomFactor;
        return currentTicks + Math.round(tmp);
    }

    private long getPreviousTicks(long currentTicks, Double zoomFactor) {
        if (this.monthStep != -1) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(currentTicks);
            double tmp = this.monthStep / zoomFactor;
            int months = Math.max(1, (int) tmp);
            date.add(Calendar.MONTH, -months);

            return date.getTimeInMillis();
        } else if (this.yearStep != -1) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(currentTicks);
            double tmp = this.yearStep / zoomFactor;
            int years = Math.max(1, (int) tmp);
            date.add(Calendar.YEAR, -years);

            return date.getTimeInMillis();
        }

        return currentTicks - Math.round(this.majorStep / zoomFactor);
    }

    class DateTimePoint implements Comparable<DateTimePoint> {
        public Calendar date;
        public DataPoint point;
        public TimeSlot slot;

        @Override
        public int compareTo(DateTimePoint other) {
            return this.date.compareTo(other.date);
        }
    }

    class TimeSlot {
        public double startTicks;
        public double ticks;
    }

    class PlotInfo {
        public long min = 0L;
        public long max = 0L;
        public long extend = 0L;
    }
}

