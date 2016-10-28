package com.telerik.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.SparseArray;

import com.telerik.android.common.Function;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The class of the calendar month cell. The calendar month cell is a {@link android.view.View}
 * which represent a visual within {@link com.telerik.widget.calendar.RadCalendarView}.
 * This visual represents a month while the calendar view displays a full year.
 */
public class CalendarMonthCell extends CalendarCell {

    protected static final Paint todayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    static int todayBackgroundColor;
    static int todayTextColor;
    static Typeface todayTypeFace;
    private static long todayDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private Calendar dateCalendar;
    private boolean yearModeIsCompact = false;

    /**
     * The text bounds of the month name.
     */
    protected Rect monthNameBounds;
    protected Rect monthNameBoundsCompact;
    /**
     * The text bounds of the day names.
     */
    protected final Rect dayBounds;
    /**
     * The text bounds of the dates with one symbol.
     */
    protected final Rect dateBoundsOneSymbol;
    /**
     * The text bounds of the dates with two symbols.
     */
    protected final Rect dateBoundsTwoSymbols;
    /**
     * The available space for drawing the day names and the dates bellow the month name. It is affected by the offset percentages for the four sides.
     */
    protected final Rect monthAvailableSpace;
    /**
     * THe paint to be used for the month name.
     */
    protected final Paint monthNamePaint;
    /**
     * The paint to be used for the date.
     */
    protected final Paint datePaint;
    /**
     * The paint to be used for the day names.
     */
    protected final Paint dayNamesPaint;
    /**
     * The light typeface to be used.
     */
    protected final Typeface typefaceLight;
    /**
     * Cached names for the days of the week.
     */
    protected final SparseArray<String> daysOfWeek;
    /**
     * Cached names for the dates.
     */
    protected final SparseArray<String> dateValues;
    private final Calendar workCalendar;
    private final MonthCellTitleElement titleElement;
    private final List<MonthCellDayNameElement> elements;
    private final List<MonthCellDateElement> dateElements;
    /**
     * The offset percentage on the left side of the month cell.
     */
    protected double offsetLeft;
    /**
     * The offset percentage on the top side of the month cell.
     */
    protected double offsetTop = .05;
    /**
     * The offset percentage on the right side of the month cell.
     */
    protected double offsetRight;
    /**
     * The offset percentage on the bottom side of the month cell.
     */
    protected double offsetBottom;
    /**
     * The current horizontal slot size for a single day.
     */
    protected double horizontalSlot;
    /**
     * The current vertical slot size for a single day.
     */
    protected double verticalSlot;
    /**
     * Current month name text size.
     */
    protected float monthNameTextSize;
    /**
     * Current month name text size compact.
     */
    protected float monthNameTextSizeCompact;
    /**
     * Current month name position.
     */
    protected int monthNamePosition;
    /**
     * Current text color for month name in enabled state.
     */
    protected int monthNameTextColorEnabled;
    /**
     * Current month name text color in disabled state.
     */
    protected int monthNameTextColorDisabled;
    /**
     * Current type face for the month name.
     */
    protected Typeface monthNameTypeFace;
    /**
     * Current day names text size.
     */
    protected float dayNameTextSize;
    /**
     * Current dat names text color in enabled state.
     */
    protected int dayNameTextColorEnabled;
    /**
     * Current day names text color in disabled state.
     */
    protected int dayNameTextColorDisabled;
    /**
     * Current day names type face.
     */
    protected Typeface dayNameTypeFace;
    /**
     * Current date text size.
     */
    protected float dateTextSize;
    /**
     * Current date text color in enabled state.
     */
    protected int dateTextColorEnabled;
    /**
     * Current date text color in disabled state.
     */
    protected int dateTextColorDisabled;
    /**
     * Current date type face.
     */
    protected Typeface dateTypeFace;
    /**
     * Name of the current month displayed by the cell instance.
     */
    protected String monthName;
    /**
     * The calculated position along the x axis for drawing the month name.
     */
    protected int monthNamePositionX;
    /**
     * The calculated position along the y axis for drawing the month name.
     */
    protected int monthNamePositionY;
    /**
     * Number of days in the current month. It is being updated when {@link #setDate(long)} is being invoked.
     */
    protected int numberOfDays;
    private int currentDayNameColor;
    private int currentDateTextColor;

