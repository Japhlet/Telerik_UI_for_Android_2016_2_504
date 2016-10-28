package com.telerik.widget.calendar;

/**
 * Holds the different scroll modes in which the calendar is capable of responding after a specific
 * touch or gesture has been performed on it.
 */
public enum ScrollMode {
    /**
     * The calendar will not scroll in any direction while in this mode.
     */
    None,
    /**
     * The calendar will follow the finger of the user exactly with no further logic.
     */
    Plain,
    /**
     * The calendar will stick to the current month as soon as it is not being touched or dragged.
     */
    Sticky,
    /**
     * The calendar will spin free by receiving velocity by the user via the fling gesture.
     */
    Free,
    /**
     * The calendar will have both the functionality of the {@link #Sticky} mode and the {@link #Free} mode,
     * resulting in snapping after the fling is done.
     */
    Combo,
    /**
     * The calendar will overlap the next and the previous fragments on top of the current one in each direction going from either edge of the screen and going
     * towards the center. The edge iof the screen will matter in parallel to the current display mode.
     */
    Overlap,
    /**
     * The calendar will overlap the next and the previous fragments on top of the current. Increasing the date will cause a stack effect and decreasing in will cause
     * an unstack effect.
     */
    Stack
}
