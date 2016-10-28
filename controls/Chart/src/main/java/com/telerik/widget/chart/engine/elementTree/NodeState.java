package com.telerik.widget.chart.engine.elementTree;

/**
 * Enlists all possible states of a {@link ChartNode} within its owning tree.
 */
public enum NodeState {

    /**
     * Depicts the initial state of a {@link ChartNode} and is initialized when the node is being created.
     */
    INITIAL,

    /**
     * Depicts the process of loading the {@link ChartNode} into the chart tree.
     */
    LOADING,

    /**
     * The {@link ChartNode} has been loaded and is ready to be visualized.
     */
    LOADED,

    /**
     * The {@link ChartNode} is in process of being unloaded from the chart tree.
     */
    UNLOADING,

    /**
     * The {@link ChartNode} has been detached from the chart tree.
     */
    UNLOADED
}

