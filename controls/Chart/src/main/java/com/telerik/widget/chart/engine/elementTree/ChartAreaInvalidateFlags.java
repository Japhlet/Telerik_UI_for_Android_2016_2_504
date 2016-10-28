package com.telerik.widget.chart.engine.elementTree;

/**
 * Enlists all possible types of inpact a {@link ChartNode} property change may have on the rest of the chart tree.
 */
public class ChartAreaInvalidateFlags {

    /**
     * The property change has no impact on other {@link ChartNode} instances.
     */
    public static final int NONE = 0,

    /**
     * The property change implies that the {@link ChartNode} instances representing the axes should be reset.
     */
    RESET_AXES = 1,

    /**
     * The property change implies that the plot information about the {@link ChartNode} instances representing the axes should be invalidated.
     */
    INVALIDATE_AXES = RESET_AXES << 1,

    /**
     * The property change implies that the plot information about the {@link ChartNode} instances representing the axes should be invalidated.
     */
    INVALIDATE_SERIES = INVALIDATE_AXES << 1,

    /**
     * The property change implies that the plot information about the {@link ChartNode} instances representing the grid should be invalidated.
     */
    INVALIDATE_GRID = INVALIDATE_SERIES << 1,

    /**
     * The property change implies that the {@link ChartNode} instances representing the annotations should be reset.
     */
    RESET_ANNOTATIONS = INVALIDATE_GRID << 1,

    /**
     * The property change implies that the plot information about the {@link ChartNode} instances representing the annotations should be invalidated.
     */
    INVALIDATE_ANNOTATIONS = RESET_ANNOTATIONS << 1,

    /**
     * The property change implies that the plot information about the {@link ChartNode} instances representing the axes and the grid should be invalidated.
     */
    INVALIDATE_AXES_AND_GRID = INVALIDATE_AXES | INVALIDATE_GRID,

    /**
     * The property change implies that the plot information about all {@link ChartNode} instances should be invalidated.
     */
    INVALIDATE_ALL = INVALIDATE_AXES | INVALIDATE_SERIES | INVALIDATE_GRID | INVALIDATE_ANNOTATIONS,

    /**
     * The property change implies that the plot information about all {@link ChartNode} instances should be reset and invalidated.
     */
    ALL = INVALIDATE_ALL | RESET_AXES | RESET_ANNOTATIONS;
}

