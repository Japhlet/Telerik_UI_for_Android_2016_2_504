package com.telerik.widget.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Base class for all calendar elements. It holds the basic properties such as position, dimensions and arrange logic.
 */
public abstract class CalendarElement {

    /**
     * The position of the text will be on the top side of the element.
     */
    public static final int TOP = 1;

    /**
     * The position of the text will be on the left side of the element.
     */
    public static final int LEFT = 1 << 1;

    /**
     * The position of the text will be on the right side of the element.
     */
    public static final int RIGHT = 1 << 2;

    /**
     * The position of the text will be on the bottom side of the element.
     */
    public static final int BOTTOM = 1 << 3;

    /**
     * The position of the text will be centered alongside the x axis.
     */
    public static final int CENTER_HORIZONTAL = 1 << 4;

    /**
     * The position of the text will be centered alongside the y axis.
     */
    public static final int CENTER_VERTICAL = 1 << 5;

    /**
     * The position of the text will be centered alongside both axes.
     */
    public static final int CENTER = CENTER_HORIZONTAL | CENTER_VERTICAL;

    private Bitmap bitmap;

    private int bitmapPosition = BOTTOM | RIGHT;

    /**
     * Virtual offset of the element along the x axis. Used in scrolling for better performance.
     */
    protected int virtualOffsetX;
    /**
     * Virtual offset of the element along the y axis. Used in scrolling for better performance.
     */
    protected int virtualOffsetY;

    /**
     * Current context.
     */
    protected final Context context;
    /**
     * The current calendar instance owning the element.
     */
    protected final RadCalendarView owner;
    /**
     * Paint used to fill the background of the element.
     */
    protected final Paint backgroundPaint;
    /**
     * Parenting element that contains the current instance.
     */
    protected CalendarElement parent;
    /**
     * The visible state of the element.
     */
    protected ElementVisibility visibility = ElementVisibility.Visible;
    /**
     * States whether the element is enabled.
     */
    protected boolean enabled = true;

    protected int alpha = 255;

