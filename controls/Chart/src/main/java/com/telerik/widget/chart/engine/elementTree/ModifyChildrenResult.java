package com.telerik.widget.chart.engine.elementTree;

/**
 * Enlists the possible results that can occur after {@link ChartNode} relations modification attempts.
 */
public enum ModifyChildrenResult {

    /**
     * The modification attempt has been accepted.
     */
    ACCEPT,

    /**
     * The modification attempt was canceled.
     */
    CANCEL,

    /**
     * The modification attempt was refused.
     */
    REFUSE
}