    /**
     * Creates an instance of the {@link com.telerik.widget.calendar.CalendarMonthCell} class.
     *
     * @param owner the calendar instance owning the cell.
     */
    public CalendarMonthCell(RadCalendarView owner) {
        super(owner);

        workCalendar = owner.getCalendar();
        dateCalendar = owner.getCalendar();

        this.monthAvailableSpace = new Rect();
        this.monthNameBounds = new Rect();
        this.dayBounds = new Rect();

        this.dateBoundsOneSymbol = new Rect();
        this.dateBoundsTwoSymbols = new Rect();

        this.daysOfWeek = new SparseArray<String>();
        this.dateValues = new SparseArray<String>();

        this.monthNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.dayNamesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        this.typefaceLight = Typeface.create("sans-serif-light", Typeface.NORMAL);

        this.titleElement = new MonthCellTitleElement();
        this.elements = new ArrayList<MonthCellDayNameElement>();
        this.dateElements = new ArrayList<MonthCellDateElement>();

        // Adding day names.
        for (int day = 0, daysCount = CalendarTools.DAYS_IN_A_WEEK; day < daysCount; day++)
            elements.add(new MonthCellDayNameElement());

        // Adding dates.
        for (int week = 0, weeksCount = CalendarTools.WEEKS_IN_A_MONTH; week < weeksCount; week++)
            for (int day = 0, daysCount = CalendarTools.DAYS_IN_A_WEEK; day < daysCount; day++)
                dateElements.add(new MonthCellDateElement());

        //updateValuesCache();
    }

    @Override
    protected void onAlphaChanged() {
        super.onAlphaChanged();
        this.datePaint.setAlpha(this.alpha);
        this.monthNamePaint.setAlpha(this.alpha);
        this.dayNamesPaint.setAlpha(this.alpha);
    }

    /**
     * Gets the current month name font size.
     *
     * @return the current month name font size.
     */
    public float getMonthNameTextSize() {
        return this.monthNameTextSize;
    }

    /**
     * Sets the current month name font size.
     *
     * @param size the new month name font size.
     */
    public void setMonthNameTextSize(float size) {
        if (this.monthNameTextSize == size)
            return;

        this.monthNameTextSize = size;
        this.monthNamePaint.setTextSize(size);

        updateMonthNameBounds();
        updatePositions();
    }

    /**
     * Gets the current month name font size in compact mode.
     *
     * @return the current compact month name font size.
     */
    public float getMonthNameTextSizeCompact() {
        return this.monthNameTextSizeCompact;
    }

    /**
     * Sets the current month name font size in compact mode.
     *
     * @param size the new compact month name font size.
     */
    public void setMonthNameTextSizeCompact(float size) {
        if (this.monthNameTextSizeCompact == size)
            return;

        setTextSize(size);

        this.monthNameTextSizeCompact = size;
        this.textPaint.setTextSize(size);
        updateMonthNameBounds();
        updateMonthNamePosition();
    }

    /**
     * Gets the current month name text color for the enabled state.
     *
     * @return the current enabled month name text color.
     */
    public int getMonthNameTextColorEnabled() {
        return monthNameTextColorEnabled;
    }

    /**
     * Sets the current month name text color for the enabled state.
     *
     * @param monthNameTextColorEnabled the new enabled month name text color.
     */
    public void setMonthNameTextColorEnabled(int monthNameTextColorEnabled) {
        if (this.monthNameTextColorEnabled != monthNameTextColorEnabled) {
            this.monthNameTextColorEnabled = monthNameTextColorEnabled;
            setTextColor(monthNameTextColorEnabled);

            updateMonthNameColor();
        }
    }

    /**
     * Gets the current month name text color for the disabled state.
     *
     * @return the current month name text color for the disabled state.
     */
    public int getMonthNameTextColorDisabled() {
        return monthNameTextColorDisabled;
    }

    /**
     * Sets the current month name text color for the disabled state.
     *
     * @param monthNameTextColorDisabled the new disabled month name text color.
     */
    public void setMonthNameTextColorDisabled(int monthNameTextColorDisabled) {
        if (this.monthNameTextColorDisabled != monthNameTextColorDisabled) {
            this.monthNameTextColorDisabled = monthNameTextColorDisabled;
            setTextColor(monthNameTextColorDisabled);

            updateMonthNameColor();
        }
    }

    /**
     * Sets the current month name color for both enabled and disabled states.
     *
     * @param monthNameColorEnabled  the enabled month name text color.
     * @param monthNameColorDisabled the disabled month name text color.
     */
    public void setMonthNameColor(int monthNameColorEnabled, int monthNameColorDisabled) {
        setMonthNameTextColorEnabled(monthNameColorEnabled);
        setMonthNameTextColorDisabled(monthNameColorDisabled);

        setTextColor(monthNameColorEnabled, monthNameColorDisabled);
    }

    /**
     * Gets the current month name type face.
     *
     * @return the current month name type face.
     */
    public Typeface getMonthNameTypeFace() {
        return monthNameTypeFace;
    }

    /**
     * Sets the current month name type face.
     *
     * @param monthNameFont the new month name type face.
     */
    public void setMonthNameTypeFace(Typeface monthNameFont) {
        if (this.monthNameTypeFace == monthNameFont)
            return;

        setTypeface(monthNameFont);

        this.monthNameTypeFace = monthNameFont;
        this.monthNamePaint.setTypeface(monthNameFont);
        this.textPaint.setTypeface(monthNameFont);

        updateMonthNameBounds();
        updatePositions();
    }

