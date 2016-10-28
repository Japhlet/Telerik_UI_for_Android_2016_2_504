package com.telerik.widget.chart.visualization.cartesianChart;

/**
 * Defines how grid lines are displayed.
 */
public final class GridLineRenderMode {

    /**
     * FIRST line is rendered.
     */
    public static final int FIRST = 1;

    /**
     * INNER lines are rendered.
     */
    public static final int INNER = FIRST << 1;

    /**
     * LAST line is rendered.
     */
    public static final int LAST = INNER << 1;

    /**
     * FIRST and inner lines are rendered.
     */
    public static final int FIRST_AND_INNER = FIRST | INNER;

    /**
     * INNER and last lines are rendered.
     */
    public static final int INNER_AND_LAST = INNER | LAST;

    /**
     * FIRST and last lines are rendered.
     */
    public static final int FIRST_AND_LAST = FIRST | LAST;

    /**
     * ALL lines are rendered.
     */
    public static final int ALL = FIRST | INNER | LAST;

    public static int valueOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value");
        }

        String lower = value.toLowerCase();

        if (lower.equals("first")) {
            return FIRST;
        }

        if (lower.equals("inner")) {
            return INNER;
        }

        if (lower.equals("last")) {
            return LAST;
        }

        if (lower.equals("firstandinner")) {
            return FIRST_AND_INNER;
        }

        if (lower.equals("innerandlast")) {
            return INNER_AND_LAST;
        }

        if (lower.equals("firstandlast")) {
            return FIRST_AND_LAST;
        }

        if (lower.equals("all")) {
            return ALL;
        }

        throw new IllegalArgumentException("value");
    }
}

