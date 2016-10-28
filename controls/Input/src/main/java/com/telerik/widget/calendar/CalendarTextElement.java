package com.telerik.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.telerik.android.common.Util;

/**
 * Calendar element that holds some text.
 */
public class CalendarTextElement extends CalendarElement {

    private Rect textBounds;

    /**
     * The current text.
     */
    protected String text;

    /**
     * The paint that will be used when rendering the text.
     */
    protected Paint textPaint;

    /**
     * The color to be used when the element is enabled.
     */
    protected int textColorEnabled;

    /**
     * The color to be used when the element is disabled.
     */
    protected int textColorDisabled;

    /**
     * The current position of the text.
     */
    protected int textPosition;

    /**
     * Current position of the text along the x axis. Updated during {@link #calculateTextPosition()}
     */
    protected int textPositionX;

    /**
     * Current position of the text along the y axis.  Updated during {@link #calculateTextPosition()}
     */
    protected int textPositionY;

    private Typeface typeface;

    /**
     * Creates a new instance of the {@link CalendarTextElement} class.
     *
     * @param owner the current calendar instance owning the element.
     */
    public CalendarTextElement(RadCalendarView owner) {
        super(owner);

        this.textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public int textPositionX() {
        return textPositionX;
    }

    public int textPositionY() {
        return textPositionY;
    }

    @Override
    protected void onAlphaChanged() {
        super.onAlphaChanged();
        this.textPaint.setAlpha(this.alpha);
    }

    /**
     * Gets the color to be used when the element is enabled.
     *
     * @return the enabled color.
     */
    public int getTextColorEnabled() {
        return this.textColorEnabled;
    }

    /**
     * Sets the color to be used when the element is enabled.
     *
     * @param color the new enabled color.
     */
    public void setTextColorEnabled(int color) {
        if (this.textColorEnabled == color)
            return;

        this.textColorEnabled = color;
        updateTextColor();
    }

    /**
     * Gets the color to be used when the element is disabled.
     *
     * @return the disabled color.
     */
    public int getTextColorDisabled() {
        return this.textColorDisabled;
    }

    /**
     * Sets the color to be used when the element is disabled.
     *
     * @param color the new disabled color.
     */
    public void setTextColorDisabled(int color) {
        if (this.textColorDisabled == color)
            return;

        this.textColorDisabled = color;
        updateTextColor();
    }

    /**
     * Gets the current position of the text.
     *
     * @return the current text position.
     */
    public int getTextPosition() {
        return this.textPosition;
    }

    /**
     * Sets the current position of the text.
     *
     * @param textPosition the new position.
     */
    public void setTextPosition(int textPosition) {
        if (this.textPosition == textPosition)
            return;

        this.textPosition = textPosition;
        calculateTextPosition();
    }

    /**
     * Gets the current text color.
     *
     * @return the current text color.
     */
    public int getTextColor() {
        return this.textPaint.getColor();
    }

    /**
     * Sets the current text color.
     *
     * @param color the new text color.
     */
    public void setTextColor(int color) {
        if (this.textColorEnabled == color)
            return;

        this.textColorEnabled = color;

        updateTextColor();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);

        calculateTextPosition();
    }

    /**
     * Sets the current text color for both the enabled and the disabled states.
     *
     * @param colorEnabled  the text color enabled.
     * @param colorDisabled the text color disabled.
     */
    public void setTextColor(int colorEnabled, int colorDisabled) {
        if (this.textColorEnabled == colorEnabled && this.textColorDisabled == colorDisabled)
            return;

        this.textColorEnabled = colorEnabled;
        this.textColorDisabled = colorDisabled;

        updateTextColor();
    }

    /**
     * Gets the current text size.
     *
     * @return the current text size.
     */
    public float getTextSize() {
        return this.textPaint.getTextSize();
    }

    /**
     * Sets the current text size.
     *
     * @param textSize the new text size.
     */
    public void setTextSize(float textSize) {
        if(this.textPaint.getTextSize() == textSize) {
            return;
        }
        this.textPaint.setTextSize(textSize);

        updateTextBounds();
        calculateTextPosition();
    }

    /**
     * Gets the current typeface.
     *
     * @return the current typeface.
     */
    public Typeface getTypeface() {
        return this.typeface;
    }

    /**
     * Sets the current typeface.
     *
     * @param typeface the new typeface.
     */
    public void setTypeface(Typeface typeface) {
        if(this.typeface == typeface) {
            return;
        }
        this.typeface = typeface;
        updateTypeFace();
        updateTextBounds();
        calculateTextPosition();
    }

    /**
     * Gets the current text.
     *
     * @return the current text.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the current text.
     *
     * @param text the new text.
     */
    public void setText(String text) {
        if(text == null || text.equals(this.text)) {
            return;
        }
        this.text = text;

        updateTextBounds();
        calculateTextPosition();
    }

    /**
     * Gets the current paint.
     *
     * @return the current paint.
     */
    public Paint getTextPaint() {
        return this.textPaint;
    }

    /**
     * Sets the current paint.
     *
     * @param paint the new paint.
     */
    public void setTextPaint(Paint paint) {
        if(this.textPaint == paint) {
            return;
        }
        this.textPaint = paint;

        updateTextBounds();
        calculateTextPosition();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        updateTextColor();
    }

    @Override
    public void arrange(int left, int top, int right, int bottom) {
        super.arrange(left, top, right, bottom);
        calculateTextPosition();
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        if (this.text != null && !this.text.equals(""))
            canvas.drawText(this.text, this.textPositionX, this.textPositionY, this.textPaint);
    }

    /**
     * Used to update the text color according to the state of the element.
     */
    protected void updateTextColor() {
        if (this.isEnabled())
            this.textPaint.setColor(this.textColorEnabled);
        else
            this.textPaint.setColor(this.textColorDisabled);
    }

    /**
     * Used to update the typeface according to the state of the element.
     */
    protected void updateTypeFace() {
        this.textPaint.setTypeface(this.typeface);
    }

    private void updateTextBounds() {
        if(textPaint == null || this.text == null) {
            return;
        }
        textBounds = new Rect();
        this.textPaint.getTextBounds(Util.generateDummyText(this.text), 0, this.text.length(), textBounds);
    }

    /**
     * Used to calculate the position of the text where TOP | RIGHT is default.
     */
    protected void calculateTextPosition() {
        if (this.text == null || this.text.equals(""))
            return;

        if(textBounds == null) {
            updateTextBounds();
        }

        if ((this.textPosition & CENTER_HORIZONTAL) > 0) {
            this.textPositionX = (this.getLeft() + (this.getWidth() / 2) - (textBounds.width() / 2));
        } else if ((this.textPosition & LEFT) > 0) {
            this.textPositionX = (this.getLeft() + this.getPaddingLeft());
        } else { // RIGHT is default
            this.textPositionX = (this.getRight() - this.getPaddingRight() - textBounds.width());
        }

        if ((this.textPosition & CENTER_VERTICAL) > 0) {
            this.textPositionY = (this.getTop() + (this.getHeight() / 2) + (textBounds.height() / 2));
        } else if ((this.textPosition & BOTTOM) > 0) {
            this.textPositionY = (this.getBottom() - this.getPaddingBottom());
        } else { // TOP is default
            this.textPositionY = (this.getTop() + this.getPaddingTop() + textBounds.height());
        }
    }
}