    /**
     * Gets the current day names text size.
     *
     * @return the current day names text size.
     */
    public float getDayNameTextSize() {
        return dayNameTextSize;
    }

    /**
     * Sets the current day names text size.
     *
     * @param dayNameTextSize the new day names text size.
     */
    public void setDayNameTextSize(float dayNameTextSize) {
        if (this.dayNameTextSize == dayNameTextSize)
            return;

        this.dayNameTextSize = dayNameTextSize;
        this.dayNamesPaint.setTextSize(dayNameTextSize);

        updateDayNameBounds();
    }

    /**
     * Gets the current day names text color for the enabled state.
     *
     * @return the current day names text color enabled.
     */
    public int getDayNameTextColorEnabled() {
        return dayNameTextColorEnabled;
    }

    /**
     * Sets the current day names text color for the enabled state.
     *
     * @param dayNameTextColorEnabled the new day names text color enabled.
     */
    public void setDayNameTextColorEnabled(int dayNameTextColorEnabled) {
        if (this.dayNameTextColorEnabled == dayNameTextColorEnabled)
            return;

        this.dayNameTextColorEnabled = dayNameTextColorEnabled;

        updateDayNamesColor();
        onElementTextColorChanged();
    }

    /**
     * Sets the day names text color for both enabled and disabled states.
     *
     * @param colorEnabled  day names text color enabled.
     * @param colorDisabled day names text color disabled.
     */
    public void setDayNamesColor(int colorEnabled, int colorDisabled) {
        setDayNameTextColorEnabled(colorEnabled);
        setDayNameTextColorDisabled(colorDisabled);
    }

    /**
     * Gets the day names text color for disabled state.
     *
     * @return the day names text color for disabled state.
     */
    public int getDayNameTextColorDisabled() {
        return dayNameTextColorDisabled;
    }

    /**
     * Sets the day names text color for disabled state.
     *
     * @param dayNameTextColorDisabled the new day names text color disabled.
     */
    public void setDayNameTextColorDisabled(int dayNameTextColorDisabled) {
        if (this.dayNameTextColorDisabled == dayNameTextColorDisabled)
            return;

        this.dayNameTextColorDisabled = dayNameTextColorDisabled;

        updateDayNamesColor();
        onElementTextColorChanged();
    }

    /**
     * Gets the day names type face.
     *
     * @return the current day names type face.
     */
    public Typeface getDayNameTypeFace() {
        return dayNameTypeFace;
    }

    /**
     * Sets the day names type face.
     *
     * @param dayNameTypeFace the new day names type face.
     */
    public void setDayNameTypeFace(Typeface dayNameTypeFace) {
        if (this.dayNameTypeFace == dayNameTypeFace)
            return;

        this.dayNameTypeFace = dayNameTypeFace;
        this.dayNamesPaint.setTypeface(dayNameTypeFace);

        updateDayNameBounds();
    }

    /**
     * Gets the date text size.
     *
     * @return the current date text size.
     */
    public float getDateTextSize() {
        return dateTextSize;
    }

    /**
     * Sets the date text size.
     *
     * @param dateTextSize the new date text size.
     */
    public void setDateTextSize(float dateTextSize) {
        if (this.dateTextSize == dateTextSize)
            return;

        this.dateTextSize = dateTextSize;
        this.datePaint.setTextSize(dateTextSize);
        todayPaint.setTextSize(dateTextSize);

        updateTextBounds();
    }

    /**
     * Gets the date text color for the enabled state.
     *
     * @return the current date text color enabled.
     */
    public int getDateTextColorEnabled() {
        return dateTextColorEnabled;
    }

    /**
     * Sets the date text color for the enabled state.
     *
     * @param dateTextColorEnabled the new date text color enabled.
     */
    public void setDateTextColorEnabled(int dateTextColorEnabled) {
        if (this.dateTextColorEnabled == dateTextColorEnabled)
            return;

        this.dateTextColorEnabled = dateTextColorEnabled;

        updateDateTextColor();
        onElementTextColorChanged();
    }

    /**
     * Sets the date text color for both enabled and disabled states.
     *
     * @param colorEnabled  the new date text color enabled.
     * @param colorDisabled the new date text color disabled.
     */
    public void setDateTextColor(int colorEnabled, int colorDisabled) {
        setDateTextColorEnabled(colorEnabled);
        setDateTextColorDisabled(colorDisabled);
    }

    /**
     * Gets the date text color for the disabled state.
     *
     * @return the current date text color disabled.
     */
    public int getDateTextColorDisabled() {
        return dateTextColorDisabled;
    }

