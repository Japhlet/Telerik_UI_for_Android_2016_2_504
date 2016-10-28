package com.telerik.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * The class holding the logic common for every cell type in the calendar.
 */
public abstract class CalendarCell extends CalendarTextElement {

    private Paint borderPaint;

    private boolean lastCellInRow;
    private boolean drawBorderInsideCell = false;

    /**
     * The type of the cell.
     */
    protected CalendarCellType cellType;
    /**
     * States whether the cell is holding the today's date.
     */
    protected boolean cellToday;
    private long date;
    private boolean isWeekend = false;
    private CalendarRow row;
    private int borderColor;
    private float borderWidth;

    /**
     * Initializes a new instance of the {@link CalendarCell} class with passed
     * {@link Context}.
     *
     * @param owner the calendar instance owning this cell.
     */
    public CalendarCell(RadCalendarView owner) {
        super(owner);
    }

    public boolean isLastCellInRow() {
        return lastCellInRow;
    }

    public void setLastCellInRow(boolean lastCellInRow) {
        this.lastCellInRow = lastCellInRow;
    }

    /**
     * Gets the border color of the cell.
     *
     * @return the current border color.
     */
    public int getBorderColor() {
        return this.borderColor;
    }

    /**
     * Sets the border color of the cell.
     *
     * @param color the new border color.
     */
    public void setBorderColor(int color) {
        if (this.borderColor != color) {
            this.borderColor = color;
            updateBorder();
        }
    }

    /**
     * Gets the border width of the cell.
     *
     * @return the current border width.
     */
    public float getBorderWidth() {
        return this.borderWidth;
    }

    /**
     * Sets the border width of the cell.
     *
     * @param width the new border width.
     */
    public void setBorderWidth(float width) {
        if (this.borderWidth != width) {
            this.borderWidth = width;
            updateBorder();
        }
    }

    public boolean isWeekend() {
        return this.isWeekend;
    }

    /**
     * Gets the row that currently holds the cell.
     *
     * @return the current row.
     */
    public CalendarRow getRow() {
        return this.row;
    }

    /**
     * Sets the current row holding the cell.
     *
     * @param row the new holding row.
     */
    public void setRow(CalendarRow row) {
        this.row = row;
    }

    /**
     * Gets the {@link CalendarCellType}
     * object that defines the type of this CalendarCell.
     *
     * @return the cell type
     */
    public CalendarCellType getCellType() {
        return this.cellType;
    }

    /**
     * Sets a {@link CalendarCellType}
     * object that defines the type of information that will be visualized with
     * this CalendarCell - date, day name, week number, etc.
     *
     * @param value the new cell type value
     */
    public void setCellType(CalendarCellType value) {
        if (this.cellType != value) {
            this.cellType = value;
        }
    }

    /**
     * Gets the date represented by the current CalendarCell.
     *
     * @return the represented date
     */
    public long getDate() {
        return this.date;
    }

    /**
     * Sets the date represented by the current CalendarCell.
     *
     * @param date the represented date
     */
    public void setDate(long date) {
        if (this.date != date) {
            this.date = CalendarTools.getDateStart(date);
            this.isWeekend = CalendarTools.isWeekend(date);
        }
    }

    /**
     * The border paint.
     *
     * @return the current border paint.
     */
    protected Paint borderPaint() {
        if (this.borderPaint == null) {
            this.borderPaint = new Paint();
            this.borderPaint.setStyle(Paint.Style.STROKE);
        }

        return this.borderPaint;
    }

    /**
     * Gets a value that determines whether the current cell holds the today's date.
     *
     * @return <code>true</code> if the today's date is being presented by this cell, <code>false</code> otherwise.
     */
    public boolean isToday() {
        return this.cellToday;
    }

    /**
     * Sets a value that determines whether the current cell holds the today's date.
     *
     * @param cellToday <code>true</code> if the today's date is being presented by this cell, <code>false</code> otherwise.
     */
    public void setAsToday(boolean cellToday) {
        if (this.cellToday != cellToday) {
            this.cellToday = cellToday;

            if (!cellToday && (this.borderColor == Color.TRANSPARENT || this.borderWidth == 0))
                this.borderPaint = null;
        }
    }

    protected void updateBorder() {
        if (this.borderColor == Color.TRANSPARENT)
            this.borderPaint = null;
        else {
            borderPaint().setColor(this.borderColor);
            borderPaint().setStrokeWidth(this.borderWidth);
        }
    }

    @Override
    public void postRender(Canvas canvas) {
        super.postRender(canvas);

        if (this.borderPaint != null) {
            int left = getLeft();
            int top = getTop();
            int right = getRight();
            int bottom = getBottom();
            if(drawBorderInsideCell) {
                float strokeWidth = borderPaint.getStrokeWidth() / 2;
                left += (int)strokeWidth;
                top += (int)strokeWidth;
                right -= (int)strokeWidth;
                bottom -= (int)strokeWidth;
            }
            if(owner.getDisplayMode() == CalendarDisplayMode.Week && borderPaint.getStrokeWidth() % 2 != 0) {
                top += 1;
            }
            canvas.drawRect(left, top, right, bottom, this.borderPaint);
        }
    }

    public boolean isDrawBorderInsideCell() {
        return drawBorderInsideCell;
    }

    public void setDrawBorderInsideCell(boolean value) {
        this.drawBorderInsideCell = value;
    }
}
