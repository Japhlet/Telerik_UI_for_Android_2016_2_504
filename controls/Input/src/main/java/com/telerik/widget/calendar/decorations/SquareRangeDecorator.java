package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telerik.widget.calendar.RadCalendarView;

/**
 * Renders a square range decorator.
 */
public class SquareRangeDecorator extends RangeDecorator {

    /**
     * Creates a new instance of the {@link SquareRangeDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public SquareRangeDecorator(RadCalendarView owner) {
        super(owner);
    }

    @Override
    protected void renderShape(Canvas canvas, RectF shapeBounds) {
        this.paint.setStyle(this.stroked ? Paint.Style.STROKE : Paint.Style.FILL);
        this.paint.setColor(this.shapeColor);

        canvas.drawRect(shapeBounds, this.paint);
    }

    @Override
    protected void renderIndicator(Canvas canvas, int centerX, int centerY) {
        this.paint.setStyle(this.stroked ? Paint.Style.STROKE : Paint.Style.FILL);
        this.paint.setColor(this.color);
        canvas.drawRect(centerX - this.indicatorSize, centerY - this.indicatorSize, centerX + this.indicatorSize, centerY + this.indicatorSize, this.paint);
    }
}
