package com.telerik.widget.chart.engine.axes.continuous;

/**
 * Contains a minimum and maximum value.
 *
 * @param <T> The type of the min and maxvalues.
 */
public class ValueRange<T extends Comparable<T>> {

    T minimum;
    T maximum;

    /**
     * Creates a new instance of the {@link ValueRange} class.
     */
    public ValueRange() {
    }

    /**
     * Creates a new instance of the {@link ValueRange} class with the specified min and max values.
     *
     * @param min The min value.
     * @param max The max value.
     */
    public ValueRange(T min, T max) {
        this.minimum = min;
        this.maximum = max;
    }

    /**
     * Gets the maximum value.
     *
     * @return The maximum value.
     */
    public T getMaximum() {
        return this.maximum;
    }

    /**
     * Sets the maximum value.
     *
     * @param value The maximum value.
     */
    public void setMaximum(T value) {
        int compare = value.compareTo(this.minimum);
        if (compare < 0) {
            value = this.minimum;
        }

        this.maximum = value;
    }

    /**
     * Gets the minimum value.
     *
     * @return The minimum value.
     */
    public T getMinimum() {
        return this.minimum;
    }

    /**
     * Sets the minimum value.
     *
     * @param value The minimum value.
     */
    public void setMinimum(T value) {
        int compare = value.compareTo(this.maximum);
        if (compare > 0) {
            value = this.maximum;
        }

        this.minimum = value;
    }

    /**
     * Determines whether the specified value is within the range, excluding the minimum and maximum values.
     *
     * @param value The value to check.
     * @return <code>true</code> if the value is less than maximum and greater than minimum. <code>false</code> otherwise.
     */
    public boolean isInRangeExclusive(T value) {
        return value.compareTo(this.minimum) > 0 && value.compareTo(this.maximum) < 0;
    }

    /**
     * Determines whether the specified value is within the range, including its minimum and maximum values.
     *
     * @param value The value to check.
     * @return <code>true</code> if the value is less than or equal the maximum and greater than or equal to the minimum. <code>false</code> otherwise.
     */
    public boolean isInRangeInclusive(T value) {
        return value.compareTo(this.minimum) >= 0 && value.compareTo(this.maximum) <= 0;
    }

    @Override
    public ValueRange<T> clone() {
        return new ValueRange<T>(this.getMinimum(), this.getMaximum());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ValueRange) {
            ValueRange arg = (ValueRange) o;

            return this.getMinimum().equals(arg.getMinimum())
                    &&
                    this.getMaximum().equals(arg.getMaximum());
        }
        return false;
    }
}

