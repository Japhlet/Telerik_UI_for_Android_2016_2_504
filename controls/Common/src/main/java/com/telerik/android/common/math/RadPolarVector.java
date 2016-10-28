package com.telerik.android.common.math;

/**
 * This class represents a vector in the two-dimensional space.
 */
public final class RadPolarVector {

    public double centerX;

    public double centerY;

    public double pointX;

    public double pointY;

    /**
     * An instance of the {@link RadPoint} class that specifies the center of the vector.
     */
    public RadPoint center() {
        return new RadPoint(this.centerX, this.centerY);
    }

    /**
     * An instance of the {@link RadPoint} class that specifies the magnitude of the vector.
     */
    public RadPoint point() {
        return new RadPoint(this.pointX, this.pointY);
    }

    /**
     * The angle of the {@link RadPolarVector} that specifies its direction.
     */
    public double angle;
}

