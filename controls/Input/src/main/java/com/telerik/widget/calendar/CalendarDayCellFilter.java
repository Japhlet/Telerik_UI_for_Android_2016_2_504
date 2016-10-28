package com.telerik.widget.calendar;

import com.telerik.android.common.Function;

import java.util.Calendar;

public class CalendarDayCellFilter {
    private CalendarCellType cellType = CalendarCellType.Date;
    private Boolean isToday;
    private Boolean isWeekend;
    private Boolean isSelected;
    private Boolean isFromCurrentMonth;
    private CalendarDisplayMode calendarDisplayMode;
    private Function<CalendarCell, Boolean> custom;

    boolean apply(CalendarDayCell cell) {
        if(custom != null) {
            if(!custom.apply(cell)) {
                return false;
            }
        }
        if(cellType != null) {
            if (!applyCellTypeFilter(cell)) {
                return false;
            }
        }
        if(isToday != null) {
            if(!applyTodayFilter(cell)) {
                return false;
            }
        }
        if(isWeekend != null ) {
            if(!applyWeekendFilter(cell)) {
                return false;
            }
        }
        if(isSelected != null) {
            if(!applySelectedFilter(cell)) {
                return false;
            }
        }
        if(isFromCurrentMonth != null) {
            if(!applyIsFromCurrentMonthFilter(cell)) {
                return false;
            }
        }
        if(calendarDisplayMode != null) {
            if(!applyCalendarDisplayModeFilter(cell)) {
                return false;
            }
        }
        return true;
    }

    private boolean applyCellTypeFilter(CalendarCell cell) {
        if(cellType == cell.getCellType()) {
            return true;
        }
        return false;
    }

    private boolean applyCalendarDisplayModeFilter(CalendarCell cell) {
        if(calendarDisplayMode == cell.owner.getDisplayMode()) {
            return true;
        }
        return false;
    }

    private boolean applyIsFromCurrentMonthFilter(CalendarDayCell cell) {
        if(cellType != CalendarCellType.Date && cellType != CalendarCellType.WeekNumber) {
            return false;
        }
        boolean cellIsFromCurrentMonth = cell.getIsFromCurrentMonth();
        if((cellIsFromCurrentMonth && !isFromCurrentMonth) || !cellIsFromCurrentMonth && isFromCurrentMonth) {
            return false;
        }
        return true;
    }

    private boolean applySelectedFilter(CalendarDayCell cell) {
        if(cellType != CalendarCellType.Date) {
            return false;
        }
        boolean cellIsSelected = cell.isSelected();
        if((cellIsSelected && !isSelected) || !cellIsSelected && isSelected) {
            return false;
        }
        return true;
    }

    private boolean applyTodayFilter(CalendarDayCell cell) {
        if(cellType != CalendarCellType.Date) {
            return false;
        }

        boolean cellIsToday = cell.isToday();

        if((cellIsToday && !isToday) || (!cellIsToday && isToday)) {
            return false;
        }
        return true;
    }

    private boolean applyWeekendFilter(CalendarDayCell cell) {
        if (cell.getCellType() == CalendarCellType.Date || cell.getCellType() == CalendarCellType.DayName) {
            return cell.isWeekend();
        }
        return false;
    }

    public CalendarDisplayMode getCalendarDisplayMode() {
        return calendarDisplayMode;
    }

    public void setCalendarDisplayMode(CalendarDisplayMode calendarDisplayMode) {
        this.calendarDisplayMode = calendarDisplayMode;
    }

    public CalendarCellType getCellType() {
        return cellType;
    }

    public void setCellType(CalendarCellType cellType) {
        this.cellType = cellType;
    }

    public Function<CalendarCell, Boolean> getCustom() {
        return custom;
    }

    public void setCustom(Function<CalendarCell, Boolean> custom) {
        this.custom = custom;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsFromCurrentMonth() {
        return isFromCurrentMonth;
    }

    public void setIsFromCurrentMonth(Boolean isFromCurrentMonth) {
        this.isFromCurrentMonth = isFromCurrentMonth;
    }

    public Boolean getIsToday() {
        return isToday;
    }

    public void setIsToday(Boolean isToday) {
        this.isToday = isToday;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }
}
