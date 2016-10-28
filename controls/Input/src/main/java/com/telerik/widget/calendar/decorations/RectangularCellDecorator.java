package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.RadCalendarView;

/**
 * Decorator that renders a rectangular shape for decorating cells. It is best used with the text of the elements being set to
 * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
 */
public class RectangularCellDecorator extends CellDecorator {

    private static final float DEFAULT_RECTANGLE_SCALE = .85f;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.decorations.RectangularCellDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public RectangularCellDecorator(RadCalendarView owner) {
        this(owner, DEFAULT_RECTANGLE_SCALE);
    }

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.decorations.RectangularCellDecorator} class.
     *
     * @param owner          the calendar instance owning this decorator.
     * @param rectangleScale the scale of the rectangle [0, 1]
     * @see #getScale()
     */
    public RectangularCellDecorator(RadCalendarView owner, float rectangleScale) {
        super(owner);

        setScale(rectangleScale);
    }


    @Override
    protected void renderDecorationForCell(Canvas canvas, CalendarCell cell) {
        renderDecoration(canvas,
                cell.getLeft() + cell.getVirtualOffsetX() + ((cell.getWidth() - (int) (cell.getWidth() * scale)) >> 1),
                cell.getTop() + cell.getVirtualOffsetY() + ((cell.getHeight() - (int) (cell.getHeight() * scale)) >> 1),
                cell.getRight() + cell.getVirtualOffsetX() - ((cell.getWidth() - (int) (cell.getWidth() * scale)) >> 1),
                cell.getBottom() + cell.getVirtualOffsetY() - ((cell.getHeight() - (int) (cell.getHeight() * scale)) >> 1),
                cell.textPositionX() + cell.getVirtualOffsetX(), cell.textPositionY() + cell.getVirtualOffsetY(),
                cell.getText(), cell.getTextPaint());
    }

    private void renderDecoration(Canvas canvas, int left, int top, int right, int bottom, int textX, int textY, String text, Paint textPaint) {
        canvas.drawRect(left, top, right, bottom, this.paint);
        if (!this.stroked)
            canvas.drawText(text, textX, textY, textPaint);
    }
}