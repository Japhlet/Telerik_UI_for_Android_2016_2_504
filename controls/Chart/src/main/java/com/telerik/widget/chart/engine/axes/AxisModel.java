package com.telerik.widget.chart.engine.axes;

import com.telerik.android.common.Function;
import com.telerik.android.common.RadThickness;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.axes.common.AxisLastLabelVisibility;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation;
import com.telerik.widget.chart.engine.axes.common.layout.AxisModelLayoutStrategy;
import com.telerik.widget.chart.engine.axes.common.layout.HorizontalAxisLayoutStrategy;
import com.telerik.widget.chart.engine.axes.common.layout.VerticalAxisLayoutStrategy;
import com.telerik.widget.chart.engine.axes.continuous.ValueRange;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ElementCollection;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

import java.util.ArrayList;

/**
 * This class represents the part of the Chart engine which calculates the position of labels, ticks and values on the
 * different axes supported by the Chart component. This class is abstract and should not be used directly in your application.
 */
public abstract class AxisModel extends ChartElement {

    public static final int PLOT_ORIGIN_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "plotOrigin");
    public static final int PLOT_DIRECTION_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "plotDirection");
    private static final int MAJOR_TICK_LENGTH_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "MajorTickLength", ChartAreaInvalidateFlags.ALL);
    private static final int MAJOR_TICK_OFFSET_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "MajorTickOffset", ChartAreaInvalidateFlags.ALL);
    private static final int TICK_THICKNESS_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "TickThickness", ChartAreaInvalidateFlags.INVALIDATE_AXES);
    private static final int LABEL_MARGIN_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LabelMargin", ChartAreaInvalidateFlags.INVALIDATE_AXES);
    private static final int LINE_THICKNESS_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LineThickness", ChartAreaInvalidateFlags.INVALIDATE_AXES);
    private static final int SHOW_LABELS_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "ShowLabels", ChartAreaInvalidateFlags.ALL);
    private static final int LABEL_INTERVAL_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LabelInterval", ChartAreaInvalidateFlags.ALL);
    private static final int LABEL_OFFSET_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LabelOffset", ChartAreaInvalidateFlags.ALL);
    private static final int LABEL_FIT_MODE_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LabelFitMode", ChartAreaInvalidateFlags.ALL);
    private static final int LABEL_FORMAT_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LabelFormat", ChartAreaInvalidateFlags.ALL);
    private static final int LABEL_LAYOUT_MODE_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LabelLayoutMode", ChartAreaInvalidateFlags.NONE);
    private static final int LAST_LABEL_VISIBILITY_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "LastLabelVisibility", ChartAreaInvalidateFlags.ALL);
    private static final int HORIZONTAL_LOCATION_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "getHorizontalLocation", ChartAreaInvalidateFlags.ALL);
    private static final int VERTICAL_LOCATION_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "VerticalLocation", ChartAreaInvalidateFlags.ALL);
    private static final int NORMALIZED_LABEL_ROTATION_ANGLE_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "NormalizedLabelRotationAngle", ChartAreaInvalidateFlags.ALL);
    private static final int WIDTH_PROPERTY_KEY = PropertyKeys.register(AxisModel.class, "Width", ChartAreaInvalidateFlags.INVALIDATE_AXES);

    private ElementCollection<AxisTickModel> ticks;
    private RadSize desiredSize;
    private RadThickness desiredMargin = RadThickness.getEmpty();
    private boolean isUpdated = false;
    private AxisModelLayoutStrategy layoutStrategy;
    private AxisLabelFitMode labelFitMode = AxisLabelFitMode.NONE;
    private boolean isPrimary = false;
    private ElementCollection<AxisLabelModel> labels;
    AxisType type;
    RadRect desiredArrangeRect;
    boolean isMeasureValid = false;
    AxisTitleModel title;
    ValueRange<Double> visibleRange;
    String labelFormat;
    private RadSize lastMeasureSize;
    private Function<Object, String> labelToStringConverter;


    protected boolean isPlotValid = false;

    /**
     * Creates an instance of the {@link AxisModel} class.
     */
    public AxisModel() {
        this.trackPropertyChanged = true;
        this.ticks = new ElementCollection<AxisTickModel>(this);
        this.labels = new ElementCollection<AxisLabelModel>(this);
        this.title = new AxisTitleModel();
        this.title.trackPropertyChanged = true; // track changes to invalidate the widget area when content is changed

        this.children.add(this.title);

        this.labelFormat = "";

        this.setType(AxisType.FIRST);
    }

    protected AxisModelLayoutStrategy getLayoutStrategy() {
        return this.layoutStrategy;
    }

    public int getWidth() {
        return this.getTypedValue(WIDTH_PROPERTY_KEY, -1);
    }

    public void setWidth(int value) {
        this.setValue(WIDTH_PROPERTY_KEY, value);
    }

    public RadSize getDesiredSize() {
        return this.desiredSize;
    }

    /**
     * Gets a boolean value indicating whether the current {@link AxisModel} class
     * is in an up-to-date state.
     *
     * @return <code>true</code> if the state is up to date, otherwise <code>false</code>.
     */
    public boolean isUpdated() {
        return this.isUpdated;
    }

    /**
     * Gets a boolean value determining whether the current {@link AxisModel}
     * describes a primary axis in the associated chart.
     *
     * @return <code>true</code> if the axis is primary, otherwise <code>false/code>.
     */
    public boolean getIsPrimary() {
        return this.isPrimary;
    }

    /**
     * Sets a boolean value determining whether the current {@link AxisModel}
     * describes a primary axis in the associated chart.
     *
     * @param value <code>true</code> if the axis will be primary, otherwise <code>false</code>.
     */
    public void setIsPrimary(boolean value) {
        if (this.isPrimary == value) {
            return;
        }

        this.isPrimary = value;
    }

    /**
     * Gets the count of the major ticks currently generated
     * in this {@link AxisModel}.
     *
     * @return the count of the major ticks.
     */
    public abstract int majorTickCount();


    /**
     * Returns the desired margin for the axis associated with this {@link AxisModel}.
     *
     * @return an instance of the {@link RadThickness} class describing the margin.
     */
    public RadThickness desiredMargin() {
        return this.desiredMargin.clone();
    }

    /**
     * Gets a boolean value determining whether the current plot is valid.
     *
     * @return <code>true</code> if the plot is valid, otherwise <code>false</code>.
     */
    public boolean isPlotValid() {
        return this.isPlotValid;
    }

    /**
     * Gets a string representing the format used to display the labels on the {@link com.telerik.widget.chart.visualization.common.Axis} associated with this {@link AxisModel}.
     *
     * @return the label format.
     */
    public String getLabelFormat() {
        return this.labelFormat;
    }

    /**
     * Sets a string representing the format used to display the labels on the {@link com.telerik.widget.chart.visualization.common.Axis} associated with this {@link AxisModel}.
     *
     * @param value the label format.
     */
    public void setLabelFormat(String value) {
        this.setValue(LABEL_FORMAT_PROPERTY_KEY, value);
    }

    /**
     * Gets a value from the {@link AxisLastLabelVisibility} enum which determines how the last axis label will be displayed.
     *
     * @return the last label display mode.
     */
    public AxisLastLabelVisibility getLastLabelVisibility() {
        AxisLastLabelVisibility defaultVisibility = this.layoutStrategy == null ? AxisLastLabelVisibility.HIDDEN : this.layoutStrategy.getDefaultLastLabelVisibility();
        return this.getTypedValue(LAST_LABEL_VISIBILITY_PROPERTY_KEY, defaultVisibility);
    }

    /**
     * Gets a value from the {@link AxisLastLabelVisibility} enum which determines how the last axis label will be displayed.
     *
     * @param value the last label display mode.
     */
    public void setLastLabelVisibility(AxisLastLabelVisibility value) {
        this.setValue(LAST_LABEL_VISIBILITY_PROPERTY_KEY, value);
    }

    /**
     * Gets a value from the {@link AxisLabelFitMode} enum which determines
     * how labels are positioned on the axis.
     *
     * @return the {@link AxisLabelFitMode} value.
     */
    public AxisLabelFitMode getLabelFitMode() {
        return this.labelFitMode;
    }

    /**
     * Sets a value from the {@link AxisLabelFitMode} enum which determines
     * how labels are positioned on the axis.
     *
     * @param value the {@link AxisLabelFitMode} value.
     */
    public void setLabelFitMode(AxisLabelFitMode value) {
        this.setValue(LABEL_FIT_MODE_PROPERTY_KEY, value);
    }

    /**
     * Gets the label interval in count of label positions.
     *
     * @return the count of the positions which represent the label interval.
     */
    public int getLabelInterval() {
        return this.getTypedValue(LABEL_INTERVAL_PROPERTY_KEY, 1);
    }

    /**
     * Sets the label interval in count of label positions.
     *
     * @param value the count of the positions which represent the label interval.
     */
    public void setLabelInterval(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("value cannot be negative or zero");
        }

        this.setValue(LABEL_INTERVAL_PROPERTY_KEY, value);
    }

    /**
     * Gets the index of the label from the first label to be displayed.
     */
    public int getLabelOffset() {
        return this.getTypedValue(LABEL_OFFSET_PROPERTY_KEY, 0);
    }

    /**
     * Sets the index of the label from the first label to be displayed.
     */
    public void setLabelOffset(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("the label offset cannot be negative value");
        }

        this.setValue(LABEL_OFFSET_PROPERTY_KEY, value);
    }

    /**
     * Gets the index of the tick from the first tick to be displayed.
     */
    public int getMajorTickOffset() {
        return this.getTypedValue(MAJOR_TICK_OFFSET_PROPERTY_KEY, 0);
    }

    /**
     * Sets the index of the tick from the first tick to be displayed.
     */
    public void setMajorTickOffset(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("MajorTickOffset value cannot be less than 0.");
        }

        this.setValue(MAJOR_TICK_OFFSET_PROPERTY_KEY, value);
    }

    /**
     * Gets the normalized rotation angle of the labels.
     *
     * @return the rotation angle.
     */
    public double getNormalizedLabelRotationAngle() {
        return this.getTypedValue(NORMALIZED_LABEL_ROTATION_ANGLE_PROPERTY_KEY, 300D);
    }

    /**
     * Gets the normalized rotation angle of the labels.
     *
     * @param value the rotation angle.
     */
    public void setNormalizedLabelRotationAngle(double value) {
        if (value < 0 || value > 360) {
            throw new IllegalArgumentException("NormalizedLabelRotationAngle cannot ne negative or greater than 360");
        }

        this.setValue(NORMALIZED_LABEL_ROTATION_ANGLE_PROPERTY_KEY, value);
    }

    /**
     * Gets the {@link java.lang.Iterable} collection with all the major ticks currently present on the axis.
     */
    public Iterable<AxisTickModel> getMajorTicks() {
        ArrayList<AxisTickModel> majorTicks = new ArrayList<AxisTickModel>();
        for (AxisTickModel tick : this.ticks) {
            if (tick.getType() == TickType.MAJOR) {
                majorTicks.add(tick);
            }
        }
        return majorTicks;
    }

    /**
     * Gets the {@link com.telerik.widget.chart.engine.elementTree.ElementCollection} instance holding all
     * available labels on the axis.
     */
    public ElementCollection<AxisLabelModel> getLabels() {
        return this.labels;
    }

    /**
     * Gets the thickness in pixels of a single tick presented on the axis.
     *
     * @return the thickness.
     */
    public float getTickThickness() {
        return this.getTypedValue(TICK_THICKNESS_PROPERTY_KEY, 1F);
    }

    /**
     * Sets the thickness in pixels of a single tick presented on the axis.
     *
     * @param value the thickness.
     */
    public void setTickThickness(float value) {
        if (value < 0) {
            throw new IllegalArgumentException("Thickness may not be negative value.");
        }

        this.setValue(TICK_THICKNESS_PROPERTY_KEY, value);
    }

    /**
     * Gets the margin between the label and the axis line.
     *
     * @return the label margin.
     */
    public float getLabelMargin() {
        return this.getTypedValue(LABEL_MARGIN_PROPERTY_KEY, 0f);
    }

    /**
     * Sets the margin between the label and the axis line.
     *
     * @param value The label margin.
     */
    public void setLabelMargin(float value) {
        if (value < 0)
            throw new IllegalArgumentException("value cannot be negative");

        this.setValue(LABEL_MARGIN_PROPERTY_KEY, value);
    }

    /**
     * Gets a boolean value indicating whether labels will be displayed on this axis.
     *
     * @return <code>true</code> if labels are shown, otherwise <code>false</code>.
     */
    public boolean getShowLabels() {
        return this.getTypedValue(SHOW_LABELS_PROPERTY_KEY, true);
    }

    /**
     * Sets a value indicating whether labels will be displayed on this axis.
     *
     * @param value <code>true</code> if labels are shown, otherwise <code>false</code>.
     */
    public void setShowLabels(boolean value) {
        this.setValue(SHOW_LABELS_PROPERTY_KEY, value);
    }


    /**
     * Gets the label layout mode. See {@link com.telerik.widget.chart.engine.axes.AxisLabelLayoutMode} for more information.
     */
    public AxisLabelLayoutMode getLabelLayoutMode() {
        return this.getTypedValue(LABEL_LAYOUT_MODE_PROPERTY_KEY, AxisLabelLayoutMode.OUTER);
    }

    /**
     * Sets the labels layout mode. The default value is OUTER. See {@link com.telerik.widget.chart.engine.axes.AxisLabelLayoutMode} for more information.
     *
     * @param value The new layout mode.
     */
    public void setLabelLayoutMode(AxisLabelLayoutMode value) {
        this.setValue(LABEL_LAYOUT_MODE_PROPERTY_KEY, value);
    }

    /**
     * Gets the length of a single tick presented on the axis. The default
     * tick length is 5.
     *
     * @return the tick length.
     */
    public float getMajorTickLength() {
        return this.getTypedValue(MAJOR_TICK_LENGTH_PROPERTY_KEY, 5F);
    }

    /**
     * Sets the length of a single tick presented on the axis.
     *
     * @param value the tick length.
     */
    public void setMajorTickLength(float value) {
        if (value < 0) {
            throw new IllegalArgumentException("Thickness may not be a negative value.");
        }

        this.setValue(MAJOR_TICK_LENGTH_PROPERTY_KEY, value);
    }

    /**
     * Gets the thickness of the {@link com.telerik.widget.chart.visualization.common.Axis} associated with this {@link AxisModel}.
     *
     * @return the thickness of the {@link com.telerik.widget.chart.visualization.common.Axis}.
     */
    public float getLineThickness() {
        return this.getTypedValue(LINE_THICKNESS_PROPERTY_KEY, 2F);
    }

    /**
     * Sets the thickness of the {@link com.telerik.widget.chart.visualization.common.Axis} associated with this {@link AxisModel}.
     *
     * @param value the thickness of the {@link com.telerik.widget.chart.visualization.common.Axis}.
     */
    public void setLineThickness(float value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Thickness must be above zero");
        }

        this.setValue(LINE_THICKNESS_PROPERTY_KEY, value);
    }

    /**
     * Gets a {@link AxisHorizontalLocation} value determining
     * the horizontal location of the axis (rendered vertically) in relation to the plot area.
     *
     * @return the {@link AxisHorizontalLocation} value.
     */
    public AxisHorizontalLocation getHorizontalLocation() {
        return this.getTypedValue(HORIZONTAL_LOCATION_PROPERTY_KEY,
                AxisHorizontalLocation.LEFT);
    }

    /**
     * Sets a {@link AxisHorizontalLocation} value determining
     * the horizontal location of the axis (rendered vertically) in relation to the plot area.
     *
     * @param value the {@link AxisHorizontalLocation} value.
     */
    public void setHorizontalLocation(AxisHorizontalLocation value) {
        this.setValue(HORIZONTAL_LOCATION_PROPERTY_KEY, value);
    }

    /**
     * Gets a {@link AxisVerticalLocation} value determining
     * the horizontal location of the axis (rendered horizontally) in relation to the plot area.
     *
     * @return the {@link AxisVerticalLocation} value.
     */
    public AxisVerticalLocation getVerticalLocation() {
        return this.getTypedValue(VERTICAL_LOCATION_PROPERTY_KEY,
                AxisVerticalLocation.BOTTOM);
    }

    /**
     * Sets a {@link AxisVerticalLocation} value determining
     * the horizontal location of the axis (rendered horizontally) in relation to the plot area.
     *
     * @param value the {@link AxisVerticalLocation} value.
     */
    public void setVerticalLocation(AxisVerticalLocation value) {
        this.setValue(VERTICAL_LOCATION_PROPERTY_KEY, value);
    }

    /**
     * Gets an instance of the {@link AxisTickModel} class representing the first tick of the axis.
     * Can be called when the axis is loaded.
     *
     * @return the {@link AxisTickModel} instance.
     */
    public AxisTickModel getFirstTick() {
        if (this.ticks.size() > 0) {
            return this.ticks.get(0);
        }

        return null;
    }

    /**
     * Gets an instance of the {@link AxisTickModel} class representing the last tick of the axis.
     * Can be called when the axis is loaded.
     *
     * @return the {@link AxisTickModel} instance.
     */
    public AxisTickModel getLastTick() {
        if (this.ticks.size() > 1) {
            return this.ticks.get(this.ticks.size() - 1);
        }

        return null;
    }

    /**
     * Gets a value from the {@link AxisType} enum depicting the type of the axis represented
     * by this {@link AxisModel}.
     *
     * @return the {@link AxisType} value.
     */
    public AxisType getType() {
        return this.type;
    }

    /**
     * Sets a value from the {@link AxisType} enum depicting the type of the axis represented
     * by this {@link AxisModel}.
     *
     * @param value the {@link AxisType} value.
     */
    public void setType(AxisType value) {
        this.type = value;
        this.updateLayoutStrategy();
    }

    /**
     * Gets an instance of the {@link AxisTitleModel} class representing the title associated with this {@link AxisModel}
     *
     * @return the {@link AxisTitleModel} instance.
     */
    public AxisTitleModel title() {
        return this.title;
    }

    /**
     * Gets a value from the {@link AxisPlotMode} enum depicting the plot mode
     * actually used by the axis associated with this {@link AxisModel}.
     *
     * @return the {@link com.telerik.widget.chart.engine.axes.common.AxisPlotMode} value.
     */
    public AxisPlotMode getActualPlotMode() {
        return AxisPlotMode.ON_TICKS;
    }

    /**
     * Returns an instance of the {@link ElementCollection} class
     * holding all ticks associated with the current axis.
     *
     * @return the {@link ElementCollection} instance.
     */
    public ElementCollection<AxisTickModel> ticks() {
        return this.ticks;
    }

    @Override
    public void applyLayoutRounding() {
        this.layoutStrategy.applyLayoutRounding();
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == LABEL_FORMAT_PROPERTY_KEY) {
            this.labelFormat = (String) e.newValue();
        } else if (e.getKey() == LABEL_FIT_MODE_PROPERTY_KEY) {
            this.labelFitMode = (AxisLabelFitMode) e.newValue();
        }

        super.onPropertyChanged(e);
    }

    /**
     * Resets the state of the current {@link AxisModel} instance. Marks the plot data and the layout params
     * as invalid and resets the range.
     */
    public void resetState() {
        this.isPlotValid = false;
        this.isUpdated = false;
        this.isMeasureValid = false;
        this.visibleRange = new ValueRange<Double>(-1.0, -1.0);
    }

    /**
     * Called when the zoom factor of the owning chart changes.
     */
    public void onZoomChanged() {
        this.isMeasureValid = false;
    }

    /**
     * Called when the pan offset of the owning chart changes.
     */
    public void onPanOffsetChanged() {
        this.isMeasureValid = false;
    }

    /**
     * Updates the current {@link AxisModel} instance with the
     * provided {@link AxisUpdateContext}. Called by the
     * associated {@link com.telerik.widget.chart.engine.chartAreas.ChartAreaModel}.
     *
     * @param context the {@link AxisUpdateContext} used for the update.
     */
    public void update(AxisUpdateContext context) {
        if (this.isUpdated) {
            return;
        }

        this.updateCore(context);
        this.isUpdated = true;
    }

    /**
     * Measures the axis associated with this {@link AxisModel} and initializes its desired size.
     *
     * @param availableSize an instance of {@link RadSize} depicting the measure size for the axis.
     * @return <code>true</code> if a measure pass was performed, otherwise <code>false</code>.
     */
    public boolean measure(final RadSize availableSize) {
        if (this.getPresenter() == null) {
            this.desiredSize = RadSize.getEmpty();
            this.desiredArrangeRect = RadRect.getEmpty();
            return false;
        }

        if (this.lastMeasureSize == null || !this.lastMeasureSize.equals(availableSize)) {
            this.isMeasureValid = false;
        }

        if (this.isMeasureValid) {
            return false;
        }

        this.lastMeasureSize = availableSize;
        this.measureCore(availableSize);

        if (!availableSize.equals(this.desiredSize)) {
            // additional measure pass so that the axis properly fits the last visible label
            this.measureCore(availableSize);
        }
        this.isMeasureValid = true;

        return true;
    }

    /**
     * Creates an instance of the {@link AxisModelLayoutStrategy}
     * class representing the layout strategy for the axis associated with the current {@link AxisModel}.
     *
     * @return the {@link AxisModelLayoutStrategy} instance.
     */
    protected AxisModelLayoutStrategy createLayoutStrategy() {
        if (this.type == AxisType.FIRST) {
            return new HorizontalAxisLayoutStrategy(this);
        }

        return new VerticalAxisLayoutStrategy(this);
    }

    /**
     * Plots the axis elements using the provided {@link AxisUpdateContext}.
     *
     * @param context the {@link AxisUpdateContext}
     */
    public void plot(AxisUpdateContext context) {
        if (!this.isPlotValid) {
            // actual points plot
            this.plotCore(context);
            this.isPlotValid = true;
        }
    }

    /**
     * Calculates which ticks should be visible according to the provided plot bounds.
     *
     * @param clipRect an instance of the {@link RadRect} class describing the plot bounds.
     */
    public void updateTicksVisibility(final RadRect clipRect) {
        this.layoutStrategy.updateTicksVisibility(clipRect);
    }

    /**
     * Gets the key used to group series when combination mode like STACK is specified.
     * This method is used by the Chart engine and is not meant to be called by you.
     *
     * @return an object representing the combine group key.
     */
    public Object getCombineGroupKey(DataPoint point) {
        return point.collectionIndex() + 1;
    }

    public Function<Object, String> getLabelValueToStringConverter() {
        return this.labelToStringConverter;
    }

    public void setLabelValueToStringConverter(Function<Object, String> converter) {
        this.labelToStringConverter = converter;
    }

    /**
     * Gets the value for a DataPoint used by a CombineStrategy to calculate the stack sum for each stack group.
     * <p/>
     * <p/>
     * <param name="positive">Determines whether the point value instanceof positive relative to the plot origin.</param>
     */
    public StackValue getStackValue(DataPoint point) {
        StackValue returnValue = new StackValue();
        returnValue.value = 0.0F;
        returnValue.positive = true;
        return returnValue;
    }

    public boolean isDataReady() {
        return true;
    }

    /**
     * Plots all child elements of this {@link AxisModel} class.
     *
     * @param context the {@link AxisUpdateContext} object providing information needed for the plotting.
     */
    protected void plotCore(AxisUpdateContext context) {
    }

    /**
     * Updates all child elements of this {@link AxisModel} class.
     *
     * @param context the {@link AxisUpdateContext} object providing information needed for the update.
     */
    protected void updateCore(AxisUpdateContext context) {
    }

    /**
     * When implemented in inheriting classes, generates all ticks for the current axis.
     *
     * @param range the value range for which ticks need to be generated.
     * @return an {@link Iterable} instance containing the generated ticks.
     */
    protected abstract Iterable<AxisTickModel> generateTicks(final ValueRange<Double> range);

    /**
     * Generates all labels for the current axis.
     *
     * @return an {@link Iterable} instance containing the generated labels.
     */
    protected Iterable<AxisLabelModel> generateLabels() {

        ArrayList<AxisLabelModel> labels = new ArrayList<AxisLabelModel>();
        AxisPlotMode plotMode = this.getActualPlotMode();
        int labelIndex = 0;
        int startIndex = this.getLabelOffset();
        int labelStep = this.getLabelInterval();
        int skipLabelCount = 1;

        // generate label for each major tick
        for (AxisTickModel tick : this.ticks) {
            if (labelIndex < startIndex) {
                labelIndex++;
                continue;
            }

            // skip minor ticks
            if (tick.getType() == TickType.MINOR) {
                continue;
            }

            if (skipLabelCount > 1) {
                skipLabelCount--;
                continue;
            }

            // no need to process last tick if we are plotting between ticks
            if (plotMode == AxisPlotMode.BETWEEN_TICKS && RadMath.isOne(tick.normalizedValue)) {
                break;
            }

            double normalizedPosition;
            if (plotMode == AxisPlotMode.BETWEEN_TICKS) {
                double length = tick.getNormalizedForwardLength();
                if (length == 0.0) {
                    length = tick.getNormalizedBackwardLength();
                }
                normalizedPosition = tick.normalizedValue + (length / 2);
            } else {
                normalizedPosition = tick.normalizedValue;
            }

            AxisLabelModel label = new AxisLabelModel(normalizedPosition, RadPoint.getEmpty(), RadSize.getEmpty());
            tick.associatedLabel = label;

            label.setContent(getLabelContent(tick));
            labels.add(label);
            skipLabelCount = labelStep;
        }

        return labels;
    }

    public Object getLabelContent(AxisTickModel tick) {
        if (this.labelToStringConverter == null) {
            return this.getLabelContentCore(tick);
        } else {
            return this.labelToStringConverter.apply(tick);
        }
    }

    /**
     * Gets the content for the label associated with the provided {@link AxisTickModel}.
     *
     * @param tick the {@link AxisTickModel} instance identifying the tick for which to get the label content.
     * @return the label content for the tick.
     */
    protected Object getLabelContentCore(AxisTickModel tick) {
        if (this.labelFormat == null || this.labelFormat.equals("")) {
            return tick.value;
        }

        if (this.labelToStringConverter == null) {
            return String.format(this.labelFormat, tick.value);
        } else {
            return this.labelToStringConverter.apply(tick.value);
        }
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        if (this.getPresenter() == null) {
            this.desiredArrangeRect = RadRect.getEmpty();
            return rect;
        }
        RadSize availableSize = new RadSize(rect.getWidth(), rect.getHeight());
        if (!availableSize.equals(this.lastMeasureSize)) {
            this.measure(availableSize);
        }
        this.layoutStrategy.arrange(rect);
        this.desiredArrangeRect = rect;
        return rect;
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child.equals(this.title) ||
                child instanceof AxisTickModel || child instanceof AxisLabelModel) {
            return ModifyChildrenResult.ACCEPT;
        }

        return ModifyChildrenResult.REFUSE;
    }

    /**
     * Creates an instance of the {@link AxisPlotInfo} class containing the plot information about the provided
     * axis value.
     *
     * @param value the value to create the plot information for.
     * @return the {@link com.telerik.widget.chart.engine.axes.common.AxisPlotInfo} instance.
     */
    public AxisPlotInfo createPlotInfo(Object value) {
        return null;
    }

    /**
     * Calculates the value which corresponds to the provided axis coordinate.
     *
     * @param coordinate the coordinate to calculate the value for.
     * @return the calculated value.
     */
    public Object convertPhysicalUnitsToData(double coordinate) {
        return null;
    }

    public abstract AxisLabelModel generateLastLabel();

    private void updateLayoutStrategy() {
        this.layoutStrategy = this.createLayoutStrategy();
    }

    protected boolean buildTicksAndLabels(final RadSize availableSize) {
        ValueRange<Double> newVisibleRange = this.layoutStrategy.getVisibleRange(availableSize);
        if (newVisibleRange.equals(this.visibleRange)) {
            return false;
        }
        this.visibleRange = newVisibleRange;

        this.labels.clear();
        this.ticks.clear();

        this.updateTicks();
        this.updateLabels();
        return true;
    }

    private void measureCore(final RadSize availableSize) {
        this.buildTicksAndLabels(availableSize);
        for (AxisLabelModel label : this.labels) {
            // We may enter one or more additional measure passes until all axes are best fit,
            // so do not re-measure already measured labels.
            if (label.desiredSize.equals(RadSize.getEmpty())) {
                label.desiredSize = this.getPresenter().measureContent(label, label.getContent());
            }
        }
        if (this.title.desiredSize.equals(RadSize.getEmpty())) {
            this.title.desiredSize = this.getPresenter().measureContent(this, this.title.getContent());
        }

        this.desiredSize = this.layoutStrategy.getDesiredSize(availableSize);
        this.desiredMargin = this.layoutStrategy.getDesiredMargin(availableSize);
    }

    private void updateTicks() {
        AxisTickModel previous = null;
        int tickIndex = 0;
        int startIndex = this.getMajorTickOffset();

        Iterable<AxisTickModel> localTicks = this.generateTicks(this.visibleRange);
        for (AxisTickModel tick : localTicks) {
            // consider tick offset
            if (tickIndex < startIndex) {
                tickIndex++;
                continue;
            }

            tick.position = TickPosition.INNER;
            this.ticks.add(tick);
            tick.previous = previous;

            if (previous != null) {
                previous.next = tick;
            }

            previous = tick;
        }

        if (this.ticks.size() > 0) {
            this.ticks.get(0).position = TickPosition.FIRST;
        }
        if (this.ticks.size() > 1) {
            this.ticks.get(this.ticks.size() - 1).position = TickPosition.LAST;
        }
    }

    private void updateLabels() {
        if (!this.getShowLabels()) {
            return;
        }

        for (AxisLabelModel label : this.generateLabels()) {
            this.labels.add(label);
        }

        if (this.labels.size() > 1 && this.getLastLabelVisibility() == AxisLastLabelVisibility.HIDDEN) {
            this.labels.get(this.labels.size() - 1).setIsVisible(false);
        }
    }

    public class StackValue {
        public double value;
        public boolean positive;
    }
}
