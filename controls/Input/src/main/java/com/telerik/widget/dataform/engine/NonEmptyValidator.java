package com.telerik.widget.dataform.engine;

public class NonEmptyValidator extends PropertyValidatorBase {
    public NonEmptyValidator() {
        this.setNegativeMessage("Entered value cannot be empty.");
    }

    @Override
    protected boolean validateCore(Object input, String propertyName) {
        return input != null && input.toString().compareTo("") != 0;
    }
}
