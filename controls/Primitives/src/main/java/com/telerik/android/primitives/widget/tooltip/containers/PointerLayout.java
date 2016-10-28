package com.telerik.android.primitives.widget.tooltip.containers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.telerik.android.primitives.R;

/**
 * Custom view group providing functionality for rendering a pointer.
 */
public class PointerLayout extends FrameLayout {

    private int pointerSize;
    private boolean alignPointerVertically;
    private int pointerDistanceToTarget;
    private boolean paddingChangeScheduled;

    private Paint pointerPaint;
    private Path pointerShape;
    private Point targetLocation;
    private Point containerLocation;

    /**
     * Creates a new instance of the {@link PointerLayout} class.
     *
     * @param context the context for the pointer layout.
     * @param attrs   the attributes set.
     */
    public PointerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);

        this.pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.pointerPaint.setStyle(Paint.Style.FILL);
        this.pointerShape = new Path();
        this.containerLocation = new Point();

        this.paddingChangeScheduled = true;
        setPadding(0, 0, 0, 0);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PointerLayout);

        if (array != null) {
            this.initFromXML(array);
        }
    }

    /**
     * Gets the current alignment strategy.
     *
     * @return <code>true</code> if the pointer is vertical, <code>false</code> if it is horizontal.
     */
    public boolean getAlignPointerVertically() {
        return alignPointerVertically;
    }

    /**
     * Sets the current alignment strategy.
     *
     * @param alignPointerVertically the new alignment strategy.
     */
    public void setAlignPointerVertically(boolean alignPointerVertically) {
        this.alignPointerVertically = alignPointerVertically;
    }

    /**
     * Updates the current target location to which the pointer will be pointing.
     *
     * @param targetLocation the new target location.
     */
    public void updateTargetLocation(Point targetLocation) {
        this.targetLocation = targetLocation;
    }

    /**
     * Updates the currently known location for the current container.
     *
     * @param location the new location.
     */
    public void updateContainerLocation(Point location) {
        this.containerLocation.x = location.x;
        this.containerLocation.y = location.y;
    }

    /**
     * Padding is disabled for the pointer layout since it is needed for its logic and cannot be changed directly.
     *
     * @param left   the padding from the left side.
     * @param top    the padding from the top side.
     * @param right  the padding from the right side.
     * @param bottom the padding from the bottom side.
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (!this.paddingChangeScheduled)
            return;

        super.setPadding(left, top, right, bottom);
        this.paddingChangeScheduled = false;
    }

    /**
     * Gets the size of the tooltip pointer.
     *
     * @return the current pointer size.
     */
    public int getPointerSize() {
        return this.pointerSize;
    }

    /**
     * Sets the current pointer size and updates the current padding accordingly.
     *
     * @param pointerSize the new pointer size.
     */
    public void setPointerSize(int pointerSize) {
        if (pointerSize < 0)
            throw new IllegalArgumentException("pointerSize cannot be negative");

        updatePadding(this.pointerSize, pointerSize);
        this.pointerSize = pointerSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (pointerSize <= 0) {
            return;
        }

        super.onDraw(canvas);
        drawPointer(canvas);
    }

    private void drawPointer(Canvas canvas) {
        int x;
        int y;
        this.pointerShape.reset();

        if (this.alignPointerVertically) {
            int halfWidth = getMeasuredWidth() / 2;
            int left = this.containerLocation.x;

            boolean drawPointerBellow = (this.containerLocation.y + getMeasuredHeight()) <= targetLocation.y;

            int padding = getPaddingLeft();

            x = targetLocation.x - left;
            y = drawPointerBellow ? getMeasuredHeight() - padding : padding;

            boolean leftToMiddle = x <= halfWidth;

            if (x < padding)
                x = padding;

            if (x > getMeasuredWidth() - padding)
                x = getMeasuredWidth() - padding;

            this.pointerShape.moveTo(x, y);
            this.pointerShape.lineTo(x, y + (drawPointerBellow ? this.pointerSize : -this.pointerSize));
            //this.pointerShape.lineTo(x + (leftToMiddle ? this.pointerSize : -this.pointerSize), y);

            int offsetX;
            if (leftToMiddle) {
                if (x + this.pointerSize <= getMeasuredWidth() - padding)
                    offsetX = this.pointerSize;
                else
                    offsetX = (getMeasuredWidth() - padding) - x;
            } else {
                if (x - this.pointerSize >= padding)
                    offsetX = -this.pointerSize;
                else
                    offsetX = x - padding;
            }

            this.pointerShape.lineTo(x + offsetX, y);
            this.pointerShape.lineTo(x, y);
            this.pointerShape.close();
        } else {
            boolean drawPointerLeft = (this.containerLocation.x + getMeasuredWidth()) > targetLocation.x;

            x = drawPointerLeft ? getPaddingLeft() : getMeasuredWidth() - getPaddingRight();
            y = targetLocation.y - this.containerLocation.y;

            boolean drawPointerAbove = targetLocation.y > this.containerLocation.y + (getMeasuredHeight() / 2);
            if (drawPointerAbove && y > getBottom() - getPaddingBottom())
                y = getBottom() - getPaddingBottom();

            if (!drawPointerAbove && y < getTop() + getPaddingTop())
                y = getTop() + getPaddingTop();

            this.pointerShape.moveTo(x, y);
            this.pointerShape.lineTo(x + (drawPointerLeft ? -pointerSize : pointerSize), y);
            this.pointerShape.lineTo(x, +y + (drawPointerAbove ? -pointerSize : pointerSize));

            int offsetY;
            if (drawPointerAbove) {
                if (y - this.pointerSize >= this.pointerSize)
                    offsetY = -pointerSize;
                else
                    offsetY = y - this.pointerSize;
            } else {
                if (y + this.pointerSize <= getMeasuredHeight() - this.pointerSize)
                    offsetY = pointerSize;
                else
                    offsetY = (getMeasuredHeight() - this.pointerSize) - y;
            }

            this.pointerShape.lineTo(x, y + offsetY);

            this.pointerShape.lineTo(x, y);
            this.pointerShape.close();
        }

        canvas.drawPath(this.pointerShape, this.pointerPaint);
    }

    public int getTooltipMargin() {
        return this.pointerDistanceToTarget;
    }

    public void setTooltipMargin(int margin) {
        if (margin < 0)
            throw new IllegalArgumentException("margin cannot be negative");

        updatePadding(this.pointerDistanceToTarget, margin);
        this.pointerDistanceToTarget = margin;
    }

    public int getPointerColor() {
        return this.pointerPaint.getColor();
    }

    public void setPointerColor(int color) {
        this.pointerPaint.setColor(color);
    }

    private void updatePadding(int oldValue, int value) {
        this.paddingChangeScheduled = true;
        int newValue = Math.abs(oldValue - value);
        setPadding(getPaddingLeft() + newValue, getPaddingTop() + newValue,
                getPaddingRight() + newValue, getPaddingBottom() + newValue);
    }

    private void initFromXML(TypedArray array) {
        setPointerSize((int) array.getDimension(R.styleable.PointerLayout_pointerSize, 0));
        setPointerColor(array.getColor(R.styleable.PointerLayout_pointerFill, Color.parseColor("#282828")));
        setTooltipMargin((int) array.getDimension(R.styleable.PointerLayout_pointerMargin, 0));
    }
}
