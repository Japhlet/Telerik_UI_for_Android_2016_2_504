package com.telerik.android.common;

/**
 * Represents a tuple of values.
 *
 * @param <T> the type of the first value.
 * @param <U> the type of the second value.
 * @param <V> the type of the third value.
 */
public class DataTuple<T, U, V> {

    public T firstValue;
    public U secondValue;
    public V thirdValue;

    /**
     * Creates a new instance of the {@link DataTuple} class with
     * specified values.
     *
     * @param firstValue  the first value of the tuple.
     * @param secondValue the second value of the tuple.
     * @param thirdValue  the third value of the tuple.
     */
    public DataTuple(T firstValue, U secondValue, V thirdValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.thirdValue = thirdValue;
    }

    /**
     * Creates a new instance of the {@link DataTuple} class by specifying the first two values.
     *
     * @param firstValue  the first value of the tuple.
     * @param secondValue the second value of the tuple.
     */
    public DataTuple(T firstValue, U secondValue) {
        this(firstValue, secondValue, null);
    }

    /**
     * Creates a new instance of the {@link DataTuple} class by specifying its first value.
     *
     * @param firstValue the first value of the tuple.
     */
    public DataTuple(T firstValue) {
        this(firstValue, null, null);
    }

    @Override
    public String toString() {
        return String.format("FirstValue: %s SecondValue: %s ThirdValue: %s", this.firstValue, this.secondValue, thirdValue);
    }
}

