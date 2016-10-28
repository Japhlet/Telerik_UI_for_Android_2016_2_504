package com.telerik.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.telerik.widget.calendar.events.Event;

import java.util.List;

/**
 * Class holding the logic specific for day cells.
 */
public class CalendarDayCell extends CalendarCell {

    private static final int SELECTABLE_ENABLED_ALPHA = 255;
    private static final int SELECTABLE_DISABLED_ALPHA = 76;

    private boolean isFromCurrentMonth;
    private boolean selected;
    private boolean selectable = true;
    private boolean hasDecoration;

    private String secondaryText;
    private int secondaryTextDrawLocationX;
    private int secondaryTextDrawLocationY;

    private Paint secondaryTextPaint;
    //private boolean secondaryTextUsesDefaultLocation = true; // TODO

    private int secondaryTextColorEnabled;
    private int secondaryTextColorDisabled;
    private float secondaryTextSize;
    private int secondaryTextPosition;
    private Rect secondaryTextBounds;

    private List<Event> events;

    /**
     * Initializes a new instance of the {@link com.telerik.widget.calendar.CalendarCell} class.
     *
     * @param owner the calendar instance owning this cell.
     */
    public CalendarDayCell(RadCalendarView owner) {
        super(owner);
    }

    /**
     * Gets a value that states whether the cell can be selected or not. This will not be taken in consideration if the cell's date is
     * lesser than the current minimum or greater than the current maximum if such are present.
     *
     * @return <code>true</code> if the cell can be selected, <code>false</code> otherwise.
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Sets a value that states whether the cell can be selected or not. This will not be taken in consideration if the cell's date is
     * lesser than the current minimum or greater than the current maximum if such are present.
     *
     * @param selectable the new selectable state.
     */
    public void setSelectable(boolean selectable) {
        if (this.selectable != selectable) {
            this.selectable = selectable;

            owner.applyDayCellStyles(this);
        }
    }

    @Override
    protected void updateTextColor() {
        super.updateTextColor();

        if (this.selectable)
            this.textPaint.setAlpha(SELECTABLE_ENABLED_ALPHA);
        else
            this.textPaint.setAlpha(SELECTABLE_DISABLED_ALPHA);
    }

    @Override
    protected void onAlphaChanged() {
        super.onAlphaChanged();
        if (secondaryTextPaint != null)
            this.secondaryTextPaint.setAlpha(this.alpha);
    }

    @Override
    public void setVisibility(ElementVisibility visibility) {
        super.setVisibility(visibility);
    }

    /**
     * Gets a {@link boolean}
     * value that specifies whether this CalendarCell is part from the month
     * that is currently visualized.
     *
     * @return whether the button is from current view
     */
    public boolean getIsFromCurrentMonth() {
        return this.isFromCurrentMonth;
    }

    /**
     * Sets a {@link boolean}
     * value that specifies whether this CalendarCell is part
     * from the currently displayed month.
     *
     * @param value the value that specifies whether this button is from current month
     */
    public void setIsFromCurrentMonth(boolean value) {
        if (this.isFromCurrentMonth == value)
            return;

        this.isFromCurrentMonth = value;
        this.setEnabled(value);

        owner.applyDayCellStyles(this);
    }

    /**
     * Gets a {@link java.lang.String}
     * value which represents a secondary text information contained in the
     * current CalendarCell.
     *
     * @return the value of the secondary text
     */
    public String getSecondaryText() {
        return this.secondaryText;
    }

    /**
     * Sets a {@link java.lang.String}
     * value that specifies additional information that is contained in the
     * current CalendarCell.
     *
     * @param value the value that specifies the secondary text
     */
    public void setSecondaryText(String value) {
        if (this.secondaryText == null || !this.secondaryText.equals(value)) {
            this.secondaryText = value;
            /*if (this.secondaryTextUsesDefaultLocation) {
                //this.requestLayout(); // TODO
            }*/
        }
    }

    /**
     * Provides access to the {@link android.graphics.Paint}
     * object which is used to render secondary text information
     * contained in the current CalendarCell.
     *
     * @return the paint for the secondary text
     */
    public Paint secondaryTextPaint() {
        if (this.secondaryTextPaint == null)
            this.secondaryTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        return this.secondaryTextPaint;
    }

    /**
     * Gets the events related for the date presented by the current CalendarCell.
     *
     * @return the events for the current cell
     */
    public List<Event> getEvents() {
        return this.events;
    }

    /**
     * Sets the events related for the date presented by the current CalendarCell.
     *
     * @param events the events for the current cell
     */
    public void setEvents(List<Event> events) {
        if(this.events == events) {
            return;
        }

        this.events = events;
        owner.applyDayCellStyles(this);
    }

    /**
     * Gets a {@link boolean}
     * value that specifies whether this CalendarCell is currently selected.
     *
     * @return whether the current button is selected
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Sets a {@link boolean}
     * value that specifies whether this CalendarCell is currently selected.
     *
     * @param value the value that specifies whether this button is selected
     */
    public void setSelected(boolean value) {
        if (this.selected != value) {
            this.selected = value;
            this.setHasDecoration(value);
            owner.getAdapter().updateDateCellStyle(this);
        }
    }

    /**
     * Gets the color to be used for the secondary text when the cell is enabled.
     *
     * @return the current enabled secondary text color.
     */
    public int getSecondaryTextColorEnabled() {
        return secondaryTextColorEnabled;
    }

    /**
     * Gets the color to be used for the secondary text when the cell is enabled.
     *
     * @return the current enabled secondary text color.
     */
    public int getSecondaryTextColorDisabled() {
        return secondaryTextColorDisabled;
    }

