package com.telerik.widget.chart.engine.elementTree;

/**
 * Defines how a message should be dispatched in the element tree.
 */
//TODO c#[Flags]
public enum MessageDispatchMode {
    /**
     * Message is dispatched to the direct target and its ancestors.
     */
    BUBBLE,//TODO c# = 1,

    /**
     * Message is dispatched to the direct target all its descendants.
     */
    TUNNEL,//TODO c# = BUBBLE << 1,

    /**
     * Message is dispatched to the direct target, its ancestors and all its descendants.
     */
    BUBBLE_AND_TUNNEL,//TODO c# = BUBBLE | TUNNEL,
}
