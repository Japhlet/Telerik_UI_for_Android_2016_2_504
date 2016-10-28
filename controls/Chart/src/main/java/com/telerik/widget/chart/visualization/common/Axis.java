package com.telerik.widget.chart.visualization.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.telerik.android.common.Function;
import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisLabelLayoutMode;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.axes.common.AxisLastLabelVisibility;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModelWithAxes;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.visualization.common.renderers.ChartLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.List;

/**
 * This class is a base for all types of Axes on the Chart surface. This class
 * is abstract and should not be used in your application.
 */
public abstract class Axis extends ChartElementPresenter implements ChartLabelRenderer {

    public static final int AXIS_Z_INDEX = 2000;

    public static final String LINE_THICKNESS_KEY = "LineThickness";
    public static final String LINE_COLOR_KEY = "LineColor";
    public static final String LABEL_COLOR = "LabelColor";
    public static final String LABEL_SIZE_KEY = "LabelSize";
    public static final String LABEL_FONT_KEY = "LabelFont";
    public static final String LABEL_FONT_STYLE_KEY = "LabelFontStyle";
    public static final String LABEL_FIT_MODE = "LabelFitMode";
    public static final String LABEL_ROTATION_ANGLE = "LabelRotationAngle";
    public static final String TICK_COLOR_KEY = "TickColor";
    public static final String TICK_THICKNESS_KEY = "TickThickness";

    public static final int LABEL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis axis = (Axis)sender;

