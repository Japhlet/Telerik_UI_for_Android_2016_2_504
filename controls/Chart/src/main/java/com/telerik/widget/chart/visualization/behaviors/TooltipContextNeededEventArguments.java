package com.telerik.widget.chart.visualization.behaviors;

public class TooltipContextNeededEventArguments {
    private final ChartDataContext defaultContext;
    private ChartDataContext context;

    public TooltipContextNeededEventArguments(ChartDataContext defaultContext) {
        if (defaultContext == null) {
            throw new IllegalArgumentException("defaultContext cannot be null.");
        }
        this.defaultContext = defaultContext;
    }

    public ChartDataContext getDefaultContext() {
        return this.defaultContext;
    }

    public ChartDataContext getContext() {
        return this.context;
    }

    public void setContext(ChartDataContext context) {
        this.context = context;
    }
}
