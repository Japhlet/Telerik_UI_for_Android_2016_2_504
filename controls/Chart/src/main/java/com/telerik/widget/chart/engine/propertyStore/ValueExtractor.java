package com.telerik.widget.chart.engine.propertyStore;

/**
 * This generic class is used to implement the boolean tryGetValue(result) pattern. If a value is found it is returned through the argument and
 * the return value of the tryGet() method indicates whether something was returned or not.
 *
 * @param <T> The type of the value to be extracted.
 */
public class ValueExtractor<T> {
    public T value;
}