    private int left;
    private int top;
    private int right;
    private int bottom;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int width;
    private int height;
    private int currentBackgroundColor;
    private int backgroundColorEnabled;
    private int backgroundColorDisabled;
    private int bitmapPositionX;
    private int bitmapPositionY;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarElement} class.
     *
     * @param owner the calendar instance owning the element.
     */
    public CalendarElement(RadCalendarView owner) {
        this.context = owner.getContext();
        this.owner = owner;
        this.backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * The virtual left coordinate of the element according to the current left and the current virtual
     * offset along the x axis. Used when scrolling for better performance.
     *
     * @return the current virtual left position.
     */
    public int virtualLeft() {
        return this.left + virtualOffsetX;
    }

    /**
     * The virtual top coordinate of the element according to the current top and the current virtual
     * offset along the y axis. Used when scrolling for better performance.
     *
     * @return the current virtual top position.
     */
    public int virtualTop() {
        return this.top + virtualOffsetY;
    }

    /**
     * The virtual right coordinate of the element according to the current right and the current virtual
     * offset along the x axis. Used when scrolling for better performance.
     *
     * @return the current virtual right position.
     */
    public int virtualRight() {
        return this.right + virtualOffsetX;
    }

    /**
     * The virtual bottom coordinate of the element according to the current bottom and the current virtual
     * offset along the y axis. Used when scrolling for better performance.
     *
     * @return the current virtual bottom position.
     */
    public int virtualBottom() {
        return this.bottom + virtualOffsetY;
    }

    /**
     * Gets the virtual offset of the element along the x axis. Used when scrolling for better performance.
     *
     * @return the current virtual offset along the x axis.
     */
    public int getVirtualOffsetX() {
        return virtualOffsetX;
    }

    /**
     * Sets the virtual offset of the element along the x axis. Used when scrolling for better performance.
     *
     * @param offset the new virtual offset along the x axis.
     */
    public void setVirtualOffsetX(int offset) {
        this.virtualOffsetX = offset;
    }

    /**
     * Gets the virtual offset of the element along the y axis. Used when scrolling for better performance.
     *
     * @return the current virtual offset along the y axis.
     */
    public int getVirtualOffsetY() {
        return virtualOffsetY;
    }

    /**
     * Sets the virtual offset of the element along the y axis. Used when scrolling for better performance.
     *
     * @param offset the new virtual offset along the y axis.
     */
    public void setVirtualOffsetY(int offset) {
        this.virtualOffsetY = offset;
    }

    /**
     * Sets the virtual offsets of the element along the x and y axes. Used when scrolling for better performance.
     *
     * @param offsetX the new virtual offset along the x axis.
     * @param offsetY the new virtual offset along the y axis.
     */
    public void setVirtualOffsets(int offsetX, int offsetY) {
        setVirtualOffsetX(offsetX);
        setVirtualOffsetY(offsetY);
    }

    /**
     * Gets the position of the bitmap. Expected values are: {@link CalendarElement#LEFT}, {@link CalendarElement#TOP},
     * {@link CalendarElement#RIGHT}, {@link CalendarElement#BOTTOM}, {@link CalendarElement#CENTER_VERTICAL}, {@link CalendarElement#CENTER_HORIZONTAL} and
     * {@link CalendarElement#CENTER}
     *
     * @return the current bitmap position.
     */
    public int getBitmapPosition() {
        return bitmapPosition;
    }

    /**
     * Sets the position of the bitmap. Expected values are: {@link CalendarElement#LEFT}, {@link CalendarElement#TOP},
     * {@link CalendarElement#RIGHT}, {@link CalendarElement#BOTTOM}, {@link CalendarElement#CENTER_VERTICAL}, {@link CalendarElement#CENTER_HORIZONTAL} and
     * {@link CalendarElement#CENTER}
     *
     * @param bitmapPosition the new bitmap position.
     */
    public void setBitmapPosition(int bitmapPosition) {
        if (this.bitmapPosition != bitmapPosition) {
            this.bitmapPosition = bitmapPosition;
            arrangeBitmap();
        }
    }

    /**
     * Gets the current bitmap for the element.
     *
     * @return the current bitmap.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Sets the current bitmap for the element.
     *
     * @param bitmap the new bitmap.
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (bitmap != null)
            arrangeBitmap();
    }

    private void arrangeBitmap() {
        if (this.bitmap != null) {
            if ((this.bitmapPosition & CENTER_HORIZONTAL) > 0) {
                this.bitmapPositionX = (this.getLeft() + (this.getWidth() / 2) - (this.bitmap.getWidth() / 2));
            } else if ((this.bitmapPosition & LEFT) > 0) {
                this.bitmapPositionX = (this.getLeft() + this.getPaddingLeft());
            } else { // RIGHT is default
                this.bitmapPositionX = (this.getRight() - this.getPaddingRight() - bitmap.getWidth());
            }

            if ((this.bitmapPosition & CENTER_VERTICAL) > 0) {
                this.bitmapPositionY = (this.getTop() + (this.getHeight() / 2) - (this.bitmap.getHeight() / 2));
            } else if ((this.bitmapPosition & BOTTOM) > 0) {
                this.bitmapPositionY = (this.getBottom() - this.getPaddingBottom() - this.bitmap.getHeight());
            } else { // TOP is default
                this.bitmapPositionY = (this.getTop() + this.getPaddingTop());
            }
        }
    }

    /**
     * Gets the alpha value of the element.
     *
     * @return the current alpha value.
     */
    public int getAlpha() {
        return this.alpha;
    }

    /**
     * Sets the alpha value of the element.
     *
     * @param alpha the new alpha value.
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;

        onAlphaChanged();
    }

    /**
     * Called when the alpha has changed.
     */
    protected void onAlphaChanged() {
        this.backgroundPaint.setAlpha(alpha);
    }

    /**
     * Gets the current left position.
     *
     * @return the current left.
     */
    public int getLeft() {
        return this.left;
    }

    /**
     * Gets the current top position.
     *
     * @return the current top.
     */
    public int getTop() {
        return this.top;
    }

    /**
     * The current right position.
     *
     * @return the current right.
     */
    public int getRight() {
        return this.right;
    }

    /**
     * Gets the current bottom position.
     *
     * @return the current bottom.
     */
    public int getBottom() {
        return this.bottom;
    }

    /**
     * Gets the getWidth of the element.
     *
     * @return the current getWidth.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the getHeight of the element.
     *
     * @return the current getHeight.
     */
    public int getHeight() {
        return this.height;
    }

    public ElementVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ElementVisibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Gets a value determining whether the current element is enabled.
     *
     * @return <code>true</code> if the element is enabled, <code>false</code> otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets a value determining whether the current element is enabled.
     *
     * @param enabled the new enabled state.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateBackgroundColor();
    }

    /**
     * Gets the current parent element, that holds this element.
     *
     * @return the current parent element.
     */
    public CalendarElement getParent() {
        return parent;
    }

    /**
     * Sets the current parent element, that holds this element.
     *
     * @param parent the new parent element.
     */
    public void setParent(CalendarElement parent) {
        this.parent = parent;
    }

    /**
     * Gets the left padding of the element.
     *
     * @return the current left padding.
     */
    public int getPaddingLeft() {
        return this.paddingLeft;
    }

    /**
     * Gets the top padding of the element.
     *
     * @return the current top padding.
     */
    public int getPaddingTop() {
        return this.paddingTop;
    }

    /**
     * Gets the right padding of the element.
     *
     * @return the current right padding.
     */
    public int getPaddingRight() {
        return this.paddingRight;
    }

    /**
     * Gets the bottom padding of the element.
     *
     * @return the current bottom padding.
     */
    public int getPaddingBottom() {
        return this.paddingBottom;
    }

    /**
     * Sets the padding for the current element.
     *
     * @param left   the new left padding.
     * @param top    the new top padding.
     * @param right  the new right padding.
     * @param bottom the new bottom padding.
     */
    public void setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
    }

    /**
     * Sets padding to both left and right side of the element.
     *
     * @param padding the padding to be set.
     */
    public void setPaddingHorizontal(int padding) {
        setPadding(padding, this.paddingTop, padding, this.paddingBottom);
    }

    /**
     * Sets padding to both top and bottom side of the element.
     *
     * @param padding the padding to be set.
     */
    public void setPaddingVertical(int padding) {
        setPadding(this.paddingLeft, padding, this.paddingRight, padding);
    }

    /**
     * Gets the color that will be used as background when the element is idle.
     *
     * @return the current idle color.
     */
    public int getBackgroundColorEnabled() {
        return this.backgroundColorEnabled;
    }

    /**
     * Sets the color that will be used as background when the element is idle.
     *
     * @param color the new idle color.
     */
    public void setBackgroundColorEnabled(int color) {
        if (this.backgroundColorEnabled == color)
            return;

        this.backgroundColorEnabled = color;
        updateBackgroundColor();
    }

    /**
     * Gets the color that will be used as background when the element is disabled.
     *
     * @return the current disabled color.
     */
    public int getBackgroundColorDisabled() {
        return this.backgroundColorDisabled;
    }

    /**
     * Sets the color that will be used as background when the element is disabled.
     *
     * @param color the new disabled color.
     */
    public void setBackgroundColorDisabled(int color) {
        if (this.backgroundColorDisabled != color) {
            this.backgroundColorDisabled = color;

            updateBackgroundColor();
        }
    }

    /**
     * Sets the background color for both the enabled and the disabled state of the element.
     *
     * @param colorEnabled  the color for the enabled state.
     * @param colorDisabled the color for the disabled state.
     */
    public void setBackgroundColor(int colorEnabled, int colorDisabled) {
        if (this.backgroundColorEnabled == colorEnabled &&
                this.backgroundColorDisabled == colorDisabled)
            return;

        setBackgroundColorEnabled(colorEnabled);
        setBackgroundColorDisabled(colorDisabled);
    }

    /**
     * This method is called at onDraw() call from the main calendar view instance and is used for drawing all custom elements building the calendar structure.
     *
     * @param canvas the canvas of the main calendar view instance.
     */
    public void render(Canvas canvas) {
        canvas.drawRect((float) this.left, (float) this.top, (float) this.right, (float) this.bottom, this.backgroundPaint);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, bitmapPositionX, bitmapPositionY, null);
        }
    }

    /**
     * This method is called after calling the {@link #render(android.graphics.Canvas)} method.
     *
     * @param canvas the canvas of the main calendar view instance.
     */
    public void postRender(Canvas canvas) {
    }

    /**
     * Calculates the border rect for this cell.
     *
     * @return the border rect for this cell
     */
    public Rect calcBorderRect() {
        return new Rect(getLeft(), getTop(), getRight(), getBottom());
    }

    /**
     * Used to arrange the current element instance according to the given dimensions.
     *
     * @param left   the left border.
     * @param top    the top border.
     * @param right  the right border.
     * @param bottom the bottom border.
     */
    public void arrange(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        this.width = right - left;
        this.height = bottom - top;

        /*this.virtualOffsetX = 0;
        this.virtualOffsetY = 0;*/

        onArrange();
    }

    /**
     * Called after arranging the current element.
     */
    protected void onArrange() {
        if (this.bitmap != null) {
            arrangeBitmap();
        }
    }

    /**
     * Used to translate the element using the given offset which is being applied to the current position of the element.
     *
     * @param offsetX offset along the x axis.
     * @param offsetY offset along the y axis.
     */
    public void translate(int offsetX, int offsetY) {
        this.left += offsetX;
        this.top += offsetY;
        onArrange();
    }

    public void virtualTranslate(int offsetX, int offsetY) {
        this.virtualOffsetX += offsetX;
        this.virtualOffsetY += offsetY;
    }

    /**
     * Used to determine if a point is inside the current element's dimensions.
     *
     * @param x the x coordinate of the point.
     * @param y the y coordinate of the point.
     * @return <code>true</code> of the point is inside the current element's dimensions, <code>false</code> if it is outside the dimensions.
     */
    public final boolean pointIsInsideElement(int x, int y) {
        return x >= this.getLeft() && x <= this.right &&
                y >= this.getTop() && y <= this.getBottom();
    }

    /**
     * Used to update the background color according to the current element state.
     */
    protected void updateBackgroundColor() {
        if (this.enabled)
            setBackgroundColor(this.backgroundColorEnabled);
        else
            setBackgroundColor(this.backgroundColorDisabled);
    }

    /**
     * Used to set the current background of the element instance.
     *
     * @param color the new color.
     */
    protected void setBackgroundColor(int color) {
        if (this.currentBackgroundColor != color) {
            this.backgroundPaint.setColor(color);
            this.currentBackgroundColor = color;
        }
    }
}
