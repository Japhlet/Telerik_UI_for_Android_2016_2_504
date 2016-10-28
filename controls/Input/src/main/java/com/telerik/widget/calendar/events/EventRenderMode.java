package com.telerik.widget.calendar.events;

/**
 * An enumeration with the different render modes for
 * the default {@link com.telerik.widget.calendar.events.EventRenderer}.
 */
public enum EventRenderMode {

    /**
     * The events are represented only by shape which represent their length.
     */
    Shape,

    /**
     * The events are represented only by text.
     */
    Text,

    /**
     * The events are represented by shape and text.
     */
    Shape_And_Text,

    /**
     * The events are not displayed.
     */
    None
}
