package com.telerik.android.common.math;

/**
 * This class represents a two-dimensional point specified with polar coordinates.
 */
public final class RadPolarCoordinates {

    /**
     * This field holds the rotation angle in degrees.
     */
    public double angle;

    /**
     * This field holds the radius.
     */
    public double radius;

    /**
     * Creates an instance of the {@link RadPolarCoordinates} class with
     * specified rotation angle and radius.
     *
     * @param angle  the rotation angle in degrees.
     * @param radius the radius.
     */
    public RadPolarCoordinates(double angle, double radius) {
        this.angle = angle;
        this.radius = radius;
    }
}
