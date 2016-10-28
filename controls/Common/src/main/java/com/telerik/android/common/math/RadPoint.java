package com.telerik.android.common.math;

/**
 * This class represents a two-dimensional point with {@link #x} and {@link #y} coordinates.
 */
public final class RadPoint {

    /**
     * This field holds the X coordinate of the {@link RadPoint} instance.
     */
    private double x;


    /**
     * This field holds the Y coordinate of the {@link RadPoint} instance.
     */
    private double y;


    /**
     * Creates an instance of the {@link RadPoint} class with its
     * {@link #x} and {@link #y} coordinates initialized to 0.
     */
    public RadPoint() {
    }

    /**
     * Creates an instance of the {@link RadPoint} class with specified
     * {@link #x} and {@link #y} coordinates.
     *
     * @param x the value for the {@link #x} coordinate.
     * @param y the value for the {@link #y} coordinate.
     */
    public RadPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a {@link RadPoint} instance with its {@link #x} and {@link #y}
     * coordinates initialized to 0.
     *
     * @return the empty {@link RadPoint} instance.
     */
    public static RadPoint getEmpty() {
        return new RadPoint();
    }

    public static RadPoint getInfinityPoint() {
        return new RadPoint(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public RadPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Rounds the coordinates of the specified {@link RadPoint} instance
     * by using the {@link Math#round(double)} method.
     *
     * @param point the {@link RadPoint} instance which coordinates will be rounded.
     * @return a new {@link RadPoint} instance with rounded coordinates.
     */
    public static RadPoint round(final RadPoint point) {
        RadPoint newPoint = new RadPoint(point.x, point.y);
        newPoint.x = Math.round(newPoint.x);
        newPoint.y = Math.round(newPoint.y);

        return newPoint;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return String.format("%f, %f", this.x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadPoint)) {
            return false;
        }

        if (o == this) return true;
        RadPoint comparedPoint = (RadPoint) o;
        return comparedPoint.y == this.y &&
                comparedPoint.x == this.x;
    }
}
