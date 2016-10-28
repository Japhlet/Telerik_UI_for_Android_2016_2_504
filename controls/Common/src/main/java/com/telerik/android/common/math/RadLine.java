package com.telerik.android.common.math;

/**
 * This class represents a two dimensional line build out of two points in the plane.
 */
public final class RadLine {

    /**
     * This field holds the X coordinate of the first point of the {@link RadLine} instance.
     */
    public double x1;

    /**
     * This field holds the X coordinate of the second point of the {@link RadLine} instance.
     */
    public double x2;

    /**
     * This field holds the Y coordinate of the first point of the {@link RadLine} instance.
     */
    public double y1;

    /**
     * This field holds the Y coordinate of the second point of the {@link RadLine} instance.
     */
    public double y2;

    /**
     * Creates an instance of the {@link RadLine} class with specified coordinates
     * for its building points.
     *
     * @param x1 the value for the X coordinate of the first point.
     * @param x2 the value for the X coordinate of the second point
     * @param y1 the value for the Y coordinate of the first point.
     * @param y2 the value for the Y coordinate of the second point.
     */
    public RadLine(float x1, float x2, float y1, float y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Creates an instance of the {@link RadLine} class with specified coordinates for its building points.
     *
     * @param point1 an instance of the {@link RadPoint} class specifying the first point for the line.
     * @param point2 an instance of the {@link RadPoint} class specifying the second point for the line.
     */
    public RadLine(RadPoint point1, RadPoint point2) {
        this.x1 = point1.getX();
        this.y1 = point1.getY();
        this.x2 = point2.getX();
        this.y2 = point2.getY();
    }

    /**
     * Rounds the parameters of the specified {@link RadLine} by adding 0.5 to the points
     * that build it.
     *
     * @param line the instance of the {@link RadLine} class which will be rounded.
     * @return a new instance of the {@link RadLine} class with rounded coordinates.
     */
    public static RadLine round(RadLine line) {
        return new RadLine((int) (line.x1 + 0.5), (int) (line.x2 + 0.5), (int) (line.y1 + 0.5), (int) (line.y2 + 0.5));
    }
}

