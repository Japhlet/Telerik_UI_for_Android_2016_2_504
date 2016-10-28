package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.RadCalendarView;

/**
 * Decorator that renders a circular shape for decorating cells. It is best used with the text of the elements being set to
 * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
 */
public class CircularCellDecorator extends CellDecorator {

    private static final float DEFAULT_CIRCLE_SCALE = .85f;

    /**
     * Creates a new instance of the {@link CircularCellDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public CircularCellDecorator(RadCalendarView owner) {
        this(owner, DEFAULT_CIRCLE_SCALE);
    }

    /**
     * Creates a new instance of the {@link CircularCellDecorator} class.
     *
     * @param owner       the calendar instance owning this decorator.
     * @param circleScale the shapeScale of the circle [0, 1]
     * @see #getScale()
     */
    public CircularCellDecorator(RadCalendarView owner, float circleScale) {
        super(owner);
        setScale(circleScale);
    }

    @Override
    protected void renderDecorationForCell(Canvas canvas, CalendarCell cell) {
        renderDecoration(canvas,
                cell.getLeft() + (cell.getWidth() >> 1) + cell.getVirtualOffsetX(),
                cell.getTop() + (cell.getHeight() >> 1) + cell.getVirtualOffsetY(),
                ((int) (Math.min(cell.getWidth(), cell.getHeight()) * this.scale)) >> 1,
                cell.textPositionX() + cell.getVirtualOffsetX(),
                cell.textPositionY() + cell.getVirtualOffsetY(),
                cell.getText(), cell.getTextPaint());
    }

    private void renderDecoration(Canvas canvas, int decorationX, int decorationY, int radius, int textX, int textY, String text, Paint textPaint) {
        canvas.drawCircle(decorationX, decorationY, radius, this.paint);
        if (!this.stroked)
            canvas.drawText(text, textX, textY, textPaint);
    }
}