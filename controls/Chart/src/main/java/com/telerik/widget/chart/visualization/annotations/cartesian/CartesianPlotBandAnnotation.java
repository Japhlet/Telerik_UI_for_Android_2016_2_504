package com.telerik.widget.chart.visualization.annotations.cartesian;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.plotBand.CartesianPlotBandAnnotationModel;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Represents a {@link CartesianStrokedAnnotation} that is used to display a rectangular shaped
 * annotation in cartesian charts.
 */
public class CartesianPlotBandAnnotation extends CartesianStrokedAnnotation {
    private CartesianPlotBandAnnotationModel model;
    private Paint plotBandPaint;
    private Object to;
    private Object from;

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianPlotBandAnnotation cpb = (CartesianPlotBandAnnotation)sender;
            cpb.plotBandPaint.setColor(cpb.getFillColor());
            cpb.requestRender();
        }
    });

    /**
     * Creates an instance of the {@link CartesianPlotBandAnnotation} using a specified
     * {@link Context}, an axis on which the annotation will be placed on, a value stating where the
     * annotation will begin, a value stating where the annotation will end, a set of styleable
     * attributes and a default style.
     *
     * @param axis the axis on which the annotation will be placed.
     * @param from the value stating where the annotation will begin.
     * @param to   the value stating where the annotation will end.
     * @see CartesianAxis
     */
    public CartesianPlotBandAnnotation(CartesianAxis axis, Object from, Object to) {
        super(axis);

        this.model = new CartesianPlotBandAnnotationModel();
        this.plotBandPaint = new Paint();
        this.plotBandPaint.setStyle(Paint.Style.FILL);

        this.setAxis(axis);
        this.setFrom(from);
        this.setTo(to);
    }

    /**
     * Gets the current starting point of the annotation.
     *
     * @return from.
     */
    public Object getFrom() {
        return this.from;
    }

    /**
     * Sets the current starting point of the annotation.
     *
     * @param value the new from.
     */
    public void setFrom(Object value) {

        if (value == null)
            throw new IllegalArgumentException("value cannot be null");

        if (value.equals(this.from)) {
            return;
        }

        if (value.equals(this.to)) {
            throw new IllegalArgumentException("value for from must be different than the value of to");
        }

        this.from = value;
        this.model.setFrom(value);

        this.requestLayout();
    }

    /**
     * Gets the current ending point of the annotation.
     *
     * @return the current to.
     */
    public Object getTo() {
        return this.to;
    }

    /**
     * Sets the current ending point of the annotation.
     *
     * @param value the new to.
     */
    public void setTo(Object value) {

        if (value == null)
            throw new IllegalArgumentException("value cannot be null");

        if (value.equals(this.to)) {
            return;
        }

        if (value.equals(this.from)) {
            throw new IllegalArgumentException("value for to must be different than the value of from");
        }

        this.to = value;
        this.model.setTo(value);

        this.requestLayout();
    }

    /**
     * Gets the current color index that specifies how the shape's interior is painted.
     *
     * @return the current color index.
     */
    public int getFillColor() {
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the current color index that specifies how the shape's interior is painted.
     *
     * @param value the new color index.
     */
    public void setFillColor(int value) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, value);
    }

    @Override
    public ChartAnnotationModel getModel() {
        return this.model;
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.CARTESIAN_PLOT_BAND_ANNOTATION;
    }

    @Override
    protected void drawCore(Canvas canvas) {
        if (!this.getModel().isUpdated()) {
            return;
        }

        RectF rect = Util.convertToRectF(this.model.getLayoutSlot());
        canvas.drawRect(rect, this.plotBandPaint);
        canvas.drawRect(rect, this.strokePaint);

        super.drawCore(canvas);
    }

    @Override
    protected void processPaletteEntry(PaletteEntry entry) {
        super.processPaletteEntry(entry);

        this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
    }

    @Override
    protected boolean isStrokeInset() {
        return true;
    }
}