    /**
     * Sets the date text color for the disabled state.
     *
     * @param dateTextColorDisabled the new date text color disabled.
     */
    public void setDateTextColorDisabled(int dateTextColorDisabled) {
        if (this.dateTextColorDisabled == dateTextColorDisabled)
            return;

        this.dateTextColorDisabled = dateTextColorDisabled;

        updateDateTextColor();
        onElementTextColorChanged();
    }

    /**
     * Gets the date type face.
     *
     * @return the current date type face.
     */
    public Typeface getDateTypeFace() {
        return dateTypeFace;
    }

    /**
     * Sets the date type face.
     *
     * @param dateTypeFace the new date type face.
     */
    public void setDateTypeFace(Typeface dateTypeFace) {
        if (this.dateTypeFace == dateTypeFace)
            return;

        this.dateTypeFace = dateTypeFace;
        this.datePaint.setTypeface(dateTypeFace);

        updateTextBounds();
    }

    /**
     * Gets the current offset percentage on the left of the available space for drawing the day names and the dates bellow the month name.
     *
     * @return the current left offset percentage.
     */
    public double getOffsetLeft() {
        return this.offsetLeft;
    }

    /**
     * Sets the current offset percentage on the left of the available space for drawing the day names and the dates bellow the month name.
     *
     * @param offset the new left offset percentage.
     */
    public void setOffsetLeft(double offset) {
        if (offset < 0)
            throw new IllegalArgumentException("offset must be positive value");

        this.offsetLeft = offset;
    }

    /**
     * Gets the current offset percentage on the top of the available space for drawing the day names and the dates bellow the month name.
     *
     * @return the current top offset percentage.
     */
    public double getOffsetTop() {
        return this.offsetTop;
    }

    /**
     * Sets the current offset percentage on the top of the available space for drawing the day names and the dates bellow the month name.
     *
     * @param offset the new top offset percentage.
     */
    public void setOffsetTop(double offset) {
        if (offset < 0)
            throw new IllegalArgumentException("offset must be positive value");

        this.offsetTop = offset;
    }

    /**
     * Gets the current offset percentage on the right of the available space for drawing the day names and the dates bellow the month name.
     *
     * @return the current right offset percentage.
     */
    public double getOffsetRight() {
        return this.offsetRight;
    }

    /**
     * Sets the current offset percentage on the right of the available space for drawing the day names and the dates bellow the month name.
     *
     * @param offset the new right offset percentage.
     */
    public void setOffsetRight(double offset) {
        if (offset < 0)
            throw new IllegalArgumentException("offset must be positive value");

        this.offsetRight = offset;
    }

    /**
     * Gets the current offset percentage on the bottom of the available space for drawing the day names and the dates bellow the month name.
     *
     * @return the current bottom offset percentage.
     */
    public double getOffsetBottom() {
        return this.offsetBottom;
    }

    /**
     * Sets the current offset percentage on the bottom of the available space for drawing the day names and the dates bellow the month name.
     *
     * @param offset the new bottom offset percentage.
     */
    public void setOffsetBottom(double offset) {
        if (offset < 0)
            throw new IllegalArgumentException("offset must be positive value");

        this.offsetBottom = offset;
    }

    /**
     * Gets the month name for this cell.
     *
     * @return the current month name.
     */
    public String getMonthName() {
        return this.monthName;
    }

    /**
     * Sets the month name for this cell.
     *
     * @param monthName the new month name.
     */
    public void setMonthName(String monthName) {
        if (monthName == null)
            throw new NullPointerException("monthName");

        if (this.monthName == null || !this.monthName.equals(monthName)) {
            this.monthName = monthName;
            titleElement.text = this.monthName;
            updateMonthNameBounds();
            updatePositions();
        }
    }

    /**
     * Gets a value determining the position of the month name.
     *
     * @return the current month name position.
     */
    public int getMonthNamePosition() {
        return monthNamePosition;
    }

    /**
     * Sets the position of the month name. It could be LEFT, CENTER or RIGHT.
     *
     * @param monthNamePosition the new month name gravity.
     */
    public void setMonthNameTextPosition(int monthNamePosition) {
        if(this.monthNamePosition == monthNamePosition) {
            return;
        }
        this.monthNamePosition = monthNamePosition;
        setTextPosition(monthNamePosition);
        updateMonthNamePosition();
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean updateStyles = false;
        if(isEnabled() != enabled) {
            updateStyles = true;
        }
        super.setEnabled(enabled);

        if(updateStyles) {
            updateTextColors();
            updateElementsColors();
            owner.applyMonthCellStyles(this);
        }
    }

    /**
     * Gets the number of days in the current month.
     *
     * @return the number of days.
     */
    public int numberOfDays() {
        return this.numberOfDays;
    }

    @Override
    public void setDate(long date) {
        if(this.getDate() == date) {
            return;
        }

        super.setDate(date);

        workCalendar.setTimeInMillis(date);
        this.numberOfDays = workCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        updateElements();
    }

