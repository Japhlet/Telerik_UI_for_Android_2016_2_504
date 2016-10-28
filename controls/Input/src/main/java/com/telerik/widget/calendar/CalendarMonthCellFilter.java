package com.telerik.widget.calendar;

import com.telerik.android.common.Function;

public class CalendarMonthCellFilter {
    private Boolean textIsDate;
    private Boolean textIsToday;
    private Boolean textIsWeekend;
    private Boolean textIsDayName;
    private Boolean textIsMonthName;
    private Function<Long, Boolean> textIsCustomDate;
    private Boolean monthIsCurrent;
    private Boolean monthIsCompact;
    private Function<Long, Boolean> monthIsCustomDate;

    boolean applyMonthFilter(CalendarMonthCell monthCell) {
        if(monthIsCurrent != null) {
            if((!monthCell.isToday() && monthIsCurrent) || (monthCell.isToday() && !monthIsCurrent)) {
                return false;
            }
        }
        if(monthIsCustomDate != null) {
            if(!monthIsCustomDate.apply(monthCell.getDate())) {
                return false;
            }
        }
        if(monthIsCompact != null) {
            if((monthIsCompact && !monthCell.owner.isYearModeCompact()) || (!monthIsCompact && monthCell.owner.isYearModeCompact())) {
                return false;
            }
        }
        return true;
    }

    boolean applyDayFilter(CalendarMonthCell monthCell, CalendarMonthCell.MonthCellElement element) {
        if(monthIsCustomDate != null) {
            if(!monthIsCustomDate.apply(monthCell.getDate())) {
                return false;
            }
        }
        if(monthIsCompact != null) {
            if((monthIsCompact && !monthCell.owner.isYearModeCompact()) || (!monthIsCompact && monthCell.owner.isYearModeCompact())) {
                return false;
            }
        }
        if(element instanceof CalendarMonthCell.MonthCellDateElement) {
            if(!applyDateFilter(monthCell, (CalendarMonthCell.MonthCellDateElement) element)) {
                return false;
            }
        } else if(element instanceof CalendarMonthCell.MonthCellTitleElement) {
            if(!applyTitleFilter(monthCell, (CalendarMonthCell.MonthCellTitleElement) element)) {
                return false;
            }
        } else if(element instanceof CalendarMonthCell.MonthCellDayNameElement){
            if(!applyDayNameFilter(monthCell, (CalendarMonthCell.MonthCellDayNameElement) element)) {
                return false;
            }
        }
        return true;
    }

    private boolean applyDateFilter(CalendarMonthCell monthCell, CalendarMonthCell.MonthCellDateElement element) {

        if(textIsCustomDate != null) {
            if(!textIsCustomDate.apply(element.time)) {
                return false;
            }
        }

        if(textIsDate != null && !textIsDate) {
            return false;
        }

        if(textIsDayName != null && textIsDayName) {
            return false;
        }

        if(textIsMonthName != null && textIsMonthName) {
            return false;
        }

        if(textIsToday != null) {
            if(!applyTodayFilter(element)) {
                return false;
            }
        }

        if(textIsWeekend != null) {
            if(!applyWeekendFilter(monthCell, element.isWeekend)) {
                return false;
            }
        }

        if(monthIsCurrent != null) {
            if((!monthCell.isToday() && monthIsCurrent) || (monthCell.isToday() && !monthIsCurrent)) {
                return false;
            }
        }

        return true;
    }

    private boolean applyTitleFilter(CalendarMonthCell monthCell, CalendarMonthCell.MonthCellTitleElement element) {
        if(textIsDate != null && textIsDate) {
            return false;
        }

        if(textIsDayName != null && textIsDayName) {
            return false;
        }

        if(textIsMonthName != null && !textIsMonthName) {
            return false;
        }

        if(textIsCustomDate != null) {
            return false;
        }

        if(textIsWeekend != null) {
            return false;
        }

        if(monthIsCurrent != null) {
            if((!monthCell.isToday() && monthIsCurrent) || (monthCell.isToday() && !monthIsCurrent)) {
                return false;
            }
        }
        return true;
    }

    private boolean applyDayNameFilter(CalendarMonthCell monthCell, CalendarMonthCell.MonthCellDayNameElement element) {
        if(textIsDate != null && textIsDate) {
            return false;
        }

        if(textIsDayName != null && !textIsDayName) {
            return false;
        }

        if(textIsMonthName != null && textIsMonthName) {
            return false;
        }

        if(textIsCustomDate != null) {
            return false;
        }

        if(textIsWeekend != null) {
            if(!applyWeekendFilter(monthCell, element.isWeekend)) {
                return false;
            }
        }

        if(monthIsCurrent != null) {
            if((!monthCell.isToday() && monthIsCurrent) || (monthCell.isToday() && !monthIsCurrent)) {
                return false;
            }
        }
        return true;
    }

    private boolean applyTodayFilter(CalendarMonthCell.MonthCellDateElement element) {
        boolean cellIsToday = element.isToday;
        if((cellIsToday && !textIsToday) || (!cellIsToday && textIsToday)) {
            return false;
        }
        return true;
    }

    private boolean applyWeekendFilter(CalendarMonthCell monthCell, boolean isWeekend) {
        if((textIsWeekend && !isWeekend)||(!textIsWeekend && isWeekend)) {
            return false;
        }
        return true;
    }

    public Function<Long, Boolean> getMonthIsCustomDate() {
        return monthIsCustomDate;
    }

    public void setMonthIsCustomDate(Function<Long, Boolean> monthIsCustomDate) {
        this.monthIsCustomDate = monthIsCustomDate;
    }

    public Function<Long, Boolean> getTextIsCustomDate() {
        return textIsCustomDate;
    }

    public void setTextIsCustomDate(Function<Long, Boolean> textIsCustomDate) {
        this.textIsCustomDate = textIsCustomDate;
    }

    public Boolean getTextIsDate() {
        return textIsDate;
    }

    public void setTextIsDate(Boolean textIsDate) {
        this.textIsDate = textIsDate;
    }

    public Boolean getTextIsDayName() {
        return textIsDayName;
    }

    public void setTextIsDayName(Boolean textIsDayName) {
        this.textIsDayName = textIsDayName;
    }

    public Boolean getTextIsMonthName() {
        return textIsMonthName;
    }

    public void setTextIsMonthName(Boolean textIsMonthName) {
        this.textIsMonthName = textIsMonthName;
    }

    public Boolean getTextIsToday() {
        return textIsToday;
    }

    public void setTextIsToday(Boolean textIsToday) {
        this.textIsToday = textIsToday;
    }

    public Boolean getTextIsWeekend() {
        return textIsWeekend;
    }

    public void setTextIsWeekend(Boolean textIsWeekend) {
        this.textIsWeekend = textIsWeekend;
    }

    public Boolean getMonthIsCurrent() {
        return monthIsCurrent;
    }

    public void setMonthIsCurrent(Boolean monthIsCurrent) {
        this.monthIsCurrent = monthIsCurrent;
    }

    public Boolean getMonthIsCompact() {
        return monthIsCompact;
    }

    public void setMonthIsCompact(Boolean monthIsCompact) {
        this.monthIsCompact = monthIsCompact;
    }
}
