package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.RadCalendarView;

/**
 * Decorator that renders a square shape for decorating cells. It is best used with the text of the elements being set to
 * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
 */
public class SquareCellDecorator extends CellDecorator {

    private static final float DEFAULT_RECTANGLE_SCALE = .85f;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.decorations.SquareCellDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public SquareCellDecorator(RadCalendarView owner) {
        this(owner, DEFAULT_RECTANGLE_SCALE);
    }

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.decorations.SquareCellDecorator} class.
     *
     * @param owner       the calendar instance owning this decorator.
     * @param squareScale the scale of the square [0, 1]
     * @see #getScale()
     */
    public SquareCellDecorator(RadCalendarView owner, float squareScale) {
        super(owner);

        setScale(squareScale);
    }

    @Override
    protected void renderDecorationForCell(Canvas canvas, CalendarCell cell) {
        int offset = (int) (Math.min(cell.getWidth(), cell.getHeight()) * scale) >> 1;
        renderDecoration(canvas,
                cell.getLeft() + cell.getVirtualOffsetX() + (cell.getWidth() >> 1) - offset,
                cell.getTop() + cell.getVirtualOffsetY() + (cell.getHeight() >> 1) - offset,
                cell.getLeft() + cell.getVirtualOffsetX() + (cell.getWidth() >> 1) + offset,
                cell.getTop() + cell.getVirtualOffsetY() + (cell.getHeight() >> 1) + offset,
                cell.textPositionX() + cell.getVirtualOffsetX(), cell.textPositionY() + cell.getVirtualOffsetY(),
                cell.getText(), cell.getTextPaint());
    }

    private void renderDecoration(Canvas canvas, int left, int top, int right, int bottom, int textX, int textY, String text, Paint textPaint) {
        canvas.drawRect(left, top, right, bottom, this.paint);
        if (!this.stroked)
            canvas.drawText(text, textX, textY, textPaint);
    }
}