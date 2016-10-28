package com.telerik.widget.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import static com.telerik.widget.calendar.RadCalendarView.OnSelectedDatesChangedListener;

/**
 * Class responsible for handling the selection of dates corresponding to invoked gestures.
 */
public class CalendarSelectionManager {

    /**
     * The calendar view instance owning the manager.
     */
    protected final RadCalendarView owner;
    /**
     * The current selection mode. It is internally updated by the owning calendar and should not be changed directly.
     */
    protected CalendarSelectionMode selectionMode;
    /**
     * Collection holding the currently selected cells.
     */
    protected List<CalendarDayCell> selectedCells;
    /**
     * Listener to be invoked when a date is selected or deselected.
     */
    protected OnSelectedDatesChangedListener onSelectedDatesChangedListener;
    /**
     * The current collection of selected dates.
     */
    protected List<Long> selectedDates;
    /**
     * The current selected range.
     */
    protected DateRange selectedRange;


    /**
     * Creates a new instance of the {@link CalendarSelectionManager} class.
     *
     * @param owner the calendar owning this instance.
     */
    public CalendarSelectionManager(RadCalendarView owner) {
        if (owner == null)
            throw new NullPointerException("owner");

        this.owner = owner;

        this.selectedDates = new ArrayList<Long>();
        this.selectedCells = new ArrayList<CalendarDayCell>();
    }

    CalendarSelectionMode getSelectionMode() {
        return this.selectionMode;
    }

