package com.telerik.widget.calendar;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.telerik.widget.calendar.events.EventsDisplayMode;

import java.util.Calendar;
import java.util.List;

/**
 * Used to handle gestures applied to the calendar view.
 */
public class CalendarGestureManager implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ScaleGestureDetector.OnScaleGestureListener {

    private static final float UPPER_PART_RATIO = 0.5f;
    private static final float LOWER_PART_RATIO = 0.8f;

    private static final float FLING_DATE_CHANGE_RATIO = .4f;
    /**
     * The current calendar view instance owning the current gesture manager instance.
     */
    protected final RadCalendarView owner;
    private final Handler handler = new Handler();
    /**
     * The current animations manager.
     */
    protected CalendarAnimationsManager animationsManager;
    /**
     * The current scroll manager.
     */
    protected CalendarScrollManager scrollManager;
    /**
     * The current selection manager.
     */
    protected CalendarSelectionManager selectionManager;
    /**
     * Listener to be called when a cell has been clicked.
     */
    protected RadCalendarView.OnCellClickListener onCellClickListener;
    /**
     * The current display mode.
     */
    protected CalendarDisplayMode displayMode;
    /**
     * Holds the current shapeScale state of the gesture manager. <code>true</code> if currently there is a shapeScale gesture
     * being applied, <code>false</code> otherwise.
     */
    protected boolean isScaleInProgress;
    /**
     * Holds a value determining if the current shapeScale is positive. <code>true</code> if the shapeScale is positive, <code>false</code> otherwise.
     */
    protected boolean currentScaleFactorIsPositive;
    /**
     * The current gesture detector.
     */
    protected GestureDetector gestureDetector;

    /**
     * The current shapeScale gesture detector.
     */
    protected ScaleGestureDetector scaleGestureDetector;

    /**
     * The current scroll mode.
     */
    protected ScrollMode scrollMode;
    CalendarSelectionMode selectionMode;
    private boolean suspendScroll;
    private boolean isScrollInProgress;
    private CalendarCell lastPressedCell;
    private CalendarCell firstPressedCell;
    private boolean hasMoved;
    private boolean usingDragToMakeRangeSelection;
    private boolean doubleTapToChangeDisplayMode = true;
    private boolean tapToChangeDisplayMode = true;
    private boolean swipeUpToChangeDisplayMode;
    private boolean swipeDownToChangeDisplayMode = true;
    private boolean pinchCloseToChangeDisplayMode = true;
    private boolean pinchOpenToChangeDisplayMode = true;
    private long dateOnGestureStart;

