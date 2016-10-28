package com.telerik.android.common.math;

import java.util.ArrayList;

/**
 * This class provides methods for mathematical calculations that
 * are used throughout the Chart engine.
 */
public final class RadMath {

    private RadMath() {
    }

    /**
     * Holds a value that represents the conversion factor for
     * converting degrees to radians.
     */
    public final static double DEG_TO_RAD_FACTOR = Math.PI / 180;

    /**
     * Holds a value that represents the conversion factor for
     * converting radians to degrees.
     */
    public final static double RAD_TO_DEG_FACTOR = 180 / Math.PI;

    /**
     * Holds the value of <code>epsilon<code/>.
     */
    public final static double EPSILON = 2.2204460492503131e-9;

    /**
     * Calculates whether the provided {@link java.lang.Double} value is ZERO.
     *
     * @param value the value to check.
     * @return <code>true</code> if the value is ZERO, otherwise <code>false</code>.
     */
    public static boolean isZero(double value) {
        return Math.abs(value) < 10.0 * EPSILON;
    }

    /**
     * Calculates whether the provided {@link java.lang.Double} value is ONE.
     *
     * @param value the value to check.
     * @return <code>true</code> if the value is ONE, otherwise <code>false</code>.
     */
    public static boolean isOne(double value) {
        return Math.abs(value - 1.0) < 10.0 * EPSILON;
    }

    /**
     * Determines whether the two provided {@link java.lang.Double} values are close.
     *
     * @param value1 the first value.
     * @param value2 the second value.
     * @return <code>true<code/> if the values are close, otherwise <code>false</code>.
     */
    public static boolean areClose(double value1, double value2) {
        // in case they are Infinities (then epsilon check does not work)
        if (value1 == value2) {
            return true;
        }

        // This computes (|value1-value2| / (|value1| + |value2| + 10.0)) < DBL_EPSILON
        double eps = (Math.abs(value1) + Math.abs(value2) + 10.0) * EPSILON;
        double delta = value1 - value2;
        return (-eps < delta) && (eps > delta);
    }

    /**
     * Determines whether the two provided {@link java.lang.Double} values are close in
     * the terms of the specified tolerance.
     *
     * @param value1    the first value.
     * @param value2    the second value.
     * @param tolerance the tolerance.
     * @return <code>true<code/> if the values are close, otherwise <code>false</code>.
     */
    public static boolean areClose(double value1, double value2, double tolerance) {
        // in case they are Infinities (then epsilon check does not work)
        if (value1 == value2) {
            return true;
        }

        // This computes (|value1-value2| / (|value1| + |value2| + 10.0)) < tolerance
        double eps = (Math.abs(value1) + Math.abs(value2) + 10.0) * tolerance;
        double delta = value1 - value2;
        return (-eps < delta) && (eps > delta);
    }


    /**
     * Calculates the distance between two points specified by the provided coordinates.
     *
     * @param x1 the X coordinate of the first point.
     * @param x2 the X coordinate of the second point.
     * @param y1 the Y coordinate of the first point.
     * @param y2 the Y coordinate of the second point.
     * @return the distance between the two points.
     */
    public static double getPointDistance(double x1, double x2, double y1, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;

        return Math.sqrt((dx * dx) + (dy * dy));
    }

    /**
     * Calculates the {@link RadPoint} that lies on the arc segment of the ellipse, described by the center and radius parameters.
     *
     * @param angle  the angle in DEGREES which describes which point from the arc will
     *               be calculated.
     * @param center an instance of the {@link RadPoint} class that depicts the center of the arc.
     * @param radius the radius of the arc.
     * @return an instance of the {@link RadPoint} class which represents the point.
     */
    public static RadPoint getArcPoint(double angle, final RadPoint center, double radius) {
        double angleInRad = angle * RadMath.DEG_TO_RAD_FACTOR;

        double x = (center.getX() + (Math.cos(angleInRad) * radius));
        double y = (center.getY() + (Math.sin(angleInRad) * radius));

        return new RadPoint(x, y);
    }

    /**
     * Calculates the polar coordinates (radius and angle) from the given {@link RadPoint}, part of
     * an arc with a specified center.
     *
     * @param arcPoint an instance of the {@link RadPoint} class depicting the point which polar coordinates should be calculated.
     * @param center   an instance of the {@link RadPoint} class depicting the center of the arc on which the point resides.
     * @return an instance of the {@link RadPolarCoordinates} class representing the polar coordinates of the point.
     */
    public static RadPolarCoordinates getPolarCoordinates(final RadPoint arcPoint, final RadPoint center) {
        double x = Math.abs(center.getX() - arcPoint.getX());
        double y = Math.abs(center.getY() - arcPoint.getY());

        double radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double angle = Math.atan2(y, x);
        angle *= RadMath.RAD_TO_DEG_FACTOR;
        if (arcPoint.getX() >= center.getX()) {
            if (arcPoint.getY() > center.getY()) {
                // quadrant 4
                angle = 360 - angle;
            }
        } else {
            if (arcPoint.getY() < center.getY()) {
                // quadrant 2
                angle = 180 - angle;
            } else {
                // quadrant 3
                angle += 180;
            }
        }
        return new RadPolarCoordinates(angle, radius);
    }

    /**
     * Calculates a new size (bounds) based on the provided one by rotating it
     * with the specified angle in radians.
     *
     * @param size    an instance of the {@link RadSize} class which will be rotated.
     * @param radians the rotation angle specified in radians.
     * @return an instance of the {@link RadSize} class that represents the rotated size.
     */
    public static RadSize getRotatedSize(RadSize size, double radians) {
        ArrayList<RadPoint> rotatedPoints = new ArrayList<RadPoint>();
        rotatedPoints.add(new RadPoint(0, 0));
        rotatedPoints.add(new RadPoint(size.getWidth(), 0));
        rotatedPoints.add(new RadPoint(size.getWidth(), size.getHeight()));
        rotatedPoints.add(new RadPoint(0, size.getHeight()));

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double minusSin = -sin;

        for (int i = 0; i < rotatedPoints.size(); ++i) {
            RadPoint point = rotatedPoints.get(i);
            double x = point.getX() * cos + point.getY() * sin;
            double y = point.getX() * minusSin + point.getY() * cos;

            if (x < minX) {
                minX = x;
            }

            if (x > maxX) {
                maxX = x;
            }

            if (y < minY) {
                minY = y;
            }

            if (y > maxY) {
                maxY = y;
            }

            rotatedPoints.set(i, new RadPoint(x, y));
        }

        return new RadSize(maxX - minX, maxY - minY);
    }
}
