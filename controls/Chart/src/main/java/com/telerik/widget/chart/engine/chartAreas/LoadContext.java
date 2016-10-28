package com.telerik.widget.chart.engine.chartAreas;

/**
 * Represents the load context used to initialize {@link com.telerik.widget.chart.engine.elementTree.ChartNode} instances.
 */
public class LoadContext {

    private ChartAreaModel chartArea;

    /**
     * Creates an instance of the {@link LoadContext} class with the associated
     * {@link ChartAreaModel}.
     *
     * @param area the {@link ChartAreaModel} delegated by this {@link LoadContext}.
     */
    LoadContext(ChartAreaModel area) {
        this.chartArea = area;
    }

    /**
     * Gets the {@link ChartAreaModel} associated with this load context.
     *
     * @return the {@link ChartAreaModel} instance.
     */
    public ChartAreaModel getChartArea() {
        return this.chartArea;
    }
}

