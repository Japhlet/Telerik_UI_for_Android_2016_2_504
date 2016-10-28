package com.telerik.android.common.math;

/**
 * This class describes a two-dimensional size with {@link #width} and {@link #height} parameters
 * used by the Chart engine for layout calculations.
 */
public final class RadSize {
    private double width;
    private double height;

    public RadSize() {
    }

    /**
     * Creates an instance of the {@link RadSize} class with specified
     * {@link #width} and {@link #height} parameters.
     *
     * @param width  the value for the {@link #width} parameter.
     * @param height the value for the {@link #height} parameter.
     */
    public RadSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns an instance of the {@link RadSize} class that represents an empty (zero) size.
     * An empty size has its {@link #width} and {@link #height} fields initialized to 0.0.
     *
     * @return the empty {@link RadSize} instance.
     */
    public static RadSize getEmpty() {
        return new RadSize(0, 0);
    }

    /**
     * Returns an instance of the {@link RadSize} class that represents an invalid size.
     * An invalid size has its {@link #width} and {@link #height} fields initialized to -1.0.
     *
     * @return the invalid {@link RadSize} instance.
     */
    public static RadSize getInvalid() {
        return new RadSize(-1.0, -1.0);
    }

    /**
     * Gets a {@link RadSize} that has positive infinity for both width and height.
     *
     * @return the positive infinity rad size.
     */
    public static RadSize getInfinitySize() {
        return new RadSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double halfWidth() {
        return this.width / 2.0;
    }

    public double halfHeight() {
        return this.height / 2.0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RadSize) {
            RadSize source = (RadSize) o;

            return source.width == this.width &&
                    source.height == this.height;
        }

        return super.equals(o);
    }
}
