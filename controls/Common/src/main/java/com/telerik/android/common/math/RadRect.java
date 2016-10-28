package com.telerik.android.common.math;

import com.telerik.android.common.RadThickness;

/**
 * This class describes a rectangle and is used by the Chart engine to store the bounds
 * of the different chart elements.
 */
public final class RadRect {
    private double x;
    private double y;
    private double width;
    private double height;

    public RadRect() {
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    /**
     * Creates an instance of the {@link RadRect} class with a specified {@link #width} and {@link #height}.
     * The default values of the {@link #x} and {@link #y} coordinates are 0.0.
     *
     * @param width  the width of the rectangle which will be created.
     * @param height the height of the rectangle which will be created.
     */
    public RadRect(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Creates an instance of the {@link RadRect} class with specified top left
     * and bottom right points.
     *
     * @param point1 the top left point depicting the position of the rectangle.
     * @param point2 the bottom right point depicting the width and height of the rectangle.
     */
    public RadRect(final RadPoint point1, final RadPoint point2) {
        this.x = Math.min(point1.getX(), point2.getX());
        this.y = Math.min(point1.getY(), point2.getY());
        this.width = Math.max(Math.max(point1.getX(), point2.getX()) - this.x, 0);
        this.height = Math.max(Math.max(point1.getY(), point2.getY()) - this.y, 0);
    }

    /**
     * Creates an instance of the {@link RadRect} class with specified coordinates and dimensions.
     *
     * @param x      the {@link #x} coordinate of the rectangle.
     * @param y      the {@link #y} coordinate of the rectangle.
     * @param width  the {@link #width} of the rectangle.
     * @param height the {@link #height} of the rectangle.
     */
    public RadRect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets a {@link RadRect} instance with all of its parameters set to 0.0. This
     * depicts an empty rectangle in the Chart engine.
     *
     * @return A new {@link RadRect} instance representing the empty rectangle.
     */
    public static RadRect getEmpty() {
        return new RadRect(0, 0);
    }

    /**
     * Performs a rounding of all parameters of the supplied {@link RadRect} instance
     * by adding 0.5 to them.
     *
     * @return A new rounded {@link RadRect} instance.
     */
    public static RadRect round(final RadRect rect) {
        return new RadRect((int) (rect.x + 0.5), (int) (rect.y + 0.5), (int) (rect.width + 0.5), (int) (rect.height + 0.5));
    }

    /**
     * Creates a new instance of the supplied {@link RadRect} representing a square width side length the shortest from the {@link #width} or {@link #height}.
     *
     * @param rect   the {@link RadRect} instance from which to create the square.
     * @param offset determines whether the location of the square will be offset with half the difference
     *               between the width (for the {@link #x} coordinate) and height (for the {@link #y} coordinate)
     *               and the side of the square.
     * @return A new {@link RadRect} instance representing the square.
     */
    public static RadRect toSquare(final RadRect rect, boolean offset) {
        RadRect newRect = rect;
        double minLength = Math.min(newRect.width, newRect.height);

        if (offset) {
            newRect.x = newRect.x + (newRect.width - minLength) / 2;
            newRect.y = newRect.y + (newRect.height - minLength) / 2;
        }

        newRect.width = minLength;
        newRect.height = minLength;

        return newRect;
    }

    /**
     * Centers the {@link RadRect} instance in the first parameter in the bounds depicted by the {@link RadRect} instance of the second parameter.
     *
     * @param rect   the {@link RadRect} instance that will be centered.
     * @param bounds the {@link RadRect} instance which depicts the bounds in which the first rectangle will be centered.
     * @return A new {@link RadRect} instance representing the centered rectangle.
     */
    public static RadRect centerRect(final RadRect rect, final RadRect bounds) {
        RadRect newRect = rect;
        double offsetX = (bounds.width - newRect.width) / 2;
        double offsetY = (bounds.height - newRect.height) / 2;

        newRect.x = bounds.x + offsetX;
        newRect.y = bounds.y + offsetY;

        return newRect;
    }

    /**
     * Inflates the bounds of the provided {@link RadRect} instance with the margin
     * stored in the second {@link RadThickness} parameter.
     *
     * @param rect    the {@link RadRect} to be inflated.
     * @param margins the {@link RadThickness} instance depicting the margins used for the inflation.
     * @return a new inflated {@link RadRect} instance.
     */
    public static RadRect inflate(final RadRect rect, final RadThickness margins) {
        return new RadRect(rect.x + margins.left, rect.y + margins.top, rect.width - margins.left - margins.right, rect.height - margins.top - margins.bottom);
    }

    /**
     * Calculates the bottom of the {@link RadRect} instance by adding the {@link #height} to the {@link #y} coordinate.
     *
     * @return the bottom of the {@link RadRect} instance.
     */
    public double getBottom() {
        return this.y + this.height;
    }

    /**
     * Calculates the right of the {@link RadRect} instance by adding the {@link #width} to the {@link #x} coordinate.
     *
     * @return the right of the {@link RadRect} instance.
     */
    public double getRight() {
        return this.x + this.width;
    }

    /**
     * Calculates the center of the {@link RadRect} instance by adding half the width and height to the {@link #x} and {@link #y} coordinate correspondingly.
     *
     * @return an instance of the {@link RadPoint} class representing the center of the {@link RadRect} supplied as a parameter.
     */
    public RadPoint getCenter() {
        return new RadPoint(this.x + (this.width / 2), this.y + (this.height / 2));
    }

    /**
     * Creates a {@link RadPoint} instance representing the location of this {@link RadRect}.
     *
     * @return an instance of the {@link RadPoint} class representing the location.
     */
    public RadPoint getLocation() {
        return new RadPoint(this.x, this.y);
    }

    /**
     * Returns a boolean value determining whether the provided {@link RadRect} instance shares
     * any common points with the current {@link RadRect} instance.
     *
     * @return <code>true<code/> if common points exist, otherwise <code>false<code/>.
     */
    public boolean intersectsWith(final RadRect rect) {
        return rect.x <= this.getRight() && rect.getRight() >= this.x && rect.y <= this.getBottom() && rect.getBottom() >= this.y;
    }

    /**
     * Returns a boolean value determining whether the size of the current {@link RadRect} instance is non-empty.
     *
     * @return <code>true<code/> if the size is non-empty, otherwise <code>false<code/>.
     */
    public boolean isSizeValid() {
        return this.width > 0 && this.height > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadRect)) return false;
        if (o == this) return true;
        RadRect comparedRect = (RadRect) o;
        return comparedRect.width == this.width &&
                comparedRect.height == this.height &&
                comparedRect.x == this.x &&
                comparedRect.y == this.y;
    }

    /**
     * Returns a boolean value determining whether the given point
     * is part of this RadRect.
     *
     * @param x the x coordinate of the point to be checked.
     * @param y the y coordinate of the point to be checked.
     * @return <code>true</code> if the point is part of this {@link RadRect} instance, otherwise <code>false</code>.
     */
    public boolean contains(double x, double y) {
        return x >= this.x && x <= this.x + this.width &&
                y >= this.y && y <= this.y + this.height;
    }

    /**
     * Returns a boolean value determining whether the given RadRect
     * is inside of this RadRect.
     *
     * @param rect the rectangle to be checked.
     */
    public boolean contains(RadRect rect) {
        return rect.x >= this.x && rect.getRight() <= this.getRight() &&
               rect.y >= this.y && rect.getBottom() <= this.getBottom();
    }

    @Override
    public String toString() {
        return String.format("X: %f, Y: %f, W: %f, H: %f", x, y, width, height);
    }
}
