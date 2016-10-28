package com.telerik.widget.chart.visualization.cartesianChart;

/**
 * Possible values for the chart grid lines and stripes visibility.
 */
public final class GridLineVisibility {

    /**
     * lines are hidden.
     */
    public static final int NONE = 0;

    /**
     * The lines along the x-axis are visible.
     */
    public static final int X = 1;

    /**
     * The lines along the y-axis are visible.
     */
    public static final int Y = X << 1;

    /**
     * The lines are visible along both axes.
     */
    public static final int XY = X | Y;

    public static int valueOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value");
        }

        String lower = value.toLowerCase();

        if (lower.equals("none")) {
            return NONE;
        }

        if (lower.equals("x")) {
            return X;
        }

        if (lower.equals("y")) {
            return Y;
        }

        if (lower.equals("xy")) {
            return XY;
        }

        throw new IllegalArgumentException("invalid value");
    }
}