    @Override
    protected void updateTypeFace() {
        if (this.cellToday) {
            this.textPaint.setTypeface(todayTypeFace);
        } else {
            super.updateTypeFace();
        }
    }

    /**
     * Used to update the text colors according to the current active state of the element.
     */
    protected void updateTextColors() {
        updateMonthNameColor();
        updateDayNamesColor();
        updateDateTextColor();
    }

    /**
     * Used to update the elements of the cell. Updates the dates, calculates the positions, applies the custom colors and prepares for rendering.
     */
    protected void updateElements() {
        if (this.dateValues.size() == 0 || this.horizontalSlot == 0 || this.verticalSlot == 0)
            return;

        workCalendar.setTimeInMillis(this.getDate());
        workCalendar.set(Calendar.DAY_OF_WEEK, workCalendar.getFirstDayOfWeek());

        titleElement.reset();
        titleElement.text = this.monthName;

        for (int i = 0, len = CalendarTools.DAYS_IN_A_WEEK; i < len; i++, workCalendar.set(Calendar.DAY_OF_WEEK, i + 1)) {
            MonthCellDayNameElement element = this.elements.get(i);
            element.reset();
            element.isWeekend = (i + 1) == Calendar.SATURDAY || (i + 1) == Calendar.SUNDAY;
            element.time = workCalendar.getTimeInMillis();
            element.text = this.daysOfWeek.get(i);

            Function<Long, Integer> dayNameToColor = this.owner.getDayNameToColor();
            if (dayNameToColor != null) {
                Integer color = dayNameToColor.apply(element.time);
                if (color != null) {
                    element.color = color;
                } else {
                    element.color = this.currentDayNameColor;
                }
            } else {
                element.color = this.currentDayNameColor;
            }

            element.x = (int) (this.monthAvailableSpace.left + (this.horizontalSlot * (i + 1)) - (this.dayBounds.width()));
            element.y = this.monthAvailableSpace.top + this.dayBounds.height();
        }

        int value = 1;

        workCalendar.setTimeInMillis(this.getDate());
        int difference = workCalendar.get(Calendar.DAY_OF_WEEK) - workCalendar.getFirstDayOfWeek();
        if (difference < 0) {
            difference += CalendarTools.DAYS_IN_A_WEEK;
        }

        value -= difference;

        workCalendar.set(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0, row = 0; row < CalendarTools.WEEKS_IN_A_MONTH; row++) {
            for (int column = 0; column < CalendarTools.DAYS_IN_A_WEEK; column++) {
                MonthCellDateElement element = this.dateElements.get(i++);
                element.reset();
                element.dayOfMonth = value;
                if (1 <= value && value <= this.numberOfDays) {
                    workCalendar.set(Calendar.DAY_OF_MONTH, value);
                    long time = workCalendar.getTimeInMillis();
                    element.text = this.dateValues.get(value);
                    element.time = time;
                    int dayOfWeek = workCalendar.get(Calendar.DAY_OF_WEEK);
                    element.isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
                    Rect dateBounds = element.text.length() == 1 ? this.dateBoundsOneSymbol : this.dateBoundsTwoSymbols;

                    if (this.owner.getDateToColor() != null) {
                        Integer color = this.owner.getDateToColor().apply(time);
                        if (color != null) {
                            element.color = color;
                        } else
                            element.color = this.currentDateTextColor;
                    } else
                        element.color = this.currentDateTextColor;

                    element.x = (int) (this.monthAvailableSpace.left + (this.horizontalSlot * (column + 1)) - (dateBounds.width()));
                    element.y = (int) (this.monthAvailableSpace.top + (this.verticalSlot * (row + 1)) + (dateBounds.height()));
                } else {
                    element.text = "";
                }

                value++;
            }
        }
    }

    protected void updateElementsColors() {

        for (int i = 0, len = CalendarTools.DAYS_IN_A_WEEK; i < len; i++, workCalendar.set(Calendar.DAY_OF_WEEK, i + 1)) {
            MonthCellDayNameElement element = this.elements.get(i);

            Function<Long, Integer> dayNameToColor = this.owner.getDayNameToColor();
            if (dayNameToColor != null) {
                Integer color = dayNameToColor.apply(element.time);
                if (color != null) {
                    element.color = color;
                } else {
                    element.color = this.currentDayNameColor;
                }
            } else {
                element.color = this.currentDayNameColor;
            }
        }

        for (int i = 0, row = 0; row < CalendarTools.WEEKS_IN_A_MONTH; row++) {
            for (int column = 0; column < CalendarTools.DAYS_IN_A_WEEK; column++) {
                MonthCellDateElement element = this.dateElements.get(i++);

                if (this.owner.getDateToColor() != null) {
                    Integer color = this.owner.getDateToColor().apply(element.time);
                    if (color != null) {
                        element.color = color;
                    } else
                        element.color = this.currentDateTextColor;
                } else
                    element.color = this.currentDateTextColor;

            }
        }
    }

