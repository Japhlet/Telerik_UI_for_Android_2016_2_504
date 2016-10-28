package com.telerik.widget.chart.engine.series.combination;

/**
 * Identifies widget series that can be combined with other
 * {@link SupportCombineMode} instances of same type.
 */
public interface SupportCombineMode {

    /**
     * Gets the {@link ChartSeriesCombineMode} value for this instance.
     *
     * @return the combined chart series.
     */
    public ChartSeriesCombineMode getCombineMode();

    /**
     * Gets the key of the stack where this instance is plotted.
     *
     * @return the key where this instance is plotted.
     */
    public Object getStackGroupKey();
}

