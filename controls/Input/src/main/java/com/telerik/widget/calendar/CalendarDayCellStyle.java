package com.telerik.widget.calendar;

import android.graphics.Typeface;

public class CalendarDayCellStyle extends CalendarCellStyle {

    private CalendarDayCellFilter filter;

    private Integer paddingHorizontal;
    private Integer paddingVertical;

    private Integer textPosition;

    void apply(CalendarDayCell cell) {
        if(filter == null) {
            return;
        }
        if(!filter.apply(cell)) {
            return;
        }
        if(getBorderWidth() != null && filter.getIsSelected() == null) {
            cell.setDrawBorderInsideCell(true);
            cell.setBorderWidth(getBorderWidth());
        }
        if(getBorderColor() != null && filter.getIsSelected() == null) {
            cell.setDrawBorderInsideCell(true);
            cell.setBorderColor(getBorderColor());
        }
        if(textPosition != null) {
            cell.setTextPosition(textPosition);
        }
        if(paddingHorizontal != null) {
            cell.setPaddingHorizontal(paddingHorizontal);
        }
        if(paddingVertical != null) {
            cell.setPaddingVertical(paddingVertical);
        }
        if(getTextColor() != null) {
            if(filter.getIsFromCurrentMonth() == null) {
                cell.setTextColor(getTextColor(), getTextColor());
            } else if(filter.getIsFromCurrentMonth()) {
                cell.setTextColor(getTextColor());
            } else {
                cell.setTextColor(cell.getTextColorEnabled(), getTextColor());
            }
        }
        if(getBackgroundColor() != null) {
            if(filter.getIsFromCurrentMonth() == null) {
                cell.setBackgroundColor(getBackgroundColor(), getBackgroundColor());
            } else if(filter.getIsFromCurrentMonth()) {
                cell.setBackgroundColor(getBackgroundColor());
            } else {
                cell.setBackgroundColor(cell.getBackgroundColorEnabled(), getBackgroundColor());
            }
        }
        if(getTextSize() != null) {
            cell.setTextSize(getTextSize());
        }
        if(getFontStyle() != null || getFontName() != null) {
            Typeface typeface = Typeface.create(getFontName(), getFontStyle() != null ? getFontStyle() : Typeface.NORMAL);
            cell.setTypeface(typeface);
        }
    }

    public Integer getPaddingHorizontal() {
        return paddingHorizontal;
    }

    public void setPaddingHorizontal(Integer paddingHorizontal) {
        this.paddingHorizontal = paddingHorizontal;
    }

    public Integer getPaddingVertical() {
        return paddingVertical;
    }

    public void setPaddingVertical(Integer paddingVertical) {
        this.paddingVertical = paddingVertical;
    }

    public Integer getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(Integer textPosition) {
        this.textPosition = textPosition;
    }

    public CalendarDayCellFilter getFilter() {
        return filter;
    }

    public void setFilter(CalendarDayCellFilter filter) {
        this.filter = filter;
    }
}
