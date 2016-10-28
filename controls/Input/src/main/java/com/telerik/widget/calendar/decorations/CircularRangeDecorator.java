package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telerik.widget.calendar.RadCalendarView;

/**
 * Renders a range with a circular indicator and rounded rectangle.
 */
public class CircularRangeDecorator extends RangeDecorator {

    /**
     * Creates a new instance of the {@link CircularRangeDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public CircularRangeDecorator(RadCalendarView owner) {
        super(owner);
    }

    @Override
    protected void renderShape(Canvas canvas, RectF shapeBounds) {
        this.paint.setStyle(this.stroked ? Paint.Style.STROKE : Paint.Style.FILL);
        this.paint.setColor(this.shapeColor);
        canvas.drawRoundRect(shapeBounds, this.shapeSize, this.shapeSize, this.paint);
    }

    @Override
    protected void renderIndicator(Canvas canvas, int centerX, int centerY) {
        this.paint.setStyle(this.stroked ? Paint.Style.STROKE : Paint.Style.FILL);
        this.paint.setColor(this.color);
        canvas.drawCircle(centerX, centerY, (this.indicatorSize), this.paint);
    }
}
