package com.telerik.widget.chart.visualization.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.common.AxisHorizontalLocation;
import com.telerik.widget.chart.engine.axes.common.AxisVerticalLocation;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * This class is a base class for all axes that draw a line.
 * This class is abstract and should not be used in your application.
 */
public abstract class LineAxis extends Axis {

    public static final int LINE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            LineAxis axis = (LineAxis)sender;
            int lineColor = (Integer)propertyValue;
            if (axis.lineColor == lineColor) {
                return;
            }
            axis.lineColor = lineColor;
            axis.linePaint.setColor(lineColor);
            axis.requestRender();
        }
    });

    public static final int LINE_THICKNESS_PROPERTY_KEY = registerProperty(2.0f, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            LineAxis axis = (LineAxis)sender;
            float value = (Float)propertyValue;
            if (value < 0)
                throw new IllegalArgumentException("value cannot be negative");

            axis.getModel().setLineThickness(value);
            axis.linePaint.setStrokeWidth(value);
            axis.requestRender();
        }
    });

    private Paint linePaint;
    private int lineColor = Color.BLACK;
    private float[] lineDashArray = null;
    private boolean showLine = true;

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.common.LineAxis} class
     * with a specified activity context, a set of styleable attributes and the ID of the default style.
     */
    protected LineAxis() {
        this.linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.linePaint.setColor(this.lineColor);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeWidth(this.getLineThickness());
    }

    /**
     * Gets the line thickness of the of the axis in pixels.
     *
     * @return the thickness in pixels.
     */
    public float getLineThickness() {
        return (Float)this.getValue(LINE_THICKNESS_PROPERTY_KEY);
    }

    /**
     * Sets the line thickness of the axis in pixels.
     *
     * @param value the thickness in pixels.
     */
    public void setLineThickness(float value) {
        this.setValue(LINE_THICKNESS_PROPERTY_KEY, value);
    }

    /**
     * Gets a value that determines whether the axis will draw its line.
     */
    public boolean getShowLine() {
        return showLine;
    }

    /**
     * Sets a value that determines whether the axis will draw its line.
     */
    public void setShowLine(boolean value) {
        if(value == this.showLine) {
            return;
        }

        this.showLine = value;
        this.requestRender();
    }

    /**
     * Gets the color used to draw the axis' line.
     *
     * @return the integer representation of the color.
     */
    public int getLineColor() {
        return (Integer)this.getValue(LINE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the color used to draw the axis' line.
     *
     * @param lineColor the integer representation of the color.
     */
    public void setLineColor(int lineColor) {
        this.setValue(LINE_COLOR_PROPERTY_KEY, lineColor);
    }

    /**
     * Gets the pattern used when drawing axis line.
     *
     * @return an array of float values determining the dash pattern for the axis line.
     */
    public float[] getLineDashArray() {
        return this.lineDashArray;
    }

    /**
     * Sets the pattern used when drawing axis line.
     *
     * @param value an array of float values determining the dash pattern for the axis line.
     */
    public void setLineDashArray(float[] value) {
        this.lineDashArray = value;
        DashPathEffect pathEffect = null;
        if (value != null) {
            pathEffect = new DashPathEffect(value, 0);
        }
        this.linePaint.setPathEffect(pathEffect);
        this.requestRender();
    }

    @Override
    public void render(Canvas canvas) {

        if(this.showLine) {
            this.drawLine(canvas);
        }
        super.render(canvas);
    }

    private void drawLine(Canvas canvas) {
        float startX;
        float startY;
        float endX;
        float endY;

        AxisModel model = this.getModel();

        RadRect layoutSlot = this.getModel().getLayoutSlot();
        if (this.getAxisType() == AxisType.FIRST) {
            if (model.getVerticalLocation() == AxisVerticalLocation.BOTTOM) {
                startY = Math.round(layoutSlot.getY() + this.getLineThickness() / 2);
            } else {
                startY = Math.round((layoutSlot.getY() + layoutSlot.getHeight()) - this.getLineThickness() / 2);
            }

            startX = Math.round(layoutSlot.getX());
            endX = Math.round(layoutSlot.getRight());
            endY = startY;
        } else {
            if (model.getHorizontalLocation() == AxisHorizontalLocation.LEFT) {
                startX = Math.round(layoutSlot.getRight() - (getLineThickness() / 2));
            } else {
                startX = Math.round(layoutSlot.getX() + (getLineThickness() / 2));
            }

            startY = Math.round(layoutSlot.getY());
            endX = startX;
            endY = Math.round(layoutSlot.getBottom());
        }

        PathEffect effect = linePaint.getPathEffect();
        if (effect != null) {
            Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(endX, endY);

            canvas.drawPath(path, linePaint);
        } else {
            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);

        PaletteEntry paletteEntry = palette.getEntry(this.getPaletteFamilyCore());

        if (paletteEntry == null) {
            return;
        }

        String stringValue = paletteEntry.getCustomValue(Axis.LINE_COLOR_KEY);
        if (stringValue != null) {
            int lineColor = Color.parseColor(stringValue);
            this.setValue(LINE_COLOR_PROPERTY_KEY, PALETTE_VALUE, lineColor);
        }

        stringValue = paletteEntry.getCustomValue(Axis.LINE_THICKNESS_KEY);
        if (stringValue != null) {
            float lineThickness = Float.parseFloat(stringValue);
            this.setValue(LINE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, lineThickness);
        }
    }
}