    boolean showingEventsForCell() {
        return this.owner.eventsManager != null && this.owner.eventsManager.currentExpandedCell != null;
    }

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarGestureManager} class.
     *
     * @param owner the calendar view instance owning the current gesture manager.
     */
    public CalendarGestureManager(RadCalendarView owner) {
        this.owner = owner;
        init();
    }

    /**
     * Used to prevent the processing of the scrolling from the current manager instance.
     *
     * @see #enableScroll()
     */
    public void suspendScroll() {
        this.suspendScroll = true;
    }

    /**
     * Used to enable the scrolling if it was previously disabled.
     *
     * @see #suspendScroll()
     */
    public void enableScroll() {
        this.suspendScroll = false;
    }

    CalendarAnimationsManager getAnimationsManager() {
        return this.animationsManager;
    }

    void setAnimationsManager(CalendarAnimationsManager animationsManager) {
        this.animationsManager = animationsManager;
    }

    CalendarScrollManager getScrollManager() {
        return this.scrollManager;
    }

    void setScrollManager(CalendarScrollManager scrollManager) {
        this.scrollManager = scrollManager;
    }

    ScrollMode getScrollMode() {
        return this.scrollMode;
    }

    void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }

    CalendarSelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    void setSelectionManager(CalendarSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    CalendarDisplayMode getDisplayMode() {
        return this.displayMode;
    }

    void setDisplayMode(CalendarDisplayMode displayMode) {
        this.displayMode = displayMode;
        init();
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is pinch open. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @return whether display mode is changed while in year view and the detected gesture is pinch open
     */
    public boolean isUsingPinchOpenToChangeDisplayMode() {
        return this.pinchOpenToChangeDisplayMode;
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is pinch open. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @param pinchOpenToChangeDisplayMode whether display mode is changed while in year view and the detected gesture is pinch open
     */
    public void setPinchOpenToChangeDisplayMode(boolean pinchOpenToChangeDisplayMode) {
        this.pinchOpenToChangeDisplayMode = pinchOpenToChangeDisplayMode;
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is pinch close. If the gesture is enabled the
     * display mode will be changed to year.
     *
     * @return whether display mode is changed while in month view and the detected gesture is pinch close
     */
    public boolean isUsingPinchCloseToChangeDisplayMode() {
        return this.pinchCloseToChangeDisplayMode;
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is pinch close. If the gesture is enabled the
     * display mode will be changed to year.
     *
     * @param pinchCloseToChangeDisplayMode whether display mode is changed while in month view and the detected gesture is pinch close
     */
    public void setPinchCloseToChangeDisplayMode(boolean pinchCloseToChangeDisplayMode) {
        this.pinchCloseToChangeDisplayMode = pinchCloseToChangeDisplayMode;
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in week view and the detected gesture is swipe down. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @return whether display mode is changed while in week view and the detected gesture swipe down
     */
    public boolean isUsingSwipeDownToChangeDisplayMode() {
        return this.swipeDownToChangeDisplayMode;
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in week view and the detected gesture is swipe down. If the gesture is enabled the
     * display mode will be changed to month.
     *
     * @param swipeDownToChangeDisplayMode whether display mode is changed while in week view and the detected gesture swipe down
     */
    public void setSwipeDownToChangeDisplayMode(boolean swipeDownToChangeDisplayMode) {
        this.swipeDownToChangeDisplayMode = swipeDownToChangeDisplayMode;
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is swipe up. If the gesture is enabled the
     * display mode will be changed to week.
     *
     * @return whether display mode is changed while in month view and the detected gesture is swipe up
     */
    public boolean isUsingSwipeUpToChangeDisplayMode() {
        return this.swipeUpToChangeDisplayMode;
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month view and the detected gesture is swipe up. If the gesture is enabled the
     * display mode will be changed to week.
     *
     * @param swipeUpToChangeDisplayMode whether display mode is changed while in month view and the detected gesture is swipe up
     */
    public void setSwipeUpToChangeDisplayMode(boolean swipeUpToChangeDisplayMode) {
        this.swipeUpToChangeDisplayMode = swipeUpToChangeDisplayMode;
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is tap. If the gesture is enabled the
     * display mode will be changed to month and the display date will be changed
     * so that the month that is tapped becomes visible.
     *
     * @return whether display mode is changed while in year view and the detected gesture is tap
     */
    public boolean isUsingTapToChangeDisplayMode() {
        return this.tapToChangeDisplayMode;
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in year view and the detected gesture is tap. If the gesture is enabled the
     * display mode will be changed to month and the display date will be changed
     * so that the month that is tapped becomes visible.
     *
     * @param tapToChangeDisplayMode whether display mode is changed while in year view and the detected gesture is tap
     */
    public void setTapToChangeDisplayMode(boolean tapToChangeDisplayMode) {
        this.tapToChangeDisplayMode = tapToChangeDisplayMode;
    }

    /**
     * Gets a value which determines whether the display mode will be changed while the calendar is
     * in month or year view and the detected gesture is double tap. If the gesture is enabled the
     * display mode will be changed from year to month and the display date will be changed
     * so that the month that is tapped becomes visible if the calendar is in year view. If it is in
     * month view the display mode will be changed to year.
     *
     * @return whether display mode is changed while in month or year view and the detected gesture is double tap
     */
    public boolean isUsingDoubleTapToChangeDisplayMode() {
        return this.doubleTapToChangeDisplayMode;
    }

    /**
     * Sets a value which determines whether the display mode will be changed while the calendar is
     * in month or year view and the detected gesture is double tap. If the gesture is enabled the
     * display mode will be changed from year to month and the display date will be changed
     * so that the month that is tapped becomes visible if the calendar is in year view. If it is in
     * month view the display mode will be changed to year.
     *
     * @param doubleTapToChangeDisplayMode whether display mode is changed while in month or year view and the detected gesture is double tap
     */
    public void setDoubleTapToChangeDisplayMode(boolean doubleTapToChangeDisplayMode) {
        this.doubleTapToChangeDisplayMode = doubleTapToChangeDisplayMode;
    }

    /**
     * Gets a value which determines whether the drag gesture will be used to select a rage of cells. Once enabled this will
     * prevent the scroll behavior and will enable range selection using a drag.
     *
     * @return <code>true</code> if drag for range selection is enabled, <code>false</code> otherwise.
     */
    public boolean isUsingDragToMakeRangeSelection() {
        return this.usingDragToMakeRangeSelection;
    }

    /**
     * Sets a value which determines whether the drag gesture will be used to select a rage of cells. Once enabled this will
     * prevent the scroll behavior and will enable range selection using a drag.
     *
     * @param usingDragToMakeRangeSelection the new drag state.
     */
    public void setUsingDragToMakeRangeSelection(boolean usingDragToMakeRangeSelection) {
        this.usingDragToMakeRangeSelection = usingDragToMakeRangeSelection;
    }

    /**
     * Gets a listener that will be called when a cell is being pressed.
     *
     * @return the current pressed cell listener.
     */
    public RadCalendarView.OnCellClickListener getOnCellClickListener() {
        return this.onCellClickListener;
    }

    /**
     * Sets a listener that will be called when a cell is being pressed.
     *
     * @param onCellClickListener the new pressed cell listener.
     */
    public void setOnCellClickListener(RadCalendarView.OnCellClickListener onCellClickListener) {
        this.onCellClickListener = onCellClickListener;
    }

    /**
     * Handles the scroll gesture using the passed parameters and responding according to the current display mode. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)} method.
     *
     * @param x         the current finger position along the x axis.
     * @param y         the current finger position along the y axis.
     * @param distanceX the distance of the scroll along the x axis.
     * @param distanceY the distance of the scroll along the y axis.
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleScroll(float x, float y, float distanceX, float distanceY) {
        return false;
    }

    /**
     * Handles the touch gesture.
     *
     * @param event the event to be handled.
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     */
    public boolean handleTouch(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        this.scaleGestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP)
            onFingerUp();

        return true;
    }

    /**
     * Handles the fling gesture and applies the result by repeatedly invalidating the calendar view instance as needed. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)} method.
     *
     * @param velocityX the velocity produced by the gesture along the x axis.
     * @param velocityY the velocity produced by the gesture along the y axis.
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleFling(float velocityX, float velocityY) {
        return false;
    }

    /**
     * Handles the onDown event. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onDown(android.view.MotionEvent)} method.
     *
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleOnDown() {
        return false;
    }

    /**
     * Handles the confirmed single tap gesture. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onSingleTapConfirmed(android.view.MotionEvent)} method.
     *
     * @param event the event.
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleSingleTapConfirmed(MotionEvent event) {
        return false;
    }

    /**
     * Handles the double tap gesture. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onDoubleTap(android.view.MotionEvent)} method.
     *
     * @param event the event to be handled.
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleDoubleTap(MotionEvent event) {
        return false;
    }

    private Long getSafeDate(long date) {
        Calendar calendar = this.owner.getCalendar();
        calendar.setTimeInMillis(date);
        int month = calendar.get(Calendar.MONTH);

        if (this.owner.getMinDate() != 0 && this.owner.getMinDate() > date) {
            calendar.setTimeInMillis(this.owner.getMinDate());
            if (month < calendar.get(Calendar.MONTH))
                return null;
            else
                date = calendar.getTimeInMillis();
        } else if (this.owner.getMaxDate() != 0 && this.owner.getMaxDate() < date) {
            calendar.setTimeInMillis(this.owner.getMaxDate());
            if (month > calendar.get(Calendar.MONTH))
                return null;
            else
                date = calendar.getTimeInMillis();
        }

        return date;
    }

    /**
     * Handles the beginning of a shapeScale gesture. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onScaleBegin(android.view.ScaleGestureDetector)} method.
     *
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleOnScaleBegin() {
        return false;
    }

    /**
     * Handles the end of a shapeScale gesture. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onScaleEnd(android.view.ScaleGestureDetector)} method.
     *
     * @deprecated
     */
    public void handleOnScaleEnd() {
    }

    /**
     * Handles the shapeScale gesture. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onScale(android.view.ScaleGestureDetector)} method.
     *
     * @param scaleGestureDetector the shapeScale gesture detector to be used from which to take the current shapeScale factor.
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleOnScale(ScaleGestureDetector scaleGestureDetector) {
        return false;
    }

    /**
     * Handles the single tap up gesture. Deprecated - the gesture manager is no longer being called from the calendar to manage gestures, but rather manages
     * all gestures. Use the {@link #onSingleTapUp(android.view.MotionEvent)} method.
     *
     * @return <code>true</code> if the gesture was handled, <code>false</code> otherwise.
     * @deprecated
     */
    public boolean handleOnSingleTapUp() {
        return false;
    }

    /**
     * Invoked on finger up.
     */
    protected void onFingerUp() {
        if (this.isScrollInProgress) {
            this.isScrollInProgress = false;
            if (this.hasMoved) {
                this.animationsManager.requestActiveDateChange();

                if ((this.scrollMode == ScrollMode.Sticky || this.scrollMode == ScrollMode.Combo) ||
                        (this.scrollMode == ScrollMode.Overlap || this.scrollMode == ScrollMode.Stack) ||
                        displayMode == CalendarDisplayMode.Week ||
                        this.scrollManager.scrollShouldBeHorizontal())
                    this.animationsManager.snapFragments();

                this.owner.invalidate();
                this.hasMoved = false;
            }

            if (this.scrollMode == ScrollMode.Plain ||
                    (this.scrollMode == ScrollMode.Free && !isScrollInProgress)) {
                this.scrollManager.updateEventsForFragments();
            }
        }
    }

    /**
     * Handles the tap gesture.
     *
     * @param calendarCell the calendar cell on which the tap gesture occurred.
     */
    protected void handleTapGesture(final CalendarDayCell calendarCell) {
        if (this.displayMode == CalendarDisplayMode.Year) {
            return;
        }

        this.selectionManager.handleTapGesture(calendarCell);

        if (this.owner.eventsDisplayMode != EventsDisplayMode.Normal) {
            if (calendarCell.getEvents() != null) {
                if (this.owner.eventsDisplayMode == EventsDisplayMode.Inline && displayMode == CalendarDisplayMode.Month) {
                    if (showingEventsForCell()) {
                        animationsManager.implodeCalendar(this.owner.eventsManager.hideEvents(), this.owner.eventsManager.currentExpandedCell != null && this.owner.eventsManager.currentExpandedCell != calendarCell ? new OnTransitionCallback() {
                            @Override
                            public void onTransitionComplete() {
                                animationsManager.explodeCalendar(owner.eventsManager.showEvents(calendarCell));
                            }
                        }
                                : null);
                    } else {
                        animationsManager.explodeCalendar(this.owner.eventsManager.showEvents(calendarCell));
                    }
                } else {
                    // Open Popup
                    owner.eventsManager.showEvents(calendarCell);
                }
            } else if (showingEventsForCell()) {
                if (this.owner.eventsDisplayMode == EventsDisplayMode.Inline) {
                    animationsManager.implodeCalendar(this.owner.eventsManager.hideEvents(), null);
                } else {
                    // Close Popup
                    owner.eventsManager.hideEvents();
                }
            }
        }

        if (this.onCellClickListener != null) {
            this.onCellClickListener.onCellClick(calendarCell);
        }
    }

    void onDisplayModeChange() {
        if (owner.eventsManager != null && showingEventsForCell()) {
            owner.eventsManager.hideEvents();
        }
    }

    interface OnTransitionCallback {
        void onTransitionComplete();
    }

    /**
     * Invoked when the user pinches in.
     */
    protected void onPinchOpen() {
        if (!this.pinchOpenToChangeDisplayMode) {
            return;
        }

        if (this.displayMode == CalendarDisplayMode.Year) {
            this.owner.changeDisplayMode(CalendarDisplayMode.Month);
        }
        /*else if (this.displayMode == CalendarDisplayMode.Month) {
            this.owner.changeDisplayMode(CalendarDisplayMode.Week);
        }*/
    }

    /**
     * Invoked when the user pinches out.
     */
    protected void onPinchClose() {
        if (!this.pinchCloseToChangeDisplayMode) {
            return;
        }

        if (this.displayMode == CalendarDisplayMode.Month) {
            if (showingEventsForCell()) {
                if (owner.eventsDisplayMode == EventsDisplayMode.Popup) {
                    owner.eventsManager.hideEvents();
                } else {
                    animationsManager.implodeCalendar(owner.eventsManager.hideEvents(), new OnTransitionCallback() {
                        @Override
                        public void onTransitionComplete() {
                            onPinchClose();
                        }
                    });

                    return;
                }
            }
            this.owner.changeDisplayMode(CalendarDisplayMode.Year);
        }
        /*else if (this.displayMode == CalendarDisplayMode.Week) {
            this.owner.changeDisplayMode(CalendarDisplayMode.Month);
        }*/
    }

    private void init() {
        this.gestureDetector = new GestureDetector(this.owner.getContext(), this);
        this.gestureDetector.setIsLongpressEnabled(false);
        this.scaleGestureDetector = new ScaleGestureDetector(this.owner.getContext(), this);
    }

    void setSelectionMode(CalendarSelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        List<CalendarCell> pressedCells = this.scrollManager.getCellsAtLocation((int) event.getX(), (int) event.getY());

        if (pressedCells != null && pressedCells.size() > 0) {

            CalendarCell cell = pressedCells.get(0);
            if (cell != null) {
                if (cell instanceof CalendarDayCell)
                    this.handleTapGesture((CalendarDayCell) cell);

                if (this.tapToChangeDisplayMode && this.displayMode == CalendarDisplayMode.Year) {

                    long date = cell.getDate();
                    if ((this.owner.getMinDate() != 0 && date < this.owner.getMinDate()) ||
                            (this.owner.getMaxDate() != 0 && date > this.owner.getMaxDate())) {
                        Long safeDate = getSafeDate(date);
                        if (safeDate == null)
                            return true;

                        date = safeDate;
                    }

                    //this.owner.setDisplayDate(date);
                    //this.owner.setDisplayDateSilent(date);
                    //this.owner.changeDisplayMode(CalendarDisplayMode.Month);
                    this.owner.changeDisplayModeToMonth(date);
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(final MotionEvent event) {
        if (showingEventsForCell()) {
            if (owner.eventsDisplayMode == EventsDisplayMode.Popup) {
                owner.eventsManager.hideEvents();
            } else {
                animationsManager.implodeCalendar(owner.eventsManager.hideEvents(), new OnTransitionCallback() {
                    @Override
                    public void onTransitionComplete() {
                        onDoubleTap(event);
                    }
                });

                return false;
            }
        }

        if (this.displayMode == CalendarDisplayMode.Month) {
            if (!this.doubleTapToChangeDisplayMode) {
                return false;
            }
            this.owner.changeDisplayMode(CalendarDisplayMode.Year);
            return true;
        }
        if (this.displayMode == CalendarDisplayMode.Year) {
            if (!this.doubleTapToChangeDisplayMode) {
                return false;
            }

            List<CalendarCell> pressedCell = this.scrollManager.getCellsAtLocation((int) event.getX(), (int) event.getY());

            if (pressedCell != null && pressedCell.size() > 0) {
                long date = pressedCell.get(0).getDate();

                if ((this.owner.getMinDate() != 0 && date < this.owner.getMinDate()) ||
                        (this.owner.getMaxDate() != 0 && date > this.owner.getMaxDate())) {
                    Long safeDate = getSafeDate(date);
                    if (safeDate == null)
                        return true;

                    date = safeDate;
                }

                //this.owner.setDisplayDate(date);
                //this.owner.setDisplayDateSilent(date);
                //this.owner.changeDisplayMode(CalendarDisplayMode.Month);
                this.owner.changeDisplayModeToMonth(date);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        if (this.animationsManager.animationInProcess())
            return false;

        this.dateOnGestureStart = this.owner.getDisplayDate();
        this.animationsManager.reset();

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (showingEventsForCell())
            return false;

        if (selectionMode == CalendarSelectionMode.Range &&
                this.usingDragToMakeRangeSelection) {

            List<CalendarCell> pressedCell = this.scrollManager.getCellsAtLocation((int) e2.getX(), (int) e2.getY());
            if (pressedCell != null && this.displayMode != CalendarDisplayMode.Year && pressedCell.size() > 0) {
                CalendarDayCell cell = (CalendarDayCell) pressedCell.get(0);
                if (cell == null || cell.getCellType() != CalendarCellType.Date || !cell.isSelectable() ||
                        (owner.getMinDate() != 0 && cell.getDate() < owner.getMinDate()) ||
                        (owner.getMaxDate() != 0 && cell.getDate() > owner.getMaxDate())) {
                    return true;
                }

                if (this.isScrollInProgress) {
                    if (cell != this.lastPressedCell) {
                        this.lastPressedCell = cell;
                        this.selectionManager.setSelectedRange(new DateRange(Math.min(firstPressedCell.getDate(), lastPressedCell.getDate()), Math.max(firstPressedCell.getDate(), lastPressedCell.getDate())));
                    }
                } else {
                    this.isScrollInProgress = true;
                    this.firstPressedCell = cell;
                    this.selectionManager.setSelectedRange(new DateRange(firstPressedCell.getDate(), firstPressedCell.getDate()));
                }
                this.owner.invalidate();
            }

            return true;
        }

        if (this.suspendScroll) {
            return false;
        }

        this.hasMoved = true;
        this.isScrollInProgress = true;

        if (this.scrollMode != ScrollMode.None)
            if (!this.isScaleInProgress) {
                this.owner.beginUpdate();
                this.scrollManager.scroll((int) -distanceX, (int) -distanceY);
                this.owner.endUpdate();
            }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

        if (showingEventsForCell())
            return false;

        //if (this.suspendScroll || this.isScaleInProgress || (this.scrollMode != ScrollMode.Free && this.scrollMode != ScrollMode.Combo)) {
        // TODO: fix scrolling before using this.
            /*if (this.scrollMode == ScrollMode.Sticky && this.dateOnGestureStart == this.owner.getDisplayDate()) {
                if (scrollManager.scrollShouldBeHorizontal()) {
                    if (Math.abs(velocityX * FLING_DATE_CHANGE_RATIO) > this.scrollManager.currentFragmentHolder.getWidth()) {
                        if (velocityX < 0) {
                            this.owner.animateToNext();
                        } else {
                            this.owner.animateToPrevious();
                        }
                        this.animationsManager.reset();
                    }
                } else {
                    if (Math.abs(velocityY * FLING_DATE_CHANGE_RATIO) > this.scrollManager.currentFragmentHolder.getHeight()) {
                        if (velocityY < 0) {
                            this.owner.animateToNext();
                        } else {
                            this.owner.animateToPrevious();
                        }
                        this.animationsManager.reset();
                    }
                }
            }*/


        //return false;
        //}

        if (!this.owner.isHorizontalScroll() || this.displayMode == CalendarDisplayMode.Week) {
            if ((event1 != null && event2 != null)) {
                if (this.displayMode == CalendarDisplayMode.Month && event1.getY() / this.owner.getHeight() > LOWER_PART_RATIO && event2.getY() / this.owner.getHeight() < UPPER_PART_RATIO) {
                    boolean handled = this.onDragTop();
                    if (handled)
                        return true;
                } else if (this.displayMode == CalendarDisplayMode.Week && event1.getY() > this.scrollManager.getTop() && event1.getY() < this.scrollManager.getBottom() && event2.getY() > this.scrollManager.getBottom()) {
                    boolean handled = this.onDragBottom();
                    if (handled)
                        return true;
                }
            }
        }

        if (this.suspendScroll || this.isScaleInProgress || (this.scrollMode != ScrollMode.Free && this.scrollMode != ScrollMode.Combo))
            return false;

        this.animationsManager.setVelocity((int) velocityX, (int) velocityY);

        return true;
    }

    private boolean onDragTop() {
        if (this.displayMode == CalendarDisplayMode.Month && !showingEventsForCell()) {

            if (!this.swipeUpToChangeDisplayMode) {
                return false;
            }

            this.owner.changeDisplayMode(CalendarDisplayMode.Week);
            return true;
        }
        return false;
    }

    private boolean onDragBottom() {
        if (this.displayMode == CalendarDisplayMode.Week && !showingEventsForCell()) {

            if (!this.swipeDownToChangeDisplayMode) {
                return false;
            }

            this.owner.changeDisplayMode(CalendarDisplayMode.Month);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (scaleGestureDetector.getScaleFactor() > 1) {
            this.currentScaleFactorIsPositive = true;
        } else if (scaleGestureDetector.getScaleFactor() < 1) {
            this.currentScaleFactorIsPositive = false;
        }

        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        this.isScaleInProgress = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (currentScaleFactorIsPositive) {
            if (!this.pinchOpenToChangeDisplayMode) {
                this.isScaleInProgress = false;
                return;
            }

            this.onPinchOpen();
        } else {
            if (!this.pinchCloseToChangeDisplayMode) {
                this.isScaleInProgress = false;
                return;
            }

            this.onPinchClose();
        }

        this.isScaleInProgress = false;
    }
}
