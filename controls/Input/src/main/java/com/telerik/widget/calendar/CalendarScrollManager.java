package com.telerik.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.telerik.widget.calendar.events.Event;
import com.telerik.widget.calendar.events.read.GenericResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is responsible for scrolling the calendar fragments and keeping track of their positions. It manages a set of three fragments and updates and rearranges them accordingly
 * to the current scroll modes and direction so that it creates the illusion of endless fragments in both directions.
 */
public class CalendarScrollManager extends CalendarElement {

    class DecorationsLayer extends View {

        public DecorationsLayer(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (scrollMode != ScrollMode.Overlap && scrollMode != ScrollMode.Stack) {
                updateDecorations();
                drawDecorationsForFragment(previousFragment, canvas);
                drawDecorationsForFragment(currentFragment, canvas);
                drawDecorationsForFragment(nextFragment, canvas);
            }
        }

        private void drawDecorationsForFragment(CalendarFragment fragment, Canvas canvas) {
            if (fragment.visibility == ElementVisibility.Visible) {
                canvas.save(Canvas.MATRIX_SAVE_FLAG);
                canvas.translate(fragment.virtualOffsetX - getLeft(), fragment.virtualOffsetY - getTop());
                fragment.postRender(canvas, true);
                canvas.restore();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }
    }

    class FragmentHolder extends ViewGroup {

        LayerView frontLayer;
        LayerView backLayer;

        private CalendarFragment fragment;

        public FragmentHolder(Context context) {
            super(context);
            setClipChildren(false);
            frontLayer = new LayerView(getContext());
            backLayer = new LayerView(getContext());
            frontLayer.postRender = true;
            addView(backLayer);
            addView(frontLayer);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        public CalendarFragment fragment() {
            return this.fragment;
        }

        public void setFragment(CalendarFragment fragment) {
            this.fragment = fragment;
        }

        public void invalidateLayers() {
            if (fragment.dirty)
                backLayer.invalidate();
        }

        public void postInvalidateLayers() {
            if (fragment.dirty)
                frontLayer.invalidate();
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            fragment.setVirtualOffsets(getLeft(), getTop());
            if (backLayer.getWidth() != getWidth() || backLayer.getHeight() != getHeight()) {
                backLayer.layout(0, 0, getWidth(), getHeight());
                frontLayer.layout(0, 0, getWidth(), getHeight());
            }
        }

        public void translate(int offsetX, int offsetY) {
            layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX, getBottom() + offsetY);
        }

        class LayerView extends View {
            boolean postRender;

            public LayerView(Context context) {
                super(context);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return false;
            }

            @Override
            protected void onDraw(Canvas canvas) {
                if (postRender) {
                    if ((scrollMode == ScrollMode.Overlap || scrollMode == ScrollMode.Stack)) {
                        if (owner.showCellDecorations) {
                            updateDecorations();
                            if (fragment.visibility == ElementVisibility.Visible)
                                fragment.postRender(canvas, true);
                        } else {
                            if (fragment.visibility == ElementVisibility.Visible)
                                fragment.postRender(canvas, false);
                        }
                    }

                } else {
                    if (fragment.visibility == ElementVisibility.Visible)
                        fragment.render(canvas, false);
                }
                fragment.dirty = false;
            }
        }
    }

    private static final double MAX_FRAGMENT_CLOSURE_VERTICAL = .17f;
    private static final double MAX_FRAGMENT_CLOSURE_HORIZONTAL = .12f;
    /**
     * The current scroll mode. It is internally updated by the owning chart and should not be directly changed.
     */
    protected ScrollMode scrollMode;
    /**
     * The current active date. Might not always be the current display date while scrolling is in progress.
     */
    protected long activeDate;
    /**
     * States whether the first arrange has been initiated.
     */
    protected boolean arrangePassed;
    /**
     * The fragment that comes before the current fragment.
     */
    protected CalendarFragment previousFragment;
    /**
     * The current or center fragment.
     */
    protected CalendarFragment currentFragment;
    /**
     * The fragment that comes after the current fragment.
     */
    protected CalendarFragment nextFragment;
    /**
     * The fragment that is currently being dragged.
     */
    protected FragmentHolder currentDragFragmentHolder;

    protected FragmentHolder previousFragmentHolder;
    protected FragmentHolder currentFragmentHolder;
    protected FragmentHolder nextFragmentHolder;
    protected DecorationsLayer decorationsLayer;

    private boolean horizontalScroll;
    private boolean suspendActiveDateUpdate;
    private boolean forwardBorderReached;
    private boolean backwardBorderReached;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarScrollManager} class.
     *
     * @param owner the calendar view instance that owns this instance.
     */
    public CalendarScrollManager(RadCalendarView owner) {
        super(owner);

        init();
    }

