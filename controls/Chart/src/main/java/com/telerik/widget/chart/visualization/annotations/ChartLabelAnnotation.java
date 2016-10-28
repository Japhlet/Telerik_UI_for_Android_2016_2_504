package com.telerik.widget.chart.visualization.annotations;

import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.palettes.PaletteEntry;

public abstract class ChartLabelAnnotation extends ChartAnnotation {
    private ChartAnnotationLabelUpdateContext lastLabelContext;
    private String label;
    private final TextPaint labelPaint = new TextPaint();
    private float labelSize;

    public static final int LABEL_FORMAT_PROPERTY_KEY = registerProperty("", new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
        }
    });

    public static final int LABEL_LOCATION_PROPERTY_KEY = registerProperty("", new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
        }
    });

    public static final int HORIZONTAL_ALIGNMENT_PROPERTY_KEY = registerProperty("", new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
        }
    });

    public static final int VERTICAL_ALIGNMENT_PROPERTY_KEY = registerProperty("", new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
        }
    });

    public static final int HORIZONTAL_OFFSET_PROPERTY_KEY = registerProperty("", new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
        }
    });

    public static final int VERTICAL_OFFSET_PROPERTY_KEY = registerProperty("", new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
        }
    });

    /**
     * Creates a new instance of the {@link ChartLabelAnnotation} class.
     */
    public ChartLabelAnnotation() {
        this.setLabelSize(Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 12));
    }

    /**
     * Gets a string representing the format used to display the annotation.
     *
     * @return the format string.
     */
    public String getLabelFormat() {
        return (String)this.getValue(LABEL_FORMAT_PROPERTY_KEY);
    }

    /**
     * Sets the format string used to display the annotation.
     *
     * @param value the format string.
     */
    public void setLabelFormat(String value) {
        this.setValue(LABEL_FORMAT_PROPERTY_KEY, value);
    }

    /**
     * Gets a value from the {@link com.telerik.widget.chart.visualization.annotations.ChartAnnotationLabelLocation}
     * enum describing the location of the annotation related to its parent.
     *
     * @return the annotation location.
     */
    public ChartAnnotationLabelLocation getLabelLocation() {
        return (ChartAnnotationLabelLocation)this.getValue(LABEL_LOCATION_PROPERTY_KEY);
    }

    /**
     * Sets a value from the {@link com.telerik.widget.chart.visualization.annotations.ChartAnnotationLabelLocation}
     * enum determining the location of the annotation relatively to its parent.
     *
     * @param value the annotation location to set.
     */
    public void setLabelLocation(ChartAnnotationLabelLocation value) {
        this.setValue(LABEL_LOCATION_PROPERTY_KEY, value);
    }

    /**
     * Gets a value from the {@link com.telerik.widget.chart.visualization.annotations.HorizontalAlignment} enum
     * determining the way the annotation is aligned horizontally in its bounds.
     *
     * @return the horizontal alignment.
     */
    public HorizontalAlignment getLabelHorizontalAlignment() {
        return (HorizontalAlignment)this.getValue(HORIZONTAL_ALIGNMENT_PROPERTY_KEY);
    }

    /**
     * Sets a value from the {@link com.telerik.widget.chart.visualization.annotations.HorizontalAlignment} enum
     * determining the way the annotation is aligned horizontally in its bounds.
     *
     * @param value the horizontal alignment.
     */
    public void setLabelHorizontalAlignment(HorizontalAlignment value) {
        this.setValue(HORIZONTAL_ALIGNMENT_PROPERTY_KEY, value);
    }

    /**
     * Gets a value from the {@link com.telerik.widget.chart.visualization.annotations.VerticalAlignment} enum
     * determining the way the annotation is aligned vertically in its bounds.
     *
     * @return the vertical alignment.
     */
    public VerticalAlignment getLabelVerticalAlignment() {
        return (VerticalAlignment)this.getValue(VERTICAL_ALIGNMENT_PROPERTY_KEY);
    }

    /**
     * Sets a value from the {@link com.telerik.widget.chart.visualization.annotations.VerticalAlignment} enum
     * determining the way the annotation is aligned vertically in its bounds.
     *
     * @param value the vertical alignment.
     */
    public void setLabelVerticalAlignment(VerticalAlignment value) {
        this.setValue(VERTICAL_ALIGNMENT_PROPERTY_KEY, value);
    }

    /**
     * Gets a boolean value determining the horizontal offset of the annotation relatively to its
     * original bounds.
     *
     * @return the horizontal offset.
     */
    public double getLabelHorizontalOffset() {
        return (double)this.getValue(HORIZONTAL_OFFSET_PROPERTY_KEY);
    }

    /**
     * Sets a boolean value determining the horizontal offset of the annotation relatively to its
     * original bounds.
     *
     * @param value the horizontal offset.
     */
    public void setLabelHorizontalOffset(double value) {
        this.setValue(HORIZONTAL_OFFSET_PROPERTY_KEY, value);
    }

    /**
     * Gets a boolean value determining the vertical offset of the annotation relatively to its
     * original bounds.
     *
     * @return the vertical offset.
     */
    public double getLabelVerticalOffset() {
        return (double)this.getValue(VERTICAL_OFFSET_PROPERTY_KEY);
    }

    /**
     * Sets a boolean value determining the vertical offset of the annotation relatively to its
     * original bounds.
     *
     * @param value the vertical offset.
     */
    public void setLabelVerticalOffset(double value) {
        this.setValue(VERTICAL_OFFSET_PROPERTY_KEY, value);
    }

    /**
     * Gets the current label.
     *
     * @return the current label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the current label.
     *
     * @param value the new label.
     */
    public void setLabel(String value) {
        this.label = value;
        this.requestLayout();
    }

    /**
     * Gets the label size in pixels.
     */
    public float getLabelSize() {
        return this.labelSize;
    }

    /**
     * Sets the label size in pixels.
     *
     * @param value The new label size.
     */
    public void setLabelSize(float value) {
        if (this.labelSize == value) {
            return;
        }

        this.labelSize = value;
        this.labelPaint.setTextSize(value);
        this.requestRender();
    }

    @Override
    protected void drawCore(Canvas canvas) {
        if (this.lastLabelContext == null) {
            return;
        }

        Object labelContent = this.getLabelContent();
        if (labelContent == null)
            return;

        drawLabel(canvas, labelContent);
    }

    private void drawLabel(Canvas canvas, Object labelContent) {
        String labelString = labelContent.toString();

        double width = this.labelPaint.measureText(labelString);
        StaticLayout textSize = new StaticLayout(labelString, 0, labelString.length(), this.labelPaint, (int)Math.round(width), Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, false);

        RadRect slot = this.getLabelSlot(textSize, this.lastLabelContext);

        canvas.drawText(labelContent.toString(), (float) slot.getX(), (float) slot.getY(), this.labelPaint);
    }

    private void processLabel(ChartAnnotationLabelUpdateContext context) {
        this.lastLabelContext = context;
    }

    @Override
    protected void updatePresenters() {
        this.processLabel(new ChartAnnotationLabelUpdateContext(this.getModel().getLayoutSlot()));
    }

    private RadRect getLabelSlot(StaticLayout labelSize, ChartAnnotationLabelUpdateContext context) {
        double x = this.getLabelSlotX(context, labelSize);
        double y = this.getLabelSlotY(context, labelSize);

        return new RadRect(x, y, labelSize.getWidth(), labelSize.getHeight());
    }

    private double getLabelSlotX(ChartAnnotationLabelUpdateContext context, StaticLayout labelSize) {
        ChartAnnotationLabelLocation location = this.getLabelLocation();
        RadRect annotationSlot = context.layoutSlot;
        HorizontalAlignment horizontalAlignment = this.getLabelHorizontalAlignment();
        double horizontalOffset = this.getLabelHorizontalOffset();
        double x = context.location.getX();

        switch (location) {
            case LEFT: {
                x = annotationSlot.getX() - labelSize.getWidth();
                break;
            }
            case RIGHT: {
                x = annotationSlot.getRight();
                break;
            }
            case TOP:
            case BOTTOM: {
                switch (horizontalAlignment) {
                    case LEFT:
                        x = annotationSlot.getX();
                        break;
                    case CENTER:
                        x = annotationSlot.getX() + ((annotationSlot.getWidth() - labelSize.getWidth()) / 2);
                        break;
                    case RIGHT:
                        x = annotationSlot.getRight() - labelSize.getWidth();
                        break;
                }
                break;
            }
            case INSIDE: {
                switch (horizontalAlignment) {
                    case LEFT:
                        x = annotationSlot.getX();
                        break;
                    case CENTER:
                        x = annotationSlot.getX() + ((annotationSlot.getWidth() - labelSize.getWidth()) / 2);
                        break;
                    case RIGHT:
                        x = annotationSlot.getRight() - labelSize.getWidth();
                        break;
                }
                break;
            }
        }

        x += horizontalOffset;
        return x;
    }

    private double getLabelSlotY(ChartAnnotationLabelUpdateContext context, StaticLayout labelSize) {
        ChartAnnotationLabelLocation location = this.getLabelLocation();
        RadRect annotationSlot = context.layoutSlot;
        VerticalAlignment verticalAlignment = this.getLabelVerticalAlignment();
        double verticalOffset = this.getLabelVerticalOffset();
        double y = context.location.getY();

        switch (location) {
            case TOP: {
                y = annotationSlot.getY() - labelSize.getHeight();
                break;
            }
            case BOTTOM: {
                y = annotationSlot.getBottom();
                break;
            }
            case LEFT:
            case RIGHT: {
                switch (verticalAlignment) {
                    case TOP:
                        y = annotationSlot.getY();
                        break;
                    case CENTER:
                        y = annotationSlot.getY() + ((annotationSlot.getHeight() - labelSize.getHeight()) / 2);
                        break;
                    case BOTTOM:
                        y = annotationSlot.getBottom() - labelSize.getHeight();
                        break;
                }
                break;
            }
            case INSIDE: {
                switch (verticalAlignment) {
                    case TOP:
                        y = annotationSlot.getY();
                        break;
                    case CENTER:
                        y = annotationSlot.getY() + ((annotationSlot.getHeight() - labelSize.getHeight()) / 2);
                        break;
                    case BOTTOM:
                        y = annotationSlot.getBottom() - labelSize.getHeight();
                        break;
                }
                break;
            }
        }

        y += verticalOffset + labelSize.getLineBaseline(0);
        return y;
    }

    private Object getLabelContent() {
        Object label = this.getLabel();

        String format = this.getLabelFormat();
        if (label != null && format != null && !format.equals("")) {
            return String.format(this.getLabelFormat(), label);
        }

        return label;
    }

    @Override
    protected void processPaletteEntry(PaletteEntry entry) {
        this.setValue(VERTICAL_OFFSET_PROPERTY_KEY, PALETTE_VALUE, Double.parseDouble(entry.getCustomValue("LabelVerticalOffset", 0.0)));
        this.setValue(HORIZONTAL_OFFSET_PROPERTY_KEY, PALETTE_VALUE, Double.parseDouble(entry.getCustomValue("LabelHorizontalOffset", 0.0)));
        this.setValue(VERTICAL_ALIGNMENT_PROPERTY_KEY, PALETTE_VALUE, VerticalAlignment.valueOf(entry.getCustomValue("LabelVerticalAlignment", VerticalAlignment.TOP)));
        this.setValue(HORIZONTAL_ALIGNMENT_PROPERTY_KEY, PALETTE_VALUE, HorizontalAlignment.valueOf(entry.getCustomValue("LabelHorizontalAlignment", HorizontalAlignment.RIGHT)));
        this.setValue(LABEL_LOCATION_PROPERTY_KEY, PALETTE_VALUE, ChartAnnotationLabelLocation.valueOf(entry.getCustomValue("LabelLocation", ChartAnnotationLabelLocation.INSIDE)));
        this.setValue(LABEL_FORMAT_PROPERTY_KEY, PALETTE_VALUE, entry.getCustomValue("LabelFormat"));
    }
}
