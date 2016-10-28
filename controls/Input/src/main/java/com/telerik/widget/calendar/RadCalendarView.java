package com.telerik.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

import com.telerik.android.common.Function;
import com.telerik.android.common.Procedure;
import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.widget.calendar.decorations.Decorator;
import com.telerik.widget.calendar.decorations.RangeDecorator;
import com.telerik.widget.calendar.events.EventAdapter;
import com.telerik.widget.calendar.events.EventsDisplayMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * Represents a control that allows you to select and display dates in a calendar.
 */
public class RadCalendarView extends ViewGroup {

    EventsDisplayMode eventsDisplayMode = EventsDisplayMode.Normal;
    ViewGroup datesHolder;

    class DatesHolderView extends ViewGroup {

        public DatesHolderView(Context context) {
            this(context, null);
        }

        public DatesHolderView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public DatesHolderView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            setClipChildren(true);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }
    }

    public EventsDisplayMode getEventsDisplayMode() {
        return eventsDisplayMode;
    }

    public void setEventsDisplayMode(EventsDisplayMode eventsDisplayMode) {
        if(this.eventsDisplayMode == eventsDisplayMode) {
            return;
        }
        if (eventsDisplayMode != EventsDisplayMode.Normal && (selectionMode != CalendarSelectionMode.Single && selectionMode != CalendarSelectionMode.None)) {
            throw new IllegalArgumentException("The selected events display mode requires Single or None selection modes to be present.");
        }

        this.eventsDisplayMode = eventsDisplayMode;

        hideEvents(new CalendarGestureManager.OnTransitionCallback() {
            @Override
            public void onTransitionComplete() {
                updateEventManager();
            }
        });
    }

    void hideEvents(CalendarGestureManager.OnTransitionCallback callback) {
        if(this.eventsManager != null && this.eventsManager.currentExpandedCell != null) {
            if(this.eventsManager instanceof EventsInlineManager && displayMode == CalendarDisplayMode.Month) {
                animationsManager.implodeCalendar(this.eventsManager.hideEvents(), callback);
                return;
            } else {
                eventsManager.hideEvents();
            }
        }
        if(callback != null) {
            callback.onTransitionComplete();
        }
    }

    private void updateEventManager() {
        switch (eventsDisplayMode) {

            case Normal:
                this.eventsManager = null;
                break;
            case Inline:
                this.eventsManager = new EventsInlineManager(this);
                break;
            case Popup:
                this.eventsManager = new EventsPopupManager(this);
                break;
        }
    }

    public EventsManager eventsManager() {
        return this.eventsManager;
    }

    class FragmentHolderView extends View {
        CalendarFragment fragment;
        public boolean arrangePassed;
        private int left;
        private int top;
        private int right;
        private int bottom;
        private boolean active;

        public FragmentHolderView(Context context) {
            this(context, null);
        }

        public FragmentHolderView(Context context, CalendarFragment fragment) {
            super(context);
            this.fragment = fragment;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (active)
                fragment.render(canvas);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);

            if(this.left == left && this.top == top && this.right == right && this.bottom == bottom) {
                return;
            }
            this.fragment.arrange((int) (drawingVerticalGridLines ? gridLinesLayer.halfLineWidth : 0), 0, (int) (getWidth() - (drawingVerticalGridLines ? gridLinesLayer.halfLineWidth : 0)), (int) (getHeight() - (drawingHorizontalGridLines ? gridLinesLayer.halfLineWidth : 0)));
            this.arrangePassed = true;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return active;
        }
    }

    class HeadWrapperView extends View {

        boolean dirty = true;

        public HeadWrapperView(Context context) {
            super(context);
        }

        @Override
        public void bringToFront() {
            super.bringToFront();
            dirty = true;
        }

        @Override
        public void invalidate() {
            if (dirty)
                super.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (title != null && title.getVisibility() == ElementVisibility.Visible) {
                drawTitle(canvas);
            }

            if (dayNames != null && dayNames.getVisibility() == ElementVisibility.Visible) {
                drawDayNames(canvas);
            }

            dirty = false;
        }
    }

    private static final int DEFAULT_ANIMATION_DURATION = 200;
    protected int initialRowHeight = -1;
    HeadWrapperView headWrapperView;
    EventsManager eventsManager;

    private Locale locale;
    private Calendar calendar;
    private Calendar workCalendar;
    private boolean suspendUpdate;
    private boolean suspendArrange;

    private boolean showTitle = true;
    private boolean showDayNames = true;
    private boolean showGridLines = true;
    boolean showCellDecorations = true;

    private long displayDate;
    private long minDate;
    private long maxDate;

    private CalendarTask taskToBeExecutedAfterArrangeHasPassed;

    private Decorator cellDecorationsLayer;
    private CalendarAdapter calendarAdapter;
    private CalendarAnimationsManager animationsManager;
    private CalendarGestureManager gestureManager;
    private CalendarSelectionManager selectionManager;
    private EventAdapter eventAdapter;
    CalendarScrollManager scrollManager;
    private CalendarDisplayMode displayMode = CalendarDisplayMode.Month;
    protected CalendarSelectionMode selectionMode = CalendarSelectionMode.Multiple;
    private WeekNumbersDisplayMode weekNumbersDisplayMode = WeekNumbersDisplayMode.None;
    ScrollMode scrollMode = ScrollMode.Sticky;

    int backgroundColor;

    private Drawable yearImage;

    private boolean selectionChangesBackground = true;

    private boolean inOriginalSizeForAllModes = false;
    private boolean isYearModeCompact = false;
    public boolean suspendDisplayModeChange;
    private boolean controlInitialized = false;

    private List<CalendarDayCellStyle> dayCellStyles = new ArrayList<>();
    private List<CalendarMonthCellStyle> monthCellStyles = new ArrayList<>();

    private Procedure<CalendarCell> customizationRule;
    private Function<Long, Integer> dateToColor;
    private Function<Long, Integer> dayNameToColor;

    private OnDisplayDateChangedListener onDisplayDateChangedListener;
    private OnDisplayModeChangedListener onDisplayModeChangedListener;

    private Hashtable<Long, List<CalendarDayCell>> dateToCell;

    private int stateToSave;

    private CalendarDayCell title;
    private CalendarRow dayNames;
    private boolean animationEnabled = true;
    private boolean horizontalScroll = false;
    private GridLinesLayer gridLinesLayer;
    private int dayNamesHeight;
    private int titleHeight;
    private boolean arrangePassed;
    private int originalHeight;
    private boolean calendarShrinked;
    private FragmentHolderView monthFragmentHolder;
    private FragmentHolderView yearFragmentHolder;
    private boolean cellRegistrationSuspended;
    private boolean suspendTouch;

    boolean drawingAllCells = true;
    boolean drawingHorizontalGridLines = true;
    boolean drawingVerticalGridLines = true;

    /**
     * Creates an instance of the {@link com.telerik.widget.calendar.RadCalendarView} class.
     *
     * @param context the context to be used
     */
    public RadCalendarView(Context context) {
        this(context, null);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.calendar.RadCalendarView} class.
     *
     * @param context the context to be used
     * @param attrs   the attributes
     */
    public RadCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.calendarStyle);
    }

    /**
     * Creates an instance of the {@link com.telerik.widget.calendar.RadCalendarView} class.
     *
     * @param context  the context to be used
     * @param attrs    the attributes
     * @param defStyle the default style
     */
    public RadCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        beginUpdate();
        initializeControl();

        final TypedArray array = context.obtainStyledAttributes(
                attrs,
                R.styleable.RadCalendarView,
                defStyle, 0);

        if (array != null) {
            initFromXML(array);
            array.recycle();
        }

        endUpdate();
    }

    public void addDayCellStyle(CalendarDayCellStyle cellStyle) {
        if(cellStyle.getFilter() != null && cellStyle.getFilter().getIsSelected() != null) {
            if(cellStyle.getBorderColor() != null) {
                getCellDecorator().setColor(cellStyle.getBorderColor());
            }
            if(cellStyle.getBorderWidth() != null) {
                getCellDecorator().setStrokeWidth(cellStyle.getBorderWidth());
            }
        }

        this.dayCellStyles.add(cellStyle);
        updateCalendar();
    }

    public void removeDayCellStyle(CalendarDayCellStyle cellStyle) {
        if(cellStyle.getFilter() != null && cellStyle.getFilter().getIsSelected() != null) {
            getCellDecorator().setColor(getAdapter().getStyle().decorationsColor);
            getCellDecorator().setStrokeWidth(getAdapter().getStyle().decorationsStrokeWidth);
        }

        this.dayCellStyles.remove(cellStyle);
        updateCalendar();
    }

    public void addMonthCellStyle(CalendarMonthCellStyle cellStyle) {
        this.monthCellStyles.add(cellStyle);
        updateCalendar();
    }

    public void removeMonthCellStyle(CalendarMonthCellStyle cellStyle) {
        this.monthCellStyles.remove(cellStyle);
        updateCalendar();
    }

    void applyMonthStylesToMonthCell(Paint paint, CalendarCellType dateType, Long date, CalendarMonthCell monthCell) {
        for(CalendarMonthCellStyle monthCellStyle : this.monthCellStyles) {
            monthCellStyle.applyMonthStyle(monthCell);
        }
    }

    void applyMonthStylesToMonthCellElement(CalendarMonthCell monthCell, CalendarMonthCell.MonthCellElement element) {
        for(CalendarMonthCellStyle monthCellStyle : this.monthCellStyles) {
            monthCellStyle.applyDayStyle(monthCell, element);
        }
    }

    /**
     * Gets the day name customization color. This will only affect day names. Effective in all display modes.
     *
     * @return the current day name customization color.
     */
    public Function<Long, Integer> getDayNameToColor() {
        return dayNameToColor;
    }

    /**
     * Sets the day name customization color. This will only affect day names. Effective in all display modes.
     *
     * @param dayNameToColor the new day name customization color.
     */
    public void setDayNameToColor(Function<Long, Integer> dayNameToColor) {
        if(this.dayNameToColor == dayNameToColor) {
            return;
        }
        this.dayNameToColor = dayNameToColor;
        generateCalendarDayNameElements();
        invalidateArrange();
    }

    /**
     * Gets a value determining whether to draw all cells of the different fragments, or only draw the cells of the current fragment's date.
     *
     * @return <code>true</code> if all cells should be drawn, <code>false</code> otherwise.
     */
    public boolean isDrawingAllCells() {
        return drawingAllCells;
    }

    /**
     * Sets a value determining whether to draw all cells of the different fragments, or only draw the cells of the current fragment's date.
     *
     * @param drawingAllCells <code>true</code> if all cells should be drawn, <code>false</code> otherwise.
     */
    public void setDrawingAllCells(boolean drawingAllCells) {
        if(this.drawingAllCells == drawingAllCells) {
            return;
        }
        this.drawingAllCells = drawingAllCells;
        setBackgroundColor(backgroundColor);
        updateFragments(true);
    }

    /**
     * Gets a value determining whether the vertical grid lines of the cells will be rendered.
     *
     * @return <code>true</code> if the vertical grid lines of the cells will be rendered, <code>false</code> otherwise.
     */
    public boolean isDrawingVerticalGridLines() {
        return this.drawingVerticalGridLines;
    }

    /**
     * Sets a value determining whether the vertical grid lines of the cells will be rendered.
     *
     * @param drawingVerticalGridLines <code>true</code> if the vertical grid lines of the cells will be rendered, <code>false</code> otherwise.
     */
    public void setDrawingVerticalGridLines(boolean drawingVerticalGridLines) {
        this.drawingVerticalGridLines = drawingVerticalGridLines;
    }

    /**
     * Gets a value determining whether the horizontal grid lines of the cells will be rendered.
     *
     * @return <code>true</code> if the horizontal grid lines of the cells will be rendered, <code>false</code> otherwise.
     */
    public boolean isDrawingHorizontalGridLines() {
        return drawingHorizontalGridLines;
    }

    /**
     * Sets a value determining whether the horizontal grid lines of the cells will be rendered.
     *
     * @param drawingHorizontalGridLines <code>true</code> if the horizontal grid lines of the cells will be rendered, <code>false</code> otherwise.
     */
    public void setDrawingHorizontalGridLines(boolean drawingHorizontalGridLines) {
        this.drawingHorizontalGridLines = drawingHorizontalGridLines;
    }

    /**
     * Gets the height of the day names. It will be preserved over display mode changes and applied when appropriate.
     *
     * @return the current stored height for the day names.
     */
    public int getDayNamesHeight() {
        return this.dayNamesHeight;
    }

    /**
     * Sets the height of the day names. It will be preserved over display mode changes and applied when appropriate.
     *
     * @param dayNamesHeight the new height for the day names to be stored.
     */
    public void setDayNamesHeight(int dayNamesHeight) {
        this.dayNamesHeight = dayNamesHeight;

        if (!this.calendarShrinked) {
            invalidateArrange();
            invalidate();
        }
    }

    /**
     * Gets the height of the title. It will be preserved over display mode changes and applied when appropriate.
     *
     * @return the current stored height for the title.
     */
    public int getTitleHeight() {
        return this.titleHeight;
    }

    /**
     * Sets the height of the title. It will be preserved over display mode changes and applied when appropriate.
     *
     * @param titleHeight the new height for the title to be stored.
     */
    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;

        if (!this.calendarShrinked) {
            invalidateArrange();
            invalidate();
        }
    }

    /**
     * Gets the manager responsible for handling the gestures.
     *
     * @return the current gestures manager.
     */
    public CalendarGestureManager getGestureManager() {
        return gestureManager;
    }

    /**
     * Sets the manager responsible for handling the gestures.
     *
     * @param gestureManager the new gestures manager.
     */
    public void setGestureManager(CalendarGestureManager gestureManager) {
        if(this.gestureManager == gestureManager) {
            return;
        }
        this.gestureManager = gestureManager;
        this.gestureManager.setAnimationsManager(this.animationsManager);
        this.gestureManager.setDisplayMode(this.displayMode);
        this.gestureManager.setScrollManager(this.scrollManager);
        this.gestureManager.setScrollMode(this.scrollMode);
        this.gestureManager.setSelectionManager(this.selectionManager);
        this.gestureManager.setSelectionMode(this.selectionMode);
    }

    /**
     * Gets the animations manager.
     *
     * @return the current animations manager.
     */
    public CalendarAnimationsManager getAnimationsManager() {
        return this.animationsManager;
    }

    /**
     * Sets the animation manager.
     *
     * @param animationsManager the new animations manager.
     */
    public void setAnimationsManager(CalendarAnimationsManager animationsManager) {
        if(this.animationsManager == animationsManager) {
            return;
        }
        this.animationsManager = animationsManager;
        this.gestureManager.setAnimationsManager(animationsManager);
        invalidate();
    }

    /**
     * Gets the selection manager.
     *
     * @return the current selection manager.
     */
    public CalendarSelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    /**
     * Sets the selection manager.
     *
     * @param selectionManager the new selection manager.
     */
    public void setSelectionManager(CalendarSelectionManager selectionManager) {
        if(this.selectionManager == selectionManager) {
            return;
        }
        this.selectionManager = selectionManager;
        this.gestureManager.setSelectionManager(selectionManager);
    }

    /**
     * Gets a value that determines whether the calendar will scroll horizontally or vertically.
     *
     * @return <code>true</code> will result the calendar to scroll horizontally, <code>false</code>
     * will result it to scroll vertically.
     */
    public boolean isHorizontalScroll() {
        return this.horizontalScroll;
    }

    /**
     * Sets a value that determines whether the calendar will scroll horizontally or vertically.
     *
     * @param horizontal the new scroll direction.  <code>true</code> will result the calendar to scroll horizontally, <code>false</code>
     *                   will result it to scroll vertically.
     */
    public void setHorizontalScroll(boolean horizontal) {
        if(this.horizontalScroll == horizontal) {
            return;
        }
        this.horizontalScroll = horizontal;
        this.scrollManager.setHorizontalScroll(horizontal);

        invalidateArrange(); // TODO position fragments instead of rearranging them
        updateFragments(true);
        invalidate();
    }

    /**
     * Gets the scroll mode.
     *
     * @return the current scroll mode.
     */
    public ScrollMode getScrollMode() {
        return this.scrollMode;
    }

    /**
     * Sets the scroll mode.
     *
     * @param scrollMode the new scroll mode.
     */
    public void setScrollMode(ScrollMode scrollMode) {
        if(this.scrollMode == scrollMode) {
            return;
        }
        this.scrollMode = scrollMode;
        this.gestureManager.setScrollMode(scrollMode);
        this.scrollManager.setScrollMode(scrollMode);
        invalidateArrange(); // TODO position fragments instead of rearranging them
        updateFragments(true);
        invalidate();
    }

    /**
     * Gets a values stating whether the animations are enabled or not.
     *
     * @return <code>true</code> if the animations are enabled, <code>false</code> otherwise.
     */
    public boolean isAnimationEnabled() {
        return this.animationEnabled;
    }

    /**
     * Sets a values stating whether the animations are enabled or not.
     *
     * @param enabled the new animations state.
     */
    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }

    /**
     * Holds a collection that has all the cells sorted and accessible vy date.
     *
     * @return the current collection of cells accessible by date.
     */
    public Hashtable<Long, List<CalendarDayCell>> dateToCell() {
        return this.dateToCell;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LicensingProvider.verify(this.getContext());
    }

    /**
     * Returns the currently used {@link com.telerik.widget.calendar.CalendarAdapter}
     * by this instance. This calendarAdapter is used for getting and updating the
     * {@link com.telerik.widget.calendar.CalendarCell} instances which populate the calendar.
     *
     * @return the currently used CalendarAdapter
     */
    public CalendarAdapter getAdapter() {
        return calendarAdapter;
    }

    /**
     * Sets a new {@link com.telerik.widget.calendar.CalendarAdapter} instance that will be
     * used by this calendar instance to create and update its
     * {@link com.telerik.widget.calendar.CalendarCell} instances.
     *
     * @param calendarAdapter the new CalendarAdapter
     */
    public void setAdapter(CalendarAdapter calendarAdapter) {
        if(this.calendarAdapter == calendarAdapter) {
            return;
        }
        this.calendarAdapter = calendarAdapter;
        rebuildCalendar();
    }

    /**
     * Returns the current display date. It is of type <code>long</code> and represents one of the
     * dates that are currently visible by the calendar.
     * The default value is today.
     *
     * @return date that is currently visible
     */
    public long getDisplayDate() {
        return this.displayDate;
    }

    /**
     * Sets a new display date. If the new value is already visible, the change may not be noticed.
     * Otherwise, the visible period will be changed so that the new display date becomes visible.
     * If the new value represents a certain period of the day, for example January 1st 2014, 3:23 AM,
     * the value will be clamped to the value which represents January 1st 2014, 0:00 AM.
     *
     * @param value the new display date
     * @see #setOnDisplayDateChangedListener(com.telerik.widget.calendar.RadCalendarView.OnDisplayDateChangedListener)
     */
    public void setDisplayDate(long value) {
        if(this.displayDate == value) {
            return;
        }
        long oldDate = this.displayDate;
        long newDate = CalendarTools.getDateStart(value);

        if (this.maxDate != 0) {
            if (this.maxDate < newDate) {
                throw new IllegalArgumentException("The value of displayDate should be less than the value of maxDate.");
            }
        }
        if (this.minDate != 0) {
            if (this.minDate > newDate) {
                throw new IllegalArgumentException("The value of minDate should be less than the value of displayDate.");
            }
        }
        this.displayDate = newDate;
        this.calendar.setTimeInMillis(this.displayDate);
        this.onDisplayDateChanged();
        if (this.onDisplayDateChangedListener != null) {
            this.onDisplayDateChangedListener.onDisplayDateChanged(oldDate, newDate);
        }

        this.scrollManager.updateBorders();
        invalidate();
    }

    void changeDisplayModeToMonth(long date) {
        this.displayDate = date;
        changeDisplayMode(CalendarDisplayMode.Month);
    }

    public void setDisplayDateSilent(long value) {
        long oldDate = this.displayDate;
        long newDate = CalendarTools.getDateStart(value);

        if (this.maxDate != 0) {
            if (this.maxDate < newDate) {
                throw new IllegalArgumentException("The value of displayDate should be less than the value of maxDate.");
            }
        }
        if (this.minDate != 0) {
            if (this.minDate > newDate) {
                throw new IllegalArgumentException("The value of minDate should be less than the value of displayDate.");
            }
        }
        this.displayDate = newDate;
        this.calendar.setTimeInMillis(this.displayDate);
        //this.onDisplayDateChanged();
        if (this.onDisplayDateChangedListener != null) {
            this.onDisplayDateChangedListener.onDisplayDateChanged(oldDate, newDate);
        }

        this.scrollManager.updateBorders();
        invalidate();
    }

    /**
     * Gets the calendar title element.
     *
     * @return the current calendar title element.
     */
    public CalendarTextElement title() {
        return this.title;
    }

    /**
     * Gets the day names row.
     *
     * @return the current day names row.
     */
    public CalendarRow dayNames() {
        return this.dayNames;
    }

    /**
     * Shifts the current date either forward or backward, having in mind the current display mode.
     *
     * @param increase <code>true</code> will result in the current date to increase, <code>false</code>
     *                 will result in the date to decrease.
     */
    public void shiftDate(boolean increase) {
        long newDate = CalendarTools.calculateNewValue(increase, this.displayDate, this.displayMode);

        if (minDate != 0) {
            if (newDate < minDate) {
                newDate = getAlternativeValueForMinDate();
            }
        }

        if (maxDate != 0) {
            if (newDate > maxDate) {
                newDate = getAlternativeValueForMaxDate();
            }
        }

        if ((maxDate != 0 && newDate > maxDate) || (minDate != 0 && newDate < minDate))
            return;

        this.setDisplayDate(newDate);
    }

    /**
     * States whether the calendar can shift back to the previous date in accordance to the current display mode and minimum date value.
     *
     * @return <code>true</code> if the shift can be made, <code>false</code> otherwise.
     */
    public boolean canShiftToPreviousDate() {
        return this.minDate == 0 ||
                getAlternativeValueForMinDate() >= minDate;
    }

    long getAlternativeValueForMinDate() {
        switch (displayMode) {
            case Month:
                return CalendarTools.getLastDateInMonth(CalendarTools.calculateNewValue(false, displayDate, displayMode));
            case Year:
                return CalendarTools.getLastDateInYear(CalendarTools.calculateNewValue(false, displayDate, displayMode));
            case Week:
                return CalendarTools.getLastDateInWeek(CalendarTools.calculateNewValue(false, displayDate, displayMode));
        }

        return minDate - 1;
    }

    /**
     * States whether the calendar can shift forward to the next date in accordance to the current display mode and maximum date value.
     *
     * @return <code>true</code> if the shift can be made, <code>false</code> otherwise.
     */
    public boolean canShiftToNextDate() {
        return this.maxDate == 0 ||
                getAlternativeValueForMaxDate() <= maxDate;
    }

    long getAlternativeValueForMaxDate() {
        switch (displayMode) {
            case Month:
                return CalendarTools.getFirstDateInMonth(CalendarTools.calculateNewValue(true, displayDate, displayMode));
            case Year:
                return CalendarTools.getFirstDateInYear(CalendarTools.calculateNewValue(true, displayDate, displayMode));
            case Week:
                return CalendarTools.getFirstDateInWeek(CalendarTools.calculateNewValue(true, displayDate, displayMode));
        }

        return maxDate + 1;
    }

    /**
     * Changes the display date to a date that is not currently visible. The new date is
     * determined depending on the current {@link com.telerik.widget.calendar.CalendarDisplayMode}.
     * If it is month, the display date is increased by one month, etc.
     * The value will not be changed if that would exceed the limits defined by max date.
     *
     * @see #getDisplayMode()
     * @see #animateToPrevious()
     * @see #getMaxDate()
     */
    public void animateToNext() {
        if (!gestureManager.showingEventsForCell() && canShiftToNextDate()) {
            this.animationsManager.animateToNextDate();
        }
    }

    /**
     * Changes the display date to a date that is not currently visible. The new date is
     * determined depending on the current {@link com.telerik.widget.calendar.CalendarDisplayMode}.
     * If it is month, the display date is decreased by one month, etc.
     * The value will not be changed if that would exceed the limits defined by min date.
     *
     * @see #getDisplayMode()
     * @see #animateToNext()
     */
    public void animateToPrevious() {
        if (!gestureManager.showingEventsForCell() && canShiftToPreviousDate()) {
            this.animationsManager.animateToPreviousDate();
        }
    }

    /**
     * Notifies the instance that it needs to updateActiveFragment,
     * for example when the events are added or updated.
     */
    public void notifyDataChanged() {
        updateFragments(true);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("stateToSave", this.stateToSave);

        bundle.putSerializable("displayMode", this.displayMode);

        //bundle.putSerializable("style", this.calendarAdapter.getStyle());

        bundle.putLong("displayDate", this.displayDate);

        List<Long> selectedDates = this.gestureManager.getSelectionManager().getSelectedDates();
        if (selectedDates != null && selectedDates.size() > 0) {
            long list[] = new long[selectedDates.size()];
            for (int i = 0; i < selectedDates.size(); i++) {
                list[i] = selectedDates.get(i);
            }
            bundle.putLongArray("selectedDates", list);
        }

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            this.stateToSave = bundle.getInt("stateToSave");

            long[] selectedDatesArray = bundle.getLongArray("selectedDates");
            if (selectedDatesArray != null) {
                List<Long> selectedDates = new ArrayList<Long>();
                for (long value : selectedDatesArray) {
                    selectedDates.add(value);
                }

                this.gestureManager.getSelectionManager().setSelectedDates(selectedDates);
                this.gestureManager.getSelectionManager().syncSelectedCellsWithDates();
            }

            this.taskToBeExecutedAfterArrangeHasPassed = (new CalendarTask() {
                @Override
                public void execute() {
                    changeDisplayMode(displayMode(), false);
                }

                @Override
                public CalendarDisplayMode displayMode() {
                    return (CalendarDisplayMode) bundle.getSerializable("displayMode");
                }
            });

            setDisplayDate(bundle.getLong("displayDate"));

            state = bundle.getParcelable("instanceState");
            //this.calendarAdapter.setStyle((CalendarStyle) bundle.getSerializable("style"));
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            if (!arrangePassed)
                arrangePassed = true;

            invalidateArrange();

            if (taskToBeExecutedAfterArrangeHasPassed != null)
                executeWaitingTask();
        }
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    public void setBackgroundColor(int color) {
        if(this.backgroundColor == color) {
            return;
        }
        super.setBackgroundColor(color);
        if (!drawingAllCells && displayMode != CalendarDisplayMode.Year && (scrollMode == ScrollMode.Stack || scrollMode == ScrollMode.Overlap || scrollManager.scrollShouldBeHorizontal())) {
            this.scrollManager.currentFragmentHolder.setBackgroundColor(color);
            this.scrollManager.previousFragmentHolder.setBackgroundColor(color);
            this.scrollManager.nextFragmentHolder.setBackgroundColor(color);
        }

        this.backgroundColor = color;
    }

    private void executeWaitingTask() {
        if (this.taskToBeExecutedAfterArrangeHasPassed != null) {
            this.taskToBeExecutedAfterArrangeHasPassed.execute();
            this.taskToBeExecutedAfterArrangeHasPassed = null;
        }
    }

    /**
     * Used to invalidate the arrange of all calendar elements.
     */
    protected void invalidateArrange() {
        if (!arrangePassed || this.suspendArrange)
            return;

        int halfGridLineWidthH = 0;
        int halfGridLineWidthV = 0;

        if (!drawingVerticalGridLines)
            halfGridLineWidthH = (int) (gridLinesLayer.halfLineWidth);
        if (!drawingHorizontalGridLines)
            halfGridLineWidthV = (int) (gridLinesLayer.halfLineWidth);

        int left = this.getPaddingLeft();
        int top = this.getPaddingTop();
        int right = (this.getMeasuredWidth() - (this.getPaddingLeft() + this.getPaddingRight()));
        int bottom = this.getMeasuredHeight() - (this.getPaddingTop() + this.getPaddingBottom());

        if (this.title.getVisibility() != ElementVisibility.Gone) {
            this.title.arrange(left + (int) (gridLinesLayer.halfLineWidth), top + (int) (gridLinesLayer.halfLineWidth), right - (int) (gridLinesLayer.halfLineWidth), (top + this.titleHeight));
            top += this.title.getHeight();
        } else {
            this.title.arrange(left, top, right, top);
        }

        if (this.dayNames.getVisibility() != ElementVisibility.Gone) {
            this.dayNames.arrange(left + (int) (gridLinesLayer.halfLineWidth), top + (int) (gridLinesLayer.halfLineWidth), right - (int) (gridLinesLayer.halfLineWidth), (top + this.dayNamesHeight));
            top += this.dayNames.getHeight();
        } else {
            this.dayNames.arrange(left, top, right, top);
        }

        this.headWrapperView.layout(getPaddingLeft(), getPaddingTop(), right, top + (int)gridLinesLayer.halfLineWidth);

        this.datesHolder.layout(left, top, right, this.displayMode == CalendarDisplayMode.Week ? (top + this.initialRowHeight) : bottom);
        this.scrollManager.arrange(0, 0, this.datesHolder.getWidth(), this.datesHolder.getHeight());

        if(!calendarShrinked && !suspendDisplayModeChange) {
            this.yearFragmentHolder.layout(left, dayNames.getTop(), right, bottom);
        }

        if (this.initialRowHeight == -1) {
            this.initialRowHeight = (int) this.scrollManager.currentFragment().rowHeight();
        }

        this.suspendArrange = false;
    }

    /**
     * Gets a value indicating whether grid lines between the cells will be drawn.
     *
     * @return whether grid lines are shown
     */
    public boolean getShowGridLines() {
        return this.showGridLines;
    }

    /**
     * Sets a value indicating whether grid lines between the cells will be drawn.
     *
     * @param showGridLines whether grid lines are shown
     */
    public void setShowGridLines(boolean showGridLines) {
        if (this.showGridLines != showGridLines) {
            this.showGridLines = showGridLines;
            invalidate();
        }
    }

    /**
     * Gets a value indicating whether there will be additional decorations for selected cells.
     * The default decoration layer draws a border over the selected cells.
     *
     * @return whether additional decoration are drawn for the selected cells
     */
    public boolean getShowCellDecorations() {
        return this.showCellDecorations;
    }

    /**
     * Sets a value indicating whether there will be additional decorations for selected cells.
     * The default decoration layer draws a border over the selected cells.
     *
     * @param showCellDecorations whether additional decoration are drawn for the selected cells
     */
    public void setShowCellDecorations(boolean showCellDecorations) {
        if (this.showCellDecorations != showCellDecorations) {
            this.showCellDecorations = showCellDecorations;
            this.scrollManager.markFragmentsDirty();
            this.getCellDecorator().clearDecorations();
            invalidate();
        }
    }

    /**
     * Gets a layer which is responsible for additional decoration of the selected cells.
     * The default decoration layer draws a border over the selected cells. Deprecated - use {@link #getCellDecorator()} instead.
     *
     * @return the cell decorations layer
     * @deprecated
     */
    public Decorator getCellDecorationsLayer() {
        return this.cellDecorationsLayer;
    }

    /**
     * Sets a new layer which will be responsible for additional decoration of the selected cells.
     * The default decoration layer draws a border over the selected cells. Deprecated - {@link #setCellDecorator(com.telerik.widget.calendar.decorations.Decorator)} instead.
     *
     * @param cellDecorator the layer that draws decoration for selected cells
     * @deprecated
     */
    public void setCellDecorationsLayer(CellDecorationsLayer cellDecorator) {
        setCellDecorator(cellDecorator);
    }

    /**
     * Gets a decorator responsible for the decoration of the selected cells.
     *
     * @return the current cells decorator.
     */
    public Decorator getCellDecorator() {
        return this.cellDecorationsLayer;
    }

    boolean selectionChangesBackground() {
        return selectionChangesBackground;
    }

    /**
     * Sets a decorator responsible for the decoration of the selected cells.
     *
     * @param cellDecorator the new cells decorator.
     */
    public void setCellDecorator(Decorator cellDecorator) {
        if (cellDecorator == null)
            throw new NullPointerException("cellDecorator");

        if (this.cellDecorationsLayer != cellDecorator) {
            this.cellDecorationsLayer = cellDecorator;
            selectionChangesBackground = !(cellDecorator instanceof RangeDecorator);

            invalidate();
        }
    }

    /**
     * Returns the currently used {@link com.telerik.widget.calendar.WeekNumbersDisplayMode}
     * by this instance. This value defines how the week number information will be
     * visualized. The default value is <code>None</code> which means that week number
     * information will not be visible.
     *
     * @return the current week number display mode
     */
    public WeekNumbersDisplayMode getWeekNumbersDisplayMode() {
        return this.weekNumbersDisplayMode;
    }

    /**
     * Sets a new {@link com.telerik.widget.calendar.WeekNumbersDisplayMode} that will be
     * used by this calendar instance to determine how the week number information will be
     * presented. The default value is <code>None</code>.
     *
     * @param value the new week number display mode
     */
    public void setWeekNumbersDisplayMode(WeekNumbersDisplayMode value) {
        if (this.weekNumbersDisplayMode != value) {
            this.weekNumbersDisplayMode = value;
            rebuildCalendar();
        }
    }

    /**
     * Used to rebuild the calendar when needed.
     */
    protected void rebuildCalendar() {
        this.resetCalendar();
        this.updateCalendar();
        this.scrollManager.updateActiveFragment();
        this.invalidateArrange();
        invalidate();
    }

    private void updateCalendar(boolean force) {
        if(!controlInitialized) {
            return;
        }

        this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
        for(int i = 0; i < dayNames.cellsCount(); i++) {
            CalendarDayCell cell = (CalendarDayCell)dayNames.getCell(i);
            this.calendarAdapter.updateDayNameCell(cell, i - 1);
        }
        this.updateFragments(force);
    }

    public void updateCalendar() {
        updateCalendar(true);
    }

    /**
     * Returns the currently used {@link com.telerik.widget.calendar.events.EventAdapter}.
     * This calendarAdapter is responsible for the events that will be visualized by this instance.
     * The calendarAdapter defines the full list of events and determines
     * which of them should be visible for each date.
     *
     * @return the current event calendarAdapter
     */
    public EventAdapter getEventAdapter() {
        return eventAdapter;
    }

    /**
     * Sets a new {@link com.telerik.widget.calendar.events.EventAdapter} that will be
     * used by this calendar instance. This calendarAdapter will define the full list of events
     * that will be visualized and will determine which of them should be visible
     * for each date.
     *
     * @param eventAdapter a new event calendarAdapter
     */
    public void setEventAdapter(EventAdapter eventAdapter) {
        if(this.eventAdapter == eventAdapter) {
            return;
        }
        this.eventAdapter = eventAdapter;
        updateFragments();
        invalidate();
    }

    /**
     * Returns the current {@link com.telerik.widget.calendar.CalendarSelectionMode}.
     * This mode determines the type of selection used by this instance. For example,
     * in all modes when you choose a cell it will be selected, however when you
     * choose another cell the selection will be determined by the selection mode.
     * If it is <code>Single</code>, the old selection will be cleared.
     * If it si <code>Multiple</code>, the new selection will consist of the both dates - the old and the new.
     * And if it is <code>Range</code>, the new selection will consist of the whole range of dates between the old and the new date.
     * The default value is <code>Multiple</code>.
     *
     * @return the current selection mode
     */
    public CalendarSelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * Sets a new {@link com.telerik.widget.calendar.CalendarSelectionMode}.
     * This mode determines the type of selection used by this instance. For example,
     * in all modes when you choose a cell it will be selected, however when you
     * choose another cell the selection will be determined by the selection mode.
     * If it is <code>Single</code>, the old selection will be cleared.
     * If it si <code>Multiple</code>, the new selection will consist of the both dates - the old and the new.
     * And if it is <code>Range</code>, the new selection will consist of the whole range of dates between the old and the new date.
     * The default value is <code>Multiple</code>.
     *
     * @param selectionMode a new calendar selection mode
     */
    public void setSelectionMode(CalendarSelectionMode selectionMode) {
        if (this.selectionMode != selectionMode) {
            this.selectionMode = selectionMode;
            this.selectionManager.setSelectionMode(selectionMode);
            this.gestureManager.setSelectionMode(selectionMode);
        }
    }

    /**
     * Gets the renderer responsible for rendering the grid lines.
     *
     * @return the current grid lines renderer.
     */
    public GridLinesLayer getGridLinesLayer() {
        return gridLinesLayer;
    }

    /**
     * Sets the renderer responsible for rendering the grid lines.
     *
     * @param gridLinesLayer the new grid lines renderer.
     */
    public void setGridLinesLayer(GridLinesLayer gridLinesLayer) {
        if (gridLinesLayer == null)
            throw new NullPointerException("gridLinesLayer");

        if (this.gridLinesLayer != gridLinesLayer) {
            this.gridLinesLayer = gridLinesLayer;
            this.invalidate();
        }
    }

    /**
     * Gets the scroll manager.
     *
     * @return the current scroll manager.
     */
    public CalendarScrollManager getScrollManager() {
        return scrollManager;
    }

    /**
     * Sets the scroll manager.
     *
     * @param scrollManager the new scroll manager.
     */
    public void setScrollManager(CalendarScrollManager scrollManager) {
        if(this.scrollManager == scrollManager) {
            return;
        }
        this.scrollManager = scrollManager;
        this.gestureManager.setScrollManager(scrollManager);
        this.animationsManager.setScrollManager(scrollManager);
        invalidate();
    }

    /**
     * Gets the listener that is being called when a date has been selected.
     *
     * @return the current date selected listener.
     */
    public OnSelectedDatesChangedListener getOnSelectedDatesChangedListener() {
        return this.selectionManager.getOnSelectedDatesChangedListener();
    }

    /**
     * Sets the listener that is being called when a date has been selected.
     *
     * @param listener the new date selected listener.
     */
    public void setOnSelectedDatesChangedListener(OnSelectedDatesChangedListener listener) {
        this.selectionManager.setOnSelectedDatesChangedListener(listener);
    }

    /**
     * Gets the selected dates.
     *
     * @return the currently selected dates.
     */
    public List<Long> getSelectedDates() {
        return this.selectionManager.getSelectedDates();
    }

    /**
     * Sets the selected dates.
     *
     * @param selectedDates the new selected dates.
     */
    public void setSelectedDates(List<Long> selectedDates) {
        this.selectionManager.setSelectedDates(selectedDates);
        invalidate();
    }

    /**
     * Gets the selected range.
     *
     * @return the current selected range.
     */
    public DateRange getSelectedRange() {
        return this.selectionManager.getSelectedRange();
    }

    /**
     * Sets the selected range.
     *
     * @param selectionRange the new selected range.
     */
    public void setSelectedRange(DateRange selectionRange) {
        this.selectionManager.setSelectedRange(selectionRange);
        invalidate();
    }

    /**
     * Sets a listener to be called when the display date has been changed.
     *
     * @param listener the new listener.
     */
    public void setOnDisplayDateChangedListener(OnDisplayDateChangedListener listener) {
        this.onDisplayDateChangedListener = listener;
    }

    /**
     * Sets a listener to be called when the display mode has been changed.
     *
     * @param listener the new listener.
     */
    public void setOnDisplayModeChangedListener(OnDisplayModeChangedListener listener) {
        this.onDisplayModeChangedListener = listener;
    }

    /**
     * Sets a listener to be called when a cell has been clicked.
     *
     * @param listener the new listener.
     */
    public void setOnCellClickListener(OnCellClickListener listener) {
        this.gestureManager.setOnCellClickListener(listener);
    }

    /**
     * Returns a boolean which determines whether the current instance
     * will render title. The title is the first row and displays the year if the display mode is Year
     * and the month, otherwise.
     * The default value is <code>true</code>.
     *
     * @return whether the default title will be shown
     */
    public boolean getShowTitle() {
        return this.showTitle;
    }

    /**
     * Sets a boolean which determines whether the current instance
     * will render title. The title is the first row and displays the year if the display mode is Year
     * and the month, otherwise.
     * The default value is <code>true</code>.
     *
     * @param value whether the title should be drawn
     */
    public void setShowTitle(boolean value) {
        if (this.showTitle != value) {
            this.showTitle = value;
            this.handleShowTitleChange();
        }
    }

    /**
     * Returns a boolean which determines whether the current instance
     * will render day names. The day names are drawn below the title
     * only when the display mode is month or week.
     * The default value is <code>true</code>.
     *
     * @return whether the day names will be shown
     */
    public boolean getShowDayNames() {
        return this.showDayNames;
    }

    /**
     * Sets a boolean which determines whether the current instance
     * will render day names. The day names are drawn below the title
     * only when the display mode is month or week.
     * The default value is <code>true</code>.
     *
     * @param value whether the title should be drawn
     */
    public void setShowDayNames(boolean value) {
        if (this.showDayNames != value) {
            this.showDayNames = value;
            if (this.displayMode != CalendarDisplayMode.Year) {
                this.handleShowDayNamesChange();
            }
        }
    }

    /**
     * Returns a {@link com.telerik.android.common.Function} which determines the color that is
     * used for each date in this instance. If the value returned by the function is
     * <code>null</code> for some date, the default value for color will be used.
     * This can be used for example to render dates which are holidays in <code>Red</code>.
     *
     * @return the current function which determines different color for different dates
     */
    public Function<Long, Integer> getDateToColor() {
        return dateToColor;
    }

    /**
     * Sets a {@link com.telerik.android.common.Function} which determines the color that is
     * used for each date in this instance. If the value returned by the function is
     * <code>null</code> for some date, the default value for color will be used.
     * This can be used for example to render dates which are holidays in <code>Red</code>.
     *
     * @param dateToColor a function which will determine what color to be used for different dates
     */
    public void setDateToColor(Function<Long, Integer> dateToColor) {
        if(this.dateToColor == dateToColor) {
            return;
        }
        this.dateToColor = dateToColor;

        rebuildCalendar();
    }

    /**
     * Returns a {@link com.telerik.android.common.Procedure} which makes modifications
     * to a {@link com.telerik.widget.calendar.CalendarCell} after it is updated and/or
     * created.
     * For example, this modifications may include setting the foreground that is used by the cell
     * to <code>Red</code> for cell which represent holidays.
     *
     * @return the current procedure which defines modification applied for a calendar cell
     */
    public Procedure<CalendarCell> getCustomizationRule() {
        return customizationRule;
    }

    /**
     * Sets a {@link com.telerik.android.common.Procedure} which makes modifications
     * to a {@link com.telerik.widget.calendar.CalendarCell} after it is updated and/or
     * created.
     * For example, this modifications may include setting the foreground that is used by the cell
     * to <code>Red</code> for cell which represent holidays.
     *
     * @param customizationRule a procedure which defines modification applied for a calendar cell
     */
    public void setCustomizationRule(Procedure<CalendarCell> customizationRule) {
        if(this.customizationRule == customizationRule) {
            return;
        }
        this.customizationRule = customizationRule;

        rebuildCalendar();
    }

    /**
     * Returns the current min date. It is of type <code>long</code> and represents the min date
     * that can be represented and/or selected by the calendar.
     * The default value is <code>0</code>, which means that there is no min date.
     *
     * @return the current min date
     */
    public long getMinDate() {
        return this.minDate;
    }

    /**
     * Sets a new min date. If the new value is after the current display date, the display date will be changed
     * as well. If the new min date value represents a certain period of the day, for example January 1st 2014, 3:23 AM,
     * the value will be clamped to the value which represents January 1st 2014, 0:00 AM.
     * If the new value is after the current max date, an exception will be thrown.
     *
     * @param minDate the new min date
     * @throws java.lang.IllegalArgumentException If the new min date is after the current max date
     * @see #getMaxDate()
     */
    public void setMinDate(long minDate) {
        long newMinDate = CalendarTools.getDateStart(minDate);
        if (newMinDate != this.minDate) {

            if (this.maxDate != 0) {
                if (this.maxDate < newMinDate) {
                    throw new IllegalArgumentException("The value of minDate should be less than the value of maxDate.");
                }
            }

            this.minDate = newMinDate;

            if (this.displayDate < this.minDate) {
                this.setDisplayDate(this.minDate);
            }

            this.updateFragments(true);
            this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
            this.scrollManager.updateBorders();
            invalidate();
        }
    }

    /**
     * Returns the current max date. It is of type <code>long</code> and represents the max date
     * that can be represented and/or selected by the calendar.
     * The default value is <code>0</code>, which means that there is no max date.
     *
     * @return the current max date
     */
    public long getMaxDate() {
        return this.maxDate;
    }

    /**
     * Sets a new max date. If the new value is before the current display date, the display date will be changed
     * as well. If the new max date value represents a certain period of the day, for example January 1st 2014, 3:23 AM,
     * the value will be clamped to the value which represents January 1st 2014, 0:00 AM.
     * If the new value is before the current min date, an exception will be thrown.
     *
     * @param maxDate the new max date
     * @throws java.lang.IllegalArgumentException If the new max date is before the current min date
     * @see #getMinDate()
     */
    public void setMaxDate(long maxDate) {
        long newMaxDate = CalendarTools.getDateStart(maxDate);
        if (newMaxDate != this.maxDate) {

            if (this.minDate != 0) {
                if (this.minDate > newMaxDate) {
                    throw new IllegalArgumentException("The value of minDate should be less than the value of maxDate.");
                }
            }

            this.maxDate = newMaxDate;

            if (this.displayDate > this.maxDate) {
                this.setDisplayDate(this.maxDate);
            }

            this.updateFragments(true);
            this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
            this.scrollManager.updateBorders();
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!suspendTouch)
            this.gestureManager.handleTouch(event);

        return super.onTouchEvent(event);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onDown(MotionEvent event) {
        return this.gestureManager.onDown(event);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public void onShowPress(MotionEvent e) {
        this.gestureManager.onShowPress(e);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onSingleTapUp(MotionEvent event) {
        return this.gestureManager.onSingleTapUp(event);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onScroll(MotionEvent event, MotionEvent event2, float v, float v2) {
        return gestureManager.onScroll(event, event2, v, v2);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public void onLongPress(MotionEvent event) {
        this.gestureManager.onLongPress(event);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocity1, float velocity2) {
        return false;
    }

    /**
     * Begins update thus preventing the calendar to invalidate.
     */
    public void beginUpdate() {
        beginUpdate(false);
    }

    /**
     * Begins update preventing the calendar to both invalidate and rearrange its elements.
     *
     * @param suspendArrange <code>true</code> will result in suspending the arrange as well, <code>false</code>
     *                       will not prevent the arrange.
     */
    public void beginUpdate(boolean suspendArrange) {
        this.suspendUpdate = true;
        this.suspendArrange = suspendArrange;
    }

    /**
     * Ends the update by calling the calendar to invalidate one single time.
     */
    public void endUpdate() {
        endUpdate(false);
    }

    /**
     * Ends the update by first calling the {@link #invalidateArrange()} followed by an invalidation.
     *
     * @param releaseArrange <code>true</code> will cause the arrange to be performed before the invalidation,
     *                       <code>false</code> will not cause arrange.
     */
    public void endUpdate(boolean releaseArrange) {
        this.suspendUpdate = false;

        if (releaseArrange) {
            this.suspendArrange = false;
            invalidateArrange();
        }

        invalidate();
    }

    @Override
    public void invalidate() {
        if (this.suspendUpdate)
            return;

        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(yearImage != null) {
            yearImage.draw(canvas);
        }

        this.headWrapperView.invalidate();

        drawFragments(canvas);

        this.animationsManager.onInvalidate();
    }

    protected Drawable createImageFromView(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        BitmapDrawable swipeImage = new BitmapDrawable(view.getContext().getResources(), bitmap);
        int left = getPaddingLeft();
        int top = getPaddingTop() + title.getHeight();
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        swipeImage.setBounds(left, top, right, bottom);
        return swipeImage;
    }

    /**
     * Used to draw the fragments.
     *
     * @param canvas the current canvas.
     */
    private void drawFragments(Canvas canvas) {
        this.scrollManager.render(canvas);

        if (this.showGridLines)
            this.gridLinesLayer.drawLine(this.scrollManager.getLeft(), this.scrollManager.getTop(), this.scrollManager.getRight(), this.scrollManager.getTop(), canvas, this.scrollManager.getAlpha());

        this.scrollManager.postRender(canvas);
    }

    /**
     * Used to draw the day names.
     *
     * @param canvas the current canvas.
     */
    protected void drawDayNames(Canvas canvas) {
        this.dayNames.render(canvas);
        if (this.showGridLines) {
            this.gridLinesLayer.drawLine(this.dayNames.getLeft(), this.dayNames.getBottom(), this.dayNames.getRight(), this.dayNames.getBottom(), canvas, this.dayNames.getAlpha());
            this.gridLinesLayer.drawLine(this.dayNames.getLeft(), this.dayNames.getTop(), this.dayNames.getRight(), this.dayNames.getTop(), canvas, this.dayNames.getAlpha());
            this.gridLinesLayer.drawLine(this.dayNames.getLeft(), this.dayNames.getTop(), this.dayNames.getLeft(), this.dayNames.getBottom(), canvas, this.dayNames.getAlpha());
            this.gridLinesLayer.drawLine(this.dayNames.getRight(), this.dayNames.getTop(), this.dayNames.getRight(), this.dayNames.getBottom(), canvas, this.dayNames.getAlpha());

            /*for (int i = 0, len = this.dayNames.cellsCount() - 1; i < len; i++) {
                CalendarCell cell = this.dayNames.getCell(i);
                this.gridLinesLayer.drawLine(cell.getRight(), cell.getTop(), cell.getRight(), cell.getBottom(), canvas);
            }*/
        }
        this.dayNames.postRender(canvas);
    }

    /**
     * Used to draw the title.
     *
     * @param canvas the current canvas.
     */
    private void drawTitle(Canvas canvas) {
        this.title.render(canvas);
        if (this.showGridLines) {
            this.gridLinesLayer.drawLine(this.title.getLeft(), this.title.getTop(), this.title.getRight(), this.title.getTop(), canvas, this.title.getAlpha());
            this.gridLinesLayer.drawLine(this.title.getLeft(), this.title.getBottom(), this.title.getRight(), this.title.getBottom(), canvas, this.title.getAlpha());
            this.gridLinesLayer.drawLine(this.title.getLeft(), this.title.getTop(), this.title.getLeft(), this.title.getBottom(), canvas, this.title.getAlpha());
            this.gridLinesLayer.drawLine(this.title.getRight(), this.title.getTop(), this.title.getRight(), this.title.getBottom(), canvas, this.title.getAlpha());
        }
        this.title.postRender(canvas);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return this.gestureManager.handleSingleTapConfirmed(event);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onDoubleTap(MotionEvent event) {
        return this.gestureManager.onDoubleTap(event);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onDoubleTapEvent(MotionEvent event) {
        return gestureManager.onDoubleTapEvent(event);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        return this.gestureManager.onScale(scaleGestureDetector);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return this.gestureManager.onScaleBegin(scaleGestureDetector);
    }

    /**
     * Deprecated - use the corresponding method at the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @deprecated
     */
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        this.gestureManager.onScaleEnd(scaleGestureDetector);
    }

    public void changeDisplayMode(CalendarDisplayMode displayMode) {
        this.changeDisplayMode(displayMode, this.animationEnabled);
    }

    /**
     * Changes the current display mode with or without animation depending on the value
     * provided as parameter. Disregarding the value of the animate parameter, animations are not
     * used of the value is changed between <code>Week</code> and <code>Year</code>.
     *
     * @param displayMode the new display mode
     * @param animate     whether
     * @see #setDisplayMode(CalendarDisplayMode)
     */
    public void changeDisplayMode(final CalendarDisplayMode displayMode, final boolean animate) {
        if (this.suspendDisplayModeChange || this.displayMode == displayMode)
            return;

        if (!arrangePassed) {
            if (this.taskToBeExecutedAfterArrangeHasPassed.displayMode() != displayMode)
                this.taskToBeExecutedAfterArrangeHasPassed = new CalendarTask() {
                    @Override
                    public void execute() {
                        changeDisplayMode(displayMode(), animate);
                    }

                    @Override
                    public CalendarDisplayMode displayMode() {
                        return displayMode;
                    }
                };

            return;
        }

        gestureManager.onDisplayModeChange();

        CalendarDisplayMode oldMode = this.displayMode;
        if (displayMode == oldMode) {
            return;
        }
        switch (oldMode) {
            case Week:
                if (!inOriginalSizeForAllModes) {
                    expandCalendar(displayMode, animate);
                } else {
                    onDisplayModeChanged(displayMode);
                }
                break;
            case Month:
                if (displayMode == CalendarDisplayMode.Week) {
                    if (!this.inOriginalSizeForAllModes) {
                        shrinkCalendar(displayMode, animate);
                    } else {
                        onDisplayModeChanged(displayMode);
                    }
                } else if (displayMode == CalendarDisplayMode.Year && animate) {
                    this.animateMonthToYear();
                } else {
                    this.onDisplayModeChanged(displayMode);
                }
                break;
            case Year:
                if (displayMode == CalendarDisplayMode.Month) {
                    if (animate) {
                        this.animateYearToMonth();
                    } else {
                        onDisplayModeChanged(displayMode);
                    }
                } else {
                    if (!this.inOriginalSizeForAllModes)
                        shrinkCalendar(displayMode, animate);
                    else
                        onDisplayModeChanged(displayMode);
                }
                break;
        }
    }

    /**
     * Returns the current {@link com.telerik.widget.calendar.CalendarDisplayMode}.
     * The enumeration represents the period of dates that is visible at once on the calendar.
     * The default value is <code>Month</code>, which means that the calendar shows one month.
     *
     * @return the current display mode
     */
    public CalendarDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    /**
     * Sets a new {@link com.telerik.widget.calendar.CalendarDisplayMode}.
     * The enumeration represents the period of dates that is visible at once on the calendar.
     * The default value is <code>Month</code>, which means that the calendar shows one month.
     * You can use
     * {@link #setOnDisplayModeChangedListener(com.telerik.widget.calendar.RadCalendarView.OnDisplayModeChangedListener)}
     * and provide a listener which follows changes in the display mode.
     *
     * @param value the new display mode
     * @see #setOnDisplayModeChangedListener(com.telerik.widget.calendar.RadCalendarView.OnDisplayModeChangedListener)
     */
    public void setDisplayMode(final CalendarDisplayMode value) {
        changeDisplayMode(value);
    }

    private void onDisplayModeChanged(CalendarDisplayMode displayMode) {
        if (this.displayMode != displayMode) {
            this.gestureManager.setDisplayMode(displayMode);
            CalendarDisplayMode oldValue = this.displayMode;

            this.displayMode = displayMode;

            this.calendarAdapter.updateTitle(this.title, this.displayDate, this.displayMode);
            this.resetCalendar();
            this.updateFragments();
            this.scrollManager.setActiveDate(this.displayDate);
            this.scrollManager.updateActiveFragment();
            this.scrollManager.updateBorders();

            if (displayMode != CalendarDisplayMode.Year)
                this.selectionManager.syncSelectedCellsWithDates();

            if (this.onDisplayModeChangedListener != null) {
                this.onDisplayModeChangedListener.onDisplayModeChanged(oldValue, displayMode);
            }

            if (this.displayMode == CalendarDisplayMode.Year || !this.showDayNames) {
                this.dayNames.setVisibility(ElementVisibility.Gone);
            } else {
                this.dayNames.setVisibility(ElementVisibility.Visible);
            }

            invalidateArrange();
            invalidate();
        }
    }

    /**
     * Returns a value which represents whether the calendar will keep its original size in all display modes.
     * The default value is <code>false</code>. This means that when the display mode is changed to <code>Week</code>,
     * the calendar will consume less space (that is the space consumed by one week only). If the
     * value is changed to <code>true</code>, the calendar will maintain its original size.
     *
     * @return whether the calendar will maintain its original size in week mode
     */
    public boolean isInOriginalSizeForAllModes() {
        return inOriginalSizeForAllModes;
    }

    /**
     * Sets a value which determines whether the calendar will keep its original size in all display modes.
     * The default value is <code>false</code>. This means that when the display mode is changed to <code>Week</code>,
     * the calendar will consume less space (that is the space consumed by one week only). If the
     * value is changed to <code>true</code>, the calendar will maintain its original size.
     *
     * @param keepOriginalSizeInAllModes whether the calendar will maintain its original size in week mode
     */
    public void setInOriginalSizeForAllModes(boolean keepOriginalSizeInAllModes) {
        if (this.inOriginalSizeForAllModes == keepOriginalSizeInAllModes)
            return;

        this.inOriginalSizeForAllModes = keepOriginalSizeInAllModes;

        if (this.displayMode == CalendarDisplayMode.Week) {
            if (keepOriginalSizeInAllModes)
                expandCalendar(displayMode, false);
            else
                shrinkCalendar(displayMode, false);
        }
    }

    /**
     * Returns a value which represents whether the calendar draws all days when the display mode is
     * <code>Year</code>. The default value is <code>false</code>.
     * This means that when the display mode is changed to <code>Year</code>, the calendar will render all
     * dates from the year. If the value is changed to <code>true</code>,
     * the year view will show only the names of the months.
     *
     * @return whether the months in year view will be drawn entirely or only by their names
     */
    public boolean isYearModeCompact() {
        return this.isYearModeCompact;
    }

    /**
     * Sets a value which determines whether the calendar will render all days when the display mode is
     * <code>Year</code>. The default value is <code>false</code>.
     * This means that when the display mode is changed to <code>Year</code>, the calendar will render all
     * dates from the year. If the value is changed to <code>true</code>,
     * the year view will show only the names of the months.
     *
     * @param yearModeCompact whether the months in year view will be drawn entirely or only by their names
     */
    public void setYearModeCompact(boolean yearModeCompact) {
        if (this.isYearModeCompact != yearModeCompact) {
            this.isYearModeCompact = yearModeCompact;
            //this.yearFragment = null;
            rebuildCalendar();
        }
    }

    /**
     * Returns the current {@link java.util.Locale} used by the calendar.
     * The locale is used for determining the names of the days and the months visualized
     * by this instance. The default locale is the one provided by the current device.
     *
     * @return the current locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets a new {@link java.util.Locale} to be used by the calendar.
     * The locale is used for determining the names of the days and the months visualized
     * by this instance. The default locale is the one provided by the current device.
     *
     * @param locale the new locale
     */
    public void setLocale(Locale locale) {
        if (this.locale != locale) {
            this.locale = locale;
            this.calendarAdapter.setLocale(this.locale);
            CalendarTools.setLocale(locale);

            this.calendar = Calendar.getInstance(this.locale);
            this.workCalendar = Calendar.getInstance(this.locale);
            this.calendar.setTimeInMillis(this.displayDate);
            this.calendarAdapter.setCalendar(this.calendar);

            this.rebuildCalendar();
        }
    }

    /**
     * Returns the current {@link java.util.Calendar} used by the calendar.
     * By default the calendar that is used is {@link java.util.GregorianCalendar}.
     * If you want to use a different calendar set it through
     * {@link #setCalendar(java.util.Calendar)}.
     *
     * @return the current calendar
     */
    public Calendar getCalendar() {
        return (Calendar) this.calendar.clone();
    }

    /**
     * Sets a new {@link java.util.Calendar} to be used by this instance.
     * By default the calendar that is used is {@link java.util.GregorianCalendar}.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(Calendar calendar) {
        if (this.calendar != calendar) {
            this.calendar = (Calendar) calendar.clone();
            this.workCalendar = (Calendar) calendar.clone();
            this.calendarAdapter.setCalendar(this.calendar);
            this.setDisplayDate(this.calendar.getTimeInMillis());
            CalendarTools.setCalendar(calendar);

            this.rebuildCalendar();
        }
    }

    private void initializeControl() {
        //setClipChildren(false);

        this.datesHolder = new DatesHolderView(getContext());
        addView(datesHolder);

        this.dayNamesHeight = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 21);
        this.titleHeight = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 40);

        this.gridLinesLayer = new GridLinesLayer();
        this.gestureManager = new CalendarGestureManager(this);
        this.gestureManager.setDisplayMode(this.displayMode);

        this.setAnimationsManager(new CalendarAnimationsManager(this));

        this.locale = Locale.getDefault();
        this.calendar = Calendar.getInstance(this.locale);
        this.workCalendar = Calendar.getInstance(this.locale);
        CalendarTools.setLocale(this.locale);

        long displayDate = CalendarTools.getDateStart(this.calendar.getTimeInMillis());
        if (this.minDate != 0 && this.minDate > displayDate) {
            displayDate = this.minDate;
        }
        if (this.maxDate != 0 && this.maxDate < displayDate) {
            displayDate = this.maxDate;
        }
        this.displayDate = displayDate;

        this.setClickable(true);
        this.setFocusable(true);

        this.eventAdapter = new EventAdapter(this);
        this.calendarAdapter = new CalendarAdapter(this);

        CalendarDisplayMode currentDisplayMode = displayMode;

        displayMode = CalendarDisplayMode.Month;

        displayMode = CalendarDisplayMode.Year;
        this.yearFragmentHolder = new FragmentHolderView(getContext(), calendarAdapter.generateFragment());
        this.yearFragmentHolder.setActive(true);
        this.yearFragmentHolder.setVisibility(INVISIBLE);
        this.yearFragmentHolder.fragment.setDisplayMode(CalendarDisplayMode.Year);

        displayMode = currentDisplayMode;

        setScrollManager(new CalendarScrollManager(this));
        //this.eventsManager = new EventsManager(this);
        this.cellDecorationsLayer = new CellDecorationsLayer(this);

        this.title = this.calendarAdapter.getTitleCell(this.displayDate, this.displayMode);

        setSelectionManager(new CalendarSelectionManager(this));
        this.selectionManager.setSelectionMode(this.selectionMode);

        this.dateToCell = new Hashtable<Long, List<CalendarDayCell>>();

        this.headWrapperView = new HeadWrapperView(getContext());
        addView(this.headWrapperView);

        this.generateCalendarDayNameElements();

        this.gestureManager.setScrollMode(scrollMode);
        this.scrollManager.setScrollMode(scrollMode);
        this.scrollManager.setHorizontalScroll(this.horizontalScroll);

        controlInitialized = true;
        this.calendarAdapter.setStyle(CalendarStyles.materialLight(getContext()));

        updateFragmentYearMode(yearFragmentHolder.fragment, displayDate, false);
        hideAnimationHolders();

        if (isInEditMode()) {
            setScrollMode(ScrollMode.Overlap);
        }
    }

    /**
     * Used to update the calendar fragments according to the current display date.
     */
    public void updateFragments() {
        updateFragments(false);
    }

    /**
     * Used to update the calendar fragments according to the current display date.
     *
     * @param force if forced the fragments will be updated disregarding their current state.
     */
    public void updateFragments(boolean force) {
        for (List<CalendarDayCell> list : this.dateToCell.values())
            list.clear();

        if (this.displayMode == CalendarDisplayMode.Month) {
            updateFragmentMonthMode(this.scrollManager.currentFragment(), this.displayDate, force);
            updateFragmentMonthMode(this.scrollManager.nextFragment(), CalendarTools.calculateNewValue(true, this.displayDate, this.displayMode), force);
            updateFragmentMonthMode(this.scrollManager.previousFragment(), CalendarTools.calculateNewValue(false, this.displayDate, this.displayMode), force);
            this.selectionManager.syncSelectedCellsWithDates();
        } else if (this.displayMode == CalendarDisplayMode.Year) {
            updateFragmentYearMode(this.scrollManager.currentFragment(), this.displayDate, force);
            updateFragmentYearMode(this.scrollManager.nextFragment(), CalendarTools.calculateNewValue(true, this.displayDate, this.displayMode), force);
            updateFragmentYearMode(this.scrollManager.previousFragment(), CalendarTools.calculateNewValue(false, this.displayDate, this.displayMode), force);
        } else if (this.displayMode == CalendarDisplayMode.Week) {
            updateFragmentWeekMode(this.scrollManager.currentFragment(), this.displayDate, force);
            updateFragmentWeekMode(this.scrollManager.nextFragment(), CalendarTools.calculateNewValue(true, this.displayDate, this.displayMode), force);
            updateFragmentWeekMode(this.scrollManager.previousFragment(), CalendarTools.calculateNewValue(false, this.displayDate, this.displayMode), force);

            this.selectionManager.syncSelectedCellsWithDates();
        }

        this.scrollManager.updateActiveFragment();
        this.scrollManager.updateEventsForFragments();
        this.scrollManager.onDateChanged();
    }

    private void initFromXML(TypedArray array) {

        if (this.getResources() == null) {
            throw new IllegalStateException("The resources are not accessible.");
        }

        int backgroundColorFromStyle = array.getColor(
                R.styleable.RadCalendarView_calendarBackground,
                calendarAdapter.style.popupEventsWindowBackgroundColor);

        this.setBackgroundColor(backgroundColorFromStyle);
        backgroundColor = backgroundColorFromStyle;

        this.showTitle = array.getBoolean(R.styleable.RadCalendarView_showTitle, true);

        this.showDayNames = array.getBoolean(R.styleable.RadCalendarView_showDayNames, true);

        boolean showGridLinesFromStyle = array.getBoolean(R.styleable.RadCalendarView_showGridLines, true);
        this.setShowGridLines(showGridLinesFromStyle);

        boolean showCellDecorationsFromStyle = array.getBoolean(R.styleable.RadCalendarView_showCellDecorations, true);
        this.setShowCellDecorations(showCellDecorationsFromStyle);

        this.isYearModeCompact = array.getBoolean(R.styleable.RadCalendarView_isYearModeCompact, this.isYearModeCompact);

        int selectionModeFromStyle = array.getInteger(R.styleable.RadCalendarView_selectionMode, CalendarSelectionMode.Multiple.ordinal());
        this.selectionMode = CalendarSelectionMode.values()[selectionModeFromStyle];

        final int displayModeFromStyle = array.getInteger(R.styleable.RadCalendarView_displayMode, CalendarDisplayMode.Month.ordinal());
        this.taskToBeExecutedAfterArrangeHasPassed = (new CalendarTask() {
            @Override
            public void execute() {
                changeDisplayMode(displayMode(), false);
            }

            @Override
            public CalendarDisplayMode displayMode() {
                return CalendarDisplayMode.values()[displayModeFromStyle];
            }
        });

        int weekNumberDisplayModeFromStyle = array.getInteger(R.styleable.RadCalendarView_weekNumberDisplayMode, this.weekNumbersDisplayMode.ordinal());
        setWeekNumbersDisplayMode(WeekNumbersDisplayMode.values()[weekNumberDisplayModeFromStyle]);
    }

    private void resetCalendar() {
        this.generateCalendarDayNameElements();
        this.dateToCell.clear();
        this.selectionManager.selectedCells().clear();
        this.calendarAdapter.reset();
        this.scrollManager.reset(true);
        this.cellDecorationsLayer.clearDecorations();
        setBackgroundColor(backgroundColor);
    }

    private void generateCalendarDayNameElements() {
        if (this.calendarAdapter == null)
            return;

        this.dayNames = this.calendarAdapter.generateCalendarRow();

        this.dayNames.addCell(this.calendarAdapter.getDayNameCell());
        if (this.weekNumbersDisplayMode != WeekNumbersDisplayMode.Block)
            dayNames.getCell(0).setVisibility(ElementVisibility.Gone);

        for (int i = 0; i < CalendarTools.DAYS_IN_A_WEEK; i++) {
            CalendarCell dayNameCell = calendarAdapter.getDayNameCell(i);
            this.dayNames.addCell(dayNameCell);
        }

        if (!this.showDayNames || this.displayMode == CalendarDisplayMode.Year) {
            this.dayNames.setVisibility(ElementVisibility.Gone);
        }

        this.headWrapperView.dirty = true;
    }

    /**
     * Updates a fragment that is in month mode.
     *
     * @param fragment      the fragment to be updated.
     * @param dateToDisplay the date to be displayed by the fragment.
     * @param force         <code>true</code> if the changes should be forced, <code>false</code> if a simple
     *                      update will be enough.
     */
    protected void updateFragmentMonthMode(CalendarFragment fragment, long dateToDisplay, boolean force) {
        if (!force && fragment.getDisplayMode() == CalendarDisplayMode.Month && fragment.getDisplayDate() == dateToDisplay) {
            if (!cellRegistrationSuspended) {
                updateDateToCellsForFragment(fragment);
            }

            updateFragmentIsFromCurrentMonth(fragment);
            updateFragmentCustomizations(fragment);

            return;
        }

        this.workCalendar.setTimeInMillis(CalendarTools.getFirstDisplayDate(dateToDisplay));

        long startDate = CalendarTools.getFirstDateInMonth(dateToDisplay);
        long endDate = CalendarTools.getLastDateInMonth(dateToDisplay);

        for (int week = 0; week < CalendarTools.WEEKS_IN_A_MONTH; week++) {
            updateWeek(fragment.rows().get(week), startDate, endDate);
        }

        fragment.setDisplayDate(dateToDisplay);
        fragment.setDisplayMode(CalendarDisplayMode.Month);

        updateFragmentIsFromCurrentMonth(fragment);
        updateFragmentCustomizations(fragment);
        fragment.dirty = true;
    }

    private void updateWeek(CalendarRow currentWeek, long startDate, long endDate) {
        currentWeek.firstVisibleCellIndex = -1;
        currentWeek.lastVisibleCellIndex = -1;
        CalendarDayCell currentCell = (CalendarDayCell) currentWeek.getCell(CalendarRow.WEEK_NUMBER_CELL_INDEX);
        if (this.weekNumbersDisplayMode == WeekNumbersDisplayMode.Block) {
            calendarAdapter.updateWeekNumberCell(currentCell, this.workCalendar.get(Calendar.WEEK_OF_YEAR));
            currentWeek.firstVisibleCellIndex = 0;
        }

        currentCell.setVisibility(this.weekNumbersDisplayMode == WeekNumbersDisplayMode.Block ? ElementVisibility.Visible : ElementVisibility.Gone);

        List<CalendarDayCell> dateToCells;
        for (int day = 0; day < CalendarTools.DAYS_IN_A_WEEK; day++, this.workCalendar.add(Calendar.DAY_OF_YEAR, 1)) {
            currentCell = (CalendarDayCell) currentWeek.getCell(day + 1);

            this.calendarAdapter.updateDateCell(currentCell, this.workCalendar.getTimeInMillis(),
                    null,
                    day == 0 && this.weekNumbersDisplayMode == WeekNumbersDisplayMode.Inline);

            if ((!drawingAllCells || (scrollMode != ScrollMode.Overlap && scrollMode != ScrollMode.Stack && !scrollManager.scrollShouldBeHorizontal())) && currentCell.getDate() != 0 && (currentCell.getDate() < startDate || currentCell.getDate() > endDate)) {
                currentCell.setVisibility(ElementVisibility.Invisible);
                if(currentWeek.lastVisibleCellIndex == -1) {
                    currentWeek.firstVisibleCellIndex = -1;
                }
                continue;
            }

            currentCell.setVisibility(ElementVisibility.Visible);

            if (currentWeek.firstVisibleCellIndex < 0) {
                currentWeek.firstVisibleCellIndex = day + 1;
            }

            currentWeek.lastVisibleCellIndex = day + 1;

            if (!this.cellRegistrationSuspended) {
                dateToCells = this.dateToCell.get(this.workCalendar.getTimeInMillis());
                if (dateToCells == null) {
                    dateToCells = new ArrayList<CalendarDayCell>();
                    this.dateToCell.put(this.workCalendar.getTimeInMillis(), dateToCells);
                }

                dateToCells.add(currentCell);
            }

            validateDisabledDate(currentCell, this.workCalendar.getTimeInMillis());
            currentCell.setDate(this.workCalendar.getTimeInMillis());
        }
    }

    private void updateFragmentIsFromCurrentMonth(CalendarFragment fragment) {
        int currentMonth = calendar.get(Calendar.MONTH);
        ArrayList<CalendarRow> rows = fragment.rows();
        for (int i1 = 0; i1 < rows.size(); i1++) {
            CalendarRow row = rows.get(i1);

            int startIndex = 1;
            int endIndex = row.cellsCount() - 1;

            CalendarCell cell;
            boolean startReached = false;
            boolean endReached = false;

            while ((!startReached || !endReached) && startIndex <= endIndex && endIndex >= startIndex) {
                if (!startReached) {
                    cell = row.cells.get(startIndex++);
                    workCalendar.setTimeInMillis(cell.getDate());
                    if (workCalendar.get(Calendar.MONTH) == currentMonth) {
                        startReached = true;
                        ((CalendarDayCell) cell).setIsFromCurrentMonth(true);
                    } else {
                        ((CalendarDayCell) cell).setIsFromCurrentMonth(false);
                    }
                }
                if (!endReached) {
                    cell = row.cells.get(endIndex--);
                    workCalendar.setTimeInMillis(cell.getDate());
                    if (workCalendar.get(Calendar.MONTH) == currentMonth) {
                        endReached = true;
                        ((CalendarDayCell) cell).setIsFromCurrentMonth(true);
                    } else {
                        ((CalendarDayCell) cell).setIsFromCurrentMonth(false);
                    }
                }
            }

            while (startIndex <= endIndex)
                ((CalendarDayCell) row.cells.get(startIndex++)).setIsFromCurrentMonth(true);
        }
    }

    public void applyDayCellStyles(CalendarDayCell cell) {
        if(dayNameToColor != null && cell.cellType == CalendarCellType.DayName) {
            Integer dayNameColor = this.dayNameToColor.apply(cell.getDate());
            if(dayNameColor != null) {
                cell.setTextColor(dayNameColor);
            }
        }
        if(dateToColor != null && cell.cellType == CalendarCellType.Date) {
            Integer dateColor = this.dateToColor.apply(cell.getDate());
            if(dateColor != null) {
                cell.setTextColor(dateColor);
            }
        }
        if(customizationRule != null) {
            this.customizationRule.apply(cell);
        }
        for (CalendarDayCellStyle style : dayCellStyles) {
            style.apply(cell);
        }
    }

    public void applyMonthCellStyles(CalendarMonthCell cell) {
        if(dateToColor != null) {
            Integer color = this.dateToColor.apply(cell.getDate());
            if (color != null) {
                cell.setTextColor(color);
            }
        }
        if(customizationRule != null) {
            this.customizationRule.apply(cell);
        }
        for (CalendarMonthCellStyle style : monthCellStyles) {
            style.applyMonthStyle(cell);
        }
    }

    private void updateFragmentCustomizations(CalendarFragment fragment) {
        for (CalendarRow row : fragment.rows())
            for (CalendarCell cell : row.cells) {
                resetCellStyle(cell);
            }
    }

    private void resetCellStyle(CalendarCell cell) {
        if (cell instanceof CalendarMonthCell)
            calendarAdapter.updateMonthCellStyle((CalendarMonthCell) cell);
        else
            calendarAdapter.updateDateCellStyle((CalendarDayCell) cell);
    }

    /**
     * Updates a fragment that is in week mode.
     *
     * @param fragment      the fragment to be updated.
     * @param dateToDisplay the date to be displayed by the fragment.
     * @param force         <code>true</code> if the changes should be forced, <code>false</code> if a simple
     *                      update will be enough.
     */
    protected void updateFragmentWeekMode(CalendarFragment fragment, long dateToDisplay, boolean force) {
        if (!force && fragment.getDisplayMode() == this.displayMode && fragment.getDisplayDate() == dateToDisplay) {
            updateDateToCellsForFragment(fragment);
            updateFragmentIsFromCurrentMonth(fragment);
            updateFragmentCustomizations(fragment);

            return;
        }

        this.workCalendar.setTimeInMillis(CalendarTools.getDateStart(CalendarTools.getFirstDateOfWeekWith(dateToDisplay)));
        updateWeek(fragment.rows().get(0), drawingAllCells ? -1 : CalendarTools.getFirstDateInMonth(dateToDisplay), drawingAllCells ? -1 : CalendarTools.getLastDateInMonth(dateToDisplay));

        fragment.setDisplayDate(dateToDisplay);
        fragment.setDisplayMode(this.displayMode);
        updateFragmentIsFromCurrentMonth(fragment);
        updateFragmentCustomizations(fragment);
        fragment.dirty = true;
    }

    private void validateDisabledDate(CalendarDayCell currentCell, long time) {
        if (minDate != 0 && maxDate != 0)
            currentCell.setSelectable(time >= minDate && time <= maxDate);
        else if (minDate != 0)
            currentCell.setSelectable(time >= minDate);
        else if (maxDate != 0)
            currentCell.setSelectable(time <= maxDate);
    }

    /**
     * Updates a fragment that is in year mode.
     *
     * @param fragment      the fragment to be updated.
     * @param dateToDisplay the date to be displayed by the fragment.
     * @param force         <code>true</code> if the changes should be forced, <code>false</code> if a simple
     *                      update will be enough.
     */
    protected void updateFragmentYearMode(CalendarFragment fragment, long dateToDisplay, boolean force) {
        if (!force && fragment.getDisplayMode() == CalendarDisplayMode.Year && fragment.getDisplayDate() == dateToDisplay) {
            updateFragmentCustomizations(fragment);

            return;
        }

        this.workCalendar.setTimeInMillis(dateToDisplay);
        this.workCalendar.set(Calendar.MONTH, Calendar.JANUARY);
        this.workCalendar.set(Calendar.DAY_OF_MONTH, 1);
        this.workCalendar.set(Calendar.HOUR, 0);
        this.workCalendar.set(Calendar.MINUTE, 0);
        this.workCalendar.set(Calendar.SECOND, 0);
        this.workCalendar.set(Calendar.MILLISECOND, 0);
        this.workCalendar.set(Calendar.AM_PM, 0);

        for (int i = 0; i < fragment.rows().size(); i++) {
            CalendarRow row = fragment.rows().get(i);
            row.firstVisibleCellIndex = 0;
            row.lastVisibleCellIndex = row.cellsCount() - 1;
            for (int j = 0; j < row.cellsCount(); j++) {
                this.calendarAdapter.updateCalendarMonthCell((CalendarMonthCell) row.getCell(j), this.workCalendar.getTimeInMillis());
                this.workCalendar.add(Calendar.MONTH, 1);
            }
        }

        fragment.setDisplayDate(dateToDisplay);
        fragment.setDisplayMode(CalendarDisplayMode.Year);
        updateFragmentCustomizations(fragment);
        fragment.dirty = true;
    }

    private void shrinkCalendar(CalendarDisplayMode displayMode, boolean animate) {
        this.suspendDisplayModeChange = true;
        if (!this.calendarShrinked) {
            this.originalHeight = getMeasuredHeight();
            this.calendarShrinked = true;
        }

        if (animate)
            shrinkCalendarAnimated(displayMode);
        else {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = calculateCurrentCalendarHeight();
            this.setLayoutParams(params);
            onDisplayModeChanged(displayMode);
            this.suspendDisplayModeChange = false;
        }
    }

    private void expandCalendar(CalendarDisplayMode displayMode, boolean animate) {
        this.suspendDisplayModeChange = true;

        if (animate) {
            expandCalendarAnimated(displayMode);
        } else {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = this.originalHeight;

            this.setLayoutParams(params);
            onDisplayModeChanged(displayMode);
            this.suspendDisplayModeChange = false;
        }

        this.calendarShrinked = false;
    }

    private void expandCalendarAnimated(final CalendarDisplayMode displayMode) {
        final int start = calculateCurrentCalendarHeight();
        final int change = Math.abs(originalHeight - start);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                getLayoutParams().height = (int) (start + (change * interpolatedTime));
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                suspendDisplayModeChange = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(DEFAULT_ANIMATION_DURATION);
        onDisplayModeChanged(displayMode);
        startAnimation(a);
    }

    private void shrinkCalendarAnimated(final CalendarDisplayMode displayMode) {
        final int initialHeight = getMeasuredHeight();
        final int finalHeight = calculateCurrentCalendarHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                getLayoutParams().height = initialHeight - (int) ((initialHeight - finalHeight) * interpolatedTime);
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) {
                suspendTouch = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onDisplayModeChanged(displayMode);
                suspendDisplayModeChange = false;
                suspendTouch = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(DEFAULT_ANIMATION_DURATION);
        startAnimation(a);
    }

    private int calculateCurrentCalendarHeight() {
        if (this.displayMode != CalendarDisplayMode.Month) {
            CalendarDisplayMode currentMode = this.displayMode;
            this.suspendUpdate = true;
            onDisplayModeChanged(CalendarDisplayMode.Month);
            int height = this.title.getHeight() + this.dayNames.getHeight() + initialRowHeight;
            onDisplayModeChanged(currentMode);
            this.suspendUpdate = false;
            return height;
        }

        return this.title.getHeight() + this.dayNames.getHeight() + initialRowHeight;
    }

    private void animateMonthToYear() {
        if (this.getWidth() == 0 || this.getHeight() == 0) {
            this.onDisplayModeChanged(CalendarDisplayMode.Year);
            return;
        }

        /*prepareMonthFragment();

        this.suspendUpdate = true;
        this.onDisplayModeChanged(CalendarDisplayMode.Year);
        this.suspendUpdate = false;

        prepareYearFragment();

        this.animationsManager.beginAnimation(this.monthFragment, this.yearFragment, getMonthCell(getCurrentMonth()).calcBorderRect());*/
        CalendarCell monthCell = getMonthCell(getCurrentMonth());

        updateFragmentYearMode(yearFragmentHolder.fragment, displayDate, false);

        this.calendarAdapter.updateTitle(this.title, this.displayDate, CalendarDisplayMode.Year);
        this.dayNames.setVisibility(ElementVisibility.Gone);

        this.headWrapperView.dirty = true;
        this.headWrapperView.invalidate();

        showAnimationHolders();

        animateMonthView(1, 0, 1, 0, (monthCell.virtualRight() - (monthCell.getWidth() / 2)) / (float) getWidth(), (monthCell.virtualBottom() - (monthCell.getHeight() / 2)) / (float) getHeight(), new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                beginUpdate();
                suspendTouch = true;
                animationsManager.reset();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onDisplayModeChanged(CalendarDisplayMode.Year);
                suspendTouch = false;
                endUpdate();
                hideAnimationHolders();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animateYearToMonth() {
        if (this.getWidth() == 0 || this.getHeight() == 0) {
            this.onDisplayModeChanged(CalendarDisplayMode.Month);
            return;
        }

        /*updateFragmentYearMode(yearFragmentHolder.fragment, displayDate, false);

        CalendarCell cell = getMonthCell(getCurrentMonth());

        this.suspendUpdate = true;
        this.onDisplayModeChanged(CalendarDisplayMode.Month);
        this.suspendUpdate = false;

        updateMonthFragment();

        //this.animationsManager.beginAnimation(this.monthFragment, this.yearFragment, cell.calcBorderRect());*/

        CalendarCell monthCell = getMonthCell(getCurrentMonth());

        updateFragmentYearMode(yearFragmentHolder.fragment, displayDate, false);

        showAnimationHolders();

        animateMonthView(0, 1, 0, 1, (monthCell.virtualRight() - (monthCell.getWidth() / 2)) / (float) getWidth(), (monthCell.virtualBottom() - (monthCell.getHeight() / 2)) / (float) getHeight(), new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                beginUpdate();
                suspendTouch = true;
                onDisplayModeChanged(CalendarDisplayMode.Month);
                animationsManager.reset();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                suspendTouch = false;
                endUpdate();
                hideAnimationHolders();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showAnimationHolders() {
        yearImage = createImageFromView(yearFragmentHolder);
        invalidate();
    }

    private void hideAnimationHolders() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                yearImage = null;
            }
        });
    }

    private int getCurrentMonth() {
        this.workCalendar.setTimeInMillis(this.displayDate);
        return this.workCalendar.get(Calendar.MONTH);
    }

    protected void animateMonthView(float startScale, float endScale, float startAlpha, float endAlpha, float pivotX, float pivotY, Animation.AnimationListener listener) {
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setDuration(200);

        Animation scaleAnimation = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, pivotX,
                Animation.RELATIVE_TO_SELF, pivotY);
        //scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        //scaleAnimation.setAnimationListener(listener);

        Animation fadeAnimation = new AlphaAnimation(startAlpha, endAlpha);
        fadeAnimation.setDuration(200);
        //fadeAnimation.setFillAfter(true);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(fadeAnimation);
        //animationSet.setFillAfter(true);
        animationSet.setAnimationListener(listener);

        this.datesHolder.startAnimation(animationSet);
    }

    private CalendarCell getMonthCell(int month) {
        int cellsCount = this.yearFragmentHolder.fragment.rows().get(0).cellsCount();
        int row = month / cellsCount;

        return this.yearFragmentHolder.fragment.rows().get(row).getCell(month - (row * cellsCount));
    }

    private void handleShowDayNamesChange() {
        if (this.dayNames == null) {
            return;
        }

        if (this.showDayNames) {
            this.dayNames.setVisibility(ElementVisibility.Visible);
        } else {
            this.dayNames.setVisibility(ElementVisibility.Gone);
        }

        //resetTransitionFragments();

        this.invalidateArrange();
        if (!this.inOriginalSizeForAllModes && this.displayMode == CalendarDisplayMode.Week)
            shrinkCalendar(this.displayMode, false);

        this.invalidate();
    }

    private void handleShowTitleChange() {
        if (this.title == null) {
            return;
        }

        if (this.showTitle) {
            this.title.setVisibility(ElementVisibility.Visible);
        } else {
            this.title.setVisibility(ElementVisibility.Gone);
        }

        //resetTransitionFragments();

        invalidateArrange();
        if (!this.inOriginalSizeForAllModes && this.displayMode == CalendarDisplayMode.Week)
            shrinkCalendar(this.displayMode, false);

        this.invalidate();
    }

    /*private void resetTransitionFragments() {
        this.yearFragment = null;
        this.monthFragment = null;
    }*/

    private void onDisplayDateChanged() {
        if (headWrapperView != null)
            headWrapperView.dirty = true;
        this.updateCalendar(false);
        this.scrollManager.setActiveDate(this.displayDate);
        this.scrollManager.updateEventsForFragments();
    }

    private void updateDateToCellsForFragment(CalendarFragment fragment) {
        for (int row = 0, rowsCount = fragment.rows().size(); row < rowsCount; row++) {
            CalendarRow currentRow = fragment.rows().get(row);
            for (int cell = 1, cellsCount = currentRow.cellsCount(); cell < cellsCount; cell++) {
                CalendarDayCell currentCell = (CalendarDayCell) currentRow.getCell(cell);
                List<CalendarDayCell> dateToCells = this.dateToCell.get(currentCell.getDate());
                if (dateToCells == null) {
                    dateToCells = new ArrayList<CalendarDayCell>();
                    this.dateToCell.put(currentCell.getDate(), dateToCells);
                }

                dateToCells.add(currentCell);
            }
        }
    }

    private interface CalendarTask {
        CalendarDisplayMode displayMode();

        void execute();
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * the display date in {@link com.telerik.widget.calendar.RadCalendarView} is changed.
     */
    public static interface OnDisplayDateChangedListener {

        /**
         * Represents a method which will be executed when
         * the display date in {@link com.telerik.widget.calendar.RadCalendarView} is changed.
         *
         * @param oldValue the old display date
         * @param newValue the new display date
         */
        public void onDisplayDateChanged(long oldValue, long newValue);
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * the display mode in {@link com.telerik.widget.calendar.RadCalendarView} is changed.
     */
    public static interface OnDisplayModeChangedListener {

        /**
         * Represents a method which will be executed when
         * the display mode in {@link com.telerik.widget.calendar.RadCalendarView} is changed.
         *
         * @param oldValue the old display mode
         * @param newValue the new display mode
         */
        public void onDisplayModeChanged(CalendarDisplayMode oldValue, CalendarDisplayMode newValue);
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * the selected dates in {@link com.telerik.widget.calendar.RadCalendarView} are changed.
     */
    public static interface OnSelectedDatesChangedListener {

        /**
         * Represents a method which will be executed when the selected dates in
         * {@link com.telerik.widget.calendar.RadCalendarView} are changed.
         *
         * @param context a selection context which contains information about the old selection,
         *                the new selection and the items that are currently
         *                added or removed from the selection
         */
        public void onSelectedDatesChanged(SelectionContext context);
    }

    /**
     * Represents an interface for a listener that will execute its method when
     * a cell from the {@link com.telerik.widget.calendar.RadCalendarView} is clicked.
     */
    public static interface OnCellClickListener {

        /**
         * Represents a method which will be executed when a cell in
         * {@link com.telerik.widget.calendar.RadCalendarView} is clicked.
         *
         * @param clickedCell the cell that is clicked
         */
        public void onCellClick(CalendarCell clickedCell);
    }

    /**
     * Represents a class which represents a context for selection. A parameter of this type is
     * used when the current selection is changed. The context contains information for the
     * old and the new selection, as well as the items that are currently being added or removed.
     *
     * @see #setOnSelectedDatesChangedListener(OnSelectedDatesChangedListener)
     */
    public static class SelectionContext {
        List<Long> oldSelection;
        List<Long> newSelection;
        List<Long> datesAdded;
        List<Long> datesRemoved;

        /**
         * Creates a new SelectionContext instance.
         */
        public SelectionContext() {
            oldSelection = new ArrayList<Long>();
            newSelection = new ArrayList<Long>();
            datesAdded = new ArrayList<Long>();
            datesRemoved = new ArrayList<Long>();
        }

        /**
         * Returns the old selection.
         */
        public List<Long> oldSelection() {
            return oldSelection;
        }

        /**
         * Returns the new selection.
         */
        public List<Long> newSelection() {
            return newSelection;
        }

        /**
         * Returns the dates that are currently added to the selection.
         */
        public List<Long> datesAdded() {
            return datesAdded;
        }

        /**
         * Returns the dates that are currently removed from the selection.
         */
        public List<Long> datesRemoved() {
            return datesRemoved;
        }
    }
}