    void markFragmentsDirty() {
        this.previousFragment.dirty = true;
        this.currentFragment.dirty = true;
        this.nextFragment.dirty = true;
    }

    @Override
    protected void onAlphaChanged() {
        this.currentFragment.setAlpha(this.alpha);
        this.previousFragment.setAlpha(this.alpha);
        this.nextFragment.setAlpha(this.alpha);
    }

    /**
     * Gets the maximum scroll offset that will be applied for a given gesture.
     * Deprecated - the scrolling offset will no longer be suppressed.
     *
     * @return the maximum scroll offset.
     * @deprecated
     */
    public int getMaxScrollOffset() {
        return 0;
    }

    /**
     * Sets the maximum scroll offset that will be applied for a given gesture.
     * Deprecated - the scrolling offset will no longer be suppressed
     *
     * @param maxScrollOffset the new max scroll offset.
     * @deprecated
     */
    public void setMaxScrollOffset(int maxScrollOffset) {
    }

    ScrollMode getScrollMode() {
        return this.scrollMode;
    }

    void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }

    boolean isHorizontalScroll() {
        return this.horizontalScroll;
    }

    void setHorizontalScroll(boolean horizontalScroll) {
        this.horizontalScroll = horizontalScroll;
    }

    /**
     * Gets the current active date. This date might differ from the display date of the calendar during scroll.
     *
     * @return the current active date.
     */
    public long getActiveDate() {
        return activeDate;
    }

    /**
     * Gets the current active date. This date might differ from the display date of the calendar during scroll.
     * The active date will be updated after which a call to the {@link #updateActiveFragment()} will be made, so that the change
     * can be completed.
     *
     * @param activeDate the new active date.
     */
    public void setActiveDate(long activeDate) {
        if (!this.suspendActiveDateUpdate)
            this.activeDate = activeDate;

        updateActiveFragment();
    }

    /**
     * The current or center fragment.
     *
     * @return the current fragment.
     */
    public CalendarFragment currentFragment() {
        return this.currentFragment;
    }

    /**
     * The fragment that comes after the current fragment.
     *
     * @return the next fragment.
     */
    public CalendarFragment nextFragment() {
        return this.nextFragment;
    }

    /**
     * The fragment that comes before the current fragment.
     *
     * @return the previous fragment.
     */
    public CalendarFragment previousFragment() {
        return this.previousFragment;
    }

    /**
     * Determines whether the scroll should be horizontal or vertical.
     *
     * @return <code>true</code> if the scroll should be horizontal, <code>false</code> if it should be vertical.
     */
    protected boolean scrollShouldBeHorizontal() {
        return this.horizontalScroll || this.owner.getDisplayMode() == CalendarDisplayMode.Week; // TODO cache
    }

    @Override
    public void arrange(int left, int top, int right, int bottom) {
        super.arrange(left, top, right, bottom);
        this.arrangePassed = true;
    }

    /**
     * Returns a collection of cells that are located at a specific coordinates. The reason it is a collection
     * is because in some scroll modes there is overlapping of the fragments and there are invisible cells at the same location as visible ones.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @return the collection of cells at this location.
     */
    public List<CalendarCell> getCellsAtLocation(int x, int y) {
        /*if (x < getLeft() || x > getRight() ||
                y < getTop() || y > getBottom())
            return new ArrayList<CalendarCell>();*/

        ArrayList<CalendarCell> cells = new ArrayList<CalendarCell>();
        CalendarCell cell = this.currentFragment.getCellAtLocation(x - currentFragmentHolder.getLeft(), y - currentFragmentHolder.getTop() - owner.datesHolder.getTop());
        if (cell != null) {
            cells.add(cell);
            currentFragment.dirty = true;
        }

        cell = this.previousFragment.getCellAtLocation(x - previousFragmentHolder.getLeft(), y - previousFragmentHolder.getTop() - owner.datesHolder.getTop());
        if (cell != null) {
            cells.add(cell);
            previousFragment.dirty = true;
        }

        cell = this.nextFragment.getCellAtLocation(x - nextFragmentHolder.getLeft(), y - nextFragmentHolder.getTop() - owner.datesHolder.getTop());
        if (cell != null) {
            cells.add(cell);
            nextFragment.dirty = true;
        }

        return cells;
    }

    private boolean currentScrollHorizontal;

    /**
     * Scrolls the fragment with a specified offset according to the current position and the current scroll mode.
     *
     * @param offsetX the offset along the x axis.
     * @param offsetY the offset along the y axis.
     * @return <code>true</code> if scroll was performed, <code>false</code> otherwise.
     */
    public boolean scroll(int offsetX, int offsetY) {
        currentScrollHorizontal = scrollShouldBeHorizontal();

        offsetX = currentScrollHorizontal ? offsetX : 0;
        offsetY = !currentScrollHorizontal ? offsetY : 0;

        if (forwardBorderReached) {
            if (currentScrollHorizontal) {
                if (offsetX < 0 && this.currentFragmentHolder.getLeft() + offsetX < getLeft()) {
                    offsetX += getLeft() - (currentFragmentHolder.getLeft() + offsetX);
                }
            } else {
                if (offsetY < 0 && this.currentFragmentHolder.getTop() + offsetY < getTop()) {
                    offsetY += getTop() - (currentFragmentHolder.getTop() + offsetY);
                }
            }
        }

        if (backwardBorderReached) {
            if (currentScrollHorizontal) {
                if (offsetX > 0 && this.currentFragmentHolder.getLeft() + offsetX > getLeft()) {
                    offsetX -= Math.abs((currentFragmentHolder.getLeft() + offsetX)) - getLeft();
                }
            } else {
                if (offsetY > 0 && this.currentFragmentHolder.getTop() + offsetY > getTop()) {
                    offsetY -= Math.abs(currentFragmentHolder.getTop() + offsetY) - getTop();
                }
            }
        }

        if (offsetX == 0 && offsetY == 0)
            return false;

        if (this.scrollMode == ScrollMode.Overlap || this.scrollMode == ScrollMode.Stack) {
            handleScrollWithOverlap(offsetX, offsetY);
        } else {
            handleScrollWithoutOverlap(offsetX, offsetY);
        }

        return true;
    }

    /**
     * Handles scrolling when in overlap or stacked modes.
     *
     * @param offsetX the offset along the x axis.
     * @param offsetY the offset along the y axis.
     */
    protected void handleScrollWithOverlap(int offsetX, int offsetY) {
        if (this.scrollShouldBeHorizontal()) {
            handleHorizontalOverlappingScroll(offsetX);
        } else {
            handleVerticalOverlappingScroll(offsetY);
        }
    }

    /**
     * Handles vertical scrolling when in overlap or stacked modes.
     *
     * @param offsetY the offset along the y axis.
     */
    protected void handleVerticalOverlappingScroll(int offsetY) {
        if (this.scrollMode == ScrollMode.Overlap) {
            if (this.currentDragFragmentHolder == null)
                if (offsetY < 0) {
                    this.currentDragFragmentHolder = this.nextFragmentHolder;
                } else {
                    this.currentDragFragmentHolder = this.previousFragmentHolder;
                }

            this.currentDragFragmentHolder.translate(0, offsetY);

            /*if (this.currentDragFragment == this.previousFragment && this.previousFragment.getVirtualOffsetY() > this.getHeight()) {
                this.previousFragment.translate(0, getHeight() - this.previousFragment.getVirtualOffsetY());
            } else if (this.currentDragFragment == this.nextFragment && this.nextFragment.getVirtualOffsetY() > 0) {
                this.nextFragment.translate(0, -this.nextFragment.getVirtualOffsetY());
            }*/
        } else {
            if (this.currentDragFragmentHolder == null)
                if (offsetY < 0) {
                    this.currentDragFragmentHolder = this.nextFragmentHolder;
                } else {
                    this.currentDragFragmentHolder = this.currentFragmentHolder;
                }

            this.currentDragFragmentHolder.translate(0, offsetY);

            /*if (this.currentDragFragmentHolder.getTop() < getTop()) {
                this.currentDragFragmentHolder.translate(0, Math.abs(this.currentDragFragmentHolder.getTop()));
            } else if (this.currentDragFragmentHolder.getTop() > getHeight()) {
                this.currentDragFragmentHolder.translate(0, -(this.currentDragFragmentHolder.getTop() - getHeight()));
            }*/
        }
    }

    /**
     * Handles horizontal scrolling when in overlap or stacked modes.
     *
     * @param offsetX the offset along the x axis.
     */
    protected void handleHorizontalOverlappingScroll(int offsetX) {
        if (this.scrollMode == ScrollMode.Overlap) {
            if (this.currentDragFragmentHolder == null)
                if (offsetX < 0) {
                    this.currentDragFragmentHolder = this.nextFragmentHolder;
                } else {
                    this.currentDragFragmentHolder = this.previousFragmentHolder;
                }

            this.currentDragFragmentHolder.translate(offsetX, 0);

            if (this.currentDragFragmentHolder == this.previousFragmentHolder) {
                if (this.currentDragFragmentHolder.getLeft() > 0) {
                    this.currentDragFragmentHolder.translate(-this.currentDragFragmentHolder.getLeft(), 0);
                } else if (this.currentDragFragmentHolder.getLeft() < -getWidth()) {
                    this.currentDragFragmentHolder.translate(Math.abs(-getWidth() - this.currentDragFragmentHolder.getLeft()), 0);
                }
            } else if (this.currentDragFragmentHolder == this.nextFragmentHolder) {
                if (this.currentDragFragmentHolder.getLeft() < getLeft()) {
                    this.currentDragFragmentHolder.translate(Math.abs(this.nextFragment.getLeft()), 0);
                } else if (this.currentDragFragmentHolder.getLeft() > getWidth()) {
                    this.currentDragFragmentHolder.translate(-(this.currentDragFragmentHolder.getLeft() - getWidth()), 0);
                }
            }
        } else {
            if (this.currentDragFragmentHolder == null)
                if (offsetX < 0) {
                    this.currentDragFragmentHolder = this.nextFragmentHolder;
                } else {
                    this.currentDragFragmentHolder = this.currentFragmentHolder;
                }

            this.currentDragFragmentHolder.translate(offsetX, 0);

            if (this.currentDragFragmentHolder.getLeft() > getWidth()) {
                this.currentDragFragmentHolder.translate(-(this.currentDragFragmentHolder.getLeft() - getWidth()), 0);
            } else if (currentDragFragmentHolder.getLeft() < getLeft()) {
                this.currentDragFragmentHolder.translate(Math.abs(this.currentDragFragmentHolder.getLeft()), 0);
            }
        }
    }

    /**
     * Handles horizontal scrolling for non overlapping modes.
     *
     * @param offsetX the offset along the x axis.
     * @param offsetY the offset along the y axis.
     */
    protected void handleScrollWithoutOverlap(int offsetX, int offsetY) {
        if (scrollShouldBeHorizontal()) {
            previousFragmentHolder.translate(offsetX, 0);
            currentFragmentHolder.translate(offsetX, 0);
            nextFragmentHolder.translate(offsetX, 0);
            //virtualOffsetX += offsetX;
        } else {
            previousFragmentHolder.translate(0, offsetY);
            currentFragmentHolder.translate(0, offsetY);
            nextFragmentHolder.translate(0, offsetY);
            //virtualOffsetY += offsetY;
        }

        attemptCurrentFragmentUpdate(offsetX, offsetY);
    }

    /**
     * Used to update the manager after the display date of the owning calendar has been changed.
     */
    public void onDateChanged() {
        if ((this.scrollMode != ScrollMode.Overlap && this.scrollMode != ScrollMode.Stack) && this.owner.getDisplayMode() == CalendarDisplayMode.Month && !this.scrollShouldBeHorizontal()) {
            this.previousFragment.trim();
            this.currentFragment.trim();
            this.nextFragment.trim();
        }

        if (this.arrangePassed)
            snapFragments();
    }

    /**
     * Calculates the current offset along the x axis that needs to be addressed so that the appropriate fragment according to the current scroll mode snaps to the screen.
     *
     * @return the current snap offset along the x axis.
     */
    public int currentSnapOffsetX() {
        if (this.currentDragFragmentHolder != null) {
            if (!this.scrollShouldBeHorizontal())
                return 0;

            if (this.scrollMode == ScrollMode.Overlap || this.currentDragFragmentHolder != this.currentFragmentHolder) {
                if (getFragmentExposure(this.currentDragFragmentHolder) >= MAX_FRAGMENT_CLOSURE_HORIZONTAL)
                    return -this.currentDragFragmentHolder.getLeft();
                else if (this.currentDragFragmentHolder.getLeft() > getLeft())
                    return this.getWidth() - currentDragFragmentHolder.getLeft();
                else
                    return -this.currentDragFragmentHolder.getRight();
            } else if (this.scrollMode == ScrollMode.Stack) {
                if (getFragmentExposure(this.currentDragFragmentHolder) >= 1 - (MAX_FRAGMENT_CLOSURE_HORIZONTAL)) {
                    return -currentDragFragmentHolder.getLeft();
                } else
                    return this.getWidth() - this.currentDragFragmentHolder.getLeft();
            }
        }

        return -this.currentFragmentHolder.getLeft() + getLeft();
    }

    /**
     * Calculates the current offset along the y axis that needs to be addressed so that the appropriate fragment according to the current scroll mode snaps to the screen.
     *
     * @return the current snap offset along the y axis.
     */
    public int currentSnapOffsetY() {
        if (this.currentDragFragmentHolder != null) {
            if (this.scrollShouldBeHorizontal())
                return 0;

            if (this.scrollMode == ScrollMode.Overlap || this.currentDragFragmentHolder != this.currentFragmentHolder) {
                if (getFragmentExposure(this.currentDragFragmentHolder) > MAX_FRAGMENT_CLOSURE_VERTICAL)
                    return -(this.currentDragFragmentHolder.getTop() - getTop());
                else if (this.currentDragFragmentHolder.getTop() < getTop())
                    return -(currentDragFragmentHolder.getBottom() - getTop());
                else
                    return Math.abs(getHeight() - this.currentDragFragmentHolder.getTop() + getTop());
            } else {
                if (getFragmentExposure(this.currentDragFragmentHolder) >= 1 - (MAX_FRAGMENT_CLOSURE_VERTICAL)) {
                    return -this.currentDragFragmentHolder.getTop() + getTop();
                } else {
                    return getHeight() - this.currentDragFragmentHolder.getTop() + getTop();
                }
            }
        }

        return -this.currentFragmentHolder.getTop() + getTop();
    }

    /**
     * Used to reset all the fragments of the manager.
     *
     * @see CalendarFragment#reset()
     */
    public void reset() {
        reset(false);
    }

    /**
     * Used to reset all the fragments of the manager in either forced or normal mode. Forced mode will cause the
     * fragments to reinitialize to match a significant calendar change such as display mode change.
     *
     * @param force <code>true</code> if the reset should be forced, <code>false</code> otherwise.
     * @see CalendarFragment#reset()
     */
    public void reset(boolean force) {
        this.currentFragment.reset();
        this.previousFragment.reset();
        this.nextFragment.reset();
    }

    /**
     * Used to update the active state of the fragment.
     */
    public void updateActiveFragment() {
        this.previousFragment.setEnabled(this.previousFragment.getDisplayDate() == this.activeDate);
        this.nextFragment.setEnabled(this.nextFragment.getDisplayDate() == this.activeDate);
        this.currentFragment.setEnabled(this.currentFragment.getDisplayDate() == this.activeDate);
    }

    @Override
    public void render(Canvas canvas) {
        if (previousFragment.dirty || currentFragment.dirty || nextFragment.dirty) {
            this.previousFragmentHolder.invalidateLayers();
            this.currentFragmentHolder.invalidateLayers();
            this.nextFragmentHolder.invalidateLayers();
        }
    }

    @Override
    public void postRender(Canvas canvas) {
        if (previousFragment.dirty || currentFragment.dirty || nextFragment.dirty) {
            if (scrollMode == ScrollMode.Overlap || scrollMode == ScrollMode.Stack) {
                this.previousFragmentHolder.postInvalidateLayers();
                this.currentFragmentHolder.postInvalidateLayers();
                this.nextFragmentHolder.postInvalidateLayers();
            }
        }

        if (scrollMode != ScrollMode.Overlap && scrollMode != ScrollMode.Stack) {
            this.decorationsLayer.invalidate();
        }
    }

    /**
     * Handles the update of the fragments after snapping to the screen.
     */
    public void onSnapComplete() {
        boolean activeDateChanged = false;
        if (this.currentDragFragmentHolder != null && (this.scrollMode == ScrollMode.Overlap || this.scrollMode == ScrollMode.Stack)) {

            if (this.scrollMode == ScrollMode.Overlap) {
                if ((this.scrollShouldBeHorizontal() && (this.previousFragmentHolder.getLeft() == getLeft() || this.nextFragmentHolder.getLeft() == getLeft())) ||
                        (!this.scrollShouldBeHorizontal() && (this.previousFragmentHolder.getTop() == getTop() || this.nextFragmentHolder.getTop() == getTop()))) {

                    this.requestFragmentsSwitch(this.currentDragFragmentHolder == this.nextFragmentHolder);
                    if(getActiveDate() != this.owner.getDisplayDate()) {
                        setActiveDate(this.owner.getDisplayDate());
                        updateActiveFragment();
                        activeDateChanged = true;
                    }
                }
            } else {
                if ((this.scrollShouldBeHorizontal() && (this.currentFragmentHolder.getLeft() == this.getWidth() || this.nextFragmentHolder.getLeft() == getLeft())) ||
                        (!this.scrollShouldBeHorizontal() && (this.currentFragmentHolder.getTop() == getBottom() || this.nextFragmentHolder.getTop() == getTop()))) {

                    this.requestFragmentsSwitch(this.currentDragFragmentHolder == this.nextFragmentHolder);

                    if(getActiveDate() != this.owner.getDisplayDate()) {
                        setActiveDate(this.owner.getDisplayDate());
                        updateActiveFragment();
                        activeDateChanged = true;
                    }
                }
            }

            this.currentDragFragmentHolder = null;
        }

        if(activeDateChanged) {
            updateEventsForFragments();
        }
    }

    /**
     * Used to update the events of the calendar fragments. Scrolling will be disabled and the events for all cells will be updated. In case of
     * attempting to scroll, the update will be canceled and the scrolling will be enabled.
     */
    public void updateEventsForFragments() {
        if (this.owner.getDisplayMode() != CalendarDisplayMode.Year) {

            List<Long> dates = new ArrayList<Long>();
            loadEventDatesForFragment(this.previousFragment, dates);
            loadEventDatesForFragment(this.currentFragment, dates);
            loadEventDatesForFragment(this.nextFragment, dates);

            this.owner.getEventAdapter().requestEventsForDates(dates, new GenericResultCallback<HashMap<Long, List<Event>>>() {
                @Override
                public void onResult(HashMap<Long, List<Event>> result) {
                    for (long date : result.keySet()) {
                        if (owner.dateToCell().containsKey(date))
                            for (CalendarDayCell cell : owner.dateToCell().get(date)) {
                                if (cell.getVisibility() == ElementVisibility.Visible) {
                                    cell.setEvents(result.get(date));
                                }
                            }
                    }

                    markFragmentsDirty();
                    owner.invalidate();
                }
            });
        }
    }

    /**
     * Used to update the current display date of the calendar and request fragments rearrange if needed.
     */
    protected void attemptCurrentFragmentUpdate(int offsetX, int offsetY) {
        if (this.scrollShouldBeHorizontal()) {
            if (offsetX < 0) {
                if (this.currentFragmentHolder.getLeft() < getLeft() && this.currentFragmentHolder.getLeft() / (double) getWidth() < -MAX_FRAGMENT_CLOSURE_HORIZONTAL)
                    requestFragmentsSwitch(true);
            } else {
                if (this.currentFragmentHolder.getLeft() > getLeft() && this.currentFragmentHolder.getLeft() / (double) getWidth() > MAX_FRAGMENT_CLOSURE_HORIZONTAL)
                    requestFragmentsSwitch(false);
            }
        } else {
            if (offsetY < 0) {
                if (this.currentFragmentHolder.getTop() < getTop() && (getTop() - this.currentFragmentHolder.getTop()) / (double) getHeight() > MAX_FRAGMENT_CLOSURE_VERTICAL)
                    requestFragmentsSwitch(true);
            } else {
                if (owner.getDisplayMode() == CalendarDisplayMode.Month && (scrollMode != ScrollMode.Overlap && scrollMode != ScrollMode.Stack)) {
                    if (this.nextFragmentHolder.getTop() > getBottom()) {
                        requestFragmentsSwitch(false);
                    }
                } else {
                    if (this.currentFragmentHolder.getTop() > getTop() && (currentFragmentHolder.getTop() - getTop()) / (double) getHeight() > MAX_FRAGMENT_CLOSURE_VERTICAL)
                        requestFragmentsSwitch(false);
                }
            }
        }
    }

    /**
     * Used to call update of the current display date of the owning calendar and to rearrange and update the fragments.
     *
     * @param increase <code>true</code> means the date should increase, <code>false</code> means the date should decrease.
     */
    protected void requestFragmentsSwitch(boolean increase) {
        if (increase) {
            shiftFragmentsForward();
        } else {
            shiftFragmentsBackward();
        }

        updateCurrentFragmentState();

        this.suspendActiveDateUpdate = true;
        this.owner.shiftDate(increase);
        this.suspendActiveDateUpdate = false;
    }

    private void updateCurrentFragmentState() {
        this.currentFragment.setCurrentFragment(true);
        this.previousFragment.setCurrentFragment(false);
        this.nextFragment.setCurrentFragment(false);
    }

    void updateBorders() {
        this.forwardBorderReached = !this.owner.canShiftToNextDate();
        this.backwardBorderReached = !this.owner.canShiftToPreviousDate();
    }

    /**
     * Determines if the given fragment has some part of it currently visible on the screen. Deprecated - this method will no longer be needed.
     *
     * @param fragment the fragment to evaluate.
     * @return <code>true</code> if at least some part of the fragment is being currently visible, <code>false</code> otherwise.
     * @deprecated
     */
    protected final boolean fragmentIsVisible(CalendarFragment fragment) {
        return true;
    }

    /**
     * Calculates the percentage of the currently visible area of a given fragment.
     *
     * @param fragmentHolder the fragment holder to evaluate.
     * @return the currently visible area percentage.
     */
    protected final double getFragmentExposure(FragmentHolder fragmentHolder) {
        if (this.scrollShouldBeHorizontal()) {
            if (fragmentHolder.getLeft() < getLeft())
                return (fragmentHolder.getLeft() + fragmentHolder.getWidth()) / (double) this.getWidth();
            else
                return (this.getRight() - fragmentHolder.getLeft()) / (double) this.getWidth();
        } else {
            if (fragmentHolder.getTop() < getTop()) {
                return Math.abs(fragmentHolder.getBottom() - getTop()) / (double) this.getHeight();
            } else {
                return Math.abs((getBottom() - fragmentHolder.getTop())) / (double) this.getHeight();
            }
        }
    }

    @Override
    protected void onArrange() {
        super.onArrange();

        int halfWidthH = 0;
        int halfWidthV = 0;

        if (owner.drawingVerticalGridLines)
            halfWidthH = (int) owner.getGridLinesLayer().halfLineWidth;
        if (owner.drawingHorizontalGridLines)
            halfWidthV = (int) owner.getGridLinesLayer().halfLineWidth;

        this.currentFragment.arrange(this.getLeft(), halfWidthV, this.getRight(), getHeight() + halfWidthV);
        this.previousFragment.arrange(this.getLeft(), halfWidthV, this.getRight(), getHeight() + halfWidthV);
        this.nextFragment.arrange(this.getLeft(), halfWidthV, this.getRight(), getHeight() + halfWidthV);

        this.currentFragmentHolder.layout(getLeft(), getTop(), getRight(), getBottom());
        this.previousFragmentHolder.layout(getLeft(), getTop(), getRight(), getBottom());
        this.nextFragmentHolder.layout(getLeft(), getTop(), getRight(), getBottom());

        this.decorationsLayer.layout(0, 0, getWidth(), getHeight());

        this.virtualOffsetX = 0;
        this.virtualOffsetY = 0;

        snapFragments();
    }

    /**
     * Used to snap the fragments accordingly to the current display mode and direction of scrolling.
     */
    protected void snapFragments() {
        owner.beginUpdate();
        int offset = 0;
        if (this.owner.getDisplayMode() == CalendarDisplayMode.Month && !this.scrollShouldBeHorizontal() && (this.scrollMode != ScrollMode.Overlap && this.scrollMode != ScrollMode.Stack)) {
            offset = (this.previousFragment.getBottom() - (this.previousFragment.rows().get(this.previousFragment.lastRowWithCurrentDateCellsIndex()).getBottom()));

            this.previousFragmentHolder.layout(this.currentFragmentHolder.getLeft(), this.currentFragmentHolder.getTop() - this.currentFragmentHolder.getHeight() + offset, this.currentFragmentHolder.getRight(), this.currentFragmentHolder.getTop() + offset);

            offset = ((this.currentFragment.getBottom() - this.currentFragment.rows().get(this.currentFragment.lastRowWithCurrentDateCellsIndex()).getBottom()));

            this.nextFragmentHolder.layout(this.currentFragmentHolder.getLeft(), this.currentFragmentHolder.getBottom() - offset, this.currentFragmentHolder.getRight(), this.currentFragmentHolder.getBottom() - offset + this.currentFragmentHolder.getHeight());
        } else if (this.scrollShouldBeHorizontal()) {
            if (this.scrollMode == ScrollMode.Stack) {
                this.previousFragmentHolder.layout(this.currentFragmentHolder.getLeft(), this.currentFragmentHolder.getTop(), this.currentFragmentHolder.getRight(), this.currentFragmentHolder.getBottom());
                this.currentFragmentHolder.bringToFront();
            } else {
                this.previousFragmentHolder.layout(
                        this.currentFragmentHolder.getLeft() - this.currentFragmentHolder.getWidth() + offset,
                        this.currentFragmentHolder.getTop(),
                        this.currentFragmentHolder.getLeft() + offset,
                        this.currentFragmentHolder.getBottom());
                this.previousFragmentHolder.bringToFront();
            }

            this.nextFragmentHolder.layout(
                    this.currentFragmentHolder.getRight() - offset,
                    this.currentFragmentHolder.getTop(),
                    this.currentFragmentHolder.getRight() + this.currentFragmentHolder.getWidth() - offset,
                    this.currentFragmentHolder.getBottom());
            this.nextFragmentHolder.bringToFront();
        } else {
            if (this.scrollMode == ScrollMode.Stack) {
                this.previousFragmentHolder.layout(this.currentFragmentHolder.getLeft(), (this.currentFragmentHolder.getTop()), this.currentFragmentHolder.getRight(), (this.currentFragmentHolder.getBottom()));
                this.currentFragmentHolder.bringToFront();
            } else {
                this.previousFragmentHolder.layout(
                        this.currentFragmentHolder.getLeft(),
                        this.currentFragmentHolder.getTop() - this.currentFragmentHolder.getHeight() + offset,
                        this.currentFragmentHolder.getRight(),
                        this.currentFragmentHolder.getTop() + offset);
                this.previousFragmentHolder.bringToFront();
            }

            this.nextFragmentHolder.layout(
                    this.currentFragmentHolder.getLeft(),
                    this.currentFragmentHolder.getBottom() - offset,
                    this.currentFragmentHolder.getRight(),
                    this.currentFragmentHolder.getBottom() + this.currentFragmentHolder.getHeight() - offset);
            this.nextFragmentHolder.bringToFront();
        }

        this.decorationsLayer.bringToFront();
        owner.headWrapperView.bringToFront();
        owner.endUpdate();
    }

    /**
     * Used to rearrange the fragments after decreasing the date so that they are ready to be scrolled again.
     */
    protected void shiftFragmentsBackward() {
        FragmentHolder tmp;
        tmp = this.currentFragmentHolder;
        this.currentFragmentHolder = this.previousFragmentHolder;
        this.previousFragmentHolder = this.nextFragmentHolder;
        this.nextFragmentHolder = tmp;

        onFragmentsShifted();
    }

    /**
     * Used to rearrange the fragments after increasing the date so that they are ready to be scrolled again.
     */
    protected void shiftFragmentsForward() {
        FragmentHolder tmp;
        tmp = this.currentFragmentHolder;
        this.currentFragmentHolder = this.nextFragmentHolder;
        this.nextFragmentHolder = this.previousFragmentHolder;
        this.previousFragmentHolder = tmp;

        onFragmentsShifted();
    }

    private void onFragmentsShifted() {
        this.previousFragment = previousFragmentHolder.fragment;
        this.currentFragment = currentFragmentHolder.fragment;
        this.nextFragment = nextFragmentHolder.fragment;
    }

    public void updateDecorations() {
        if (this.owner.getShowCellDecorations() && this.owner.getDisplayMode() != CalendarDisplayMode.Year) {
            this.owner.getCellDecorator().clearDecorations();
            this.previousFragment.updateDecorations();
            this.currentFragment.updateDecorations();
            this.nextFragment.updateDecorations();
        }
    }

    private void init() {
        CalendarAdapter adapter = this.owner.getAdapter();

        if(this.previousFragment == null) {
            this.previousFragment = adapter.generateFragment();
        }
        if(this.currentFragment == null) {
            this.currentFragment = adapter.generateFragment();
        }
        if(this.nextFragment == null) {
            this.nextFragment = adapter.generateFragment();
        }

        if(this.previousFragmentHolder != null)
            this.owner.removeView(this.previousFragmentHolder);
        if(this.currentFragmentHolder != null)
            this.owner.removeView(this.currentFragmentHolder);
        if(this.nextFragmentHolder != null)
            this.owner.removeView(this.nextFragmentHolder);
        if(this.decorationsLayer != null)
            this.owner.removeView(this.decorationsLayer);

        FragmentHolder holder = new FragmentHolder(this.context);
        holder.setFragment(previousFragment);
        this.previousFragmentHolder = holder;
        //this.previousFragmentHolder.setBackgroundColor(Color.RED);
        this.owner.datesHolder.addView(holder);

        holder = new FragmentHolder(this.context);
        holder.setFragment(currentFragment);
        this.currentFragmentHolder = holder;
        //this.currentFragmentHolder.setBackgroundColor(Color.GREEN);
        this.owner.datesHolder.addView(holder);

        holder = new FragmentHolder(this.context);
        holder.setFragment(nextFragment);
        this.nextFragmentHolder = holder;
        //this.nextFragmentHolder.setBackgroundColor(Color.BLUE);
        this.owner.datesHolder.addView(holder);

        this.decorationsLayer = new DecorationsLayer(this.context);
        this.owner.datesHolder.addView(this.decorationsLayer);

        this.activeDate = owner.getDisplayDate();
        updateActiveFragment();

        updateCurrentFragmentState();
    }

    private void loadEventDatesForFragment(CalendarFragment fragment, List<Long> dates) {
        for (CalendarRow row : fragment.rows()) {
            for (int i = 1; i < row.cellsCount(); i++) {
                if (!dates.contains(row.getCell(i).getDate()))
                    dates.add(row.getCell(i).getDate());
            }
        }
    }
}
