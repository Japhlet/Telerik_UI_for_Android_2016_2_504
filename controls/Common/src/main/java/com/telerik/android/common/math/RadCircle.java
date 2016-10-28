package com.telerik.android.common.math;

/**
 * This class represents a circle with specified center and radius. The center
 * is defined by an instance of the {@link RadPoint} class.
 */
public final class RadCircle {

    public double centerX;

    public double centerY;

    /**
     * The radius of the {@link RadCircle}.
     */
    public double radius;

    /**
     * Creates an instance of the {@link RadCircle} class with a center specified by
     * a default {@link RadPoint} instance and radius equal to 1.0.
     */
    public RadCircle() {
        this.radius = 1;
    }

    /**
     * Creates an instance of the {@link RadCircle} class with specified center and radius.
     *
     * @param center an instance of the {@link RadPoint} class depicting the circle's center.
     * @param radius the radius of the circle.
     */
    public RadCircle(final RadPoint center, double radius) {
        this(center.getX(), center.getY(), radius);
    }

    public RadCircle(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public RadPoint center() {
        return new RadPoint(this.centerX, this.centerY);
    }

    /**
     * Calculates the bounds of the current {@link RadCircle} instance.
     *
     * @return an instance of the {@link RadRect} class depicting the bounds of the circle.
     */
    public RadRect getBounds() {
        return new RadRect(this.centerX - this.radius, this.centerY - this.radius, this.radius * 2, this.radius * 2);
    }
}

