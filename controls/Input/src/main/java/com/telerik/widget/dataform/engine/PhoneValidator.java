package com.telerik.widget.dataform.engine;

public class PhoneValidator extends PropertyValidatorBase {
    public PhoneValidator() {
        this.setNegativeMessage("The phone is invalid.");
    }

    @Override
    protected boolean validateCore(Object input, String propertyName) {
        return android.util.Patterns.PHONE.matcher(input.toString()).matches();
    }
}