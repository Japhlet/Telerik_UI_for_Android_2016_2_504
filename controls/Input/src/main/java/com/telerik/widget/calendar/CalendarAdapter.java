package com.telerik.widget.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.SparseArray;

import com.telerik.android.common.Function;
import com.telerik.android.common.Procedure;
import com.telerik.widget.calendar.events.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Default adapter for {@link com.telerik.widget.calendar.RadCalendarView}.
 */
public class CalendarAdapter {

    /**
     * The calendar instance owning this adapter.
     */
    protected final RadCalendarView owner;
    /**
     * The current context.
     */
    protected final Context context;
    /**
     * All current date cells.
     */
    protected final List<CalendarCell> dateCells;
    /**
     * All current week number cells. Does not include date cells with inline week numbers.
     */
    protected final List<CalendarCell> weekNumberCells;
    /**
     * All current day name cells.
     */
    protected final List<CalendarCell> dayNameCells;
    private final long todayDay;
    private final long todayMonth;
    private final long todayYear;
    private final SparseArray<String> monthNames;
    /**
     * The currently applied style.
     */
    protected CalendarStyle style;
    /**
     * The calendar used for working with dates within the adapter.
     */
    protected Calendar workCalendar;
    private Calendar calendar;
    private Locale locale;
    private SparseArray<String> dateValues = new SparseArray<String>();

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarAdapter}.
     */
    public CalendarAdapter(RadCalendarView owner) {
        this.context = owner.getContext();
        this.calendar = owner.getCalendar();
        this.workCalendar = owner.getCalendar();
        this.locale = owner.getLocale();
        this.owner = owner;

        this.todayDay = calendar.get(Calendar.DATE);
        this.todayMonth = calendar.get(Calendar.MONTH);
        this.todayYear = calendar.get(Calendar.YEAR);

        for (int i = 1, len = 32; i < len; i++) {
            this.dateValues.put(i, String.valueOf(i));
        }

        this.monthNames = new SparseArray<String>();

        updateMonthNamesCache();

        this.dateCells = new ArrayList<CalendarCell>();
        this.weekNumberCells = new ArrayList<CalendarCell>();
        this.dayNameCells = new ArrayList<CalendarCell>();
        this.style = new CalendarStyle();
    }

    void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.workCalendar = (Calendar) calendar.clone();
        updateMonthNamesCache();
    }

    void setLocale(Locale locale) {
        this.locale = locale;
        setCalendar(Calendar.getInstance(locale));
        updateMonthNamesCache();
    }

    public float getInlineEventTitleTextSize() {
        return this.style.inlineEventTitleTextSize;
    }

    public void setInlineEventTitleTextSize(float textSize) {
        if(style.inlineEventTitleTextSize == textSize) {
            return;
        }
        this.style.inlineEventTitleTextSize = textSize;
        owner.hideEvents(null);
    }

    public float getInlineEventTimeStartTextSize() {
        return style.inlineEventTimeStartTextSize;
    }

    public void setInlineEventTimeStartTextSize(float textSize) {
        if(style.inlineEventTimeStartTextSize == textSize) {
            return;
        }
        style.inlineEventTimeStartTextSize = textSize;
        owner.hideEvents(null);
    }

    public float getInlineEventTimeEndTextSize() {
        return style.inlineEventTimeEndTextSize;
    }

    public void setInlineEventTimeEndTextSize(float textSize) {
        if(style.inlineEventTimeEndTextSize == textSize) {
            return;
        }
        style.inlineEventTimeEndTextSize = textSize;
        owner.hideEvents(null);
    }

    public int getInlineEventTimeStartTextColor() {
        return style.inlineEventTimeStartTextColor;
    }

    public void setInlineEventTimeStartTextColor(int color) {
        if(style.inlineEventTimeStartTextColor == color) {
            return;
        }
        style.inlineEventTimeStartTextColor = color;
        owner.hideEvents(null);
    }

    public int getInlineEventTimeEndTextColor() {
        return style.inlineEventTimeEndTextColor;
    }

    public void setInlineEventTimeEndTextColor(int color) {
        if(style.inlineEventTimeEndTextColor == color) {
            return;
        }
        style.inlineEventTimeEndTextColor = color;
        owner.hideEvents(null);
    }

    public float getPopupEventTitleTextSize() {
        return style.popupEventTitleTextSize;
    }

    public void setPopupEventTitleTextSize(float textSize) {
        if(style.popupEventTitleTextSize == textSize) {
            return;
        }
        style.popupEventTitleTextSize = textSize;
        owner.hideEvents(null);
    }

    public float getPopupEventTimeTextSize() {
        return style.popupEventTimeTextSize;
    }

    public void setPopupEventTimeTextSize(float textSize) {
        if(style.popupEventTimeTextSize == textSize) {
            return;
        }
        style.popupEventTimeTextSize = textSize;
        owner.hideEvents(null);
    }

    public int getInlineEventsBackgroundColor() {
        return style.inlineEventsBackgroundColor;
    }

    public void setInlineEventsBackgroundColor(int color) {
        if(style.inlineEventsBackgroundColor == color) {
            return;
        }
        this.style.inlineEventsBackgroundColor = color;
        owner.hideEvents(null);
    }

    public int getPopupEventsWindowBackgroundColor() {
        return style.popupEventsWindowBackgroundColor;
    }

    public void setPopupEventsWindowBackgroundColor(int color) {
        if(style.popupEventsWindowBackgroundColor == color) {
            return;
        }
        this.style.popupEventsWindowBackgroundColor = color;
        owner.hideEvents(null);
    }

    public float getTodayCellBorderWidth() {
        return this.style.todayBorderWidth;
    }

    public void setTodayCellBorderWidth(float width) {
        if (this.style.todayBorderWidth == width)
            return;

        this.style.todayBorderWidth = width;

        this.updateOwner();
    }

    public int getTodayCellBorderColor() {
        return this.style.todayBorderColor;
    }

    public void setTodayCellBorderColor(int color) {
        if (this.style.todayBorderColor == color)
            return;

        this.owner.beginUpdate();

        this.style.todayBorderColor = color;

        this.updateOwner();
    }

    public float getSelectedCellBorderWidth() {
        return this.style.decorationsStrokeWidth;
    }

    public void setSelectedCellBorderWidth(float width) {
        if (this.style.decorationsStrokeWidth == width) {
            return;
        }

        this.style.decorationsStrokeWidth = width;

        if(this.owner.getCellDecorator() != null) {
            this.owner.getCellDecorator().setStrokeWidth(width);
        }

        this.owner.invalidate();
    }

    /**
     * Gets the horizontal padding for the date cells.
     *
     * @return the current date cells horizontal padding.
     */
    public int getDateCellPaddingHorizontal() {
        return this.style.dateCellPaddingHorizontal;
    }

    /**
     * Sets the horizontal padding for the date cells.
     *
     * @param padding the new date cells horizontal padding.
     */
    public void setDateCellPaddingHorizontal(final int padding) {
        if (this.style.dateCellPaddingHorizontal == padding)
            return;

        this.owner.beginUpdate(true);
        this.style.dateCellPaddingHorizontal = padding;

        applyProcedureToWeekNumberCells(new Procedure<CalendarCell>() {
            @Override
            public void apply(CalendarCell argument) {
                argument.setPaddingHorizontal(padding);
            }
        });

        applyProcedureToDateCells(new Procedure<CalendarCell>() {
            @Override
            public void apply(CalendarCell argument) {
                argument.setPaddingHorizontal(padding);
            }
        });

        this.owner.endUpdate(true);
    }

    /**
     * Gets the vertical padding for the date cells.
     *
     * @return the current date cells vertical padding.
     */
    public int getDateCellPaddingVertical() {
        return this.style.dateCellPaddingVertical;
    }

    /**
     * Sets the vertical padding for the date cells.
     *
     * @param padding the new date cells vertical padding.
     */
    public void setDateCellPaddingVertical(final int padding) {
        if (this.style.dateCellPaddingVertical == padding)
            return;

        this.owner.beginUpdate(true);
        this.style.dateCellPaddingVertical = padding;
        applyProcedureToWeekNumberCells(new Procedure<CalendarCell>() {
            @Override
            public void apply(CalendarCell argument) {
                argument.setPaddingVertical(padding);
            }
        });
        applyProcedureToDateCells(new Procedure<CalendarCell>() {
            @Override
            public void apply(CalendarCell argument) {
                argument.setPaddingVertical(padding);
            }
        });

        this.owner.endUpdate(true);
    }

    /**
     * Gets the horizontal padding for the month cells.
     *
     * @return the current month cells horizontal padding.
     */
    public int getMonthCellPaddingHorizontal() {
        return this.style.monthCellPaddingHorizontal;
    }

    /**
     * Sets the horizontal padding for the month cells.
     *
     * @param padding the new month cells horizontal padding.
     */
    public void setMonthCellPaddingHorizontal(final int padding) {
        if (this.style.monthCellPaddingHorizontal == padding)
            return;

        this.owner.beginUpdate(true);
        this.style.monthCellPaddingHorizontal = padding;
        applyProcedureToDateCells(new Procedure<CalendarCell>() {
            @Override
            public void apply(CalendarCell argument) {
                if (argument instanceof CalendarMonthCell)
                    argument.setPaddingHorizontal(padding);
            }
        });

        this.owner.endUpdate(true);
    }

    /**
     * Gets the vertical padding for the month cells.
     *
     * @return the current month cells vertical padding.
     */
    public int getMonthCellPaddingVertical() {
        return this.style.monthCellPaddingVertical;
    }

    /**
     * Sets the vertical padding for the month cells.
     *
     * @param padding the new month cells vertical padding.
     */
    public void setMonthCellPaddingVertical(final int padding) {
        if (this.style.monthCellPaddingVertical == padding)
            return;

        this.owner.beginUpdate(true);
        this.style.monthCellPaddingVertical = padding;
        applyProcedureToDateCells(new Procedure<CalendarCell>() {
            @Override
            public void apply(CalendarCell argument) {
                if (argument instanceof CalendarMonthCell)
                    argument.setPaddingVertical(padding);
            }
        });

        this.owner.endUpdate(true);
    }

    /**
     * Gets the today's typeface in year mode.
     *
     * @return the current today's typeface.
     */
    public Typeface getTodayTypeFace() {
        return CalendarMonthCell.todayTypeFace;
    }

    /**
     * Sets the today's typeface in year mode.
     *
     * @param typeface the new today's typeface.
     */
    public void setTodayTypeFace(Typeface typeface) {
        if (CalendarMonthCell.todayTypeFace == typeface)
            return;

        CalendarMonthCell.todayTypeFace = typeface;
        this.owner.invalidate();
    }

    /**
     * Gets the today's typeface for the today cell.
     *
     * @return the current today's typeface.
     */
    public Typeface getTodayCellTypeFace() {
        return style.todayTypeFace;
    }

    /**
     * Sets the today's typeface for the today cell.
     *
     * @param typeface the new today's typeface.
     */
    public void setTodayCellTypeFace(Typeface typeface) {
        if (style.todayTypeFace == typeface)
            return;

        style.todayTypeFace = typeface;

        this.updateOwner();
    }

    /**
     * Gets today's background color in year mode.
     *
     * @return the current today's background color.
     */
    public int getTodayBackgroundColor() {
        return CalendarMonthCell.todayBackgroundColor;
    }

    /**
     * Sets today's background color in year mode.
     *
     * @param color the new today's background color.
     */
    public void setTodayBackgroundColor(int color) {
        if (CalendarMonthCell.todayBackgroundColor == color)
            return;

        CalendarMonthCell.todayBackgroundColor = color;

        this.owner.invalidate();
    }

    /**
     * Gets today's text color in year mode.
     *
     * @return the current today's text color.
     */
    public int getTodayTextColor() {
        return CalendarMonthCell.todayTextColor;
    }

    /**
     * Sets today's text color in year mode.
     *
     * @param color the new today's text color.
     */
    public void setTodayTextColor(int color) {
        if (CalendarMonthCell.todayTextColor == color)
            return;

        CalendarMonthCell.todayTextColor = color;
        applyProcedureToDateCells(new Procedure<CalendarCell>() {
            @Override
            public void apply(CalendarCell argument) {
                argument.updateTextColor();
            }
        });

        this.owner.invalidate();
    }

    /**
     * Gets today's text color for the today cell.
     *
     * @return the current today's text color.
     */
    public int getTodayCellTextColor() {
        return style.todayCellTextColor;
    }

    /**
     * Sets today's text color for the today cell.
     *
     * @param color the new today's text color.
     */
    public void setTodayCellTextColor(int color) {
        if (this.style.todayCellTextColor == color)
            return;

        this.style.todayCellTextColor = color;

        this.updateOwner();
    }

    /**
     * Gets today's text color for the today cell in selected state.
     *
     * @return the current today's text color in selected state.
     */
    public int getTodayCellSelectedTextColor() {
        return style.todayCellTextColor;
    }

    /**
     * Sets today's text color for the today cell in selected state.
     *
     * @param color the new today's text color in selected state.
     */
    public void setTodayCellSelectedTextColor(int color) {
        if (style.todayCellTextColor == color)
            return;

        this.style.todayCellSelectedTextColor = color;

        this.updateOwner();
    }

    /**
     * Gets the current collection of date values cached.
     *
     * @return the current date values collection cached.
     */
    public SparseArray<String> getDateValues() {
        return dateValues;
    }

    /**
     * Sets the current collection of date values cached.
     *
     * @param dateValues the new date values collection cached.
     */
    public void setDateValues(SparseArray<String> dateValues) {
        this.dateValues = dateValues;

        this.owner.beginUpdate();
        this.owner.rebuildCalendar();
        this.owner.endUpdate();
    }

    /**
     * Gets the style.
     *
     * @return the currently applied style.
     */
    public CalendarStyle getStyle() {
        return style;
    }

    /**
     * Sets the style.
     *
     * @param style the new calendar style.
     */
    public void setStyle(CalendarStyle style) {
        if (style == null)
            throw new NullPointerException("style");

        applyStyle(style);
        this.style = style;

        this.updateOwner();
    }

    /**
     * Gets the text position of the title. Expected values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @return the current title text position.
     */
    public int getTitleTextPosition() {
        return this.style.titleTextPosition;
    }

    /**
     * Sets the text position of the title. Accepted values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @param position the new title text position.
     */
    public void setTitleTextPosition(int position) {
        if (this.style.titleTextPosition == position)
            return;

        this.style.titleTextPosition = position;
        this.owner.title().setTextPosition(position);
    }

    /**
     * Gets the title's typeface.
     *
     * @return the current typeface for the title.
     */
    public Typeface getTitleTypeFace() {
        return this.style.titleTypeFace;
    }

    /**
     * Sets the title's typeface.
     *
     * @param typeFace the new typeface for the title.
     */
    public void setTitleTypeFace(Typeface typeFace) {
        if (this.style.titleTypeFace == typeFace)
            return;

        this.style.titleTypeFace = typeFace;
        this.owner.title().setTypeface(typeFace);
    }

    /**
     * Gets the title text size.
     *
     * @return the current title text size.
     */
    public float getTitleTextSize() {
        return this.style.titleTextSize;
    }

    /**
     * Sets the title text size.
     *
     * @param size the new title text size.
     */
    public void setTitleTextSize(float size) {
        if (this.style.titleTextSize == size)
            return;

        this.style.titleTextSize = size;
        this.owner.title().setTextSize(size);
    }

    /**
     * Gets the title background color.
     *
     * @return the current title background color.
     */
    public int getTitleBackgroundColor() {
        return this.style.titleBackgroundColor;
    }

    /**
     * Sets the title background color.
     *
     * @param color the new title background color.
     */
    public void setTitleBackgroundColor(int color) {
        if (this.style.titleBackgroundColor == color)
            return;

        this.style.titleBackgroundColor = color;
        this.owner.title().setBackgroundColor(color, color);
    }

    /**
     * Gets the title text color.
     *
     * @return the current title text color.
     */
    public int getTitleTextColor() {
        return this.style.titleTextColor;
    }

    /**
     * Sets the title text color.
     *
     * @param color the new title text color.
     */
    public void setTitleTextColor(int color) {
        if (this.style.titleTextColor == color)
            return;

        this.style.titleTextColor = color;
        this.owner.title().setTextColor(color);
    }

    /**
     * Gets the selected cells background color.
     *
     * @return the current selected cells background color.
     */
    public int getSelectedCellBackgroundColor() {
        return style.selectedCellBackgroundColor;
    }

    /**
     * Sets the selected cells background color.
     *
     * @param color the new selected cells background color.
     */
    public void setSelectedCellBackgroundColor(int color) {
        if (style.selectedCellBackgroundColor == color)
            return;

        this.style.selectedCellBackgroundColor = color;

        this.updateOwner();
    }

    /**
     * Gets the text color for selected cells.
     *
     * @return the current selected cells text color.
     */
    public int getSelectedCellTextColor() {
        return this.style.selectedCellTextColor;
    }

    /**
     * Sets the text color for selected cells.
     *
     * @param color the new selected cells text color.
     */
    public void setSelectedCellTextColor(int color) {
        if (this.style.selectedCellTextColor == color)
            return;

        this.style.selectedCellTextColor = color;

        this.updateOwner();
    }

    /**
     * Gets the typeface for selected cells.
     *
     * @return the current selected cells typeface.
     */
    public Typeface getSelectedCellTypeFace() {
        return this.style.selectedCellTypeFace;
    }

    /**
     * Sets the typeface for selected cells.
     *
     * @param typeFace the new selected cells typeface.
     */
    public void setSelectedCellTypeFace(Typeface typeFace) {
        if (this.style.selectedCellTypeFace == typeFace)
            return;

        this.style.selectedCellTypeFace = typeFace;

        this.updateOwner();
    }

    /**
     * Gets the today cell background color.
     *
     * @return the current today cell background color.
     */
    public int getTodayCellBackgroundColor() {
        return style.todayCellBackgroundColor;
    }

    /**
     * Sets the today cell background color.
     *
     * @param color the new today cell background color.
     */
    public void setTodayCellBackgroundColor(int color) {
        if (style.todayCellBackgroundColor == color)
            return;

        style.todayCellBackgroundColor = color;

        this.updateOwner();
    }

    /**
     * Gets the background color of the enabled date cells.
     *
     * @return the current date cells enabled color.
     */
    public int getDateCellBackgroundColorEnabled() {
        return style.dateCellBackgroundColorEnabled;
    }

    /**
     * Gets the background color of the disabled date cells.
     *
     * @return the current date cells disabled color.
     */
    public int getDateCellBackgroundColorDisabled() {
        return style.dateCellBackgroundColorDisabled;
    }

    /**
     * Sets the background color for both the enabled and the disabled cells.
     *
     * @param colorEnabled  the new date cells enabled color.
     * @param colorDisabled the new date cells disabled color.
     */
    public void setDateCellBackgroundColor(final int colorEnabled, final int colorDisabled) {
        if (this.style.dateCellBackgroundColorEnabled == colorEnabled &&
                this.style.dateCellBackgroundColorDisabled == colorDisabled)
            return;

        this.style.dateCellBackgroundColorEnabled = colorEnabled;
        this.style.dateCellBackgroundColorDisabled = colorDisabled;

        this.updateOwner();
    }

    /**
     * Gets the date text color enabled.
     *
     * @return the date text color enabled.
     */
    public int getDateTextColorEnabled() {
        return style.dateTextColorEnabled;
    }

    /**
     * Gets the date text color disabled.
     *
     * @return the date text color disabled.
     */
    public int getDateTextColorDisabled() {
        return style.dateTextColorDisabled;
    }

    /**
     * Sets the text color for both enabled and disabled dates.
     *
     * @param colorEnabled  the new enabled date text color.
     * @param colorDisabled the new disabled date text color.
     */
    public void setDateTextColor(final int colorEnabled, final int colorDisabled) {
        if (this.style.dateTextColorEnabled == colorEnabled &&
                this.style.dateTextColorDisabled == colorDisabled)
            return;

        this.style.dateTextColorEnabled = colorEnabled;
        this.style.dateTextColorDisabled = colorDisabled;

        this.updateOwner();
    }

    /**
     * Gets the day names text color.
     *
     * @return the current day names text color.
     */
    public int getDayNameTextColor() {
        return style.dayNameTextColor;
    }

    /**
     * Sets the day names text color.
     *
     * @param color the new day names text color.
     */
    public void setDayNameTextColor(final int color) {
        if (this.style.dayNameTextColor == color)
            return;

        this.style.dayNameTextColor = color;

        this.updateOwner();
    }

    /**
     * Gets the enabled week numbers text color.
     *
     * @return the current enabled week numbers text color.
     */
    public int getWeekNumberTextColorEnabled() {
        return style.weekNumberTextColorEnabled;
    }

    /**
     * Gets the disabled week numbers text color.
     *
     * @return the current disabled week numbers text color.
     */
    public int getWeekNumberTextColorDisabled() {
        return style.weekNumberTextColorDisabled;
    }

    /**
     * Sets the text color for both the enabled and the disabled week numbers.
     *
     * @param colorEnabled  the new enabled week number text color.
     * @param colorDisabled the new disabled week number text color.
     */
    public void setWeekNumberTextColor(final int colorEnabled, final int colorDisabled) {
        if (this.style.weekNumberTextColorEnabled == colorEnabled &&
                this.style.weekNumberTextColorDisabled == colorDisabled)
            return;

        this.style.weekNumberTextColorEnabled = colorEnabled;
        this.style.weekNumberTextColorDisabled = colorDisabled;

        this.updateOwner();
    }

    /**
     * Gets the month name enabled text color.
     *
     * @return the current month name enabled text color.
     */
    public int getMonthNameTextColorEnabled() {
        return style.monthNameTextColorEnabled;
    }

    /**
     * Gets the month name disabled text color.
     *
     * @return the current month name disabled text color.
     */
    public int getMonthNameTextColorDisabled() {
        return style.monthNameTextColorDisabled;
    }

    /**
     * Sets the text color for both enabled and disabled month names.
     *
     * @param colorEnabled  the new month name enabled text color.
     * @param colorDisabled the new month name disabled text color.
     */
    public void setMonthNameTextColor(final int colorEnabled, final int colorDisabled) {
        if (this.style.monthNameTextColorEnabled == colorEnabled &&
                this.style.monthNameTextColorDisabled == colorDisabled)
            return;

        this.style.monthNameTextColorEnabled = colorEnabled;
        this.style.monthNameTextColorDisabled = colorDisabled;

        this.updateOwner();
    }

    /**
     * Gets the month name text size.
     *
     * @return the current month name text size.
     */
    public float getMonthNameTextSize() {
        return this.style.monthNameTextSize;
    }

    /**
     * Sets the month name text size.
     *
     * @param size the new month name text size.
     */
    public void setMonthNameTextSize(final float size) {
        if (this.style.monthNameTextSize == size)
            return;

        this.style.monthNameTextSize = size;

        this.updateOwner();
    }

    /**
     * Gets the month name text size for compact mode.
     *
     * @return the current month name text size for compact mode.
     */
    public float getMonthNameTextSizeCompact() {
        return this.style.monthNameTextSizeCompact;
    }

    /**
     * Sets the month name text size for compact mode.
     *
     * @param size the new month name text size for compact mode.
     */
    public void setMonthNameTextSizeCompact(final float size) {
        if (this.style.monthNameTextSizeCompact == size)
            return;

        this.style.monthNameTextSizeCompact = size;

        this.updateOwner();
    }

    /**
     * Gets the month name typeface.
     *
     * @return the current month name typeface.
     */
    public Typeface getMonthNameTypeFace() {
        return this.style.monthNameTypeFace;
    }

    /**
     * Sets the month name typeface.
     *
     * @param typeface the new month name typeface.
     */
    public void setMonthNameTypeFace(final Typeface typeface) {
        if (this.style.monthNameTypeFace == typeface)
            return;

        this.style.monthNameTypeFace = typeface;

        this.updateOwner();
    }

    /**
     * Gets the month name text position. Expected values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @return the current month name text position.
     */
    public int getMonthNameTextPosition() {
        return this.style.monthNameTextPosition;
    }

    /**
     * Sets the month name text position.
     *
     * @param position the new month name text position.
     */
    public void setMonthNameTextPosition(final int position) {
        if (this.style.monthNameTextPosition == position)
            return;

        this.style.monthNameTextPosition = position;

        this.updateOwner();
    }

    /**
     * Gets the day names background color.
     *
     * @return the current day names background color.
     */
    public int getDayNameBackgroundColor() {
        return style.dayNameBackgroundColor;
    }

    /**
     * Sets the day names background color.
     *
     * @param color the new day names background color.
     */
    public void setDayNameBackgroundColor(final int color) {
        if (this.style.dayNameBackgroundColor == color)
            return;

        this.style.dayNameBackgroundColor = color;

        this.updateOwner();
    }

    /**
     * Gets the enabled week numbers background color.
     *
     * @return the current enabled week numbers background color.
     */
    public int getWeekNumberBackgroundColorEnabled() {
        return style.weekNumberBackgroundColorEnabled;
    }

    /**
     * Gets the disabled week numbers background color.
     *
     * @return the current disabled week numbers background color.
     */
    public int getWeekNumberBackgroundColorDisabled() {
        return style.weekNumberBackgroundColorDisabled;
    }

    /**
     * Sets the background color for both enabled and disabled week numbers.
     *
     * @param colorEnabled  the new enabled week number background color.
     * @param colorDisabled the new disabled week number background color.
     */
    public void setWeekNumberBackgroundColor(final int colorEnabled, final int colorDisabled) {
        if (this.style.weekNumberBackgroundColorEnabled == colorEnabled &&
                this.style.weekNumberBackgroundColorDisabled == colorDisabled)
            return;

        this.style.weekNumberBackgroundColorEnabled = colorEnabled;
        this.style.weekNumberBackgroundColorDisabled = colorDisabled;

        this.updateOwner();
    }

    /**
     * Gets the date text size.
     *
     * @return the current date text size.
     */
    public float getDateTextSize() {
        return style.dateTextSize;
    }

    /**
     * Sets the date text size.
     *
     * @param size the new date text size.
     */
    public void setDateTextSize(final float size) {
        if (this.style.dateTextSize == size)
            return;

        this.style.dateTextSize = size;

        this.updateOwner();
    }

    private void updateOwner() {
        if(!applyStyleInProgress) {
            this.owner.updateCalendar();
        }
    }

    /**
     * Gets the date text position. Expected values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @return the current date text position.
     */
    public int getDateTextPosition() {
        return this.style.dateTextPosition;
    }

    /**
     * Sets the date text position. Accepted values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @param position the new date text position.
     */
    public void setDateTextPosition(final int position) {
        if (this.style.dateTextPosition == position)
            return;

        this.style.dateTextPosition = position;

        this.updateOwner();
    }

    /**
     * Gets the day names text position. Expected values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @return the current day names text position.
     */
    public int getDayNameTextPosition() {
        return style.dayNameTextPosition;
    }

    /**
     * Sets the day names text position. Accepted values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @param position the new day names text position.
     */
    public void setDayNameTextPosition(final int position) {
        if (this.style.dayNameTextPosition == position)
            return;

        this.style.dayNameTextPosition = position;

        this.updateOwner();
    }

    /**
     * Gets the week numbers text position. Expected values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @return the current week numbers text position.
     */
    public int getWeekNumberTextPosition() {
        return style.weekNumberTextPosition;
    }

    /**
     * Sets the week numbers text position. Accepted values are:
     * {@link com.telerik.widget.calendar.CalendarElement#LEFT}, {@link com.telerik.widget.calendar.CalendarElement#TOP}, {@link com.telerik.widget.calendar.CalendarElement#RIGHT},
     * {@link com.telerik.widget.calendar.CalendarElement#BOTTOM}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_HORIZONTAL}, {@link com.telerik.widget.calendar.CalendarElement#CENTER_VERTICAL},
     * {@link com.telerik.widget.calendar.CalendarElement#CENTER}
     *
     * @param position the new week numbers text position.
     */
    public void setWeekNumberTextPosition(final int position) {
        if (this.style.weekNumberTextPosition == position)
            return;

        this.style.weekNumberTextPosition = position;

        this.updateOwner();
    }

    /**
     * Gets the week numbers text size.
     *
     * @return the current week numbers text size.
     */
    public float getWeekNumberTextSize() {
        return style.weekNumberTextSize;
    }

    /**
     * Sets the week numbers text size.
     *
     * @param size the new week numbers text size.
     */
    public void setWeekNumberTextSize(final float size) {
        if (this.style.weekNumberTextSize == size)
            return;

        this.style.weekNumberTextSize = size;

        this.owner.beginUpdate(true);

        this.updateOwner();
    }

    /**
     * Gets the dates typeface.
     *
     * @return the current dates typeface.
     */
    public Typeface getDateTypeFace() {
        return style.dateTypeFace;
    }

    /**
     * Sets the dates typeface.
     *
     * @param typeface the new dates typeface.
     */
    public void setDateTypeFace(final Typeface typeface) {
        if (this.style.dateTypeFace == typeface)
            return;

        this.style.dateTypeFace = typeface;

        this.updateOwner();
    }

    /**
     * Gets the day names typeface.
     *
     * @return the current day names typeface.
     */
    public Typeface getDayNameTypeFace() {
        return style.dayNameTypeFace;
    }

    /**
     * Sets the current day names typeface.
     *
     * @param typeface the new day names typeface.
     */
    public void setDayNameTypeFace(final Typeface typeface) {
        if (this.style.dayNameTypeFace == typeface)
            return;

        this.style.dayNameTypeFace = typeface;

        this.updateOwner();
    }

    /**
     * Gets the day names horizontal padding.
     *
     * @return the current horizontal padding.
     */
    public int getDayNamePaddingHorizontal() {
        return style.dayNamePaddingHorizontal;
    }

    /**
     * Gets the day names vertical padding.
     *
     * @return the current vertical padding.
     */
    public int getDayNamePaddingVertical() {
        return style.dayNamePaddingHorizontal;
    }

    /**
     * Sets the day names horizontal and vertical paddings.
     *
     * @param horizontalPadding the new horizontal padding.
     * @param verticalPadding   the new vertical padding.
     */
    public void setDayNamePadding(final int horizontalPadding, final int verticalPadding) {
        if (this.style.dayNamePaddingHorizontal == horizontalPadding &&
                this.style.dayNamePaddingVertical == verticalPadding)
            return;

        this.style.dayNamePaddingHorizontal = horizontalPadding;
        this.style.dayNamePaddingVertical = verticalPadding;

        this.updateOwner();
    }

    /**
     * Gets the week numbers typeface.
     *
     * @return the current week numbers typeface.
     */
    public Typeface getWeekNumberTypeFace() {
        return style.weekNumberTypeFace;
    }

    /**
     * Sets the week numbers typeface.
     *
     * @param typeface the new week numbers typeface.
     */
    public void setWeekNumberTypeFace(final Typeface typeface) {
        if (this.style.weekNumberTypeFace == typeface)
            return;

        this.style.weekNumberTypeFace = typeface;

        this.updateOwner();
    }

    /**
     * Gets the enabled day names text color in year mode.
     *
     * @return the current day names enabled color.
     */
    public int getDayNameTextColorYearModeEnabled() {
        return style.dayNameTextColorYearModeEnabled;
    }

    /**
     * Gets the disabled day names text color in year mode.
     *
     * @return the current day names disabled color.
     */
    public int getDayNameTextColorYearModeDisabled() {
        return style.dayNameTextColorYearModeDisabled;
    }

    /**
     * Sets the day names text color in year mode for both enabled and disabled states.
     *
     * @param colorEnabled  the new enabled day names text color.
     * @param colorDisabled the new disabled day names text color.
     */
    public void setDayNameTextColorYearMode(final int colorEnabled, final int colorDisabled) {
        if (this.style.dayNameTextColorYearModeEnabled == colorEnabled &&
                this.style.dayNameTextColorYearModeDisabled == colorDisabled)
            return;

        this.style.dayNameTextColorYearModeEnabled = colorEnabled;
        this.style.dayNameTextColorYearModeDisabled = colorDisabled;

        this.updateOwner();
    }

    /**
     * Gets the day names text size.
     *
     * @return the current day names text size.
     */
    public float getDayNameTextSize() {
        return style.dayNameTextSize;
    }

    /**
     * Sets the day names text size.
     *
     * @param size the new day names text size.
     */
    public void setDayNameTextSize(final float size) {
        if (this.style.dayNameTextSize == size)
            return;

        this.style.dayNameTextSize = size;

        this.updateOwner();
    }

    /**
     * Gets the day names text size in year mode.
     *
     * @return the current day names text size in year mode.
     */
    public float getDayNameTextSizeYearMode() {
        return style.dayNameTextSizeYearMode;
    }

    /**
     * Sets the day names text size in year mode.
     *
     * @param size the new day names text size in year mode.
     */
    public void setDayNameTextSizeYearMode(final float size) {
        if (this.style.dayNameTextSizeYearMode == size)
            return;

        this.style.dayNameTextSizeYearMode = size;

        this.updateOwner();
    }

    /**
     * Gets the day names typeface in year mode.
     *
     * @return the current day names typeface in year mode.
     */
    public Typeface getDayNameTypefaceYearMode() {
        return style.dayNameTypefaceYearMode;
    }

    /**
     * Sets the day names typeface in year mode.
     *
     * @param typeface the new day names typeface in year mode.
     */
    public void setDayNameTypefaceYearMode(final Typeface typeface) {
        if (this.style.dayNameTypefaceYearMode == typeface)
            return;

        this.style.dayNameTypefaceYearMode = typeface;

        this.updateOwner();
    }

    /**
     * Gets the enabled dates text color in year mode.
     *
     * @return the current enabled dates text color in year mode.
     */
    public int getDateTextColorYearModeEnabled() {
        return this.style.dateTextColorYearModeEnabled;
    }

    /**
     * Gets the disabled dates text color in year mode.
     *
     * @return the current disabled dates text color in year mode.
     */
    public int getDateTextColorYearModeDisabled() {
        return this.style.dateTextColorYearModeDisabled;
    }

    /**
     * Sets the dates text color in year mode for both enabled and disabled states.
     *
     * @param colorEnabled  the new enabled dates text color.
     * @param colorDisabled the new disabled dates text color.
     */
    public void setDateTextColorYearMode(final int colorEnabled, final int colorDisabled) {
        if (this.style.dateTextColorYearModeEnabled == colorEnabled &&
                this.style.dateTextColorYearModeDisabled == colorDisabled)
            return;

        this.style.dateTextColorYearModeEnabled = colorEnabled;
        this.style.dateTextColorYearModeDisabled = colorDisabled;

        this.updateOwner();
    }

    /**
     * Gets dates text size in year mode.
     *
     * @return the current dates text size in year mode.
     */
    public float getDateTextSizeYearMode() {
        return style.dateTextSizeYearMode;
    }

    /**
     * Sets the dates text size in year mode.
     *
     * @param size the new dates text size in year mode.
     */
    public void setDateTextSizeYearMode(final float size) {
        if (this.style.dateTextSizeYearMode == size)
            return;

        this.style.dateTextSizeYearMode = size;

        this.updateOwner();
    }

    /**
     * Gets the dates typeface in year mode.
     *
     * @return the current dates typeface in year mode.
     */
    public Typeface getDateTypeFaceYearMode() {
        return style.dateTypeFaceYearMode;
    }

    /**
     * Sets the dates typeface in year mode.
     *
     * @param typeface the new dates typeface in year mode.
     */
    public void setDateTypeFaceYearMode(final Typeface typeface) {
        if (this.style.dateTypeFaceYearMode == typeface)
            return;

        this.style.dateTypeFaceYearMode = typeface;

        this.updateOwner();
    }

    /**
     * Gets a {@link com.telerik.widget.calendar.CalendarTextElement} which represents the first row in
     * the {@link com.telerik.widget.calendar.RadCalendarView} usually containing
     * the current month name and year for the provided date.
     *
     * @param date        date from the current month
     * @param displayMode the display mode
     * @return text element containing information about the current title depending on the provided display mode
     */
    public CalendarDayCell getTitleCell(long date, CalendarDisplayMode displayMode) {
        CalendarDayCell titleCell = new CalendarDayCell(this.owner);
        titleCell.setCellType(CalendarCellType.Title);

        this.updateTitle(titleCell, date, displayMode);

        return titleCell;
    }

    /**
     * Updates the provided {@link com.telerik.widget.calendar.CalendarTextElement} with information about the provided date
     * according to the current display mode.
     *
     * @param convertCell the cell that will be reused for the new date
     * @param date        date from the current month
     * @param displayMode the display mode
     */
    public void updateTitle(CalendarDayCell convertCell, long date, CalendarDisplayMode displayMode) {
        this.workCalendar.setTimeInMillis(date);
        String monthName = this.workCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, this.locale);

        String content;

        if (displayMode == CalendarDisplayMode.Year) {
            content = String.valueOf(this.workCalendar.get(Calendar.YEAR));
        } else {
            content = String.format("%s %d", monthName, this.workCalendar.get(Calendar.YEAR));
        }

        convertCell.setText(content);
        convertCell.setDate(this.workCalendar.getTimeInMillis());

        this.updateTitleCellStyle(convertCell);
    }

    /**
     * Updates the provided {@link com.telerik.widget.calendar.CalendarCell} with the name of the
     * day of the week with the specified index according to the current locale.
     *
     * @param convertCell the cell that will be reused for the new day name
     * @param index       the index of the requested day name element
     */
    public void updateDayNameCell(CalendarDayCell convertCell, int index) {
        if(index < 0) {
            return;
        }
        int startDayNumber = this.calendar.getFirstDayOfWeek();
        int dayOfWeek = startDayNumber + index;
        if (dayOfWeek > CalendarTools.DAYS_IN_A_WEEK) {
            dayOfWeek -= CalendarTools.DAYS_IN_A_WEEK;
        }

        this.workCalendar.set(Calendar.YEAR, 1111);
        this.workCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        String dayName = CalendarTools.getShortDayName(dayOfWeek);

        convertCell.setText(dayName);
        convertCell.setDate(this.workCalendar.getTimeInMillis());

        this.updateDayNameCellStyle(convertCell);
    }

    /**
     * Gets a {@link com.telerik.widget.calendar.CalendarCell} which contains the specified number.
     * This cell will be used as a number of week.
     * If the weekNumber is <code>0</code>, it will not be displayed.
     *
     * @return cell containing the specified week number
     */
    public CalendarDayCell getWeekNumberCell() {
        CalendarDayCell weekNumberCell = generateCalendarDayCell();
        weekNumberCell.setCellType(CalendarCellType.WeekNumber);
        updateWeekNumberCellStyle(weekNumberCell);
        if (this.owner.getWeekNumbersDisplayMode() != WeekNumbersDisplayMode.Block)
            weekNumberCell.setVisibility(ElementVisibility.Gone);

        this.weekNumberCells.add(weekNumberCell);

        return weekNumberCell;
    }

    /**
     * Generates an empty calendar row.
     *
     * @return the newly created calendar row.
     */
    public CalendarRow generateCalendarRow() {
        return new CalendarRow(this.owner);
    }

    /**
     * Generates a new calendar fragment.
     *
     * @return the newly created calendar fragment.
     */
    public CalendarFragment generateFragment() {
        return new CalendarFragment(this.owner);
    }

    /**
     * Updates the style of a passed title cell using the current style of the adapter.
     *
     * @param titleCell the cell to be updated.
     */
    public void updateTitleCellStyle(CalendarDayCell titleCell) {
        titleCell.setBorderColor(Color.TRANSPARENT);
        titleCell.setBorderWidth(0);
        titleCell.setPaddingHorizontal(0);
        titleCell.setPaddingVertical(0);
        titleCell.setTextColor(style.titleTextColor);
        titleCell.setTextSize(style.titleTextSize);
        titleCell.setTypeface(style.titleTypeFace);
        titleCell.setTextPosition(style.titleTextPosition);
        titleCell.setBackgroundColor(style.titleBackgroundColor, style.titleBackgroundColor);

        owner.applyDayCellStyles(titleCell);
    }

    /**
     * Updates the style of a passed week number cell using the current style of the adapter.
     *
     * @param weekNumberCell the cell to be updated.
     */
    public void updateWeekNumberCellStyle(CalendarDayCell weekNumberCell) {
        weekNumberCell.setBorderColor(Color.TRANSPARENT);
        weekNumberCell.setBorderWidth(0);
        weekNumberCell.setPaddingHorizontal(this.style.dateCellPaddingHorizontal);
        weekNumberCell.setPaddingVertical(this.style.dateCellPaddingVertical);
        weekNumberCell.setTextColor(this.style.weekNumberTextColorEnabled, this.style.weekNumberTextColorDisabled);
        weekNumberCell.setTextSize(style.weekNumberTextSize);
        weekNumberCell.setTypeface(style.weekNumberTypeFace);
        weekNumberCell.setTextPosition(style.weekNumberTextPosition);
        weekNumberCell.setBackgroundColor(this.style.weekNumberBackgroundColorEnabled, this.style.weekNumberBackgroundColorDisabled);

        owner.applyDayCellStyles(weekNumberCell);
    }

    /**
     * Updates the style of a passed date cell using the current style of the adapter.
     *
     * @param dateCell the cell to be updated.
     */
    public void updateDateCellStyle(CalendarDayCell dateCell) {
        if(dateCell.isToday()) {
            dateCell.setBorderColor(style.todayBorderColor);
            dateCell.setBorderWidth(style.todayBorderWidth);
            if(dateCell.isSelected()) {
                dateCell.setTextColor(this.style.todayCellSelectedTextColor, this.style.todayCellSelectedTextColor);
            } else {
                dateCell.setTextColor(this.style.todayCellTextColor, this.style.todayCellTextColor);
            }
            dateCell.setTypeface(style.todayCellTypeFace);
            dateCell.setBackgroundColor(this.style.todayCellBackgroundColor, this.style.todayCellBackgroundColor);
        } else {
            dateCell.setBorderColor(Color.TRANSPARENT);
            dateCell.setBorderWidth(0);
            if(dateCell.isSelected()) {
                dateCell.setTypeface(style.selectedCellTypeFace);
                if(owner.selectionChangesBackground()) {
                    dateCell.setBackgroundColor(style.selectedCellBackgroundColor, style.selectedCellBackgroundColor);
                } else {
                    dateCell.setBackgroundColor(this.style.dateCellBackgroundColorEnabled, this.style.dateCellBackgroundColorDisabled);
                }
                dateCell.setTextColor(style.selectedCellTextColor, style.selectedCellTextColor);
            } else {
                dateCell.setTypeface(style.dateTypeFace);
                dateCell.setBackgroundColor(this.style.dateCellBackgroundColorEnabled, this.style.dateCellBackgroundColorDisabled);
                dateCell.setTextColor(this.style.dateTextColorEnabled, this.style.dateTextColorDisabled);
            }
        }
        dateCell.setPaddingHorizontal(this.style.dateCellPaddingHorizontal);
        dateCell.setPaddingVertical(this.style.dateCellPaddingVertical);
        dateCell.setTextSize(style.dateTextSize);
        dateCell.setTextPosition(style.dateTextPosition);
        dateCell.setBitmap(null);

        dateCell.setSecondaryTextSize(this.style.weekNumberTextSize);
        dateCell.setSecondaryTextPosition(this.style.weekNumberTextPosition);
        dateCell.setSecondaryTextColor(this.style.weekNumberTextColorEnabled, this.style.weekNumberTextColorDisabled);

        owner.applyDayCellStyles(dateCell);
    }

    /**
     * Updates the style of a passed day name cell using the current style of the adapter.
     *
     * @param dayNameCell the cell to be updated.
     */
    public void updateDayNameCellStyle(CalendarDayCell dayNameCell) {
        dayNameCell.setBorderColor(Color.TRANSPARENT);
        dayNameCell.setBorderWidth(0);
        dayNameCell.setPaddingHorizontal(this.style.dateCellPaddingHorizontal);
        dayNameCell.setPaddingVertical(this.style.dateCellPaddingVertical);
        dayNameCell.setTextColor(this.style.dayNameTextColor);
        dayNameCell.setTextSize(style.dayNameTextSize);
        dayNameCell.setTypeface(style.dayNameTypeFace);
        dayNameCell.setTextPosition(style.dayNameTextPosition);
        dayNameCell.setBackgroundColor(this.style.dayNameBackgroundColor, this.style.dayNameBackgroundColor);

        owner.applyDayCellStyles(dayNameCell);
    }

    /**
     * Updates the style of a passed month cell using the current style of the adapter.
     *
     * @param monthCell the cell to be updated.
     */
    public void updateMonthCellStyle(CalendarMonthCell monthCell) {
        if(monthCell.isToday()) {
            monthCell.setBorderColor(style.todayBorderColor);
            monthCell.setBorderWidth(style.todayBorderWidth);
            monthCell.setTypeface(style.todayCellTypeFace);
            monthCell.setBackgroundColor(this.style.todayCellBackgroundColor, this.style.todayCellBackgroundColor);
        } else {
            monthCell.setBorderColor(Color.TRANSPARENT);
            monthCell.setBorderWidth(0);
            monthCell.setTypeface(style.dateTypeFace);
            monthCell.setBackgroundColor(this.style.dateCellBackgroundColorEnabled, this.style.dateCellBackgroundColorDisabled);
        }

        monthCell.setPaddingHorizontal(this.style.monthCellPaddingHorizontal);
        monthCell.setPaddingVertical(this.style.monthCellPaddingVertical);
        monthCell.setTextColor(this.style.dateTextColorEnabled, this.style.dateTextColorDisabled);
        monthCell.setTextSize(style.dateTextSize);
        monthCell.setTextPosition(CalendarElement.CENTER);

        monthCell.setMonthNameColor(this.style.monthNameTextColorEnabled, this.style.monthNameTextColorDisabled);
        monthCell.setMonthNameTextSize(this.style.monthNameTextSize);
        monthCell.setMonthNameTypeFace(this.style.monthNameTypeFace);
        monthCell.setMonthNameTextPosition(this.style.monthNameTextPosition);
        monthCell.setDayNamesColor(this.style.dayNameTextColorYearModeEnabled, this.style.dayNameTextColorYearModeDisabled);
        monthCell.setDayNameTextSize(this.style.dayNameTextSizeYearMode);
        monthCell.setDayNameTypeFace(this.style.dayNameTypefaceYearMode);
        monthCell.setDateTextColor(this.style.dateTextColorYearModeEnabled, this.style.dateTextColorYearModeDisabled);
        monthCell.setDateTextSize(this.style.dateTextSizeYearMode);
        monthCell.setDateTypeFace(this.style.dateTypeFaceYearMode);

        monthCell.setMonthNameTextSizeCompact(this.style.monthNameTextSizeCompact);

        owner.applyMonthCellStyles(monthCell);
    }

    /**
     * Updates the provided {@link com.telerik.widget.calendar.CalendarCell} with information
     * about the new week number.
     * If the weekNumber is <code>0</code>, it will not be displayed.
     *
     * @param convertCell the cell that will be reused for the new week number
     * @param weekNumber  the week number that will be contained in the cell
     */
    public void updateWeekNumberCell(CalendarDayCell convertCell, int weekNumber) {
        if (weekNumber != 0) {
            convertCell.setText(String.format("%d", weekNumber));
        } else {
            convertCell.setText("");
        }
        updateWeekNumberCellStyle(convertCell);
    }

    /**
     * Updates the provided {@link com.telerik.widget.calendar.CalendarCell} with information
     * about the new specified date.
     *
     * @param convertCell       the cell that will be reused for the new date
     * @param date              the date that will be represented by this cell
     * @param eventList         list of event that should be presented by this cell
     * @param includeWeekNumber a value which indicates whether this cell will also contain the number of the week
     */
    public void updateDateCell(CalendarDayCell convertCell, Long date, List<Event> eventList, boolean includeWeekNumber) {
        this.workCalendar.setTimeInMillis(date);
        convertCell.setText(dateValues.get(this.workCalendar.get(Calendar.DAY_OF_MONTH)));
        convertCell.setDate(date);

        convertCell.setAsToday(this.workCalendar.get(Calendar.DAY_OF_MONTH) == this.todayDay && this.workCalendar.get(Calendar.MONTH) == this.todayMonth && this.workCalendar.get(Calendar.YEAR) == this.todayYear);

        convertCell.setEvents(eventList);

        if (includeWeekNumber) {
            convertCell.setSecondaryText(String.valueOf(this.workCalendar.get(Calendar.WEEK_OF_YEAR)));
        }

        updateDateCellStyle(convertCell);
    }

    /**
     * Updates the provided {@link com.telerik.widget.calendar.CalendarMonthCell} with information
     * about the month that contains the specified date.
     *
     * @param convertCell the cell that will be reused for the new date
     * @param date        the date that will be represented by this cell
     */
    public void updateCalendarMonthCell(CalendarMonthCell convertCell, long date) {
        convertCell.setDate(date);

        this.workCalendar.setTimeInMillis(date);

        convertCell.setMonthName(getMonthNameCached(this.workCalendar.get(Calendar.MONTH)));

        convertCell.setAsToday(workCalendar.get(Calendar.YEAR) == todayYear && workCalendar.get(Calendar.MONTH) == todayMonth);

        updateMonthCellStyle(convertCell);
    }

    protected String getMonthNameCached(int monthIndex) {
        return monthNames.get(monthIndex);
    }

    /**
     * Gets a {@link com.telerik.widget.calendar.CalendarCell} which contains the name of the day of the week
     * with the specified index according to the current locale.
     *
     * @return cell containing the name of the day of the week with the specified index
     */
    public CalendarCell getDayNameCell() {
        return getDayNameCell(-1);
    }

    /**
     * Gets a {@link com.telerik.widget.calendar.CalendarCell} which contains the name of the day of the week
     * with the specified index according to the current locale.
     *
     * @param index the index of the requested day name element
     * @return cell containing the name of the day of the week with the specified index
     */
    public CalendarDayCell getDayNameCell(int index) {
        CalendarDayCell dayNameCell = generateCalendarDayCell();
        dayNameCell.setCellType(CalendarCellType.DayName);

        this.updateDayNameCell(dayNameCell, index);

        this.updateDayNameCellStyle(dayNameCell);

        this.dayNameCells.add(dayNameCell);

        return dayNameCell;
    }

    /**
     * Used to generate a new date cell, applies the current style to it and adds it to the current collection of date cells,
     * so that it could be tracked and procedures may be applied to it at a later stage.
     *
     * @return the newly created and updated cell.
     */
    public CalendarDayCell getDateCell() {
        CalendarDayCell dateCell = generateCalendarDayCell();
        dateCell.setCellType(CalendarCellType.Date);
        updateDateCellStyle(dateCell);

        this.dateCells.add(dateCell);

        return dateCell;
    }

    /**
     * Used to generate a new month cell, applies the current style to it and adds it to the current collection of month cells,
     * so that it could be tracked and procedures may be applied to it at a later stage.
     *
     * @return the newly created and updated cell.
     */
    public CalendarMonthCell getMonthCell() {
        CalendarMonthCell monthCell = generateCalendarMonthCell();
        monthCell.setCellType(CalendarCellType.Date);

        updateMonthCellStyle(monthCell);

        this.dateCells.add(monthCell);

        return monthCell;
    }

    /**
     * Clears the current cells from the tracked collection allowing the adapter to refill them with the newly created ones at the time
     * of rebuilding the calendar.
     */
    public void reset() {
        this.weekNumberCells.clear();
        this.dateCells.clear();
    }

    /**
     * Applies a given procedures to all tracked cells.
     *
     * @param procedure the procedure to be applied to the cells.
     */
    public void applyProcedureToAllCells(Procedure<CalendarCell> procedure) {
        applyProcedureToDateCells(procedure);
        applyProcedureToWeekNumberCells(procedure);
        applyProcedureToDayNameCells(procedure);
    }

    /**
     * Applies a given procedure to the tracked date cells.
     *
     * @param procedure the procedure to be applied to the date cells.
     */
    public void applyProcedureToDateCells(Procedure<CalendarCell> procedure) {
        for (CalendarCell dateCell : this.dateCells) procedure.apply(dateCell);
    }

    /**
     * Applies a given procedure to the tracked week number cells.
     *
     * @param procedure the procedure to be applied to the week number cells.
     */
    public void applyProcedureToWeekNumberCells(Procedure<CalendarCell> procedure) {
        for (CalendarCell weekNumberCell : this.weekNumberCells) procedure.apply(weekNumberCell);
    }

    /**
     * Applies a given procedure to the tracked day name cells.
     *
     * @param procedure the procedure to be applied to the day name cells.
     */
    public void applyProcedureToDayNameCells(Procedure<CalendarCell> procedure) {
        for (CalendarCell dayNameCell : this.dayNameCells) procedure.apply(dayNameCell);
    }

    /**
     * Generates an empty calendar day cell.
     *
     * @return the newly created day cell.
     */
    protected CalendarDayCell generateCalendarDayCell() {
        return new CalendarDayCell(this.owner);
    }

    /**
     * Generates an empty calendar month cell.
     *
     * @return the newly created month cell.
     */
    protected CalendarMonthCell generateCalendarMonthCell() {
        return new CalendarMonthCell(this.owner);
    }

    private boolean applyStyleInProgress = false;

    /**
     * Used to apply a given style to the current calendar.
     *
     * @param style the style to be applied.
     */
    protected void applyStyle(CalendarStyle style) {
        applyStyleInProgress = true;
        // Dates
        setDateCellBackgroundColor(style.dateCellBackgroundColorEnabled, style.dateCellBackgroundColorDisabled);
        setDateTextColor(style.dateTextColorEnabled, style.dateTextColorDisabled);
        setDateTextPosition(style.dateTextPosition);
        setDateTextSize(style.dateTextSize);
        setDateTypeFace(style.dateTypeFace);

        setSelectedCellBackgroundColor(style.selectedCellBackgroundColor);
        setTodayCellBackgroundColor(style.todayCellBackgroundColor);

        setDateTextColorYearMode(style.dateTextColorYearModeEnabled, style.dateTextColorYearModeDisabled);
        setDateTextSizeYearMode(style.dateTextSizeYearMode);
        setDateTypeFaceYearMode(style.dateTypeFaceYearMode);

        setMonthNameTextColor(style.monthNameTextColorEnabled, style.monthNameTextColorDisabled);
        setMonthNameTypeFace(style.monthNameTypeFace);
        setMonthNameTextPosition(style.monthNameTextPosition);
        setMonthNameTextSize(style.monthNameTextSize);
        setMonthNameTextSizeCompact(style.monthNameTextSizeCompact);

        // Week Numbers
        setWeekNumberBackgroundColor(style.weekNumberBackgroundColorEnabled, style.weekNumberBackgroundColorDisabled);
        setWeekNumberTextColor(style.weekNumberTextColorEnabled, style.weekNumberTextColorDisabled);
        setWeekNumberTextSize(style.weekNumberTextSize);
        setWeekNumberTextPosition(style.weekNumberTextPosition);
        setWeekNumberTypeFace(style.weekNumberTypeFace);

        // Day Names
        setDayNameTextColor(style.dayNameTextColor);
        setDayNameBackgroundColor(style.dayNameBackgroundColor);
        setDayNameTextPosition(style.dayNameTextPosition);
        setDayNameTextSize(style.dayNameTextSize);
        setDayNameTypeFace(style.dayNameTypeFace);
        setDayNamePadding(style.dateCellPaddingHorizontal, 0);

        setDayNameTypefaceYearMode(style.dayNameTypefaceYearMode);
        setDayNameTextColorYearMode(style.dayNameTextColorYearModeEnabled, style.dayNameTextColorYearModeDisabled);
        setDayNameTextSizeYearMode(style.dayNameTextSizeYearMode);

        setTitleTextColor(style.titleTextColor);
        setTitleBackgroundColor(style.titleBackgroundColor);
        setTitleTextSize(style.titleTextSize);
        setTitleTypeFace(style.titleTypeFace);
        setTitleTextPosition(style.titleTextPosition);

        this.owner.getGridLinesLayer().setColor(style.gridLinesColor);

        setTodayBackgroundColor(style.todayBackgroundColor);
        setTodayTextColor(style.todayTextColor);
        setTodayTypeFace(style.todayTypeFace);

        setTodayCellTextColor(style.todayCellTextColor);
        setTodayCellSelectedTextColor(style.todayCellSelectedTextColor);
        setTodayCellTypeFace(style.todayCellTypeFace);
        setTodayCellBorderColor(style.todayBorderColor);
        setTodayCellBorderWidth(style.todayBorderWidth);

        setDateCellPaddingHorizontal(style.dateCellPaddingHorizontal);
        setDateCellPaddingVertical(style.dateCellPaddingVertical);

        setMonthCellPaddingHorizontal(style.monthCellPaddingHorizontal);
        setMonthCellPaddingVertical(style.monthCellPaddingVertical);
        this.owner.getCellDecorator().setColor(style.decorationsColor);
        this.owner.getCellDecorator().setStrokeWidth(style.decorationsStrokeWidth);
        this.owner.setBackgroundColor(style.inlineEventsBackgroundColor);
        setSelectedCellTextColor(style.selectedCellTextColor);
        setSelectedCellTypeFace(style.selectedCellTypeFace);

        applyStyleInProgress = false;
        this.owner.updateCalendar();
    }

    /**
     * Used to update the cached month names.
     */
    protected void updateMonthNamesCache() {
        this.monthNames.clear();
        this.workCalendar.set(Calendar.AM_PM, 0);
        this.workCalendar.set(Calendar.HOUR, 0);
        this.workCalendar.set(Calendar.MINUTE, 0);
        this.workCalendar.set(Calendar.SECOND, 0);
        this.workCalendar.set(Calendar.MILLISECOND, 0);
        this.workCalendar.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0, len = 12; i < len; i++) {
            this.workCalendar.set(Calendar.MONTH, i);
            this.monthNames.append(i, this.workCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, this.owner.getLocale()));
        }
    }

    private void applyCustomizationRule(CalendarCell argument) {
        if (this.owner.getCustomizationRule() != null)
            this.owner.getCustomizationRule().apply(argument);
    }

    private Integer getCustomColor(long date) {
        if (this.owner.getDateToColor() != null)
            return this.owner.getDateToColor().apply(date);
        return null;
    }
}
