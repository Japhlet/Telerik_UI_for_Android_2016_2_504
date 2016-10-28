package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.RadCalendarView;

/**
 * Basic class for decoration containing the minimum amount of information needed for decoration.
 */
public abstract class Decorator {

    /**
     * The paint to be used in decorating.
     */
    protected final Paint paint;

    /**
     * The stroke width of the decorations.
     */
    protected float strokeWidth;

    /**
     * The color of the decorations.
     */
    protected int color = Color.parseColor("#a9d6d2");

    /**
     * The current calendar instance owning the renderer.
     */
    protected final RadCalendarView owner;

    /**
     * Creates an instance of the {@link com.telerik.widget.calendar.CellDecorationsLayer} class.
     *
     * @param owner the current calendar instance owning the element.
     */
    public Decorator(RadCalendarView owner) {
        this.owner = owner;

        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Gets the color to be used for decorating.
     *
     * @return the current decorations color.
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color to be used for decorating.
     *
     * @param color the new decorations color.
     */
    public void setColor(int color) {
        if (this.color != color) {
            this.color = color;
            this.paint.setColor(color);
            this.owner.invalidate();
        }
    }

    /**
     * Returns the getWidth of the cell decoration lines.
     *
     * @return the current cell decoration lines getWidth
     */
    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    /**
     * Sets a new value for the getWidth of the cell decoration lines.
     *
     * @param value the new getWidth for the cell decoration lines
     */
    public void setStrokeWidth(float value) {
        if (value <= 0)
            throw new IllegalArgumentException("value must be a positive number");

        if (this.strokeWidth != value) {
            this.strokeWidth = value;
            this.paint.setStrokeWidth(value);
            this.owner.invalidate();
        }
    }

    /**
     * Removes the decorations for all cells that are currently decorated.
     */
    public abstract void clearDecorations();

    /**
     * Gets called by the calendar to render the decorations that do not belong to a specific layer, but are rather stored on the default or base layer which is 0.
     *
     * @param canvas the canvas for drawing the decorations.
     */
    public void render(Canvas canvas) {
        renderLayer(0, canvas);
    }

    public abstract void renderLayer(int layerId, Canvas canvas);

    /**
     * Used to toggle the decoration of a specified cell instance. If the cell is already being marked for decoration, the previous decoration
     * will be removed and the new one will not be added. If currently there is no decoration for the cell, a new decoration will be prepared for the time of invalidation.
     *
     * @param cell the cell to be decorated.
     */
    public void toggleDecorationForCell(CalendarCell cell) {
        toggleDecorationForCell(cell, 0);
    }

    /**
     * Used to toggle the decoration of a specified cell instance using a layer id. If the cell is already being marked for decoration, the previous decoration
     * will be removed and the new one will not be added. If currently there is no decoration for the cell, a new decoration will be prepared for the time of invalidation.
     * The decoration will be stored on the specified layer using the provided layer id(usually the id of the fragment holding the cell instance).
     *
     * @param cell    the cell to be decorated.
     * @param layerId the id of the layer that will store the decoration.
     */
    public abstract void toggleDecorationForCell(CalendarCell cell, int layerId);
}
