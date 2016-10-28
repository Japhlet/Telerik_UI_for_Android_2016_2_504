package com.telerik.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Used to render the grid lines for the elements that request it.
 */
public class GridLinesLayer {

    /**
     * The current paint to be used in rendering the lines.
     */
    protected Paint paint;

    float halfLineWidth;

    /**
     * Creates a new instance of the {@link GridLinesLayer} class.
     */
    public GridLinesLayer() {
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Gets the current paint used to render the grid lines.
     *
     * @return the current paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Used to set the current paint used to render the grid lines.
     *
     * @param paint the new grid lines paint.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Gets the getWidth of the grid line.
     *
     * @return the current grid line width.
     */
    public float getWidth() {
        return this.paint.getStrokeWidth();
    }

    /**
     * Sets the getWidth of the grid line.
     *
     * @param width the new grid line getWidth.
     */
    public void setWidth(float width) {
        if (width <= 0)
            throw new IllegalArgumentException("width must be greater than 0");

        this.paint.setStrokeWidth(width);
        this.halfLineWidth = width / 2;
    }

    /**
     * Gets the color of the grid line.
     *
     * @return the current grid line color.
     */
    public int getColor() {
        return this.paint.getColor();
    }

    /**
     * Sets the color of the grid line.
     *
     * @param color the new grid line color.
     */
    public void setColor(int color) {
        this.paint.setColor(color);
    }

    /**
     * Used to render a single grid line on a given canvas.
     *
     * @param startX the start position for the line along the x axis.
     * @param startY the start position for the line along the y axis.
     * @param endX   the end position for the line along the x axis.
     * @param endY   the end position for the line along the y axis.
     * @param canvas the canvas onto which the line will be drawn.
     */
    public void drawLine(float startX, float startY, float endX, float endY, Canvas canvas) {
        drawLine(startX, startY, endX, endY, canvas, 255);
    }

    public void drawLine(float startX, float startY, float endX, float endY, Canvas canvas, int alpha) {
        this.paint.setAlpha(alpha);
        canvas.drawLine(startX, startY, endX, endY, this.paint);
    }
}
