package com.telerik.widget.chart.engine.elementTree;

/**
 * Enlists all possible message dispatching phases.
 */
public enum MessageDispatchPhase {

    /**
     * The message will be dispatched up through the parent chain.
     */
    BUBBLE,

    /**
     * The message will be dispatched down through all descendants.
     */
    TUNNEL
}