    void setSelectionMode(CalendarSelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    /**
     * Returns a list of dates that are currently selected.
     *
     * @return a list of the currently selected dates
     */
    public List<Long> getSelectedDates() {
        return selectedDates;
    }

    /**
     * Sets a list of dates as selected.
     * The list should be compliant with the
     * current {@link com.telerik.widget.calendar.CalendarSelectionMode}.
     *
     * @param selectedDates a list of dates that will be selected
     * @see #getSelectionMode()
     */
    public void setSelectedDates(List<Long> selectedDates) {
        if (selectedDates == null || selectedDates.size() == 0) {
            this.setSelection(null);
            return;
        }

        if (this.selectionMode == CalendarSelectionMode.Single) {

            long selectedDate = selectedDates.get(0);
            selectedDate = CalendarTools.getDateStart(selectedDate);
            selectedDates.clear();
            selectedDates.add(selectedDate);
            this.setSelection(selectedDates);

        } else if (this.selectionMode == CalendarSelectionMode.Range) {

            Long startDate = selectedDates.get(0);
            startDate = CalendarTools.getDateStart(startDate);

            Long endDate = selectedDates.get(selectedDates.size() - 1);
            endDate = CalendarTools.getDateStart(endDate);

            if (startDate < endDate) {
                this.selectedRange = new DateRange(startDate, endDate);
            } else {
                this.selectedRange = new DateRange(endDate, startDate);
            }

            List<Long> dates = this.getDateRangeList(startDate, endDate);
            this.setSelection(dates);

        } else {
            List<Long> dates = new ArrayList<Long>();
            for (Long selectedDate : selectedDates) {
                dates.add(CalendarTools.getDateStart(selectedDate));
            }
            this.setSelection(dates);
        }
    }

    /**
     * Returns an instance of type {@link com.telerik.widget.calendar.DateRange}
     * that represent the current selection if the current
     * {@link com.telerik.widget.calendar.CalendarSelectionMode} is <code>Range</code>.
     *
     * @return the currently selected date range
     * @throws java.lang.IllegalStateException If the current selection mode is not <code>Range</code>
     */
    public DateRange getSelectedRange() {
        if (this.selectionMode != CalendarSelectionMode.Range) {
            throw new IllegalStateException("Selected Range is applicable only when calendar's selection mode is Range. Use getSelectedDates().");
        }
        return this.selectedRange;
    }

    /**
     * Sets the selection to the range passed as a parameter if the current
     * {@link com.telerik.widget.calendar.CalendarSelectionMode} is <code>Range</code>.
     *
     * @param range date range to be selected
     * @throws java.lang.IllegalStateException If the current selection mode is not <code>Range</code>
     */
    public void setSelectedRange(DateRange range) {
        if (this.selectionMode != CalendarSelectionMode.Range) {
            throw new IllegalStateException("Selected Range is applicable only when calendar's selection mode is Range. Use setSelectedDates(List<Long>).");
        }
        if (this.selectedRange != range) {
            long start = CalendarTools.getDateStart(range.getStart());
            long end = CalendarTools.getDateStart(range.getEnd());
            this.selectedRange = new DateRange(start, end);

            List<Long> dates = this.getDateRangeList(start, end);
            this.setSelection(dates);
        }
    }

    /**
     * Gets the current listener for date selection change.
     *
     * @return the current listener.
     */
    public OnSelectedDatesChangedListener getOnSelectedDatesChangedListener() {
        return this.onSelectedDatesChangedListener;
    }

    /**
     * Sets the current listener for date selection change.
     *
     * @param listener the new listener.
     */
    public void setOnSelectedDatesChangedListener(OnSelectedDatesChangedListener listener) {
        this.onSelectedDatesChangedListener = listener;
    }

    /**
     * The current collection of selected cells.
     *
     * @return the currently selected cells.
     */
    public List<CalendarDayCell> selectedCells() {
        return this.selectedCells;
    }

    /**
     * Handles the tap gesture associated with the passed cell.
     *
     * @param calendarCell the cell that was affected by the gesture
     */
    public void handleTapGesture(CalendarDayCell calendarCell) {
        if (owner.selectionMode == CalendarSelectionMode.None || !calendarCell.isSelectable() || (this.owner.getMinDate() != 0 && calendarCell.getDate() < owner.getMinDate()) || (this.owner.getMaxDate() != 0 && calendarCell.getDate() > owner.getMaxDate()))
            return;

        switch (this.selectionMode) {
            case Single:
                handleTouchForSingleSelection(calendarCell);
                break;
            case Multiple:
                handleTouchForMultipleSelection(calendarCell);
                break;
            case Range:
                handleTouchForRangeSelection(calendarCell);
                break;
        }

        syncSelectedCellsWithDates();
        this.owner.invalidate();
    }

    /**
     * Used to sync the selected dates with the corresponding cells.
     */
    public void syncSelectedCellsWithDates() {
        this.owner.getScrollManager().markFragmentsDirty();
        for (CalendarDayCell cell : this.selectedCells) {
            cell.setSelected(false);
        }

        this.selectedCells.clear();

        Hashtable<Long, List<CalendarDayCell>> dateToCell = this.owner.dateToCell();
        for (long date : this.selectedDates) {
            List<CalendarDayCell> cells = dateToCell.get(date);

            if (cells == null || cells.size() == 0)
                continue;

            for (CalendarDayCell cell : cells) {
                if (cell.getVisibility() == ElementVisibility.Visible) {
                    cell.setSelected(true);
                    this.selectedCells.add(cell);
                    //break;
                }
            }
        }
    }

    /**
     * Selects the dates in the passed collection.
     *
     * @param selectedDates the collection holding the selected dates.
     */
    protected void setSelection(List<Long> selectedDates) {
        setSelection(selectedDates, null);
    }

    /**
     * Selects the dates in the passed collection.
     *
     * @param selectedDates    the collection holding the selected dates.
     * @param selectionContext the context to be passed to the selection changed listener if it is present.
     */
    protected void setSelection(List<Long> selectedDates, RadCalendarView.SelectionContext selectionContext) {
        boolean buildSelectionContext = selectionContext == null;

        if (buildSelectionContext) {
            selectionContext = new RadCalendarView.SelectionContext();
            selectionContext.oldSelection = new ArrayList<Long>(this.selectedDates);
            selectionContext.datesRemoved = new ArrayList<Long>(this.selectedDates);
        }
        if (selectedDates != null) {
            this.selectedDates = selectedDates;
            if (buildSelectionContext) {
                selectionContext.newSelection = new ArrayList<Long>(this.selectedDates);
                selectionContext.datesAdded = new ArrayList<Long>(this.selectedDates);
            }
        } else {
            this.selectedDates.clear();
        }
        if (this.onSelectedDatesChangedListener != null) {
            this.onSelectedDatesChangedListener.onSelectedDatesChanged(selectionContext);
        }
        this.syncSelectedCellsWithDates();
    }

    /**
     * Handles touch gesture associated with the passed cell when in single selection mode.
     *
     * @param touchedCell the cell that was affected by the gesture
     */
    protected void handleTouchForSingleSelection(CalendarDayCell touchedCell) {
        if (touchedCell.isSelected())
            return;

        for (CalendarDayCell selectedCell : this.selectedCells)
            selectedCell.setSelected(false);

        RadCalendarView.SelectionContext selectionContext = new RadCalendarView.SelectionContext();
        selectionContext.datesRemoved = new ArrayList<Long>(this.selectedDates);
        selectionContext.oldSelection = new ArrayList<Long>(this.selectedDates);
        this.selectedCells.clear();
        this.selectedDates.clear();

        touchedCell.setSelected(true);
        this.selectedCells.add(touchedCell);
        this.selectedDates.add(touchedCell.getDate());
        selectionContext.datesAdded = new ArrayList<Long>(this.selectedDates);
        selectionContext.newSelection = new ArrayList<Long>(this.selectedDates);

        if (this.onSelectedDatesChangedListener != null)
            this.onSelectedDatesChangedListener.onSelectedDatesChanged(selectionContext);

    }

    /**
     * Handles touch gesture associated with the passed cell when in multiple selection mode..
     *
     * @param touchedCell the cell that was affected by the gesture
     */
    protected void handleTouchForMultipleSelection(CalendarDayCell touchedCell) {
        List<Long> datesWithChangedSelection = new ArrayList<Long>();
        datesWithChangedSelection.add(touchedCell.getDate());

        RadCalendarView.SelectionContext selectionContext = new RadCalendarView.SelectionContext();
        selectionContext.oldSelection = new ArrayList<Long>(this.selectedDates);

        if (touchedCell.isSelected()) {
            touchedCell.setSelected(false);
            this.selectedCells.remove(touchedCell);
            this.selectedDates.remove(touchedCell.getDate());
            selectionContext.datesRemoved = datesWithChangedSelection;
        } else {
            touchedCell.setSelected(true);
            this.selectedCells.add(touchedCell);
            if (!selectedDates.contains(touchedCell.getDate()))
                this.selectedDates.add(touchedCell.getDate());
            selectionContext.datesAdded = datesWithChangedSelection;
        }

        selectionContext.newSelection = new ArrayList<Long>(this.selectedDates);

        if (this.onSelectedDatesChangedListener != null) {
            this.onSelectedDatesChangedListener.onSelectedDatesChanged(selectionContext);
        }
    }

    /**
     * Handles touch gesture associated with the passed cell when in range selection mode.
     *
     * @param touchedCell the cell that was affected by the gesture
     */
    protected void handleTouchForRangeSelection(CalendarDayCell touchedCell) {
        int selectedDatesSize = this.selectedDates.size();

        RadCalendarView.SelectionContext selectionContext = new RadCalendarView.SelectionContext();

        if (selectedDatesSize != 1) {
            if (selectedDatesSize > 1) {
                for (CalendarDayCell cell : this.selectedCells) {
                    cell.setSelected(false);
                }
                selectionContext.oldSelection = new ArrayList<Long>(this.selectedDates);
                selectionContext.datesRemoved = new ArrayList<Long>(this.selectedDates);
                this.selectedCells.clear();
                this.selectedDates.clear();
            }
            touchedCell.setSelected(true);
            this.selectedCells.add(touchedCell);
            this.selectedDates.add(touchedCell.getDate());
            selectionContext.datesAdded = new ArrayList<Long>(this.selectedDates);
            selectionContext.newSelection = new ArrayList<Long>(this.selectedDates);
            this.selectedRange = new DateRange(this.selectedDates.get(0), this.selectedDates.get(0));
            if (this.onSelectedDatesChangedListener != null)
                this.onSelectedDatesChangedListener.onSelectedDatesChanged(selectionContext);
        } else {
            selectionContext.oldSelection = new ArrayList<Long>(this.selectedDates);
            Long selectedDate = this.selectedDates.get(0);
            Long touchedDate = touchedCell.getDate();
            if (selectedDate.equals(touchedDate)) {
                return;
            }
            if (selectedDate < touchedDate) {
                this.selectedRange = new DateRange(selectedDate, touchedDate);
            } else {
                this.selectedRange = new DateRange(touchedDate, selectedDate);
            }

            List<Long> dates = this.getDateRangeList(selectedDate, touchedDate);
            selectionContext.datesAdded = new ArrayList<Long>(dates).subList(1, dates.size());
            selectionContext.newSelection = new ArrayList<Long>(dates);
            this.setSelection(dates, selectionContext);
        }
    }

    /**
     * Gets a date range list according to the passed start and end values.
     *
     * @param start the start of the range.
     * @param end   the end of the range.
     * @return the range list.
     */
    protected final List<Long> getDateRangeList(long start, long end) {
        List<Long> dateRange = new ArrayList<Long>();
        Calendar calendar = this.owner.getCalendar();

        boolean borderReached = false;
        boolean increase = start < end;
        calendar.setTimeInMillis(start);

        while (true) {
            List<CalendarDayCell> cells = this.owner.dateToCell().get(calendar.getTimeInMillis());
            if (cells != null && cells.size() > 0) {

                for (CalendarDayCell cell : cells) {
                    if (!cell.isSelectable())
                        borderReached = true;
                }
            }

            if (borderReached)
                break;

            dateRange.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DATE, increase ? 1 : -1);

            if (increase) {
                if (calendar.getTimeInMillis() > end)
                    break;
            } else {
                if (calendar.getTimeInMillis() < end)
                    break;
            }
        }

        return dateRange;
    }
}