    @Override
    protected void onArrange() {
        super.onArrange();

        updateValuesCache();
        updatePositions();
        updateElements();
    }

    @Override
    public void render(Canvas canvas) {

        setText("");
        super.render(canvas);

        if(this.owner.isYearModeCompact() != yearModeIsCompact) {
            yearModeIsCompact = this.owner.isYearModeCompact();
            updateMonthNamePosition();
        }

        if (this.owner.isYearModeCompact()) {
            titleElement.text = this.monthName.length() > 3 ? this.monthName.substring(0, 3) : this.monthName;
            owner.applyMonthStylesToMonthCellElement(this, titleElement);
            boolean paintChanged = updatePaintFontForElement(monthNamePaint, titleElement);
            renderTitle(titleElement, canvas);
            if(paintChanged) {
                resetTitlePaint();
            }
            return;
        }

        renderMonthName(canvas);
        renderDayNames(canvas);
        renderDates(canvas);
    }

    /**
     * Used to render the month name.
     *
     * @param canvas the current canvas.
     */
    protected void renderMonthName(Canvas canvas) {
        if (monthName != null) {
            owner.applyMonthStylesToMonthCellElement(this, titleElement);
            boolean paintColorChanged = updatePaintColorForElement(monthNamePaint, titleElement);
            boolean paintFontChanged = updatePaintFontForElement(monthNamePaint, titleElement);
            renderTitle(titleElement, canvas);
            if(paintFontChanged || paintColorChanged) {
                resetTitlePaint();
            }
        }
    }

    /**
     * Used to render the date elements.
     *
     * @param canvas the current canvas.
     */
    protected void renderDates(Canvas canvas) {
        for (MonthCellDateElement element : this.dateElements) {
            dateCalendar.set(Calendar.DAY_OF_MONTH, element.dayOfMonth);
            if(isDrawingToday(element.dayOfMonth)) {
                element.isToday = true;
                updateElementForToday(element);
            }
            owner.applyMonthStylesToMonthCellElement(this, element);

            if(element.backgroundColor != Color.TRANSPARENT && !element.text.isEmpty()) {
                renderElementBackground(element, canvas);
            }

            boolean paintColorChanged = updatePaintColorForElement(datePaint, element);
            boolean paintFontChanged = updatePaintFontForElement(datePaint, element);
            canvas.drawText(element.text, (float) element.x, (float) element.y, this.datePaint);
            if(paintFontChanged) {
                resetDatePaintFont();
            }
            if(paintColorChanged) {
                updateDateTextColor();
            }
        }
    }

    private boolean updatePaintColorForElement(Paint paint, MonthCellElement element) {
        boolean updated = false;
        if(element.color != paint.getColor()) {
            paint.setColor(element.color);
            updated = true;
        }
        return updated;
    }

    private boolean updatePaintFontForElement(Paint paint, MonthCellElement element) {
        boolean updated = false;
        if(element.textSize != paint.getTextSize()) {
            paint.setTextSize(element.textSize);
            updated = true;
        }
        if(element.typeface != paint.getTypeface()) {
            paint.setTypeface(element.typeface);
            updated = true;
        }
        return updated;
    }

    private void updateElementForToday(MonthCellElement element) {
        element.color = todayTextColor;
        element.textSize = todayPaint.getTextSize();
        element.typeface = todayTypeFace;
        element.backgroundColor = todayBackgroundColor;
    }

    private void resetTitlePaint() {
        updateMonthNameColor();
        if(owner.isYearModeCompact()) {
            monthNamePaint.setTextSize(getMonthNameTextSizeCompact());
        } else {
            monthNamePaint.setTextSize(getMonthNameTextSize());
        }
        monthNamePaint.setTypeface(getMonthNameTypeFace());
    }

    private void resetDayNamesPaint() {
        updateDayNamesColor();
        dayNamesPaint.setTextSize(getDayNameTextSize());
        dayNamesPaint.setTypeface(getDayNameTypeFace());
        updateDayNameBounds();
    }

    private void resetDatePaintFont() {
        datePaint.setTextSize(getDateTextSize());
        datePaint.setTypeface(getDateTypeFace());
        updateTextBounds();
    }

    /**
     * Used to render today's date element.
     *
     * @param element the today's date element.
     * @param canvas  the current canvas.
     */
    protected void renderElementBackground(MonthCellDateElement element, Canvas canvas) {
        if(element.textSize != -1) {
            datePaint.setTextSize(element.textSize);
        }
        if(element.typeface != null) {
            datePaint.setTypeface(element.typeface);
        }
        updateTextBounds();
        drawElementBackground(canvas, element, (int) Math.min(horizontalSlot, verticalSlot), element.text.length() == 1 ? this.dateBoundsOneSymbol : this.dateBoundsTwoSymbols);
    }

