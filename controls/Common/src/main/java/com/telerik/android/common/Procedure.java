package com.telerik.android.common;

/**
 * Interface that bounds its implementers to provide a method {@link #apply(Object)} which
 * doesn't return a value and has one generic argument.
 *
 * @param <TArg> the type of the argument.
 */
public interface Procedure<TArg> {
    /**
     * Method with one generic argument without return value.
     *
     * @param argument the argument.
     */
    void apply(TArg argument);
}