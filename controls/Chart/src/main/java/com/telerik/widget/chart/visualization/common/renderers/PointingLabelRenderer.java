package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Path;
import android.graphics.Rect;
import android.util.TypedValue;

import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.visualization.common.ChartSeries;

/**
 * Label renderer that adds a pointer as part of the standard label shape.
 */
public class PointingLabelRenderer extends BaseLabelRenderer {

    /**
     * The length of the pointer measured from the corresponding side of the label shape.
     */
    protected float pointerLength;

    /**
     * The width of the pointer at its base.
     */
    protected float pointerWidth;

    /**
     * Creates a new instance of the {@link PointingLabelRenderer} class.
     *
     * @param owner the chart series that own the current renderer instance.
     * @see BaseLabelRenderer
     */
    public PointingLabelRenderer(ChartSeries owner) {
        this(owner, Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5), Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 7));
    }

    /**
     * Creates a new instance of the {@link PointingLabelRenderer} class.
     *
     * @param owner         the chart series that own the current renderer instance.
     * @param pointerLength the length of the pointer.
     * @param pointerWidth  the width of the pointer.
     */
    public PointingLabelRenderer(ChartSeries owner, float pointerLength, float pointerWidth) {
        super(owner);

        setPointerLength(pointerLength);
        setPointerWidth(pointerWidth);
    }

    /**
     * Gets the length of the label pointer measured from its base to the end point.
     *
     * @return the current pointer length.
     */
    public float getPointerLength() {
        return this.pointerLength;
    }

    /**
     * Sets the length of the label pointer measured from its base to the end point.
     *
     * @param pointerLength the new pointer length.
     */
    public void setPointerLength(float pointerLength) {
        if (pointerLength < 0)
            throw new IllegalArgumentException("pointerLength cannot be a negative value");

        this.pointerLength = pointerLength;
    }

    /**
     * Gets the width of the label pointer measured from its base.
     *
     * @return the current pointer width.
     */
    public float getPointerWidth() {
        return this.pointerWidth;
    }

    /**
     * Sets the width of the label pointer measured from its base.
     *
     * @param pointerWidth the new pointer width.
     */
    public void setPointerWidth(float pointerWidth) {
        if (pointerWidth < 0)
            throw new IllegalArgumentException("pointerWidth cannot be a negative value");
        this.pointerWidth = pointerWidth;
    }

    @Override
    protected float offsetBottom() {
        return super.offsetBottom() + this.pointerLength;
    }

    @Override
    protected void prepareLabel(Path path, Rect labelBounds, RadRect dataPointSlot) {
        double pointerX = (int) ((dataPointSlot.getX() + (dataPointSlot.getWidth() / 2)));

        if (pointerX < labelBounds.left)
            pointerX = labelBounds.left;
        else if (pointerX > labelBounds.right)
            pointerX = labelBounds.right;

        path.moveTo(labelBounds.left, labelBounds.top);
        path.lineTo(labelBounds.right, labelBounds.top);
        path.lineTo(labelBounds.right, labelBounds.bottom);

        preparePointer(path, pointerX, labelBounds.bottom, pointerX <= labelBounds.centerX());

        path.lineTo(labelBounds.left, labelBounds.bottom);
        path.lineTo(labelBounds.left, labelBounds.top);
    }

    /**
     * This method defines the points of a label pointer using a given predefined path, coordinates for the base of the pointer
     * and whether it positioned on the left or right side of the label background.
     *
     * @param path   the predefined path already holding some points defining the label background or a part of it.
     * @param x      the x coordinate of the pointer base.
     * @param y      the y coordinate of the pointer base.
     * @param isLeft the value stating with <code>true</code> if the base of the label is in the center or on the left of it, <code>false</code> if it is on the right side of the center.
     */
    protected void preparePointer(Path path, double x, double y, boolean isLeft) {
        float pointerBottomPoint = (float) (y + this.pointerLength);
        float pointerOffsetXPoint = (float) (x + (isLeft ? this.pointerWidth : -(this.pointerWidth)));

        if (isLeft) {
            path.lineTo(pointerOffsetXPoint, (float) y);
            path.lineTo((float) x, pointerBottomPoint);
            path.lineTo((float) x, (float) y);
        } else {
            path.lineTo((float) x, (float) y);
            path.lineTo((float) x, pointerBottomPoint);
            path.lineTo(pointerOffsetXPoint, (float) y);
        }
    }
}
