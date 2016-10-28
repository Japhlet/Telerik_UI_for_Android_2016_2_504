package com.telerik.widget.dataform.engine;

public class MailValidator extends PropertyValidatorBase {
    public MailValidator() {
        this.setNegativeMessage("This email is invalid.");
    }

    @Override
    protected boolean validateCore(Object input, String propertyName) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input.toString()).matches();
    }
}
