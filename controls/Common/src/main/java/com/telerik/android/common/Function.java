package com.telerik.android.common;

/**
 * Simple interface that bounds its implementers to provide a method {@link #apply(Object)} that has
 * a generic argument and a generic return type.
 *
 * @param <TArg>    the type of the argument.
 * @param <TResult> the return type.
 */
public interface Function<TArg, TResult> {
    /**
     * Method that has generic return type and a generic argument.
     *
     * @param argument generic argument.
     * @return generic result after processing the argument.
     */
    TResult apply(TArg argument);
}