            axis.resolveLabelRenderer().setLabelTextColor((Integer) propertyValue);
        }
    });

    public static final int LABEL_SIZE_PROPERTY_KEY = registerProperty(Util.getDP(12), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis axis = (Axis)sender;
            axis.resolveLabelRenderer().setLabelSize((Float) propertyValue);
        }
    });

    public static final int LABEL_FONT_PROPERTY_KEY = registerProperty(null, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis axis = (Axis)sender;
            axis.resolveLabelRenderer().setLabelFont((Typeface) propertyValue);
        }
    });

    public static final int LABEL_FONT_STYLE_PROPERTY_KEY = registerProperty(null, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis axis = (Axis)sender;
            axis.resolveLabelRenderer().setLabelFontStyle((int) propertyValue);
        }
    });

    public static final int LABEL_FIT_MODE_PROPERTY_KEY = registerProperty(AxisLabelFitMode.NONE, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis axis = (CartesianAxis)sender;
            if (axis.getLabelLayoutMode() == AxisLabelLayoutMode.INNER && propertyValue != AxisLabelFitMode.NONE) {
                throw new IllegalStateException(labelFitModeExceptionMessage);
            }

            axis.labelFitMode = (AxisLabelFitMode)propertyValue;
            axis.getModel().setLabelFitMode((AxisLabelFitMode)propertyValue);
        }
    });

    public static final int LABEL_ROTATION_ANGLE_PROPERTY_KEY = registerProperty(300.0f, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis presenter = (Axis)sender;
            presenter.labelRotationAngle = (Float)propertyValue;
            float normalizedRotationAngle = presenter.labelRotationAngle % 360;
            if (normalizedRotationAngle < 0) {
                normalizedRotationAngle += 360;
            }
            presenter.getModel().setNormalizedLabelRotationAngle(normalizedRotationAngle);
        }
    });

    public static final int TICK_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis axis = (Axis)sender;

            int color = (Integer)propertyValue;
            if (axis.tickColor == color) {
                return;
            }

            axis.tickColor = color;
            axis.ticksPaint.setColor(color);
            axis.requestRender();
        }
    });

    public static final int TICK_THICKNESS_PROPERTY_KEY = registerProperty(2.0f, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            Axis axis = (Axis)sender;
            axis.getModel().setTickThickness((Float)propertyValue);
        }
    });

    private static String labelFitModeExceptionMessage = "Label fit mode is not supported when label layout mode is set to INNER.";

    private float labelRotationAngle = 300;
    private AxisLabelFitMode labelFitMode = AxisLabelFitMode.NONE;

    private AxisType type;
    private AxisModel model;

    private int tickColor = Color.BLACK;

    private Paint ticksPaint;

    private ChartLabelRenderer labelRenderer;
    protected ChartLabelRenderer defaultLabelRenderer;
    private AxisLabelLayoutMode labelLayoutMode;

    public void setVerticalWidth(int value) {
        this.getModel().setWidth(value);
    }

    public int getVerticalWidth() {
        return this.getModel().getWidth();
    }

    /**
     * Creates an instance of the {@link Axis} class with a specified context,
     * a set of styleable attributes and a default style ID.
     */
    protected Axis() {
        this.ticksPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.ticksPaint.setColor(this.tickColor);
    }

    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);

        PaletteEntry paletteEntry = palette.getEntry(this.getPaletteFamilyCore());

        if (paletteEntry == null) {
            return;
        }

        String stringValue = paletteEntry.getCustomValue(Axis.LABEL_FONT_KEY);
        if (stringValue != null) {
            this.setValue(LABEL_FONT_PROPERTY_KEY, PALETTE_VALUE, Typeface.create(stringValue, labelRenderer.getLabelFontStyle()));
        }

        stringValue = paletteEntry.getCustomValue(Axis.LABEL_FONT_STYLE_KEY);
        if (stringValue != null) {
            String lowerCase = stringValue.toLowerCase();
            int styleCode = -1;
            if (lowerCase.equals("bold")) {
                styleCode = Typeface.BOLD;
            } else if (lowerCase.equals("italic")) {
                styleCode = Typeface.ITALIC;
            } else if (lowerCase.equals("bolditalic")) {
                styleCode = Typeface.BOLD_ITALIC;
            } else {
                styleCode = Typeface.NORMAL;
            }

            this.setValue(LABEL_FONT_STYLE_PROPERTY_KEY, PALETTE_VALUE, styleCode);
        }

        stringValue = paletteEntry.getCustomValue(Axis.LABEL_SIZE_KEY);
        if (stringValue != null) {
            float fontSize = Float.parseFloat(stringValue);
            fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, fontSize, this.chart.getContext().getResources().getDisplayMetrics());
            this.setValue(LABEL_SIZE_PROPERTY_KEY, PALETTE_VALUE, fontSize);
        }

        stringValue = paletteEntry.getCustomValue(Axis.LABEL_COLOR);
        if (stringValue != null) {
            int colorCode = Color.parseColor(stringValue);
            this.setValue(LABEL_COLOR_PROPERTY_KEY, PALETTE_VALUE, colorCode);
        }

        stringValue = paletteEntry.getCustomValue(Axis.LABEL_FIT_MODE);
        if (stringValue != null) {
            String lowerCase = stringValue.toLowerCase();
            AxisLabelFitMode nativeValue;
            if (lowerCase.equals("rotate")) {
                nativeValue = AxisLabelFitMode.ROTATE;
            } else if (lowerCase.equals("multiline")) {
                nativeValue = AxisLabelFitMode.MULTI_LINE;
            } else {
                nativeValue = AxisLabelFitMode.NONE;
            }

            this.setValue(LABEL_FIT_MODE_PROPERTY_KEY, PALETTE_VALUE, nativeValue);
        }

        stringValue = paletteEntry.getCustomValue(Axis.LABEL_ROTATION_ANGLE);
        if (stringValue != null) {
            float rotationAngle = Float.parseFloat(stringValue);
            this.setValue(LABEL_ROTATION_ANGLE_PROPERTY_KEY, PALETTE_VALUE, rotationAngle);
        }

        stringValue = paletteEntry.getCustomValue(Axis.TICK_COLOR_KEY);

        if (stringValue != null) {
            int colorCode = Color.parseColor(stringValue);
            this.setValue(TICK_COLOR_PROPERTY_KEY, PALETTE_VALUE, colorCode);
        }

        stringValue = paletteEntry.getCustomValue(Axis.TICK_THICKNESS_KEY);
        if (stringValue != null) {
            float thickness = Float.parseFloat(stringValue);
            this.setValue(TICK_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, thickness);
        }
    }

    /**
     * Gets a value from the {@link com.telerik.widget.chart.engine.axes.AxisType} enum
     * determining the type of the current {@link Axis}.
     *
     * @return the type of the axis.
     */
    public AxisType getAxisType() {
        return this.type;
    }

    /**
     * Sets a value from the {@link com.telerik.widget.chart.engine.axes.AxisType} enum
     * determining the type of the current {@link Axis}.
     *
     * @param type the type of the axis.
     */
    public void setAxisType(AxisType type) {
        this.type = type;
    }

    public AxisModel getModel() {
        if (this.model == null) {
            this.model = this.createModel();
        }

        return this.model;
    }

    public abstract List<DataPoint> getDataPointsForValue(Object value);

    /**
     * Gets the rotation angle of the labels when LabelFitMode equals ROTATE.
     *
     * @return the rotation angle in degrees.
     */
    public float getLabelRotationAngle() {
        return (Float)this.getValue(LABEL_ROTATION_ANGLE_PROPERTY_KEY);
    }

    /**
     * Sets the rotation angle of the labels when LabelFitMode equals ROTATE.
     *
     * @param value the rotation angle in degrees.
     */
    public void setLabelRotationAngle(float value) {
        this.setValue(LABEL_ROTATION_ANGLE_PROPERTY_KEY, value);
    }

    /**
     * Gets a value from the {@link AxisLastLabelVisibility} enum
     * determining the strategy that defines the last axis label visibility.
     *
     * @return a value from the {@link AxisLastLabelVisibility} enum
     * determining how the last label on the axis is displayed.
     */
    public AxisLastLabelVisibility getLastLabelVisibility() {
        return this.getModel().getLastLabelVisibility();
    }

    /**
     * Sets a value from the {@link AxisLastLabelVisibility} enum
     * determining the strategy that defines the last axis label visibility.
     *
     * @param value a value from the {@link AxisLastLabelVisibility} enum
     *              determining how the last label on the axis is displayed.
     */
    public void setLastLabelVisibility(AxisLastLabelVisibility value) {
        this.getModel().setLastLabelVisibility(value);
        this.requestLayout();
    }

    /**
     * Gets index-based offset of the first major tick to be displayed.
     *
     * @return the index of the first major tick on the axis to be displayed.
     */
    public int getMajorTickOffset() {
        return this.getModel().getMajorTickOffset();
    }

    /**
     * Sets index-based offset of the first major tick to be displayed.
     *
     * @param value the index of the first major tick on the axis to be displayed.
     */
    public void setMajorTickOffset(int value) {
        this.getModel().setMajorTickOffset(value);
    }

    /**
     * Gets index-based offset of the first label to be displayed.
     *
     * @return the index of the first label on the axis to be displayed.
     */
    public int getLabelOffset() {
        return this.getModel().getLabelOffset();
    }

    /**
     * Sets index-based offset of the first label to be displayed.
     *
     * @param value the index of the first label on the axis to be displayed.
     */
    public void setLabelOffset(int value) {
        if (value < 0)
            throw new IllegalArgumentException("value cannot be negative");

        this.getModel().setLabelOffset(value);
    }

    /**
     * Gets a boolean value indicating whether labels will be displayed on this axis.
     *
     * @return <code>true</code> if labels are visible, otherwise <code>false</code>.
     */
    public boolean getShowLabels() {
        return this.getModel().getShowLabels();
    }

    /**
     * Sets a boolean value indicating whether labels will be displayed on this axis.
     *
     * @param value <code>true</code> if labels are visible, otherwise <code>false</code>.
     */
    public void setShowLabels(boolean value) {
        this.getModel().setShowLabels(value);
    }

    /**
     * Gets a value from the {@link AxisLabelFitMode} enum
     * that determines how the axis labels will be laid out when they are overlapping each other.
     *
     * @return the {@link AxisLabelFitMode} value.
     */
    public AxisLabelFitMode getLabelFitMode() {
        return (AxisLabelFitMode)this.getValue(LABEL_FIT_MODE_PROPERTY_KEY);
    }

    /**
     * Sets a value from the {@link AxisLabelFitMode} enum
     * that determines how the axis labels will be laid out when they are overlapping each other.
     *
     * @param value the {@link AxisLabelFitMode} value.
     */
    public void setLabelFitMode(AxisLabelFitMode value) {
        this.setValue(LABEL_FIT_MODE_PROPERTY_KEY, value);
    }

    /**
     * Sets the labels layout mode. The default value is OUTER. See {@link com.telerik.widget.chart.engine.axes.AxisLabelLayoutMode} for more information.
     *
     * @param value The new layout mode.
     */
    public void setLabelLayoutMode(AxisLabelLayoutMode value) {
        if (value == AxisLabelLayoutMode.INNER && this.labelFitMode != AxisLabelFitMode.NONE) {
            throw new IllegalStateException(labelFitModeExceptionMessage);
        }

        if (this.labelLayoutMode == value) {
            return;
        }

        this.labelLayoutMode = value;
        this.getModel().setLabelLayoutMode(value);
        this.requestLayout();
    }

    /**
     * Gets the label layout mode. See {@link com.telerik.widget.chart.engine.axes.AxisLabelLayoutMode} for more information.
     */
    public AxisLabelLayoutMode getLabelLayoutMode() {
        return this.labelLayoutMode;
    }

    /**
     * Gets the thickness of a single tick present on the axis in pixels.
     *
     * @return the thickness of a tick in pixels.
     */
    public float getTickThickness() {
        return (Float)this.getValue(TICK_THICKNESS_PROPERTY_KEY);
    }

    /**
     * Sets the thickness of a single tick present on the axis in pixels.
     *
     * @param value the thickness of a tick in pixels.
     */
    public void setTickThickness(float value) {
        this.setValue(TICK_THICKNESS_PROPERTY_KEY, value);
    }

    /**
     * Sets the color used to display the axis ticks.
     *
     * @param color the color for the ticks.
     */
    public void setTickColor(int color) {
        this.setValue(TICK_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets the color used to display the axis ticks.
     *
     * @return the color for the ticks.
     */
    public int getTickColor() {
        return (Integer)this.getValue(TICK_COLOR_PROPERTY_KEY);
    }

    /**
     * Gets an integer determining the step between two visible ticks on the axis.
     *
     * @return the amount of the omitted ticks.
     */
    public int getLabelInterval() {
        return this.getModel().getLabelInterval();
    }

    /**
     * Sets an integer determining the step between two visible labels on the axis.
     *
     * @param value the amount of the omitted labels.
     */
    public void setLabelInterval(int value) {
        Axis presenter = this;
        presenter.getModel().setLabelInterval(value);
    }

    /**
     * Gets the {@link ChartLabelRenderer} responsible for drawing the axis labels.
     *
     * @return A {@link ChartLabelRenderer} instance.
     */
    public ChartLabelRenderer getLabelRenderer() {
        if (this.labelRenderer == null)
            this.labelRenderer = createDefaultLabelRenderer();

        return this.labelRenderer;
    }

    /**
     * Sets a {@link ChartLabelRenderer} object that will be responsible for drawing the axis labels.
     *
     * @param value the new label renderer.
     */
    public void setLabelRenderer(ChartLabelRenderer value) {
        if (this.labelRenderer == value) {
            return;
        }

        this.labelRenderer = value;
        this.requestRender();
    }

    @Override
    protected String defaultPaletteFamily() {
        if (this.getAxisType() == AxisType.FIRST) {
            return ChartPalette.HORIZONTAL_AXIS_FAMILY;
        }
        return ChartPalette.VERTICAL_AXIS_FAMILY;
    }

    @Override
    protected ChartElement getElement() {
        return this.getModel();
    }

    @Override
    protected int getDefaultZIndex() {
        return AXIS_Z_INDEX;
    }

    public ChartLayoutContext getLastLayoutContext() {
        return this.lastLayoutContext;
    }

    /**
     * Called when the {@link AxisModel} instance associated with this
     * {@link Axis} needs to be created.
     *
     * @return the {@link AxisModel} associated with this {@link Axis}.
     */
    protected abstract AxisModel createModel();

    @Override
    protected void onAttached() {
        super.onAttached();
        ((ChartAreaModelWithAxes) this.chart.chartAreaModel()).setAxis(this.getModel(), this.type);
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        this.getModel().getLabels().clear();
        this.getModel().ticks().clear();

        super.onDetached(oldChart);

        ((ChartAreaModelWithAxes) oldChart.chartAreaModel()).removeAxis(this.getModel());
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        this.arrangeVisuals(canvas, this.lastLayoutContext);
    }

    /**
     * Gets the label renderer if set and if not, returns the main implementation.
     */
    public ChartLabelRenderer resolveLabelRenderer() {
        if (this.labelRenderer != null) {
            return this.labelRenderer;
        } else {
            if (this.defaultLabelRenderer == null) {
                this.defaultLabelRenderer = this.createDefaultLabelRenderer();
            }
        }

        return this.defaultLabelRenderer;
    }

    public RadRect getLayoutSlot(ChartNode node, ChartLayoutContext context) {
        RadRect layoutSlot = node.getLayoutSlot();
        double x = layoutSlot.getX();
        double y = layoutSlot.getY();

        if (this.type == AxisType.FIRST) {
            x = (layoutSlot.getX() + context.panOffset().getX());
        } else {
            y = (layoutSlot.getY() + context.panOffset().getY());
        }

        return new RadRect(x, y, layoutSlot.getWidth(), layoutSlot.getHeight());
    }

    private void arrangeVisuals(Canvas canvas, ChartLayoutContext context) {
        this.updateTicks(canvas, context);
        this.updateLabels(canvas);
    }

    private void updateTicks(Canvas canvas, ChartLayoutContext context) {
        for (AxisTickModel tick : this.getModel().ticks()) {
            if (!tick.isVisible()) {
                continue;
            }

            RadRect layoutSlot = this.getLayoutSlot(tick, context);

            canvas.drawRect(Math.round(layoutSlot.getX()), Math.round(layoutSlot.getY()), Math.round(layoutSlot.getRight()), Math.round(layoutSlot.getBottom()), this.ticksPaint);
        }
    }

    private void updateLabels(Canvas canvas) {
        ChartLabelRenderer renderer = this.resolveLabelRenderer();
        if (renderer == null) {
            return;
        }

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(Util.convertToRectF(getModel().chartArea().getLayoutSlot()));
        for (AxisLabelModel label : this.getModel().getLabels()) {
            if (!label.isVisible()) {
                continue;
            }

            renderer.renderLabel(canvas, label);
        }
        canvas.restore();
    }

    protected ChartLabelRenderer createDefaultLabelRenderer() {
        return null;
    }

    @Override
    public void renderLabel(Canvas canvas, ChartNode dataPoint) {
        this.resolveLabelRenderer().renderLabel(canvas, dataPoint);
    }

    @Override
    public int getLabelTextColor() {
        return (Integer)this.getValue(LABEL_COLOR_PROPERTY_KEY);
    }

    @Override
    public void setLabelTextColor(int color) {
        this.setValue(CartesianAxis.LABEL_COLOR_PROPERTY_KEY, color);
    }

    @Override
    public float getLabelSize() {
        return (Float)this.getValue(LABEL_SIZE_PROPERTY_KEY);
    }

    @Override
    public void setLabelSize(float value) {
        this.setValue(LABEL_SIZE_PROPERTY_KEY, value);
    }

    @Override
    public Typeface getLabelFont() {
        return (Typeface)this.getValue(LABEL_FONT_PROPERTY_KEY);
    }

    @Override
    public void setLabelFont(Typeface value) {
        this.setValue(LABEL_FONT_PROPERTY_KEY, value);
    }

    @Override
    public int getLabelFontStyle() {
        return (int)this.getValue(LABEL_FONT_STYLE_PROPERTY_KEY);
    }

    @Override
    public void setLabelFontStyle(int value) {
        this.setValue(LABEL_FONT_STYLE_PROPERTY_KEY, value);
    }

    @Override
    public String getLabelFormat() {
        return this.resolveLabelRenderer().getLabelFormat();
    }

    @Override
    public void setLabelFormat(String format) {
        this.resolveLabelRenderer().setLabelFormat(format);
    }

    @Override
    public float getLabelMargin() {
        return this.resolveLabelRenderer().getLabelMargin();
    }

    @Override
    public void setLabelMargin(float offset) {
        this.resolveLabelRenderer().setLabelMargin(offset);
    }

    @Override
    public Function<Object, String> getLabelValueToStringConverter() {
        return this.resolveLabelRenderer().getLabelValueToStringConverter();
    }

    @Override
    public void setLabelValueToStringConverter(Function<Object, String> converter) {
        this.resolveLabelRenderer().setLabelValueToStringConverter(converter);
    }
}
