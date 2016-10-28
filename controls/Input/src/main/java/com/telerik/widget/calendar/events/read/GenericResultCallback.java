package com.telerik.widget.calendar.events.read;

/**
 * Used to retrieve the result of a call.
 *
 * @param <T> the type of the result.
 */
public interface GenericResultCallback<T> {

    /**
     * Will be called once the result is available.
     *
     * @param result the result of the call.
     */
    void onResult(T result);
}
