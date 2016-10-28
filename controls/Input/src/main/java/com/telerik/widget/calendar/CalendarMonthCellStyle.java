package com.telerik.widget.calendar;

import android.graphics.Typeface;

public class CalendarMonthCellStyle extends CalendarCellStyle {

    private Integer textBackgroundColor;

    private CalendarMonthCellFilter filter;

    void applyMonthStyle(CalendarMonthCell cell) {
        if(filter == null) {
            return;
        }
        if(!filter.applyMonthFilter(cell)) {
            return;
        }

        if(getBorderWidth() != null) {
            cell.setBorderWidth(getBorderWidth());
        }
        if(getBorderColor() != null) {
            cell.setBorderColor(getBorderColor());
        }
        if(getTextColor() != null) {
            cell.setTextColor(getTextColor(), getTextColor());
        }
        if(getBackgroundColor() != null) {
            cell.setBackgroundColor(getBackgroundColor(), getBackgroundColor());
        }
        if(getTextSize() != null) {
            cell.setTextSize(getTextSize());
        }
    }

    void applyDayStyle(CalendarMonthCell monthCell, CalendarMonthCell.MonthCellElement element) {
        if(getFilter() == null) {
            return;
        }
        if(getFilter().applyDayFilter(monthCell, element)) {
            if(getTextBackgroundColor() != null) {
                element.backgroundColor = getTextBackgroundColor();
            }
            if(getTextColor() != null) {
                element.color = getTextColor();
            }
            if(getTextSize() != null) {
                element.textSize = getTextSize();
            }
            if(getFontStyle() != null || getFontName() != null) {
                element.typeface = Typeface.create(getFontName(), getFontStyle() != null ? getFontStyle() : Typeface.NORMAL);
            }
        }
    }

    public Integer getTextBackgroundColor() {
        return textBackgroundColor;
    }

    public void setTextBackgroundColor(Integer textBackgroundColor) {
        this.textBackgroundColor = textBackgroundColor;
    }

    public CalendarMonthCellFilter getFilter() {
        return filter;
    }

    public void setFilter(CalendarMonthCellFilter filter) {
        this.filter = filter;
    }
}
