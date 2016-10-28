package com.telerik.android.common.exceptions;

/**
 * This exception is thrown when a Telerik view finds a layout part with
 * a specific name, but is of the wrong type.
 */
public class WrongLayoutPartTypeException extends Error {
    public WrongLayoutPartTypeException(String message) {
        super(message);
    }
}