    /**
     * Sets the color for the secondary text for both enabled and disabled state.
     *
     * @param colorEnabled  the secondary color for enabled state.
     * @param colorDisabled the secondary color for disabled state.
     */
    public void setSecondaryTextColor(int colorEnabled, int colorDisabled) {
        if (this.secondaryTextColorEnabled == colorEnabled &&
                this.secondaryTextColorDisabled == colorDisabled)
            return;

        this.secondaryTextColorEnabled = colorEnabled;
        this.secondaryTextColorDisabled = colorDisabled;

        updateSecondaryTextColor();
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        if (this.secondaryText != null)
            canvas.drawText(this.secondaryText, (float) this.secondaryTextDrawLocationX,
                    (float) this.secondaryTextDrawLocationY, this.secondaryTextPaint());

        this.drawEvents(canvas);
    }

    @Override
    public void arrange(int left, int top, int right, int bottom) {
        super.arrange(left, top, right, bottom);

        if (this.getSecondaryText() != null
            //&& this.secondaryTextUsesDefaultLocation TODO
                ) {
            this.calculateSecondaryTextPosition();
        }
    }

    /**
     * Calculates the point where the secondary text is drawn.
     */
    protected void calculateSecondaryTextPosition() {
        if (this.secondaryText == null)
            return;

        if(secondaryTextBounds == null) {
            calculateSecondaryTextBounds();
        }

        if ((this.secondaryTextPosition & CENTER_HORIZONTAL) > 0) {
            this.secondaryTextDrawLocationX = (this.getLeft() + (this.getWidth() / 2) - (secondaryTextBounds.width() / 2));
        } else if ((this.secondaryTextPosition & LEFT) > 0) {
            this.secondaryTextDrawLocationX = (this.getLeft() + this.getPaddingLeft());
        } else { // RIGHT is default
            this.secondaryTextDrawLocationX = (this.getRight() - this.getPaddingRight() - secondaryTextBounds.width());
        }

        if ((this.secondaryTextPosition & CENTER_VERTICAL) > 0) {
            this.secondaryTextDrawLocationY = (this.getTop() + (this.getHeight() / 2) + (secondaryTextBounds.height() / 2));
        } else if ((this.secondaryTextPosition & BOTTOM) > 0) {
            this.secondaryTextDrawLocationY = (this.getBottom() - this.getPaddingBottom());
        } else { // TOP is default
            this.secondaryTextDrawLocationY = (this.getTop() + this.getPaddingTop() + secondaryTextBounds.height());
        }
    }

    private void calculateSecondaryTextBounds() {
        if(this.secondaryText == null) {
            return;
        }
        secondaryTextBounds = new Rect();
        this.secondaryTextPaint().getTextBounds(this.secondaryText, 0, this.secondaryText.length(), secondaryTextBounds);
    }

    /**
     * Gets a {@link boolean}
     * value that specifies whether this CalendarCell has a decoration border.
     *
     * @return whether the current button has decoration border
     */
    boolean getHasDecoration() {
        return this.hasDecoration;
    }

    /**
     * Sets a {@link boolean}
     * value that specifies whether this CalendarCell has a decoration border.
     *
     * @param value the value that specifies whether this button has decoration border
     */
    void setHasDecoration(boolean value) {
        if (this.hasDecoration != value) {
            this.hasDecoration = value;
        }
    }

    /**
     * Gets the current secondary text size.
     *
     * @return current secondary text size.
     */
    public float getSecondaryTextSize() {
        return secondaryTextSize;
    }

    /**
     * Sets the current secondary text size.
     *
     * @param secondaryTextSize new secondary text size.
     */
    public void setSecondaryTextSize(float secondaryTextSize) {
        if (this.secondaryTextSize == secondaryTextSize)
            return;

        this.secondaryTextSize = secondaryTextSize;
        this.secondaryTextPaint().setTextSize(secondaryTextSize);

        calculateSecondaryTextBounds();
        calculateSecondaryTextPosition();
    }

    /**
     * Gets the current secondary text position. Default is TOP | RIGHT from the {@link com.telerik.widget.calendar.CalendarTextElement} class.
     *
     * @return the current secondary text position.
     */
    public int getSecondaryTextPosition() {
        return secondaryTextPosition;
    }

    /**
     * Sets the current secondary text position. Default is TOP | RIGHT from the {@link com.telerik.widget.calendar.CalendarTextElement} class.
     *
     * @param secondaryTextPosition the new secondary text position.
     */
    public void setSecondaryTextPosition(int secondaryTextPosition) {
        this.secondaryTextPosition = secondaryTextPosition;
        calculateSecondaryTextPosition();
    }

    /**
     * Used to draw the events for the current cell.
     *
     * @param canvas the current canvas.
     */
    protected void drawEvents(Canvas canvas) {
        if (this.events != null && this.events.size() > 0) {
            this.owner.getEventAdapter().getRenderer().renderEvents(canvas, this);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean updateStyles = false;
        if(isEnabled() != enabled) {
            updateStyles = true;
        }
        super.setEnabled(enabled);
        if(updateStyles) {
            owner.applyDayCellStyles(this);
            updateSecondaryTextColor();
        }
    }

    /**
     * Updates the secondary color based on the current state of the cell.
     */
    protected void updateSecondaryTextColor() {
        if (this.enabled)
            this.secondaryTextPaint().setColor(this.secondaryTextColorEnabled);
        else
            this.secondaryTextPaint().setColor(this.secondaryTextColorDisabled);
    }
}
