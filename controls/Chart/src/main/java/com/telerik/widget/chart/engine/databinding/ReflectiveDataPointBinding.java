package com.telerik.widget.chart.engine.databinding;

import android.annotation.SuppressLint;

/**
 * This class is a base for all classes that use a name for accessing the value of a bound member
 * using reflection.
 */
public abstract class ReflectiveDataPointBinding extends DataPointBinding {

    /**
     * The name of the bound member.
     */
    private String name;

    /**
     * Creates an instance of the {@link ReflectiveDataPointBinding} class.
     *
     * @param name The name of the bound member.
     * @see DataPointBinding
     */
    public ReflectiveDataPointBinding(String name) {
        setName(name);
    }

    /**
     * Gets the name of the bound member.
     *
     * @return The name of the bound member.
     */
    protected String getName() {
        return this.name;
    }

    /**
     * Sets the name of the bound member.
     *
     * @param value The name of the bound member.
     * @return <code>true</code> if name was set successfully and <code>false</code> otherwise.
     * @throws IllegalArgumentException if the passed value is null or empty.
     */
    protected boolean setName(String value) throws IllegalArgumentException {
        this.verifyMemberName(value);

        if (this.name != null && this.name.equals(value)) {
            return false;
        }

        this.name = value;

        return true;
    }

    @SuppressLint("all")
    @Override
    public Object getValue(Object instance) throws IllegalArgumentException {
        if (instance == null) {
            throw new IllegalArgumentException("instance cannot be null");
        }

        try {
            return this.getMemberValue(instance);
        } catch (Exception ex) {
            // If we try to catch ReflectiveOperationException the Java run-time crashes with VerifyError. Absolutely no idea why.
            throw new IllegalArgumentException(String.format(
                    "Reflection failed for argument and property name. Reflection exception: %s",
                    ex.toString()));
        }
    }

    /**
     * Gets the value of the bound member using reflection.
     *
     * @param instance The object from which the value of the specified member will be extracted.
     * @return The value of the bound member.
     */
    protected abstract Object getMemberValue(Object instance);

    /**
     * Makes sure the passed value is in the correct format.
     *
     * @param value the name of the bound member.
     * @throws IllegalArgumentException if the value is null or empty.
     */
    private void verifyMemberName(String value) throws IllegalArgumentException {
        if (value == null || value.equals("")) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }
    }
}
