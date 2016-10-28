package com.telerik.android.common.exceptions;

/**
 * This exception is thrown when a Telerik view is inflated with xml that does not
 * contain a required component with a specific name.
 */
public class MissingLayoutPartException extends Error {
    public MissingLayoutPartException(String message) {
        super(message);
    }
}
