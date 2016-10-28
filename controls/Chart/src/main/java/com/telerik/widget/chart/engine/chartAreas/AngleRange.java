package com.telerik.widget.chart.engine.chartAreas;

/**
 * Represents a structure that defines the starting and sweeping angles of an arc.
 * This class is used by the Chart engine for drawing purposes.
 */
public class AngleRange {

    private double startAngle;
    private double sweepAngle;

    /**
     * Creates an instance of the {@link AngleRange} class with specified start and sweep angles.
     * The startAngle specifies the angle in degrees where the AngleRange starts and
     * the sweepAngle specifies its size in degrees.
     *
     * @param startAngle specifies the start angle.
     * @param sweepAngle specifies the sweep angle.
     */
    public AngleRange(double startAngle, double sweepAngle) {
        if (startAngle < 0 || startAngle > 360) {
            throw new IllegalArgumentException(startAngle + " is not a valid value for startAngle in AngleRange. Please provide a value from the [0, 360] interval.");
        }
        this.startAngle = startAngle;

        if (sweepAngle < 0 || sweepAngle > 360) {
            throw new IllegalArgumentException(sweepAngle + " is not a valid value for sweepAngle in AngleRange. Please provide a value from the [0, 360] interval.");
        }
        this.sweepAngle = sweepAngle;
    }

    /**
     * Gets an instance of {@link AngleRange} with default values.
     * The default value for startAngle is <code>0.0</code> and
     * the default value for sweepAngle is <code>360.0</code>.
     *
     * @return the default {@link AngleRange} instance.
     */
    public static AngleRange getDefault() {
        return new AngleRange(0.0F, 360.0F);
    }

    /**
     * Gets the current start angle.
     *
     * @return the current start angle.
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Gets the current sweep angle.
     *
     * @return the current sweep angle.
     */
    public double getSweepAngle() {
        return this.sweepAngle;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AngleRange))
            return false;

        AngleRange angleRangeObj = (AngleRange) o;
        return angleRangeObj == this || angleRangeObj.startAngle == this.startAngle && angleRangeObj.sweepAngle == this.sweepAngle;
    }
}