    /**
     * Used to render the day names.
     *
     * @param canvas the current canvas.
     */
    protected void renderDayNames(Canvas canvas) {
        for (MonthCellElement element : this.elements) {
            owner.applyMonthStylesToMonthCellElement(this, element);
            boolean paintColorChanged = updatePaintColorForElement(dayNamesPaint, element);
            boolean paintFontChanged = updatePaintFontForElement(dayNamesPaint, element);
            renderDayName(element, canvas);
            if(paintColorChanged || paintFontChanged) {
                resetDayNamesPaint();
            }
        }
    }

    protected void renderTitle(MonthCellElement element, Canvas canvas) {
        canvas.drawText(element.text, (float) element.x, (float) element.y, this.monthNamePaint);
    }

    /**
     * Used to render a single day name element.
     *
     * @param element the day name element.
     * @param canvas  the current canvas.
     */
    protected void renderDayName(MonthCellElement element, Canvas canvas) {
        canvas.drawText(element.text, (float) element.x, (float) element.y, this.dayNamesPaint);
    }

    /**
     * Used to render a single date element.
     *
     * @param element the date element.
     * @param canvas  the current canvas.
     */
    protected void renderDate(MonthCellDateElement element, Canvas canvas) {
        canvas.drawText(element.text, (float) element.x, (float) element.y, this.datePaint);
    }

    /**
     * Draws today's date.
     *
     * @param canvas     the canvas onto which the cell will be drawn.
     * @param element    the element.
     * @param circleSize the size of the circle indicating the today's date.
     * @param bounds     the bounds of the today's date.
     */
    protected void drawElementBackground(Canvas canvas, MonthCellDateElement element, int circleSize, Rect bounds) {
        int radius = circleSize / 2;
        datePaint.setColor(element.backgroundColor);
        canvas.drawCircle(element.x + bounds.left + bounds.width() / 2, element.y + bounds.bottom - bounds.height() / 2, radius, datePaint);
    }

    /**
     * Used to calculate the current month name position according to the current calendar compact mode value.
     */
    protected void updateMonthNamePosition() {
        if (this.monthName == null)
            return;

        if(monthNameBoundsCompact == null || monthNameBounds == null) {
            updateMonthNameBounds();
        }
        if (this.owner.isYearModeCompact()) {
            this.monthNamePositionX = (this.getLeft() + (this.getWidth() / 2) - (this.monthNameBoundsCompact.width() / 2));
            this.monthNamePositionY = (this.getTop() + (this.getHeight() / 2) + (this.monthNameBoundsCompact.height() / 2));
        } else {
            if ((this.monthNamePosition & CENTER_HORIZONTAL) > 0) {
                this.monthNamePositionX = (this.getLeft() + (this.getWidth() / 2) - (this.monthNameBounds.width() / 2));
            } else if ((this.monthNamePosition & LEFT) > 0) {
                this.monthNamePositionX = (this.getLeft() + this.getPaddingLeft());
            } else {
                this.monthNamePositionX = (this.getRight() - this.getPaddingRight() - this.monthNameBounds.width());
            }

            this.monthNamePositionY = (this.getTop() + this.getPaddingTop() + this.monthNameBounds.height());
        }
        titleElement.x = this.monthNamePositionX;
        titleElement.y = this.monthNamePositionY;
    }

    private void updateMonthNameBounds() {
        if(this.monthName == null || this.monthNamePaint == null) {
            return;
        }
        this.monthNameBounds = new Rect();
        this.monthNameBoundsCompact = new Rect();
        this.monthNamePaint.getTextBounds(this.monthName, 0, 3, this.monthNameBoundsCompact);
        this.monthNamePaint.getTextBounds(this.monthName, 0, this.monthName.length(), this.monthNameBounds);
    }

    /**
     * Used to update the positions of the elements of the month cell.
     */
    protected void updatePositions() {
        updateMonthNamePosition();
        updateAvailableSpace();

        this.horizontalSlot = this.monthAvailableSpace.width() / 7;
        this.verticalSlot = this.monthAvailableSpace.height() / 7;
    }

    /**
     * Used to update the space that is available after drawing the month name.
     */
    protected void updateAvailableSpace() {
        this.monthAvailableSpace.top = this.monthNamePositionY;
        this.monthAvailableSpace.left = (this.getLeft() + this.getPaddingLeft());
        this.monthAvailableSpace.right = (this.getRight() - this.getPaddingRight());
        this.monthAvailableSpace.bottom = (this.getBottom() - this.getPaddingBottom());

        int offsetLeft = (int) (this.monthAvailableSpace.width() * this.offsetLeft);
        int offsetTop = (int) (this.monthAvailableSpace.height() * this.offsetTop);
        int offsetRight = (int) (this.monthAvailableSpace.width() * this.offsetRight);
        int offsetBottom = (int) (this.monthAvailableSpace.height() * this.offsetBottom);

        this.monthAvailableSpace.left += offsetLeft;
        this.monthAvailableSpace.top += offsetTop;
        this.monthAvailableSpace.right -= offsetRight;
        this.monthAvailableSpace.bottom -= offsetBottom;
    }

