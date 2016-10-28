package com.telerik.widget.chart.engine.axes.continuous;

/**
 * Specifies how a numerical axis auto-range will be extended so that each data point is visualized in the best possible way.
 */
public final class NumericalAxisRangeExtendDirection {

    /**
     * The range minimum is the minimum data point value and the range maximum is the maximum data point value.
     */
    public static final int NONE = 0,

    /**
     * The range maximum will be extended (if necessary) with one major step.
     */
    POSITIVE = 1,

    /**
     * The range minimum will be extended (if necessary) with one major step.
     */
    NEGATIVE = POSITIVE << 1,

    /**
     * The range will be extended in both negative and positive direction.
     */
    BOTH = POSITIVE | NEGATIVE;
}

