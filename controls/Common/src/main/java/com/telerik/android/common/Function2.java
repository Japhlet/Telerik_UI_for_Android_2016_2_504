package com.telerik.android.common;

/**
 * Interface that bounds its implementers to provide a method {@link #apply(Object, Object)} that
 * has a generic return type and two generic arguments.
 *
 * @param <TArg1>   the type of the first argument.
 * @param <TArg2>   the type of the second argument.
 * @param <TResult> the return type.
 */
public interface Function2<TArg1, TArg2, TResult> {
    /**
     * Method with a generic return type and two generic arguments.
     *
     * @param argument1 the first argument.
     * @param argument2 the second argument.
     * @return the generic result of the processed two arguments.
     */
    TResult apply(TArg1 argument1, TArg2 argument2);
}
