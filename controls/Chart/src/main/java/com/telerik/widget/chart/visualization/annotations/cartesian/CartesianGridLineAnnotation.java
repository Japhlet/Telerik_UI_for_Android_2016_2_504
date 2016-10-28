package com.telerik.widget.chart.visualization.annotations.cartesian;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.line.CartesianGridLineAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.line.GridLineAnnotationModel;
import com.telerik.widget.chart.visualization.annotations.ChartAnnotationLabelLocation;
import com.telerik.widget.chart.visualization.annotations.HorizontalAlignment;
import com.telerik.widget.chart.visualization.annotations.VerticalAlignment;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.palettes.ChartPalette;

/**
 * Allows you to place a straight line at a specific place in your
 * {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 */
public class CartesianGridLineAnnotation extends CartesianStrokedAnnotation {
    private CartesianGridLineAnnotationModel model;
    private Object value;

    /**
     * Creates an instance of the {@link CartesianGridLineAnnotation} using a specified
     * {@link Context}, a {@link CartesianAxis} on which the annotation will be placed, a value that
     * determines the position of the annotation, styleable attributes and a default style.
     *
     * @param axis  the axis on which the annotation will be placed.
     * @param value the value that determines the position of the annotation along the given axis.
     */
    public CartesianGridLineAnnotation(CartesianAxis axis, Object value) {
        super(axis);

        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        this.model = new CartesianGridLineAnnotationModel();
        this.setLabelLocation(ChartAnnotationLabelLocation.INSIDE);
        this.setLabelVerticalAlignment(VerticalAlignment.TOP);
        this.setLabelHorizontalAlignment(HorizontalAlignment.RIGHT);
        this.setAxis(axis);
        this.setValue(value);
    }

    /**
     * Gets the current value.
     *
     * @return the current value.
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Sets the current value.
     *
     * @param value the new value.
     */
    public void setValue(Object value) {

        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (value.equals(this.value)) {
            return;
        }

        this.value = value;
        this.model.setValue(GridLineAnnotationModel.VALUE_PROPERTY_KEY, value);

        this.requestLayout();
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.CARTESIAN_GRID_LINE_ANNOTATION;
    }

    @Override
    public ChartAnnotationModel getModel() {
        return this.model;
    }

    @Override
    protected void drawCore(Canvas canvas) {
        RectF slot = Util.convertToRectF(this.model.getLayoutSlot());

        float x = slot.left;
        float y = slot.top;
        if (this.getAxis().getModel().getType() == AxisType.SECOND) {
            canvas.drawRect(x, y, slot.right, (y + this.getStrokeWidth()), this.strokePaint);
        } else {
            canvas.drawRect(x, y, (x + this.getStrokeWidth()), slot.bottom, this.strokePaint);
        }

        super.drawCore(canvas);
    }
}