    /**
     * Used to update the current instance and clear the cache.
     */
    protected void updateValuesCache() {
        this.daysOfWeek.clear();
        workCalendar.setTimeInMillis(this.getDate());

        workCalendar.set(Calendar.DAY_OF_WEEK, workCalendar.getFirstDayOfWeek());

        for (int i = 0; i < CalendarTools.DAYS_IN_A_WEEK; i++) {
            elements.get(i).dayOfWeek = workCalendar.get(Calendar.DAY_OF_WEEK);
            this.daysOfWeek.put(i, workCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, this.owner.getLocale()).substring(0, 1).toLowerCase());
            workCalendar.add(Calendar.DATE, 1);
        }

        this.dateValues.clear();
        for (int i = 1, len = 32; i < len; i++) {
            this.dateValues.put(i, String.valueOf(i));
        }
    }

    /**
     * Updates the current day names color.
     */
    protected void updateDayNamesColor() {
        if (this.enabled)
            this.currentDayNameColor = this.dayNameTextColorEnabled;
        else
            this.currentDayNameColor = this.dayNameTextColorDisabled;

        this.dayNamesPaint.setColor(this.currentDayNameColor);
    }

    /**
     * Updates the current month name color.
     */
    protected void updateMonthNameColor() {
        if (this.enabled) {
            this.monthNamePaint.setColor(this.monthNameTextColorEnabled);
            this.textPaint.setColor(this.monthNameTextColorEnabled);
        } else {
            this.monthNamePaint.setColor(this.monthNameTextColorDisabled);
            this.textPaint.setColor(this.monthNameTextColorDisabled);
        }
    }

    /**
     * Updates the current date text color.
     */
    protected void updateDateTextColor() {
        if (this.enabled)
            this.currentDateTextColor = this.dateTextColorEnabled;
        else
            this.currentDateTextColor = this.dateTextColorDisabled;

        this.datePaint.setColor(this.currentDateTextColor);
    }

//    @Override
//    protected void updateBackgroundColor() {
//        if (this.cellToday) {
//            setBackgroundColor(CalendarDayCell.todayCellBackgroundColor);
//        } else
//            super.updateBackgroundColor();
//    }

    /**
     * Updates the text bounds of the elements of the month cell.
     */
    protected void updateTextBounds() {
        this.datePaint.getTextBounds("88", 0, 1, this.dateBoundsOneSymbol);
        this.datePaint.getTextBounds("88", 0, 2, this.dateBoundsTwoSymbols);
    }

    protected void updateDayNameBounds() {
        this.dayNamesPaint.getTextBounds("f", 0, 1, this.dayBounds);
    }

    /**
     * Used to determine if the value stands for today's date.
     *
     * @param value the value to be evaluated.
     * @return <code>true</code> if the date value stands for today, <code>false</code> otherwise.
     */
    protected final boolean isDrawingToday(int value) {
        return this.isToday() && value == todayDayOfMonth;
    }

    private void onElementTextColorChanged() {
        updateElements();
    }

    /**
     * Used to store an text element with color inside the month name.
     */
    protected class MonthCellElement {
        public int x;
        public int y;

        public String text = "";
        public int color = Color.TRANSPARENT;
        public float textSize = -1;
        public Typeface typeface = null;
        public int backgroundColor = Color.TRANSPARENT;

        public void reset() {
        }
    }

    protected class MonthCellDayNameElement extends MonthCellElement {
        int dayOfWeek;
        public long time;
        public boolean isWeekend;

        @Override
        public void reset() {
            textSize = dayNameTextSize;
            typeface = dayNameTypeFace;
            color = dayNameTextColorEnabled;
            backgroundColor = Color.TRANSPARENT;
        }
    }

    protected class MonthCellTitleElement extends MonthCellElement {
        @Override
        public void reset() {
            textSize = monthNameTextSize;
            typeface = monthNameTypeFace;
            color = monthNameTextColorEnabled;
            backgroundColor = Color.TRANSPARENT;
        }
    }

    /**
     * Used to store a single date inside the month cell.
     */
    protected class MonthCellDateElement extends MonthCellElement {
        public int dayOfMonth;
        public long time;
        public boolean isToday = false;
        public boolean isWeekend;

        @Override
        public void reset() {
            textSize = dateTextSize;
            typeface = dateTypeFace;
            color = dateTextColorEnabled;
            backgroundColor = Color.TRANSPARENT;
        }
    }
}
