package com.telerik.widget.calendar;

/**
 * Holds the visible state of an element.
 */
public enum ElementVisibility {
    /**
     * The element will be visible and will take part in both arrange and render phases.
     */
    Visible,

    /**
     * The element will be invisible therefore will not take part in the render phase, but will
     * however take part in the arrange phase resulting in taking space without being seen.
     */
    Invisible,

    /**
     * The element will be invisible and will not take any space of his own. Will not take part in
     * neither the render nor the arrange phases.
     */
    Gone
}
