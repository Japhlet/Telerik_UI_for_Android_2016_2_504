package com.telerik.widget.chart.visualization.behaviors;

/**
 * Contract bounding implementers to provide needed context to a tooltip instance.
 */
public interface TooltipContextNeededListener {
    void onContextNeeded(TooltipContextNeededEventArgs args);
}
