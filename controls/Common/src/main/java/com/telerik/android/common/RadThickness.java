package com.telerik.android.common;

/**
 * This class is used for defining a margin or a padding used for the Chart engine layout purposes.
 */
public class RadThickness {

    /**
     * Holds the value for the left side of the thickness object.
     */
    public double left = 0.0F;

    /**
     * Holds the value for the top side of the thickness object.
     */
    public double top = 0.0F;

    /**
     * Holds the value for the right side of the thickness object.
     */
    public double right = 0.0F;

    /**
     * Holds the value for the bottom side of the thickness object.
     */
    public double bottom = 0.0F;

    public RadThickness() {
    }

    /**
     * Creates a new instance of the {@link RadThickness} class with specified values
     * for left, top, right and bottom.
     *
     * @param left   the value for the left side.
     * @param top    the value for the top side.
     * @param right  the value for the right side.
     * @param bottom the value for the bottom side.
     */
    public RadThickness(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * Gets an instance of{@link RadThickness} with all parameters initialised to 0.0.
     *
     * @return the empty {@link RadThickness}.
     */
    public static RadThickness getEmpty() {
        return new RadThickness();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadThickness)) {
            return false;
        }

        if (o == this) return true;
        RadThickness comparedThickness = (RadThickness) o;
        return comparedThickness.bottom == this.bottom &&
                comparedThickness.left == this.left &&
                comparedThickness.right == this.right &&
                comparedThickness.top == this.top;
    }

    @Override
    public RadThickness clone() {
        return new RadThickness(this.left, this.top, this.right, this.bottom);
    }
}

