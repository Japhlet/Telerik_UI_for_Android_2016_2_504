package com.telerik.widget.chart.engine.series.combination;

/**
 * Defines how multiple series of same type are combined on the plot area.
 */
public enum ChartSeriesCombineMode {

    /**
     * No combining.&nbsp;Each series is plotted independently.
     */
    NONE,

    /**
     * Series are combined next to each other.
     */
    CLUSTER,

    /**
     * Series form stacks.
     */
    STACK,

    /**
     * Series for stacks that occupy 100% of the plot area.
     */
    STACK_100
}

